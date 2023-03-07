package nl.numworx.geodefinergwt.client.toolbox;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntKey;
import dagger.multibindings.IntoMap;
import fi.euclides.event.Tracker;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.model.Punt;
import nl.numworx.geodefiner.common.Instance;
import nl.numworx.geodefiner.common.Tools;
import nl.numworx.geodefiner.common.UIShim;
import nl.numworx.geodefinergwt.client.ui.AngleModel;
import nl.numworx.geodefinergwt.client.ui.CircleModel;
import nl.numworx.geodefinergwt.client.ui.DashModel;
import nl.numworx.geodefinergwt.client.ui.LineModel;
import nl.numworx.geodefinergwt.client.ui.PointModel;
import nl.numworx.geodefinergwt.client.ui.SegmentModel;
import nl.numworx.geodefinergwt.client.ui.TextModel;
import nl.numworx.geodefinergwt.client.ui.TriangleModel;

@Module
@SuppressWarnings("unchecked")
public abstract class ShimModule {
	
	@Provides @Singleton @Named("point") static
	UIShim<? extends Destroyable, Void> point(PointModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Punt, Void>(ui, instance.getStateConfiguration(), tracker);
	}
	@Binds @IntKey(Tools.POINT) @IntoMap abstract UIShim<? extends Destroyable, Void>pointInMap(@Named("point") UIShim<? extends Destroyable, Void> point);
	
	@Provides @IntKey(Tools.ANGLE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> angle(AngleModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Label, Void>(ui, instance.getStateConfiguration(), tracker);
	}

	@Provides @IntKey(Tools.MIDPOINT) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> midpoint(PointModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Punt, Void>(ui, instance.getStateConfiguration(), tracker);
	}

	@Provides @IntKey(Tools.LINE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> line(LineModel ui, Instance instance, Tracker tracker,@Named("point") UIShim<? extends Destroyable, Void> point) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker, (UIShim<Destroyable, Void>) point);
	}
	@Provides @IntKey(Tools.PARALLEL) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> parallel(LineModel ui, Instance instance, Tracker tracker,@Named("point") UIShim<? extends Destroyable, Void> point) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker, (UIShim<Destroyable, Void>) point);
	}
	@Provides @IntKey(Tools.PERPENDICULAR) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> perpendicular(LineModel ui, Instance instance, Tracker tracker,@Named("point") UIShim<? extends Destroyable, Void> point) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker, (UIShim<Destroyable, Void>) point);
	}
	@Provides @IntKey(Tools.BISECTRICE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> bisectrice(LineModel ui, Instance instance, Tracker tracker,@Named("point") UIShim<? extends Destroyable, Void> point) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker, (UIShim<Destroyable, Void>) point);
	}
	@Provides @IntKey(Tools.TANGENT) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> tangent(LineModel ui, Instance instance, Tracker tracker,@Named("point") UIShim<? extends Destroyable, Void> point) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker, (UIShim<Destroyable, Void>) point);
	}
	@Provides @IntKey(Tools.POLELINE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> poleline(LineModel ui, Instance instance, Tracker tracker,@Named("point") UIShim<? extends Destroyable, Void> point) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker, (UIShim<Destroyable, Void>) point);
	}

	@Provides @IntKey(Tools.SEGMENT) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> segment(SegmentModel ui, Instance instance, Tracker tracker,@Named("point") UIShim<? extends Destroyable, Void> point) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker, (UIShim<Destroyable, Void>) point);
	}
	@Provides @IntKey(Tools.HALFLINE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> halfline(SegmentModel ui, Instance instance, Tracker tracker,@Named("point") UIShim<? extends Destroyable, Void> point) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker, (UIShim<Destroyable, Void>) point);
	}
	
	@Provides @IntKey(Tools.CIRCLE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> circle(CircleModel ui, Instance instance, Tracker tracker,@Named("point") UIShim<? extends Destroyable, Void> point) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker, (UIShim<Destroyable, Void>) point);
	}
	@Provides @IntKey(Tools.CIRCLE_WITH_RADIUS) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> circleWithRadius(CircleModel ui, Instance instance, Tracker tracker,@Named("point") UIShim<? extends Destroyable, Void> point) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker, (UIShim<Destroyable, Void>) point);
	}
	@Provides @IntKey(Tools.ARC) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> arc(CircleModel ui, Instance instance, Tracker tracker,@Named("point") UIShim<? extends Destroyable, Void> point) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker, (UIShim<Destroyable, Void>) point);
	}

	@Provides @IntKey(Tools.TRIANGLE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> triangle(TriangleModel ui, Instance instance, Tracker tracker,@Named("point") UIShim<? extends Destroyable, Void> point) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker, (UIShim<Destroyable, Void>) point);
	}

	@Provides @IntKey(Tools.ANGLE_POINT) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> anglepoint(PointModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Punt, Void>(ui, instance.getStateConfiguration(), tracker);
	}

	@Provides @IntKey(Tools.TEXT) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> text(TextModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Label, Void>(ui, instance.getStateConfiguration(), tracker);
	}
	
	@Provides @IntKey(Tools.LINE_PALETTE) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> linePalette(DashModel ui, Instance instance, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, instance.getStateConfiguration(), tracker);
	}

	@Provides @IntKey(Tools.ZOOM_IN) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> zoomin(ZoomInModel ui, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, null, tracker);
	}
	@Provides @IntKey(Tools.ZOOM_OUT) @IntoMap @Singleton static
	UIShim<? extends Destroyable, Void> zoomout(ZoomOutModel ui, Tracker tracker) {
		return new UIShim<Destroyable, Void>(ui, null, tracker);
	}
	
	
}
