from enum import Enum


class Format(Enum):
    """
    Enum for current SBOM Formats
    """
    JSON = 1,
    SPDX = 2


def to_format(format_str: str):
    match format_str.lower():
        case "json":
            return Format.JSON
        case "spdx":
            return Format.SPDX
        case _:
            raise Exception(f"'{format_str}' is an unsupported format")
