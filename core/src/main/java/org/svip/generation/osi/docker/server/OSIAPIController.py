"""
file: OSIAPIController.py

API that exposes endpoints to manage the generations of SBOMs using Open Source Tools inside the svip-osi Docker
Container.

@author Ian Dunn
"""
import os

from flask import Flask, request
import json

import ToolUtils
from OSTool import OSTool
from ToolMapper import get_tool
from constants import CONTAINER_BIND_CODE, CONTAINER_BIND_SBOM, Language

# Create Flask app
app = Flask(__name__)


@app.route('/tools', methods=['GET'])
def get_tools():
    """
    Endpoint: GET http://localhost:50001/tools

    Returns: A list of names of valid open-source tools.
    """

    # Get tools from env
    return os.environ['OSI_TOOL'].split(":")


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
        tools = parse_tools(json.loads(request.get_json()["tools"]), langs)
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


def parse_tools(tool_names: str, langs: list[Language]) -> list[OSTool]:
    """
    Helper method to parse a list of tool names and project languages into a list of valid, applicable OSTool instances.

    Parameter: tool_names - A list of tool names.
    Parameter: langs      - A list of project languages.
    Returns: A list of valid, applicable OSTool instances that correspond to tool names. If a tool name is invalid or
             not applicable, it will be skipped.
    """

    tools = []
    valid_tools = ToolUtils.get_tools(langs)

    app.logger.info("Tools selected:")
    for name in tool_names:
        # Make sure tool exists
        tool = get_tool(name)
        if tool is None:
            app.logger.warning(name + " -- Invalid tool name. Skipping.")
            continue

        # Add tool if valid
        if tool in valid_tools:
            tools.append(tool)
            app.logger.info(name)
        else:
            app.logger.warning(name + " -- Invalid tool for detected languages. Skipping.")

    return tools


if __name__ == '__main__':

    app.run(host='0.0.0.0', debug=True)  # TODO move to config
