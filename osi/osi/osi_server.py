"""
file: osi_server.py

API that exposes endpoints to manage the generations of SBOMs using Open Source Tools inside the svip-osi Docker Container.

@author Ian Dunn
@auther Derek Garcia
"""
import base64
import configparser
import glob
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
    VERSION = "4.1"
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

    @staticmethod
    def _command_exists(command: str) -> bool:
        return shutil.which(command) is not None

    def _run_prepare_command(self, command: List[str], cwd: str, description: str) -> None:
        self._app.logger.info(f"Prepare | Running {description} in {os.path.relpath(cwd, os.environ.get('CODE_IN', cwd))}")
        try:
            result = subprocess.run(
                command,
                cwd=cwd,
                check=True,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
            )
            output = result.stdout.strip()
            if output:
                self._app.logger.info(f"Prepare | {description} output:\n{output}")
            else:
                self._app.logger.info(f"Prepare | {description} completed with no output")
        except FileNotFoundError:
            self._app.logger.warning(f"Prepare | Command not found for {description}")
        except subprocess.CalledProcessError as exc:
            output = exc.stdout.strip() if exc.stdout else "(no output)"
            self._app.logger.warning(
                f"Prepare | {description} failed with exit code {exc.returncode}: {output}")

    def _prepare_project(self, project_dir: str) -> None:
        self._app.logger.info("Prepare | Ensuring dependency metadata for uploaded project")

        def _rel(path: str) -> str:
            try:
                return os.path.relpath(path, project_dir)
            except ValueError:
                return path

        node_manifests = glob.glob(os.path.join(project_dir, "**", "package.json"), recursive=True)
        for manifest in node_manifests:
            base_dir = os.path.dirname(manifest)
            
            # Full install for dependency tree analysis (production only, no scripts)
            if self._command_exists("npm"):
                self._app.logger.info(f"Prepare | Installing dependencies for dependency graph analysis in {_rel(base_dir)}")
                
                # First try npm ci for faster, reproducible installs
                try:
                    result = subprocess.run(
                        ["npm", "ci", "--omit=dev", "--ignore-scripts"],
                        cwd=base_dir,
                        capture_output=True,
                        text=True,
                        timeout=300
                    )
                    if result.returncode != 0:
                        # npm ci failed, try regular npm install as fallback
                        self._app.logger.warning(f"Prepare | npm ci failed in {_rel(base_dir)}, falling back to npm install")
                        
                        # Check if node_modules already exists
                        node_modules_path = os.path.join(base_dir, "node_modules")
                        if os.path.exists(node_modules_path):
                            self._app.logger.info(f"Prepare | Found existing node_modules in {_rel(base_dir)}, will use for scanning")
                        else:
                            # Try npm install as fallback
                            self._run_prepare_command(
                                ["npm", "install", "--omit=dev", "--ignore-scripts"],
                                base_dir,
                                f"npm install --omit=dev ({_rel(base_dir)})",
                            )
                    else:
                        self._app.logger.info(f"Prepare | npm ci succeeded in {_rel(base_dir)}")
                except subprocess.TimeoutExpired:
                    self._app.logger.warning(f"Prepare | npm ci timed out in {_rel(base_dir)}")
                except Exception as e:
                    self._app.logger.warning(f"Prepare | npm ci failed with error: {e}")
                    
            elif self._command_exists("yarn") and os.path.exists(os.path.join(base_dir, "yarn.lock")):
                self._run_prepare_command(
                    ["yarn", "install", "--production", "--frozen-lockfile", "--ignore-scripts"],
                    base_dir,
                    f"yarn install --production ({_rel(base_dir)})",
                )
            elif self._command_exists("pnpm") and os.path.exists(os.path.join(base_dir, "pnpm-lock.yaml")):
                self._run_prepare_command(
                    ["pnpm", "install", "--prod", "--frozen-lockfile", "--ignore-scripts"],
                    base_dir,
                    f"pnpm install --prod ({_rel(base_dir)})",
                )

        go_modules = glob.glob(os.path.join(project_dir, "**", "go.mod"), recursive=True)
        if self._command_exists("go"):
            for go_mod in go_modules:
                base_dir = os.path.dirname(go_mod)
                self._run_prepare_command(["go", "mod", "tidy"], base_dir, f"go mod tidy ({_rel(base_dir)})")

        cargo_manifests = glob.glob(os.path.join(project_dir, "**", "Cargo.toml"), recursive=True)
        if self._command_exists("cargo"):
            for cargo_toml in cargo_manifests:
                base_dir = os.path.dirname(cargo_toml)
                self._run_prepare_command(
                    ["cargo", "generate-lockfile"],
                    base_dir,
                    f"cargo generate-lockfile ({_rel(base_dir)})",
                )

        pyprojects = glob.glob(os.path.join(project_dir, "**", "pyproject.toml"), recursive=True)
        for pyproject in pyprojects:
            base_dir = os.path.dirname(pyproject)
            try:
                with open(pyproject, "r", encoding="utf-8") as handle:
                    pyproject_contents = handle.read()
            except OSError as exc:
                self._app.logger.warning(f"Prepare | Unable to read pyproject.toml at {_rel(pyproject)}: {exc}")
                pyproject_contents = ""

            if "tool.poetry" in pyproject_contents:
                if self._command_exists("poetry"):
                    self._app.logger.info(f"Prepare | Installing Python dependencies via poetry in {_rel(base_dir)}")
                    self._run_prepare_command(
                        ["poetry", "install", "--no-dev", "--no-root"],
                        base_dir,
                        f"poetry install --no-dev ({_rel(base_dir)})",
                    )
                else:
                    self._app.logger.warning(
                        f"Prepare | poetry not available; skipping install for {_rel(base_dir)}")

        pipfiles = glob.glob(os.path.join(project_dir, "**", "Pipfile"), recursive=True)
        for pipfile in pipfiles:
            base_dir = os.path.dirname(pipfile)
            if self._command_exists("pipenv"):
                self._app.logger.info(f"Prepare | Installing Python dependencies via pipenv in {_rel(base_dir)}")
                self._run_prepare_command(
                    ["pipenv", "install", "--deploy", "--ignore-pipfile"],
                    base_dir,
                    f"pipenv install ({_rel(base_dir)})",
                )
            else:
                self._app.logger.warning(
                    f"Prepare | pipenv not available; skipping install for {_rel(base_dir)}")
        
        # Handle requirements.txt for pip-based projects
        requirements_files = glob.glob(os.path.join(project_dir, "**", "requirements.txt"), recursive=True)
        for req_file in requirements_files:
            base_dir = os.path.dirname(req_file)
            # Skip if poetry or pipenv already handled it
            if os.path.exists(os.path.join(base_dir, "pyproject.toml")) or os.path.exists(os.path.join(base_dir, "Pipfile")):
                continue
            if self._command_exists("pip3"):
                self._app.logger.info(f"Prepare | Installing Python dependencies via pip in {_rel(base_dir)}")
                self._run_prepare_command(
                    ["pip3", "install", "--target", os.path.join(base_dir, ".pip-packages"), "-r", req_file, "--no-cache-dir"],
                    base_dir,
                    f"pip install -r requirements.txt ({_rel(base_dir)})",
                )

        dotnet_projects = glob.glob(os.path.join(project_dir, "**", "*.sln"), recursive=True) + \
            glob.glob(os.path.join(project_dir, "**", "*.csproj"), recursive=True)
        if dotnet_projects and self._command_exists("dotnet"):
            processed_paths = set()
            for project in dotnet_projects:
                base_dir = os.path.dirname(project)
                if base_dir not in processed_paths:
                    self._run_prepare_command(["dotnet", "restore"], base_dir, f"dotnet restore ({_rel(base_dir)})")
                    processed_paths.add(base_dir)
        elif dotnet_projects:
            self._app.logger.warning("Prepare | dotnet not available; skipping restore for .NET projects")

        composer_manifests = glob.glob(os.path.join(project_dir, "**", "composer.json"), recursive=True)
        for composer_json in composer_manifests:
            base_dir = os.path.dirname(composer_json)
            if not os.path.exists(os.path.join(base_dir, "composer.lock")):
                if self._command_exists("composer"):
                    self._run_prepare_command(
                        ["composer", "update", "--lock", "--no-interaction"],
                        base_dir,
                        f"composer update --lock ({_rel(base_dir)})",
                    )
                else:
                    self._app.logger.warning(
                        f"Prepare | composer not available; skipping composer.lock generation for {_rel(base_dir)}")

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
            """

            # purge previous project
            _purge_directory(os.environ['CODE_IN'])

            # Extract zip contents
            zip_data = request.get_data()
            try:
                with zipfile.ZipFile(BytesIO(zip_data)) as zip_ref:
                    zip_ref.extractall(os.environ['CODE_IN'])
                self._app.logger.info("Extracted project successfully")
                try:
                    self._prepare_project(os.environ['CODE_IN'])
                except Exception as prep_error:
                    self._app.logger.warning(f"Prepare | Failed to prepare project: {prep_error}")
                return 'Zip extracted successfully', 201
            except Exception as e:
                self._app.logger.error(f"Failed to extract project: {e}")
                return 'Invalid ZIP file', 400

        @self._app.route('/generate', methods=['POST'])
        def generate():
            """
            Endpoint: POST /generate

            Request Body: A JSON list of tool names to use in generation. If null, defaults to all tools.
            Returns:      list of JSON of file name and base64 encoding
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
                tool_profiles = []
                for tool_name in tool_names:
                    tool_profiles.extend(self._available_tools[tool_name].profiles)

            else:
                # No tools provided, default to all relevant tools to the project
                self._app.logger.info("Generate | No tools provided; Defaulting to relevant tools.")
                tool_profiles = self._get_applicable_tools()

            # Check to make sure there are tools that can be used
            if not tool_profiles:
                self._app.logger.error("Generate | No tools selected")
                return "No tools selected", 422

            _purge_directory(os.environ['SBOM_OUT'])
            self._app.logger.info(f"Generate | Running with tools: { {p.name for p in tool_profiles} }")
            osi_start = time.time()
            success, fail = set(), set()
            # Execute each run profile
            for tool_profile in tool_profiles:
                try:
                    self._app.logger.info(
                        f"Generate | Executing {tool_profile} with command string: "
                        f"{' && '.join(tool_profile.commands)}")
                    start_time = time.time()
                    tool_profile.execute(os.environ['CODE_IN'])  # execute run commands set in the tool config
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

            # encode results
            sbom_data = {}
            for filename in os.listdir(os.environ['SBOM_OUT']):
                file_path = os.path.join(os.environ['SBOM_OUT'], filename)
                if os.path.isfile(file_path):
                    with open(file_path, "rb") as f:
                        encoded_content = base64.b64encode(f.read()).decode('utf-8')
                        sbom_data[filename] = encoded_content

            # Return 200 (ok) if sboms were generated, otherwise return 204 (no content)
            return jsonify(sbom_data), 200 if generated_sboms > 0 else 204

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
