package fi.weblogogwt.client.parameters;

import fi.weblogogwt.client.formuleobjects.StringUtils;
import fi.weblogogwt.client.JavaLogoSchuifVeld;
import fi.weblogogwt.client.VarSet;

/**
 * This class represents the list of comma separated identifiers that are the parameters
 * in the header of a deeltaak definition.
 * The number of identifiers is bound to a maximum, for practical reasons. You can't edit more than 3 parameters.
 * 
 * For simplicity, this maximum number of identifiers is always created at construction, an idCount tells
 * how many are actually in use.
 * 
 * @author berge020
 */
public class IdentifierList extends TAParameter
{
	/**
	 * number of identifiers in the list that are in use
	 */
	private int idCount;
	/**
	 * the identifierList
	 */
	private Identifier[] ids;

	/**
	 * Create a new IdentifierList with zero identifiers in use
	 */
	public IdentifierList()
	{
		ids = new Identifier[JavaLogoSchuifVeld.maxParamCount];
		for ( int i=0; i<JavaLogoSchuifVeld.maxParamCount; i++)
		{
			ids[i] = new Identifier("");
		}
		idCount = 0;
		isCorrect = true;
		parameterText = "";
	}

	/**
	 * Set the list from a comma separated string and determine the number and correctness.
	 * The IdentifierList is correct iff
	 * - there aren't any parameters (parameterless deeltaak) OR
	 * - the number of parameters is less than or equal to JavaLogoSchuifVeld.maxParameterCount AND
	 * - all parameters are correct identifiers
	 */
	public void setParameter(String s)
	{
		s = s.trim();
		parameterText = s;			// remember inserted text for editing incorrect lists (count)
		if ( s.isEmpty() )
		{
			idCount = 0;
			isCorrect = true;
			return;
		}
		String idstrings[] = StringUtils.split(s, ",");
		if ( idstrings.length > JavaLogoSchuifVeld.maxParamCount )
		{
			idCount = 0;
			isCorrect = false;
			return;			
		}
		idCount = idstrings.length;
		isCorrect = true;
		for ( int i=0; i<idCount; i++ )
		{
			ids[i].setParameter(idstrings[i]);
			isCorrect = isCorrect && ids[i].isCorrect();
		}
	}

	/**
	 * @return  a comma separated String of all id's in use
	 */
	public String getParameterText()
	{
		if ( idCount == 0 ) return parameterText;			// will be "" or too many params
		String s = ids[0].getParameterText();
		for ( int i=1; i<idCount; i++ )
		{
			s = s + ", " + ids[i].getParameterText();
		}
		return s;
	}

	/**
	 * Returns true if all identifiers are correct (varSet irrelevant).
	 * Note: also returns true if there are no identifiers, a deeltaak without parameters is ok!
	 * @return value of isCorrect
	 */
	public boolean isCorrect(VarSet varSet)
	{
		return isCorrect;
	}

	/**
	 * Returns true if all identifiers are correct.
	 * Note: also returns true if there are no identifiers, a deeltaak without parameters is ok!
	 * @return value of isCorrect
	 */
	public boolean isCorrect()
	{
		return isCorrect;
	}
	
	/**
	 * get the number of identifiers in use 
	 * @return idCount
	 */
	public int getIdCount()
	{
		return idCount;
	}
	
	/**
	 * get the identifier at index index
	 * @param index the index
	 * @return the identifier at index index
	 */
	public String getIdentifier(int index)
	{
		return ids[index].getParameterText();
	}

	/**
	 * calls getParameterText()
	 * @return list of all identifiers in use separated by komma's
	 */
	public String getValueText()
	{
		return getParameterText();
	}

}
