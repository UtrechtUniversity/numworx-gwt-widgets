package nl.numworx.leerdoelwidgetgwt.client;

import com.google.gwt.event.dom.client.ContextMenuEvent;

import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.FilterTitle;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraph;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;

public class LeerdoelGraph extends StudentResultsGraph {
	
	

	private boolean leerdoelPopup, voorkennisKnop;
	private boolean doFilter, zoomKnoppen;

	public LeerdoelGraph(boolean voorkennisKnop, boolean zoomKnoppen, boolean voorkennisMenu, boolean leerdoelPopup, DescriptionPresenter description, boolean filterHeader) {
		super(description);
		this.leerdoelPopup = leerdoelPopup;
		this.voorkennisKnop = voorkennisKnop;
		this.zoomKnoppen = zoomKnoppen;
		super.setVoorKennisVisible(voorkennisKnop);
		setZoomVisible(zoomKnoppen);
		getElement().getStyle().clearBackgroundColor();
		initHandlers(voorkennisKnop, voorkennisMenu, zoomKnoppen);
	    setTitleVisible(filterHeader, voorkennisMenu);
	}

	private void initHandlers(boolean voorkennisKnop, boolean voorkennisMenu, boolean zoomKnoppen) {
		
		if (zoomKnoppen) {
			image.addMouseMoveHandler(this);
			image.addMouseUpHandler(this);
			image.addMouseDownHandler(this);
			image.addMouseOutHandler(this);
			zoomFitBtn.addClickHandler(new ZoomFit());
			zoomOutBtn.addClickHandler(new Zoom(true));
			zoomInBtn.addClickHandler(new Zoom(false));
		}
		if (voorkennisKnop) {
			voorkennisBtn.addClickHandler(new Voorkennis());
			verbergBtn.addClickHandler(new VerbergVoorkennis());
		}
		if (voorkennisMenu)
			addDomHandler(this, ContextMenuEvent.getType()); // FIXME
	}

	@Override
	protected void initHandlers() {
	}

	
	@Override
	protected FilterTitle createFilterTitle() {
		if (doFilter) return super.createFilterTitle();

		return new FilterTitle(null) {

			@Override
			protected void initialize() {
				setStylePrimaryName("filter-title-alt");
				initClose();
				close.setText("Terug");
			} 
			
		};
	}

	@Override
	protected void initTitle() {
	}

	public void setVoorKennisVisible(boolean visible) {
		if(voorkennisKnop)
			super.setVoorKennisVisible(visible);
	}

	public void setZoomVisible(boolean visible) {
		setWidgetVisible(zoomFitBtn, visible);
		setWidgetVisible(zoomInBtn, visible);
		setWidgetVisible(zoomOutBtn, visible);
	}
	
	public void setTitleVisible(boolean visible, boolean voorkennisMenu) {
		this.doFilter = visible;
		super.initTitle();
		
		setWidgetVisible(title, visible||voorkennisMenu);
	}

	@Override
	protected Node nodeFactory(DomStudentModelObj obj, String p, DomStudentModelMethodInfo info) {
		final String p1 = p;
		Node node = new Node(obj, info, p);
		if (!leerdoelPopup) return node;
		return node.addClickHandler();
	}

//	@Override
//	public void doFilter(Map<String, Map<String, Set<Integer>>> f) {
//		if (doFilter)
//			super.doFilter(f);
//	}

	@Override
	protected void doFilterFit(DomStudentModelMethodInfo info) {
		if (doFilter)
			super.doFilterFit(info);
	}

	@Override
	public void zoomFit() {
		super.zoomFit();
	}

	@Override
	protected void resizer() {
		if (zoomKnoppen)
			super.resizer();
		else
			zoomFit();
	}

}
