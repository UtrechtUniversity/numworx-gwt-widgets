package fi.nabouwenaanzichtengwt.client;

import java.util.HashMap;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class NabouwenAanzichtenDebug extends NabouwenAanzichtenGWT {

	public class OpdrNav implements OpdrNavIF {

		@Override
		public void setChanged(boolean fout) {
			// TODO Auto-generated method stub

		}

		@Override
		public FormuleKeyboardIF getKeyboard() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FormuleClipboardIF getFormuleClipboard() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getMode() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getLearnerId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getLearnerName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CssColor getBackground() {
			return CssColor.make("white");
		}

		@Override
		public String getUUID() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public LessonMode getLessonMode() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Role getRole() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public HandlerRegistration addCBookEventListener(String command, CBookEventListener listener) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void fireEvent(CBookEvent event) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean hasListeners(String command) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void pause() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unpause() {
			// TODO Auto-generated method stub

		}

		@Override
		public ObjectMap getConfiguration() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ObjectMap getContext() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	@Override
	public void onModuleLoad() {
		makeResources();
		RootPanel.get().add(panel);
		
		HashMap<String, Object> launchData = new HashMap<String, Object>();
		init(breedte, hoogte, launchData, new HashMap<String, Number>());
		setCommunicationRoot(new OpdrNav());
	}

}
