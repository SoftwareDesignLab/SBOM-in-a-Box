#!/usr/bin/env bash

# File: validate.sh
# Validate OSI environment by checking for Languages, Package Managers, and Tools
#
# @author Derek Garcia


RED="\e[31m"
GREEN="\e[32m"
CLR="\e[0m"

function pass() {
  echo -e "$1 : ${GREEN}PASS${CLR}"
}

function fail() {
  echo -e "$1 : ${RED}FAIL${CLR}"
}

# Verify Languages are installed
function verify_lang(){
  result="LANGUAGES"

  result+="\n$(python3 --version &> /dev/null && pass "Python" || fail "Python")"
  result+="\n$(java --version &> /dev/null && pass "Java" || fail "Java")"
  result+="\n$(dotnet --info &> /dev/null && pass ".NET" || fail ".NET")"
  result+="\n$(go --version &> /dev/null && pass "Go" || fail "Go")"
  result+="\n$(node -v &> /dev/null && pass "Node.js" || fail "Node.js")"
  result+="\n$(rustc --version &> /dev/null && pass "Rust" || fail "Rust")"
  result+="\n$(php --version &> /dev/null && pass "PHP" || fail "PHP")"

  echo -e "$result" | column -t
}

main(){
  # Setup
  apt update
  apt install -y bsdmainutils

  echo "-= OSI VALIDATION =-"
  verify_lang
}

main