"""
file: OSI.py

API that exposes endpoints to manage the generations of SBOMs using Open Source Tools inside the svip-osi Docker
Container.

@author Ian Dunn
"""

from flask import Flask
from constants import CONTAINER_BIND_CODE, CONTAINER_BIND_SBOM
from ToolMapper import get_tool, get_tool_names
from ToolUtils import cleanup, detect_language, clean_manifest, get_tools, run_tools

# Create Flask app
app = Flask(__name__)


@app.route('/tools', methods=['GET'])
def get_tools():
    """
    Endpoint: GET http://localhost:8081/tools

    Returns: A list of names of valid open-source tools.
    """

    return get_tool_names()


@app.route('/generate', methods=['GET'])
def generate(tool_names):
    """
    Endpoint: GET http://localhost:8081/generate

    Parameter: A list of tool names to use in generation. If null, defaults to all tools.
    Returns:   Generated SBOMs in osi/bound_dir/sboms
    """

    # Cleanup, detect languages
    cleanup(True)
    langs, manifest_files = detect_language(CONTAINER_BIND_CODE)
    manifest_clean = clean_manifest(manifest_files)

    # Get valid tools
    valid_tools = get_tools(langs)
    tools = list()

    # Select tools given names
    print("Tools selected:")
    for name in tool_names:
        # Make sure tool exists
        tool = get_tool(name)
        if tool is None:
            print(name, " -- Invalid tool name. Skipping.")

        # Add tool if valid
        if tool in valid_tools:
            tools.append(tool)
            print(name, end=", ")
        else:
            print(name, " -- Invalid tool for detected languages. Skipping.")

    print()

    # Run tools and cleanup
    gen_count = run_tools(tools, manifest_clean, CONTAINER_BIND_CODE, CONTAINER_BIND_SBOM)
    cleanup()

    # Return 0 if sboms were generated, otherwise return 1
    return int(not (gen_count > 0))


if __name__ == '__main__':
    app.run(debug=True)
