# Use Ubuntu
FROM ubuntu:latest

# PHP Setup
ENV DEBIAN_FRONTEND noninteractive
ENV DEBCONF_NONINTERACTIVE_SEEN true
ENV COMPOSER_ALLOW_SUPERUSER 1

# Copy startup script
COPY scripts/setup.sh /usr/local/bin/
# Copy Flask webserver scripts
COPY server/ /application/

# Run setup script
RUN /bin/bash -c /usr/local/bin/setup.sh

ENTRYPOINT ["python3", "/application/OSIAPIController.py"]