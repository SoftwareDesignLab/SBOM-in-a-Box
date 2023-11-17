#!/bin/bash

# File: runner.sh
# Launch the Flask API server to host OSI
#
# @author Derek Garcia

main(){

  # Activate env
  . "$HOME"/.cargo/env
  . "$NVM_DIR"/nvm.sh

  # Check before running
  . validate.sh

  # Launch server
  echo "Launching Server"
  cd /server || exit 1
  python3 OSIServer.py
}

main
