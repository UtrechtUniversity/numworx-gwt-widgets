package fi.weblogogwt.client;

/**
 * A simple wrapper class for variables in TekenApplet.
 * A TAVariable just has a name, a value and a level
 * @author berge020
 */
public class TAVariable
{
	/**
	 * name of the variable
	 */
	private String name;
	/** 
	 * value of the variable
	 */
	private double value;
	/**
	 * execution stack level of the variable, level = 0 is a global variable
	 * see class VarSet  
	 */
	private int level;

	/**
	 * constructor
	 * @param n name
	 * @param v value
	 * @param l level
	 */
	public TAVariable(String n, double v, int l)
	{	name = n;
		value = v;
		level = l;
	}

	/**
	 * getter for value
	 * @return value
	 */
	public double getValue()
	{	return value;
	}

	/**
	 * setter for value 
	 * @param value new value
	 */
	public void setValue(double value)
	{	this.value = value;
	}

	/**
	 * getter for name
	 * @return name
	 */
	public String getName()
	{	return name;
	}

	/**
	 * getter for level
	 * @return level
	 */
	public int getLevel()
	{	return level;
	}
}
