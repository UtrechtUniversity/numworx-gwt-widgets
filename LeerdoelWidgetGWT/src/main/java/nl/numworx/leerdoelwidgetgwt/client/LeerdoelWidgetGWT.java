package nl.numworx.leerdoelwidgetgwt.client;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.FailedPromisesException;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import fi.dwo.gwt.lib.rest.DwoConstants;
import fi.dwo.gwt.lib.rest.CallManagers.MethodManager;
import fi.dwo.gwt.lib.rest.CallManagers.OAuthManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentStudentModelManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.ui.IdleDetect;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import fi.dwo.gwt.lib.rest.util.Dwo2LocaleMessageGWTTranslator;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelService_Factory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter_Factory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.EastPanel;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.EastPanel_Factory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.XAPIService_Factory;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
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
public class LeerdoelWidgetGWT implements EntryPoint, InteractionStub, DispatcherFilter, SwitchViewEventHandler, CBookEventListener {

	enum Type {
		GRAPH,
		TREE,
		RECOMMENDER,
	}
	
	
	private static final Failure FAILURE = new Failure() {

		@Override
		public void fail(Promise<?> resolved) throws Exception {
			Throwable failure = resolved.getFailure();
			Collection<Promise<?>> list = Collections.singleton(resolved) ;
			
			if (failure instanceof FailedPromisesException) {
				list = ((FailedPromisesException) failure).getFailedPromises();
			}
			
			for (Promise<?> t: list)
			{
				if (t.getFailure()instanceof ReviewAPI.ReviewException) continue;
				java.util.logging.Logger.getGlobal().log(Level.WARNING, "failure " + t, t.getFailure());		
			}
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
	final DomContext context = new DomContext();

	
	LayoutPanel panel;
	private Map<String, Map<String, Set<Integer>>> filter;
	private DomMethod activeMethod;
	private DomDwoProfileId profile;
	private DomStudentModelContextId studentModelID;
	private DomStudentModelContext4Student studentModel;
	private boolean leerdoelScore;
	private boolean filterHeader;
	private boolean zoomKnoppen;
	private boolean voorkennisMenu;
	private boolean voorkennisKnop;
	private boolean leerdoelPopup;
	private boolean visible;
	private Type type;
	private EventBus evbus;
	
	@SuppressWarnings("unchecked")
	public LeerdoelWidgetGWT(HashMap<String, Object> h, HashMap<String, Number> randomVarWaarden, int volleBreedte) {
		this();
		ObjectMap map = JSONUtilities.wrapMap(h);
		width = map.getInt("breedte");
		wantedheight = map.getInt("hoogte");
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
		HashMap<String, Object> state = new HashMap<>();
		switch(type) {
		case RECOMMENDER:
			recommender.getState(state);
			header.getState(state);
		case TREE:
		case GRAPH:
		}
		return state;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		ObjectMap state = JSONUtilities.wrapMap(h);
		switch(type) {
		case RECOMMENDER:
			try {
				ready.resolveWith(recommender.extradiff());
			} catch (Exception e) {
			}
			ready.getPromise().onResolve( () -> {
				recommender.setState(state);
				header.setState(state);
			});
			break;
		case TREE:
		case GRAPH:
		}
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
	RoleAPI roleAPI;
	private LeerdoelTree tree;
	private LeerdoelPresenter presenter;
	private EastPanel east;
	LeerdoelRecommender recommender;
	private List<String> objectives;
	private int wantedheight;

	interface RoleAPI {
		//StudentResultsService getService();
		DescriptionService getDescriptionService();
		MethodManager getMethodManager();
		Promise<DomStudentModelContext> getStudentModel(DomContext context, DomStudentModelContextId studentModelID,
				DomSchoolClass schoolclass);
		Promise<DomStudentModelDataScore> getScore(DomStudentModelContext4Student studentModel);
		default Promise<DomMethod> getMethod(DomContext context, DomMethod id, DomDwoProfileId profile) {
			return getMethodManager().getMethod(context, id, profile);
		}
	}
	
	Deferred<Object> ready = new Deferred<>();

	class LearnerAPI implements RoleAPI {
		final StudentResultsService service;
		final SecuredStudentStudentModelManager models = new SecuredStudentStudentModelManager();
		final MethodManager methods;
	
		public LearnerAPI(DwoGlobalVars vars) {
			methods = MethodManager.student();
			service = XAPIService_Factory.newInstance(models, vars, methods, context);
		}

		public DescriptionService getDescriptionService() {
			return service;
		}
		
		public MethodManager getMethodManager() {
			return methods;
		}
		public Promise<DomStudentModelContext> getStudentModel(DomContext context, DomStudentModelContextId id, DomSchoolClass schoolclass) {
			return models.getStudentModel(context, id, schoolclass);
		}

		@Override
		public Promise<DomStudentModelDataScore> getScore(DomStudentModelContext4Student studentModel) {
			return service.getScore(studentModel).recoverWith(p -> emptyScore(studentModel));
		}
	}
	
	class TeacherAPI implements RoleAPI {
		final MethodManager methods = MethodManager.teacher();
		final StudentModelService service;

		TeacherAPI(DwoGlobalVars vars) {
			service = StudentModelService_Factory.newInstance(vars, context, methods);
		}

		@Override
		public DescriptionService getDescriptionService() {
			return service;
		}

		public MethodManager getMethodManager() {
			return methods;
		}

		@Override
		public Promise<DomStudentModelContext> getStudentModel(DomContext context,
				DomStudentModelContextId studentModelID, DomSchoolClass schoolclass) {
			return service.getStudentModel(studentModelID.getId());
		}

		@Override
		public Promise<DomStudentModelDataScore> getScore(DomStudentModelContext4Student studentModel) {
			return emptyScore(studentModel);
		}
	}
	
	boolean pasAanH = true;
	RecommenderHeader header;
	
	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.root = comRoot;
		Role role = comRoot.getRole();
		//if (role != Role.Learner) leerdoelScore = false;
		DomSchoolClass schoolclass = role == Role.Learner ? new DomSchoolClass() : null;
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
		
		comRoot.addCBookEventListener("action.setNotEditable", this);
		
		if (comRoot.getLessonMode() == LessonMode.review)
		{
			leerdoelScore = false;
			roleAPI = new ReviewAPI(vars);
		} else {
			roleAPI = role == Role.Learner ? new LearnerAPI(vars) : new TeacherAPI(vars);
		}
		DescriptionPresenter service;
		switch(type) {
		
		case GRAPH: // graph
			DescriptionPresenter description = new DescriptionPresenter(Optional.of(evbus), true, roleAPI.getDescriptionService());
			graph = new LeerdoelGraph(voorkennisKnop, zoomKnoppen, voorkennisMenu, leerdoelPopup, description, filterHeader);
			panel.add(graph);
			panel.setWidgetLeftRight(graph, 0, Unit.PX, 0, Unit.PX);
			panel.setWidgetTopBottom(graph, 0, Unit.PX, 0, Unit.PX);
			break;
		case TREE: //lijst
			Panel parent = new ScrollPanel();
			panel.add(parent);
			int right = Math.min(440, width/2);
			if (!leerdoelPopup) right = 0;
			panel.setWidgetLeftRight(parent, 0, Unit.PCT, right, Unit.PX);
			tree = new LeerdoelTree(parent, evbus);
			service = DescriptionPresenter_Factory.newInstance(Optional.of(evbus), false, roleAPI.getDescriptionService());
			east = EastPanel_Factory.newInstance(service);
			if (leerdoelPopup) {
				panel.add(east);
				panel.setWidgetRightWidth(east, 0, Unit.PCT, right, Unit.PX);
			}
			tree.enableScore(leerdoelScore);
			east.enableScore(leerdoelScore);
			presenter = new LeerdoelPresenter(evbus, vars);
			presenter.setView(tree);
			presenter.setEast(east);
			break;
		case RECOMMENDER:
			recommender = new LeerdoelRecommender(this);
			header = new RecommenderHeader();
			header.initialDown(visible);
			recommender.setComRoot(comRoot);
// font overerven, altijd aan
			ObjectMap instellingen = comRoot.getConfiguration();
			// fontSize, fontName, fgColor
			if (instellingen.containsKey("fontSize")) {
				panel.getElement().getStyle().setFontSize(instellingen.getInt("fontSize"), Unit.PX);
			}
			if (instellingen.containsKey("fontName")) {
				panel.getElement().getStyle().setProperty("fontFamily", instellingen.getString("fontName"));
			}
			if (instellingen.containsKey("fgColor")) {
				String fgcolor;
				Object o = instellingen.get("fgColor");
				if (o instanceof JSONString) {
					fgcolor = ((JSONString) o).stringValue();
				} else
				if (o instanceof String) {
					fgcolor = o.toString();
				} else {
					ObjectMap m = instellingen.getObjectMap("fgColor");
					if (m != null) {
						int red = m.getInt("red");
						int green = m.getInt("green");
						int blue = m.getInt("blue");
						fgcolor = CssColor.make(red, green, blue).value();
					} else 
						fgcolor = CssColor.make(0,0,0).value();
				}
				panel.getElement().getStyle().setColor(fgcolor);
			}

			panel.add(header);
			int margin = 10;
			panel.setWidgetTopHeight(header, margin, Unit.PX, header.getHeight(), Unit.PX);
			panel.setWidgetLeftRight(header, margin, Unit.PX, margin, Unit.PX);
			panel.add(recommender);
			panel.setWidgetTopBottom(recommender, header.getHeight() + margin*2, Unit.PX, margin, Unit.PX);
			panel.setWidgetLeftRight(recommender, margin, Unit.PX, margin, Unit.PX);
			panel.addStyleName("framed");
			panel.addStyleName("profile-borderBox");
			recommender.setObjectives(objectives);
			recommender.setService(roleAPI.getDescriptionService());
			recommender.enableScore(leerdoelScore);
			recommender.keyboard = root.getKeyboard();
			recommender.idler = new IdleDetect(evbus);
			recommender.width = width - 9;
			header.setCenter(recommender);
			
			header.addValueChangeHandler(ev -> { 
				HashMap<String,Number> parameters = new HashMap<>();
				parameters.put("width", width = panel.getOffsetWidth());
				parameters.put("height", wantedheight = height);
				if (!ev.getValue().booleanValue())
				{	parameters.put("height", wantedheight = header.getHeight() + margin*2);
				} else {
					if (false && pasAanH)
					AnimationScheduler.get().requestAnimationFrame
					
					((t) -> {
						Promise<Object> x = recommender.extradiff().then( qq ->  { int extra = qq.getValue(); 
						GWT.log("extra is = " + extra);
						if (pasAanH && extra != 0) {
							this.height += extra;
							parameters.put("height", wantedheight = height);
							comRoot.fireEvent(new CBookEvent(this, "resize", parameters));
							pasAanH = false;
						}
							return null; });
						try {
							ready.resolveWith(x);
						} catch (Exception e) {
						}
						
						
					});
					recommender.stackResize(); // big hack, deze werkt altijd.

				}
				comRoot.fireEvent(new CBookEvent(this, "resize", parameters));
			});
			int wanted = 220 + objectives.size() * 30; 
			if (height < wanted) {
				HashMap<String,Number> parameters = new HashMap<>();
				parameters.put("width", width);
				parameters.put("height", height = wanted);
				comRoot.fireEvent(new CBookEvent(this, "resize", parameters));			
			}	
			header.setDown(false);
			break;
		}
		
		Promise<DomMethod> m;
		if (activeMethod != null ) m = roleAPI.getMethod(context, activeMethod, profile);
		else {
			DomMethod value = new DomMethod();
			value.setMethod(DwoLocalesForGWT.instance.NUM_LBL_METHOD_NONE()); // FIXED i18n
			m = Promises.resolved(value);
		}
		Promise<DomStudentModelContext4Student> p;
		p = roleAPI.getStudentModel(context, studentModelID, schoolclass).map(value -> {
			DomStudentModelContext4Student result = new DomStudentModelContext4Student();
			result.setFilter(filter);
			result.setId(value.getId());
			result.setModelStructure(value.getModelStructure());
			result.setSchoolClass(schoolclass);
			return result;
		});
		Promises.all(p,m).then(xxx -> {
			initialize(m,p);
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
		return wantedheight;
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
		this.wantedheight = height;
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
	     type = Type.GRAPH;
	     objectives = Collections.emptyList();
	    
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
	      zoomKnoppen = h.getBoolean("zoomKnoppen"); // en dan ook niet slepen!
	    if(h.containsKey("filterHeader"))
	      filterHeader = h.getBoolean("filterHeader");
	    if(h.containsKey("leerdoelScore"))
	      leerdoelScore = h.getBoolean("leerdoelScore");
	    visible = h.getBoolean("visible", false);
	    if (h.containsKey("type"))
	    	type = Type.values()[h.getInt("type")];
	    if (h.containsKey("objectives"))
	    	objectives = h.getStringList("objectives");
	    
	    this.filter = convert(filter);
	    if (activeMethod != null)
	    	this.activeMethod = new DomMethod(new PersistenceId(activeMethod));
	    this.studentModelID = this.studentModel = new DomStudentModelContext4Student(new PersistenceId(studentModelID));
		profile = new DomDwoProfileId();
		profile.setId(new PersistenceId(h.getString("dwoProfileID"))); // from launchdata
	}

	private void initialize(Promise<DomMethod> m, Promise<DomStudentModelContext4Student> p) {
		activeMethod = m.getValue();
		studentModel = p.getValue();
		studentModel.setFilter(filter);
		studentModel.getModelStructure().setActiveMethod(activeMethod.getId());
// bij recommender(2): altijd score ophalen!
		final Promise<DomStudentModelDataScore> s = 
				leerdoelScore || type == Type.RECOMMENDER ? roleAPI.getScore(studentModel) : emptyScore(studentModel) ;

		switch(type) {
		case GRAPH:		
			graph.setModelScore(studentModel, s, activeMethod);
		    graph.doFilter(filter);
		    if (!leerdoelScore) graph.zoomFit();
		    break;
		case TREE:
			presenter.setModelScore(studentModel, s, activeMethod);
			break;
		case RECOMMENDER:
			recommender.setModelScore(studentModel, s, activeMethod);
			break;
		}
	}

	private Promise<DomStudentModelDataScore> emptyScore(DomStudentModelContext4Student studentModel) {
		DomStudentModelDataScore score = new DomStudentModelDataScore();
		score.setDomStudentModelStructureScore(studentModel.getModelStructure().generateStudentModelStructureScore());
		score.setModelId(studentModel);
		return Promises.resolved(score);
	}

	private Map<String, Map<String, Set<Integer>>> convert(ObjectMap filter) {
		if (filter == null) return null;
		Map<String, Map<String, Set<Integer>>> result = new HashMap<>();
		Set<String> keys = filter.keySet();
		for(String key: keys) {
			ObjectMap value = filter.getObjectMap(key); // else NPE
			if ("null".equals(key)) key = null; // BUG in keySet()?????
			result.put(key, convert2(value));
		}
		return result;
	}

	private Map<String, Set<Integer>> convert2(ObjectMap objectMap) {
		if (objectMap == null) return null;
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
			CBookEvent cbe = new CBookEvent(this, "gotoPlace", message);
			root.fireEvent(cbe);
			break;
		default:
		}
		
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) {
		// Alleen voor "action.setNotEditable"
		// TODO doe niks
	}

	@Override
	public int getConstantHeight() { // niet goed, alleen in uitgeklapt?
		if (type != Type.GRAPH) return wantedheight;
		return 0;
	}

	public void requestDelta(int extra) {
		if (extra != 0) {
			HashMap<String,Number> parameters = new HashMap<>();
			parameters.put("width", width = panel.getOffsetWidth());
			this.height += extra;
			parameters.put("height", wantedheight = height);
			root.fireEvent(new CBookEvent(this, "resize", parameters));
		}
	}
	
	

}