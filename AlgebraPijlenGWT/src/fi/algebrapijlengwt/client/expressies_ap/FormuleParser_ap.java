package fi.algebrapijlengwt.client.expressies_ap;

public class FormuleParser_ap
{		
	public FormuleParser_ap()
	{	
	}
		
	public static String formuleString(String s)
	{	s = "(" + s.substring(2,s.length()-1) + ")";
		
		
		int n = s.indexOf("$n");
		while(n>-1)
		{	int index$ = n-1;
			int niv = 0;
			while(!(s.charAt(index$)=='$' && niv==0))
			{	if(s.charAt(index$)=='$')niv--;
				else if(s.charAt(index$)=='@')niv++;
				index$--;
			}
			int indexAt = n+2;
			niv = 0;
			while(!(s.charAt(indexAt)=='@' && niv==0))
			{	if(s.charAt(indexAt)=='$')niv++;
				else if(s.charAt(indexAt)=='@')niv--;
				indexAt++;
			}
			if(s.charAt(index$+1)=='b')s = s.substring(0,n) + ")/(" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
			else if(s.charAt(index$+1)=='o')s = s.substring(0,n) + ")+(" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
			else if(s.charAt(index$+1)=='a')s = s.substring(0,n) + ")-(" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
			else if(s.charAt(index$+1)=='v')s = s.substring(0,n) + ")*(" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
			else if(s.charAt(index$+1)=='p')s = s.substring(0,n) + ")^(" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
			n = s.indexOf("$n");
		}
		
		n = s.indexOf("$w");
		while(n>-1)
		{	s = s.substring(0,n) + "sqrt(" + s.substring(n+2);
			n = s.indexOf("$w");
		}
		n = s.indexOf("$m");
		while(n>-1)
		{	s = s.substring(0,n) + "^(" + s.substring(n+2);
			n = s.indexOf("$m");
		}
		n = s.indexOf("$h");
		while(n>-1)
		{	s = s.substring(0,n) + "(" + s.substring(n+2);
			n = s.indexOf("$h");
		}
		n = s.indexOf("$b");
		while(n>-1)
		{	s = s.substring(0,n) + "((" + s.substring(n+2);
			n = s.indexOf("$b");
		}
		n = s.indexOf("$o");
		while(n>-1)
		{	s = s.substring(0,n) + "((" + s.substring(n+2);
			n = s.indexOf("$o");
		}
		n = s.indexOf("$a");
		while(n>-1)
		{	s = s.substring(0,n) + "((" + s.substring(n+2);
			n = s.indexOf("$a");
		}
		n = s.indexOf("$v");
		while(n>-1)
		{	s = s.substring(0,n) + "((" + s.substring(n+2);
			n = s.indexOf("$v");
		}
		n = s.indexOf("$p");
		while(n>-1)
		{	s = s.substring(0,n) + "((" + s.substring(n+2);
			n = s.indexOf("$p");
		}
		n = s.indexOf("@");
		while(n>-1)
		{	s = s.substring(0,n) + ")" + s.substring(n+1);
			n = s.indexOf("@");
		}
		n = s.indexOf("·");
		while(n>-1)
		{	s = s.substring(0,n) + "*" + s.substring(n+1);
			n = s.indexOf("·");
		}
		return s;	
	}
	
	public static String schoon(String s)
	{	int index = 0;
		s = s.replace(',','.');
		while(index >-1)
		{	index = s.indexOf(" ");
			if(index >-1)s = s.substring(0,index) + s.substring(index+1);
		}
		//een breuk constructie als "2((1)/(2))" wordt vervangen door 2+1/2
		int start = 0;
		index = s.indexOf(")/(",start);
		while(index >-1)
		{	
			start = index+3;
			int telmin = index-1;
			while(telmin>0 && Character.isDigit(s.charAt(telmin)))
			{	telmin--;
			}
			int telmax = index+3;
			while(telmax<s.length() && Character.isDigit(s.charAt(telmax)))
			{	telmax++;
			}
			if(s.charAt(telmin)=='(' 
			   && s.charAt(telmin-1)=='('
			   && Character.isDigit(s.charAt(telmin-2))
			   && s.charAt(telmax)==')'
			   && s.charAt(telmax+1)==')')
			{	int telminmin = telmin-3;
				while(telminmin>0 && Character.isDigit(s.charAt(telminmin)))
				{	telminmin--;
				}
				s = s.substring(0,telminmin+1) + "(" + s.substring(telminmin+1,telmin-1) + "+" +  s.substring(telmin-1,telmax+2) + ")" + s.substring(telmax+2);
			}
			index = s.indexOf(")/(",start);
		}
		for(int i=0 ; i<s.length()-1 ; i++)
		{	if((Letter.isLetter(s.charAt(i)) || Character.isDigit(s.charAt(i)) || s.charAt(i)==')') && (Letter.isLetter(s.charAt(i+1)) || s.charAt(i+1)=='('))
			{	s = s.substring(0,i+1) + '*' +  s.substring(i+1);
			}
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("s*q*r*t*");
			if(index >-1)s = s.substring(0,index) + "sqrt" + s.substring(index+8);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("s*i*n*");
			if(index >-1)s = s.substring(0,index) + "sin" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("c*o*s*");
			if(index >-1)s = s.substring(0,index) + "cos" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("t*a*n*");
			if(index >-1)s = s.substring(0,index) + "tan" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("(-");
			if(index >-1)s = s.substring(0,index) + "(0-" + s.substring(index+2);
		}
		
		return s;	
	}
	
	public static Expressie parse(String s)
	{	Expressie exp = null;
		if(s.equals("...") || s.equals(""))return null;
		//verwijder overbodige haakjes
		try
		{
		boolean pelbaar = true;
		while(pelbaar)
		{	if(s.charAt(0)=='(' && s.charAt(s.length()-1)==')')
			{	pelbaar = true;
			}
			else pelbaar = false;
			if(pelbaar)
			{	int niv = 0;
				int minNiv = 0;
				for(int i=0 ; i<s.length()-1 ; i++)
				{	if(s.charAt(i)=='(')
					{	niv++;
						
					}
					else if(s.charAt(i)==')')
					{	niv--;
						if(niv<1)
						{	pelbaar = false;
							break;
						}
					}
				}
			}
			if(pelbaar)s = s.substring(1,s.length()-1);
		}
		
		//is het een letter?		
		if(s.length()==1 && Letter.isLetter(s.charAt(0)))
		{	exp = new BasisExpressie(s);
			return exp;
		}
		
		//is het een getal?
		boolean isGetal = true;
		try
		{	Double d = Double.valueOf(s);
		}
		catch(NumberFormatException nfe)
		{	isGetal = false;
		}
		if(isGetal)
		{	exp = new BasisExpressie(s);
			return exp;
		}
		
		//is het een optelling, aftrekking, enz?
		char[] operatoren = {'+','-','*','/','^'};
		for(int j=0 ; j<5 ; j++)
		{	int niv = 0;
			for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(s.charAt(i)==operatoren[j] && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(j==0)return new Optelling(e1,e2);
					else if(j==1)return new Aftrekking(e1,e2);
					else if(j==2)return new Vermenigvuldiging(e1,e2);
					else if(j==3)return new Deling(e1,e2);
					else if(j==4)return new Macht(e1,e2);
					return exp;
				}
			}
		}
		
		//is het een wortel
		if(s.substring(0,4).equals("sqrt"))
		{	Expressie e = parse(s.substring(4,s.length()));
			return new Wortel(e);
		}
		}
		catch(Exception e)
		{}
		return exp;

	}
	
	public static Expressie geefExpressie(String codeString)
	{	return parse(schoon(formuleString(codeString)));
	}
	
	
}

