"""
file: OSIAPIController.py

API that exposes endpoints to manage the generations of SBOMs using Open Source Tools inside the svip-osi Docker
Container.

@author Ian Dunn
"""
import configparser
import json
import os
import subprocess

from flask import Flask, request, jsonify

from tools.ToolFactory import ToolFactory, RunConfig, Profile

LANGUAGE_EXT_CONFIG = "configs/language_ext.cfg"
MANIFEST_EXT_CONFIG = "configs/manifest_ext.cfg"

FILE_NAME_SED_PATTERN = r's|.*\/||'

# Create Flask app
app = Flask(__name__)

AVAILABLE_TOOLS = []
LANGUAGE_MAP = dict[str, str]
MANIFEST_MAP = dict[str, str]


@app.route('/tools', methods=['GET'])
def get_tools():
    """
    Endpoint: GET http://localhost:50001/tools?list=
    param arg
    Returns: A list of names of valid open-source tools.
    """
    match request.args.get('list'):
        case None:
            return os.environ['OSI_TOOL'].split(":"), 200
        case "all":
            return os.environ['OSI_TOOL'].split(":"), 200
        case "project":
            tools = get_applicable_tools()
            tool_names = [tool.name for tool in tools]
            return tool_names, 200
        case _:
            return f"'{request.args.get('list')}' is an unknown param", 400


@app.route('/generate', methods=['POST'])
def generate():
    """
    Endpoint: GET http://localhost:50001/generate

    Request Body: A JSON list of tool names to use in generation. If null, defaults to all tools.
    Returns:      Generated SBOMs in osi/bound_dir/sboms.
    Returns:      200 if SBOMs were generated, 204 otherwise.
    """

    # Cleanup, detect languages
    tools = []
    if request.is_json:
        try:
            post_json = jsonify(request.json)
            tool_names = post_json.get_json()['tools']
            app.logger.info(f"Attempting to use provided tools: {tool_names}")
        except Exception as e:
            app.logger.error(f"Failed to parse json: {e}")
            return "Failed to paser tools", 400

        for tool_name in tool_names:
            try:
                tf = ToolFactory()
                tool = tf.build_tool(tool_name)
                if not any(a_tool.name == tool.name for a_tool in AVAILABLE_TOOLS):
                    app.logger.warning(f"Attempting to use '{tool.name}' when not available, skipping . . .")
                else:
                    tools.append(tool)
            except Exception as e:
                app.logger.error(f"{e}")
                continue
    else:
        app.logger.info("No tools provided; Defaulting to relevant tools.")
        tools = get_applicable_tools()

    if len(tools) == 0:
        app.logger.error("No tools selected")
        return "No tools available", 422
    #
    # Run tools and cleanup
    app.logger.info(f"Running with tools: {tools}")

    #
    # # Return 200 (ok) if sboms were generated, otherwise return 204 (no content)
    # if gen_count > 0:
    #     app.logger.info(str(gen_count) + " SBOMs generated.")
    #     return "Successfully generated SBOMs.", 200
    # else:
    #     app.logger.info("No SBOMs generated.")
    #     return "No SBOMs generated.", 204


def get_applicable_tools() -> list[Profile]:
    languages = set()
    package_managers = set()

    # find . -type f -name '*.*' | sed 's/.*\///' | sort -u
    files = subprocess.run(
        f"find . -type f -name '*.*' | sed '{FILE_NAME_SED_PATTERN}' | sort -u",
        shell=True, capture_output=True, text=True).stdout.strip().split("\n")

    for file_name in files:
        if file_name.lower().split(".")[1] in LANGUAGE_MAP:
            languages.add(LANGUAGE_MAP.get(file_name.split(".")[1]))
        if file_name.lower() in MANIFEST_MAP:
            package_managers.add(MANIFEST_MAP[file_name.lower()])

    app.logger.info(f"Detected languages: {languages}")
    app.logger.info(f"Detected package managers: {package_managers}")

    run_config = RunConfig(languages, package_managers)

    tools = []
    for tool in AVAILABLE_TOOLS:
        tools += tool.get_matching_profiles(run_config)

    return tools


def load_ext_mapper(config_file: str) -> dict[str, str]:
    cfg = configparser.ConfigParser(allow_no_value=True)
    cfg.read(config_file)
    ext_map = {}
    for sec in cfg.sections():
        ext_map.update(
            dict((key, sec.lower()) for key, y in cfg.items(sec))  # ignore y since empty
        )
    return ext_map


if __name__ == '__main__':
    # Load tools available in this instance
    # tf = ToolFactory()
    # for tool_name in os.environ['OSI_TOOL'].split(":"):
    #     tool = tf.build_tool(tool_name)
    #     AVAILABLE_TOOLS.append(tool)
    # LANGUAGE_MAP = load_ext_mapper(LANGUAGE_EXT_CONFIG)
    # MANIFEST_MAP = load_ext_mapper(MANIFEST_EXT_CONFIG)
    app.run(host='0.0.0.0', debug=True)  # TODO move to config
