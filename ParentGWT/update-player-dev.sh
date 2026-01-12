#!/bin/bash
# copy to az, for teuniz.dwo.nl
. ./generate-sas.sh
PATH=$PATH:/usr/local/bin
set -e
cd ../WiskOpdrPlayer;

mvn clean verify -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2 -Dgwt.style=PRETTY -Pgithub
(cd target/WiskOpdrPlayer; 
	
	azcopy sync DWOplayer.css https://$ACCOUNT.blob.core.windows.net/$CONTAINER/apps/DWOplayer.css?"$SAS"
	azcopy sync KeyboardGWT.css https://$ACCOUNT.blob.core.windows.net/$CONTAINER/apps/KeyboardGWT.css?"$SAS"
	azcopy sync DWOplayer/ https://$ACCOUNT.blob.core.windows.net/$CONTAINER/apps/DWOplayer/?"$SAS" --recursive=true --delete-destination true
	
)
cd ../WidgetPlayer
mvn clean verify -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2 -Dgwt.style=PRETTY  -Pgithub
(cd target/WidgetPlayer;

	azcopy sync WidgetPlayer/ https://$ACCOUNT.blob.core.windows.net/$CONTAINER/apps/WidgetPlayer/?"$SAS" --recursive=true --delete-destination true
)
cd ../PrintPlayer
mvn clean verify -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2 -Dgwt.style=PRETTY  -Pgithub
(cd target/PrintPlayer;

        azcopy sync PrintPlayer/ https://$ACCOUNT.blob.core.windows.net/$CONTAINER/apps/PrintPlayer/?"$SAS" --recursive=true --delete-destination true
)

