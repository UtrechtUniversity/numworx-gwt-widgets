package fi.graphtoolgwt.client;

import fi.graphtoolgwt.client.FormuleComponentGWT.GraphtFormuleEditor;
import fi.graphtoolgwt.client.FormuleComponentGWT.TriforkFormuleEditor;

public class TriforkFormuleEditorFactory extends FormuleEditorFactory {

	@Override
	GraphtFormuleEditor build(int i) {
		// TODO Auto-generated method stub
		return new TriforkFormuleEditor(i, fc);
	}

}
