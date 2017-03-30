package fi.weblogo3dgwt.client.expressies;

import java.util.Vector;

//import fi.wiskopdr.WiskOpdr;
import fi.weblogo3dgwt.client.expressies.Algebra;
import fi.weblogo3dgwt.client.expressies.BasisExpressie;
import fi.weblogo3dgwt.client.expressies.Expressie;
import fi.weblogo3dgwt.client.expressies.Vergelijking;
//import fi.javalogoweb.expressies.VergelijkingMeerv;

//import fi.javalogoweb.WiskOpdr;


public class VergelijkingMeerv 
{	
	Vergelijking[] vergelijkingen;
	//OngelijkheidObject[] ongelijkheidObjecten;
	
	public VergelijkingMeerv(Vergelijking[] v)
	{	vergelijkingen = v;
	}
	
	public int geefAantal()
	{	return	vergelijkingen.length;
	}
	
	public boolean isGelijkMet(VergelijkingMeerv verg)
	{
		if(geefAantal()!=verg.geefAantal()) return false;
		else if(geefAantal()==1)
		{
			Expressie e01 = vergelijkingen[0].kind1;
			Expressie e02 = vergelijkingen[0].kind2;
			Expressie f01 = verg.geefVergelijking(0).kind1;
			Expressie f02 = verg.geefVergelijking(0).kind2;
			boolean b0 = Algebra.isGelijkwaardig(e01, f01) && Algebra.isGelijkwaardig(e02, f02);
			
			return b0;
		}
		else if(geefAantal()==2)
		{
			Expressie e01 = vergelijkingen[0].kind1;
			Expressie e02 = vergelijkingen[0].kind2;
			Expressie e11 = vergelijkingen[1].kind1;
			Expressie e12 = vergelijkingen[1].kind2;
			Expressie f01 = verg.geefVergelijking(0).kind1;
			Expressie f02 = verg.geefVergelijking(0).kind2;
			Expressie f11 = verg.geefVergelijking(1).kind1;
			Expressie f12 = verg.geefVergelijking(1).kind2;
			boolean b0 = Algebra.isGelijkwaardig(e01, f01) && Algebra.isGelijkwaardig(e02, f02);
			boolean b1 = Algebra.isGelijkwaardig(e11, f11) && Algebra.isGelijkwaardig(e12, f12);
			boolean b2 = Algebra.isGelijkwaardig(e01, f01) && Algebra.isGelijkwaardig(e12, f12);
			boolean b3 = Algebra.isGelijkwaardig(e11, f11) && Algebra.isGelijkwaardig(e02, f02);
			
			return b0 && b1 || b2 && b3;
		}
		return false;
		
	}
	
	public VergelijkingMeerv bewerkVergelijking(String operator, Expressie en)
	{
		return bewerkVergelijking(operator, en, -1);
	}
	public VergelijkingMeerv bewerkVergelijking(String operator, Expressie en, int nr)
	{	Vergelijking[] vergelijkingenNieuw = new Vergelijking[vergelijkingen.length];
	
    	if(vergelijkingen.length==1 && operator.equals("wortel"))
        {   Expressie e1 = vergelijkingen[0].kind1;
            Expressie e2 = vergelijkingen[0].kind2;
            Expressie[] we = Algebra.geefWortels(e1,e2);
            if(we!=null && we[2]==null)
            {   vergelijkingenNieuw = new Vergelijking[1];
                e1 = we[0];
                e2 = we[1];
                vergelijkingenNieuw[0] = new Vergelijking(e1,e2);
            }
            else if(we!=null)
            {   vergelijkingenNieuw = new Vergelijking[2];
                e1 = we[0];
                e2 = we[1];
                Expressie e3 = we[0];
                Expressie e4 = we[2];
                vergelijkingenNieuw[0] = new Vergelijking(e1,e2);
                vergelijkingenNieuw[1] = new Vergelijking(e3,e4);
            }
        }
        else if(vergelijkingen.length==1 && operator.equals("splits"))
        {   Expressie e1 = vergelijkingen[0].kind1;
            Expressie e2 = vergelijkingen[0].kind2;
            Expressie[] we = Algebra.geefSplitsing(e1,e2);
            if(we!=null && we[1]!=null)
            {   vergelijkingenNieuw = new Vergelijking[2];
                e1 = we[0];
                e2 = new BasisExpressie(0);
                Expressie e3 = we[1];
                Expressie e4 = new BasisExpressie(0);
                vergelijkingenNieuw[0] = new Vergelijking(e1,e2);
                vergelijkingenNieuw[1] = new Vergelijking(e3,e4);
            }
            else if(we!=null)
            {   vergelijkingenNieuw = new Vergelijking[1];
                e1 = we[0];
                e2 = new BasisExpressie(0);
                vergelijkingenNieuw[0] = new Vergelijking(e1,e2);
            }
            else vergelijkingenNieuw = vergelijkingen;
            
        }
        
        else for(int j=0 ; j<vergelijkingen.length ; j++)
		{	if(nr==-1 || nr==j) vergelijkingenNieuw[j] = vergelijkingen[j].bewerkVergelijking(operator,en);
			else vergelijkingenNieuw[j] = vergelijkingen[j];
		}
		return new VergelijkingMeerv(vergelijkingenNieuw);
	}
	
	public boolean isOngelijkheid()
	{	for(int i=0 ; i<vergelijkingen.length ; i++)
		{ 	if(vergelijkingen[i].isOngelijkheid()) return true;
		}
		return false;
	}
	
	public boolean isAfronding()
	{	for(int i=0 ; i<vergelijkingen.length ; i++)
		{ 	if(vergelijkingen[i].isAfronding()) return true;
		}
		return false;
	}
	
	public Vergelijking geefVergelijking(int nr)
	{	if(nr>=vergelijkingen.length)return null;
		return vergelijkingen[nr];
	}
	
	public String[] geefVergTekens()
	{	String[] vergTekens = new String[vergelijkingen.length];
		for(int i=0 ; i<vergelijkingen.length ; i++)
		{	vergTekens[i] = vergelijkingen[i].geefVergTeken();
		}
		return vergTekens;
	}
	
	public boolean isOplossing(double subst)
	{	String[] varNamen = geefVarNamen();
		if(varNamen.length > 1) return false;
		boolean isOplossing = false;
		for(int j=0 ; j<vergelijkingen.length ; j++)
		{	if(!isOplossing)
			isOplossing = vergelijkingen[j].isOplossing(subst);
		}
		return isOplossing;
	}
	
	public boolean isOplossing(double subst, String vergTeken)
	{	String[] varNamen = geefVarNamen();
		if(varNamen.length > 1) return false;
		boolean isOplossing = false;
		for(int j=0 ; j<vergelijkingen.length ; j++)
		{	if(!isOplossing)
			isOplossing = vergelijkingen[j].isOplossing(subst, vergTeken);
		}
		return isOplossing;
	}
	
	public boolean isOplossing(Expressie subst, String var)
	{	String[] varNamen = geefVarNamen();
		boolean isOplossing = false;
		for(int j=0 ; j<vergelijkingen.length ; j++)
		{	if(!isOplossing)
			isOplossing = vergelijkingen[j].isOplossing(subst, var);
		}
		return isOplossing;
	}
	
	public boolean isOplossing(Expressie subst, String var, String vergTeken)
	{	String[] varNamen = geefVarNamen();
		boolean isOplossing = false;
		for(int j=0 ; j<vergelijkingen.length ; j++)
		{	if(!isOplossing)
			isOplossing = vergelijkingen[j].isOplossing(subst, var, vergTeken);
		}
		return isOplossing;
	}
	
	public boolean isEindOplossingExact(Expressie subst, String var, String vergTeken)
	{	String[] varNamen = geefVarNamen();
		boolean isOplossing = false;
		for(int j=0 ; j<vergelijkingen.length ; j++)
		{	if(!isOplossing)
			{	isOplossing = vergelijkingen[j].isOplossing(subst, var, vergTeken);
				if(isOplossing)
				{	boolean exact = vergelijkingen[j].isEindOplossingExact(subst, var);
					if(!exact)return false;
				}
			}	
		}
		return isOplossing;
	}
	
	public boolean isEindOplossingExact(Expressie[] subst, String var, String vergTeken)
	{	String[] varNamen = geefVarNamen();
		boolean isOplossing = false;
		for(int j=0 ; j<vergelijkingen.length ; j++)
		{	if(!isOplossing)
			{	isOplossing = vergelijkingen[j].bevatOplossingP(subst, var, vergTeken);
				if(isOplossing)
				{	boolean exact = vergelijkingen[j].isEindOplossingExact(subst, var);
					if(!exact)return false;
				}
			}	
		}
		return isOplossing;
	}
	
	/*public boolean bevatFouteOplossing(VergelijkingMeerv antw)
	{	boolean isOplossing = true;
		for(int j=0 ; j<vergelijkingen.length ; j++)
		{	if(vergelijkingen[j].isEindOplossing())
			{	isOplossing = antw.isOplossing(vergelijkingen[j].geefEindOplossing());
				if(!isOplossing)return true;
			}
		}
		return false;
	}*/
	
	public boolean bevatFouteOplossing(VergelijkingMeerv antw, String var)
	{	for(int j=0 ; j<vergelijkingen.length ; j++)
		{	if(!vergelijkingen[j].bevatOplossing(antw.geefEindOplossing(var), var))
			{	return true;
			}
		}
		return false;
	}
	
	public boolean bevatFouteOplossing(VergelijkingMeerv antw, String var, String[] vergTekens)
	{	for(int j=0 ; j<vergelijkingen.length ; j++)
		{	if(!vergelijkingen[j].bevatOplossing(antw.geefEindOplossingen(var), var, vergTekens))
			{	return true;
			}
		}
		return false;
	}
	public boolean isOplossing(double[] subst)
	{	String[] varNamen = geefVarNamen();
		if(varNamen.length > 1) return false;
		for(int i=0 ; i<subst.length ; i++)
		{	if(!isOplossing(subst[i]))return false;
		}
		return true;
	}
	
	public boolean isOplossing(double[] subst, String[] vergTekens)
	{	String[] varNamen = geefVarNamen();
		if(varNamen.length > 1) return false;
		for(int i=0 ; i<subst.length ; i++)
		{	if(!isOplossing(subst[i], vergTekens[i]))return false;
		}
		return true;
	}
	
	public boolean isOplossing(Expressie[] subst, String var)
	{	for(int i=0 ; i<subst.length ; i++)
		{	if(!isOplossing(subst[i],var))return false;
		}
		return true;
	}
	
	public boolean isOplossing(Expressie[] subst, String var, String[] vergTekens)
	{	for(int i=0 ; i<subst.length ; i++)
		{	if(!isOplossing(subst[i],var, vergTekens[i]))return false;
		}
		return true;
	}
	
	public boolean isOplossing(Expressie[][] subst, String var, String[] vergTekens)
	{	for(int i=0 ; i<subst.length ; i++)
		{	if(!bevatOplossing(subst[i],var, vergTekens[i]))return false;
		}
		return true;
	}
	
	public boolean bevatOplossing(Expressie[] subst, String var, String vergTekens)
	{	for(int i=0 ; i<vergelijkingen.length ; i++)
		{	if(vergelijkingen[i].bevatOplossingP(subst,var, vergTekens))return true;
		}
		return false;
	}
	
	
	
	public boolean isDeelOplossing(double[] subst)
	{	String[] varNamen = geefVarNamen();
		if(varNamen.length > 1) return false;
		for(int i=0 ; i<subst.length ; i++)
		{	if(isOplossing(subst[i]))return true;
		}
		return false;
	}
	
	public boolean isDeelOplossing(Expressie[] subst, String var)
	{	for(int i=0 ; i<subst.length ; i++)
		{	if(isOplossing(subst[i], var))return true;
		}
		return false;
	}
	
	public boolean isDeelOplossing(Expressie[] subst, String var, String[] vergTekens)
	{	for(int i=0 ; i<subst.length ; i++)
		{	if(isOplossing(subst[i], var, vergTekens[i]))return true;
		}
		return false;
	}
	
	public boolean isDeelOplossing(Expressie[][] subst, String var, String[] vergTekens)
	{	for(int i=0 ; i<subst.length ; i++)
		{	
			for(int j=0 ; j<vergelijkingen.length ; j++)
			{	if(vergelijkingen[j].bevatOplossingP(subst[i],var, vergTekens[i]))return true;
			}
		
		}
		return false;
	}
	
	public boolean checkDiscriminant(int discriminant, String varNaam)
	{	for (int i = 0; i < vergelijkingen.length; i++) 
		{	boolean isGeen = vergelijkingen[i].checkDiscriminant(discriminant, varNaam);
			if(isGeen)return true;
		}
		return false;
	}
	
	public Vector geefVarN()
	{	Vector v = new Vector();
		for(int i=0 ; i<vergelijkingen.length ; i++)
		{	Vector vNieuw = vergelijkingen[i].geefVarN();
		
			int lengte = v.size();
			for(int j=0 ; j<vNieuw.size() ; j++)
			{	boolean anders = true;
				for(int k=0 ; k<lengte ; k++)
				{	if(((String)v.elementAt(k)).equals(((String)vNieuw.elementAt(j))))
					{	anders = false;
					}
				}
				if(anders)v.addElement(vNieuw.elementAt(j));
			}
		}
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
	
	public String toString()
	{	String s = vergelijkingen[0].toString();
		for(int i=1 ; i<vergelijkingen.length ; i++)
		{	s = s + "  " + "of" + "  " + vergelijkingen[i].toString();
		}	
		
		return s;
	}
	
	public String toStringStrikt()
	{
		String s = vergelijkingen[0].toStringStrikt();
		for(int i=1 ; i<vergelijkingen.length ; i++)
		{	s = s + "  " + "of" + "  " + vergelijkingen[i].toStringStrikt();
		}	
		
		return s;
	}
	/*public boolean isEindOplossing()
	{	for(int i=0 ; i<vergelijkingen.length ; i++)
		{	if(!vergelijkingen[i].isEindOplossing())return false;
		}
		return true;
	}*/
	
	public boolean isEindOplossing(String var)
	{	for(int i=0 ; i<vergelijkingen.length ; i++)
		{	if(!vergelijkingen[i].isEindOplossing(var))return false;
		}
		return true;
	}
	
	public boolean isEindOplossingExact(Expressie[][] subst, String var,  String[] vergTekens)
	{	for(int i=0 ; i<subst.length ; i++)
		{	if(!isEindOplossingExact(subst[i],var, vergTekens[i]))return false;
		}
		return true;
	}
	
	
	
	/*
	public double[] geefEindOplossing()
	{	double[] oplossingen = new double[vergelijkingen.length];
		if(isEindOplossing())
		{	for(int i=0 ; i<vergelijkingen.length ; i++)
			{	oplossingen[i] = vergelijkingen[i].geefEindOplossing();
			}
		}
		return oplossingen;
	}*/
	
	public Expressie[] geefEindOplossing(String var)
	{	Expressie[] oplossingen = new Expressie[vergelijkingen.length];
		if(isEindOplossing(var))
		{	for(int i=0 ; i<vergelijkingen.length ; i++)
			{	oplossingen[i] = vergelijkingen[i].geefEindOplossing(var);
			}
		}
		return oplossingen;
	}
	
	public Expressie[][] geefEindOplossingen(String var)
	{	Expressie[][] oplossingen = new Expressie[vergelijkingen.length][];
		if(isEindOplossing(var))
		{	for(int i=0 ; i<vergelijkingen.length ; i++)
			{	oplossingen[i] = vergelijkingen[i].geefEindOplossingen(var);
			}
		}
		return oplossingen;
	}
	
	public String geefVergelijkingVar()
	{	String var = vergelijkingen[0].geefVergelijkingVar();
		for(int i=1 ; i<vergelijkingen.length ; i++)
		{	String varNieuw = vergelijkingen[i].geefVergelijkingVar();
			if(!var.equals(varNieuw))return null;
		}
		return var;
	}
	
	public VergelijkingMeerv substitueer(Expressie subst, String var)
	{	Vergelijking[] vergelijkingenNieuw = new Vergelijking[vergelijkingen.length];
		for(int i=0 ; i<vergelijkingen.length ; i++)
		{	vergelijkingenNieuw[i] = vergelijkingen[i].substitueer(subst, var);
		}
		return new VergelijkingMeerv(vergelijkingenNieuw);
	}
	
}
