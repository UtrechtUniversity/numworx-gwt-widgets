set -ex
cd ../$1/target/$3
PATH=$PATH:/usr/local/bin
PW=/usr/local/etc/azssh.txt
X=echo
X=
if test -f $2/$2.nocache.js
then
sshpass -f $PW sftp numworxcontentdev.content.content@numworxcontentdev.blob.core.windows.net:apps <<EOF
put -r $2
put $3.css
EOF
else
	echo $2 missing in $(pwd)
fi
