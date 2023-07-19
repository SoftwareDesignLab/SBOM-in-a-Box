#
# File: Dockerfile
# The Dockerfile to build and run the OSI Container v1.0.0.
#
# Author: Matt London
#

ADD setup.sh setup.sh
RUN ./setup.sh

COPY ContainerController.py /usr/local/bin/ContainerController.py

# Launch the python program
CMD ["python3", "/usr/local/bin/ContainerController.py"]