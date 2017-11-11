cd ../

mvn dependency:purge-local-repository -DmanualInclude="mergesort:mergesort"

cd lib

rm -r mergesort
rm checked.jar