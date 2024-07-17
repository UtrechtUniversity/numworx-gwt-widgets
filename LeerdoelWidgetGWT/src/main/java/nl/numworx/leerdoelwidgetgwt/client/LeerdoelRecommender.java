package nl.numworx.leerdoelwidgetgwt.client;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import nl.numworx.leerdoelwidgetgwt.client.locale.LeerdoelWidgetMessages;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.EastPanel;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.EastPanel_Factory;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class LeerdoelRecommender extends Composite {
	
	private static final LeerdoelWidgetMessages rb = GWT.create(LeerdoelWidgetMessages.class);
	
	private VerticalPanel list;
	private List<String> objectives;
	private DescriptionPresenter service;
	private boolean showScore = true;

	LeerdoelRecommender() {
		list = new VerticalPanel();
		list.addStyleName("recommender");
		Label header = new Label(rb.intro());
		list.add(header);
		initWidget(list);
	}

	public void setObjectives(List<String> objectives) {
		this.objectives = objectives.stream().map(this::strip).collect(Collectors.toList());
// debuggertje:
		for (String o : objectives) {
			Label l = new Label(o);
			list.add(l);
		}
		
	}

	private String strip(String in) {
		return in.split("/",2)[0];
	}
	
	
	Promise<?> setModelScore(DomStudentModelContext4Student studentModel, DomStudentModelDataScore s, DomMethod activeMethod) {		

		for (String o : objectives) {	
			
			DomStudentModelContextInfo info = getInfo(studentModel, o);
			if (info == null) 
				continue;
			DomStudentModelScore<?> score = getScore(s.getDomStudentModelStructureScore(), o);
			EastPanel east = EastPanel_Factory.newInstance(service);
			east.setDescription(studentModel, info);
			east.setPerc(score);
			east.enableScore(showScore);
			list.add(east);
		}		
		return null;
	}
	
	
	private DomStudentModelScore<?> getScore(DomStudentModelScore<?> structure, String o) {
		if (structure.getId().equals(o)) return structure;
		List<?> children = structure.getChildren();
		if (children == null) return null;
		Iterator<?> it = children.iterator();
		while (it.hasNext()) {
			DomStudentModelScore<?> type = (DomStudentModelScore<?>) it.next();
			DomStudentModelScore<?> result = getScore(type, o);
			if (result != null) return result;
		}		
		return null;
	}

	private DomStudentModelContextInfo getInfo(DomStudentModelContext4Student studentModel, String o) {
		DomStudentModelStructure structure = studentModel.getModelStructure();
		DomStudentModelContextInfo info = structure.getInfo();
		if (info.getId().equals(o)) return info;
		List<DomStudentModelCategory> cats = structure.getCategories();
		for( DomStudentModelCategory cat: cats) {
			info = cat.getInfo();
			if (info.getId().equals(o)) return info;
			List<DomStudentModelObj> objs = cat.getObjectives();
			for (DomStudentModelObj obj: objs) {
				Optional<DomStudentModelContextInfo> opt = getInfo(obj, o);
				if (opt.isPresent()) return opt.get();
			}			
		}
		return null;
	}

	private Optional<DomStudentModelContextInfo> getInfo(DomStudentModelObj parent, String o) {
		DomStudentModelContextInfo info = parent.getInfo();
		if (info.getId().equals(o)) return Optional.of(info);
		List<DomStudentModelObj> objs = parent.getObjectives();
		if (objs != null)
			for (DomStudentModelObj obj: objs) {
				Optional<DomStudentModelContextInfo> opt = getInfo(obj, o);
				if (opt.isPresent()) return opt;
			}			
		return Optional.empty();
	}

	void setModelScore(DomStudentModelContext4Student studentModel, Promise<DomStudentModelDataScore> s,
			DomMethod activeMethod) {
		s.then(  p -> setModelScore(studentModel, p.getValue(), activeMethod));
		
	}

	public void setService(DescriptionPresenter service) {
		this.service = service;		
	}

	public boolean isShowScore() {
		return showScore;
	}

	public void setShowScore(boolean showScore) {
		//this.showScore = showScore;
	}

}
