# Use Ubuntu
FROM ubuntu:latest

# PHP Setup
ENV DEBIAN_FRONTEND noninteractive
ENV DEBCONF_NONINTERACTIVE_SEEN true
ENV COMPOSER_ALLOW_SUPERUSER 1

# Go Setup
ENV PATH $PATH:/usr/local/go/bin:/root/go/bin

# Dotnet tools
ENV PATH $PATH:/root/.dotnet/tools

# Node.js Setup
ENV NVM_DIR /root/.nvm

# Bound Directories
ENV CODE_IN /bound_dir/code
ENV SBOM_OUT /bound_dir/sboms

# Copy startup scripts
COPY scripts/ /usr/local/bin/
# Copy Flask webserver scripts
COPY server/ /server

# Run setup script
RUN /bin/bash -c setup.sh

# Cleanup
RUN rm /usr/local/bin/setup.sh

RUN ["chmod", "+x", "/usr/local/bin/runner.sh"]

ENTRYPOINT ["usr/local/bin/runner.sh"]