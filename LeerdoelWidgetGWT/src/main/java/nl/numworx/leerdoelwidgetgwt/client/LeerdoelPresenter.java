package nl.numworx.leerdoelwidgetgwt.client;

import java.util.Optional;
import java.util.logging.Logger;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.web.bindery.event.shared.EventBus;

import nl.numworx.leerdoelwidgetgwt.client.LeerdoelWidgetGWT.RoleAPI;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.AbstractStudentModelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter_Factory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.EastPanel;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class LeerdoelPresenter implements SelectionHandler<TreeItem> {
	private static final Logger LOG = Logger.getLogger(LeerdoelPresenter.class.getName());
	private LeerdoelTree view;
	private EastPanel east;
	private EventBus bus;
    private String lang = LocaleInfo.getCurrentLocale().getLocaleName();
    private Failure FAILURE;
    private DomStudentModelContext4Student model;
	private Promise<DomStudentModelDataScore> score;


	LeerdoelPresenter(EventBus bus, DwoGlobalVars vars) {
		this.bus = bus;
		FAILURE = new LoggingFailure(LOG, bus);
	}

	void setView(LeerdoelTree view) {
		this.view = view;
		bus.addHandlerToSource(SelectionEvent.getType(), view, this);
	}
	
	void setEast(EastPanel east) {
		this.east = east;
	}

	public void setModelScore(DomStudentModelContext4Student studentModel, Promise<DomStudentModelDataScore> s,
			DomMethod method) {
		this.model = studentModel;
		this.score = s;
		DomStudentModelStructure structure = studentModel.getModelStructure();
		DomStudentModelContextInfo info = structure.getInfo();
		// build tree
		view.buildTree(studentModel, s,  method);
		east.title.setText(getTitle(info));
		
	}

	private String getTitle(DomStudentModelContextInfo info) {
		return AbstractStudentModelPresenter.getTitle(info, lang);
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		LOG.info("selected " + item);
		Object userObject = item.getUserObject();
		east.clearVisibility();
		if (userObject instanceof DomStudentModelContext4Student) {
			DomStudentModelContext4Student model = this.model;
			east.setDescription(model, model.getModelStructure().getInfo());
			score.then ( p -> {
				DomStudentModelStructureScore score = p.getValue().getDomStudentModelStructureScore();
				east.setPerc(score);
				return p;
			}, FAILURE);
		} else if (userObject instanceof Integer) {
			DomStudentModelContext4Student model = this.model;
			DomStudentModelStructure structure = model.getModelStructure();
			DomStudentModelCategory o = structure.getCategories().get(((Integer) userObject).intValue());
			east.setDescription(model, o.getInfo());
			score.then(p -> { 
				DomStudentModelCategoryScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(((Integer) userObject).intValue());
				east.setPerc(score);
				return p; }, FAILURE)
			;			
		} else if (userObject instanceof int[]) {
			int[] elems = (int[]) userObject;
			int cat = elems[0], obj = elems[1];
			DomStudentModelContext4Student model = this.model;
			DomStudentModelStructure structure = model.getModelStructure();
			DomStudentModelCategory o = structure.getCategories().get(cat);

			DomStudentModelObj o0 = o.getObjectives().get(obj);
			for (int i = 2; i < elems.length; i++ ) {
				o0 = o0.getObjectives().get(elems[i]);
			}
			final DomStudentModelObj oo = o0;
			east.setDescription(model, oo.getInfo());

            score.then( p -> { 
				DomStudentModelObjectiveScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(cat).getObjectives().get(obj);
				for (int i = 2; i < elems.length; i++ ) score = score.getChildren().get(elems[i]);
				east.setPerc(score);
				return p; }, FAILURE);
		}
		
	}

}
