"""
file:

Collection of utilities to validate, run, and organize the output of Open Source SBOM tools.

@author Derek Garcia
"""
import os

import yaml

from constants import Schema, Format, Language


class Profile(object):

    def __init__(self, schema: Schema, spec_version: str, sbom_format: Format, languages: list[Language],
                 commands: list[str]):
        self.schema = schema
        self.spec_version = spec_version
        self.format = sbom_format
        self.languages = languages
        self.commands = commands

    def execute(self, runtime_args: dict):

        runtime_cmd = []
        # Pass 1: Update args
        for cmd in self.commands:
            for arg in runtime_args.keys():
                cmd = cmd.replace(f"{{{arg}}}", runtime_args.get(arg))
            runtime_cmd.append(cmd)

        # Pass 2: Execute
        for cmd in runtime_cmd:
            os.system(cmd)


class Tool(object):
    def __init__(self, config_file: str):
        self.profiles = []
        self.languages = []

        try:
            with open(config_file) as config:
                data = yaml.safe_load(config)
                self.name = data['tool']
                self.runtime_args = data['runtime_args']

                # parse all profiles
                for profile in data['profiles']:
                    lang = []
                    for lang_str in profile['language']:
                        lang_enum = Language.to_language(lang_str)
                        lang.append(lang_enum)
                        if lang_enum not in self.languages:
                            self.languages.append(lang_enum)

                    self.profiles.append(Profile(
                        Schema.to_schema(profile['schema']),
                        profile['spec_version'],
                        lang,
                        Format.to_format(profile['format']),
                        profile['commands']
                    ))
        except:
            raise Exception(f"Error while parsing '{config_file}'")

    def execute(self, runtime_args=None):
        # pre-checks to make sure args are defined
        if runtime_args is None:
            runtime_args = {}
        for arg in self.runtime_args:
            if not bool(runtime_args.get(arg)):
                raise Exception(f"{self.name} is missing runtime argument '{arg}': tool will not run")

        # Ok, run profiles
        for profile in self.profiles:
            profile.execute(runtime_args)

    def __str__(self):
        return f"{self.name}"
