set -e
cd ../$1/target/$3
PATH=$PATH:/usr/local/bin
W=/Volumes/fisme-sites/www-dev/dwo/apps
D=../../../DWOplayer/war
#G=../../../DWOApp/war/dwo/apps
S3=s3://test-dwo-nl/apps
#S3=s3://cds.dwo.nl/apps
X=echo
X=
OPTIONS=-rclD
if test -f $2/$2.nocache.js
then
#$X rsync --delete $OPTIONS $2 $3.css $D/
$X rsync --delete $OPTIONS $2 $3.css $W/
$X rsync --delete $OPTIONS $2 $3.css $W/2014_v1_0/
$X rsync --delete $OPTIONS $2 $3.css $W/plantyn/
#$X rsync --delete $OPTIONS $2 $3.css $G/
$X aws --profile prod s3 cp --recursive --acl public-read $2 $S3/$2
$X aws --profile prod s3 cp --acl public-read $3.css $S3/$3.css
else
	echo $2 missing in $(pwd)
fi
