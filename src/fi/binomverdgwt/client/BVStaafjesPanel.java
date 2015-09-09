package fi.binomverdgwt.client;

//import java.awt.Color;
//import java.awt.Font;
//import java.awt.FontMetrics;
//import java.awt.Graphics;
//import java.awt.Rectangle;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;

//import javax.swing.JPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

import fi.binomverdgwt.client.Slider.MouseHandler;
import fi.binomverdgwt.client.Slider.TouchHandler;


/**
 * Dit is een panel dat een grafische weergave van een binomiale verdeling geeft
 * door middel van een staafdiagram.
 */
public class BVStaafjesPanel //extends JPanel implements ActionListener 
{
	
	private BinomVerdPanel interactiePanel;
	private double staafBreedte;
	private double multiplier; //vermenigvuldigingsfactor tov de x-as;
	
	private static final double VULHOOGTE = 0.8; //welk deel van de hoogte van het staafjespanel het grootste staafje inneemt
	
	private int sliderOffset = 5;
	
	public static final int XASBALKHEIGHT = 25;
	public static final int YASBALKWIDTH = 35;
	public static final int MAX_STREEPJES = 10;
	public static final int YAS_INKORTEN = 10;

	private int grensLinks;
	private int grensRechts;
	
	private boolean tweeGrenzen; //true = twee grenzen, false = 1 grens
	private GrenzenOptie grenzenOptie;
	
	private boolean showXAs;
	private boolean showYAs;
	private boolean showGrensSlider;
	
	private Slider successenSlider;
	private DoubleSlider successenDoubleSlider;

	private int[] toegestaneSchaalverdelingen = {1, 2, 5, 10, 20, 50, 100, 200, 500, 1000};
	
	//private Font font;
	//private FontMetrics fontMetrics;
	String fontString = "12px sans-serif";
	TextMetrics tm;
	
	private Rectangle lastBounds; //laatst gezette bounds, om een resize te kunnen merken
	
	Canvas binomVerdGWTCanvas;
	Context2d binomVerdGWTContext2d;
	
	int breedte, hoogte;
	
	boolean mouseDown;
	
	/**
	 * Constructor
	 * @param interactiePanel Het BVInteractiePanel dat de gegevens geeft voor dit BVStaafjesPanel
	 */
	public BVStaafjesPanel(BinomVerdPanel interactiePanel, GrenzenOptie grenzenOptie, int b, int h) 
	{
		//this.lastBounds = new Rectangle(0,0,0,0);
		//this.setLayout(null);
		this.interactiePanel = interactiePanel;
	
		breedte = b;
		hoogte = h;
		
		binomVerdGWTCanvas = Canvas.createIfSupported();
		
		binomVerdGWTCanvas.setWidth(breedte + "px");
		binomVerdGWTCanvas.setHeight(hoogte + "px");
		binomVerdGWTCanvas.setCoordinateSpaceWidth(breedte);
		binomVerdGWTCanvas.setCoordinateSpaceHeight(hoogte);
		
		MouseHandler mouseHandler = new MouseHandler();
		binomVerdGWTCanvas.addMouseDownHandler(mouseHandler);
		binomVerdGWTCanvas.addMouseMoveHandler(mouseHandler);
		binomVerdGWTCanvas.addMouseUpHandler(mouseHandler);
		
		TouchHandler touchHandler = new TouchHandler();
		binomVerdGWTCanvas.addTouchStartHandler(touchHandler);
		binomVerdGWTCanvas.addTouchMoveHandler(touchHandler);
		binomVerdGWTCanvas.addTouchEndHandler(touchHandler);
		
		binomVerdGWTContext2d = binomVerdGWTCanvas.getContext2d(); 
		
		//this.font = new Font("Dialog", Font.PLAIN, 12);
		//this.fontMetrics = getFontMetrics(this.font);
		
		this.showXAs = true;
		this.showYAs = true;
		this.grensLinks = 5;
		this.grensRechts = 10;
		
		this.tweeGrenzen = false;
		this.grenzenOptie = grenzenOptie;
// tijdelijk	
		this.showGrensSlider = true;
		
//GWT (2)		
		//maak de sliders aan, maar doe er verder nog niets mee
		this.successenDoubleSlider = new DoubleSlider(interactiePanel, 100, 40, 80, 0, 0, binomVerdGWTContext2d);
		this.successenDoubleSlider.zetLinksEnabled(false);
		this.successenDoubleSlider.zetRechtsEnabled(false);
		//this.successenDoubleSlider.zetShowLine(false);
		//this.successenSlider = new Slider(100,40);
		this.successenSlider = new Slider(interactiePanel, 100, 40, 0, 0, binomVerdGWTContext2d, "grens");
		this.successenSlider.zetEnabled(false);
		//this.successenSlider.zetShowLine(false);
		
//GWT (2)		
		//this.add(this.successenDoubleSlider);
		//this.add(this.successenSlider);
		
//GWT (2)		
		//this.successenDoubleSlider.addActionListener(this);
		//this.successenSlider.addActionListener(this);
		
		this.multiplier = 1.0;

		this.addRightSlider();
		this.updateSliderBounds();
		this.updateSuccessenSliderPosition();
	}
	
	/**
	 * Geef anderen de mogelijkheid om de events van de successensliders te volgen. Wordt gebruikt in EditPanel.
	 */
	
//GWT??
/*	
	public void addSuccessenSliderListener(ActionListener al) {
		this.successenSlider.addActionListener(al);
		this.successenDoubleSlider.addActionListener(al);
	}
*/	
	/**
	 * Bereken de breedte van de staafjes aan de hand van het aantal staafjes en 
	 * de breedte van het panel
	 */
	public void berekenStaafBreedte() 
	{
		//hou rekening met ruimte om de y-as in te tekenen
		int asRuimte = 0;
		if(this.showYAs) 
		{
			asRuimte = BVStaafjesPanel.YASBALKWIDTH;
		}
		
		//bereken staafbreedte
		//this.staafBreedte = (double)(this.getWidth()-asRuimte)/(double)(this.interactiePanel.getN()+1);
		this.staafBreedte = (double)(breedte-asRuimte)/(double)(this.interactiePanel.getN()+1);
		
	}
	
//GWT

	public void bepaalGrenzenMetSlider() 
	{
		
		
//System.out.println("bepaal grenzen met sliders");
		if (this.showGrensSlider) 
		{
			if(this.tweeGrenzen) 
			{
			   	this.grensRechts = (int)((double)(this.successenDoubleSlider.geefStandRechts())/this.staafBreedte);
				this.grensLinks = (int)((double)(this.successenDoubleSlider.geefStandLinks())/this.staafBreedte);
	        }
	        else 
	        {
	        	this.grensRechts = (int)((double)(this.successenSlider.geefStand())/this.staafBreedte);
	        }
		
		}
		
	}
	
	/**
	 * @return De modus van de binomiale verdeling
	 */
	private int getModeBV() 
	{
		if(this.interactiePanel.getP() == 1.0) 
		{
			return this.interactiePanel.getN();
		}
		else 
		{
			return (int)((this.interactiePanel.getN()+1)*this.interactiePanel.getP());
		}
	}
	
	private int getModeHyp() 
	{
		if(this.interactiePanel.getM() >= this.interactiePanel.getPopulatie()) 
		{
			return this.interactiePanel.getN();
		}
		else 
		{
			return (int)((this.interactiePanel.getN() + 1) * (this.interactiePanel.getM() + 1)/(this.interactiePanel.getPopulatie()+2));
		}
	}
	
	/**
	 * Bepaal hoeveel alle staafjes vergroot moeten worden om het scherm goed te vullen
	 */
	private void berekenMultiplier() 
	{
		int modus;
		double maxHoogte;
		if(!this.interactiePanel.getHypergeometrisch()) 
		{
			modus = this.getModeBV();
			maxHoogte = this.interactiePanel.berekenKansK(modus);
		}
		else 
		{
			modus = this.getModeHyp();
			maxHoogte = this.interactiePanel.berekenHyperKansK(modus);
		}
		
		this.multiplier = 1/(maxHoogte/BVStaafjesPanel.VULHOOGTE);
	}
	public void setTweeGrenzen(boolean tweeGrenzen) 
	{
		this.tweeGrenzen = tweeGrenzen;
		this.updateSliderBounds();
		this.addRightSlider();
		this.updateSuccessenSliderPosition();
	}
	public boolean getTweeGrenzen() 
	{
		return this.tweeGrenzen;
	}
	public void setGrenzenOptie(GrenzenOptie grenzenOptie) 
	{
		this.grenzenOptie = grenzenOptie;
		this.paint();
	}
	public GrenzenOptie getGrenzenOptie() 
	{
		return this.grenzenOptie;
	}
	public boolean getShowXAs() 
	{
		return this.showXAs;
	}
	public void setShowXAs(boolean b) 
	{
		this.showXAs = b;
		this.updateSliderBounds();
		this.paint();
	}
	public boolean getShowYAs() {
		return this.showYAs;
	}
	public void setShowYAs(boolean b) 
	{
		this.showYAs = b;
		this.updateSliderBounds();
		this.paint();
	}
	public void setShowGrensSlider(boolean b) 
	{
		this.showGrensSlider = b;
		this.addRightSlider();
		this.paint();
	}
	public boolean getShowGrensSlider() 
	{
		return this.showGrensSlider;
	}
	public int getGrensLinks() 
	{
		return this.grensLinks;
	}
		
	public int getGrensRechts() 
	{
		return this.grensRechts;
	}
	
	public void setGrensLinks(int grensLinks) 
	{
		this.grensLinks = grensLinks;
		this.updateSuccessenSliderPosition();
	}
	
	public void setGrensRechts(int grensRechts) 
	{
		this.grensRechts = grensRechts;
		this.updateSuccessenSliderPosition();
	}
	
	/**
	 * zet de locatie en lengte voor de slider
	 */
	private void updateSliderBounds() 
	{
		this.berekenStaafBreedte(); //update de staafbreedte
		
		int x;
		int y;
		int lengte;
		
		x = BVStaafjesPanel.YASBALKWIDTH-6;			
		y = hoogte-BVStaafjesPanel.XASBALKHEIGHT-5;
		lengte = breedte-BVStaafjesPanel.YASBALKWIDTH;

		if (!this.showXAs) 
		{
			y = hoogte - 8;
		}
		if (!this.showYAs) {
			lengte += BVStaafjesPanel.YASBALKWIDTH;
			x = -6;
		}

		
		this.successenDoubleSlider.zetLengte(lengte);
		this.successenDoubleSlider.setLocation(x, y);
		this.successenDoubleSlider.zetStandRechts((int)((this.grensRechts)*this.staafBreedte) - this.successenDoubleSlider.getMinimumLinks());
		this.successenSlider.zetLengte(lengte);
		this.successenSlider.setLocation(x, y);
		
	}
	
	/**
	 * Zorgt dat de juiste slider aan het panel toegevoegd is.
	 */
	private void addRightSlider() 
	{
		
		this.berekenStaafBreedte();
		if (this.showGrensSlider) 
		{
			if (this.tweeGrenzen) 
			{
				this.successenSlider.zetEnabled(false);
				this.successenDoubleSlider.zetStandRechts(this.successenSlider.geefStand());				
				this.successenDoubleSlider.zetLinksEnabled(true);
				this.successenDoubleSlider.zetRechtsEnabled(true);

			}

			else 
			{
			
				this.successenDoubleSlider.zetLinksEnabled(false);
				this.successenDoubleSlider.zetRechtsEnabled(false);
				this.successenSlider.zetStand(this.successenDoubleSlider.geefStandRechts());
				this.successenSlider.zetEnabled(true);
			}
		}
		else 
		{
			this.successenDoubleSlider.zetLinksEnabled(false);
			this.successenDoubleSlider.zetRechtsEnabled(false);
			this.successenSlider.zetEnabled(false);
		}
		
	}
	
	/**
	 * Paint de y-as met schaal 
	 * @param g Het Graphics object waarin de as getekend gaat worden
	 * @param multiplier De factor waarmee de staafjes vermenigvuldigd zijn, en de schaal dus ook vermenigvuldigd moet worden
	 */
	//private void paintYAs(Graphics g, double multiplier)
	private void paintYAs(Context2d g, double multiplier)
	{
		if (this.showYAs) 
		{
			//g.setColor(Color.BLACK);
			g.setStrokeStyle(CssColor.make(0,0,0));
			
			g.setLineWidth(0.8d);
			
			//bepaal de lengte van de as
			int asHoogte = hoogte - BVStaafjesPanel.YAS_INKORTEN;
			if (this.showXAs) 
			{
				asHoogte -= BVStaafjesPanel.XASBALKHEIGHT;
			}

			//teken de as zelf
			//g.drawLine(BVStaafjesPanel.YASBALKWIDTH-1, BVStaafjesPanel.YAS_INKORTEN, 
			//		   BVStaafjesPanel.YASBALKWIDTH-1, BVStaafjesPanel.YAS_INKORTEN+asHoogte);
			g.beginPath();
			g.moveTo(BVStaafjesPanel.YASBALKWIDTH-1, BVStaafjesPanel.YAS_INKORTEN);
			g.lineTo(BVStaafjesPanel.YASBALKWIDTH-1, BVStaafjesPanel.YAS_INKORTEN+asHoogte);
			g.stroke();
			
			
			
			//teken de streepjes op de as
			for(int i = 1; i <= 10; i++) 
			{
				//g.drawLine(BVStaafjesPanel.YASBALKWIDTH-6, asHoogte - (int)(i*(double)asHoogte/(double)10)+BVStaafjesPanel.YAS_INKORTEN, 
				//		   BVStaafjesPanel.YASBALKWIDTH-2, asHoogte - (int)(i*(double)asHoogte/(double)10)+BVStaafjesPanel.YAS_INKORTEN);
				g.beginPath();
				g.moveTo(BVStaafjesPanel.YASBALKWIDTH-6, asHoogte - (int)(i*(double)asHoogte/(double)10)+BVStaafjesPanel.YAS_INKORTEN);
				g.lineTo(BVStaafjesPanel.YASBALKWIDTH-2, asHoogte - (int)(i*(double)asHoogte/(double)10)+BVStaafjesPanel.YAS_INKORTEN);
				g.stroke();

			}
			
			g.setLineWidth(1.0d);
			
			//teken de tekst bij de streepjes op de as
			//g.setColor(Color.BLACK);
			g.setFillStyle(CssColor.make(0,0,0));
			for(int i = 1; i <= 10; i++) 
			{
				int j = (int)(i*10.0*asHoogte/(asHoogte+BVStaafjesPanel.YAS_INKORTEN)/this.multiplier);
				//g.drawString(Double.toString((double)j/100.0), 1, 
				//            asHoogte + BVStaafjesPanel.YAS_INKORTEN - 
				//             (int)(1.0*i*(double)asHoogte/10.0 - (1.0 * this.fontMetrics.getHeight() / 2.0))-2);
//GWT 20
				g.fillText(Double.toString((double)j/100.0), 1, 
			               asHoogte + BVStaafjesPanel.YAS_INKORTEN - 
			               (int)(1.0*i*(double)asHoogte/10.0 - (1.0 * 20 / 2.0))-2);
				
			}
		}
	}
	
	/**
	 * Paint de x-as met schaal 
	 * @param g Het Graphics object waarin de as getekend gaat worden
	 */
	//private void paintXAs(Graphics g)
	private void paintXAs(Context2d g)
	{
		if (this.showXAs) 
		{
			//g.setColor(Color.BLACK);
			g.setStrokeStyle(CssColor.make(0,0,0));
			
			g.setLineWidth(0.8d);
			
			//teken de as zelf
			//als de slider niet getekend wordt, teken dan een lijn
			if (!this.showGrensSlider) 
			{
				//g.setColor(Color.BLACK);
				g.setFillStyle(CssColor.make(0,0,0));
				if (this.showYAs)
				{
					// een rectangle van hoogte 1 tekent niet lekker
					//g.fillRect(BVStaafjesPanel.YASBALKWIDTH-1, hoogte-BVStaafjesPanel.XASBALKHEIGHT, 
					//		   breedte- BVStaafjesPanel.YASBALKWIDTH +1, 1);
					g.beginPath();
					g.moveTo(BVStaafjesPanel.YASBALKWIDTH-1, hoogte-BVStaafjesPanel.XASBALKHEIGHT);
					g.lineTo(BVStaafjesPanel.YASBALKWIDTH-1 + breedte- BVStaafjesPanel.YASBALKWIDTH +1, 
							 hoogte-BVStaafjesPanel.XASBALKHEIGHT);
					g.stroke();
				}
				else 
				{
					//een rectangle van hoogte 1 tekent niet lekker
					//g.fillRect(0, hoogte-BVStaafjesPanel.XASBALKHEIGHT, breedte, 1);
					g.beginPath();
					g.moveTo(0, hoogte-BVStaafjesPanel.XASBALKHEIGHT);
					g.lineTo(breedte, hoogte-BVStaafjesPanel.XASBALKHEIGHT);
					g.stroke();

				}
			}
			
			//bepaal de lengte van de as en waar de as begint
			int asLengte = breedte; //this.getWidth();
			int xOffset = 0;
			if (this.showYAs) {
				asLengte -= BVStaafjesPanel.YASBALKWIDTH;
				xOffset = BVStaafjesPanel.YASBALKWIDTH;
			}
			
			//bepaal per hoeveel staafjes er een streepje en tekst komt
			int index = 0;
			int streepjesFrequentie = this.toegestaneSchaalverdelingen[index];
			while ((index < this.toegestaneSchaalverdelingen.length-1) && (double)(this.interactiePanel.getN()+1)/ (double)(streepjesFrequentie) >= BVStaafjesPanel.MAX_STREEPJES) {
				index++;
				streepjesFrequentie = this.toegestaneSchaalverdelingen[index];
			}
						
			//teken de streepjes op de as
			if (this.interactiePanel.getN() <= 100) 
			{
				for(int i = 0; i < this.interactiePanel.getN()+1; i++) 
				{
					//g.drawLine(xOffset + (int)((double)(i+0.5)*this.staafBreedte), this.getHeight()-BVStaafjesPanel.XASBALKHEIGHT+1, 
					//		     xOffset + (int)((double)(i+0.5)*this.staafBreedte), this.getHeight()-BVStaafjesPanel.XASBALKHEIGHT+2);
					g.beginPath();
					g.moveTo(xOffset + (int)((double)(i+0.5)*this.staafBreedte), hoogte-BVStaafjesPanel.XASBALKHEIGHT+1);
					g.lineTo(xOffset + (int)((double)(i+0.5)*this.staafBreedte), hoogte-BVStaafjesPanel.XASBALKHEIGHT+2);
					g.stroke();
				}
			}
			else 
			{
				for(int i = 0; i < (this.interactiePanel.getN()+1)/5; i++) 
				{
					//g.drawLine(xOffset + (int)((double)(5*i+0.5)*this.staafBreedte), this.getHeight()-BVStaafjesPanel.XASBALKHEIGHT+1, 
					//		   xOffset + (int)((double)(5*i+0.5)*this.staafBreedte), this.getHeight()-BVStaafjesPanel.XASBALKHEIGHT+2);
					g.beginPath();
					g.moveTo(xOffset + (int)((double)(i+0.5)*this.staafBreedte), hoogte-BVStaafjesPanel.XASBALKHEIGHT+1);
					g.lineTo(xOffset + (int)((double)(i+0.5)*this.staafBreedte), hoogte-BVStaafjesPanel.XASBALKHEIGHT+2);
					g.stroke();
					
				}
			}
			for(int i = 0; i < 15; i++) 
			{
				//g.drawLine(xOffset + (int)(((double)i*(double)streepjesFrequentie+0.5)*this.staafBreedte), this.getHeight()-BVStaafjesPanel.XASBALKHEIGHT+1, 
				//		   xOffset + (int)(((double)i*(double)streepjesFrequentie+0.5)*this.staafBreedte), this.getHeight()-BVStaafjesPanel.XASBALKHEIGHT+5);
				g.beginPath();
				g.moveTo(xOffset + (int)(((double)i*(double)streepjesFrequentie+0.5)*this.staafBreedte), hoogte-BVStaafjesPanel.XASBALKHEIGHT+1);
				g.lineTo(xOffset + (int)(((double)i*(double)streepjesFrequentie+0.5)*this.staafBreedte), hoogte-BVStaafjesPanel.XASBALKHEIGHT+5);
				g.stroke();
				
			}
			
			g.setLineWidth(1.0d);
			
			//teken de tekst bij de streepjes
			//g.setColor(Color.BLACK);
			g.setFillStyle(CssColor.make(0,0,0));
			//g.setFont(this.font);
			g.setFont(fontString);
			for(int i = 0; i < 15; i++) 
			{
				String text = Integer.toString(i*streepjesFrequentie);
				TextMetrics tm = g.measureText(text);
				int textWidth = (int) Math.round(tm.getWidth());
				
				int width = textWidth + 
							(xOffset + 
									(int)( ( (double)i*(double)streepjesFrequentie+0.5) * this.staafBreedte) - 
										   (textWidth/2)
						    );
				
				//int width = fontMetrics.stringWidth(text)+(xOffset + 
				//		(int)( ((double)i*(double)streepjesFrequentie+0.5) * this.staafBreedte) - 
				//		      (fontMetrics.stringWidth(text)/2)
				//		      );
				//teken alleen als de tekst helemaal op het paneel past
				//if (!(
				//		(this.fontMetrics.stringWidth(Integer.toString(i*streepjesFrequentie))+
				//		(xOffset + (int)(((double)i*(double)streepjesFrequentie+0.5)*this.staafBreedte)- (this.fontMetrics.stringWidth(Integer.toString(i*streepjesFrequentie))/2))
				//				
				//    )> this.getWidth())) 
				
				//if (!(width > this.getWidth())
				if (!(width > breedte))
				{
					//g.drawString(Integer.toString(i*streepjesFrequentie), xOffset + (int)(((double)i*(double)streepjesFrequentie+0.5)*this.staafBreedte) - (this.fontMetrics.stringWidth(Integer.toString(i*streepjesFrequentie))/2), this.getHeight()-BVStaafjesPanel.XASBALKHEIGHT+18);
					g.fillText(text, 
							   xOffset + (int)(((double)i*(double)streepjesFrequentie+0.5)*this.staafBreedte) - (textWidth/2), 
							   hoogte-BVStaafjesPanel.XASBALKHEIGHT+18);
				}
			}
		}
		else 
		{
			//teken lijn als er geen slider getekend wordt
			if (!this.showGrensSlider) 
			{
				//g.setColor(Color.BLACK);
				g.setStrokeStyle(CssColor.make(0,0,0));
				
				g.setLineWidth(0.8d);
				
				if (this.showYAs) 
				{
					//g.drawLine(BVStaafjesPanel.YASBALKWIDTH, this.getHeight()-3, this.getWidth()-BVStaafjesPanel.YASBALKWIDTH, this.getHeight()-3);
					g.beginPath();
					g.moveTo(BVStaafjesPanel.YASBALKWIDTH, hoogte-3);
					g.lineTo(breedte-BVStaafjesPanel.YASBALKWIDTH, hoogte -3);
					g.stroke();
				}
				else 
				{
					//g.drawLine(0, this.getHeight()-3, this.getWidth(), this.getHeight()-3);
					g.beginPath();
					g.moveTo(0, hoogte-3);
					g.lineTo(breedte, hoogte -3);
					g.stroke();

				}
				
				g.setLineWidth(1.0d);
			}
		}
	}

	private CssColor bepaalStaafKleur(int k) 
	{		
		if ((this.tweeGrenzen && k <= this.grensRechts && k >= this.grensLinks) ||
			(!this.tweeGrenzen && this.grenzenOptie == GrenzenOptie.LINKS && k<=this.grensRechts) ||
			(!this.tweeGrenzen && this.grenzenOptie == GrenzenOptie.GELIJK && k==this.grensRechts) ||
			(!this.tweeGrenzen && this.grenzenOptie == GrenzenOptie.RECHTS && k>=this.grensRechts))	{
			return this.interactiePanel.STAAFJE_TELT;
		}
		else {
			return this.interactiePanel.STAAFJE_TELT_NIET;
		}
	}
	
	/**
	 * Paint een enkel staafje
	 * @param g Het Graphics object waarin het staafje getekend moet worden
	 * @param k Het nummer van het te tekenen staafje
	 */
	//public void paintStaafje(Graphics g, int k, double multiplier)
	public void paintStaafje(Context2d g, int k, double multiplier)
	{		
		//hou indien nodig ruimte vrij om de assen te tekenen
		int xOffset = 0;
		int yOffset = 3;
		if (this.showYAs) 
		{
			xOffset = BVStaafjesPanel.YASBALKWIDTH;
		}
		if(this.showXAs) 
		{
			yOffset = BVStaafjesPanel.XASBALKHEIGHT;
		}
		
		//bepaal grootte
		int x = (int)(k*this.staafBreedte+xOffset);
		int y;
		int height;
		if (!this.interactiePanel.getHypergeometrisch()) 
		{
			y = hoogte - (int)(this.interactiePanel.berekenKansK(k)*multiplier*(hoogte-yOffset)+yOffset);
			height = (int)(this.interactiePanel.berekenKansK(k)*(hoogte-yOffset)*this.multiplier);
		}
		else 
		{
			y = hoogte - (int)(this.interactiePanel.berekenHyperKansK(k)*multiplier*(hoogte-yOffset)+yOffset);
			height = (int)(this.interactiePanel.berekenHyperKansK(k)*(hoogte-yOffset)*this.multiplier);
		}
		int width = (int)((k+1) * this.staafBreedte + xOffset) - (int)(k * this.staafBreedte + xOffset) + 1;
		
		
		if (width > 0 && height > 0) 
		{
			//teken omleining(!! Huub)
			//g.setColor(Color.BLACK);
			g.setStrokeStyle(CssColor.make(0,0,0));
			
			//kleur het staafje in
			//g.setColor(this.bepaalStaafKleur(k));
			g.setFillStyle(this.bepaalStaafKleur(k));
			g.fillRect(x, y, width, height);
			
			g.setLineWidth(0.8d);
			//g.drawRect(x, y, width-1, height-1);
			g.strokeRect(x, y, width -1, height - 1);
			
			g.setLineWidth(1.0d);
		}
	}
	
	public void paint()
	{
		paintComponent(binomVerdGWTContext2d);
	}
	
	/**
	 * Paint gehele component
	 */
	//Override
	//public void paintComponent(Graphics g)
	public void paintComponent(Context2d g)
	{		
        this.berekenStaafBreedte();
        this.berekenMultiplier();
        //g.setColor(Color.white);
        g.setFillStyle(CssColor.make(255,255,255));
        //g.clearRect(0, 0, this.getWidth(), this.getHeight());
        //g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.fillRect(0, 0, breedte, hoogte);
        
        this.paintXAs(g);
        this.paintYAs(g,this.multiplier);
        
		for(int k = 0; k <= this.interactiePanel.getN(); k++) 
		{
			this.paintStaafje(g,k,this.multiplier);
		}
		
		if (showGrensSlider)
		{
			successenSlider.paint();
			successenDoubleSlider.paint();
		}
	}
	
//GWT??	
	//Override
/*	
	public void setBounds(int x, int y, int b, int h) {
		//check of de bounds daadwerkelijk worden veranderd
		Rectangle r = new Rectangle(x,y,b,h);
		if(!r.equals(this.lastBounds)) {
			
			super.setBounds(x,y,b,h);
			this.updateSliderBounds();
			this.successenDoubleSlider.zetStandRechts((int)((this.grensRechts+0.5)*this.staafBreedte) - this.successenDoubleSlider.getMinimumLinks());
			this.successenDoubleSlider.zetStandLinks((int)((this.grensLinks+0.5)*this.staafBreedte) - this.successenDoubleSlider.getMinimumLinks());
			this.successenSlider.zetStand((int)((this.grensRechts+0.5)*this.staafBreedte) - this.successenSlider.getMinimum());
		}
		
		//noteer dat deze bounds zijn gezet
		this.lastBounds = r;
		
	}
*/	
	/**
	 * Zet de slidergrenzen weer midden onder het staafje
	 */
	public void updateSuccessenSliderPosition() 
	{

		
		this.successenDoubleSlider.zetStandRechts((int)((this.grensRechts+0.5)*this.staafBreedte) - this.successenDoubleSlider.getMinimumLinks());
		this.successenDoubleSlider.zetStandLinks((int)((this.grensLinks+0.5)*this.staafBreedte) - this.successenDoubleSlider.getMinimumLinks());
		this.successenSlider.zetStand((int)((this.grensRechts+0.5)*this.staafBreedte) - this.successenSlider.getMinimum());
		
		//paint();
	}

	public void processGrensSlider(boolean finished)
	{
//System.out.println("BVStaaf processGrensSlider");		
		this.bepaalGrenzenMetSlider();
		paint();
		if (finished) 
		{
			this.updateSuccessenSliderPosition();
			paint();
		}
	}
	
	public void processTweeGrenzenSlider(boolean finished)
	{
		this.bepaalGrenzenMetSlider();
		paint();
		if (finished) 
		{
			this.updateSuccessenSliderPosition();
			paint();
		}

	}
//GWT
/*	
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == this.successenSlider) {
			this.bepaalGrenzenMetSlider();
			if(e.getActionCommand().equals("stop")) {
				this.updateSuccessenSliderPosition();
			}
		}
		
		if(e.getSource() == this.successenDoubleSlider) {
			this.bepaalGrenzenMetSlider();
			if(e.getActionCommand().equals("stop")) {
				this.updateSuccessenSliderPosition();
			}
		}
		this.interactiePanel.updateKansBalk();
		this.repaint();
		
	}
*/	
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		
		//public void mousePressed(MouseEvent e)
		public void onMouseDown(MouseDownEvent e)
		{
			e.preventDefault();
			
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true;
			
			if (tweeGrenzen)
				successenDoubleSlider.mouseDownTouchStartAction(eventX, eventY);
			else
				successenSlider.mouseDownTouchStartAction(eventX, eventY);
		}
		
		//public void mouseDragged(MouseEvent e)
		public void onMouseMove(MouseMoveEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
//System.out.println("mouse move veld");			
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();
			
//System.out.println("sp = " + shiftPressed);

			if (tweeGrenzen)
				successenDoubleSlider.mouseMoveTouchMoveAction(eventX, eventY);
			else 
				successenSlider.mouseMoveTouchMoveAction(eventX, eventY);
			
			
		} // onMouseMove
		
		//public void mouseReleased(MouseEvent e)
		public void onMouseUp(MouseUpEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			if (tweeGrenzen)
				successenDoubleSlider.mouseUpTouchEndAction();
			else
				successenSlider.mouseUpTouchEndAction();

		}

	} //MLMML


	// tablet, dwo 
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				
				int eventX = touch.getPageX() - interactiePanel.getAbsoluteLeft();
				int eventY = touch.getPageY() - interactiePanel.getAbsoluteTop();				
				
				if (tweeGrenzen)
					successenDoubleSlider.mouseDownTouchStartAction(eventX, eventY);
				else
					successenSlider.mouseDownTouchStartAction(eventX, eventY);

				
		    }
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - interactiePanel.getAbsoluteLeft();
				int eventY = touch.getPageY() - interactiePanel.getAbsoluteTop();				
			    
				if (tweeGrenzen)
					successenDoubleSlider.mouseMoveTouchMoveAction(eventX, eventY);
				else 
					successenSlider.mouseMoveTouchMoveAction(eventX, eventY);

				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			if (tweeGrenzen)
				successenDoubleSlider.mouseUpTouchEndAction();
			else
				successenSlider.mouseUpTouchEndAction();

		}

	}

}