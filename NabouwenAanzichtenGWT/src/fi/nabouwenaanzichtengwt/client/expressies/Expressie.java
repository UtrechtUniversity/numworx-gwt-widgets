package fi.nabouwenaanzichtengwt.client.expressies;

import java.util.*;

public class Expressie 
{	
	public Expressie kind1, kind2;
	public String operatorString;
	boolean isVeelterm;
	boolean isProdukt;
	boolean isBasis;

	public Expressie()
	{	
	}
	public double geefWaarde()
	{	return Double.NaN;
	}
	
	public double geefWaarde(double subst)
	{	return Double.NaN;
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return Double.NaN;
	}
	
	public Expressie substitueer(double subst, String var)
	{	return null;
	}
	
	public boolean isWaarde(double subst)
	{	return true;
	}
	public String geefVarNaam()
	{	return null;
	}
	
	public boolean isGelijkwaardig(Expressie e)
	{	Vector v = (new Optelling(this,e)).geefVarNamen();
		int aantalVars = v.size();
		String[] vars = new String[aantalVars];
		for(int i=0 ; i<aantalVars ; i++)
		{	vars[i] = (String)v.elementAt(i);
		}
		boolean gelijkwaardig = true;
		if(aantalVars<2)
		{	for(int i=-5 ; i<6 ; i++)
			{	boolean nan1 = (Double.isInfinite (geefWaarde(i)) || Double.isNaN(geefWaarde(i)));
				boolean nan2 = (Double.isInfinite (e.geefWaarde(i)) || Double.isNaN(e.geefWaarde(i)));
				if(nan1 && !nan2 || !nan1 &&nan2 || (Math.abs(geefWaarde(i) - e.geefWaarde(i))>0.000000001 && !(nan1 && nan2)))
				{	gelijkwaardig = false;
					break;
				}
			}
		}
		else if(aantalVars==2)
		{	for(int i=-5 ; i<6 ; i++)
			{	for(int j=-5 ; j<6 ; j++)
				{	double[] subst = {i,j};
					if(Math.abs(geefWaarde(subst,vars) - e.geefWaarde(subst,vars))>0.000000001 
					   && !(Double.isNaN(geefWaarde(subst,vars)) && Double.isNaN(e.geefWaarde(subst,vars))))
					//bovenstaande moet nog aangepast worden (+ of - oneindig is niet NaN)
					{	gelijkwaardig = false;
						break;
					}
				}
			}
		}
		else if(aantalVars==3)
		{	for(int i=-5 ; i<6 ; i++)
			{	for(int j=-5 ; j<6 ; j++)
				{	for(int k=-5 ; k<6 ; k++)
					{	double[] subst = {i,j,k};
						if(Math.abs(geefWaarde(subst,vars) - e.geefWaarde(subst,vars))>0.000000001 && !(Double.isNaN(geefWaarde(subst,vars)) && Double.isNaN(e.geefWaarde(subst,vars))))
						{	gelijkwaardig = false;
							break;
						}
					}
				}
			}
		}
		else if(aantalVars==4)
		{	for(int i=-5 ; i<6 ; i++)
			{	for(int j=-5 ; j<6 ; j++)
				{	for(int k=-5 ; k<6 ; k++)
					{	for(int l=-5 ; l<6 ; l++)
						{	double[] subst = {i,j,k,l};
							if(Math.abs(geefWaarde(subst,vars) - e.geefWaarde(subst,vars))>0.000000001 && !(Double.isNaN(geefWaarde(subst,vars)) && Double.isNaN(e.geefWaarde(subst,vars))))
							{	gelijkwaardig = false;
								break;
							}
						}
					}
				}
			}
		}
		else if(aantalVars==5)
		{	for(int i=-5 ; i<6 ; i++)
			{	for(int j=-5 ; j<6 ; j++)
				{	for(int k=-5 ; k<6 ; k++)
					{	for(int l=-5 ; l<6 ; l++)
						{	for(int m=-5 ; m<6 ; m++)
							{	double[] subst = {i,j,k,l,m};
								if(Math.abs(geefWaarde(subst,vars) - e.geefWaarde(subst,vars))>0.000000001 && !(Double.isNaN(geefWaarde(subst,vars)) && Double.isNaN(e.geefWaarde(subst,vars))))
								{	gelijkwaardig = false;
									break;
								}
							}
						}
					}
				}
			}
		}
		else if(aantalVars==6)
		{	for(int i=-5 ; i<6 ; i++)
			{	for(int j=-5 ; j<6 ; j++)
				{	for(int k=-5 ; k<6 ; k++)
					{	for(int l=-5 ; l<6 ; l++)
						{	for(int m=-5 ; m<6 ; m++)
							{	for(int n=-5 ; n<6 ; n++)
								{	double[] subst = {i,j,k,l,m,n};
									if(Math.abs(geefWaarde(subst,vars) - e.geefWaarde(subst,vars))>0.000000001 && !(Double.isNaN(geefWaarde(subst,vars)) && Double.isNaN(e.geefWaarde(subst,vars))))
									{	gelijkwaardig = false;
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		else if(aantalVars>6)gelijkwaardig = false;
		return gelijkwaardig;
		
	}
	
	public static boolean isGelijkwaardigeLinVergelijking(Expressie eLinks1,Expressie eRechts1,Expressie eLinks2,Expressie eRechts2)
	{	Expressie e1 = new Aftrekking(eLinks1,eRechts1);
		Expressie e2 = new Aftrekking(eLinks2,eRechts2);
		double factorA = e1.geefWaarde(0)/e2.geefWaarde(0);
		double factorB = e1.geefWaarde(1)/e2.geefWaarde(1);
		if(Math.abs(factorA - factorB)>0.000000001)return false;
		return true;
	}
	
	public Vector geefVarNamen()
	{	Vector v = null;
		if(kind2 !=null)
		{	Vector v1 = kind1.geefVarNamen();
			Vector v2 = kind2.geefVarNamen();
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
		}
		else 
		{	v=kind1.geefVarNamen();
		}
		return v;
	}
	
	public String toString()
	{	return null;
	}
	public String toStringStrikt()
	{	return null;
	}
	
	public Vector geefTermen(Vector v)
	{	if((this instanceof Optelling || this instanceof Aftrekking)
			&& !
				  (this instanceof Optelling
				   && kind1 instanceof BasisExpressie
				   && !Double.isNaN(kind1.geefWaarde())
				   && kind2 instanceof Deling
				   && kind2.kind1 instanceof BasisExpressie
				   && !Double.isNaN(kind2.kind1.geefWaarde())
				   && kind2.kind2 instanceof BasisExpressie
				   && !Double.isNaN(kind2.kind2.geefWaarde())
					)
				)
		{	
			if(this instanceof Optelling 
			   && (kind2 instanceof Optelling || kind2 instanceof Aftrekking))
			{	v = kind2.geefTermen(v);
			}
			else
			{	if(this instanceof Optelling)
				{	v.insertElementAt(kind2,0);
				}
				else 
				{	Expressie en = new Aftrekking(new BasisExpressie(0),kind2);
					v.insertElementAt(en,0);
				}
			}
			v = kind1.geefTermen(v);
		}
		else
		{	if(!(this instanceof BasisExpressie && this.geefWaarde()==0))v.insertElementAt(this,0);
		}
		return v;
	}
	
	public Vector geefFactoren(Vector v)
	{	if(this instanceof Vermenigvuldiging)
		{	v = kind1.geefFactoren(v);
			v = kind2.geefFactoren(v);
		}
		else
		{	v.addElement(this);
		}
		return v;
	}
	
	public Expressie maakFactorenExpressie(Vector v)
	{	Expressie e = new BasisExpressie(1);
		for(int i=0 ; i<v.size() ; i++)
		{	Expressie ee = (Expressie)v.elementAt(i);
			if(ee instanceof BasisExpressie && !Double.isNaN(ee.geefWaarde()))
			{	e = new BasisExpressie((new Vermenigvuldiging(e,ee)).geefWaarde());
			}
		}
		for(int i=0 ; i<v.size() ; i++)
		{	Expressie ee = (Expressie)v.elementAt(i);
			if(ee instanceof BasisExpressie && Double.isNaN(ee.geefWaarde()))
			{	String varnaam = ((BasisExpressie)ee).basisString;
				int aantalF = 1;
				for(int j=i+1 ; j<v.size() ; j++)
				{	Expressie eee = (Expressie)v.elementAt(j);
					if(eee instanceof BasisExpressie && ((BasisExpressie)eee).basisString.equals(varnaam))
					{	aantalF++;
						v.setElementAt(new BasisExpressie(1),j);
					}
				}
				if(aantalF>1)ee = new Macht(ee,new BasisExpressie(aantalF));	
				if(e.geefWaarde()==1)e = ee;
				else e = new Vermenigvuldiging(e,ee);
			}
		}
		for(int i=0 ; i<v.size() ; i++)
		{	Expressie ee = (Expressie)v.elementAt(i);
			if(!(ee instanceof BasisExpressie))
			{	if(e.geefWaarde()==1)e = ee;
				else e = new Vermenigvuldiging(e,ee);
			}
		}
		return e;
	}
	
	public Vector geefTermenSoortBijSoort(Vector v, String[] varNamen)
	{	Vector w = new Vector();
		double totaalC = 0;
		double totaalX = 0;
		boolean bC = true;
		Expressie eC = new BasisExpressie(0);
		Expressie eCTotaal = new BasisExpressie(0);
		
		int n = varNamen.length;
		boolean[] bX = new boolean[n];
		Expressie[] eX = new Expressie[n];
		Expressie[] eXTotaal = new Expressie[n];
		
		for(int i=0 ; i<n ; i++)
		{	bX[i] = true;
			eX[i] = new BasisExpressie(0);
			eXTotaal[i] = new BasisExpressie(0);
		}
		
		for(int j=0 ; j<v.size() ; j++)
		{	Expressie e = (Expressie)v.elementAt(j);
			if(!Double.isNaN(e.geefWaarde()))
			{	eCTotaal = e.telOp(eCTotaal);
				if(bC)
				{	bC = false;
					w.addElement(eC);
				}
			}
			else if(e instanceof BasisExpressie
					&& e.geefVarNaam()!=null)
			{	for(int i=0 ; i<n ; i++)
				{	if(e.geefVarNaam().equals(varNamen[i]))
					{	eXTotaal[i] = eXTotaal[i].telOp(new BasisExpressie(1));
						if(bX[i])
						{	bX[i] = false;
							w.addElement(eX[i]);
						}
						break;
					}
				}
			}
			else if(e instanceof Vermenigvuldiging 
					&& !Double.isNaN(e.kind1.geefWaarde())
					&& e.kind2 instanceof BasisExpressie)
			{	for(int i=0 ; i<n ; i++)
				{	if(e.kind2.geefVarNaam().equals(varNamen[i]))
					{	eXTotaal[i] = eXTotaal[i].telOp(e.kind1);
						if(bX[i])
						{	bX[i] = false;
							w.addElement(eX[i]);
						}
						break;
					}
				}
			}
			else if(e instanceof Aftrekking
					&& e.kind1.geefWaarde()==0
					&& e.kind2 instanceof BasisExpressie 
					&& e.kind2.geefVarNaam()!=null)
			{	for(int i=0 ; i<n ; i++)
				{	if(e.kind2.geefVarNaam().equals(varNamen[i]))
					{	eXTotaal[i] = eXTotaal[i].telOp(new BasisExpressie(-1));
						if(bX[i])
						{	bX[i] = false;
							w.addElement(eX[i]);
						}
						break;
					}
				}
			}
			else if(e instanceof Aftrekking
					&& e.kind1.geefWaarde()==0
					&& e.kind2 instanceof Vermenigvuldiging 
					&& !Double.isNaN(e.kind2.kind1.geefWaarde())
					&& e.kind2.kind2 instanceof BasisExpressie)
			{	for(int i=0 ; i<n ; i++)
				{	if(e.kind2.kind2.geefVarNaam().equals(varNamen[i]))
					{	eXTotaal[i] = eXTotaal[i].telOp(new Aftrekking(new BasisExpressie(0),e.kind2.kind1));
						if(bX[i])
						{	bX[i] = false;
							w.addElement(eX[i]);
						}
						break;
					}
				}
			}
			else
			{	w.addElement(e);
			}
		}
		if(eCTotaal.geefWaarde()==0)
		{	w.removeElement(eC);
		}
		else
		{	w.setElementAt(eCTotaal,w.indexOf(eC));
		}
		
		for(int i=0 ; i<n ; i++)
		{	if(eXTotaal[i].geefWaarde()==0)
			{	w.removeElement(eX[i]);
			}
			else if(eXTotaal[i].geefWaarde()==1)
			{	Expressie exp = new BasisExpressie(varNamen[i]);
				w.setElementAt(exp,w.indexOf(eX[i]));
			}
			else if(eXTotaal[i].geefWaarde()==-1)
			{	Expressie exp = new Aftrekking(new BasisExpressie(0),new BasisExpressie(varNamen[i]));
				w.setElementAt(exp,w.indexOf(eX[i]));
			}
			else if(eXTotaal[i].geefWaarde()>0)
			{	Expressie exp = new Vermenigvuldiging(eXTotaal[i],new BasisExpressie(varNamen[i]));
				w.setElementAt(exp,w.indexOf(eX[i]));
			}
			else if(eXTotaal[i].geefWaarde()<0)
			{	Expressie exp = new Aftrekking(new BasisExpressie(0),new Vermenigvuldiging(eXTotaal[i].kind2,new BasisExpressie(varNamen[i])));
				w.setElementAt(exp,w.indexOf(eX[i]));
			}
		}
		return w;
	}
	
	public Vector vermenigvuldigTermen(Expressie factor,Vector v )
	{	Vector w = new Vector();
		boolean factorIsGetal = false;
		if(!Double.isNaN(factor.geefWaarde()))factorIsGetal = true;
		if(factorIsGetal)
		{	for(int i=0 ; i<v.size() ; i++)
			{	Expressie e = (Expressie)v.elementAt(i);
				if(!Double.isNaN(e.geefWaarde()))
				{	Expressie nieuweWaarde = factor.vermenigvuldigExpressie(e);
					if(nieuweWaarde.geefWaarde()>0)
					{	Expressie exp = nieuweWaarde;
						w.addElement(exp);
					}
					else if(nieuweWaarde.geefWaarde()<0)
					{	Expressie exp = new Aftrekking(new BasisExpressie(0),nieuweWaarde.kind2);
						w.addElement(exp);
					}
				}
				else if(e instanceof BasisExpressie)
				{	Expressie nieuweWaarde = factor.vermenigvuldigExpressie(new BasisExpressie(1));
					
					if(nieuweWaarde.geefWaarde()>0)
					{	Expressie exp = new Vermenigvuldiging(nieuweWaarde,e);
						if(nieuweWaarde.geefWaarde()==1)exp = e;
						w.addElement(exp);
					}
					else if(nieuweWaarde.geefWaarde()<0)
					{	Expressie exp = new Aftrekking(new BasisExpressie(0),new Vermenigvuldiging(nieuweWaarde.kind2,e));
						if(nieuweWaarde.geefWaarde()==-1)exp = new Aftrekking(new BasisExpressie(0),e);
						w.addElement(exp);
					}
				}
				else if(e instanceof Vermenigvuldiging 	&& !Double.isNaN(e.kind1.geefWaarde()))
				{	Expressie nieuweWaarde = factor.vermenigvuldigExpressie(e.kind1);
					
					if(nieuweWaarde.geefWaarde()>0)
					{	Expressie exp = new Vermenigvuldiging(nieuweWaarde,e.kind2);
						if(nieuweWaarde.geefWaarde()==1)exp = e.kind2;
						w.addElement(exp);
					}
					else if(nieuweWaarde.geefWaarde()<0)
					{	Expressie exp = new Aftrekking(new BasisExpressie(0),new Vermenigvuldiging(nieuweWaarde.kind2,e.kind2));
						if(nieuweWaarde.geefWaarde()==-1)exp = new Aftrekking(new BasisExpressie(0),e.kind2);
						w.addElement(exp);
					}
				}
				else if(e instanceof Deling	&& !Double.isNaN(e.kind2.geefWaarde()))
				{	Expressie nieuweWaarde = factor.vermenigvuldigExpressie(new Deling(new BasisExpressie(1),e.kind2));
					
					if(nieuweWaarde.geefWaarde()>0)
					{	Expressie exp = new Vermenigvuldiging(nieuweWaarde,e.kind1);
						if(nieuweWaarde.geefWaarde()==1)exp = e.kind1;
						w.addElement(exp);
					}
					else if(nieuweWaarde.geefWaarde()<0)
					{	Expressie exp = new Aftrekking(new BasisExpressie(0),new Vermenigvuldiging(nieuweWaarde.kind2,e.kind1));
						if(nieuweWaarde.geefWaarde()==-1)exp = new Aftrekking(new BasisExpressie(0),e.kind1);
						w.addElement(exp);
					}
				}
				else if(e instanceof Aftrekking
						&& e.kind1.geefWaarde()==0
						&& e.kind2 instanceof BasisExpressie) 
					{	Expressie nieuweWaarde = factor.vermenigvuldigExpressie(new Aftrekking(new BasisExpressie(0),new BasisExpressie(1)));
					
					if(nieuweWaarde.geefWaarde()>0)
					{	Expressie exp = new Vermenigvuldiging(nieuweWaarde,e.kind2);
						if(nieuweWaarde.geefWaarde()==1)exp = e.kind2;
						w.addElement(exp);
					}
					else if(nieuweWaarde.geefWaarde()<0)
					{	Expressie exp = new Aftrekking(new BasisExpressie(0),new Vermenigvuldiging(nieuweWaarde.kind2,e.kind2));
						if(nieuweWaarde.geefWaarde()==-1)exp = new Aftrekking(new BasisExpressie(0),e.kind2);
						w.addElement(exp);
					}
				}
				else if(e instanceof Aftrekking
						&& e.kind1.geefWaarde()==0
						&& e.kind2 instanceof Vermenigvuldiging 
						&& !Double.isNaN(e.kind2.kind1.geefWaarde()))
				{	Expressie nieuweWaarde = factor.vermenigvuldigExpressie(new Aftrekking(new BasisExpressie(0),e.kind2.kind1));
					
					if(nieuweWaarde.geefWaarde()>0)
					{	Expressie exp = new Vermenigvuldiging(nieuweWaarde,e.kind2.kind2);
						if(nieuweWaarde.geefWaarde()==1)exp = e.kind2.kind2;
						w.addElement(exp);
					}
					else if(nieuweWaarde.geefWaarde()<0)
					{	Expressie exp = new Aftrekking(new BasisExpressie(0),new Vermenigvuldiging(nieuweWaarde.kind2,e.kind2.kind2));
						if(nieuweWaarde.geefWaarde()==-1)exp = new Aftrekking(new BasisExpressie(0),e.kind2.kind2);
						w.addElement(exp);
					}
				}
				else if(e instanceof Aftrekking
						&& e.kind1.geefWaarde()==0
						&& e.kind2 instanceof Deling 
						&& !Double.isNaN(e.kind2.kind2.geefWaarde()))
				{	Expressie nieuweWaarde = factor.vermenigvuldigExpressie(new Deling(new BasisExpressie(1),e.kind2.kind2));
					
					if(nieuweWaarde.geefWaarde()>0)
					{	Expressie exp = new Vermenigvuldiging(nieuweWaarde,e.kind2.kind1);
						if(nieuweWaarde.geefWaarde()==1)exp = e.kind2.kind1;
						w.addElement(exp);
					}
					else if(nieuweWaarde.geefWaarde()<0)
					{	Expressie exp = new Aftrekking(new BasisExpressie(0),new Vermenigvuldiging(nieuweWaarde.kind2,e.kind2.kind1));
						if(nieuweWaarde.geefWaarde()==-1)exp = new Aftrekking(new BasisExpressie(0),e.kind2.kind1);
						w.addElement(exp);
					}
				}/**/
				else if(e instanceof Aftrekking	&& e.kind1.geefWaarde()==0)
				{	Expressie nieuweWaarde = factor;
					if(nieuweWaarde.geefWaarde()>0)
					{	Expressie exp = new Aftrekking(new BasisExpressie(0),new Vermenigvuldiging(nieuweWaarde,e.kind2));
						if(nieuweWaarde.geefWaarde()==1)exp = new Aftrekking(new BasisExpressie(0),e.kind2);
						w.addElement(exp);
					}
					else if(nieuweWaarde.geefWaarde()<0)
					{	Expressie exp = new Vermenigvuldiging(nieuweWaarde.kind2,e.kind2);
						if(nieuweWaarde.geefWaarde()==-1)exp = e.kind2;
						w.addElement(exp);
					}
						
				}
				else 
				{	Expressie nieuweWaarde = factor;
					if(nieuweWaarde.geefWaarde()<0)
					{	Expressie exp = new Aftrekking(new BasisExpressie(0),new Vermenigvuldiging(nieuweWaarde.kind2,e));
						if(nieuweWaarde.geefWaarde()==-1)exp = new Aftrekking(new BasisExpressie(0),e);
						
						w.addElement(exp);
					}
					else if(nieuweWaarde.geefWaarde()>0)
					{	Expressie exp = new Vermenigvuldiging(nieuweWaarde,e);
						if(nieuweWaarde.geefWaarde()==1)exp = e;
						w.addElement(exp);
					}	
				}
			}
		}
		return w;
	}
	public Expressie maakTermenExpressie(Vector v)
	{	Expressie e = null;
		if(v.size()>0)e = (Expressie)v.elementAt(0);
		else return new BasisExpressie(0);
		for(int i=1 ; i<v.size() ; i++)
		{	Expressie ee = (Expressie)v.elementAt(i);
			if(ee instanceof Aftrekking && ee.kind1.geefWaarde()==0)
			{	e = new Aftrekking(e,ee.kind2);
			}
			else
			{	e = new Optelling(e,ee);
			}
		}
		return e;
	}
	
	public Expressie herleid()
	{	Vector varn = geefVarNamen();
		String[] varNamen = new String[varn.size()];
		for(int i=0 ; i<varn.size() ; i++)
		{	varNamen[i] = (String)varn.elementAt(i);
		}
		Vector v = geefTermen(new Vector());
		v = geefTermenSoortBijSoort(v,varNamen);
		return maakTermenExpressie(v);
	}
	
	public Expressie vermenigvuldig(Expressie factor)
	{	Vector v = geefTermen(new Vector());
		v = vermenigvuldigTermen(factor,v);
		return maakTermenExpressie(v);
	}
	
	public Expressie verwijderHaakjes()
	{	Vector v = geefTermen(new Vector());
		Vector w = new Vector();
		for(int i=0 ; i<v.size() ; i++)
		{	Expressie e = (Expressie)v.elementAt(i);
			if(e instanceof Vermenigvuldiging && !Double.isNaN(e.kind1.geefWaarde()))
			{	Vector u = e.kind2.geefTermen(new Vector());
				u = vermenigvuldigTermen(e.kind1,u);
				for(int j=0 ; j<u.size() ; j++)
				{	w.addElement(u.elementAt(j));
				}
			}
			else if(e instanceof Deling && !Double.isNaN(e.kind2.geefWaarde()))
			{	Vector u = e.kind1.geefTermen(new Vector());
				u = vermenigvuldigTermen(new Deling(new BasisExpressie(1),e.kind2),u);
				for(int j=0 ; j<u.size() ; j++)
				{	w.addElement(u.elementAt(j));
				}
			}
			else if(e instanceof Aftrekking
					&& e.kind1.geefWaarde()==0
					&& e.kind2 instanceof Vermenigvuldiging 
					&& !Double.isNaN(e.kind2.kind1.geefWaarde()))
			{	Vector u = e.kind2.kind2.geefTermen(new Vector());
				Expressie factor = new Aftrekking(new BasisExpressie(0),e.kind2.kind1);
				u = vermenigvuldigTermen(factor,u);
				for(int j=0 ; j<u.size() ; j++)
				{	w.addElement(u.elementAt(j));
				}
			}
			else if(e instanceof Aftrekking
					&& e.kind1.geefWaarde()==0
					&& e.kind2 instanceof Deling 
					&& !Double.isNaN(e.kind2.kind2.geefWaarde()))
			{	Vector u = e.kind2.kind1.geefTermen(new Vector());
				Expressie factor = new Aftrekking(new BasisExpressie(0),new Deling(new BasisExpressie(1),e.kind2.kind2));
				u = vermenigvuldigTermen(factor,u);
				for(int j=0 ; j<u.size() ; j++)
				{	w.addElement(u.elementAt(j));
				}
			}
			else if(e instanceof Aftrekking
					&& e.kind1.geefWaarde()==0
					&& (e.kind2 instanceof Optelling || e.kind2 instanceof Aftrekking))
			{	Vector u = e.kind2.geefTermen(new Vector());
				Expressie factor = new BasisExpressie(-1);
				u = vermenigvuldigTermen(factor,u);
				for(int j=0 ; j<u.size() ; j++)
				{	w.addElement(u.elementAt(j));
				}
			}
			else
			{	w.addElement(e);
			}
		}
		return maakTermenExpressie(w);
	}
	
	public Expressie telOp(Expressie e)
	{	Expressie exp = null;
		boolean basis1 = false;
		boolean breuk1 = false;
		boolean breukPlus1 = false;
		boolean minBasis1 = false;
		boolean minBreuk1 = false;
		boolean minBreukPlus1 = false;
		boolean basis2 = false;
		boolean breuk2 = false;
		boolean breukPlus2 = false;
		boolean minBasis2 = false;
		boolean minBreuk2 = false;
		boolean minBreukPlus2 = false;
		int teller1 = 0;
		int teller2 = 0;
		int noemer1 = 1;
		int noemer2 = 1;
		if(this instanceof BasisExpressie
			&& geefWaarde() - (int)(geefWaarde())==0)
		{	basis1 = true;
			teller1 = (int)geefWaarde();
		}
		else if(this instanceof Deling
		   && kind1 instanceof BasisExpressie
		   && kind1.geefWaarde() - (int)(kind1.geefWaarde())==0
		   && kind2 instanceof BasisExpressie
		   && kind2.geefWaarde() - (int)(kind2.geefWaarde())==0)
		{	breuk1 = true;
			teller1 = (int)kind1.geefWaarde();
			noemer1 = (int)kind2.geefWaarde();
		}
		else if(this instanceof Optelling
		   && kind1 instanceof BasisExpressie
		   && kind1.geefWaarde() - (int)(kind1.geefWaarde())==0
		   && kind2 instanceof Deling
		   && kind2.kind1 instanceof BasisExpressie
		   && kind2.kind1.geefWaarde() - (int)(kind2.kind1.geefWaarde())==0
		   && kind2.kind2 instanceof BasisExpressie
		   && kind2.kind2.geefWaarde() - (int)(kind2.kind2.geefWaarde())==0)
		{	breukPlus1 = true;
			teller1 = (int)kind1.geefWaarde() * (int)kind2.kind2.geefWaarde() + (int)kind2.kind1.geefWaarde();
			noemer1 = (int)kind2.kind2.geefWaarde();
		}
		else if(this instanceof Aftrekking
		   && kind1 instanceof BasisExpressie
		   && kind1.geefWaarde()==0
		   && kind2 instanceof BasisExpressie
		   && kind2.geefWaarde() - (int)(kind2.geefWaarde())==0)
		{	minBasis1 = true;
			teller1 = -(int)kind2.geefWaarde();
		}
		else if(this instanceof Aftrekking
		   && kind1 instanceof BasisExpressie
		   && kind1.geefWaarde()==0
		   && kind2 instanceof Deling
		   && kind2.kind1 instanceof BasisExpressie
		   && kind2.kind1.geefWaarde() - (int)(kind2.kind1.geefWaarde())==0
		   && kind2.kind2 instanceof BasisExpressie
		   && kind2.kind2.geefWaarde() - (int)(kind2.kind2.geefWaarde())==0)
		{	minBreuk1 = true;
			teller1 = -(int)kind2.kind1.geefWaarde();
			noemer1 = (int)kind2.kind2.geefWaarde();
		}
		else if(this instanceof Aftrekking
		   && kind1 instanceof BasisExpressie
		   && kind1.geefWaarde()==0
		   && kind2 instanceof Optelling
		   && kind2.kind1 instanceof BasisExpressie
		   && kind2.kind1.geefWaarde() - (int)(kind2.kind1.geefWaarde())==0
		   && kind2.kind2 instanceof Deling
		   && kind2.kind2.kind1 instanceof BasisExpressie
		   && kind2.kind2.kind1.geefWaarde() - (int)(kind2.kind2.kind1.geefWaarde())==0
		   && kind2.kind2.kind2 instanceof BasisExpressie
		   && kind2.kind2.kind2.geefWaarde() - (int)(kind2.kind2.kind2.geefWaarde())==0)
		{	minBreukPlus1 = true;
			teller1 = -(int)kind2.kind1.geefWaarde() * (int)kind2.kind2.kind2.geefWaarde() - (int)kind2.kind2.kind1.geefWaarde();
			noemer1 = (int)kind2.kind2.kind2.geefWaarde();
		}
		
		if(e instanceof BasisExpressie
			&& e.geefWaarde() - (int)(e.geefWaarde())==0)
		{	basis2 = true;
			teller2 = (int)e.geefWaarde();
		}
		else if(e instanceof Deling
		   && e.kind1 instanceof BasisExpressie
		   && e.kind1.geefWaarde() - (int)(e.kind1.geefWaarde())==0
		   && e.kind2 instanceof BasisExpressie
		   && e.kind2.geefWaarde() - (int)(e.kind2.geefWaarde())==0)
		{	breuk2 = true;
			teller2 = (int)e.kind1.geefWaarde();
			noemer2 = (int)e.kind2.geefWaarde();
		}
		else if(e instanceof Optelling
		   && e.kind1 instanceof BasisExpressie
		   && e.kind1.geefWaarde() - (int)(e.kind1.geefWaarde())==0
		   && e.kind2 instanceof Deling
		   && e.kind2.kind1 instanceof BasisExpressie
		   && e.kind2.kind1.geefWaarde() - (int)(e.kind2.kind1.geefWaarde())==0
		   && e.kind2.kind2 instanceof BasisExpressie
		   && e.kind2.kind2.geefWaarde() - (int)(e.kind2.kind2.geefWaarde())==0)
		{	breukPlus2 = true;
			teller2 = (int)e.kind1.geefWaarde() * (int)e.kind2.kind2.geefWaarde() + (int)e.kind2.kind1.geefWaarde();
			noemer2 = (int)e.kind2.kind2.geefWaarde();
		}
		else if(e instanceof Aftrekking
		   && e.kind1 instanceof BasisExpressie
		   && e.kind1.geefWaarde()==0
		   && e.kind2 instanceof BasisExpressie
		   && e.kind2.geefWaarde() - (int)(e.kind2.geefWaarde())==0)
		{	minBasis2 = true;
			teller2 = -(int)e.kind2.geefWaarde();
		}
		else if(e instanceof Aftrekking
		   && e.kind1 instanceof BasisExpressie
		   && e.kind1.geefWaarde()==0
		   && e.kind2 instanceof Deling
		   && e.kind2.kind1 instanceof BasisExpressie
		   && e.kind2.kind1.geefWaarde() - (int)(e.kind2.kind1.geefWaarde())==0
		   && e.kind2.kind2 instanceof BasisExpressie
		   && e.kind2.kind2.geefWaarde() - (int)(e.kind2.kind2.geefWaarde())==0)
		{	minBreuk2 = true;
			teller2 = -(int)e.kind2.kind1.geefWaarde();
			noemer2 = (int)e.kind2.kind2.geefWaarde();
		}
		else if(e instanceof Aftrekking
		   && e.kind1 instanceof BasisExpressie
		   && e.kind1.geefWaarde()==0
		   && e.kind2 instanceof Optelling
		   && e.kind2.kind1 instanceof BasisExpressie
		   && e.kind2.kind1.geefWaarde() - (int)(e.kind2.kind1.geefWaarde())==0
		   && e.kind2.kind2 instanceof Deling
		   && e.kind2.kind2.kind1 instanceof BasisExpressie
		   && e.kind2.kind2.kind1.geefWaarde() - (int)(e.kind2.kind2.kind1.geefWaarde())==0
		   && e.kind2.kind2.kind2 instanceof BasisExpressie
		   && e.kind2.kind2.kind2.geefWaarde() - (int)(e.kind2.kind2.kind2.geefWaarde())==0)
		{	minBreukPlus2 = true;
			teller2 = -(int)e.kind2.kind1.geefWaarde() * (int)e.kind2.kind2.kind2.geefWaarde() - (int)e.kind2.kind2.kind1.geefWaarde();
			noemer2 = (int)e.kind2.kind2.kind2.geefWaarde();
		}
		
		if((basis1 || breuk1 || breukPlus1 || minBasis1 || minBreuk1 || minBreukPlus1)
		   && (basis2 || breuk2 || breukPlus2 || minBasis2 || minBreuk2 || minBreukPlus2))
		{	int kgv = 0;
			for(int i=0 ; i<noemer2 ; i++)
			{	kgv = kgv + noemer1;
				if(kgv%noemer2==0)break;
			}
			teller1 = teller1*kgv/noemer1;
			teller2 = teller2*kgv/noemer2;
			
			int teller = teller1 + teller2;
			int noemer = kgv;
			
			int ggd = 1;
			for(int i=1 ; i<noemer ; i++)
			{	if(teller%i==0 && noemer%i==0)
				{	ggd = i;
				}
			}
			teller = teller/ggd;
			noemer = noemer/ggd;
			
			if(teller>0 && (int)Math.abs(teller)<(int)Math.abs(noemer))
			{	exp = new Deling(new BasisExpressie(teller),new BasisExpressie(noemer));
			}
			else if(teller>0 && (int)Math.abs(teller)>(int)Math.abs(noemer))
			{	int helen = teller/noemer;
				int delen = teller%noemer;
				exp = new Optelling(new BasisExpressie(helen),new Deling(new BasisExpressie(delen),new BasisExpressie(noemer)));
				if(delen==0)exp = new BasisExpressie(helen);
			}
			else if(teller<0 && (int)Math.abs(teller)<(int)Math.abs(noemer))
			{	exp = new Aftrekking(new BasisExpressie(0),new Deling(new BasisExpressie(-teller),new BasisExpressie(noemer)));
			}
			else if(teller<0 && (int)Math.abs(teller)>(int)Math.abs(noemer))
			{	int helen = (int)Math.abs(teller)/(int)Math.abs(noemer);
				int delen = (int)Math.abs(teller)%(int)Math.abs(noemer);
				exp = new Aftrekking(new BasisExpressie(0),new Optelling(new BasisExpressie(helen),new Deling(new BasisExpressie(delen),new BasisExpressie(noemer))));
				if(delen==0)exp = new Aftrekking(new BasisExpressie(0),new BasisExpressie(helen));
			}
			else if(teller==0)
			{	exp = new BasisExpressie(0);
			}
			else if(teller==noemer)
			{	exp = new BasisExpressie(1);
			}
			else if(teller==-noemer)
			{	exp = new Aftrekking(new BasisExpressie(0),new BasisExpressie(1));
			}
		}
		else
		{	double waarde = geefWaarde()+e.geefWaarde();
			if(waarde>=0)exp = new BasisExpressie(waarde);
			else if(waarde<0)exp = new Aftrekking(new BasisExpressie(0),new BasisExpressie(-waarde));
		}
		return exp;
	}
	
	public Expressie vermenigvuldigExpressie(Expressie e)
	{	Expressie exp = null;
		boolean basis1 = false;
		boolean breuk1 = false;
		boolean breukPlus1 = false;
		boolean minBasis1 = false;
		boolean minBreuk1 = false;
		boolean minBreukPlus1 = false;
		boolean deelMinBasis1 = false;
		boolean deelBreuk1 = false;
		boolean deelMinBreuk1 = false;
		boolean deelBreukPlus1 = false;
		boolean deelMinBreukPlus1 = false;
		boolean basis2 = false;
		boolean breuk2 = false;
		boolean breukPlus2 = false;
		boolean minBasis2 = false;
		boolean minBreuk2 = false;
		boolean minBreukPlus2 = false;
		int teller1 = 0;
		int teller2 = 0;
		int noemer1 = 1;
		int noemer2 = 1;
		if(this instanceof BasisExpressie
			&& geefWaarde() - (int)(geefWaarde())==0)
		{	basis1 = true;
			teller1 = (int)geefWaarde();
		}
		else if(this instanceof Deling
		   && kind1 instanceof BasisExpressie
		   && kind1.geefWaarde() - (int)(kind1.geefWaarde())==0
		   && kind2 instanceof BasisExpressie
		   && kind2.geefWaarde() - (int)(kind2.geefWaarde())==0)
		{	breuk1 = true;
			teller1 = (int)kind1.geefWaarde();
			noemer1 = (int)kind2.geefWaarde();
		}
		else if(this instanceof Optelling
		   && kind1 instanceof BasisExpressie
		   && kind1.geefWaarde() - (int)(kind1.geefWaarde())==0
		   && kind2 instanceof Deling
		   && kind2.kind1 instanceof BasisExpressie
		   && kind2.kind1.geefWaarde() - (int)(kind2.kind1.geefWaarde())==0
		   && kind2.kind2 instanceof BasisExpressie
		   && kind2.kind2.geefWaarde() - (int)(kind2.kind2.geefWaarde())==0)
		{	breukPlus1 = true;
			teller1 = (int)kind1.geefWaarde() * (int)kind2.kind2.geefWaarde() + (int)kind2.kind1.geefWaarde();
			noemer1 = (int)kind2.kind2.geefWaarde();
		}
		else if(this instanceof Aftrekking
		   && kind1 instanceof BasisExpressie
		   && kind1.geefWaarde()==0
		   && kind2 instanceof BasisExpressie
		   && kind2.geefWaarde() - (int)(kind2.geefWaarde())==0)
		{	minBasis1 = true;
			teller1 = -(int)kind2.geefWaarde();
		}
		else if(this instanceof Aftrekking
		   && kind1 instanceof BasisExpressie
		   && kind1.geefWaarde()==0
		   && kind2 instanceof Deling
		   && kind2.kind1 instanceof BasisExpressie
		   && kind2.kind1.geefWaarde() - (int)(kind2.kind1.geefWaarde())==0
		   && kind2.kind2 instanceof BasisExpressie
		   && kind2.kind2.geefWaarde() - (int)(kind2.kind2.geefWaarde())==0)
		{	minBreuk1 = true;
			teller1 = -(int)kind2.kind1.geefWaarde();
			noemer1 = (int)kind2.kind2.geefWaarde();
		}
		else if(this instanceof Aftrekking
		   && kind1 instanceof BasisExpressie
		   && kind1.geefWaarde()==0
		   && kind2 instanceof Optelling
		   && kind2.kind1 instanceof BasisExpressie
		   && kind2.kind1.geefWaarde() - (int)(kind2.kind1.geefWaarde())==0
		   && kind2.kind2 instanceof Deling
		   && kind2.kind2.kind1 instanceof BasisExpressie
		   && kind2.kind2.kind1.geefWaarde() - (int)(kind2.kind2.kind1.geefWaarde())==0
		   && kind2.kind2.kind2 instanceof BasisExpressie
		   && kind2.kind2.kind2.geefWaarde() - (int)(kind2.kind2.kind2.geefWaarde())==0)
		{	minBreukPlus1 = true;
			teller1 = -(int)kind2.kind1.geefWaarde() * (int)kind2.kind2.kind2.geefWaarde() - (int)kind2.kind2.kind1.geefWaarde();
			noemer1 = (int)kind2.kind2.kind2.geefWaarde();
		}
		else if(this instanceof Deling
				&& kind1.geefWaarde()==1
				&& kind2 instanceof Aftrekking
				&& kind2.kind1.geefWaarde()==0
				&& kind2.kind2 instanceof BasisExpressie
				&& kind2.kind2.geefWaarde() - (int)(kind2.kind2.geefWaarde())==0)
		{	deelMinBasis1 = true;
			teller1 = -1;
			noemer1 = (int)kind2.kind2.geefWaarde();
		}
		else if(this instanceof Deling
				&& kind1.geefWaarde()==1
				&& kind2 instanceof Deling
				&& kind2.kind1 instanceof BasisExpressie
				&& kind2.kind1.geefWaarde() - (int)(kind2.kind1.geefWaarde())==0
				&& kind2.kind2 instanceof BasisExpressie
				&& kind2.kind2.geefWaarde() - (int)(kind2.kind2.geefWaarde())==0)
		{	deelBreuk1 = true;
			teller1 = (int)kind2.kind2.geefWaarde();
			noemer1 = (int)kind2.kind1.geefWaarde();
		}
		else if(this instanceof Deling
				&& kind1.geefWaarde()==1
				&& kind2 instanceof Aftrekking
				&& kind2.kind1.geefWaarde()==0
				&& kind2.kind2 instanceof Deling
				&& kind2.kind2.kind1 instanceof BasisExpressie
				&& kind2.kind2.kind1.geefWaarde() - (int)(kind2.kind2.kind1.geefWaarde())==0
				&& kind2.kind2.kind2 instanceof BasisExpressie
				&& kind2.kind2.kind2.geefWaarde() - (int)(kind2.kind2.kind2.geefWaarde())==0)
		{	deelMinBreuk1 = true;
			teller1 = -(int)kind2.kind2.kind2.geefWaarde();
			noemer1 = (int)kind2.kind2.kind1.geefWaarde();
		}
		else if(this instanceof Deling
				&& kind1.geefWaarde()==1
				&& kind2 instanceof Optelling
				&& kind2.kind1 instanceof BasisExpressie
				&& kind2.kind1.geefWaarde() - (int)(kind2.kind1.geefWaarde())==0
				&& kind2.kind2 instanceof Deling
				&& kind2.kind2.kind1 instanceof BasisExpressie
				&& kind2.kind2.kind1.geefWaarde() - (int)(kind2.kind2.kind1.geefWaarde())==0
				&& kind2.kind2.kind2 instanceof BasisExpressie
				&& kind2.kind2.kind2.geefWaarde() - (int)(kind2.kind2.kind2.geefWaarde())==0)
		{	deelBreukPlus1 = true;
			teller1 = (int)kind2.kind2.kind2.geefWaarde();
			noemer1 = (int)kind2.kind1.geefWaarde() * (int)kind2.kind2.kind2.geefWaarde() + (int)kind2.kind2.kind1.geefWaarde();
		}
		else if(this instanceof Deling
				&& kind1.geefWaarde()==1
				&& kind2 instanceof Aftrekking
				&& kind2.kind1.geefWaarde()==0
				&& kind2.kind2 instanceof Optelling
				&& kind2.kind2.kind1 instanceof BasisExpressie
				&& kind2.kind2.kind1.geefWaarde() - (int)(kind2.kind2.kind1.geefWaarde())==0
				&& kind2.kind2.kind2 instanceof Deling
				&& kind2.kind2.kind2.kind1 instanceof BasisExpressie
				&& kind2.kind2.kind2.kind1.geefWaarde() - (int)(kind2.kind2.kind2.kind1.geefWaarde())==0
				&& kind2.kind2.kind2.kind2 instanceof BasisExpressie
				&& kind2.kind2.kind2.kind2.geefWaarde() - (int)(kind2.kind2.kind2.kind2.geefWaarde())==0)
		{	deelMinBreukPlus1 = true;
			teller1 = -(int)kind2.kind2.kind2.kind2.geefWaarde();
			noemer1 = (int)kind2.kind2.kind1.geefWaarde() * (int)kind2.kind2.kind2.kind2.geefWaarde() + (int)kind2.kind2.kind2.kind1.geefWaarde();
		}
		
		if(e instanceof BasisExpressie
			&& e.geefWaarde() - (int)(e.geefWaarde())==0)
		{	basis2 = true;
			teller2 = (int)e.geefWaarde();
		}
		else if(e instanceof Deling
		   && e.kind1 instanceof BasisExpressie
		   && e.kind1.geefWaarde() - (int)(e.kind1.geefWaarde())==0
		   && e.kind2 instanceof BasisExpressie
		   && e.kind2.geefWaarde() - (int)(e.kind2.geefWaarde())==0)
		{	breuk2 = true;
			teller2 = (int)e.kind1.geefWaarde();
			noemer2 = (int)e.kind2.geefWaarde();
		}
		else if(e instanceof Optelling
		   && e.kind1 instanceof BasisExpressie
		   && e.kind1.geefWaarde() - (int)(e.kind1.geefWaarde())==0
		   && e.kind2 instanceof Deling
		   && e.kind2.kind1 instanceof BasisExpressie
		   && e.kind2.kind1.geefWaarde() - (int)(e.kind2.kind1.geefWaarde())==0
		   && e.kind2.kind2 instanceof BasisExpressie
		   && e.kind2.kind2.geefWaarde() - (int)(e.kind2.kind2.geefWaarde())==0)
		{	breukPlus2 = true;
			teller2 = (int)e.kind1.geefWaarde() * (int)e.kind2.kind2.geefWaarde() + (int)e.kind2.kind1.geefWaarde();
			noemer2 = (int)e.kind2.kind2.geefWaarde();
		}
		else if(e instanceof Aftrekking
		   && e.kind1 instanceof BasisExpressie
		   && e.kind1.geefWaarde()==0
		   && e.kind2 instanceof BasisExpressie
		   && e.kind2.geefWaarde() - (int)(e.kind2.geefWaarde())==0)
		{	minBasis2 = true;
			teller2 = -(int)e.kind2.geefWaarde();
		}
		else if(e instanceof Aftrekking
		   && e.kind1 instanceof BasisExpressie
		   && e.kind1.geefWaarde()==0
		   && e.kind2 instanceof Deling
		   && e.kind2.kind1 instanceof BasisExpressie
		   && e.kind2.kind1.geefWaarde() - (int)(e.kind2.kind1.geefWaarde())==0
		   && e.kind2.kind2 instanceof BasisExpressie
		   && e.kind2.kind2.geefWaarde() - (int)(e.kind2.kind2.geefWaarde())==0)
		{	minBreuk2 = true;
			teller2 = -(int)e.kind2.kind1.geefWaarde();
			noemer2 = (int)e.kind2.kind2.geefWaarde();
		}
		else if(e instanceof Aftrekking
		   && e.kind1 instanceof BasisExpressie
		   && e.kind1.geefWaarde()==0
		   && e.kind2 instanceof Optelling
		   && e.kind2.kind1 instanceof BasisExpressie
		   && e.kind2.kind1.geefWaarde() - (int)(e.kind2.kind1.geefWaarde())==0
		   && e.kind2.kind2 instanceof Deling
		   && e.kind2.kind2.kind1 instanceof BasisExpressie
		   && e.kind2.kind2.kind1.geefWaarde() - (int)(e.kind2.kind2.kind1.geefWaarde())==0
		   && e.kind2.kind2.kind2 instanceof BasisExpressie
		   && e.kind2.kind2.kind2.geefWaarde() - (int)(e.kind2.kind2.kind2.geefWaarde())==0)
		{	minBreukPlus2 = true;
			teller2 = -(int)e.kind2.kind1.geefWaarde() * (int)e.kind2.kind2.kind2.geefWaarde() - (int)e.kind2.kind2.kind1.geefWaarde();
			noemer2 = (int)e.kind2.kind2.kind2.geefWaarde();
		}
		
		if((basis1 || breuk1 || breukPlus1 || minBasis1 || minBreuk1 || minBreukPlus1 || deelMinBasis1 ||deelBreuk1 || deelBreukPlus1 || deelMinBreuk1 || deelMinBreukPlus1)
		   && (basis2 || breuk2 || breukPlus2 || minBasis2 || minBreuk2 || minBreukPlus2))
		{	int teller = teller1 * teller2;
			int noemer = noemer1 * noemer2;
			
			int ggd = 1;
			for(int i=1 ; i<noemer ; i++)
			{	if(teller%i==0 && noemer%i==0)
				{	ggd = i;
				}
			}
			teller = teller/ggd;
			noemer = noemer/ggd;
			
			if(teller>0 && (int)Math.abs(teller)<(int)Math.abs(noemer))
			{	exp = new Deling(new BasisExpressie(teller),new BasisExpressie(noemer));
			}
			else if(teller>0 && (int)Math.abs(teller)>(int)Math.abs(noemer))
			{	int helen = teller/noemer;
				int delen = teller%noemer;
				exp = new Optelling(new BasisExpressie(helen),new Deling(new BasisExpressie(delen),new BasisExpressie(noemer)));
				if(delen==0)exp = new BasisExpressie(helen);
			}
			else if(teller<0 && (int)Math.abs(teller)<(int)Math.abs(noemer))
			{	exp = new Aftrekking(new BasisExpressie(0),new Deling(new BasisExpressie(-teller),new BasisExpressie(noemer)));
			}
			else if(teller<0 && (int)Math.abs(teller)>(int)Math.abs(noemer))
			{	int helen = (int)Math.abs(teller)/(int)Math.abs(noemer);
				int delen = (int)Math.abs(teller)%(int)Math.abs(noemer);
				exp = new Aftrekking(new BasisExpressie(0),new Optelling(new BasisExpressie(helen),new Deling(new BasisExpressie(delen),new BasisExpressie(noemer))));
				if(delen==0)exp = new Aftrekking(new BasisExpressie(0),new BasisExpressie(helen));
			}
			else if(teller==0)
			{	exp = new BasisExpressie(0);
			}
			else if(teller==noemer)
			{	exp = new BasisExpressie(1);
			}
			else if(teller==-noemer)
			{	exp = new Aftrekking(new BasisExpressie(0),new BasisExpressie(1));
			}
		}
		else
		{	double waarde = geefWaarde()*e.geefWaarde();
			if(waarde>=0)exp = new BasisExpressie(waarde);
			else if(waarde<0)exp = new Aftrekking(new BasisExpressie(0),new BasisExpressie(-waarde));
		}
		return exp;
	}
}
