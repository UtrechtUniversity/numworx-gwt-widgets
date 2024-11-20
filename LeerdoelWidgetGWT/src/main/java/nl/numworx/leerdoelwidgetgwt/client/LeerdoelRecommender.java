package nl.numworx.leerdoelwidgetgwt.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.dwo.gwt.lib.rest.ui.IdleDetect;
import nl.numworx.leerdoelwidgetgwt.client.locale.LeerdoelWidgetMessages;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.AbstractStudentModelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.Util;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class LeerdoelRecommender extends ResizeComposite implements SelectionHandler<Integer> {
	
	private static final String $IMAGE$MAP$ = "$IMAGE$MAP$";
	private static final LeerdoelWidgetMessages rb = GWT.create(LeerdoelWidgetMessages.class);
    private final static String lang = LocaleInfo.getCurrentLocale().getLocaleName();
    private static java.util.logging.Logger LOG = Logger.getLogger("LeerdoelRecommender");
	
	private DockLayoutPanel list;
	private List<String> objectives;
	private DescriptionService service;
	FormuleKeyboardIF keyboard;
	private boolean showScore = true;

	private Label header;

	IdleDetect idler;
	int width;
	private OpdrNavIF comRoot;
	private LeerdoelWidgetGWT parent;
	StackLayoutPanel stack;
	private Deferred<List<Promise<StubWidget>>> widgets = new Deferred<>();

	class FollowFlow extends SimplePanel implements RequiresResize {

		@Override
		public void onResize() {
			int h = header.getOffsetHeight();
			int oldh = list.getWidgetSize(this).intValue();
			if (oldh != h && h != 0)
				list.setWidgetSize(this, h);			
		}		
	}
	
//	class Selector implements SelectionHandler<Integer> {
//
//		@Override
//		public void onSelection(SelectionEvent<Integer> event) {
//			int index = event.getSelectedItem();
//			final Promise<StubWidget> p = widgets.getPromise().flatMap(w -> w.get(index));
//			if (!p.isDone()) return;
//			StubWidget s = p.getValue();
//			LOG.severe("hoogte " + s.getOffsetHeight() + " wanted " + s.getHeight());
//			
//		}
//		
//	}
	
	LeerdoelRecommender(LeerdoelWidgetGWT parent) {
		this.parent = parent;
		list = new DockLayoutPanel(Unit.PX);
		list.addStyleName("recommender");
		FollowFlow flow = new FollowFlow();
		header = new Label(rb.intro());
		flow.add(header);
		header.addStyleName("intro");
		list.addNorth(flow, 50);
		initWidget(list);
	}

	OpdrNavIF getComRoot() {
		return comRoot;
	}

	void setComRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
	}

	public void setObjectives(List<String> objectives) {
		this.objectives = objectives.stream().map(this::strip).collect(Collectors.toList());
// debuggertje:
//		for (String o : objectives) {
//			Label l = new Label(o);
//			list.add(l);
//		}
		
	}

	private String strip(String in) {
		return in.split("/",2)[0];
	}
	private String getTitle(DomStudentModelContextInfo info) {
		return AbstractStudentModelPresenter.getTitle(info, lang);
	}
	
	static class HeaderLabel extends Label {
		final String objective;
		final Promise<StubWidget> widget;
		HeaderLabel(String text, String objective, Promise<StubWidget> promise) {
			super(text);
			this.objective = objective;
			this.widget = promise;
			this.setTitle(text);
		}
	}
	
	Promise<Integer> setModelScore(DomStudentModelContext4Student studentModel, DomStudentModelDataScore s, DomMethod activeMethod) {		
		int cnt = 0;
		stack = new StackLayoutPanel(Unit.EM);
		stack.setAnimationDuration(0);
		stack.addSelectionHandler(this);
		List<Promise<StubWidget>> widgets = new ArrayList<>();
		for (String o : objectives) {	
			
			DomStudentModelContextInfo info = getInfo(studentModel, o);
			if (info == null) 
				continue;
			DomStudentModelScore<?> score = getScore(s.getDomStudentModelStructureScore(), o);
			double greenPerc = Util.getGreen(score) * 200;
			if (greenPerc >= 90) 
				continue;
			//east.setPixelSize(-1, 200); // size of "content panel of iframe, echter er zit een scrollpane tussen en die heeft size 0
			Deferred<StubWidget> defer = new Deferred<>();
			Label title = new HeaderLabel(getTitle(info), o, defer.getPromise());
			widgets.add(defer.getPromise());
			SimpleLayoutPanel east = tekstPanel(service.getDescription(studentModel, info),width,200, defer);
			com.google.gwt.user.client.ui.Widget panel = east;
			if (showScore) {
				DockLayoutPanel p = new DockLayoutPanel(Unit.EM);
				p.addSouth(Util.scoreItem("", score, Util.MAX_LEVEL), 2); // als in eastPanel
				p.add(east);
				panel = p;
			}
			stack.add(panel, title, 2);
			cnt ++;
		}
		if (cnt == 0) header.setText(rb.allok());
		else
			list.add(stack);
		//stack.addSelectionHandler(new Selector());
		list.forceLayout();
		RootLayoutPanel.get().setStyleName("alert", cnt!=0);
		this.widgets.resolve(widgets);
		return null;
	}

	private Promise<Integer> calcdiff(List<Promise<StubWidget>> wl) 
	{
		if (wl.isEmpty()) {
			// only header in view
			int h = header.getOffsetHeight();
			int l = list.getOffsetHeight();
			return Promises.resolved(h-l);
		};
		
		return Promises.all(wl).then(p -> {
			int max = 0;
			int wanted = 0;
			for(Promise<StubWidget> w : wl) {
				StubWidget ss = w.getValue();
				max = Math.max(ss.getOffsetHeight(), max);
				wanted = Math.max(ss.getHeight(), wanted);
			}
			int extra = 12 + wanted - max;
			return Promises.resolved(extra);
		});
	}	
	
	protected Promise<Integer> extradiff() {
		return widgets.getPromise()
				.map( l -> {
					if (l.isEmpty() || stack == null) return l;
					return Collections.singletonList(l.get(stack.getVisibleIndex())); // only one
				})
				.flatMap(this::calcdiff);
	}
	protected Promise<Integer> singlediff(Promise<StubWidget> w) {
		return calcdiff(Collections.singletonList(w));
	}
	
	
	private DomStudentModelScore<?> getScore(DomStudentModelScore<?> structure, String o) {
		if (structure.getId().equals(o)) return structure;
		List<?> children = structure.getChildren();
		if (children == null) return null;
		Iterator<?> it = children.iterator();
		while (it.hasNext()) {
			DomStudentModelScore<?> type = (DomStudentModelScore<?>) it.next();
			DomStudentModelScore<?> result = getScore(type, o);
			if (result != null) return result;
		}		
		return null;
	}

	private DomStudentModelContextInfo getInfo(DomStudentModelContext4Student studentModel, String o) {
		DomStudentModelStructure structure = studentModel.getModelStructure();
		DomStudentModelContextInfo info = structure.getInfo();
		if (info.getId().equals(o)) return info;
		List<DomStudentModelCategory> cats = structure.getCategories();
		for( DomStudentModelCategory cat: cats) {
			info = cat.getInfo();
			if (info.getId().equals(o)) return info;
			List<DomStudentModelObj> objs = cat.getObjectives();
			for (DomStudentModelObj obj: objs) {
				Optional<DomStudentModelContextInfo> opt = getInfo(obj, o);
				if (opt.isPresent()) return opt.get();
			}			
		}
		return null;
	}

	private Optional<DomStudentModelContextInfo> getInfo(DomStudentModelObj parent, String o) {
		DomStudentModelContextInfo info = parent.getInfo();
		if (info.getId().equals(o)) return Optional.of(info);
		List<DomStudentModelObj> objs = parent.getObjectives();
		if (objs != null)
			for (DomStudentModelObj obj: objs) {
				Optional<DomStudentModelContextInfo> opt = getInfo(obj, o);
				if (opt.isPresent()) return opt;
			}			
		return Optional.empty();
	}

	void setModelScore(DomStudentModelContext4Student studentModel, Promise<DomStudentModelDataScore> s,
			DomMethod activeMethod) {
		s.then(  p -> setModelScore(studentModel, p.getValue(), activeMethod));
		
	}

	public void setService(DescriptionService service) {
		this.service = service;		
	}

	public void enableScore(boolean showScore) {
		this.showScore = showScore;
	}

	private static native void shift5(JavaScriptObject o) /*-{
		o.shift();
		o.shift();
		o.shift();
		o.shift();
		o.shift();
	}-*/;
	
	
	SimpleLayoutPanel tekstPanel(Promise<String> promise, int width, int height, Deferred<StubWidget> defer) {
		SimpleLayoutPanel parent = new SimpleLayoutPanel();
		parent.addStyleName("parent-iframe");
		promise.then(p -> {
			StubWidget tekstpanel = new StubWidget(this.parent, 9, keyboard, idler, comRoot);
			ObjectMap launch;
			String value = p.getValue();
			JSONValue js;
			js = JSONParser.parseLenient(value);
			JSONValue imagemap = js.isObject().get($IMAGE$MAP$);
			if (imagemap != null) imagemap = imagemap.isObject();
			js = js.isObject().get("opdracht_1_1");
			js = js.isObject().get("interactiePanelLaunchData");
			js = js.isArray().get(5);
			js = js.isObject().get("interactiePanelLaunchState");
// FIXME Altijd 1 hok er omheen. Je ziet niets als dat niet zo is.			

			if (imagemap != null) {
				js.isObject().put($IMAGE$MAP$, imagemap);
			}
			LOG.info(js.toString());
			
			launch = JSONUtilities.wrapMap(js.isObject());
			
			tekstpanel.init(width, height, launch);
			parent.add(tekstpanel);
			defer.resolve(tekstpanel);
			return null;
		}).then(null, (p)  -> {
			GWT.log("ERROR in tekstPanel", p.getFailure());
			parent.add(new Label(p.getFailure().toString()));
		});
		return parent;
	}

	HashMap<String, Object> getState(HashMap<String, Object> result) {
		if (stack != null) {
			int selection = stack.getVisibleIndex();
			if (selection < 0) return result;
			Widget w = stack.getHeaderWidget(selection);
			HeaderLabel h = (HeaderLabel) w;
			String objective = h.objective;
			result.put("selected", objective);		
		}
		return result;
	}

	void setState(ObjectMap state) {
		if (stack != null) {
		String objective = state.getString("selected");
		int n = stack.getWidgetCount();
		for(int i = 0; i < n; i++) {
			Widget w = stack.getHeaderWidget(i);
			HeaderLabel h = (HeaderLabel) w;
			if  (h.objective.equals(objective)) {
				stack.showWidget(i);
				break;
			}
		}}
	}

	@Override
	public void onSelection(SelectionEvent<Integer> event) {
		int selected = event.getSelectedItem();
		AnimationScheduler.get().requestAnimationFrame( (t) -> {
		HeaderLabel label = (HeaderLabel) stack.getHeaderWidget(selected);
// de single diff berekeing werkt alleen als widget zichtbaar is.		
		Promise<Integer> diff = singlediff(label.widget);
		diff.onResolve(() -> {
			int current = stack.getVisibleIndex();
			boolean down = parent.header.isDown();
			if (current == selected && down) {
				int delta = diff.getValue().intValue();
				parent.requestDelta(delta);
			}
		});
	});}

}
