################# OSI Container v1.0.0
# Use Ubuntu
FROM ubuntu:latest

# PHP install likes to ask questions, these variables say "No PHP, no questions for you"
ENV DEBIAN_FRONTEND noninteractive
ENV DEBCONF_NONINTERACTIVE_SEEN true
ENV COMPOSER_ALLOW_SUPERUSER 1

# Install all necessary components
RUN apt clean && apt update && apt install -y python3 python3-pip wget curl libicu-dev nodejs npm default-jre php

# Create the bound_dir directory since we need to mount binds to it
RUN mkdir /bound_dir

# Install composer
RUN cd /tmp && \
    php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');" && \
    php -r "if (hash_file('sha384', 'composer-setup.php') === '55ce33d7678c5a611085589f1f3ddf8b3c52d662cd01d4ba75c0ee0459970c2200a51f492d557530c71c15d8dba01eae') { echo 'Installer verified'; } else { echo 'Installer corrupt'; unlink('composer-setup.php'); } echo PHP_EOL;" && \
    php composer-setup.php && \
    php -r "unlink('composer-setup.php');" && \
    mv composer.phar /usr/local/bin/composer
RUN composer global config --no-plugins allow-plugins.cyclonedx/cyclonedx-php-composer true
RUN composer global require cyclonedx/cyclonedx-php-composer --ignore-platform-req=ext-xmlwriter --ignore-platform-req=ext-dom -n

# Install go (STATIC)
RUN cd /tmp && wget https://go.dev/dl/go1.20.linux-amd64.tar.gz
RUN cd /tmp && rm -rf /usr/local/go && tar -C /usr/local -xzf go1.20.linux-amd64.tar.gz
RUN echo "export PATH=$PATH:/usr/local/go/bin" >> /root/.profile
RUN cp /usr/local/go/bin/go /usr/local/bin/go

# Install jake, cyclonedx-conan, cyclonedx-python, ochrona, scanoss
RUN pip install jake cyclonedx-conan cyclonedx-bom ochrona scanoss

# Install Retire.js
RUN npm install -g retire

# Install cdxgen
RUN npm install -g @appthreat/cdxgen

# Install cargo
RUN curl https://sh.rustup.rs -sSf | sh -s -- -y

# Install SPDX SBOM Generator
RUN wget https://github.com/opensbom-generator/spdx-sbom-generator/releases/download/v0.0.15/spdx-sbom-generator-v0.0.15-linux-amd64.tar.gz -O /tmp/spdx-gen.tar.gz && \
    cd /tmp && tar xvf spdx-gen.tar.gz && \
    mv spdx-sbom-generator /usr/local/bin/spdx-sbom-generator

# Install JBOM (incomplete)
RUN wget https://github.com/eclipse/jbom/releases/download/v1.2.1/jbom-1.2.1.jar -O /usr/local/bin/jbom.jar

# Install CycloneDX CLI
RUN wget https://github.com/CycloneDX/cyclonedx-cli/releases/latest/download/cyclonedx-linux-x64 -O /usr/local/bin/cyclonedx-cli && chmod +x /usr/local/bin/cyclonedx-cli

# Setup Syft
RUN curl -sSfL https://raw.githubusercontent.com/anchore/syft/main/install.sh | sh -s -- -b /usr/local/bin

# Bug fix for cyclonedx-conan
RUN pip install markupsafe==2.0.1

COPY ContainerController.py /usr/local/bin/ContainerController.py

# Launch the python program
CMD ["python3", "/usr/local/bin/ContainerController.py"]
#CMD ["/bin/bash"]