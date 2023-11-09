#!/usr/bin/env bash

# File: setup.sh
# Setup OSI environment with required Languages, Package Managers, and Tools
#
# @author Derek Garcia
# @author Tyler Drake

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
}

function installNode() {
  # Install Node Version Manager
  curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash && source $NVM_DIR/nvm.sh

  # Install LTS for Node
  nvm install --lts
}

function installDotNet() {

  # Get package
  wget https://packages.microsoft.com/config/ubuntu/22.04/packages-microsoft-prod.deb
  dpkg -i packages-microsoft-prod.deb

  # Install .NET SDK
  apt install apt-transport-https
  apt install dotnet-sdk-6.0

  # Install .NET Core Runtime
  apt install apt-transport-https
  apt install dotnet-runtime-6.0

}

#
# PACKAGE MANAGERS
#
function installCargo() {
  curl https://sh.rustup.rs -sSf | sh
}


#
# TOOLS : Installed with Package Manager
#
function installWithPIP(){
  # Install flask for web server
  pip install flask

  # Install jake, cyclonedx-conan, cyclonedx-python, scanoss, sbom4python, sbom4files
  pip install jake cyclonedx-conan cyclonedx-bom scanoss sbom4python sbom4rust sbom4files

  # Bug fix for cyclonedx-conan
  pip install markupsafe==2.0.1
}

function installWithNPM() {
  # Install Retire.js
  npm install -g retire
  # Install Bower
  npm install -g bower
  # Install CycloneDX Bower Bom
  npm install -g cdx-bower-bom
  # Install cdxgen
  npm install -g @cyclonedx/cdxgen
}

function installWithGo() {
  # Install CycloneDX-Go
  go install github.com/ozonru/cyclonedx-go/cmd/cyclonedx-go@latest
  # Install GoBom
  go install github.com/mattermost/gobom/cmd/gobom@latest
}

function installWithDotNet(){
  # Covenant
  dotnet tool install --global covenant
}

function installWithCargo(){
  # CDX for Cargo
  cargo install cargo-cyclonedx
}

#
# TOOLS : Manual Installation
#
function installSPDXSBOMGenerator() {
  wget $SPDX_SBOM_GENERATOR
  tar xzf spdx-sbom-generator-v0.0.15-linux-amd64.tar.gz -C /usr/local/bin
}

function installJBOM() {
  wget $JBOM -O /usr/local/bin/jbom.jar
}

function installCycloneDXCLI() {
  wget $CYCLONEDX_CLI -O /usr/local/bin/cyclonedx-cli && chmod +x /usr/local/bin/cyclonedx-cli
}

function installSyft() {
  curl -sSfL https://raw.githubusercontent.com/anchore/syft/main/install.sh | sh -s -- -b /usr/local/bin
}

function installSBOMTool() {
  curl -Lo sbom-tool https://github.com/microsoft/sbom-tool/releases/latest/download/sbom-tool-linux-x64 && chmod -x /usr/local/bin/sbom-tool
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
  apt install -y python3-pip libicu-dev

  # Install Languages
  apt install -y python3 default-jre php
  installGo
  wait
  echo "Languages installed"

  # Install Package Managers
  apt install -y composer
  installCargo
  wait
  echo "Package managers installed"

  # Install Node.js
  installNode
  wait
  echo "Node.js installed"

  # Install dotnet
  installDotNet
  wait
  echo ".NET installed"

  # Install tools using package managers
  # jake, cyclonedx-conan, cyclonedx-python, scanoss
  installWithPIP &
  # Install Retire.js cdxgen
  installWithNPM &
  # CycloneDX-Go, GoBom
  installWithGo &
  # Covenant
  installWithDotNet &
  # CDX for Cargo
  installWithCargo &
  wait
  echo "Package manager tools installed"

  # Install tools that need manual installation
  installSPDXSBOMGenerator &
  installJBOM &
  installCycloneDXCLI &
  installSyft &
  installSBOMTool &
  wait

  echo "Manual tools installed"
}

main
