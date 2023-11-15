"""
file:

Collection of utilities to validate, run, and organize the output of Open Source SBOM tools.

@author Derek Garcia
"""
import configparser
import os
import sys

import yaml

LANGUAGE_EXT_CONFIG = "../constant/configs/language_ext.cfg"
MANIFEST_EXT_CONFIG = "../constant/configs/manifest_ext.cfg"
SBOM_CONFIG = "constant/configs/sbom.cfg"
TOOL_CONFIGS_DIR = "tool_configs"


class Profile(object):

    def __init__(self, schema: str, spec_version: str, sbom_format: str, manifests: list[str], languages: list[str],
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

    def load_config(self, config_path: str) -> configparser:
        cfg = configparser.ConfigParser(allow_no_value=True)
        cfg.read(config_path)
        return cfg

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
            raise FileNotFoundError(f"'{name}.yml' was not found: {e}")
        return tool

    def build_profile(self, profile_data) -> Profile:
        schema = profile_data['schema'].lower()
        spec_version = profile_data['spec_version'].lower()
        format = profile_data['format'].lower()

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

        return Profile(schema, spec_version, format, profile_data['package_manager'], profile_data['language'],
                       profile_data['commands'])


def load_ext_mapper(config_file: str):
    cfg = configparser.ConfigParser(allow_no_value=True)
    cfg.read(config_file)
    ext_map = {}
    for sec in cfg.sections():
        ext_map.update(
            dict((key, sec.lower()) for key, y in cfg.items(sec))  # ignore y since empty
        )
    return ext_map
