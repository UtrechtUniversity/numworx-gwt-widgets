package fi.nabouwengwt.client;

import java.awt.*;

public class GetalRooster 
{	
	private double zijde;
	private int aantal;
	private int [][] hoogten;
	//private Font f;
	
	public GetalRooster(int aantal, int x, int y, int b)
	{	//setBounds(x,y,b,b);
		this.aantal = aantal;
		hoogten = new int[aantal][aantal];
		for(int i=0 ; i<aantal ; i++)
		{	for(int j=0 ; j<aantal ; j++)
			{	hoogten[i][j] = 0;
			}
		}
		zijde = 1.0*b/aantal;
		//f = new Font("SansSerif", Font.PLAIN, (int)(zijde*2/3));
	}
	
	/*public void paint(Graphics g)
	{	g.setColor(Color.black);
		g.setFont(f);
		//g.drawRect(0,0,getSize().width - 1, getSize().height - 1);
		for(int i=0 ; i<aantal ; i++)
		{	for(int j=0 ; j<aantal ; j++)
			{	if(hoogten[i][j]>0)
				{	int x = (int)(zijde/2 + i*zijde);
					int y = (int)(zijde/3*2 + j*zijde);
					String s = Integer.toString(hoogten[i][j]);
					FontMetrics fm = g.getFontMetrics(f);
					int woordbreedte = fm.stringWidth(s);
					g.drawString(s,x - woordbreedte/2, y + fm.getAscent()/2 - fm.getDescent() );
				}
			}
		}
	}
	*/
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