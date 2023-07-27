package fi.geomalggwt.client;


import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;


class LineaalHor  
{	
	int schaal = 24;
	int aantal;
	int breedte;
	int hoogte;
	int min, max;
	int huidigeWaarde;
	Rectangle[] getalknoppen;
	int[] getallen;
	int nulPositie;

	boolean negatieveWaarden = true;
	
	Context2d lhContext2d;
	int lhX ;
	int lhY ;
	int lhBreedte;
	
	//public LineaalHor(int x, int y)
	public LineaalHor(int x, int y, Context2d ct2d)
	{			
		
		lhContext2d = ct2d;
		
		hoogte = 20;
		breedte = x - hoogte;
		
		//setBounds(hoogte / 2, y - hoogte - 42, breedte + hoogte / 2 - 1, hoogte);

		lhX = hoogte / 2;
		lhY = y - hoogte - 42;
		lhBreedte = breedte + hoogte / 2 - 1;
		
		aantal = breedte /(2 * schaal) + 2;
		
		getalknoppen = new Rectangle[2 * aantal + 1];
		getallen = new int[2 * aantal + 1];
		nulPositie = breedte / 2;
		min = -aantal + 2 - (nulPositie - breedte / 2) / (schaal);
		max = aantal + 2 - (nulPositie - breedte / 2) / (schaal);


	}
	
	public void paint()
	{
		paint(lhContext2d);
	}
	

	public void paint(Context2d g)
	{	
		//g.setColor(new Color(220,220,220));
		g.setFillStyle(CssColor.make(220, 220, 220));
		//g.fillRect(0, 0, getSize().width, getSize().height);
		g.fillRect(lhX, lhY, lhBreedte, hoogte);
		
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0, 0, 0));
		g.setFillStyle(CssColor.make(0, 0, 0));
		
		
		if (negatieveWaarden)
		{	//g.drawLine(0, 0, breedte, 0);
		
			g.beginPath();
			g.moveTo(lhX, lhY);
			g.lineTo(lhX + breedte, lhY);
			g.stroke();
		}
		else
		{	//g.drawLine(nulPositie, 0, breedte, 0);
		
			g.beginPath();
			g.moveTo(lhX + nulPositie, lhY);
			g.lineTo(lhX + breedte, lhY);
			g.stroke();
		
		
		}
		aantal = breedte / (2 * schaal);

		int einde = max - 3;
		if (!negatieveWaarden)
			einde = max - 2;
		
		String fontString = "12px sans-serif";
		g.setFont(fontString);
		
		for (int i = min; i < einde; i++)
		{	
			int dx = nulPositie + i * schaal;
			getalknoppen[i - min] = null;
			if ((negatieveWaarden && i < 0) || (i > 0))
			{	//g.drawLine(dx, 0, dx, 6);

				g.beginPath();
				g.moveTo(lhX + dx, lhY);
				g.lineTo(lhX + dx, lhY + 6);
				g.stroke();
			
				//g.drawLine(dx - schaal / 2, 0, dx - schaal / 2, 4);
			
				g.beginPath();
				g.moveTo(lhX + dx - schaal / 2, lhY);
				g.lineTo(lhX + dx - schaal / 2, lhY + 4);
				g.stroke();
				
				
				if (i < 0 || i >= 10) 
					dx -= 3;
				if (dx >= 3)
				{	//g.drawString(Integer.toString(i), dx - 3, 18);
				
					g.fillText(Integer.toString(i), lhX + dx - 3, lhY + 18);
				}
				
				// hor an ver positio is hier goed, maak ze iets breder
				//getalknoppen[i - min] = new Rectangle(lhX + dx - 3, lhY, 10, hoogte);
				getalknoppen[i - min] = new Rectangle(lhX + dx - 2, lhY, 12, hoogte);
				getallen[i - min] = i;
//g.setFillStyle(CssColor.make(0,255,0));
//g.fillRect(getalknoppen[i - min].x, getalknoppen[i - min].y, getalknoppen[i - min].width, getalknoppen[i - min].height);
				
			}	
		}
		//g.setFont(new Font("SansSerif", Font.BOLD, 12));
		fontString = "14px bold sans-serif";
		g.setFont(fontString);

		//g.drawLine(nulPositie, 0, nulPositie, 6);
		g.beginPath();
		g.moveTo(lhX + nulPositie, lhY);
		g.lineTo(lhX + nulPositie, lhY + 6);
		g.stroke();
		
		//g.drawString(Integer.toString(0), nulPositie - 3, 18);
		g.fillText(Integer.toString(0), lhX + nulPositie - 3, lhY + 18);
	}
	
	public void zetNegatieveWaarden(boolean b)
	{
		negatieveWaarden = b;
		if (negatieveWaarden)
		{	nulPositie = breedte / 2;
			aantal = breedte /(2 * schaal) + 2;
			min = -aantal + 2 - (nulPositie - breedte / 2) / (schaal);
			max = aantal + 2 - (nulPositie - breedte / 2) / (schaal);
		}
		else
		{
			nulPositie = 2 * schaal;
			aantal = breedte /(2 * schaal) + 2;
			min = -2;
			max = min + 2 * aantal;
		}
		paint();
		
	}
	
	public boolean mouseDownTouchStartAction(int eventX, int eventY)
	{	for (int i = 0; i < 2 * aantal + 1; i++)
		{	if ((getalknoppen[i] != null) && 
				getalknoppen[i].contains(eventX, eventY))
				//getalknoppen[i].contains(e.getX(), e.getY()))
			{	huidigeWaarde = getallen[i];
				return true;
			}
		}
		return false;
	}
	
		
}