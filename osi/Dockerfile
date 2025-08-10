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
ENV JBOM=https://github.com/eclipse/jbom/releases/download/$JBOM_VERSION/jbom-$JBOM_VERSION.jar
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
WORKDIR /tmp
RUN apt update && apt install curl -y
RUN curl -sSfL https://raw.githubusercontent.com/anchore/syft/main/install.sh | sh -s -- -b /tmp "v$SYFT_VERSION"


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
ENV PATH=$PATH:/opt/gradle/gradle/bin:/osi/.cargo/bin

# Working directories
ENV CODE_IN=/tmp/code
ENV SBOM_OUT=/tmp/out
# create non-root user
RUN useradd -m osi

#
# Setup the OSI enviroment
#
WORKDIR /tmp/setup
# Add Microsoft Package Repository
RUN apt update && apt install -y curl \
    && curl -L https://packages.microsoft.com/config/debian/12/packages-microsoft-prod.deb -o packages-microsoft-prod.deb \
    && dpkg -i packages-microsoft-prod.deb
# Install languages and package managers
RUN apt update && apt install -y \
    # install languages \
    python3 openjdk-17-jre nodejs golang php dotnet-sdk-8.0 \
    # install package managers \
    python3-pip maven npm \
    # dotnet utils \
    apt-transport-https software-properties-common \
    # composer utils \
    php-cli php-mbstring git unzip
# gradle
COPY --from=bin_download /tmp/gradle.zip gradle.zip
RUN mkdir /opt/gradle && unzip -d /opt/gradle /tmp/setup/gradle.zip && mv /opt/gradle/gradle-* /opt/gradle/gradle
# composer
RUN curl -L https://getcomposer.org/installer -o composer-setup.php \
    && php composer-setup.php --install-dir=/usr/local/bin --filename=composer \
    && composer self-update
# Install tools using package managers
# pip
RUN pip install --break-system-packages --no-cache-dir \
    conan \
    # install tools \
    jake==3.0.14 cyclonedx-conan==0.4.1 scanoss==1.31.1 sbom4python==0.12.4 sbom4rust==0.6.0 sbom4files==0.4.5
# npm
RUN npm install -g bower@1.8.14 \
    # install tools \
    retire@5.2.8 cdx-bower-bom@0.3.0 @cyclonedx/cdxgen@11.5.0
# go
RUN go install github.com/ozonru/cyclonedx-go/cmd/cyclonedx-go@latest
RUN go install github.com/mattermost/gobom/cmd/gobom@latest
# dotnet
RUN dotnet tool install --global covenant --version 0.20.1-preview.2
RUN dotnet tool install --global Microsoft.Sbom.DotNetTool --version 4.1.0
# composer
RUN composer global config --no-plugins allow-plugins.cyclonedx/cyclonedx-php-composer true
RUN composer \
    --ignore-platform-req=ext-xmlwriter  \
    --ignore-platform-req=ext-dom  \
    global require cyclonedx/cyclonedx-php-composer:5.2.3
# Copy binaries
COPY --from=bin_download /tmp/spdx-sbom-generator /tmp/jbom.jar /tmp/cyclonedx-cli /usr/local/bin/
COPY --from=anchore_install /tmp/syft /usr/local/bin/

USER osi
# rust and cargo and
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
RUN . $HOME/.cargo/env && cargo install -- cargo-cyclonedx@0.5.7
# Cleanup
USER root
RUN apt remove -y curl git unzip
RUN rm -rf /var/lib/apt/lists/* /tmp/setup

USER osi
# Setup flask api server
COPY --chmod=755 scripts/ /usr/local/bin/
WORKDIR /app
COPY requirements.txt requirements.txt
RUN pip install --break-system-packages --no-cache-dir -r requirements.txt
COPY osi osi

RUN mkdir $CODE_IN && mkdir $SBOM_OUT
ENTRYPOINT ["runner.sh"]