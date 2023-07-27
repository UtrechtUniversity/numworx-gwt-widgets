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
import fi.euclides.model.algo.FreePoint;
import fi.euclides.model.math.Numbers;
import nl.numworx.geodefiner.common.Snapper;
import nl.numworx.geodefinergwt.client.SnapperImpl.PH;

class SnapperDoubleImpl extends Snapper implements Visitor {
		
	
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
/*
 * Deze routine werkt nooit goed, omdat meerdere punten kunnen meebewegen en daarmee altijd "in de buurt" van de muis zijn. 
 * Dat is niet alleen het punt onder de muis, maar ook alle andere afhankelijke punten.
 * Helaas voor Mieke's opgave
 */
	boolean hits(double x0, double y0, int id, PH ph) {
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
	
	
	void pmUp(int x0i, int y0i, int id, PH ph) {
		double x0 = x0i;
		double y0 = y0i;
		if(isGravity()) {
			double ox = ph.getModel().getO().getXd();
			double dx = ph.getModel().getU().getXd() - ox;
			//System.out.print(ev.getX() + " " + ox + " " + dx);
			double x = (x0-ox) % dx;
			if ( x < 0 ) x += dx;
			if ( x*2 > dx) x -= dx;
			//System.out.println(" " + x);
			if(x > SNAP || x < -SNAP) x = 0;

			double oy = ph.getModel().getO().getYd();
			double dy = dx;
			//System.out.print(ev.getX() + " " + ox + " " + dx);
			double y = (y0-oy) % dy;
			if ( y < 0 ) y += dy;
			if ( y*2 > dy) y -= dy;
			//System.out.println(" " + x);
			if(y > SNAP || y < -SNAP) y = 0;
			if ( (x != 0 || y !=0) && !hits(x0, y0, id, ph))
			{ x0 = x0-x;
			  y0 = y0-y;
			}
		}
		ph.pmUp(Numbers.createDouble(x0), Numbers.createDouble(y0), id);

	}
	
	void pmDrag(int x0i, int y0i, int id, PH ph) {
		double x0 = x0i;
		double y0 = y0i;
		setMoved(true);
		if(isGravityM()) {
			double ox = ph.getModel().getO().getXd();
			double dx = ph.getModel().getU().getXd() - ox;
			//System.out.print(ev.getX() + " " + ox + " " + dx);
			double x = (x0-ox) % dx;
			if ( x < 0 ) x += dx;
			if ( x*2 > dx) x -= dx;
			//System.out.println(" " + x);
			if(x > SNAP || x < -SNAP) x = 0;

			double oy = ph.getModel().getO().getYd();
			double dy = dx;
			//System.out.print(ev.getX() + " " + ox + " " + dx);
			double y = (y0-oy) % dy;
			if ( y < 0 ) y += dy;
			if ( y*2 > dy) y -= dy;
			//System.out.println(" " + x);
			if(y > SNAP || y < -SNAP) y = 0;

			//if ( (x != 0 || y !=0) && !hits(x0, y0, id, ph))
            { x0 = x0-x;
              y0 = y0-y;
            }
		}
		ph.pmDrag(Numbers.createDouble(x0), Numbers.createDouble(y0), id);

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
//		this.hitTester = ph.getHitTester().copy();
//		this.hitTester.setVisitor(this);
	}

	@Override
	public void snap(FreePoint fp, Model model) {
		if(fp != null && isGravity() && fp.isFree()) {
			double ox = model.getO().getXd();
			double dx = model.getU().getXd()-ox;
			double x0 = fp.getX().doubleValue();
			double x = (x0-ox)%dx;
			if (x < 0) x += dx;
			if (x*2 >dx) x =- dx;
			if (x > SNAP || x < -SNAP) x=0;
			
			double oy = model.getO().getYd();
			double dy = dx;
			double y0 = fp.getY().doubleValue();
			double y = (y0-oy)%dy;
			if (y < 0) y += dx;
			if (y*2 > dy) y -= dy;
			if (y > SNAP || y < -SNAP) y = 0;
			Numbers nx = Numbers.createDouble(x);
			Numbers ny = Numbers.createDouble(y);
			fp.moveTo( Numbers.sub(fp.getX(), nx), Numbers.sub(fp.getY(), ny));
		}
	}

	
}