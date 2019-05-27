package nl.numworx.geodefinergwt.client;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

import fi.euclides.event.HitTester;
import fi.euclides.gwt.ViewerWidget;
import fi.euclides.model.Boog;
import fi.euclides.model.Cirkel;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Kegelsnede2;
import fi.euclides.model.Label;
import fi.euclides.model.Lijn;
import fi.euclides.model.Locus;
import fi.euclides.model.Punt;
import fi.euclides.model.Segment;
import fi.euclides.model.Triangle;
import fi.euclides.model.Visitor;
import nl.numworx.geodefiner.common.Grid;
import nl.numworx.geodefinergwt.client.ui.StrokeStyle;

class HighLighter implements Visitor, MouseDownHandler, MouseUpHandler, MouseMoveHandler {

	interface GeoDefinerWidget extends ViewerWidget {

		float getPointSize();
		void setPointSize(float f);
		
		StrokeStyle getStroke();
		void setStroke(StrokeStyle stroke);
		int getOffX();
		int getOffY();
	}
	
	
	private HitTester hits;
	private GeoDefinerWidget widget;


	HighLighter(HitTester hits, GeoDefinerWidget w) {
		this.hits = hits;
		hits.setVisitor(this);
		widget = w;

	}

	@Override
	public void visitPunt(Punt p) {
		widget.setPointSize(widget.getPointSize()+ 2f);
	}

	private void thickerStroke() {
		StrokeStyle s = widget.getStroke();
		widget.setStroke(new StrokeStyle(s.lineWidth+1.0, s.dash));
	}
	
	@Override
	public void visitLijn(Lijn l) {
		thickerStroke();
	}

	@Override
	public void visitCirkel(Cirkel c) {
		thickerStroke();
	}

	@Override
	public void visitSegment(Segment s) {
		thickerStroke();
	}

	@Override
	public void visitLabel(Label label) {
	}

	@Override
	public void visitTriangle(Triangle t) {
		thickerStroke();
	}

	@Override
	public void visitKegelsnede(Kegelsnede2 k) {
		thickerStroke();
	}

	@Override
	public void visitLocus(Locus l) {
	  if (!(l instanceof Grid))
		thickerStroke();
	}

	@Override
	public void visitBoog(Boog b) {
		thickerStroke();
	}

	public void hilight(Destroyable d) {
		d.visit(hits);
		hits.done();
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		onEvent(event);
	}

	private void onEvent(MouseEvent<?> event) {
		int x = event.getX()-widget.getOffX();
		int y = event.getY()-widget.getOffY();
		hits.setXY(x, y);
		widget.getViewer().paint();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		onEvent(event);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		onEvent(event);
	}

}
