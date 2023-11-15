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

# Copy startup scripts
COPY scripts/ /usr/local/bin/
# Copy Flask webserver scripts
COPY server/ /server

# Run setup script
RUN /bin/bash -c setup.sh

# Cleanup
RUN rm /usr/local/bin/setup.sh


ENTRYPOINT ["runner.sh"]