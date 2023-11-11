#!/usr/bin/env bash

# File: runner.sh
# Launch the Flask API server to host OSI
#
# @author Derek Garcia

main(){
  # Flask setup
  apt update
  apt install -y python3 python3-pip
  pip install -r /server/requirements.txt

  # Check before running
  validate.sh

  # Launch server
  echo "Launching Server"
  python3 /server/OSIAPIController.py
}

main
