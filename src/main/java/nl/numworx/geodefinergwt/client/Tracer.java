package nl.numworx.geodefinergwt.client;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import fi.euclides.event.NameMapper;
import fi.euclides.event.Tracker;
import fi.euclides.event.TrackerContext;
import fi.euclides.model.AbstractViewer;
import fi.euclides.model.Boog;
import fi.euclides.model.Cirkel;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Kegelsnede2;
import fi.euclides.model.Label;
import fi.euclides.model.Lijn;
import fi.euclides.model.Locus;
import fi.euclides.model.Punt;
import fi.euclides.model.Segment;
import fi.euclides.model.Track;
import fi.euclides.model.Triangle;
import fi.euclides.model.Visitor;
import fi.euclides.model.math.Numbers;
import fi.euclides.util.Observable;
import fi.euclides.util.Observer;

@Singleton
public class Tracer implements Observer, Visitor {

  static final Logger LOG = Logger.getLogger(Tracer.class.getName());
  
  public static class Entry {
    long timestamp;
    String name;
    boolean visible;
    Boolean dragging;
    Double x,y,value;
    double evx, evy;
    String arg;
  }
    
  List<Entry> entries = new LinkedList<>();
  Set<Destroyable> drags = new HashSet<>();
  
  class Adder implements Visitor {

    @Override
    public void visitPunt(Punt p) {
      drags.add(p);
    }

    @Override
    public void visitLijn(Lijn l) {
      drags.add(l);
    }

    @Override
    public void visitCirkel(Cirkel c) {
      drags.add(c);     
    }

    @Override
    public void visitSegment(Segment s) {
      drags.add(s);
    }

    @Override
    public void visitLabel(Label label) {
      drags.add(label);
    }

    @Override
    public void visitTriangle(Triangle t) {
      drags.add(t);
    }

    @Override
    public void visitKegelsnede(Kegelsnede2 k) {
      drags.add(k);
    }

    @Override
    public void visitLocus(Locus l) {
      drags.add(l);
    }

    @Override
    public void visitBoog(Boog b) {
      drags.add(b);
    }
  }
  
  final Adder ADDER = new Adder();
  Entry top;
  NameMapper mapper;
  AbstractViewer viewer;

  @Inject Tracer(Tracker mapper, AbstractViewer viewer) { 
    this.mapper = mapper.getMapper();
    this.viewer = viewer;
    viewer.addObserver(this);
  }
  
  @Override
  public void update(Observable observable, Object arg) {
    LOG.info("update " + observable + "," +arg);
    if (observable instanceof Destroyable) {
      Destroyable d = (Destroyable)observable;
      top = new Entry();
      if (arg == null) arg = "update";
      top.arg = arg.toString();
      d.visit(this);
    } else if (observable == viewer) {
      if (arg instanceof Iterable) {
        Set<Destroyable> old = new HashSet<>(drags);
        drags.clear();
        Iterable<TrackerContext> iter = (Iterable<TrackerContext>)arg;
        for (TrackerContext item : iter) {
          Track t = item.getTrack();
          if (t == null) continue;
          top = new Entry();
          top.evx = item.getHitTester().getX();
          top.evy = item.getHitTester().getY();
          top.dragging = Boolean.TRUE;
          top.arg = item.toString();
          t.visit(ADDER);
          t.visit(this);
        }
        old.removeAll(drags);
        for( Destroyable d: old) {
          top = new Entry();
          top.dragging = Boolean.FALSE;
          top.arg = "dragging";
          d.visit(this);
        }
      } else if (arg instanceof TrackerContext) {
        top = new Entry();
        TrackerContext ctx = (TrackerContext) arg;
        top.arg = arg.toString();
        top.name = arg.toString();
        top.evx = ctx.getHitTester().getX();
        top.evy = ctx.getHitTester().getY();
        entries.add(top);
      }
    }
  }

  private void add(Destroyable p) {
    if (p.adapt(Tracer.class) != null) {
      top.name = mapper.toString(p);
      top.timestamp = System.currentTimeMillis();
      top.visible = p.isVisible() && p.isDefined();
      entries.add(top);
    }
  }
  
  
  
  @Override
  public void visitPunt(Punt p) {
      top.x = p.getXd();
      top.y = p.getYd();
      add(p);
  }

  @Override
  public void visitLijn(Lijn l) {
    top.x = l.getX1();
    top.y = l.getY1();
    add(l);
  }

  @Override
  public void visitCirkel(Cirkel c) {
    top.x = c.getCenter().getXd();
    top.y = c.getCenter().getYd();
    add(c);
  }

  @Override
  public void visitSegment(Segment s) {
    top.x = s.getX1();
    top.y = s.getX2();
    add(s);
  }

  @Override
  public void visitLabel(Label label) {
    top.x = label.getXd();
    top.y = label.getYd();
    top.value = label.value.doubleValue();
    add(label);
  }

  @Override
  public void visitTriangle(Triangle t) {
    top.x = t.getA().getXd();
    top.y = t.getA().getYd();
    add(t);
  }

  @Override
  public void visitKegelsnede(Kegelsnede2 k) {
    top.x = k.getA().getXd();
    top.y = k.getA().getYd();
    add(k);
    
  }

  @Override
  public void visitLocus(Locus l) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visitBoog(Boog b) {
    top.x = b.getCenter().getXd();
    top.y = b.getCenter().getYd();
    add(b);
  }

  public void getState(Map<String,Object> state) {
    
  }
  public void setState(Map<String,Object> state) {
    
  }
  
  
  
}
