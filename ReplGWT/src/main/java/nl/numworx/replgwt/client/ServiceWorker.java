package nl.numworx.replgwt.client;

import com.google.gwt.core.client.JavaScriptObject;

class ServiceWorker extends JavaScriptObject {

	  public final native void postMessage(String message) /*-{
	    this.postMessage(message);
	  }-*/;
	  
	  protected ServiceWorker() {}
  }