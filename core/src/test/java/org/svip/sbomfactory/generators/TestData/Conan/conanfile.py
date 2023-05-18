from conan import ConanFile


class Engine(ConanFile):
    name = "engine"
    settings = "arch"

    def requirements(self):
        self.requires("matrix/[>=1.0 <2.0]")
        if self.settings.arch == "x86":
            self.requires("sound32/[>=1.0 <2.0]")
            self.requires('zlib/1.7.0')
            self.requires('imgui/1.7.0')
            self.requires("mysql-connector-c/6.1.11")
            self.requires("libmysqlclient/8.0.31")
            self.requires("libmodbus/3.1.8")
            self.requires("cmake/3.26.3")
"""
from conan import ConanFile
from conan.tools.cmake import CMakeToolchain, CMake, cmake_layout
from conan.tools.files import get


class helloRecipe(ConanFile):
    name = "hello"
    version = "1.0"

    # Binary configuration
    settings = "os", "compiler", "build_type", "arch"

    options = {"shared": [True, False],
               "fPIC": [True, False]}

    default_options = {"shared": False,
                       "fPIC": True}

    generators = "CMakeDeps"
"""

    def config_options(self):
        if self.settings.os == "Windows":
            del self.options.fPIC

    def configure(self):
        if self.options.shared:
            self.options.rm_safe("fPIC")

    def source(self):
        # Please, be aware that using the head of the branch instead of an immutable tag
        # or commit is not a good practice in general
        get(self, "https://github.com/conan-io/libhello/archive/refs/heads/main.zip",
            strip_root=True)

    def layout(self):
        cmake_layout(self, src_folder="src")

    def generate(self):
        tc = CMakeToolchain(self)
        tc.generate()

    def build(self):
        cmake = CMake(self)
        cmake.configure()
        cmake.build()

    def package(self):
        cmake = CMake(self)
        cmake.install()

    def package_info(self):
        self.cpp_info.libs = ["hello"]
        # Hello comment from a blank like
        self.c_pingliu = ["empty string"]#not_a_comment     #but this is a comment
