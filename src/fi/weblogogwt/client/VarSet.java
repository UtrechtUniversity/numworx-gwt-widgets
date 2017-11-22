package fi.weblogogwt.client;

import java.util.*;

import fi.weblogogwt.client.expressies.*;

/**
 * class keeping track of variables (global or local) in the program code
 */
public class VarSet 
{
	/**
	 * 'Level' in the stack of calls. Every call of a Deeltaak will increase the level, returning
	 * from a Deeltaak will decrease the level. In this way we will be able to remove local variables
	 * when their block finishes and print variables in a stack-trace manner.
	 */
	private int level;
	/**
	 * level of current deeltaak. This decides if a variable is visible: the level of the variable must be
	 * greater than or equal to this value for the variable to be in scope. We may include level 0,
	 * this will make the variables in tekenalgoritme global.
	 */
	private int levelOfCurrentDeeltaak;
	/**
	 * Fix for resolving the scope: this array records for every level the level of the deeltaak
	 * it is in. Will typically look like 0,0,2,2,2,5,5... with levels 0,2,5 deeltaken and 
	 * the other levels blocks in if-statements and loops. Needed to restore the correct levelCDT
	 * when ending a level.
	 */
	private int[] deeltaaklevels;
	/**
	 * List of all variables. New variables will be added to the end, so the list will be ordered
	 * with respect to the level that we're in.
	 */
	private ArrayList<TAVariable> variabelen;
	/**
	 * Descriptions associated with each level, such as 'In Deeltaak vierkant'.
	 * this array is used as a stack
	 */
	private String[] levelDescriptions;
	
	/**
	 * constructor
	 */
	public VarSet()
	{	
		variabelen = new ArrayList<TAVariable>();
		level = 0;
		levelOfCurrentDeeltaak = 0;
		deeltaaklevels = new int[100];
		for ( int i=0; i<100; i++ )
			deeltaaklevels[i] = 0;
		levelDescriptions = new String[100];
		levelDescriptions[0] = "Tekenalgoritme";
	}
	
	/**
	 * getter for level
	 * @return level
	 */
	public int getLevel()
	{	return level;
	}

	/**
	 * increase the level, keep track of deeltaken
	 * @param description String describing the level
	 * @param isDeeltaak is the program starting a deeltaak?  
	 */
	void increaseLevel(String description, boolean isDeeltaak)
	{
		level++;
		levelDescriptions[level] = description;
		if ( isDeeltaak )
			levelOfCurrentDeeltaak = level;
		deeltaaklevels[level] = levelOfCurrentDeeltaak;

	}
	
	/**
	 * remove variables with level the current level, then decrease the current level
	 */
	void decreaseLevel()
	{
		Iterator<TAVariable> it = variabelen.iterator();
		while ( it.hasNext() )
		{
			TAVariable tav = it.next();
			if ( tav.getLevel()==level )
			{	it.remove();
			}
		}
		level--;
		levelOfCurrentDeeltaak = deeltaaklevels[level];
	}
	
	/**
	 * Checks if a var is visible, that is if its level is greater than levelCDT. If we want to get rid of
	 * global variables, we can remove the condition "or level==0".
	 * Note: Used 3 times: in findVariable, getExpressionValue and getSubstEquation
	 * @param tav	The variable
	 * @return		true if visible
	 */
	private boolean isVarVisible(TAVariable tav)
	{
		return ( tav.getLevel() >= levelOfCurrentDeeltaak ||  tav.getLevel()==0 );
	}

	/**
	 * find a variable with name s; Search order 'last to first', because we want to find a parameter before we find a global variable
	 * of the same name. Only return variables in scope (using method isVarVisible), that is level larger then or equal levelCDT 
	 * (local variable) or level==0 (global)
 	 * @param s name to search for
	 * @return a TAVariable or null
	 */
	private TAVariable findVariable(String s)
	{
		TAVariable tav;
		for ( int i=variabelen.size()-1; i>=0; i-- )
		{
			tav = variabelen.get(i);
			if (s.equals(tav.getName()) ) 
			{
				if ( isVarVisible(tav) )
				{
					return variabelen.get(i);
				} 
			}
		}
		return null;
	}
	
	/**
	 * add or replace a variable whose value is obtained by 
	 * evaluating Expression e
	 * @param varName name of the variable
	 * @param e Expression for evaluating the value of the variable  
	 */
	public void setVar(String varName, Expressie e)
	{	
		double value = getExpressionValue(e);
		if( !Double.isNaN(value) )
		{	
			TAVariable tav = findVariable(varName);
			if ( tav == null )
			{
				variabelen.add(new TAVariable(varName, value, level));
			} 
			else
			{
				tav.setValue(value);
			}
		}
	}
	
	/**
	 * Add a variable to the varset that is a parameter. Parameters are 'call by value',
	 * so a value is added, not an expression!
	 * @param varName name of the variable
	 * @param value value of the variable
	 */
	public void setParameter(String varName, double value)
	{
		variabelen.add(new TAVariable(varName, value, level));
	}
	
	/**
	 * substitute the value and name of all visible variables in 
	 * Expressie e and determine the value of this Expression
	 * @param e Expressie e
	 * @return value of Expressie e after substitution
	 */
	public double getExpressionValue(Expressie e)
	{	
		TAVariable tav;
		for ( int i=variabelen.size()-1; i>=0; i-- )
		{	
			tav = variabelen.get(i);
			if ( isVarVisible(tav) )
			{	e = e.substitueer(tav.getValue(), tav.getName());
			}
		}
		return e.geefWaarde();
	}
	
	/**
	 * substitute the value (as a BasisExpressie) and the name of all visible
	 * variables in the equations given by VergelijkingMeerv v  
	 * @param v the equations
	 * @return the substituted equations
	 */
	public VergelijkingMeerv getSubstEquation(VergelijkingMeerv v)
	{	
		TAVariable tav;
		for ( int i=variabelen.size()-1; i>=0; i-- )
		{	
			tav = variabelen.get(i);
			if ( isVarVisible(tav) )
			{
				Expressie e = new BasisExpressie(tav.getValue());
				v = v.substitueer(e, tav.getName());
				
			}
		}
		return v;
	}

	/**
	 * produce a String containing an ordered list of the current variables in the program 
	 */
	public String toString()
	{	// we want to print the description as soon as the level in a variable is up.
		int runninglevel = 0;					
		String indent = "  ";
		String s = levelDescriptions[0]+"\n";
		for ( TAVariable tav: variabelen )
		{
			while ( runninglevel < tav.getLevel() )
			{
				runninglevel++;
				s = s + indent + levelDescriptions[runninglevel]+"\n";
				indent = indent+"  ";
			}
			s = s + indent + " " + tav.getName()+": "+tav.getValue()+"\n";
		}
		// also print descriptions of remaining blocks that don't have variables
		while ( runninglevel < level )			
		{
			runninglevel++;
			s = s + indent + levelDescriptions[runninglevel]+"\n";
			indent = indent+"  ";
		}
		return s;
	}
}
