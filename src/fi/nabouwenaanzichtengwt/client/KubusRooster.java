package fi.nabouwenaanzichtengwt.client;
 

public class KubusRooster
{	int maxAantal;
	int aantalKubussen;
	int[][] maxy;
	double totLengte, ribLengte;
	RKubus[][][] kubussen;
	//boolean[][][] isZichtbaar;
	//boolean[][][][] isOnbedekt;
	RVierkant[][] vierkanten;
	double beginpos;
	RVierkant grondvlak;
	Veelvlak pijl;
	String vulkleur;
	
	public KubusRooster(int maxn, double totL)
	{	vulkleur = "geel";
		aantalKubussen = 0;
		maxAantal = maxn;
		totLengte = totL;
		ribLengte = totLengte/maxAantal;
		beginpos = -(totLengte-ribLengte)/2;
		grondvlak = new RVierkant(1.2, 0, -0.5, 0);
		pijl = maakPijl();
		kubussen = new RKubus[maxAantal][maxAantal][maxAantal];
		//isZichtbaar = new boolean[maxAantal][maxAantal][maxAantal];
		//isOnbedekt = new boolean[maxAantal][maxAantal][maxAantal][6];
		vierkanten = new RVierkant[maxAantal][maxAantal];
		maxy = new int[maxAantal][maxAantal];
		for(int i=0 ; i<maxAantal ; i++)
		{	for(int j=0 ; j<maxAantal ; j++)
			{	maxy[i][j]=0;
				vierkanten[i][j]=new RVierkant(ribLengte, beginpos + i*ribLengte, -totLengte/2, beginpos + j*ribLengte);
				//for(int k=0 ; k<maxAantal ; k++)
				//{	//kubussen[i][j][k] = new RKubus(ribLengte, beginpos + i*ribLengte, beginpos + k*ribLengte, beginpos + j*ribLengte);
					//isZichtbaar[i][j][k] = false;
					//for(int m=0 ; m<6 ; m++)
					//{	isOnbedekt[i][j][k][m] = true;
					//}
				//}
			}
		}
	}
	
	public KubusRooster(boolean[][][] rooster, double totL)
	{	aantalKubussen = 0;
		maxAantal = rooster.length;
		totLengte = totL;
		ribLengte = totLengte/maxAantal;
		beginpos = -(totLengte-ribLengte)/2;
		grondvlak = new RVierkant(1.2, 0, -0.5, 0);
		pijl = maakPijl();
		kubussen = new RKubus[maxAantal][maxAantal][maxAantal];
		vierkanten = new RVierkant[maxAantal][maxAantal];
		maxy = new int[maxAantal][maxAantal];
		for(int i=0 ; i<maxAantal ; i++)
		{	for(int j=0 ; j<maxAantal ; j++)
			{	maxy[i][j]=0;
				vierkanten[i][j]=new RVierkant(ribLengte, beginpos + i*ribLengte, -totLengte/2, beginpos + j*ribLengte);
			}
		}
		for(int i=0 ; i<maxAantal ; i++)
		{	for(int j=0 ; j<maxAantal ; j++)
			{	for(int k=0 ; k<maxAantal ; k++)
				{	if(rooster[i][j][k])
					{	voegKubusToe(i,j,k);
					}
				}
			}
		}
	}
	
	public boolean[][][] geefBooleanRooster()
	{	boolean[][][] rooster = new boolean[maxAantal][maxAantal][maxAantal];
		for(int i=0 ; i<maxAantal ; i++)
		{	for(int j=0 ; j<maxAantal ; j++)
			{	for(int k=0 ; k<maxAantal ; k++)
				{	if(kubussen[i][j][k] != null)
					{	rooster[i][j][k]=true;
					}
				}
			}
		}
		return rooster;
	}
	
	
	
	
	Veelvlak maakPijl()
	{	double[] hp = {0, -0.5, -0.85,	0.01, -0.5, -0.85,	0.01, -0.5, -0.65,	 0, -0.5, -0.65,
					   -0.05, -0.5, -0.75,	 0.05, -0.5, -0.75};
		int[] vl = {4,
					4,	0,1,2,3,
					4,	0,3,2,1,
					3,	3,4,5,
					3,	3,5,4};
		Veelvlak v = new Veelvlak(hp,vl);
		for(int i=0 ; i<v.aantalVlakken ; i++)
		{	v.vlakken[i].vulkleur="zwart";
		}
		return v;
	}
	
	public void zetVulkleur(String kleur)
	{	vulkleur = kleur;
		for(int i=0 ; i<maxAantal ; i++)
		{	for(int j=0 ; j<maxAantal ; j++)
			{	for(int k=0 ; k<maxAantal ; k++)
				{	if(kubussen[i][j][k] != null)
					{	kubussen[i][j][k].zetVulkleur(vulkleur);
					}
				}
			}
		}
	}
	

	
	public void voegKubusToe(int x, int z, int y)
	{	if(x<maxAantal && y<maxAantal && z<maxAantal && x>-1 && y>-1 && z>-1)
		{	kubussen[x][z][y] = new RKubus(ribLengte, beginpos + x*ribLengte, beginpos + y*ribLengte, beginpos + z*ribLengte);
			aantalKubussen++;
			if(y>0 && kubussen[x][z][y-1] !=null)
			{	kubussen[x][z][y-1].isOnbedekt[0] = false;
				kubussen[x][z][y].isOnbedekt[5] = false;
			}
			if(y<maxAantal-1 && kubussen[x][z][y+1] !=null)
			{	kubussen[x][z][y+1].isOnbedekt[5] = false;
				kubussen[x][z][y].isOnbedekt[0] = false;
			}
			if(z>0 && kubussen[x][z-1][y] !=null)
			{	kubussen[x][z-1][y].isOnbedekt[3] = false;
				kubussen[x][z][y].isOnbedekt[1] = false;
			}
			if(z<maxAantal-1 && kubussen[x][z+1][y] !=null)
			{	kubussen[x][z+1][y].isOnbedekt[1] = false;
				kubussen[x][z][y].isOnbedekt[3] = false;
			}
			if(x>0 && kubussen[x-1][z][y] !=null)
			{	kubussen[x-1][z][y].isOnbedekt[2] = false;
				kubussen[x][z][y].isOnbedekt[4] = false;
			}
			if(x<maxAantal-1 && kubussen[x+1][z][y] !=null)
			{	kubussen[x+1][z][y].isOnbedekt[4] = false;
				kubussen[x][z][y].isOnbedekt[2] = false;
			}
		}
	}
	
	
	
	public void verwijderKubus(int x, int z, int y)
	{	if(x<maxAantal && y<maxAantal && z<maxAantal && x>-1 && y>-1 && z>-1)
		{	//isZichtbaar[x][z][y] = false;
			if(kubussen[x][z][y] != null)aantalKubussen--;
			kubussen[x][z][y] = null;
			if(y>0 && kubussen[x][z][y-1] !=null)kubussen[x][z][y-1].isOnbedekt[0] = true;
			if(y<maxAantal-1 && kubussen[x][z][y+1] !=null)kubussen[x][z][y+1].isOnbedekt[5] = true;
			if(z>0 && kubussen[x][z-1][y] !=null)kubussen[x][z-1][y].isOnbedekt[3] = true;
			if(z<maxAantal-1 && kubussen[x][z+1][y] !=null)kubussen[x][z+1][y].isOnbedekt[1] = true;
			if(x>0 && kubussen[x-1][z][y] !=null)kubussen[x-1][z][y].isOnbedekt[2] = true;
			if(x<maxAantal-1 && kubussen[x+1][z][y] !=null)kubussen[x+1][z][y].isOnbedekt[4] = true;
			
		}
	}
	
	public void maakVol()
	{	for(int i=0 ; i<maxAantal ; i++)
		{	for(int j=0 ; j<maxAantal ; j++)
			{	maxy[i][j]=0;
				vierkanten[i][j]=new RVierkant(ribLengte, beginpos + i*ribLengte, -totLengte/2, beginpos + j*ribLengte);
				for(int k=0 ; k<maxAantal ; k++)
				{	kubussen[i][j][k] = new RKubus(ribLengte, beginpos + i*ribLengte, beginpos + k*ribLengte, beginpos + j*ribLengte);
					//isZichtbaar[i][j][k] = true;
					
					if(k==0)kubussen[i][j][k].isOnbedekt[5] = true;
					else kubussen[i][j][k].isOnbedekt[5] = false;
					if(k==maxAantal-1)kubussen[i][j][k].isOnbedekt[0] = true;
					else kubussen[i][j][k].isOnbedekt[0] = false;
					if(j==0)kubussen[i][j][k].isOnbedekt[1] = true;
					else kubussen[i][j][k].isOnbedekt[1] = false;
					if(j==maxAantal-1)kubussen[i][j][k].isOnbedekt[3] = true;
					else kubussen[i][j][k].isOnbedekt[3] = false;
					if(i==0)kubussen[i][j][k].isOnbedekt[4] = true;
					else kubussen[i][j][k].isOnbedekt[4] = false;
					if(i==maxAantal-1)kubussen[i][j][k].isOnbedekt[2] = true;
					else kubussen[i][j][k].isOnbedekt[2] = false;
				}
			}
		}
		aantalKubussen = maxAantal*maxAantal*maxAantal;
	}
	
	public void maakLeeg()
	{	for(int i=0 ; i<maxAantal ; i++)
		{	for(int j=0 ; j<maxAantal ; j++)
			{	//maxy[i][j]=0;
				//vierkanten[i][j]=new RVierkant(ribLengte, beginpos + i*ribLengte, -totLengte/2, beginpos + j*ribLengte);
				for(int k=0 ; k<maxAantal ; k++)
				{	kubussen[i][j][k] = null;
					//isZichtbaar[i][j][k] = false;
					//for(int m=0 ; m<6 ; m++)
					//{	isOnbedekt[i][j][k][m] = true;
					//}
				}
			}
		}
		aantalKubussen = 0;
	}
	
	public int geefAantalK()
	{	return aantalKubussen;
	}
	
	public boolean isGelijk(KubusRooster kr)
	{	
		if(maxAantal!=kr.maxAantal)return false;
		for(int i=0 ; i<maxAantal ; i++)
		{	for(int j=0 ; j<maxAantal ; j++)
			{	for(int k=0 ; k<maxAantal ; k++)
				{	if(kubussen[i][j][k]==null 
					   && kr.kubussen[i][j][k]!=null
					   || kubussen[i][j][k]!=null 
					   && kr.kubussen[i][j][k]==null)
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isGelijkAanzichten(KubusRooster kr)
	{	
		if(maxAantal!=kr.maxAantal)return false;
		for(int i=0 ; i<maxAantal ; i++)
		{	for(int j=0 ; j<maxAantal ; j++)
			{	for(int k=0 ; k<maxAantal ; k++)
				{	if(kubussen[i][j][k]!=null) 
					{   boolean bb = false;
						for(int m=0 ; m<maxAantal ; m++)
						{	if(!bb) bb = kr.kubussen[i][j][m]!=null;
						}
						boolean bv = false;
						for(int m=0 ; m<maxAantal ; m++)
						{	if(!bv) bv = kr.kubussen[i][m][k]!=null;
						}
						boolean br = false;
						for(int m=0 ; m<maxAantal ; m++)
						{	if(!br) br = kr.kubussen[m][j][k]!=null;
						}
						if(!bb || !bv || !br)return false;
					}
				}
			}
		}
		for(int i=0 ; i<maxAantal ; i++)
		{	for(int j=0 ; j<maxAantal ; j++)
			{	for(int k=0 ; k<maxAantal ; k++)
				{	if(kr.kubussen[i][j][k]!=null) 
					{   boolean bb = false;
						for(int m=0 ; m<maxAantal ; m++)
						{	if(!bb) bb = kubussen[i][j][m]!=null;
						}
						boolean bv = false;
						for(int m=0 ; m<maxAantal ; m++)
						{	if(!bv) bv = kubussen[i][m][k]!=null;
						}
						boolean br = false;
						for(int m=0 ; m<maxAantal ; m++)
						{	if(!br) br = kubussen[m][j][k]!=null;
						}
						if(!bb || !bv || !br)return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean isGelijkAanzichtenVB(KubusRooster kr)
	{	
		if(maxAantal!=kr.maxAantal)return false;
		for(int i=0 ; i<maxAantal ; i++)
		{	for(int j=0 ; j<maxAantal ; j++)
			{	for(int k=0 ; k<maxAantal ; k++)
				{	if(kubussen[i][j][k]!=null) 
					{   boolean bb = false;
						for(int m=0 ; m<maxAantal ; m++)
						{	if(!bb) bb = kr.kubussen[i][j][m]!=null;
						}
						boolean bv = false;
						for(int m=0 ; m<maxAantal ; m++)
						{	if(!bv) bv = kr.kubussen[i][m][k]!=null;
						}
						boolean br = false;
						for(int m=0 ; m<maxAantal ; m++)
						{	if(!br) br = kr.kubussen[m][j][k]!=null;
						}
						//if(!bb || !bv || !br)return false;
						if(!bv || !br)return false;
					}
				}
			}
		}
		for(int i=0 ; i<maxAantal ; i++)
		{	for(int j=0 ; j<maxAantal ; j++)
			{	for(int k=0 ; k<maxAantal ; k++)
				{	if(kr.kubussen[i][j][k]!=null) 
					{   boolean bb = false;
						for(int m=0 ; m<maxAantal ; m++)
						{	if(!bb) bb = kubussen[i][j][m]!=null;
						}
						boolean bv = false;
						for(int m=0 ; m<maxAantal ; m++)
						{	if(!bv) bv = kubussen[i][m][k]!=null;
						}
						boolean br = false;
						for(int m=0 ; m<maxAantal ; m++)
						{	if(!br) br = kubussen[m][j][k]!=null;
						}
						//if(!bb || !bv || !br)return false;
						if(!bv || !br)return false;
					}
				}
			}
		}
		return true;
	}
}
