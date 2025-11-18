set -e
cd ../$1/target/$3
PATH=$PATH:/usr/local/bin
W=$USER@gemini.science.uu.nl:/science/wwwprojects/FI-Sites/www/dwo/apps
D=../../../DWOplayer/war
S3=s3://test-dwo-nl/apps
#S3=s3://cds.dwo.nl/apps
X=echo
X=
OPTIONS=-rclD
if test -f $2/$2.nocache.js
then
$X rsync --delete $OPTIONS $2 $3.css $W/

$X aws --profile prod s3 cp --recursive --acl public-read $2 $S3/$2
$X aws --profile prod s3 cp --acl public-read $3.css $S3/$3.css

#azcopy sync $3.css https://numworxprod.blob.core.windows.net/test/apps/$3.css?"$SAS"
#azcopy sync $2/ https://numworxprod.blob.core.windows.net/test/apps/$2/?"$SAS" --recursive=true --delete-destination true

else
	echo $2 missing in $(pwd)
fi
