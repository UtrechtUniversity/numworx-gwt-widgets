package nl.numworx.aimodelgwt.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AIModelGWT extends SimplePanel implements EntryPoint, InteractionStub, CBookEventListener {
  private static final String TEXT = "text.aimodel";
private int width;
  private int height;
private OpdrNavIF comRoot;
  
  static final String TUPELS = "tupels.aimodel", ACTION = "action.visible";
  
  static final Tupel[] DUMMY = {
		  new Tupel("ha", "rho≠0"),
		  new Tupel("testvalue", "r=0.835"),
		  new Tupel("n", "9"),
		  new Tupel("test","rpearson"),
		  new Tupel("alpha","0.01")
  };
  

/**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	  RootPanel root = RootPanel.get();
	  root.add(this);
	  Stub.publish(this);
  }

@Override
public HashMap<String, Object> getState() {
	HashMap<String,Object> state = new HashMap<>();
	return state;
}

@Override
public void setState(HashMap<String, Object> h) {
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
	this.comRoot = comRoot;
	comRoot.addCBookEventListener("text.program", this);
}

@Override
public void zetVolledigeBreedte(int breedte) {
}

@Override
public Widget asWidget() {
	return this;
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
}


@Override
public void acceptCBookEvent(CBookEvent event) {
	if (TEXT.equals(event.getCommand())) {
		String model = (String) event.getParameter("content");
		GWT.log(model);
		fire(DUMMY);
		
	}
}

private void fire(Tupel[] tupels) {
	List<Map<String,String>> list = new ArrayList<>();
	for(Tupel t: tupels) { 
		list.add(t.toJSON());
	}
	CBookEvent ev = new CBookEvent(this, TUPELS, Collections.singletonMap(TUPELS, list));
	comRoot.fireEvent(ev);
	ev = new CBookEvent(this, ACTION);
	comRoot.fireEvent(ev);
}

}
