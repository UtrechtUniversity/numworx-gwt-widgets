#!/bin/bash
. ~/.bashrc
set -ex
cd ../DWOPlayer;
OPTIONS=-rclDv

W=/Volumes/fisme-sites/www-dev/dwo
TODAY=$(date +%-d-%-m-%Y)
mvn package -P TinCanPlayer -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2
(cd target/TinCanPlayer; rsync --delete $OPTIONS DWOplayer DWOplayer.css KeyboardGWT.css images scripts $W/apps/plantyn/)
cd $W/apps
TODAY=$(date +%Y%m%d)
rm -rf plantyn-$TODAY.zip
zip -r plantyn-$TODAY.zip plantyn
