#
# File: Dockerfile
# The Dockerfile to build and run the OSI Container v1.0.0.
#
# Author: Matt London
#

FROM ubuntu:latest

ENV DEBIAN_FRONTEND noninteractive
ENV DEBCONF_NONINTERACTIVE_SEEN true
ENV COMPOSER_ALLOW_SUPERUSER 1

COPY setup.sh ContainerController.py /usr/local/bin/
RUN /bin/bash -c /usr/local/bin/setup.sh

# Launch the python program
CMD ["python3", "/usr/local/bin/ContainerController.py"]