package nl.numworx.geodefinergwt.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import nl.numworx.geodefiner.common.CheckObjectList;
import nl.numworx.geodefiner.common.GeoTriangle;
import nl.numworx.geodefiner.common.Instance;
import nl.numworx.geodefiner.common.NamingModel;
import nl.numworx.geodefiner.common.Tools;
import nl.numworx.geodefiner.common.UIModel;
import nl.numworx.geodefiner.common.UIShim;
import nl.numworx.geodefiner.common.locus.Builder;
import nl.numworx.geodefinergwt.client.i18n.MessagesImpl;
import nl.numworx.geodefinergwt.client.i18n.messages;
import nl.numworx.geodefinergwt.client.module.Components;
import nl.numworx.geodefinergwt.client.module.DaggerComponents;
import nl.numworx.geodefinergwt.client.toolbox.RadioMode;
import nl.numworx.geodefinergwt.client.ui.UIModelFactoryGWT;
import nl.numworx.geodefinergwt.client.ui.UserConfig;
import nl.tue.win.riaca.openmath.lang.OMBinding;
import nl.tue.win.riaca.openmath.lang.OMObject;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import dagger.Lazy;
import fi.euclides.event.SelectHandler;
import fi.euclides.formuleobjects.Lambda;
import fi.euclides.gwt.PrettyFormat;
import fi.euclides.math.IntegerFactory;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Locus;
import fi.euclides.model.Pair;
import fi.euclides.model.Punt;
import fi.euclides.model.math.DoubleFormat;
import fi.euclides.model.math.Numbers;
import fi.euclides.proof.Const;
import fi.euclides.proof.LabelValue;
import fi.euclides.util.Messages;
import fi.euclides.util.Observable;
import fi.euclides.util.Observer;
import fi.wiskopdr.VariableCollection;

public class GeoDefinerGWT extends Instance implements EntryPoint, InteractionStub, CBookEventListener, RequiresResize /*, Observer */{

	public static final messages MESSAGES = GWT.create(messages.class);
	private static final String LOG_OPTION = "logOption";
	private static final String GOED_CSS = "goed";
	private static final String FOUT_CSS = "fout";
	private static final String HALF_CSS = "half";
	private int width = 700, orgWidth;
	private int height = 650, orgHeight;
	private OpdrNavIF comRoot;
	//private final static Logger LOG = Logger.getLogger("GeoDefinerGWT");
	
	private void lognagekeken() {
		logger.warning("nagekeken = " + isNagekeken() + ", score = " + score + ", feedback = " + getStatus() + ", err = " + getErrorCount());
	}
	
	static class MyDockLayoutPanel extends DockLayoutPanel {

		MyDockLayoutPanel(Unit unit) {
			super(unit);
		}
		
		static boolean isHidden(Widget w) {
			LayoutData data = (LayoutData) w.getLayoutData();
			return data.hidden;
		}
	}
	
	private boolean southPanelVisible() {
		return ! MyDockLayoutPanel.isHidden(southPanel);
	}
	
	@Override
	public int getConstantHeight() {
		return toolbox.getHeight() 		// 38px (wel of niet?)
			   + (southPanelVisible() ? root.getWidgetSize(southPanel).intValue() : 0)
		       ;//+ southPanel.getOffsetHeight(); 	// 30px
	}


	private boolean volledigeBreedte;
	interface MyUiBinder extends UiBinder<DockLayoutPanel, GeoDefinerGWT> {}
	static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private static final String CHECK = "check";
	private static final String ACTION_RESET = "action.reset";

	@UiField DockLayoutPanel southPanel;
	@UiField Label status;
	DockLayoutPanel  root;
	@UiField CanvasViewer widget;
	@UiField FlowPanel check;
	@UiField Button checkBtn;
	@UiField ToolBoxPanel toolbox;
	@UiField(provided=true) messages rb = MESSAGES;
	private int mode;
	private LessonMode lessonMode;
	
	static {
		Numbers.setFactory(IntegerFactory.INSTANCE);
		Messages.setInstance(new MessagesImpl(MESSAGES));
	}
	
	
	public GeoDefinerGWT() {}
	
	public GeoDefinerGWT(HashMap<String, Object> h,
			HashMap<String, Number> randomVarWaarden, int volleBreedte) {

		root = uiBinder.createAndBindUi(this);
		
		ObjectMap map = JSONUtilities.wrapMap(h);
		
		if(map != null)
		{
			if(map.containsKey("breedte"))
				width = map.getInt("breedte");
			if(map.containsKey("hoogte"))
				height = map.getInt("hoogte");
			if(map.containsKey("volledigeBreedte"))
				volledigeBreedte = map.getBoolean("volledigeBreedte");
		}
		
		if(volledigeBreedte)
			width = volleBreedte;
		Map<String,Object> launchState = Collections.emptyMap();
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
				
		//alle gegevens uit launchData halen: 
		init(width, height, launchState, randomVarWaarden);

	}

	@Override
	protected void installToolbox() {
	    super.installToolbox();
		if( launchData.containsKey("toolbox")) {
			ObjectList list = launchData.getObjectList("toolbox");
			if(list.size() > 0) {
				toolbox.init(list, launchData.getObjectList("toolboxConfig"), width, buttons.get(), shims.get(), model.get());
				root.setWidgetSize(toolbox, toolbox.getHeight());
				root.setWidgetHidden(southPanel, false);
				if(checkDWO == null) {
					southPanel.setWidgetHidden(check, true);
				}
				selectSelector();
				return;
			}
		}
		root.setWidgetHidden(toolbox, true);
	}

	private void selectSelector() {
		selector.command();
		toolbox.selectSelector();
	}

	
	
	
  @Override
  protected void install(ObjectMap configuration) {
	if (configuration == null) {
	  configuration = JSONUtilities.wrapMap(Collections.emptyMap());
	}
    UIModelFactoryGWT.setConfiguration(configuration); // for toMap();
    super.install(configuration);
  }

  @Override
	public void onModuleLoad() {
        Element body = Document.get().getBody();
        body.setAttribute("oncontextmenu", "return false;");
		
		root = uiBinder.createAndBindUi(this);		
		RootLayoutPanel.get().add(root);
		volledigeBreedte = true;
		Stub.publish(this);
	}


	public HashMap<String, Object> getState() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		logger.info("voor getState ");
		fetchScore();
		if (mode == OpdrNavIF.EINDTOETS||mode == OpdrNavIF.ZELFTOETS)
		{
			if (mode == OpdrNavIF.EINDTOETS) {
				if (!isNagekeken()) {
					setAttempt();
				}
				setNagekeken(true);
			}
		} else 
			if (checkDWO != null && checkDWO.isCheck())
				super.setNagekeken(true); // ommiddelijke feedback bij setState(map)
		super.getState(hashMap);
		if (volledigeBreedte) {
			hashMap.put("width", Numbers.sub(widget.clipRight() , widget.clipLeft()).intValue());
			hashMap.put("height", Numbers.sub(widget.clipBottom(), widget.clipTop()).intValue());
		}
		getLogState(hashMap);
		logger.info("getState " + hashMap);
		return hashMap;
	}


	
	private void getLogState(Map<String, Object> hashMap) {
      Tracer t = tracker.adapt(Tracer.class);
      if (t != null) {
        t.getState(hashMap);
      }
  }

  public void setState(HashMap<String, Object> h) {
        widget.cancel();
		Map<String,Object> map = h;
		viewer.getModel().addObserver(UserConfig.INSTANCE);
		//LOG.severe("O before " + viewer.getModel().getO().getXd());
		setState(map);
		//LOG.severe("O after " + viewer.getModel().getO().getXd());
		boolean nagekeken = this.isNagekeken();
		if (state.containsKey("height") && state.containsKey("width")) {
			int oldw = Numbers.sub(widget.clipRight() , widget.clipLeft()).intValue();
			int oldh = Numbers.sub(widget.clipBottom(), widget.clipTop()).intValue();
			int width = state.getInt("width");
			int height = state.getInt("height");
			if (width != oldw || height != oldh ) {
				LOG.severe("should relocate from " + width + " to " + oldw);
				widget.init(width, height);
				boolean oldsema = sema;
				try {
					sema = true; // relocating..
					relocate(oldw, oldh); // trashes nagekeken
				} finally {
					sema = oldsema;
				}
			}
		}
		setLogState(map);
		lognagekeken();
		observeNewItems(UserConfig.INSTANCE, new CheckObjectList.CheckVisitor(checkObjects, viewer.getModel()));
		lognagekeken();
		restoreNagekeken(nagekeken);
		lognagekeken();
		start();
		addFireUpdates();
		lognagekeken();
		widget.paint();
	}

private void restoreNagekeken(boolean nagekeken) {
	if(nagekeken) {
		LOG.warning("set feedback in setstate");
		super.setNagekeken(true);
		//if(mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN) 
		fetchScore();
		// wanneer feedback:
		if ( mode == OpdrNavIF.OEFENEN
		  || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN
		  || mode == OpdrNavIF.ZELFTOETS
		  || (mode == OpdrNavIF.EINDTOETS && lessonMode != LessonMode.normal)
		)
		feedback();
	}
}

	private void setLogState(Map<String, Object> map) {
      Tracer t = tracker.adapt(Tracer.class);
      if (t != null) {
        t.setState(map);
      }
    
  }

  public int[][] getScoreObjectives() {
		return null;
	}


	public Boolean isCorrect() {
		return getStatus();
	}

	
	public void kijkNa() {
		update(null, "changed");
		feedback();
		setAttempt();
		setNagekeken(true);logger.info("KijkNA");
		incErrorCount();
		fire();
		lognagekeken();
		selectSelector();
	}

	@UiHandler("checkBtn") void kijkNa(ClickEvent evt) { kijkNa(); }
	
	private void feedback() {
		Boolean status = getStatus();
		LOG.warning("set feedback " + status);
		check.setStyleName(HALF_CSS, status == null);
		check.setStyleName(FOUT_CSS, Boolean.FALSE.equals(status));
		check.setStyleName(GOED_CSS, Boolean.TRUE.equals(status));
		if(checkObjects != null)
			checkObjects.feedback();
	}
	
	private void nofeedback() {
		setNagekeken(false);
	}

  private void fire() {
    Boolean status = getStatus();
    if (Boolean.TRUE.equals(status))
      fire("action.correct");
    else if (Boolean.FALSE.equals(status)) {
      if (getErrorCount() > 1) {
        fire("action.false_2");
      } else {
        fire("action.false");
      }
    }
  }

  private void fire(String action) {
    comRoot.fireEvent(new CBookEvent(action));
  }

	public void setAttempt(Map<String, ?> parameters) {
		if (attempt && comRoot != null) {
			comRoot.fireEvent(new CBookEvent(this, LOG_OPTION, parameters));
			logger.info(parameters.toString());
		}
	}

	public void setAttempt() {
		if (attempt) {
// Build parameters voor logging: zie FormuleEditorWithAnswer.buildLoggingMap
			Map<String,Object> parameters = new HashMap<>();
			parameters.put("verb", "http://adlnet.gov/expapi/verbs/attempted"); // standaard voor "poging"
			if (isCorrect()!= null) parameters.put("success", isCorrect());
			parameters.put("score", Collections.singletonMap("raw", getScore()));
			
			//parameters.put("response", "???"); 
			setAttempt(parameters);
		}
	}

  
  private void nofeedbackImpl() {
	  if (!sema) LOG.fine("remove feedback");
	  else return; // XXX dit moet je testen!!!
		check.removeStyleName(HALF_CSS);
		check.removeStyleName(FOUT_CSS);
		check.removeStyleName(GOED_CSS);
		if(checkObjects != null)
			checkObjects.removeFeedback();
	}

	public void zetNagekeken(boolean b) {
		setNagekeken(b);
	}

	public void setNagekeken(boolean b) {
		super.setNagekeken(b);
		if( !isNagekeken())
			nofeedbackImpl();
		else 
			if (mode == OpdrNavIF.ZELFTOETS) { fetchScore(); feedback(); }
	}

	private void addFireUpdates() {
		for (Destroyable d: viewer.getModel().getLijnen()) {
			if(d instanceof fi.euclides.model.Label) {
				final fi.euclides.model.Label label = (fi.euclides.model.Label) d;
				final String name = viewer.getMapper().toString(label);
				final String command = "double." + name;
				if (label.getRegistered() instanceof LabelValue &&
					! name.startsWith("%")	&&
					comRoot.hasListeners(command)) {
					label.addObserver(new Observer() {

						@Override
						public void update(Observable observable, Object arg) {
							if(arg == null)
							{	
								double value = label.value.doubleValue();
								if (Double.isFinite(value)) {
								  Map<String,Object> map = new TreeMap<String,Object>();
                                  map.put("value", value);
                                  map.put("name", name);
                                  comRoot.fireEvent(new CBookEvent(GeoDefinerGWT.this, command, map));
								}
							}
						}});
				}
			
			
			}
		}
	}
	
	
	
	
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		widget.setBackground(comRoot.getBackground().toString());
		//comRoot.addCBookEventListener("double", this);
		for (Destroyable d: viewer.getModel().getLijnen()) {
			if(d instanceof fi.euclides.model.Label) {
				fi.euclides.model.Label label = (fi.euclides.model.Label) d;
				if(label.getSubKey() == Const.TYPE) {
					String name = viewer.getMapper().toString(label);
					comRoot.addCBookEventListener("double." + name, this);
				} else {
                  boolean isExpression =
                      label.getSubKey() == Lambda.TYPE && label.adapt(OMObject.class) instanceof OMBinding;
                  if (isExpression) {
                    String name = viewer.getMapper().toString(label);
                    comRoot.addCBookEventListener("expression." + name, this);
                  }
				}
			}
		}
		
		FormuleHolder.installKeyboard(comRoot.getKeyboard());
		
		comRoot.addCBookEventListener(CHECK, this);
		comRoot.addCBookEventListener(ACTION_RESET, this);
		this.mode = comRoot.getMode();
		this.lessonMode = comRoot.getLessonMode();
		toetsStyle();
		widget.init(width, height-getConstantHeight());
	}

	private void toetsStyle() {
		if (lessonMode != LessonMode.review) {
			if (mode == OpdrNavIF.EINDTOETS || mode == OpdrNavIF.ZELFTOETS) {
				checkBtn.addStyleName("extern"); // no Kijkna knop.
			}			
		}
	}


	public void zetVolledigeBreedte(int breedte) {
	}

	public Widget asWidget() {
		return root;
	}

	public int getAsHoogte() {
		return 0;
	}


	
	public int getHeight() {
		return height;
	}


	
	public int getWidth() {
		return width;
	}


	
	public void setAsHoogte(int ashoogte) {
	}
		
	@Inject void setUiModelFactory(UIModelFactoryGWT f) {
		uiModelFactory = f;
	}

	private TrackerImpl tracker;
	@Inject void setViewer(TrackerImpl viewer) {
		this.viewer = viewer;
		this.tracker = viewer;
	}
	
	@Inject void setDefinitions(DefinitionsGWT definitions) {
		this.definitions = definitions;
	}
	
	@Inject void setNamingModel(NamingModel m) {
		widget.setMapper(m);
	}

	@Inject void setCheckObjects(CheckObjectList c) {
		checkObjects = c;
	}

	@Inject void setRandomizer(GWTRandomizer r) {
		random = r;
	}
	
	@Inject Lazy<Map<Integer,Provider<ToggleButton>>> buttons;
	@Inject Lazy<Map<Integer,Provider<UIShim<? extends Destroyable, Void>>>> shims;
	@Inject Lazy<RadioMode> model;
    @Inject Lazy<Tracer> tracerProvider;
	private boolean logOption, attempt;

	@Inject void setExpressions(@Named("expressions") Map<String,String> map) {
	    expressions = map;
	}
	
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		DoubleFormat.setInstance(new PrettyFormat());
		Locus.BUILDER = new Builder();
		widget.init(width, height);
		orgWidth = this.width = width;
		orgHeight = this.height = height;
		Components c = DaggerComponents.builder()
				.status(status)
				.widget(widget)
				.instance(this)
				.build();
		c.provideComponent(this);

		SelectHandler h = selector;
		h.setTracker(tracker);
		tracker.setPointerHandler(h);
		tracker.setStatus("");
		
		root.setPixelSize(width, height);
// initial model		
		createModel(tracker.getModel(), width, height);
// random variables
		String random = (String) launchData.get("random");
		values = launchRandomVars(random, values);
		logOption = Boolean.TRUE.equals(launchData.get("logOption"));
		if (logOption) {
		  tracker.setTracer(tracerProvider);
		}
		attempt = logOption || launchData.containsKey("smObjectives");

		// if launchdata contains Tools.GEO_TRIANGLE: 		
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		installGeoTriangle(map);
		
		setLaunchData(launchData, values);
        definitions.readonly = viewer.getModel().getIndex(); // readonly moet gezet na init definitions, niet idempotent, na of voor setState
// highlighter after init launchdata.
        widget.enableHighLight(selector);
		widget.init(width, height-getConstantHeight());
		tracker.paint();
		if (volledigeBreedte) toolbox.setResizer(this);
	}

	private void installGeoTriangle(ObjectMap launchData) {
		if (launchData.containsKey("toolbox")) {
			Collection<Integer> tools = launchData.getIntegerList("toolbox");
			if (tools.contains(Tools.GEO_TRIANGLE)) {
				GeoTriangle triangle = new GWTTriangle(viewer);
				triangle.setVisible(false);
				viewer.getMapper().rename(triangle, GeoTriangle.NAME);
				viewer.getModel().add(triangle);						
			}
		}
	}


	private Map<String, Number> launchRandomVars(String random,
			Map<String, Number> values) {
		if(random == null || random.isEmpty())
			return values;
		VariableCollection vc = new VariableCollection();
		if (vc.setVariables(random)) {
			Map<String, Number> vars = vc.getRandomValues();
			vars.putAll(values);
			values = vars;
		}
		return values;
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if(CHECK.equals(event.getCommand()) && checkDWO != null)
		{
			kijkNa();
			return;
		}
		if(event.getCommand().startsWith("double.")) {
			int dot = event.getCommand().indexOf('.');
			String name = event.getCommand().substring(dot+1);
			Number number = (Number)event.getParameter("value");
			String message = event.getMessage();
			Numbers value;
			if(number == null) {
				number = Double.valueOf(message);
				value = Numbers.createDouble(number.doubleValue());
			} else {
				value = Numbers.createDouble(number.doubleValue());
			}
			message = Numbers.toString(value);
			fi.euclides.model.Label label = (fi.euclides.model.Label) viewer.getMapper().fromString(name);
			if(label.getSubKey() == Const.TYPE) { 
				label.setString(message);
				label.setValue(value);
			}
			viewer.paint();
			return;
		}
        if (event.getCommand().startsWith("expression.") ) {
          int dot = event.getCommand().indexOf('.');
          String name = event.getCommand().substring(dot+1);
          String expr = event.getMessage();
          if (!expr.startsWith("$f")) // missing from simpel formule vak.
            expr = "$f" + expr + "@";
          acceptExpressionEvent(name, expr);
          viewer.paint();
          return;
        }
        if (ACTION_RESET.equals(event.getCommand())) {
        	reset();
        	return;
        }
	}

	/* (non-Javadoc)
	 * @see nl.numworx.geodefiner.common.Instance#update(fi.euclides.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object arg) {
		logger.info("update " + arg);
		super.update(observable, arg);
		lognagekeken();
		if("changed".equals(arg) && comRoot != null) {
			comRoot.setChanged(Boolean.FALSE.equals(getStatus()));
		} else if (observable == viewer.getModel()) {
			nofeedback();
		}
		if ( viewer.getModel() == observable) {
			nofeedback();
		}
	}

	@Override
	protected boolean installCheckDWO() {
		if (super.installCheckDWO())
		{
			boolean visible = checkDWO.isExtern();
			checkBtn.setStyleName("extern", visible);
			nofeedback();
			return true;
		} else {
			checkBtn.addStyleName("extern");
		}
		root.setWidgetHidden(southPanel, true);
		return false;
	}



	private void reset0(int width, int height) {
		toolbox.destroy();
		if(checkObjects != null) checkObjects.destroyAll();
		definitions.clear();
		expressions.clear();
		createModel(viewer.getModel(), width, height);
		installGeoTriangle(launchData);
		installLaunchData();
		toetsStyle();
		widget.init(width, height-getConstantHeight());
		start();
	}
	
	protected void reset() {
		if (width == orgWidth && height == orgHeight) 
		{
			reset0(width, height);
		} else {
			int header = getConstantHeight();
			reset0(orgWidth, orgHeight);
			relocate(width, height-header); //terug naar huidige grootte
		}

		fire(ACTION_RESET);
	}
	
	

	@Override
	public void start() {
		super.start();
		if(checkObjects != null)
			checkObjects.start();
		viewer.getModel().addObserver(UserConfig.INSTANCE);
	}

	Logger LOG = Logger.getLogger(getClass().getName());
	boolean sema;
	@Override
	public void onResize() {
		if (sema) return;
		sema = true;
		try {
			int w = Window.getClientWidth();
			int h = Window.getClientHeight();
			if (w != width || h != height) {
				LOG.severe(width + " need resize for " + w + "=" + width + ", " + h + "=" + height);
				boolean nagekeken = isNagekeken();
				width  = w;
				height = h;
				root.setPixelSize(w, h);
				h -= toolbox.getOffsetHeight(); // 38px (wel of niet?)
				h -= southPanel.getOffsetHeight(); // 30px
//				widget.init(w, h);
//				widget.getModel().getO().forceChanged();
				relocate(w,h);
				restoreNagekeken(nagekeken);
				widget.paint();
			}
		} finally {
			sema = false;
		}
	}

	protected void relocate(int rw, int rh) {
		widget.getModel().executeDelay();
		Punt o = widget.getModel().getO();
		Numbers right = widget.clipRight();
		Numbers left  = widget.clipLeft();
		Numbers width = Numbers.sub(right, left);
		Numbers Ox = o.getX();
		Numbers nOx = nX(rw, left, width, Ox);
		
		Numbers top = widget.clipTop();
		Numbers bottom = widget.clipBottom();
		Numbers height = Numbers.sub(bottom, top);
		Numbers Oy = o.getY();
		Numbers nOy = nY(rh, top, height, Oy);
		Punt u = widget.getModel().getU();
		List<Punt> p = widget.getModel().getPunten();
		p = p.subList(2, p.size());
		List<Punt> p2 = widget.getModel().getLijnen().stream()
				.filter(d -> d instanceof fi.euclides.model.Label)
				.map( d -> {
					fi.euclides.model.Label l = (fi.euclides.model.Label) d;
					return l.getP();
				}).collect(Collectors.toList());		
		List<Pair<Numbers, Numbers>> save = new ArrayList<>(p.size());
		p.forEach(n -> save.add(new Pair<>(n.getX(), n.getY())));
		p2.forEach(n ->save.add(new Pair<>(n.getX(), n.getY())));
		Numbers dx = Numbers.sub(u.getX(),Ox);
		Numbers ndx = Numbers.div(Numbers.mul(dx, Numbers.createInteger(rw)), width);
		widget.init(rw, rh); // ipv setPixelSize
		if (Ox.equals(nOx) && Oy.equals(nOy)) o.forceChanged(); // force changed
		else o.setXY(nOx, nOy);
		u.setXY(Numbers.add(ndx, nOx), nOy);
// FIXME dit is niet goed, omdat het coordinatensysteem vierkant is en niet rechthoekig. Helaas.
//		O gaat van Ox,Oy naar nOx, nOy
//		U gaat van Ox+dx, Oy naar nOx+ndx, nOy
		
		Iterator<Punt> i = p.iterator();
		Iterator<Pair<Numbers, Numbers>> pairs = save.iterator();
		while (i.hasNext() && pairs.hasNext()) {
			Punt punt = i.next();
			Pair<Numbers, Numbers> pair = pairs.next();
			//punt.moveTo(nX(rw, left, width, pair.getA()), nY(rh, top, height, pair.getB()));
			Numbers a = pair.getA();
			Numbers na = nC(Ox, nOx, dx, ndx, a);
			Numbers b = pair.getB();
			Numbers nb = nC(Oy, nOy, dx, ndx, b);
			punt.moveTo(na, nb);
			
		}
		i = p2.iterator();
		while (i.hasNext() && pairs.hasNext()) {
			Punt punt = i.next();
			Pair<Numbers, Numbers> pair = pairs.next();
			//punt.setXY(nX(rw, left, width, pair.getA()), nY(rh, top, height, pair.getB()));
			//if (!pairs.hasNext()) LOG.info("move to " + Oy  + " " + pair.getB() + " to " + nOy + " " + punt.getY());
			Numbers a = pair.getA();
			Numbers na = nC(Ox, nOx, dx, ndx, a);
			Numbers b = pair.getB();
			Numbers nb = nC(Oy, nOy, dx, ndx, b);
			punt.setXY(na, nb);

		}
		
	}

	private Numbers nC(Numbers Ox, Numbers nOx, Numbers dx, Numbers ndx, Numbers a) {
		Numbers cx = Numbers.div(Numbers.sub(a, Ox),dx);
		Numbers na = Numbers.add(nOx, Numbers.mul(cx, ndx));
		return na;
	}

	protected Numbers nY(int rh, Numbers top, Numbers height, Numbers Oy) {
		return Numbers.div(Numbers.mul(Numbers.sub(Oy, top), Numbers.createInteger(rh)), height);
	}

	protected Numbers nX(int rw, Numbers left, Numbers width, Numbers Ox) {
		return Numbers.div(Numbers.mul(Numbers.sub(Ox, left), Numbers.createInteger(rw)), width);
	}

	@Override
	protected void installCheckObjects() {
		super.installCheckObjects();
		boolean feedback = checkObjects.isFeedback();
		widget.setFeedback(feedback);
	}
	
	
}
