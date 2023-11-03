import sys
from typing import Optional

from constants import *
from OSTool import OSTool

"""
file: ToolMapper.py

A map of all supported Open Source SBOM Tools and their corresponding languages, commands, and other attributes.

@author Matt London
@author Ian Dunn
@author Tyler Drake
"""

"""
Map of all current open-source tools.
"""
TOOL_LIST = [
    OSTool("Jake", BOMFormat.CYCLONE_DX, [Language.PYTHON], [
        "cd {code}",
        "jake sbom -t PIP -f {manifest} --output-format xml -o {output}/" + SBOM_TEMP_NAME + "." + SBOM_FORMAT
    ], True),
    # TODO figure out how to get output to only be SBOM and no messages
    OSTool("CycloneDX Conan", BOMFormat.CYCLONE_DX, [Language.C_CPLUSPLUS], [
        "cyclonedx-conan {manifest} > {output}/" + SBOM_TEMP_NAME + "." + SBOM_FORMAT
    ], True),
    OSTool("CycloneDX Generator", BOMFormat.CYCLONE_DX, [
        Language.JAVA, Language.PHP, Language.PYTHON, Language.GO, Language.RUBY, Language.RUST, Language.DART,
        Language.HASKELL, Language.C_CPLUSPLUS
    ], [
               "cd {code}", "cdxgen -r -o {output}/" + SBOM_TEMP_NAME + "." + SBOM_FORMAT
           ]),
    OSTool("CycloneDX Python", BOMFormat.CYCLONE_DX, [Language.PYTHON], [
        "cd {code}", "cyclonedx-py --format xml -r -i {manifest} -o {output}/" + SBOM_TEMP_NAME + "." + SBOM_FORMAT
    ], True),
    # TODO Fix cargo pkgid error
    OSTool("SPDX SBOM Generator", BOMFormat.SPDX, [
        Language.GO, Language.RUST, Language.PHP, Language.JAVA, Language.JAVASCRIPT, Language.PYTHON,
        Language.RUBY, Language.SWIFT
    ], [
               "cd {code}", "spdx-sbom-generator -p . -o {output}/"
           ]),
    OSTool("CycloneDX PHP", BOMFormat.CYCLONE_DX, [Language.PHP], [
        "cd {code}",
        "composer make-bom --output-format=XML --output-file={output}/" + SBOM_TEMP_NAME + "." + SBOM_FORMAT
        + " {manifest}"
    ], True),
    OSTool("Ochrona CLI", BOMFormat.OTHER, [], []),
    OSTool("Syft CDX", BOMFormat.CYCLONE_DX, [
        Language.C_CPLUSPLUS, Language.DART, Language.ERLANG, Language.GO, Language.HASKELL, Language.JAVA,
        Language.JAVASCRIPT, Language.PHP, Language.PYTHON, Language.RUBY, Language.RUST, Language.SWIFT
    ], ["cd {code}", "syft . -vv -o cyclonedx-xml > {output}/" + SBOM_TEMP_NAME + "." + SBOM_FORMAT]),
    OSTool("JBOM Jar", BOMFormat.CYCLONE_DX, [Language.JAVA_JAR], [
        "cd {code}", "java -jar /usr/local/bin/jbom.jar -f . -o {output}"
    ]),
    OSTool("JBOM", BOMFormat.CYCLONE_DX, [Language.JAVA], [
        "cd {code}", "java -jar /usr/local/bin/jbom.jar -d . -o {output}"
    ]),
    # todo can add '--no-wfp-output' to skip fingerprinting. Removed because only generates JSON SBOMs
    # OSTool("Scanoss Python", BOMFormat.CYCLONE_DX, [Language.PYTHON], [
    #    "cd {code}", "scanoss-py scan --dependencies --format cyclonedx -o {output}/"+SBOM_TEMP_NAME+"."+SBOM_FORMAT+" ."
    # ]),
    OSTool("Syft SPDX", BOMFormat.SPDX, [
        Language.C_CPLUSPLUS, Language.DART, Language.ERLANG, Language.GO, Language.HASKELL, Language.JAVA,
        Language.JAVASCRIPT, Language.PHP, Language.PYTHON, Language.RUBY, Language.RUST, Language.SWIFT
    ], ["cd {code}", "syft . -vv -o spdx-tag-value > {output}/" + SBOM_TEMP_NAME + ".spdx"]),
    OSTool("GoBom", BOMFormat.CYCLONE_DX, [Language.GO], ["cd {code}", "gobom generate . -o {output}"]),
    OSTool("CycloneDX Go", BOMFormat.CYCLONE_DX, [Language.GO], ["cd {code}", "cyclonedx-go -o {output}"]),
    OSTool("sbom-tool", BOMFormat.SPDX, [
        Language.GO, Language.PYTHON, Language.RUBY, Language.RUST
    ], ["cd {code}", "sbom-tool generate -ps . -b . -m {output} -pn . -pv ."]),
    OSTool("CycloneDX Bower Bom", BOMFormat.CYCLONE_DX, [Language.JSON], [
        "cd {code}", "bower install", "cdx-bower-bom -o {output}"]),
    OSTool("CycloneDX Rust Cargo", BOMFormat.CYCLONE_DX, [Language.RUST],
        ["cd {code}", "cargo cyclonedx -f json -a -o {output}/" + SBOM_TEMP_NAME + "." + SBOM_FORMAT]),
    OSTool("sbom4python CDX", BOMFormat.CYCLONE_DX, [Language.PYTHON],
        ["cd {code}", "sbom4python --sbom cyclonedx --output-file={output}/" + SBOM_TEMP_NAME + "." + SBOM_FORMAT]),
    OSTool("sbom4python SPDX", BOMFormat.SPDX, [Language.PYTHON],
        ["cd {code}", "sbom4python --sbom spdx --output-file={output}/" + SBOM_TEMP_NAME + "." + SBOM_FORMAT]),
    OSTool("sbom4rust CDX", BOMFormat.CYCLONE_DX, [Language.RUST],
        ["cd {code}", "sbom4rust --sbom cyclonedx --output-file={output}/" + SBOM_TEMP_NAME + "." + SBOM_FORMAT]),
    OSTool("sbom4rust SPDX", BOMFormat.SPDX, [Language.RUST],
        ["cd {code}", "sbom4rust --sbom spdx --output-file={output}/" + SBOM_TEMP_NAME + "." + SBOM_FORMAT]),
]

# Add tools into a list dictionary keyed by language
TOOL_MAPPING = {}

# Map in all tools to their respective languages
for l in Language:
    langList = []

    # No this is not a for loop in a for loop...
    for t in TOOL_LIST:
        if l in t.languages:
            langList.append(t)

    TOOL_MAPPING[l] = langList


def get_tool_names() -> list[str]:
    """
    Gets a complete list of tool names.
    """
    return list(map(str, TOOL_LIST))


def get_tool(name: str) -> Optional[OSTool]:
    """
    Gets an OSTool instance given a tool name. If no tool could be found, returns None.
    """
    for tool in TOOL_LIST:
        if tool.name == name:
            return tool
    return


def get_tools(language: Language) -> list[OSTool]:
    """
    Gets a list of tools for a given language

    :param language: Language to get tools for
    :return: List of tools
    """
    return TOOL_MAPPING[language]
