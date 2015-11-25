package fi.statistiekgwt.client.dotplot;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.HorizontalScrollbar;
import com.google.gwt.user.client.ui.VerticalScrollbar;

import fi.statistiekgwt.client.StatTableModelTest;
import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;

public class DotplotViewTest extends GWTTestCase
{
	private DotplotView view;
	private DotplotModel model;
	private DotplotController controller;
	
	private HorizontalScrollbar hScrollbar;
	private Element hScrollbarContainer;
	private VerticalScrollbar vScrollbar;
	private Element vScrollbarContainer;
	
	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;

	/**
	 * Must refer to a valid module that sources this class.
	 */
	@Override
	public String getModuleName()
	{
		return "fi.statistiekgwt.StatistiekGWT";
	}

	/**
	 * Set up test case 1. 
	 * @throws Exception
	 */
	public void gwtSetUp() throws Exception
	{
		StatistiekGWT statistiek = new StatistiekGWT();
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.setUpTestCase1();
	}
	
	/**
	 * Set up test case 1:
	 * 		columnIndex = 1 (gewicht)
	 * 		columnSplitIndex = 5 (geslacht)
	 * 		case 0-2 selected
	 */
	private void setUpTestCase1()
	{
		StatTableModelTest statTableModelTest = new StatTableModelTest();
		StatTableModel statTableModel = 
			statTableModelTest.getStatTableModelWithSelection();

		int width = 800;
		int height = 600;
		
		this.controller = new DotplotController(
			statTableModel, "Dotplot", 1, width, height);
		this.model = new DotplotModel(
			statTableModel, "Dotplot", false);
		view = new DotplotView(this.model, this.controller);
		// columnIndex = -1!
		this.model.setColumnXIndex(1); // gewicht
		this.setSplitGeslacht();
		this.view.update();
		
		this.hScrollbar = this.view.getScrollPanel().getHorizontalScrollbar();
		this.hScrollbarContainer = hScrollbar.asWidget().getElement().getParentElement();
		this.vScrollbar = this.view.getScrollPanel().getVerticalScrollbar();
		this.vScrollbarContainer = this.vScrollbar.asWidget().getElement().getParentElement();
	}

	private void setSplitGeslacht()
	{
		this.model.setColumnSplitIndex(5); // geslacht
	}

	public void testCustomScrollPanel()
	{
		int expected = 0;
		int actual;
		
		if (this.hScrollbarContainer != null)
		{
			actual = this.hScrollbarContainer.getOffsetHeight();
		}
		else
		{
			actual = -1;
		}

		assertEquals("", expected, actual);
	}

}
