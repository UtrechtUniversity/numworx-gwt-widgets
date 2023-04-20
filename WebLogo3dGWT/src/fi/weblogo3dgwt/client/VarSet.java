package fi.weblogo3dgwt.client;

import java.util.*;

import fi.weblogo3dgwt.client.expressies.*;

/**
 * see also class VarSet in WebLogoGWT
 */
public class VarSet 
{
	/**
	 * 'Level' in the stack of calls. Every call of a Deeltaak will increase the level, returning
	 * from a Deeltaak will decrease the level. In this way we will be able to remove local variables
	 * when their block finishes and print vars in a stack-trace manner.
	 */
	private int level;
	/**
	 * level of current deeltaak. This decides if a var is visible: the level of the var must be
	 * greater than or equal to this value for the var to be in scope. We may include level 0,
	 * this will make the vars in tekenalgoritme global.
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
	 * List of all variables. New vars will be added to the end, so the list will be ordered
	 * with repect to the level that we're in.
	 */
	private ArrayList<TAVariable> variabelen;
	/**
	 * Descriptions associated with each level, such as 'In Deeeltaak vierkant'.
	 * Using an array as a stack :-)
	 */
	private String[] levelDescriptions;
	
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
	
	public int getLevel()
	{
		return level;
	}

	void increaseLevel(String description, boolean isDeeltaak)
	{
		level++;
		levelDescriptions[level] = description;
		if ( isDeeltaak )
			levelOfCurrentDeeltaak = level;
		deeltaaklevels[level] = levelOfCurrentDeeltaak;

	}
	
	void decreaseLevel()
	{
		Iterator<TAVariable> it = variabelen.iterator();
		while ( it.hasNext() )
		{
			TAVariable tav = it.next();
			if ( tav.getLevel()==level )
			{
				it.remove();
			}
		}
		level--;
		levelOfCurrentDeeltaak = deeltaaklevels[level];
	}
	
	/**
	 * Checks if a var is visible, that is level larger than levelCDT. If we want to get rid of
	 * global vars, we can remove the condition "or level==0".
	 * Note: Used 3 times: find and substitute
	 * @param tav	The var
	 * @return		true if visible
	 */
	private boolean isVarVisible(TAVariable tav)
	{
		return ( tav.getLevel()>= levelOfCurrentDeeltaak ||  tav.getLevel()==0 );
	}

	private TAVariable findVariable(String s)
	{
		// Search order 'last to first', because we want to find a parameter before we find a global variable
		// of the same name.
		// Only return variables in scope, that is level>=levelCDT (local var) or level==0 (global)
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
	
	public void setVar(String varName, Expressie e)
	{	
		double value = getExpressionValue(e);
		if( !Double.isNaN(value) )
		{	
			TAVariable tav = findVariable(varName);
			if ( tav == null )
			{
				variabelen.add(new TAVariable(varName, value, level));
			} else
			{
				tav.setValue(value);
			}
		}
	}
	
	/**
	 * Add a variable to the varset that is a parameter. Parameters are 'call by value',
	 * so a value is added, not an expression!
	 * @param varName variable name
	 * @param value variable value
	 */
	public void setParameter(String varName, double value)
	{
		variabelen.add(new TAVariable(varName, value, level));
	}
	
	public double getExpressionValue(Expressie e)
	{	
		TAVariable tav;
		for ( int i=variabelen.size()-1; i>=0; i-- )
		{	
			tav = variabelen.get(i);
			if ( isVarVisible(tav) )
			{
				e = e.substitueer(tav.getValue(), tav.getName());
			}
		}

		return e.geefWaarde();
	}
	
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

	public String toString()
	{
		int runninglevel = 0;					// we want to print the description as soon as the leve in a variable is up.
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
		while ( runninglevel < level )			// also print descriptions of remaining blocks that don't have variables
		{
			runninglevel++;
			s = s + indent + levelDescriptions[runninglevel]+"\n";
			indent = indent+"  ";
		}
		return s;
	}

}
