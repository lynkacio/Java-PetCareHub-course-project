#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
rm -rf out dist
mkdir -p out dist
javac -encoding UTF-8 -d out $(find src/main/java -name '*.java')
printf 'Main-Class: com.petcarehub.app.Main\n' > MANIFEST.MF
jar cfm dist/petcarehub.jar MANIFEST.MF -C out .
rm MANIFEST.MF
echo "Built dist/petcarehub.jar"
