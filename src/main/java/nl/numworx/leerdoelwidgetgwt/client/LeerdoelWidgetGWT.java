package nl.numworx.leerdoelwidgetgwt.client;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;
import org.osgi.util.promise.FailedPromisesException;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import fi.dwo.gwt.lib.rest.DwoConstants;
import fi.dwo.gwt.lib.rest.CallManagers.MethodManager;
import fi.dwo.gwt.lib.rest.CallManagers.OAuthManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentStudentModelManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import fi.dwo.gwt.lib.rest.util.Dwo2LocaleMessageGWTTranslator;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsService;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import nl.uu.fi.dwo.rest.util.Dwo2LocaleMessageTranslator;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LeerdoelWidgetGWT implements EntryPoint, InteractionStub, DispatcherFilter, SwitchViewEventHandler {

	private static final Failure FAILURE = new Failure() {

		@Override
		public void fail(Promise<?> resolved) throws Exception {
			Throwable failure = resolved.getFailure();
			Throwable[] list = { failure } ;
			
			if (failure instanceof FailedPromisesException) {
				list = ((FailedPromisesException) failure).getFailedPromises().toArray(list);
			}
			
			for (Throwable t: list)
				java.util.logging.Logger.getGlobal().log(Level.SEVERE, "failure " + t, t);		
		} };

		static {
	        //Initialize an Exception translator.imply removing all DOM elements can cause issues with other elements in the page.
	        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
	        Dwo2LocaleMessageTranslator.setTranslator(new Dwo2LocaleMessageGWTTranslator());
	}
		
		
	OpdrNavIF root;
	private int height;
	private int width;
	private boolean volledigeBreedte;
	private int asHoogte;
	private LeerdoelGraph graph;
	DomContext context = new DomContext();
	
	LayoutPanel panel;
	private Map<String, Map<String, Set<Integer>>> filter;
	private DomMethod activeMethod;
	private DomDwoProfileId profile;
	private DomStudentModelContextId studentModelID;
	private boolean leerdoelScore;
	private boolean filterHeader;
	private boolean zoomKnoppen;
	private boolean voorkennisMenu;
	private boolean voorkennisKnop;
	private boolean leerdoelPopup;
	private EventBus evbus;
	
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
		evbus = new SimpleEventBus();
		SwitchViewEventHandler handler = this;
		evbus.addHandler(SwitchViewEvent.TYPE, handler);
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
		DomSchoolClass schoolclass = new DomSchoolClass();
		setContext(root);

	    OAuthManager oauth = new OAuthManager();	    
		SecuredUserAccountManager account = new SecuredUserAccountManager();

		DwoGlobalVars vars = new DwoGlobalVars(account, oauth) {

			@Override
			public boolean isPremium() {
				return true;
			}

			@Override
			public Promise<DomDwoProfileFull> getProfile() {
				
				DomDwoProfileFull value = new DomDwoProfileFull();
				value.setId(profile.getId());
				return Promises.resolved(value);
			}

			@Override
			public DomSchoolClass getCurrentSchoolClass() {
				return schoolclass;
			}
			
			
		};
		//vars.setProfile(profile);
		//StudentModelService service = new StudentModelService(vars, context, methods);
		StudentResultsService service = new StudentResultsService(models, vars, methods, context);
		DescriptionPresenter description = new DescriptionPresenter(Optional.empty(), true, service);
		graph = new LeerdoelGraph(voorkennisKnop, zoomKnoppen, voorkennisMenu, leerdoelPopup, description);
	    graph.setTitleVisible(filterHeader);
	    
	    panel.add(graph);
	    panel.setWidgetLeftWidth(graph, 0, Unit.PX, width, Unit.PX);
	    panel.setWidgetTopHeight(graph, 0, Unit.PX, height, Unit.PX);

		
		Promise<DomMethod> m;
		//m = service.getActiveMethod(activeMethod.getId());
		if (activeMethod != null ) m = methods.getMethod(context, activeMethod, profile);
		else {
			DomMethod value = new DomMethod();
			value.setMethod(DwoLocalesForGWT.instance.NUM_LBL_METHOD_NONE()); // FIXED i18n
			m = Promises.resolved(value);
		}
		Promise<DomStudentModelContext4Student> p;
		p = models.getStudentModel(context, studentModelID, schoolclass).map(value -> {
			DomStudentModelContext4Student result = new DomStudentModelContext4Student();
			result.setFilter(filter);
			result.setId(value.getId());
			result.setModelStructure(value.getModelStructure());
			result.setSchoolClass(schoolclass);
			return result;
		});
		final Promise<DomStudentModelDataScore> s = 
				leerdoelScore ?	models.getStudentModelDataScore(context, studentModelID) : Promises.failed(new Error()) ;
		Promises.all(p,m).then(xxx -> {
			initialize(m,p,s);
			return xxx;
		}, FAILURE);
	}

	void setContext(OpdrNavIF root) {
		DomHasRole role = new DomHasRole();
		context.setDomHasRole(role);
		String learnerId = root.getLearnerId();
		if (learnerId.startsWith("1-")) {
			String[] split = learnerId.split("-");
			String u = ";" + split[1];
			String sg = ";" + split[2]; // uitzoeken		
			role.setUserId(new PersistenceId("MYSQL;" + PersistenceClassType.PersistentUser + u));
			role.setSchoolGroupId(new PersistenceId("MYSQL;" + PersistenceClassType.PersistentSchoolGroup + sg));
			role.setId(new PersistenceId("MYSQL;"+ PersistenceClassType.PersistentHasRole + u + sg));	
		}
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
	     leerdoelPopup = true;
	     voorkennisKnop = false;
	     voorkennisMenu = false;
	     zoomKnoppen = false;
	     filterHeader = false;
	     leerdoelScore = false;
	    
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
	    this.leerdoelScore = leerdoelScore;
	    if (activeMethod != null)
	    	this.activeMethod = new DomMethod(new PersistenceId(activeMethod));
	    this.studentModelID = new DomStudentModelContextId(new PersistenceId(studentModelID));
		profile = new DomDwoProfileId();
		profile.setId(new PersistenceId(h.getString("dwoProfileID"))); // from launchdata
	    
	    

	
	}

	private void initialize(Promise<DomMethod> m, Promise<DomStudentModelContext4Student> p, Promise<DomStudentModelDataScore> s) {
		activeMethod = m.getValue();
		DomStudentModelContext4Student item = p.getValue();
		item.setFilter(filter);
		item.getModelStructure().setActiveMethod(activeMethod.getId());
		graph.setModelScore(item, s, activeMethod);
	    graph.doFilter(filter);
	    if (!leerdoelScore) graph.zoomFit();
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
		String auth = root.getContext().getString("Authorization");
		builder.setHeader("Authorization", auth);
		RestAuthenticator.instance.setAuthorization(auth);
		return true;
	}

	@Override
	public void onSwitchViewEvent(SwitchViewEvent event) {
		switch(event.getEventValue()) {
		case GOTO_URL:
			String message = event.getSearch().get("message").substring(5);
			CBookEvent cbe = new CBookEvent(this, "gotoPlace", "#" + message);
			root.fireEvent(cbe);
			break;
		default:
		}
		
	}
}