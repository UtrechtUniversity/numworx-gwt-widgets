package fi.weblogo3dgwt.client;

/**
 * A simple wrapper class for variables in TekenApplet.
 * A TAVariable just has a name and a value.
 * 
 * @author berge020
 */
public class TAVariable
{
	private String name;
	private double value;
	
	private int level;
	//private boolean isParameter;

	public TAVariable(String n, double v, int l)
	{
		name = n;
		value = v;
		level = l;
	}

	public double getValue()
	{
		return value;
	}

	public void setValue(double value)
	{
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public int getLevel()
	{
		return level;
	}

}
