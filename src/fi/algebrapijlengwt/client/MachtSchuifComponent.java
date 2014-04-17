package fi.algebrapijlengwt.client;

//import java.awt.Polygon;
//import java.awt.*;
//import java.awt.event.*;
import fi.algebrapijlengwt.client.expressies_ap.*;

//import javax.swing.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class MachtSchuifComponent extends BewerkingSchuifComponent 
{	
	//int fontSize = 14;
	
	public MachtSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(asv,x,y,b,h);
		
		beginw = new BasisExpressie("2");
		
//GWT
/*		
		tf = new JTextField("2");
		if(!links)tf.setBounds(30,1,16,15);
		else tf.setBounds(20,1,16,15);
		tf.addActionListener(this);
		tf.addFocusListener(this);
		tf.setVisible(false);
		tf.setEnabled(false);
*/		
	}
	
	public void zetLinks(boolean b)
	{	links = b;
		if(!links)
		{	
//GWT			
			//tf.setBounds(30,1,16,15);
		
		}
		else 
		{	
//GWT			
			//tf.setBounds(20,1,16,15);
		
		}
		for(int i=0 ; i<aantalPu ; i++)
		{	pijlUit[i].zetLinks(b);
			if(!links)
			{	//pijlUit[i].zetPlaats(getLocation().x + getSize().width+9 ,getLocation().y + 10 );
				pijlUit[i].zetPlaats(xPos + breedte+9 ,yPos + 10 );
			}
			else 
			{	//pijlUit[i].zetPlaats(getLocation().x - 10 ,getLocation().y + 10 );
				pijlUit[i].zetPlaats(xPos - 10 ,yPos + 10 );
			
			}
		}
		
		
		
		if(!links)
		{	
//GWT			
			//plusMinKnop.setLocation(getSize().width-12,1);
		}
		else
		{	
//GWT			
			//plusMinKnop.setLocation(getSize().width-22,1);
		}
		//asv.tekenOpnieuw();
	}
	
	//public void paint(Graphics g)
	public void paint(Context2d g)
  	{ 	super.paint(g);
		if (!visible)
  			return; 

		//g.setColor(Color.black);
		g.setFillStyle(CssColor.make(0,0,0));
		
		String s1 = "...";
		//String s2 = Expressie.df.format(beginw.geefWaarde());
		String s2 = UF.format0(beginw.geefWaarde(),3);
		
//		Font f1 = new Font("SansSerrif",Font.PLAIN,14);
//		Font f2 = new Font("SansSerrif",Font.PLAIN,10);
//		g.setFont(f);
		
		String fontString1 = "14px sans-serif";
		String fontString2 = "10px sans-serif";
		
//		g.setFont(fontString);
//		TextMetrics tm = g.measureText(s);
//		int w = (int) Math.round(tm.getWidth());
		
		
		int sccrollCorr = 0;
		if(scrollable)sccrollCorr = 5;
		
		if(!links)
		{	
			//g.setFont(f1);
			g.setFont(fontString1);
			
			//g.drawString(s1,20-sccrollCorr,getSize().height-4);
			g.fillText(s1,xPos + 20-sccrollCorr,yPos + hoogte-4);
			
			//g.setFont(f2);
			g.setFont(fontString2);
//GWT			
//			if (!tf.isVisible())
			{	//g.drawString(s2,35-sccrollCorr,getSize().height-8);
				g.fillText(s2,xPos + 35-sccrollCorr,yPos + hoogte-8);
			}
		}
		else 
		{	//g.setFont(f1);
			g.setFont(fontString1);
			//g.drawString(s1,10-sccrollCorr,getSize().height-4);
			g.fillText(s1,xPos + 10-sccrollCorr,yPos + hoogte-4);
			
			//g.setFont(f2);
			g.setFont(fontString2);
			
//GWT			
//			if (!tf.isVisible())
			{	//g.drawString(s2,25-sccrollCorr,getSize().height-8);
				g.fillText(s2,xPos + 25-sccrollCorr,yPos + hoogte-8);
			}
		}
		
	}
	
	public void zetMaat()
	{	int b = 50;
		int h = 20;
		int corr = 0;
		if(beginw!=null)
		{	b = beginw.breedte;
			if(b > 10)b = b+40;
			else b = 50;
			
		}
		
		//setSize(b,h);
		breedte = b;
		hoogte = h;
		
		if(!links)
		{	
//GWT			
			//tf.setBounds(30,1,b-31,15);
			//plusMinKnop.setLocation(b-12,2);
		}
		else 
		{	
//GWT			
			//tf.setBounds(20,1,b-31,15);
			//plusMinKnop.setLocation(b-22,2);
		}

	}
	
	public Expressie geefUitvoer(int max)
	{	if(AlgebraPijlenGWT.simplify)
		{	Expressie uitv = new Expressie();
			if (pijlIn1 == null)
				return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max-1);
			Expressie e2 = beginw;
			if (e1 == null)
				return null;
			if (e2.geefWaarde().doubleValue() == 0)
				uitv = new BasisExpressie("1");
			else if (e2.geefWaarde().doubleValue() == 1)
				uitv = e1;
			else if ((e1 instanceof Wortel) && (e2.geefWaarde().doubleValue() == 2))
				uitv = e1.kind1;
			else 
				uitv = new Macht(e1, e2);
			return uitv;
		}
		else
		{	Expressie uitv = new Expressie();
			if(pijlIn1==null)return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max-1);
			Expressie e2 = beginw;
			if(e1==null)return null;
			if(e2.geefWaarde().doubleValue()==0)uitv = new BasisExpressie("1");
			else if(e2.geefWaarde().doubleValue()==1)uitv = e1;
			else uitv = new Macht(e1,e2);
			return uitv;
		}
	}
	
	public Expressie geefVerborgenUitvoer(int max)
	{	Expressie uitv = new Expressie();
		if(pijlIn1==null)return null;
		Expressie e1 = pijlIn1.zender.geefVerborgenUitvoer(max-1);
		Expressie e2 = beginw;
		if(e1==null)return null;
		if(e2.geefWaarde().doubleValue()==0)uitv = new BasisExpressie("1");
		else if(e2.geefWaarde().doubleValue()==1)uitv = e1;
		else uitv = new Macht(e1,e2);
		return uitv;
	}
	
	public void zetInvulWaarde()
	{	boolean isGeldigeInvoer=true;
		{	try
			{	Double w = Double.valueOf(tf.getText());
			}
			catch(NumberFormatException ex)
			{	isGeldigeInvoer = false;
				tf.setText(UF.format0(beginw.geefWaarde(),3));
			}
		}
		if(isGeldigeInvoer)
		{	beginw = new BasisExpressie( tf.getText());
			//beginw.zetMaat(fm);
			beginw.zetMaat(fontSize,asv.asvContext2d);
		}
		else
		{	beginw = new BasisExpressie("2");
			//beginw.zetMaat(fm);
			beginw.zetMaat(fontSize,asv.asvContext2d);
		}
		zetMaat();
		zetVeranderd(20);
		
		//tf.setEnabled(false);
		tf.setVisible(false);
		inputOwner.remove(tf);
		
		asv.tekenOpnieuw();
	}
}
