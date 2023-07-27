package fi.nabouwenaanzichtengwt.client;

/**
 * klasse die het kubusbouwsel representeert: <br>
 * dit bouwsel bestaat uit een grondvlak (een groot vierkant)
 * waarop maxAantal x maxAantal kleine vierkanten die de basis
 * van de eerste laag kubusjes vormen; er zijn maximaal 
 * maxAantal lagen kubusjes; de klasse houdt bij welke kubusjes
 * aanwezig zijn en voegt kubusjes toe op een specifieke positie
 * of verwijdert deze van een specifieke positie;<br>
 * de klasse bevat ook methoden om een of meer aanzichten van dit
 * kubusrooster tevergelijken met een gegeven kubusrooster.    
 * @author Peter Boon
 */

public class KubusRooster
{
	/**
	 * bouwsel bestaat uit maximaal maxAaantal x maxAantal x maxAantal kubusjes  
	 */
	int maxAantal;
	/**
	 * actuele aantal kubusjes
	 */
	int aantalKubussen;

	/**
	 * totale lengte van maxAantal kubusjes op een rij
	 */
	double totLengte;
	/**
	 * lengte ribbe van een(1) kubusje 	
	 */
	double ribLengte;
	/**
	 * de kubusjes in het rooster (null als geen kusbusje op een specifieke positie)
	 */
	RKubus[][][] kubussen;
	/**
	 * de vierkanten op het grondvlak 
	 */
	RVierkant[][] vierkanten;
	/**
	 * het vierkant voor het grondvlak
	 */
	RVierkant grondvlak;
	/**
	 * beginpositie bouwsel is (beginpos,beginpos,beginpos)
	 */
	double beginpos;
	/**
	 * balk aan de voorkant van het grondvlak (instelbaar)
	 */
	RBalk balk;
	/**
	 * pijl die wijst naar de voorkant van het grondvlak (instelbaar)
	 */
	Veelvlak pijl;
	/**
	 * vulkleur van de vlakjes van de kubusjes
	 */
	String vulkleur;

	/**
	 * constructor: construeer een grondvlak met daarop een leeg kubusbouwsel
	 * van gegeven afmetingen
	 * @param maxn bouwsel heeft maximaal maxn x maxn x maxn kubusjes 
	 * @param totL totale lengte van het kubus
	 */
	public KubusRooster(int maxn, double totL)
	{
		vulkleur = "geel";
		aantalKubussen = 0;
		maxAantal = maxn;
		totLengte = totL;
		ribLengte = totLengte / maxAantal;
		beginpos = -(totLengte - ribLengte) / 2;
		grondvlak = new RVierkant(1.2 * totLengte, 0, -0.5 * totLengte, 0);
		balk = new RBalk(1.2 * totLengte, 0.1 * totLengte, 0, -0.5 * totLengte, 0);
		pijl = maakPijl();
		kubussen = new RKubus[maxAantal][maxAantal][maxAantal];
		vierkanten = new RVierkant[maxAantal][maxAantal];
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				vierkanten[i][j] = new RVierkant(ribLengte, beginpos + i * ribLengte, -totLengte / 2, beginpos + j * ribLengte);
			}
		}
	}

	/**
	 * constructor: construeer een grondvlak met daarop een kubusrooster
	 * bestaande uit kubusjes op posities (i,j,k)  waar rooster[i][j][k] == true   
	 * @param rooster boolean 3d-rooster voor de aanwezigheid van kubusjes op specifieke posities
	 * @param totL totale lengte rooster.length kubusjes op een rij
	 */
	public KubusRooster(boolean[][][] rooster, double totL)
	{
		aantalKubussen = 0;
		maxAantal = rooster.length;
		totLengte = totL;
		ribLengte = totLengte / maxAantal;
		beginpos = -(totLengte - ribLengte) / 2;
		grondvlak = new RVierkant(1.2 * totLengte, 0, -0.5 * totLengte, 0);
		balk = new RBalk(1.2 * totLengte, 0.1 * totLengte, 0, -0.5 * totLengte, 0);
		pijl = maakPijl();
		kubussen = new RKubus[maxAantal][maxAantal][maxAantal];
		vierkanten = new RVierkant[maxAantal][maxAantal];
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				vierkanten[i][j] = new RVierkant(ribLengte, beginpos + i * ribLengte, -totLengte / 2, beginpos + j * ribLengte);
			}
		}
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (rooster[i][j][k])
					{
						voegKubusToe(i, j, k);
					}
				}
			}
		}
	}

	/**
	 * compatibility
	 * @param stateNew kubusrooster als Object[]
	 * @return boolean 3d-rooster voor de aanwezigheid van kubusjes op specifieke posities 
	 */
	static boolean[][][] toBooleanArray(Object[] stateNew)
	{
		boolean[][][] result = new boolean[stateNew.length][][];
		for (int i = 0; i < result.length; i++)
		{
			Object[] o = (Object[]) stateNew[i];
			result[i] = new boolean[o.length][];
			for (int j = 0; j < o.length; j++)
			{
				Object[] oo = (Object[]) o[j];
				result[i][j] = new boolean[oo.length];
				for (int k = 0; k < oo.length; k++)
				{
					result[i][j][k] = Boolean.TRUE.equals(oo[k]);
				}
			}
		}
		return result;
	}

	/**
	 * maak een boolean 3d-rooster voor de aanwezigheid van kubusjes in dit KubusRooster
	 * @return boolean 3d-rooster voor de aanwezigheid van kubusjes op specifieke posities
	 */
	public boolean[][][] geefBooleanRooster()
	{
		boolean[][][] rooster = new boolean[maxAantal][maxAantal][maxAantal];
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (kubussen[i][j][k] != null)
					{
						rooster[i][j][k] = true;
					}
				}
			}
		}
		return rooster;
	}

	/**
	 * maak de pijl die naar de voorkant van het grondvlak wijst
	 * @return de pijl als Veelvlak 
	 */
	Veelvlak maakPijl()
	{
		double[] hp =
		{ 0, -0.5, -0.85, 0.01, -0.5, -0.85, 0.01, -0.5, -0.65, 0, -0.5, -0.65, -0.05, -0.5, -0.75, 0.05, -0.5, -0.75 };
		int[] vl =
		{ 4, 4, 0, 1, 2, 3, 4, 0, 3, 2, 1, 3, 3, 4, 5, 3, 3, 5, 4 };
		Veelvlak v = new Veelvlak(hp, vl);
		for (int i = 0; i < v.aantalVlakken; i++)
		{
			v.vlakken[i].vulkleur = "zwart";
		}
		return v;
	}

	/**
	 * zet de vulkleur van de vlakjes van alle aanwezige kubusjes
	 * @param kleur nieuwe vulkleur
	 */
	public void zetVulkleur(String kleur)
	{
		vulkleur = kleur;
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (kubussen[i][j][k] != null)
					{
						kubussen[i][j][k].zetVulkleur(vulkleur);
					}
				}
			}
		}
	}

	/**
	 * voeg een kubusje toe op positie (x,y,z) in het rooster;
	 * pas het bedekt zijn van het nieuwe kubusje en eventuele 
	 * aangrenzende kubusjes aan
	 * @param x x-positie nieuwe kubusje 
	 * @param z z-positie nieuwe kubusje
	 * @param y y-positie nieuwe kubusje
	 * @return true als een kubusje werd toegvoegd
	 */
	public boolean voegKubusToe(int x, int z, int y)
	{
		boolean toegevoegd = false;
		if (x < maxAantal && y < maxAantal && z < maxAantal && x > -1 && y > -1 && z > -1)
		{
			kubussen[x][z][y] = new RKubus(ribLengte, beginpos + x * ribLengte, beginpos + y * ribLengte, beginpos + z * ribLengte);
			toegevoegd = true;
			aantalKubussen++;
			if (y > 0 && kubussen[x][z][y - 1] != null)
			{
				kubussen[x][z][y - 1].isOnbedekt[0] = false;
				kubussen[x][z][y].isOnbedekt[5] = false;
			}
			if (y < maxAantal - 1 && kubussen[x][z][y + 1] != null)
			{
				kubussen[x][z][y + 1].isOnbedekt[5] = false;
				kubussen[x][z][y].isOnbedekt[0] = false;
			}
			if (z > 0 && kubussen[x][z - 1][y] != null)
			{
				kubussen[x][z - 1][y].isOnbedekt[3] = false;
				kubussen[x][z][y].isOnbedekt[1] = false;
			}
			if (z < maxAantal - 1 && kubussen[x][z + 1][y] != null)
			{
				kubussen[x][z + 1][y].isOnbedekt[1] = false;
				kubussen[x][z][y].isOnbedekt[3] = false;
			}
			if (x > 0 && kubussen[x - 1][z][y] != null)
			{
				kubussen[x - 1][z][y].isOnbedekt[2] = false;
				kubussen[x][z][y].isOnbedekt[4] = false;
			}
			if (x < maxAantal - 1 && kubussen[x + 1][z][y] != null)
			{
				kubussen[x + 1][z][y].isOnbedekt[4] = false;
				kubussen[x][z][y].isOnbedekt[2] = false;
			}
		}
		return toegevoegd;
	}

	/**
	 * verwijder een kubusje van positie (x,y,z) in het rooster;
	 * pas het bedekt zijn van eventuele 
	 * aangrenzende kubusjes aan
	 * @param x x-positie nieuwe kubusje 
	 * @param z z-positie nieuwe kubusje
	 * @param y y-positie nieuwe kubusje
	 * @return true als een kubusje werd verwijderd
	 */
	public boolean verwijderKubus(int x, int z, int y)
	{
		boolean verwijderd = false;
		if (x < maxAantal && y < maxAantal && z < maxAantal && x > -1 && y > -1 && z > -1)
		{ //isZichtbaar[x][z][y] = false;
			if (kubussen[x][z][y] != null)
			{
				verwijderd = true;
				aantalKubussen--;
			}
			kubussen[x][z][y] = null;
			if (y > 0 && kubussen[x][z][y - 1] != null)
				kubussen[x][z][y - 1].isOnbedekt[0] = true;
			if (y < maxAantal - 1 && kubussen[x][z][y + 1] != null)
				kubussen[x][z][y + 1].isOnbedekt[5] = true;
			if (z > 0 && kubussen[x][z - 1][y] != null)
				kubussen[x][z - 1][y].isOnbedekt[3] = true;
			if (z < maxAantal - 1 && kubussen[x][z + 1][y] != null)
				kubussen[x][z + 1][y].isOnbedekt[1] = true;
			if (x > 0 && kubussen[x - 1][z][y] != null)
				kubussen[x - 1][z][y].isOnbedekt[2] = true;
			if (x < maxAantal - 1 && kubussen[x + 1][z][y] != null)
				kubussen[x + 1][z][y].isOnbedekt[4] = true;

		}
		return verwijderd;
	}

	/**
	 * maak het rooster helemaal vol mat kubusjes; 
	 * pas de bedekking aan
	 */
	public void maakVol()
	{
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				vierkanten[i][j] = new RVierkant(ribLengte, beginpos + i * ribLengte, -totLengte / 2, beginpos + j * ribLengte);
				for (int k = 0; k < maxAantal; k++)
				{
					kubussen[i][j][k] = new RKubus(ribLengte, beginpos + i * ribLengte, beginpos + k * ribLengte, beginpos + j * ribLengte);

					if (k == 0)
						kubussen[i][j][k].isOnbedekt[5] = true;
					else
						kubussen[i][j][k].isOnbedekt[5] = false;
					if (k == maxAantal - 1)
						kubussen[i][j][k].isOnbedekt[0] = true;
					else
						kubussen[i][j][k].isOnbedekt[0] = false;
					if (j == 0)
						kubussen[i][j][k].isOnbedekt[1] = true;
					else
						kubussen[i][j][k].isOnbedekt[1] = false;
					if (j == maxAantal - 1)
						kubussen[i][j][k].isOnbedekt[3] = true;
					else
						kubussen[i][j][k].isOnbedekt[3] = false;
					if (i == 0)
						kubussen[i][j][k].isOnbedekt[4] = true;
					else
						kubussen[i][j][k].isOnbedekt[4] = false;
					if (i == maxAantal - 1)
						kubussen[i][j][k].isOnbedekt[2] = true;
					else
						kubussen[i][j][k].isOnbedekt[2] = false;
				}
			}
		}
		aantalKubussen = maxAantal * maxAantal * maxAantal;
	}

	/**
	 * verwijder alle kubusjes uit het rooster
	 */
	public void maakLeeg()
	{
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{ 
				for (int k = 0; k < maxAantal; k++)
				{
					kubussen[i][j][k] = null;
				}
			}
		}
		aantalKubussen = 0;
	}

	/**
	 * check of het rooster alle mogelijke kubusjes bevat
	 * @return true/false
	 */
	public boolean isVol()
	{
		return aantalKubussen == maxAantal * maxAantal * maxAantal;
	}

	/** 
	 * check of het rooster geen kubusjes bevat (leeg is)	
	 * @return true/false
	 */
	public boolean isLeeg()
	{
		return aantalKubussen == 0;
	}

	/**
	 * geef het actuele aantal kubusjes in het rooster
	 * @return aantalKubussen
	 */
	public int geefAantalK()
	{
		return aantalKubussen;
	}

	/**
	 * check of dit KubusRooster identiek is (i.e. kubusjes op
	 * dezelfde posities) als KubusRooster kr
	 * @param kr gegeven KubusRooster
	 * @return true/false
	 */
	public boolean isGelijk(KubusRooster kr)
	{
		if (maxAantal != kr.maxAantal)
			return false;
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (kubussen[i][j][k] == null && kr.kubussen[i][j][k] != null || kubussen[i][j][k] != null && kr.kubussen[i][j][k] == null)
						return false;
				}
			}
		}
		return true;
	}

	/**
	 * check of het boven-, voor- en rechtsaanzicht van dit KubusRooster
	 * gelijk is aan het boven-, voor- en rechtsaanzicht van KubusRooster kr
	 * @param kr gegeven KubusRooster
	 * @return true/false
	 */
	public boolean isGelijkAanzichten(KubusRooster kr)
	{
		if (maxAantal != kr.maxAantal)
			return false;
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (kubussen[i][j][k] != null)
					{
						boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!bb)
								bb = kr.kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!bv)
								bv = kr.kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!br)
								br = kr.kubussen[m][j][k] != null;
						}
						if (!bb || !bv || !br)
							return false;
					}
				}
			}
		}
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (kr.kubussen[i][j][k] != null)
					{
						boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!bb)
								bb = kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!bv)
								bv = kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!br)
								br = kubussen[m][j][k] != null;
						}
						if (!bb || !bv || !br)
							return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * check of het voor- en rechtsaanzicht van dit KubusRooster
	 * gelijk is aan het voor- en rechtsaanzicht van KubusRooster kr
	 * @param kr gegeven KubusRooster
	 * @return true/false
	 */
	public boolean isGelijkAanzichtenVB(KubusRooster kr)
	{
		if (maxAantal != kr.maxAantal)
			return false;
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (kubussen[i][j][k] != null)
					{
						boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!bb)
								bb = kr.kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!bv)
								bv = kr.kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!br)
								br = kr.kubussen[m][j][k] != null;
						}
						if (!bv || !br)
							return false;
					}
				}
			}
		}
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (kr.kubussen[i][j][k] != null)
					{
						boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!bb)
								bb = kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!bv)
								bv = kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!br)
								br = kubussen[m][j][k] != null;
						}
						if (!bv || !br)
							return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * check of het voor- en rechtsaanzicht van dit KubusRooster
	 * gelijk is aan het voor- en rechtsaanzicht van KubusRooster kr
	 * @param kr gegeven KubusRooster
	 * @return true/false
	 */
	public boolean isGelijkVoorEnRechtsAanzicht(KubusRooster kr)
	{
		if (maxAantal != kr.maxAantal)
			return false;
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (kubussen[i][j][k] != null)
					{
						boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!bb)
								bb = kr.kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!bv)
								bv = kr.kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!br)
								br = kr.kubussen[m][j][k] != null;
						}
						if (!bv || !br)
							return false;
					}
				}
			}
		}
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (kr.kubussen[i][j][k] != null)
					{
						boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!bb)
								bb = kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!bv)
								bv = kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{
							if (!br)
								br = kubussen[m][j][k] != null;
						}
						if (!bv || !br)
							return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * check of het boven- en vooraanzicht van dit KubusRooster
	 * gelijk is aan het boven- en vooraanzicht van KubusRooster kr
	 * @param kr gegeven KubusRooster
	 * @return true/false
	 */
	public boolean isGelijkBovenEnVoorAanzicht(KubusRooster kr)
	{	
		if (maxAantal != kr.maxAantal)
			return false;
		for (int i = 0; i < maxAantal; i++)
		{	for (int j = 0; j < maxAantal; j++)
			{	for (int k = 0; k < maxAantal; k++)
				{	if (kubussen[i][j][k] != null) 
					{   boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bb) 
								bb = kr.kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bv) 
								bv = kr.kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!br) 
							br = kr.kubussen[m][j][k] != null;
						}
						if (!bb || !bv)
							return false;
					}
				}
			}
		}
		for (int i = 0; i < maxAantal; i++)
		{	for (int j = 0; j < maxAantal; j++)
			{	for (int k = 0; k < maxAantal; k++)
				{	if (kr.kubussen[i][j][k] != null) 
					{   boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bb) 
								bb = kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bv) 
								bv = kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!br) 
								br = kubussen[m][j][k] != null;
						}
						if(!bb || !bv)
							return false;
					}
				}
			}
		}
		return true;
	}	

	/**
	 * check of het boven- en rechtsaanzicht van dit KubusRooster
	 * gelijk is aan het boven- en rechtsaanzicht van KubusRooster kr
	 * @param kr gegeven KubusRooster
	 * @return true/false
	 */
	public boolean isGelijkBovenEnRechtsAanzicht(KubusRooster kr)
	{	
		if (maxAantal != kr.maxAantal)
			return false;
		for (int i = 0; i < maxAantal; i++)
		{	for (int j = 0; j < maxAantal; j++)
			{	for (int k = 0; k < maxAantal; k++)
				{	if (kubussen[i][j][k] != null) 
					{   boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bb) 
								bb = kr.kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bv) 
								bv = kr.kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!br) 
							br = kr.kubussen[m][j][k] != null;
						}
						if (!bb || !br)
							return false;
					}
				}
			}
		}
		for (int i = 0; i < maxAantal; i++)
		{	for (int j = 0; j < maxAantal; j++)
			{	for (int k = 0; k < maxAantal; k++)
				{	if (kr.kubussen[i][j][k] != null) 
					{   boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bb) 
								bb = kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bv) 
								bv = kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!br) 
								br = kubussen[m][j][k] != null;
						}
						if(!bb || !br)
							return false;
					}
				}
			}
		}
		return true;
	}	

	/**
	 * check of het bovenaanzicht van dit KubusRooster
	 * gelijk is aan het bovenaanzicht van KubusRooster kr
	 * @param kr gegeven KubusRooster
	 * @return true/false
	 */
	public boolean isGelijkBovenAanzicht(KubusRooster kr)
	{	
		if (maxAantal != kr.maxAantal)
			return false;
		for (int i = 0; i < maxAantal; i++)
		{	for (int j = 0; j < maxAantal; j++)
			{	for (int k = 0; k < maxAantal; k++)
				{	if (kubussen[i][j][k] != null) 
					{   boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bb) 
								bb = kr.kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bv) 
								bv = kr.kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!br) 
							br = kr.kubussen[m][j][k] != null;
						}
						if (!bb)
							return false;
					}
				}
			}
		}
		for (int i = 0; i < maxAantal; i++)
		{	for (int j = 0; j < maxAantal; j++)
			{	for (int k = 0; k < maxAantal; k++)
				{	if (kr.kubussen[i][j][k] != null) 
					{   boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bb) 
								bb = kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bv) 
								bv = kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!br) 
								br = kubussen[m][j][k] != null;
						}
						if(!bb)
							return false;
					}
				}
			}
		}
		return true;
	}	

	/**
	 * check of het vooraanzicht van dit KubusRooster
	 * gelijk is aan het vooraanzicht van KubusRooster kr
	 * @param kr gegeven KubusRooster
	 * @return true/false
	 */
	public boolean isGelijkVoorAanzicht(KubusRooster kr)
	{	
		if (maxAantal != kr.maxAantal)
			return false;
		for (int i = 0; i < maxAantal; i++)
		{	for (int j = 0; j < maxAantal; j++)
			{	for (int k = 0; k < maxAantal; k++)
				{	if (kubussen[i][j][k] != null) 
					{   boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bb) 
								bb = kr.kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bv) 
								bv = kr.kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!br) 
							br = kr.kubussen[m][j][k] != null;
						}
						if (!bv)
							return false;
					}
				}
			}
		}
		for (int i = 0; i < maxAantal; i++)
		{	for (int j = 0; j < maxAantal; j++)
			{	for (int k = 0; k < maxAantal; k++)
				{	if (kr.kubussen[i][j][k] != null) 
					{   boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bb) 
								bb = kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bv) 
								bv = kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!br) 
								br = kubussen[m][j][k] != null;
						}
						if(!bv)
							return false;
					}
				}
			}
		}
		return true;
	}	
	
	/**
	 * check of het rechtsaanzicht van dit KubusRooster
	 * gelijk is aan het rechtsaanzicht van KubusRooster kr
	 * @param kr gegeven KubusRooster
	 * @return true/false
	 */
	public boolean isGelijkRechtsAanzicht(KubusRooster kr)
	{	
		if (maxAantal != kr.maxAantal)
			return false;
		for (int i = 0; i < maxAantal; i++)
		{	for (int j = 0; j < maxAantal; j++)
			{	for (int k = 0; k < maxAantal; k++)
				{	if (kubussen[i][j][k] != null) 
					{   boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bb) 
								bb = kr.kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bv) 
								bv = kr.kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!br) 
							br = kr.kubussen[m][j][k] != null;
						}
						if (!br)
							return false;
					}
				}
			}
		}
		for (int i = 0; i < maxAantal; i++)
		{	for (int j = 0; j < maxAantal; j++)
			{	for (int k = 0; k < maxAantal; k++)
				{	if (kr.kubussen[i][j][k] != null) 
					{   boolean bb = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bb) 
								bb = kubussen[i][j][m] != null;
						}
						boolean bv = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!bv) 
								bv = kubussen[i][m][k] != null;
						}
						boolean br = false;
						for (int m = 0; m < maxAantal; m++)
						{	if (!br) 
								br = kubussen[m][j][k] != null;
						}
						if(!br)
							return false;
					}
				}
			}
		}
		return true;
	}	

}
