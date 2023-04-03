#!/bin/bash
W=/Volumes/fisme-sites/www-dev/dwo
NOW=$(date +%Y%m%d)
cd $W/apps
zip -r numworx-engine-$NOW.zip *GWT.css *gwt *GWT geogebra DWOplayer DWOplayer.css scripts images .htaccess
