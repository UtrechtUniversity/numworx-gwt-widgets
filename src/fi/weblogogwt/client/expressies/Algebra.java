package fi.weblogogwt.client.expressies;

import java.util.*;
//import java.awt.*;

public class Algebra
{	
	public static boolean checkGelijkwaardig(Expressie e1, Expressie e2, String[] varNamen)
	{	double[] tryValues = {0.1, 1.1, 2.1, 3.1};
		if(varNamen.length==0) 
		{	boolean nan1 = (Double.isInfinite (e1.geefWaarde()) || Double.isNaN(e1.geefWaarde()));
			boolean nan2 = (Double.isInfinite (e2.geefWaarde()) || Double.isNaN(e2.geefWaarde()));
			boolean ongelijk = Math.abs(e1.geefWaarde() - e2.geefWaarde())>0.000000001 &&  Math.abs(e1.geefWaarde()/e2.geefWaarde()-1)>0.000000001;
			if(nan1 && !nan2 || !nan1 &&nan2 || (ongelijk && !(nan1 && nan2)))
			{	return false;
			}
			return true;
		}
		else
		{	String[] varNamenNieuw = new String[varNamen.length-1];
			for(int i=0 ; i<varNamen.length-1 ; i++)
			{	varNamenNieuw[i] = varNamen[i+1];
			}
			for(int i=0 ; i<tryValues.length-1 ; i++)
			{	double value = tryValues[i];
				e1 = e1.substitueer(value, varNamen[0]);
				e2 = e2.substitueer(value, varNamen[0]);
				boolean gelijkwaardig = checkGelijkwaardig(e1,e2,varNamenNieuw);
				if(!gelijkwaardig)return false;
			}
			return true;
		}
	}
	
	/*Bepaalt of twee expressies gelijkwaardig zijn.
	 *Dit gebeurt door voor de aanwezige variabelen een tiental waarden
	 *in te vullen. Gaat alleen goed voor expressies met minder dan 7
	 *variabelen. Bij meer variabelen wordt de terugkeerwaarde false.
	 *Bovendien gaat het fout indien beide expressies een domein hebben
	 *waar deze tien testwaarden niet in voorkomen. In dat geval is de
	 *terugkeerwaarde true.
	 */
	
	public static boolean isGelijkwaardig(Expressie e1, Expressie e2)
	{	String[] vars = geefVarNamen((new Optelling(e1,e2)));
		return checkGelijkwaardig(e1,e2,vars);
	}
		
	/*public static boolean isGelijkwaardig(Expressie e1, Expressie e2)
	{	double d = 5.1; //verschuiving
		double a = 1;
		String[] vars = geefVarNamen((new Optelling(e1,e2)));
		int aantalVars = vars.length;
		boolean gelijkwaardig = true;
		if(aantalVars<2)
		{	for(int i=-5 ; i<6 ; i++)
			{	double subst = a*i+d;
				boolean nan1 = (Double.isInfinite (e1.geefWaarde(subst)) || Double.isNaN(e1.geefWaarde(subst)));
				boolean nan2 = (Double.isInfinite (e2.geefWaarde(subst)) || Double.isNaN(e2.geefWaarde(subst)));
				boolean ongelijk = Math.abs(e1.geefWaarde(subst) - e2.geefWaarde(subst))>0.000000001 &&  Math.abs(e1.geefWaarde(subst)/e2.geefWaarde(subst)-1)>0.000000001;
				//System.out.println(""+e1.geefWaarde(subst) + "  "+e2.geefWaarde(subst));
				if(nan1 && !nan2 || !nan1 &&nan2 || (ongelijk && !(nan1 && nan2)))
				{	gelijkwaardig = false;
					break;
				}
			}
			return gelijkwaardig;
		}
		else if(aantalVars==2)
		{	for(int i=-5 ; i<6 ; i++)
			{	for(int j=-5 ; j<6 ; j++)
				{	double[] subst = {a*i+d,a*j+d};
					boolean nan1 = (Double.isInfinite (e1.geefWaarde(subst,vars)) || Double.isNaN(e1.geefWaarde(subst,vars)));
					boolean nan2 = (Double.isInfinite (e2.geefWaarde(subst,vars)) || Double.isNaN(e2.geefWaarde(subst,vars)));
					boolean ongelijk = Math.abs(e1.geefWaarde(subst,vars) - e2.geefWaarde(subst,vars))>0.000000001 &&  Math.abs(e1.geefWaarde(subst,vars)/e2.geefWaarde(subst,vars)-1)>0.000000001;
					
					if(nan1 && !nan2 || !nan1 &&nan2 || (ongelijk && !(nan1 && nan2)))
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
					{	double[] subst = {a*i+d,a*j+d,a*k+d};
						boolean nan1 = (Double.isInfinite (e1.geefWaarde(subst,vars)) || Double.isNaN(e1.geefWaarde(subst,vars)));
						boolean nan2 = (Double.isInfinite (e2.geefWaarde(subst,vars)) || Double.isNaN(e2.geefWaarde(subst,vars)));
						boolean ongelijk = Math.abs(e1.geefWaarde(subst,vars) - e2.geefWaarde(subst,vars))>0.000000001 &&  Math.abs(e1.geefWaarde(subst,vars)/e2.geefWaarde(subst,vars)-1)>0.000000001;
						if(nan1 && !nan2 || !nan1 &&nan2 || (ongelijk && !(nan1 && nan2)))
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
						{	double[] subst = {a*i+d,a*j+d,a*k+d,a*l+d};
							boolean nan1 = (Double.isInfinite (e1.geefWaarde(subst,vars)) || Double.isNaN(e1.geefWaarde(subst,vars)));
							boolean nan2 = (Double.isInfinite (e2.geefWaarde(subst,vars)) || Double.isNaN(e2.geefWaarde(subst,vars)));
							boolean ongelijk = Math.abs(e1.geefWaarde(subst,vars) - e2.geefWaarde(subst,vars))>0.000000001 &&  Math.abs(e1.geefWaarde(subst,vars)/e2.geefWaarde(subst,vars)-1)>0.000000001;
							if(nan1 && !nan2 || !nan1 &&nan2 || (ongelijk && !(nan1 && nan2)))
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
							{	double[] subst = {a*i+d,a*j+d,a*k+d,a*l+d,a*m+d};
								boolean nan1 = (Double.isInfinite (e1.geefWaarde(subst,vars)) || Double.isNaN(e1.geefWaarde(subst,vars)));
								boolean nan2 = (Double.isInfinite (e2.geefWaarde(subst,vars)) || Double.isNaN(e2.geefWaarde(subst,vars)));
								boolean ongelijk = Math.abs(e1.geefWaarde(subst,vars) - e2.geefWaarde(subst,vars))>0.000000001 &&  Math.abs(e1.geefWaarde(subst,vars)/e2.geefWaarde(subst,vars)-1)>0.000000001;
								if(nan1 && !nan2 || !nan1 &&nan2 || (ongelijk && !(nan1 && nan2)))
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
								{	double[] subst = {a*i+d,a*j+d,a*k+d,a*l+d,a*m+d,a*n+d};
									boolean nan1 = (Double.isInfinite (e1.geefWaarde(subst,vars)) || Double.isNaN(e1.geefWaarde(subst,vars)));
									boolean nan2 = (Double.isInfinite (e2.geefWaarde(subst,vars)) || Double.isNaN(e2.geefWaarde(subst,vars)));
									boolean ongelijk = Math.abs(e1.geefWaarde(subst,vars) - e2.geefWaarde(subst,vars))>0.000000001 &&  Math.abs(e1.geefWaarde(subst,vars)/e2.geefWaarde(subst,vars)-1)>0.000000001;
									if(nan1 && !nan2 || !nan1 &&nan2 || (ongelijk && !(nan1 && nan2)))
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
		
	}*/
	
	/*Bepaalt of twee expressies evenredig zijn. e1 = k*e2
	 *Dit gebeurt door voor de aanwezige variabelen een tiental waarden
	 *in te vullen. Gaat alleen goed voor expressies met minder dan 7
	 *variabelen. Bij meer variabelen wordt de terugkeerwaarde false.
	 *Bovendien gaat het fout indien beide expressies een domein hebben
	 *waar deze tien testwaarden niet in voorkomen. In dat geval is de
	 *terugkeerwaarde true.
	 */
	public static boolean zijnEvenredig(Expressie e1, Expressie e2)
	{	String[] vars = geefVarNamen((new Optelling(e1,e2)));
		int aantalVars = vars.length;
		boolean gelijkwaardig = true;
		double factor = 0;
		if(aantalVars<2)
		{	for(int i=-5 ; i<6 ; i++)
			{	double d1 = e1.geefWaarde(i);
				double d2 = e2.geefWaarde(i);
				boolean nan1 = (Double.isInfinite(d1) || Double.isNaN(d1));
				boolean nan2 = (Double.isInfinite(d2) || Double.isNaN(d2));
				if(nan1 && !nan2 || !nan1 &&nan2)
				{	gelijkwaardig = false;
					break;
				}
				else if(d1!=0  &&  d2!=0 && !(nan1 && nan2))
				{	if(factor==0)factor = d1/ d2;
					if(Math.abs(d1/d2 - factor)>0.0001)
					{	gelijkwaardig = false;
						break;
					}
				}
			}
		}
		else if(aantalVars==2)
		{	for(int i=-5 ; i<6 ; i++)
			{	for(int j=-5 ; j<6 ; j++)
				{	double[] subst = {i,j};
					double d1 = e1.geefWaarde(subst,vars);
					double d2 = e2.geefWaarde(subst,vars);
					boolean nan1 = (Double.isInfinite (d1) || Double.isNaN(d1));
					boolean nan2 = (Double.isInfinite (d2) || Double.isNaN(d2));
					if(nan1 && !nan2 || !nan1 &&nan2)
					{	gelijkwaardig = false;
						break;
					}
					else if(d1!=0  &&  d2!=0 && !(nan1 && nan2))
					{	if(factor==0)factor = d1/ d2;
						if(Math.abs(d1/d2 - factor)>0.000000001)
						{	gelijkwaardig = false;
							break;
						}
					}
				}
			}
		}
		else if(aantalVars==3)
		{	for(int i=-5 ; i<6 ; i++)
			{	for(int j=-5 ; j<6 ; j++)
				{	for(int k=-5 ; k<6 ; k++)
					{	double[] subst = {i,j,k};
						double d1 = e1.geefWaarde(subst,vars);
						double d2 = e2.geefWaarde(subst,vars);
						boolean nan1 = (Double.isInfinite (d1) || Double.isNaN(d1));
						boolean nan2 = (Double.isInfinite (d2) || Double.isNaN(d2));
						if(nan1 && !nan2 || !nan1 &&nan2)
						{	gelijkwaardig = false;
							break;
						}
						else if(d1!=0  &&  d2!=0 && !(nan1 && nan2))
						{	if(factor==0)factor = d1/ d2;
							if(Math.abs(d1/d2 - factor)>0.000000001)
							{	gelijkwaardig = false;
								break;
							}
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
							double d1 = e1.geefWaarde(subst,vars);
							double d2 = e2.geefWaarde(subst,vars);
							boolean nan1 = (Double.isInfinite (d1) || Double.isNaN(d1));
							boolean nan2 = (Double.isInfinite (d2) || Double.isNaN(d2));
							if(nan1 && !nan2 || !nan1 &&nan2)
							{	gelijkwaardig = false;
								break;
							}
							else if(d1!=0  &&  d2!=0 && !(nan1 && nan2))
							{	if(factor==0)factor = d1/ d2;
								if(Math.abs(d1/d2 - factor)>0.000000001)
								{	gelijkwaardig = false;
									break;
								}
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
								double d1 = e1.geefWaarde(subst,vars);
								double d2 = e2.geefWaarde(subst,vars);
								boolean nan1 = (Double.isInfinite (d1) || Double.isNaN(d1));
								boolean nan2 = (Double.isInfinite (d2) || Double.isNaN(d2));
								if(nan1 && !nan2 || !nan1 &&nan2)
								{	gelijkwaardig = false;
									break;
								}
								else if(d1!=0  &&  d2!=0 && !(nan1 && nan2))
								{	if(factor==0)factor = d1/ d2;
									if(Math.abs(d1/d2 - factor)>0.000000001)
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
		else if(aantalVars==6)
		{	for(int i=-5 ; i<6 ; i++)
			{	for(int j=-5 ; j<6 ; j++)
				{	for(int k=-5 ; k<6 ; k++)
					{	for(int l=-5 ; l<6 ; l++)
						{	for(int m=-5 ; m<6 ; m++)
							{	for(int n=-5 ; n<6 ; n++)
								{	double[] subst = {i,j,k,l,m,n};
									double d1 = e1.geefWaarde(subst,vars);
									double d2 = e2.geefWaarde(subst,vars);
									boolean nan1 = (Double.isInfinite (d1) || Double.isNaN(d1));
									boolean nan2 = (Double.isInfinite (d2) || Double.isNaN(d2));
									if(nan1 && !nan2 || !nan1 &&nan2)
									{	gelijkwaardig = false;
										break;
									}
									else if(d1!=0  &&  d2!=0 && !(nan1 && nan2))
									{	if(factor==0)factor = d1/ d2;
										if(Math.abs(d1/d2 - factor)>0.000000001)
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
		}
		else if(aantalVars>6)gelijkwaardig = false;
		return gelijkwaardig;
		
	}
	
	public static boolean isGelijkDouble(double d1, double d2)
	{	return Math.abs(d1-d2)<0.000000001;
		
	}
	
	public static boolean zijnGelijk(Expressie e1, Expressie e2)
	{	if(e1==null || e2==null) return false;
		if(e1.toStringStrikt().equals(e2.toStringStrikt())) return true;
		//else if(e1 instanceof BasisExpressie && e2 instanceof BasisExpressie)
		//{	if(e1.toStringStrikt().equals(e2.toStringStrikt())) return true;
		//	else return false;
		//}
		else if(e1 instanceof BasisExpressie && e2 instanceof BasisExpressie)
		{	return false;
		}
		else if(e1 instanceof BasisExpressie || e2 instanceof BasisExpressie)
		{	return false;
		}
		else if(e1 instanceof ArcCosinus && e2 instanceof ArcCosinus)
		{	return zijnGelijk(e1.kind1,e2.kind1);
		}
		else if(e1 instanceof ArcCosinus || e2 instanceof ArcCosinus)
		{	return false;
		}
		else if(e1 instanceof ArcSinus && e2 instanceof ArcSinus)
		{	return zijnGelijk(e1.kind1,e2.kind1);
		}
		else if(e1 instanceof ArcSinus || e2 instanceof ArcSinus)
		{	return false;
		}
		else if(e1 instanceof ArcTangens && e2 instanceof ArcTangens)
		{	return zijnGelijk(e1.kind1,e2.kind1);
		}
		else if(e1 instanceof ArcTangens || e2 instanceof ArcTangens)
		{	return false;
		}
		else if(e1 instanceof Cosinus && e2 instanceof Cosinus)
		{	return zijnGelijk(e1.kind1,e2.kind1);
		}
		else if(e1 instanceof Cosinus || e2 instanceof Cosinus)
		{	return false;
		}
		else if(e1 instanceof Sinus && e2 instanceof Sinus)
		{	return zijnGelijk(e1.kind1,e2.kind1);
		}
		else if(e1 instanceof Sinus || e2 instanceof Sinus)
		{	return false;
		}
		else if(e1 instanceof Tangens && e2 instanceof Tangens)
		{	return zijnGelijk(e1.kind1,e2.kind1);
		}
		else if(e1 instanceof Tangens || e2 instanceof Tangens)
		{	return false;
		}
		else if(e1 instanceof Ln && e2 instanceof Ln)
		{	return zijnGelijk(e1.kind1,e2.kind1);
		}
		else if(e1 instanceof Ln || e2 instanceof Ln)
		{	return false;
		}
		else if(e1 instanceof Log && e2 instanceof Log)
		{	return zijnGelijk(e1.kind1,e2.kind1);
		}
		else if(e1 instanceof Log || e2 instanceof Log)
		{	return false;
		}
		
		else if(e1 instanceof Wortel && e2 instanceof Wortel)
		{	return zijnGelijk(e1.kind1,e2.kind1);
		}
		else if(e1 instanceof Wortel || e2 instanceof Wortel)
		{	return false;
		}
		
		else if(e1 instanceof Deling && e2 instanceof Deling && e1.kind1 instanceof BasisExpressie && isGelijkDouble(e1.kind1.geefWaarde(), 1)  && e2.kind1 instanceof BasisExpressie && isGelijkDouble(e2.kind1.geefWaarde(), 1))
		{	return zijnGelijk(e1.kind2,e2.kind2);
		}
		//else if(e1 instanceof Deling && e2 instanceof Deling && e1.kind1 instanceof BasisExpressie && e2.kind1 instanceof BasisExpressie && isGelijkDouble(e1.kind1.geefWaarde(), e2.kind1.geefWaarde()))
		//{	return zijnGelijk(e1.kind2,e2.kind2);
		//}
		
		else if(e1 instanceof Macht && e2 instanceof Macht)
		{	return zijnGelijk(e1.kind1,e2.kind1) && zijnGelijk(e1.kind2,e2.kind2);
		}
		else if(e1 instanceof Macht || e2 instanceof Macht)
		{	return false;
		}
		else if(e1 instanceof NdeWortel && e2 instanceof NdeWortel)
		{	return zijnGelijk(e1.kind1,e2.kind1) && zijnGelijk(e1.kind2,e2.kind2);
		}
		else if(e1 instanceof NdeWortel || e2 instanceof NdeWortel)
		{	return false;
		}
		else if(e1 instanceof NdeLog && e2 instanceof NdeLog)
		{	return zijnGelijk(e1.kind1,e2.kind1) && zijnGelijk(e1.kind2,e2.kind2);
		}
		else if(e1 instanceof NdeLog || e2 instanceof NdeLog)
		{	return false;
		}
		
		else if(e1 instanceof Aftrekking && e2 instanceof Aftrekking && e1.kind1 instanceof BasisExpressie && e1.kind1.geefWaarde()==0 && e2.kind1 instanceof BasisExpressie && e2.kind1.geefWaarde()==0)
		{	return zijnGelijk(e1.kind2,e2.kind2);
		}
		Vector v1 = geefTermen(e1,new Vector());
		Vector v2 = geefTermen(e2,new Vector());
		if(v1.size()==1 && v2.size()==1)
		{	e1 = (Expressie)v1.elementAt(0);
			e2 = (Expressie)v2.elementAt(0);
			v1 = geefFactorenBeperkt(e1,new Vector());
			v2 = geefFactorenBeperkt(e2,new Vector());
			if(v1.size()==1 && v2.size()==1)
			{	e1 = (Expressie)v1.elementAt(0);
				e2 = (Expressie)v2.elementAt(0);
				//System.out.println(e1.toStringStrikt());
				//System.out.println(e2.toStringStrikt());
				if((isBreukPlusGetal(e1) || isBreukPlusGetal(e2)) && !e1.toStringStrikt().equals(e2.toStringStrikt())) return false;
				else if(e1.toStringStrikt().length()>0 && e2.toStringStrikt().length()>0 && !e1.toStringStrikt().substring(0,1).equals(e2.toStringStrikt().substring(0,1))) return false;
				else if(e1.toStringStrikt().length()>1 && e2.toStringStrikt().length()>1 && !e1.toStringStrikt().substring(0,2).equals(e2.toStringStrikt().substring(0,2))) return false;
				else if(e1.toStringStrikt().length()>3 && e2.toStringStrikt().length()>3 && e1.toStringStrikt().substring(0,3).equals("arc") && !e1.toStringStrikt().substring(0,4).equals(e2.toStringStrikt().substring(0,4))) return false;
				return zijnGelijk(e1,e2);
			}
			else return zijnGelijk(v1,v2);
		}
		else return zijnGelijk(v1,v2);
		
	
	}
	
	public static boolean zijnGelijk(Vector v1, Vector v2)
	{	int aantal1 = v1.size();
		int aantal2 = v2.size();
		if(aantal1 != aantal2) return false;
		boolean zijnGelijk = true;
		for(int i=0 ; i<aantal1 ; i++)
		{	boolean bevat = false;
			Expressie e1 = (Expressie)v1.elementAt(i);
			for(int j=0 ; j<v2.size() ; j++)
			{	Expressie e2 = (Expressie)v2.elementAt(j);
				if(zijnGelijk(e1,e2)) 
				{ 	bevat = true;
					v2.removeElementAt(j);
					break;
				}
			}
			if(!bevat) 
			{	zijnGelijk = false;
				break;
			}
		}
		return zijnGelijk;
	}
	
	/*Controleert de gelijkwaardigheid van de twee lineaire vergelijkingen
	 *eLinks1 = eRechts1 en eLinks2 = eRechts2
	 *
	 *(wordt niet gebruikt. In feite overbodig geworden)
	 */
	public static boolean isGelijkwaardigeLinVergelijking(Expressie eLinks1,Expressie eRechts1,Expressie eLinks2,Expressie eRechts2)
	{	Expressie e1 = new Aftrekking(eLinks1,eRechts1);
		Expressie e2 = new Aftrekking(eLinks2,eRechts2);
		if(Math.abs(e1.geefWaarde(0))< 0.000000001 && Math.abs(e2.geefWaarde(0))<0.000000001
		   || Math.abs(e1.geefWaarde(1))<0.000000001 && Math.abs(e2.geefWaarde(1))<0.000000001)return true;
		double factorA = e1.geefWaarde(0)/e2.geefWaarde(0);
		double factorB = e1.geefWaarde(1)/e2.geefWaarde(1);
		if(Math.abs(factorA - factorB)>0.000000001)return false;
		return true;
	}
	/*Controleert of de vergelijking eLinks1 = eRechts1 gelijkwaardig is met
	 *het koppel eLinks2 = eRechts2 of eLinks3 = eRechts3
	 */
	public static boolean zijnGelijkwaardigeVergelijkingen1Naar2(Expressie eLinks1,Expressie eRechts1,Expressie eLinks2,Expressie eRechts2,Expressie eLinks3,Expressie eRechts3)
	{	Expressie e1 = new Aftrekking(eLinks1 , eRechts1);
		Expressie e2 = new Aftrekking(eLinks2 , eRechts2);
		Expressie e3 = new Aftrekking(eLinks3 , eRechts3);
		e2 = new Vermenigvuldiging(e2,e3);
		return 	zijnEvenredigePolynomen(e1,e2);
	}
	/*Controleert of het koppel vergelijkingen 
	 *eLinks1 = eRechts1 of eLinks2 = eRechts2  gelijkwaardig is met
	 *de vergelijking eLinks3 = eRechts3
	 */
	public static boolean zijnGelijkwaardigeVergelijkingen2Naar1(Expressie eLinks1,Expressie eRechts1,Expressie eLinks2,Expressie eRechts2,Expressie eLinks3,Expressie eRechts3)
	{	Expressie e1 = new Aftrekking(eLinks1 , eRechts1);
		Expressie e2 = new Aftrekking(eLinks2 , eRechts2);
		Expressie e3 = new Aftrekking(eLinks3 , eRechts3);
		e1 = new Vermenigvuldiging(e1,e2);
		return 	zijnEvenredigePolynomen(e1,e3);
	}
	/*Controleert of het koppel vergelijkingen 
	 *eLinks1 = eRechts1 of eLinks2 = eRechts2  gelijkwaardig is met
	 *eLinks3 = eRechts3 of eLinks4 = eRechts4
	 *
	 *niet gebruikt
	 */
	public static boolean zijnGelijkwaardigeVergelijkingen2Naar2(Expressie eLinks1,Expressie eRechts1,Expressie eLinks2,Expressie eRechts2,Expressie eLinks3,Expressie eRechts3,Expressie eLinks4,Expressie eRechts4)
	{	Expressie e1 = new Aftrekking(eLinks1 , eRechts1);
		Expressie e2 = new Aftrekking(eLinks2 , eRechts2);
		Expressie e3 = new Aftrekking(eLinks3 , eRechts3);
		Expressie e4 = new Aftrekking(eLinks4 , eRechts4);
		e1 = new Vermenigvuldiging(e1,e2);
		e3 = new Vermenigvuldiging(e3,e4);
		return 	zijnEvenredigePolynomen(e1,e3);
	}
	/*Controleert de gelijkwaardigheid van de twee vergelijkingen
	 *eLinks1 = eRechts1 en eLinks2 = eRechts2
	 */	
	public static boolean zijnGelijkwaardigeVergelijkingen(Expressie eLinks1,Expressie eRechts1,Expressie eLinks2,Expressie eRechts2)
	{	Expressie e1 = new Aftrekking(eLinks1 , eRechts1);
		Expressie e2 = new Aftrekking(eLinks2 , eRechts2);
		return 	zijnEvenredigePolynomen(e1,e2);																	
	}
	/*Bepaalt of twee polynoom expressies evenredig zijn. e1 = k*e2
	 *Dit gebeurde door de standaardvorm te maken en de coefficienten
	 *te vergelijken (daardoor slecht één variabele magoelijk).
	 *Nu wordt: zijnEvenredig aangeroepen. Hierdoor werkt het 
	 *ook op expressies met meer variabelen 
	 */
	public static boolean zijnEvenredigePolynomen(Expressie e1, Expressie e2)
	{	return zijnEvenredig(e1,e2);
		/*e1 = benaderWortels(e1);
		e2 = benaderWortels(e2);
		e1 = verwijderHaakjes(e1);
		e2 = verwijderHaakjes(e2);
		e1 = herleid(e1);
		e2 = herleid(e2);
		double[] coeff1 = geefCoefficienten(e1);
		double[] coeff2 = geefCoefficienten(e2);
		int graad1 = coeff1.length-1;
		int graad2 = coeff2.length-1;
		if(graad1==graad2)
		{	int graad = graad1;
			double factor = coeff1[graad]/coeff2[graad];
			{	for(int i=0 ; i<graad ; i++)
				{	if(Math.abs(factor*coeff2[i] - coeff1[i]) >	0.0000001)
					{	return false;
					}
				}
			}
			return true;
		}
		//is eigenlijk een beetje raar. het zijn dan niet evenredige polynomen, maar hebben wel dezelfde nulpuntenverzameling
		else if(graad1==2 && graad2==1 && coeff1[1]*coeff1[1] -4*coeff1[2]*coeff1[0]==0)
		{	e2 = new Vermenigvuldiging(e2,e2);
			return zijnEvenredigePolynomen(e1, e2);
		}
		return false;*/
	}
	/*Bepaalt of de oplossing van vergelijking e1 = 0 één van de oplossingen is
	 *van e2 = 0.
	 *Werkt alleen als e1 lineair en e2 kwadratisch, beide polynomen met 
	 *één variabele. Geeft exception bij expressies van meer variabelen.
	 */ 
	public static boolean isDeeloplossingVan(Expressie e1,Expressie e2)
	{	String[] varNamen = geefVarNamen(new Optelling(e1,e2));
		if(varNamen.length>1)return false;
		
		e1 = benaderWortels(e1);
		e2 = benaderWortels(e2);
		e1 = verwijderHaakjes(e1);
		e2 = verwijderHaakjes(e2);
		e1 = herleid(e1);
		e2 = herleid(e2);
		double[] coeff1 = geefCoefficienten(e1);
		double[] coeff2 = geefCoefficienten(e2);
		int graad1 = coeff1.length-1;
		int graad2 = coeff2.length-1;
		if(coeff1.length!=2 || coeff2.length!=3)return false;
		if(coeff2[1]*coeff2[1]-4*coeff2[0]*coeff2[2]<0)return false;
		
		double oplossing1 = -coeff1[0]/coeff1[1];
		double oplossing2a = (-coeff2[1]+Math.sqrt(coeff2[1]*coeff2[1]-4*coeff2[0]*coeff2[2]))/(2*coeff2[2]);
		double oplossing2b = (-coeff2[1]-Math.sqrt(coeff2[1]*coeff2[1]-4*coeff2[0]*coeff2[2]))/(2*coeff2[2]);
		
		if(Math.abs(oplossing1-oplossing2a)<0.00001 || Math.abs(oplossing1-oplossing2b)<0.00001)
		{	return true;
		}
		else return false;
	}
	/*Zet de vergelijking e1 = e2 in het paar 
	 *sqrt(e1)=sqrt(e2) , sqrt(e1)=-sqrt(e2)
	 *Geeft teru een array e van expressies, waarbij:
	 *e[0]=sqrt(e1), e[1]=sqrt(e2) en e[2]=-sqrt(e2)
	 *of e[0]=sqrt(e2), e[1]=sqrt(e1) en e[2]=-sqrt(e1)
	 *indien e1 of e2 constant zijn <0, dan return null
	 */
	public static Expressie[] geefWortels(Expressie e1, Expressie e2)
	{	Expressie[] e = new Expressie[3];
		Expressie e1Wortel = geefWortel(e1);
		Expressie e2Wortel = geefWortel(e2);
		if(!Double.isNaN(e1Wortel.geefWaarde()))
		{	e1Wortel = geefWortel(e2);
			e2Wortel = geefWortel(e1);
		}
		if(e2Wortel.geefWaarde()==0)
		{	e[0] = e1Wortel;
			e[1] = new BasisExpressie(0);
			e[2] = null;
		}
		else 
		{	e[0] = e1Wortel;
			e[1] = e2Wortel;
			e[2] = new Aftrekking(new BasisExpressie(0),e2Wortel);
		}
		return e;
		
		
		/*Expressie[] e = new Expressie[3];
		Expressie e1Wortel = new Wortel(e1);
		Expressie e2Wortel = new Wortel(e2);
		
		if(e1 instanceof Macht && e1.kind2.geefWaarde()==2)e1Wortel = e1.kind1;
		if(e2 instanceof Macht && e2.kind2.geefWaarde()==2)e2Wortel = e2.kind1;
		
		
		if( e1 instanceof Macht && e1.kind2.geefWaarde()==2 && !(e2 instanceof Macht && e2.kind2.geefWaarde()==2))//!Double.isNaN(e2.geefWaarde()) &&
		{	if(e2.geefWaarde()<0)return null;
			double wortel = Math.sqrt(e2.geefWaarde());
			e[0] = e1.kind1;
			if(wortel==0)
			{	e[1] = new BasisExpressie(wortel);
				e[2] = null;
			}
			else 
			{	e[1] = evalueerGetalsExpressie(new Wortel(e2));
				e[2] = new Aftrekking(new BasisExpressie(0),e[1]);
			}
		}
		else if(e2 instanceof Macht && e2.kind2.geefWaarde()==2 && !(e1 instanceof Macht && e1.kind2.geefWaarde()==2))//!Double.isNaN(e1.geefWaarde()) && 
		{	if(e1.geefWaarde()<0)return null;
			double wortel = Math.sqrt(e1.geefWaarde());
			e[0] = e2.kind1;
			if(wortel==0)
			{	e[1] = new BasisExpressie(wortel);
				e[2] = null;
			}
			else 
			{	e[1] = evalueerGetalsExpressie(new Wortel(e1));
				e[2] = new Aftrekking(new BasisExpressie(0),e[1]);
			}
			
		}
		else if( e1 instanceof Macht && e1.kind2.geefWaarde()==2 && e2 instanceof Macht && e2.kind2.geefWaarde()==2)//!Double.isNaN(e2.geefWaarde()) &&
		{	e[0] = e1.kind1;
			e[1] = e2.kind1;
			e[2] = new Aftrekking(new BasisExpressie(0),e[1]);
		}
		else
		{	double wortel1 = Math.sqrt(e1.geefWaarde());
			double wortel2 = Math.sqrt(e2.geefWaarde());
			if(wortel1==0)
			{	e[0] = new Wortel(e2);
				e[1] = new BasisExpressie(0);
				e[2] = null;
			}
			else if(wortel2==0)
			{	e[0] = new Wortel(e1);
				e[1] = new BasisExpressie(0);
				e[2] = null;
			}
			else 
			{	e[0] = new Wortel(e1);
				e[1] = new Wortel(e2);
				e[2] = new Aftrekking(new BasisExpressie(0),e[1]);
			}
		}
		
		
		return e;*/
	}
	/*Probeert de vergelijking e1 = e2 te splitsen. 
	 *Lukt alleen als e1 van de vorm e3*e4 is en e2 een waarde heeft van 0
	 *(anders return null).
	 *Geeft in dat geval terug een array van Expressies e, met
	 *e[0] = e3 en e[1] = e4
	 *Indien e3 = e4, dan:
	 *e[0] = e3 en e[1] = null
	 */	
	public static Expressie[] geefSplitsing(Expressie e1, Expressie e2)
	{	Expressie[] e = new Expressie[2];
		if(e2.geefWaarde()==0)
		{	if(e1 instanceof Vermenigvuldiging && Double.isNaN(e1.kind1.geefWaarde()) && Double.isNaN(e1.kind2.geefWaarde()))
			{	e[0] = e1.kind1;
				e[1] = e1.kind2;
			}
			else if(e1 instanceof Aftrekking && e1.kind1.geefWaarde()==0 && e1.kind2 instanceof Vermenigvuldiging && Double.isNaN(e1.kind2.kind1.geefWaarde()) && Double.isNaN(e1.kind2.kind2.geefWaarde()))
			{	e[0] = new Aftrekking(new BasisExpressie(0),e1.kind2.kind1);
				e[1] = e1.kind2.kind2;
			}
			else if(e1 instanceof Macht && e1.kind2.geefWaarde()==2)
			{	e[0] = e1.kind1;
				e[1] = null;
			}
			else if(e1 instanceof Aftrekking && e1.kind1.geefWaarde()==0 && e1.kind2 instanceof Macht && e1.kind2.kind2.geefWaarde()==2)
			{	e[0] = new Aftrekking(new BasisExpressie(0),e1.kind2.kind1);
				e[1] = e1.kind2.kind1;
			}
		}
		else if(e1.geefWaarde()==0)
		{	if(e2 instanceof Vermenigvuldiging && Double.isNaN(e2.kind1.geefWaarde()) && Double.isNaN(e2.kind2.geefWaarde()))
			{	e[0] = e2.kind1;
				e[1] = e2.kind2;
			}
			else if(e2 instanceof Aftrekking && e2.kind1.geefWaarde()==0 && e2.kind2 instanceof Vermenigvuldiging && Double.isNaN(e2.kind2.kind1.geefWaarde()) && Double.isNaN(e2.kind2.kind2.geefWaarde()))
			{	e[0] = new Aftrekking(new BasisExpressie(0),e2.kind2.kind1);
				e[1] = e2.kind2.kind2;
			}
			else if(e1 instanceof Macht && e2.kind2.geefWaarde()==2)
			{	e[0] = e2.kind1;
				e[1] = null;
			}
			else if(e2 instanceof Aftrekking && e2.kind1.geefWaarde()==0 && e2.kind2 instanceof Macht && e2.kind2.kind2.geefWaarde()==2)
			{	e[0] = new Aftrekking(new BasisExpressie(0),e2.kind2.kind1);
				e[1] = e2.kind2.kind1;
			}
		}
		else return null;
		if(e[1]!=null && isGelijkwaardig(e[0],e[1])) e[1] = null;
		return e;
	}
	/*Bepaald van een 2e graads vergelijking e1 = e2 of er oplossingen zijn
	 *indien niet 2e graads, dan return true
	 */	
	public static boolean heeftOplossingen(Expressie e1, Expressie e2)
	{	Expressie e = new Aftrekking(e1 , e2);
		double[] coeff = geefCoefficienten(herleid(verwijderHaakjes(e)));
		if(coeff.length==3 && coeff[1]*coeff[1] - 4*coeff[0]*coeff[2]<0)return false;
		return true;
	}
	
	public static double geefDiscriminant(Vergelijking v)
	{	Expressie e1 = v.kind1;
		Expressie e2 = v.kind2;
		Expressie e = new Aftrekking(e1 , e2);
		double[] coeff = geefCoefficienten(herleid(verwijderHaakjes(e)));
		if(coeff==null) return Double.NaN;
		double a = 0;
		double b = 0;
		double c = 0;
		if(coeff.length>0) c = coeff[0];
		if(coeff.length>1) b = coeff[1];
		if(coeff.length>2) a = coeff[2];
		if(coeff.length>3) return Double.NaN;
		return b*b - 4*a*c;
	}
	
	public static double geefNormDiscriminant(Vergelijking v)
	{	Expressie e1 = v.kind1;
		Expressie e2 = v.kind2;
		Expressie e = new Aftrekking(e1 , e2);
		double[] coeff = geefCoefficienten(herleid(verwijderHaakjes(e)));
		if(coeff==null || coeff.length!=3) return Double.NaN;
		double a = coeff[2];
		double b = coeff[1];
		double c = coeff[0];
		if(a==0) return Double.NaN;
		b=b/a;
		c=c/a;
		return b*b - 4*c;
	}
	
	public static double[] geefCoefficienten(Vergelijking v)
	{	Expressie e1 = v.kind1;
		Expressie e2 = v.kind2;
		Expressie e = new Aftrekking(e1 , e2);
		return geefCoefficienten(herleid(verwijderHaakjes(e)));
		
	}
	
	/*Geeft de coefficienten van een polynoom van één variabele.
	 *coefficienten worden teruggegeven in een array met doubles
	 */	
	public static double[] geefCoefficienten(Expressie e)
	{	Expressie[] exp = geefCoefficientenExpressies(e);
		if(exp==null)return null;
		double[] coeff = new double[exp.length];
		for(int i=0 ; i<exp.length ; i++)
		{	coeff[i] = exp[i].geefWaarde();
		}
		return coeff;
	}
	/* Geeft de coefficienten van een polynoom in één variabele.
	 *De coefficienten worden teruggegeven als een array van (getals)expressies.
	 */
	public static Expressie[] geefCoefficientenExpressies(Expressie e)
	{	String[] varNamen = geefVarNamen(e);
		if(varNamen.length>1 || varNamen.length<1)return null;
		String varNaam = varNamen[0];
		Vector v = geefTermen(e, new Vector());
		sorteerTermen(v);
		int[] exponenten = new int[v.size()];
		for(int i=0 ; i<v.size() ; i++)
		{	exponenten[i]=0;
		}
		for(int i=0 ; i<v.size() ; i++)
		{	Vector u = geefFactoren((Expressie)v.elementAt(i),new Vector());
			for(int k=0 ; k<u.size() ; k++)
			{	Expressie exp = (Expressie)u.elementAt(k);
				if(exp instanceof BasisExpressie  && ((BasisExpressie)exp).basisString!=null && ((BasisExpressie)exp).basisString.equals(varNaam))
				{	exponenten[i]++;
					u.setElementAt(new BasisExpressie(1),k);
				}
				else if(Double.isNaN(exp.geefWaarde())) return null;
			}
			v.setElementAt(maakFactorenExpressie(u),i);
		}
		int graad = 0;
		for(int i=0 ; i<v.size() ; i++)
		{	if(exponenten[i]>graad)graad = exponenten[i];
		}
		Expressie[] exp = new Expressie[graad+1];
		for(int i=0 ; i<graad+1 ; i++)
		{	boolean aanwezig = false;
			for(int j=0 ; j<v.size() ; j++)
			{	if(exponenten[j]==i)
				{	aanwezig = true;
					exp[i] = (Expressie)v.elementAt(j);
					break;
				}
			}
			if(!aanwezig)
			{	exp[i] = new BasisExpressie(0);
			}
		}
		
		return exp;
		
	}
	/*Geeft de variabele namen van een expressie
	 *Geeft een vector met Strings terug.
	 */
	public static Vector geefVarN(Expressie e)
	{	Vector v;
		if(e instanceof BasisExpressie)
		{	Vector v0 = new Vector();
			if(e.geefVarNaam()!=null)v0.addElement(e.geefVarNaam());
			return v0;
		}
		if(e.kind2 !=null)
		{	Vector v1 = geefVarN(e.kind1);
			Vector v2 = geefVarN(e.kind2);
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
		{	v=geefVarN(e.kind1);
		}
		return v;
	}
	/*Geeft de variabele namen van een expressie
	 *Geeft een array met Strings terug.
	 */
	public static String[] geefVarNamen(Expressie e)
	{	Vector varn = Algebra.geefVarN(e);
		String[] varNamen = new String[varn.size()];
		for(int i=0 ; i<varn.size() ; i++)
		{	varNamen[i] = (String)varn.elementAt(i);
		}
		return varNamen;
	}
	
	public static boolean bevatVarNaam(Expressie e, String s)
	{	String[] varNamen = geefVarNamen(e) ;
		for(int i=0 ; i<varNamen.length; i++)
	    {	if(s.equals(varNamen[i]))return true;
	    }
	    return false;
	}
	/*Geeft de termen van een expressie en stopt ze in een meegegeven vector.
	 */
	public static Vector geefTermen(Expressie e, Vector v)
	{	if((e instanceof Optelling || e instanceof Aftrekking)
			&& !
				  (e instanceof Optelling
				   && e.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind1.geefWaarde())
				   && e.kind2 instanceof Deling
				   && e.kind2.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind1.geefWaarde())
				   && e.kind2.kind2 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind2.geefWaarde())
					)
				)
		{	
			if(e instanceof Optelling 
			   && (e.kind2 instanceof Optelling || e.kind2 instanceof Aftrekking))
			{	v = geefTermen(e.kind2,v);
			}
			else
			{	if(e instanceof Optelling)
				{	v.insertElementAt(e.kind2,0);
				}
				else 
				{	Expressie en = new Aftrekking(new BasisExpressie(0),e.kind2);
					v.insertElementAt(en,0);
				}
			}
			v = geefTermen(e.kind1,v);
		}
		else
		{	if(!(e instanceof BasisExpressie && e.geefWaarde()==0))v.insertElementAt(e,0);
		}
		return v;
	}
	/*Maakt een somexpressie van de expressies in de meegegeven vector
	 */
	public static Expressie maakTermenExpressie(Vector v)
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
	/*Geeft de factoren van de expressie en stopt ze in een meegegeven vector.
	 *Bij gebroken expressies worden een factor f in de noemen als 1/f 
	 *toegevoegd aan de vector
	 */
	public static Vector geefFactoren(Expressie e, Vector v)
	{	if(e instanceof Aftrekking && e.kind1.geefWaarde()==0)
		{	v = Algebra.geefFactoren(e.kind2,v);
			v.addElement(new BasisExpressie(-1));
		}
		else if(e instanceof Deling)
		{	//if(!(e.kind1 instanceof BasisExpressie && e.kind1.geefWaarde()==1)) 
			v = Algebra.geefFactoren(e.kind1,v);
			Vector u = Algebra.geefFactoren(e.kind2,new Vector());
			for (int i=0 ; i<u.size() ; i++)
			{	Expressie ee = (Expressie)u.elementAt(i);
				if(ee instanceof Deling)
				{	v = Algebra.geefFactoren(ee.kind2,v);
				}
				else
				{	v.addElement(new Deling(new BasisExpressie(1),(Expressie)u.elementAt(i)));
				}
			}
		}
		else if(e instanceof Vermenigvuldiging)
		{	v = Algebra.geefFactoren(e.kind1,v);
			v = Algebra.geefFactoren(e.kind2,v);
		}
		else if(e instanceof Macht && !Double.isNaN(e.kind2.geefWaarde())&& (e.kind2.geefWaarde() - (int)(e.kind2.geefWaarde()))==0)
		{	if(e.kind2.geefWaarde()>0)
			{	for (int i=0 ; i<e.kind2.geefWaarde() ; i++)
				{	v = Algebra.geefFactoren(e.kind1,v);
				}
			}
			else
			{	for (int i=0 ; i<-e.kind2.geefWaarde() ; i++)
				{	Vector u = Algebra.geefFactoren(e.kind1,new Vector());
					for (int j=0 ; j<u.size() ; j++)
					{	v.addElement(new Deling(new BasisExpressie(1),(Expressie)u.elementAt(j)));
					}
					//v.addElement(new Deling(new BasisExpressie(1),e.kind1));
				}
			}
		}
		else
		{	v.addElement(e);
		}
		return v;
	}
	
	/*toevoeging voor "Herleiden" 
	 *
	 */
	public static int geefAantalFactorenTermen(Expressie e)
	{	int aantal = 0;
		Vector v = geefTermen(e, new Vector());
		for(int i=0 ; i<v.size() ; i++)
		{	Expressie exp = (Expressie)v.elementAt(i);
			Vector u = geefFactorenBeperkt(exp, new Vector());
			aantal+=u.size();
		}
		return aantal;
	} 
	
	/*toevoeging voor "Herleiden" 
	 *Geeft het aantal factoren in alle termen samen. Machten worden als één factor geteld.
	 */
	public static Vector geefFactorenBeperkt(Expressie e, Vector v)
	{	if(e instanceof Aftrekking && e.kind1.geefWaarde()==0)
		{	v = Algebra.geefFactorenBeperkt(e.kind2,v);
			v.addElement(new BasisExpressie(-1));
		}
		else if(e instanceof Deling)
		{	v = Algebra.geefFactorenBeperkt(e.kind1,v);
			Vector u = Algebra.geefFactoren(e.kind2,new Vector());
			for (int i=0 ; i<u.size() ; i++)
			{	Expressie ee = (Expressie)u.elementAt(i);
				if(ee instanceof Deling)
				{	v = Algebra.geefFactorenBeperkt(ee.kind2,v);
				}
				else
				{	v.addElement(new Deling(new BasisExpressie(1),(Expressie)u.elementAt(i)));
				}
			}
		}
		else if(e instanceof Vermenigvuldiging)
		{	v = Algebra.geefFactorenBeperkt(e.kind1,v);
			v = Algebra.geefFactorenBeperkt(e.kind2,v);
		}
		else
		{	v.addElement(e);
		}
		return v;
	}
	/*toevoeging voor "Herleiden" 
	 *
	 */
	public static int geefAantalOperatoren(Expressie e)
	{	int aantal = 0;
		if(e.kind1!=null)aantal += geefAantalOperatoren(e.kind1);
		if(e.kind2!=null)aantal += geefAantalOperatoren(e.kind2);
		if(e.kind1!=null || e.kind2!=null)aantal++;
		return aantal;
	}
	/*toevoeging voor "Herleiden" 
	 *
	 */
	public static int geefAantalBreukPlusGetal(Expressie e)
	{	int aantal = 0;
		if(e.kind1!=null)aantal += geefAantalBreukPlusGetal(e.kind1);
		if(e.kind2!=null)aantal += geefAantalBreukPlusGetal(e.kind2);
		if(e instanceof Optelling
				   && e.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind1.geefWaarde())
				   && e.kind2 instanceof Deling
				   && e.kind2.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind1.geefWaarde())
				   && e.kind2.kind2 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind2.geefWaarde())
					)aantal++;
		return aantal;
	}
	/*toevoeging voor "Herleiden" 
	 *
	 */
	public static Vector geefMachten(Expressie e, Vector v)
	{	if(e instanceof Macht)v.addElement(e);
		if(e.kind1!=null)Algebra.geefMachten(e.kind1,v);
		if(e.kind2!=null)Algebra.geefMachten(e.kind2,v);
		return v;
	}
	
	public static Vector geefLogExpressies(Expressie e, Vector v)
	{	if(e instanceof Log)v.addElement(e);
		if(e.kind1!=null)Algebra.geefLogExpressies(e.kind1,v);
		if(e.kind2!=null)Algebra.geefLogExpressies(e.kind2,v);
		return v;
	}
	
	public static Vector geefDelingen(Expressie e, Vector v)
	{	if(e instanceof Deling)v.addElement(e);
		if(e.kind1!=null)Algebra.geefDelingen(e.kind1,v);
		if(e.kind2!=null)Algebra.geefDelingen(e.kind2,v);
		return v;
	}
	
	public static Vector geefAftrekkingen(Expressie e, Vector v)
	{	if(e instanceof Aftrekking)v.addElement(e);
		if(e.kind1!=null)Algebra.geefAftrekkingen(e.kind1,v);
		if(e.kind2!=null)Algebra.geefAftrekkingen(e.kind2,v);
		return v;
	}
	
	public static Vector geefWortels(Expressie e, Vector v)
	{	if(e instanceof Wortel || e instanceof NdeWortel)v.addElement(e);
		if(e.kind1!=null)Algebra.geefWortels(e.kind1,v);
		if(e.kind2!=null)Algebra.geefWortels(e.kind2,v);
		return v;
	}
	
	public static boolean isBreukPlusGetal(Expressie e)
	{	if(e instanceof Optelling
				   && e.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind1.geefWaarde())
				   && e.kind2 instanceof Deling
				   && e.kind2.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind1.geefWaarde())
				   && e.kind2.kind2 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind2.geefWaarde())
					)
		{	return true;
		}
		else return false;
	}
	public static Expressie bijBreukPlusGetalGeefBreuk(Expressie e)
	{	if(e instanceof Optelling
				   && e.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind1.geefWaarde())
				   && e.kind2 instanceof Deling
				   && e.kind2.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind1.geefWaarde())
				   && e.kind2.kind2 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind2.geefWaarde())
					)
		{	return new Deling(new BasisExpressie(e.kind1.geefWaarde() * e.kind2.kind2.geefWaarde() + e.kind2.kind1.geefWaarde()),e.kind2.kind2);
		}
		else return null;
	}
	
	/*toevoeging voor "Herleiden" 
	 *
	 */
	public static int geefAantalMachten(Expressie e)
	{	int aantal = 0;
		if(e.kind1!=null)aantal += geefAantalMachten(e.kind1);
		if(e.kind2!=null)aantal += geefAantalMachten(e.kind2);
		if(e instanceof Macht)aantal++;
		return aantal;
	}
	
	/*Maakt een produkt expressie met behulp van de expressies in de vector die 
	 *meegegeven worden.
	 */
	public static Expressie maakFactorenExpressie(Vector v)
	{	Expressie eGetal = new BasisExpressie(1);
		for(int i=0 ; i<v.size() ; i++)
		{	Expressie ee = (Expressie)v.elementAt(i);
			if(eval(ee)!=null)
			{	eGetal = evalueerGetalsExpressie(new Vermenigvuldiging(eGetal,ee));
				v.setElementAt(new BasisExpressie(1),i);
			}
		}
		for(int i=0 ; i<v.size() ; i++)
		{	Expressie ee = (Expressie)v.elementAt(i);
			if((ee instanceof BasisExpressie && Double.isNaN(ee.geefWaarde()))
			   || (ee instanceof Deling && ee.kind1.geefWaarde()==1 && ee.kind2 instanceof BasisExpressie && Double.isNaN(ee.kind2.geefWaarde())))
			{	String varnaam;
				int aantalF;
				Expressie basisE;
				if(ee instanceof BasisExpressie && Double.isNaN(ee.geefWaarde()))
				{	aantalF = 1;
					basisE = ee;
					varnaam = ((BasisExpressie)ee).basisString;
				}
				else
				{	aantalF = -1;
					basisE = ee.kind2;
					varnaam = ((BasisExpressie)ee.kind2).basisString;
				}
				for(int j=i+1 ; j<v.size() ; j++)
				{	Expressie eee = (Expressie)v.elementAt(j);
					if(eee instanceof BasisExpressie && ((BasisExpressie)eee).basisString!=null && ((BasisExpressie)eee).basisString.equals(varnaam))
					{	aantalF++;
						v.setElementAt(new BasisExpressie(1),j);
					}
					else if(eee instanceof Deling && eee.kind1.geefWaarde()==1 && eee.kind2 instanceof BasisExpressie && ((BasisExpressie)eee.kind2).basisString!=null && ((BasisExpressie)eee.kind2).basisString.equals(varnaam))
					{	aantalF--;
						v.setElementAt(new BasisExpressie(1),j);
					}
				}
				if(aantalF==0)
				{	v.setElementAt(new BasisExpressie(1),i);
				}
				if(aantalF==1)
				{	v.setElementAt(basisE,i);
				}
				else if(aantalF==-1)
				{	v.setElementAt(new Deling(new BasisExpressie(1),basisE),i);
				}
				else if(aantalF>1)
				{	ee = new Macht(basisE,new BasisExpressie(aantalF));	
					v.setElementAt(ee,i);
				}
				else if(aantalF<-1)
				{	ee = new Deling(new BasisExpressie(1),new Macht(basisE,new BasisExpressie(-aantalF)));	
					v.setElementAt(ee,i);
				}
			}
			else
			{	int aantalF;
				Expressie basisE;
				if(ee instanceof Deling && ee.kind1.geefWaarde()==1)
				{	aantalF = -1;
					basisE = ee.kind2;
				}
				else
				{	aantalF = 1;
					basisE = ee;
				}
				for(int j=i+1 ; j<v.size() ; j++)
				{	Expressie eee = (Expressie)v.elementAt(j);
					if(isGelijkwaardig(basisE,eee))
					{	aantalF++;
						v.setElementAt(new BasisExpressie(1),j);
					}
					else if(eee instanceof Deling && eee.kind1.geefWaarde()==1 && isGelijkwaardig(basisE,eee.kind2))
					{	aantalF--;
						v.setElementAt(new BasisExpressie(1),j);
					}
				}
				if(aantalF==0)
				{	v.setElementAt(new BasisExpressie(1),i);
				}
				if(aantalF==1)
				{	v.setElementAt(basisE,i);
				}
				else if(aantalF==-1)
				{	v.setElementAt(new Deling(new BasisExpressie(1),basisE),i);
				}
				else if(aantalF>1)
				{	ee = new Macht(basisE,new BasisExpressie(aantalF));	
					v.setElementAt(ee,i);
				}
				else if(aantalF<-1)
				{	ee = new Deling(new BasisExpressie(1),new Macht(basisE,new BasisExpressie(-aantalF)));	
					v.setElementAt(ee,i);
				}
			}
		}
		
		Expressie exp = null;
		Expressie expTeller = null;
		Expressie expNoemer = null;
		for(int i=v.size()-1 ; i>-1 ; i--)
		{	Expressie ee = (Expressie)v.elementAt(i);
			if(ee.geefWaarde()!=1 && !(ee instanceof Deling))
			{	if(expTeller==null)expTeller = ee;
				else expTeller = new Vermenigvuldiging(expTeller,ee);
			}
			else if(ee.geefWaarde()!=1 && ee instanceof Deling)
			{	if(expNoemer==null)expNoemer = ee.kind2;
				else expNoemer = new Vermenigvuldiging(expNoemer,ee.kind2);
			}
		}
		
		
		if(expTeller==null && expNoemer==null)
		{	exp = eGetal;
		}
		else if(eGetal.geefWaarde()==1)
		{	if(expNoemer==null)exp = expTeller;
			else if(expTeller==null)exp = new Deling(new BasisExpressie(1), expNoemer);
			else exp = new Deling(expTeller, expNoemer);
		}
		else if(eGetal.geefWaarde()==-1)
		{	if(expNoemer==null)exp = new Aftrekking(new BasisExpressie(0),expTeller);
			else if(expTeller==null)exp = new Aftrekking(new BasisExpressie(0),new Deling(new BasisExpressie(1), expNoemer));
			else exp = new Aftrekking(new BasisExpressie(0),new Deling(expTeller, expNoemer));
		}
		else if(eGetal instanceof Aftrekking && eGetal.kind1.geefWaarde()==0)
		{	Expressie tellerGetal = new BasisExpressie(eval(eGetal.kind2).x);
			Expressie noemerGetal = new BasisExpressie(eval(eGetal.kind2).y);
			if(expNoemer==null)exp = new Aftrekking(new BasisExpressie(0),new Vermenigvuldiging(eGetal.kind2,expTeller));
			else if(expTeller==null && noemerGetal.geefWaarde()==1)exp = new Aftrekking(new BasisExpressie(0),new Deling(tellerGetal,expNoemer));
			else if(expTeller==null)exp = new Aftrekking(new BasisExpressie(0),new Deling(tellerGetal,new Vermenigvuldiging(noemerGetal,expNoemer)));
			else if(tellerGetal.geefWaarde()==1)exp = new Aftrekking(new BasisExpressie(0),new Deling(expTeller,new Vermenigvuldiging(noemerGetal,expNoemer)));
			else if(noemerGetal.geefWaarde()==1)exp = new Aftrekking(new BasisExpressie(0),new Deling(new Vermenigvuldiging(tellerGetal,expTeller),expNoemer));
			else exp = new Aftrekking(new BasisExpressie(0),new Deling(new Vermenigvuldiging(tellerGetal,expTeller),new Vermenigvuldiging(noemerGetal,expNoemer)));
		}
		
		else
		{	Expressie tellerGetal = new BasisExpressie(eval(eGetal).x);
			Expressie noemerGetal = new BasisExpressie(eval(eGetal).y);
			if(expNoemer==null)exp = new Vermenigvuldiging(eGetal,expTeller);
			else if(expTeller==null && noemerGetal.geefWaarde()==1)exp = new Deling(tellerGetal,expNoemer);
			else if(expTeller==null)exp = new Deling(tellerGetal,new Vermenigvuldiging(noemerGetal,expNoemer));
			else if(tellerGetal.geefWaarde()==1)exp = new Deling(expTeller,new Vermenigvuldiging(noemerGetal,expNoemer));
			else if(noemerGetal.geefWaarde()==1)exp = new Deling(new Vermenigvuldiging(tellerGetal,expTeller),expNoemer);
			else exp = new Deling(new Vermenigvuldiging(tellerGetal,expTeller),new Vermenigvuldiging(noemerGetal,expNoemer));
		}
		
		return exp;
	}
	/*Vermenigvuldigt een expressie termsgewijs met een factor(expressie)
	 */
	public static Expressie vermenigvuldig(Expressie e, Expressie factor)
	{	Vector v = geefTermen(e, new Vector());
		for(int i=0 ; i<v.size() ; i++)
		{	Expressie exp = (Expressie)v.elementAt(i);
			exp = new Vermenigvuldiging(factor,exp);
			exp = herleid(exp);
			v.setElementAt(exp,i);
		}
		return Algebra.maakTermenExpressie(v);
	}
	/*Herleid een expressie: Gelijksoortige termen bij elkaar en binnen de
	 *termen de gelijksoortige factoren bij elkaar.
	 */
	public static Expressie herleid(Expressie e)
	{	if(e instanceof Wortel)return new Wortel(herleid(e.kind1));
		if(e instanceof Macht && Double.isNaN(e.kind2.geefWaarde()))return new Macht(herleid(e.kind1),herleid(e.kind2));
		if(e instanceof Log)return new Log(herleid(e.kind1));
		if(e instanceof Ln)return new Ln(herleid(e.kind1));
//GWT nodig??
/*		
		if(e instanceof Sinus)return new Sinus(herleid(e.kind1));
		if(e instanceof Cosinus)return new Cosinus(herleid(e.kind1));
		if(e instanceof Tangens)return new Tangens(herleid(e.kind1));
		if(e instanceof ArcSinus)return new ArcSinus(herleid(e.kind1));
		if(e instanceof ArcCosinus)return new ArcCosinus(herleid(e.kind1));
		if(e instanceof ArcCosinus)return new ArcCosinus(herleid(e.kind1));
*/		
		if(e instanceof NdeWortel)return new NdeWortel(herleid(e.kind1),herleid(e.kind2));
		if(e instanceof NdeLog)return new NdeLog(herleid(e.kind1),herleid(e.kind2));
		if(e instanceof E)return e;
		if(e instanceof PI)return e;
		Vector v = Algebra.geefTermen(e,new Vector());
		v = sorteerTermen(v);
		return Algebra.maakTermenExpressie(v);
	}
	/*Herleid een expressie: Herleid zaken als 1*... , +0 enz.
	 */
	public static Expressie herleidMild(Expressie e)
	{	if(e.kind1!=null)e.kind1 = herleidMild(e.kind1);
		if(e.kind2!=null)e.kind2 = herleidMild(e.kind2);
		if(e.isWaarde())
		{	e = evalueerGetalsExpressie(e);
			//System.out.println(e.toString());
		}
		
		else if(e instanceof Vermenigvuldiging)
		{	if(e.kind1.geefWaarde()==1) return e.kind2;
		 	else if(e.kind1.geefWaarde()==0 || e.kind2.geefWaarde()==0) return new BasisExpressie(0);
			else if(e.kind2.geefWaarde()==1) return e.kind1;
			else if(e.kind1.geefWaarde()==-1) return new Aftrekking(new BasisExpressie(0),e.kind2);
			else if(e.kind2.geefWaarde()==-1) return new Aftrekking(new BasisExpressie(0),e.kind1);
			else if(e.kind1 instanceof Aftrekking && e.kind1.kind1.geefWaarde()==0) return new Aftrekking(new BasisExpressie(0),new Vermenigvuldiging(e.kind1.kind2,e.kind2));
			else if(e.kind2 instanceof Aftrekking && e.kind2.kind1.geefWaarde()==0) return new Aftrekking(new BasisExpressie(0),new Vermenigvuldiging(e.kind1,e.kind2.kind2));
		}
		else if(e instanceof Optelling)
		{	if(e.kind1.geefWaarde()==0)return e.kind2;
			else if(e.kind2.geefWaarde()==0)return e.kind1;
			else if(e.kind2 instanceof Aftrekking && e.kind2.kind1.geefWaarde()==0)return new Aftrekking(e.kind1, herleidMild(e.kind2.kind2));
		}
		else if(e instanceof Aftrekking)
		{	if(e.kind2.geefWaarde()==0)return e.kind1;
			else if(e.kind1.geefWaarde()==0 && e.kind2 instanceof Aftrekking && e.kind2.kind1.geefWaarde()==0)return herleidMild(e.kind2.kind2);
			else if(e.kind2 instanceof Aftrekking && e.kind2.kind1.geefWaarde()==0)return new Optelling(e.kind1, herleidMild(e.kind2.kind2));
		}
		else if(e instanceof Macht)
		{	if(e.kind2.geefWaarde()==1) return e.kind1;
			if(e.kind2.geefWaarde()==0) return new BasisExpressie(1);
		}
		else if(e instanceof NdeWortel)
		{	if(e.kind2.geefWaarde()==1) return e.kind1;
			if(e.kind2.geefWaarde()==2) return new Wortel(e.kind1);
		}
		else if(e instanceof Deling)
		{	if(e.kind2.geefWaarde()==1) return e.kind1;
		}
		else if(e instanceof BasisExpressie && e.geefWaarde()<0)
		{	return new Aftrekking(new BasisExpressie(0),new BasisExpressie(-e.geefWaarde()));
		}
		return e;
	}
	/* Bepaalt of een expressie zonder haakjes is geschreven
	 */
	public static boolean expanded(Expressie e)
	{	Vector v = Algebra.geefTermen(e,new Vector());
		for(int i=0 ; i<v.size() ; i++)
		{	Expressie ee = (Expressie)v.elementAt(i);
			Vector u = geefFactoren(ee, new Vector());
			for(int j=0 ; j<u.size() ; j++)
			{	Expressie eee = (Expressie)u.elementAt(j);
				if(Double.isNaN(eee.geefWaarde())
				   && !(eee instanceof BasisExpressie))
				{	return false;
				}
			}
		}
		return true;
	}
	
	
	/*Werkt alle haakjes weg en stopt alle termen (ongesorteerd) in een vector.
	 */
	public static Vector expand(Expressie e, Vector v)
	{	
		if(e instanceof Optelling)
		{	
			v = expand(e.kind1,v);
			v = expand(e.kind2,v);
		}
		else if((e instanceof Aftrekking))
		{	
			v = expand(e.kind1,v);
			v = expand(new Vermenigvuldiging(new BasisExpressie(-1),e.kind2),v);
		}
		else
		{	if(e instanceof Vermenigvuldiging)
			{	Vector u1 = expand(e.kind1,new Vector());
				Vector u2 = expand(e.kind2,new Vector());
				for(int i=0 ; i<u1.size() ; i++)
				{	for(int j=0 ; j<u2.size() ; j++)
					{	Expressie e1 = (Expressie)u1.elementAt(i);
						Expressie e2 = (Expressie)u2.elementAt(j);
						v.addElement(new Vermenigvuldiging(e1,e2));
					}
				}
			}
			else if(e instanceof Deling)
			{	Vector u1 = expand(e.kind1,new Vector());
				for(int i=0 ; i<u1.size() ; i++)
				{	Expressie e1 = (Expressie)u1.elementAt(i);
					v.addElement(new Deling(e1,e.kind2));
				}
			}
			else if(e instanceof Macht && !Double.isNaN(e.kind2.geefWaarde()))//nog geen controle op exponenten als gehele getallen
			{	if(e.kind2.geefWaarde()==1)
				{	v = expand(e.kind1,v);
				}
				else if(e.kind2.geefWaarde()==-1)
				{	v = expand(new Deling(new BasisExpressie(1),e.kind1),v);
				}
				
				
				Vector u1 = new Vector();
				Vector u2 = new Vector();
				if(e.kind2.geefWaarde()==2)
				{	u1 = expand(e.kind1,new Vector());
					u2 = expand(e.kind1,new Vector());
				}
				else if(e.kind2.geefWaarde()>2)
				{	u1 = expand(e.kind1,new Vector());
					Expressie exp = new Macht(e.kind1, new BasisExpressie(e.kind2.geefWaarde()-1));
					u2 = expand(exp,new Vector());
				}
				else if(e.kind2.geefWaarde()==-2)
				{	u1 = expand(new Deling(new BasisExpressie(1),e.kind1),new Vector());
					u2 = expand(new Deling(new BasisExpressie(1),e.kind1),new Vector());
				}
				else if(e.kind2.geefWaarde()<-2)
				{	u1 = expand(new Deling(new BasisExpressie(1),e.kind1),new Vector());
					Expressie exp = new Macht(e.kind1, new BasisExpressie(e.kind2.geefWaarde()+1));
					u2 = expand(exp,new Vector());
				}
				for(int i=0 ; i<u1.size() ; i++)
				{	for(int j=0 ; j<u2.size() ; j++)
					{	Expressie e1 = (Expressie)u1.elementAt(i);
						Expressie e2 = (Expressie)u2.elementAt(j);
						v.addElement(new Vermenigvuldiging(e1,e2));
					}
				}
			}
			else
			{	if(e.geefWaarde()!=0)v.addElement(e);
			}
		}
		
		/**/
		
		return v;
	}
	/*Werkt de haakjes weg.
	 *Doet dat per term. Herleid ook elke term.
	 *Kan daarna eventueel als geheel herleid worden met herleid();
	 */
	public static Expressie verwijderHaakjes(Expressie e)
	{	Vector v = geefTermen(e, new Vector());
		Vector w = new Vector();
		for(int i=0 ; i<v.size() ; i++)
		{	Expressie exp = (Expressie)v.elementAt(i);
			Vector u = expand(exp,new Vector());
			u = sorteerTermen(u);
			for(int j=0 ; j<u.size() ; j++)
			{	w.addElement((Expressie)u.elementAt(j));
			}
		}
		return Algebra.maakTermenExpressie(w);
	}
	/*Geeft de (indien mogelijk herleide)wortel van een expressie
	 *Herleiding vind plaats als de exponenten van de variabele factoren even zijn
	 *en bij een getalsexpressie indien het kwadraat van een rationaal getal is.
	 */
	public static Expressie geefWortel(Expressie e)
	{	String[] varNamen = geefVarNamen(e);
		Vector t = new Vector();
		Vector extraTermen = new Vector();
		int[] exponenten = new int[varNamen.length];
		int[] exponentenExtra = new int[100]; // die 100 is arbitrair
		
		for(int i=0 ; i<varNamen.length ; i++)
		{	exponenten[i]=0;
		}
		for(int i=0 ; i<100 ; i++)
		{	exponentenExtra[i]=0;
		}
	
		Vector u;
		u = geefFactoren(e,new Vector());
		for(int k=0 ; k<u.size() ; k++)
		{	Expressie exp = (Expressie)u.elementAt(k);
			for(int i=0 ; i<varNamen.length ; i++)
			{	if(exp instanceof BasisExpressie  && ((BasisExpressie)exp).basisString!=null  && ((BasisExpressie)exp).basisString.equals(varNamen[i]))
				{	exponenten[i]++;
					u.setElementAt(new BasisExpressie(1),k);
				}
				else if(exp instanceof Deling && exp.kind1.geefWaarde()==1  && exp.kind2 instanceof BasisExpressie  && ((BasisExpressie)exp.kind2).basisString!=null  && ((BasisExpressie)exp.kind2).basisString.equals(varNamen[i]))
				{	exponenten[i]--;
					u.setElementAt(new BasisExpressie(1),k);
				} 
			}				
			if(eval(exp)==null && !(exp instanceof BasisExpressie) && !(exp instanceof Deling && exp.kind1.geefWaarde()==1  && exp.kind2 instanceof BasisExpressie)) 
			{	boolean kwamAlVoor = false;
				for(int i=0 ; i<extraTermen.size() ; i++)
				{	if(isGelijkwaardig(exp,(Expressie)extraTermen.elementAt(i)))
					{	exponentenExtra[i]++;
						u.setElementAt(new BasisExpressie(1),k);
						kwamAlVoor = true;
					}
					else if(exp instanceof Deling && exp.kind1.geefWaarde()==1 && isGelijkwaardig(exp.kind2,(Expressie)extraTermen.elementAt(i)))
					{	exponentenExtra[i]--;
						u.setElementAt(new BasisExpressie(1),k);
						kwamAlVoor = true;
					}
				}
				if(!kwamAlVoor)
				{	if(exp instanceof Deling && exp.kind1.geefWaarde()==1)
					{	extraTermen.addElement(exp.kind2);
						exponentenExtra[extraTermen.size()-1]--;
						u.setElementAt(new BasisExpressie(1),k);
					}
					else
					{	extraTermen.addElement(exp);
						exponentenExtra[extraTermen.size()-1]++;
						u.setElementAt(new BasisExpressie(1),k);
					}
				}
				
			}	
				
		}
		Expressie getalsExpressie = maakFactorenExpressie(u);
		Expressie wortelGetal = evalueerGetalsExpressie(new Wortel(getalsExpressie));
		
		boolean isKwadraat = true;
		if(wortelGetal instanceof Wortel)isKwadraat = false;
		for(int i=0 ; i<varNamen.length ; i++)
		{	if(exponenten[i]%2==1)isKwadraat = false;
		}
		for(int i=0 ; i<100 ; i++)
		{	if(exponentenExtra[i]%2==1)isKwadraat = false;
		}
		
		if(isKwadraat)
		{	Vector expVector = new Vector();
			expVector.addElement(wortelGetal);
			for(int i=0 ; i<varNamen.length ; i++)
			{	if(exponenten[i]/2==1)
				{	expVector.insertElementAt(new BasisExpressie(varNamen[i]),0);
				}
				else if(exponenten[i]/2>1)
				{	for(int k=0;k<exponenten[i]/2;k++)
					{	expVector.insertElementAt(new BasisExpressie(varNamen[i]),0);
					}
				}
				else if(exponenten[i]==0)
				{	expVector.insertElementAt(new BasisExpressie(1),0);
				}
				else if(exponenten[i]/2==-1)
				{	expVector.insertElementAt(new Deling(new BasisExpressie(1),new BasisExpressie(varNamen[i])),0);
				}
				else if(exponenten[i]/2<-1)
				{	for(int k=0;k<-exponenten[i]/2;k++)
					{	expVector.insertElementAt(new Deling(new BasisExpressie(1),new BasisExpressie(varNamen[i])),0);
					}
				}
			}
			for(int i=0 ; i<extraTermen.size() ; i++)
			{	if(exponentenExtra[i]/2==1)
				{	expVector.insertElementAt((Expressie)extraTermen.elementAt(i),0);
				}
				else if(exponentenExtra[i]>1)
				{	for(int k=0;k<exponentenExtra[i]/2;k++)
					{	expVector.insertElementAt((Expressie)extraTermen.elementAt(i),0);
					}
				}
				else if(exponentenExtra[i]==0)
				{	expVector.insertElementAt(new BasisExpressie(1),0);
				}
				else if(exponentenExtra[i]/2==-1)
				{	expVector.insertElementAt(new Deling(new BasisExpressie(1),(Expressie)extraTermen.elementAt(i)),0);
				}
				else if(exponentenExtra[i]/2<-1)
				{	for(int k=0;k<-exponentenExtra[i]/2;k++)
					{	expVector.insertElementAt(new Deling(new BasisExpressie(1),(Expressie)extraTermen.elementAt(i)),0);
					}
				}
			}
			return maakFactorenExpressie(expVector);
		}
		else return new Wortel(e);
		
	
	}
	/*Ontbind een 2e graads expressie.met 1 variabele.
	 *Lukt alleen als e=0 rationale oplossingen heeft
	 *expressies met meer variabelen en/of van een andere graad
	 *worden ontbonden met ontbindExtra(e)
	 */
	public static Expressie ontbindExtra(Expressie e)
	{	String[] varnamen = geefVarNamen(e);
	
		Vector v = geefTermen(e, new Vector());
		if(v.size()==2 && (geefCoefficienten(e)==null || geefCoefficienten(e).length>3))
		{	Expressie e1 = (Expressie)v.elementAt(0);
			Expressie e2 = (Expressie)v.elementAt(1);
			if(e1 instanceof Aftrekking && e1.kind1.geefWaarde()==0 && !(geefWortel(e1.kind2)instanceof Wortel) && !(geefWortel(e2)instanceof Wortel))
			{	return new Vermenigvuldiging(new Optelling(geefWortel(e2),geefWortel(e1.kind2)),new Aftrekking(geefWortel(e2),geefWortel(e1.kind2)));
			}
			else if(e2 instanceof Aftrekking && e2.kind1.geefWaarde()==0 && !(geefWortel(e2.kind2)instanceof Wortel) && !(geefWortel(e1)instanceof Wortel))
			{	return new Vermenigvuldiging(new Optelling(geefWortel(e1),geefWortel(e2.kind2)),new Aftrekking(geefWortel(e1),geefWortel(e2.kind2)));
			}
		}
		else if(v.size()==3 && (geefCoefficienten(e)==null || geefCoefficienten(e).length>3))
		{	Expressie e1 = (Expressie)v.elementAt(0);
			Expressie e2 = (Expressie)v.elementAt(1);
			Expressie e3 = (Expressie)v.elementAt(2);
			Expressie e1w = geefWortel(e1);
			Expressie e2w = geefWortel(e2);
			Expressie e3w = geefWortel(e3);
			if(!(e1w instanceof Wortel) && !(e2w instanceof Wortel))
			{	if(isGelijkwaardig(e3,new Vermenigvuldiging(new BasisExpressie(2),new Vermenigvuldiging(e1w,e2w))))
				{	return new Macht(new Optelling(e1w,e2w),new BasisExpressie(2));
				}
				else if(isGelijkwaardig(e3,new Vermenigvuldiging(new BasisExpressie(-2),new Vermenigvuldiging(e1w,e2w))))
				{	return new Macht(new Aftrekking(e1w,e2w),new BasisExpressie(2));
				}				
			}
			else if(!(e1w instanceof Wortel) && !(e3w instanceof Wortel))
			{	if(isGelijkwaardig(e2,new Vermenigvuldiging(new BasisExpressie(2),new Vermenigvuldiging(e1w,e3w))))
				{	return new Macht(new Optelling(e1w,e3w),new BasisExpressie(2));
				}
				else if(isGelijkwaardig(e2,new Vermenigvuldiging(new BasisExpressie(-2),new Vermenigvuldiging(e1w,e3w))))
				{	return new Macht(new Aftrekking(e1w,e3w),new BasisExpressie(2));
				}				
			}
			else if(!(e2w instanceof Wortel) && !(e3w instanceof Wortel))
			{	if(isGelijkwaardig(e1,new Vermenigvuldiging(new BasisExpressie(2),new Vermenigvuldiging(e2w,e3w))))
				{	return new Macht(new Optelling(e2w,e3w),new BasisExpressie(2));
				}
				else if(isGelijkwaardig(e1,new Vermenigvuldiging(new BasisExpressie(-2),new Vermenigvuldiging(e2w,e3w))))
				{	return new Macht(new Aftrekking(e2w,e3w),new BasisExpressie(2));
				}				
			}
			else
			{	e = vermenigvuldig(e,new BasisExpressie(-1));
				v = geefTermen(e, new Vector());
				e1 = (Expressie)v.elementAt(0);
				e2 = (Expressie)v.elementAt(1);
				e3 = (Expressie)v.elementAt(2);
				e1w = geefWortel(e1);
				e2w = geefWortel(e2);
				e3w = geefWortel(e3);
				if(!(e1w instanceof Wortel) && !(e2w instanceof Wortel))
				{	if(isGelijkwaardig(e3,new Vermenigvuldiging(new BasisExpressie(2),new Vermenigvuldiging(e1w,e2w))))
					{	return new Aftrekking(new BasisExpressie(0),new Macht(new Optelling(e1w,e2w),new BasisExpressie(2)));
					}
					else if(isGelijkwaardig(e3,new Vermenigvuldiging(new BasisExpressie(-2),new Vermenigvuldiging(e1w,e2w))))
					{	return new Aftrekking(new BasisExpressie(0),new Macht(new Aftrekking(e1w,e2w),new BasisExpressie(2)));
					}				
				}
				else if(!(e1w instanceof Wortel) && !(e3w instanceof Wortel))
				{	if(isGelijkwaardig(e2,new Vermenigvuldiging(new BasisExpressie(2),new Vermenigvuldiging(e1w,e3w))))
					{	return new Aftrekking(new BasisExpressie(0),new Macht(new Optelling(e1w,e3w),new BasisExpressie(2)));
					}
					else if(isGelijkwaardig(e2,new Vermenigvuldiging(new BasisExpressie(-2),new Vermenigvuldiging(e1w,e3w))))
					{	return new Aftrekking(new BasisExpressie(0),new Macht(new Aftrekking(e1w,e3w),new BasisExpressie(2)));
					}				
				}
				else if(!(e2w instanceof Wortel) && !(e3w instanceof Wortel))
				{	if(isGelijkwaardig(e1,new Vermenigvuldiging(new BasisExpressie(2),new Vermenigvuldiging(e2w,e3w))))
					{	return new Aftrekking(new BasisExpressie(0),new Macht(new Optelling(e2w,e3w),new BasisExpressie(2)));
					}
					else if(isGelijkwaardig(e1,new Vermenigvuldiging(new BasisExpressie(-2),new Vermenigvuldiging(e2w,e3w))))
					{	return new Aftrekking(new BasisExpressie(0),new Macht(new Aftrekking(e2w,e3w),new BasisExpressie(2)));
					}				
				}
				e = vermenigvuldig(e,new BasisExpressie(-1));
			}
			
			
		}
		
		//controle op aantal variabelen
		if(varnamen.length>1 || varnamen.length<1)
		{	return(e);
			//return(ontbindExtra(e));
		}
		
		//varnaam vastleggen
		String varnaam = varnamen[0];
		
		//coefficienten bepalen
		Expressie[] exp = geefCoefficientenExpressies(herleid(verwijderHaakjes(e)));
		if(exp==null)
		{	return(e);
			//return(ontbindExtra(e));
		}
		double[] coeff = new double[exp.length];
		for(int i=0 ; i<exp.length ; i++)
		{	coeff[i] = exp[i].geefWaarde();
		}
		//graad ongelijk aan 2
		if(coeff.length!=3)
		{	return(e);
			//return(ontbindExtra(e));
		}
		
		
		
		//coefficienten noemen we a,b en c
		double a = coeff[2];
		double b = coeff[1];
		double c = coeff[0];
		
		// de ggd van a,b en c: factor,  wordt uitgedeeld 
		double factor = 1;
		if(a<0)
		{	a = -a;
			b = -b;
			c = -c;
			exp[2] = vermenigvuldig(exp[2], new BasisExpressie(-1));
			exp[1] = vermenigvuldig(exp[1], new BasisExpressie(-1));
			exp[0] = vermenigvuldig(exp[0], new BasisExpressie(-1));
			factor = -1;
		}
		if(Math.rint(a)-a==0 && Math.rint(b)-b==0 && Math.rint(c)-c==0)
		{	int ggd = (int)ggd((int)a,ggd((int)b,(int)c));
			a/=ggd; 
			b/=ggd; 
			c/=ggd;
			exp[2] = vermenigvuldig(exp[2], new Deling(new BasisExpressie(1),new BasisExpressie(ggd) ));
			exp[1] = vermenigvuldig(exp[1], new Deling(new BasisExpressie(1),new BasisExpressie(ggd) ));
			exp[0] = vermenigvuldig(exp[0], new Deling(new BasisExpressie(1),new BasisExpressie(ggd) ));
			factor=factor*ggd;
		}
		
		//D<0
		double d = b*b-4*a*c;
		if(d<0) 
		{	return(e);
			//return(ontbindExtra(e));
		}
		
		//D=0 Schrijf als p(x+q)^2 (
		if(d<0.000000001)
		{	Expressie factorExpressie = vermenigvuldig(exp[2],new BasisExpressie(factor));
			exp[1] = evalueerGetalsExpressie(new Vermenigvuldiging(exp[1],new Deling(new BasisExpressie(1), exp[2])));
			exp[0] = evalueerGetalsExpressie(new Vermenigvuldiging(exp[0],new Deling(new BasisExpressie(1), exp[2])));
			Expressie e1 = new Optelling(new BasisExpressie(varnaam),evalueerGetalsExpressie(new Vermenigvuldiging(exp[1],new Deling(new BasisExpressie(1), new BasisExpressie(2)))));
			e1 = herleid(e1);
			Expressie e2 = new Macht(e1, new BasisExpressie(2));
			Expressie ee = vermenigvuldig(e2,factorExpressie);
			return ee;
		}
		
		//als de coefficienten rationaal zijn,dan eerst een factor of breuk 
		//buiten haakjes halen. (nieuwe coefficienten geheel)
		int kgv = 1;
		if(eval(exp[0])!=null && eval(exp[1])!=null && eval(exp[2])!=null)
		{	int d0 = (int)eval(exp[0]).y;
			int d1 = (int)eval(exp[1]).y;
			int d2 = (int)eval(exp[2]).y;
			kgv = (int)(d0*d1/ggd(d0,d1));
			kgv = (int)(kgv*d2/ggd(kgv,d2));
		}
		else
		{	return(e);
			//return(ontbindExtra(e));
		}
		exp[2] = vermenigvuldig(exp[2], new BasisExpressie(kgv));
		exp[1] = vermenigvuldig(exp[1], new BasisExpressie(kgv));
		exp[0] = vermenigvuldig(exp[0], new BasisExpressie(kgv));
		Expressie factorExpressie = new Deling(new BasisExpressie(factor),new BasisExpressie(kgv));
		
		//als sqrt(D) rationaal is, dan ontbinden.
		Expressie discr = new Aftrekking(new Vermenigvuldiging(exp[1],exp[1]),new Vermenigvuldiging(new BasisExpressie(4),new Vermenigvuldiging(exp[2], exp[0])));
		if(eval(new Wortel(discr))==null)
		{	return(e);
			//return(ontbindExtra(e));
		}
		else
		{	Expressie expX1 = evalueerGetalsExpressie(new Optelling(evalueerGetalsExpressie(new Vermenigvuldiging(new BasisExpressie(-1),exp[1])),new Wortel(discr)));
			expX1 = evalueerGetalsExpressie(new Vermenigvuldiging(expX1,new Deling(new BasisExpressie(1), new Vermenigvuldiging(new BasisExpressie(2),exp[2]))));
			Expressie expX2 = evalueerGetalsExpressie(new Aftrekking(evalueerGetalsExpressie(new Vermenigvuldiging(new BasisExpressie(-1),exp[1])),new Wortel(discr)));
			expX2 = evalueerGetalsExpressie(new Vermenigvuldiging(expX2,new Deling(new BasisExpressie(1), new Vermenigvuldiging(new BasisExpressie(2),exp[2]))));
			
			int deler1 = (int)eval(expX1).y;
			Expressie e1 = new Aftrekking(new Vermenigvuldiging(new BasisExpressie(deler1),new BasisExpressie(varnaam)),evalueerGetalsExpressie(new Vermenigvuldiging(expX1,new BasisExpressie(deler1))));
			e1 = herleid(e1);
			
			int deler2 = (int)eval(expX2).y;
			Expressie e2 = new Aftrekking(new Vermenigvuldiging(new BasisExpressie(deler2),new BasisExpressie(varnaam)),evalueerGetalsExpressie(new Vermenigvuldiging(expX2,new BasisExpressie(deler2))));
			e2 = herleid(e2);
			
			Expressie ee = new Vermenigvuldiging(e1,e2);
			ee = vermenigvuldig(ee,factorExpressie);
			ee = herleid(ee);
			return ee;
		}
	}
	/*Brengt factoren buiten haakjes van een willekeurige expressie evt met 
	 *meer variabelen. Brengt ook onder één noemer indien nodig.
	 */
	public static Expressie ontbind(Expressie e)
	{	//Voor alle termen wordt bekeken uit welke bouwstenen ze zijn opgebouwd.
		//Daartoe worden van de termen de aanwezige factoren bepaald met
		//geefFactoren(..). De factoren worden per soort per term geteld,
		//deze gegevens worden opgeslagen in exponenten[][] (de variabelen)
		//en in exponentenExtra[][] (andere niet getals expressies die 
		//als factor voorkomen)
		//Deze andere voorkomende factoren worden bovendien geinventariseerd
		//en opgeslagen in de Vector extraTermen.
		//Na te zijn geteld worden de factoren in de termen vervangen door 1,
		//zodat aan het eind alleen nog een getalsexpressie overblijft
		
		String[] varNamen = geefVarNamen(e);
		Vector v = geefTermen(e,new Vector());
		Vector t = new Vector();
		Vector extraTermen = new Vector();
		int[][] exponenten = new int[varNamen.length][v.size()];
		int[][] exponentenExtra = new int[100][v.size()]; // die 100 is arbitrair
		int[] factorExponenten = new int[varNamen.length];
		int[] factorExponentenExtra = new int[100];
		for(int i=0 ; i<varNamen.length ; i++)
		{	for(int j=0 ; j<v.size() ; j++)
			{	exponenten[i][j]=0;
			}
		}
		for(int i=0 ; i<100 ; i++)
		{	for(int j=0 ; j<v.size() ; j++)
			{	exponentenExtra[i][j]=0;
			}
		}
		for(int i=0 ; i<varNamen.length ; i++)
		{	factorExponenten[i]=0;
		}
		for(int i=0 ; i<100 ; i++)
		{	factorExponentenExtra[i]=0;
		}
		for(int j=0 ; j<v.size() ; j++)
		{	Vector u;
			u = geefFactoren((Expressie)v.elementAt(j),new Vector());
			/*for(int k=0 ; k<u.size() ; k++)
			{	Expressie exp = (Expressie)u.elementAt(k);
				if(eval(exp)==null && !(exp instanceof BasisExpressie) && !(exp instanceof Deling && exp.kind1.geefWaarde()==1  && exp.kind2 instanceof BasisExpressie)) 
				{	Expressie ontbindingExtra = ontbindExtra(exp);
					Vector vOntbinding = geefFactoren(ontbindingExtra, new Vector());
					if(vOntbinding.size()>1)
					{	u.setElementAt(new BasisExpressie(1),k);
						for(int m=0 ; m<vOntbinding.size() ; m++)
						{	u.addElement(vOntbinding.elementAt(m));
						}
					}
				}
			}*/
			for(int k=0 ; k<u.size() ; k++)
			{	Expressie exp = (Expressie)u.elementAt(k);
				for(int i=0 ; i<varNamen.length ; i++)
				{	if(exp instanceof BasisExpressie && ((BasisExpressie)exp).basisString!=null  && ((BasisExpressie)exp).basisString.equals(varNamen[i]))
					{	exponenten[i][j]++;
						u.setElementAt(new BasisExpressie(1),k);
					}
					else if(exp instanceof Deling && exp.kind1.geefWaarde()==1  && exp.kind2 instanceof BasisExpressie  && ((BasisExpressie)exp.kind2).basisString!=null  && ((BasisExpressie)exp.kind2).basisString.equals(varNamen[i]))
					{	exponenten[i][j]--;
						u.setElementAt(new BasisExpressie(1),k);
					} 
				}
				
				if(eval(exp)==null && !(exp instanceof BasisExpressie) && !(exp instanceof Deling && exp.kind1.geefWaarde()==1  && exp.kind2 instanceof BasisExpressie)) 
				{	boolean kwamAlVoor = false;
					for(int i=0 ; i<extraTermen.size() ; i++)
					{	if(isGelijkwaardig(exp,(Expressie)extraTermen.elementAt(i)))
						{	exponentenExtra[i][j]++;
							u.setElementAt(new BasisExpressie(1),k);
							kwamAlVoor = true;
						}
						else if(exp instanceof Deling && exp.kind1.geefWaarde()==1 && isGelijkwaardig(exp.kind2,(Expressie)extraTermen.elementAt(i)))
						{	exponentenExtra[i][j]--;
							u.setElementAt(new BasisExpressie(1),k);
							kwamAlVoor = true;
						}
					}
					if(!kwamAlVoor)
					{	if(exp instanceof Deling && exp.kind1.geefWaarde()==1)
						{	extraTermen.addElement(exp.kind2);
							exponentenExtra[extraTermen.size()-1][j]--;
							u.setElementAt(new BasisExpressie(1),k);
						}
						else
						{	extraTermen.addElement(exp);
							exponentenExtra[extraTermen.size()-1][j]++;
							u.setElementAt(new BasisExpressie(1),k);
						}
					}
				
				}	
				
			}
			v.setElementAt(maakFactorenExpressie(u),j);
		}
		
		//Voor elke variabele wordt kleinste exponent die onder de termen 
		//voorkomt bepaald. Deze wordt per variabele opgeslagen in 
		//factorExponenten[] (zoveel kan straks buiten haakjes gehaald worden)
		//exponenten[][] wordt verlaagt met deze waarde.
		//Hetzelfde gebeurt met de extraTermen.
		
		for(int i=0 ; i<varNamen.length ; i++)
		{	int minAantalF = 1000;
			for(int j=0 ; j<v.size() ; j++)
			{	if(exponenten[i][j]<minAantalF)
				{	minAantalF = exponenten[i][j];
				}
			}
			factorExponenten[i] = minAantalF;
			for(int j=0 ; j<v.size() ; j++)
			{	exponenten[i][j] = exponenten[i][j] - minAantalF;
			}
		}
		for(int i=0 ; i<extraTermen.size() ; i++)
		{	int minAantalF = 1000;
			for(int j=0 ; j<v.size() ; j++)
			{	if(exponentenExtra[i][j]<minAantalF)
				{	minAantalF = exponentenExtra[i][j];
				}
			}
			factorExponentenExtra[i] = minAantalF;
			for(int j=0 ; j<v.size() ; j++)
			{	exponentenExtra[i][j] = exponentenExtra[i][j] - minAantalF;
			}
		}
		
		/*for(int i=0 ; i<varNamen.length ; i++)
		{	int minAantalF = 1000;
			int maxAantalF = -1000;
			for(int j=0 ; j<v.size() ; j++)
			{	if(exponenten[i][j]<minAantalF)
				{	minAantalF = exponenten[i][j];
				}
				if(exponenten[i][j]>maxAantalF)
				{	maxAantalF = exponenten[i][j];
				}
			}
			if(minAantalF<0 && maxAantalF>=0)minAantalF = 0;
			else if(minAantalF<0 && maxAantalF<0)minAantalF = maxAantalF;
			factorExponenten[i] = minAantalF;
			for(int j=0 ; j<v.size() ; j++)
			{	exponenten[i][j] = exponenten[i][j] - minAantalF;
			}
		}
		for(int i=0 ; i<extraTermen.size() ; i++)
		{	int minAantalF = 1000;
			int maxAantalF = -1000;
			for(int j=0 ; j<v.size() ; j++)
			{	if(exponentenExtra[i][j]<minAantalF)
				{	minAantalF = exponentenExtra[i][j];
				}
				if(exponentenExtra[i][j]>maxAantalF)
				{	maxAantalF = exponentenExtra[i][j];
				}
			}
			if(minAantalF<0 && maxAantalF>=0)minAantalF = 0;
			else if(minAantalF<0 && maxAantalF<0)minAantalF = maxAantalF;
			factorExponentenExtra[i] = minAantalF;
			for(int j=0 ; j<v.size() ; j++)
			{	exponentenExtra[i][j] = exponentenExtra[i][j] - minAantalF;
			}
		}*/
		
		
		//de termen (het deel binnen de 'haakjes')worden opnieuw opgebouwd 
		//mbv de gegevens van exponenten[][] en exponentenExtra[][]
		//en tijdelijk opgeslagen in
		//expVector. De nieuwe termen worden opgeslagen in vector t
		//en vervolgens samengevoegd tot de expressie e2
		
		int ggdTeller = 0;
		int ggdNoemer = 0;
		int kgvNoemer = 1;
		boolean heeftGGD = true;
		for(int j=0 ; j<v.size() ; j++)
		{	if(((Expressie)v.elementAt(j)).geefWaarde() != 0)
			{	Vector expVector = new Vector();
				//Eerst wordt de overgebleven getalsexpressie toegevoegd
				expVector.addElement(v.elementAt(j));
				for(int i=0 ; i<varNamen.length ; i++)
				{	if(exponenten[i][j]==1)
					{	expVector.insertElementAt(new BasisExpressie(varNamen[i]),0);
					}
					else if(exponenten[i][j]>1)
					{	for(int k=0;k<exponenten[i][j];k++)
						{	expVector.insertElementAt(new BasisExpressie(varNamen[i]),0);
						}
					}
					else if(exponenten[i][j]==0)
					{	expVector.insertElementAt(new BasisExpressie(1),0);
					}
					else if(exponenten[i][j]==-1)
					{	expVector.insertElementAt(new Deling(new BasisExpressie(1),new BasisExpressie(varNamen[i])),0);
					}
					else if(exponenten[i][j]<-1)
					{	for(int k=0;k<-exponenten[i][j];k++)
						{	expVector.insertElementAt(new Deling(new BasisExpressie(1),new BasisExpressie(varNamen[i])),0);
						}
					}
				}
				for(int i=0 ; i<extraTermen.size() ; i++)
				{	if(exponentenExtra[i][j]==1)
					{	expVector.insertElementAt((Expressie)extraTermen.elementAt(i),0);
					}
					else if(exponentenExtra[i][j]>1)
					{	for(int k=0;k<exponentenExtra[i][j];k++)
						{	expVector.insertElementAt((Expressie)extraTermen.elementAt(i),0);
						}
					}
					else if(exponentenExtra[i][j]==0)
					{	expVector.insertElementAt(new BasisExpressie(1),0);
					}
					else if(exponentenExtra[i][j]==-1)
					{	expVector.insertElementAt(new Deling(new BasisExpressie(1),(Expressie)extraTermen.elementAt(i)),0);
					}
					else if(exponentenExtra[i][j]<-1)
					{	for(int k=0;k<-exponentenExtra[i][j];k++)
						{	expVector.insertElementAt(new Deling(new BasisExpressie(1),(Expressie)extraTermen.elementAt(i)),0);
						}
					}
				}
				
				if(eval((Expressie)v.elementAt(j))==null)
				{	heeftGGD = false;
				}		
				else
				{	int teller = (int)eval((Expressie)v.elementAt(j)).x;
					int noemer = (int)eval((Expressie)v.elementAt(j)).y;
				
					if(ggdTeller==0)ggdTeller = teller;
					ggdTeller = (int)ggd(ggdTeller,teller);
					if(ggdNoemer==0)ggdNoemer = noemer;
					ggdNoemer = (int)ggd(kgvNoemer,noemer);
					kgvNoemer = kgvNoemer*noemer/ggdNoemer;
				}
				
				Expressie exp = maakFactorenExpressie(expVector);
				t.addElement(exp);
			}
		}
		Expressie e2 = maakTermenExpressie(t);
		
		
		//De gemeenschappelijke factor die buiten haakjes gehaald is wordt 
		//opnieuw opgebouwd mbv de gegevens van factorExponenten[]
		//en factorExponentenExtra[] en tijdelijk opgeslagen in expVector.
		//en vervolgens gcombineerd met e2 tot de gevraagde ontbinding
		
		Vector expVector = new Vector();
		Expressie exp = null;
		for(int i=0 ; i<varNamen.length ; i++)
		{	if(factorExponenten[i]==1)
			{	expVector.insertElementAt(new BasisExpressie(varNamen[i]),0);
			}
			else if(factorExponenten[i]>1)
			{	for(int k=0 ; k<factorExponenten[i] ; k++)
				{	expVector.insertElementAt(new BasisExpressie(varNamen[i]),0);
				}
			}
			else if(factorExponenten[i]==0)
			{	expVector.insertElementAt(new BasisExpressie(1),0);
			}
			else if(factorExponenten[i]==-1)
			{	expVector.insertElementAt(new Deling(new BasisExpressie(1),new BasisExpressie(varNamen[i])),0);
			}
			else if(factorExponenten[i]<-1)
			{	for(int k=0 ; k<-factorExponenten[i] ; k++)
				{	expVector.insertElementAt(new Deling(new BasisExpressie(1),new BasisExpressie(varNamen[i])),0);
				}
			}
		}
		for(int i=0 ; i<extraTermen.size() ; i++)
		{	if(factorExponentenExtra[i]==1)
			{	expVector.insertElementAt((Expressie)extraTermen.elementAt(i),0);
			}
			else if(factorExponentenExtra[i]>1)
			{	for(int k=0 ; k<factorExponentenExtra[i] ; k++)
				{	expVector.insertElementAt((Expressie)extraTermen.elementAt(i),0);
				}
			}
			else if(factorExponentenExtra[i]==0)
			{	expVector.insertElementAt(new BasisExpressie(1),0);
			}
			else if(factorExponentenExtra[i]==-1)
			{	expVector.insertElementAt(new Deling(new BasisExpressie(1),(Expressie)extraTermen.elementAt(i)),0);
			}
			else if(factorExponentenExtra[i]<-1)
			{	for(int k=0 ; k<-factorExponentenExtra[i] ; k++)
				{	expVector.insertElementAt(new Deling(new BasisExpressie(1),(Expressie)extraTermen.elementAt(i)),0);
				}
			}
		}
		
		if(heeftGGD)
		{	expVector.insertElementAt(new Deling(new BasisExpressie(ggdTeller),new BasisExpressie(kgvNoemer)),0);
			e2 = vermenigvuldig(e2,new Deling(new BasisExpressie(kgvNoemer),new BasisExpressie(ggdTeller)));
		}	
		e2 = ontbindExtra(e2);
		Vector e2Termen = geefTermen(e2,new Vector());
		if(e2Termen.size()>1)
		{	Vector waardeVector = new Vector();
			for(int i=0 ; i<expVector.size() ; i++)
			{	waardeVector.insertElementAt(expVector.elementAt(i),0);
			}
			double waarde = maakFactorenExpressie(waardeVector).geefWaarde();
			Expressie eerste = (Expressie)e2Termen.elementAt(0);
			if(eerste instanceof Aftrekking && eerste.kind1.geefWaarde()==0 && !(waarde==1))
			{	e2 = vermenigvuldig(e2,new BasisExpressie(-1));
				expVector.insertElementAt(new BasisExpressie(-1),0);
			}
		
		}
		Vector e2Vector = geefFactoren(e2,new Vector());
		for(int i=0 ; i<e2Vector.size() ; i++)
		{	expVector.insertElementAt(e2Vector.elementAt(i),0);
		}
		return maakFactorenExpressie(expVector);
	}
	/*Sorteert de expressies in de meegegeven vector en voegt samen waar 
	 *mogelijk. Het resultaat in een nieuwe vector met expressies.
	 */
	public static Vector sorteerTermen(Vector w)
	{	String[] varNamen = geefVarNamen(maakTermenExpressie(w));
		Vector t = new Vector();
		Vector v = new Vector();
		Vector extraTermen = new Vector();
		for(int j=0 ; j<w.size() ; j++)
		{	v.addElement(w.elementAt(j));
		}
		int[][] exponenten = new int[varNamen.length][v.size()];
		int[][] exponentenExtra = new int[100][v.size()]; // die 100 is arbitrair
		for(int i=0 ; i<varNamen.length ; i++)
		{	for(int j=0 ; j<v.size() ; j++)
			{	exponenten[i][j]=0;
			}
		}
		for(int i=0 ; i<100 ; i++)
		{	for(int j=0 ; j<v.size() ; j++)
			{	exponentenExtra[i][j]=0;
			}
		}
		for(int j=0 ; j<v.size() ; j++)
		{	Vector u;
			u = geefFactoren((Expressie)v.elementAt(j),new Vector());
			for(int k=0 ; k<u.size() ; k++)
			{	Expressie exp = (Expressie)u.elementAt(k);
				for(int i=0 ; i<varNamen.length ; i++)
				{	if(exp instanceof BasisExpressie && ((BasisExpressie)exp).basisString!=null  && ((BasisExpressie)exp).basisString.equals(varNamen[i]))
					{	exponenten[i][j]++;
						u.setElementAt(new BasisExpressie(1),k);
					}
					else if(exp instanceof Deling && exp.kind1.geefWaarde()==1  && exp.kind2 instanceof BasisExpressie && ((BasisExpressie)exp.kind2).basisString!=null  && ((BasisExpressie)exp.kind2).basisString.equals(varNamen[i]))
					{	exponenten[i][j]--;
						u.setElementAt(new BasisExpressie(1),k);
					}
				}
				
				if(eval(exp)==null && !(exp instanceof BasisExpressie) && !(exp instanceof Deling && exp.kind1.geefWaarde()==1  && exp.kind2 instanceof BasisExpressie)) 
				{	boolean kwamAlVoor = false;
					for(int i=0 ; i<extraTermen.size() ; i++)
					{	if(isGelijkwaardig(exp,(Expressie)extraTermen.elementAt(i)))
						{	exponentenExtra[i][j]++;
							u.setElementAt(new BasisExpressie(1),k);
							kwamAlVoor = true;
						}
						else if(exp instanceof Deling && exp.kind1.geefWaarde()==1 && isGelijkwaardig(exp.kind2,(Expressie)extraTermen.elementAt(i)))
						{	exponentenExtra[i][j]--;
							u.setElementAt(new BasisExpressie(1),k);
							kwamAlVoor = true;
						}
					}
					if(!kwamAlVoor)
					{	if(exp instanceof Deling && exp.kind1.geefWaarde()==1)
						{	exp.kind2 = herleid(exp.kind2);
							extraTermen.addElement(exp.kind2);
							exponentenExtra[extraTermen.size()-1][j]--;
							u.setElementAt(new BasisExpressie(1),k);
						}
						else
						{	exp = herleid(exp);
							extraTermen.addElement(exp);
							exponentenExtra[extraTermen.size()-1][j]++;
							u.setElementAt(new BasisExpressie(1),k);
						}
					}
				
				}	
				
			}
			v.setElementAt(maakFactorenExpressie(u),j);
		}
		
		for(int i=varNamen.length-1 ; i>-1 ; i--)
		{	for(int j=0 ; j<v.size()-1 ; j++)
			{	int max= 0;
				int plaats = j;
				for(int k=j ; k<v.size() ; k++)
				{	if(max < exponenten[i][k])
					{	max = exponenten[i][k];
						plaats = k;
					}
				}
				if(plaats>j)
				{	for(int m=0 ; m<varNamen.length ; m++)
					{	int res = exponenten[m][plaats];
						for(int k=plaats ; k>j ; k--)
						{	exponenten[m][k] = exponenten[m][k-1];
							
						}
						exponenten[m][j] = res;
					}
					for(int m=0 ; m<extraTermen.size() ; m++)
					{	int res = exponentenExtra[m][plaats];
						for(int k=plaats ; k>j ; k--)
						{	exponentenExtra[m][k] = exponentenExtra[m][k-1];
							
						}
						exponentenExtra[m][j] = res;
					}
					Object res = v.elementAt(plaats);
					v.removeElementAt(plaats);
					v.insertElementAt(res,j);
					
					res = w.elementAt(plaats);
					w.removeElementAt(plaats);
					w.insertElementAt(res,j);
				}
				
			}
		}
		
		for(int j=0 ; j<v.size()-1 ; j++)
		{	if(eval((Expressie)v.elementAt(j))!=null && ((Expressie)v.elementAt(j)).geefWaarde() != 0)
			{	for(int k=j+1 ; k<v.size() ; k++)
				{	if(eval((Expressie)v.elementAt(k))!=null && ((Expressie)v.elementAt(k)).geefWaarde() != 0)
					{	boolean komtOvereen1 = true;
						for(int i=0 ; i<varNamen.length ; i++)
						{	if(exponenten[i][k] != exponenten[i][j])
							{	komtOvereen1 = false;
								break;
							}
						}
						boolean komtOvereen2 = true;
						for(int i=0 ; i<extraTermen.size() ; i++)
						{	if(exponentenExtra[i][k] != exponentenExtra[i][j])
							{	komtOvereen2 = false;
								break;
							}
						}
					
						if(komtOvereen1 && komtOvereen2)
						{	Expressie exp1 = (Expressie)v.elementAt(j);
							Expressie exp2 = (Expressie)v.elementAt(k);
							v.setElementAt(evalueerGetalsExpressie(new Optelling(exp1,exp2)),j);
							v.setElementAt(new BasisExpressie(0),k);
						}
					}
				}
			}
		}
		
		for(int j=0 ; j<v.size() ; j++)
		{	if(((Expressie)v.elementAt(j)).geefWaarde() != 0)
			{	Vector expVector = new Vector();
				expVector.addElement(v.elementAt(j));
				Expressie exp = null;
				for(int i=0 ; i<varNamen.length ; i++)
				{	if(exponenten[i][j]==1)
					{	expVector.insertElementAt(new BasisExpressie(varNamen[i]),0);
					}
					else if(exponenten[i][j]>1)
					{	for(int k=0;k<exponenten[i][j];k++)
						{	expVector.insertElementAt(new BasisExpressie(varNamen[i]),0);
						}
					}
					else if(exponenten[i][j]==0)
					{	expVector.insertElementAt(new BasisExpressie(1),0);
					}
					else if(exponenten[i][j]==-1)
					{	expVector.insertElementAt(new Deling(new BasisExpressie(1),new BasisExpressie(varNamen[i])),0);
					}
					else if(exponenten[i][j]<-1)
					{	for(int k=0;k<-exponenten[i][j];k++)
						{	expVector.insertElementAt(new Deling(new BasisExpressie(1),new BasisExpressie(varNamen[i])),0);
						}
					}
				}
				for(int i=0 ; i<extraTermen.size() ; i++)
				{	if(exponentenExtra[i][j]==1)
					{	expVector.insertElementAt((Expressie)extraTermen.elementAt(i),0);
					}
					else if(exponentenExtra[i][j]>1)
					{	for(int k=0;k<exponentenExtra[i][j];k++)
						{	expVector.insertElementAt((Expressie)extraTermen.elementAt(i),0);
						}
					}
					else if(exponentenExtra[i][j]==0)
					{	expVector.insertElementAt(new BasisExpressie(1),0);
					}
					else if(exponentenExtra[i][j]==-1)
					{	expVector.insertElementAt(new Deling(new BasisExpressie(1),(Expressie)extraTermen.elementAt(i)),0);
					}
					else if(exponentenExtra[i][j]<-1)
					{	for(int k=0;k<-exponentenExtra[i][j];k++)
						{	expVector.insertElementAt(new Deling(new BasisExpressie(1),(Expressie)extraTermen.elementAt(i)),0);
						}
					}
				}
				exp = maakFactorenExpressie(expVector);
				t.addElement(exp);
			}
		}
		return t;
	}
	/*Benadert een wortelexpressie met een BasisExpressie van een double.
	 */	
	public static Expressie benaderWortels(Expressie e)
	{	if(e instanceof Wortel && !Double.isNaN(e.geefWaarde()))
		{	double w = e.geefWaarde();
			e = new BasisExpressie(w);
		}
		else if(!(e instanceof BasisExpressie))
		{	e.kind1 = benaderWortels(e.kind1);
			if(e.kind2 != null)e.kind2 = benaderWortels(e.kind2);
		}
		return e;
	}
	/*Vereenvoudigt de breuk x/y
	 */
	public static PointLong vereenvoudigBreuk(PointLong p)
    {	if(p.x==0)return p;
        long ggd = ggd(p.x, p.y);
        p.x = p.x / ggd;
        p.y = p.y / ggd;
        return new PointLong(p.x, p.y);
    }
	/*Bepaalt de ggd van de twee gegeven getallen
	 */
	public static long ggd(long m, long n)
    {   long hlp;
        if(m<0)m  =-m;
		if(n<0)n  =-n;
		if(m < n)
        {
            hlp = n;
            n = m;
            m = hlp;
        }
        if(n==0)return m;
        hlp = m % n;
        if(hlp == 0) return n;
        else return ggd(n, hlp);
    }
	/*Evalueert een expressie tot een breuk indien dat mogelijk is.
	 *Anders is de terugkeerwaarde null.
	 */
	public static PointLong eval(Expressie e)
	{	if(e instanceof E) return null;
		if(e instanceof PI) return null;
		if(e instanceof BasisExpressie && !Double.isNaN(e.geefWaarde()))
		{	
			if(Math.rint(e.geefWaarde())-e.geefWaarde()!=0)
			{	long x=0;
				long y=0;
				boolean isBreuk = false;
				for(int i=0 ; i<7 ; i++)
				{	double w = e.geefWaarde()*Math.pow(10,i);
					if(Math.rint(w)-w==0)
					{	isBreuk = true;
						x = (long)w;
						y = (long)Math.pow(10,i);
						break;
					}
					else if(i==6)
					{	isBreuk = true;
						x = (long)Math.rint(w);
						y = (long)Math.pow(10,i);
					}
				}
				if(isBreuk)
				{	PointLong p = new PointLong(x,y);
					return vereenvoudigBreuk(p);
				}
				
				return null;
			}
			else return new PointLong((long)e.geefWaarde(),1);
		}
		else if(e instanceof Optelling)
		{	PointLong p1 = eval(e.kind1);
			PointLong p2 = eval(e.kind2);
			if(p1==null || p2==null)return null;
			long x1 = p1.x;
			long y1 = p1.y;
			long x2 = p2.x;
			long y2 = p2.y;
			long x = x1*y2+x2*y1;
			long y = y1*y2;
			if(y<0)
			{	y=-y;
				x=-x;
			}
			PointLong p = new PointLong(x,y);
			return vereenvoudigBreuk(p);
		}
		else if(e instanceof Aftrekking)
		{	PointLong p1 = eval(e.kind1);
			PointLong p2 = eval(e.kind2);
			if(p1==null || p2==null)return null;
			long x1 = p1.x;
			long y1 = p1.y;
			long x2 = p2.x;
			long y2 = p2.y;
			long x = x1*y2-x2*y1;
			long y = y1*y2;
			if(y<0)
			{	y=-y;
				x=-x;
			}
			PointLong p = new PointLong(x,y);
			return vereenvoudigBreuk(p);
		}
		else if(e instanceof Vermenigvuldiging)
		{	PointLong p1 = eval(e.kind1);
			PointLong p2 = eval(e.kind2);
			if(p1==null || p2==null)return null;
			long x1 = p1.x;
			long y1 = p1.y;
			long x2 = p2.x;
			long y2 = p2.y;
			long x = x1*x2;
			long y = y1*y2;
			if(y<0)
			{	y=-y;
				x=-x;
			}
			PointLong p = new PointLong(x,y);
			return vereenvoudigBreuk(p);
		}
		else if(e instanceof Deling)
		{	PointLong p1 = eval(e.kind1);
			PointLong p2 = eval(e.kind2);
			if(p1==null || p2==null)return null;
			long x1 = p1.x;
			long y1 = p1.y;
			long x2 = p2.x;
			long y2 = p2.y;
			long x = x1*y2;
			long y = y1*x2;
			if(y<0)
			{	y=-y;
				x=-x;
			}
			PointLong p = new PointLong(x,y);
			return vereenvoudigBreuk(p);
		}
		else if(e instanceof Macht)
		{	PointLong p1 = eval(e.kind1);
			PointLong p2 = eval(e.kind2);
			if(p1==null || p2==null)return null;
			long x1 = p1.x;
			long y1 = p1.y;
			long x2 = p2.x;
			long y2 = p2.y;
			if(y2!=1)return null;
			long x = (long)Math.pow(x1,x2);
			long y = (long)Math.pow(y1,x2);
			if(y<0)
			{	y=-y;
				x=-x;
			}
			PointLong p = new PointLong(x,y);
			return vereenvoudigBreuk(p);
		}
		else if(e instanceof Wortel)
		{	PointLong p1 = eval(e.kind1);
			if(p1==null)return null;
			p1 = vereenvoudigBreuk(p1);
			long x1 = p1.x;
			long y1 = p1.y;
			boolean tellerIsKwadraat = false;
			boolean noemerIsKwadraat = false;
			for(int i=1 ; i<x1+1 ; i++)
			{	if(i*i==x1)
				{	tellerIsKwadraat = true;
					x1 = i;
					break;
				}
			}
			for(int i=1 ; i<y1+1 ; i++)
			{	if(i*i==y1)
				{	noemerIsKwadraat = true;
					y1 = i;
					break;
				}
			}
			if(tellerIsKwadraat && noemerIsKwadraat)
			{	PointLong p = new PointLong(x1,y1);
				return p;
			}
			else return null;
		}/*
		
		double waarde = e.geefWaarde();
		for(int i=1 ; i<1000 ; i++)
		{	double mogelijkeTeller = i*waarde;
			if(Math.rint(mogelijkeTeller)-mogelijkeTeller==0)
			{	long x1 = (long)mogelijkeTeller;
				long y1 = i;
				PointLong p = new PointLong(x1,y1);
				return p;
			}
		}*/
		return null;
	}
	/*getalsexpressie wordt eerst geevalueerd tot een breuk en vervolgens wordt
	 *een expressie gebouwd die die breuk correct weergeeft.
	 *Indien een getalsexpressie niet door een breuk kan worden weergegeven,
	 *dan wordt de beginexpressie zelf teruggegeven
	 */
	public static Expressie evalueerGetalsExpressie(Expressie exp)
	{	PointLong p = eval(exp);
		
		if(p==null)
		{	return exp;
		}
		//if(isWortelBenadering(exp))return new BasisExpressie(exp.geefWaarde());
		
		
		
		long teller = p.x;
		long noemer = p.y;
		
		if(teller>0 && (long)Math.abs(teller)<(long)Math.abs(noemer))
		{	exp = new Deling(new BasisExpressie(teller),new BasisExpressie(noemer));
		}
		else if(teller>0 && (long)Math.abs(teller)>(long)Math.abs(noemer))
		{	long helen = teller/noemer;
			long delen = teller%noemer;
			exp = new Optelling(new BasisExpressie(helen),new Deling(new BasisExpressie(delen),new BasisExpressie(noemer)));
			if(delen==0)exp = new BasisExpressie(helen);
		}
		else if(teller<0 && (long)Math.abs(teller)<(long)Math.abs(noemer))
		{	exp = new Aftrekking(new BasisExpressie(0),new Deling(new BasisExpressie(-teller),new BasisExpressie(noemer)));
		}
		else if(teller<0 && (long)Math.abs(teller)>(long)Math.abs(noemer))
		{	long helen = (long)Math.abs(teller)/(long)Math.abs(noemer);
			long delen = (long)Math.abs(teller)%(long)Math.abs(noemer);
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
		return exp;
	}
}

