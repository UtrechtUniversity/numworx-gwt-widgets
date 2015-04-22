package fi.tekenveelvlakgwt.client;

//import java.awt.*;
//import java.awt.event.*;

//import javax.swing.ImageIcon;
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JPanel;

import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Image;

class VaktekPanel extends LayoutPanel
{	
	TekenVeelvlakGWT tvGWT;
	
	int breedte, hoogte;
	int vakBreedte;
	Viewer3d va, ba, ra, la;
	
	boolean vlakkenKleurenOptie = false;
    
    boolean docentModus = false;
    
	PushButton kijkNaButton;
	LayoutPanel kijkNaPanel;
	Label kijkNaLabelGoed;
	Label kijkNaLabelFout;

	
	public VaktekPanel(int x, int y, int b, int h, TekenVeelvlakGWT tvGWT)
	{	
		this(new Veelvlak(),new Veelvlak(),new Veelvlak(),new Veelvlak(),x,y,b,h,tvGWT);
		
	}	
	
	public VaktekPanel(Veelvlak vva, Veelvlak vra, Veelvlak vla ,Veelvlak  vba, int x, int y, int b, int h,
					   TekenVeelvlakGWT tvGWT)
	{	
		this.tvGWT = tvGWT;
		
		breedte = b;
		hoogte = h;
		vakBreedte = Math.min((breedte-6)/3, (hoogte-6)/2);
				
		la = new Viewer3d(vla, breedte/2-3*vakBreedte/2+1, hoogte/2+1, vakBreedte-1, vakBreedte-1,tvGWT);
		la.vaktek = this;
		la.k = 230;
		la.zetAfstand(10000000);
		la.zetSchaduw(false);
		la.zetBeginHoeken(0,90);
		la.zetMuisAan(false);
		la.border = true;
		add(la.canvas);
		setWidgetLeftWidth(la.canvas,breedte/2-3*vakBreedte/2+1,Style.Unit.PX,vakBreedte-1,Style.Unit.PX);
		setWidgetTopHeight(la.canvas,hoogte/2+1,Style.Unit.PX,vakBreedte-1,Style.Unit.PX);

		ba = new Viewer3d(vba, breedte/2-vakBreedte/2+1, hoogte/2-vakBreedte+1, vakBreedte-1, vakBreedte-1,tvGWT);
		ba.vaktek = this;
		ba.k = 230;
		ba.zetAfstand(10000000);
		ba.zetSchaduw(false);
		ba.zetBeginHoeken(90,0);
		ba.zetMuisAan(false);
		ba.border = true;
		add(ba.canvas);
		setWidgetLeftWidth(ba.canvas,breedte/2-vakBreedte/2+1,Style.Unit.PX,vakBreedte-1,Style.Unit.PX);
		setWidgetTopHeight(ba.canvas,hoogte/2-vakBreedte+1,Style.Unit.PX,vakBreedte-1,Style.Unit.PX);
		
		
		va = new Viewer3d(vva, breedte/2-vakBreedte/2+1, hoogte/2+1, vakBreedte-1, vakBreedte-1,tvGWT);
		va.vaktek = this;
		va.k = 230;
		va.zetAfstand(10000000);
		va.zetSchaduw(false);
		va.zetBeginHoeken(0,0);
		va.zetMuisAan(false);
		va.border = true;
		add(va.canvas);
		setWidgetLeftWidth(va.canvas,breedte/2-vakBreedte/2+1,Style.Unit.PX,vakBreedte-1,Style.Unit.PX);
		setWidgetTopHeight(va.canvas,hoogte/2+1,Style.Unit.PX,vakBreedte-1,Style.Unit.PX);
		
		
		ra = new Viewer3d(vra, breedte/2-vakBreedte/2+vakBreedte+1, hoogte/2+1, vakBreedte-1, vakBreedte-1,tvGWT);
		ra.vaktek = this;
		ra.k = 230;
		ra.zetAfstand(10000000);
		ra.zetSchaduw(false);
		ra.zetBeginHoeken(0,-90);
		ra.zetMuisAan(false);
		ra.border = true;
		add(ra.canvas);
		setWidgetLeftWidth(ra.canvas,breedte/2-vakBreedte/2+vakBreedte+1,Style.Unit.PX,vakBreedte-1,Style.Unit.PX);
		setWidgetTopHeight(ra.canvas,hoogte/2+1,Style.Unit.PX,vakBreedte-1,Style.Unit.PX);

		kijkNaButton = new PushButton("Kijk Na");
		kijkNaLabelGoed = new Label("V");
		kijkNaLabelFout = new Label("X");
		kijkNaPanel = new LayoutPanel();

		kijkNaLabelGoed.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.labelgoed());
		kijkNaLabelFout.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.labelfout());
		
		add(kijkNaPanel);
		setWidgetLeftWidth(kijkNaPanel, (breedte - 120)/2, Style.Unit.PX, 120, Style.Unit.PX);
		setWidgetTopHeight(kijkNaPanel, hoogte - 25, Style.Unit.PX, 25, Style.Unit.PX);

		kijkNaPanel.add(kijkNaButton);
		kijkNaPanel.setWidgetLeftWidth(kijkNaButton, 0, Style.Unit.PX, 80, Style.Unit.PX);
		kijkNaPanel.setWidgetTopHeight(kijkNaButton, 0, Style.Unit.PX, 25, Style.Unit.PX);
		
		kijkNaPanel.add(kijkNaLabelGoed);
		kijkNaPanel.setWidgetLeftWidth(kijkNaLabelGoed, 80, Style.Unit.PX, 30, Style.Unit.PX);
		kijkNaPanel.setWidgetTopHeight(kijkNaLabelGoed, 5, Style.Unit.PX, 20, Style.Unit.PX);
		
		kijkNaPanel.add(kijkNaLabelFout);
		kijkNaPanel.setWidgetLeftWidth(kijkNaLabelFout, 80, Style.Unit.PX, 30, Style.Unit.PX);
		kijkNaPanel.setWidgetTopHeight(kijkNaLabelFout, 5, Style.Unit.PX, 20, Style.Unit.PX);
		
		//kijkNaPanel.add(tvGWT.goedKrulImage);
		//kijkNaPanel.setWidgetLeftWidth(tvGWT.goedKrulImage, 80, Style.Unit.PX, 30, Style.Unit.PX);
		//kijkNaPanel.setWidgetTopHeight(tvGWT.goedKrulImage, 0, Style.Unit.PX, 25, Style.Unit.PX);
		
		//kijkNaPanel.add(tvGWT.foutKruisImage);
		//kijkNaPanel.setWidgetLeftWidth(tvGWT.foutKruisImage, 80, Style.Unit.PX, 30, Style.Unit.PX);
		//kijkNaPanel.setWidgetTopHeight(tvGWT.foutKruisImage, 0, Style.Unit.PX, 25, Style.Unit.PX);

		kijkNaButton.addClickHandler(new PushClickHandler());
		kijkNaButton.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		//kijkNaPanel.setWidgetVisible(tvGWT.goedKrulImage, true);
		//kijkNaPanel.setWidgetVisible(tvGWT.foutKruisImage, false);

		kijkNaPanel.setWidgetVisible(kijkNaLabelGoed, false);
		kijkNaPanel.setWidgetVisible(kijkNaLabelFout, false);

		setWidgetVisible(kijkNaPanel, false);
		
	}	
	
	public void initContext2d()
	{	va.initContext2d();
		ra.initContext2d();
		la.initContext2d();
		ba.initContext2d();
	
	}
	
	public void setState(Map map)
	{
		va.setState(map);
		ra.setState(map);
		la.setState(map);
		ba.setState(map);
	}

	public void paint()
	{	va.paint();
		ra.paint();
		la.paint();
		ba.paint();
	
	}

	public void zetDocentModus(boolean b)
	{	docentModus = b;
		va.zetDocentModus(b);
		ra.zetDocentModus(b);
		la.zetDocentModus(b);
		ba.zetDocentModus(b);
		
	}
	public void zetVlakkenKleurenOptie(boolean b)
    {	vlakkenKleurenOptie = b;
    	va.zetVlakkenKleurenOptie(b);
    	ra.zetVlakkenKleurenOptie(b);
    	la.zetVlakkenKleurenOptie(b);
    	ba.zetVlakkenKleurenOptie(b);
    }

    public void resetColors()
    {
    	va.resetColors();
    	ra.resetColors();
    	la.resetColors();
    	ba.resetColors();
    }
    
    public void resetLeerlingColors()
    {
    	va.resetLeerlingColors();
    	ra.resetLeerlingColors();
    	la.resetLeerlingColors();
    	ba.resetLeerlingColors();
    }

    public String[] getKleuren()
    {
    	String[] vaKleuren = getVaKleuren();
    	String[] raKleuren = getRaKleuren();
    	String[] laKleuren = getLaKleuren();
    	String[] baKleuren = getVaKleuren();
    	String[] result = new String[vaKleuren.length];
    	for (int cCnt = 0; cCnt < vaKleuren.length; cCnt++)
    	{	result[cCnt] = vaKleuren[cCnt];
    		if (raKleuren[cCnt].equals("rood"))
    			result[cCnt] = "rood";
    		if (laKleuren[cCnt].equals("rood"))
    			result[cCnt] = "rood";
    		if (baKleuren[cCnt].equals("rood"))
    			result[cCnt] = "rood";
    		
    	}
    	
    	return result;
    }
    
    public String[] getVaKleuren()
    {  	return va.getKleuren();
    }
    public String[] getRaKleuren()
    {  	return ra.getKleuren();
    }
    public String[] getLaKleuren()
    {  	return la.getKleuren();
    }
    public String[] getBaKleuren()
    {  	return ba.getKleuren();
    }

    public void zetVaktekKleuren(String[] kleuren)
    {  	va.zetKleuren(kleuren);
    	ra.zetKleuren(kleuren);
    	la.zetKleuren(kleuren);
    	ba.zetKleuren(kleuren);
    }

    public void setVaktekKleuren(String[] kleuren)
    {  	setVaKleuren(kleuren);
    	setRaKleuren(kleuren);
    	setLaKleuren(kleuren);
    	setBaKleuren(kleuren);
    }
    
    public void setVaKleuren(String[] kleuren)
    {  	va.setKleuren(kleuren);
    }
    public void setRaKleuren(String[] kleuren)
    {  	ra.setKleuren(kleuren);
    }
    public void setLaKleuren(String[] kleuren)
    {  	la.setKleuren(kleuren);
    }
    public void setBaKleuren(String[] kleuren)
    {  	ba.setKleuren(kleuren);
    }
 
    public void synchronizeViewerKleuren(Viewer3d v3d)
    {	
    	
System.out.println("synchronize");

    	String[] result = null;
    	if (v3d == va)
    	{	result = getVaKleuren();
    	}
    	else if (v3d == ra)
    	{	result = getRaKleuren();
    	}
    	else if (v3d == la)
    	{	result = getLaKleuren();
    	}
    	else if (v3d == ba)
    	{	result = getBaKleuren();
    	}
    	
       	String[] vaKleuren = getVaKleuren();
    	String[] raKleuren = getRaKleuren();
    	String[] laKleuren = getLaKleuren();
    	String[] baKleuren = getBaKleuren();
    	
    	for (int cCnt = 0; cCnt < result.length; cCnt++)
    	{	vaKleuren[cCnt] = result[cCnt];
    		raKleuren[cCnt] = result[cCnt];
    		laKleuren[cCnt] = result[cCnt];
    		baKleuren[cCnt] = result[cCnt];
    	}
    	
    	va.zetKleuren(result);
    	ra.zetKleuren(result);
    	la.zetKleuren(result);
    	ba.zetKleuren(result);
    	
    	//paint();
    	va.tekenOpnieuw();
    	ra.tekenOpnieuw();
    	la.tekenOpnieuw();
    	ba.tekenOpnieuw();
    }
    
    public void updateViewerKleuren()
    {	
    	
       	String[] vaKleuren = getVaKleuren();
    	String[] raKleuren = getRaKleuren();
    	String[] laKleuren = getLaKleuren();
    	String[] baKleuren = getBaKleuren();
    	String[] result = new String[vaKleuren.length];
    	for (int cCnt = 0; cCnt < vaKleuren.length; cCnt++)
    	{	result[cCnt] = vaKleuren[cCnt];
    		if (raKleuren[cCnt].equals("rood"))
    			result[cCnt] = "rood";
    		if (laKleuren[cCnt].equals("rood"))
    			result[cCnt] = "rood";
    		if (baKleuren[cCnt].equals("rood"))
    			result[cCnt] = "rood";
    		
    	}
    	
    	//tvGWT.viewerKleuren = result;
    	
    }
    
    public boolean evalueer(String[] nakijkKleuren)
    {
		String[] viewerKleuren = getKleuren();
		boolean result = true;
		for (int i = 0; i < viewerKleuren.length; i++)
		{	
			result = result && viewerKleuren[i].equals(nakijkKleuren[i]);
		}

		return result;
    	
    }
    
	public void zetAchtergrond(CssColor c)
	{	//setBackground(c);
		va.zetAchtergrond(c);
		ra.zetAchtergrond(c);
		la.zetAchtergrond(c);
		ba.zetAchtergrond(c);
	}
	public void zetKlikAan(boolean b)
	{	va.zetKlikAan(b);
		ra.zetKlikAan(b);
		la.zetKlikAan(b);
		ba.zetKlikAan(b);
	}
	public void zetVeelvlak(Veelvlak vva, Veelvlak vra, Veelvlak vla ,Veelvlak vba)
	{	la.zetVeelvlak(vla);	
		ba.zetVeelvlak(vba);	
		va.zetVeelvlak(vva);	
		ra.zetVeelvlak(vra);	
	}
						
    class PushClickHandler implements ClickHandler
    {
    	//public void onMouseDown(MouseDownEvent e)
    	public void onClick(ClickEvent e)
    	{
    		
    		//if (touchStart)
    		//	return;
	    		
			//e.preventDefault();
			e.stopPropagation();
	    		
    		if (e.getSource() == kijkNaButton)
    		{
    			tvGWT.kijkNa();
    			if (tvGWT.correct)
    			{
    				//kijkNaPanel.setWidgetVisible(tvGWT.goedKrulImage,true);
    				//kijkNaPanel.setWidgetVisible(tvGWT.foutKruisImage,false);
    				
    				kijkNaPanel.setWidgetVisible(kijkNaLabelGoed,true);
    				kijkNaPanel.setWidgetVisible(kijkNaLabelFout,false);

    			}
    			
    			else
    			{
    				//kijkNaPanel.setWidgetVisible(tvGWT.goedKrulImage,false);
    				//kijkNaPanel.setWidgetVisible(tvGWT.foutKruisImage,true);

    				kijkNaPanel.setWidgetVisible(kijkNaLabelGoed,false);
    				kijkNaPanel.setWidgetVisible(kijkNaLabelFout,true);

    				
    			}

    		}

    	}	
    }
/*    
	class VaktekRooster extends Component
	{	
		public VaktekRooster()
		{	setBounds(0,0,breedte,hoogte);
		}
		public void paint(Graphics g)
		{	g.drawRect(breedte/2-3*vakBreedte/2, hoogte/2, vakBreedte, vakBreedte);
			g.drawRect(breedte/2-vakBreedte/2, hoogte/2-vakBreedte, vakBreedte, vakBreedte);
			g.drawRect(breedte/2-vakBreedte/2, hoogte/2, vakBreedte, vakBreedte);
			g.drawRect(breedte/2-vakBreedte/2+vakBreedte, hoogte/2, vakBreedte, vakBreedte);
		}
	}
*/	
}
