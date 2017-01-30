package nl.numworx.geodefinergwt.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import nl.numworx.geodefiner.common.Definitions;
import nl.numworx.geodefiner.common.Instance;
import nl.numworx.geodefiner.common.NamingModel;
import nl.numworx.geodefinergwt.client.i18n.MessagesImpl;
import nl.numworx.geodefinergwt.client.i18n.messages;
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
import com.google.gwt.user.client.ui.HasText;
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
import fi.euclides.model.Destroyable;
import fi.euclides.model.Model;
import fi.euclides.model.Track;
import fi.euclides.model.math.DoubleFormat;
import fi.euclides.proof.LabelDelegate;
import fi.euclides.util.Messages;
import fi.euclides.util.Observable;
import fi.euclides.util.Observer;

public class GeoDefinerGWT extends Instance implements EntryPoint, InteractionStub, CBookEventListener, Observer {

	private static final String GOED_CSS = "goed";
	private static final String FOUT_CSS = "fout";
	private static final String HALF_CSS = "half";
	private int width = 500;
	private int height = 450;
	private OpdrNavIF comRoot;
	
	private boolean volledigeBreedte;
	interface MyUiBinder extends UiBinder<DockLayoutPanel, GeoDefinerGWT> {}
	static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField DockLayoutPanel southPanel;
	@UiField Label status;
	DockLayoutPanel  root;
	@UiField ViewerWidget widget;
	@UiField FlowPanel check;
	@UiField Button checkBtn;
	@UiField ToolBoxPanel toolbox;
	@UiField messages rb = GWT.create(messages.class);
	
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

		@Override
		public <T> T adapt(Class<T> cls) {
			return viewer.adapt(cls);
		}
		
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
				toolbox.init(list, viewer);
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
		super.getState(hashMap);
		return hashMap;
	}


	
	public void setState(HashMap<String, Object> h) {
		Map<String,Object> map = h;
		setState(map);
		start();
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
	}

	@UiHandler("checkBtn") void kijkNa(ClickEvent evt) { kijkNa(); }
	
	private void feedback() {
		Boolean status = getStatus();
		check.setStyleName(HALF_CSS, status == null);
		check.setStyleName(FOUT_CSS, Boolean.FALSE.equals(status));
		check.setStyleName(GOED_CSS, Boolean.TRUE.equals(status));		
	}
	
	private void nofeedback() {
		check.removeStyleName(HALF_CSS);
		check.removeStyleName(FOUT_CSS);
		check.removeStyleName(GOED_CSS);
	}

	public void zetNagekeken(boolean b) {
		this.nagekeken = b;
	}
	
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		comRoot.addCBookEventListener("double", this);
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
		viewer = new TrackerImpl(viewer, new NamingModel(viewer, new HashMap<String,Destroyable>()));
		uiModelFactory = new UIModelFactory(viewer);
		widget.setMapper(viewer.getMapper());
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
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see nl.numworx.geodefiner.common.Instance#update(fi.euclides.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object arg) {
		super.update(observable, arg);
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

}
