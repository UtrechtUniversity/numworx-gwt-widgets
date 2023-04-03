set -e
#cd ../GraphToolGWT; mvn install
#cd ../WriteMathGWT; mvn install

cd ../../workspace-neon/DWOPlayer;

W=/volumes/fisme-sites/www-dev/dwo
TODAY=$(date +%-d-%-m-%Y)
mvn package -P TinCanPlayer -Dgwt.compiler.force=true
(cd target/TinCanPlayer; rsync --delete -rav DWOplayer DWOplayer.css $W/apps/2014_v1_0)
cd $W/apps
TODAY=$(date +%Y%m%d)b
rm -rf 2014_v1_0-$TODAY.zip
zip -r 2014_v1_0-$TODAY.zip 2014_v1_0
