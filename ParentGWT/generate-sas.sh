export KEY=key
export SAS=sas
export CONTAINER=test
export ACCOUNT=numworxacc
. $HOME/aws.env
SAS=$(az storage container generate-sas --account-name $ACCOUNT --name $CONTAINER --auth-mode key  --permissions dlrw --expiry $EXP --account-key $KEY)
SAS=$(echo $SAS|tr -d '"')
