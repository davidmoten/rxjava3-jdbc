#!/bin/bash
set -e
cd rxjava3-jdbc
NAME=${PWD##*/}
mvn site
cd ../../davidmoten.github.io
git pull
mkdir -p $NAME
cp -r ../$NAME/rxjava3-jdbc/target/site/* $NAME/
git add .
git commit -am "update site reports"
git push
cd ../$NAME
