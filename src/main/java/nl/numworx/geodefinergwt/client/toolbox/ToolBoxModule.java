package nl.numworx.geodefinergwt.client.toolbox;

import javax.inject.Named;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntKey;
import dagger.multibindings.IntoMap;
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
import fi.euclides.event.AddRaakLijnHandler;
import fi.euclides.event.AddSpiegelHandler;
import fi.euclides.event.EventHandler;
import fi.euclides.expr.TrailHandler;
import fi.euclides.proof.AfstandHandler;
import fi.euclides.proof.HoekHandler;
import fi.euclides.proof.OppHandler;
import fi.euclides.proof.VectorHandler;
import nl.numworx.geodefiner.common.AddCirkelHandler;
import nl.numworx.geodefiner.common.AddPolygonHandler;
import nl.numworx.geodefiner.common.AddSnapPuntHandler;
import nl.numworx.geodefiner.common.Definitions;
import nl.numworx.geodefiner.common.FilteredDestroyHandler;
import nl.numworx.geodefiner.common.Instance;
import nl.numworx.geodefiner.common.ResetHandler;
import nl.numworx.geodefiner.common.Tools;
import nl.numworx.geodefinergwt.client.TrackerImpl;
import nl.numworx.geodefinergwt.client.i18n.messages;
import nl.numworx.geodefinergwt.client.CirkelRadiusHandler;
import nl.numworx.geodefinergwt.client.FormuleHandler;
import nl.numworx.geodefinergwt.client.GeoDefinerGWT;
import nl.numworx.geodefinergwt.client.SvgBundle;
import nl.numworx.geodefinergwt.client.TextHandler;
import nl.numworx.geodefinergwt.client.ToolBoxPanel;
import nl.numworx.geodefinergwt.client.ToolBoxPanel.Action;
import nl.numworx.geodefinergwt.client.ToolBoxPanel.CirkelAction;
import nl.numworx.geodefinergwt.client.ToolBoxPanel.PuntAction;

@Module
public class ToolBoxModule {

	private final static messages rb = GeoDefinerGWT.MESSAGES;
	private final static SvgBundle svg = GWT.create(SvgBundle.class);
	
	private static ToggleButton newPBtn(EventHandler handler, TrackerImpl tracker, RadioMode model) {
		ToggleButton btn;
		String puntIcon = "point";
		String puntOpIcon = "pointon";
		String puntOp2Icon = "intersection";
		btn = new ToggleButton();
		btn.setTitle(rb.Euclides_46());
		btn.addClickHandler(new PuntAction(handler, tracker,btn, model, puntIcon, puntOpIcon, puntOp2Icon));
		return btn;
	}

	static private ToggleButton newCBtn(EventHandler handler, TrackerImpl tracker, RadioMode model, String string) {
		ToggleButton btn;
		String cirkelIcon  = "circle0";
		String compassIcon = "circle1";
		String cirkel3Icon = "circle2";
		btn = new ToggleButton();
		btn.setTitle(string);
		btn.addClickHandler(new CirkelAction(handler, tracker, btn, model, cirkelIcon, compassIcon, cirkel3Icon));
		return btn;
	}

	@Provides @IntKey(Tools.POINT) @IntoMap static
	ToggleButton point(TrackerImpl tracker, RadioMode model) {
		return newPBtn(new AddSnapPuntHandler(), tracker, model);
	}
	
	@Provides @IntKey(Tools.LINE) @IntoMap static
	ToggleButton line(TrackerImpl tracker, RadioMode model) {
		return newSBtn(svg.lijn_svg(), svg.lijn_active_svg(), new AddLijnHandler(AddLijnHandler.LINE), tracker, model, rb.Euclides_50());
	}

	@Provides @IntKey(Tools.SELECTOR) @IntoMap static
	ToggleButton selector(TrackerImpl tracker, RadioMode model, Instance instance) {
      return newBtnSpan("move", instance.selector, tracker, model, rb.Euclides_35());
	}

  private static ToggleButton newBtnSpan(String cls, EventHandler handler, TrackerImpl tracker,
      RadioMode model, String t) {
    ToggleButton btn;
    btn = new ToggleButton();
    btn.getUpFace().setHTML(ToolBoxPanel.face(cls));
    if (t != null) btn.setTitle(t);
    btn.addClickHandler(new Action(handler, tracker,btn,model));
    return btn;
  }
  
  private static ToggleButton newSBtn(DataResource r, DataResource r_active, EventHandler h, TrackerImpl tracker, RadioMode model, String t) {
    ToggleButton btn;
    btn = new ToggleButton();
    Image image = new Image(r.getSafeUri());
    image.setPixelSize(32, 32);
    btn.getUpFace().setImage(image);
    image = new Image(r_active.getSafeUri());
    image.setPixelSize(32, 32);
    btn.getDownFace().setImage(image);
    if (t != null) btn.setTitle(t);
    btn.addClickHandler(new Action(h, tracker, btn, model));
    return btn;
  }
  

	@Provides @IntKey(Tools.DESTROY) @IntoMap static
	ToggleButton destroy(TrackerImpl tracker, RadioMode model, Instance instance) {
		return newBtnSpan("delete", new FilteredDestroyHandler(instance), tracker, model,rb.Euclides_37());
	}
	@Provides @IntKey(Tools.RESET) @IntoMap static
	ToggleButton reset(TrackerImpl tracker, RadioMode model, Instance instance) {
		return newBtnSpan("reset", new ResetHandler("Reset",instance), tracker, model,null);
	}

	@Provides @IntKey(Tools.PAN) @IntoMap static
	ToggleButton pan(TrackerImpl tracker, RadioMode model, @Named("panHandler") EventHandler handler) {
	    return newSBtn(svg.pan_svg(), svg.pan_active_svg(), handler, tracker, model, rb.Euclides_41());
	}
	
	@Provides @IntKey(Tools.SEGMENT) @IntoMap static
	ToggleButton segment(TrackerImpl tracker, RadioMode model) {
		return newSBtn(svg.lijnstuk_svg(),svg.lijnstuk_active_svg(), new AddLijnHandler(AddLijnHandler.SEGMENT), tracker, model,rb.Euclides_48());
	}
	@Provides @IntKey(Tools.HALFLINE) @IntoMap static
	ToggleButton halfline(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("ray", new AddLijnHandler(AddLijnHandler.RAY), tracker, model,rb.Euclides_49());
	}
	@Provides @IntKey(Tools.TRIANGLE) @IntoMap static
	ToggleButton triangle(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("triangle", new AddPolygonHandler("Veelhoek"), tracker, model,"Veelhoek");
	}

	@Provides @IntKey(Tools.CIRCLE) @IntoMap static
	ToggleButton circle(TrackerImpl tracker, RadioMode model) {
		return newCBtn(new AddCirkelHandler(), tracker, model,rb.Euclides_52());
	}

	@Provides @IntKey(Tools.ARC) @IntoMap static
	ToggleButton arc(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("arc", new AddBoogHandler("Boog"), tracker, model,null);
	}
	@Provides @IntKey(Tools.MIDPOINT) @IntoMap static
	ToggleButton midpoint(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("midpoint", new AddMiddelPuntHandler(), tracker, model,rb.Euclides_54());
	}

	@Provides @IntKey(Tools.PERPENDICULAR) @IntoMap static
	ToggleButton perpendicular(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("perpendicular", new AddLoodLijnHandler(), tracker, model,rb.Euclides_56());
	}

	@Provides @IntKey(Tools.PARALLEL) @IntoMap static
	ToggleButton parallel(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("parallel", new AddParallelHandler(), tracker, model,rb.Euclides_58());
	}

	@Provides @IntKey(Tools.BISECTRICE) @IntoMap static
	ToggleButton bissectrice(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("bissectrice", new AddBissectriceHandler(), tracker, model,rb.Euclides_60());
	}

	@Provides @IntKey(Tools.MIRROR) @IntoMap static
	ToggleButton mirror(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("mirror", new AddSpiegelHandler(), tracker, model, rb.Euclides_62());
	}

	@Provides @IntKey(Tools.CONIC_SECTION) @IntoMap static
	ToggleButton conic(TrackerImpl tracker, RadioMode model) {
		return newSBtn(svg.kegelsnede_svg(), svg.kegelsnede_active_svg(), new AddKegelsnedeHandler("Kegelsnede"), tracker, model, "Kegelsnede");
	}

	@Provides @IntKey(Tools.FOCUS) @IntoMap static
	ToggleButton focus(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("focus", new AddFocusHandler(), tracker, model, "Brandpunt");
	}

	@Provides @IntKey(Tools.LOCUS) @IntoMap static
	ToggleButton locus(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("locus", new AddLocusHandler("Meetkundige plaats"), tracker, model, "Meetkundige plaats");
	}

	@Provides @IntKey(Tools.TANGENT) @IntoMap static
	ToggleButton tangent(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("tangent", new AddRaakLijnHandler(), tracker, model, "Raaklijn"); // FIXME icon
	}

	@Provides @IntKey(Tools.POLELINE) @IntoMap static
	ToggleButton pole(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("poleline", new AddPoollijnHandler(), tracker, model, "Poollijn"); // FIXME icon
	}
	//labels
	@Provides @IntKey(Tools.DISTANCE) @IntoMap static
	ToggleButton distance(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("distance", new AfstandHandler(rb.Euclides_88()), tracker, model,rb.Euclides_88());
	}

	@Provides @IntKey(Tools.AREA) @IntoMap static
	ToggleButton area(TrackerImpl tracker, RadioMode model) {
		return newSBtn(svg.oppervlakte_svg(), svg.oppervlakte_active_svg(), new OppHandler(rb.Euclides_91()), tracker, model,rb.Euclides_91());
	}

	@Provides @IntKey(Tools.ANGLE) @IntoMap static
	ToggleButton angle(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("angle", new HoekHandler(rb.Euclides_85()), tracker, model,rb.Euclides_85());
	}

	@Provides @IntKey(Tools.VECTOR) @IntoMap static
	ToggleButton vector(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("vector", new VectorHandler("vector"), tracker, model,null);
	}

	@Provides @IntKey(Tools.CIRCLE_WITH_RADIUS) @IntoMap static
	ToggleButton circleWithRadius(TrackerImpl tracker, RadioMode model) {
		return newSBtn(svg.cirkel_met_straal_svg(), svg.cirkel_met_straal_active_svg(),new CirkelRadiusHandler(rb.AddCirkelHandler_0()), tracker, model,null);
	}

	@Provides @IntKey(Tools.FORMULA) @IntoMap static
	ToggleButton formula(TrackerImpl tracker, RadioMode model, Definitions definitions) {
		return newBtnSpan("formula", new FormuleHandler("Formule", definitions), tracker, model,"Formule");
	}

	@Provides @IntKey(Tools.TEXT) @IntoMap static
	ToggleButton text(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("text", new TextHandler("Label"), tracker, model, "Label bij punt");
	}

	@Provides @IntKey(Tools.TRAIL) @IntoMap static
	ToggleButton trail(TrackerImpl tracker, RadioMode model) {
		return newBtnSpan("trail", new TrailHandler(rb.Euclides_44()), tracker, model,rb.Euclides_44());
	}

}
