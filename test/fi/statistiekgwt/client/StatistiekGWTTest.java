package fi.statistiekgwt.client;

import java.util.ArrayList;
import java.util.Arrays;

import fi.statistiekgwt.client.StatistiekGWT;

import com.google.gwt.junit.client.GWTTestCase;

public class StatistiekGWTTest extends GWTTestCase
{

	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName()
	{
		return "fi.statistiekgwt.StatistiekGWT";
	}

	public void testAppropriateBoundaries()
	{
		double min = 0;
		double max = 10;
		int noBins = 5;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(0.0, 3.0, 6.0, 9.0, 12.0, 15.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesOneBin40_70()
	{
		double min = 40;
		double max = 70;
		int noBins = 1;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(40.0, 80.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBins40_70()
	{
		double min = 40;
		double max = 70;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(40.0, 60.00, 80.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBins156_171()
	{
		double min = 156;
		double max = 171;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(156.0, 166.00, 176.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBins90_93()
	{
		double min = 90;
		double max = 93;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(90.0, 92.00, 94.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBins40_90()
	{
		double min = 40;
		double max = 90;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(40.0, 70.00, 100.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBins156_200()
	{
		double min = 156;
		double max = 200;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(156.0, 181.0, 206.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBins6_8d9()
	{
		double min = 6;
		double max = 8.9;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(6.0, 8.0, 10.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBins1_21()
	{
		double min = 1;
		double max = 21;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(1.0, 16.0, 31.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBins5_10()
	{
		double min = 5;
		double max = 10;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(5.0, 8.0, 11.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBins1976_2014()
	{
		double min = 1976;
		double max = 2014;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(1976.0, 1996.0, 2016.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBins1_12()
	{
		double min = 1;
		double max = 12;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(1.0, 11.0, 21.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBins0_360()
	{
		double min = 0;
		double max = 360;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(0.0, 200.0, 400.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBins0d5_18()
	{
		double min = 0.5;
		double max = 18;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(0.0, 10.0, 20.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBins2d6_41d7()
	{
		double min = 2.6;
		double max = 41.7;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(0.0, 25.0, 50.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBinsm12d2_27d2()
	{
		double min = -12.2; // m12d2: m = min, d = decimal separator
		double max = 27.2;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(-40.0, 0.0, 40.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBinsm17d1_20d9()
	{
		double min = -17.1; // m17d1: m = min, d = decimal separator
		double max = 20.9;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(-40.0, 0.0, 40.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTwoBinsm9d7_35()
	{
		double min = -9.7; // m9d7: m = min, d = decimal separator
		double max = 35;
		int noBins = 2;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(-50.0, 0.0, 50.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTenBins40_90()
	{
		double min = 40;
		double max = 90;
		int noBins = 10;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(
			40.0, 46.0, 52.0, 58.0, 64.0, 70.0, 76.0, 82.0, 88.0, 94.0, 100.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTenBins2_3()
	{
		double min = 2;
		double max = 3;
		int noBins = 10;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(
			2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesTenBins16_16()
	{
		double min = 16;
		double max = 16;
		int noBins = 10;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(
			16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesSixBins2d48_2d57()
	{
		double min = 2.48;
		double max = 2.57;
		int noBins = 6;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(
			2.48, 2.50, 2.52, 2.54, 2.56, 2.58, 2.60));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	public void testAppropriateBoundariesSixBins248_257()
	{
		double min = 248;
		double max = 257;
		int noBins = 6;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(
			248.0, 250.0, 252.0, 254.0, 256.0, 258.0, 260.0));
		
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundaries(min, max, noBins);
		assertEquals("", expected, actual);
	}

	/**
	 * Minimum 9.73 (9d73), 
	 * maximum 10.28 (10d28),
	 * bin width 0.05 (w0d05),
	 * start bin 9.7 (s9d7)
	 */
	public void testAppropriateBoundariesFromBinSettings9d73_10d28_w0d05_s9d7()
	{
		double min = 9.73;
		double max = 10.28;
		double binWidth = 0.05;
		double minBoundary = 9.7;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(
			9.7, 9.75, 9.8, 9.85, 9.9, 9.95, 10.0, 10.05, 10.1, 10.15, 10.2, 10.25, 10.3));
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundariesFromBinSettings(min, max, binWidth, minBoundary);
		assertEquals("Expected " + expected.toArray()
			+ ", actual " + actual.toArray(), actual, expected);
	}

	/**
	 * Minimum -2.2 (m2d2), 
	 * maximum 1.1 (1d1),
	 * bin width 1.0 (1),
	 * start bin -3.0 (m3)
	 */
	public void testAppropriateBoundariesFromBinSettingsm2d2_1d1_w1_sm3()
	{
		double min = -2.2;
		double max = 1.1;
		double binWidth = 1.0;
		double minBoundary = -3.0;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(
			-3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0));
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundariesFromBinSettings(min, max, binWidth, minBoundary);
		assertEquals("Expected " + expected.toArray()
			+ ", actual " + actual.toArray(), actual, expected);
	}

	/**
	 * Minimum 16.0, 
	 * maximum 16.0,
	 * bin width 1.0,
	 * start bin 16.0
	 */
	public void testAppropriateBoundariesFromBinSettings16_16_w1_s16()
	{
		double min = 16.0;
		double max = 16.0;
		double binWidth = 1.0;
		double minBoundary = 16.0;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(16.0, 17.0));
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundariesFromBinSettings(min, max, binWidth, minBoundary);
		assertEquals("Expected " + expected.toArray()
			+ ", actual " + actual.toArray(), actual, expected);
	}

	public void testAppropriateBoundariesFromBinSettings()
	{
		double min = 0;
		double max = 10;
		double binWidth = 3;
		double minBoundary = 0;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(0.0, 3.0, 6.0, 9.0, 12.0));
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundariesFromBinSettings(min, max, binWidth, minBoundary);
		assertEquals("Expected " + expected.toArray()
			+ ", actual " + actual.toArray(), actual, expected);
	}

	public void testAppropriateBoundariesFromBinSettingsBinWidthTooSmall()
	{
		double min = 0;
		double max = 10;
		double binWidth = 10/51;
		double minBoundary = 0;
		
		ArrayList<Double> expected = null;
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundariesFromBinSettings(min, max, binWidth, minBoundary);
		assertEquals(expected, actual);
	}

	public void testAppropriateBoundariesFromBinSettingsBinWidthSmallButCorrect()
	{
		double min = 0;
		double max = 10;
		double binWidth = (double) 10/50;
		double minBoundary = 0;
		
		ArrayList<Double> expected = new ArrayList<Double>(Arrays.asList(
			0.0, 0.2, 0.4, 0.6, 0.8, 1.0, 1.2, 1.4, 1.6, 1.8, 
			2.0, 2.2, 2.4, 2.6, 2.8, 3.0, 3.2, 3.4, 3.6, 3.8, 
			4.0, 4.2, 4.4, 4.6, 4.8, 5.0, 5.2, 5.4, 5.6, 5.8, 
			6.0, 6.2, 6.4, 6.6, 6.8, 7.0, 7.2, 7.4, 7.6, 7.8, 
			8.0, 8.2, 8.4, 8.6, 8.8, 9.0, 9.2, 9.4, 9.6, 9.8, 
			10.0, 10.2, 10.4, 10.6, 10.8, 11.0));
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundariesFromBinSettings(min, max, binWidth, minBoundary);
		assertEquals("Expected " + expected.toArray()
			+ ", actual " + actual.toArray(), actual, expected);
	}

	public void testAppropriateBoundariesFromBinSettingsBinWidthTooLarge()
	{
		double min = 0;
		double max = 10;
		double binWidth = 25;
		double minBoundary = 0;
		
		ArrayList<Double> expected = null;
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundariesFromBinSettings(min, max, binWidth, minBoundary);
		assertEquals(expected, actual);
	}

	public void testAppropriateBoundariesFromBinSettingsBinWidthNegative()
	{
		double min = 0;
		double max = 10;
		double binWidth = -1;
		double minBoundary = 0;
		
		ArrayList<Double> expected = null;
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundariesFromBinSettings(min, max, binWidth, minBoundary);
		assertEquals(expected, actual);
	}

	public void testAppropriateBoundariesFromBinSettingsBinWidthZero()
	{
		double min = 0;
		double max = 10;
		double binWidth = 0;
		double minBoundary = 0;
		
		ArrayList<Double> expected = null;
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundariesFromBinSettings(min, max, binWidth, minBoundary);
		assertEquals(expected, actual);
	}

	public void testAppropriateBoundariesFromBinSettingsMinBoundaryTooSmall()
	{
		double min = 0;
		double max = 10;
		double binWidth = 1;
		double minBoundary = -7;
		
		ArrayList<Double> expected = null;
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundariesFromBinSettings(min, max, binWidth, minBoundary);
		assertEquals(expected, actual);
	}

	public void testAppropriateBoundariesFromBinSettingsMinBoundaryTooLarge()
	{
		double min = 0;
		double max = 10;
		double binWidth = 1;
		double minBoundary = 1;
		
		ArrayList<Double> expected = null;
		ArrayList<Double> actual = StatistiekGWT.appropriateBoundariesFromBinSettings(min, max, binWidth, minBoundary);
		assertEquals(expected, actual);
	}
	
	public void testgetNumberOfDecimals0d00099()
	{
		String doubleString = "9.9E-4";
		int expected = 5;
		int actual = StatistiekGWT.getNumberOfDecimals(doubleString);
		assertEquals(expected, actual);
	}
	
	public void testgetNumberOfDecimals8d5()
	{
		String doubleString = "8.5";
		int expected = 1;
		int actual = StatistiekGWT.getNumberOfDecimals(doubleString);
		assertEquals(expected, actual);
	}
	
	public void testgetNumberOfDecimals0d0001()
	{
		String doubleString = "1.0E-4";
		int expected = 4;
		int actual = StatistiekGWT.getNumberOfDecimals(doubleString);
		assertEquals(expected, actual);
	}
}
