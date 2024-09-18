package nl.numworx.leerdoelwidgetgwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.web.bindery.event.shared.HandlerRegistration;

import fi.dwo.gwt.lib.rest.ui.IdleDetect;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.json.JSONObjectMapImpl;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;

public class StubWidget extends Composite implements Handler, LoadHandler, FormuleEditorIF, RequiresResize {

	private static FormuleFont defaultFont;
	private static final Logger LOG = Logger.getLogger(StubWidget.class.getName());

	private native static void setState(Object inner, String state) /*-{
		inner.setState(state);
	}-*/;

	private native static String getState(Object inner) /*-{
		return inner.getState();
	}-*/;

	private native static void zetNagekeken(Object inner, boolean b) /*-{
		inner.setNagekeken(b);
	}-*/;

	private static native void init(Object inner, int width, int height, String launchdata,
			JavaScriptObject randomVars) /*-{ 
		inner.init(width, height, launchdata, randomVars);
	}-*/;
	
	private static native int getHeight(Object inner) /*-{
		return inner.getHeight();
	}-*/;
	
	private Frame frame;
	private HandlerRegistration detachhandler, loadhandler;
	private int width;
	private int height; 
	private Object innerView;
	private ObjectMap innerMap;
	Boolean nagekekenPending;
	private String pendingState;
	private HashMap<String, Number> randomVars = new HashMap<>();
	private HashMap<String, Object> lastResort;
	private IdleDetect idler;
	private OpdrNavIF comRoot;
	final LeerdoelWidgetGWT parent;

	public StubWidget(LeerdoelWidgetGWT parent, int id, FormuleKeyboardIF kb, IdleDetect idler, OpdrNavIF comRoot) {
		this.parent = parent;
		this.kb = kb;
		this.idler = idler;
		this.comRoot = comRoot;
		String profile = "77";
		String p = Window.Location.getParameter("profile");
		if (p != null) profile = p;
		
		String locale;
		locale = LocaleInfo.getCurrentLocale().getLocaleName();

		frame = new Frame("WidgetPlayer.jsp?responsive=true&id=" + id + "&profile=" + profile + "&locale=" + locale);
		frame.addStyleDependentName("widget");
	}

	private void initFrame() {
		frame.setPixelSize(width , height);
		detachhandler = frame.addAttachHandler(this);
		initWidget(frame);
	}

	public void init(int width, int height, ObjectMap launch) {
		this.width = width;
		this.height = height;
		this.innerMap = launch;
		initFrame();
	}

	@Override
	public void onAttachOrDetach(AttachEvent event) {
		boolean detach = !event.isAttached();
		if( detach )
		{
			if (innerView != null) {
				getState0(); // last chance to fill lastResort en correct/score
			}
			innerView = null;
		}
		else 
			loadhandler = frame.addLoadHandler(this);
		
	}

	@Override
	public void onLoad(LoadEvent event) {
		loadhandler.removeHandler();
		Object w = getContentWindow(frame.getElement());
		if (w != null)
		{
			innerView = getApplet(w, this);
			if(innerView != null)
				publish(innerView);
		}
	}
	private native static Object getContentWindow(Element frame) /*-{
	return frame.contentWindow;
}-*/;

public static native Object getApplet(Object wnd, StubWidget view) /*-{
	wnd.outer = view;
	wnd.publish = function(o, viewer) {
		return viewer.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::publish(Ljava/lang/Object;)(o)
	}
	wnd.setChanged = function(b, viewer) {
//		viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::comRoot.@nl.uu.fi.dwo.interaction.client.OpdrNavIF::setChanged(Z)(b);
	}
	wnd.setFocus = function(b, viewer) {
//		viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::setFocus(Z)(b)
		viewer.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::setFocus(Z)(b)
	}
	wnd.setFocus2 = function(b, soft, viewer) {
//		viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::setFocus(ZZ)(b,soft)
		viewer.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::setFocus(ZZ)(b, soft)
	}
	wnd.getMode = function(viewer) {
		return viewer.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::getMode()()
	}
	wnd.getLearnerName = function (viewer) {
		return viewer.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::getLearnerName()()
	}
	wnd.getLearnerId = function (viewer) {
		return viewer.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::getLearnerId()()
	}
	wnd.getUUID = function (viewer) {
		return viewer.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::getUUID()()
	}
	wnd.getBackground = function (viewer) {
		return viewer.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::getBackgroundAsString()()
	}
	
	wnd.fireEvent = function (event, viewer) {
		if ( typeof event === 'string' )
			event = JSON.parse(event)
		return viewer.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::fireJSEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(event)
	}
	
	wnd.addCBookEventListener = function (command, listener, viewer) {
//		return viewer.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::addCBookEventListener(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(command, listener)
	}
	wnd.removeCBookEventListener = function (registration) {
//		return @nl.numworx.leerdoelwidgetgwt.client.StubWidget::removeCBookListener(Lcom/google/web/bindery/event/shared/HandlerRegistration;)(registration)
	}
	wnd.setEnterType = function(type, viewer) {
		return viewer.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::setEnterType(Ljava/lang/String;)(type)
	}
	wnd.getConfiguration = function(viewer) {
		return viewer.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::getConfiguration0()()
	}
	wnd.getContext = function(viewer) {
		return viewer.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::getContext0()()
	}
	wnd.tickle = function() {
		view.@nl.numworx.leerdoelwidgetgwt.client.StubWidget::tickle()()
	}
	wnd.setVisited = function(viewer) {
	}

	return wnd.inner;
}-*/;

private void publish(Object inner) {
	
	innerView = inner;
	try {
		HashMap<String, Object> inits = new HashMap<String,Object>();			
		inits.putAll(randomVars);
		init(inner, width, height, JSONUtilities.toJSONObject(innerMap).toString(), 
				JSONUtilities.toJSONObject(inits).isObject().getJavaScriptObject()); // FIXME ook toString?

		if(pendingState != null) {
			setState(inner, pendingState);
			pendingState = null;
		} 
		if (nagekekenPending != null) {
			zetNagekeken(nagekekenPending.booleanValue());
		}
		int height = getHeight(inner);
		if (height > 0 && height != this.height) {
			Logger.getLogger("StubView").info("Change height to " + height);
			frame.setPixelSize(-1, height);
			this.height = height;
		}
	} catch(Exception e) {
		Logger.getLogger("StubView").log(Level.SEVERE,"init "+ e);
	}
	
			
}
private HashMap<String, Object> getState0() {
	if(innerView != null)
	{
		String jso = getState(innerView); // FIXME innerview := null als frame hides or disappears
		if(jso != null)
		{
			JSONObject js = JSONParser.parseLenient(jso).isObject();
			//return JSONUtilities.fromJSONObject(js);
			return lastResort = wrap(JSONUtilities.wrapMap(js));
		}
	}
	if(pendingState != null)
	{
		JSONObject js = JSONParser.parseLenient(pendingState).isObject();
		return lastResort = wrap(JSONUtilities.wrapMap(js));
	}
	
	if (lastResort != null) 
		return lastResort;
	
	HashMap<String,Object> map = new HashMap<String,Object>();
	return wrap(map);
}

public HashMap<String, Object> getState() {
//	if (facade.hasState()) 
//		return facade.getState();
	return getState0();
}


private void setFocus(boolean f) {
	setFocus(f, true);
}

final private FormuleKeyboardIF kb;

private void setFocus(boolean b, boolean soft) {
	kb.setEditor( b ? this : null);
	
	// extra parameter 'soft' of hard focus
	if (b) {
		if(soft) kb.softFocus();
		else kb.focus();
	} else 
		kb.blur();

}

public void zetNagekeken(boolean b) {
	if(innerView != null)
	{	nagekekenPending = null;
		zetNagekeken(innerView, b);
	}
	else
		nagekekenPending = Boolean.valueOf(b);
}
private HashMap<String, Object> wrap(HashMap<String, Object> map) {
	if(map == null) map = new HashMap<String, Object>();
	return map;
}

public void setState(HashMap<String, Object> h) {
	boolean isNull = h == null;
	if(isNull)
		h = new HashMap<String, Object>(); // Never NULL, komt voor!
	lastResort = h;
	JSONValue object = JSONUtilities.toJSONObject(h);
	if(innerView != null)
	{
		setState(innerView, object.toString());
		pendingState = null;
		pendingState = object.toString(); // reset komt mogelijk na
	}
	else 
		pendingState = object.toString(); // XXX NPE!
}

	public int getMode() {
		return OpdrNavIF.OEFENEN;
	}
	
	public String getLearnerName() {
		return "leerling";
	}
	
	public String getLearnerId() {
		return "leerling";
	}

	String uuid = GUID.get();
	
	public String getUUID() {
		return uuid;
	}
	
	public String getBackgroundAsString() {
		return "transparent";
	}

	private void fireJSEvent(JavaScriptObject jso) {
		JSONObject value = new JSONObject(jso);
		CBookEvent evt = new CBookEvent(JSONUtilities.wrapMap(value));
		fireEvent(evt);
		Timer t = new Timer()
		{
			@Override
			public void run()
			{
			}
		};
		t.schedule(1);

	}

	private void fireEvent(CBookEvent evt) {
		if (evt.getCommand().equals("gotoPlace"))
		{
			String message = evt.getMessage();
			LOG.severe("fireEvent message is " + message);
			if (message.startsWith("s:")) {
				evt = new CBookEvent(parent, "gotoPlace", "x" + message);
			}
			
			comRoot.fireEvent(evt);
		}
		else if (evt.getCommand().equals("resize")) {
			LOG.severe("resize " + evt);
		}
	}

	private void setEnterType(String type) {
		try {
			EnterType e = EnterType.valueOf(type);
			//comRoot.getKeyboard().setEnterType(e);
		} catch (Exception e) {
			GWT.log("setEnterType " + type, e);
		}
	}

	public ObjectMap getConfiguration() {
//		if(comRoot!=this && comRoot!=null)
//			return comRoot.getConfiguration();
		return null;
	}

	public JavaScriptObject getConfiguration0() {
		ObjectMap map = getConfiguration();
		if(map instanceof JSONObjectMapImpl) {
			return ((JSONObjectMapImpl) map).unwrap().getJavaScriptObject();
		} else if (map instanceof Map) {
			return JSONUtilities.toJSONObject(map).isObject().getJavaScriptObject();
		}
		else {
			return null;
		}
	}

	public void tickle() {
		GWT.log("tickle");
		idler.reset();
	}
	
	
	public ObjectMap getContext() {
//		if(comRoot != this && comRoot != null) {
//			return comRoot.getContext();
//		}
		JSONObject object = new JSONObject();
		object.put("premium", JSONBoolean.getInstance(isPremium())); //werkt altijd!
		return JSONUtilities.wrapMap(object);
	}

	private boolean isPremium() {
		// TODO Auto-generated method stub
		return false;
	}

	public JavaScriptObject getContext0() {
		ObjectMap map = getContext(); // Never null!
		return JSONUtilities.toJSONObject(map).isObject().getJavaScriptObject();		
	}

	@Override
	public void clearAll() {
		if(innerView != null)
			clearAll(innerView);
	}

	private static native void clearAll(Object inner)/*-{
		inner.clearAll();
	}-*/;

	@Override
	public void insert(String text) {
		if(innerView != null)
			insert(text, innerView);		
	}
	
	private static native void insert(String text, Object inner) /*-{
		inner.insert(text);
	}-*/;
	
	public static void createDefaultFont(int size) {
		defaultFont = FormuleFont.createFromFontSize(size);
	}
	
	
	@Override
	public FormuleFont getDefaultFont() {
		return defaultFont;
	}

	@Override
	public void setFont(FormuleFont font) {
	}

	@Override
	public void setCurrentElementRepaint() {
	}

	@Override
	public void enter() {
		if(innerView != null)
			enter(innerView);
	}
	private static native void enter(Object inner) /*-{
		inner.enter();
	}-*/;
	
	
	@Override
	public void removeCurrentElement() {
		backspace(innerView);
	}
	private static native void backspace(Object inner) /*-{
		inner.backspace();
	}-*/;
	
	@Override
	public void removeNextElement() {
		removeNextElement(innerView);
	}
	
	private static native void removeNextElement(Object inner) /*-{
		inner.removeNextElement();
	}-*/;

	@Override
	public void cursorToLeft() {
		cursorToLeft(innerView);
	}
	private static native void cursorToLeft(Object inner) /*-{
		inner.cursorToLeft();
	}-*/;

	@Override
	public void cursorToRight() {
		cursorToRight(innerView);
	}
	private static native void cursorToRight(Object inner) /*-{
		inner.cursorToRight();
	}-*/;
	
	@Override
	public void cursorToLeftShift() {
		cursorToRight(innerView);
	}
	private static native void cursorToLeftShift(Object inner) /*-{
		inner.cursorToLeftShift();
	}-*/;
	
	@Override
	public void cursorToRightShift() {
		cursorToRight(innerView);
	}
	private static native void cursorToRightShift(Object inner) /*-{
		inner.cursorToRightShift();
	}-*/;

	@Override
	public void cursorUp() {
		cursorUp(innerView);
	}
	private static native void cursorUp(Object inner) /*-{
		inner.cursorUp();
	}-*/;
	
	@Override
	public void cursorDown() {
		cursorDown(innerView);
	}
	private static native void cursorDown(Object inner) /*-{
		inner.cursorDown();
	}-*/;
	
	@Override
	public void insert(char charAt) {
		insert(String.valueOf(charAt));
	}

	@Override
	public String getSelectionString() {
		return kopieer(innerView);
	}
	
	
	@Override
	public void kopieer(FormuleClipboardIF clip) {
		String s = kopieer(innerView);
		if (s != null) clip.setClipboard(s);
		
	}
	private static native String kopieer(Object inner) /*-{
		return inner.kopieer();
	}-*/;
	
	@Override
	public void knip(FormuleClipboardIF clip) {
		String s = knip(innerView);
		if (s != null) clip.setClipboard(s);
	}

	private static native String knip(Object inner) /*-{
		return inner.knip();
	}-*/;
	
	@Override
	public void plak(FormuleClipboardIF clip) {
		insert(clip.getClipboard());
	}
//	private static native void plak(Object inner) /*-{
//		inner.plak();
//	}-*/;
	
	@Override
	public void macht() {
		insert("$m@"); 
	}

	@Override
	public void wortel() {
		insert("$w@");
	}

	@Override
	public void breuk() {
		insert("$b$n@@");
	}

	@Override
	public void kwadraat() {
		insert("$m2@");
	}

	@Override
	public void ndewortel() {
		insert("$W$n@@");
	}

	@Override
	public void haakjes() {
		insert("$h@");
	}

	@Override
	public void integraal() {
		insert("$i$n$k$l@@@@");
	}

	@Override
	public void prv() {
		insert("$q$n$k$l@@@@");
	}

	@Override
	public void ndelog() {
		insert("$L$n@@");
	}

	@Override
	public void abs() {
		insert("$r@");
	}

	@Override
	public void subscript() {
		insert("$s@");
	}

	@Override
	public void bin() {
		insert("$y$n@@");
	}

	@Override
	public void diff() {
		insert("$d$n@@");
	}

	@Override
	public void diff_partial() {
		insert("$D$n@@");
	}
	
	@Override
	public void limiet0() {
		insert("$T$n$k$l@@@@");
	}

	@Override
	public void limiet1() {
		insert("$T$n$k$l@@@@");
	}

	@Override
	public void limiet2() {
		insert("$T$n$k$l@@@@");
	}

	@Override
	public void primitieve() {
		insert("$P$n@@");
	}

	@Override
	public void conjug() {
		insert("$c@");
	}

	@Override
	public void sigma() {
		insert("$S$n$k$l@@@@");
	}
	
    @Override
    public void stelsel() {
        insert("$Q@");
    }
    
    @Override
    public void stelsel(int aantalRijen)
    {
        insert("$Q@");
    }
    
	@Override
	public void vectornotatie()
	{
		insert("$z@");
	}

	@Override
	public void vector()
	{
		insert("$Y@");
	}

	@Override
	public void vector(int aantalRijen)
	{
		insert("$Y@");
	}

	@Override
	public void matrix()
	{
		insert("$M@");
	}

	@Override
	public void matrix(int aantalRijen, int aantalKolommen)
	{
		insert("$M@");
	}
	@Override
	public void tab() {
		try {
			tab(innerView);
		} catch(Exception not_implemented) {	
		}
	}

	private static native void tab(Object innerView)/*-{ innerView.tab() }-*/;
	private static native void shiftTab(Object innerView)/*-{ innerView.shiftTab() }-*/;

	@Override
	public void shiftTab() {
		try {
			shiftTab(innerView);
		} catch(Exception not_implemented) {	
		}
	}

	private static native void selectAll(Object innerView) /*-{ innerView.selectAll() }-*/;
	
	@Override
	public void selectAll() {
		try {
			selectAll(innerView);
		} catch(Exception not_implemented) {
		}
		
	}

	@Override
	public void insertcp(int codepoint) {
		insert("$Z" + codepoint + "@");
		
	}

	@Override
	public void onResize() {
		int w = getParent().getOffsetWidth();
		int h = getParent().getOffsetHeight();
		GWT.log("offsetwidth = " + w + ", " + h);
		if (h == 0) h = -1;
		frame.setPixelSize(w, h);	// frame volgt parent size, maar geen hoogte 0
	}

	public int getHeight() {
		return height;
	}


}
