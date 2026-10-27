set -e
cd ../$1/target/$2
PATH=$PATH:/usr/local/bin
S3=s3://dev-dwo-nl-581490975307-eu-west-1-an/apps
X=echo
X=
OPTIONS=-rclD
if test -f $2/$2.nocache.js
then

$X aws --profile prod s3 cp --recursive --acl public-read $2 $S3/$2
$X aws --profile prod s3 cp --acl public-read $3.css $S3/$3.css

else
	echo $2 missing in $(pwd)
fi
