"""
file:

Collection of utilities to validate, run, and organize the output of Open Source SBOM tools.

@author Derek Garcia
"""
import configparser
import subprocess
import sys

import yaml

SBOM_CONFIG = "configs/sbom.cfg"
TOOL_CONFIGS_DIR = "tool_configs"


class RunConfig(object):
    def __init__(self, languages: set[str], package_managers: set[str]):
        self.languages = languages
        self.package_managers = package_managers


class Profile(object):

    def __init__(self, name: str, schema: str, spec_version: str, sbom_format: str, languages: set[str],
                 package_managers: set[str],
                 commands: list[str]):
        self.name = name
        self.schema = schema
        self.spec_version = spec_version
        self.format = sbom_format
        self.package_managers = package_managers
        self.languages = languages
        self.commands = commands

    def match(self, run_config: RunConfig) -> bool:
        if any(lang in run_config.languages for lang in self.languages):
            return True
        if any(pm in run_config.package_managers for pm in self.package_managers):
            return True
        return False

    def execute(self, location: str):
        cmd = self.build_exe_string(location)
        try:
            subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True, timeout=120, universal_newlines=True)
        except subprocess.CalledProcessError as exc:
            raise Exception(f"Error during execution: {exc.output}")
        except subprocess.TimeoutExpired:
            raise Exception("Exceeded allowed runtime")

    def build_exe_string(self, location: str):
        cmds = f"cd {location} && "
        cmds += " && ".join(self.commands)
        return cmds
    def __str__(self):
        return f"{self.name} : {self.schema} : {self.spec_version} : {self.format}"


class Tool(object):
    def __init__(self, name: str):
        self.name = name
        self.profiles = []

    def add_profile(self, profile: Profile) -> None:
        self.profiles.append(profile)

    def get_matching_profiles(self, run_config: RunConfig) -> list[Profile]:
        matches = []
        for profile in self.profiles:
            if profile.match(run_config):
                matches.append(profile)

        return matches

    def __str__(self):
        return f"{self.name}"


class ToolFactory(object):

    def __init__(self):
        self.sbom_config = self.load_config(SBOM_CONFIG)

    def build_tool(self, name: str) -> Tool | None:
        tool = Tool(name)
        try:
            with open(f"{TOOL_CONFIGS_DIR}/{name}.yml") as config:
                data = yaml.safe_load(config)

                # parse all profiles
                for profile_data in data['profiles']:
                    try:
                        profile = self.build_profile(name, profile_data)
                        tool.add_profile(profile)
                    except Exception as e:
                        print(f"Failed to parse profile: {e}", file=sys.stderr)
        except FileNotFoundError as e:
            raise FileNotFoundError(f"'{name}.yml' was not found: {e}")
        except Exception as e:
            raise Exception(f"Failed to parse '{name}.yml: {e}")

        return tool

    def build_profile(self, tool: str, profile_data) -> Profile:

        try:
            schema = profile_data['schema'].lower()
            spec_version = profile_data['spec_version'].lower()
            format = profile_data['format'].lower()

            languages = set(map(lambda value: value.lower(), profile_data['languages'])) \
                if 'languages' in profile_data else set()

            package_managers = set(map(lambda value: value.lower(), profile_data['package_managers'])) \
                if 'package_managers' in profile_data else set()

            commands = profile_data['commands']
        except Exception as e:
            raise Exception(f"Missing required field: {e}")

        if not self.sbom_config.has_section(f'{schema}.format'):
            raise Exception(f"SBOM config section missing '{schema}.format'; Has it been added to the config?")

        if not self.sbom_config.has_section(f'{schema}.spec_version'):
            raise Exception(f"SBOM config section missing '{schema}.spec_version'; Has it been added to the config?")

        if spec_version not in dict(self.sbom_config[f'{schema}.spec_version']):
            raise Exception(
                f"'{schema}.spec_version does not support version '{spec_version}'; Has it been added to the config?")

        if format not in dict(self.sbom_config[f'{schema}.format']):
            raise Exception(
                f"'{schema}.spec_version does not support '{format}'format; Has it been added to the config?")

        return Profile(tool, schema, spec_version, format, languages, package_managers, commands)

    def load_config(self, config_path: str) -> configparser:
        cfg = configparser.ConfigParser(allow_no_value=True)
        cfg.read(config_path)
        return cfg
