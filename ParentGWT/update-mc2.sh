#!/bin/bash
. ~/.bashrc
set -e
#cd ../DWOinteraction; mvn install
#cd ../XmlRpcGWT; mvn install
#cd ../IdeasGWT; mvn install
#cd ../WiskOpdrGWT; mvn install
#cd ../DWOformule; mvn install
#cd ../KeyboardGWT; mvn install
#cd ../GraphToolGWT; mvn install
#cd ../WriteMathGWT; mvn install

cd ../DWOPlayer;

W=../../../DWOApp/war
TODAY=$(date +%-d-%-m-%Y)
T=$W/mcs/DWOplayer
#if ! test -e $T-$TODAY; then mv $T $T-$TODAY; fi
T=$W/dwo/apps/DWOplayer
#if ! test -e $T-$TODAY; then mv $T $T-$TODAY; fi
mvn package -Dgwt.compiler.force=true -P MC2LMS
(cd target/MC2LMS; rsync --delete -rav DWOplayer MCSquared.jsp $W/mcs/tablet)
mvn package -P MC2Player -Dgwt.compiler.force=true
(cd target/MC2Player; rsync --delete -rav DWOplayer DWOplayer.css images scripts MCSquared.jsp $W/dwo/apps/)
