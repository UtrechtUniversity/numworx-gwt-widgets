package fi.weblogogwt.client.parameters;

import fi.weblogogwt.client.VarSet;

/**
 * Superclass for all parameters in the TekenApplet. Parameters should be taken a bit loosely,
 * it stands for all things users can change in a program by typing in CC's, that is:
 * numerical expressions, colours, var/deeltaak identifiers, conditions, loop counts.
 * 
 * All communication will go through the four methods below, but most subclasses will
 * add one accessible method: getValue(), with return type double/Color/boolean.
 * 
 * @author berge020
 */
public abstract class TAParameter
{
	protected String parameterText;
	protected boolean isCorrect;
	
	public TAParameter()
	{
		// nothing here
	}
	
	/**
	 * Set the (text of the) parameter and test it for syntactic corretness (if necessary)
	 * 
	 * @param s
	 */
	public abstract void setParameter(String s);
	
	/**
	 * Gets the text of the parameter, regardless of if it's correct or not
	 * Note: users want to edit an incorrect param to fix the problem
	 * 
	 * @return
	 */
	public abstract String getParameterText();
	
	/**
	 * Test the parameter by computing it with the current VarSet. If correct, the value should
	 * also be computed.
	 * At this stage (runtime) we can see if all variables in the expression exist, don't give
	 * division by zero, etc...
	 * 
	 * @param varSet
	 * @return
	 */
	public abstract boolean isCorrect(VarSet varSet);
	
	/**
	 * @return true if (1, edit-time) expression is well-formed (2, runtime) is computable with the current VarSet
	 * Note: shortcut, at runtime preferably use  isCorrect(VarSet varSet) or make sure it has been called.
	 */
	public abstract boolean isCorrect();

	/**
	 * Returns the value of the parameter as a String, used to display the 'actual' command when tracing
	 * Note: parameter must have been tested with the VarSet
	 * 
	 * @return	value as String
	 */
	public abstract String getValueText();

}
