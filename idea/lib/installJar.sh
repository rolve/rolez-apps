cd ../../../lightweight-rolez/ch.trick17.rolez.checked.transformer/sootOutput/appIdea/

jar cvf checked.jar classes/*

cd ../../../../rolez-apps/idea/lib

cp ../../../lightweight-rolez/ch.trick17.rolez.checked.transformer/sootOutput/appIdea/checked.jar .

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=checked.jar -DgroupId=idea -DartifactId=idea -Dversion=1.0 -Dpackaging=jar -DlocalRepositoryPath=.