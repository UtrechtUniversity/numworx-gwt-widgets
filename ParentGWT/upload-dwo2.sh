cd ../../workspace-neon/DWOplayer/target
set -ex
ssh-add ~/.ssh/root-keys.pem
V=$(ssh ubuntu@oldtest.dwo.nl ls /opt/dwo/tomcat-dwo/webapps/dwo#tablet##*.war|tail -1 | sed -e s/^.*##// -e s/.war//)
V=$(expr $V + 1)
scp DWO2player-dwo2.war ubuntu@oldtest.dwo.nl:dwo#tablet##$V.war
ssh ubuntu@oldtest.dwo.nl sudo mv dwo#tablet##$V.war /opt/dwo/tomcat-dwo/webapps/
