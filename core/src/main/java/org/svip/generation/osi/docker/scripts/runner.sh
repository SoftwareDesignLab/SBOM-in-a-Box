#!/usr/bin/env bash

# File: runner.sh
# Launch the Flask API server to host OSI
#
# @author Derek Garcia

main(){

  # Activate env
  . "$HOME"/.cargo/env
  . "$NVM_DIR"/nvm.sh

  # Check before running
  validate.sh

  # Launch server
  echo "Launching Server"
  python3 /server/OSIAPIController.py
}

main
