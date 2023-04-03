set -e
#cd ../DWOinteraction; mvn install
#cd ../XmlRpcGWT; mvn install
#cd ../IdeasGWT; mvn install
#cd ../WiskOpdrGWT; mvn install
#cd ../DWOformule; mvn install
#cd ../KeyboardGWT; mvn install
#cd ../GraphToolGWT; mvn install
#cd ../WriteMathGWT; mvn install

cd ../../workspace-neon/DWOPlayer;

W=/volumes/fisme-sites/www-dev/dwo
TODAY=$(date +%-d-%-m-%Y)
#T=$W/apps/noordhoff/DWOplayer
#if ! test -e $T-$TODAY; then mv $T $T-$TODAY; fi
#mvn package -P NoordhoffPlayer -Dgwt.compiler.force=true
#(cd target/NoordhoffPlayer; rsync --delete -rav DWOplayer $W/apps/noordhoff/)
mvn clean package -P CDPLogicaPlayer -Dgwt.compiler.force=true
(cd target/CDPLogicaPlayer; rsync --delete -rav DWOplayer DWOplayer.css $W/apps/2014_v1_0)
cd $W/apps
TODAY=$(date +%Y%m%d)a
rm -rf 2014_v1_0-$TODAY.zip
zip -r 2014_v1_0-$TODAY.zip 2014_v1_0
