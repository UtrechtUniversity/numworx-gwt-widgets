#!/bin/bash
. ~/.bashrc
cd ../DWOPlayer;

W=/volumes/fisme-sites/www-dev/dwo
TODAY=$(date +%-d-%-m-%Y)
T=$W/apps/noordhoff/DWOplayer
if ! test -e $T-$TODAY; then mv $T $T-$TODAY; fi
mvn package -P NoordhoffPlayer -Dgwt.compiler.force=true
(cd target/NoordhoffPlayer; rsync --delete -rav DWOplayer $W/apps/noordhoff/)
