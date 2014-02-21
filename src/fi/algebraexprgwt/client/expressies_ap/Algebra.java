package fi.algebraexprgwt.client.expressies_ap;

import java.util.*;


public class Algebra
{	
	private static double tryValuesStart = 0;
	private static double tryValuesWidthfactor = 1;
	private static double absPrecision = 0.000000001;
	private static double relPrecision = 0.000000001;
	
	public static void setTestValues(double start, double end)
	{	tryValuesStart = start;
		tryValuesWidthfactor = (end - start)/5;
	}
	
	public static void setDefaultTestValues()
	{	tryValuesStart = 0;
		tryValuesWidthfactor = 1;
	}
	
	public static void setAbsPrecision(double d)
	{	if(d<0.000000001)setDefaultAbsPrecision();
		else absPrecision = d+0.0000000000001;
	}
	
	public static void setDefaultAbsPrecision()
	{	absPrecision = 0.000000001;
	}
	
	public static boolean checkGelijkwaardig(Expressie e1, Expressie e2, String[] varNamen, double[] tryValues)
	{	double[] defaultTryIntValues = {0.101, 1.102, 2.103, 3.104, 7.105};
		if (tryValues == null) 
			tryValues = defaultTryIntValues;

		double e1Waarde = e1.geefWaarde().doubleValue();
		double e2Waarde = e2.geefWaarde().doubleValue();
		if (varNamen.length == 0  || (!Double.isNaN(e1Waarde) && !Double.isNaN(e2Waarde)))
		{	boolean nan1 = (Double.isInfinite (e1Waarde) || Double.isNaN(e1Waarde));
			boolean nan2 = (Double.isInfinite (e2Waarde) || Double.isNaN(e2Waarde));
			boolean ongelijk = Math.abs(e1Waarde - e2Waarde)>absPrecision &&  Math.abs(e1Waarde/e2Waarde-1)>relPrecision;
			//System.out.println(e1Waarde +"  "+ e1.toString() +"  "+ e2Waarde+"  "+ e2.toString());
			if(nan1 && !nan2 || !nan1 &&nan2 || (ongelijk && !(nan1 && nan2)))
			{	return false;
			}
			return true;
		}
		else
		{	String[] varNamenNieuw = new String[varNamen.length - 1];
			for (int i = 0; i < varNamen.length - 1; i++)
			{	varNamenNieuw[i] = varNamen[i+1];
			}
			for (int i = 0; i < tryValues.length; i++)
			{	double value = tryValues[i];
				//System.out.println(""+value);
				Expressie ee1 = e1.substitueer(value, varNamen[0]);
                Expressie ee2 = e2.substitueer(value, varNamen[0]);
                double[] tryValuesNieuw = new double[tryValues.length];
                for (int j = 0; j < tryValues.length; j++) 
                	tryValuesNieuw[j] = tryValues[j]+0.012*tryValuesWidthfactor;
                boolean gelijkwaardig = checkGelijkwaardig(ee1, ee2, varNamenNieuw, tryValuesNieuw);
				//System.out.println(""+i);
				if (!gelijkwaardig)
					return false;
			}
			return true;
		}
	}
	
	public static boolean domainTryValuesOK(Expressie e1, Expressie e2, String[] varNamen, double[] tryValues)
	{	double[] defaultTryIntValues = {0.101, 1.102, 2.103, 3.104, 7.105};
		if (tryValues == null) 
			tryValues = defaultTryIntValues;
		double e1Waarde = e1.geefWaarde().doubleValue();
		double e2Waarde = e2.geefWaarde().doubleValue();
		if (varNamen.length == 0  || (!Double.isNaN(e1Waarde) && !Double.isNaN(e2Waarde)))
		{	boolean nan1 = (Double.isInfinite(e1Waarde) || Double.isNaN(e1Waarde));
			boolean nan2 = (Double.isInfinite(e2Waarde) || Double.isNaN(e2Waarde));

			if (!(nan1 && nan2))
			{	return true;
			}
			return false;
		}
		else
		{	String[] varNamenNieuw = new String[varNamen.length-1];
			for(int i=0 ; i<varNamen.length-1 ; i++)
			{	varNamenNieuw[i] = varNamen[i+1];
			}
			for(int i=0 ; i<tryValues.length ; i++)
			{	double value = tryValues[i];
				Expressie ee1 = e1.substitueer(value, varNamen[0]);
                Expressie ee2 = e2.substitueer(value, varNamen[0]);
                double[] tryValuesNieuw = new double[tryValues.length];
                for(int j=0 ; j<tryValues.length ; j++) tryValuesNieuw[j] = tryValues[j]+0.012*tryValuesWidthfactor;
                boolean domainTryValuesOK = domainTryValuesOK(ee1,ee2,varNamenNieuw,tryValuesNieuw);
				if(domainTryValuesOK)return true;
			}
			return false;
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
	{	String[] vars = geefVarNamen((new Optelling(e1, e2)));
		String[] varsNieuw = null;
		if (vars.length > 0) 
			varsNieuw = new String[vars.length - 1];
		boolean complex = false;
		int teller = 0;
		for (int i = 0; i < vars.length; i++)
		{	if (!complex && vars[i].equals("i")) 
			{	complex = true;
			}
			else 
			{	teller++;
				if (teller - 1 < varsNieuw.length)
					varsNieuw[teller - 1] = vars[i];
			}
		}
		double[] tryIntValues = { 
			    tryValuesStart+0.101*tryValuesWidthfactor, 
				tryValuesStart+1.102*tryValuesWidthfactor, 
				tryValuesStart+2.103*tryValuesWidthfactor, 
				tryValuesStart+3.104*tryValuesWidthfactor, 
				tryValuesStart+4.105*tryValuesWidthfactor };
		double[] tryIntValuesBeperkt = {
				tryValuesStart+0.101*tryValuesWidthfactor, 
				tryValuesStart+2.102*tryValuesWidthfactor, 
				tryValuesStart+4.103*tryValuesWidthfactor };
		if (vars.length > 3)
			tryIntValues = tryIntValuesBeperkt;

		return checkGelijkwaardig(e1, e2, vars, tryIntValues);
	}
	
	
	
	public static boolean isGelijkDouble(double d1, double d2)
	{	return Math.abs(d1-d2)<0.000000001;
		
	}
	
	public static boolean isGelijkDouble(double d1, double d2, double marge)
	{	return Math.abs(d1-d2)<marge;
		
	}
	
	
	
	public static boolean gelijkGevormd(Expressie e1, Expressie e2)
	{	return zijnGelijk(e1,e2,true);
	}
	
	public static boolean zijnGelijk(Expressie e1, Expressie e2)
	{	return zijnGelijk(e1,e2,false);
	}
	
	public static boolean zijnGelijk(Expressie e1, Expressie e2, boolean vorm)
	{	if(e1==null || e2==null) 
			return false;
	
		if( vorm && e2 instanceof BasisExpressie && e2.toString().equals("G")
				&& !Double.isNaN(e1.geefWaarde().doubleValue())) 
			return true;
		if( vorm && e2 instanceof BasisExpressie && e2.toString().equals("Q")) 
			return true;
	
		if(e1.toStringStrikt().equals(e2.toStringStrikt())) 
			return true;
		else if(e1 instanceof BasisExpressie && e2 instanceof BasisExpressie)
		{	return false;
		}
		else if(e1 instanceof BasisExpressie || e2 instanceof BasisExpressie)
		{	return false;
		}
		else if(e1 instanceof Wortel && e2 instanceof Wortel)
		{	return zijnGelijk(e1.kind1,e2.kind1,vorm);
		}
		else if(e1 instanceof Wortel || e2 instanceof Wortel)
		{	return false;
		}
		else if(e1 instanceof Deling && e2 instanceof Deling && e1.kind1 instanceof BasisExpressie && 
				isGelijkDouble(e1.kind1.geefWaarde().doubleValue(), 1)  && e2.kind1 instanceof BasisExpressie && 
				isGelijkDouble(e2.kind1.geefWaarde().doubleValue(), 1))
		{	return zijnGelijk(e1.kind2,e2.kind2,vorm);
		}
		else if(e1 instanceof Macht && e2 instanceof Macht)
		{	return zijnGelijk(e1.kind1,e2.kind1,vorm) && zijnGelijk(e1.kind2,e2.kind2,vorm);
		}
		else if(e1 instanceof Macht || e2 instanceof Macht)
		{	return false;
		}
		else if(e1 instanceof Aftrekking && e2 instanceof Aftrekking && 
				e1.kind1 instanceof BasisExpressie && e1.kind1.geefWaarde().doubleValue()==0 && 
				e2.kind1 instanceof BasisExpressie && e2.kind1.geefWaarde().doubleValue()==0)
		{	return zijnGelijk(e1.kind2,e2.kind2,vorm);
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
				return zijnGelijk(e1,e2,vorm);
			}
			else return zijnGelijk(v1,v2,vorm);
		}
		else return zijnGelijk(v1,v2,vorm);
		
	
	}
	
	public static boolean zijnGelijk(Vector v1, Vector v2, boolean vorm)
	{	
		if(vorm)
		{	Vector v4 = new Vector();
			for(int j=0 ; j<v2.size(); j++)
			{	Expressie e2 = (Expressie)v2.elementAt(j);
				if(vorm && e2 instanceof BasisExpressie && e2.toString().equals("G")) 
				{ 	v4.addElement(e2);
				}
				else if(vorm && e2 instanceof BasisExpressie && e2.toString().equals("Q")) 
				{ 	v4.addElement(e2);
				}
				else v4.add(0,e2);
			}
			v2 = v4;
			
		}
		int aantal1 = v1.size();
		int aantal2 = v2.size();
		if(aantal1 != aantal2) return false;
		boolean zijnGelijk = true;
		for(int i=0 ; i<aantal1 ; i++)
		{	boolean bevat = false;
			Expressie e1 = (Expressie)v1.elementAt(i);
			for(int j=0 ; j<v2.size() ; j++)
			{	
				//System.out.println(v2.toString());
				Expressie e2 = (Expressie)v2.elementAt(j);
				if(zijnGelijk(e1,e2, vorm)) 
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
	
	/*Geeft de variabele namen van een expressie
	 *Geeft een vector met Strings terug.
	 */
	public static Vector geefVarN(Expressie e)
	{	Vector v;
		if (e instanceof BasisExpressie)
		{	Vector v0 = new Vector();
			if(e.geefVarNaam()!=null)v0.addElement(e.geefVarNaam());
			return v0;
		}
		
		v=geefVarN(e.kind1);
		if(e.kind2 !=null)
		{	Vector v2 = geefVarN(e.kind2);
			int lengte = v.size();
			for(int i=0 ; i<v2.size() ; i++)
			{	boolean anders = true;
				for(int j=0 ; j<lengte ; j++)
				{	if(((String)v.elementAt(j)).equals(((String)v2.elementAt(i))))
					{	anders = false;
					}
				}
				if(anders)v.addElement(v2.elementAt(i));
			}
		}
		return v;
	}
	/*Geeft de variabele namen van een expressie
	 *Geeft een array met Strings terug.
	 */
	public static String[] geefVarNamen(Expressie e)
	{	Vector varn = Algebra.geefVarN(e);
		String[] varNamen = new String[varn.size()];
		for (int i = 0; i < varn.size(); i++)
		{	varNamen[i] = (String) varn.elementAt(i);
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
				   && !Double.isNaN(e.kind1.geefWaarde().doubleValue())
				   && e.kind2 instanceof Deling
				   && e.kind2.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind1.geefWaarde().doubleValue())
				   && e.kind2.kind2 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind2.geefWaarde().doubleValue())
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
				{	Expressie en = new Aftrekking(new BasisExpressie("0"),e.kind2);
					v.insertElementAt(en,0);
				}
			}
			v = geefTermen(e.kind1,v);
		}
		else
		{	if(!(e instanceof BasisExpressie && e.geefWaarde().doubleValue()==0))v.insertElementAt(e,0);
		}
		return v;
	}
	/*Maakt een somexpressie van de expressies in de meegegeven vector
	 */
	public static Expressie maakTermenExpressie(Vector v)
	{	Expressie e = null;
		if(v.size()>0)e = (Expressie)v.elementAt(0);
		else return new BasisExpressie("0");
		for(int i=1 ; i<v.size() ; i++)
		{	Expressie ee = (Expressie)v.elementAt(i);
			if(ee instanceof Aftrekking && ee.kind1.geefWaarde().doubleValue()==0)
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
	{	if(e instanceof Aftrekking && e.kind1.geefWaarde().doubleValue()==0)
		{	v = Algebra.geefFactoren(e.kind2,v);
			v.addElement(new BasisExpressie("-1"));
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
				{	v.addElement(new Deling(new BasisExpressie("1"),(Expressie)u.elementAt(i)));
				}
			}
		}
		else if(e instanceof Vermenigvuldiging)
		{	v = Algebra.geefFactoren(e.kind1,v);
			v = Algebra.geefFactoren(e.kind2,v);
		}
		else if(e instanceof Macht && !Double.isNaN(e.kind2.geefWaarde().doubleValue())&& 
				(e.kind2.geefWaarde().doubleValue() - (int)(e.kind2.geefWaarde().doubleValue()))==0)
		{	if(e.kind2.geefWaarde().doubleValue()>0)
			{	for (int i=0 ; i<e.kind2.geefWaarde().doubleValue() ; i++)
				{	v = Algebra.geefFactoren(e.kind1,v);
				}
			}
			else
			{	for (int i=0 ; i<-e.kind2.geefWaarde().doubleValue() ; i++)
				{	Vector u = Algebra.geefFactoren(e.kind1,new Vector());
					for (int j=0 ; j<u.size() ; j++)
					{	v.addElement(new Deling(new BasisExpressie("1"),(Expressie)u.elementAt(j)));
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
	{	if(e instanceof Aftrekking && e.kind1.geefWaarde().doubleValue()==0)
		{	v = Algebra.geefFactorenBeperkt(e.kind2,v);
			v.addElement(new BasisExpressie("-1"));
		}
		else if(e instanceof Deling)
		{	v = Algebra.geefFactorenBeperkt(e.kind1,v);
			Vector u = Algebra.geefFactorenBeperkt(e.kind2,new Vector()); // veranderd. was geefFactoren(
			for (int i=0 ; i<u.size() ; i++)
			{	Expressie ee = (Expressie)u.elementAt(i);
				if(ee instanceof Deling)
				{	v = Algebra.geefFactorenBeperkt(ee.kind2,v);
				}
				else
				{	v.addElement(new Deling(new BasisExpressie("1"),(Expressie)u.elementAt(i)));
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
				   && !Double.isNaN(e.kind1.geefWaarde().doubleValue())
				   && e.kind2 instanceof Deling
				   && e.kind2.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind1.geefWaarde().doubleValue())
				   && e.kind2.kind2 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind2.geefWaarde().doubleValue())
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
	
	
	public static boolean isBreukPlusGetal(Expressie e)
	{	if(e instanceof Optelling
				   && e.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind1.geefWaarde().doubleValue())
				   && e.kind2 instanceof Deling
				   && e.kind2.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind1.geefWaarde().doubleValue())
				   && e.kind2.kind2 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind2.geefWaarde().doubleValue())
					)
		{	return true;
		}
		else if(e instanceof Aftrekking && e.kind1.isBasis && e.kind1.geefWaarde().doubleValue()==0)
		{	if(e.kind2 instanceof Optelling
					&& e.kind2.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind1.geefWaarde().doubleValue())
				   && e.kind2.kind2 instanceof Deling
				   && e.kind2.kind2.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind2.kind1.geefWaarde().doubleValue())
				   && e.kind2.kind2.kind2 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind2.kind2.geefWaarde().doubleValue())
					)
			{	return true;
			}
			else return false;
		}
		else return false;
	}
	public static Expressie bijBreukPlusGetalGeefBreuk(Expressie e)
	{	if(e instanceof Optelling
				   && e.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind1.geefWaarde().doubleValue())
				   && e.kind2 instanceof Deling
				   && e.kind2.kind1 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind1.geefWaarde().doubleValue())
				   && e.kind2.kind2 instanceof BasisExpressie
				   && !Double.isNaN(e.kind2.kind2.geefWaarde().doubleValue())
					)
		{	return new Deling(new BasisExpressie("" + 
				(e.kind1.geefWaarde().doubleValue() * e.kind2.kind2.geefWaarde().doubleValue() + 
				 e.kind2.kind1.geefWaarde().doubleValue())),e.kind2.kind2);
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
	
	/* Bepaalt of een expressie zonder haakjes is geschreven
	 */
	public static boolean expanded(Expressie e)
	{	Vector v = Algebra.geefTermen(e,new Vector());
		for(int i=0 ; i<v.size() ; i++)
		{	Expressie ee = (Expressie)v.elementAt(i);
			Vector u = geefFactoren(ee, new Vector());
			for(int j=0 ; j<u.size() ; j++)
			{	Expressie eee = (Expressie)u.elementAt(j);
				if(Double.isNaN(eee.geefWaarde().doubleValue())
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
			v = expand(new Vermenigvuldiging(new BasisExpressie("-1"),e.kind2),v);
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
			else if(e instanceof Macht && !Double.isNaN(e.kind2.geefWaarde().doubleValue()))//nog geen controle op exponenten als gehele getallen
			{	if(e.kind2.geefWaarde().doubleValue()==1)
				{	v = expand(e.kind1,v);
				}
				else if(e.kind2.geefWaarde().doubleValue()==-1)
				{	v = expand(new Deling(new BasisExpressie("1"),e.kind1),v);
				}
				
				
				Vector u1 = new Vector();
				Vector u2 = new Vector();
				if(e.kind2.geefWaarde().doubleValue()==2)
				{	u1 = expand(e.kind1,new Vector());
					u2 = expand(e.kind1,new Vector());
				}
				else if(e.kind2.geefWaarde().doubleValue()>2)
				{	u1 = expand(e.kind1,new Vector());
					Expressie exp = new Macht(e.kind1, new BasisExpressie("" + (e.kind2.geefWaarde().doubleValue()-1)));
					u2 = expand(exp,new Vector());
				}
				else if(e.kind2.geefWaarde().doubleValue()==-2)
				{	u1 = expand(new Deling(new BasisExpressie("1"),e.kind1),new Vector());
					u2 = expand(new Deling(new BasisExpressie("1"),e.kind1),new Vector());
				}
				else if(e.kind2.geefWaarde().doubleValue()<-2)
				{	u1 = expand(new Deling(new BasisExpressie("1"),e.kind1),new Vector());
					Expressie exp = new Macht(e.kind1, new BasisExpressie("" + (e.kind2.geefWaarde().doubleValue()+1)));
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
			{	if(e.geefWaarde().doubleValue()!=0)
					v.addElement(e);
			}
		}
		
		/**/
		
		return v;
	}
	
	/*Benadert een wortelexpressie met een BasisExpressie van een double.
	 */	
	public static Expressie benaderWortels(Expressie e)
	{	if(e instanceof Wortel && !Double.isNaN(e.geefWaarde().doubleValue()))
		{	double w = e.geefWaarde().doubleValue();
			e = new BasisExpressie("" + w);
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
	
	public static boolean withinLongRange(long num)
	{
		if(num<-100000000000000000L || num>100000000000000000L)return false;
		return true;
	}
	
	public static PointLong eval(Expressie e)
	{	
		if(e instanceof BasisExpressie && !Double.isNaN(e.geefWaarde().doubleValue()))
		{	
			if(Math.rint(e.geefWaarde().doubleValue())-e.geefWaarde().doubleValue()!=0)
			{	/**/
				long x=0;
				long y=0;
				boolean isBreuk = false;
				for(int i=0 ; i<7 ; i++)
				{	double w = e.geefWaarde().doubleValue()*Math.pow(10,i);
					if(Math.rint(w)-w==0)
					{	isBreuk = true;
						x = (long)w;
						y = (long)Math.pow(10,i);
						break;
					}
				}
				if(isBreuk)
				{	PointLong p = new PointLong(x,y);

					if(!withinLongRange(x)  || !withinLongRange(y)) return null;
					return vereenvoudigBreuk(p);
				}
				
				return null;
			}
			else if(!withinLongRange((long)e.geefWaarde().doubleValue())) return null;
			else return new PointLong((long)e.geefWaarde().longValue(),1);
		}
		else
		if(e instanceof Optelling)
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

			if(!withinLongRange(x)  || !withinLongRange(y)) return null;
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

			if(!withinLongRange(x)  || !withinLongRange(y)) return null;
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

			if(!withinLongRange(x)  || !withinLongRange(y)) return null;
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

			if(!withinLongRange(x)  || !withinLongRange(y)) return null;
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
			if(x2<0)
			{	x = (long)Math.pow(y1,-x2);//1; lelijke bug!!!!
				y = (long)Math.pow(x1,-x2);
			}
			if(y<0)
			{	y=-y;
				x=-x;
			}
			PointLong p = new PointLong(x,y);

			if(!withinLongRange(x)  || !withinLongRange(y)) return null;
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
			double wx1 = Math.rint(Math.sqrt((double)x1));
			double wy1 = Math.rint(Math.sqrt((double)y1));
			if(isGelijkDouble(wx1*wx1,(double)x1)) tellerIsKwadraat = true;
			if(isGelijkDouble(wy1*wy1,(double)y1)) noemerIsKwadraat = true;
			
			if(tellerIsKwadraat && noemerIsKwadraat)
			{	PointLong p = new PointLong((long)wx1,(long)wy1);
				//if((long)wx1==9223372036854775807L  || (long)wy1==9223372036854775807L) return null;
				if(!withinLongRange((long)wx1)  || !withinLongRange((long)wy1)) return null;
				return p;
			}
			else return null;
		}
		return null;
	}
	
}

