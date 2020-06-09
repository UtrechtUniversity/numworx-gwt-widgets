package nl.numworx.geodefinergwt.client.toolbox;

import javax.inject.Singleton;

import com.google.gwt.core.client.GWT;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntKey;
import dagger.multibindings.IntoMap;
import fi.euclides.event.Tracker;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Punt;
import nl.numworx.geodefiner.common.Instance;
import nl.numworx.geodefiner.common.Tools;
import nl.numworx.geodefiner.common.UIShim;
import nl.numworx.geodefinergwt.client.ui.CircleModel;
import nl.numworx.geodefinergwt.client.ui.LineModel;
import nl.numworx.geodefinergwt.client.ui.PointModel;
import nl.numworx.geodefinergwt.client.ui.SegmentModel;

@Module
public abstract class ShimModule {
	
	@Provides @IntKey(Tools.POINT) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> point(PointModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Punt, Void>(ui, instance.getStateConfiguration(), tracker);
	}
	@Provides @IntKey(Tools.MIDPOINT) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> midpoint(PointModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Punt, Void>(ui, instance.getStateConfiguration(), tracker);
	}

	@Provides @IntKey(Tools.LINE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> line(LineModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker);
	}
	@Provides @IntKey(Tools.PARALLEL) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> parallel(LineModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker);
	}
	@Provides @IntKey(Tools.PERPENDICULAR) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> perpendicular(LineModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker);
	}
	@Provides @IntKey(Tools.BISECTRICE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> bisectrice(LineModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker);
	}
	@Provides @IntKey(Tools.TANGENT) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> tangent(LineModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker);
	}
	@Provides @IntKey(Tools.POLELINE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> poleline(LineModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker);
	}

	@Provides @IntKey(Tools.SEGMENT) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> segment(SegmentModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker);
	}
	@Provides @IntKey(Tools.HALFLINE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> halfline(SegmentModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker);
	}
	
	@Provides @IntKey(Tools.CIRCLE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> circle(CircleModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker);
	}
	@Provides @IntKey(Tools.CIRCLE_WITH_RADIUS) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> circleWithRadius(CircleModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker);
	}
	@Provides @IntKey(Tools.ARC) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> arc(CircleModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker);
	}

	@Provides @IntKey(Tools.TRIANGLE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> triangle(CircleModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker);
	}

}
