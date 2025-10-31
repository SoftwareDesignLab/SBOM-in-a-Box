# File: Dockerfile
# The Dockerfile to build and run the SVIP API inside of a Docker container.
#
# Author: Ian Dunn
# Author: Derek Garcia

FROM gradle:8.14-jdk21-alpine AS build
WORKDIR /tmp/sbox
COPY --chown=gradle:gradle api api
COPY --chown=gradle:gradle core core
COPY --chown=gradle:gradle settings.gradle .
# Append "-x test" argument to skip tests; this is useful for development builds.
# Docker takes ~3-5m to build the initial image, it's faster than waiting
RUN gradle build --no-daemon -x test


FROM eclipse-temurin:21-jre-jammy AS runtime
# Install vulnerability scanners
USER root
RUN apt-get update && apt-get install -y curl wget ca-certificates
# Install Grype
ARG GRYPE_VERSION=0.84.0
RUN curl -sSfL https://raw.githubusercontent.com/anchore/grype/main/install.sh | sh -s -- -b /usr/local/bin v${GRYPE_VERSION}
# Install Trivy
ARG TRIVY_VERSION=0.58.1
RUN curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh | sh -s -- -b /usr/local/bin v${TRIVY_VERSION}
# Install OSV Scanner - using install script
RUN curl -L https://github.com/google/osv-scanner/releases/latest/download/osv-scanner_linux_amd64 -o /usr/local/bin/osv-scanner && chmod +x /usr/local/bin/osv-scanner || echo "OSV Scanner installation failed, continuing without it"
# Cleanup
RUN apt-get clean && rm -rf /var/lib/apt/lists/*

# create user (Debian/Ubuntu syntax)
RUN useradd -m -s /bin/bash sbox
USER sbox
# copy jar
WORKDIR /app
COPY --from=build /tmp/sbox/api/build/libs/api-1.0.0-alpha.jar /app/sbox.jar
# launch api server
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","sbox.jar"]
# ENTRYPOINT ["java", "-jar", "sbox.jar"]
