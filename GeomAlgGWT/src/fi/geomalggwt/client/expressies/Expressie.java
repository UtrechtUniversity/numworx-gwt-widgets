package fi.geomalggwt.client.expressies;

//import java.awt.*;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.net.URLConnection;
//import java.text.*;
import java.util.*;

import fi.geomalggwt.client.GeomAlgGWT;
//import fi.wiskopdr.formuleobjects.*;
//import fi.beans.ideas.IdeasIF;
//import fi.beans.ideas.RuleIF;
import fi.geomalggwt.client.StringUtils;
//import fi.geomalggwt.client.formuleobjects.*;


public class Expressie 
{	
	public Expressie kind1, kind2, kind3, kind4;
	public String operatorString;
	boolean isVeelterm;
	boolean isProdukt;
	boolean isBasis;

	//static DecimalFormatSymbols dfs;
	//public static DecimalFormat df;
	//public static DecimalFormat dfe;
	//public static DecimalFormat df3;
	//public static FontMetrics fm;
	
//	private static Hashtable casEvalStrings = new Hashtable();
	
	static boolean hoekGraden;
	
	public Expressie()
	{	//dfs = new DecimalFormatSymbols();
		//if(GeomAlgGWT.language.equals("nl")) dfs.setDecimalSeparator(',');
		//else dfs.setDecimalSeparator('.');
		//if(GeomAlgGWT.language.equals("nl")) dfs.setGroupingSeparator(' ');
		//else dfs.setGroupingSeparator(' ');
		//df = new DecimalFormat("0.##########", dfs);
		//dfe = new DecimalFormat("0.##########E0", dfs);
		//df3 = new DecimalFormat("0.###", dfs);
	}
	public static void zetHoekGraden(boolean b)
  	{	hoekGraden=b;
	}
	//public void zetMaat(FontMetrics fm)
  	//{
	//}
	//public void teken(Context2d g, int x, int y)
  	//{ 
	//}
	public double geefWaarde()
	{	return Double.NaN;
	}
	
	public Complex geefWaardeComplex()
	{	return null;
	}
	
	public double geefWaarde(double subst)
	{	return Double.NaN;
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	return null;
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return Double.NaN;
	}
	
	public Complex geefWaardeComplex(Complex[] subst, String[] vars)
	{	return null;
	}
	
	public Expressie substitueer(double subst, String var)
	{	return null;
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return null;
	}
	
	public boolean isWaarde(double subst)
	{	return true;
	}
	public String geefVarNaam()
	{	return null;
	}
	
	public boolean isVar()
	{	return this instanceof BasisExpressie;
	}
	
	public boolean isWaarde()
	{	return !Double.isNaN(geefWaarde());
	}
	
	public String toString()
	{	return null;
	}
    
    public String toStringCAS()
    {   return null;
    }
    
	public String toStringStrikt()
	{	return null;
	}
	
/*	
	public static Expressie evalWithCAS(Expressie e)
	{
		return evalWithCAS(e.toStringCAS());
		//return evalWithIdeas(e.toStringStrikt());
	}
*/
/*	
	private static Expressie evalWithIdeas(String evalCommand)
	{
		Expressie expr = (Expressie) casEvalStrings.get(evalCommand);
		if(expr != null)
			return expr;
		RuleIF result = WiskOpdr.ideas.interpret(evalCommand);
		if (result.isException())
			return null;
		expr =  FormuleParser.geefExpressie("$f" + result.getExpr() + "@");
		if(expr != null)
			casEvalStrings.put(evalCommand, expr);
		return expr;
	}
*/	
	/**
	 * Bereken de (double) waarde van een Expressie via een CAS.
	 * @param e
	 * @return waarde
	 */
/*	
	public static double geefWaardeViaIdeas(Expressie e)
	{
		RuleIF result = WiskOpdr.ideas.interpret(IdeasIF.NUMERIC, e.toStringStrikt());
		if(result.isException())
			return Double.NaN;
		return Double.parseDouble(result.getExpr());
	}
*/	
	
/*	
	public static Expressie evalWithCAS(String evalCommand)
	{	
       	Expressie e = null;
       	String s = "";
       	
    	if(casEvalStrings.containsKey(evalCommand)) s = (String)casEvalStrings.get(evalCommand);
    	else
    	{	System.out.println(evalCommand);
       	
	        try
	        {   WiskOpdr.phrasebook.eval("ClearAll[x]");
	            s = WiskOpdr.phrasebook.eval("InputForm[" + evalCommand + "]");
	            //s = WiskOpdr.phrasebook.eval(evalCommand);
	            
	            System.out.println(s);
	        }
	        catch(Exception ex)
	        {}
	        casEvalStrings.put(evalCommand, s);
    	}
		
		s = s.substring(0,s.length()-1);
		s = s.replace('[','(');
		s = s.replace(']',')');
		s = StringUtils.replaceStr(s,"Pi","\u03C0");
		s = StringUtils.replaceStr(s,"E","e");
		s = StringUtils.replaceStr(s,"Log","ln");
		s = StringUtils.replaceStr(s,"Sin","sin");
		s = StringUtils.replaceStr(s,"Cos","cos");
		s = StringUtils.replaceStr(s,"Tan","tan");
		s = StringUtils.replaceStr(s,"Arc","arc");
		s = StringUtils.replaceStr(s,"Sqrt","sqrt");
		
		System.out.println("$f"+s+"@");
		e = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f"+s+"@")));
		return e;
	}
*/
/*
	public static VergelijkingMeerv solveWithCAS(String evalCommand, String arg)
	{	
       	VergelijkingMeerv v = null;
       	String s = "";
       	
    	if(casEvalStrings.containsKey(evalCommand)) s = (String)casEvalStrings.get(evalCommand);
    	else
    	{	System.out.println(evalCommand);
       	
	        try
	        {   System.out.println(s);
	        	s = WiskOpdr.phrasebook.eval("InputForm[" + arg+"/."+"Solve[" + evalCommand + "," + arg + "]" + "]");
	            System.out.println(s);
	        }
	        catch(Exception ex)
	        {ex.printStackTrace();}
	        //casEvalStrings.put(evalCommand, s);
    	}
    	
    	String[] oplossingen = StringUtils.split(s.substring(1,s.length()-2), ",");
    	
    	for(int i=0 ; i<oplossingen.length ; i++)
		{	s = oplossingen[i];
			s = s.replace('[','(');
			s = s.replace(']',')');
			s = StringUtils.replaceStr(s,"Pi","\u03C0");
			s = StringUtils.replaceStr(s,"E","e");
			s = StringUtils.replaceStr(s,"I","i");
			s = StringUtils.replaceStr(s,"Log","ln");
			s = StringUtils.replaceStr(s,"Sin","sin");
			s = StringUtils.replaceStr(s,"Cos","cos");
			s = StringUtils.replaceStr(s,"Tan","tan");
			s = StringUtils.replaceStr(s,"Arc","arc");
			s = StringUtils.replaceStr(s,"Sqrt","sqrt");
			oplossingen[i] = s;
			//System.out.println(oplossingen[i]);
		}
		Expressie[] es = new Expressie[oplossingen.length];
		
		Vergelijking[] vs = new Vergelijking[oplossingen.length];
		for(int i=0 ; i<es.length ; i++)
		{	
			es[i] = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f"+oplossingen[i].trim()+"@")));
			//System.out.println(oplossingen[i]);
			//System.out.println(es[i].toString());
			vs[i] = new Vergelijking(new BasisExpressie(arg),es[i]);
		}
		v = new VergelijkingMeerv(vs);
		return v;
	}
*/	
}
