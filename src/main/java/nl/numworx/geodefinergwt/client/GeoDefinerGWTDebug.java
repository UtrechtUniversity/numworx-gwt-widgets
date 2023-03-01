package nl.numworx.geodefinergwt.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import nl.numworx.geodefiner.common.LineType;
import nl.numworx.geodefiner.common.PointType;
import nl.numworx.geodefiner.common.Snapper;
import nl.numworx.geodefiner.common.Tips;
import nl.numworx.geodefiner.common.Tools;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

import fi.wiskopdr.FormuleParser;

public class GeoDefinerGWTDebug extends GeoDefinerGWT implements EntryPoint, RequiresResize {

	private class MockOpdrNav implements OpdrNavIF, FormuleKeyboardIF, FormuleClipboardIF {

		private FormuleEditorIF editor;

		@Override
		public void setChanged(boolean fout) {

		}

		public ObjectMap getContext() {
		  return JSONUtilities.wrapMap(Collections.singletonMap("premium", Boolean.TRUE));
		}
		
		@Override
		public FormuleKeyboardIF getKeyboard() {
			return this;
		}

		@Override
		public FormuleClipboardIF getFormuleClipboard() {
			return this;
		}

		@Override
		public int getMode() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getLearnerId() {
			return "0";
		}

		@Override
		public String getLearnerName() {
			return "guest";
		}

		@Override
		public CssColor getBackground() {
			return CssColor.make("white");
		}

		@Override
		public String getUUID() {
			// TODO Auto-generated method stub
			return "00-00-00";
		}

		@Override
		public LessonMode getLessonMode() {
			return LessonMode.normal;
		}

		@Override
		public Role getRole() {
			return Role.Learner;
		}

		@Override
		public HandlerRegistration addCBookEventListener(String command, CBookEventListener listener) {
			return null;
		}

		@Override
		public void fireEvent(CBookEvent event) {

		}

		@Override
		public boolean hasListeners(String command) {
			return false;
		}

		@Override
		public void pause() {

		}

		@Override
		public void unpause() {

		}

		@Override
		public ObjectMap getConfiguration() {
			return JSONUtilities.wrapMap(Collections.emptyMap());
		}

		@Override
		public void setEditor(FormuleEditorIF formuleEditor) {
			this.editor = formuleEditor;
		}

		@Override
		public void backspace() {
			if(editor != null)
				editor.removeCurrentElement();
		}

		@Override
		public void delete() {
			if(editor != null)
				editor.removeNextElement();
			
		}

		@Override
		public void enter() {
			if(editor != null)
				editor.enter();
		}

		@Override
		public void focus() {
			FocusOnTouch.focus();
		}

		@Override
		public FormuleEditorIF getEditor() {
			return editor;
		}

		@Override
		public void softFocus() {
			FocusOnTouch.focus();
		}

		@Override
		public void blur() {
			editor = null;
		}

		@Override
		public void functionKey(int minF) {
			
		}

		@Override
		public void setEnterType(EnterType type) {
			
		}

		@Override
		public String getClipboard() {
			return "";
		}

		@Override
		public void setClipboard(String formule) {
			
		}

	}

	private FocusPanel main;

	@Override
	public void onModuleLoad() {
      //Element body = Document.get().getBody();
      //body.setAttribute("oncontextmenu", "return false;");

      root = uiBinder.createAndBindUi(this);
      //toolbox.setResizer(this);
      main = FocusOnTouch.wrap(root, true);
      RootLayoutPanel r = RootLayoutPanel.get();
      r.add(main);
      r.setWidgetTopHeight(main, 0, Unit.PC, getHeight(), Unit.PC);
      r.setWidgetLeftWidth(main, 0, Unit.PC, getWidth(), Unit.PC);
      
      
		//FormuleParser.zetWoordFormule(true);
		
		Map<String, Object> launchDebug = new HashMap<String, Object>();
		List<Integer> toolbox = Arrays.asList(
				Tools.SELECTOR,
//				Tools.COLOR_PALETTE,
				Tools.POINT,
				Tools.LINE,
//				Tools.HALFLINE,
//				Tools.SEGMENT,
//				Tools.LINE_PALETTE,
//
//				Tools.PERPENDICULAR,
//				Tools.PARALLEL,

				Tools.CIRCLE,
				Tools.ARC,
//				Tools.TRIANGLE,
//				Tools.CIRCLE_WITH_RADIUS,
//				Tools.ANGLE_POINT,
//
//				Tools.MIDPOINT,
//				Tools.BISECTRICE,
//				Tools.MIRROR,
//
//				Tools.CONIC_SECTION,
//				Tools.FOCUS,
//				Tools.TANGENT,
//				Tools.POLELINE,
//				
//				Tools.LOCUS,
//
//				Tools.DISTANCE,
//				Tools.AREA,
//				Tools.ANGLE,
//				Tools.VECTOR,
//
//				Tools.TRAIL,
//				Tools.TEXT,			
//				Tools.FORMULA,
//
//				Tools.PAN,
				Tools.DESTROY,
				Tools.RESET,
//				Tools.ANGLE_POINT
				Tools.GEO_TRIANGLE
			);
		Vector<Map<String,Object>> configs = new Vector<>();
		configs.setSize(32);
		HashMap<String, Object> element = new HashMap<>(Collections.singletonMap("color", 0X80FF0000));
		element.put("size", 17);
		configs.set(Tools.POINT, element);
		configs.set(Tools.CIRCLE, Collections.singletonMap("color", 0XFFFFFF00));
		configs.set(Tools.CIRCLE_WITH_RADIUS, new HashMap<>(Collections.singletonMap("color", 0XFFFF8800)));
		configs.set(Tools.TEXT, new HashMap<>(Collections.singletonMap("color", 0xFFFF0000)));
		configs.set(Tools.ANGLE_POINT, new HashMap<>(Collections.singletonMap("color", 0xFFFFFF00)));
		configs.set(Tools.LINE_PALETTE, new HashMap<>(Collections.singletonMap(LineType.DOTTED.name(), false)));
		//configs.set(Tools.ANGLE,  Collections.singletonMap("rad", false));
		HashMap<String,Object> mm = new HashMap<>();
		mm.put("color", 0xFF00AF00);
		mm.put("width", 4.5);
		//mm.put("tip", Tips.ATSTART.name());
		configs.set(Tools.SEGMENT, mm );

		mm = new HashMap<String,Object>(configs.get(Tools.CIRCLE));
		mm.put("fill", 0x80808080);
		configs.set(Tools.CIRCLE, mm);
		
		
		mm = new HashMap<>(configs.get(Tools.TEXT));
		mm.put("dx", 6);  // 		textModel.setDXY(6f,-5f);
		mm.put("dy", -5);
		configs.set(Tools.TEXT, mm);
		
		launchDebug.put("toolbox", toolbox);
		launchDebug.put("toolboxConfig", configs);
		Map<String,Object> checkDWO = new HashMap<String,Object>();
		checkDWO.put("formule", "$ftrue@");
		checkDWO.put("score", 10);
		checkDWO.put("check", Boolean.TRUE);
		checkDWO.put("extern", Boolean.FALSE);
		List<String> definitions = Arrays.asList(
				//"$fa=9..#a#@" 
				"$ff(x)=$o1.8$m$ax$n0.84@@@$n0.22@@"
				//,"$ft=text(\"$P4x$nx@@$px$n8@@$b1$n2@@M$sx@$o{a}$nbc@@$w{a}+2$b1$n{a}/2@@$m2@@\",O)@"
				//,"$ft=text(\"M$s8@ afstand e tan $zM@$sx@\",O)@"
				,"$fP=point(1,1)@"
				,"$fQ=point(-1,1)@"
				,"$fh=halfline(Q,P)@"
				,"$fZ=point(2,2)@"
				//,"$fv =map(t -> text(\"{t}\u03c0\", point(t,2)), 1..3)@"
				,"$fwaarde=true@"
				//, "$fy<-1-x*x@"
				,"$ftt=linetype(h,2)@"
				);
		launchDebug.put("definitions", definitions);
		Map<String,Object> h = new HashMap<>();
	// Collections.singletonMap("color", 0);
		h.put("color", 0XFFFF0000);
		h.put("width", 3);
		h.put("tip", "ATSTART");
		h.put("rigid", Boolean.FALSE);
		Map <String,Map<String,Object>> configuration = new HashMap<>();
		configuration.put("h", h);
		h = new HashMap<>();
		h.put("font", Collections.singletonMap("size", 24));
	       h.put("color", 0XFFFF0000);
	       h.put("visible", Boolean.TRUE);
	       configuration.put("waarde", h);
	    h  = new HashMap<>();
	    h.put("color", 0X6F808080);
	    configuration.put("$fy<-1@", h);
	    h = new HashMap<>();
	    h.put("rigid", false);
	    h.put("color", 0x80FF0000);
	    h.put("type", PointType.DISK.name());
	    h.put("size", 15);
	    h.put("log", false);
	    configuration.put("P", h);configuration.put("Q", h);
	    h = new HashMap<>();
	    h.put("rigid", true);
	    h.put("color", 0x80FF0000);
	    h.put("type", PointType.DISK.name());
	    h.put("size", 15);
	    h.put("log", false);
	    configuration.put("Z", h);
	    h = new HashMap<>();
	    h.put("alwaysF", true);
	    h.put("color", -16777216);
	    configuration.put("v", h);
	    
	    launchDebug.put("configuration", configuration);
		h = new HashMap<>();
		h.put("gravity", true); 
		h.put("color", 0xFF000000);
		h.put("type", LineType.SOLID.name());
		h.put("width", 0.5);
		launchDebug.put("axes", Collections.singletonMap("$#@", h));
		launchDebug.put("checkDWO", checkDWO);
	//"checkObjects":[{"score":5,"value":"$fpoint(2,1)@"}
		Map<String,Object> checkObject = new HashMap<>();
		checkObject.put("score", 5);
		checkObject.put("value", "$fpoint(2,1)@");		
		launchDebug.put("checkObjects", Collections.singletonList(checkObject));

		Map<String,Object> positions = new HashMap<>();
		positions.put("waarde", Arrays.asList(1, 50, 1, 50));
		launchDebug.put("positions", positions);
		
		launchDebug.put("logOption", Boolean.TRUE);
		
		Map<String, Number> values = new HashMap<String, Number>();
		values.put("a", Integer.valueOf(10));
		init(getWidth(), getHeight(), launchDebug, values);
		MockOpdrNav opdrnav = new MockOpdrNav();
		FocusOnTouch.installKeyboard(opdrnav, opdrnav);
		FocusOnTouch.focus();
		setCommunicationRoot(opdrnav);
		viewer.adapt(Snapper.class).setGravity(true);
		
		setState(new HashMap<>());
		start();
	}

	int oldtop, oldleft, cnt;
	@Override
	public void onResize() {
		int rh = Window.getClientHeight();
		int rw = Window.getClientWidth();
		tryResize(rw, rh);
		int w = root.getOffsetWidth();
		int h = root.getOffsetHeight();
		Logger.getGlobal().warning(cnt++ + " On Resize " + w + "<" + rw + ", " + h + "<" + rh);
		RootLayoutPanel p = RootLayoutPanel.get();		
		int left = (rw-w)/2;
		if (left != oldleft) {
			oldleft = left;
			p.setWidgetLeftWidth(main, left, Unit.PX, w, Unit.PX);
		}
		int top = (rh-h)/2;
		if (top != oldtop) {
			oldtop = top;
			p.setWidgetTopHeight(main, top, Unit.PX, h, Unit.PX);
		}
		//p.forceLayout();
	}

	private void tryResize(int rw, int rh) {
		if (getWidth() * rh > getHeight() * rw) {
			rh = rw * getHeight() / getWidth();
		} else {
			rw = rh * getWidth() / getHeight();
		}
		if (root.getOffsetHeight() != rh || root.getOffsetWidth() != rw)
		{
			Logger.getGlobal().warning(cnt++ + " Scale " +  rw + ", " + rh);	
			root.setPixelSize(rw, rh);	
			rh -= getConstantHeight();
// rw/rh nieuwe maten widget
			relocate(rw, rh);
			widget.paint();
		}
		
	}

	
}
