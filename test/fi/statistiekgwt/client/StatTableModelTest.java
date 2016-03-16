package fi.statistiekgwt.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.junit.client.GWTTestCase;

import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

public class StatTableModelTest extends GWTTestCase
{
	private StatTableModel statTableModel;
	private static double delta = 1e-15; // error in comparing doubles 

	/**
	 * Must refer to a valid module that sources this class.
	 */
	@Override
	public String getModuleName()
	{
		return "fi.statistiekgwt.StatistiekGWT";
	}

	/**
	 * Create an initial StatTableModel with 10 rows, 6 columns, none selected.
	 * @throws Exception
	 */
	public void gwtSetUp() throws Exception
	{
		StatistiekGWT statistiek = new StatistiekGWT();
		this.statTableModel = new StatTableModel();
		
		// set the state of statTableModel
		HashMap<String, Object> h = new HashMap<String, Object>();
		//ArrayList<ColumnType> columnClass = new ArrayList<ColumnType>();
		List columnClassMap = new ArrayList();

		this.setColumnClassMap(columnClassMap);
		ArrayList<ArrayList<Object>> values = new ArrayList<ArrayList<Object>>(); // ArrayList van ArrayLists
		this.setValues(values);
		
		h.put("rowCount", new Integer(10));
		h.put("columnCount", new Integer(6));
		h.put("columnNames", new ArrayList<String>(Arrays.asList(
			"Naam", "Gewicht", "Lengte", "Klein", "Profiel", "Geslacht")));
		h.put("columnClassMapped", columnClassMap);
		h.put("values", values);
		
		this.statTableModel.setState(h);
		this.setSelectionListNoneSelected();
	}

	/**
	 * Create an initial StatTableModel with 12 rows, 6 columns, 
	 * four rows selected.
	 * @throws Exception
	 */
	public void setUpDataWithWildcards() throws Exception
	{
		//StatistiekGWT statistiek = new StatistiekGWT();
		this.statTableModel = new StatTableModel();
		
		// set the state of statTableModel
		HashMap<String, Object> h = new HashMap<String, Object>();
		ArrayList<ColumnType> columnClass = new ArrayList<ColumnType>();
		this.setColumnClassMap(columnClass);
		ArrayList<ArrayList<Object>> values = new ArrayList<ArrayList<Object>>(); // ArrayList van ArrayLists
		this.setValuesWithWildcards(values);
		
		h.put("rowCount", new Integer(12));
		h.put("columnCount", new Integer(6));
		h.put("columnNames", new ArrayList<String>(Arrays.asList(
			"Naam", "Gewicht", "Lengte", "Klein", "Profiel", "Geslacht")));
		h.put("columnClass", columnClass);
		h.put("values", values);
		
		this.statTableModel.setState(h);
		this.setSelectionListFourSelectedWithWildcards();
	}

	private void setValues(ArrayList<ArrayList<Object>> values)
	{
		ArrayList<Object> rij = new ArrayList<Object>(Arrays.asList(
			"Sylvia", "55", "156", "0.03", "CM", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Sietske", "40", "156", "0.03", "NT", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Mieke", "52", "156", "0.04", "NG", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Susanne", "56", "157", "0.05", "EM", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Thea", "55", "168", "0.03", "EM", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Mariozee", "51", "168", "0.06", "NG", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Wim", "51", "170", "0.05", "NG", "m"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Peter", "70", "170", "0.04", "NG", "m"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Ada", "54", "171", "0.05", "NG", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Al", "56", "171", "0.03", "NG", "m"));
		values.add(rij);
	}

	private void setValuesWithWildcards(ArrayList<ArrayList<Object>> values)
	{
		ArrayList<Object> rij = new ArrayList<Object>(Arrays.asList(
			"Sylvia", "55", "156", "0.03", "CM", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Sietske", "40", "156", "0.03", "NT", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Mieke", "*", "156", "0.04", "NG", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Susanne", "56", "157", "0.05", "EM", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Thea", "*", "168", "0.03", "EM", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Mariozee", "51", "*", "0.06", "NG", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Wim", "51", "170", "0.05", "NG", "m"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Peter", "70", "*", "0.04", "NG", "m"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Ada", "54", "171", "0.05", "NG", "v"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Al", "56", "171", "0.03", "*", "m"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Mr. Wildcard", "*", "*", "*", "NG", "m"));
		values.add(rij);
		rij = new ArrayList<Object>(Arrays.asList(
			"Ms. Wildcard", "*", "*", "*", "NG", "v"));
		values.add(rij);
	}

	private void setColumnClassMap(List columnClassMap)
	{
		ColumnType type= new ColumnType(AllowedTypes.STRING); // Naam
		columnClassMap.add(type.toMap());
		type = new ColumnType(AllowedTypes.DOUBLE); // Gewicht
		columnClassMap.add(type.toMap());
		type = new ColumnType(AllowedTypes.DOUBLE); // Lengte
		columnClassMap.add(type.toMap());
		type = new ColumnType(AllowedTypes.DOUBLE); // Klein
		columnClassMap.add(type.toMap());
		String[] options = {"NG", "NT", "EM", "CM", "*"};
		type = new ColumnType(AllowedTypes.ENUM, options); // Profiel
		columnClassMap.add(type.toMap());
		String[] optionsGeslacht = {"m", "v", "*"};
		type = new ColumnType(AllowedTypes.ENUM, optionsGeslacht); // Geslacht
		columnClassMap.add(type.toMap());
	}

	private void setSelectionListNoneSelected()
	{
		this.statTableModel.setSelectionListWithoutEvent(new ArrayList<Boolean>(Arrays.asList(
			false, false, false, false, false, false, false, false, false, false)));
	}

	private void setSelectionListThreeSelected()
	{
		this.statTableModel.setSelectionListWithoutEvent(new ArrayList<Boolean>(Arrays.asList(
			true, true, true, false, false, false, false, false, false, false)));
	}
	
	private void setSelectionListFourSelectedWithWildcards()
	{
		this.statTableModel.setSelectionListWithoutEvent(new ArrayList<Boolean>(Arrays.asList(
			true, true, true, false, false, false, false, false, false, false, true, false)));
	}
	
	/**
	 * Test set 1.
	 * @return
	 */
	public StatTableModel getStatTableModelWithSelection()
	{
		try
		{
			this.gwtSetUp();
			this.setSelectionListThreeSelected();
			return this.statTableModel;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public StatTableModel getStatTableModelWithSelectionAndWildcards()
	{
		try
		{
			this.setUpDataWithWildcards();
			return this.statTableModel;
		}
		catch (Exception e)
		{
			return null;
		}
	}


	/**
	 ******************  TEST COLUMN Gewicht ******************
	 */
	public void testGetColumnMinGewicht()
	{
		double expected = 40;
		double actual = this.statTableModel.getColumnMin(1);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnMaxGewicht()
	{
		double expected = 70;
		double actual = this.statTableModel.getColumnMax(1);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnMeanGewicht()
	{
		double expected = 54;
		double actual = this.statTableModel.getColumnMean(1);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnSDGewicht()
	{
		double expected = 6.96;
		double actual = StatistiekGWT.round(this.statTableModel.getColumnSD(1), 2);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnMedianGewicht()
	{
		double expected = 54.5;
		double actual = this.statTableModel.getColumnMedian(1);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnModeGewicht()
	{
		//StatistiekGWT statistiek = new StatistiekGWT(); 
		String expected = StatistiekGWT.rb.notAvailable();
		String actual = this.statTableModel.getColumnMode(1);
		assertEquals("", expected, actual);
	}

	public void testGetColumnMinGewichtOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = "40";
		String actual = this.statTableModel.getColumnMinOfSelection(1);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnMaxGewichtOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = "55";
		String actual = this.statTableModel.getColumnMaxOfSelection(1);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnMeanGewichtOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = "49";
		String actual = this.statTableModel.getColumnMeanOfSelection(1);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnSDGewichtOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		double expected = 6.48;
		double actual = StatistiekGWT.round(this.statTableModel.getColumnSDOfSelection(1), 2);
		assertEquals("", expected, actual, delta);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnMedianGewichtOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		double expected = 52;
		double actual = this.statTableModel.getColumnMedianOfSelection(1);
		assertEquals("", expected, actual, delta);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnModeGewichtOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		//StatistiekGWT statistiek = new StatistiekGWT(); 
		String expected = StatistiekGWT.rb.notAvailable();
		String actual = this.statTableModel.getColumnModeOfSelection(1);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	/**
	 ******************  TEST COLUMN Lengte ******************
	 */

	public void testGetColumnMinLengte()
	{
		double expected = 156;
		double actual = this.statTableModel.getColumnMin(2);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnMaxLengte()
	{
		double expected = 171;
		double actual = this.statTableModel.getColumnMax(2);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnMeanLengte()
	{
		double expected = 164.3;
		double actual = this.statTableModel.getColumnMean(2);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnSDLengte()
	{
		double expected = 6.65;
		double actual = StatistiekGWT.round(this.statTableModel.getColumnSD(2), 2);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnMedianLengte()
	{
		double expected = 168;
		double actual = this.statTableModel.getColumnMedian(2);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnModeLengte()
	{
		String expected = "156";
		String actual = this.statTableModel.getColumnMode(2);
		assertEquals("", expected, actual);
	}
	
	/**
	 * In development mode is
	 * 		String.valueOf(156) = "156.0"
	 * in production mode is
	 * 		String.valueOf(156) = "156"
	 * 
	 * Kwam naar voren in this.statTableModel.getColumnMode().
	 * 
	 * Met NumberFormat is er geen verschil tussen development
	 * en production mode.
	 */
	public void testDoubleToString()
	{
		String expected = "156";
		Double d = new Double(156);
		NumberFormat nf = StatistiekGWT.getNumberFormat(d);
		String actual = nf.format(d);

		assertEquals("", expected, actual);
	}

	public void testGetColumnMinLengteOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = "156";
		String actual = this.statTableModel.getColumnMinOfSelection(2);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnMaxLengteOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = "156";
		String actual = this.statTableModel.getColumnMaxOfSelection(2);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnMeanLengteOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = "156";
		String actual = this.statTableModel.getColumnMeanOfSelection(2);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnSDLengteOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		double expected = 0;
		double actual = StatistiekGWT.round(this.statTableModel.getColumnSDOfSelection(2), 2);
		assertEquals("", expected, actual, delta);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnMedianLengteOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		double expected = 156;
		double actual = this.statTableModel.getColumnMedianOfSelection(2);
		assertEquals("", expected, actual, delta);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnModeLengteOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = "156";
		String actual = this.statTableModel.getColumnModeOfSelection(2);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	/**
	 ******************  TEST COLUMN Klein ******************
	 */

	public void testGetColumnMinKlein()
	{
		double expected = 0.03;
		double actual = this.statTableModel.getColumnMin(3);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnMaxKlein()
	{
		double expected = 0.06;
		double actual = this.statTableModel.getColumnMax(3);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnMeanKlein()
	{
		double expected = 0.041;
		double actual = this.statTableModel.getColumnMean(3);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnSDKlein()
	{
		double expected = 0.0104;
		double actual = StatistiekGWT.round(
			this.statTableModel.getColumnSD(3), 4);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnMedianKlein()
	{
		double expected = 0.04;
		double actual = this.statTableModel.getColumnMedian(3);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnModeKlein()
	{
		String expected = "0.03";
		String actual = this.statTableModel.getColumnMode(3);
		assertEquals("", expected, actual);
	}

	public void testGetColumnMinKleinOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = "0,03";
		String actual = this.statTableModel.getColumnMinOfSelection(3);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnMaxKleinOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = "0,04"; // komma voor nl
		String actual = this.statTableModel.getColumnMaxOfSelection(3);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnMeanKleinOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = "0,0333";
		String actual = StatistiekGWT.round(
			this.statTableModel.getColumnMeanOfSelection(3), 4);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnSDKleinOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		double expected = 0.0047;
		double actual = StatistiekGWT.round(
			this.statTableModel.getColumnSDOfSelection(3), 4);
		assertEquals("", expected, actual, delta);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnMedianKleinOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		double expected = 0.03;
		double actual = this.statTableModel.getColumnMedianOfSelection(3);
		assertEquals("", expected, actual, delta);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnModeKleinOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = "0.03";
		String actual = this.statTableModel.getColumnModeOfSelection(3);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	/**
	 ******************  TEST COLUMN Profiel ******************
	 */

	public void testGetColumnMinProfiel()
	{
		double expected = 0;
		double actual = this.statTableModel.getColumnMin(4);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnMaxProfiel()
	{
		double expected = 0;
		double actual = this.statTableModel.getColumnMax(4);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnMeanProfiel()
	{
		double expected = 0;
		double actual = this.statTableModel.getColumnMean(4);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnSDProfiel()
	{
		double expected = 0;
		double actual = StatistiekGWT.round(
			this.statTableModel.getColumnSD(4), 4);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnMedianProfiel()
	{
		double expected = 0;
		double actual = this.statTableModel.getColumnMedian(4);
		assertEquals("", expected, actual, delta);
	}

	public void testGetColumnModeProfiel()
	{
		String expected = "NG";
		String actual = this.statTableModel.getColumnMode(4);
		assertEquals("", expected, actual);
	}

	public void testGetColumnMinProfielOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = StatistiekGWT.rb.notAvailable();
		String actual = this.statTableModel.getColumnMinOfSelection(4);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnMaxProfielOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = StatistiekGWT.rb.notAvailable();
		String actual = this.statTableModel.getColumnMaxOfSelection(4);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnMeanProfielOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		String expected = StatistiekGWT.rb.notAvailable();
		String actual = this.statTableModel.getColumnMeanOfSelection(4);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnSDProfielOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		double expected = 0;
		double actual = StatistiekGWT.round(
			this.statTableModel.getColumnSDOfSelection(4), 4);
		assertEquals("", expected, actual, delta);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnMedianProfielOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		double expected = 0;
		double actual = this.statTableModel.getColumnMedianOfSelection(4);
		assertEquals("", expected, actual, delta);
		
		this.setSelectionListNoneSelected();
	}

	public void testGetColumnModeProfielOfSelection()
	{
		this.setSelectionListThreeSelected();
		
		//StatistiekGWT statistiek = new StatistiekGWT(); 
		String expected = StatistiekGWT.rb.notAvailable();
		String actual = this.statTableModel.getColumnModeOfSelection(4);
		assertEquals("", expected, actual);
		
		this.setSelectionListNoneSelected();
	}
}
