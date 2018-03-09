package nl.numworx.geodefinergwt.client.module;

import java.util.HashMap;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import fi.euclides.event.Tracker;
import fi.euclides.gwt.ViewerWidget;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Destroyable;
import fi.euclides.proof.LabelDelegate;
import nl.numworx.geodefiner.common.Definitions;
import nl.numworx.geodefiner.common.NamingModel;
import nl.numworx.geodefiner.common.math.Expression;
import nl.numworx.geodefinergwt.client.TrackerImpl;
import nl.numworx.geodefinergwt.client.ui.HerleidList;

@Module
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
	@Provides @Singleton static Expression expression() {
		return new Expression();
	}
	@Provides @Singleton static Definitions definitions(Tracker t) {
		return new Definitions(t);
	}
	
	@Provides
	@IntoMap
	@StringKey("list1.list") static LabelDelegate herleidList(HerleidList delegate) {
		return delegate;
	}
	
}
