package nl.numworx.leerdoelwidgetgwt.client;

import java.util.List;

import org.osgi.util.promise.Promise;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;

public class LeerdoelRecommender extends Composite {
	
	private VerticalPanel list;
	private List<String> objectives;
	private DescriptionPresenter service;

	LeerdoelRecommender() {
		list = new VerticalPanel();
		initWidget(list);
	}

	public void setObjectives(List<String> objectives) {
		this.objectives = objectives;
// debuggertje:
		for (String o : objectives) {
			Label l = new Label(o);
			list.add(l);
		}
		
	}

	Promise<?> setModelScore(DomStudentModelContext4Student studentModel, DomStudentModelDataScore s, DomMethod activeMethod) {
		return null;
	}
	
	
	void setModelScore(DomStudentModelContext4Student studentModel, Promise<DomStudentModelDataScore> s,
			DomMethod activeMethod) {
		s.then(  p -> setModelScore(studentModel, p.getValue(), activeMethod));
		
	}

	public void setService(DescriptionPresenter service) {
		this.service = service;		
	}

}
