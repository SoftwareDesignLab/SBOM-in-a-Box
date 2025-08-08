"""
file: osi_server.py

API that exposes endpoints to manage the generations of SBOMs using Open Source Tools inside the svip-osi Docker Container.

@author Ian Dunn
@auther Derek Garcia
"""
import configparser
import os
import shutil
import subprocess
import time
import zipfile
from io import BytesIO
from typing import List, Dict

from flask import Flask, request, jsonify

from tool_factory import ToolFactory, RunConfig, Profile, Tool

# Extension Configuration files
LANGUAGE_EXT_CONFIG = os.path.join(os.path.dirname(os.path.abspath(__file__)), "configs", "language_ext.cfg")
MANIFEST_EXT_CONFIG = os.path.join(os.path.dirname(os.path.abspath(__file__)), "configs", "manifest_ext.cfg")

# SED pattern to get file name from path
FILE_NAME_SED_PATTERN = r's|.*\/||'


class OSIAPIServer:
    VERSION = "4.0"
    DEFAULT_FLASK_HOST = "localhost"
    DEFAULT_FLASK_PORT = 5000

    def __init__(self,
                 host: str = DEFAULT_FLASK_HOST,
                 port: int = DEFAULT_FLASK_PORT,
                 debug: bool = True):
        """
        Create a new Flask API Server for OSI

        :param host: Host of flask server (Default: localhost)
        :param port: Port of flask server (Default: 5000)
        :param debug: Enable debug mode (Default: False)
        """
        # flask setup
        self._app = Flask(__name__)
        # flask config
        self._host = host
        self._port = port
        self._debug = debug
        # extensions maps
        self._language_map = _load_ext_mapper(LANGUAGE_EXT_CONFIG)
        self._manifest_map = _load_ext_mapper(MANIFEST_EXT_CONFIG)
        # available tools
        self._available_tools = _load_available_tools()
        # create endpoints
        self._setup_routes()

    def _get_applicable_tools(self) -> List[Profile]:
        """
        Looks at the files stored in the code bound directory
        and determines which tool run profiles apply

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
            # Use extension to determine language
            ext = f".{file_name.lower().split('.')[-1]}"
            if ext in self._language_map:
                languages.add(self._language_map.get(ext))

            # Use filename to determine package manager
            if file_name.lower() in self._manifest_map:
                package_managers.add(self._manifest_map.get(file_name.lower()))

        self._app.logger.info(f"Applicable Tools | Detected languages: {languages}")
        self._app.logger.info(f"Applicable Tools | Detected package managers: {package_managers}")

        # Make a run config with the info found and get all matching run profiles
        run_config = RunConfig(languages, package_managers)
        tools = []
        for tool in self._available_tools.values():
            tools += tool.get_matching_profiles(run_config)
        return tools

    def _setup_routes(self):
        @self._app.route('/healthcheck', methods=['GET'])
        def healthcheck():
            """
            Simple healthcheck to determine if server is up
            """
            return jsonify(status="ok"), 200

        @self._app.route('/tools', methods=['GET'])
        def get_tools():
            """
            Endpoint: GET /tools
            Default returns all tools installed in the OSI instance Has one

            optional request param: list
            - /tools?list=all : get all tools installed in the OSI instance
            - /tools?list=project : get all tools installed that can be used with the project in the bound directory

            Returns: A list of names of valid open-source tools.
            """
            match request.args.get('list', 'all'):
                case 'all':
                    return sorted(list(self._available_tools.keys())), 200
                case 'project':
                    tool_names = list({t.name for t in self._get_applicable_tools()})  # remove duplicate tool names
                    return sorted(tool_names), 200
                case _:
                    return f"'{request.args.get('list')}' is an unknown param", 400

        @self._app.route('/upload', methods=['POST'])
        def upload_project():
            """
            Endpoint: POST /upload

            Upload a project to generate SBOMs for

            Request Body:
                - archive: zip file containing a project

            Returns: A list of names of valid open-source tools.
            """

            # purge previous project
            _purge_directory(os.environ['CODE_IN'])

            # Extract zip contents
            zip_data = request.get_data()
            try:
                with zipfile.ZipFile(BytesIO(zip_data)) as zip_ref:
                    zip_ref.extractall(os.environ['CODE_IN'])
                self._app.logger.info("Extracted project successfully")
                return 'Zip extracted successfully', 201
            except Exception as e:
                self._app.logger.error(f"Failed to extract project: {e}")
                return 'Invalid ZIP file', 400

        @self._app.route('/generate', methods=['POST'])
        def generate():
            """
            Endpoint: POST /generate

            Request Body: A JSON list of tool names to use in generation. If null, defaults to all tools.
            Returns:      Number of SBOMs generated
            Returns:      200 if SBOMs were generated, 204 otherwise.
            """
            # Parse request body if one is provided
            if request.is_json:
                try:
                    # Get tools and create a list to lowercase strings
                    tool_names = {t.lower() for t in request.get_json()['tools']}
                    self._app.logger.info(f"Attempting to use provided tools: {', '.join(tool_names)}")

                    # Check to see if attempting to use any unknown tools
                    tool_diff = tool_names - self._available_tools.keys()
                    if tool_diff:
                        self._app.logger.error(
                            f"Generate | Attempting to use unknown or unavailable tools: {tool_diff}")
                        return f"Attempting to use unknown or unavailable tools: {tool_diff}", 400

                except Exception as e:
                    self._app.logger.error(f"Generate | Failed to parse json: {e}")
                    return "Failed to parse tools", 400
                # fetch profiles
                tool_profiles = [self._available_tools[tool_name].profile for tool_name in tool_names]

            else:
                # No tools provided, default to all relevant tools to the project
                self._app.logger.info("Generate | No tools provided; Defaulting to relevant tools.")
                tool_profiles = self._get_applicable_tools()

            # Check to make sure there are tools that can be used
            if not tool_profiles:
                self._app.logger.error("Generate | No tools selected")
                return "No tools selected", 422

            self._app.logger.info(f"Generate | Running with tools: { {p.name for p in tool_profiles} }")
            osi_start = time.time()
            success, fail = set(), set()
            # Execute each run profile
            for tool_profile in tool_profiles:
                try:
                    self._app.logger.info(
                        f"Generate | Executing {tool_profile} with command string: "
                        f"{' '.join(tool_profile.commands('$CODE_IN'))}")
                    start_time = time.time()
                    tool_profile.execute('$CODE_IN')  # execute run commands set in the tool config
                    self._app.logger.info(f"Generate | Completed in {time.time() - start_time:.2f} seconds")
                    success.add(tool_profile.name)

                except Exception as e:
                    # Problem when running tool
                    self._app.logger.error(f"Generate | Failed to generate with {tool_profile.name}: {e}")
                    fail.add(tool_profile.name)

            osi_end = time.time()
            generated_sboms = len(success)
            # Report summary
            self._app.logger.info(f"Generate | COMPLETED")
            self._app.logger.info(f"Generate | {generated_sboms} SBOMs generated in {osi_end - osi_start:.2f} seconds")
            self._app.logger.info(f"Generate | Success Tools: {len(success)} | {success}")
            self._app.logger.info(f"Generate | Failed Tools: {len(fail)} | {fail}")

            # Return 200 (ok) if sboms were generated, otherwise return 204 (no content)
            return str(generated_sboms), 200 if generated_sboms > 0 else 204

    def run(self):
        """
        Launch the server
        """
        print(f"Running OSIv{self.VERSION} with {list(self._available_tools.keys())}")
        self._app.run(host=self._host, port=self._port, debug=self._debug, use_reloader=False)


#
# HELPER METHODS
#

def _load_ext_mapper(config_file: str) -> Dict[str, str]:
    """
    Load the extension / file config files into memory

    :param config_file: Path to config file
    :return: Dict of extension and value
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
        ext_map.update({key: sec.lower() for key, _ in cfg.items(sec)})

    return ext_map


def _load_available_tools() -> Dict[str, Tool]:
    """
    Parse the 'OSI_TOOL' env variable to init the
    tools available to use

    :return: Dict of tool name and tool object
    """
    tf = ToolFactory()
    # 'OSI_TOOL' set with validate.sh
    return {tool_name: tf.build_tool(tool_name) for tool_name in os.environ['OSI_TOOL'].split(":")}


def _purge_directory(target_dir: str) -> None:
    """
    Purge a directory's contents

    :param target_dir: Target directory to purge
    """
    for filename in os.listdir(target_dir):
        file_path = os.path.join(target_dir, filename)
        if os.path.isfile(file_path) or os.path.islink(file_path):
            os.unlink(file_path)  # remove file or symlink
        elif os.path.isdir(file_path):
            shutil.rmtree(file_path)  # remove directory
