set -ex
PATH=$PATH:/usr/local/bin
X=echo
X=
if test -z "$KEY" 
then
   echo no key
   exit 1
fi
if test -z "$SAS" 
then
. generate-sas.sh
fi

cd ../$1/target/$2
if test -f $2/$2.nocache.js
then

azcopy sync $3.css https://$ACCOUNT.blob.core.windows.net/$CONTAINER/apps/$3.css?"$SAS"
azcopy sync $2/ https://$ACCOUNT.blob.core.windows.net/$CONTAINER/apps/$2/?"$SAS" --recursive=true --delete-destination true

else
	echo $2 missing in $(pwd)
fi
