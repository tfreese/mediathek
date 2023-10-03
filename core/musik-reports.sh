#!/bin/bash
#
# Thomas Freese
echo "Musik Reports"

#BASEDIR=$PWD #Verzeichnis des Callers, aktuelles Verzeichnis
BASEDIR=$(dirname $0) #Verzeichnis des Skripts
cd $BASEDIR

#if [ ! -f target/classes/de/freese/mediathek/report/MultimediaReporter.class ]; then
#    mvn clean compile;
#fi

# Ausführung in der gleichen Runtime-Instanz wie Maven, in POM definiert.
# mvn -q exec:java -Dexec.mainClass="..." -Dexec.classpathScope=runtime -Dexec.daemonThreadJoinTimeout=120000 -Dexec.killAfter=-1
# mvn -q exec:java -Dexec.mainClass="..." -D<PARAM>="..."

# Ausführung in einer separaten Runtime-Instanz, in POM definiert.
# mvn -q exec:exec -Dexec.executable="java" -Dexec.args="%classpath" -Dexec.mainClass="..."

# Ohne Plugin-Konfiguration
# mvn -o -q exec:java -Dexec.mainClass="..." -Dexec.classpathScope=runtime

# Mit Plugin-Konfiguration
## mvn -o -q exec:exec;
#mvn -q exec:exec;
## mvn exec:exec;

gradle --quiet run

cd ~

# Shell offen lassen.
#$SHELL
