set -e
. ~/.bashrc
cd ../DWOinteraction; mvn install
cd ../XmlRpcGWT; mvn install
cd ../IdeasGWT; mvn install
cd ../WiskOpdrGWT; mvn install
cd ../DWOformule; mvn install
cd ../KeyboardGWT; mvn install
cd ../GraphToolGWT; mvn install
cd ../WriteMathGWT; mvn install

cd ../DWOPlayer

mvn package -P Trifork -Dgwt.compiler.force=true -Dgwt.style=OBF

DEST=../WiskOpdr-facet/resources/fi/wiskopdr/ref/html
cp target/Trifork/Trifork/????????????????????????????????.cache.html $DEST/Player.cache.html

TODAY=$(date +%Y%m%d)
CITO=/Volumes/fisme-sites/www/dwo/cito
( cd $CITO;
  if ! test -e _sources/$TODAY; then mkdir _sources/$TODAY; cp *.jar _sources/$TODAY/; fi
)

cd ../WiskOpdr-facet/toolfiles; 
sh -ex resources.sh
sh -ex deploy.sh

