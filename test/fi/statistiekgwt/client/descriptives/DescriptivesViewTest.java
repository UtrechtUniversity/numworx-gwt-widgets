package fi.statistiekgwt.client.descriptives;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.Label;

import fi.statistiekgwt.client.StatTableModelTest;
import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.descriptives.DescriptivesController;
import fi.statistiekgwt.client.descriptives.DescriptivesModel;
import fi.statistiekgwt.client.descriptives.DescriptivesView;

public class DescriptivesViewTest extends GWTTestCase
{
	private DescriptivesView view;
	private DescriptivesModel model;
	private DescriptivesController controller;
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
		StatistiekGWT statistiek = new StatistiekGWT();
		StatTableModelTest statTableModelTest = new StatTableModelTest();
		StatTableModel statTableModel = 
			statTableModelTest.getStatTableModelWithSelection();

		int width = 800;
		int height = 600;
		
		this.controller = new DescriptivesController(
			statTableModel, "Beschrijvende statistiek", 1, width, height);
		this.model = new DescriptivesModel(
			statTableModel, "Beschrijvende statistiek");
		view = new DescriptivesView(this.model, this.controller);
		// columnIndex = -1!
		this.model.setColumnIndex(1); // gewicht
		this.setSplitGeslacht();
		this.view.update();		
	}

	private void setSplitGeslacht()
	{
		this.model.setColumnSplitIndex(5); // geslacht
		this.model.setSplitOptions(this.model.getSplitOptions());
		this.controller.setSplit(5);
	}

	public void testData()
	{
		this.view.update();
		Label[][][] expected = new Label[7][2][2]; // 7 descriptives, selection, 2 split classes
		expected = getExpectedDataTestCase1();
		Label[][][] actual = view.getDataLabels();
		
		for (int i = 0; i < 7; i++) // descriptive fields: nr, min, max, mean, sd, median, mode
		{
			for (int j = 0; j < 2; j++) // selection: 0 (all cases), 1 (selected cases)
			{
				for (int k = 0; k < 2; k++) // number of splits: m, v
				{
					// compare the text of the label
					assertEquals("i=" + i + ", j=" + j + ", k=" + k, expected[i][j][k].getText(), actual[i][j][k].getText());
				}
			}
		}
	}

	private Label[][][] getExpectedDataTestCase1()
	{
		Label[][][] expected = new Label[7][2][2];
		
		Label label;
		String notAvailable = StatistiekGWT.rb.notAvailable();
		
		String[][][] expectedStrings = new String[][][]{
			// m, all cases
			{{"3", "51", "70", "59.00", "8.04", "56", notAvailable},
			// m, selected cases
			{"0", notAvailable, notAvailable, notAvailable, notAvailable, notAvailable, notAvailable}},
			// v, all cases
			{{"7", "40", "56", "51.86", "5.11", "54", "55"},
			// v, selected cases
			{"3", "40", "55", "49.00", "6.48", "52", notAvailable}}};
		
		for (int i = 0; i < 7; i++) // descriptive fields: nr, min, max, mean, sd, median, mode
		{
			for (int j = 0; j < 2; j++) // selection: 0 (all cases), 1 (selected cases)
			{
				for (int k = 0; k < 2; k++) // number of splits: m, v
				{
					label = new Label(expectedStrings[k][j][i]);
					label.addStyleName(statistiekCss.noWrap());
					expected[i][j][k] = label;
				}
			}
		}
		
		return expected;
	}

}
