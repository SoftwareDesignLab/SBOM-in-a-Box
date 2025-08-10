#!/bin/bash
# File: entrypoint.sh
# Launch the Flask API server to host OSI
#
# @author Derek Garcia

# Check before running
validate.sh

# Launch server
echo "Launching Server"
python3 osi server -H 0.0.0.0