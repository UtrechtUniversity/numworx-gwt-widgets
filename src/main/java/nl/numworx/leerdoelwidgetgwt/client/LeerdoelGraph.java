package nl.numworx.leerdoelwidgetgwt.client;

import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraph;

public class LeerdoelGraph extends StudentResultsGraph {

	public LeerdoelGraph(boolean voorkennisKnop, boolean zoomKnoppen, boolean voorkennisMenu) {
		super();
		
		initHandlers(voorkennisMenu);
	}

	private void initHandlers(boolean voorkennisMenu) {
		super.initHandlers(); // FIXME
	}

	@Override
	protected void initHandlers() {
	}

}
