cd ../../../lightweight-rolez/ch.trick17.rolez.checked.transformer/sootOutput/appHistogram/

jar cvf checked.jar classes/*

cd ../../../../rolez-apps/histogram/lib

cp ../../../lightweight-rolez/ch.trick17.rolez.checked.transformer/sootOutput/appHistogram/checked.jar .

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=checked.jar -DgroupId=histogram -DartifactId=histogram -Dversion=1.0 -Dpackaging=jar -DlocalRepositoryPath=.