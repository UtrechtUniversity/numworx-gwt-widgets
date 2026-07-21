<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
/**
 * script en css loader
 */
<% 
	String cdn = System.getProperty("CDN_HOST", "cdn.dwo.nl");
	String env = System.getProperty("DWO_ENV","app");
	String cas = System.getProperty("DWO_CAS", "/ideas/IdeasServlet");
	String hub = System.getProperty("DWO_HUB", "https://hub-dev.dwo.nl/hub/api/");
	String chat = System.getProperty("DWO_CHAT", "chat-dev.dwo.nl");
    response.setHeader("Cache-Control", "max-age=3600"); // HTTP 1.1.
    response.setDateHeader("Expires", System.currentTimeMillis() + 1000*60*60 );
%>
var deploy = "//<%= cdn %>/apps/"
var dwo_env = "<%= env %>"
var casServer = "<%= cas %>"
var hubServer = "<%=hub%>"
var chatServer = "<%=chat%>"

function script(name) {
	var elem = document.createElement('script');
	elem.src = deploy + name;
	elem.async = false;
	document.head.appendChild(elem);
}

function css(name) {
	var elem = document.createElement('link');
	elem.type='text/css';
	elem.rel = 'stylesheet';
	elem.href = deploy + name;
	document.head.appendChild(elem);
}