package fi.statistiekgwt.client.types;

import java.io.Serializable;

/**
 * Type-safe enumeration die de toegestande types voor kolommen geeft.
 * 
 * @author Manu Drijvers
 * 
 */
public class AllowedTypes implements Serializable
{
	private static final long serialVersionUID = -6262339455107483614L;
	private String type;
	private Class typeClass;

	public static final AllowedTypes INTEGER = new AllowedTypes("Integer",
		Integer.class);
	public static final AllowedTypes DOUBLE = new AllowedTypes("Double",
		Double.class);
	public static final AllowedTypes STRING = new AllowedTypes("String",
		String.class);
	public static final AllowedTypes ENUM = new AllowedTypes("Enum",
		String.class);
	public static final AllowedTypes[] allowedTypes =
		{ AllowedTypes.INTEGER, AllowedTypes.DOUBLE, AllowedTypes.STRING,
			AllowedTypes.ENUM };

	/**
	 * Private constructor to achieve type-safe enumeration
	 * 
	 * @param type
	 * @param typeClass
	 */
	private AllowedTypes(String type, Class typeClass)
	{
		this.type = type;
		this.typeClass = typeClass;
	}

	/**
	 * Override toString
	 */
	public String toString()
	{
		return this.type;
	}
	
	/**
	 * Tests if o is valid instance of this AllowedType.
	 * Enums must be tested in ColumnType.
	 * 
	 * @param o
	 *            Object to be tested
	 * @return true if o is a valid instance of this AllowedType
	 */
	public boolean isValidInstance(Object o)
	{
		if (this.equals(AllowedTypes.INTEGER))
		{
			try
			{
				Integer.parseInt((String) o);
				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		}
		else if (this.equals(AllowedTypes.DOUBLE))
		{
			try
			{
				// Allow commas in doubles
				String s = ((String) o).replaceAll(",", ".");
				Double.parseDouble((String) s);
//				Double.parseDouble((String) o);
				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		}
		else if (this.equals(AllowedTypes.STRING))
		{
			try
			{
				String a = (String) o;
				if (!a.equals(""))
					return true;
				else
					return false;
			}
			catch (ClassCastException e)
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * @return The Class you can typecast elements of this AllowedType to
	 */
	public Class getTypeClass()
	{
		return this.typeClass;
	}

	public boolean isNumber()
	{
		return this.equals(AllowedTypes.INTEGER)
			|| this.equals(AllowedTypes.DOUBLE);
	}

	public boolean equals(Object o)
	{
		if (o == null || o.getClass() != AllowedTypes.class)
		{
			return false;
		}
		else
		{
			AllowedTypes other = (AllowedTypes) o;
			if (this.type.equals(other.type)
				&& this.typeClass.equals(other.typeClass))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}
