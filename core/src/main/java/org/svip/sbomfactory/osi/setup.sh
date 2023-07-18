#!/bin/bash

GO_VERSION_=""

function installComposer(){
  # Source: https://getcomposer.org/doc/faqs/how-to-install-composer-programmatically.md
  EXPECTED_CHECKSUM="$(php -r 'copy("https://composer.github.io/installer.sig", "php://stdout");')"
  php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');"
  ACTUAL_CHECKSUM="$(php -r "echo hash_file('sha384', 'composer-setup.php');")"

  if [ "$EXPECTED_CHECKSUM" != "$ACTUAL_CHECKSUM" ]
  then
      >&2 echo 'ERROR: Invalid installer checksum'
      rm composer-setup.php
      exit 1
  fi

  php composer-setup.php --quiet
  RESULT=$?
  rm composer-setup.php
  composer -V
  exit $RESULT
}

#function installGo() {
#    cd /tmp && wget https://go.dev/dl/go1.20.linux-amd64.tar.gz
#    RUN cd /tmp && rm -rf /usr/local/go && tar -C /usr/local -xzf go1.20.linux-amd64.tar.gz
#    RUN echo "export PATH=$PATH:/usr/local/go/bin" >> /root/.profile
#    RUN cp /usr/local/go/bin/go /usr/local/bin/go
#}


funtion main(){
  # Setup the dev environment
  apt clean && apt update

  # Install Languages
  apt install -y python3 nodejs default-jre php

  # Install Package Managers
  installComposer

  # Install Utils
  apt install -y python3-pip wget curl libicu-dev npm
}

main
