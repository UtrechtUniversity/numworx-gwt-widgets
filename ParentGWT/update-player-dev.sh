#!/bin/bash
. ~/.bashrc
PATH=$PATH:/usr/local/bin
S3=s3://test-dwo-nl/apps
S3=s3://ebs-dev-dwo-nl/apps
set -e
PROD="--profile prod"
PROD=
cd ../DWOPlayer;
mvn clean
mvn package -P WiskOpdrPlayer -Dgwt.compiler.force=true
(cd target/WiskOpdrPlayer;\
	aws $PROD s3 sync --delete --acl public-read DWOplayer $S3/DWOplayer;\
	aws $PROD s3 cp --acl public-read DWOplayer.css $S3/;\
)
mvn package -P NoordhoffPlayer -Dgwt.compiler.force=true
(cd target/NoordhoffPlayer;\
	aws $PROD s3 sync --acl public-read --delete DWOplayer $S3/noordhoff/DWOplayer;\
	aws $PROD s3 cp --acl public-read DWOplayer.css $S3/noordhoff/;\
)
