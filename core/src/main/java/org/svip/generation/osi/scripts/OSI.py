"""
file: OSI.py

API that exposes endpoints to manage the generations of SBOMs using Open Source Tools inside the svip-osi Docker
Container.

@author Ian Dunn
"""

from flask import Flask, jsonify, request
from config import CONTAINER_BIND_CODE, CONTAINER_BIND_SBOM
from ToolUtils import cleanup, detect_language, clean_manifest, get_tools, run_tools

# Create Flask app
app = Flask(__name__)


@app.route('/tools', methods=['GET'])
def getTools():
    """
    Endpoint: GET http://localhost:8081/tools

    Returns: A list of valid open-source tools.
    """

    if request.method == 'GET':
        data = "hello world"
        return jsonify({'data': data})


@app.route('/generate', methods=['GET'])
def generate(tools):
    """
    Endpoint: GET http://localhost:8081/generate

    Parameter: A list of tool IDs to use in generation. If null, defaults to all tools.
    Returns:   Generated SBOMs in osi/bound_dir/sboms
    """

    cleanup(True)
    langs, manifest_files = detect_language(CONTAINER_BIND_CODE)
    manifest_clean = clean_manifest(manifest_files)
    tools = get_tools(langs)
    print("Will use:")
    for i in tools:
        print(i.name, end=", ")
    print()
    gen_count = run_tools(tools, manifest_clean, CONTAINER_BIND_CODE, CONTAINER_BIND_SBOM)
    cleanup()

    # Return 0 if sboms were generated, otherwise return 1
    return int(not (gen_count > 0))


if __name__ == '__main__':
    app.run(debug=True)
