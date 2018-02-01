package fi.grafiek3dgwt.client.expressies;


import java.util.Vector;

import fi.grafiek3dgwt.client.expressies.Algebra;

public class Vergelijking 
{	
	Expressie kind1;
	Expressie kind2;
	Expressie[] eindoplossingen;
	String vergelijkingsTeken;
	static String[] tekens = {"=","<",">","\u2264","\u2265","\u2248","~"};
	static String[][] tekenParen = {{"<","<"},{"<","\u2264"},{"\u2264","<"},{"\u2264","\u2264"},{">",">"},{"\u2265",">"},{">","\u2265"},{"\u2265","\u2265"}};
	
	public Vergelijking(Expressie e1,Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		vergelijkingsTeken = "=";
	}
	
	public Vergelijking(Expressie e1, Expressie[] eindopl )
	{	kind1 = e1;
		kind2 = eindopl[0];
		eindoplossingen = eindopl;
		vergelijkingsTeken = "=";
	}
	
	public Vergelijking(Expressie e1,Expressie e2, String vergTeken )
	{	kind1 = e1;
		kind2 = e2;
		vergelijkingsTeken = vergTeken;
	}
	
	public Vergelijking(Expressie e1, Expressie[] eindopl, String vergTeken )
	{	kind1 = e1;
		kind2 = eindopl[0];
		eindoplossingen = eindopl;
		vergelijkingsTeken = vergTeken;
	}
	
	public boolean isOngelijkheid()
	{	return(!(vergelijkingsTeken.equals("=") || vergelijkingsTeken.equals("\u2248")));
	}
	
	public boolean isAfronding()
	{	return(vergelijkingsTeken.equals("\u2248"));
	}
	
	public String geefVergTeken()
	{	return vergelijkingsTeken;
	}
	
	public static String geefInvVergTeken(String vergTeken)
	{	if(vergTeken.equals("\u2264")) return "\u2265";
		else if(vergTeken.equals("\u2265")) return "\u2264";
		else if(vergTeken.equals(">")) return "<";
		else if(vergTeken.equals("<")) return ">";
        else if(vergTeken.equals("\u2248")) return "\u2248";
        else if(vergTeken.equals("~")) return "~";
		else return "=";
	}
	
	public Vergelijking bewerkVergelijking(String operator, Expressie en)
	{
		Expressie e1 = kind1;
		Expressie e2 = kind2;
		if(operator.equals("+"))
		{	e1 = new Optelling(e1,en);
			e2 = new Optelling(e2,en);
			e1 = Algebra.herleid(e1);
			e2 = Algebra.herleid(e2);
		}
		else if(operator.equals("-"))
		{	e1 = new Aftrekking(e1,en);
			e2 = new Aftrekking(e2,en);
			e1 = Algebra.herleid(e1);
			e2 = Algebra.herleid(e2);
		}
		else if(operator.equals("*"))
		{	e1 = Algebra.vermenigvuldig(e1,en);
			e2 = Algebra.vermenigvuldig(e2,en);
			if(en.geefWaarde()<0)vergelijkingsTeken = geefInvVergTeken(vergelijkingsTeken);
		}
		else if(operator.equals(":"))
		{	en = new Deling(new BasisExpressie(1),en);
			e1 = Algebra.vermenigvuldig(e1,en);
			e2 = Algebra.vermenigvuldig(e2,en);
			if(en.geefWaarde()<0)vergelijkingsTeken = geefInvVergTeken(vergelijkingsTeken);
		}
		else if(operator.equals("haakjes"))
		{	e1 = Algebra.verwijderHaakjes(e1);
			e2 = Algebra.verwijderHaakjes(e2);
			
		}
		else if(operator.equals("herleid"))
		{	e1 = Algebra.herleid(e1);
			e2 = Algebra.herleid(e2);
		}
		else if(operator.equals("ontbind"))
        {   e1 = Algebra.ontbind(e1);
            e2 = Algebra.ontbind(e2);
        }
		
		return new Vergelijking(e1,e2, vergelijkingsTeken);
	}
	
	public boolean isOplossing(double subst)
	{	return Algebra.isGelijkDouble(kind1.geefWaarde(subst),kind2.geefWaarde(subst));
	}
	
	//werkt alleen nog voor lineaire ongelijkheden
	public boolean isOplossing(double subst, String vergTeken)
	{	//vergTeken = geefVergTeken();
		boolean grensKlopt = Algebra.isGelijkDouble(kind1.geefWaarde(subst),kind2.geefWaarde(subst));
		if(vergTeken.equals("=") && vergelijkingsTeken.equals("=")) return grensKlopt;
        if(vergTeken.equals("\u2248") && vergelijkingsTeken.equals("\u2248")) return grensKlopt;
		boolean verTekenKlopt = vergTeken.equals(vergelijkingsTeken) || vergTeken.equals(geefInvVergTeken(vergelijkingsTeken));
		boolean juisteKant = false;
		if((vergTeken.equals("\u2264") || vergTeken.equals("<")) && (vergelijkingsTeken.equals("\u2264") || vergelijkingsTeken.equals("<")))
		{		juisteKant = kind1.geefWaarde(subst-0.1) < kind2.geefWaarde(subst-0.1);
		}		
		else if((vergTeken.equals("\u2265") || vergTeken.equals(">")) && (vergelijkingsTeken.equals("\u2265") || vergelijkingsTeken.equals(">")))
		{		juisteKant = kind1.geefWaarde(subst+0.1) > kind2.geefWaarde(subst+0.1);
		}
		else if((vergTeken.equals("\u2264") || vergTeken.equals("<")) && (vergelijkingsTeken.equals("\u2265") || vergelijkingsTeken.equals(">")))
		{		juisteKant = kind1.geefWaarde(subst-0.1) > kind2.geefWaarde(subst-0.1);
		}		
		else if((vergTeken.equals("\u2265") || vergTeken.equals(">")) && (vergelijkingsTeken.equals("\u2264") || vergelijkingsTeken.equals("<")))
		{		juisteKant = kind1.geefWaarde(subst+0.1) < kind2.geefWaarde(subst+0.1);
		}
		return grensKlopt && verTekenKlopt && juisteKant;
	}
	
	public boolean bevatOplossing(double[] subst)
	{	for(int i=0 ; i<subst.length ; i++)
		{	if(Algebra.isGelijkDouble(kind1.geefWaarde(subst[i]),kind2.geefWaarde(subst[i])))
			{	return true;
			}
		}
		return false;
	}
	
	private Expressie evalCAS(Expressie e)
	{	boolean casNodig = e.toString().indexOf("$i")>-1 || e.toString().indexOf("$d")>-1 || e.toString().indexOf("$T")>-1 || e.toString().indexOf("$P")>-1;
			return e;
		
	}
	
	public boolean isOplossing(Expressie subst, String var)
	{	
	
		Expressie e1 = evalCAS(kind1.substitueer(subst, var));
		Expressie e2 = evalCAS(kind2.substitueer(subst, var));
		
		if(Algebra.isGelijkwaardig(e1,e2))
		{	return true;
		}
		else if(subst instanceof Optelling && Algebra.isGelijkDouble(subst.kind2.geefWaarde(),0.1234567))
		{	
			return Algebra.isGelijkDouble(Algebra.geefNormDiscriminant(this),subst.kind1.geefWaarde());
		}
		else if(subst instanceof Optelling && Algebra.isGelijkDouble(subst.kind2.geefWaarde(),0.2345678))
		{	double a = subst.kind1.kind1.geefWaarde();
			double b = subst.kind1.kind2.kind1.geefWaarde() / a;
			double c = subst.kind1.kind2.kind2.geefWaarde() / a;
			double[] coeff = Algebra.geefCoefficienten(this);
			if(coeff==null) return false;
			if(coeff.length!=3 || coeff[2]==0) return false;
			boolean b0 = Algebra.isGelijkDouble(coeff[0]/coeff[2],c);
			boolean b1 = Algebra.isGelijkDouble(coeff[1]/coeff[2],b);
			return b0 && b1 ;
		}
		else return false;
		
	}
	
	public boolean isOplossing(Expressie subst, String var, String vergTeken)
	{	boolean grensKlopt = isOplossing(subst, var);
		if(vergTeken.equals("=") && vergelijkingsTeken.equals("=")) return grensKlopt;
        if(vergTeken.equals("\u2248") && vergelijkingsTeken.equals("\u2248")) return grensKlopt;
		boolean verTekenKlopt = vergTeken.equals(vergelijkingsTeken) || vergTeken.equals(geefInvVergTeken(vergelijkingsTeken));
		boolean juisteKant = false;
		if((vergTeken.equals("\u2264") || vergTeken.equals("<")) && (vergelijkingsTeken.equals("\u2264") || vergelijkingsTeken.equals("<")))
		{		juisteKant = kind1.geefWaarde(subst.geefWaarde()-0.1) < kind2.geefWaarde(subst.geefWaarde()-0.1);
		}		
		else if((vergTeken.equals("\u2265") || vergTeken.equals(">")) && (vergelijkingsTeken.equals("\u2265") || vergelijkingsTeken.equals(">")))
		{		juisteKant = kind1.geefWaarde(subst.geefWaarde()+0.1)> kind2.geefWaarde(subst.geefWaarde()+0.1);
		}
		else if((vergTeken.equals("\u2264") || vergTeken.equals("<")) && (vergelijkingsTeken.equals("\u2265") || vergelijkingsTeken.equals(">")))
		{		juisteKant = kind1.geefWaarde(subst.geefWaarde()-0.1) > kind2.geefWaarde(subst.geefWaarde()-0.1);
		}		
		else if((vergTeken.equals("\u2265") || vergTeken.equals(">")) && (vergelijkingsTeken.equals("\u2264") || vergelijkingsTeken.equals("<")))
		{		juisteKant = kind1.geefWaarde(subst.geefWaarde()+0.1) < kind2.geefWaarde(subst.geefWaarde()+0.1);
		}
		else if(vergTeken.equals("~") && vergelijkingsTeken.equals("~") && subst.kind1.geefWaarde()==kind2.kind1.geefWaarde())
		{	boolean b1 = Algebra.isGelijkwaardig(subst.kind2.kind1, kind2.kind2.kind1);
			boolean b2 = Algebra.isGelijkwaardig(subst.kind2.kind2, kind2.kind2.kind2);
			return b1 && b2;
		}
		else if(vergTeken.equals("~") && vergelijkingsTeken.equals("~") && (subst.kind1.geefWaarde()+4)%8==kind2.kind1.geefWaarde())
		{	boolean b1 = Algebra.isGelijkwaardig(subst.kind2.kind1, kind2.kind2.kind2);
			boolean b2 = Algebra.isGelijkwaardig(subst.kind2.kind2, kind2.kind2.kind1);
			return b1 && b2;
		}
		
		return grensKlopt && verTekenKlopt && juisteKant;
	}
	
	public boolean bevatOplossing(Expressie[] subst, String var)
	{	for(int i=0 ; i<subst.length ; i++)
		{	if(isOplossing(subst[i], var))
			{	return true;
			}
		}
		return false;
	}
	
	public boolean bevatOplossing(Expressie[] subst, String var, String vergTeken[])
	{	for(int i=0 ; i<subst.length ; i++)
		{	if(isOplossing(subst[i], var, vergTeken[i]))
			{	return true;
			}
		}
		return false;
	}
	
	public boolean bevatOplossing(Expressie[][] subst, String var, String vergTeken[])
	{	for(int i=0 ; i<subst.length ; i++)
		{	if(bevatOplossingP(subst[i], var, vergTeken[i]))
			{	return true;
			}
		}
		return false;
	}
	
	public boolean bevatOplossingP(Expressie[] subst, String var, String vergTeken)
	{	if(subst==null) return false;
		for(int i=0 ; i<subst.length ; i++)
		{	if(isOplossing(subst[i], var, vergTeken))
			{	return true;
			}
		}
		return false;
	}
	
	public boolean checkDiscriminant(int discriminant, String varNaam)
	{	return discriminant == Algebra.geefNormDiscriminant(this);
		
	}
	public Expressie geefExpLinks()
	{	return kind1;
	}
	
	public Expressie geefExpRechts()
	{	return kind2;
	}
	
	public Vector geefVarN()
	{	Vector v;
		Vector v1 = Algebra.geefVarN(kind1);
		Vector v2 = Algebra.geefVarN(kind2);
		int lengte = v1.size();
		for(int i=0 ; i<v2.size() ; i++)
		{	boolean anders = true;
			for(int j=0 ; j<lengte ; j++)
			{	if(((String)v1.elementAt(j)).equals(((String)v2.elementAt(i))))
				{	anders = false;
				}
			}
			if(anders)v1.addElement(v2.elementAt(i));
		}
		v=v1;
		return v;
	}
	
	public String[] geefVarNamen()
	{	Vector varn = geefVarN();
		String[] varNamen = new String[varn.size()];
		for(int i=0 ; i<varn.size() ; i++)
		{	varNamen[i] = (String)varn.elementAt(i);
		}
		return varNamen;
	}
	
	public String geefVarNaam()
	{	String s1 = kind1.geefVarNaam();
		String s2 = kind2.geefVarNaam();
		if(s1!=null && s2!=null && (s1.equals("") || s2.equals("")))return "";
		else if(s1!=null && s2!=null && !s1.equals(s2))return "";
		else if(s1!=null && s2!=null && s1.equals(s2))return s1;
		else if(s1!=null && s2==null)return s1;
		else if(s1==null && s2!=null)return s2;
		else return null;
	}
	
	public boolean isEindOplossing(String var)
	{	return(kind1.isVar() && kind1.geefVarNaam().equals(var) && !Algebra.bevatVarNaam(kind2, var) 
			|| kind2.isVar() && kind2.geefVarNaam().equals(var) && !Algebra.bevatVarNaam(kind1, var)
			|| kind1.isVar() && kind1.geefVarNaam().equals("D") && !(kind2.isVar() && kind2.geefVarNaam().equals("D")) && !Algebra.bevatVarNaam(kind2, var)
			|| kind1.isVar() && kind1.geefVarNaam().equals("Q") && !(kind2.isVar() && kind2.geefVarNaam().equals("Q")) && !Algebra.bevatVarNaam(kind2, var));
	}
	
	public boolean isEindOplossingExact(Expressie subst,String var)
	{	return(kind1.isVar() && kind1.geefVarNaam().equals(var) && !Algebra.bevatVarNaam(kind2, var) && Algebra.zijnGelijk(subst, kind2) 
			|| kind2.isVar() && kind2.geefVarNaam().equals(var) && !Algebra.bevatVarNaam(kind1, var) && Algebra.zijnGelijk(subst, kind1)
			|| kind1.isVar() && kind1.geefVarNaam().equals("D") && !(kind2.isVar() && kind2.geefVarNaam().equals("D")) && !Algebra.bevatVarNaam(kind2, var)
			|| kind1.isVar() && kind1.geefVarNaam().equals("Q") && !(kind2.isVar() && kind2.geefVarNaam().equals("Q")) && !Algebra.bevatVarNaam(kind2, var));
	}
	
	public boolean isEindOplossingExact(Expressie subst[],String var)
	{	for(int i=0 ; i<subst.length ; i++)
		{	boolean b =(kind1.isVar() && kind1.geefVarNaam().equals(var) && !Algebra.bevatVarNaam(kind2, var) && Algebra.zijnGelijk(subst[i], kind2) 
				|| kind2.isVar() && kind2.geefVarNaam().equals(var) && !Algebra.bevatVarNaam(kind1, var) && Algebra.zijnGelijk(subst[i], kind1)
				|| kind1.isVar() && kind1.geefVarNaam().equals("D") && !(kind2.isVar() && kind2.geefVarNaam().equals("D")) && !Algebra.bevatVarNaam(kind2, var)
				|| kind1.isVar() && kind1.geefVarNaam().equals("Q") && !(kind2.isVar() && kind2.geefVarNaam().equals("Q")) && !Algebra.bevatVarNaam(kind2, var));
			if(b)return b;
		}
		return false;
	}
	
	
	public Expressie geefEindOplossing(String var)
	{	if(isEindOplossing(var))
		{	if(kind1.isVar() && kind1.geefVarNaam().equals(var))return kind2;
			else if(kind2.isVar() && kind2.geefVarNaam().equals(var))return kind1;
			else if(kind1.isVar() && kind1.geefVarNaam().equals("D"))return new Optelling(kind2, new BasisExpressie(0.1234567));
			else if(kind1.isVar() && kind1.geefVarNaam().equals("Q"))return new Optelling(kind2, new BasisExpressie(0.2345678));
			else return null;
		}
	
		else return null;
	}
	
	public Expressie[] geefEindOplossingen(String var)
	{	if(eindoplossingen!=null) return eindoplossingen;
		else
		{	Expressie[] e = new Expressie[1];
			e[0] = geefEindOplossing(var);
			return e;
		}
	}
	
	public String geefVergelijkingVar()
	{	String var = ""	;	
		if(kind1.isVar()) var = kind1.geefVarNaam();
		else if(kind2.isVar()) var = kind2.geefVarNaam();
		return var;
	}
	
	public Vergelijking substitueer(Expressie subst, String var)
	{	Expressie e1 = kind1.substitueer(subst, var);
		Expressie e2 = kind2.substitueer(subst, var);
		return new Vergelijking(e1,e2);
	}
	
	
	public String toString()
	{	String s = "";
		if(vergelijkingsTeken.equals("~"))
		{	String s1 = kind2.kind2.kind1.toString();
			String s2 = kind2.kind2.kind2.toString();
			int comb = (int)kind2.kind1.geefWaarde();
			s = s1 + tekenParen[comb][0] + kind1.toString() + tekenParen[comb][1] + s2;
		}
		else if(Algebra.isGelijkDouble(kind2.geefWaarde(), 0.1234567)) 
		{	
			s = "geenOplossingen";
		}
		else  s = kind1.toString() +""+  vergelijkingsTeken  +""+ kind2.toString();
		return s;
	}
	
	public String toStringStrikt()
	{	String s = "";
		if(vergelijkingsTeken.equals("~"))
		{	String s1 = kind2.kind2.kind1.toString();
			String s2 = kind2.kind2.kind2.toString();
			int comb = (int)kind2.kind1.geefWaarde();
			s = s1 + tekenParen[comb][0] + kind1.toString() + tekenParen[comb][1] + s2;
		}
		else if(Algebra.isGelijkDouble(kind2.geefWaarde(), 0.1234567)) 
		{	
			s = "geenOplossingen";
		}
		else  s = kind1.toStringStrikt() +""+  vergelijkingsTeken  +""+ kind2.toStringStrikt();
		return s;
	}
	
	
}
