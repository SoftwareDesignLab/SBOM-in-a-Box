"""
file:

Collection of utilities to validate, run, and organize the output of Open Source SBOM tools.

@author Derek Garcia
"""
import configparser
import os
import sys

import yaml

SBOM_CONFIG = "constant/configs/sbom.cfg"
TOOL_CONFIGS_DIR = "tool_configs"


class Profile(object):

    def __init__(self, schema: str, spec_version: str, sbom_format: str, languages: set[str], manifests: set[str],
                 commands: list[str]):
        self.schema = schema
        self.spec_version = spec_version
        self.format = sbom_format
        self.manifests = manifests
        self.languages = languages
        self.commands = commands

    def execute(self):
        for cmd in self.commands:
            os.system(cmd)

    def __str__(self):
        return f"{self.schema} : {self.spec_version} : {self.format}"


class Tool(object):
    def __init__(self, name: str):
        self.name = name
        self.profiles = []

    def add_profile(self, profile: Profile):
        self.profiles.append(profile)

    def __str__(self):
        return f"{self.name}"


class ToolFactory(object):

    def __init__(self):
        self.sbom_config = self.load_config(SBOM_CONFIG)

    def build_tool(self, name: str) -> Tool:
        tool = Tool(name)
        try:
            with open(f"{TOOL_CONFIGS_DIR}/{name}.yml") as config:
                data = yaml.safe_load(config)

                # parse all profiles
                for profile_data in data['profiles']:
                    try:
                        profile = self.build_profile(profile_data)
                        tool.add_profile(profile)
                    except Exception as e:
                        print(f"Failed to parse profile: {e}", file=sys.stderr)
        except FileNotFoundError as e:
            print(f"'{name}.yml' was not found: {e}", file=sys.stderr)
        return tool

    def build_profile(self, profile_data) -> Profile:

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

        return Profile(schema, spec_version, format, languages, package_managers, commands)

    def load_config(self, config_path: str) -> configparser:
        cfg = configparser.ConfigParser(allow_no_value=True)
        cfg.read(config_path)
        return cfg



