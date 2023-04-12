package fi.geomalggwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

class LineaalVer  
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
	
	Context2d lvContext2d;
	int lvX = 2;
	int lvY = 5;
	
	public LineaalVer(int y, Context2d ct2d)
	{			
		lvContext2d = ct2d;
		
		breedte = 20;
		hoogte = y-breedte-50;

		//setBounds(2,5,breedte,hoogte);
		
		aantal = hoogte/(2*schaal)+2;
		
		getalknoppen = new Rectangle[2*aantal+1];
		getallen = new int[2*aantal+1];
		nulPositie = hoogte/2;
		min = -aantal+2-(nulPositie-hoogte/2)/(schaal);
		max = aantal+2-(nulPositie-hoogte/2)/(schaal);
		
	}
	
	public void paint()
	{
		paint(lvContext2d);
	}
	
	public void paint(Context2d g)
	{
		
		g.setFillStyle(CssColor.make(220, 220, 220));
		//g.setColor(new Color(220,220,220));
		g.fillRect(lvX, lvY, breedte, hoogte);
		//g.fillRect(0, 0, getSize().width, getSize().height);
		
		g.setStrokeStyle(CssColor.make(0, 0, 0));
		g.setFillStyle(CssColor.make(0, 0, 0));
		//g.setColor(Color.black);
		
		if (negatieveWaarden)
		{	//g.drawLine(breedte-1,0,breedte-1,hoogte);
		
			g.beginPath();
			g.moveTo(lvX + breedte-1,lvY);
			g.lineTo(lvX + breedte-1,lvY+hoogte);
			g.stroke();
		}
		else
		{	//g.drawLine(breedte-1,0,breedte-1,nulPositie);
		
			g.beginPath();
			g.moveTo(lvX + breedte-1,lvY);
			g.lineTo(lvX + breedte-1,lvY+nulPositie);
			g.stroke();
		
		}
		aantal = hoogte / (2 * schaal);
		
		int einde = max - 3;
		if (!negatieveWaarden)
			einde = max - 2;

		String fontString = "12px sans-serif";
		g.setFont(fontString);
		
		for (int i = min; i < einde; i++)
		{	int dy = nulPositie - i * schaal;
			getalknoppen[i - min] = null;
			if ((negatieveWaarden && i < 0) || i > 0)
			{
				//g.drawLine(breedte, dy, breedte - 6, dy);
				
				g.beginPath();
				g.moveTo(lvX + breedte, lvY + dy);
				g.lineTo(lvX + breedte - 6, lvY + dy);
				g.stroke();
				
				//g.drawLine(breedte, dy - schaal / 2, breedte - 4, dy - schaal / 2);
				
				g.beginPath();
				g.moveTo(lvX + breedte, lvY + dy - schaal / 2);
				g.lineTo(lvX + breedte - 4, lvY + dy - schaal / 2);
				g.stroke();
				
//				g.drawString(Integer.toString(i), breedte - 18, dy + 3);
				g.fillText(Integer.toString(i), lvX + breedte - 18, lvY + dy + 3);
				
				//getalknoppen[i-min] = new Rectangle(0,dy-8,breedte,10);
				// hor en ver positie is hier goed, maak ze iets hoger
				//getalknoppen[i-min] = new Rectangle(lvX,lvY + dy - 6, breedte, 10);
				getalknoppen[i-min] = new Rectangle(lvX,lvY + dy - 6, breedte, 12);
				getallen[i-min] = i;
				
//g.setFillStyle(CssColor.make(0,255,0));
//g.fillRect(getalknoppen[i - min].x, getalknoppen[i - min].y, getalknoppen[i - min].width, getalknoppen[i - min].height);
				
			}
		}
	
		
//		g.setFont(new Font("SansSerif", Font.BOLD, 12));
		fontString = "14px bold sans-serif";
		g.setFont(fontString);
				
		g.beginPath();
		g.moveTo(breedte, lvY + nulPositie);
		g.lineTo(breedte - 6, lvY + nulPositie);
		g.stroke();
		//g.drawLine(breedte, nulPositie, breedte - 6, nulPositie);
		
		g.beginPath();
		g.moveTo(lvX + breedte, lvY + nulPositie - schaal / 2);
		g.lineTo(lvX + breedte - 4, lvY + nulPositie - schaal / 2);
		g.stroke();
		//g.drawLine(breedte, nulPositie - schaal / 2, breedte - 4, nulPositie - schaal / 2);
		
//		g.drawString(Integer.toString(0),breedte - 18, nulPositie + 3);
		g.fillText(Integer.toString(0), lvX + breedte - 18, lvY + nulPositie + 3);
	}
	
	public void zetNegatieveWaarden(boolean b)
	{
		negatieveWaarden = b;
		if (negatieveWaarden)
		{	nulPositie = hoogte / 2;
			aantal = hoogte /(2 * schaal) + 2;
			min = -aantal + 2 - (nulPositie - hoogte / 2) / (schaal);
			max = aantal + 2 - (nulPositie - hoogte / 2) / (schaal);
		}
		else
		{
			nulPositie = hoogte - schaal;
			aantal = hoogte /(2 * schaal) + 2;
			min = -1;
			max = min + 2 * aantal;
		}
		paint();
		
	}
	
	public boolean mouseDownTouchStartAction(int eventX, int eventY)
	{	
		for(int i=0 ; i<2*aantal+1 ; i++)
		{	if ((getalknoppen[i] != null) && 
				getalknoppen[i].contains(eventX, eventY))
				// getalknoppen[i].contains(e.getX(), e.getY()))
			{	huidigeWaarde = getallen[i];
				return true;
			}
		}
		return false;
	}
	
			
}