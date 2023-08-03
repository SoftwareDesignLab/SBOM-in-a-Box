import glob
import os
from constants import Language, LANGUAGE_MAP, MANIFEST_MAP, SBOM_FORMAT, CONTAINER_BIND_SBOM, CONTAINER_BIND_CODE
from ToolMapper import OSTool, ToolMapper

"""
file: ToolUtils.py

Collection of utilities to validate, run, and organize the output of Open Source SBOM tools.

@author Matt London
@author Ian Dunn
"""


def detect_language(path: str) -> tuple[list[Language], list[str]]:
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
        if file_extension in LANGUAGE_MAP.keys():
            # Add language to list
            languages.add(LANGUAGE_MAP[file_extension])

            # Check if it is perl or prolog (which have the same extension)
            if LANGUAGE_MAP[file_extension] == Language.PERL:
                languages.add(Language.PROLOG)
            if LANGUAGE_MAP[file_extension] == Language.PROLOG:
                languages.add(Language.PERL)

        # Check for manifest
        file_cleaned = filename.replace("\\", "/").replace("./", "").split("/")[-1]
        if file_cleaned.lower() in MANIFEST_MAP.keys():
            languages.add(MANIFEST_MAP[file_cleaned.lower()])
            manifest_paths.add(filename.replace("\\", "/").replace("./", ""))

    print("Detected languages: " + str(languages))
    return list(languages), list(manifest_paths)


def get_tools(languages: list[Language]) -> list[OSTool]:
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


def run_tools(tools: list[OSTool], manifest_files: list[str], code_path: str, output_path: str) -> int:
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
                    if MANIFEST_MAP[manifest_file.split("/")[-1].lower()] not in tool.languages:
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


def clean_manifest(manifest_paths: list[str]) -> list[str]:
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


def cleanup(initial: bool = False) -> None:
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

