#!/usr/bin/env bash

# File: setup.sh
# Setup OSI environment with required Languages, Package Managers, and Tools
#
# @author Derek Garcia
# @author Tyler Drake

# Distro and Constants
GO_DISTRO=go1.20.linux-amd64.tar.gz
# Tools
SPDX_SBOM_GENERATOR=https://github.com/opensbom-generator/spdx-sbom-generator/releases/download/v0.0.15/spdx-sbom-generator-v0.0.15-linux-amd64.tar.gz
JBOM=https://github.com/eclipse/jbom/releases/download/v1.2.1/jbom-1.2.1.jar
CYCLONEDX_CLI=https://github.com/CycloneDX/cyclonedx-cli/releases/latest/download/cyclonedx-linux-x64

#
# LANGUAGES
#
function installGo() {
  wget "https://go.dev/dl/$GO_DISTRO"
  tar -C /usr/local -xzf $GO_DISTRO
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

# Will also install cargo
function installRust() {
  curl https://sh.rustup.rs -sSf | sh -s -- -y
  source "$HOME"/.cargo/env
  apt install -y libssl-dev pkg-config build-essential
}

#
# PACKAGE MANAGERS
#
function installComposer(){
    apt install -y curl php-cli unzip php-xml
    curl -sS https://getcomposer.org/installer -o /tmp/composer-setup.php
    HASH=`curl -sS https://composer.github.io/installer.sig`
    php -r "if (hash_file('SHA384', '/tmp/composer-setup.php') === '$HASH') { echo 'Installer verified'; } else { echo 'Installer corrupt'; unlink('composer-setup.php'); } echo PHP_EOL;"
    php /tmp/composer-setup.php --install-dir=/usr/local/bin --filename=composer
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
  npm install -g @cyclonedx/cdxgen@8.6.0
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

function installWithComposer(){
  # cyclonedx-php-composer
  composer global config --no-plugins allow-plugins.cyclonedx/cyclonedx-php-composer true
  yes | composer global require cyclonedx/cyclonedx-php-composer
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
  curl -L https://github.com/microsoft/sbom-tool/releases/latest/download/sbom-tool-linux-x64 -o /usr/local/bin/sbom-tool
  chmod +x /usr/local/bin/sbom-tool
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
  apt install -y libicu-dev

  # Install Languages
  apt install -y python3 openjdk-19-jdk
  installGo &
  installNode &
  wait
  echo "Languages installed"

  # Install Package Managers
  apt install -y maven gradle python3-pip
  installCargo &
  installComposer &
  wait
  echo "Package managers installed"

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
  # cyclonedx-php-composer
  installComposer &
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
