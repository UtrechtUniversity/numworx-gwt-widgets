package fi.weblogogwt.client.parameters;

import fi.weblogogwt.client.VarSet;
import fi.weblogogwt.client.expressies.*;
import fi.weblogogwt.client.formuleobjects.*;

/**
 * NumericParameter is the class for any numerical input value that can be calculated (Expressie)
 * Used for parameters in CC's, or the number of repetitions in 'Herhaal'
 * 
 * @author berge020
 */
public class NumericParameter extends TAParameter
{
	/**
	 * expression representing the parameter
	 */
	private Expressie waarde;
	/**
	 * value of waarde
	 */
	private double value;
	/**
	 * true if 'waarde' is a syntactically correct expression, given that all variables exist
	 */
	private boolean isValid;
	/**
	 * true if 'waarde' can be calculated, that is: all its variables exist and have valid numerical values, 
	 * no division by zero, etc.
	 * NOTE: When editing, this var is equal to isValid, because we don't know about the vars yet.
	 * At runtime, isCorrect may turn false, because there are missing vars. This will be used in executing (no)
	 * and painting the CC (it will turn red) 
	 */
	// private boolean isCorrect;  defined in superclass

	public NumericParameter()
	{
		waarde = new BasisExpressie(0);
		parameterText = "0";
		isValid = true;
		isCorrect = true;
		value = 0;				// this is the true value only is expression has been tested with VarSet
	}
	
	/**
	 * Sets the text and determines if the expression is valid.
	 * @param text		the input string to be parsed
	 */
	public void setParameter(String text)
	{
		parameterText = text.trim();
		isValid = true;				// will be set to false when parsing fails...
		isCorrect = true;			// we assume variables to be ok until the program runs
		try
		{	
			waarde = FormuleParser.geefExpressie("$f"+parameterText+"@");
		}
		catch(NumberFormatException ex)
		{	
			isValid = false;
			isCorrect = false;
		}
		if ( waarde == null )
		{
			isValid = false;
			isCorrect = false;
		}
		//System.out.println(parameterText+", "+value+", "+isValid+", "+isCorrect);
	}
	
	/**
	 * @return parameterText
	 */
	public String getParameterText()
	{
		return parameterText;
	}
	
	/**
	 * @return isCorrect
	 */
	public boolean isCorrect(VarSet varSet)
	{
		if ( isValid )
		{
			value = waarde.geefWaarde();
			if(Double.isNaN(value))
				value = varSet.getExpressionValue(waarde);
			isCorrect = !(Double.isNaN(value));
		} 
		else
		{
			isCorrect = false;
		}
		//System.out.println(parameterText+", "+value+", "+isValid+", "+isCorrect);
		return isCorrect;
	}
	
	/**
	 * @return true if (1, edit-time) expression is well-formed (2, runtime) is computable with the current VarSet
	 */
	public boolean isCorrect()
	{
		return isCorrect;
	}
	
	/**
	 * Get the value. This is only guaranteed to yield a valid result if the parameter expression
	 * has been tested for correctness with the current Varset
	 * @return the numeric value
	 */
	public double getValue()
	{
		return value;
	}
	
	/**
	 * Get the 'Expressie'. This is only guaranteed to yield a valid result if the parameter expression
	 * has been tested for correctness with the current Varset
	 * 
	 * @return the Expressie in the parameter
	 */
	public Expressie getExpressie()
	{
		return waarde;
	}
	
	/**
	 * @return value as text
	 */
	public String getValueText()
	{
		return ""+(int)value;
	}
	
}
