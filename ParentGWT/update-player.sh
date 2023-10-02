#!/bin/bash
. ~/.bashrc
PATH=$PATH:/usr/local/bin
S3=s3://test-dwo-nl/apps
#S3=s3://ebs-dev-dwo-nl/apps
set -e
PROD="--profile prod"
#PROD=
cd ../DWOPlayer;

W=/Volumes/fisme-sites/www-dev/dwo
W=$USER@gemini.science.uu.nl:/science/wwwprojects/FI-Sites/www-dev/dwo
TODAY=$(date +%-d-%-m-%Y)

T=$W/apps/DWOplayer
#if ! test -e $T-$TODAY; then mv $T $T-$TODAY; fi
#T=$W/apps/noordhoff/DWOplayer
#if ! test -e $T-$TODAY; then mv $T $T-$TODAY; fi
OPTIONS=-rclD
mvn clean verify -P WiskOpdrPlayer -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2
(cd target/WiskOpdrPlayer; 
	rsync --delete $OPTIONS DWOplayer KeyboardGWT.css DWOplayer.css $W/apps/;\
	aws $PROD s3 cp --acl public-read --recursive DWOplayer $S3/DWOplayer;\
	aws $PROD s3 cp --acl public-read DWOplayer.css $S3/;\
	aws $PROD s3 cp --acl public-read KeyboardGWT.css $S3/;\
#	azcopy sync DWOplayer.css https://numworxprod.blob.core.windows.net/test/apps/DWOplayer.css?"$SAS"
#	azcopy sync KeyboardGWT.css https://numworxprod.blob.core.windows.net/test/apps/KeyboardGWT.css?"$SAS"
#	azcopy sync DWOplayer/ https://numworxprod.blob.core.windows.net/test/apps/DWOplayer/?"$SAS" --recursive=true --delete-destination true
)
mvn clean verify -P WidgetPlayer -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2
(cd target/WidgetPlayer; rsync --delete $OPTIONS WidgetPlayer $W/apps/;\
	aws $PROD s3 cp --acl public-read --recursive WidgetPlayer $S3/WidgetPlayer;\
#	azcopy sync WidgetPlayer/ https://numworxprod.blob.core.windows.net/test/apps/WidgetPlayer/?"$SAS" --recursive=true --delete-destination true
)

#mvn package -P NoordhoffPlayer -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2
#(cd target/NoordhoffPlayer; rsync --delete $OPTIONS DWOplayer KeyboardGWT.css DWOplayer.css $W/apps/noordhoff/;\
#	aws $PROD s3 cp --acl public-read --recursive DWOplayer $S3/noordhoff/DWOplayer;\
#	aws $PROD s3 cp --acl public-read DWOplayer.css $S3/noordhoff/;\
#	aws $PROD s3 cp --acl public-read KeyboardGWT.css $S3/noordhoff/;\
#)
#mvn package -P CDPLogicaPlayer -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2
#(cd target/CDPLogicaPlayer; rsync --delete $OPTIONS DWOplayer KeyboardGWT.css DWOplayer.css $W/apps/2014_v1_0)
#cd $W/apps
#TODAY=$(date +%Y%m%d)
#rm -rf 2014_v1_0-$TODAY.zip
#zip -r 2014_v1_0-$TODAY.zip 2014_v1_0
