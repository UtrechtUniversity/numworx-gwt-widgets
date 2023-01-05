package nl.numworx.leerdoelwidgetgwt.client;

import java.util.Collections;
import java.util.HashMap;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.web.bindery.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class LeerdoelWidgetDebug extends LeerdoelWidgetGWT implements OpdrNavIF {

	public LeerdoelWidgetDebug() {
		super();
	}

	@Override
	public void onModuleLoad() {
		HashMap data = new HashMap();
		int w = 400;
		int h = 400;
		init(w, h, data, Collections.emptyMap());
		setCommunicationRoot(this);
	}

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
		// TODO Auto-generated method stub
		return null;
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
