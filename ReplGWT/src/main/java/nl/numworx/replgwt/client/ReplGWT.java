package nl.numworx.replgwt.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.webworker.client.ErrorEvent;
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
public class ReplGWT extends SimplePanel implements EntryPoint, InteractionStub, CBookEventListener, MessageHandler, ErrorHandler {
  private int width;
  private int height;
  String modules;
  Worker worker;

    
  protected static native void install(ReplGWT me) /*-{
  	$wnd.runit = function(test) {
  		me.@nl.numworx.replgwt.client.ReplGWT::runit(Ljava/lang/String;)(test)
  	}
  }-*/;

  int cnt;
  protected void runit(String message) {
	  GWT.log(message);
	  JSONObject object = new JSONObject();
	  object.put("python", new JSONString(message));
	  object.put("id", new JSONNumber(++cnt));
	  worker.postMessage(object.toString());
  }
  
/**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	  modules = GWT.getModuleBaseURL();
	  GWT.log("modules = " + modules);
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
	worker = Worker.create(modules + "dist/webworker.js");
	worker.setOnMessage(this);
	worker.setOnError(this);
}

@Override
public void acceptCBookEvent(CBookEvent event) {
	if ("text.program".equals(event.getCommand())) {
		String program = (String) event.getParameter("content");
		runit(program);
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
		String antw = Window.prompt(obj.get("request").toString(), "") + "\n";
		printx(antw);
		worker.postMessage(new JSONString(antw).toString());
	}
			
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

}
