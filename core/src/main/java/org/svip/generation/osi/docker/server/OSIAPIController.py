"""
file: OSIAPIController.py

API that exposes endpoints to manage the generations of SBOMs using Open Source Tools inside the svip-osi Docker
Container.

@author Ian Dunn
"""

from flask import Flask, request

import ToolUtils
from OSTool import OSTool
from constants import CONTAINER_BIND_CODE, CONTAINER_BIND_SBOM, Language
from ToolMapper import get_tool, get_tool_names

# Create Flask app
app = Flask(__name__)


@app.route('/tools', methods=['GET'])
def get_tools():
    """
    Endpoint: GET http://localhost:5000/tools

    Returns: A list of names of valid open-source tools.
    """

    return get_tool_names()


@app.route('/generate', methods=['POST'])
def generate():
    """
    Endpoint: GET http://localhost:5000/generate

    Request Body: A JSON list of tool names to use in generation. If null, defaults to all tools.
    Returns:      Generated SBOMs in osi/bound_dir/sboms.
    Returns:      200 if SBOMs were generated, 204 otherwise.
    """

    # Cleanup, detect languages
    ToolUtils.cleanup(True)
    langs, manifest_files = ToolUtils.detect_language(CONTAINER_BIND_CODE)
    manifest_clean = ToolUtils.clean_manifest(manifest_files)

    tools = []
    if request.is_json and len(request.get_json()) > 0:
        tools = parse_tools(request.get_json(), langs)
    else:
        app.logger.info("No tools provided. Defaulting to all tools.")
        tools = ToolUtils.get_tools(langs)

    # Run tools and cleanup
    gen_count = ToolUtils.run_tools(tools, manifest_clean, CONTAINER_BIND_CODE, CONTAINER_BIND_SBOM)
    ToolUtils.cleanup()

    # Return 200 (ok) if sboms were generated, otherwise return 204 (no content)
    if gen_count > 0:
        app.logger.info("{} SBOMs generated.", gen_count)
        return "Successfully generated SBOMs.", 200
    else:
        app.logger.info("No SBOMs generated.")
        return "No SBOMs generated.", 204


def parse_tools(tool_names: str, langs: list[Language]) -> list[OSTool]:
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
