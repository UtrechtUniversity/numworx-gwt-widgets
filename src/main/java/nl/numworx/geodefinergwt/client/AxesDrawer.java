package nl.numworx.geodefinergwt.client;

import nl.numworx.geodefiner.common.CELL;
import nl.numworx.geodefinergwt.client.ui.AxesModel;
import fi.euclides.gwt.ViewerWidget;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Boog;
import fi.euclides.model.Cirkel;
import fi.euclides.model.Destroyable;
import fi.euclides.model.ExtendedLijn;
import fi.euclides.model.Kegelsnede2;
import fi.euclides.model.Label;
import fi.euclides.model.Lijn;
import fi.euclides.model.Locus;
import fi.euclides.model.Model;
import fi.euclides.model.Punt;
import fi.euclides.model.Segment;
import fi.euclides.model.Triangle;
import fi.euclides.model.Visitor;

class AxesDrawer implements Visitor {
		ViewerWidget widget;
		AbstractViewer viewer;
			
		AxesDrawer(ViewerWidget widget) {
			super();
			this.widget = widget;
			viewer = widget.getViewer();
			ll.setClip(viewer.clipLeft(), viewer.clipTop(), viewer.clipRight(), viewer.clipBottom());
		}

		public void visitBoog(Boog b) {
			viewer.visitBoog(b);
		}

		public void visitCirkel(Cirkel c) {
			viewer.visitCirkel(c);
		}

		public void visitLijn(Lijn l) {
			viewer.visitLijn(l);
		}

		public void visitTriangle(Triangle t) {
			viewer.visitTriangle(t);
		}

		public void visitKegelsnede(Kegelsnede2 k) {
			viewer.visitKegelsnede(k);
		}

		public void visitLocus(Locus l) {
			viewer.visitLocus(l);
		}

		public void visitPunt(Punt punt) {
			viewer.visitPunt(punt);
		}

		public void visitSegment(Segment s) {
			viewer.visitSegment(s);
		}

		public void visitLabel(Label label) {
			viewer.visitLabel(label);
		}

		ExtendedLijn ll = new ExtendedLijn();
		private String color;
		private String background = "white";
		
		String getBackground() {
			return background;
		}

		AxesDrawer setBackground(String background) {
			this.background = background;
			return this;
		}

		void drawAxes() {
			Destroyable grid = getModel().getLijnen().elementAt(2);
			if(grid.isVisible()) grid.visit(this);
	//draw grid
			Lijn x = (Lijn) getModel().getLijnen().firstElement();
			if(grid.isVisible() && !x.isVisible() && x.isDefined()) {
				// draw x in grid mode
				ll.setLijn(x);
				widget.drawLine(ll.getX1(), ll.getY1() , ll.getX2(), ll.getY2());		
			}
			Lijn y = (Lijn) getModel().getLijnen().elementAt(1);
			if(grid.isVisible() && !y.isVisible() && y.isDefined()) {
				// draw y in grid mode
				ll.setLijn(y);
				widget.drawLine(ll.getX1(), ll.getY1() , ll.getX2(), ll.getY2());		
			}
			color = "black"; widget.setColor(0);
			CELL item = x.adapt(CELL.class);
			boolean bx = false, by = false;
			if (item != null) {
				AxesModel configX = (AxesModel) item.config;
				bx = configX != null && configX.numbers && x.isVisible();
				if(bx) { drawXnumbers(); }
			}
			item = y.adapt(CELL.class);
			if (item != null) {
				AxesModel configY = (AxesModel) item.config;
				by = configY != null && configY.numbers && y.isVisible();
				if(by) { drawYnumbers(); }
			}
			if (bx || by) drawO();
		}

		private Model getModel() {
			return viewer.getModel();
		}
		private void drawO() {
			double x, y;
			x = getModel().getO().getXd();
			y = getModel().getO().getYd();
			color = "black";
			widget.drawString("0", x, y, ViewerWidget.TEXT_END, ViewerWidget.TEXT_TOP, background );
		}

		private void drawXnumbers() {
			double left = viewer.clipLeft().doubleValue();
			double right = viewer.clipRight().doubleValue();
			double x = getModel().getO().getXd();
			double y = getModel().getO().getYd();
			double dx = getModel().getU().getXd() - x;
			if(dx <= 1) return;
			int i = 0, s = 1;
			while(dx < 20) { dx += dx; s+=s; if(dx >= 20) break; dx = 2.5*dx; s += s+s/2; if(dx >= 20) break; dx += dx; s += s; }
			left -= dx;i=s;
			for(double xr = x+dx ; xr < right; xr += dx, i+=s) {
				String value = String.valueOf(i);
				widget.drawString(value, xr, y, ViewerWidget.TEXT_MIDDLE, ViewerWidget.TEXT_TOP, background);
			}
			i = -s;
			for(double xr = x-dx ; xr > left; xr -= dx, i-=s) {
				String value = String.valueOf(i);
				widget.drawString(value, xr, y, ViewerWidget.TEXT_MIDDLE, ViewerWidget.TEXT_TOP, background);
			}
			
		}

		private void drawYnumbers() {
			double bottom = viewer.clipBottom().doubleValue();
			double top = viewer.clipTop().doubleValue();
			double x = getModel().getO().getXd()-2;
			double y = getModel().getO().getYd();
			double dy = getModel().getU().getXd() - x -2;
			if (dy <= 1) return;
			int i = 0, s = 1;
			while(dy < 20) { dy += dy; s+=s; if(dy >= 20) break; dy = 2.5*dy; s += s+s/2; if(dy >= 20) break; dy += dy; s += s; }
			i=s;
			for(double yr = y-dy ; yr > top; yr -= dy, i+=s) {
				String value = String.valueOf(i);
				widget.drawString(value, x, yr, ViewerWidget.TEXT_END, ViewerWidget.TEXT_CENTRAL, background);
			}
			i = -s;
			bottom += dy;
			for(double yr = y+dy; yr < bottom; yr += dy, i-=s) {
				String value = String.valueOf(i);
				widget.drawString(value, x, yr, ViewerWidget.TEXT_END, ViewerWidget.TEXT_CENTRAL, background);
			}
		}

}
