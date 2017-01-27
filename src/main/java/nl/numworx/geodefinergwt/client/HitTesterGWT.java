package nl.numworx.geodefinergwt.client;

import fi.euclides.event.HitTester;
import fi.euclides.model.Label;
import fi.euclides.model.Punt;
import fi.euclides.model.Triangle;
import gwt.awt.Shape;

public class HitTesterGWT extends HitTester {

	@Override
	public void visitPunt(Punt p) {
		// TODO Iets met Point sizes
		super.visitPunt(p);
	}

	@Override
	public void visitLabel(Label label) {
		Shape shape = label.adapt(Shape.class);
		if(shape != null) {
			if ( shape.contains(lastx, lasty))
				call(label);
			return;
		}
		super.visitLabel(label);
	}

	@Override
	public void visitTriangle(Triangle t) {
		Shape shape = t.adapt(Shape.class);
		if(shape != null) {
			if ( shape.contains(lastx, lasty))
				call(t);
			return;
		}
		super.visitTriangle(t);
	}

	@Override
	public HitTester copy() {
		return new HitTesterGWT();
	}

}
