package fi.nabouwengwt.client;



class Polygon
{
	int aantalPunten;
	int[] puntenX;
	int[] puntenY;
	
	public Polygon()
	{
		
	}
	public Polygon(int[] puntenX, int[]puntenY, int aantalPunten)
	{
		this.aantalPunten = aantalPunten;
		this.puntenX = puntenX;
		this.puntenY = puntenY;
	}
	
	public int geefPuntX(int nr)
	{ 	return puntenX[nr];
	}
	
	public int geefPuntY(int nr)
	{ 	return puntenY[nr];
	}
	
	public int geefAantalPunten()
	{	return aantalPunten;
	}
	
	public boolean contains(int x, int y) {
		return false;
	}
}
