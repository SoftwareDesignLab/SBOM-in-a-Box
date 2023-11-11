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

# Copy startup script
COPY scripts/setup.sh /usr/local/bin/
# Copy Flask webserver scripts
COPY server/ /app/

# Run setup script
RUN /bin/bash -c /usr/local/bin/setup.sh

ENTRYPOINT ["python3", "/app/OSIAPIController.py"]