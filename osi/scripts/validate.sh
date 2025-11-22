#!/bin/bash

# File: validate.sh
# Validate OSI environment by checking for Languages, Package Managers, and Tools
#
# @author Derek Garcia


RED="\e[31m"
GREEN="\e[32m"
CLR="\e[0m"

# Update env vars with what's installed on the box
function update_env(){
  case "$2" in
    # Update lang if not present
    1)
      [ "$(echo "$OSI_LANG" | grep -qE "$1"; echo $?)" -eq 1 ] && export OSI_LANG="${OSI_LANG:+$OSI_LANG:}$1"
      ;;
    # Update package manager if not present
    2)
      [ "$(echo "$OSI_PM" | grep -qE "$1"; echo $?)" -eq 1 ] && export OSI_PM="${OSI_PM:+$OSI_PM:}$1"
      ;;
    # Update tool if not present
    3)
      [ "$(echo "$OSI_TOOL" | grep -qE "$1"; echo $?)" -eq 1 ] && export OSI_TOOL="${OSI_TOOL:+$OSI_TOOL:}$1"
      ;;
  esac
}

function pass() {
  update_env "$1" "$2"
  echo -e "$1 : ${GREEN}PASS${CLR}"
}

function fail() {
  echo -e "$1 : ${RED}FAIL${CLR}"
}

# Verify Languages are installed
function verify_lang(){
  echo -e "\nLANGUAGES"

  python3 --version &> /dev/null && pass "python" 1 || fail "python"
  java --version &> /dev/null && pass "java" 1 || fail "java"
  dotnet --info &> /dev/null && pass ".net" 1 || fail ".net"
  go version &> /dev/null && pass "go" 1 || fail "go"
  node -v &> /dev/null && pass "node.js" 1 || fail "node.js"
  rustc --version &> /dev/null && pass "rust" 1 || fail "rust"
  php --version &> /dev/null && pass "php" 1 || fail "php"

}

# Verify Package managers are installed
function verify_pm(){
  echo -e "\nPACKAGE MANAGERS"

  mvn --version &> /dev/null && pass "maven" 2 || fail "maven"
  gradle --version &> /dev/null && pass "gradle" 2 || fail "gradle"
  pip --version &> /dev/null && pass "pip" 2 || fail "pip"
  dotnet nuget --version &> /dev/null && pass "nuget" 2 || fail "nuget"
  conan --version &> /dev/null && pass "conan" 2 || fail "conan"
  composer --version &> /dev/null && pass "composer" 2 || fail "composer"
  cargo --version &> /dev/null && pass "cargo" 2 || fail "cargo"
  npm --version &> /dev/null && pass "npm" 2 || fail "npm"

}

# Verify Tools are installed
function verify_tools(){
  echo -e "\nTOOLS"

  cdxgen -h &> /dev/null && pass "cdxgen" 3 || fail "cdxgen"
  dotnet covenant -h &> /dev/null && pass "covenant" 3 || fail "covenant"
  cdx-bower-bom -h &> /dev/null && pass "cdx-bower-bom" 3 || fail "cdx-bower-bom"
  cyclonedx-conan --help &> /dev/null && pass "cyclonedx-conan" 3 || fail "cyclonedx-conan"
  cyclonedx-go -h &> /dev/null && pass "cyclonedx-go" 3 || fail "cyclonedx-go"
  composer CycloneDX:make-sbom --help &> /dev/null && pass "cyclonedx-php" 3 || fail "cyclonedx-php"
  cyclonedx-py --help &> /dev/null && pass "cyclonedx-py" 3 || fail "cyclonedx-py"
  cargo cyclonedx -h &> /dev/null && pass "cyclonedx-cargo" 3 || fail "cyclonedx-cargo"
  gobom -h &> /dev/null && pass "gobom" 3 || fail "gobom"
  jake --help &> /dev/null && pass "jake" 3 || fail "jake"
  java -jar /usr/local/bin/jbom.jar -D &> /dev/null && pass "jbom" 3 || fail "jbom"
  retire -h &> /dev/null && pass "retire.js" 3 || fail "retire.js"
  sbom4files -h &> /dev/null && pass "sbom4files" 3 || fail "sbom4files"
  sbom4python -h &> /dev/null && pass "sbom4python" 3 || fail "sbom4python"
  sbom4rust -h &> /dev/null && pass "sbom4rust" 3 || fail "sbom4rust"
  sbom-tool --version &> /dev/null && fail "sbom-tool" || pass "sbom-tool" 3  # returns 1 on success
  spdx-sbom-generator -h &> /dev/null && pass "spdx-sbom-generator" 3 || fail "spdx-sbom-generator"
  syft -h &> /dev/null && pass "syft" 3 || fail "syft"
  
  # Vulnerability Scanners
  grype version &> /dev/null && pass "grype" 3 || fail "grype"
  trivy --version &> /dev/null && pass "trivy" 3 || fail "trivy"
  osv-scanner --version &> /dev/null && pass "osv-scanner" 3 || fail "osv-scanner"
  dependency-check.sh --version &> /dev/null && pass "dependency-check" 3 || fail "dependency-check"

}

main(){
  echo "-= OSI VALIDATION =-"
  verify_lang
  verify_pm
  verify_tools
}

main