package nl.numworx.leerdoelwidgetgwt.client;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.dwo.gwt.lib.rest.DwoConstants;
import fi.dwo.gwt.lib.rest.CallManagers.MethodManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentStudentModelManager;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LeerdoelWidgetGWT implements EntryPoint, InteractionStub, DispatcherFilter {

	OpdrNavIF root;
	private int height;
	private int width;
	private boolean volledigeBreedte;
	private int asHoogte;
	private LeerdoelGraph graph;
	private DomContext context = new DomContext();
	
	LayoutPanel panel;
	private Map<String, Map<String, Set<Integer>>> filter;
	private DomMethod activeMethod;
	private DomDwoProfileId profile;
	private DomStudentModelContextId studentModelID;
	
	public LeerdoelWidgetGWT(HashMap<String, Object> h, HashMap<String, Number> randomVarWaarden, int volleBreedte) {
		this();
		ObjectMap map = JSONUtilities.wrapMap(h);
		width = map.getInt("breedte");
		height = map.getInt("hoogte");
		volledigeBreedte = map.getBoolean("volledigeBreedte", false);
		if (volledigeBreedte) 
			width = volleBreedte;
		Map<String,Object> launchState = Collections.emptyMap();
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		panel = new LayoutPanel();
		init(width, height, launchState, randomVarWaarden);		
	}
	
	public LeerdoelWidgetGWT() {
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		DwoConstants constants = GWT.create(DwoConstants.class);
		Defaults.setServiceRoot(constants.server());
	    Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
    	DefaultFilterawareDispatcher.singleton().addFilter(this);
		panel = RootLayoutPanel.get();
		Stub.publish(this);
	}

	@Override
	public HashMap<String, Object> getState() {
		return new HashMap<>();
	}

	@Override
	public void setState(HashMap<String, Object> h) {
	}

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public int[][] getScoreObjectives() {
		return null;
	}

	@Override
	public Boolean isCorrect() {
		return Boolean.TRUE;
	}

	@Override
	public void kijkNa() {
	}

	@Override
	public void zetNagekeken(boolean b) {
		
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.root = comRoot;
		MethodManager methods;
		methods = MethodManager.student(); // of teacher! 
		SecuredStudentStudentModelManager models = new SecuredStudentStudentModelManager();
		profile = new DomDwoProfileId();
		profile.setId(new PersistenceId("MYSQL:PersistenceDwoProfile;77")); // from context
		DomSchoolClass schoolclass = null;
		
		Promise<DomMethod> m = methods.getMethod(context, activeMethod, profile);
		Promise<DomStudentModelContext4Student> p;
		p = models.getStudentModelForClass(context, studentModelID, schoolclass);
		Promises.all(p,m).then(xxx -> {
			initialize(m,p,null);
			return xxx;
		});
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		if (volledigeBreedte) {
			width = breedte;
		}
	}

	@Override
	public Widget asWidget() {
		return panel;
	}

	@Override
	public int getAsHoogte() {
		return asHoogte;
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public void setAsHoogte(int ashoogte) {
		asHoogte = ashoogte;
	}
	
	@Override
	public void init(int width, int height, Map<String, Object> launchData, Map<String, Number> values) {
		this.width = width;
		this.height = height;
		ObjectMap h = JSONUtilities.wrapMap(launchData);
		
	    String studentModelID = null;
	    ObjectMap filter = null;
	    String activeMethod = null;
	    boolean leerdoelPopup = true;
	    boolean voorkennisKnop = false;
	    boolean voorkennisMenu = false;
	    boolean zoomKnoppen = false;
	    boolean filterHeader = false;
	    boolean leerdoelScore = false;
	    
	    if(h.containsKey("activeMethod"))
	      activeMethod = h.getString("activeMethod");
	    if(h.containsKey("studentModelID"))
	      studentModelID = h.getString("studentModelID");
	    if(h.containsKey("filter"))
	      filter = h.getObjectMap("filter");
	    if(h.containsKey("leerdoelPopup"))
	      leerdoelPopup = h.getBoolean("leerdoelPopup");
	    if(h.containsKey("voorkennisKnop"))
	      voorkennisKnop = h.getBoolean("voorkennisKnop");
	    if(h.containsKey("voorkennisMenu"))
	      voorkennisMenu = h.getBoolean("voorkennisMenu");
	    if(h.containsKey("zoomKnoppen"))
	      zoomKnoppen = h.getBoolean("zoomKnoppen");
	    if(h.containsKey("filterHeader"))
	      filterHeader = h.getBoolean("filterHeader");
	    if(h.containsKey("leerdoelScore"))
	      leerdoelScore = h.getBoolean("leerdoelScore");
	    
	    this.filter = convert(filter);
	    this.activeMethod = new DomMethod(new PersistenceId(activeMethod));
	    this.studentModelID = new DomStudentModelContextId(new PersistenceId(studentModelID));
	    
	    graph = new LeerdoelGraph(voorkennisKnop, zoomKnoppen, voorkennisMenu);
	
	    panel.add(graph);
	    panel.setWidgetLeftWidth(graph, 0, Unit.PX, width, Unit.PX);
	    panel.setWidgetTopHeight(graph, 0, Unit.PX, height, Unit.PX);
	    

	
	}

	private void initialize(Promise<DomMethod> m, Promise<DomStudentModelContext4Student> p, Promise<DomStudentModelDataScore> s) {
		activeMethod = m.getValue();
		DomStudentModelContext4Student item = p.getValue();
		item.setFilter(filter);
		item.getModelStructure().setActiveMethod(activeMethod.getId());
		graph.setModelScore(item, s, activeMethod);
	    graph.doFilter(filter);
	}

	private Map<String, Map<String, Set<Integer>>> convert(ObjectMap filter) {
		if (filter == null) return null;
		Map<String, Map<String, Set<Integer>>> result = new HashMap<>();
		Set<String> keys = filter.keySet();
		for(String key: keys) {
			result.put(key, convert2(filter.getObjectMap(key)));
		}
		return result;
	}

	private Map<String, Set<Integer>> convert2(ObjectMap objectMap) {
		Map<String, Set<Integer>> result = new HashMap<>();
		for(String key: objectMap.keySet()) {
			result.put(key, convert3(objectMap.getObjectList(key)));
		}
		return result;
	}

	private Set<Integer> convert3(ObjectList list) {
		Set<Integer> result = new TreeSet<>();
		for(int i = 0; i  < list.size(); i++) {
			result.add(list.getInt(i));
		}
		return result;
	}

	@Override
	public boolean filter(Method method, RequestBuilder builder) {
		builder.setHeader("Authorization", root.getContext().getString("Authorization"));
		return true;
	}
}