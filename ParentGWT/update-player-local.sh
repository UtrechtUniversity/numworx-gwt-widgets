#!/bin/bash
. ~/.bashrc
PATH=$PATH:/usr/local/bin
set -e
cd ../WiskOpdrPlayer;

W=$HOME/Public
TODAY=$(date +%-d-%-m-%Y)

T=$W/apps/DWOplayer
OPTIONS=-rclD
mvn clean verify -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2 -Dgwt.style=PRETTY
(cd target/WiskOpdrPlayer; 
    rsync --delete $OPTIONS DWOplayer KeyboardGWT.css DWOplayer.css PrintPlayer.css $W/apps/;
)
cd ../WidgetPlayer
mvn clean verify -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2
(cd target/WidgetPlayer; rsync --delete $OPTIONS WidgetPlayer $W/apps/;
)
cd ../PrintPlayer
mvn clean verify -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2
(cd target/PrintPlayer; rsync --delete $OPTIONS PrintPlayer $W/apps/;
)
