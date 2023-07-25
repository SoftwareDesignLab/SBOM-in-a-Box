# Use Ubunutu
FROM ubuntu:latest

# PHP Setup
ENV DEBIAN_FRONTEND noninteractive
ENV DEBCONF_NONINTERACTIVE_SEEN true
ENV COMPOSER_ALLOW_SUPERUSER 1

# Copy Scripts
COPY setup.sh ContainerController.py /usr/local/bin/

# Run setup script
RUN /bin/bash -c /usr/local/bin/setup.sh

ENTRYPOINT ["python3", "/usr/local/bin/ContainerController.py"]