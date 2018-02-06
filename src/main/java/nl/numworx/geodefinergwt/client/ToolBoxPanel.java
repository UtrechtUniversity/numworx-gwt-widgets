package nl.numworx.geodefinergwt.client;

import nl.numworx.geodefiner.common.AddCirkelHandler;
import nl.numworx.geodefiner.common.AddPolygonHandler;
import nl.numworx.geodefiner.common.FilteredDestroyHandler;
import nl.numworx.geodefiner.common.ResetHandler;
import nl.numworx.geodefiner.common.Tools;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;

import fi.euclides.event.AddBissectriceHandler;
import fi.euclides.event.AddBoogHandler;
import fi.euclides.event.AddFocusHandler;
import fi.euclides.event.AddKegelsnedeHandler;
import fi.euclides.event.AddLijnHandler;
import fi.euclides.event.AddLocusHandler;
import fi.euclides.event.AddLoodLijnHandler;
import fi.euclides.event.AddMiddelPuntHandler;
import fi.euclides.event.AddParallelHandler;
import fi.euclides.event.AddPoollijnHandler;
import fi.euclides.event.AddPuntHandler;
import fi.euclides.event.AddRaakLijnHandler;
import fi.euclides.event.AddSpiegelHandler;
import fi.euclides.event.DestroyHandler;
import fi.euclides.event.EventHandler;
import fi.euclides.event.SelectHandler;
import fi.euclides.event.Tracker;
import fi.euclides.expr.TrailHandler;
import fi.euclides.proof.AfstandHandler;
import fi.euclides.proof.HoekHandler;
import fi.euclides.proof.OppHandler;
import fi.euclides.proof.VectorHandler;
import fi.euclides.util.Messages;

public class ToolBoxPanel extends Composite implements Tools {

	class Action implements ClickHandler {

		final private EventHandler h;
		final private ToggleButton btn;

		public Action(EventHandler h, Tracker t, ToggleButton btn) {
			this.h = h;
			this.btn = btn;
			h.setTracker(t);
		}

		@Override
		public void onClick(ClickEvent event) {
			down(btn);
			h.command();
			h.getTracker().paint();
		}

	}

	FlowPanel panel;
	ToggleButton downBtn;
	
	public ToolBoxPanel() {
		panel = new FlowPanel();
		initWidget(panel);
	}
	
	void down(ToggleButton btn) {
		if(downBtn != null && downBtn != btn)
			downBtn.setDown(false);
		downBtn = btn;
		btn.setDown(true);
		
	}

	void destroy() {
		int size = panel.getWidgetCount();
		for(int i = 0; i < size; i++)
			panel.getWidget(0).removeFromParent();
	}
	
	private int height = 38;
	private int width;
	int getHeight() {
		return height;
	}
	
	void init(ObjectList list, Tracker tracker, GeoDefinerGWT geoDefinerGWT) {		
		ToggleButton btn;
		width = geoDefinerGWT.getWidth();
		height = ((list.size()*38-1)/width+1)*38;
		String url = GWT.getModuleBaseURL() + "fi/euclides/resources";
		for (int i = 0; i < list.size(); i++ ) {
			int n = list.getInt(i);
			btn = null;
			ResetHandler resetter;
			switch(n) {
			case SELECTOR:
				btn = newBtn(url + "/move.png", new SelectHandler(), tracker);break;
			case POINT: 
				btn = newBtn(url + "/point.png", new AddPuntHandler(), tracker);break;		
			case LINE:
				btn = newBtn(url + "/line.png", new AddLijnHandler(AddLijnHandler.LINE), tracker);break;
			case SEGMENT:
				btn = newBtn(url + "/segment.png", new AddLijnHandler(AddLijnHandler.SEGMENT), tracker);break;
			case TRIANGLE:
				btn = newBtn(url + "/triangle.png", new AddPolygonHandler("Veelhoek"), tracker);break;
			case CIRCLE:
				btn = newBtn(url + "/circle.png", new AddCirkelHandler(), tracker);break;
			case DESTROY:
				btn = newBtn(url + "/delete.png", new FilteredDestroyHandler(geoDefinerGWT), tracker);break;
			case HALFLINE:
				btn = newBtn(url + "/ray.png", new AddLijnHandler(AddLijnHandler.RAY), tracker);break;		
			case ARC:
				btn = newBtn(url + "/angle.png", new AddBoogHandler("Boog"), tracker);break;		
			case MIDPOINT:
				btn = newBtn(url + "/midpoint.png", new AddMiddelPuntHandler(), tracker);break;		
			case PERPENDICULAR:
				btn = newBtn(url + "/plumb.png", new AddLoodLijnHandler(), tracker);break;		
			case PARALLEL:
				btn = newBtn(url + "/parallel.png", new AddParallelHandler(), tracker);break;		
			case BISECTRICE:
				btn = newBtn(url + "/bissectrice.png", new AddBissectriceHandler(), tracker);break;		
			case MIRROR:
				btn = newBtn(url + "/mirror.png", new AddSpiegelHandler(), tracker);break;		
			case CONIC_SECTION:
				btn = newBtn(url + "/quadric.png", new AddKegelsnedeHandler("Kegelsnede"), tracker);break;		
			case FOCUS:
				btn = newBtn(url + "/quadric.png", new AddFocusHandler(), tracker);break;		
			case LOCUS:
				btn = newBtn(url + "/objecttracker.png", new AddLocusHandler("Meetkundige plaats"), tracker);break;		
			case TANGENT:
				btn = newBtn(url + "/line.png", new AddRaakLijnHandler(), tracker);break;		
			case POLELINE:
				btn = newBtn(url + "/line.png", new AddPoollijnHandler(), tracker);break;		
// labels
			case DISTANCE:
				btn = newBtn(url + "/segment.png", new AfstandHandler("lengte"), tracker); break;
			case AREA:
				btn = newBtn(url + "/area.png", new OppHandler("oppervlakte"), tracker); break;
			case ANGLE:
				btn = newBtn(url + "/angle.png", new HoekHandler("hoek"), tracker); break;
			case VECTOR:
				btn = newBtn(url + "/ray.png", new VectorHandler("vector"), tracker); break;
				
/*			
		item = new MenuItem("Signed Area", new Action(new OppHandler("Signed Area", true), tracker ));
		item = new MenuItem("Ratio", new Action(new RatioHandler("Ratio"), tracker ));
		item = new MenuItem("Signed Ratio", new Action(new RatioHandler("Signed Ratio", true), tracker ));
		item = new MenuItem("CrossRatio", new Action(new CrossRatio("CrossRatio"), tracker ));
		
*/			
			case FORMULA: // definitie, /formuleknop.gif
				btn = newBtn(url + "/function.png", new FormuleHandler("Definitie"), tracker);break;
			case TEXT: 
				btn = newBtn(url + "/text.png", new TextHandler("Text"), tracker); break;
			case TRAIL: // trail
				btn = newBtn(url+"/thickness2.png", new TrailHandler(Messages.getString("Euclides.44")), tracker);
					break;					
			case PAN: // pan
				btn = newBtn(url + "/pan.png", geoDefinerGWT.widget.getPanHandler(), tracker);
					break;
			case RESET: // reset
				resetter = new ResetHandler("Reset",geoDefinerGWT);
				btn = newBtn(url + "/reseticon.gif", resetter, tracker); break;
			}
			if(btn != null)	panel.add(btn);
		}
	}

	ToggleButton newBtn(String url, EventHandler handler, Tracker tracker) {
		ToggleButton btn;
		btn = new ToggleButton(new Image(url));
		btn.addClickHandler(new Action(handler, tracker,btn));
		return btn;
	}
}
