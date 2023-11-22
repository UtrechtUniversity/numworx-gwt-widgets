package nl.numworx.replgwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;import com.google.gwt.webworker.client.ErrorEvent;
import com.google.gwt.webworker.client.ErrorHandler;
import com.google.gwt.webworker.client.MessageEvent;
import com.google.gwt.webworker.client.MessageHandler;
import com.google.gwt.webworker.client.Worker;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ReplGWT extends SimplePanel implements EntryPoint, InteractionStub, CBookEventListener, MessageHandler, ErrorHandler, Consumer<String>, RequestCallback {
  private static final Logger LOG = java.util.logging.Logger.getLogger("ReplGWT");
  private int width;
  private int height;
  String modules;
  Worker worker;
//  ServiceWorker service;
//  
//  static native void installServiceWorker(ReplGWT me) /*-{
//  	serviceWorker = $wnd.serviceWorker
//	me.@nl.numworx.replgwt.client.ReplGWT::setServiceWorker(Lnl/numworx/replgwt/client/ServiceWorker;)(serviceWorker)
//  }-*/;
  
  
//  protected void setServiceWorker(ServiceWorker w) {
//	  service = w;
//	  java.util.logging.Logger.getLogger("ReplGWT").severe(String.valueOf(w));
//  }
    
  protected static native void install(ReplGWT me) /*-{
  	$wnd.runit = function(test) {
  		me.@nl.numworx.replgwt.client.ReplGWT::runit(Ljava/lang/String;)(test)
  	}
  }-*/;

  int cnt;
protected InputReader w;
protected boolean consuming;
  protected void runit(String message) {
	  GWT.log(message);
	  reset();
	  JSONObject object = new JSONObject();
	  object.put("python", new JSONString(message));
	  object.put("id", new JSONNumber(++cnt));
	  worker.postMessage(object.toString());
  }

  private void reset() {
	  w.removeFromParent();
	  consuming = false;
	  
	  RootPanel output = RootPanel.get("output");
	  output.clear();
	  output.getElement().setInnerHTML(""); // clear it all
  }
  
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	  modules = GWT.getModuleBaseURL();
	  GWT.log("modules = " + modules);
//	  installServiceWorker(this);
	  
	  RootPanel root = RootPanel.get();
	  root.add(this);
	  Stub.publish(this);
  }

@Override
public HashMap<String, Object> getState() {
	HashMap<String,Object> state = new HashMap<>();
	RootPanel content = RootPanel.get("output");
	String inner = content.getElement().getInnerHTML();
	state.put("content", inner);
	return state;
}

@Override
public void setState(HashMap<String, Object> h) {
	String inner = h.getOrDefault("content", "").toString();
	RootPanel content = RootPanel.get("output");
	content.getElement().setInnerHTML(inner);
}

@Override
public int getScore() {
	return 0;
}

@Override
public int[][] getScoreObjectives() {
	return null;
}

@Override
public Boolean isCorrect() {
	return Boolean.TRUE;
}

@Override
public void kijkNa() {
}

@Override
public void zetNagekeken(boolean b) {
}

@Override
public void setCommunicationRoot(OpdrNavIF comRoot) {
	comRoot.addCBookEventListener("text.program", this);
}

@Override
public void zetVolledigeBreedte(int breedte) {
}

@Override
public int getAsHoogte() {
	return 0;
}

@Override
public int getHeight() {
	return height;
}

@Override
public int getWidth() {
	return width;
}

@Override
public void setAsHoogte(int ashoogte) {
}

@Override
public void init(int width, int height, Map<String, Object> launchData, Map<String, Number> values) {
	this.width = width;
	this.height = height;
	worker = Worker.create("/dwo/apps/webworker.js");
	worker.setOnMessage(this);
	worker.setOnError(this);
	w = new InputReader();
	w.setConsumer(this);

}

@Override
public void acceptCBookEvent(CBookEvent event) {
	if ("text.program".equals(event.getCommand())) {
		String program = (String) event.getParameter("content");
		runit(program);
	} else if ("action.reset".contentEquals(event.getCommand())) {
		reset();
	}
}

@Override
public void onMessage(MessageEvent event) {
	GWT.log(event.getDataAsString());
	JSONObject obj = new JSONObject(event.getDataAsJSO());
	if (obj.containsKey("results")) {
		printx(obj.get("results"));
	} else if (obj.containsKey("error")) {
		printx("\n");
		printx(obj.get("error"));
	} else if (obj.containsKey("output")) {
		printx(obj.get("output"));
	} else if (obj.containsKey("request")) {
		id = obj.get("id").isString().stringValue();
		requestInput();
	}
			
}

protected void requestInput() {
	if (consuming) {
		  LOG.severe("consuming");
		  return;
	}
	startInput();
}

private void print(String string) {
	RootPanel output = RootPanel.get("output");
	String inner = output.getElement().getInnerHTML();
	output.getElement().setInnerHTML(inner + (inner.isEmpty()?"":"\n") + string);
}
protected void printx(String string) {
	RootPanel output = RootPanel.get("output");
	String inner = output.getElement().getInnerHTML();
	output.getElement().setInnerHTML(inner + string);
}

protected void printx(JSONValue value) {
	JSONString s = value.isString();
	if (s != null) printx(s.stringValue());
	else printx(value.toString());
}

@Override
public void onError(ErrorEvent event) {
	GWT.log(event.getMessage());
}


protected void startInput() {
	RootPanel.get("output").add(w);
	w.start();
	consuming = true;
}

String id = "";

@Override
public void accept(String t) {
	w.removeFromParent();
	consuming = false;
	t += "\n";
	printx(t);
	RequestBuilder builder = new RequestBuilder(RequestBuilder.PUT, "/dwo/apps/get_input/"+id);
	builder.setRequestData(t);
	builder.setCallback(this);
	try {
		builder.send();
	} catch (RequestException e) {
	}
}

@Override
public void onResponseReceived(Request request, Response response) {
	LOG.warning("on success " + request + " " + response.getStatusCode() + " " + response.getText());
	
}

@Override
public void onError(Request request, Throwable exception) {
	LOG.severe("On Error  " + request + " " + exception);
	
}

}
