package fi.weblogogwt.client.parameters;

import fi.weblogogwt.client.VarSet;

/**
 * A parameter class intended for overloaded print-commands. This parameter can either be
 * a String constant (TekenApplet doesn't do String vars) or a numeric expression.
 * 
 * @author berge020
 */
public class TextParameter extends NumericParameter
{
	private boolean isConstantString;

	public TextParameter()
	{
		parameterText = "";
		isConstantString = true;
		isCorrect = true;
	}

	@Override
	public void setParameter(String s)
	{
		if ( s.startsWith("\"") && s.endsWith("\""))
		{
			isConstantString = true;
			parameterText = s;
			isCorrect = true;
		} else
		{
			isConstantString = false;
			super.setParameter(s);
		}
	}

	/**
	 * This method returns the String constants WITH the enclosing quotes (for editing, exporting)
	 * Use getValueText() to get the value without quotes (or the numeric value)
	 * 
	 * @see fi.javalogoweb.NumericParameter#getParameterText()
	 */
	@Override
	public String getParameterText()
	{
		return parameterText;
	}

	@Override
	public boolean isCorrect(VarSet varSet)
	{
		if ( isConstantString )
		{
			return true;
		} else
		{
			return super.isCorrect(varSet);
		}
	}

	public String getValueText()
	{
		if ( isConstantString )
		{
			return getParameterText().substring(1, getParameterText().length()-1);
		} else
		{
			return ""+(super.getValue());
		}
	}

}
