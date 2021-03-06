#!/bin/bash

benchmarks="animator histogram idea k-means mergesort montecarlo nbody quicksort raytracer"


for b in $benchmarks
do
    cd $b
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
