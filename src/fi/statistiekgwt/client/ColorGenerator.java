package fi.statistiekgwt.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.google.gwt.canvas.dom.client.CssColor;

/**
 * @author Sylvia van Borkulo
 * 
 */
public class ColorGenerator
{
	// Dark2 van http://colorbrewer2.org/:
	private static final CssColor[] COLORS = {
		CssColor.make(117, 112, 179), // paars
		CssColor.make(230, 171, 2), // geel
		CssColor.make(102, 166, 30), // groen
		CssColor.make(217, 95, 2), // oranje
		CssColor.make(231, 41, 138), // roze
		CssColor.make(102, 102, 102), // grijs
		CssColor.make(27, 158, 119), // groenblauw
		CssColor.make(166, 118, 29) // bruin
	};
	
	private static final CssColor LIGHT_GREY = CssColor.make(240, 240, 240);
	private static final CssColor GREY = CssColor.make(220, 220, 220);
	public static final CssColor DEFAULT_VIEW_ELEMENT_COLOR = CssColor.make(67,147,195); // blue
	public static final CssColor SELECTION_COLOR = CssColor.make(192,192,192); // darker light grey
	public static final CssColor BUTTON_TEXT_GREY = CssColor.make(82, 82, 82); // dark grey
	
	private static ArrayList<CssColor> colorList = 
		new ArrayList<CssColor>(Arrays.asList(COLORS));
	
	private static Random random = new Random();

	/**
	 * Get color for displaying multiple split groups in a single view
	 * 
	 * @param number
	 *            the number of the split group
	 * @return the color in which this split group will be displayed
	 */
	public static CssColor getColor(int number)
	{
		if (number < colorList.size())
		{
			return colorList.get(number);
		}
		else
		{
			CssColor c = CssColor.make(random.nextInt(256),
				random.nextInt(256), random.nextInt(256));
			colorList.add(c);
			return c;
		}
	}

	/**
	 * Get random color
	 * 
	 * @return the random color
	 */
	public static CssColor getColor()
	{
		CssColor c = CssColor.make(random.nextInt(256),
			random.nextInt(256), random.nextInt(256));
		colorList.add(c);
		return c;
	}
	
	/**
	 * Get grey line color
	 * 
	 * @return the grey line color
	 */
	public static CssColor getGreyLineColor()
	{
		return GREY;
	}
	
	/**
	 * Get the default view element color
	 * 
	 * @return the default view element color
	 */
	public static CssColor getDefaultViewElementColor()
	{
		return DEFAULT_VIEW_ELEMENT_COLOR;
	}
	
	/**
	 * Get the red value (RGB) of the given color. 
	 * @param c
	 * @return
	 */
	public static int getRed(CssColor c)
	{
		int red;
		
		// colorStr e.g. "rgb(67,147,195)"
		String colorStr = c.toString();
		int beginIndex = 4;
		int endIndex = colorStr.indexOf(",");
		
		red = Integer.valueOf(colorStr.substring(beginIndex, endIndex));//, 16);
		
		return red;
	}
	
	/**
	 * Get the green value (RGB) of the given color. 
	 * @param c
	 * @return
	 */
	public static int getGreen(CssColor c)
	{
		int green;
		
		// colorStr e.g. "rgb(67,147,195)"
		String colorStr = c.toString();
		int beginIndex = colorStr.indexOf(",") + 1;
		int endIndex = colorStr.indexOf(",", beginIndex);

		green = Integer.valueOf(colorStr.substring(beginIndex, endIndex));
		
		return green;
	}
	
	/**
	 * Get the blue value (RGB) of the given color. 
	 * @param c
	 * @return
	 */
	public static int getBlue(CssColor c)
	{
		int blue;
		
		// colorStr e.g. "rgb(67,147,195)"
		String colorStr = c.toString();
		int indexFirstComma = colorStr.indexOf(",");
		int beginIndex = colorStr.indexOf(",", indexFirstComma + 1) + 1; // begin after second comma
		int endIndex = colorStr.indexOf(")", beginIndex);

		blue = Integer.valueOf(colorStr.substring(beginIndex, endIndex));
		
		return blue;
	}
	
	public static RGBColor getRGBColor(CssColor c)
	{
		RGBColor rgb;
		int r = ColorGenerator.getRed(c);
		int g = ColorGenerator.getGreen(c);
		int b = ColorGenerator.getBlue(c);
		
		rgb = new RGBColor(r, g, b);
		
		return rgb;
	}

	
	public static class RGBColor
	{
		private CssColor c;
		private int r, g, b;
		
		public RGBColor(int r, int g, int b)
		{
			c = CssColor.make(r, g, b);
			this.r = r;
			this.g = g;
			this.b = b;
		}
		
		public int getRed()
		{
			return this.r;
		}
		
		public int getGreen()
		{
			return this.g;
		}
		
		public int getBlue()
		{
			return this.b;
		}
		
		public CssColor getCssColor()
		{
			return this.c;
		}
	}
}
