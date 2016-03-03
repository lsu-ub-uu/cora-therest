#!/usr/bin/env bash

export JAVA_HOME="/home/diva2/.jenkins/tools/hudson.model.JDK/JDK_8/"

DIRS="cora-beefeater cora-bookkeeper cora-spider cora-systemone"

for DIR in $DIRS
do
    cd ../$DIR
    /home/diva2/.jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.3.x/bin/mvn -P sonar sonar:sonar
done