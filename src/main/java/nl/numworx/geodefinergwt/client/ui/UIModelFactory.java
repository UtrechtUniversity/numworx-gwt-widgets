package nl.numworx.geodefinergwt.client.ui;

import nl.numworx.geodefiner.common.UIModel;
import fi.euclides.event.Tracker;
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

public class UIModelFactory extends nl.numworx.geodefiner.common.UIModelFactory implements Visitor  {

	private UIModel<?, Void> model;
	private Tracker tracker;
	
	
	public UIModelFactory(Tracker tracker) {
		this.tracker = tracker;
	}

	@Override
	public UIModel<?, ?> build(Destroyable d) {
		if(d == null) throw new NullPointerException();
		model = new ColorModel<Destroyable>().init(d);
		d.visit(this);
		return model.set(tracker);
	}

	@Override
	public void visitPunt(Punt p) {
		model = new PointModel().init(p);
	}

	@Override
	public void visitLijn(Lijn l) {
		model = new LineModel().init(l);
	}

	@Override
	public void visitCirkel(Cirkel c) {
		model = new CircleModel().init(c);
	}

	@Override
	public void visitSegment(Segment s) {
		model = new LineModel().init(s);
	}

	@Override
	public void visitLabel(Label label) {
		model = new TextModel().init(label);
	}

	@Override
	public void visitTriangle(Triangle t) {
		model = new ColorModel<Triangle>().init(t);
	}

	@Override
	public void visitKegelsnede(Kegelsnede2 k) {
	}

	@Override
	public void visitLocus(Locus l) {
		model = new LineModel().init(l);
	}

	@Override
	public void visitBoog(Boog b) {
		model = new CircleModel().init(b);
	}

}
