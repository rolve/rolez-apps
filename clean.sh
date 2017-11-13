#!/bin/bash

rm -r $1/lib/$1
rm $1/lib/checked.jar

cd $1

mvn dependency:purge-local-repository -DmanualInclude="$1:$1"
