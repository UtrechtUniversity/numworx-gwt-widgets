package nl.numworx.leerdoelwidgetgwt.client;

import java.util.logging.Logger;

import org.osgi.util.promise.Promise;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.web.bindery.event.shared.EventBus;

import nl.numworx.leerdoelwidgetgwt.client.LeerdoelWidgetGWT.RoleAPI;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel.AbstractStudentModelView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.AbstractStudentModelPresenter;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class LeerdoelPresenter implements SelectionHandler<TreeItem> {
	private static final Logger LOG = Logger.getLogger(LeerdoelPresenter.class.getName());
	private RoleAPI service;
	private LeerdoelTree view;
	private EventBus bus;
    private String lang = LocaleInfo.getCurrentLocale().getLocaleName();


	LeerdoelPresenter(EventBus bus, DwoGlobalVars vars) {
		this.bus = bus;
	}

	void setView(LeerdoelTree view) {
		this.view = view;
		bus.addHandlerToSource(SelectionEvent.getType(), view, this);
	}

	void setService(RoleAPI s) {
		this.service = s;
	}

	public void setModelScore(DomStudentModelContext4Student studentModel, Promise<DomStudentModelDataScore> s,
			DomMethod method) {
		DomStudentModelStructure structure = studentModel.getModelStructure();
		DomStudentModelContextInfo info = structure.getInfo();
		// build tree
		view.buildTree(studentModel, s,  method);
		
	}

	private String getTitle(DomStudentModelContextInfo info) {
		return AbstractStudentModelPresenter.getTitle(info, lang);
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		LOG.info(event.toDebugString());
		
	}

}
