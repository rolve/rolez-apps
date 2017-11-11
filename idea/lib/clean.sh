rm -r idea
rm -r checked.jar

cd ../

mvn dependency:purge-local-repository -DmanualInclude="idea:idea"

cd lib
