#!/bin/bash

# File: setup.sh
# Setup OSI environment with required Languages, Package Managers, and Tools
#
# @author Derek Garcia
# @author Tyler Drake

# Distro and Constants
GO_DISTRO=go1.21.linux-amd64.tar.gz
BIN=/usr/local/bin
# Tools
SPDX_SBOM_GENERATOR=https://github.com/opensbom-generator/spdx-sbom-generator/releases/download/v0.0.15/spdx-sbom-generator-v0.0.15-linux-amd64.tar.gz
JBOM=https://github.com/eclipse/jbom/releases/download/v1.2.1/jbom-1.2.1.jar
CYCLONEDX_CLI=https://github.com/CycloneDX/cyclonedx-cli/releases/latest/download/cyclonedx-linux-x64

#
# LANGUAGES
#
function installGo() {
  rm -rf /usr/local/go
  curl -LO "https://go.dev/dl/$GO_DISTRO"
  tar -C /usr/local -xzf $GO_DISTRO
}

function installNode() {
  # Install Node Version Manager
  curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash && . $NVM_DIR/nvm.sh

  # Install LTS for Node
  nvm install --lts
}

# Will also install cargo
function installRust() {
  curl https://sh.rustup.rs -sSf | sh -s -- -y
  . "$HOME"/.cargo/env
  apt install -y libssl-dev pkg-config build-essential  # util libs for cargo
}

#
# PACKAGE MANAGERS
#
function installComposer(){
  apt install -y php-cli unzip php-xml php-curl
  curl -sS https://getcomposer.org/installer -o /tmp/composer-setup.php
  HASH=`curl -sS https://composer.github.io/installer.sig`
  php -r "if (hash_file('SHA384', '/tmp/composer-setup.php') === '$HASH') { echo 'Installer verified'; } else { echo 'Installer corrupt'; unlink('composer-setup.php'); } echo PHP_EOL;"
  php /tmp/composer-setup.php --install-dir=$BIN --filename=composer
}


#
# TOOLS : Installed with Package Manager
#
function installWithPIP(){

  # Install jake, cyclonedx-conan, cyclonedx-python, scanoss, sbom4python, sbom4files
  pip install jake cyclonedx-conan cyclonedx-bom scanoss sbom4python sbom4rust sbom4files

}

function installWithNPM() {
  # Install Retire.js, Bower, CycloneDX, Bower Bom, cdxgen
  npm install -g retire bower cdx-bower-bom @cyclonedx/cdxgen@8.6.0
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
  cargo install -- cargo-cyclonedx
}

function installWithComposer(){
  # cyclonedx-php-composer
  composer global config --no-plugins allow-plugins.cyclonedx/cyclonedx-php-composer true
  yes | composer global require cyclonedx/cyclonedx-php-composer
}

#
# TOOLS : Manual Installation
#
function installSPDXSBOMGenerator() {
  curl -L $SPDX_SBOM_GENERATOR -o /tmp/spdx-sbom-generator.tar.gz \
  && tar -C $BIN -xzf /tmp/spdx-sbom-generator.tar.gz
}

function installJBOM() {
  curl -L $JBOM -o $BIN/jbom.jar \
  && chmod +x $BIN/jbom.jar
}

function installCycloneDXCLI() {
  curl -L $CYCLONEDX_CLI -o $BIN/cyclonedx-cli \
  && chmod +x $BIN/cyclonedx-cli
  apt install -y libicu-dev
}

function installSyft() {
  curl -sSfL https://raw.githubusercontent.com/anchore/syft/main/install.sh | sh -s -- -b $BIN
}

function installSBOMTool() {
  curl -L https://github.com/microsoft/sbom-tool/releases/latest/download/sbom-tool-linux-x64 -o $BIN/sbom-tool
  chmod +x $BIN/sbom-tool
}

#
# Flask Server setup
#
function setupFlask() {
  pip install -r /server/requirements.txt
}

#
# Setup OSI environment and install all tools
#
main() {
  # Change into temp for any installations
  cd /tmp || exit 1

  # Setup the dev environment and utils
  apt clean
  apt update
  apt install -y curl

  # Install Languages
  apt install -y python3 openjdk-19-jdk dotnet6
  installGo
  installNode
  installRust
  echo "Languages installed"

  # Install Package Managers
  apt install -y maven gradle python3-pip nuget
  pip install conan
  installComposer
  echo "Package managers installed"

  # Install tools using package managers
  # jake, cyclonedx-conan, cyclonedx-python, scanoss
  installWithPIP
  # Install Retire.js cdxgen
  installWithNPM
  # CycloneDX-Go, GoBom
  installWithGo
  # Covenant
  installWithDotNet
  # CDX for Cargo
  installWithCargo
  # cyclonedx-php-composer
  installWithComposer
  echo "Package manager tools installed"

  # Install tools that need manual installation
  installSPDXSBOMGenerator
  installJBOM
  installCycloneDXCLI
  installSyft
  installSBOMTool

  echo "Manual tools installed"

  # Install Flask API requirements
  setupFlask

  apt clean
  rm -rf /tmp/*

  echo "Cleanup"
}

main
