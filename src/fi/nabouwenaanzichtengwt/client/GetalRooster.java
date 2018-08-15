package fi.nabouwenaanzichtengwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * klasse die een rooster met getallen representeert 
 * dat op het bovenaanzicht van een kubusbouwsel wordt
 * getekend (dit is de optie bovenAanzichtMetHoogtes)
 * en dat op elke positie de hoogte (het aantal kubusjes)
 * weergeeft
 */

public class GetalRooster 
{	
	private double zijde;
	/**
	 * rooster bevat aantal x aantal vakjes
	 */
	private int aantal;
	/**
	 * de actuele hoogtes van het bovenaanzicht waarop het rooster ligt 
	 */
	private int [][] hoogten;

	/**
	 * x- en y-positie (pixels) van het rooster
	 */
	int xPos, yPos;
	/**
	 * breedte van het rooster (pixels)
	 */
	int breedte;
	
	/**
	 * constructor initieer de hoogten-matrix
	 * @param aantal rooster heeft aantal x aantal vakjes
	 * @param x x-positie rooster
	 * @param y y-positie rooster
	 * @param b breedte rooster in pixels
	 * @param h niet gebruikt
	 * @param bb niet gebruikt
	 * @param bh niet gebruikt
	 */
	public GetalRooster(int aantal, int x, int y, int b, int h, int bb, int bh)
	{	
		this.aantal = aantal;
		xPos = x;
		yPos = y;
		breedte = b;
		hoogten = new int[aantal][aantal];
		for(int i=0 ; i<aantal ; i++)
		{	for(int j=0 ; j<aantal ; j++)
			{	hoogten[i][j] = 0;
			}
		}
		zijde = 1.0*b/aantal;
	}
	
	/**
	 * teken het rooster: het probleem is hier de keuze van de fontgroote
	 * en de keuze van de positie waar te tekst moet starten; merk op dat
	 * de vakjes waarin de tekst moet passen bekend zijn; pas dus het de fontgrootte
	 * aan aan het aantal vakjes in in rooster en de breedte van het rooster;
	 * bepaal verder ad hoc(!) 2 correcties voor de x-start van de tekst en 1 correctie
	 * voor de y-start van de tekst 
	 * @param g Context2d om te tekenen
	 * @param p een matrix van Polygons die de vakjes van
	 * het rooster representeren
	 */
	public void paint(Context2d g, Polygon[][] p)
	{		
		g.setFillStyle(CssColor.make(0,0,0));
		
		String fontString3 = "bold 30px arial, sans-serif";
		String fontString4 = "bold 28px arial, sans-serif";
		String fontString4a = "bold 26px arial, sans-serif";
		String fontString5 = "bold 26px arial, sans-serif";
		String fontString5a = "bold 23px arial, sans-serif";
		String fontString6 = "bold 24px arial, sans-serif";
		String fontString6a = "bold 20px arial, sans-serif";
		String fontString7 = "bold 22px arial, sans-serif";
		String fontString7a = "bold 18px arial, sans-serif";
		String fontString8 = "bold 19px arial, sans-serif";
		String fontString8a = "bold 16px arial, sans-serif";
		String fontString9 = "bold 16px arial, sans-serif";
		String fontString9a = "bold 13px arial, sans-serif";
		double corrx = 0;
		double corrx2 = 0;
		double corry = 0;
		if (aantal == 3)
		{	g.setFont(fontString3);
			corry = -4;
		}
		else if (aantal == 4)
		{	if (breedte < 50)
			{	g.setFont(fontString4a);
				corry = -1;
			}
			else
			{	g.setFont(fontString4a);
				corry = -4;
			}
		}	
		else if (aantal == 5)
		{	if (breedte < 50)
			{	g.setFont(fontString5a);
				corry = 2;
			}
			else
			{	g.setFont(fontString5);
				corry = 0;
			}
		}
		else if (aantal == 6)
		{	if (breedte < 50)
			{	g.setFont(fontString6a);
				corry = 2;
				corrx2 = 2;
			}	
			else
			{	g.setFont(fontString6);
				corry = 0;
			}
		}
		else if (aantal == 7)
		{	if (breedte < 50)
			{	g.setFont(fontString7a);
				corry = 2;
				corrx2 = 2;
			}	
			else
			{	g.setFont(fontString7);
				corry = 0;
			}	
		}
		else if (aantal == 8)
		{	if (breedte < 50)
			{	g.setFont(fontString8a);
				corry = 2;
				corrx2 = 2;
			}	
			else
			{	g.setFont(fontString8);
				corry = 2;
			}
			corrx = 2;
			
		}
		else if (aantal == 9)
		{	if (breedte < 50)
			{	g.setFont(fontString9a);
				corry = 2;
			}
			else
			{	g.setFont(fontString9);
				corry = 0;
			}
			corrx = 4;
		}

		for (int i = 0; i < aantal; i++)
		{	for (int j = 0; j < aantal; j++)
			{	if (hoogten[i][j] > 0)
				{	
					zijde = (((double) p[i][aantal-1 - j].geefPuntX(2)) - p[i][aantal-1 - j].geefPuntX(1)) / 2;

					double xT = p[i][aantal-1 - j].geefPuntX(1) + 
								(((double) p[i][aantal-1 - j].geefPuntX(2)) - p[i][aantal-1 - j].geefPuntX(1)) / 2;
					double yT = p[i][aantal-1 - j].geefPuntY(1) + 
								(((double) p[i][aantal-1 - j].geefPuntY(0)) - p[i][aantal-1 - j].geefPuntY(1)) / 2;

					String s = Integer.toString(hoogten[i][j]);
					TextMetrics tm = g.measureText(s);
					int woordbreedte = (int) Math.round(tm.getWidth());
					if (aantal > 9)
					{
						if (hoogten[i][j] < 10)
						{
							corrx = (aantal % 10)+ Math.abs(aantal-15);
						}
						else
						{
							corrx = 1;
						}
					}
					g.fillText(s, xT - 8 + corrx + corrx2, yT + zijde/2 + corry);
					
				}
			}
		}
	}

	/**
	 * verhoog de hoogte op positie (x,y) met 1 (als mogelijk)
	 * @param x x-positie nieuwe hoogte
	 * @param y y-positie nieuwe hoogte
	 */
	public void verhoog(int x, int y)
	{	if (hoogten[x][aantal-1-y] < aantal)
			hoogten[x][aantal-1-y]++;
	}

	/**
	 * verlaag de hoogte op positie (x,y) met 1 (als mogelijk)
	 * @param x x-positie nieuwe hoogte
	 * @param y y-positie nieuwe hoogte
	 */
	public void verlaag(int x, int y)
	{	if (hoogten[x][aantal-1-y] > 0)
			hoogten[x][aantal-1-y]--;
	}
	
	/**
	 * zet de hoogte op positie (x,y) op h
	 * @param x x-positie nieuwe hoogte
	 * @param y y-positie nieuwe hoogte
	 * @param h nieuwe hoogte
	 */
	public void zetHoogte(int x, int y, int h)
	{	hoogten[x][aantal-1-y] = h+1;
	}

	/**
	 * geef de hoogte op positie (x,y)
	 * @param x x-positie nieuwe hoogte
	 * @param y y-positie nieuwe hoogte
	 * @return hoogte op (x,y)
	 */
	public int geefHoogte(int x, int y)
	{	return hoogten[x][aantal-1-y];
	}
	
	/**
	 * zet alle hoogtes op nul
	 */
	public void wis()
	{	for(int i=0 ; i<aantal ; i++)
		{	for(int j=0 ; j<aantal ; j++)
			{	hoogten[i][j] = 0;
			}
		}
	}
}