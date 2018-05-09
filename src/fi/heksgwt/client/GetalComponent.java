package fi.heksgwt.client;

import fi.heksgwt.client.scobjects.*;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.i18n.client.NumberFormat;

public class GetalComponent extends ScComponent 
{
	private int waarde;
	private String waardeText = "";
	private String formula = "";
	private boolean isTemp, instelbaar, bekend, leeg;
	
	Context2d getalContext2d;
	
	boolean visible = true;
	
	boolean transparent = false;
	boolean p23Klein = false;
	boolean p23Emmer = false;
	
	HeksGWT owner;

	public GetalComponent(int x, int y, int b, int h, HeksGWT owner) 
	{
		super(x, y, b, h);
		this.owner = owner;
		
		waarde = 0;
		bekend = true;
		leeg = false;
		instelbaar = false;
		isTemp = false;
	}

	public void setVisible(boolean b)
	{
		visible = b;
	}

	public void paint(Context2d g)
	{
		if (!visible)
			return;
		
		int fontSize = (int) Math.round(3 * schaal * relh / 4);
		String fontString = "" + fontSize + "px arial, sans-serif";
		
		g.setFont(fontString);
		
		String s;
		if (bekend) 
		{
			if (isTemp)
				s = Integer.toString(waarde) + "\u2103";
			else
				s = Integer.toString(waarde);
		} 
		else 
		{
			if (leeg)
				s = "";
			else if (isTemp)
				s = "...\u00B0C";
			else
				s = "...";
		}
		
		TextMetrics tm = g.measureText(s);
		int woordbreedte = (int) Math.round(tm.getWidth());
		
		if (!transparent)
		{	
			g.setFillStyle(CssColor.make(255,255,255));
			g.fillRect(xPos, yPos, breedte, hoogte);
		}
		
		g.setFillStyle(CssColor.make(0,0,0));
		if (p23Klein)
			g.fillText(s, xPos + (breedte - woordbreedte) / 2, yPos + 30 * schaal);
		else if (p23Emmer)
			g.fillText(s, xPos + (breedte - woordbreedte) / 2, yPos + 4 * schaal);
		else
			g.fillText(s, xPos + (breedte - woordbreedte) / 2, yPos + 42 * schaal);
	}

	public String geefWaardeText() 
	{
		return waardeText; 
	}	

	public void zetWaarde(String t) 
	{
		waardeText = t;
		
		int w;
		try 
		{
			w = Integer.parseInt(t);
			zetBekend(true);
			zetWaarde(w);
		} 
		catch (NumberFormatException ex) 
		{}
	}	

	/**
	 * kijk of de tekst in de invoerpopup valide invoer is m.b.v. expressie.
	 */
	public void zetInvulWaarde(String formula)
	{
		try
		{
			this.formula = formula;
			zetInvulWaarde0();
		}
		catch (RestartException r)
		{
			r.restart(new Runnable()
			{
				public void run()
				{
					try
					{
						zetInvulWaarde0();
					}
					catch (RestartException e)
					{
						e.restart(this);
					}
				}
			});
		}
	}
	
	public void zetInvulWaarde0() throws RestartException
	{
		try
		{	
			String s = formula;
			s = s.replace(',','.');
			
			// formules uit formuleeditor zoals 3$m2@ verwerken
			fi.wiskopdr.expressies.Expressie exp = FormuleParser.geefExpressie(addFormulaCodes(formula));
			
			if (s.equals("") || (exp == null))
			{
				zetWaarde("");
			}
			else
			{ 
				boolean casNodig = false;
				if (exp!=null) 
					casNodig = exp.toString().indexOf("$i")>-1 
						|| exp.toString().indexOf("$d")>-1 
						|| exp.toString().indexOf("$T")>-1  
						|| exp.toString().indexOf("$S")>-1  
						|| exp.toString().indexOf("$P")>-1;
				if (casNodig)
					exp = fi.wiskopdr.expressies.Expressie.evalWithCAS(exp); // deze kan een restartexception geven
				Double d = null;
				if (exp != null && exp.isWaarde())
				{
					d = exp.geefWaarde();
					// afronden op 3 decimalen; format voor grote getallen met wetenschappelijke notatie zoals 1234567^2 = 1524155677489...
					String formatted = NumberFormat.getFormat("0.###").format(d);
					formatted = formatted.replace(',', '.'); // dit moet, anders gaat BasisExpressie.geefWaarde() met Double.valueOf(basisString) mis
					zetWaarde(formatted);
				}
				else
				{
					zetWaarde("");
				}
			}
		}	
		catch (NumberFormatException ex)
		{
			zetWaarde("");
		}
	}
  
	/**
	 * Surround the given string with the formule codes "$f" and "@".
	 * Used for fomula editor.
	 * 
	 * @param string
	 * @return
	 */
	private String addFormulaCodes(String string)
	{
		String startCode = "$f";
		String endCode = "@";
		String s = startCode + string + endCode;
		return s;
	}

	public int geefWaarde() 
	{
		String s = waardeText;
		int w;
		try 
		{
			w = Integer.parseInt(s);
			zetBekend(true);
			zetWaarde(w);
		} 
		catch (NumberFormatException ex) 
		{}
		return waarde;
	}
	
	public void paint()
	{
		owner.paint();
	}

	public void zetWaarde(int t) 
	{
		zetBekend(true);
		waarde = t;
		waardeText = Integer.toString(waarde);
		paint();
	}

	public void zetBekend(boolean b) 
	{
		bekend = b;
		
		if (!b)
			waardeText = "";
		if (!b)
			waarde = -999;
	}

	public void zetLeeg(boolean b)
	{
		leeg = b;
	}

	public void zetAlsTemp(boolean b)
	{
		isTemp = b;
	}

	public boolean isBekend()
	{
		return bekend;
	}

	public boolean isInstelbaar()
	{
		return instelbaar;
	}

	public void zetInstelbaar(boolean b) 
	{
		instelbaar = b;
	}

	public void verhoog() 
	{
		waarde++;
		waardeText = Integer.toString(waarde);
		paint();
	}

	public void verlaag() 
	{
		waarde--;
		waardeText = Integer.toString(waarde);
		paint();
	}

	public void verhoog(int d) 
	{
		waarde += d;
		waardeText = Integer.toString(waarde);
		paint();
	}

	public void verlaag(int d) 
	{
		waarde -= d;
		waardeText = Integer.toString(waarde);
		paint();
	}

	public void vulIn() 
	{
		if (instelbaar) 
		{
		}
	}

	public void mouseUpTouchEndAction() 
	{	vulIn();
	}

	public void actionPerformed() 
	{
		String s = waardeText;
		int w;
		try 
		{
			w = Integer.parseInt(s);
			zetBekend(true);
			zetWaarde(w);
		}
		catch (NumberFormatException ex) 
		{
			waardeText = "";
			zetBekend(false);
			waarde = -999;
			paint();
		}
		paint();
	}
}
