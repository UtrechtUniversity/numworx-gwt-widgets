package nl.numworx.replgwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Text;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;import com.google.gwt.webworker.client.ErrorEvent;
import com.google.gwt.webworker.client.ErrorHandler;
import com.google.gwt.webworker.client.MessageEvent;
import com.google.gwt.webworker.client.MessageHandler;
import com.google.gwt.webworker.client.Worker;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ReplGWT extends SimplePanel implements EntryPoint, InteractionStub, CBookEventListener, MessageHandler, ErrorHandler, Consumer<String>, RequestCallback {
  private static final Logger LOG = java.util.logging.Logger.getLogger("ReplGWT");
  private int width;
  private int height;
  String modules;
  String turtleOutput;
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
	  boolean hasTurtle = message.contains("turtle");
	  JSONObject object = new JSONObject();
	  object.put("python", new JSONString(message));
	  object.put("id", new JSONNumber(++cnt));
	  object.put("hasTurtle", JSONBoolean.getInstance(hasTurtle));
	  worker.postMessage(object.toString());
  }

  private void reset() {
	  w.removeFromParent();
	  recreateWorker();
	  
	  empty(RootPanel.get("output"));
	  empty(RootPanel.get("north")); 
	  
  }

  protected void empty(Panel output) {
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
	if (turtleOutput != null) {
		content = RootPanel.get(turtleOutput);
		inner = content.getElement().getInnerHTML();
		state.put("turtle", inner);
	}
	return state;
}

@Override
public void setState(HashMap<String, Object> h) {
	String inner = h.getOrDefault("content", "").toString();
	RootPanel content = RootPanel.get("output");
	content.getElement().setInnerHTML(inner);
	if (turtleOutput != null) {
		inner = h.getOrDefault("turtle", "").toString();
		content = RootPanel.get(turtleOutput);
		content.getElement().setInnerHTML(inner);
	}
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
	comRoot.addCBookEventListener("action.reset", this);
	w.setComRoot(comRoot);
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
	createWorker();
	w = new InputReader();
	w.setConsumer(this);
	
	ObjectMap map = JSONUtilities.wrapMap(launchData);
	String side = map.getString("side");
	RootPanel north = RootPanel.get("north");
	if ("north".equals(side)) {
		turtleOutput = side;
		String size = map.getString("size");
		if (size == null) size = "100%"; // default is full height;
		north.getElement().getStyle().setProperty("height", size); // as a string, 100%, 40px etc. 
	}

	RootPanel south = RootPanel.get("south");
	if ("south".equals(side)) {
		turtleOutput = side;
		String size = map.getString("size");
		if (size == null) size = "100%"; // default is full height;
		south.getElement().getStyle().setProperty("height", size); // as a string, 100%, 40px etc. 
	}

	if (side != null) {
		RootPanel.get("content").setStylePrimaryName(side);
	}

	
}

private void createWorker() {
	worker = Worker.create("/dwo/apps/webworker.js");
	worker.setOnMessage(this);
	worker.setOnError(this);
}

private void recreateWorker() {
	if (consuming) {
		worker.terminate();
		createWorker();
		RequestBuilder builder = new RequestBuilder(RequestBuilder.DELETE, "/dwo/apps/get_input/"+id);
		builder.setRequestData("");
		builder.setCallback(this);
		Request r = null;
		try {
			r = builder.send();
		} catch (RequestException e) {
			onError(r, e);
		}
		consuming = false;
	}
}



@Override
public void acceptCBookEvent(CBookEvent event) {
	if ("text.program".equals(event.getCommand())) {
		String program = (String) event.getParameter("content");
		//$Z35@ -> #
		// $Z nnn @ -> codepoint(nnn)
		program = Util.decodeZ(program);
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
		JSONValue value = obj.get("error");
		JSONString s = value.isString();
		if (s != null) {
			String trace = s.stringValue();
			int file = trace.indexOf("File \"<exec>\"");
			if (file >= 0) {
				trace = trace.substring(file).replace("File \"<exec>\",", "At");
				value = new JSONString(trace);
			}
		}
		printx(value);
		scrollToBottom();
	} else if (obj.containsKey("output")) {
		printx(obj.get("output"));
	} else if (obj.containsKey("request")) {
		id = obj.get("id").isString().stringValue();
		requestInput();
	} else if (obj.containsKey("display_type") && turtleOutput != null) {
		// assume turtle
		obj = obj.get("content").isObject();
		Element element = (Element) makeElement(obj);
// north of south
		String string = element.getString();
		LOG.info("SVG " + string);		
		Document.get().getElementById(turtleOutput).setInnerHTML(string);
	}
			
}

private Node makeElement(JSONObject obj) {
	JSONValue jsonValue = obj.get("tag");
	if (jsonValue == null) {
		// if not a tag, a text element?
		jsonValue = obj.get("text");
		String text = "";
		if (jsonValue != null) {
		 text = jsonValue.isString().stringValue();
		}
		Text elem = Document.get().createTextNode(text);
		return elem;
	}
	String tag = jsonValue.isString().stringValue();
	Element elem = Document.get().createElement(tag);
	JSONObject props = obj.get("props").isObject();
	Set<String> keys = props.keySet();
	for (String prop: keys) {
		JSONValue value = props.get(prop);
		if (value.isString() != null)
			elem.setAttribute(prop, value.isString().stringValue());
		else if (value.isNumber() != null)
			elem.setAttribute(prop, value.toString());
			
	}
	JSONArray children = obj.get("children").isArray();
	int count = children.size();
	for(int i = 0; i < count; i++) {
		elem.appendChild(makeElement(children.get(i).isObject()));
	}
	return elem;
}

private void scrollToBottom() {
	RootPanel output = RootPanel.get("output");
	int h = output.getElement().getScrollHeight();
	Element div = output.getElement().getParentElement();
	int s = div.getClientHeight();
	output.getElement().setScrollTop(Math.max(0, h-s));	
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
	string = string.replace("&", "&amp;");
	string = string.replace("<", "&lt;");
	string = string.replace(">", "&gt;");
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
	LOG.warning(event.getMessage());
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
