package fi.statistiekgwt.client.descriptives;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.junit.client.GWTTestCase;

import fi.statistiekgwt.client.StatTableModelTest;
import fi.statistiekgwt.client.SplitOptions;
import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.descriptives.DescriptivesController;
import fi.statistiekgwt.client.descriptives.DescriptivesModel;
import fi.statistiekgwt.client.descriptives.DescriptivesView;
import fi.statistiekgwt.client.histogram.HistogramModel.FrequencyTuple;

public class DescriptivesModelTest extends GWTTestCase
{
	private DescriptivesView view;
	private DescriptivesModel model;
	private DescriptivesController controller;
	private StatistiekGWT statistiek;
	private static double delta = 1e-15; // error in comparing doubles 

	/**
	 * Must refer to a valid module that sources this class.
	 */
	@Override
	public String getModuleName()
	{
		return "fi.statistiekgwt.StatistiekGWT";
	}

	public void gwtSetUp() throws Exception
	{
		statistiek = new StatistiekGWT();
		this.setUpTestCase1();
	}

	/**
	 * Set up test case 1:
	 * 		10 cases
	 * 		columnIndex = 1 (gewicht)
	 * 		columnSplitIndex = 5 (geslacht)
	 * 		cases 0-2 selected
	 */
	private void setUpTestCase1()
	{
		// use a statTableModel with the test data
		StatTableModelTest statTableModelTest = new StatTableModelTest();
		StatTableModel statTableModel = 
			statTableModelTest.getStatTableModelWithSelection();
		
		int width = 800;
		int height = 600;
		
		this.controller = new DescriptivesController(
			statTableModel, "Kengetallen", 1, width, height);
		this.model = new DescriptivesModel(
			statTableModel, "Kengetallen");
		view = new DescriptivesView(this.model, this.controller);
		// columnIndex = -1!
		this.model.setColumnIndex(1); // gewicht
		this.setSplitGeslacht();
		this.view.update();		
	}

	/**
	 * Set up test case 2:
	 * 		12 cases
	 * 		columnIndex = 1 (gewicht)
	 * 		columnSplitIndex = 5 (geslacht)
	 * 		cases 0-2 and 10 selected
	 */
	private void setUpTestCase2()
	{
		// use the test data
		StatTableModelTest statTableModelTest = new StatTableModelTest();
		StatTableModel statTableModel = 
			statTableModelTest.getStatTableModelWithSelectionAndWildcards();
		
		int width = 800;
		int height = 600;
		
		this.controller = new DescriptivesController(
			statTableModel, "Kengetallen", 1, width, height);
		this.model = new DescriptivesModel(
			statTableModel, "Kengetallen");
		view = new DescriptivesView(this.model, this.controller);
		// columnIndex = -1!
		this.model.setColumnIndex(1); // gewicht
		this.setSplitGeslacht();
		this.view.update();		
	}

	private void setSplitGeslacht()
	{
		this.model.setColumnSplitIndex(5); // Geslacht
		this.model.setSplitOptions(this.model.getSplitOptions());
		this.controller.setSplit(5);
	}

	public void testGetBinBoundaries()
	{
		double min = this.model.getStatTableModel().getColumnMin(1); // columnIndex = 1 (gewicht)
		double max = this.model.getStatTableModel().getColumnMax(1); // columnIndex = 1 (gewicht)
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(min, max + 10)); // verschil in tientallen, dus 1 tiental meer voor bovengrens
		ArrayList<Double> actual = this.model.getBinBoundaries();
		assertEquals("", expected, actual);
	}

	public void testGetViewName()
	{
		String expected = "Kengetallen";
		String actual = this.model.getViewName();
		assertEquals("", expected, actual);
	}

	public void testGetColumnIndex()
	{
		int expected = 1;
		int actual = this.model.getColumnIndex();
		
		assertEquals("", expected, actual);
	}

	public void testNumberClassFrequencyNull()
	{
		// set enum column index
		this.model.setColumnIndex(5); // columnIndex = 5 (Geslacht), enum

		int[][] expected = null; // columnIndex = 5 (Geslacht), not number
		int[][] actual = this.model.numberClassFrequency();
		
		assertEquals("", expected, actual);
	}

	public void testNumberClassFrequency()
	{
		int[][] expected = {{3, 0}, {7, 3}}; // m, v; // columnIndex = 1 (Gewicht), split = 5 (Geslacht)
		int[][] actual = this.model.numberClassFrequency();
		
		int numberOfColumnClasses = 1; // columnIndex = 1 (Gewicht)
		int numberOfSplitClasses = 2; // split = 5 (Geslacht)
		for (int i = 0; i < numberOfSplitClasses; i++)
		{
			for (int j = 0; j < numberOfColumnClasses; j++)
			{
				assertEquals("", expected[i][j], actual[i][j]);
			}
		}
	}

	public void testEnumClassFrequencyNull()
	{
		FrequencyTuple[][] expected = null; // columnIndex = 1 (gewicht), not enum
		FrequencyTuple[][] actual = this.model.enumClassFrequency();
		
		assertEquals("", expected, actual);
	}

	public void testEnumClassFrequencyEnum()
	{
		// set enum column index
		this.model.setColumnIndex(4); // columnIndex = 4 (profiel), enum
		
		FrequencyTuple[][] expected = this.getExpectedEnumClassFrequencyEnum(); 
		
		FrequencyTuple[][] actual = this.model.enumClassFrequency();
		
		int numberOfColumnClasses = 4; // columnIndex = 4 (profiel)
		int numberOfSplitClasses = 2; // split = geslacht
		for (int i = 0; i < numberOfSplitClasses; i++)
		{
			for (int j = 0; j < numberOfColumnClasses; j++)
			{
				assertEquals("", expected[i][j].label, actual[i][j].label);
				assertEquals("", expected[i][j].frequency, actual[i][j].frequency);
				assertEquals("", expected[i][j].selectionFrequency, actual[i][j].selectionFrequency);
			}
		}
		
		// reset
		this.model.setColumnIndex(1); // columnIndex = 1 (gewicht)
	}

	private FrequencyTuple[][] getExpectedEnumClassFrequencyEnum()
	{
		int numberOfColumnClasses = 4; // columnIndex = 4 (profiel)
		int numberOfSplitClasses = 2; // split = geslacht

		FrequencyTuple[][] expected = new FrequencyTuple[numberOfSplitClasses][]; // split = geslacht
		
		String[] labels = {"NG", "NT", "EM", "CM"}; // non-alphabetic order: "NG", "NT", "EM", "CM"
		// was CM, EM, NG, NT
		int[][] frequencies = {{3, 0, 0, 0}, {3, 1, 2, 1}}; // m, v
		int[][] freqSelection = {{0, 0, 0, 0}, {1, 1, 0, 1}}; // m, v
		
		for (int i = 0; i < numberOfSplitClasses; i++)
		{
			expected[i] = new FrequencyTuple[numberOfColumnClasses];
			for (int j = 0; j < numberOfColumnClasses; j++)
			{
				expected[i][j] = new FrequencyTuple(labels[j], frequencies[i][j],
					freqSelection[i][j]);
			}
		}
		
		return expected;
	}

	public void testEnumClassFrequencyString()
	{
		// set enum column index
		this.model.setColumnIndex(0); // columnIndex = 0 (naam), string
		
		FrequencyTuple[][] expected = this.getExpectedEnumClassFrequencyString(); 
		
		FrequencyTuple[][] actual = this.model.enumClassFrequency();
		
		int numberOfColumnClasses = 10; // columnIndex = 0 (naam)
		int numberOfSplitClasses = 2; // split = geslacht
		for (int i = 0; i < numberOfSplitClasses; i++)
		{
			for (int j = 0; j < numberOfColumnClasses; j++)
			{
//				System.out.println("(" + i + "," + j + ")");
				assertEquals("", expected[i][j].label, actual[i][j].label);
				assertEquals("", expected[i][j].frequency, actual[i][j].frequency);
				assertEquals("", expected[i][j].selectionFrequency, actual[i][j].selectionFrequency);
			}
		}
		
		// reset
		this.model.setColumnIndex(1); // columnIndex = 1 (gewicht)
	}

	private FrequencyTuple[][] getExpectedEnumClassFrequencyString()
	{
		int numberOfColumnClasses = 10; // columnIndex = 0 (naam)
		int numberOfSplitClasses = 2; // split = geslacht

		FrequencyTuple[][] expected = new FrequencyTuple[numberOfSplitClasses][];
		
		String[] labels = {"Ada", "Al", "Mariozee", "Mieke", "Peter", "Sietske", 
			"Susanne", "Sylvia", "Thea", "Wim"}; // alphabetic order
		int[][] frequencies = {{0, 1, 0, 0, 1, 0, 0, 0, 0, 1}, {1, 0, 1, 1, 0, 1, 1, 1, 1, 0}}; // m, v
		int[][] freqSelection = {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 1, 0, 1, 0, 1, 0, 0}}; // m, v
		
		for (int i = 0; i < numberOfSplitClasses; i++)
		{
			expected[i] = new FrequencyTuple[numberOfColumnClasses];
			for (int j = 0; j < numberOfColumnClasses; j++)
			{
				expected[i][j] = new FrequencyTuple(labels[j], frequencies[i][j],
					freqSelection[i][j]);
			}
		}
		
		return expected;
	}

	public void testGetSplitOptions()
	{
		SplitOptions expected = new SplitOptions();
		expected.setColumnSplitIndex(5); // geslacht
		
		SplitOptions actual = this.model.getSplitOptions();
		
//		assertEquals("", 
//			expected.getBinBoundaries().toArray(), 
//			actual.getBinBoundaries().toArray());
		assertEquals("", expected.getBinBoundaries(), actual.getBinBoundaries());
		
		assertEquals("",
			expected.getColumnSplitIndex(), 
			actual.getColumnSplitIndex());
		
		int numberOfSplitClasses = 2;
		for (int splitClass = 0; splitClass < numberOfSplitClasses; splitClass++)
		{
			assertEquals("", 
				expected.getSplitClassLabel(splitClass, this.model.getStatTableModel()), 
				actual.getSplitClassLabel(splitClass, this.model.getStatTableModel()));
		}
	}

	public void testGetColumnSplitIndex()
	{
		int expected = 5;
		int actual = this.model.getSplitOptions().getColumnSplitIndex();

		assertEquals("", expected, actual);
	}

	public void testGetColumnModeSplit0NoSelection()
	{
		String expected = StatistiekGWT.rb.notAvailable();
		String actual = this.model.getColumnMode(
			1, 0, false); // columnIndex = 1 (gewicht), split 0 (m), no selection

		assertEquals("", expected, actual);
	}

	public void testGetColumnModeSplit1NoSelection()
	{
		String expected = "55";
		String actual = this.model.getColumnMode(
			1, 1, false); // columnIndex = 1 (gewicht), split 1 (v), no selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnModeSplit0Selection()
	{
		String expected = StatistiekGWT.rb.notAvailable();
		String actual = this.model.getColumnMode(
			1, 0, true); // columnIndex = 1 (gewicht), split 0 (m), selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnModeSplit1Selection()
	{
		String expected = StatistiekGWT.rb.notAvailable();
		String actual = this.model.getColumnMode(
			1, 1, true); // columnIndex = 1 (gewicht), split 1 (v), selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMaxSplit0NoSelection()
	{
		String expected = "70";
		String actual = this.model.getColumnMax(
			1, 0, false); // columnIndex = 1 (gewicht), split 0 (m), no selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMaxSplit1NoSelection()
	{
		String expected = "56";
		String actual = this.model.getColumnMax(
			1, 1, false); // columnIndex = 1 (gewicht), split 1 (v), no selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMaxSplit0Selection()
	{
		String expected = StatistiekGWT.rb.notAvailable();
		String actual = this.model.getColumnMax(
			1, 0, true); // columnIndex = 1 (gewicht), split 0 (m), selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMaxSplit1Selection()
	{
		String expected = "55";
		String actual = this.model.getColumnMax(
			1, 1, true); // columnIndex = 1 (gewicht), split 1 (v), selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMinSplit0NoSelection()
	{
		String expected = "51";
		String actual = this.model.getColumnMin(
			1, 0, false); // columnIndex = 1 (gewicht), split 0 (m), no selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMinSplit1NoSelection()
	{
		String expected = "40";
		String actual = this.model.getColumnMin(
			1, 1, false); // columnIndex = 1 (gewicht), split 1 (v), no selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMinSplit0Selection()
	{
		String expected = StatistiekGWT.rb.notAvailable();
		String actual = this.model.getColumnMin(
			1, 0, true); // columnIndex = 1 (gewicht), split 0 (m), selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMinSplit1Selection()
	{
		String expected = "40";
		String actual = this.model.getColumnMin(
			1, 1, true); // columnIndex = 1 (gewicht), split 1 (v), selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMeanSplit0NoSelection()
	{
		String expected = "59";
		String actual = this.model.getColumnMean(
			1, 0, false); // columnIndex = 1 (gewicht), split 0 (m), no selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMeanSplit1NoSelection()
	{
		String expected = "51,86";
		String actual = StatistiekGWT.round(this.model.getColumnMean(
			1, 1, false), 2); // columnIndex = 1 (gewicht), split 1 (v), no selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMeanSplit0Selection()
	{
		String expected = StatistiekGWT.rb.notAvailable();
		String actual = this.model.getColumnMean(
			1, 0, true); // columnIndex = 1 (gewicht), split 0 (m), selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMeanSplit1Selection()
	{
		String expected = "49";
		String actual = this.model.getColumnMean(
			1, 1, true); // columnIndex = 1 (gewicht), split 1 (v), selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnSDSplit0NoSelection()
	{
		String expected = "8,04";
		String actual = StatistiekGWT.round(
			this.model.getColumnSD(1, 0, false), 2); // columnIndex = 1 (gewicht), split 0, no selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnSDSplit1NoSelection()
	{
		String expected = "5,11";
		String actual = StatistiekGWT.round(
			this.model.getColumnSD(1, 1, false), 2); // columnIndex = 1 (gewicht), split 1, no selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnSDSplit0Selection()
	{
		String expected = StatistiekGWT.rb.notAvailable();
		String actual = this.model.getColumnSD(1, 0, true); // columnIndex = 1 (gewicht), split 0, selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnSDSplit1Selection()
	{
		String expected = "6,48";
		String actual = StatistiekGWT.round(
			this.model.getColumnSD(1, 1, true), 2); // columnIndex = 1 (gewicht), split 1, selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMedianSplit0NoSelection()
	{
		double expected = 56;
		double actual = StatistiekGWT.round(
			this.model.getColumnMedian(1, 0, false), 2); // columnIndex = 1 (gewicht), split 0, no selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMedianSplit1NoSelection()
	{
		double expected = 54;
		double actual = StatistiekGWT.round(
			this.model.getColumnMedian(1, 1, false), 2); // columnIndex = 1 (gewicht), split 1, no selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMedianSplit0Selection()
	{
		double expected = 0; // median is not available; no values in the selection for split 0
		double actual = this.model.getColumnMedian(1, 0, true); // columnIndex = 1 (gewicht), split 0, selection
		
		assertEquals("", expected, actual);
	}

	public void testGetColumnMedianSplit1Selection()
	{
		double expected = 52;
		double actual = StatistiekGWT.round(
			this.model.getColumnMedian(1, 1, true), 2); // columnIndex = 1 (gewicht), split 1, selection
		
		assertEquals("", expected, actual);
	}

}
