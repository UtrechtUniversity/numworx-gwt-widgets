package fi.statistiekgwt.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import fi.statistiekgwt.client.ColorGenerator.RGBColor;

/**
 * 
 * Extended JPanel that shows a color gradient
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class ColorPreviewer
{
	private RGBColor c1;
	private RGBColor c2;
	private Canvas canvas;
	private Context2d g;

	/**
	 * Constructor
	 * 
	 * @param c1
	 *            The color of the left-hand side
	 * @param c2
	 *            The color of the right-hand side
	 */
	public ColorPreviewer(RGBColor c1, RGBColor c2)
	{
		this.c1 = c1;
		this.c2 = c2;
		this.canvas = Canvas.createIfSupported();
		this.initContext2d();
	}

	public void initContext2d() 
	{
		this.g = canvas.getContext2d();	
	}

	public void paint()
	{
		paint(g);
	}
	
	public void paint(Context2d context)
	{
		int w = this.canvas.getOffsetWidth();
		int h = this.canvas.getOffsetHeight();
		
		 // black
		RGBColor black = new RGBColor(0,0,0);
		context.setStrokeStyle(black.getCssColor());
		context.clearRect(0, 0, w, h);
		context.strokeRect(0, 0, w - 1, h - 1);
		int width = w - 2;

		for (int x = 0; x < width; x++)
		{
			RGBColor mixColor = ColorPreviewer.mixColors(c1, c2, (double) x
				/ (double) width);
			context.beginPath();
			 // change to color c
			context.setStrokeStyle(mixColor.getCssColor());
			// stroke from (x + 1, 1)
			context.moveTo(x + 1, 1);
			// ... to (x + 1, h - 2)
			context.lineTo(x + 1, h - 2);
			context.stroke();
		}
	}

	/**
	 * Mix two colors
	 * 
	 * @param c1
	 *            Color 1
	 * @param c2
	 *            Color 2
	 * @param d
	 *            How to mix; 0 is just color 1, 1 is just color 2
	 * @return The mixed color
	 */
	public static RGBColor mixColors(RGBColor c1, RGBColor c2, double d)
	{
		int red = (int) (d * c2.getRed() + (1 - d) * c1.getRed());
		int green = (int) (d * c2.getGreen() + (1 - d) * c1.getGreen());
		int blue = (int) (d * c2.getBlue() + (1 - d) * c1.getBlue());
		
		RGBColor mixColor = new RGBColor(red, green, blue);
		return (mixColor);
	}

	/**
	 * Mix two colors
	 * 
	 * @param c1
	 *            Color 1
	 * @param c2
	 *            Color 2
	 * @param d
	 *            How to mix; 0 is just color 1, 1 is just color 2
	 * @return The mixed color in cssColor string format
	 */
	public static String mixColorsToString(RGBColor c1, RGBColor c2, double d)
	{
		int red = (int) (d * c2.getRed() + (1 - d) * c1.getRed());
		int green = (int) (d * c2.getGreen() + (1 - d) * c1.getGreen());
		int blue = (int) (d * c2.getBlue() + (1 - d) * c1.getBlue());
		
		RGBColor mixColor = new RGBColor(red, green, blue);
		return (mixColor.getCssColor().value());
	}

	/**
	 * Change the color of the left-hand side
	 * 
	 * @param c1
	 *            the new color of the left-hand side
	 */
	public void setColorA(RGBColor c1)
	{
		this.c1 = c1;
		this.paint();
	}

	/**
	 * Change the color of the right-hand side
	 * 
	 * @param c2
	 *            the new color of the right-hand side
	 */
	public void setColorB(RGBColor c2)
	{
		this.c2 = c2;

		this.paint();
	}
}
