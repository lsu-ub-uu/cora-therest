#!/bin/bash
~/scripts/therest/fitnesse_stop.sh
~/tomcat_stop
~/scripts/therest/download.sh
~/scripts/therest/deploy.sh
~/tomcat_start
~/scripts/therest/fitnesse_start.sh
