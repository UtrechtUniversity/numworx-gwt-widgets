package nl.numworx.geodefinergwt.client;

import java.util.Vector;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Widget;

import dagger.Lazy;
import fi.euclides.event.NameMapper;
import fi.euclides.event.TrackerContext;
import fi.euclides.gwt.RectShape;
import fi.euclides.gwt.ViewerWidget;
import fi.euclides.gwt.canvas.SpeelVeld;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Boog;
import fi.euclides.model.Cirkel;
import fi.euclides.model.Destroyable;
import fi.euclides.model.GeoImage;
import fi.euclides.model.Label;
import fi.euclides.model.Lijn;
import fi.euclides.model.Locus;
import fi.euclides.model.MP;
import fi.euclides.model.Punt;
import fi.euclides.model.Ray;
import fi.euclides.model.Segment;
import fi.euclides.model.SegmentVisitor;
import fi.euclides.model.TrailBuilder;
import fi.euclides.model.Triangle;
import fi.euclides.model.math.Numbers;
import fi.euclides.proof.FlipFlop;
import fi.euclides.util.Adaptee;
import fi.euclides.util.Adapter;
import fi.euclides.util.DefaultAdapter;
import gwt.awt.Rectangle;
import gwt.awt.Shape;
import gwt.awt.geom.Area;
import gwt.awt.geom.Path2D;
import gwt.awt.geom.PathIterator;
import nl.numworx.geodefiner.common.Align;
import nl.numworx.geodefiner.common.CheckObject;
import nl.numworx.geodefiner.common.Hoekpunt;
import nl.numworx.geodefiner.common.Instance.Selector;
import nl.numworx.geodefiner.common.Integral;
import nl.numworx.geodefiner.common.Interval;
import nl.numworx.geodefiner.common.ShortSegment;
import nl.numworx.geodefiner.common.Snapper;
import nl.numworx.geodefiner.common.Tips;
import nl.numworx.geodefinergwt.client.ui.ColorStyle;
import nl.numworx.geodefinergwt.client.ui.FillStyle;
import nl.numworx.geodefinergwt.client.ui.FontStyle;
import nl.numworx.geodefinergwt.client.ui.StrokeStyle;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

public class CanvasViewer extends SpeelVeld implements SnapperImpl.PH, HighLighter.GeoDefinerWidget, TrailBuilder {
	private static final String SELECT_COLOR = "rgba(200,128,128,0.4)";

    static final FontStyle FONT_STYLE = new FontStyle();

	private static final float DEFAULT_POINTSIZE = 5;
	private static final StrokeStyle DEFAULT_STROKE = new StrokeStyle(1, null);

	private static final ColorStyle COLOR_GREEN   = new ColorStyle(0xFF008000);
	private static final ColorStyle COLOR_RED     = new ColorStyle(0xFFFF0000);
	private static final ColorStyle COLOR_BLUE    = new ColorStyle(0xFF0000FF);
	private static final ColorStyle COLOR_MAGENTA = new ColorStyle(0xFFFF00FF);
	private static final ColorStyle COLOR_POINTER = new ColorStyle(0xFF404040);
	private static final ColorStyle COLOR_BLACK   = new ColorStyle(0xFF000000);
	private static final ColorStyle COLOR_LT_GRAY = new ColorStyle(0xFFC0C0C0);
	private static final ColorStyle COLOR_WHITE   = new ColorStyle(-1);

	private AnimationHandle animator;
	private boolean down;
	private String background = "white";
	private HighLighter hiLighter;
	private ColorStyle select;
	
	private NameMapper mapper = super.getMapper();
	@Override public NameMapper getMapper() { return mapper; }
	@Override public void setMapper(NameMapper n) { mapper = n; }

	public CanvasViewer(int width, int height) {
		super(width, height);
		hitTester = new HitTesterGWT();
		asWidget().addStyleName("canvas");		
		getModel().setTrailBuilder(this);
		snapper.setPH(this);
	}

	void enableHighLight(Selector selector) {
		hiLighter = new HighLighter(hitTester.copy(), this, selector);
		canvas.addMouseMoveHandler(hiLighter);
	}
	
	
	public CanvasViewer() {
		this(200, 200);
	}
	
	public  void cancel() {
	  if (animator != null) {
	    GWT.log("animator canceled");
	    animator.cancel();
	    animator = null;
	  }
	}
	
	/** Lazy
	 * @see fi.euclides.gwt.svg.SVGWidget#paint()
	 */
	@Override
	public void paint() {
		if(animator == null)
		animator = AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
			
			@Override
			public void execute(double timestamp) {
				animator = null;
				doPaint();
			}
		}, canvas.getElement());
	}

	private void doPaint() {
		super.paint();
	}

	SnapperDoubleImpl snapper = new SnapperDoubleImpl();
	private StrokeStyle stroke;

    private Lazy<Tracer> tracerProvider = () -> null;
	@Override
	public void processMouseUp(int x0, int y0, int id) {
		down = false;
		snapper.pmUp(x0, y0, id, this);
	}
	@Override
	public void pmUp(Numbers x, Numbers y, int id) {
		processMouseUp(x, y, id);
	}

	protected void processMouseUp(Numbers x, Numbers y, int id) {

		x = Numbers.add(x, clipLeft());
		y = Numbers.add(y, clipTop());
		if (!isMoved())
		{
			getHandler().pointerClicked(x, y, getCtx(id));
		}
		getHandler().pointerReleased(x, y, getCtx(id));
		paint();
	}

	@Override
	public void processMouseDrag(int x0, int y0, int id) {
		snapper.pmDrag(x0, y0, id, this);
	}
	
	@Override
	public void pmDrag(Numbers x, Numbers y, int id) {
		
        TrackerContext ctx = getCtx(id);
		//if (ctx.getTrack() == null)
		{
		    Tracer t = tracerProvider.get();
		    if (t != null) {
		        ctx.getHitTester().setXY(x.doubleValue(), y.doubleValue());
		        t.update(this, ctx);
		    }
		}
        processMouseDrag(x, y, id);
		
	}

	protected void processMouseDrag(Numbers x, Numbers y, int id) {
		setMoved(true);
		getHandler().pointerDragged(Numbers.add(x, clipLeft()), Numbers.add(y, clipTop()), getCtx(id));
		paint();
	}

	@Override
	public void processMouseDown(int x, int y, int id) {
		snapper.setMoved(false);
		super.processMouseDown(x, y, id);
		down = true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T adapt(Class<T> cls) {
		if(cls == Snapper.class) return (T) snapper;
		if(cls == AbstractViewer.class) return (T) this;
		if(cls == Widget.class) return (T) asWidget();
		if(cls == Tracer.class) return (T) tracerProvider.get();
		return super.adapt(cls);
	}

	@Override
	public void drawAxes() {
		new AxesDrawer(this).setBackground(background).drawAxes();
	}

	
	@Override
	public void setBackground(String string) {
		background = string;
		super.setBackground(string);
	}
	@Override
	public void visitTriangle(Triangle t) {
	    if (isSelected(t)) drawMP(t);
		selectColor(t);
		Punt[] depend = (Punt[]) t.getDepend();
		Path2D.Double path = new Path2D.Double(Path2D.WIND_EVEN_ODD, depend.length);
		int length = depend.length;
		double x = depend[0].getXd();
		double y = depend[0].getYd();
		context.beginPath();
		context.moveTo(x, y);
		path.moveTo(x, y);
		for (int i = 1; i < length; i++) {
			Punt p = depend[i];
			x = p.getXd();
			y = p.getYd();
			context.lineTo(x, y);
			path.lineTo(x, y);
		}
		context.closePath();
		path.closePath();
		if(!"none".equals(fill))
		{	context.setFillStyle(fill);
			context.fill();
		}
		if(stroke != null) stroke.toStyle(context);
		String s = context.getStrokeStyle().toString();
		if (!"rgba(0, 0, 0, 0)".equals(s))
			context.stroke();
		DefaultAdapter.getDefault(t).put(Shape.class, path);
	}

	public void selectColor(Destroyable object) {
		if(tracking || trail)
		{
		  if (trail) {
		    ColorStyle cs = object.adapt(ColorStyle.class);
		    if (cs == null) {
		      setColor(LIGHT_GRAY);
		    } else {
		      setCssColor(CssColor.make(cs.getColor()));
		      select = cs;
		    }
		    DEFAULT_STROKE.toStyle(context);
		    stroke  = object.adapt(StrokeStyle.class);
		  }
		  return;
		}
		Adapter a = object.getAdapter();
		//java.util.logging.Logger.getLogger("CanvasViewer").info(object  + ".adapter=" + a);
		stroke = a.adapt(StrokeStyle.class);
		DEFAULT_STROKE.toStyle(context);
		FillStyle f = a.adapt(FillStyle.class);
		if(f == null) fill = "none";
		else fill = f.getColor();
		
		ColorStyle c = a.adapt(ColorStyle.class);
		CheckObject co = a.adapt(CheckObject.class);
		//java.util.logging.Logger.getLogger("CanvasViewer").info("color = " + c);
		if (c != null && (co == null||!isFeedback())) {
		    setCssColor(CssColor.make(c.getColor()));
		    select = c;
	        if(hiLighter != null)
	            hiLighter.hilight(object);
			return;
		}
// feedback color.
		if(co != null && isFeedback())
		{   // extra verificatie?
		  //setCssColor(CssColor.make("green"));
			select = COLOR_GREEN;
			setCssColor(CssColor.make(select.getColor()));
		} else
		{
	      setColor(BLACK);
		}

		if(hiLighter != null)
			hiLighter.hilight(object);
	}

	private boolean feedback = true;
	public boolean isFeedback() {
		return false;
	}
	public void setFeedback(boolean fb) {
		feedback = fb;
	}
	
	@Override
	public void drawLine(double x1, double y1, double x2, double y2) {
		if(stroke != null) stroke.toStyle(context);
		super.drawLine(x1, y1, x2, y2);
	}

	@Override
	protected void drawCircle(double i, double j, double k) {
		if(stroke != null) stroke.toStyle(context);
		super.drawCircle(i, j, k);
	}

	@Override
	protected void drawArc(double x, double y, double k, double start,
			double length) {
		if(stroke != null) stroke.toStyle(context);
		super.drawArc(x, y, k, start, length);
	}

	private Segment drawTips(Segment s, Destroyable o) {
		Tips tip = s.adapt(Tips.class);
		if(tip == null) return s;
		selectColor(o);
		double dx = s.getDX();
		double dy = s.getDY();
		double len = Math.hypot(dx, dy);
		Float width = s.adapt(Float.class);
		double tiplen = 5;
		if(width != null) tiplen *= width.doubleValue();
		if(len < tiplen*3) tiplen = len/3;
		dx *= tiplen/len; 
		dy *= tiplen/len; 
		switch(tip) {
		case ATEND: tip(s.getP2(), -dx, -dy); break;
		case ATSTARTEND: tip(s.getP2(),-(dx), -(dy));
		case ATSTART: tip(s.getP1(), dx, dy); break;
		case NOTIP: return s;
		}
		return new ShortSegment(s, dx, dy, tip);
	}

	private void tip(Punt p1, double dx, double dy) {
		double x = (float)p1.getXd();
		double y = (float)p1.getYd();
		context.beginPath();
		context.moveTo(x, y);
		context.lineTo(x + dx + dy/2, y + dy -dx/2);
		context.lineTo(x + dx - dy/2, y + dy +dx/2);
		context.closePath();
		context.fill();
	}

	protected boolean isSelected(Destroyable s) {
	  if (!trail && !tracking && getModel().getSelect().contains(s)) {
        stroke  = s.adapt(StrokeStyle.class);
        if (stroke == null) stroke = DEFAULT_STROKE;
	    fill = null;
        setStroke(new StrokeStyle(stroke.lineWidth+8.0, null));
	    context.setStrokeStyle(SELECT_COLOR);
	    stroke.toStyle(context);
	    return true;
	  }
	  return false;
	}
	
	
	@Override
	public void visitSegment(Segment s) {
	    if (isSelected(s)) {
	      drawLine(s.getX1(), s.getY1() , s.getX2(), s.getY2());
	    }
		s = drawTips(s,s);
		super.visitSegment(s);
	}

	@Override
	public void visitPunt(Punt punt) {
		Float ps = punt.adapt(Float.class);
		if(ps != null) 
			pointSize = ps.floatValue();
		else
			pointSize = DEFAULT_POINTSIZE;
		if (trail) {
		  selectColor(punt);
          float p2 = pointSize/2f;
          fillCircle(punt.getXd()-p2, punt.getYd()-p2 , pointSize);
		} else
		{
		  if (!tracking && getModel().getSelect().contains(punt)) {
		    float p = pointSize + 8;
		    context.setFillStyle(SELECT_COLOR);
		    fillCircle(punt.getXd()-p/2, punt.getYd()-p/2, p);
		  }
	      	if (punt instanceof Hoekpunt) {
	  			selectColor(punt);
	      		visitHoekPunt((Hoekpunt) punt);
	      	}
		  super.visitPunt(punt);
		}
	}

	class PathVisitor implements SegmentVisitor {
		
		double x = Double.NEGATIVE_INFINITY;
		double y = Double.NEGATIVE_INFINITY;
		
		@Override
		public void visitSegment(Segment s) {
			double x1, y1;
			x1 = s.getX1();
			y1 = s.getY1();
			if(x1 != x || y1 != y)
				context.moveTo(x1, y1);
			x = s.getX2();
			y = s.getY2();
			context.lineTo(x, y);
		}

		@Override
		public Numbers clipTop() {
			return CanvasViewer.this.clipTop();
		}

		@Override
		public Numbers clipBottom() {
			return CanvasViewer.this.clipBottom();
		}

		@Override
		public Numbers clipLeft() {
			return CanvasViewer.this.clipLeft();
		}

		@Override
		public Numbers clipRight() {
			return CanvasViewer.this.clipRight();
		}

		public void destroy() {
			context.stroke();
		}
	}

	private void visitIntegral(Integral l) {
	    if (isSelected(l))
	      drawMP(l);
		selectColor(l);
		Area shape = new Area();
		l.visitSegments(new IntegralVisitor(l, shape, this));
		PathIterator iter = shape.getPathIterator(null);
		context.beginPath();
		while(!iter.isDone()) {
			float[] p = new float[6];
			switch( iter.currentSegment(p)) {
			case PathIterator.SEG_MOVETO: 
				context.moveTo(p[0], p[1]);
				break;
			case PathIterator.SEG_LINETO:
				context.lineTo(p[0], p[1]);
				break;
			case PathIterator.SEG_CLOSE:
				context.closePath();
				break;
			}
			iter.next();
		}
		context.fill();

		DefaultAdapter.getDefault(l).put(Shape.class, shape);
	}

	public void visitMP(MP l) {
	    if (isSelected(l)) 
	      drawMP(l);
		selectColor(l);
		if(stroke != null) stroke.toStyle(context);
		drawMP(l);
	}
  public void drawMP(MP l) {
    boolean old = tracking;
		try {
    		tracking = true;
    		PathVisitor v = new PathVisitor();
    		context.beginPath();
    		l.visitSegments(v);
    		v.destroy();
		} finally {
		  tracking = old;
		}
  }

	@Override
	public void visitLocus(Locus l) {
		if(l instanceof Integral) {
			visitIntegral( (Integral) l);
		} else
		super.visitLocus(l);
	}

	public void visitFormule(Label label) {
		Align align = label.adapt(Align.class);
		if(align == null) align = Align.BASE;
		else if(align == Align.NONE) return;
		double x =  label.getXd();
		double y =  label.getYd();
		FormuleCache fc = label.adapt(FormuleCache.class);
		if(fc == null) fc = new FormuleCache(label, context.getFillStyle());
		else {
				if(     fc.item != label || // bij mappen is dat niet zo!!!
						//!down && 
						!fc.isValid(context.getFillStyle())) // als we de mouse down, laten we de cache staan.
				{
					fc.destroy();
					fc = new FormuleCache(label, context.getFillStyle());
				}
		}
		double w = fc.getW();
		double h = fc.getH();
		switch(align) {
		default: y -= fc.getAs(); break;
		case LEFT: x -= w;
		case RIGHT: y -= h/2.0; break;
		case TOP: y -= h;
		case BOTTOM: x -= w/2.0; break;
		}
		RectShape r = new RectShape(x, y, w, h);
		context.drawImage(fc.getElement(), x, y, w, h);
		DefaultAdapter.getDefault(label).put(Shape.class, r);

	}
	private void visitFlipFlop(Label label) {
		boolean value = label.getState() != Label.FALSE;
		String on = "gray";
       
		float x = (float) label.getXd();
		float y = (float) label.getYd();
        FontStyle fs = label.adapt(FontStyle.class);
        if (fs == null) fs = FONT_STYLE;
        float square = fs.getSize()+1; // ascent
        float space = 2;
		if(value) {
			context.setFillStyle(on);
			context.fillRect(x, y, square, square);
		}
		selectColor(label);
		fs.toStyle(context);
		DEFAULT_STROKE.toStyle(context);
		context.strokeRect(x, y, square, square);
		String string = getMapper().toString(label);
		if(Align.NONE == label.adapt(Align.class)) string = "";
		drawString(string, x+square + space, y+square, null, null, null);
		RectShape r = new RectShape(x, y, square + space + context.measureText(string).getWidth(), square);
		DefaultAdapter.getDefault(label).put(Shape.class, r);
	}

	@Override
	public void visitLabel(Label label) {
	    if (isSelected(label)) {
	      Shape shape = label.adapt(Shape.class);
	      if (shape != null)
	      {
	    	  Rectangle r = shape.getBounds();
	    	  context.setFillStyle(SELECT_COLOR);
	    	  context.fillRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	    }}
	  
	  
	  
	  
		if(label.getRegistered() instanceof FlipFlop) {
			visitFlipFlop(label);
			return;
		}
		selectColor(label);

		if (label.getRegistered() instanceof nl.numworx.geodefiner.common.HoekHandler) {
			visitHoek(label);
			return;
		}
		
		if (label.getString().contains("$")
		  ||Boolean.TRUE.equals(label.adapt(Boolean.class)))
		{
			visitFormule(label);
			return;
		}
		String string = label.getString();
		double x = label.getXd();
		double y = label.getYd();
		Align align = label.adapt(Align.class);
		float extra = 0;
//zet het label correct tov het puntje.
		if(label.getRegistered() instanceof Interval) {
			try {
				if(Align.NONE == align) return;
				extra = 2;
				extra = label.getP().adapt(Float.class) / 2.0f; // NPE? 
			} catch (Exception e) {
			}
		}
		String h = null;
		String v = null;
		if(align != null) {
			switch(align) {
			case NONE: return;
			case LEFT:   h = TEXT_END;   v = TEXT_CENTRAL; break; 
			case RIGHT:  h = TEXT_START; v = TEXT_CENTRAL; break;
			case BOTTOM: h = TEXT_MIDDLE;v = TEXT_TOP; y+=extra;    break;
			case TOP:    h = TEXT_MIDDLE;v = TEXT_BOTTOM; y-=extra; break;
			default: 
			}
		} else align = Align.BASE;
		FontStyle fs = label.adapt(FontStyle.class);
		if (fs == null) fs = FONT_STYLE;
		
		fs.toStyle(context);
		drawString(string, x, y, h, v, null);
		FormuleFont ff = fs.getFont();
		double fontHeight = ff.getHeight();
		double ascent = ff.getAscent();
		RectShape rect = new RectShape(x, y, context.measureText(string).getWidth(), fontHeight);
		switch(align) {
		case LEFT:	rect.x -= rect.width; break; //als maxx > x dan x moet minder worden
		case BOTTOM: 
		case TOP:	rect.x -= rect.width/2; break;
		default:
		}
		switch(align) {
		case LEFT:	
		case RIGHT: rect.y -= rect.height/2; 
			break;
		case BASE:	rect.y -= ascent;break;
		case TOP:	rect.y -= rect.height; break;
		default:
		}
		DefaultAdapter.getDefault(label).put(Shape.class, rect);
	}

	private void visitHoek(Label label) {
		Punt center = label.getP();
		String string = label.getString();
		FontStyle fs = label.adapt(FontStyle.class);
		if (fs == null) fs = FONT_STYLE;		
		fs.toStyle(context);
		double w = context.measureText(string).getWidth();
		double h = fs.getFont().getAscent();
		double HOEKSIZE = fs.getFont().getHeight() * 1.2 ;
		Destroyable depend[] = label.getDepend();
		double startAngle = 0;
		if (depend[1] instanceof Punt) {
			Punt p0 = (Punt) depend[1];
			Punt p1 = (Punt) depend[0];
			double y = p1.getYd() - p0.getYd();
			double x = p1.getXd() - p0.getXd();
			startAngle = Math.atan2(-y, x);
		} else if (depend[0] instanceof Lijn) {
			Lijn l = (Lijn) depend[0];
			startAngle = Math.atan2(-l.getDY(), l.getDX());
		}
		double value = label.value.doubleValue();
		FillStrokeStyle oldStroke = context.getStrokeStyle();
		FillStrokeStyle oldFill   = context.getFillStyle();
		int rgba = select.getRGB();
		rgba = ((rgba >>> 24)/3) << 24 | (rgba&0xFFFFFF);
				
		fill = ColorStyle.colorString(rgba); context.setStrokeStyle(fill);
		drawArc(center.getXd()-HOEKSIZE/2, center.getYd()-HOEKSIZE/2, HOEKSIZE, startAngle, value);
		context.setStrokeStyle(oldStroke);
		context.setFillStyle(oldFill);

		value = startAngle + value/2;
		double ww = Math.hypot(w/2, h/2);
		double x,y;
		x= center.getXd() + (HOEKSIZE/2+ww)*Math.cos(value);
		y= center.getYd() - (HOEKSIZE/2+ww)*Math.sin(value);
		String halign = ViewerWidget.TEXT_MIDDLE;
		String valign = ViewerWidget.TEXT_CENTRAL;
		drawString(string, x , y, halign, valign, null);

		RectShape rect = 
				new RectShape(x-w/2, y+h/2 - fs.getFont().getAscent(), w, fs.getFont().getHeight());
		DefaultAdapter.getDefault((Adaptee) label).put(Shape.class, rect);

	}
	private void visitHoekPunt(Hoekpunt punt) {
		double hoek = (punt.hoek());
		Punt[] p = new Punt[3];
		p[2] = punt; p[1] = punt.getDepend()[0]; p[0] = punt.getDepend()[1];
		Label l = new Label();
		l.setState(Label.HOEK);
		l.setAdapter(punt.getAdapter());
//XXX altijd graden?
		l.setString(Math.round(hoek * 180.0 / Math.PI)%360 + "°");
		l.setValue(Numbers.createDouble(hoek));
		l.setDepend(p);
		l.setP(p[1]);
		visitHoek(l);
		
	}
	/* (non-Javadoc)
	 * @see fi.euclides.model.AbstractViewer#visitLijn(fi.euclides.model.Lijn)
	 */
	@Override
	public void visitLijn(Lijn l) {
		if(l instanceof Ray) {
			visitRay((Ray)l);
		} else {
		  if (isSelected(l)) {
		    ll.setLijn(l);
	        drawLine(ll.getX1(), ll.getY1() , ll.getX2(), ll.getY2());
		  }
		  super.visitLijn(l);			
		}
	}

	private void visitRay(Ray l) {
		rr.setLijn(l);
		Segment s = rr;
		if (isSelected(l)) drawLine(s.getX1(), s.getY1() , s.getX2(), s.getY2());
		
		s = drawTips(s,l);
		selectColor(l);
		drawLine(s.getX1(), s.getY1() , s.getX2(), s.getY2());
	}

	@Override
	public float getPointSize() {
		return pointSize;
	}
	@Override
	public void setPointSize(float f) {
		pointSize = f;
		
	}
	@Override
	public StrokeStyle getStroke() {
		return stroke ==  null ? DEFAULT_STROKE : stroke;
	}
	@Override
	public void setStroke(StrokeStyle stroke) {
		this.stroke = stroke;
	}
	@Override
	public int getOffX() {
		return offX;
	}
	@Override
	public int getOffY() {
		return offY;
	}

  @Override
  public Destroyable trail(Destroyable d) {
    Destroyable copy = d.trail();
    if (copy == null) return null;
    copyTrailAttributes(d, copy);
    return copy;
  }

  public void copyTrailAttributes(Destroyable d, Destroyable copy) {
    DefaultAdapter adapter = DefaultAdapter.getDefault(copy);
    adapter.put(Float.class, d.adapt(Float.class)); // point size
    adapter.put(StrokeStyle.class, d.adapt(StrokeStyle.class)); // line style/width
    ColorStyle cs = d.adapt(ColorStyle.class);
    if (cs != null) 
      adapter.put(cs.trailColorStyle());
    else
      adapter.put(null, ColorStyle.class);
  }

  @Override
  public void toTrail(Destroyable key, Vector<Destroyable> values) {
    for(Destroyable copy: values) copyTrailAttributes(key, copy);
  }

  public void setTracer(Lazy<Tracer> tracerProvider) {
    this.tracerProvider = tracerProvider;
    
  }

  @Override
  public void visitCirkel(Cirkel c) {
    if (isSelected(c)) {
      drawCircle(c.getX(), c.getY(), c.getD());
    }
    super.visitCirkel(c);
  }

  @Override
  public void visitBoog(Boog b) {
    if(isSelected(b)) {
      drawArc(b.getX(), b.getY(), b.getD(), b.getStart(), b.length());
    }
    super.visitBoog(b);
  }
@Override
public void setColor(int n) {
	switch(n) {
	case WHITE: select = COLOR_WHITE; break;
	case RED: select = COLOR_RED; break;
	case AbstractViewer.blue: select = COLOR_BLUE; break;
	case AbstractViewer.magenta: select = COLOR_MAGENTA; break;
	case POINTER_COLOR:	 select = COLOR_POINTER; break;
	case LIGHT_GRAY: select = COLOR_LT_GRAY; break;
	case BLACK:
	default: select = COLOR_BLACK;
	}
	super.setColor(n);
}
@Override
protected void drawImage(GeoImage image) {
	ImageElement img = image.adapt(ImageElement.class);
	if (img == null) {	
		super.drawImage(image);
		return;
	}
	context.save();
	Punt imageCenter = image.imageCenter();
	Punt center = image.center();
	Numbers rotor = image.rotation();
	context.translate(center.getXd(), center.getYd());
	double angle = Math.atan2(Numbers.imag(rotor).doubleValue(), Numbers.real(rotor).doubleValue());
	double scale = Numbers.abs(rotor).doubleValue();
	context.rotate(angle);
	context.scale(scale, scale);
	context.drawImage(img, -imageCenter.getXd(), -imageCenter.getYd());
	context.restore();
// shape:
	Punt[] depend = (Punt[]) image.getDepend();
	Path2D.Double path = new Path2D.Double(Path2D.WIND_EVEN_ODD, depend.length);
	int length = depend.length;
	double x = depend[0].getXd();
	double y = depend[0].getYd();
	path.moveTo(x, y);
	for (int i = 1; i < length; i++) {
		Punt p = depend[i];
		x = p.getXd();
		y = p.getYd();
		path.lineTo(x, y);
	}
	path.closePath();
	DefaultAdapter.getDefault(image).put(Shape.class, path);	
  }

}
