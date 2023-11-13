"""
file:

Collection of utilities to validate, run, and organize the output of Open Source SBOM tools.

@author Derek Garcia
"""
import os

import yaml

from constants import Schema, Format

TOOL_CONFIGS = "../tool_configs"


class Profile(object):

    def __init__(self, schema: Schema, spec_version: str, sbom_format: Format, args: dict, commands: list[str]):
        self.schema = schema
        self.spec_version = spec_version
        self.format = sbom_format

        # update commands with args
        self.commands = []
        for cmd in commands:
            for arg in args.keys():
                cmd = cmd.replace(f"{{{arg}}}", args.get(arg))
            self.commands.append(cmd)

    def execute(self):
        for cmd in self.commands:
            os.system(cmd)


class Tool(object):
    def __int__(self, config_file: str):
        self.profiles = []

        try:
            with open(config_file) as config:
                data = yaml.safe_load(config)
                self.name = data['tool']

                # parse all profiles
                for profile in data['profiles']:
                    self.profiles.append( Profile(
                        Schema.to_schema(profile['schema']),
                        profile['spec_version'],
                        Format.to_format(profile['format']),
                        profile['args'],
                        profile['commands']
                    ))
        except:
            raise Exception(f"Error while parsing '{config_file}'")
        
