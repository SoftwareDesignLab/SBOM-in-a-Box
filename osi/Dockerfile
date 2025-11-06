# OSI Dockerfile
# Setup OSI environment and setup API service
#
# @author Derek Garcia

# Download tooling and package managers
FROM alpine/curl:8.14.1 AS bin_download
# spdx sbom generator
ARG SPDX_SBOM_GENERATOR_VERSION=0.0.15
ENV SPDX_SBOM_GENERATOR="https://github.com/opensbom-generator/spdx-sbom-generator/releases/download/v$SPDX_SBOM_GENERATOR_VERSION/spdx-sbom-generator-v$SPDX_SBOM_GENERATOR_VERSION-linux-amd64.tar.gz"
# JBOM
ARG JBOM_VERSION=1.2.1
ENV JBOM=https://github.com/eclipse-jbom/jbom/releases/download/v$JBOM_VERSION/jbom-$JBOM_VERSION.jar
# cyclonedx-cli
ARG CYCLONEDX_CLI_VERSION=0.29.0
ENV CYCLONEDX_CLI=https://github.com/CycloneDX/cyclonedx-cli/releases/v$CYCLONEDX_CLI_VERSION/download/cyclonedx-linux-x64
# gradle
ARG GRADLE_VERSION=9.0.0
ENV GRADLE="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
# download tools
WORKDIR /tmp
RUN curl -L $SPDX_SBOM_GENERATOR -o spdx-sbom-generator.tar.gz && tar xf /tmp/spdx-sbom-generator.tar.gz
RUN curl -L $JBOM -o jbom.jar
RUN curl -L $CYCLONEDX_CLI -o cyclonedx-cli
# download gradle
RUN curl -L $GRADLE -o gradle.zip


# Install anchore tooling on debian container
FROM debian:bookworm-slim AS anchore_install
ARG SYFT_VERSION=1.30.0
ARG GRYPE_VERSION=0.84.0
WORKDIR /tmp
RUN apt update && apt install curl -y
RUN curl -sSfL https://raw.githubusercontent.com/anchore/syft/main/install.sh | sh -s -- -b /tmp "v$SYFT_VERSION"
RUN curl -sSfL https://raw.githubusercontent.com/anchore/grype/main/install.sh | sh -s -- -b /tmp "v$GRYPE_VERSION"


# Install vulnerability scanners
FROM debian:bookworm-slim AS vuln_scanner_install
ARG TRIVY_VERSION=0.58.1
ARG OSV_SCANNER_VERSION=1.9.2
ARG DEPENDENCY_CHECK_VERSION=11.1.1
WORKDIR /tmp
RUN apt update && apt install curl unzip wget -y
# Trivy
RUN curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh | sh -s -- -b /tmp "v$TRIVY_VERSION"
# OSV Scanner
RUN curl -L "https://github.com/google/osv-scanner/releases/download/v$OSV_SCANNER_VERSION/osv-scanner_$OSV_SCANNER_VERSION\_linux_amd64" -o /tmp/osv-scanner && chmod +x /tmp/osv-scanner
# OWASP Dependency-Check
RUN wget "https://github.com/jeremylong/DependencyCheck/releases/download/v$DEPENDENCY_CHECK_VERSION/dependency-check-$DEPENDENCY_CHECK_VERSION-release.zip" -O dependency-check.zip \
    && unzip dependency-check.zip \
    && chmod +x dependency-check/bin/dependency-check.sh


# Main container
FROM debian:bookworm-slim AS osi_runtime
LABEL maintainer="Derek Garcia <dgarcia2@hawaii.edu>"
LABEL version=4.1.0
LABEL name="osi"
# PHP Setup
ENV DEBIAN_FRONTEND=noninteractive
ENV DEBCONF_NONINTERACTIVE_SEEN=true
ENV COMPOSER_ALLOW_SUPERUSER=1
# set paths
ENV PATH=$PATH:/opt/gradle/gradle/bin:/opt/dependency-check/bin

# Working directories
ENV CODE_IN=/tmp/code
ENV SBOM_OUT=/tmp/out
# create non-root user
RUN useradd -m osi

#
# Setup the OSI enviroment
#
WORKDIR /tmp/setup
# update registries
RUN apt update && apt install -y curl gnupg ca-certificates apt-transport-https software-properties-common \
    # microsoft for dotnet8 \
    && curl -L https://packages.microsoft.com/config/debian/12/packages-microsoft-prod.deb -o packages-microsoft-prod.deb \
    && dpkg -i packages-microsoft-prod.deb \
    # node 20 \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
# Install languages and package managers
RUN apt update && apt install -y \
    # install languages \
    python3 openjdk-17-jre nodejs golang php dotnet-sdk-8.0 \
    # install package managers \
    python3-pip maven \
    # composer utils \
    php-cli php-mbstring git unzip \
    && apt autoremove -y
# gradle
COPY --from=bin_download /tmp/gradle.zip gradle.zip
RUN mkdir /opt/gradle && unzip -d /opt/gradle /tmp/setup/gradle.zip && mv /opt/gradle/gradle-* /opt/gradle/gradle
# composer
RUN curl -L https://getcomposer.org/installer -o composer-setup.php \
    && php composer-setup.php --install-dir=/usr/local/bin --filename=composer \
    && composer self-update
# Install tools using package managers
# npm
RUN npm install -g bower@1.8.14 \
    # install tools \
    retire@5.2.8 cdx-bower-bom@0.3.0 @cyclonedx/cdxgen@11.5.0
# pip
RUN pip install --break-system-packages --no-cache-dir \
    conan \
    # install tools \
    jake==3.0.14 cyclonedx-conan==0.4.1 scanoss==1.31.1 sbom4python==0.12.4 sbom4rust==0.6.0 sbom4files==0.4.5
USER osi
ENV HOME=/home/osi
ENV PATH=$PATH:$HOME/.cargo/bin:$HOME/go/bin:$HOME/.dotnet/tools

# dotnet
RUN dotnet tool install --global covenant --version 0.20.1-preview.2
RUN dotnet tool install --global Microsoft.Sbom.DotNetTool --version 4.1.0
# rust and cargo and cargo cdx tool
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
RUN . $HOME/.cargo/env && cargo install -- cargo-cyclonedx@0.5.7
# go
RUN go install github.com/ozonru/cyclonedx-go/cmd/cyclonedx-go@latest
RUN go install github.com/mattermost/gobom/cmd/gobom@latest
# composer
RUN composer global config --no-plugins allow-plugins.cyclonedx/cyclonedx-php-composer true
RUN composer \
    --ignore-platform-req=ext-xmlwriter  \
    --ignore-platform-req=ext-dom  \
    global require cyclonedx/cyclonedx-php-composer:5.2.3
# Copy binaries
COPY --from=bin_download /tmp/spdx-sbom-generator /tmp/jbom.jar /tmp/cyclonedx-cli /usr/local/bin/
COPY --from=anchore_install /tmp/syft /tmp/grype /usr/local/bin/
# Copy vulnerability scanners
COPY --from=vuln_scanner_install /tmp/trivy /tmp/osv-scanner /usr/local/bin/
COPY --from=vuln_scanner_install /tmp/dependency-check /opt/dependency-check
# Cleanup
USER root
RUN apt remove -y git unzip gnupg apt-transport-https software-properties-common
RUN rm -rf /var/lib/apt/lists/* /tmp/setup

USER osi
# Setup flask api server
COPY --chmod=755 scripts/ /usr/local/bin/
WORKDIR /app
COPY requirements.txt requirements.txt
RUN pip install --break-system-packages --no-cache-dir -r requirements.txt
COPY osi osi

RUN mkdir $CODE_IN && mkdir $SBOM_OUT
ENTRYPOINT ["entrypoint.sh"]