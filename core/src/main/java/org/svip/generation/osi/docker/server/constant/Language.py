from enum import Enum


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


def to_language(lang_str: str):
    match lang_str.upper():
        case "JAVA":
            return Language.JAVA
        case "PYTHON":
            return Language.PYTHON
        case "C":
            return Language.C_CPLUSPLUS
        case "C++":
            return Language.C_CPLUSPLUS
        case "C#":
            return Language.CSHARP
        case "JAVASCRIPT":
            return Language.JAVASCRIPT
        case "RUBY":
            return Language.RUBY
        case "GO":
            return Language.GO
        case "RUST":
            return Language.RUST
        case "SWIFT":
            return Language.SWIFT
        case "KOTLIN":
            return Language.KOTLIN
        case "SCALA":
            return Language.SCALA
        case "PHP":
            return Language.PHP
        case "HASKELL":
            return Language.HASKELL
        case "PERL":
            return Language.PERL
        case "LUA":
            return Language.LUA
        case "DART":
            return Language.DART
        case "ERLANG":
            return Language.ERLANG
        case "COBOL":
            return Language.COBOL
        case "FORTRAN":
            return Language.FORTRAN
        case "PASCAL":
            return Language.PASCAL
        case "ADA":
            return Language.ADA
        case "LISP":
            return Language.LISP
        case "PROLOG":
            return Language.PROLOG
        case "R":
            return Language.R
        case "MATLAB":
            return Language.MATLAB
        case "BASIC":
            return Language.BASIC
        case "JAVA_JAR":
            return Language.JAVA_JAR
        case _:
            raise Exception(f"'{lang_str}' is an unsupported format")


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
