package fi.statistiekgwt.client;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.google.gwt.junit.tools.GWTTestSuite;

import fi.statistiekgwt.client.descriptives.DescriptivesModelTest;
import fi.statistiekgwt.client.descriptives.DescriptivesViewTest;
import fi.statistiekgwt.client.dotplot.DotplotViewTest;

public class StatistiekGWTTestSuite extends GWTTestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for a StatistiekGWT Application");
		suite.addTestSuite(StatistiekGWTTest.class);
		suite.addTestSuite(StatTableModelTest.class);
		suite.addTestSuite(DescriptivesModelTest.class);
		suite.addTestSuite(DescriptivesViewTest.class);
		suite.addTestSuite(DotplotViewTest.class);
		return suite;
	}
}
