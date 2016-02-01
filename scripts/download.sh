#!/bin/sh
URL="http://epc.ub.uu.se/nexus/service/local/artifact/maven/redirect?r=epc-snapshot"
DEST="/home/one/epc-apps"

wget "$URL&g=se.uu.ub.cora&a=therest&v=LATEST&e=war" -O $DEST/cora.war
wget "$URL&g=se.uu.ub.cora&a=therest&v=LATEST&e=zip&c=fitnesse" -O $DEST/therest-fitnesse.zip
wget "$URL&g=se.uu.ub.cora&a=jsclient&v=LATEST&e=zip&c=fitnesse" -O $DEST/jsclient-fitnesse.zip
