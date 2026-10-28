#!/bin/bash
. ~/.bashrc
PATH=$PATH:/usr/local/bin
S3=s3://dev-dwo-nl-581490975307-eu-west-1-an/apps
set -e
PROD="--profile prod"
#PROD=

cd ../WiskOpdrPlayer
mvn clean verify -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2 -Dgwt.style=PRETTY -Pgithub
(cd target/WiskOpdrPlayer; 
    
	aws $PROD s3 cp --acl public-read --recursive DWOplayer $S3/DWOplayer;\
	aws $PROD s3 cp --acl public-read DWOplayer.css $S3/;\
	aws $PROD s3 cp --acl public-read PrintPlayer.css $S3/;\
	aws $PROD s3 cp --acl public-read KeyboardGWT.css $S3/;\	
)

cd ../WidgetPlayer
mvn clean verify -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2 -Pgithub -o
(cd target/WidgetPlayer;\
	aws $PROD s3 cp --acl public-read --recursive WidgetPlayer $S3/WidgetPlayer;\
)

cd ../PrintPlayer
mvn clean verify -Dgwt.compiler.force=true -Dgwt.compiler.localWorkers=2 -Pgithub -Dgwt.style=PRETTY -o
(cd target/PrintPlayer;\
	aws $PROD s3 cp --acl public-read --recursive PrintPlayer $S3/PrintPlayer;\
)
