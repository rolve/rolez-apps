cd ../../../lightweight-rolez/ch.trick17.rolez.checked.transformer/sootOutput/appMergesort/

jar cvf checked.jar classes/*

cd ../../../../rolez-apps/mergesort/lib

cp ../../../lightweight-rolez/ch.trick17.rolez.checked.transformer/sootOutput/appMergesort/checked.jar .

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=checked.jar -DgroupId=mergesort -DartifactId=mergesort -Dversion=1.0 -Dpackaging=jar -DlocalRepositoryPath=.