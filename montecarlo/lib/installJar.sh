cd ../../../lightweight-rolez/ch.trick17.rolez.checked.transformer/sootOutput/appMonteCarlo/

jar cvf checked.jar classes/*

cd ../../../../rolez-apps/montecarlo/lib

cp ../../../lightweight-rolez/ch.trick17.rolez.checked.transformer/sootOutput/appMonteCarlo/checked.jar .

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=checked.jar -DgroupId=montecarlo -DartifactId=montecarlo -Dversion=1.0 -Dpackaging=jar -DlocalRepositoryPath=.