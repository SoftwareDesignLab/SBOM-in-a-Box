from typing import Optional

from OSTool import OSTool
from constants import BOMFormat, Language

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
        "jake sbom -t PIP --output-format json -o {output}/jake-pip-cdx14.json",
        "jake sbom -t POETRY --output-format json -o {output}/jake-poetry-cdx14.json"
    ], True),
    # TODO figure out how to get output to only be SBOM and no messages
    OSTool("CycloneDX Conan", BOMFormat.CYCLONE_DX, [Language.C_CPLUSPLUS], [
        "cd {code}",
        "cyclonedx-conan {manifest} --output {output}/cdxconan-cdx14.json"
    ], True),
    OSTool("CycloneDX Generator", BOMFormat.CYCLONE_DX, [
        Language.JAVA, Language.PHP, Language.PYTHON, Language.GO, Language.RUBY, Language.RUST, Language.DART,
        Language.HASKELL, Language.C_CPLUSPLUS
    ], [
               "cd {code}",
               "cdxgen -r -o {output}/cdxgen-cdx14.json"
           ]),
    OSTool("CycloneDX Python", BOMFormat.CYCLONE_DX, [Language.PYTHON], [
        "cd {code}",
        "cyclonedx-py --format json -r -o {output}/cdxpy-req-cdx14.json",
        "cyclonedx-py --format json -p -o {output}/cdxpy-poetry-cdx14.json"
    ], True),
    # TODO Fix cargo pkgid error
    OSTool("SPDX SBOM Generator", BOMFormat.SPDX, [
        Language.GO, Language.RUST, Language.PHP, Language.JAVA, Language.JAVASCRIPT, Language.PYTHON,
        Language.RUBY, Language.SWIFT
    ], [
               "cd {code}",
               "spdx-sbom-generator -o {output}"
           ]),
    OSTool("CycloneDX PHP", BOMFormat.CYCLONE_DX, [Language.PHP], [
        "cd {code}",
        "composer update --no-install",
        "composer CycloneDX:make-sbom --output-format=JSON --output-file={output}/cdxphp-14.json"
    ], True),
    OSTool("Syft CDX", BOMFormat.CYCLONE_DX, [
        Language.C_CPLUSPLUS, Language.DART, Language.ERLANG, Language.GO, Language.HASKELL, Language.JAVA,
        Language.JAVASCRIPT, Language.PHP, Language.PYTHON, Language.RUBY, Language.RUST, Language.SWIFT
    ], [
               "cd {code}",
               "syft . -o cyclonedx-json@1.4={output}/syft-cdx14.json"
           ]),
    OSTool("JBOM Jar", BOMFormat.CYCLONE_DX, [Language.JAVA_JAR], [
        "cd {code}",
        "java -jar /usr/local/bin/jbom.jar -d . -o {output}"
    ]),
    OSTool("JBOM", BOMFormat.CYCLONE_DX, [Language.JAVA], [
        "cd {code}",
        "java -jar /usr/local/bin/jbom.jar -d . -o {output}"
    ]),
    # todo can add '--no-wfp-output' to skip fingerprinting. Removed because only generates JSON SBOMs
    # OSTool("Scanoss Python", BOMFormat.CYCLONE_DX, [Language.PYTHON], [
    #    "cd {code}", "scanoss-py scan --dependencies --format cyclonedx -o {output}/"+SBOM_TEMP_NAME+"."+SBOM_FORMAT+" ."
    # ]),
    OSTool("Syft SPDX", BOMFormat.SPDX, [
        Language.C_CPLUSPLUS, Language.DART, Language.ERLANG, Language.GO, Language.HASKELL, Language.JAVA,
        Language.JAVASCRIPT, Language.PHP, Language.PYTHON, Language.RUBY, Language.RUST, Language.SWIFT
    ], [
               "cd {code}",
               "syft . -o spdx-tag-value={output}/syft-spdx23.spdx"
           ]),
    # todo setup manual output of SBOM file, GoBom has no output flag for their generate command
    # OSTool("GoBom", BOMFormat.CYCLONE_DX, [Language.GO],
    #    ["cd {code}/*", "gobom generate . -o {output}/" + SBOM_TEMP_NAME + "." + SBOM_FORMAT]),
    OSTool("CycloneDX Go", BOMFormat.CYCLONE_DX, [Language.GO],
           ["cd {code}", "cyclonedx-go -o {output}/cdxgo-cdx10.xml"]),
    OSTool("sbom-tool", BOMFormat.SPDX, [
        Language.GO, Language.PYTHON, Language.RUBY, Language.RUST
    ], [
               "cd {code}",
               "sbom-tool generate -ps . -b . -m . -pn . -pv .",
               "mv _manifest/spdx_2.2/manifest.spdx.json {output} && rm -rf _manifest"
           ]),
    OSTool("CycloneDX Bower Bom", BOMFormat.CYCLONE_DX, [Language.JAVASCRIPT], [
        
        "cd {code}", 
        "bower --allow-root install", 
        "cdx-bower-bom -o {output}/cdxbower-cdx10.xml"
    ]),
    OSTool("CycloneDX Rust Cargo", BOMFormat.CYCLONE_DX, [Language.RUST],
           [
               "cd {code}",
               "cargo cyclonedx -f json -a -o {output}/cdxrust-cdx14.json"
           ]),
    OSTool("sbom4python CDX", BOMFormat.CYCLONE_DX, [Language.PYTHON],
           [
               "cd {code}", 
               "sbom4python --sbom cyclonedx --output-file={output}/sbom4python-cdx15.json"
           ]),
    OSTool("sbom4python SPDX", BOMFormat.SPDX, [Language.PYTHON],
           [
               "cd {code}",
               "sbom4python --sbom spdx --output-file={output}/sbom4python-spdx23.spdx"
           ]),
    OSTool("sbom4rust CDX", BOMFormat.CYCLONE_DX, [Language.RUST],
           [
               "cd {code}", 
               "sbom4rust --sbom cyclonedx -a osi-sbom --output-file={output}/sbom4rust-cdx15.json"
           ]),
    OSTool("sbom4rust SPDX", BOMFormat.SPDX, [Language.RUST],
           [
               "cd {code}",
               "sbom4python --sbom spdx -a osi-sbom --output-file={output}/sbom4rust-spdx23.spdx"
           ]),
    OSTool("sbom4files CDX", BOMFormat.CYCLONE_DX, [
        Language.C_CPLUSPLUS, Language.GO, Language.JAVA, Language.JAVASCRIPT,
        Language.PHP, Language.PYTHON, Language.RUST, Language.PERL],
           [
               "cd {code}",
               "sbom4files -d . -p osi-sbom -r --sbom cyclonedx -o {output}/sbom4files-cdx15.json"
           ]),
    OSTool("sbom4files SPDX", BOMFormat.SPDX, [
        Language.C_CPLUSPLUS, Language.GO, Language.JAVA, Language.JAVASCRIPT,
        Language.PHP, Language.PYTHON, Language.RUST, Language.PERL],
           [
               "cd {code}",
               "sbom4files -d . -p osi-sbom -r --sbom spdx --output-file={output}/sbom4files-spdx23.spdx"
           ]),
    OSTool("Covenant", BOMFormat.CYCLONE_DX, [
        Language.C_CPLUSPLUS, Language.CSHARP, Language.DART, Language.ERLANG, Language.GO, Language.HASKELL,
        Language.JAVA,
        Language.JAVASCRIPT, Language.PHP, Language.PYTHON, Language.RUBY, Language.RUST, Language.SWIFT],
           [
               "cd {code}",
               "dotnet build",
               "dotnet covenant generate . -o /tmp/covenant.out",
               "dotnet covenant convert spdx /tmp/covenant.out -o {output}/covenant-spdx23.json",
               "rm /tmp/covenant.out"
           ]),
    
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
