#!/bin/bash

benchmarks="histogram k-means montecarlo idea mergesort quicksort"


for b in $benchmarks
do
	./clean.sh $b
    cd $b/lib
	./installJar.sh
	cd ..
    mvn clean compile exec:java
    cp *.pdf ..
    cd ..
done


read first _ <<< "$benchmarks"
head -n 1 "$first/results.tsv" > all_results.tsv

for b in $benchmarks
do
    tail -n +2 "$b/results.tsv" >> all_results.tsv
done
