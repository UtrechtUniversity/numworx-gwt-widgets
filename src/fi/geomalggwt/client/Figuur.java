package fi.geomalggwt.client;

//import java.awt.*;
//import java.io.Serializable;

class Figuur // implements Serializable
{	int aantalx, aantaly;
	Lijnstuk[] lsx, lsy;
	Point positie, posx, posy; 
	int minx,maxx,miny,maxy;
	static boolean geslotenVeld;
	static int breedte = 500, hoogte = 350;
	
	static int sens = 6;
	
	public Figuur(int x, int y)
	{	aantalx = 0;
		aantaly = 0;
		lsx = new Lijnstuk[20];
		lsy = new Lijnstuk[20];
		positie = new Point(x,y);
		posx = new Point(x,y);
		posy = new Point(x,y);
	}
	
	public Figuur(Figuur f)
	{	aantalx = f.aantalx;
		aantaly = f.aantaly;
		lsx = new Lijnstuk[20];
		lsy = new Lijnstuk[20];
		positie = new Point(f.positie.x,f.positie.y);
		posx = new Point(f.posx.x,f.posx.y);
		posy = new Point(f.posy.x,f.posy.y);
		for(int i=0 ; i<aantalx ; i++)
		{	lsx[i] = new Lijnstuk(f.lsx[i]);
		}
		for(int i=0 ; i<aantaly ; i++)
		{	lsy[i] = new Lijnstuk(f.lsy[i]);
		}
	}
	
	static void zetVeldSizes(int b, int h)
	{	breedte = b;
		hoogte = h;
	}
	
	static void zetGeslotenVeld(boolean b)
	{	geslotenVeld = b;
	}
	
	public void voegToe(Lijnstuk ls)
	{	if (ls.stand == Lijnstuk.HOR)
		{	ls.zetPositie(posx.x, posx.y);
			posx.x += ls.d;
			lsx[aantalx] = ls;
			aantalx++;
		}
		else
		{	ls.zetPositie(posy.x, posy.y);
			posy.y -= ls.d;
			lsy[aantaly] = ls;
			aantaly++;
		}
	}
	
	public void telOp(Lijnstuk ls)
	{	if(ls.stand==Lijnstuk.HOR)
		{	if(aantalx==0)voegToe(ls);
			else 
			{	lsx[aantalx-1].telOp(ls);
				if(lsx[aantalx-1].isNul())
				{	aantalx--;
					if(aantalx==0)aantaly=0;
				}
			}	
		}
		else
		{	if(aantaly==0)voegToe(ls);
			else 
			{	lsy[aantaly-1].telOp(ls);
				if(lsy[aantaly-1].isNul())
				{	aantaly--;
					if(aantaly==0)aantalx=0;
				}
			}
		}
	}
	
	public static boolean past(Figuur f1,Figuur f2)
	{	
		if(Figuur.pastVolgorde(f1,f2))return true;
		if(Figuur.pastVolgorde(f2,f1))return true;
		return false;
	}
	
	public static boolean pastVolgorde(Figuur f1,Figuur f2)
	{	if(Figuur.pastHor(f1,f2))return true;
		if(Figuur.pastVer(f1,f2))return true;
		return false;
	}
	
	public static boolean pastHor(Figuur f1,Figuur f2)
	{	if(f1.raakStaartX(f2.positie.x, f2.positie.y))
		{	if(f2.aantaly==0)return true;
			if(f2.aantaly==f1.aantaly && f2.aantalx!=0)
			{	for(int i=0 ; i<f1.aantaly ; i++)
				{	if(!Lijnstuk.isGelijk(f1.lsy[i],f2.lsy[i]))return false;
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean pastVer(Figuur f1,Figuur f2)
	{	if(f1.raakStaartY(f2.positie.x, f2.positie.y))
		{	if(f2.aantalx==0)return true;
			if(f2.aantalx==f1.aantalx && f2.aantaly!=0)
			{	for(int i=0 ; i<f1.aantalx ; i++)
				{	if(!Lijnstuk.isGelijk(f1.lsx[i],f2.lsx[i]))return false;
				}
				return true;
			}
		}
		return false;
	}
	
	public static Figuur verbind(Figuur f1,Figuur f2, boolean telOp)
	{	Figuur f = null;
		if(Figuur.pastHor(f1,f2))
		{	for(int i=0 ; i<f2.aantalx ; i++)
			{	if(telOp)f1.telOp(f2.lsx[i]);
				else f1.voegToe(f2.lsx[i]);
			}
			return f1;
		}
		if(Figuur.pastVer(f1,f2))
		{	for(int i=0 ; i<f2.aantaly ; i++)
			{	if(telOp)f1.telOp(f2.lsy[i]);
				else f1.voegToe(f2.lsy[i]);
			}
			return f1;
		}
		if(Figuur.pastHor(f2,f1))
		{	for(int i=0 ; i<f1.aantalx ; i++)
			{	if(telOp)f2.telOp(f1.lsx[i]);
				else f2.voegToe(f1.lsx[i]);
			}
			return f2;
		}
		if(Figuur.pastVer(f2,f1))
		{	for(int i=0 ; i<f1.aantaly ; i++)
			{	if(telOp)f2.telOp(f1.lsy[i]);
				else f2.voegToe(f1.lsy[i]);
			}
			return f2;
		}
		return f;
	}
	public boolean raakLijn(int x, int y)
	{	for(int i=1 ; i<aantalx ; i++)
		{	if(raakLijnX(i,x,y))return true;
		}
		for(int i=1 ; i<aantaly ; i++)
		{	if(raakLijnY(i,x,y))return true;
		}
		return false;
	}
	public boolean raakLijnX(int nr, int x, int y)
	{	int xRaak = lsx[nr].positie.x;
		if (new Rectangle(xRaak-sens,miny,2*sens,maxy-miny).contains(x,y))
			return true;
		else return false;
	}
	
	public boolean raakLijnY(int nr, int x, int y)
	{	int yRaak = lsy[nr].positie.y;
		if (new Rectangle(minx,yRaak-sens,maxx-minx,2*sens).contains(x,y))
			return true;
		else return false;
	}
	public boolean raakKop(int x, int y )
	{	if (new Rectangle(positie.x - sens, positie.y - sens, 2*sens, 2*sens).contains(x,y))
			return true;
		else 
			return false;
	}
	
	public boolean raakStaartX(int x, int y )
	{	if (new Rectangle(posx.x - sens, posx.y - sens, 2*sens, 2*sens).contains(x,y))
			return true;
		else 
			return false;
	}
	
	public boolean raakStaartY(int x, int y )
	{	if (new Rectangle(posy.x - sens, posy.y - sens, 2*sens, 2*sens).contains(x,y))
			return true;
		else 
			return false;
	}
	
	public boolean raakRechthoek(int x, int y )
	{	if (new Rectangle(minx, miny, maxx - minx, maxy - miny).contains(x,y))
			return true;
		if (miny == maxy && new Rectangle(minx, miny - sens, maxx - minx, 2*sens).contains(x,y))
			return true;
		if (minx == maxx && new Rectangle(minx - sens, miny, 2*sens, maxy - miny).contains(x,y))
			return true;
		return false;
	}
	
	public boolean raakSplits(int x, int y )
	{	for(int i=0 ; i<aantalx ; i++)
		{	if (new Rectangle(lsx[i].positie.x-sens, lsx[i].positie.y-sens, 2*sens, 2*sens).contains(x,y))
				return true;
		}
		for(int i=0 ; i<aantaly ; i++)
		{	if (new Rectangle(lsy[i].positie.x-sens, lsy[i].positie.y-sens, 2*sens, 2*sens).contains(x,y))
				return true;
		}
		return false;
	}
	
	public Figuur splitsLijnstukAf(int x, int y)
	{	Figuur f = null;
		for(int i=1 ; i<aantalx ; i++)
		{	if (new Rectangle(lsx[i].positie.x-sens, lsx[i].positie.y-sens, 2*sens, 2*sens).contains(x,y))
			{	f = new Figuur(lsx[i].positie.x, lsx[i].positie.y);
				for(int j=i ; j<aantalx ; j++)
				{	f.voegToe(lsx[j]);
				}
				aantalx = i;
				return f;
			}
		}
		for(int i=0 ; i<aantaly ; i++)
		{	if( new Rectangle(lsy[i].positie.x-sens, lsy[i].positie.y-sens, 2*sens, 2*sens).contains(x,y))
			{	f = new Figuur(lsy[i].positie.x, lsy[i].positie.y);
				for(int j=i ; j<aantaly ; j++)
				{	f.voegToe(lsy[j]);
				}
				aantaly = i;
				return f;
			}
		}
		return f;
	}
	
	public Figuur dupliceer()
	{	Figuur fn = new Figuur(positie.x,positie.y);
		for(int i=0 ; i<aantalx ; i++)
		{	fn.voegToe(new Lijnstuk(lsx[i]));
		}
		for(int i=0 ; i<aantaly ; i++)
		{	fn.voegToe(new Lijnstuk(lsy[i]));
		}
		return fn;
	}
		
	public Figuur splits()
	{	Figuur fn = new Figuur(positie.x,positie.y);
		int[] j = {1,2,3,0};
		for(int k=0 ; k<aantalx ; k++)
		{	for(int i=0 ; i<4 ; i++)
			{	if(lsx[k].lengte[j[i]] != 0 )
				{	fn.voegToe(new Lijnstuk(j[i],lsx[k].lengte[j[i]],Lijnstuk.HOR,lsx[k].positie.x,lsx[k].positie.y));
				}
			}
		}
		for(int k=0 ; k<aantaly ; k++)
		{	for(int i=0 ; i<4 ; i++)
			{	if(lsy[k].lengte[j[i]] != 0 )
				{	fn.voegToe(new Lijnstuk(j[i],lsy[k].lengte[j[i]],Lijnstuk.VER,lsy[k].positie.x,lsy[k].positie.y));
				}
			}
		}
		return fn;
	}
	
	public Figuur splitsVolledig()
	{	Figuur fn = new Figuur(positie.x,positie.y);
		int[] j = {1,2,3,0};
		for(int k=0 ; k<aantalx ; k++)
		{	for(int i=0 ; i<4 ; i++)
			{	while(lsx[k].lengte[j[i]] != 0 )
				{	if(lsx[k].lengte[j[i]] > 0 )
					{	Lijnstuk lst = new Lijnstuk(j[i],1,Lijnstuk.HOR,0,0);
						fn.voegToe(lst);
						lsx[k].lengte[j[i]]--;
						
					}
					else if(lsx[k].lengte[j[i]] < 0 )
					{	Lijnstuk lst = new Lijnstuk(j[i],-1,Lijnstuk.HOR,0,0);
						fn.voegToe(lst);
						lsx[k].lengte[j[i]]++;
					}
				}
			}
		}
		for(int k=0 ; k<aantaly ; k++)
		{	for(int i=0 ; i<4 ; i++)
			{	while(lsy[k].lengte[j[i]] != 0 )
				{	if(lsy[k].lengte[j[i]] > 0 )
					{	Lijnstuk lst = new Lijnstuk(j[i],1,Lijnstuk.VER,0,0);
						fn.voegToe(lst);
						lsy[k].lengte[j[i]]--;
					}
					else if(lsy[k].lengte[j[i]] < 0 )
					{	Lijnstuk lst = new Lijnstuk(j[i],-1,Lijnstuk.VER,0,0);
						fn.voegToe(lst);
						lsy[k].lengte[j[i]]++;
					}
				}
			}
		}
		return fn;
	}
	
	public void maakGeheel()
	{	for(int i=aantalx-1 ; i>0 ; i--)
		{	
			if(!lsx[i].isNul())
			{	aantalx--;
				telOp(lsx[i]);
			}
			
		}
		for(int i=aantaly-1 ; i>0 ; i--)
		{	
			if(!lsy[i].isNul())
			{	aantaly--;
				telOp(lsy[i]);
			}
			
			
		}
	}
	
	public void zetPositie(int x, int y)
	{	positie.x = x;
		positie.y = y;
	}
	
	public void veranderPositie(int dx,int dy)
	{	
		
		positie.x = positie.x + dx;
		positie.y = positie.y + dy;
		
		posx.x = posx.x + dx;
		posy.x = posy.x + dx;
		posx.y = posx.y + dy;
		posy.y = posy.y + dy;
		
		maxx = maxx + dx;
		minx = minx + dx;
		
		maxy = maxy + dy;

		miny = miny + dy;
		
		for(int i=0 ; i<aantalx ; i++)
		{	lsx[i].positie.x = lsx[i].positie.x + dx;
			lsx[i].positie.y = lsx[i].positie.y + dy;
		}
		for(int i=0 ; i<aantaly ; i++)
		{	lsy[i].positie.x = lsy[i].positie.x + dx;
			lsy[i].positie.y = lsy[i].positie.y + dy;
		}
		if (geslotenVeld)
		{
			if (maxx > breedte)
			{	veranderPositie(breedte - maxx, 0);


			}
			if (minx < 0)
			{	veranderPositie(-minx, 0);
			
			}
			if (maxy > hoogte)
			{	veranderPositie(0, hoogte - maxy);
			
			}
			if (miny < 25) 
			{	veranderPositie(0, 25 - miny);
			
			}
			
		}
	}
	public void plaatsOpGrid()
	{	int x = positie.x+3000;
		int y = positie.y+3000;
		int ex = x%6;
		int ey = y%6;
		if(ex<3)veranderPositie(-ex,0);
		else veranderPositie(6-ex,0);
		if(ey<3)veranderPositie(0,-ey);
		else veranderPositie(0,6-ey);
	}
	
	void pasAanVar(int varnr, int waarde)
	{	posx.x = positie.x;
		posy.x = positie.x;
		for(int i=0 ; i<aantalx ; i++)
		{	lsx[i].zetVar(varnr, waarde);
			if(i>0)lsx[i].positie.x = lsx[i-1].positie.x + lsx[i-1].d;
		}
		for(int i=0 ; i<aantaly ; i++)
		{	lsy[i].zetVar(varnr, waarde);
			if(i>0)lsy[i].positie.y = lsy[i-1].positie.y - lsy[i-1].d;
		}
	}
	
	public Figuur spiegel()
	{	Figuur fn = new Figuur(positie.x,positie.y);
		
		if(aantalx!=0 && aantaly!=0)
		{	for(int i=0 ; i<aantalx ; i++)
			{	for(int j=0 ; j<4 ; j++)
				{	lsx[i].lengte[j] = -lsx[i].lengte[j];
					lsx[i].zetLengte();
				}
				fn.voegToe(lsx[i]);
			}
			for(int i=0 ; i<aantaly ; i++)
			{	for(int j=0 ; j<4 ; j++)
				{	lsy[i].lengte[j] = -lsy[i].lengte[j];
					lsy[i].zetLengte();
				}
				fn.voegToe(lsy[i]);
			}
		}
		else return this;
		return fn;
	}
	
	public Figuur draai()
	{	Figuur fn = new Figuur(positie.x,positie.y);
		for(int i=0 ; i<aantalx ; i++)
		{	lsx[i].stand = Lijnstuk.VER;
			fn.voegToe(lsx[i]);
		}
		for(int i=0 ; i<aantaly ; i++)
		{	lsy[i].stand = Lijnstuk.HOR;
			fn.voegToe(lsy[i]);
		}
		return fn;
	}
	public Figuur negatief()
	{	Figuur fn = new Figuur(positie.x,positie.y);
		if(aantaly==0 && aantalx!=0)
		{	for(int i=0 ; i<aantalx ; i++)
			{	for(int j=0 ; j<4 ; j++)
				{lsx[i].lengte[j] = -lsx[i].lengte[j];
				 lsx[i].zetLengte();
				}
				fn.voegToe(lsx[i]);
			}
		}
		else if(aantalx==0 && aantaly!=0)
		{	for(int i=0 ; i<aantaly ; i++)
			{	for(int j=0 ; j<4 ; j++)
				{lsy[i].lengte[j] = -lsy[i].lengte[j];
				 lsy[i].zetLengte();
				}
				fn.voegToe(lsy[i]);
			}
		}
		else if(aantalx!=0 && aantaly!=0)
		{	for(int i=0 ; i<aantalx ; i++)
			{	for(int j=0 ; j<4 ; j++)
				{lsx[i].lengte[j] = -lsx[i].lengte[j];
				 lsx[i].zetLengte();
				}
				fn.voegToe(lsx[i]);
			}
			for(int i=0 ; i<aantaly ; i++)
			{	fn.voegToe(lsy[i]);
			}
		}
		
		return fn;
	}
}