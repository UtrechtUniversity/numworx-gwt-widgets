package fi.weblogogwt.client.parameters;

import fi.weblogogwt.client.VarSet;
import fi.weblogogwt.client.expressies.*;
import fi.weblogogwt.client.formuleobjects.*;

/**
 * KleurParameter is the class for an input expression representing a Color.
 * Both text ("rood") and RGB-values can be given.
 * Used for parameters in CC's, or the number of repetitions in 'Herhaal'
 * the ColorParameter is correct if the color can be calculated, that is: all its variables exist and have valid numerical values, 
 * no division by zero, etc. When color is indicated by name, it is true if the name is in the list
 * of standard colors.
 * NOTE: at this moment this can only determined at execution time.
 * @author berge020
 */

import com.google.gwt.canvas.dom.client.CssColor;

public class ColorParameter extends TAParameter
{
	/**
	 * true if parameter was given as a Color name (rood, groen...)
	 */
	private boolean isColorByName;			
	/**
	 * Three expressions for the color rgb-components
	 * Note: this was an Expressie[3] first, but then there were 'ArrayStoreExceptions' when 
	 * assigning them with a subclass of Expressie! WtF!!
	 */
	private Expressie redExpression;
	private Expressie greenExpression;	
	private Expressie blueExpression;	
	
	int[] rgb = new int[3];
	
	/**
	 * true if '...EV' is a syntactically correct expression, given that all variables exist
	 */
	private boolean redExpressionValid;
	private boolean greenExpressionValid;
	private boolean blueExpressionValid;

	/**
	 * the color 
	 */
	private CssColor theColor;
	
	public ColorParameter()
	{
		setDefaultColor();
	}
	
	/**
	 * Set the Color to black, the default when no input is given.
	 */
	private void setDefaultColor()
	{
		parameterText = "";
		isColorByName = true;
		theColor = CssColor.make(0,0,0);
		isCorrect = true;
	}
	
	/**
	 * set the validity of the r-g-b-expressiosn
	 * @param b validity
	 */
	private void setAllValid(boolean b)
	{
		redExpressionValid = b;
		greenExpressionValid = b;
		blueExpressionValid = b;
	}
	
	/**
	 * check all r-g-b-expressions are valid 
	 * @return true/false
	 */
	private boolean allValid()
	{
		return redExpressionValid && greenExpressionValid && blueExpressionValid;
	}
	
	/**
	 * Sets the text and determines if the expression is valid.
	 * 
	 * @param text		the input string to be parsed
	 */
	public void setParameter(String text)
	{
		parameterText = text.trim();
		if ( parameterText.equals("") )
		{
			setDefaultColor();
			return;
		}
		String[] args = StringUtils.split(parameterText, ",");
		if ( args.length == 1 )
		{
			parseColorName(args[0]);
		} else
		{
			parseRGB(args);
			isCorrect = allValid(); 	// we assume variables in r,g,b-expressions to be ok until the program runs
		}
	}

	/**
	 * find the color its name in a standart color list (NL and EN allowed)
	 * @param s name of the color
	 */
	private void parseColorName(String s)
	{
		isColorByName = true;
		isCorrect = true; // until proven differently...
		s = s.trim();
		if (s.equals("rood"))
			theColor = CssColor.make(255, 0, 0);
		else if (s.equals("groen"))
			theColor = CssColor.make(0, 255, 0);
		else if (s.equals("blauw"))
			theColor = CssColor.make(0, 0, 255);
		else if (s.equals("geel"))
			theColor = CssColor.make(255, 255, 0);
		else if (s.equals("cyaan"))
			theColor = CssColor.make(0, 255, 255);
		else if (s.equals("roze"))
			theColor = CssColor.make(255, 20, 147);
		else if (s.equals("zwart"))
			theColor = CssColor.make(0, 0, 0);
		else if (s.equals("grijs"))
			theColor = CssColor.make(192, 192, 192);
		else if (s.equals("lichtgrijs"))
			theColor = CssColor.make(220, 220, 220);
		else if (s.equals("magenta"))
			theColor = CssColor.make(255, 0, 255);
		else if (s.equals("wit"))
			theColor = CssColor.make(255, 255, 255);
		else if (s.equals("oranje"))
			theColor = CssColor.make(255, 127, 0);

		else if (s.equals("red"))
			theColor = CssColor.make(255, 0, 0);
		else if (s.equals("green"))
			theColor = CssColor.make(0, 255, 0);
		else if (s.equals("blue"))
			theColor = CssColor.make(0, 0, 255);
		else if (s.equals("yellow"))
			theColor = CssColor.make(255, 255, 0);
		else if (s.equals("cyan"))
			theColor = CssColor.make(0, 255, 255);
		else if (s.equals("pink"))
			theColor = CssColor.make(255, 20, 147);
		else if (s.equals("black"))
			theColor = CssColor.make(0, 0, 0);
		else if (s.equals("gray"))
			theColor = CssColor.make(192, 192, 192);
		else if (s.equals("lightGray"))
			theColor = CssColor.make(220, 220, 220);
		else if (s.equals("magenta"))
			theColor = CssColor.make(255, 0, 255);
		else if (s.equals("white"))
			theColor = CssColor.make(255, 255, 255);
		else if (s.equals("orange"))
			theColor = CssColor.make(255, 127, 0);

		else
			isCorrect = false; // ... here
	}

	/**
	 * find the color from an array containing Expressions for red/green/blue
	 * @param s expression array
	 */
	private void parseRGB(String s[])
	{
		isColorByName = false;
		setAllValid(true);
		if ( s.length != 3 )
		{
			setAllValid(false);		
			return;
		}
		// rood
		s[0] = s[0].trim();
		try
		{	
			redExpression = FormuleParser.geefExpressie("$f"+s[0]+"@");
		}
		catch(NumberFormatException ex) { redExpressionValid = false; }
		if ( redExpression == null ) { redExpressionValid = false; }
		// groen
		s[1] = s[1].trim();
		try
		{	
			greenExpression = FormuleParser.geefExpressie("$f"+s[1]+"@");
		}
		catch(NumberFormatException ex) { greenExpressionValid = false; }
		if ( greenExpression == null ) { greenExpressionValid = false; }
		// blauw
		s[2] = s[2].trim();
		try
		{	
			blueExpression = FormuleParser.geefExpressie("$f"+s[2]+"@");
		}
		catch(NumberFormatException ex) { blueExpressionValid = false; }
		if ( blueExpression == null ) { blueExpressionValid = false; }
	}
	
	/**
	 * Return the text of this ColorParameter. When the parameter is not valid or constant,
	 * the original text is returned (for editing)
	 * 
	 * @return		text
	 */
	public String getParameterText()
	{
		if ( !isColorByName )
		{
			if ( allValid() )
			{	
				return redExpression.toString()+", "+greenExpression.toString()+", "+blueExpression.toString();
			}
		} 
		return parameterText;
	}
	
	/**
	 * Calculate a single colorcomponent r/g/b with given VarSet
	 * @param e the color expression
	 * @param varSet the current varSet
	 * @return			int, one of r,g,b
	 */
	private int calculateColorValue(Expressie e, VarSet varSet)
	{
		// value of colorcomponent RED
		double value = e.geefWaarde();
		if(Double.isNaN(value))
			value = varSet.getExpressionValue(e);
		if ( Double.isNaN(value))
		{	
			isCorrect = false;
			return 0;						// irrelevant, because of 'isCorrect'
		} else
		{
			return Math.max(0, Math.min((int)value, 255));	//just limit... is not ok!!
		}
	}
	
	/**
	 * At execution time, test the parameter expressions with the given VarSet.
	 * Also generate the Color that goes with the RGB-values.
	 * Note: when the color is constant, indicated by name, this will return true if the name is ok,
	 * which has been estabished earlier.
	 * 
	 * @param varSet	current VarSet in the running program
	 * @return			true, if a valid color can be generated from teh VarSet
	 */
	public boolean isCorrect(VarSet varSet)
	{
		if ( isColorByName )
		{
			return isCorrect;			// varSet is irrelevant when color is constant, indicated by name.
		} else
		{
			if ( !allValid() )
			{
				isCorrect = false;
				return isCorrect;
			}
			isCorrect = true;			// will be set to false if an error occurs
			//int[] rgb = new int[3];
			rgb[0] = calculateColorValue(redExpression, varSet);
			rgb[1] = calculateColorValue(greenExpression, varSet);
			rgb[2] = calculateColorValue(blueExpression, varSet);
			if ( isCorrect )
			{
				//theColor = new Color(rgb[0], rgb[1], rgb[2]);
				theColor = CssColor.make(rgb[0], rgb[1], rgb[2]);
			}
			return isCorrect;
		}
	}	
		
	/**
	 * Get the Color of this ColorParameter. Only call this method at execution time after testing
	 * is the color parameter is correct
	 * 
	 * @return the Color
	 */
	public CssColor getColor()
	{
		return theColor;
	}

	public boolean isCorrect()
	{
		return isCorrect;
	}

	public String getValueText()
	{
		if ( isColorByName )
		{
			return getParameterText();
		} else
		{
			return rgb[0] + ", " + rgb[1] + ", " + rgb[2];
		}
	}
}
