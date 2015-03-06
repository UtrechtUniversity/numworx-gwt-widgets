package fi.nabouwenaanzichtengwt.client;

//import java.awt.*;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class GetalRooster 
{	
	private double zijde;
	private int aantal;
	private int [][] hoogten;
	//private Font f;
	int xPos, yPos;
	int breedte, hoogte;
	int bbreedte, bhoogte;
	
	
	
	public GetalRooster(int aantal, int x, int y, int b, int h, int bb, int bh)
	{	//setBounds(x,y,b,b);
		this.aantal = aantal;
		xPos = x;
		yPos = y;
		breedte = b;
		hoogte = h;
		bbreedte = bb;
		bhoogte = bh;
		hoogten = new int[aantal][aantal];
		for(int i=0 ; i<aantal ; i++)
		{	for(int j=0 ; j<aantal ; j++)
			{	hoogten[i][j] = 0;
			}
		}
		zijde = 1.0*b/aantal;

		
	}
	
	public void paint(Context2d g, Polygon[][] p)
	{	//g.setColor(Color.black);
		//g.setFont(f);
		
		
		g.setFillStyle(CssColor.make(0,0,0));
		
		String fontString4 = "bold 28px arial, sans-serif";
		String fontString5 = "bold 26px arial, sans-serif";
		String fontString6 = "bold 24px arial, sans-serif";
		double corry = 0;
		if (aantal == 4)
		{	g.setFont(fontString4);
			corry = -4;
		}
		else if (aantal == 5)
		{	g.setFont(fontString5);
		}
		else if (aantal == 6)
		{	g.setFont(fontString6);
			corry = 0;
		}
		
		for (int i = 0; i < aantal; i++)
		{	for (int j = 0; j < aantal; j++)
			{	if (hoogten[i][j] > 0)
				{	

//p[i][aantal-1 - j].draw(g, CssColor.make(255,0,0), null);

					zijde = (((double) p[i][aantal-1 - j].geefPuntX(2)) - p[i][aantal-1 - j].geefPuntX(1)) / 2;

					double xT = p[i][aantal-1 - j].geefPuntX(1) + 
								(((double) p[i][aantal-1 - j].geefPuntX(2)) - p[i][aantal-1 - j].geefPuntX(1)) / 2;
					double yT = p[i][aantal-1 - j].geefPuntY(1) + 
								(((double) p[i][aantal-1 - j].geefPuntY(0)) - p[i][aantal-1 - j].geefPuntY(1)) / 2;

					//g.fillRect(xT, yT, 4, 4);
					
					//double xT = xPos + (zijde/2 + i*zijde)*corr + corrx - corrb;
					//double yT = yPos + (3*zijde/4 + j*zijde)*corr + corry + corrh;
					String s = Integer.toString(hoogten[i][j]);
					TextMetrics tm = g.measureText(s);
					int woordbreedte = (int) Math.round(tm.getWidth());
					g.fillText(s, xT - 8, yT + zijde/2 + corry);
					
				}
			}
		}
	}

	public void verhoog(int x, int y)
	{	if(hoogten[x][aantal-1-y]<aantal)hoogten[x][aantal-1-y]++;
	}
	
	public void verlaag(int x, int y)
	{	if(hoogten[x][aantal-1-y]>0)hoogten[x][aantal-1-y]--;
	}
	
	public void zetHoogte(int x, int y, int h)
	{	hoogten[x][aantal-1-y] = h+1;
	}
	
	public int geefHoogte(int x, int y)
	{	return hoogten[x][aantal-1-y];
	}
	
	public void wis()
	{	for(int i=0 ; i<aantal ; i++)
		{	for(int j=0 ; j<aantal ; j++)
			{	hoogten[i][j] = 0;
			}
		}
	}
}