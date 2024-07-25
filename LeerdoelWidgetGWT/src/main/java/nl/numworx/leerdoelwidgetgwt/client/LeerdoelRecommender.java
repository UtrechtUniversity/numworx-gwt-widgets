package nl.numworx.leerdoelwidgetgwt.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;

import fi.dwo.gwt.lib.rest.ui.IdleDetect;
import nl.numworx.leerdoelwidgetgwt.client.locale.LeerdoelWidgetMessages;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.AbstractStudentModelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.EastPanel;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.EastPanel_Factory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.Util;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class LeerdoelRecommender extends ResizeComposite {
	
	private static final LeerdoelWidgetMessages rb = GWT.create(LeerdoelWidgetMessages.class);
    private final static String lang = LocaleInfo.getCurrentLocale().getLocaleName();
    private static java.util.logging.Logger LOG = Logger.getLogger("LeerdoelRecommender");
	
	private DockLayoutPanel list;
	private List<String> objectives;
	private DescriptionService service;
	FormuleKeyboardIF keyboard;
	private boolean showScore = true;

	private Label header;

	IdleDetect idler;
	int width;
	OpdrNavIF comRoot;

	LeerdoelRecommender() {
		list = new DockLayoutPanel(Unit.EM);
		list.addStyleName("recommender");
		header = new Label(rb.intro());
		header.addStyleName("intro");
		list.addNorth(header, 5);
		initWidget(list);
	}

	public void setObjectives(List<String> objectives) {
		this.objectives = objectives.stream().map(this::strip).collect(Collectors.toList());
// debuggertje:
//		for (String o : objectives) {
//			Label l = new Label(o);
//			list.add(l);
//		}
		
	}

	private String strip(String in) {
		return in.split("/",2)[0];
	}
	private String getTitle(DomStudentModelContextInfo info) {
		return AbstractStudentModelPresenter.getTitle(info, lang);
	}
	
	
	Promise<?> setModelScore(DomStudentModelContext4Student studentModel, DomStudentModelDataScore s, DomMethod activeMethod) {		
		int cnt = 0;
		StackLayoutPanel stack = new StackLayoutPanel(Unit.EM);
		for (String o : objectives) {	
			
			DomStudentModelContextInfo info = getInfo(studentModel, o);
			if (info == null) 
				continue;
			DomStudentModelScore<?> score = getScore(s.getDomStudentModelStructureScore(), o);
			double greenPerc = Util.getGreen(score) * 200;
			if (greenPerc > 50) 
				continue;
			//east.setPixelSize(-1, 200); // size of "content panel of iframe, echter er zit een scrollpane tussen en die heeft size 0
			Label title = new Label(getTitle(info));
			SimpleLayoutPanel east = tekstPanel(service.getDescription(studentModel, info),width,200);
			com.google.gwt.user.client.ui.Widget panel = east;
			if (showScore) {
				DockLayoutPanel p = new DockLayoutPanel(Unit.EM);
				p.addSouth(Util.scoreItem("", score, Util.MAX_LEVEL), 2); // als in eastPanel
				p.add(east);
				panel = p;
			}
			stack.add(panel, title, 2);
			cnt ++;
		}
		if (cnt == 0) header.setText(rb.allok());
		else
			list.add(stack);
		list.forceLayout();
		RootLayoutPanel.get().setStyleName("alert", cnt!=0);
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

	public void setService(DescriptionService service) {
		this.service = service;		
	}

	public void enableScore(boolean showScore) {
		this.showScore = showScore;
	}

	SimpleLayoutPanel tekstPanel(Promise<String> promise, int width, int height) {
		SimpleLayoutPanel parent = new SimpleLayoutPanel();
		promise.then(p -> {
			StubWidget tekstpanel = new StubWidget(9, keyboard, idler, comRoot);
			ObjectMap launch;
			String value = p.getValue();
			JSONValue js;
			js = JSONParser.parseLenient(value);
			js = js.isObject().get("opdracht_1_1");
			js = js.isObject().get("interactiePanelLaunchData");
			js = js.isArray().get(5);
			js = js.isObject().get("interactiePanelLaunchState");
			
			LOG.info(js.toString());
			launch = JSONUtilities.wrapMap(js.isObject());
			
			tekstpanel.init(width, height, launch);
			parent.add(tekstpanel);
			return null;
		});
		return parent;
	}

}
