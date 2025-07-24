#!/bin/bash
#
# Thomas Freese
echo "Musik Reports"

# BASEDIR=$PWD # Caller directory, current directory
BASEDIR="$(dirname "$0")" # Script directory
cd "$BASEDIR" || exit

rm -rf ../.gradle/configuration-cache/
#../gradlew --quiet :core:build :core:runMultimediaReporter
../gradlew --quiet :core:runMultimediaReporter
#gradle --quiet :core:build :core:runMultimediaReporter

cd ~ || exit
