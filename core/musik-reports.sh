#!/bin/bash
#
# Thomas Freese
echo "Musik Reports"

# BASEDIR=$PWD # Caller directory, current directory
BASEDIR="$(dirname $0)" # Script directory
cd "$BASEDIR" || exit

gradle --quiet :core:runMultimediaReporter

cd ~ || exit
