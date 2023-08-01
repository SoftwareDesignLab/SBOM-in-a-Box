import glob
import os
import sys
from enum import Enum

"""
file: ContainerController.py

Manages the usage of Open Source Tools inside the OSI Docker Container

@author Matt London
"""
CONTAINER_CONTROLLER = "v1.0.0"

"""
Constants that should not change, but if they do change them here
"""
# Path to the directory that will be mounted to the container
CONTAINER_BIND_DIR = "/bound_dir"
# Path to the directory where the host machine will put the code
CONTAINER_BIND_CODE = "{}/code".format(CONTAINER_BIND_DIR)
# Path to the directory where the container will save the SBOMs
CONTAINER_BIND_SBOM = "{}/sboms".format(CONTAINER_BIND_DIR)
# Name of sbom before it is renamed to a number
SBOM_TEMP_NAME = "tempbom"
# Preferred format extension of the SBOM
SBOM_FORMAT = "xml"


class BOMFormat(Enum):
    """
    Enum for current SBOM Formats
    """
    CYCLONE_DX = 1,
    SPDX = 2,
    OTHER = 3


class Language(Enum):
    """
    Enum to hold all supported languages. These will then link to a list of tools and their usage
    """
    JAVA = 1,
    PYTHON = 2,
    C_CPLUSPLUS = 3,
    CSHARP = 4,
    JAVASCRIPT = 5,
    RUBY = 6,
    GO = 7,
    RUST = 8,
    SWIFT = 9,
    KOTLIN = 10,
    SCALA = 11,
    PHP = 12,
    HASKELL = 13,
    PERL = 14,
    LUA = 15,
    DART = 16,
    ERLANG = 17,
    COBOL = 18,
    FORTRAN = 19,
    PASCAL = 20,
    ADA = 21,
    LISP = 22,
    PROLOG = 23,
    R = 24,
    MATLAB = 25,
    BASIC = 26,
    JAVA_JAR = 27


class OSTool(object):
    """
    Tool object for each supported OSI tool. This will hold name, languages, and usage
    """

    def __init__(self, name: str, generation_type: BOMFormat, languages: list[Language], usage: list[str],
                 manifest_required: bool = False):
        """
        Constructor for an OSTool

        :param name: Name of the tool
        :param generation_type: Type of BOM the tool generates
        :param languages: List of languages it supports
        :param usage: List of commands needed to execute the tool on a project ({code} for code path,
            {output} for output path, {manifest} for potential manifest files)
        :param manifest_required: If the tool requires a passed in manifest file to function
        """
        self.name = name
        self.generationType = generation_type
        self.languages = languages
        self.usage = usage
        self.manifest_required = manifest_required


class ToolMapper(object):
    """
    Class to hold all tools and map languages to a list of tools
    """
    tool_list = [
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
        ], ["syft {code} -vv -o cyclonedx-xml > {output}/" + SBOM_TEMP_NAME + "." + SBOM_FORMAT]),
        OSTool("JBOM Jar", BOMFormat.CYCLONE_DX, [Language.JAVA_JAR], [
            "java -jar /usr/local/bin/jbom.jar -f {code} -o {output}"
        ]),
        OSTool("JBOM", BOMFormat.CYCLONE_DX, [Language.JAVA], [
            "java -jar /usr/local/bin/jbom.jar -d {code} -o {output}"
        ]),
        # todo can add '--no-wfp-output' to skip fingerprinting. Removed because only generates JSON SBOMs
        # OSTool("Scanoss Python", BOMFormat.CYCLONE_DX, [Language.PYTHON], [
        #    "cd {code}", "scanoss-py scan --dependencies --format cyclonedx -o {output}/"+SBOM_TEMP_NAME+"."+SBOM_FORMAT+" ."
        # ]),
        OSTool("Syft SPDX", BOMFormat.SPDX, [
            Language.C_CPLUSPLUS, Language.DART, Language.ERLANG, Language.GO, Language.HASKELL, Language.JAVA,
            Language.JAVASCRIPT, Language.PHP, Language.PYTHON, Language.RUBY, Language.RUST, Language.SWIFT
        ], ["syft {code} -vv -o spdx-tag-value > {output}/" + SBOM_TEMP_NAME + ".spdx"]),
    ]

    # Add tools into a list dictionary keyed by language
    mapping = {}

    # Map in all tools to their respective languages
    for language in Language:
        langList = []

        # No this is not a for loop in a for loop...
        for tool in tool_list:
            if language in tool.languages:
                langList.append(tool)

        mapping[language] = langList

    def get_tools(self, language: Language) -> list[OSTool]:
        """
        Gets a list of tools for a given language

        :param language: Language to get tools for
        :return: List of tools
        """
        return self.mapping[language]


class ContainerController(object):
    """
    As of right now currently just a class that will hold functions executed by the main method when the container
    is launched
    """
    languageMap = {
        "java": Language.JAVA, "py": Language.PYTHON, "pyi": Language.PYTHON, "cpp": Language.C_CPLUSPLUS,
        "cxx": Language.C_CPLUSPLUS, "cc": Language.C_CPLUSPLUS, "h": Language.C_CPLUSPLUS,
        "hpp": Language.C_CPLUSPLUS, "C": Language.C_CPLUSPLUS, "H": Language.C_CPLUSPLUS,
        "cs": Language.CSHARP, "js": Language.JAVASCRIPT, "rb": Language.RUBY, "go": Language.GO,
        "rs": Language.RUST, "swift": Language.SWIFT, "kt": Language.KOTLIN, "scala": Language.SCALA,
        "sc": Language.SCALA, "php": Language.PHP, "hs": Language.HASKELL, "lhs": Language.HASKELL,
        "pl": Language.PERL, "lua": Language.LUA, "dart": Language.DART, "erl": Language.ERLANG,
        "hrl": Language.ERLANG, "cbl": Language.COBOL, "cob": Language.COBOL, "f90": Language.FORTRAN,
        "for": Language.FORTRAN, "f": Language.FORTRAN, "pas": Language.PASCAL, "adb": Language.ADA,
        "ads": Language.ADA, "lsp": Language.LISP, "prolog": Language.PROLOG, "r": Language.R,
        "m": Language.MATLAB, "bas": Language.BASIC, "jar": Language.JAVA_JAR
    }

    manifestMap = {
        "pom.xml": Language.JAVA, "build.gradle": Language.JAVA, "build.sbt": Language.JAVA,
        "requirements.txt": Language.PYTHON, "cargo.toml": Language.RUST, "cargo.lock": Language.RUST,
        "go.mod": Language.GO, "go.sum": Language.GO, "package.json": Language.JAVASCRIPT, "gemfile": Language.RUBY,
        "gemfile.lock": Language.RUBY, "pubspec.yaml": Language.DART, "pubspec.lock": Language.DART,
        "conanfile.txt": Language.C_CPLUSPLUS, "conanfile.py": Language.C_CPLUSPLUS, "composer.json": Language.PHP
    }

    def detect_language(self, path: str) -> tuple[list[Language], list[str]]:
        """
        Detects the language of a project
        :param path: Path to the project
        :return: Languages of the project, list of paths to manifest files
        """
        languages = set()
        manifest_paths = set()

        for filename in glob.iglob(path + '/**/*', recursive=True):
            file_extension = filename.lower().split('.')[-1]  # get file extension

            # check if extension is in the map
            if file_extension in self.languageMap.keys():
                # Add language to list
                languages.add(self.languageMap[file_extension])

                # Check if it is perl or prolog (which have the same extension)
                if self.languageMap[file_extension] == Language.PERL:
                    languages.add(Language.PROLOG)
                if self.languageMap[file_extension] == Language.PROLOG:
                    languages.add(Language.PERL)

            # Check for manifest
            file_cleaned = filename.replace("\\", "/").replace("./", "").split("/")[-1]
            if file_cleaned.lower() in self.manifestMap.keys():
                languages.add(self.manifestMap[file_cleaned.lower()])
                manifest_paths.add(filename.replace("\\", "/").replace("./", ""))

        print("Detected languages: " + str(languages))
        return list(languages), list(manifest_paths)

    def get_tools(self, languages: list[Language]) -> list[OSTool]:
        """
        Gets a list of tools for a given list of languages

        :param languages: Languages to get tools for
        :return: List of tools
        """
        # Loop through languages and get tool mapping
        tool_mapper = ToolMapper()

        tools = set()
        for language in languages:
            tools.update(tool_mapper.get_tools(language))

        return list(tools)

    def run_tools(self, tools: list[OSTool], manifest_files: list[str], code_path: str, output_path: str) -> int:
        """
        Run a list of tools on a project directory and output all generated boms to output_path
        The names of the files in the path is irrelevant and can be named anything so long as another bom is not overwritten

        :param tools: List of tools to run on project
        :param manifest_files: List of paths to manifest files
        :param code_path: Path to the project code
        :param output_path: Where to output all BOMs
        :return: Number of sboms generated
        """
        gen_count = 0

        # Now we will loop through each tool
        for tool in tools:
            # Surround the whole thing in a try block to catch keyboard interrupts and allow tools
            # to be skipped
            try:
                # Make sure the usage isn't empty
                if len(tool.usage) == 0:
                    continue

                # Check if command requires a manifest file and if that exists
                if tool.manifest_required and len(manifest_files) == 0:
                    print("{} requires a manifest file but none were found. Skipping...".format(tool.name))
                    continue

                # Loop through manifest files, if none then loop once
                if len(manifest_files) == 0:
                    manifest_files.append("")

                for manifest_file in manifest_files:
                    # Build a command from the tool's usage
                    if manifest_file != "" and tool.manifest_required:
                        # Make sure the current tool supports the language of the manifest file
                        if self.manifestMap[manifest_file.split("/")[-1].lower()] not in tool.languages:
                            continue

                    command = (" && ".join(tool.usage)).format(code=code_path, manifest=manifest_file,
                                                               output=output_path)

                    # Snapshot directory
                    file_contents_before = set(
                        [f for f in os.listdir(output_path) if os.path.isfile(os.path.join(output_path, f))])

                    # Execute command
                    print("Running: {}, with: {}".format(tool.name, command))
                    os.system(command)

                    # Snapshot directory again to see what has changed
                    file_contents_after = set(
                        [f for f in os.listdir(output_path) if os.path.isfile(os.path.join(output_path, f))])

                    # Find the list of generated files
                    generated_files = file_contents_after.difference(file_contents_before)

                    if len(generated_files) == 0:
                        continue

                    for filename in generated_files:
                        extension = ""
                        curr_name = filename.split(".")[0]

                        if SBOM_FORMAT in filename:
                            extension = SBOM_FORMAT

                        elif "spdx" in filename:
                            extension = "spdx"

                        # Other use cases can be added here
                        else:
                            continue

                        os.system("mv {}/{}.{format} {}/{}.{format}".format(output_path, curr_name, output_path,
                                                                            gen_count + 1, format=extension))

                        gen_count += 1

                    # Exit if we don't need the manifest file
                    if not tool.manifest_required:
                        break
            except KeyboardInterrupt:
                print("Skipping tool: {}".format(tool.name))
                continue

        return gen_count

    def clean_manifest(self, manifest_paths: list[str]) -> list[str]:
        """
        Cleans the manifest files and removes duplicates (like having cargo.toml and cargo.lock)

        :param manifest_paths: List of manifest paths
        :return: Cleaned list of manifest paths
        """
        root_names = []

        for path in manifest_paths:
            root_names.append(path.split("/")[-1])

        # Clean rust duplicates
        if "Cargo.toml" in root_names and "Cargo.lock" in root_names:
            manifest_paths.pop(root_names.index("Cargo.lock"))

        # Clean go duplicates
        if "go.mod" in root_names and "go.sum" in root_names:
            manifest_paths.pop(root_names.index("go.sum"))

        # Clean ruby duplicates
        if "Gemfile" in root_names and "Gemfile.lock" in root_names:
            manifest_paths.pop(root_names.index("Gemfile.lock"))

        # Clean python duplicates
        if "requirements.txt" in root_names and "Pipfile" in root_names:
            manifest_paths.pop(root_names.index("Pipfile"))

        # Clean dart duplicates
        if "pubspec.yaml" in root_names and "pubspec.lock" in root_names:
            manifest_paths.pop(root_names.index("pubspec.lock"))

        return manifest_paths

    def cleanup(self, initial: bool = False) -> None:
        """
        Clean up output directory and only keep bom files

        :param initial: If this is the initial cleanup before running
        :return: None
        """
        for name in os.scandir(CONTAINER_BIND_SBOM):
            if initial and name.name != ".gitignore":
                os.remove(CONTAINER_BIND_SBOM + "/" + name.name)
                continue

            try:
                if name.name == ".gitignore" or int(name.name.split(".")[0]) > 0:
                    continue
                else:
                    # Throw so we hit the exception
                    raise ValueError

            except ValueError:
                # Remove if not a number
                os.remove(CONTAINER_BIND_SBOM + "/" + name.name)

    def execute(self) -> int:
        """
        Run all the commands cohesively

        :return: Exit code
        """
        self.cleanup(True)
        langs, manifest_files = self.detect_language(CONTAINER_BIND_CODE)
        manifest_clean = self.clean_manifest(manifest_files)
        tools = self.get_tools(langs)
        print("Will use:")
        for i in tools:
            print(i.name, end=", ")
        print()
        gen_count = self.run_tools(tools, manifest_clean, CONTAINER_BIND_CODE, CONTAINER_BIND_SBOM)
        self.cleanup()

        # Return 0 if sboms were generated, otherwise return 1
        return int(not (gen_count > 0))


def main() -> int:
    controller = ContainerController()
    return controller.execute()


if __name__ == "__main__":
    sys.exit(main())
