"""
file: ToolFactory.py

Parse tool configuration files into usable objects

@author Derek Garcia
"""
import configparser
import subprocess
import sys

import yaml

# Constants
SBOM_CONFIG = "configs/sbom.cfg"
TOOL_CONFIGS_DIR = "tool_configs"


class RunConfig(object):
    """
    Run Config to match against tool profiles to see if they're applicable
    Can add other fields for more granular filters
    """

    def __init__(self, languages: set[str], package_managers: set[str]):
        """
        Run Config Constructor
        :param languages: List of languages to match against
        :param package_managers: List of package managers to match against
        """
        self.languages = languages
        self.package_managers = package_managers


class Profile(object):
    """
    A Profile is configured to use a tool in a certain way.
    Ex, 1 profile to generate in JSON and another for XML, but both use the same tool
    """

    def __init__(self, name: str, schema: str, spec_version: str, sbom_format: str, languages: set[str],
                 package_managers: set[str],
                 commands: list[str]):
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
        self.name = name
        self.schema = schema
        self.spec_version = spec_version
        self.format = sbom_format
        self.package_managers = package_managers
        self.languages = languages
        self.commands = commands

    def match(self, run_config: RunConfig) -> bool:
        """
        Check to see if this profile matches the run config
        :param run_config: Run Config to check
        :return: True if matches, false otherwise
        """
        # Check languages
        if any(lang in run_config.languages for lang in self.languages):
            return True
        # Check Package manages
        if any(pm in run_config.package_managers for pm in self.package_managers):
            return True
        # Does not match
        return False

    def execute(self, run_location: str) -> None:
        """
        Execute the commands to run the tool
        :param run_location: Path to where to execute the commands
        :return: NONE; raise an error if fails or times out
        """
        # Build and run the command
        cmd = self.build_exe_string(run_location)
        try:
            subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True, timeout=120, universal_newlines=True)
        except subprocess.CalledProcessError as exc:
            raise Exception(f"Error during execution: {exc.output}")
        except subprocess.TimeoutExpired:
            raise Exception("Exceeded allowed runtime")  # runtime is set to 2 minutes

    def build_exe_string(self, run_location: str) -> str:
        """
        Build a single execution string from the list of stored commands
        :param run_location: Path to where to execute the commands
        :return: Command string chained with the '&&' operator
        """
        cmds = f"cd {run_location} && "  # change into target directory
        cmds += " && ".join(self.commands)  # chain run commands
        return cmds

    def __str__(self):
        return f"[{self.name} : {self.schema} : {self.spec_version} : {self.format}]"

    def __repr__(self):
        return self.__str__()


class Tool(object):
    """
    A tool represents an SBOM generation tool that has one or more profiles with run instructions
    """
    def __init__(self, name: str, source_url: str):
        """
        Tool constructor
        :param name: Name of tool
        :param source_url: Source of tool
        """
        self.name = name
        self.source_url = source_url
        self.profiles = []

    def add_profile(self, profile: Profile) -> None:
        """
        Add a run profile to the tool
        :param profile: Profile to add
        :return: None
        """
        self.profiles.append(profile)

    def get_matching_profiles(self, run_config: RunConfig) -> list[Profile]:
        """
        Search profiles for any that match to given run config
        :param run_config: Run Config to check
        :return: list of matching run profiles
        """
        matches = []
        for profile in self.profiles:
            if profile.match(run_config):
                matches.append(profile)

        return matches

    def __str__(self):
        return f"{self.name}"

    def __repr__(self):
        return self.__str__()


class ToolFactory(object):
    """
    Factory to parse tool config files into Tool objects
    """
    def __init__(self):
        """
        ToolFactory Constructor
        """
        self.sbom_config = self.load_config(SBOM_CONFIG)    # load sbom details config file

    def build_tool(self, name: str) -> Tool | None:
        """
        Attempt to build a tool with the given name.
        The name param MUST match the config file name in the 'tool_configs' directory or parsing will fail
        Ex name=foo, config file=foo.yml
        :param name: Name of tool config file to search for
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
                        profile = self.build_profile(name, profile_data)
                        tool.add_profile(profile)
                    except Exception as e:
                        # Fail to parse profile but move onto next
                        print(f"Failed to parse profile: {e}", file=sys.stderr)
        except FileNotFoundError as e:
            # Missing / Invalid tool config file
            raise FileNotFoundError(f"'{name}.yml' was not found: {e}")
        except Exception as e:
            # Error parsingl too config file
            raise Exception(f"Failed to parse '{name}.yml: {e}")

        return tool

    def build_profile(self, name: str, profile_data) -> Profile:
        """
        Build a tool run profile using data from the tool config file
        :param name: name of tool
        :param profile_data: profil data from the tool config file
        :return: new run profile, error otherwise
        """
        try:
            # Get required args
            schema = profile_data['schema'].lower()
            spec_version = profile_data['spec_version'].lower()
            format = profile_data['format'].lower()
            commands = profile_data['commands']

            # Get optional languages arg and lower all values
            languages = set(map(lambda value: value.lower(), profile_data['languages'])) \
                if 'languages' in profile_data else set()

            # Get optional package managers arg and lower all values
            package_managers = set(map(lambda value: value.lower(), profile_data['package_managers'])) \
                if 'package_managers' in profile_data else set()

        except Exception as e:
            # Missing a require file
            raise Exception(f"Missing required field: {e}")

        # Check schema has been added to sbom.cfg
        if not self.sbom_config.has_section(f'{schema}.format'):
            raise Exception(f"SBOM config section missing '{schema}.format'; Has it been added to the config?")
        # Check schema has been added to sbom.cfg
        if not self.sbom_config.has_section(f'{schema}.spec_version'):
            raise Exception(f"SBOM config section missing '{schema}.spec_version'; Has it been added to the config?")

        # Check if the spec version is valid for the schema
        if spec_version not in dict(self.sbom_config[f'{schema}.spec_version']):
            raise Exception(
                f"'{schema}.spec_version does not support version '{spec_version}'; Has it been added to the config?")

        # Check if the format is valid for the schema
        if format not in dict(self.sbom_config[f'{schema}.format']):
            raise Exception(
                f"'{schema}.spec_version does not support '{format}'format; Has it been added to the config?")

        # All checks pass
        return Profile(name, schema, spec_version, format, languages, package_managers, commands)

    def load_config(self, config_path: str) -> configparser:
        """
        Util for loading configparser
        :param config_path: Path to config file
        :return: new config parser
        """
        cfg = configparser.ConfigParser(allow_no_value=True)
        cfg.read(config_path)
        return cfg
