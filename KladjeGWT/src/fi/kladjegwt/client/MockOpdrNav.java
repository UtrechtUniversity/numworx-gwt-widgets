package fi.kladjegwt.client;

import java.util.Collections;

import com.google.gwt.canvas.dom.client.CssColor;
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

public class MockOpdrNav implements OpdrNavIF, FormuleKeyboardIF, FormuleClipboardIF{

	public MockOpdrNav() {
		// TODO Auto-generated constructor stub
	}
	private FormuleEditorIF editor;

	@Override
	public void setChanged(boolean fout) {

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

	@Override
	public ObjectMap getContext() {
		return JSONUtilities.wrapMap(Collections.singletonMap("premium", Boolean.TRUE));
	}

}
