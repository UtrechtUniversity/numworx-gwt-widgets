set -ex
cd ../$1/target/$3
PATH=$PATH:/usr/local/bin
W=/volumes/fisme-sites/www-dev/dwo/apps
D=../../../../workspace-neon/DWOplayer/war
G=../../../DWOApp/war/dwo/apps
#S3=s3://test-dwo-nl/apps
#S3=s3://cds.dwo.nl/apps
S3=s3://ebs-dev-dwo-nl/apps
X=echo
X=
OPTIONS=-rclD
if test -f $2/$2.nocache.js
then
#$X rsync --delete $OPTIONS $2 $3.css $D/
#$X rsync --delete $OPTIONS $2 $3.css $W/
#$X rsync --delete $OPTIONS $2 $3.css $W/2014_v1_0
#$X rsync --delete $OPTIONS $2 $3.css $G/
$X aws s3 sync --acl public-read --delete $2 $S3/$2
$X aws s3 cp --acl public-read $3.css $S3/$3.css
else
	echo $2 missing in $(pwd)
fi
