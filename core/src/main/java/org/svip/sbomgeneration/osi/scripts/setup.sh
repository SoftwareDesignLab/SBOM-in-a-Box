#!/usr/bin/env bash

# File: setup.sh
# Setup OSI environment with required Languages, Package Managers, and Tools
#
# @author Derek Garcia

# Distro and Constants
GO_DISTRO=https://go.dev/dl/go1.20.linux-amd64.tar.gz
# Tools
SPDX_SBOM_GENERATOR=https://github.com/opensbom-generator/spdx-sbom-generator/releases/download/v0.0.15/spdx-sbom-generator-v0.0.15-linux-amd64.tar.gz
JBOM=https://github.com/eclipse/jbom/releases/download/v1.2.1/jbom-1.2.1.jar
CYCLONEDX_CLI=https://github.com/CycloneDX/cyclonedx-cli/releases/latest/download/cyclonedx-linux-x64

#
# LANGUAGES
#
function installGo() {
    wget $GO_DISTRO
    rm -rf /usr/local/go && tar -C /usr/local -xzf go1.20.linux-amd64.tar.gz
    export PATH=$PATH:/usr/local/go/bin
    go version  # confirm success
}

#
# PACKAGE MANAGERS
#
function installCargo() {
    curl https://sh.rustup.rs -sSf | sh -s -- -y
}


#
# TOOLS : Installed with Package Manager
#
function installWithPIP(){
  # Install jake, cyclonedx-conan, cyclonedx-python, ochrona, scanoss
  pip install jake cyclonedx-conan cyclonedx-bom ochrona scanoss

  # Bug fix for cyclonedx-conan
  pip install markupsafe==2.0.1
}

function installWithNPM() {
    # Install Retire.js
    npm install -g retire
    # Install cdxgen
    npm install -g @cyclonedx/cdxgen
}


#
# TOOLS : Manual Installation
#
function installSPDXSBOMGenerator(){
  wget $SPDX_SBOM_GENERATOR /tmp/spdx-gen.tar.gz
  # unzip and move to bin
  tar xvf spdx-gen.tar.gz mv spdx-sbom-generator /usr/local/bin/spdx-sbom-generator
}

function installJBOM(){
  wget $JBOM -O /usr/local/bin/jbom.jar
}

function installCycloneDXCLI() {
    wget $CYCLONEDX_CLI -O /usr/local/bin/cyclonedx-cli && chmod +x /usr/local/bin/cyclonedx-cli
}

function installSyft() {
    curl -sSfL https://raw.githubusercontent.com/anchore/syft/main/install.sh | sh -s -- -b /usr/local/bin
}

#
# Setup OSI environment and install all tools
#
main() {
  # Change into temp for any installations
  cd /tmp || exit 1

  # Setup the dev environment
  apt clean && apt update

  # Install Utils
  apt install -y python3-pip wget curl libicu-dev npm
  apt install -y wget curl

  # Install Languages
  apt install -y python3 nodejs default-jre php
  installGo
  echo "Languages installed"

  # Install Package Managers
  apt install -y composer
  installCargo
  echo "Package managers installed"

  # Install tools using package managers
  # jake, cyclonedx-conan, cyclonedx-python, ochrona, scanoss
  installWithPIP &
#  # Install Retire.js cdxgen
  installWithNPM &
  wait
  echo "Package manager tools installed"

  # Install tools that need manual installation
  installSPDXSBOMGenerator &
  installJBOM &
  installCycloneDXCLI &
  installSyft &
  wait

  echo "Manual tools installed"
}

main
