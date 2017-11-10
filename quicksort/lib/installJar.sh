cd ../../../lightweight-rolez/ch.trick17.rolez.checked.transformer/sootOutput/quicksortChecked/

jar cvf classes.jar classes/*

cd ../../../../rolez-apps/quicksort/lib

cp ../../../lightweight-rolez/ch.trick17.rolez.checked.transformer/sootOutput/quicksortChecked/classes.jar .

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=classes.jar -DgroupId=quicksort -DartifactId=quicksort -Dversion=1.0 -Dpackaging=jar -DlocalRepositoryPath=.