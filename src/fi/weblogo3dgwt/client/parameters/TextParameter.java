package fi.weblogo3dgwt.client.parameters;

import fi.weblogo3dgwt.client.VarSet;

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
//System.out.println("setParam isConstantString");			
		} else
		{
			isConstantString = false;
			super.setParameter(s);
//System.out.println("setParam !isConstantString");			
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
//if (!parameterText.equals(""))		
//System.out.println("getParamText=" + parameterText);		
		return parameterText;
	}

	@Override
	public boolean isCorrect(VarSet varSet)
	{
		if ( isConstantString )
		{
//System.out.println("isCorrect isConstantString");			
			return true;
		} else
		{
//System.out.println("isCorrect !isConstantString");			
			return super.isCorrect(varSet);
		}
	}

	public String getValueText()
	{
		if ( isConstantString )
		{	if (getParameterText().length() >= 1)
				return getParameterText().substring(1, getParameterText().length()-1);
			else
				return "";
		} 
		else
		{
			return ""+(super.getValue());
		}
	}

}
