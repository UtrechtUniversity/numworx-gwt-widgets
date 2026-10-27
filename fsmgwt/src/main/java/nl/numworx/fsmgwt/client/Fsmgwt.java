package nl.numworx.fsmgwt.client;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

import fi.euclides.util.Hashtable;
import nl.numworx.fsm.editor.Output;
import nl.numworx.fsm.shared.Hoekpunt;
import nl.numworx.fsm.shared.Memento;
import nl.numworx.fsm.shared.MidBoogPunt;
import nl.numworx.fsmgwt.client.text.DeleteHandler;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Fsmgwt extends Composite implements EntryPoint, InteractionStub  {

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


	
	
	
	private int height;
	private int width;
	private int scoreMax;
	private CanvasViewer viewer;
	private Memento memento;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
//		Map<String, Object> launchdata = Collections.singletonMap("scoreMax", 10);init(400,300, launchdata, null);
//		MockOpdrNav opdrnav = new MockOpdrNav();
//		FocusOnTouch.installKeyboard(opdrnav, opdrnav);
//		FocusOnTouch.focus();
//		setCommunicationRoot(opdrnav);

		Stub.publish(this);
	}

	@Override
	public HashMap<String, Object> getState() {
		Object model, names, accepted;
		viewer.getModel().clearSelection();
		JSONOutput dos = new JSONOutput();
		memento.setDataOutputStream(dos);
		HashMap<String, Object> result = new HashMap<>();
		try {
			memento.writeModel(viewer);
			model = dos.toList();
			result.put("model", model);
			dos = new JSONOutput();
			memento.writeNames(dos);
			names = dos.toList();
			result.put("names", names);
			dos = new JSONOutput();
			memento.writeAccepted(dos);
			accepted = dos.toList();
			result.put("accepted", accepted);			
		} catch (IOException e) {
			GWT.log ("does not occur", e);
		}		
		return result;	}

	private void setState(Map<String,Object> h) {
		if (h == null) return;
		ObjectMap state = JSONUtilities.wrapMap(h);
		ObjectList data = state.getObjectList("model");
		if (data != null) {
			memento.setDataInputStream(new JSONInput(data));
			try {
				memento.readModel(viewer);
				ObjectList names = state.getObjectList("names");
				memento.readNames(new JSONInput(names));
				ObjectList accepted = state.getObjectList("accepted");
				memento.readAccepted(new JSONInput(accepted));
			} catch (Exception e) {
				GWT.log("setState fails", e);
			}
		}

	}
	
	
	@Override
	public void setState(HashMap<String, Object> h) {
		setState( (Map<String,Object>)h);
		viewer.paint();
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
		if (scoreMax == 0)
			return Boolean.TRUE;
		return null;
	}

	@Override
	public void kijkNa() {

	}

	@Override
	public void zetNagekeken(boolean b) {

	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		FormuleHolder.installKeyboard(comRoot.getKeyboard());
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getAsHoogte() {
		// TODO Auto-generated method stub
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
// extra's
		Hoekpunt.addCreator();
		MidBoogPunt.addCreator();
		memento = new Memento();
		viewer = new CanvasViewer(width, height);
		viewer.setModel(memento.getModel());
		DeleteHandler handler = new DeleteHandler(viewer);
		viewer.addKeyDownHandler(handler);
		initWidget(viewer.asWidget());
		RootPanel.get().add(this);
		ObjectMap m = JSONUtilities.wrapMap(launchData);
		scoreMax = m.getInt("scoreMax");
		setState(launchData);

	}
}
