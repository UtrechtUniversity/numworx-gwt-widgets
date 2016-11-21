package nl.numworx.geodefinergwt.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import nl.numworx.geodefiner.common.Align;
import nl.numworx.geodefiner.common.Definitions;
import nl.numworx.geodefiner.common.Instance;
import nl.numworx.geodefiner.common.NamingModel;
import nl.numworx.geodefinergwt.client.ui.ColorModel;
import nl.numworx.geodefinergwt.client.ui.PointModel;
import nl.numworx.geodefinergwt.client.ui.TextModel;
import nl.numworx.geodefinergwt.client.ui.UIModelFactory;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import fi.euclides.event.EventHandler;
import fi.euclides.event.HitTester;
import fi.euclides.event.NameMapper;
import fi.euclides.event.SelectHandler;
import fi.euclides.event.Tracker;
import fi.euclides.gwt.ViewerWidget;
import fi.euclides.gwt.canvas.SpeelVeld;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Model;
import fi.euclides.model.Punt;
import fi.euclides.model.Track;
import fi.euclides.proof.LabelDelegate;

public class GeoDefinerGWT extends Instance implements EntryPoint, InteractionStub, CBookEventListener {

	private int width = 200;
	private int height = 200;
	private ViewerWidget widget;
	private OpdrNavIF comRoot;
	private SimplePanel panel;
	
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
	
	@Override
	public void onModuleLoad() {
		panel = new SimplePanel();
		uiModelFactory = new UIModelFactory();
		
		RootPanel.get().add(panel);

		
		Map<String, Object> launchData = new HashMap<String,Object>();
		String[] definitions = {
				"$fp=point(1, 1)@",
				"$fl=segment(O, p)@",
				"$ft=text(\"$wText@\", U)@",
		};

		PointModel colorP = new PointModel();
		colorP.rgba = 0xFFFF0000;
		colorP.rigid = false;
		TextModel textT = new TextModel();
		textT.align = Align.RIGHT;
		
		launchData.put("definitions", definitions);
		launchData.put("configuration", Collections.singletonMap("t", textT.toMap()));
		launchData.put("axes", Collections.singletonMap("U", colorP.toMap()));
				
		Map<String, Number> values = Collections.emptyMap();

		init(width, height, launchData, values);
		
		//Stub.publish(this);
	}


	public HashMap<String, Object> getState() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		super.getState(hashMap);
		return hashMap;
	}


	
	public void setState(HashMap<String, Object> h) {
		Map<String,Object> map = h;
		setState(map);
	}

	public int[][] getScoreObjectives() {
		return null;
	}


	public Boolean isCorrect() {
		return Boolean.TRUE;
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
		return widget.asWidget();
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

		viewer = widget.getViewer();
		viewer = new TrackerImpl(viewer, new NamingModel(viewer, new HashMap()));

		SelectHandler h = new SelectHandler();
		h.setTracker(viewer);
		viewer.setPointerHandler(h);
		definitions = new Definitions(viewer);
		
		panel.setPixelSize(width, height);
		panel.setWidget(widget);
		
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


}
