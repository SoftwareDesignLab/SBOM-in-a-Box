"""
file: OSIServer.py

API that exposes endpoints to manage the generations of SBOMs using Open Source Tools inside the svip-osi Docker Container.

@author Ian Dunn
@auther Derek Garcia
"""
import configparser
import os
import subprocess
import time

from flask import Flask, request, jsonify

from ToolFactory import ToolFactory, RunConfig, Profile

VERSION = "4.0"

# Extension Configuration files
LANGUAGE_EXT_CONFIG = "configs/language_ext.cfg"
MANIFEST_EXT_CONFIG = "configs/manifest_ext.cfg"

# SED pattern to get file name from path
FILE_NAME_SED_PATTERN = r's|.*\/||'

# Globals
AVAILABLE_TOOLS = []
AVAILABLE_TOOLS_STR = os.environ['OSI_TOOL'].split(":")  # set with validate.sh
LANGUAGE_MAP = dict[str, str]
MANIFEST_MAP = dict[str, str]

# Create Flask app
app = Flask(__name__)


#
# ENDPOINTS
#

@app.route('/tools', methods=['GET'])
def get_tools():
    """
    Endpoint: GET http://localhost:50001/tools Default returns all tools installed in the OSI instance Has one
    optional request param: list
    - http://localhost:50001/tools?list=all : get all tools installed in the OSI instance
    - http://localhost:50001/tools?list=project : get all tools installed that can be used with the project in the bound directory

    Returns: A list of names of valid open-source tools.
    """
    match request.args.get('list'):
        case None:
            return AVAILABLE_TOOLS_STR, 200
        case "all":
            return AVAILABLE_TOOLS_STR, 200
        case "project":
            tools = get_applicable_tools()
            tool_names = set(map(lambda tool: tool.name, tools))  # remove duplicate tool names
            return list(tool_names), 200
        case _:
            return f"'{request.args.get('list')}' is an unknown param", 400


@app.route('/generate', methods=['POST'])
def generate():
    """
    Endpoint: POST http://localhost:50001/generate

    Request Body: A JSON list of tool names to use in generation. If null, defaults to all tools.
    Returns:      Number of SBOMs generated
    Returns:      200 if SBOMs were generated, 204 otherwise.
    """

    tool_profiles = []
    # Parse request body if one is provided
    if request.is_json:
        try:
            # Get tools and create a list to lowercase strings
            post_json = jsonify(request.json)
            tool_names = list(map(lambda tool_name: tool_name.lower(), post_json.get_json()['tools']))
            app.logger.info(f"Attempting to use provided tools: {tool_names}")

            # Check to see if attempting to use any unknown tools
            tool_diff = list(set(tool_names) - set(AVAILABLE_TOOLS_STR))
            if len(tool_diff) > 0:
                app.logger.error(f"Generate | Attempting to use unknown or unavailable tools: {tool_diff}")
                return f"Attempting to use unknown or unavailable tools: {tool_diff}", 400

        except Exception as e:
            app.logger.error(f"Generate | Failed to parse json: {e}")
            return "Failed to pase tools", 400

        # Build the tools from the list of names provided
        # TODO just use ones created?
        tf = ToolFactory()
        for tool_name in tool_names:
            try:
                tool = tf.build_tool(tool_name)
                tool_profiles += tool.profiles
            except Exception as e:
                app.logger.error(f"{e}")

    else:
        # No tools provided, default to all relevant tools to the project
        app.logger.info("Generate | No tools provided; Defaulting to relevant tools.")
        tool_profiles = get_applicable_tools()

    # Check to make sure there are tools that can be used
    if len(tool_profiles) == 0:
        app.logger.error("Generate | No tools selected")
        return "No tools selected", 422

    app.logger.info(f"Generate | Running with tools: {set([p.name for p in tool_profiles])}")
    generated_sboms = 0
    # Execute each run profile
    for tool_profile in tool_profiles:
        try:
            app.logger.info(f"Generate | Executing {tool_profile} with command string: "
                            f"{tool_profile.build_exe_string('$CODE_IN')}")
            start_time = time.time()
            tool_profile.execute('$CODE_IN')  # execute run commands set in the tool config
            app.logger.info(f"Generate | Completed in {time.time() - start_time:.2f} seconds")
            generated_sboms += 1

        except Exception as e:
            # Problem when running tool
            app.logger.error(f"Generate | Failed to generate with {tool_profile.name}: {e}")

    # Return 200 (ok) if sboms were generated, otherwise return 204 (no content)
    app.logger.info(f"Generate | {generated_sboms} SBOMs generated")
    return generated_sboms, 200 if generated_sboms > 0 else 204


#
# HELPER METHODS
#
def get_applicable_tools() -> list[Profile]:
    """
    Looks at the files stored in the code bound directory and determines which tool run profiles apply
    :return: List of relevant tool run profiles
    """
    languages = set()
    package_managers = set()

    # List all files in the bound code directory
    files = subprocess.run(
        f"find $CODE_IN -type f -name '*.*' | sed '{FILE_NAME_SED_PATTERN}' | sort -u",
        shell=True, capture_output=True, text=True).stdout.strip().split("\n")

    # Parse each file
    for file_name in files:
        # get extension
        parts = file_name.lower().split(".")
        ext = f".{parts[len(parts) - 1]}"

        # Use extension to determine language
        if ext in LANGUAGE_MAP:
            languages.add(LANGUAGE_MAP.get(ext))

        # Use filename to detemine package manager
        if file_name.lower() in MANIFEST_MAP:
            package_managers.add(MANIFEST_MAP[file_name.lower()])

    app.logger.info(f"Applicable Tools | Detected languages: {list(languages)}")
    app.logger.info(f"Applicable Tools | Detected package managers: {list(package_managers)}")

    # Make a run config with the info found and get all matching run profiles
    run_config = RunConfig(languages, package_managers)
    tools = []
    for tool in AVAILABLE_TOOLS:
        tools += tool.get_matching_profiles(run_config)

    return tools


def load_ext_mapper(config_file: str) -> dict[str, str]:
    """
    Load the extension / file config files into memory
    :param config_file: Path to config file
    :return:
    """
    cfg = configparser.ConfigParser(allow_no_value=True)
    cfg.read(config_file)
    ext_map = {}

    """
    Set the extension as key and section as value. Example:
    [Java]
    .java
    becomes
    {".java": "java"}
    """
    for sec in cfg.sections():
        ext_map.update(
            dict((key, sec.lower()) for key, y in cfg.items(sec))  # ignore y since empty
        )
    return ext_map


if __name__ == '__main__':
    """
    Launch the server
    """
    # Load tools available in this instance
    tf = ToolFactory()
    for tool_name in AVAILABLE_TOOLS_STR:
        tool = tf.build_tool(tool_name)
        AVAILABLE_TOOLS.append(tool)

    # Load extension maps
    LANGUAGE_MAP = load_ext_mapper(LANGUAGE_EXT_CONFIG)
    MANIFEST_MAP = load_ext_mapper(MANIFEST_EXT_CONFIG)

    print(f"Running OSIv{VERSION} with {AVAILABLE_TOOLS}")

    # Launch the server
    app.run(host='0.0.0.0', debug=True)  # TODO move to config
