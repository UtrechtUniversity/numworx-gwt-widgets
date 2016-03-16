package fi.nabouwenaanzichtengwt.client;

import java.util.*;

import fi.nabouwenaanzichtengwt.client.expressies.*;

public class Variable
{
	String name;
	Vector borders;
	HashMap borderValues;
	Vector values;
	
	public Variable(String name)
	{	this.name = name;
		borders = new Vector();
		borderValues = new HashMap();
	}	
	
	public void setValues(String s)
	{	s = s.trim();
		//StringTokenizer tokenizer = new StringTokenizer(s,",");
		String[] valueStrings = s.split(",");
		for (int tokCnt = 0; tokCnt < valueStrings.length; tokCnt++)
		//while(tokenizer.hasMoreTokens())
		{	//String tok = tokenizer.nextToken();
			String tok = valueStrings[tokCnt];
			tok = tok.trim();
			int index = tok.indexOf("..");
			if(index>0)
			{	String leftExprString = tok.substring(0,index);
				leftExprString = leftExprString.trim();
				Expressie leftExpr = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + leftExprString + "@")));
				borders.addElement(leftExpr);
				
				Vector namenLeft = leftExpr.geefVarNamen();
				for(int i=0 ; i<namenLeft.size(); i++)
			    {	borderValues.put(namenLeft.elementAt(i),"leeg");
			    }
			    
				String rightExprString = tok.substring(index+2);
				rightExprString = rightExprString.trim();
				Expressie rightExpr = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + rightExprString + "@")));
				borders.addElement(rightExpr);
				
				
				Vector namenRight = rightExpr.geefVarNamen();
		    	for(int i=0 ; i<namenRight.size(); i++)
			    {	borderValues.put(namenRight.elementAt(i),"leeg");
			    }
			}
			else if(index==-1)
			{	Expressie expr = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + tok + "@")));
				borders.addElement(expr);
				borders.addElement(expr);
				
				Vector namen = expr.geefVarNamen();
		    	for(int i=0 ; i<namen.size(); i++)
			    {	borderValues.put(namen.elementAt(i),"leeg");
			    }
			}	
		}
	}
	
	public boolean isUsedVar(String s)
	{	return borderValues.containsKey(s);
	}
	
	public void substitueer(int value, String varnaam)
	{	if(borderValues.containsKey(varnaam))borderValues.put(varnaam,new Integer(value));
	}
	
	public Vector substitueerBorders()
	{	Vector v = new Vector();
		for(int i=0 ; i<borders.size(); i++)
		{	v.addElement(((Expressie)borders.elementAt(i)).substitueer(0,"geen"));
		}
//		for (Enumeration e = borderValues.keys() ; e.hasMoreElements() ;) 
//		{	String key = (String)e.nextElement();
		for (Iterator<String> e = borderValues.keySet().iterator() ; e.hasNext() ;)
		{	String key = (String)e.next();
			int value = ((Integer)borderValues.get(key)).intValue();
			for(int i=0 ; i<v.size(); i++)
		    {	Expressie b = (Expressie)v.elementAt(i);
		    	b = b.substitueer(value,key);
		    	v.setElementAt(b,i);
		    	//System.out.println(key+"="+b.geefWaarde());
		    }
     	}
     	return v;

	}
	
	public void makeValues()
	{	values = new Vector();
		Vector expSub = substitueerBorders();
		for(int i=0 ; i<expSub.size(); i+=2)
		{	int leftBorder = (int)((Expressie)expSub.elementAt(i)).geefWaarde();
			//System.out.println("l: "+((Expressie)expSub.elementAt(i)).geefWaarde());
			int rightBorder = (int)((Expressie)expSub.elementAt(i+1)).geefWaarde();
			//System.out.println("r: "+rightBorder);
			for(int j=leftBorder ; j<=rightBorder; j++)
		    {	values.addElement(new Integer(j));
		    	//System.out.println("getallen: "+j);
		    }
		}
	}
	
	public int[] getValues()
	{	makeValues();
		int[] intValues = new int[values.size()];
		for(int i=0 ; i<values.size(); i++)
	    {	intValues[i] = ((Integer)values.elementAt(i)).intValue();
	    	//System.out.println("values: "+intValues[i]);
	    }
	    return intValues;
	}
	
	public String getName()
	{	return name;
	}
}
