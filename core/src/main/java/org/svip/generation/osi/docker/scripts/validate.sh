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

  echo -e "\nLANGUAGES"
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

  echo -e "\nPACKAGE MANAGERS"
  echo -e "$result" | column -t
}

# Verify Tools are installed
function verify_tools(){

  result=""
  result+="\n$(cdxgen -h &> /dev/null && pass "cdxgen" || fail "cdxgen")"
  result+="\n$(dotnet covenant -h &> /dev/null && pass "covenant" || fail "covenant")"
  result+="\n$(cdx-bower-bom -h &> /dev/null && pass "cdx-bower-bom" || fail "cdx-bower-bom")"
  result+="\n$(cyclonedx-conan --help &> /dev/null && pass "cyclonedx-conan" || fail "cyclonedx-conan")"
  result+="\n$(cyclonedx-go -h &> /dev/null && pass "cyclonedx-go" || fail "cyclonedx-go")"
  result+="\n$(composer CycloneDX:make-sbom --help &> /dev/null && pass "cyclonedx-php" || fail "cyclonedx-php")"
  result+="\n$(cyclonedx-py --help &> /dev/null && pass "cyclonedx-py" || fail "cyclonedx-py")"
  result+="\n$(cargo cyclonedx -h &> /dev/null && pass "cyclonedx-cargo" || fail "cyclonedx-cargo")"
  result+="\n$(gobom -h &> /dev/null && pass "gobom" || fail "gobom")"
  result+="\n$(jake --help &> /dev/null && pass "jake" || fail "jake")"
  result+="\n$(java -jar /usr/local/bin/jbom.jar -D &> /dev/null && pass "jbom" || fail "jbom")"
  result+="\n$(retire -h &> /dev/null && pass "retire.js" || fail "retire.js")"
  result+="\n$(sbom4files -h &> /dev/null && pass "sbom4files" || fail "sbom4files")"
  result+="\n$(sbom4python -h &> /dev/null && pass "sbom4python" || fail "sbom4python")"
  result+="\n$(sbom4rust -h &> /dev/null && pass "sbom4rust" || fail "sbom4rust")"
  result+="\n$(sbom-tool -h &> /dev/null && pass "sbom-tool" || fail "sbom-tool")"
  result+="\n$(spdx-sbom-generator -h &> /dev/null && pass "spdx-sbom-generator" || fail "spdx-sbom-generator")"
  result+="\n$(syft -h &> /dev/null && pass "syft" || fail "syft")"

  echo -e "\nTOOLS"
  echo -e "$result" | column -t
}

main(){
  # Setup
  apt update
  apt install -y bsdmainutils

  echo "-= OSI VALIDATION =-"
  verify_lang
  verify_pm
  verify_tools
}

main