package fi.statistiekgwt.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.shared.GWT;

/**
 * @author Sylvia van Borkulo
 * 
 */
public class ColorUtils
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
	
	public static final CssColor LIGHT_GREY = CssColor.make(240, 240, 240);
	public static final CssColor PINK = CssColor.make(204, 0, 204);
	public static final CssColor GREY = CssColor.make(220, 220, 220);
	public static final CssColor DEFAULT_VIEW_ELEMENT_COLOR = CssColor.make(67,147,195); // blue
	public static final CssColor BLACK = CssColor.make(0, 0, 0);
	public static final CssColor WHITE = CssColor.make(255, 255, 255);
	public static final RGBColor WHITE_RGB = new RGBColor(255, 255, 255);
	public static final CssColor SELECTION_COLOR = CssColor.make(192,192,192); // darker light grey
	public static final RGBColor SELECTION_COLOR_RGB = new RGBColor(192,192,192); // darker light grey
	public static final CssColor BUTTON_TEXT_GREY = CssColor.make(82, 82, 82); // dark grey

	
	private static List<String> colorList = 
		//new ArrayList<CssColor>(Arrays.asList(COLORS));
		ColorUtils.initializeColorList();
	
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
			return CssColor.make(colorList.get(number));
		}
		else
		{
			CssColor c = CssColor.make(random.nextInt(256),
				random.nextInt(256), random.nextInt(256));
			colorList.add(c.value());
			return c;
		}
	}

	private static ArrayList<String> initializeColorList()
	{
		ArrayList<String> list = new ArrayList<String>();
		
		for (int i = 0; i < COLORS.length; i++) 
		{
			list.add(COLORS[i].value());
		}
		
		return list;
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
		colorList.add(c.value());
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
	 * Get wild card color.
	 * 
	 * @return the wild card color
	 */
	public static CssColor getWildCardColor()
	{
		return SELECTION_COLOR;
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
	 * If the given color is empty, 0 is returned.
	 * @param c
	 * @return
	 */
	public static int getRed(CssColor c)
	{
		int red;
		
		if (c == null)
		{
			GWT.log("ColorGenerator.getRed(c): c null!");
		}
		
		// colorStr e.g. "rgb(67,147,195)"
		String colorStr = c.toString();
		
		if (colorStr.equals(""))
		{
			red = 0;
		}
		else
		{
			int beginIndex = 4;
			int endIndex = colorStr.indexOf(",");
			
			red = Integer.valueOf(colorStr.substring(beginIndex, endIndex));
		}
		
		return red;
	}
	
	/**
	 * Get the green value (RGB) of the given color. 
	 * If the given color is empty, 0 is returned.
	 * @param c
	 * @return
	 */
	public static int getGreen(CssColor c)
	{
		int green;
		
		// colorStr e.g. "rgb(67,147,195)"
		String colorStr = c.toString();
		
		if (colorStr.equals(""))
		{
			green = 0;
		}
		else
		{
			int beginIndex = colorStr.indexOf(",") + 1;
			int endIndex = colorStr.indexOf(",", beginIndex);
	
			green = Integer.valueOf(colorStr.substring(beginIndex, endIndex));
		}
		
		return green;
	}
	
	/**
	 * Get the blue value (RGB) of the given color.
	 * If the given color is empty, 0 is returned.
	 * @param c
	 * @return
	 */
	public static int getBlue(CssColor c)
	{
		int blue;
		
		// colorStr e.g. "rgb(67,147,195)"
		String colorStr = c.toString();
		
		if (colorStr.equals(""))
		{
			blue = 0;
		}
		else
		{
			int indexFirstComma = colorStr.indexOf(",");
			int beginIndex = colorStr.indexOf(",", indexFirstComma + 1) + 1; // begin after second comma
			int endIndex = colorStr.indexOf(")", beginIndex);
		
			blue = Integer.valueOf(colorStr.substring(beginIndex, endIndex));
		}
		
		return blue;
	}
	
	public static RGBColor getRGBColor(CssColor c)
	{
		RGBColor rgb;
		int r = ColorUtils.getRed(c);
		int g = ColorUtils.getGreen(c);
		int b = ColorUtils.getBlue(c);
		
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
	} // class RGBColor
	
	public static int[] hsl2rgb(int[] hsl)
	{
		double h = hsl[0] / 360d;
		double s = hsl[1] / 100d;
		double l = hsl[2] / 100d;
		double r = 0d;
		double g = 0d;
		double b;

		if (s > 0d)
		{
			if (h >= 1d)
			{
				h = 0d;
			}

			h = h * 6d;
			double f = h - Math.floor(h);
			double a = Math.round(l * 255d * (1d - s));
			b = Math.round(l * 255d * (1d - (s * f)));
			double c = Math.round(l * 255d * (1d - (s * (1d - f))));
			l = Math.round(l * 255d);

			switch ((int) Math.floor(h))
			{
				case 0:
					r = l;
					g = c;
					b = a;
					break;
				case 1:
					r = b;
					g = l;
					b = a;
					break;
				case 2:
					r = a;
					g = l;
					b = c;
					break;
				case 3:
					r = a;
					g = b;
					b = l;
					break;
				case 4:
					r = c;
					g = a;
					b = l;
					break;
				case 5:
					r = l;
					g = a;
					break;
			}
			return new int[]
				{ (int) Math.round(r), (int) Math.round(g), (int) Math.round(b) };
		}

		l = Math.round(l * 255d);
		return new int[]
			{ (int) l, (int) l, (int) l };
	}

	public static String toHex(int v)
	{
		v = Math.min(Math.max(v, 0), 255);
		return String.valueOf("0123456789abcdef".charAt(((v - v % 16) / 16))) + //$NON-NLS-1$
			String.valueOf("0123456789abcdef".charAt(v % 16)); //$NON-NLS-1$
	}

	public static String rgb2hex(int[] rgb)
	{
		return toHex(rgb[0]) + toHex(rgb[1]) + toHex(rgb[2]);
	}

	public static String rgb2hex(int r, int g, int b)
	{
		return rgb2hex(new int[]
			{ r, g, b });
	}

	public static String hsl2hex(int[] hsl)
	{
		return rgb2hex(hsl2rgb(hsl));
	}

	public static String hsl2hex(int h, int s, int l)
	{
		return hsl2hex(new int[]
			{ h, s, l });
	}

	public static int[] rgb2hsl(int[] rgb)
	{
		double max = Math.max(Math.max(rgb[0], rgb[1]), rgb[2]); // 0xdd = 221
		double delta = max - Math.min(Math.min(rgb[0], rgb[1]), rgb[2]); // 153
		double h = 0;
		int s = 0;
		int l = (int) Math.round(max * 100d / 255d); // 87 ok
		if (max != 0)
		{
			s = (int) Math.round(delta * 100d / max); // 69 ok
			if (max == rgb[0])
			{
				h = (rgb[1] - rgb[2]) / delta;
			}
			else if (max == rgb[1])
			{
				h = (rgb[2] - rgb[0]) / delta + 2d;
			}
			else
			{
				h = (rgb[0] - rgb[1]) / delta + 4d; // 4.8888888888
			}
			h = Math.min(Math.round(h * 60d), 360d); // 293
			if (h < 0d)
			{
				h += 360d;
			}
		}
		return new int[]
			{ (int) Math.round(h), Math.round(s), l };
	}

	public static int[] getRGB(String color)
	{
		return new int[]
			{ Integer.parseInt(color.substring(0, 2), 16),
				Integer.parseInt(color.substring(2, 4), 16),
				Integer.parseInt(color.substring(4, 6), 16) };
	}
	
	public static int[] getRGB(CssColor color)
	{
		int[] rgb = new int[3];
		
		rgb[0] = ColorUtils.getRed(color);
		rgb[1] = ColorUtils.getGreen(color);
		rgb[2] = ColorUtils.getBlue(color);
		
		return rgb;
	}

}
