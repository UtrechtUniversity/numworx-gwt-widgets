rem cd ../DWOinteraction
rem call mvn install
rem cd ../XmlRpcGWT
rem call mvn install
rem cd ../IdeasGWT
rem call mvn install
rem cd ../WiskOpdrGWT
rem mvn install
rem cd ../DWOformule
rem call mvn install
rem cd ../KeyboardGWT
rem call mvn install
cd ../GraphToolGWT
call mvn install
cd ../WriteMathGWT
call mvn install
cd ../DWO-rest-lib
call mvn install
cd ../DWO-gwt-lib
call mvn install
cd ../Account
call mvn install
cd ../DWOplayer;
call mvn install -Dgwt.compiler.force=true -P DWO2player
cd ../ParentGWT