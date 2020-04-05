#!/bin/bash
#
# Thomas Freese
echo Musik Reports

#BASEDIR=$PWD #Verzeichnis des Callers, aktuelles Verzeichnis
BASEDIR=$(dirname $0) #Verzeichnis des Skripts
cd $BASEDIR

if [ ! -f target/classes/de/freese/mediathek/musik/MusikReporter.class ]; then
    mvn compile
fi

# mvn -q

# Ausführung in der gleichen Runtime-Instanz wie Maven.
# mvn -q exec:java # In POM definiert
# mvn -q exec:java -Dexec.mainClass="de.freese.mediathek.musik.MusikReporter" -Dexec.classpathScope=runtime
#-Dexec.daemonThreadJoinTimeout=120000 -Dexec.killAfter=-1

# Ausführung in einer separaten Runtime-Instanz.
mvn -q exec:exec # In POM definiert
#mvn -q exec:exec -Dexec.executable="java" -Dexec.args="%classpath" -Dexec.mainClass="de.freese.mediathek.musik.MusikReporter"

cd ~

# Shell offen lassen.
#$SHELL
