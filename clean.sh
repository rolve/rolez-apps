#!/bin/bash

rm -r $1/lib/idea
rm $1/lib/checked.jar

cd $1

mvn dependency:purge-local-repository -DmanualInclude="idea:idea"
