#!/bin/bash
#
# Thomas Freese
#
echo KODI Client

BASEDIR=$PWD #Verzeichnis des Callers, aktuelles Verzeichnis
#BASEDIR=$(dirname $0) #Verzeichnis des Skripts
cd $(dirname $0)

if [ ! -f target/classes/de/freese/mediathek/kodi/javafx/KODIJavaFXClient.class ]; then
    mvn -q compile
fi

# Ausführung in der gleichen Runtime-Instanz wie Maven.
# mvn -q exec:java # In POM definiert
# java.util.Arrays.useLegacyMergeSort=true definiert in MAVEN_OPS (.bashrc)
mvn -q exec:java -Dexec.mainClass="de.freese.mediathek.kodi.javafx.KODIJavaFXClient" -Dexec.args="sqlite" -Dexec.classpathScope=runtime

# Ausführung in einer separaten Runtime-Instanz.
#mvn -q exec:exec # In POM definiert
#mvn -q exec:exec -Dexec.executable="java" -Dexec.args="%classpath" -Dexec.mainClass="de.freese.mediathek.kodi.javafx.KODIJavaFXClient"

cd "$BASEDIR"

# Shell offen lassen.
#$SHELL


#mvn -q compile -f ~/git/mediathek/de.freese.xbmc/xbmc-core/pom.xml
#mvn -q compile
#mvn -q exec:exec -Dexec.executable="/opt/jdk1.8.0_05/bin/java" -Dexec.args="-classpath %classpath de.freese.mediathek.kodi.javafx.KODIJavaFXClient" -Dexec.classpathScope=runtime -Dexec.daemonThreadJoinTimeout=1

#/opt/jdk-1.8/bin/java \
#-classpath target/classes\
#:/opt/jdk-1.8/jre/lib/ext/jfxrt.jar\
#:/home/tommy/git/mediathek/de.freese.xbmc/xbmc-core/target/classes\
#:/home/tommy/.m2/repository/org/slf4j/slf4j-api/1.7.12/slf4j-api-1.7.12.jar\
#:/home/tommy/.m2/repository/org/slf4j/jcl-over-slf4j/1.7.12/jcl-over-slf4j-1.7.12.jar\
#:/home/tommy/.m2/repository/org/slf4j/slf4j-simple/1.7.12/slf4j-simple-1.7.12.jar\
#:/home/tommy/.m2/repository/org/springframework/spring-context/4.1.6.RELEASE/spring-context-4.1.6.RELEASE.jar\
#:/home/tommy/.m2/repository/org/springframework/spring-core/4.1.6.RELEASE/spring-core-4.1.6.RELEASE.jar\
#:/home/tommy/.m2/repository/org/springframework/spring-beans/4.1.6.RELEASE/spring-beans-4.1.6.RELEASE.jar\
#:/home/tommy/.m2/repository/org/springframework/spring-aop/4.1.6.RELEASE/spring-aop-4.1.6.RELEASE.jar\
#:/home/tommy/.m2/repository/org/springframework/spring-expression/4.1.6.RELEASE/spring-expression-4.1.6.RELEASE.jar\
#:/home/tommy/.m2/repository/org/springframework/spring-tx/4.1.6.RELEASE/spring-tx-4.1.6.RELEASE.jar\
#:/home/tommy/.m2/repository/aopalliance/aopalliance/1.0/aopalliance-1.0.jar\
#:/home/tommy/.m2/repository/org/springframework/spring-jdbc/4.1.6.RELEASE/spring-jdbc-4.1.6.RELEASE.jar\
#:/home/tommy/.m2/repository/org/apache/commons/commons-lang3/3.4/commons-lang3-3.4.jar\
#:/home/tommy/.m2/repository/commons-codec/commons-codec/1.10/commons-codec-1.10.jar\
#:/home/tommy/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar\
#:/home/tommy/.m2/repository/mysql/mysql-connector-java/5.1.35/mysql-connector-java-5.1.35.jar \
#de.freese.mediathek.kodi.javafx.KODIJavaFXClient
#:/home/tommy/.m2/repository/de/freese/xbmc/xbmc-core/0.0.1-SNAPSHOT/xbmc-core-0.0.1-SNAPSHOT.jar\