package nl.numworx.geodefinergwt.client.module;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.gwt.user.client.ui.Label;

import dagger.BindsInstance;
import dagger.Component;
import nl.numworx.geodefiner.common.Instance;
import nl.numworx.geodefinergwt.client.CanvasViewer;
import nl.numworx.geodefinergwt.client.GeoDefinerGWT;

@Component(modules={Modules.class})
@Singleton
public interface Components {
	void provideComponent(GeoDefinerGWT main);
	@Component.Builder
	interface Builder {
		Components build();
		@BindsInstance Builder widget(CanvasViewer w);
		@BindsInstance Builder status(@Named("status") Label status);
		@BindsInstance Builder instance(Instance instance);
		
	}
}
