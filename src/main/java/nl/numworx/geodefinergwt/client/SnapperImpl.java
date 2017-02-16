package nl.numworx.geodefinergwt.client;

import fi.euclides.model.Model;
import nl.numworx.geodefiner.common.Snapper;

class SnapperImpl extends Snapper {
	
	interface PH {
		void pmUp(int x, int y);
		void pmDrag(int x, int y);
		Model getModel();
	}
	
	
	final int SNAP = 3;
	boolean isGravity() { return gravity && moved; }
	private boolean moved;
	boolean isMoved() {
		return moved;
	}
	void setMoved(boolean moved) {
		this.moved = moved;
	}
	
	void pmUp(int x0, int y0, PH ph) {
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

			x0 = x0-x;
			y0 = y0-y;
		}
		ph.pmUp(x0, y0);

	}
	
	void pmDrag(int x0, int y0, PH ph) {
		setMoved(true);
		if(isGravity()) {
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

			x0 = x0-x;
			y0 = y0-y;
		}
		ph.pmDrag(x0, y0);

	}
}