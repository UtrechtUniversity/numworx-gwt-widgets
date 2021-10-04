package nl.numworx.geodefinergwt.client;

import java.util.Vector;

import fi.euclides.event.HitTester;
import fi.euclides.event.TrackerContext;
import fi.euclides.model.Boog;
import fi.euclides.model.Cirkel;
import fi.euclides.model.Kegelsnede2;
import fi.euclides.model.Label;
import fi.euclides.model.Lijn;
import fi.euclides.model.Locus;
import fi.euclides.model.Model;
import fi.euclides.model.Punt;
import fi.euclides.model.Segment;
import fi.euclides.model.Triangle;
import fi.euclides.model.Visitor;
import nl.numworx.geodefiner.common.Snapper;

class SnapperImpl extends Snapper implements Visitor {
	
	interface PH {
		void pmUp(int x, int y,  int id);
		void pmDrag(int x, int y, int id);
		Model getModel();
		HitTester getHitTester();
		TrackerContext getCtx(int id);
	}
	
	
	public boolean isGravityM() { return gravity && moved; }
	private boolean moved;
	boolean isMoved() {
		return moved;
	}
	void setMoved(boolean moved) {
		this.moved = moved;
	}
	
	boolean test;
	TrackerContext ctx;
	private HitTester hitTester;
	boolean hits(int x0, int y0, int id, PH ph) {
		if(hitTester == null) return false;
		test = false;
		ctx = ph.getCtx(id);
		hitTester.setXY(x0, y0);
		Vector<Punt> points = ph.getModel().getPunten();
		for(Punt p : points) {
			if (p.isDefined() && p.isVisible()) {
				p.visit(hitTester);
				if (test) return true;
			}
		}
		return false;
	}
	
	
	void pmUp(int x0, int y0, int id, PH ph) {
		if(isGravity()) {
			int ox = (int) ph.getModel().getO().getX().longValue();
			int dx = (int) ph.getModel().getU().getX().longValue() - ox;
			//System.out.print(ev.getX() + " " + ox + " " + dx);
			int x = (x0-ox) % dx;
			if ( x < 0 ) x += dx;
			if ( x*2 > dx) x -= dx;
			//System.out.println(" " + x);
			if(x > SNAP || x < -SNAP) x = 0;

			int oy = (int) ph.getModel().getO().getY().longValue();
			int dy = dx;
			//System.out.print(ev.getX() + " " + ox + " " + dx);
			int y = (y0-oy) % dy;
			if ( y < 0 ) y += dy;
			if ( y*2 > dy) y -= dy;
			//System.out.println(" " + x);
			if(y > SNAP || y < -SNAP) y = 0;
			if ( (x != 0 || y !=0) && !hits(x0, y0, id, ph))
			{ x0 = x0-x;
			  y0 = y0-y;
			}
		}
		ph.pmUp(x0, y0, id);

	}
	
	void pmDrag(int x0, int y0, int id, PH ph) {
		setMoved(true);
		if(isGravityM()) {
			int ox = (int) ph.getModel().getO().getXd();
			int dx = (int) ph.getModel().getU().getXd() - ox;
			//System.out.print(ev.getX() + " " + ox + " " + dx);
			int x = (x0-ox) % dx;
			if ( x < 0 ) x += dx;
			if ( x*2 > dx) x -= dx;
			//System.out.println(" " + x);
			if(x > SNAP || x < -SNAP) x = 0;

			int oy = (int) ph.getModel().getO().getYd();
			int dy = dx;
			//System.out.print(ev.getX() + " " + ox + " " + dx);
			int y = (y0-oy) % dy;
			if ( y < 0 ) y += dy;
			if ( y*2 > dy) y -= dy;
			//System.out.println(" " + x);
			if(y > SNAP || y < -SNAP) y = 0;

			if ( (x != 0 || y !=0) && !hits(x0, y0, id, ph))
            { x0 = x0-x;
              y0 = y0-y;
            }
		}
		ph.pmDrag(x0, y0, id);

	}
	@Override
	public void visitPunt(Punt p) {
		test = !ctx.isTracked(p);
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
	}
	@Override
	public void visitTriangle(Triangle t) {
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
	
	public void setPH(PH ph) {
		this.hitTester = ph.getHitTester().copy();
		this.hitTester.setVisitor(this);
	}

	
}