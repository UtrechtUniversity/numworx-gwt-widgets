package nl.numworx.geodefinergwt.client.ui;

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
import fi.euclides.util.DefaultAdapter;
import fi.euclides.util.Observable;
import fi.euclides.util.Observer;

public enum UserConfig implements Visitor, Observer {
	INSTANCE;
	static final Float USER_POINT_SIZE = Float.valueOf(7f);
	static final ColorStyle USER_TRIANGLE_COLOR = new ColorStyle("#6e5000",0xFF6E5000);
	static final FillStyle USER_TRIANGLE_PAINT = new FillStyle("rgba(220,160,0,0.5)",0x80dca000);
	@Override
	public void update(Observable observable, Object arg) {
		if(arg instanceof Destroyable) 
			((Destroyable) arg).visit(this);
	}

	@Override
	public void visitPunt(Punt p) {
		DefaultAdapter adapter = DefaultAdapter.getDefault(p);
		adapter.put(USER_POINT_SIZE);
	}

	@Override
	public void visitLijn(Lijn l) {
	}

	@Override
	public void visitCirkel(Cirkel c) {
	}

	@Override
	public void visitSegment(Segment s) {
	}

	@Override
	public void visitLabel(Label label) {
		DefaultAdapter adapter = DefaultAdapter.getDefault(label);
		adapter.put(Boolean.TRUE);
	}

	@Override
	public void visitTriangle(Triangle t) {
		DefaultAdapter adapter = DefaultAdapter.getDefault(t);
		adapter.put(USER_TRIANGLE_PAINT);
		adapter.put(USER_TRIANGLE_COLOR);
	}

	@Override
	public void visitKegelsnede(Kegelsnede2 k) {
	}

	@Override
	public void visitLocus(Locus l) {
	}

	@Override
	public void visitBoog(Boog b) {
	}
	
}
