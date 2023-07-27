package fi.binomverdgwt.client;

public class BVInvoer 
{
	String input;
	
	/**
	 * Constructor
	 * @param input De input string
	 */
	public BVInvoer(String input) 
	{
		this.input = input;
	}
	
	/**
	 * Zet nieuwe input string
	 * @param input de nieuwe input
	 */
	public void setInput(String input) 
	{
		this.input = input;
	}
	
	/**
	 * Geeft het input veld terug
	 */
	public String getInput() 
	{
		return this.input;
	}
	
	/**
	 * Kijkt of de invoer een breuk is
	 */
	public boolean isBreuk() 
	{
		boolean inRandomDeel = false;
		char c;
		for(int i = 0; i < this.input.length(); i++) 
		{
			c = this.input.charAt(i);
			if (c == '#') 
			{
				inRandomDeel = !inRandomDeel;
			}
			else if(c == '/' && !inRandomDeel) 
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Controleert op delen door nul
	 * @return true als door nul gedeeld wordt
	 */
	public boolean deeltDoorNul() 
	{
		if (!this.isBreuk()) 
		{
			return false;
		}
		else 
		{
			try 
			{
				double noemer = Double.parseDouble(this.getNoemerString());
				if(noemer == 0.0) 
				{
					return true;
				}
				else 
				{
					return false;
				}
			}
			catch(NumberFormatException e) 
			{
				return false;
			}
		}
	}
	
	/**
	 * geeft de teller-substring van een breuk input
	 */
	public String getTellerString() 
	{
		int i;
		boolean inRandomDeel = false;
		for (i = 0; i < this.input.length(); i++) 
		{
			if (this.input.charAt(i) == '#') 
			{
				inRandomDeel = !inRandomDeel;
			}
			else if (!inRandomDeel && this.input.charAt(i) == '/') 
			{
				break;
			}
		}		
		return this.input.substring(0, i);
	}
	
	/**
	 * geeft de noemer-substring van een breuk input
	 */
	public String getNoemerString() 
	{
		int i;
		boolean inRandomDeel = false;
		for (i = 0; i < this.input.length(); i++) 
		{
			if (this.input.charAt(i) == '#') 
			{
				inRandomDeel = !inRandomDeel;
			}
			else if (!inRandomDeel && this.input.charAt(i) == '/') 
			{
				break;
			}
		}		
		return this.input.substring(i+1, this.input.length());
	}
	
	/**
	 * Kijkt of de substring een random variabele is
	 * @param substring De te controleren (sub)string
	 * @return true als het een random variabele is
	 */
	public static boolean isRandomVar(String substring) 
	{
		if (substring.length() >= 3 && substring.charAt(0) == '#' && substring.charAt(substring.length()-1) == '#') 
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
	
	/**
	 * kijkt of de niet-breuk substring valide double input is
	 */
	private static boolean isValidDoubleSubstring(String substring) 
	{
		if(BVInvoer.isRandomVar(substring)) 
		{
			return true;
		}
		else 
		{
			try 
			{
				double temp = Double.parseDouble(substring);
				return true;
			}
			catch(NumberFormatException e) 
			{
				return false;
			}
		}
	}
	
	/**
	 * bepaalt of de input een geldige double invoer is met als minimum 0.0 en als maximum 1.0
	 * @return true als het geldige invoer is
	 */
	public boolean isValidDoubleInput() 
	{
		if(!this.isBreuk()) 
		{
			return BVInvoer.isValidDoubleSubstring(this.input);
		}
		else 
		{
			return BVInvoer.isValidDoubleSubstring(this.getTellerString()) && BVInvoer.isValidDoubleSubstring(this.getNoemerString()) && !this.deeltDoorNul();
		}
	}
	
	/**
	 * kijkt of de niet-breuk substring valide positieve integer invoer is
	 */
	private static boolean isValidIntSubstring(String substring) 
	{
		if(BVInvoer.isRandomVar(substring)) 
		{
			return true;
		}
		else 
		{
			try 
			{
				double temp = Double.parseDouble(substring);
				return temp >= 0.0;
			}
			catch(NumberFormatException e) 
			{
				return false;
			}
		}
	}
	
	/**
	 * bepaalt of de input een geldige positieve integer invoer is
	 * @return true als het geldige invoer is
	 */
	public boolean isValidIntInput() 
	{
		if(!this.isBreuk()) 
		{
			return BVInvoer.isValidIntSubstring(this.input);
		}
		else 
		{
			return false;
		}
	}
	
	/**
	 * Kijkt of de invoer random variabelen bevat
	 * @return true als randomvars voorkomen in input
	 */
	public boolean isRandomInput() 
	{
		if(!this.isBreuk()) 
		{
			return BVInvoer.isRandomVar(this.input);
		}
		else 
		{
			return BVInvoer.isRandomVar(this.getTellerString()) || BVInvoer.isRandomVar(this.getNoemerString());
		}
	}
	
	/**
	 * haal de .0 achter getallen weg
	 */
	public void haalPuntNulWeg() 
	{
		if(this.isBreuk()) 
		{
			if(!BVInvoer.isRandomVar(this.getTellerString())) 
			{
				double temp = Double.parseDouble(this.getTellerString());
				if((double)(int)temp == temp) 
				{
					this.setInput(Integer.toString((int)temp) + "/" + this.getNoemerString());
				}
			}
			if(!BVInvoer.isRandomVar(this.getNoemerString())) 
			{
				double temp = Double.parseDouble(this.getNoemerString());
				if((double)(int)temp == temp) 
				{
					this.setInput(this.getTellerString() + "/" + Integer.toString((int)temp));
				}
			}
		}
		else 
		{
			if(!BVInvoer.isRandomVar(this.input)) 
			{
				double temp = Double.parseDouble(this.input);
				if((double)(int)temp == temp) 
				{
					this.setInput(Integer.toString((int)temp));
				}
			}
		}
	}
	
	/**
	 * override equals
	 */
	public boolean equals(Object o) 
	{
		if(o == null || o.getClass() != BVInvoer.class) 
		{
			return false;
		}
		else 
		{
			BVInvoer other = (BVInvoer) o;
			return this.input.equals(other.input);
		}
	}
}
