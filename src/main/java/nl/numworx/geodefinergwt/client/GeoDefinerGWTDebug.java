package nl.numworx.geodefinergwt.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nl.numworx.geodefiner.common.LineType;
import nl.numworx.geodefiner.common.PointType;
import nl.numworx.geodefiner.common.Snapper;
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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

import fi.wiskopdr.FormuleParser;

public class GeoDefinerGWTDebug extends GeoDefinerGWT implements EntryPoint {

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

	@Override
	public void onModuleLoad() {
      Element body = Document.get().getBody();
      //body.setAttribute("oncontextmenu", "return false;");

      root = uiBinder.createAndBindUi(this);
      RootPanel.get().add(FocusOnTouch.wrap(root, true));

		//FormuleParser.zetWoordFormule(true);
		
		Map<String, Object> launchDebug = new HashMap<String, Object>();
		List<Integer> toolbox = Arrays.asList(
				Tools.SELECTOR,
				Tools.COLOR_PALETTE,
				Tools.POINT,
				Tools.LINE,
				Tools.HALFLINE,
//				Tools.SEGMENT,
//				Tools.LINE_PALETTE,
//
//				Tools.PERPENDICULAR,
//				Tools.PARALLEL,

				Tools.CIRCLE,
				Tools.ARC,
				Tools.TRIANGLE,
				Tools.CIRCLE_WITH_RADIUS,
				Tools.ANGLE_POINT,
//
//				Tools.MIDPOINT,
//				Tools.BISECTRICE,
//				Tools.MIRROR,
//
				Tools.CONIC_SECTION,
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
				Tools.TEXT,			
//				Tools.FORMULA,
//
//				Tools.PAN,
				Tools.DESTROY,
				Tools.RESET
			);
		Vector<Map<?,?>> configs = new Vector();
		configs.setSize(29);
		configs.set(Tools.POINT, new HashMap(Collections.singletonMap("color", 0X80FF0000)));
		configs.set(Tools.CIRCLE, Collections.singletonMap("color", 0XFFFFFF00));
		configs.set(Tools.CIRCLE_WITH_RADIUS, new HashMap(Collections.singletonMap("color", 0XFFFF8800)));
		configs.set(Tools.TEXT, new HashMap(Collections.singletonMap("color", 0xFFFF0000)));
		HashMap mm = new HashMap();
		mm.put("color", 0xFF00FF00);
		mm.put("width", 0.5);
		configs.set(Tools.SEGMENT, mm );

		mm = new HashMap(configs.get(Tools.CIRCLE));
		mm.put("fill", 0x80808080);
		configs.set(Tools.CIRCLE, mm);
		
		
		mm = new HashMap(configs.get(Tools.TEXT));
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
				"$fa=9..#a#@" 
				//,"$ft=text(\"$P4x$nx@@$px$n8@@$b1$n2@@M$sx@$o{a}$nbc@@$w{a}+2$b1$n{a}/2@@$m2@@\",O)@"
				,"$ft=text(\"M$s8@ afstand e tan $zM@$sx@\",O)@"
				//,"$fP=point(1,1)@"
				//,"$fQ=point(-1,1)@"
				//,"$fh=halfline(Q,P)@"
				,"$fv =map(t -> text(\"{t}\u03c0\", point(t,2)), 1..3)@"
				//,"$fwaarde=true@"
				//, "$fy<-1-x*x@"
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
	    h = new HashMap();
	    h.put("rigid", false);
	    h.put("color", 0xFFFF0000);
	    h.put("type", PointType.DISK.name());
	    h.put("size", 15);
	    h.put("log", true);
	    configuration.put("P", h);configuration.put("Q", h);
	    h = new HashMap();
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
		positions.put("waarde", Arrays.asList(1, 40, 1, 40));
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
		
		setState(new HashMap());
		start();
	}

	
}
