package nl.numworx.geodefinergwt.client.ui;

import java.util.Map;

import nl.numworx.geodefiner.common.Animate;
import nl.numworx.geodefiner.common.Animator;
import nl.numworx.geodefiner.common.StepValue;
import nl.numworx.geodefiner.common.UIModel;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import fi.euclides.model.Destroyable;
import fi.euclides.model.HorizontalPunt;
import fi.euclides.model.Label;
import fi.euclides.model.Segment;
import fi.euclides.model.math.Numbers;
import fi.euclides.util.DefaultAdapter;

public class IntervalModel extends TextModel {
	Animate animate = Animate.NONE;
	double length = 50;
	int interval = 2000;
	Double step, width;
	
	@Override
	public void install() {
		DefaultAdapter adapter = DefaultAdapter.getDefault(item);
		Animator instance = adapter.adapt(Animator.class);
		if (instance != null) instance.install(null);
		if (animate == Animate.NONE) {
			adapter.put(Animator.class, null);
			instance = null;
		} else {
			instance = new Animator(animate, interval, tracker, align);
			adapter.put(instance);
		}
		DefaultAdapter adapterP = DefaultAdapter.getDefault(item.getP());
		if (step == null)
		{
			adapter.put(StepValue.class, null);
			adapterP.put(Label.class, null);
		}
		else
		{	Label min = (Label) item.getDepend()[0];
			adapter.put(StepValue.class, new StepValue(Numbers.createDouble(step.doubleValue()),min));
			adapterP.put(Label.class, item);
		}
		super.install();
		ColorStyle css = item.adapt(ColorStyle.class);
		adapterP.put(css);
		Destroyable segment = item.getP().getDepend()[0];
		DefaultAdapter.getDefault(segment).put(css);
		if(width != null) {
			DefaultAdapter.getDefault(segment).put(new StrokeStyle(width.doubleValue(),null));
			adapterP.put(Float.class, 5.0f * width.floatValue());
		} else {
			DefaultAdapter.getDefault(segment).put(StrokeStyle.class, null);
			adapterP.put(Float.class, null);
		}
		HorizontalPunt hp = (HorizontalPunt) ((Segment) segment).getP2();
		hp.setDistance(Numbers.createDouble(length));
		
		if(instance != null) {
			Label button = instance.install(item);
			DefaultAdapter.getDefault(button).put(font);
		}

	}

	@Override
	public UIModel<Label, Void> init(Label item) {
		Animator animator = item.adapt(Animator.class);
		if(animator != null) {
			animate = animator.animate;
			interval = animator.interval;
		}
		Numbers step = item.adapt(Numbers.class);
		if(step != null) {
			this.step = step.doubleValue();
		} else
			this.step = null;
		
		Segment s = (Segment) item.getP().getDepend()[0];
		length = s.getDX();
		return super.init(item);
	}

//	@Override
//	public Map<String, Object> toMap() {
//		Map<String, Object> map = super.toMap();
////		map.put("animate", animate.name());
////		map.put("interval", interval);
////		map.put("length", length);
////		if(step!=null) map.put("step", step); else map.remove("step"); 
////		if(width!= null) map.put("width", width);else map.remove("width");
//		return map;
//	}

	@Override
	public void fromMap(ObjectMap map) {
		if (map.containsKey("animate"))
			animate = Animate.valueOf(map.getString("animate"));
		else
			animate = Animate.NONE;
		if (map.containsKey("interval"))
			interval = map.getInt("interval");
		else
			interval = 2000;
		if (map.containsKey("length"))
			length = map.getDouble("length");
		else
			length = 50.0;
		if (map.containsKey("step"))
			step = map.getDouble("step");
		else
			step = null;
		if (map.containsKey("width"))
			width = map.getDouble("width");
		else
			width = null;
		super.fromMap(map);
	}

}
