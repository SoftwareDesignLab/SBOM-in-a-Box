"""
file:

Collection of utilities to validate, run, and organize the output of Open Source SBOM tools.

@author Derek Garcia
"""
import os
import Format
import Schema

TOOL_CONFIGS = "../tool_configs"


class Profile(object):

    def __init__(self, profile_name: str, schema: Schema, spec_version: str, sbom_format: Format, args: dict,
                 commands: list[str]):
        self.profile_name = profile_name
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

    def __str__(self):
        return f"{self.profile_name} | {self.schema} {self.spec_version} | {self.format}"
