package nl.numworx.geodefinergwt.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			return null;
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
		root = uiBinder.createAndBindUi(this);
		
		RootPanel.get().add(FocusOnTouch.wrap(root, true));

		//FormuleParser.zetWoordFormule(true);
		
		Map<String, Object> launchDebug = new HashMap<String, Object>();
		List<Integer> toolbox = Arrays.asList(
				Tools.SELECTOR,
				Tools.POINT,
				Tools.RESET,
				Tools.LINE,
				Tools.HALFLINE,
				Tools.SEGMENT,
				Tools.CIRCLE,
				Tools.ARC,
				Tools.TEXT,
				Tools.PAN,
				Tools.TRIANGLE,
				Tools.PERPENDICULAR,
				Tools.PARALLEL,
				Tools.CIRCLE_WITH_RADIUS,
				Tools.DISTANCE,
				Tools.MIDPOINT,
				Tools.LOCUS,
				11,12,13,19,20,21,22,26);
		launchDebug.put("toolbox", toolbox);
		Map<String,Object> checkDWO = new HashMap<String,Object>();
		checkDWO.put("formule", "$ftrue@");
		checkDWO.put("score", 10);
		checkDWO.put("check", Boolean.TRUE);
		checkDWO.put("extern", Boolean.FALSE);
		List<String> definitions = Arrays.asList(
				"$fa=9..#a#@" 
				//,"$ft=text(\"$P4x$nx@@$px$n8@@$b1$n2@@M$sx@$o{a}$nbc@@$w{a}+2$b1$n{a}/2@@$m2@@\",O)@"
				,"$ft=text(\"M$s8@ afstand e tan $zM@$sx@\",O)@"
				,"$fP=point(1,1)@"
				,"$fh=halfline(O,P)@"
				,"$fy=$px$n2@@/2-2@"
				,"$fwaarde=true@"
				, "$fy<-1@"
				);
		launchDebug.put("definitions", definitions);
		Map<String,Object> h = new HashMap<>();
	// Collections.singletonMap("color", 0);
		h.put("color", 0XFFFF0000);
		h.put("width", 3);
		h.put("tip", "ATSTART");
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
		launchDebug.put("configuration", configuration);
		launchDebug.put("checkDWO", checkDWO);
	//"checkObjects":[{"score":5,"value":"$fpoint(2,1)@"}
		Map<String,Object> checkObject = new HashMap<>();
		checkObject.put("score", 5);
		checkObject.put("value", "$fpoint(2,1)@");		
		launchDebug.put("checkObjects", Collections.singletonList(checkObject));

		Map<String,Object> positions = new HashMap<>();
		positions.put("waarde", Arrays.asList(1, 40, 1, 40));
		launchDebug.put("positions", positions);
		
		Map<String, Number> values = new HashMap<String, Number>();
		values.put("a", Integer.valueOf(10));
		init(getWidth(), getHeight(), launchDebug, values);
		MockOpdrNav opdrnav = new MockOpdrNav();
		FocusOnTouch.installKeyboard(opdrnav, opdrnav);
		FocusOnTouch.focus();
		setCommunicationRoot(opdrnav);
		viewer.adapt(Snapper.class).setGravity(true);
		start();
	}

	
}
