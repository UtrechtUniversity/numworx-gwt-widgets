package nl.numworx.geodefinergwt.client.module;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;
import dagger.multibindings.StringKey;
import fi.euclides.event.EventHandler;
import fi.euclides.event.Tracker;
import fi.euclides.gwt.ViewerWidget;
import fi.euclides.gwt.canvas.PanHandler;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Destroyable;
import fi.euclides.proof.LabelDelegate;
import nl.numworx.geodefiner.common.CheckObjectList;
import nl.numworx.geodefiner.common.Definitions;
import nl.numworx.geodefiner.common.Instance;
import nl.numworx.geodefiner.common.NamingModel;
import nl.numworx.geodefiner.common.Randomizer;
import nl.numworx.geodefiner.common.math.Expression;
import nl.numworx.geodefiner.common.math.ToC;
import nl.numworx.geodefinergwt.client.CanvasViewer;
import nl.numworx.geodefinergwt.client.DefinitionsGWT;
import nl.numworx.geodefinergwt.client.GWTRandomizer;
import nl.numworx.geodefinergwt.client.GeoDefinerGWT;
import nl.numworx.geodefinergwt.client.TrackerImpl;
import nl.numworx.geodefinergwt.client.toolbox.ShimModule;
import nl.numworx.geodefinergwt.client.toolbox.ToolBoxModule;
import nl.numworx.geodefinergwt.client.ui.HerleidList;

@Module(includes= {ToolBoxModule.class, ShimModule.class})
public abstract class Modules {
	
//	@Provides static AbstractViewer viewer(CanvasViewer w) {
//		return w.getViewer();
//	}

	@Binds abstract Tracker tracker(TrackerImpl impl);
	//@Binds abstract SpeelVeld speelVeld(CanvasViewer canvas);
	@Binds abstract ViewerWidget viewerWidget(CanvasViewer canvas);
	@Binds abstract AbstractViewer viewer(CanvasViewer w);
	@Binds abstract Randomizer randomizer(GWTRandomizer r);
	@Binds abstract Definitions definitions(DefinitionsGWT definitions);
	
	@Provides @Singleton static NamingModel namingModel(CanvasViewer impl) {
		return new NamingModel(impl, new HashMap<String, Destroyable>());
	}

	@Provides @Singleton static Expression expression(Map<String, LabelDelegate> symbols) {
		Expression expression = new Expression();
		expression.symbolmap.putAll(symbols);
		return expression;
	}

//	@Provides @Singleton static Definitions definitions(Tracker t) {
//		return new Definitions(t);
//	}
	
	@Binds
	@IntoMap
	@StringKey("list1.list") abstract LabelDelegate herleidList(HerleidList delegate);
	
	@Provides static CheckObjectList checkObjectList(Tracker t, Expression e, Instance instance) {
		CheckObjectList l = new CheckObjectList(t,e);
		l.setInstance(instance);
		return l;
	}
	
	@Provides @IntoSet static LabelDelegate toc() {
		return new ToC();
	}

// ons kent ons
//	@Provides static CanvasViewer canvasViewer(ViewerWidget w) {
//	  return (CanvasViewer) w;
//	}
	
	@Provides @Reusable @Named("panHandler") static EventHandler getPanHandler(CanvasViewer w) {
	  return new PanHandler(GeoDefinerGWT.MESSAGES.Euclides_41(), w);
	}
	@Provides @Named("expressions") static Map<String,String> expressions() {
	  return new LinkedHashMap<String, String>();
    }

}
