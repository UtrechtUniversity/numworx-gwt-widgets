package nl.numworx.leerdoelwidgetgwt.client;

import java.util.Collections;
import java.util.HashMap;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class LeerdoelWidgetDebug extends LeerdoelWidgetGWT implements OpdrNavIF {

	public LeerdoelWidgetDebug() {
		super();
	}

	@Override
	public void onModuleLoad() {
		Defaults.setServiceRoot("/dwo/rest/");
	    Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
    	DefaultFilterawareDispatcher.singleton().addFilter(this);

    	HashMap data = new HashMap();
    	data.put("activeMethod", "LOCAL;none;Getal&Ruimte");
    	data.put("studentModelID", "MYSQL;" + PersistenceClassType.PersistentStudentModelContext.name() + ";1");
    	data.put("filter", Collections.emptyMap());
    	data.put("dwoProfileID", "MYSQL;" + PersistenceClassType.PersistentDwoProfile.name() + ";77");
    	data.put("leerdoelScore", true);
    	data.put("type", 1);
    	
    	panel = RootLayoutPanel.get();
    	
    	
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
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
		return "1-XXXX-YYYY";
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
		return LessonMode.normal;
	}

	@Override
	public Role getRole() {
		return Role.Learner;
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
		return null;
	}

	@Override
	public ObjectMap getContext() {
		// iets met schoolclass
		return null;
	}

	@Override
	public boolean filter(Method method, RequestBuilder builder) {
		builder.setHeader("Authorization", "Basic bWVlc3RlcndpbTo1ZmZkY2M5YzkyYzMwMWFkYjA2NGEzZmZkOWJkMjY0Yw==");
		return true;
	}

	@Override
	void setContext(OpdrNavIF root) {
		super.setContext(root);
		DomHasRole role = context.getDomHasRole();
		String u = ";306633"; //meesterwim
		String sg = ";581";   // student group
		role.setUserId(new PersistenceId("MYSQL;" + PersistenceClassType.PersistentUser.name() + u));
		role.setSchoolGroupId(new PersistenceId("MYSQL;" + PersistenceClassType.PersistentSchoolGroup.name() + sg));
		role.setId(new PersistenceId("MYSQL;" + PersistenceClassType.PersistentHasRole.name() + u + sg));	
	}
	
	
}
