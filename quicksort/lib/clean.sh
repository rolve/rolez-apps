cd ../

mvn dependency:purge-local-repository -DmanualInclude="quicksort:quicksort"

cd lib

rm -r quicksort
rm -r checked.jar