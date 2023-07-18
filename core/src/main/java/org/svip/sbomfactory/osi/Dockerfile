################# OSI Container v1.0.0
# Use Ubuntu
FROM ubuntu:latest

# PHP install likes to ask questions, these variables say "No PHP, no questions for you"
ENV DEBIAN_FRONTEND noninteractive
ENV DEBCONF_NONINTERACTIVE_SEEN true
ENV COMPOSER_ALLOW_SUPERUSER 1

ADD setup.sh setup.sh
RUN ./setup.sh

COPY ContainerController.py /usr/local/bin/ContainerController.py

# Launch the python program
CMD ["python3", "/usr/local/bin/ContainerController.py"]