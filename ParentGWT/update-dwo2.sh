#!/bin/bash
. ~/.bashrc
set -e
#cd ../DWOinteraction; mvn install
#cd ../XmlRpcGWT; mvn install
#cd ../IdeasGWT; mvn install
#cd ../WiskOpdrGWT; mvn install
#cd ../DWOformule; mvn install
#cd ../KeyboardGWT; mvn install
#cd ../GraphToolGWT; mvn install
#cd ../WriteMathGWT; mvn install
#cd ../DWO-rest-lib; mvn install
#cd ../DWO-gwt-lib; mvn install
#cd ../Account; mvn install

cd ../DWOPlayer;
mvn clean deploy -U -Dgwt.compiler.force=true -P DWO2player
