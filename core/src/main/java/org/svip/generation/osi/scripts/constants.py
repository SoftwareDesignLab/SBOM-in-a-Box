from enum import Enum

VERSION = "v1.0.0"

"""
Constants that should not change, but if they do change them here
"""
# Path to the directory that will be mounted to the container
CONTAINER_BIND_DIR = "/bound_dir"
# Path to the directory where the host machine will put the code
CONTAINER_BIND_CODE = "{}/code".format(CONTAINER_BIND_DIR)
# Path to the directory where the container will save the SBOMs
CONTAINER_BIND_SBOM = "{}/sboms".format(CONTAINER_BIND_DIR)
# Name of sbom before it is renamed to a number
SBOM_TEMP_NAME = "tempbom"
# Preferred format extension of the SBOM
SBOM_FORMAT = "xml"


class BOMFormat(Enum):
    """
    Enum for current SBOM Formats
    """
    CYCLONE_DX = 1,
    SPDX = 2,
    OTHER = 3


class Language(Enum):
    """
    Enum to hold all supported languages. These will then link to a list of tools and their usage
    """
    JAVA = 1,
    PYTHON = 2,
    C_CPLUSPLUS = 3,
    CSHARP = 4,
    JAVASCRIPT = 5,
    RUBY = 6,
    GO = 7,
    RUST = 8,
    SWIFT = 9,
    KOTLIN = 10,
    SCALA = 11,
    PHP = 12,
    HASKELL = 13,
    PERL = 14,
    LUA = 15,
    DART = 16,
    ERLANG = 17,
    COBOL = 18,
    FORTRAN = 19,
    PASCAL = 20,
    ADA = 21,
    LISP = 22,
    PROLOG = 23,
    R = 24,
    MATLAB = 25,
    BASIC = 26,
    JAVA_JAR = 27


LANGUAGE_MAP = {
    "java": Language.JAVA, "py": Language.PYTHON, "pyi": Language.PYTHON, "cpp": Language.C_CPLUSPLUS,
    "cxx": Language.C_CPLUSPLUS, "cc": Language.C_CPLUSPLUS, "h": Language.C_CPLUSPLUS,
    "hpp": Language.C_CPLUSPLUS, "C": Language.C_CPLUSPLUS, "H": Language.C_CPLUSPLUS,
    "cs": Language.CSHARP, "js": Language.JAVASCRIPT, "rb": Language.RUBY, "go": Language.GO,
    "rs": Language.RUST, "swift": Language.SWIFT, "kt": Language.KOTLIN, "scala": Language.SCALA,
    "sc": Language.SCALA, "php": Language.PHP, "hs": Language.HASKELL, "lhs": Language.HASKELL,
    "pl": Language.PERL, "lua": Language.LUA, "dart": Language.DART, "erl": Language.ERLANG,
    "hrl": Language.ERLANG, "cbl": Language.COBOL, "cob": Language.COBOL, "f90": Language.FORTRAN,
    "for": Language.FORTRAN, "f": Language.FORTRAN, "pas": Language.PASCAL, "adb": Language.ADA,
    "ads": Language.ADA, "lsp": Language.LISP, "prolog": Language.PROLOG, "r": Language.R,
    "m": Language.MATLAB, "bas": Language.BASIC, "jar": Language.JAVA_JAR
}

MANIFEST_MAP = {
    "pom.xml": Language.JAVA, "build.gradle": Language.JAVA, "build.sbt": Language.JAVA,
    "requirements.txt": Language.PYTHON, "cargo.toml": Language.RUST, "cargo.lock": Language.RUST,
    "go.mod": Language.GO, "go.sum": Language.GO, "package.json": Language.JAVASCRIPT, "gemfile": Language.RUBY,
    "gemfile.lock": Language.RUBY, "pubspec.yaml": Language.DART, "pubspec.lock": Language.DART,
    "conanfile.txt": Language.C_CPLUSPLUS, "conanfile.py": Language.C_CPLUSPLUS, "composer.json": Language.PHP
}
