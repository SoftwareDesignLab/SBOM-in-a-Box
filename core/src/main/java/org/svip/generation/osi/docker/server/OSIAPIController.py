"""
file: OSIAPIController.py

API that exposes endpoints to manage the generations of SBOMs using Open Source Tools inside the svip-osi Docker
Container.

@author Ian Dunn
"""
import configparser
import os
import subprocess

from flask import Flask, request
import json

import ToolUtils
from OSTool import OSTool
from ToolMapper import get_tool
from constants import CONTAINER_BIND_CODE, CONTAINER_BIND_SBOM, Language
from tools.ToolFactory import Tool, ToolFactory, RunConfig, Profile

LANGUAGE_EXT_CONFIG = "constant/configs/language_ext.cfg"
MANIFEST_EXT_CONFIG = "constant/configs/manifest_ext.cfg"

FILE_NAME_SED_PATTERN = 's/.*\///'

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
    ToolUtils.cleanup(True)
    langs, manifest_files = ToolUtils.detect_language(CONTAINER_BIND_CODE)
    manifest_clean = ToolUtils.clean_manifest(manifest_files)

    app.logger.info("Detected languages: " + str([l.name for l in langs]))
    app.logger.info("Detected manifest files: " + str(manifest_files))

    tools = []

    if request.data and request.is_json and request.get_json()["tools"] and len(request.get_json()["tools"]) > 0:
        tools = get_applicable_tools(json.loads(request.get_json()["tools"]), langs)
    else:
        app.logger.info("No tools provided. Defaulting to all tools.")
        tools = ToolUtils.get_tools(langs)

    # Run tools and cleanup
    app.logger.info("Running with tools: " + str(tools))
    gen_count = ToolUtils.run_tools(tools, manifest_clean, CONTAINER_BIND_CODE, CONTAINER_BIND_SBOM)
    ToolUtils.cleanup()

    # Return 200 (ok) if sboms were generated, otherwise return 204 (no content)
    if gen_count > 0:
        app.logger.info(str(gen_count) + " SBOMs generated.")
        return "Successfully generated SBOMs.", 200
    else:
        app.logger.info("No SBOMs generated.")
        return "No SBOMs generated.", 204


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

    run_config = RunConfig(languages, package_managers)

    tools = []
    for tool in AVAILABLE_TOOLS:
        tools += tool.get_matching_profiles(run_config)

    return tools


def build_extension_regex(extension: str) -> str:
    return f"find -name '*{extension}' | grep -qE '*{extension}'".replace(".", "\.")


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
    tf = ToolFactory()
    for tool_name in os.environ['OSI_TOOL'].split(":"):
        tool = tf.build_tool(tool_name)
        AVAILABLE_TOOLS.append(tool)
    LANGUAGE_MAP = load_ext_mapper(LANGUAGE_EXT_CONFIG)
    MANIFEST_MAP = load_ext_mapper(MANIFEST_EXT_CONFIG)

    app.run(host='0.0.0.0', debug=True)  # TODO move to config
