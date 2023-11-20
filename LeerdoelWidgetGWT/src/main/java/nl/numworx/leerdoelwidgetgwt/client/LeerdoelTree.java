package nl.numworx.leerdoelwidgetgwt.client;

import org.osgi.util.promise.Promise;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsTree;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class LeerdoelTree extends StudentResultsTree implements SelectionHandler<TreeItem> {

	private EventBus bus;

	public LeerdoelTree(Panel parent, EventBus bus) {
		super(bus);
		this.bus = bus;
		parent.add(this);
		addSelectionHandler(this);
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		bus.fireEventFromSource(event, this);
	}

	public void buildTree(DomStudentModelContext4Student studentModel, Promise<DomStudentModelDataScore> s, DomMethod method) {
		setMethod(method);
		DomStudentModelStructure model = studentModel.getModelStructure();
		StudentResultsPresenter.setCurrentInfo(model.getCategories(), model.getInfo(), currentInfo);
		super.insertTree(studentModel, s);
		
	}


}
