package fi.kansbomengwt.client;



import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;




public class Kansboom {

	public Canvas kansboomCanvas;
	public Context2d gIm;
	
	
	static int BREEDTEVOLGORDE=60;
	int hoogte = 400;
	int hoogteKansboom;
	int breedte = 600;
	int aantalKolommen = 3;
	int breedteKolom;
	
	
	int breedteVolgordekolom;
	int breedteKansNaastKolom;
	int breedteKansOnderKolom;
	boolean volgorde = false;
	boolean eindkansNaast = false;
	boolean eindkansOnder = false;
	boolean bovenbalk = true;
	int offset = 2;
	
	String fontString = "12px sans-serif";
	String boldFontString = "bold 12px sans-serif";
	double fontHeight = 12;
	int rijhoogte = (int) fontHeight + offset;
	
	CssColor[] gekleurdeRij = new CssColor[6];
	
	int aantalOpties = 4;
	int[] teller = new int[6];
	int[] aantal = new int[6];
	boolean terugleggen = true;
	boolean kleur = true; 
	boolean letters = false;
	boolean kans = false;
	boolean breukOnder = false;
	String volgordeString;
	String[] letter = {"b","g","r","c","o","m"};
	String trekkingTekst = KansbomenGWT.rb.getString("trekkingBalkTekst");
	
	CssColor backgroundColor = CssColor.make("white");
	

	
	public Kansboom(int w, int h)
	{
		kansboomCanvas = Canvas.createIfSupported();
		gIm = kansboomCanvas.getContext2d();
		gIm.setFont(fontString);
		
		breedte = w;
		hoogte = h;
		gekleurdeRij[0] = CssColor.make(0,0,255);
		gekleurdeRij[1] = CssColor.make(0,200,0);
		gekleurdeRij[2] = CssColor.make(255,50,50);
		gekleurdeRij[3] = CssColor.make(0,220,220);
		gekleurdeRij[4] = CssColor.make(255,180,0);
		gekleurdeRij[5] = CssColor.make(220,0,220);
		
		breedteVolgordekolom = (int) (aantalKolommen*gIm.measureText("a").getWidth())+2*offset;
		if(volgorde)
			breedteKolom = (breedte - breedteVolgordekolom)/aantalKolommen;
		else if(eindkansNaast)
			breedteKolom = (breedte - breedteKansNaastKolom)/aantalKolommen;
		else if(eindkansOnder)
			breedteKolom = (breedte - breedteKansOnderKolom)/aantalKolommen;
		else 
			breedteKolom=breedte/aantalKolommen;
		if(bovenbalk)
			
			hoogteKansboom = hoogte - rijhoogte;
		else
			hoogteKansboom = hoogte;
		
		for(int i = 0; i<4; i++)
			aantal[i] = 4; 
			aantal[4] = -1;
			aantal[5] = -1;
			
		
		//TextMetrics tm = gIm.measureText("a");
		
		setSize(w, h);
	}
	
	void setSize(int w, int h) {
		breedte = w;
		hoogte = h;
		kansboomCanvas.setWidth(w + "px");
		kansboomCanvas.setHeight(h + "px");
		kansboomCanvas.setCoordinateSpaceWidth(w);
		kansboomCanvas.setCoordinateSpaceHeight(h);
		zetMaten(breedte, hoogte);
	}
	
	public void zetKleur(boolean b)
	{
		kleur = b;
	}
	
	public void zetKansVolgorde(int i)
	{
		volgorde = false;
		eindkansNaast = false;
		eindkansOnder = false;
		if(i==1)
			volgorde = true;
		else if(i==2)
			eindkansNaast = true;
		else if(i==3)
			eindkansOnder = true;
		setSize(breedte, hoogte);
	}
	
	public int berekenBreedteKansNaastKolom()
	{
		int m = 0;
		int k = aantalOpties;
		int n = aantalKolommen;
		
		for(int j=1; j<Math.pow(k,n)+1; j++)
		{	zetTellers(n,j-1);
			if(terugleggen)
				m = Math.max((int) gIm.measureText(breukNaast(eindkansMetTerug())).getWidth(),m);
			else if(bestaanKinderen(aantal, teller))
			{	
				m = Math.max((int) gIm.measureText(breukNaast(eindkansZonderTerug())).getWidth(),m);
			}
		}
		return m + 2 * offset;
	}
	
	public int berekenBreedteKansOnderKolom()
	{
		int m = 0;
		int k = aantalOpties;
		int n = aantalKolommen;
		
		for(int j=1; j<Math.pow(k,n)+1; j++)
		{	zetTellers(n,j-1);
			if(terugleggen)
				m = Math.max((int) breukBreedte(eindkansMetTerug()[0], eindkansMetTerug()[1]),m);
			else if(bestaanKinderen(aantal,teller))
			{	
				m = Math.max((int) breukBreedte(eindkansZonderTerug()[0],eindkansZonderTerug()[1]),m);
			}
		}
		return m + 2 * offset;
	}
	
	public int berekenBreedteVolgordekolom()
	{
		int m = 0;
		int k = aantalOpties;
		int n = aantalKolommen;
		for(int j=1; j<Math.pow(k,n)+1; j++)
		{	volgordeString = bepaalVolgordeString(j);
			m = Math.max((int) gIm.measureText(volgordeString).getWidth(),m);
		}
		
		return m + 2 * offset;
	}
	
	public void zetMaten(int b, int h)
	{	breedte = b;
	    hoogte = h;
	    
	    breedteVolgordekolom = berekenBreedteVolgordekolom();
	    breedteKansNaastKolom = berekenBreedteKansNaastKolom();
	    breedteKansOnderKolom = berekenBreedteKansOnderKolom();
	    if(volgorde)
	    	breedteKolom = (breedte - breedteVolgordekolom)/aantalKolommen;
	    else if(eindkansNaast)
	    	breedteKolom = (breedte - breedteKansNaastKolom)/aantalKolommen;
	    else if(eindkansOnder)
	    	breedteKolom = (breedte - breedteKansOnderKolom)/aantalKolommen;
	    else	
	    	breedteKolom=breedte/aantalKolommen;
	    if(bovenbalk)
	    	hoogteKansboom = h-rijhoogte;
	    else
	    	hoogteKansboom = h;
	   // super.setSize(b,h);
	}
	
	public void zetTerugleggen(boolean b)
	{
		terugleggen = b;
		paint();
	}
	
	public void zetTrekkingen(int i)
	{
		aantalKolommen = i;
		zetMaten(breedte, hoogte);
		paint();
	}
	
	public Canvas getCanvas()
	{
		return kansboomCanvas;
	}
	
	public void paint()
	{
		gIm.setFillStyle("white");
		gIm.fillRect(0, 0, breedte, hoogte);
		
		int k = aantalOpties;
		
		for(int i=0; i<aantalKolommen; i++)
		{	zetStartTellers();
			for(int j=0; j<Math.pow(k,i); j++)
			{	if(!terugleggen)
					zetTellers(i,j);	
				tekenKinderen(i, j, teller);	
			}
		}
		gIm.setFillStyle("black");
		gIm.setFont(fontString);
		if(volgorde)
			tekenVolgordeStrings();
		else if(eindkansNaast)
			tekenEindkansenNaast();
		else if(eindkansOnder)
			tekenEindkansenOnder();
		if(letters)
			tekenLetters();
		else if(kans)
			tekenKansen();
		if(bovenbalk)
			tekenBovenbalk();
	}
	
	private void zetStartTellers()
	{
		for(int p = 0; p < aantalOpties; p++)
			teller[p] = 0;
		for(int p = aantalOpties; p < 6; p++)
			teller[p] = -1;
	}
	
	private void zetTellers(int i, int j)
	{	int d = 0;
		int h = 0;
		int k = aantalOpties;
		int mod = 0;
		int macht = 0;
		
		zetStartTellers();
		
		for(int s=0; s<i; s++)
		{	d=(int) (j/Math.pow(k,s));
			mod=d%k;
			macht=(int) Math.pow(10,s);
			h=h+macht*mod;
		}
		for(int q=0; q<i; q++)
		{	int p = i-q-1;
			for(int n = 5; n > -1; n--)
			{
				if(h >= n * Math.pow(10, p))
				{
					teller[n]++;
					h = (int) (h - n * Math.pow(10, p));
					break;
				}
			}
		}
	}
	
	public boolean bestaanKinderen(int[] aantal, int[] teller)
	{
		boolean bestaanKinderen = true;
		for(int i = 0; i < teller.length; i++)
			bestaanKinderen = bestaanKinderen && teller[i] <= aantal[i];
		
		return bestaanKinderen;
	}
	
	private void tekenKinderen(int i, int j, int[] teller)
	{
		int b = breedteKolom;
		int h = hoogteKansboom;
		int k = aantalOpties;
		
		CssColor[] kleurRij = new CssColor[6];
		if(kleur) 
			kleurRij = gekleurdeRij;
		else
		{	for(int p = 0; p < 6; p++)
				kleurRij[p]= CssColor.make("black");
		}
		
		if(bestaanKinderen(aantal, teller))
		{ 	for(int p = 0; p < aantalOpties; p++)
			{	if(teller[p] < aantal[p])
				{	//gIm.setFillStyle(kleurRij[p]);
					gIm.setStrokeStyle(kleurRij[p]); //welke van de twee echt nodig? stroke denk ik
					gIm.beginPath();
					gIm.moveTo(i*b, (2*j+1)/(Math.pow(k,i)*2)*h+rijhoogte);
					gIm.lineTo((i+1)*b, (2*k*j+2*p+1)/(Math.pow(k,i+1)*2)*h+rijhoogte);
					gIm.stroke();
				}
			}
		}
		gIm.setStrokeStyle("black");
	}
	
	private void tekenLetters()
	{
		int b = breedteKolom;
		int h = hoogteKansboom;
		int k = aantalOpties;
		
		TextMetrics tm = gIm.measureText("a");
		double charWidth = tm.getWidth();
		
		gIm.setFillStyle(backgroundColor);
		for(int i=0; i<aantalKolommen; i++)
		{
			gIm.fillRect((2*i+1)*b/2-charWidth/2-offset, rijhoogte, charWidth+2*offset,h-rijhoogte);
		}
	
		gIm.setFillStyle("black");
		for(int i=0; i<aantalKolommen; i++)
		{	zetStartTellers();
			for(int j=0; j<Math.pow(k,i); j++)
			{	if(!terugleggen)
					this.zetTellers(i,j);	
				if(bestaanKinderen(aantal,teller))
				{ 	for(int p = 0; p < k; p++)
					if(teller[p]<aantal[p])
						gIm.fillText(letter[p],(2*i+1)*b/2-charWidth/2, 
								(int) (h*(4*k*j+k+2*p+1)/(4*Math.pow(k,i+1)))+fontHeight/3+rijhoogte);
				}		
			}
		}	
	}
	
	private void tekenKansen()
	{
		int k = aantalOpties;
		int a = 0;
		
		for(int p = 0; p < aantalOpties; p++)
			a += aantal[p];
		
		for(int i=0; i<aantalKolommen; i++)
		{	zetStartTellers();
			for(int j=0; j<Math.pow(k,i); j++)
			{if(terugleggen)
				{
				gIm.setFillStyle(backgroundColor);
				
				if(breukOnder)
					for(int p = 0; p < aantalOpties; p++)
						tekenRechthoekOnder(p, aantal[p], a, i, j);
				else 
					for(int p = 0; p < aantalOpties; p++)
						tekenRechthoekNaast(p, aantal[p], a, i, j);
				gIm.setFillStyle("black");
				if(breukOnder)
					for(int p = 0; p < aantalOpties; p++)
						tekenBreukOnder(p, aantal[p], a, i, j);	
				else 
					for(int p = 0; p < aantalOpties; p++)
						tekenBreukNaast(p, aantal[p], a, i, j);
				}
			else //zonder terugleggen
				{	
				this.zetTellers(i,j);	
				gIm.setFillStyle(backgroundColor);
				if(bestaanKinderen(aantal,teller))
				{ 	for(int p = 0; p < aantalOpties; p++)
					if(teller[p]<aantal[p])
						if(breukOnder)
						tekenRechthoekOnder(p, aantal[p]-teller[p], a-i, i, j);
						else	
						tekenRechthoekNaast(p, aantal[p]-teller[p], a-i, i, j);
					}	
				gIm.setFillStyle("black");
					if(bestaanKinderen(aantal,teller))
					{ 
						for(int p = 0; p < aantalOpties; p++)
							if(teller[p]<aantal[p])	
								if(breukOnder)
								tekenBreukOnder(p, aantal[p]-teller[p], a-i, i, j);
								else
								tekenBreukNaast(p, aantal[p]-teller[p], a-i, i, j);
					}	
				}
			}
		}
	}
	
	public void tekenVolgordeStrings()
	{	int k = aantalOpties;
		int n = aantalKolommen;
		int b = breedte;
		int h = hoogteKansboom;
		for(int j=0; j<Math.pow(k,n); j++)
		{	volgordeString = bepaalVolgordeString(j);
			
			zetTellers(n,j);
			if(terugleggen)
				gIm.fillText(volgordeString, b - breedteVolgordekolom + offset, 
						(int) ((2*j+1)*h/(2*Math.pow(k,n)))+fontHeight/3+rijhoogte);
			else if(bestaanKinderen(aantal,teller))
				gIm.fillText(volgordeString, b - breedteVolgordekolom + offset, 
						(int) ((2*j+1)*h/(2*Math.pow(k,n)))+fontHeight/3+rijhoogte);
		}
		
	}
	
	public String bepaalVolgordeString(int j)
	{
		volgordeString = "";
		int positie=j;
		
		for(int i = 1; i < aantalKolommen + 1; i++)
		{	for(int p = 0; p < 6; p++)
			{	if(positie < (p + 1) * Math.pow(aantalOpties, aantalKolommen - i))
				{	volgordeString = volgordeString + letter[p];
					positie -= p * Math.pow(aantalOpties, aantalKolommen-i);
					break;
				}
			}
		}
		return volgordeString;
	}
	
	public int[] eindkansMetTerug()
	{
		int n = aantalKolommen;
		int a = 0;
		for (int i = 0; i < aantalOpties; i++)
			a += aantal[i];
		int totaalTeller = 1;
		
		for(int i = 0; i < aantalOpties; i++)
			totaalTeller *= Math.pow(aantal[i], Math.max(teller[i], 0));
		
		int[] breuk = simplify(totaalTeller,(int) Math.pow(a, n));
		return breuk;
		
	}
	
	public int[] eindkansZonderTerug()
	{
		int n = aantalKolommen;
		int[] s = new int[aantalOpties];
		int noemer = 1;
		int totaalTeller = 1;
		int a = 0;
		for (int i = 0; i < aantalOpties; i++)
			a += aantal[i];
		
		for(int p = 0; p < aantalOpties; p++)
			s[p] = 1;
		noemer = 1;
		for(int p = 0; p < aantalOpties; p++)
			for(int i = 0; i < teller[p]; i++)
				s[p] *= aantal[p] - i;
		for(int i=0; i < n; i++)	
			noemer *= a-i;
		for(int p = 0; p < aantalOpties; p++)
			totaalTeller *= s[p];
					
		int[] breuk = simplify(totaalTeller, noemer);
		return breuk;
	}
	
	public void tekenEindkansenNaast()
	{	int k = aantalOpties;
		int n = aantalKolommen;
		int b = breedte;
		int h = hoogteKansboom;
		
		for(int j=0; j<Math.pow(k,n); j++)
		{	zetTellers(n,j);
			if(terugleggen)
			{
				gIm.fillText(breukNaast(eindkansMetTerug()), b - breedteKansNaastKolom + offset, 
						(int) ((2*j+1)*h/(2*Math.pow(k,n)))+fontHeight/3+rijhoogte);
			}
			else if(bestaanKinderen(aantal,teller))
			{	
				gIm.fillText(breukNaast(eindkansZonderTerug()), b - breedteKansNaastKolom + offset, 
						(int) ((2*j+1)*h/(2*Math.pow(k,n)))+fontHeight/3+rijhoogte);
			}
		}
	}

	
	public void tekenEindkansenOnder()
	{	int k = aantalOpties;
		int n = aantalKolommen;
		
		for(int j=0; j<Math.pow(k,n); j++)
		{	zetTellers(n,j);
			if(terugleggen)
			{	
				tekenEindBreukOnder(eindkansMetTerug()[0], eindkansMetTerug()[1], j);
			}
			else if(bestaanKinderen(aantal,teller))
			{	
				tekenEindBreukOnder(eindkansZonderTerug()[0], eindkansZonderTerug()[1], j);
			}
		}
	}
	
	public void zetLabelsKeuze(int i)
	{
		letters = false;
		kans = false;
		breukOnder = false;
		if(i==1)
			letters =true;
		else if(i==2)
			kans = true;
		else if(i==3)
		{	kans = true;
			breukOnder = true;
		}
	}
	
	public void zetOpties(int k, int[] opties)
	{
		aantalOpties = k;
		for(int i = 0; i < aantalOpties; i++)
			aantal[i] = opties[i];
		for(int i = aantalOpties; i < 6; i++)
			aantal[i] = -1;
		zetMaten(breedte, hoogte);
		paint();
	}
	
	public void zetBovenbalkZichtbaar(boolean b)
	{
		bovenbalk = b;
		if(bovenbalk)
    		rijhoogte = (int) fontHeight + offset;
    	else
    		rijhoogte = 0;
		setSize(breedte, hoogte);
	}
	
	public void tekenBovenbalk()
	{
		gIm.setFont(boldFontString);
		//gr.setFont(theBoldFont);
		int n = aantalKolommen;
		double xpos, ypos;
		
		
		
		for(int i=0; i<n; i++)
		{	TextMetrics tm = gIm.measureText(KansbomenGWT.rb.getString("trekkingBalkTekst")+" "+(i+1));
			double width = tm.getWidth();
			xpos = (2*i+1) * breedteKolom / 2 - width/2;
			ypos = rijhoogte;
			gIm.fillText(trekkingTekst+" "+(i+1), xpos, ypos);
		}	
		gIm.setFont(fontString);
	}
	
	public int[] simplify(int nom, int denom)
    {   if (denom < 0)
        {   nom = - nom;
            denom = - denom;
        }
        if (nom == 0)
            denom = 1;
        else
        {   int g = gcd(nom, denom);
            nom = nom / g;
            denom = denom / g;
        }
        int[] breuk = {nom, denom}; 
        return breuk;
    }
  
	public int gcd(int a, int b)
	{   int m = Math.abs(a);
		int n = Math.abs(b);
		int temp = 0;
		while ( n != 0 )
		{   temp = m % n;
		    m = n;
		    n = temp;
		}
		return m;
	}
	
	public String breukNaast(int[] breuk)
	{
		return breuk[0] +"/" + breuk[1];
	}
	  
	public void tekenRechthoekNaast(int a, int p, int q, int i, int j)
	{	int b = breedteKolom;
		int h = hoogteKansboom;
		int k = aantalOpties;
		double textWidth = gIm.measureText(breukNaast(simplify(p,q))).getWidth();
		
		gIm.fillRect((2*i+1)*b/2-textWidth/2-offset,
				(int) (h*(4*k*j+k+2*a+1)/(4*Math.pow(k,i+1)))-fontHeight/2+rijhoogte,
				textWidth+2*offset,fontHeight);
	}
	  
	public void tekenBreukNaast(int a, int p, int q, int i, int j)
	{	int b = breedteKolom;
		int h = hoogteKansboom;
		int k = aantalOpties;
		double textWidth = gIm.measureText(breukNaast(simplify(p,q))).getWidth();
		
		gIm.fillText(breukNaast(simplify(p,q)),(2*i+1)*b/2-textWidth/2, 
					(int) (h*(4*k*j+k+2*a+1)/(4*Math.pow(k,i+1)))+fontHeight/3+rijhoogte);
	}
	  
	public double breukBreedte(int nom, int denom)
	{		
		return Math.max(gIm.measureText(""+simplify(nom,denom)[0]).getWidth(), gIm.measureText(""+simplify(nom,denom)[1]).getWidth());
	}
	  
	public void tekenRechthoekOnder(int a, int p, int q, int i, int j)
	{	int b = breedteKolom;
		int h = hoogteKansboom;
		int k = aantalOpties;
		gIm.fillRect((2*i+1)*b/2 - breukBreedte(p,q)/2 - offset,
			(int) (h*(4*k*j+k+2*a+1)/(4*Math.pow(k,i+1))) - fontHeight + rijhoogte,
			 breukBreedte(p,q) + 2 * offset, 2 * fontHeight + 1);	
	}
	  
	public void tekenBreukOnder(int a, int p, int q, int i, int j)
	{	int b = breedteKolom;
		int h = hoogteKansboom;
		int k = aantalOpties;
		int[] breuk = simplify(p,q);
		gIm.fillText("" + breuk[0], (2*i+1)*b/2 - gIm.measureText("" + breuk[0]).getWidth()/2, 
			  (int) (h*(4*k*j+k+2*a+1)/(4*Math.pow(k,i+1))) - fontHeight/6 +rijhoogte);
		gIm.fillText("" + breuk[1], (2*i+1)*b/2  - gIm.measureText("" + breuk[1]).getWidth()/2, 
			  (int) (h*(4*k*j+k+2*a+1)/(4*Math.pow(k,i+1)))+ 5 * fontHeight/6 +rijhoogte  +2);
		gIm.beginPath();
		gIm.moveTo((2*i+1)*b/2  - breukBreedte(p,q)/2, h*(4*k*j+k+2*a+1)/(4*Math.pow(k,i+1)) + rijhoogte);
		gIm.lineTo((2*i+1)*b/2  + breukBreedte(p,q)/2, h*(4*k*j+k+2*a+1)/(4*Math.pow(k,i+1)) + rijhoogte);
		gIm.stroke(); //(is strokestyle in orde?)
	}
	  
	
	 
	public void tekenEindBreukOnder(int p, int q, int j)
	{	int k = aantalOpties;
		int n = aantalKolommen;
		int b = breedte;
		int h = hoogteKansboom;
	  
		int[] breuk = simplify(p,q);
		gIm.fillText("" + breuk[0], b - breedteKansOnderKolom/2 - gIm.measureText("" + breuk[0]).getWidth()/2, 
			  (int) ((2*j+1)*h/(2*Math.pow(k,n))) - fontHeight/6 +rijhoogte);
		gIm.fillText("" + breuk[1], b - breedteKansOnderKolom/2  - gIm.measureText("" + breuk[1]).getWidth()/2, 
			  (int) ((2*j+1)*h/(2*Math.pow(k,n)))+ 5 * fontHeight/6 +rijhoogte + 2);
		gIm.beginPath();
		gIm.moveTo(b - breedteKansOnderKolom/2  - breukBreedte(p,q)/2, (2*j+1)*h/(2*Math.pow(k,n)) + rijhoogte);
		gIm.lineTo(b - breedteKansOnderKolom/2  + breukBreedte(p,q)/2, (2*j+1)*h/(2*Math.pow(k,n)) + rijhoogte);
		gIm.stroke(); //is strokestyle in orde?
	}
	
}
