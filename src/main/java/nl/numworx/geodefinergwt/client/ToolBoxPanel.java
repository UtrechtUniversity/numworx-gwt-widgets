package nl.numworx.geodefinergwt.client;

import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import javax.inject.Provider;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ToggleButton;

import fi.euclides.event.EventHandler;
import fi.euclides.event.Tracker;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Lijn;
import fi.euclides.model.Model;
import fi.euclides.model.Segment;
import fi.euclides.util.Observable;
import fi.euclides.util.Observer;
import nl.numworx.geodefiner.common.Tools;
import nl.numworx.geodefiner.common.UIShim;
import nl.numworx.geodefinergwt.client.toolbox.RadioMode;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class ToolBoxPanel extends Composite implements Tools, RequiresResize {

	private static final int BREEDTE_ICON = 40;

  static interface FaceTemplate extends SafeHtmlTemplates {
	  @Template("<span class='{0}' ></span>")
	  SafeHtml face(String cls);
	}
	
	static FaceTemplate faceTemplace = GWT.create(FaceTemplate.class);
	
	public static SafeHtml face(String cls) {
	    return faceTemplace.face(cls);
	}
	
	public static class Action implements ClickHandler, Observer, AttachEvent.Handler {

		final EventHandler h;
		final ToggleButton btn;
		final TrackerImpl  tracker;
		final RadioMode model;

		public Action(EventHandler h, TrackerImpl t, ToggleButton btn, RadioMode model, UIShim<? extends Destroyable, Void> shim) {
			this.h = h;
			this.btn = btn;
			this.tracker = t;
			this.model = model;
			h.setTracker(t);
			if(shim!= null) h.setDecorator(shim);
			btn.addAttachHandler(this);
		}

		@Override
		public void onClick(ClickEvent event) {
			model.down(btn);
			h.command();
			tracker.paint();
			model.down(model.toggles.get(tracker.getPointerHandler()));
		}

		@Override
		public void update(Observable observable, Object arg) {
			Vector<Destroyable> selection = tracker.getModel().getSelect();
			setEnabled(h.allowSelection(selection));
		}

		private void setEnabled(boolean enabled) {
			btn.setEnabled(enabled);
		}

		@Override
		public void onAttachOrDetach(AttachEvent event) {
			if(event.isAttached())
			{	model.toggles.put(h,btn);
				tracker.getModel().addObserver(this);
				//update(null,null);
			}
			else
			{
				tracker.getModel().deleteObserver(this);
				model.toggles.remove(h);
			}
			
		}

	}

	public static class CirkelAction extends Action {

		Image[] images;
		public CirkelAction(EventHandler h, TrackerImpl t, ToggleButton btn, RadioMode model, UIShim<? extends Destroyable, Void> shim, DataResource ... images) {
			super(h, t, btn, model,shim);
			this.images = new Image[images.length]; 
			for(int i = 0; i < images.length; i++) {
			    Image image = new Image(images[i].getSafeUri());
			    image.setPixelSize(32, 39);
			    this.images[i] = image;
			}
			  btn.getUpFace().setImage(this.images[0]);
			  btn.getDownFace().setImage(this.images[1]);
		}

		@Override
		public void update(Observable observable, Object arg) {
			super.update(observable, arg);
			if(arg != Model.SELECT)
				return;
			int cnt;
			Vector<Destroyable> select = h.getTracker().getModel().getSelect();
			if( btn.isEnabled()) {
				cnt = select.size();
			} else 
				cnt = 0;
			switch(cnt) {
			case 1:
			case 2:
				Object f = select.firstElement();
				Object l = select.lastElement();
				if (f instanceof Segment || l instanceof Segment) {
					btn.getUpFace().setImage(images[2]); btn.getDownFace().setImage(images[3]);
					break;
				}
			default:
				btn.getUpFace().setImage(images[0]); btn.getDownFace().setImage(images[1]);break;
			case 3:
				btn.getUpFace().setImage(images[4]); btn.getDownFace().setImage(images[5]);break;
			}	
		}
	}

	public static class PuntAction extends Action {

//		public PuntAction(EventHandler h, TrackerImpl t, ToggleButton btn, RadioMode model, String... images) {
//			super(h, t, btn, model);
//			faces = new SafeHtml[images.length];
//			for (int i = 0; i < images.length; i++) {
//				faces[i] = face(images[i]);
//			}
//			btn.getUpFace().setHTML(faces[0]);
//		}

		public PuntAction(EventHandler h, TrackerImpl t, ToggleButton btn, RadioMode model, DataResource...images) {
		  super(h, t, btn, model,null);
		  this.images = new Image[images.length];
		  for (int i = 0; i < images.length; i++) {
		    Image image = new Image(images[i].getSafeUri());
		    image.setPixelSize(32, 39);
		    this.images[i] = image;
		  }
		  
		  btn.getUpFace().setImage(this.images[0]);
		  btn.getDownFace().setImage(this.images[1]);
		}
		
		private Image[] images;
//		private SafeHtml[] faces;
		/* (non-Javadoc)
		 * @see fi.euclides.swing.XXXAction#update(fi.euclides.util.Observable, java.lang.Object)
		 */
		public void update(Observable observable, Object arg) {
			super.update(observable, arg);
			if(arg != Model.SELECT)
				return;
			Tracker viewer = h.getTracker();
			int cnt;
			if(!btn.isEnabled())
				cnt = 0;
			else
				cnt = viewer.getModel().getSelect().size();
			switch(cnt)
			{
			case 1: 
				Object d = viewer.getModel().getSelect().firstElement();
				String string;
				if(d instanceof Lijn)
				{ 
					string = GeoDefinerGWT.MESSAGES.AddPuntHandler_1(); //$NON-NLS-1$
				} else
				{
					string = GeoDefinerGWT.MESSAGES.AddPuntHandler_2(); //$NON-NLS-1$
				} 
				btn.setTitle(string);
				btn.getUpFace().setImage(images[2]);
				btn.getDownFace().setImage(images[3]);
				break;
			case 2: 
				btn.setTitle(GeoDefinerGWT.MESSAGES.AddPuntHandler_3());
				btn.getUpFace().setImage(images[4]);
				btn.getDownFace().setImage(images[5]);
				break;
			default:
				btn.setTitle(GeoDefinerGWT.MESSAGES.AddPuntHandler_0());
				btn.getUpFace().setImage(images[0]);
				btn.getDownFace().setImage(images[1]);
				break;
			}
		}
	}

	FlowPanel panel;
	
	public ToolBoxPanel() {
		panel = new FlowPanel();
		initWidget(panel);
	}
	

	void destroy() {
		int size = panel.getWidgetCount();
		for(int i = 0; i < size; i++)
			panel.getWidget(0).removeFromParent();
	}
	
	private int height = 40;
	int getHeight() {
		return height;
	}
	
	void init(ObjectList list, ObjectList config, int w, Map<Integer,Provider<ToggleButton>> buttons, Map<Integer, Provider<UIShim<? extends Destroyable, Void>>> shims) {		
		ToggleButton btn;
		height = ((list.size()*BREEDTE_ICON-1)/w+1) * 40;
		for (int i = 0; i < list.size(); i++ ) {
			int n = list.getInt(i);
			btn = null;
			Provider<ToggleButton> provider = buttons.get(n);
			if(provider != null)
			{
				btn = provider.get();
				installConfig(n, config, shims);
			}
			if(btn != null)	panel.add(btn);
		}
	}


	private void installConfig(int i, ObjectList config, Map<Integer, Provider<UIShim<? extends Destroyable, Void>>> shims) {
GWT.log("install config " + i + "  " + config);
		if (config != null && i < config.size()) {
			ObjectMap map = config.getObjectMap(i);
			if (map != null) {
GWT.log("map = " + map);
				Provider<UIShim<? extends Destroyable, Void>> provider = shims.get(i);
GWT.log("provider is " + provider);
				if (provider != null) provider.get().fromMap(map);
			}
		}
		
	}

	private Optional<RequiresResize> resizer = Optional.empty();

	@Override
	public void onResize() {
		resizer.ifPresent(RequiresResize::onResize);
	}

	public void setResizer(RequiresResize resizer) {
		this.resizer = Optional.ofNullable(resizer);
	}

}
