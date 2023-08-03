from typing import Optional

from constants import *
from toolmap import TOOL_MAP

"""
file: ToolMapper.py

A map of all supported Open Source SBOM Tools and their corresponding languages, commands, and other attributes.

@author Matt London
@author Ian Dunn
"""


class OSTool(object):
    """
    Tool object for each supported OSI tool. This will hold name, languages, and usage
    """

    def __init__(self, name: str, generation_type: BOMFormat, languages: list[Language], usage: list[str],
                 manifest_required: bool = False):
        """
        Constructor for an OSTool

        :param name: Name of the tool
        :param generation_type: Type of BOM the tool generates
        :param languages: List of languages it supports
        :param usage: List of commands needed to execute the tool on a project ({code} for code path,
            {output} for output path, {manifest} for potential manifest files)
        :param manifest_required: If the tool requires a passed in manifest file to function
        """
        self.name = name
        self.generationType = generation_type
        self.languages = languages
        self.usage = usage
        self.manifest_required = manifest_required

    def __str__(self):
        """
        Convert an OSTool to its string representation (currently name)
        """
        return self.name


class ToolMapper(object):
    """
    Class to hold all tools and map languages to a list of tools
    """
    tool_map = TOOL_MAP

    # Add tools into a list dictionary keyed by language
    mapping = {}

    # Map in all tools to their respective languages
    for language in Language:
        langList = []

        # No this is not a for loop in a for loop...
        for tool in tool_map:
            if language in tool.languages:
                langList.append(tool)

        mapping[language] = langList

    def get_tool_names(self) -> list[str]:
        return list(map(str, self.tool_map))

    def get_tools(self, language: Language) -> list[OSTool]:
        """
        Gets a list of tools for a given language

        :param language: Language to get tools for
        :return: List of tools
        """
        return self.mapping[language]

    def get_tool(self, name: str) -> Optional[OSTool]:
        """
        Gets an OSTool instance given a tool name. If no tool could be found, returns None.
        """
        for tool in self.tool_map:
            if tool.name == name:
                return tool
        return
