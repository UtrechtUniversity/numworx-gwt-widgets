package nl.numworx.fsmgwt.client;

import fi.euclides.event.EventHandler;
import fi.euclides.gwt.canvas.SpeelVeld;
import fi.euclides.model.Boog;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Dpunt;
import fi.euclides.model.Punt;
import fi.euclides.model.Segment;
import fi.euclides.model.VrijPunt;
import nl.numworx.fsm.shared.FSMMapper;
import nl.numworx.fsm.shared.Hits;
import nl.numworx.fsm.shared.Hoekpunt;
import nl.numworx.fsm.shared.UnifiedHandler;

public class CanvasViewer extends SpeelVeld {

	private FSMMapper mapper;

	public CanvasViewer(int width, int height) {
		super(width, height);
		pointSize = 75;
		hitTester = new Hits();
		mapper = new FSMMapper();
		EventHandler eventHandler;
		
		eventHandler = new UnifiedHandler("FSM");
		
		eventHandler.setTracker(this);
		setPointerHandler(eventHandler);
	}
	@Override
	public void visitPunt(Punt punt) {
		selectColor(punt);
		context.setLineWidth(3);
		float p2 = pointSize/2f;
		if (punt instanceof Dpunt || punt instanceof Hoekpunt) {
			p2 = 3f;
			fillCircle(punt.getXd()-p2, punt.getYd()-p2 , p2*2);
			return;
		}
		
		drawCircle(punt.getXd()-p2, punt.getYd()-p2 , pointSize);
		
		String name = mapper.toString(punt);
		if (name != null) {
			drawString(name, punt.getXd(), punt.getYd());
		}
	}

	@Override
	public void visitSegment(Segment s) {
		selectColor(s);
		context.setLineWidth(3);
		double hyp = Math.hypot(s.getDX(), s.getDY());
		double x1 = s.getX1() + pointSize/2.0 * s.getDX() / hyp;
		double y1 = s.getY1() + pointSize/2.0 * s.getDY() / hyp;
		double x2 = s.getX2() - pointSize/2.0 * s.getDX() / hyp;
		double y2 = s.getY2() - pointSize/2.0 * s.getDY() / hyp;
		String name = mapper.toString(s);
		Segment o = s;
		s = new Segment(new VrijPunt(x1, y1), new VrijPunt(x2, y2));
		drawTips(s, o);
		drawLine(x1, y1, x2, y2);
		
		if (name != null) {
			drawString(name, (x1+x2)/2, (y1+y2)/2);
		}
	}


	private Segment drawTips(Segment s, Destroyable o) {
		
		selectColor(o);
		double dx = s.getDX();
		double dy = s.getDY();
		double len = Math.hypot(dx, dy);
		Float width = 3f; // segment width
		double tiplen = 5;
		if(width != null) tiplen *= width.doubleValue();
		if(len < tiplen*3) tiplen = len/3;
		dx *= tiplen / len; 
		dy *= tiplen/len; 
		tip(s.getP2(), -dx, -dy, s.getX2(), s.getY2()); 
		return s;
	}

	private void tip(Punt p1, double dx, double dy, double xd, double yd) {
		double x = (float)p1.getXd();
		double y = (float)p1.getYd();
		context.beginPath();
		context.moveTo(x, y);
		context.lineTo(x + dx + dy/2, y + dy -dx/2);
		context.lineTo(x + dx - dy/2, y + dy +dx/2);
		context.closePath();
		context.fill();
		
	}

	@Override
	public void visitBoog(Boog b) {
		selectColor(b);
		Float width = 3f; // segment width
		context.setLineWidth(3);
		double d = b.getD();
		double s = b.getStart();
		double l = b.length();
		drawArc(b.getX(), b.getY(), d, s, l);
		Punt end = Boog.endOf(b);
		Punt center = b.getCenter();
		double xd = end.getXd();
		double yd = end.getYd();
		double dx = xd - center.getXd();
		double dy = yd - center.getYd();
		double tiplen = 5;
		if(width != null) tiplen *= width.doubleValue();
		double len = Math.hypot(dx, dy);
		if(len < tiplen*3) tiplen = len/3;
		dx *= tiplen / len; 
		dy *= tiplen/len; 
		
		tip(end, -dy, dx, xd, yd);		
		String name = mapper.toString(b);
		if (name != null) {
			Punt punt = b.getCenter();
			drawString(name, punt.getXd(), punt.getYd());
		}
	}

}
