package fi.graphtoolgwt.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

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
public class GraphToolGWTDebug extends GraphToolGWT {

	private static final int H = 800;

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
			return 3;
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
		public void functionKey(int code) {
			if (editor == null) return;
			switch(code) {
			case  1: editor.wortel();   break;
			case  2: editor.macht();    break;
			case  3: editor.kwadraat(); break;
			case  4: editor.breuk();    break;
			case  5: editor.haakjes();  break;
			case  6: editor.ndewortel();break;
			case  7: editor.integraal();break;
			case  8: editor.prv();		break;
			case  9: editor.ndelog();    break;
			case 10: editor.abs();       break;
			case 11: editor.subscript(); break;
			case 12: editor.bin();       break;
			default:
			}
			
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

	    RootLayoutPanel root = RootLayoutPanel.get();
		FocusPanel child = FocusOnTouch.wrap(asWidget(), true);
		asWidget().getElement().getStyle().clearPosition();
		
		root.add(child);
		root.setWidgetTopHeight(child, 0, Unit.PX, H, Unit.PX);
		root.setWidgetLeftWidth(child, 0, Unit.PX, 300, Unit.PX);
	
		Map<String, Object> map = new HashMap<>();
		map.put("tekenComponentAan", Boolean.TRUE);
		map.put("tabelComponentAan", Boolean.TRUE);
		map.put("veldComponentAan", Boolean.FALSE);
		map.put("formuleComponentAan", Boolean.FALSE);
		map.put("typeOpdracht" , TEKENTABELPUNTEN);
		
		map.put("docentGraphPointsX", new Double[] { 1.0, 2.0, 3.0 });
		map.put("docentGraphPointsY", new Double[] { 1.0, 2.0, 4.0 });
		map.put("docentGraphPointsIndex", new Integer[] { 0,1,2} );
		map.put("docentGraphPointsTabelIndex", new Integer[] {0,0,0 } );
		map.put("docentGraphPointsXString", new String[] { "1","2", "3"});
		map.put("docentGraphPointsYString", new String[] { "1","2", "4.0"});
		

		init(300,H, map, Collections.emptyMap());
		MockOpdrNav opdrnav = new MockOpdrNav();
		FocusOnTouch.installKeyboard(opdrnav, opdrnav);
		FocusOnTouch.focus();
		setCommunicationRoot(opdrnav);
	}

	
	
	
}
