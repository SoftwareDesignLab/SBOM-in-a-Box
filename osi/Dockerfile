# OSI Dockerfile
# Setup OSI environment and setup API service
#
# @author Derek Garcia

# Install tooling
FROM alpine/curl:8.14.1 AS spdx_sbom_generator
ENV SPDX_SBOM_GENERATOR=https://github.com/opensbom-generator/spdx-sbom-generator/releases/download/v0.0.15/spdx-sbom-generator-v0.0.15-linux-amd64.tar.gz
RUN curl -L $SPDX_SBOM_GENERATOR -o /tmp/spdx-sbom-generator.tar.gz && tar -C /tmp -xzf /tmp/spdx-sbom-generator.tar.gz

FROM alpine/curl:8.14.1 AS jbom
ENV JBOM=https://github.com/eclipse/jbom/releases/download/v1.2.1/jbom-1.2.1.jar
RUN curl -L $JBOM -o tmp/jbom.jar

FROM alpine/curl:8.14.1 AS cyclone_dx_cli
ENV CYCLONEDX_CLI=https://github.com/CycloneDX/cyclonedx-cli/releases/latest/download/cyclonedx-linux-x64
RUN curl -L $CYCLONEDX_CLI -o tmp/cyclonedx-cli
#  apt install -y libicu-dev

FROM alpine/curl:8.14.1 AS syft
RUN curl -sSfL https://raw.githubusercontent.com/anchore/syft/main/install.sh | sh -s -- -b /tmp

FROM alpine:3.22 AS osi

# PHP Setup
ENV DEBIAN_FRONTEND=noninteractive
ENV DEBCONF_NONINTERACTIVE_SEEN=true
ENV COMPOSER_ALLOW_SUPERUSER=1

# Go Setup
ENV PATH=$PATH:/usr/local/go/bin

# Rust setup
ENV PATH=$PATH:/root/.cargo/bin

# Dotnet tools
ENV PATH=$PATH:/root/.dotnet/tools

# Working directories
ENV CODE_IN=/tmp/code
ENV SBOM_OUT=/tmp/out
# create non-root user
RUN adduser -D osi

#
# Setup the OSI enviroment
#

# Install languages
RUN apk update && apk add \
    python3 \
    openjdk21-jdk \
    dotnet8-sdk \
    nodejs \
    curl
# go
COPY --from=golang:alpine /usr/local/go/ /usr/local/go/
# rust
RUN apk add \
    build-base \
    libffi-dev \
    openssl-dev
USER osi
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
RUN . "$HOME/.cargo/env" && cargo install -- cargo-cyclonedx

USER root
# Install Package managers
RUN apk add \
    py3-pip \
    maven \
    gradle \
    composer \
    npm
# conan
RUN pip install --break-system-packages conan

# Install tools using package managers
RUN pip install --break-system-packages flask jake cyclonedx-conan cyclonedx-bom scanoss sbom4python sbom4rust sbom4files
RUN npm install -g retire bower cdx-bower-bom @cyclonedx/cdxgen@8.6.0
RUN go install github.com/ozonru/cyclonedx-go/cmd/cyclonedx-go@latest
RUN go install github.com/mattermost/gobom/cmd/gobom@latest
RUN dotnet tool install --global covenant
RUN dotnet tool install --global Microsoft.Sbom.DotNetTool
RUN composer global config --no-plugins allow-plugins.cyclonedx/cyclonedx-php-composer true
RUN composer \
    --ignore-platform-req=ext-xmlwriter  \
    --ignore-platform-req=ext-dom  \
    global require cyclonedx/cyclonedx-php-composer

# Copy binaries
# Unavailable in alpine
#COPY --from=spdx_sbom_generator --chmod=755 /tmp/spdx-sbom-generator /usr/local/bin/
COPY --from=jbom tmp/jbom.jar /usr/local/bin/
COPY --from=cyclone_dx_cli tmp/cyclonedx-cli /usr/local/bin/
COPY --from=syft tmp/syft /usr/local/bin/

# Setup flask api server
COPY --chmod=755 scripts/ /usr/local/bin/
WORKDIR /app
COPY requirements.txt requirements.txt
RUN pip install --break-system-packages --no-cache-dir -r requirements.txt
COPY osi osi

# Cleanup
RUN rm -rf /var/cache/apk/* /tmp/*

USER osi
RUN mkdir $CODE_IN && mkdir $SBOM_OUT
ENTRYPOINT ["runner.sh"]