package nl.numworx.geodefinergwt.client;

import nl.uu.fi.dwo.interaction.client.json.ObjectList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PushButton;

import fi.euclides.event.AddBissectriceHandler;
import fi.euclides.event.AddBoogHandler;
import fi.euclides.event.AddCirkelHandler;
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
import fi.euclides.event.AddTriangleHandler2;
import fi.euclides.event.DestroyHandler;
import fi.euclides.event.EventHandler;
import fi.euclides.event.SelectHandler;
import fi.euclides.event.Tracker;
import fi.euclides.proof.AfstandHandler;
import fi.euclides.proof.CrossRatio;
import fi.euclides.proof.HoekHandler;
import fi.euclides.proof.OppHandler;
import fi.euclides.proof.RatioHandler;
import fi.euclides.proof.VectorHandler;

public class ToolBoxPanel extends Composite {

	static class Action implements ClickHandler {

		final private EventHandler h;

		public Action(EventHandler h, Tracker t) {
			this.h = h;
			h.setTracker(t);
		}

		@Override
		public void onClick(ClickEvent event) {
			h.command();
			h.getTracker().paint();
		}

	}

	ToolBoxPanel(ObjectList list, Tracker tracker) {
		
		HorizontalPanel p = new HorizontalPanel();
		PushButton btn;
		String url = GWT.getModuleBaseURL() + "fi/euclides/resources";
		for (int i = 0; i < list.size(); i++ ) {
			int n = list.getInt(i);
			btn = null;
			switch(n) {
			case 0:
				btn = newBtn(url + "/move.png", new SelectHandler(), tracker);break;
			case 1: 
				btn = newBtn(url + "/point.png", new AddPuntHandler(), tracker);break;
			case 2:
				btn = newBtn(url + "/line.png", new AddLijnHandler(AddLijnHandler.LINE), tracker);break;
			case 3:
				btn = newBtn(url + "/segment.png", new AddLijnHandler(AddLijnHandler.SEGMENT), tracker);break;
			case 4:
				btn = newBtn(url + "/triangle.png", new AddTriangleHandler2(), tracker);break;
			case 5:
				btn = newBtn(url + "/circle.png", new AddCirkelHandler(), tracker);break;
			case 6:
				btn = newBtn(url + "/delete.png", new DestroyHandler(), tracker);break;		

			case 7:
				btn = newBtn(url + "/ray.png", new AddLijnHandler(AddLijnHandler.RAY), tracker);break;		
			case 8:
				btn = newBtn(url + "/angle.png", new AddBoogHandler("Boog"), tracker);break;		
			case 9:
				btn = newBtn(url + "/midpoint.png", new AddMiddelPuntHandler(), tracker);break;		
			case 10:
				btn = newBtn(url + "/plumb.png", new AddLoodLijnHandler(), tracker);break;		
			case 11:
				btn = newBtn(url + "/parallel.png", new AddParallelHandler(), tracker);break;		
			case 12:
				btn = newBtn(url + "/bissectrice.png", new AddBissectriceHandler(), tracker);break;		
			case 13:
				btn = newBtn(url + "/mirror.png", new AddSpiegelHandler(), tracker);break;		
			case 14:
				btn = newBtn(url + "/quadric.png", new AddKegelsnedeHandler("Kegelsnede"), tracker);break;		
			case 15:
				btn = newBtn(url + "/quadric.png", new AddFocusHandler(), tracker);break;		
			case 16:
				btn = newBtn(url + "/objecttracker.png", new AddLocusHandler("Meetkundige plaats"), tracker);break;		
			case 17:
				btn = newBtn(url + "/line.png", new AddRaakLijnHandler(), tracker);break;		
			case 18:
				btn = newBtn(url + "/line.png", new AddPoollijnHandler(), tracker);break;		
// labels
			case 19:
				btn = newBtn(url + "/segment.png", new AfstandHandler("lengte"), tracker); break;
			case 20:
				btn = newBtn(url + "/triangle.png", new OppHandler("oppervlakte"), tracker); break;
			case 21:
				btn = newBtn(url + "/angle.png", new HoekHandler("hoek"), tracker); break;
			case 22:
				btn = newBtn(url + "/ray.png", new VectorHandler("vector"), tracker); break;
				
/*			
		item = new MenuItem("Signed Area", new Action(new OppHandler("Signed Area", true), tracker ));
		item = new MenuItem("Ratio", new Action(new RatioHandler("Ratio"), tracker ));
		item = new MenuItem("Signed Ratio", new Action(new RatioHandler("Signed Ratio", true), tracker ));
		item = new MenuItem("CrossRatio", new Action(new CrossRatio("CrossRatio"), tracker ));
		
*/			
			
			}
			if(btn != null)	p.add(btn);
		}
		initWidget(p);
	}

	PushButton newBtn(String url, EventHandler handler, Tracker tracker) {
		PushButton btn;
		btn = new PushButton(new Image(url));
				btn.addClickHandler(new Action(handler, tracker));
		return btn;
	}
}
