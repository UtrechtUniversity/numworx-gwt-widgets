package nl.numworx.sqlitegwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.webworker.client.ErrorEvent;
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
public class SQLiteGWT extends SimplePanel implements EntryPoint, InteractionStub, CBookEventListener, MessageHandler, ErrorHandler, Consumer<String>, RequestCallback {
  private static final Logger LOG = java.util.logging.Logger.getLogger("SQLiteGWT");
  private int width;
  private int height;
  String modules, url = "https://www.fi.uu.nl/dwo/resources/sqlite_danilo.db";
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
    
  protected static native void install(SQLiteGWT me) /*-{
  	$wnd.runit = function(test) {
  		me.@nl.numworx.sqlitegwt.client.SQLiteGWT::runit(Ljava/lang/String;)(test)
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
	  object.put("url", new JSONString(url));
	  worker.postMessage(object.toString());
  }

  private void reset() {
	  w.removeFromParent();
	  recreateWorker();
	  
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
      createWorker();

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
	comRoot.addCBookEventListener("action.reset", this);
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
	ObjectMap map = JSONUtilities.wrapMap(launchData);
	if (map.containsKey("url"))
		this.url = map.getString("url");
	createWorker();
	w = new InputReader();
	w.setConsumer(this);
	  

}

private void createWorker() {
	worker = Worker.create("sqlworker.js");
	worker.setOnMessage(this);
	worker.setOnError(this);

}

private void displayError(JSONValue value) {
	printx(value);
	scrollToBottom();
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

private void displayTables(JSONValue value) {
	JSONArray array = value.isArray();
	if (array != null) {
		int size = array.size();
		for (int i = 0; i < size; i++) {
			displayTable(array.get(i));
		}
	}
}

private void displayTable(JSONValue value) {
    JSONObject table = value.isObject();
    if (table == null) {
        printx("Invalid SQL result.");
        return;
    }
    
    JSONArray columns = table.get("columns").isArray();
    JSONArray values = table.get("values").isArray();
    
    if (columns == null || values == null) {
        printx("Invalid SQL result structure.");
        return;
    }
    
    StringBuilder html = new StringBuilder();
    html.append("<table border='1'><tr>");
    
    // Add column headers
    for (int i = 0; i < columns.size(); i++) {
        JSONValue json = columns.get(i);
        String string = "";
        if (json.isString() != null) string = json.isString().stringValue();
        else string = json.toString();
		html.append("<th>").append(string).append("</th>");
    }
    html.append("</tr>");
    
    // Add rows
    for (int i = 0; i < values.size(); i++) {
        JSONArray row = values.get(i).isArray();
        html.append("<tr>");
        for (int j = 0; j < row.size(); j++) {
            JSONValue json = row.get(j);
            String string = "";
            if (json.isString() != null) string = json.isString().stringValue();
            else string = json.toString();
    		html.append("<td>").append(string).append("</td>");
       }
        html.append("</tr>");
    }
    html.append("</table>");
    
    print(html.toString());
}

@Override
public void onMessage(MessageEvent event) {
	GWT.log(event.getDataAsString());
	JSONObject obj = new JSONObject(event.getDataAsJSO());
	if (obj.containsKey("result")) {
		displayTables(obj.get("result"));
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
	}
			
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
