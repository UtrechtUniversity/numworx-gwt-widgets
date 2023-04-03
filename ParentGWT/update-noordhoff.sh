#!/bin/bash
. ~/.bashrc
set -e
cd ../DWOPlayer;

W=/volumes/fisme-sites/www-dev/dwo
TODAY=$(date +%-d-%-m-%Y)
#T=$W/tablet/DWOplayer
#if ! test -e $T-$TODAY; then mv $T $T-$TODAY; fi
#T=$W/apps/DWOplayer
#if ! test -e $T-$TODAY; then mv $T $T-$TODAY; fi
T=$W/apps/noordhoff/DWOplayer
if ! test -e $T-$TODAY; then mv $T $T-$TODAY; fi
#mvn clean package -Dgwt.compiler.force=true
#(cd target/DWOplayer; rsync --delete -rav DWOplayer $W/tablet/)
#mvn package -P WiskOpdrPlayer -Dgwt.compiler.force=true
#(cd target/WiskOpdrPlayer; rsync --delete -rav DWOplayer DWOplayer.css $W/apps/)
mvn package -P NoordhoffPlayer -Dgwt.compiler.force=true
(cd target/NoordhoffPlayer; rsync --delete -rav DWOplayer $W/apps/noordhoff/)
mvn package -P CDPLogicaPlayer -Dgwt.compiler.force=true
(cd target/CDPLogicaPlayer; rsync --delete -rav DWOplayer DWOplayer.css $W/apps/2014_v1_0)
cd $W/apps
TODAY=$(date +%Y%m%d)
rm -rf 2014_v1_0-$TODAY.zip
zip -r 2014_v1_0-$TODAY.zip 2014_v1_0
