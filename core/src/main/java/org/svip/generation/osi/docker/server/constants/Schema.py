from enum import Enum


class Schema(Enum):
    """
    Enum for current SBOM Formats
    """
    CYCLONE_DX = 1,
    SPDX = 2,
    OTHER = 3


def to_schema(schema_str: str):
    match schema_str.lower():
        case "cyclonedx":
            return Schema.CYCLONE_DX
        case "spdx":
            return Schema.SPDX
        case _:
            raise Exception(f"'{schema_str}' is an unsupported schema")
