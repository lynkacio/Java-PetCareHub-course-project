#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
rm -rf out-test
mkdir -p out-test
javac -encoding UTF-8 -d out-test $(find src/main/java src/test/java -name '*.java')
java -ea -cp out-test com.petcarehub.SmokeTest
