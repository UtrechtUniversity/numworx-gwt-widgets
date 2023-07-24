set -ex
cd ../$1/target/$3
PATH=$PATH:/usr/local/bin
PW=/usr/local/etc/azssh.txt
X=echo
X=
if test -z "$KEY" 
then
   echo no key
   exit 1
fi
if test -z "$SAS" 
then
	EXP=$(date -v+1d +%Y-%m-%d)
	SAS=$(az storage container generate-sas --account-name numworxcontentdev --name content  --auth-mode key  --permissions dlrw --expiry $EXP --account-key $KEY)
	SAS=$(echo $SAS|tr -d '"')
fi



if test -f $2/$2.nocache.js
then
#sshpass -f $PW sftp numworxcontentdev.content.content@numworxcontentdev.blob.core.windows.net:apps <<EOF
#put -r $2
#put $3.css
#EOF
#EXP=$(date -v+1d +%Y-%m-%d)
#SAS=$(az storage container generate-sas --account-name numworxcontentdev --name content  --auth-mode key  --permissions dlrw --expiry $EXP --account-key $KEY)
#SAS=$(echo $SAS|tr -d '"')

azcopy copy $3.css https://numworxcontentdev.blob.core.windows.net/content/apps/?"$SAS" --recursive=true
azcopy copy $2 https://numworxcontentdev.blob.core.windows.net/content/apps/?"$SAS" --recursive=true






else
	echo $2 missing in $(pwd)
fi
