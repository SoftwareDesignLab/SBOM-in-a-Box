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
  result=""
  result+="\n$(python3 --version &> /dev/null && pass "Python" || fail "Python")"
  result+="\n$(java --version &> /dev/null && pass "Java" || fail "Java")"
  result+="\n$(dotnet --info &> /dev/null && pass ".NET" || fail ".NET")"
  result+="\n$(go --version &> /dev/null && pass "Go" || fail "Go")"
  result+="\n$(node -v &> /dev/null && pass "Node.js" || fail "Node.js")"
  result+="\n$(rustc --version &> /dev/null && pass "Rust" || fail "Rust")"
  result+="\n$(php --version &> /dev/null && pass "PHP" || fail "PHP")"

  echo "LANGUAGES"
  echo -e "$result" | column -t
}

function verify_pm(){
  result=""
  result+="\n$(mvn --version &> /dev/null && pass "Maven" || fail "Maven")"
  result+="\n$(gradle --version &> /dev/null && pass "Gradle" || fail "Gradle")"
  result+="\n$(pip --version &> /dev/null && pass "Pip" || fail "Pip")"
  result+="\n$(dotnet nuget --version &> /dev/null && pass "NuGet" || fail "NuGet")"
  result+="\n$(conan --version &> /dev/null && pass "Conan" || fail "Conan")"
  result+="\n$(composer --version &> /dev/null && pass "Composer" || fail "Composer")"
  result+="\n$(cargo --version &> /dev/null && pass "Cargo" || fail "Cargo")"
  result+="\n$(npm --version &> /dev/null && pass "NPM" || fail "NPM")"

  echo "PACKAGE MANAGERS"
  echo -e "$result" | column -t
}

main(){
  # Setup
  apt update
  apt install -y bsdmainutils

  echo "-= OSI VALIDATION =-"
  verify_lang
  echo ""
  verify_pm
}

main