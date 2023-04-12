package fi.graphtoolgwt.client;

import fi.graphtoolgwt.client.FormuleComponentGWT.GraphtFormuleEditor;

public class FormuleEditorFactory {
	FormuleComponentGWT fc;
	
	GraphtFormuleEditor build(int i) {
		return new GraphtFormuleEditor(i, fc);
	}

	void setFc(FormuleComponentGWT fc) {
		this.fc = fc;
	}
	
}
