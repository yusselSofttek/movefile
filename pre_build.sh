#!/bin/sh
cd HelloWorldFunction
echo 'Running mvn with following versions:'
mvn -v
mvn clean
mvn package

ls -lah ~/.m2/
