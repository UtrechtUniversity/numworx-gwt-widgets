package nl.numworx.geodefinergwt.client;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import nl.numworx.geodefiner.common.CheckObjectList;
import nl.numworx.geodefiner.common.Definitions;
import nl.numworx.geodefiner.common.Instance;
import nl.numworx.geodefiner.common.NamingModel;
import nl.numworx.geodefiner.common.locus.Builder;
import nl.numworx.geodefinergwt.client.i18n.MessagesImpl;
import nl.numworx.geodefinergwt.client.i18n.messages;
import nl.numworx.geodefinergwt.client.module.Components;
import nl.numworx.geodefinergwt.client.module.DaggerComponents;
import nl.numworx.geodefinergwt.client.ui.UIModelFactoryGWT;
import nl.numworx.geodefinergwt.client.ui.UserConfig;
import nl.tue.win.riaca.openmath.lang.OMBinding;
import nl.tue.win.riaca.openmath.lang.OMObject;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import dagger.Lazy;
import fi.euclides.event.SelectHandler;
import fi.euclides.formuleobjects.Lambda;
import fi.euclides.formuleobjects.ParseException;
import fi.euclides.gwt.PrettyFormat;
import fi.euclides.math.IntegerFactory;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Locus;
import fi.euclides.model.math.DoubleFormat;
import fi.euclides.model.math.Numbers;
import fi.euclides.proof.Const;
import fi.euclides.proof.LabelValue;
import fi.euclides.util.Messages;
import fi.euclides.util.Observable;
import fi.euclides.util.Observer;
import fi.wiskopdr.VariableCollection;

public class GeoDefinerGWT extends Instance implements EntryPoint, InteractionStub, CBookEventListener, Observer {

	public static final messages MESSAGES = GWT.create(messages.class);
	private static final String GOED_CSS = "goed";
	private static final String FOUT_CSS = "fout";
	private static final String HALF_CSS = "half";
	private int width = 500;
	private int height = 450;
	private OpdrNavIF comRoot;
	//private final static Logger LOG = Logger.getLogger("GeoDefinerGWT");
	
	private void lognagekeken() {
		logger.info("nagekeken = " + isNagekeken() + ", score = " + score + ", feedback = " + getStatus() + ", err = " + getErrorCount());
	}
	
	
	private boolean volledigeBreedte;
	interface MyUiBinder extends UiBinder<DockLayoutPanel, GeoDefinerGWT> {}
	static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private static final String CHECK = "check";

	@UiField DockLayoutPanel southPanel;
	@UiField Label status;
	DockLayoutPanel  root;
	@UiField CanvasViewer widget;
	@UiField FlowPanel check;
	@UiField Button checkBtn;
	@UiField ToolBoxPanel toolbox;
	@UiField(provided=true) messages rb = MESSAGES;
	private int mode;
	
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
				toolbox.init(list, width, buttons.get());
				root.setWidgetSize(toolbox, toolbox.getHeight());
				root.setWidgetHidden(southPanel, false);
				if(checkDWO == null) {
					southPanel.setWidgetHidden(check, true);
				}
				return;
			}
		}
		root.setWidgetHidden(toolbox, true);
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
		
		root = uiBinder.createAndBindUi(this);		
		RootLayoutPanel.get().add(root);
		Stub.publish(this);
	}


	public HashMap<String, Object> getState() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		logger.info("voor getState ");
		lognagekeken();
		super.getState(hashMap);
		lognagekeken();
		logger.info("getState " + hashMap);
		return hashMap;
	}


	
	public void setState(HashMap<String, Object> h) {
		Map<String,Object> map = h;
		viewer.getModel().addObserver(UserConfig.INSTANCE);
		setState(map);
		observeNewItems(UserConfig.INSTANCE, new CheckObjectList.CheckVisitor(checkObjects, viewer.getModel()));
		lognagekeken();
		if(isNagekeken()) {
			//if(mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN) 
			fetchScore();
			feedback();
		}
		lognagekeken();
		start();
		addFireUpdates();
		lognagekeken();
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
		setNagekeken(true);logger.info("KijkNA");
		incErrorCount();
		fire();
		lognagekeken();
	}

	@UiHandler("checkBtn") void kijkNa(ClickEvent evt) { kijkNa(); }
	
	private void feedback() {
		Boolean status = getStatus();
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

  private void nofeedbackImpl() {
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
	}

	private void addFireUpdates() {
		for (Destroyable d: viewer.getModel().getLijnen()) {
			if(d instanceof fi.euclides.model.Label) {
				final fi.euclides.model.Label label = (fi.euclides.model.Label) d;
				final String name = viewer.getMapper().toString(label);
				final String command = "double." + name;
				if (label.getRegistered() instanceof LabelValue &&
						
					comRoot.hasListeners(command)) {
					label.addObserver(new Observer() {

						@Override
						public void update(Observable observable, Object arg) {
							if(arg == null)
							{	Map<String,Object> map = new TreeMap<String,Object>();
								map.put("value", label.value.doubleValue());
								map.put("name", name);
								comRoot.fireEvent(new CBookEvent(GeoDefinerGWT.this, command, map));							}
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
		this.mode = comRoot.getMode();
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

	@Inject void setExpressions(@Named("expressions") Map<String,String> map) {
	    expressions = map;
	}
	
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		DoubleFormat.setInstance(new PrettyFormat());
		Locus.BUILDER = new Builder();
		widget.init(width, height);
		this.width = width;
		this.height = height;
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
// configuration
		setLaunchData(launchData, values);
        definitions.readonly = viewer.getModel().getIndex(); // readonly moet gezet na init definitions, niet idempotent, na of voor setState
// highlighter after init launchdata.
        widget.enableHighLight(selector);
		tracker.paint();
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



	public void reset() {
		toolbox.destroy();
		if(checkObjects != null) checkObjects.destroyAll();
		definitions.clear();
		expressions.clear();
		createModel(viewer.getModel(), width, height);
		installLaunchData();
		start();
	}

	@Override
	public void start() {
		super.start();
		if(checkObjects != null)
			checkObjects.start();
		viewer.getModel().addObserver(UserConfig.INSTANCE);
	}
	
}
