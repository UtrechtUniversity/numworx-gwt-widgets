package fi.geomalggwt.client;

//import java.awt.*;
//import java.util.*;
//import java.awt.event.*;
//import java.io.Serializable;

//import fi.geomalgebra.text.*;

class Lijnstuk //implements Serializable
{	
	public static int HOR = 1;
	public static int VER = 2;
	public static int SCHAAL = 24;
	public static String[] varString = {"", "x", "y", "z"};
	
	int[] lengte;
	int schaal;
	int[] varD;
	int d;
	int stand;
	Point positie;
	String varNaam;
	boolean isVar;
		
	public Lijnstuk(int var, int l, int st, int x, int y)
	{	schaal = SCHAAL;
		stand = st;
		lengte = new int[4];
		lengte[0] = 0;
		lengte[1] = 0;
		lengte[2] = 0;
		lengte[3] = 0;
		lengte[var] = l;
		varD = new int[4];
		varD[0] = SCHAAL;
		varD[1] = 84;
		varD[2] = 60;
		varD[3] = 36;
		positie = new Point(x,y);
		isVar = false;
		if (var!=0)
			isVar = true;
		zetLengte();
	}
	
	public Lijnstuk(Lijnstuk ls)
	{	schaal = SCHAAL;
		stand = ls.stand;
		lengte = new int[4];
		lengte[0] = ls.lengte[0];
		lengte[1] = ls.lengte[1];
		lengte[2] = ls.lengte[2];
		lengte[3] = ls.lengte[3];
		varD = new int[4];
		varD[0] = ls.varD[0];
		varD[1] = ls.varD[1];
		varD[2] = ls.varD[2];
		varD[3] = ls.varD[3];
		d = ls.d;
		positie = new Point(ls.positie.x,ls.positie.y);
		isVar = ls.isVar;
		varNaam = new String(ls.varNaam);
	}
	
	public void zetLengte()
	{	d = lengte[0] * schaal + lengte[1] * varD[1] + lengte[2] * varD[2] + lengte[3] * varD[3];
		varNaam = "";
		
		for (int i = 1; i < 4; i++)
		{	if (lengte[i] != 0)
			{	isVar = true;
				if (i != 1 && !varNaam.equals("") && lengte[i] > 0)
					varNaam += "+";
				if (i != 1 && !varNaam.equals("") && lengte[i] < 0)
					varNaam += "-";
				
				if (lengte[i]==1)
					varNaam += varString[i];
				else if (lengte[i] == -1 && varNaam.equals(""))
					varNaam += "-" + varString[i];
				else if (lengte[i] == -1 && !varNaam.equals(""))
					varNaam += varString[i];
				else if (lengte[i] > 1)
					varNaam += Integer.toString(lengte[i]) + varString[i];
				else if (lengte[i] < -1 && varNaam.equals(""))
					varNaam += Integer.toString(lengte[i]) + varString[i];
				else if (lengte[i] < -1 && !varNaam.equals(""))
					varNaam += Integer.toString(-lengte[i]) + varString[i];
			}
		}
		if (lengte[0] > 0)
		{	if(!varNaam.equals(""))
			{	varNaam += "+";
			}	
			varNaam += Integer.toString(lengte[0]);
		}
		if (lengte[0] < 0)	
		{	if (!varNaam.equals(""))
			{	varNaam += "-";
				varNaam += Integer.toString(-lengte[0]);
			}	
			else 
			{	varNaam += Integer.toString(lengte[0]);
			}
		}
	}
	
	public void zetStand(int s)
	{	stand = s;
	}
	
	public void zetPositie(int x, int y)
	{	positie.x = x;
		positie.y = y;
	}
	
	public static boolean isGelijk(Lijnstuk ls1, Lijnstuk ls2)
	{	if(ls1.stand!=ls2.stand)return false;
		for(int j=0 ; j<4 ; j++)
		{	if(ls1.lengte[j]!=ls2.lengte[j])return false;
		}
		
		for(int j=0 ; j<4 ; j++)
		{	if(ls1.lengte[j]!=ls2.lengte[j])return false;
		}
		return true;
	}
					
	public void telOp(Lijnstuk ls)
	{	lengte[0] += ls.lengte[0];
		lengte[1] += ls.lengte[1];
		lengte[2] += ls.lengte[2];
		lengte[3] += ls.lengte[3];
		zetLengte();
	}
	
	public void maakNegatief()
	{	for(int i=0 ; i<4 ; i++)
		{	lengte[i] = -lengte[i];
		}
		zetLengte();
	}
		
	public void draai()
	{	if(stand==1)stand = 2;
		else stand = 1;
	}
	
	public void zetVar(int varnr, int waarde)
	{	if(varnr != 0)varD[varnr] = waarde;
		d = lengte[0]*schaal + lengte[1]*varD[1] + lengte[2]*varD[2] + lengte[3]*varD[3];
	}
	
	public int geefAantalTermen()
	{	int aantalTermen = 0;
		for(int i=0 ; i<4 ; i++)
		{	if(lengte[i] != 0)aantalTermen++;
		}
		return aantalTermen;
	}
	
	public int geefTermNr()
	{	int termnr = 0;
		for(int i=0 ; i<4 ; i++)
		{	if(lengte[i] != 0)termnr = i;
		}
		return termnr;
	}
	public boolean isNul()
	{	if(lengte[0]==0 && lengte[1]==0 && lengte[2]==0 && lengte[3]==0)return true;
		else return false;
	}
}
