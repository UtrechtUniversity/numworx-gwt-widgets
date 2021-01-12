package nl.numworx.geodefinergwt.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import fi.euclides.event.HitTester;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.model.Punt;
import fi.euclides.model.Triangle;
import gwt.awt.Shape;

public class HitTesterGWT extends HitTester {

	@Override
	public void visitPunt(Punt p) {
		double old = marge;
		try {
			float pointSize;
			Float ps = p.adapt(Float.class);
			if(ps != null) {
				pointSize = ps.floatValue();
				marge = Math.max(marge, pointSize/2);
			} 
			super.visitPunt(p);
		} finally {
			marge = old;
		}
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

	protected Collection<Destroyable> triangles = new LinkedList<Destroyable>();

	public void done() {
		if(triangles != null) {
			Iterator<Destroyable> iter = triangles.iterator();
			while (iter.hasNext()) {
				Destroyable object = iter.next();
				super.call(object);
			}
			triangles.clear();
		} else 
			triangles = new LinkedList<Destroyable>();
		
	}
	
	protected void call(Destroyable d) {
		if(d instanceof Triangle) {
			if(triangles != null)
			{
				triangles.add(d); // delay...
			}
			return;
		}
		super.call(d); // d.visit(v);
		triangles = null;
	}

}
