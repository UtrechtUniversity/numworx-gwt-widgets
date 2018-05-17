package nl.numworx.geodefinergwt.client;

import java.util.Map;
import java.util.Vector;

import javax.inject.Provider;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

import fi.euclides.event.EventHandler;
import fi.euclides.event.Tracker;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Lijn;
import fi.euclides.model.Model;
import fi.euclides.model.Segment;
import fi.euclides.util.Messages;
import fi.euclides.util.Observable;
import fi.euclides.util.Observer;
import nl.numworx.geodefiner.common.Tools;
import nl.numworx.geodefinergwt.client.toolbox.RadioMode;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;

public class ToolBoxPanel extends Composite implements Tools {

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

		public Action(EventHandler h, TrackerImpl t, ToggleButton btn, RadioMode model) {
			this.h = h;
			this.btn = btn;
			this.tracker = t;
			this.model = model;
			h.setTracker(t);
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

		SafeHtml[] faces;
		public CirkelAction(EventHandler h, TrackerImpl t, ToggleButton btn, RadioMode model, String... images) {
			super(h, t, btn, model);
			faces = new SafeHtml[images.length]; 
			for(int i = 0; i < images.length; i++)
			  faces[i] = face(images[i]);
			btn.getUpFace().setHTML(faces[0]);
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
					btn.getUpFace().setHTML(faces[1]);
					break;
				}
			default:
				btn.getUpFace().setHTML(faces[0]); break;
			case 3:
				btn.getUpFace().setHTML(faces[2]); break;
			}	
		}
	}

	public static class PuntAction extends Action {

		public PuntAction(EventHandler h, TrackerImpl t, ToggleButton btn, RadioMode model, String... images) {
			super(h, t, btn, model);
			faces = new SafeHtml[images.length];
			for (int i = 0; i < images.length; i++) {
				faces[i] = face(images[i]);
			}
		}

		SafeHtml[] faces;
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
				btn.getUpFace().setHTML(faces[1]);
				break;
			case 2: 
				btn.setTitle(GeoDefinerGWT.MESSAGES.AddPuntHandler_3());
				btn.getUpFace().setHTML(faces[2]);
				break;
			default:
				btn.setTitle(GeoDefinerGWT.MESSAGES.AddPuntHandler_0());
				btn.getUpFace().setHTML(faces[0]);
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
	
	private int height = 38;
	int getHeight() {
		return height;
	}
	
	void init(ObjectList list, int w, Map<Integer,Provider<ToggleButton>> buttons) {		
		ToggleButton btn;
		height = ((list.size()*38-1)/w+1)*38;
		for (int i = 0; i < list.size(); i++ ) {
			int n = list.getInt(i);
			btn = null;
			Provider<ToggleButton> provider = buttons.get(n);
			if(provider != null)
				btn = provider.get();
			if(btn != null)	panel.add(btn);
		}
	}


}
