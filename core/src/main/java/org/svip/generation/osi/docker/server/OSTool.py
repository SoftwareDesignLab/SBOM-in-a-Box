from constants import BOMFormat, Language


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

    def __repr__(self):
        """
        Convert an OSTool representation to its string value
        """
        return self.__str__()
