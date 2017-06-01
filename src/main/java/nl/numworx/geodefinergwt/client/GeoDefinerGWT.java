package nl.numworx.geodefinergwt.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.numworx.geodefiner.common.Definitions;
import nl.numworx.geodefiner.common.Instance;
import nl.numworx.geodefiner.common.NamingModel;
import nl.numworx.geodefiner.common.Randomizer;
import nl.numworx.geodefinergwt.client.i18n.MessagesImpl;
import nl.numworx.geodefinergwt.client.i18n.messages;
import nl.numworx.geodefinergwt.client.ui.HerleidList;
import nl.numworx.geodefinergwt.client.ui.UIModelFactory;
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
import com.google.gwt.user.client.ui.Widget;

import fi.euclides.event.EventHandler;
import fi.euclides.event.HitTester;
import fi.euclides.event.NameMapper;
import fi.euclides.event.SelectHandler;
import fi.euclides.event.Tracker;
import fi.euclides.gwt.PrettyFormat;
import fi.euclides.gwt.ViewerWidget;
import fi.euclides.math.IntegerFactory;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Model;
import fi.euclides.model.Track;
import fi.euclides.model.math.DoubleFormat;
import fi.euclides.model.math.Numbers;
import fi.euclides.proof.Const;
import fi.euclides.proof.LabelDelegate;
import fi.euclides.proof.LabelValue;
import fi.euclides.util.Messages;
import fi.euclides.util.Observable;
import fi.euclides.util.Observer;
import fi.wiskopdr.FormuleParser;

public class GeoDefinerGWT extends Instance implements EntryPoint, InteractionStub, CBookEventListener, Observer, Randomizer {

	private static final String GOED_CSS = "goed";
	private static final String FOUT_CSS = "fout";
	private static final String HALF_CSS = "half";
	private int width = 500;
	private int height = 450;
	private OpdrNavIF comRoot;
	private final static Logger LOG = Logger.getLogger("GeoDefinerGWT");
	
	private void lognagekeken() {
		LOG.info("nagekeken = " + nagekeken + ", score = " + score + ", feedback = " + getStatus());
	}
	
	
	private boolean volledigeBreedte;
	interface MyUiBinder extends UiBinder<DockLayoutPanel, GeoDefinerGWT> {}
	static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private static final String CHECK = "check";

	@UiField DockLayoutPanel southPanel;
	@UiField Label status;
	DockLayoutPanel  root;
	@UiField ViewerWidget widget;
	@UiField FlowPanel check;
	@UiField Button checkBtn;
	@UiField ToolBoxPanel toolbox;
	@UiField messages rb = GWT.create(messages.class);
	private int mode;
	
	/**
	 * Decorator pattern. Decorate with a NameMapper.
	 * @author wim
	 *
	 */
	class TrackerImpl implements Tracker 
	{
		Tracker viewer;
		NameMapper mapper;
		Map<String,LabelDelegate> register;
		
		TrackerImpl(Tracker viewer, NameMapper mapper) {
			super();
			this.viewer = viewer;
			this.mapper = mapper;
			this.register = new TreeMap<String,LabelDelegate>();
		}

		@Override
		public void setTrack(Track track) {
			viewer.setTrack(track);
		}

		@Override
		public void setPointerHandler(EventHandler eventHandler) {
			viewer.setPointerHandler(eventHandler);
		}

		@Override
		public void setStatus(String string) {
			status.setText(string);
		}

		@Override
		public Model getModel() {
			return viewer.getModel();
		}

		@Override
		public void paint() {
			viewer.paint();
		}

		@Override
		public boolean contains(double x, double y) {
			return viewer.contains(x, y);
		}

		@Override
		public String describe(Destroyable d) {
			return "";
		}

		@Override
		public NameMapper getMapper() {
			return mapper;
		}

		@Override
		public void register(String key, LabelDelegate delegate) {
			register.put(key, delegate);
		}

		@Override
		public LabelDelegate getRegistered(String key) {
			return register.get(key);
		}

		@Override
		public HitTester getHitTester() {
			return viewer.getHitTester();
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T adapt(Class<T> cls) {
			if(fi.euclides.openmath.Expression.class == cls) 
				return (T) expression;
			if(Randomizer.class == cls) 
				return (T) GeoDefinerGWT.this;
			return viewer.adapt(cls);
		}

		fi.euclides.openmath.Expression expression;
	}
	
	static {
		Numbers.setFactory(IntegerFactory.INSTANCE);
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
		if( launchData.containsKey("toolbox")) {
			ObjectList list = launchData.getObjectList("toolbox");
			if(list.size() > 0) {
				toolbox.init(list, viewer, this);
				return;
			}
		}
		root.setWidgetHidden(toolbox, true);
	}

	@Override
	public void onModuleLoad() {
		
		root = uiBinder.createAndBindUi(this);		
		RootLayoutPanel.get().add(root);
		Stub.publish(this);
	}


	public HashMap<String, Object> getState() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		LOG.info("voor getState ");
		lognagekeken();
		super.getState(hashMap);
		lognagekeken();
		LOG.info("getState " + hashMap);
		return hashMap;
	}


	
	public void setState(HashMap<String, Object> h) {
		Map<String,Object> map = h;
		setState(map);
		lognagekeken();
		if(nagekeken) {
			//if(mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN) 
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
		nagekeken = true;LOG.info("KijkNA");
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
		check.removeStyleName(HALF_CSS);
		check.removeStyleName(FOUT_CSS);
		check.removeStyleName(GOED_CSS);
		if(checkObjects != null)
			checkObjects.removeFeedback();
		nagekeken=false;
	}

	public void zetNagekeken(boolean b) {
		this.nagekeken = b;
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
				}
			}
		}
		
		
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


	
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		widget.init(width, height);
		DoubleFormat.setInstance(new PrettyFormat());
		Messages.setInstance(new MessagesImpl(rb));
		viewer = widget.getViewer();
		TrackerImpl ti;
		NamingModel mapper = new NamingModel(viewer, new HashMap<String,Destroyable>());
		viewer = ti = new TrackerImpl(viewer, mapper);
		ti.expression = new nl.numworx.geodefiner.common.math.Expression(ti);
		LabelDelegate value = new HerleidList(ti);
		ti.expression.symbolmap.put("list1.list", value);
		uiModelFactory = new UIModelFactory(viewer);
		widget.setMapper(mapper);
		SelectHandler h = selector;
		h.setTracker(viewer);
		viewer.setPointerHandler(h);
		viewer.setStatus("");
		definitions = new Definitions(viewer);
		
		root.setPixelSize(width, height);
// initial model		
		createModel(viewer.getModel(), width, height);
// configuration
		setLaunchData(launchData, values);
		viewer.paint();
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
		}

	}

	/* (non-Javadoc)
	 * @see nl.numworx.geodefiner.common.Instance#update(fi.euclides.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object arg) {
		LOG.info("update " + arg);
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
			return true;
		}
		root.setWidgetHidden(southPanel, true);
		return false;
	}

	@Override
	public String randomize(String input) {
		return randomize(random, input);
	}

	@Override
	public String randomize(Map<String, Number> random, String text) {
		try {
			HashMap m = new HashMap(random);
			String[] keys = random.keySet().toArray(new String[random.size()]);
			return FormuleParser.randomizeString(text, keys, m);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "randomize " + text, e);
		}
		return super.randomize(random, text);
	}

	public void reset() {
		toolbox.destroy();
		if(checkObjects != null) checkObjects.destroy();
		createModel(viewer.getModel(), width, height);
		installLaunchData();
		start();
	}

	@Override
	public void start() {
		super.start();
		if(checkObjects != null)
			checkObjects.start();
	}
	
}
