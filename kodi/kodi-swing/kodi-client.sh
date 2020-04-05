#!/bin/bash
#
# Thomas Freese
#
echo KODI Client

BASEDIR=$PWD #Verzeichnis des Callers, aktuelles Verzeichnis
#BASEDIR=$(dirname $0) #Verzeichnis des Skripts
cd $(dirname $0)

if [ ! -f target/classes/de/freese/mediathek/kodi/swing/KODISwingClient.class ]; then
    mvn -q compile
fi

# Ausführung in der gleichen Runtime-Instanz wie Maven.
# mvn -q exec:java # In POM definiert
# java.util.Arrays.useLegacyMergeSort=true definiert in MAVEN_OPS (.bashrc)
mvn -q exec:java -Dexec.mainClass="de.freese.mediathek.kodi.swing.KODISwingClient" -Dexec.args="sqlite" -Dexec.classpathScope=runtime

# Ausführung in einer separaten Runtime-Instanz.
#mvn -q exec:exec # In POM definiert
#mvn -q exec:exec -Dexec.executable="java" -Dexec.args="%classpath" -Dexec.mainClass="de.freese.mediathek.kodi.swing.KODISwingClient"

cd "$BASEDIR"

# Shell offen lassen.
#$SHELL
