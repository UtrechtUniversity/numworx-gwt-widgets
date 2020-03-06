package nl.numworx.geodefinergwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import fi.euclides.event.NameMapper;
import fi.euclides.event.Tracker;
import fi.euclides.event.TrackerContext;
import fi.euclides.gwt.MouseContext;
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
import fi.euclides.util.Observable;
import fi.euclides.util.Observer;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

@Singleton
public class Tracer implements Observer, Visitor {

  static final Logger LOG = Logger.getLogger(Tracer.class.getName());
  private static final String LOG_STATE = "logState";
  
  EntryBean entry() { 
    EntryBean entry = Entry.entry();
    entry.setTimestamp(System.currentTimeMillis());
    setLastW(entry);
    return entry;
  }
  
  List<EntryBean> entries = new LinkedList<>();
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
  EntryBean top;
  NameMapper mapper;
  AbstractViewer viewer;

  @Inject Tracer(Tracker mapper, AbstractViewer viewer) { 
    this.mapper = mapper.getMapper();
    this.viewer = viewer;
    viewer.addObserver(this);
  }
  
  @Override
  public void update(Observable observable, Object arg) {
    if (observable instanceof Destroyable) {
      Destroyable d = (Destroyable)observable;
      top = entry();
      if (arg == null) arg = "update";
      top.setArg(arg.toString());
      d.visit(this);
    } else if (observable == viewer) {
      if (arg instanceof Iterable) {
        Set<Destroyable> old = new HashSet<>(drags);
        drags.clear();
        @SuppressWarnings("unchecked")
        Iterable<TrackerContext> iter = (Iterable<TrackerContext>)arg;
        for (TrackerContext item : iter) {
          Track t = item.getTrack();
          if (t == null) continue;
          
          top = entry();
          setEvent(item);
          top.setDragging(Boolean.TRUE);
          top.setArg(item.toString());
          t.visit(ADDER);
          t.visit(this);
        }
        old.removeAll(drags);
        for( Destroyable d: old) {
          top = entry();
          top.setDragging(Boolean.FALSE);
          top.setArg("dragging");
          d.visit(this);
        }
      } else if (arg instanceof TrackerContext) {
        top = entry();
        TrackerContext ctx = (TrackerContext) arg;
        top.setArg(arg.toString());
        top.setName(arg.toString());
        setEvent(ctx);
        LOG.info(top.getName()  + " at " + top.getTimestamp() + " " + top.getEvx());
        entries.add(top);
      }
    }
  }

  private Double lastwx, lastwy, evx, evy;
  
  public void setLastW(EntryBean top) {
    top.setWx(lastwx);
    top.setWy(lastwy);
    if (evx != null) top.setEvx(evx.doubleValue());
    if (evy != null) top.setEvy(evy.doubleValue());
  }
  
  public void setEvent(TrackerContext item) {
    MouseContext ctx = item.getAdapter().adapt(MouseContext.class);
    if (ctx != null) {
      evx = Double.valueOf(ctx.getX());
      top.setX(evx);
      top.setEvy(ctx.getY());
      top.setY(evy);
      evy = Double.valueOf(ctx.getY());
      top.setTimestamp(ctx.getTimestamp());
      lastwx = Double.valueOf(ctx.getScreenX()-ctx.getClientX());
      lastwy = Double.valueOf(ctx.getScreenY()-ctx.getClientY());
      setLastW(top);
      return;
    }
    top.setEvx(item.getHitTester().getX());
    top.setEvy(item.getHitTester().getY());
  }

  private void add(Destroyable p) {
    if (p.adapt(Tracer.class) != null) {
      top.setName(mapper.toString(p));
      top.setVisible(p.isVisible() && p.isDefined());
      LOG.info(top.getName()  + "." + top.getArg() + " at " + top.getTimestamp());
      entries.add(top);
    }
  }
   
  @Override
  public void visitPunt(Punt p) {
      top.setX(p.getXd());
      top.setY(p.getYd());
      add(p);
  }

  @Override
  public void visitLijn(Lijn l) {
    top.setX(l.getX1());
    top.setY(l.getY1());
    add(l);
  }

  @Override
  public void visitCirkel(Cirkel c) {
    top.setX(c.getCenter().getXd());
    top.setY(c.getCenter().getYd());
    add(c);
  }

  @Override
  public void visitSegment(Segment s) {
    top.setX(s.getX1());
    top.setY(s.getX2());
    add(s);
  }

  @Override
  public void visitLabel(Label label) {
    top.setX(label.getXd());
    top.setY(label.getYd());
    top.setValue(label.value.doubleValue());
    add(label);
  }

  @Override
  public void visitTriangle(Triangle t) {
    top.setX(t.getA().getXd());
    top.setY(t.getA().getYd());
    add(t);
  }

  @Override
  public void visitKegelsnede(Kegelsnede2 k) {
    top.setX(k.getA().getXd());
    top.setY(k.getA().getYd());
    add(k);
    
  }

  @Override
  public void visitLocus(Locus l) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visitBoog(Boog b) {
    top.setX(b.getCenter().getXd());
    top.setY(b.getCenter().getYd());
    add(b);
  }

  
  private List<Object> logState = new ArrayList<>();
  
  public void setState(Map<String,Object> state) {
    ObjectMap s = JSONUtilities.wrapMap(state);
    if (s.containsKey(LOG_STATE)) {
      List<Object> strings = s.getList(LOG_STATE);
      this.logState.clear();
      this.logState.addAll(strings);
    }
    newsession();
    stamp();
  }
  private void newsession() {
    top = entry();
    top.setArg("start");
    top.setName("newsession");
    entries.add(top);
  }

  public void getState(Map<String,Object> state) {
    for(EntryBean entry: entries) {
      this.logState.add(serializeToMap(entry));
    }
    entries.clear();
    state.put(LOG_STATE, logState);
  }

private Object serializeToMap(EntryBean entry) {
	Map<String,Object> map = new HashMap<>();
	putif(map,"timestamp", String.valueOf(entry.getTimestamp()));
	putif(map,"name", entry.getName());
	putif(map,"evx", entry.getEvx());
	putif(map,"evy", entry.getEvy());
	putif(map,"x", entry.getX());
	putif(map,"y", entry.getY());
	putif(map,"visible", entry.isVisible());
	putif(map,"dragging", entry.getDragging());
	putif(map,"value", entry.getValue());
	putif(map,"arg", entry.getArg());
	putif(map, "wx", entry.getWx());
	putif(map, "wy", entry.getWy());
	return map;
}

private void putif(Map<String, Object> map, String key, Object value) {
	if (value != null) 
		map.put(key, value);
	
}
  
private void stamp() {
  RequestBuilder requestBuilder = new RequestBuilder(
    RequestBuilder.GET, "https://app.dwo.nl/dwo/rest/public/status/getHeartBeat");
  try {
    top = entry();
    top.setArg("request");
    top.setName("heartbeat");
    entries.add(top);
    requestBuilder.sendRequest("", new RequestCallback() {

      @Override
      public void onResponseReceived(Request request, Response response) {
        String json = response.getText();
        double stamp = (long) JSONParser.parse(json).isObject().get("serverTimeStamp").isNumber().doubleValue();
        top = entry();
        top.setValue(stamp);
        top.setArg("response");
        top.setName("heartbeat");
        entries.add(top);
      }

      @Override
      public void onError(Request request, Throwable exception) {
        
        
      } } );
  } catch (RequestException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
  }
}
  
}
