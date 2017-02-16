package nl.numworx.geodefinergwt.client;

import nl.numworx.geodefiner.common.Integral;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Segment;
import fi.euclides.model.SegmentVisitor;
import fi.euclides.model.math.Numbers;
import gwt.awt.geom.Area;
import gwt.awt.geom.Path2D;

class IntegralVisitor implements SegmentVisitor {
	private final double y0;
	private final Area shape;
	private final AbstractViewer v;

	IntegralVisitor(Integral l, Area shape, AbstractViewer v) {
		if(l.base > 0) y0 = clipTop().doubleValue();
		else if( l.base < 0) y0 = clipBottom().doubleValue();
		else y0 = v.getModel().getO().getYd();
		this.shape = shape;
		this.v = v;
	}

	@Override
	public void visitSegment(Segment s) {
		double x1 = s.getX1();
		double x2 = s.getX2();
		double y1 = s.getY1();
		double y2 = s.getY2();
		Path2D.Double path = new Path2D.Double();
		path.moveTo(x1, y0);
		path.lineTo(x1, y1);
		path.lineTo(x2, y2);
		path.lineTo(x2, y0);
		path.closePath();
		Area area = new Area(path);
		shape.add(area);
	}

	@Override
	public Numbers clipTop() {
		return Numbers.createDouble(Double.NEGATIVE_INFINITY);
	}

	@Override
	public Numbers clipBottom() {
		return Numbers.createDouble(Double.POSITIVE_INFINITY);
	}

	@Override
	public Numbers clipLeft() {
		return v.clipLeft();
	}

	@Override
	public Numbers clipRight() {
		return v.clipRight();
	}
}