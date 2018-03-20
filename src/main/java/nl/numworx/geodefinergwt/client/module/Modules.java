package nl.numworx.geodefinergwt.client.module;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;
import dagger.multibindings.StringKey;
import fi.euclides.event.Tracker;
import fi.euclides.gwt.ViewerWidget;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Destroyable;
import fi.euclides.proof.LabelDelegate;
import nl.numworx.geodefiner.common.CheckObjectList;
import nl.numworx.geodefiner.common.Definitions;
import nl.numworx.geodefiner.common.Instance;
import nl.numworx.geodefiner.common.NamingModel;
import nl.numworx.geodefiner.common.math.Expression;
import nl.numworx.geodefiner.common.math.ToC;
import nl.numworx.geodefinergwt.client.TrackerImpl;
import nl.numworx.geodefinergwt.client.toolbox.ToolBoxModule;
import nl.numworx.geodefinergwt.client.ui.HerleidList;

@Module(includes= {ToolBoxModule.class})
public class Modules {
	
	@Provides static AbstractViewer viewer(ViewerWidget w) {
		return w.getViewer();
	}

	@Provides static Tracker tracker(TrackerImpl impl) {
		return impl;
	}

	@Provides @Singleton static NamingModel namingModel(AbstractViewer impl) {
		return new NamingModel(impl, new HashMap<String, Destroyable>());
	}

	@Provides @Singleton static Expression expression(Map<String, LabelDelegate> symbols) {
		Expression expression = new Expression();
		expression.symbolmap.putAll(symbols);
		return expression;
	}

	@Provides @Singleton static Definitions definitions(Tracker t) {
		return new Definitions(t);
	}
	
	@Provides
	@IntoMap
	@StringKey("list1.list") static LabelDelegate herleidList(HerleidList delegate) {
		return delegate;
	}
	
	@Provides static CheckObjectList checkObjectList(Tracker t, Expression e, Instance instance) {
		CheckObjectList l = new CheckObjectList(t,e);
		l.setInstance(instance);
		return l;
	}
	
	@Provides @IntoSet static LabelDelegate toc() {
		return new ToC();
	}
}
