#!/bin/bash
echo "Copy webapps"
cp ~/epc-apps/*.war $CATALINA_HOME/webapps/.
echo "Removing working directories ..."
rm -fR $CATALINA_HOME/webapps/therest
rm -fR $CATALINA_HOME/webapps/cora
ls -lt $CATALINA_HOME/webapps/
echo "Removing catalina.out ..."
rm $CATALINA_HOME/logs/*
rm ~/logs/therest/*
