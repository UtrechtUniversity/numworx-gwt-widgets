package nl.numworx.geodefinergwt.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import nl.numworx.geodefiner.common.Align;
import nl.numworx.geodefiner.common.Definitions;
import nl.numworx.geodefiner.common.Instance;
import nl.numworx.geodefiner.common.NamingModel;
import nl.numworx.geodefinergwt.client.i18n.MessagesImpl;
import nl.numworx.geodefinergwt.client.ui.PointModel;
import nl.numworx.geodefinergwt.client.ui.TextModel;
import nl.numworx.geodefinergwt.client.ui.UIModelFactory;
import nl.uu.fi.dwo.interaction.client.FacetHelper;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import fi.euclides.event.EventHandler;
import fi.euclides.event.HitTester;
import fi.euclides.event.NameMapper;
import fi.euclides.event.SelectHandler;
import fi.euclides.event.Tracker;
import fi.euclides.gwt.PrettyFormat;
import fi.euclides.gwt.ViewerWidget;
import fi.euclides.gwt.canvas.SpeelVeld;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Model;
import fi.euclides.model.Punt;
import fi.euclides.model.Track;
import fi.euclides.model.math.DoubleFormat;
import fi.euclides.proof.LabelDelegate;
import fi.euclides.util.Messages;
import fi.euclides.util.Observable;

public class GeoDefinerGWT extends Instance implements EntryPoint, InteractionStub, CBookEventListener {

	private int width = 500;
	private int height = 450;
	private ViewerWidget widget;
	private OpdrNavIF comRoot;
	private DockPanel panel;
	private boolean volledigeBreedte;
	
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
			GWT.log(string);
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
		
	}
	
	public GeoDefinerGWT() {}
	
	public GeoDefinerGWT(HashMap<String, Object> h,
			HashMap<String, Number> randomVarWaarden, int volleBreedte) {
		panel = new DockPanel();
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
	public void onModuleLoad() {
		panel = new DockPanel();
		
		RootPanel.get().add(panel);

		
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
		viewer.paint();
	}

	public int[][] getScoreObjectives() {
		return null;
	}


	public Boolean isCorrect() {
		return getStatus();
	}


	
	public void kijkNa() {
	}


	
	public void zetNagekeken(boolean b) {
	}


	
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		comRoot.addCBookEventListener("double", this);
	}


	public void zetVolledigeBreedte(int breedte) {
	}

	public Widget asWidget() {
		return panel;
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
		//widget = new SVGWidget(width, height);
		//widget = new SpeelVeld(width, height);
		widget = new InstanceViewer(width, height);
		DoubleFormat.setInstance(new PrettyFormat());
		Messages.setInstance(new MessagesImpl());

		viewer = widget.getViewer();
		viewer = new TrackerImpl(viewer, new NamingModel(viewer, new HashMap<String,Destroyable>()));
		uiModelFactory = new UIModelFactory(viewer);

		SelectHandler h = selector;
		h.setTracker(viewer);
		viewer.setPointerHandler(h);
		definitions = new Definitions(viewer);
		
		widget.asWidget().setPixelSize(width, height);
		panel.add(widget, DockPanel.CENTER);
		
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
		if("changed".equals(arg)) {
			comRoot.setChanged(Boolean.FALSE.equals(getStatus()));
		}
	}


}
