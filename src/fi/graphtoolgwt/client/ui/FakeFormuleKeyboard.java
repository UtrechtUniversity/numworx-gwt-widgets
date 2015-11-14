package fi.graphtoolgwt.client.ui;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;

public class FakeFormuleKeyboard implements FormuleKeyboardIF, IsWidget {

	private FormuleEditorIF editor;
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
	public FormuleEditorIF getEditor() {
		return editor;
	}

	@Override
	public Widget asWidget() {
		return null;
	}

	@Override
	public void focus() {		
	}

	@Override
	public void softFocus() {
	}

	@Override
	public void blur() {
	}

	
}
