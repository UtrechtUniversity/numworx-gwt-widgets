package fi.weblogo3dgwt.client.parameters;

import java.lang.*;
import fi.weblogo3dgwt.client.VarSet;

/**
 * Class for any identifier in the TekenApplet. 
 * Identifiers are varaiable names, deeltaak names and parameters
 * 
 * @author berge020
 */
public class Identifier extends TAParameter
{
	/**
	 * An indentifier can start with a default name, such as 'variabele' or 'deeltaak2'
	 * or the empty String (parameter of deeltaak)
	 * 
	 * @param the default name of this parameter's owner (VarCC or DeeltaakBodyC), or empty String
	 */
	public Identifier(String s)
	{
		setParameter(s);
	}

	@Override
	public void setParameter(String s)
	{
    	if ( s == null ) s = "";
		parameterText = s.trim();	
		isCorrect = isIdentifier(parameterText);
	}
	
    /**
     * Check if string s is an identidier using the standard Unicode rules, supplied in class Character
     * 
     * @param s		string to be tested
     * @return		true, if correct
     */
    private boolean isIdentifier(String s)
    {
    	if ( s.equals("")) 
    		return false;
    	if ( !isUnicodeIdentifierStart(s.charAt(0)) )
    	{	return false;
    	
    	}
    	for ( int i = 1; i < s.length(); i++)
    	{
    		if ( !isUnicodeIdentifierPart(s.charAt(i)) )
    			return false;
    	}
    	// s passed all tests (literally)
    	return true;
    }

    private boolean isUnicodeIdentifierStart(char ch)
    {
    	return (Character.isLetter(ch) == true); // || (Character.getType(ch) == Character.LETTER_NUMBER); 
    }

    private boolean isUnicodeIdentifierPart(char ch)
    {
    	return (Character.isLetter(ch) == true) || (Character.isDigit(ch) == true) ||
    		   (ch == '_');
    	
    		   //(Character.getType(ch) == Character.CONNECTOR_PUNCTUATION) ||
    		   //(Character.getType(ch) == Character.COMBINING_SPACING_MARK) ||
    		   //(Character.getType(ch) == Character.NON_SPACING_MARK); 
    }

	@Override
	public String getParameterText()
	{
		return parameterText;
	}

	@Override
	public boolean isCorrect(VarSet varSet)
	{
		// identifier is independent of VarSet
		return isCorrect;
	}

	@Override
	public boolean isCorrect()
	{
		return isCorrect;
	}
	
	@Override
	public String getValueText()
	{
		return getParameterText();
	}
}
