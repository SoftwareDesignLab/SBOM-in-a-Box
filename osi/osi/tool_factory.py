"""
file: tool_factory.py

Parse tool configuration files into usable objects

@author Derek Garcia
"""
import configparser
import logging
import subprocess
from typing import Dict, Any, Set, List

import yaml

# Constants
SBOM_CONFIG_FILE = "configs/sbom.cfg"
TOOL_CONFIGS_DIR = "configs/tools"


class RunConfig:
    """
    Run Config to match against tool profiles to see if they're applicable
    Can add other fields for more granular filters
    """

    def __init__(self, languages: Set[str], package_managers: Set[str]):
        """
        Run Config Constructor

        :param languages: List of languages to match against
        :param package_managers: List of package managers to match against
        """
        self._languages = languages
        self._package_managers = package_managers

    @property
    def languages(self):
        return self._languages

    @property
    def package_managers(self):
        return self._package_managers


class Profile:
    """
    A Profile is configured to use a tool in a certain way.
    Ex, 1 profile to generate in JSON and another for XML, but both use the same tool
    """
    # runtime is set to 2 minutes
    DEFAULT_TIMEOUT = 900

    def __init__(self,
                 name: str,
                 schema: str,
                 spec_version: str,
                 sbom_format: str,
                 languages: Set[str],
                 package_managers: Set[str],
                 commands: List[str]):
        """
        Profile Constructor

        :param name: Name of tool
        :param schema: SBOM schema as defined in sbom.cfg
        :param spec_version: Spec version as defined in sbom.cfg
        :param sbom_format: SBOM format as defined in sbom.cfg
        :param languages: List of languages this profile supports
        :param package_managers: List of package managers this profile supports
        :param commands: List of bash commands to use this tool
        """
        self._name = name
        self._schema = schema
        self._spec_version = spec_version
        self._format = sbom_format
        self._package_managers = package_managers
        self._languages = languages
        self._commands = commands

    def match(self, run_config: RunConfig) -> bool:
        """
        Check to see if this profile matches the run config

        :param run_config: Run Config to check
        :return: True if matches, false otherwise
        """
        # Check languages
        if any(lang in run_config.languages for lang in self._languages):
            return True
        # Check Package manages
        if any(pm in run_config.package_managers for pm in self._package_managers):
            return True
        # Does not match
        return False

    def execute(self, cwd: str, timeout: int = DEFAULT_TIMEOUT) -> None:
        """
        Execute the commands to run the tool

        :param cwd: Current working directory to run the commands in
        :param timeout: Time of the command operation
        :raises CalledProcessError: If failed to exec tool
        :raises TimeoutExpired: if timeout is reached
        """
        subprocess.run(
            self._commands,
            cwd=cwd,
            check=True,
            timeout=timeout
        )

    def __str__(self):
        return f"[{self._name} : {self._schema} : {self._spec_version} : {self._format}]"

    def __repr__(self):
        return self.__str__()


class Tool:
    """
    A tool represents an SBOM generation tool that has one or more profiles with run instructions
    """

    def __init__(self, name: str, source_url: str):
        """
        Tool constructor

        :param name: Name of tool
        :param source_url: Source of tool
        """
        self._name = name
        self._source_url = source_url
        self._profiles: List[Profile] = []

    def get_matching_profiles(self, run_config: RunConfig) -> List[Profile]:
        """
        Search profiles for any that match to given run config

        :param run_config: Run Config to check
        :return: list of matching run profiles
        """
        return [p for p in self._profiles if p.match(run_config)]

    @property
    def profile(self):
        return self._profiles

    def __str__(self):
        return f"{self._name}"

    def __repr__(self):
        return self.__str__()


class ToolFactory:
    """
    Factory to parse tool config files into Tool objects
    """

    def __init__(self, sbom_config_file: str = SBOM_CONFIG_FILE):
        """
        ToolFactory Constructor

        :param sbom_config_file: Optional path to sbom config file
        """
        # load sbom details config file
        self._sbom_cfg = configparser.ConfigParser(allow_no_value=True)
        self._sbom_cfg.read(sbom_config_file)

    def _build_profile(self, name: str, profile_data: Dict[str, Any]) -> Profile:
        """
        Build a tool run profile using data from the tool config file

        :param name: name of tool
        :param profile_data: profile data from the tool config file
        :raises KeyError: If the tool config file is missing a required field
        :raises Exception: If tool attempts to use an unsupported SBOM format, version, etc
        :return: new run profile
        """
        try:
            # Get required args
            schema = profile_data['schema'].lower()
            spec_version = profile_data['spec_version'].lower()
            sbom_format = profile_data['format'].lower()
            commands = profile_data['commands']

            # Get optional languages arg and lower all values
            languages = {l.lower() for l in profile_data.get('languages', [])}

            # Get optional package managers arg and lower all values
            package_managers = {pm.lower() for pm in profile_data.get('package_managers', [])}
        except KeyError as e:
            # Missing a required field
            raise KeyError(f"Missing required field: {e.args[0]}")

        # Check schema has been added to sbom.cfg
        if not self._sbom_cfg.has_section(f'{schema}.format'):
            raise Exception(f"SBOM config section missing '{schema}.format'; Has it been added to the config?")
        # Check schema has been added to sbom.cfg
        if not self._sbom_cfg.has_section(f'{schema}.spec_version'):
            raise Exception(f"SBOM config section missing '{schema}.spec_version'; Has it been added to the config?")

        # Check if the spec version is valid for the schema
        if spec_version not in set(self._sbom_cfg[f'{schema}.spec_version']):
            raise Exception(
                f"'{schema}.spec_version does not support version '{spec_version}'; Has it been added to the config?")

        # Check if the format is valid for the schema
        if sbom_format not in set(self._sbom_cfg[f'{schema}.format']):
            raise Exception(
                f"'{schema}.spec_version does not support '{sbom_format}'format; Has it been added to the config?")

        # All checks pass
        return Profile(name, schema, spec_version, sbom_format, languages, package_managers, commands)

    def build_tool(self, name: str) -> Tool:
        """
        Attempt to build a tool with the given name.
        The name param MUST match the config file name in the 'tool_configs' directory or parsing will fail
        Ex name=foo, config file=foo.yml

        :param name: Name of tool config file to search for
        :raise FileNotFoundError: Tool config file was not found
        :return: Tool if successfully parse config file, error and NONE otherwise
        """

        try:
            # Attempt to parse tool config file
            with open(f"{TOOL_CONFIGS_DIR}/{name}.yml") as config:
                data = yaml.safe_load(config)
                tool = Tool(name, data['source'])
                # parse all profiles
                for profile_data in data['profiles']:
                    try:
                        profile = self._build_profile(name, profile_data)
                        tool.profile.append(profile)
                    except Exception as e:
                        # Fail to parse profile but move onto next
                        logging.error(f"Failed to parse profile: {e}")
        except FileNotFoundError as e:
            # Missing / Invalid tool config file
            raise FileNotFoundError(f"'{name}.yml' was not found: {e}")
        except Exception as e:
            # Error parsing too config file
            raise Exception(f"Failed to parse '{name}.yml: {e}")

        return tool
