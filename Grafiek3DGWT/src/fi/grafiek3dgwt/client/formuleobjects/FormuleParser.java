package fi.grafiek3dgwt.client.formuleobjects;

import java.util.HashMap;

import fi.grafiek3dgwt.client.Grafiek3DGWT;
import fi.grafiek3dgwt.client.expressies.*;

public class FormuleParser
{		
	private static boolean woordFormule = false;
	private static boolean tweeHoofdletterVariabele = false;
	
	public FormuleParser()
	{	
	}
	
	public static void zetWoordFormule(boolean b)
	{
		woordFormule = b;
	}
	
	public static void zetTweeHoofdletterVariabele(boolean b)
    {
	    tweeHoofdletterVariabele = b;
    }
	
	public static boolean isWoordFormule()
	{
		return woordFormule;
	}
	
	public static boolean isTweeHoofdletterVariabele()
    {
        return tweeHoofdletterVariabele;
    }
		
	public static VergelijkingMeerv parseVergelijking(String s)
	{	try
		{
			s = s.substring(2,s.length()-1);
			if(s.length()==0)return null;
			String[] vergelijkingStrings = s.split("of");
			Vergelijking[] vergelijkingen = new Vergelijking[vergelijkingStrings.length];
			
			for(int j=0 ; j<vergelijkingStrings.length; j++) 
			{	int index1 = vergelijkingStrings[j].indexOf("[");
		    	int index2 = vergelijkingStrings[j].indexOf("]");
				if(index1>-1 && index2>index1)
				{	
					String[] expressieStrings = vergelijkingStrings[j].substring(index1 + 1, index2).split(":");
					if(expressieStrings.length==3)
					{	Expressie e1 = parse(schoon(formuleString("$f" + expressieStrings[0] + "@")));
			    		Expressie e2 = parse(schoon(formuleString("$f" + expressieStrings[1] + "@")));
			    		Expressie e3 = parse(schoon(formuleString("$f" + expressieStrings[2] + "@")));
			    		if(e1!=null && e2!=null && e3!=null && e1.isWaarde() && e2.isWaarde() && e3.isWaarde())
				    	{	vergelijkingen[j] = new Vergelijking(new BasisExpressie("Q"),new Vermenigvuldiging(e1,new Optelling(e2,e3)), "=");
				    		//System.out.println(vergelijkingen[j].toString());
				    	}
					}
					
				}
			}
			
			String[][] tekenParen = {{"<","<"},{"<","\u2264"},{"\u2264","<"},{"\u2264","\u2264"},{">",">"},{"\u2265",">"},{">","\u2265"},{"\u2265","\u2265"}};
			Vergelijking ongDubbel = null;
			boolean[] splitOngDubbel = new boolean[vergelijkingStrings.length];
	    	for(int j=0 ; j<vergelijkingStrings.length ; j++) 
			{	for(int i = 0; i < 8 && vergelijkingen[j]==null; i++)
			    { 	int index1 = vergelijkingStrings[j].indexOf(tekenParen[i][0]);
			    	int index2 = vergelijkingStrings[j].indexOf(tekenParen[i][1],index1+1);
					if(index1>0 && index2>0)
					{	String s1 = vergelijkingStrings[j].substring(0,index1);
						String s2 = vergelijkingStrings[j].substring(index1+1,index2);
						String s3 = vergelijkingStrings[j].substring(index2+1);
						Expressie e1 = parse(schoon(formuleString("$f" + s1 + "@")));
			    		Expressie e2 = parse(schoon(formuleString("$f" + s2 + "@")));
			    		Expressie e3 = parse(schoon(formuleString("$f" + s3 + "@")));
				    	if(e1!=null && e2!=null && e3!=null && e1.isWaarde() && e2.isVar() && e3.isWaarde())
				    	{	vergelijkingen[j] = new Vergelijking(e2,new Vermenigvuldiging(new BasisExpressie(i),new Optelling(e1,e3)), "~");
				    		//System.out.println(vergelijkingen[j].toString());
				    	}
					}
			    }
			}
			
			String[] vergTekens = {"=", ">", "<", "\u2264", "\u2265","\u2248"};
			
			for(int i=0 ; i<vergelijkingStrings.length; i++)
		    {	boolean split = false;
			    for(int j=0 ; j<vergTekens.length && !split  && vergelijkingen[i]==null; j++)
			    {	String[] expressieStrings  = vergelijkingStrings[i].split(vergTekens[j]);
			    	if(expressieStrings.length==2)
			    	{	
				    	if(expressieStrings[1].trim().equals("geen") || expressieStrings[1].trim().equals("none")) expressieStrings[1] = "0.1234567";
			    		Expressie e1 = null;
			    		Expressie e2 = null;
			    		String[] eindoplStrings = expressieStrings[1].split("::"); 
			    		
			    		int aantalEO = eindoplStrings.length;
			    		if(aantalEO>1)
			    		{ 	Expressie[] eindoplossingen = new Expressie[aantalEO];
			    			for(int k=0 ; k<aantalEO ; k++)
						    {	eindoplossingen[k] = parse(schoon(formuleString("$f" + eindoplStrings[k] + "@")));
						    	if(eindoplossingen[k]==null)
						    	{	eindoplossingen=null;
						    		break;
						    	}
						    }
			    			e1 = parse(schoon(formuleString("$f" + expressieStrings[0] + "@")));
			    			
			    			if(e1==null || eindoplossingen==null) 
					    	{	split = false;
					    	}
					    	else 
					    	{	split = true;
					    		vergelijkingen[i] = new Vergelijking(e1,eindoplossingen, vergTekens[j]);
					    	}
			    		}
			    		else
			    		{
				    		e1 = parse(schoon(formuleString("$f" + expressieStrings[0] + "@")));
					    	e2 = parse(schoon(formuleString("$f" + expressieStrings[1] + "@")));
					    	//System.out.println(e2.toString());
			    		
					    	if(e1==null || e2==null) 
					    	{	split = false;
					    	}
					    	else 
					    	{	split = true;
					    		vergelijkingen[i] = new Vergelijking(e1,e2, vergTekens[j]);
					    	}
			    		}
			    	}
			    }
			    if(!split && vergelijkingen[i]==null)return null;
			}
			return new VergelijkingMeerv(vergelijkingen); 
			
		}
		catch(Exception e)
		{
			return null;
		}
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
			int k = s.indexOf("$k");
			int indexAtk = k+2;
			if(k>n && k<indexAt)
			{	 ;
				niv = 0;
				while(!(s.charAt(indexAtk)=='@' && niv==0))
				{	if(s.charAt(indexAtk)=='$')niv++;
					else if(s.charAt(indexAtk)=='@')niv--;
					indexAtk++;
				}
			}
			
			int l = s.indexOf("$l");
			int indexAtl = l+2;
			if(l>k && l<indexAtk)
			{	indexAtl = l+2;
				niv = 0;
				while(!(s.charAt(indexAtl)=='@' && niv==0))
				{	if(s.charAt(indexAtl)=='$')niv++;
					else if(s.charAt(indexAtl)=='@')niv--;
					indexAtl++;
				}
			}
			
			if(s.charAt(index$+1)=='b')s = s.substring(0,n) + ")/(" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
			else if(s.charAt(index$+1)=='o')s = s.substring(0,n) + ")+(" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
			else if(s.charAt(index$+1)=='a')s = s.substring(0,n) + ")-(" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
			else if(s.charAt(index$+1)=='v')s = s.substring(0,n) + ")*(" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
			else if(s.charAt(index$+1)=='p')s = s.substring(0,n) + ")^(" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
			else if(s.charAt(index$+1)=='W')s = s.substring(0,n) + ")|(" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
			else if(s.charAt(index$+1)=='L')s = s.substring(0,n) + ")~(" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
            else if(s.charAt(index$+1)=='i')s = s.substring(0,n) + "_" + s.substring(n+2,k) + "_" + s.substring(k+2,l) + "_" + s.substring(l+2,indexAtl) + ")" + s.substring(indexAt+1);
            else if(s.charAt(index$+1)=='q')s = s.substring(0,n) + "_" + s.substring(n+2,k) + "_" + s.substring(k+2,l) + "_" + s.substring(l+2,indexAtl) + ")" + s.substring(indexAt+1);
            else if(s.charAt(index$+1)=='y')s = s.substring(0,n) + "_" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
            else if(s.charAt(index$+1)=='d')s = s.substring(0,n) + "_" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
            else if(s.charAt(index$+1)=='D')s = s.substring(0,n) + "_" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
            else if(s.charAt(index$+1)=='P')s = s.substring(0,n) + "_" + s.substring(n+2,indexAt) + ")" + s.substring(indexAt+1);
            else if(s.charAt(index$+1)=='T')s = s.substring(0,n) + "_" + s.substring(n+2,k) +  "_" + s.substring(k+2,l) + "_" + s.substring(l+2,indexAtl) + ")" + s.substring(indexAt+1);
            else if(s.charAt(index$+1)=='S')s = s.substring(0,n) + "_" + s.substring(n+2,k) +  "_" + s.substring(k+2,l) + "_" + s.substring(l+2,indexAtl) + ")" + s.substring(indexAt+1);
            
			n = s.indexOf("$n");
            
		}
		
		n = s.indexOf("$w");
		while(n>-1)
		{	s = s.substring(0,n) + "sqrt(" + s.substring(n+2);
			n = s.indexOf("$w");
		}
		n = s.indexOf("$r");
		while(n>-1)
		{	s = s.substring(0,n) + "abs(" + s.substring(n+2);
			n = s.indexOf("$r");
		}
		n = s.indexOf("$c");
        while(n>-1)
        {   s = s.substring(0,n) + "conjug(" + s.substring(n+2);
            n = s.indexOf("$c");
        }
		n = s.indexOf("$y");
		while(n>-1)
		{	s = s.substring(0,n) + "(bin(" + s.substring(n+2);
			n = s.indexOf("$y");
			
		}
		n = s.indexOf("$d");
		while(n>-1)
		{	s = s.substring(0,n) + "(dif(" + s.substring(n+2);
			n = s.indexOf("$d");
			
		}
		n = s.indexOf("$D");
		while(n>-1)
		{	s = s.substring(0,n) + "(difpar(" + s.substring(n+2);
			n = s.indexOf("$D");
			
		}
		n = s.indexOf("$P");
		while(n>-1)
		{	s = s.substring(0,n) + "(prm(" + s.substring(n+2);
			n = s.indexOf("$P");
			
		}
		n = s.indexOf("$T");
		while(n>-1)
		{	s = s.substring(0,n) + "(lim(" + s.substring(n+2);
			n = s.indexOf("$T");
			
		}
		n = s.indexOf("$S");
        while(n>-1)
        {   s = s.substring(0,n) + "(sig(" + s.substring(n+2);
            n = s.indexOf("$S");
            
        }
        n = s.indexOf("$s");
        while(n>-1)
        {   s = s.substring(0,n) + "?(" + s.substring(n+2);
            n = s.indexOf("$s");
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
		n = s.indexOf("$W");
		while(n>-1)
		{	s = s.substring(0,n) + "((" + s.substring(n+2);
			n = s.indexOf("$W");
		}
		n = s.indexOf("$L");
		while(n>-1)
		{	s = s.substring(0,n) + "((" + s.substring(n+2);
			n = s.indexOf("$L");
		}
		n = s.indexOf("$i");
		while(n>-1)
		{	s = s.substring(0,n) + "(int(" + s.substring(n+2);
			n = s.indexOf("$i");
		}
		n = s.indexOf("$q");
        while(n>-1)
        {   s = s.substring(0,n) + "(prv(" + s.substring(n+2);
            n = s.indexOf("$q");
        }
		n = s.indexOf("@");
		while(n>-1)
		{	s = s.substring(0,n) + ")" + s.substring(n+1);
			n = s.indexOf("@");
		}
		n = s.indexOf("\u00B7");
		while(n>-1)
		{	s = s.substring(0,n) + "*" + s.substring(n+1);
			n = s.indexOf("\u00B7");
		}
		n = s.indexOf("\u00d7");
		while(n>-1)
		{	s = s.substring(0,n) + "*" + s.substring(n+1);
			n = s.indexOf("\u00d7");
		}
		return s;	
	}
	
	public static String vervangFunctieScheidingstekens(String s, String functie)
	{	String s1 = s;
		String s2 = "";
		int ind = s.indexOf(functie + "(");
		int len = functie.length()+1;
		
		while(ind>-1) 
		{	int start = ind + len;
			s1 = s.substring(0, ind+len);
			s2 = s.substring(ind+len);
			int niv = 0;
			for(int i=0 ; i<s2.length() ; i++)
			{	if(s2.charAt(i)==')') niv++;
				else if(s2.charAt(i)=='(') niv--;
				else if(s2.charAt(i)==',' && niv==0) s2 = s2.substring(0,i)+"_"+s2.substring(i+1);
				if(niv>0)break;
			}
			s = s1 + s2;
			ind = s.indexOf(functie + "(", start );
		}
		return s;
	}
	
	public static String schoon(String s)
	{	return schoon(s,false);
	}
	
	public static String schoon(String s, boolean woordformule)
	{	
		s = vervangFunctieScheidingstekens(s,"normalcdf");
		s = vervangFunctieScheidingstekens(s,"invNorm");
		s = vervangFunctieScheidingstekens(s,"invnorm");
		s = vervangFunctieScheidingstekens(s,"binomcdf");
		s = vervangFunctieScheidingstekens(s,"binompdf");
		s = vervangFunctieScheidingstekens(s,"gcd");
	
		s = s.replace(',','.');
		s = s.replace(':','/');
		
		int index = 0;
		while(index >-1)
		{	index = s.indexOf(" ");
			if(index >-1)s = s.substring(0,index) + s.substring(index+1);
		}
		
		//vervangt -- door +
		index = 0;
		while(index >-1)
		{	index = s.indexOf("--");
			if(index >-1)s = s.substring(0,index) + "+" + s.substring(index+2);
		}
		
		//vervangt *-6 door *(-6)
		index = 0;
		while(index >-1)
		{	index = s.indexOf("*-");
			
			int tel = index+2;
			while(tel<s.length() && (Character.isDigit(s.charAt(tel)) || s.charAt(tel)=='.' ))
			{	tel++;
			}
			if(index >-1 && tel>index+2) s = s.substring(0,index) + "(" + s.substring(index+1,tel) + ")" + s.substring(tel);
			
			else if(index >-1 && index+2<s.length() && Letter.isLetter(s.charAt(index+2))) 
			{	tel = index+3;
				s = s.substring(0,index) + "(-1)" + s.substring(index+2);
			}	
			else if(index >-1 && index+2<s.length())s = s.substring(0,index) + "(-1)" + s.substring(index+2);
		}
		
		//vervangt /-6 door /(-6)
		index = 0;
		while(index >-1)
		{	index = s.indexOf("/-");
			
			int tel = index+2;
			while(tel<s.length() && Character.isDigit(s.charAt(tel)))
			{	tel++;
			}
			
			if(index >-1 && tel>index+2)s = s.substring(0,index) + "/(" + s.substring(index+1,tel) + ")" + s.substring(tel);
			
			else if(index >-1 && index+2<s.length() && Letter.isLetter(s.charAt(index+2))) 
			{	tel = index+3;
				s = s.substring(0,index) + "(-1)/" + s.substring(index+2);
			}	
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
		if(!(woordformule || FormuleParser.woordFormule)&& tweeHoofdletterVariabele)
        {   for(int i=0 ; i<s.length()-1 ; i++)
            {  	char c0 = s.charAt(i);
        		char c1 = s.charAt(i+1);
        		boolean isUpperCasePair = Character.isUpperCase(c0) && Character.isUpperCase(c1);
        		if(!isUpperCasePair && ((Letter.isLetter(c0) || Character.isDigit(c0) || c0==')') && (Letter.isLetter(c1) || c1=='(')))
                {   s = s.substring(0,i+1) + '*' +  s.substring(i+1);
                }
                else if(c0==')' && Character.isDigit(c1))
                {   s = s.substring(0,i+1) + '*' +  s.substring(i+1);
                }
            }
        }
		else if(!(woordformule || FormuleParser.woordFormule))
		{	for(int i=0 ; i<s.length()-1 ; i++)
			{	if((Letter.isLetter(s.charAt(i)) || Character.isDigit(s.charAt(i)) || s.charAt(i)==')') && (Letter.isLetter(s.charAt(i+1)) || s.charAt(i+1)=='('))
				{	s = s.substring(0,i+1) + '*' +  s.substring(i+1);
				}
				else if(s.charAt(i)==')' && Character.isDigit(s.charAt(i+1)))
				{	s = s.substring(0,i+1) + '*' +  s.substring(i+1);
				}
			}
		}
		
		index = 0;
		while(index >-1)
		{	index = s.indexOf("s*q*r*t*");
			if(index >-1)s = s.substring(0,index) + "sqrt" + s.substring(index+8);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("r*n*d*");
			if(index >-1)s = s.substring(0,index) + "rnd" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("r*n*s*");
			if(index >-1)s = s.substring(0,index) + "rns" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("r*n*q*");
			if(index >-1)s = s.substring(0,index) + "rnq" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("a*b*s*");
			if(index >-1)s = s.substring(0,index) + "abs" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
        {   index = s.indexOf("c*o*n*j*u*g*");
            if(index >-1)s = s.substring(0,index) + "conjug" + s.substring(index+12);
        }
        index = 0;
		while(index >-1)
		{	index = s.indexOf("b*i*n*o*m*c*d*f*");
			if(index >-1)s = s.substring(0,index) + "binomcdf" + s.substring(index+16);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("b*i*n*o*m*p*d*f*");
			if(index >-1)s = s.substring(0,index) + "binompdf" + s.substring(index+16);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("b*i*n*");
			if(index >-1)s = s.substring(0,index) + "bin" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("d*i*f*p*a*r*");
			if(index >-1)s = s.substring(0,index) + "difpar" + s.substring(index+12);
		}
        
		index = 0;
		while(index >-1)
		{	index = s.indexOf("d*i*f*");
			if(index >-1)s = s.substring(0,index) + "dif" + s.substring(index+6);
		}
        index = 0;
        while(index >-1)
		{	index = s.indexOf("p*r*m*");
			if(index >-1)s = s.substring(0,index) + "prm" + s.substring(index+6);
		}
        index = 0;
        while(index >-1)
        {   index = s.indexOf("s*i*g*");
            if(index >-1)s = s.substring(0,index) + "sig" + s.substring(index+6);
        }
        index = 0;
        while(index >-1)
		{	index = s.indexOf("l*i*m*");
			if(index >-1)s = s.substring(0,index) + "lim" + s.substring(index+6);
		}
        index = 0;
		while(index >-1)
		{	index = s.indexOf("i*n*t*");
			if(index >-1)s = s.substring(0,index) + "int" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("g*c*d*");
			if(index >-1)s = s.substring(0,index) + "gcd" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("m*i*n*");
			if(index >-1)s = s.substring(0,index) + "min" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("m*a*x*");
			if(index >-1)s = s.substring(0,index) + "max" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("n*o*r*m*a*l*c*d*f*");
			if(index >-1)s = s.substring(0,index) + "normalcdf" + s.substring(index+18);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("i*n*v*n*o*r*m*");
			if(index >-1)s = s.substring(0,index) + "invNorm" + s.substring(index+14);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("i*n*v*N*o*r*m*");
			if(index >-1)s = s.substring(0,index) + "invNorm" + s.substring(index+14);
		}
		index = 0;
        while(index >-1)
        {   index = s.indexOf("p*r*v*");
            if(index >-1)s = s.substring(0,index) + "prv" + s.substring(index+6);
        }
		index = 0;
		while(index >-1)
		{	index = s.indexOf("a*r*c*s*i*n*");
			if(index >-1)s = s.substring(0,index) + "arcsin" + s.substring(index+12);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("a*r*c*s*i*n");
			if(index >-1)s = s.substring(0,index) + "arcsin" + s.substring(index+11);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("a*r*c*c*o*s*");
			if(index >-1)s = s.substring(0,index) + "arccos" + s.substring(index+12);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("a*r*c*c*o*s");
			if(index >-1)s = s.substring(0,index) + "arccos" + s.substring(index+11);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("a*r*c*t*a*n*");
			if(index >-1)s = s.substring(0,index) + "arctan" + s.substring(index+12);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("a*r*c*t*a*n");
			if(index >-1)s = s.substring(0,index) + "arctan" + s.substring(index+11);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("s*i*n*");
			if(index >-1)s = s.substring(0,index) + "sin" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("s*i*n");
			if(index >-1)s = s.substring(0,index) + "sin" + s.substring(index+5);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("c*o*s*");
			if(index >-1)s = s.substring(0,index) + "cos" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("c*o*s");
			if(index >-1)s = s.substring(0,index) + "cos" + s.substring(index+5);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("t*a*n*");
			if(index >-1)s = s.substring(0,index) + "tan" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("t*a*n");
			if(index >-1)s = s.substring(0,index) + "tan" + s.substring(index+5);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("l*o*g*");
			if(index >-1)s = s.substring(0,index) + "log" + s.substring(index+6);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("l*o*g");
			if(index >-1)s = s.substring(0,index) + "log" + s.substring(index+5);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("l*n*");
			if(index >-1)s = s.substring(0,index) + "ln" + s.substring(index+4);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("l*n");
			if(index >-1)s = s.substring(0,index) + "ln" + s.substring(index+3);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("(-");
			if(index >-1)s = s.substring(0,index) + "(0-" + s.substring(index+2);
		}
		index = 0;
		while(index >-1)
		{	index = s.indexOf("+-");
			if(index >-1)s = s.substring(0,index) + "-" + s.substring(index+2);
		}
		
		return s;	
	}
	
	public static String pel(String s)
	{	boolean pelbaar = true;
		while(pelbaar)
		{	if(s.length()>0 && s.charAt(0)=='(' && s.charAt(s.length()-1)==')')
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
		return s;
	}
	
	public static Expressie parse(String s)
	{	return parse(s,false);
	}
	
	public static Expressie parse(String s, boolean woordformule)
	{	Expressie exp = null;
		//verwijder overbodige haakjes
		try
		{
		boolean pelbaar = true;
		while(pelbaar)
		{	if(s.length()>0 && s.charAt(0)=='(' && s.charAt(s.length()-1)==')')
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
		if(s.length()>0 && s.charAt(0)=='-')s = '0' + s;
		if(s.length()>0 && s.charAt(0)=='+')s = s.substring(1);
		
		if(woordformule || FormuleParser.woordFormule)
		{	boolean startMetLetter = true;
			startMetLetter = Letter.isLetter(s.charAt(0));
			boolean basisString = true;
			for(int i=1 ; i<s.length() ; i++)
			{	basisString = startMetLetter && (Letter.isLetter(s.charAt(i)) || Character.isDigit(s.charAt(i)));
				if(!basisString) break;
			}
			
			if(basisString)
			{	if(s.length()==1 && s.charAt(0)=='e')exp = new E();
				else if(s.length()==1 && s.charAt(0)=='\u03C0')exp = new PI();
				else exp = new BasisExpressie(s);
				return exp;
			}
		}
		else if(FormuleParser.tweeHoofdletterVariabele)
		{
			boolean upperCasePair = s.length()==2 && Character.isUpperCase(s.charAt(0))&& Character.isUpperCase(s.charAt(1));
			if(upperCasePair) return new BasisExpressie(s);
			else if(s.length()==1 && Letter.isLetter(s.charAt(0)))
			{	if(s.charAt(0)=='e')exp = new E();
				else if(s.charAt(0)=='\u03C0')exp = new PI();
				else exp = new BasisExpressie(s);
				return exp;
			}
		}
		else
		{
			//is het een letter?		
			if (s.length() == 1 && Letter.isLetter(s.charAt(0)))
			{	if (s.charAt(0) == 'e')
					exp = new E();
				else if (s.charAt(0) == '\u03C0')
					exp = new PI();
				else 
					exp = new BasisExpressie(s);
				return exp;
			}
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
		{	exp = new BasisExpressie(Double.valueOf(s).doubleValue());
			//if("MW".equals(Grafiek3DGWT.deployVariant)) 
			//	exp = new BasisExpressie(s);
			return exp;
		}
		
		// is + of - oneindig?
		if(s.equals("\u221e")) 
		{	double d = Double.POSITIVE_INFINITY;
			exp = new BasisExpressie(s);
		}
		
		if(s.equals("-\u221e")) 
		{	double d = Double.NEGATIVE_INFINITY;
			exp = new BasisExpressie(s);
		}
		
		
		int niv = 0;
			for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(s.charAt(i)=='+' && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Optelling(e1,e2);
					
				}

			}
		
		niv = 0;
			for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(s.charAt(i)=='-' && niv==0)
				{	
					Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Aftrekking(e1,e2);
					
				}

			}
		niv = 0;
		if(s.length()>4)
		{	for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(i<s.length()-4 && s.substring(i,i+4).equals("*sin") && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Vermenigvuldiging(e1,e2);
					
				}

			}
		}
		niv = 0;
		if(s.length()>4)
		{	for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(i<s.length()-4 && s.substring(i,i+4).equals("*cos") && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Vermenigvuldiging(e1,e2);
					
				}

			}
		}
		niv = 0;
		if(s.length()>4)
		{	for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(i<s.length()-4 && s.substring(i,i+4).equals("*tan") && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Vermenigvuldiging(e1,e2);
					
				}

			}
		}
		
		niv = 0;
		if(s.length()>7)
		{	for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(i<s.length()-7 && s.substring(i,i+7).equals("*arcsin") && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Vermenigvuldiging(e1,e2);
					
				}

			}
		}
		niv = 0;
		if(s.length()>7)
		{	for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(i<s.length()-7 && s.substring(i,i+7).equals("*arccos") && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Vermenigvuldiging(e1,e2);
					
				}

			}
		}
		niv = 0;
		if(s.length()>7)
		{	for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(i<s.length()-7 && s.substring(i,i+7).equals("*arctan") && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Vermenigvuldiging(e1,e2);
					
				}

			}
		} 
		 
		niv = 0;
		if(s.length()>4)
		{	for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(i<s.length()-4 && s.substring(i,i+4).equals("*log") && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Vermenigvuldiging(e1,e2);
					
				}

			}
		}
		niv = 0;
		if(s.length()>3)
		{	for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(i<s.length()-3 && s.substring(i,i+3).equals("*ln") && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Vermenigvuldiging(e1,e2);
					
				}

			}
		}
		
		
		
		niv = 0;
		if(s.length()>4 && s.substring(0,4).equals("sin^"))
		{	for(int i=4 ; i<s.length() ; i++)
			{	if(s.charAt(i)=='(')
				{	niv++;
				}
				else if(s.charAt(i)==')')
				{	niv--;
				}
				else if(s.substring(i,i+1).equals("*") && niv==0)
				{	
					Expressie e1 = parse(s.substring(4,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Macht(new Sinus(e2),e1);
				}
				
			}
			return null;
		}
		
		niv = 0;
		if(s.length()>4 && s.substring(0,4).equals("cos^"))
		{	for(int i=4 ; i<s.length() ; i++)
			{	if(s.charAt(i)=='(')
				{	niv++;
				}
				else if(s.charAt(i)==')')
				{	niv--;
				}
				else if(s.substring(i,i+1).equals("*") && niv==0)
				{	
					Expressie e1 = parse(s.substring(4,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Macht(new Cosinus(e2),e1);
				}
				
			}
			return null;
		}
		
		niv = 0;
		if(s.length()>4 && s.substring(0,4).equals("tan^"))
		{	for(int i=4 ; i<s.length() ; i++)
			{	if(s.charAt(i)=='(')
				{	niv++;
				}
				else if(s.charAt(i)==')')
				{	niv--;
				}
				else if(s.substring(i,i+1).equals("*") && niv==0)
				{	
					Expressie e1 = parse(s.substring(4,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Macht(new Tangens(e2),e1);
				}
				
			}
			return null;
		}
		
		niv = 0;
		if(s.length()>4 && s.substring(0,4).equals("log^"))
		{	for(int i=4 ; i<s.length() ; i++)
			{	if(s.charAt(i)=='(')
				{	niv++;
				}
				else if(s.charAt(i)==')')
				{	niv--;
				}
				else if(s.substring(i,i+1).equals("*") && niv==0)
				{	
					Expressie e1 = parse(s.substring(4,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Macht(new Log(e2),e1);
				}
				
			}
			return null;
		}
		
		niv = 0;
		if(s.length()>3 && s.substring(0,3).equals("ln^"))
		{	for(int i=3 ; i<s.length() ; i++)
			{	if(s.charAt(i)=='(')
				{	niv++;
				}
				else if(s.charAt(i)==')')
				{	niv--;
				}
				else if(s.substring(i,i+1).equals("*") && niv==0)
				{	
					Expressie e1 = parse(s.substring(3,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Macht(new Ln(e2),e1);
				}
				
			}
			return null;
		}
		
		if(s.length()>6 && s.substring(0,6).equals("arcsin") && s.charAt(6) != '(')
		{	Expressie e = parse(s.substring(6));
			if(e==null)return null;
			return new ArcSinus(e);
		}
		
		if(s.length()>6 && s.substring(0,6).equals("arccos") && s.charAt(6) != '(')
		{	Expressie e = parse(s.substring(6));
			if(e==null)return null;
			return new ArcCosinus(e);
		}
		
		if(s.length()>6 && s.substring(0,6).equals("arctan") && s.charAt(6) != '(')
		{	Expressie e = parse(s.substring(6));
			if(e==null)return null;
			return new ArcTangens(e);
		}
		
		if(s.length()>3 && s.substring(0,3).equals("sin") && s.charAt(3) != '(')
		{	Expressie e = parse(s.substring(3));
			if(e==null)return null;
			return new Sinus(e);
		}
		
		if(s.length()>3 && s.substring(0,3).equals("cos") && s.charAt(3) != '(')
		{	Expressie e = parse(s.substring(3));
			if(e==null)return null;
			return new Cosinus(e);
		}
		
		if(s.length()>3 && s.substring(0,3).equals("tan") && s.charAt(3) != '(')
		{	Expressie e = parse(s.substring(3));
			if(e==null)return null;
			return new Tangens(e);
		}
		
		if(s.length()>3 && s.substring(0,3).equals("log") && s.charAt(3) != '(')
		{	Expressie e = parse(s.substring(3));
			if(e==null)return null;
			return new Log(e);
		}
		
		if(s.length()>2 && s.substring(0,2).equals("ln") && s.charAt(2) != '(')
		{	Expressie e = parse(s.substring(2));
			if(e==null)return null;
			return new Ln(e);
		}
		
		
		
		 niv = 0;
			for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(s.charAt(i)=='*' && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Vermenigvuldiging(e1,e2);
					
				}
				
			}
		
		 niv = 0;
			for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(s.charAt(i)=='/' && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Deling(e1,e2);
					
				}
			}
		
		niv = 0;
			for(int i=0 ; i<s.length() ; i++)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(s.charAt(i)=='^' && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new Macht(e1,e2);
					
				}
			}
			
		niv = 0;
			for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(s.charAt(i)=='|' && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new NdeWortel(e1,e2);
					
				}
			}
			
		niv = 0;
			for(int i=s.length()-1 ; i>-1 ; i--)
			{	if(s.charAt(i)==')')
				{	niv++;
				}
				else if(s.charAt(i)=='(')
				{	niv--;
				}
				else if(s.charAt(i)=='~' && niv==0)
				{	Expressie e1 = parse(s.substring(0,i));
					Expressie e2 = parse(s.substring(i+1));
					if(e1==null || e2==null)return null;
					return new NdeLog(e1,e2);
					
				}
			}
            niv = 0;
            for(int i=s.length()-1 ; i>-1 ; i--)
            {   if(s.charAt(i)==')')
                {   niv++;
                }
                else if(s.charAt(i)=='(')
                {   niv--;
                }
                else if(s.charAt(i)=='?' && niv==0)
                {   Expressie e1 = parse(s.substring(0,i));
                    Expressie e2 = parse(s.substring(i+1));
                    if(e1==null || e2==null)return null;
                    return new BasisExpressie(s.substring(0,i) + "?" + s.substring(i+1));
                }
            }
		
		//is het een wortel
		if(s.length()>4 && s.substring(0,4).equals("sqrt"))
		{	Expressie e = parse(s.substring(4,s.length()));
			if(e==null)return null;
			return new Wortel(e);
		}
		else if(s.length()>3 && s.substring(0,3).equals("bin"))
		{	String string = s.substring(4,s.length()-1);
			int lastIndex = string.lastIndexOf('_');
			Expressie e1 = parse(string.substring(0,lastIndex));
			Expressie e2 = parse(string.substring(lastIndex+1));
			if(e1==null || e2==null)return null;
			else return new Bin(e1,e2);
		}
		else if(s.length()>3 && s.substring(0,3).equals("rnd"))
		{	String string = s.substring(4,s.length()-1);
			int lastIndex = string.lastIndexOf('_');
			Expressie e1 = parse(string.substring(0,lastIndex));
			Expressie e2 = parse(string.substring(lastIndex+1));
			if(e1==null || e2==null)return null;
			else return new DecRound(e1,e2);
		}
		else if(s.length()>3 && s.substring(0,3).equals("rns"))
		{	String string = s.substring(4,s.length()-1);
			String[] stringDelen = string.split("_");
			int lastIndex0 = string.lastIndexOf('_');
			int lastIndex1 = string.lastIndexOf('_',lastIndex0-1);
			int lastIndex2 = string.lastIndexOf('_',lastIndex1-1);
			Expressie e1 = parse(string.substring(0,lastIndex1));
			Expressie e2 = parse(string.substring(lastIndex1+1,lastIndex0));
			Expressie e3 = parse(string.substring(lastIndex0+1));
			if(e1==null || e2==null || e3==null)return null;
			else return new SigRound(e1,e2,e3);
		}
		else if(s.length()>3 && s.substring(0,3).equals("rnq"))
		{	String string = s.substring(4,s.length()-1);
			int lastIndex = string.lastIndexOf('_');
			Expressie e1 = parse(string.substring(0,lastIndex));
			Expressie e2 = parse(string.substring(lastIndex+1));
			if(e1==null || e2==null)return null;
			else return new DecRoundStrict(e1,e2);
		}
		//	is het een arcsinus
		else if(s.length()>7 && s.substring(0,7).equals("arcsin("))
		{	Expressie e = parse(s.substring(6,s.length()));
			if(e==null)return null;
			return new ArcSinus(e);
		}
		//is het een arccosinus
		else if(s.length()>7 && s.substring(0,7).equals("arccos("))
		{	Expressie e = parse(s.substring(6,s.length()));
			if(e==null)return null;
			return new ArcCosinus(e);
		}
		//is het een arctangens
		else if(s.length()>7 && s.substring(0,7).equals("arctan("))
		{	Expressie e = parse(s.substring(6,s.length()));
			if(e==null)return null;
			return new ArcTangens(e);
		}
		//is het een sinus
		else if(s.length()>4 && s.substring(0,4).equals("sin("))
		{	Expressie e = parse(s.substring(3,s.length()));
			if(e==null)return null;
			return new Sinus(e);
		}
		//is het een cosinus
		else if(s.length()>4 && s.substring(0,4).equals("cos("))
		{	Expressie e = parse(s.substring(3,s.length()));
			if(e==null)return null;
			return new Cosinus(e);
		}
		//is het een tangens
		else if(s.length()>4 && s.substring(0,4).equals("tan("))
		{	Expressie e = parse(s.substring(3,s.length()));
			if(e==null)return null;
			return new Tangens(e);
		}
		//is het een log
		else if(s.length()>4 && s.substring(0,4).equals("log("))
		{	Expressie e = parse(s.substring(3,s.length()));
			if(e==null)return null;
			return new Log(e);
		}
		//is het een ln
		else if(s.length()>3 && s.substring(0,3).equals("ln("))
		{	Expressie e = parse(s.substring(2,s.length()));
			if(e==null)return null;
			return new Ln(e);
		}
		
		}
		catch(Exception e)
		{}
		return exp;
		
	}
	
	public static Expressie geefExpressie(String codeString)
	{	return parse(schoon(formuleString(codeString)));
	}
	
	public static Expressie geefExpressie(String codeString, boolean woordformule)
	{	return parse(schoon(formuleString(codeString),woordformule),woordformule);
	}
	
	public static String substitueerRandom(String formString, String[] varnamen, HashMap<String,Object> waarden)
	{	return substitueerRandom(formString,  varnamen,  waarden, true);
	}
	
	public static String substitueerRandom(String formString, String[] varnamen, HashMap<String,Object> waarden, boolean breukenGemengd)
	{	String sNieuw = null;
		String s1Nieuw = null;
		String s2Nieuw = null;
		Expressie e = null;
		Expressie e1 = null;
		Expressie e2 = null;
		boolean parseable = true;
		int n = formString.indexOf("=");
		if(n>-1)
		{	FormuleParser p = new FormuleParser();
			String s1 = formString.substring(0,n);
			e1 = geefExpressie("$f" + s1 + "@");
			if(e1==null)parseable = false;
			if(parseable)
			{	for(int j=0 ; j<varnamen.length; j++)
				{	int value = ((Integer)waarden.get(varnamen[j])).intValue();
					e1 = e1.substitueer(value,varnamen[j]);
				}
				e1 = Algebra.herleidMild(e1, breukenGemengd);
				s1Nieuw = e1.toString();
			}
			else 
			{	s1Nieuw = s1;
			}
			
			parseable = true;
			String s2 = formString.substring(n+1);
			e2 = p.parse(p.schoon(p.formuleString("$f" + s2 + "@")));
			if(e2==null)parseable = false;
			if(parseable)
			{	for(int j=0 ; j<varnamen.length; j++)
				{	int value = ((Integer)waarden.get(varnamen[j])).intValue();
					e2 = e2.substitueer(value,varnamen[j]);
				}
				e2 = Algebra.herleidMild(e2, breukenGemengd);			
				s2Nieuw = e2.toString();
			}
			else 
			{	s2Nieuw = s2;
			}
			sNieuw = s1Nieuw + "=" + s2Nieuw;
		}
		else 
		{	String s = formString;
			//System.out.println(formString);
			FormuleParser p = new FormuleParser();
			e = p.parse(p.schoon(p.formuleString("$f" + s + "@")));
			//System.out.println(e.toString());
			if(e==null)parseable = false;
			if(parseable)
			{	for(int j=0 ; j<varnamen.length; j++)
				{	int value = ((Integer)waarden.get(varnamen[j])).intValue();
					e = e.substitueer(value,varnamen[j]);
				}
				e = Algebra.herleidMild(e, breukenGemengd);
				sNieuw = e.toString();
			}
			else 
			{	sNieuw = s;
			}
		}
		
		System.out.println(formString);
		System.out.println(sNieuw);
		return sNieuw;
	}
	
	
	
}

