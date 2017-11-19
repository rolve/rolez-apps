cd ../../../lightweight-rolez/ch.trick17.rolez.checked.transformer/sootOutput/appKmeans/

jar cvf checked.jar classes/*

cd ../../../../rolez-apps/k-means/lib

cp ../../../lightweight-rolez/ch.trick17.rolez.checked.transformer/sootOutput/appKmeans/checked.jar .

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=checked.jar -DgroupId=k-means -DartifactId=k-means -Dversion=1.0 -Dpackaging=jar -DlocalRepositoryPath=.