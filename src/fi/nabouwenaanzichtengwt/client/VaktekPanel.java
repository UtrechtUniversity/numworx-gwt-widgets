package fi.nabouwenaanzichtengwt.client;

import java.awt.*;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;


class VaktekPanel //Container
{	
	int breedte, hoogte;
	int vakBreedte;
	Viewer3d va, ba, ra, la;
	NabouwenAanzichtenGWT eigenaar;
	KubusRooster kr;
	int aantalViews;
	
	final int BOVEN = 0;
	final int VOOR = 1;
	final int RECHTS = 2;
	//final int LINKS = 3;
	int typeAanzicht = BOVEN;
	
	FlowPanel panel = new FlowPanel();
	private Grid grid = null;
	
	
	public VaktekPanel(KubusRooster k, int b, int h, int aantalViews, NabouwenAanzichtenGWT bd)
	{	this.aantalViews = aantalViews;
		
		panel.getElement().getStyle().setWidth(b, Unit.PX);
		panel.getElement().getStyle().setHeight(b, Unit.PX);
		panel.getElement().getStyle().setProperty("textAlign", "center");
		kr = k;
		eigenaar = bd;
		breedte = b;
		hoogte = h;
		
		if (aantalViews == 2)
		{	
			grid = new Grid(1, 2);
			grid.getElement().getStyle().setProperty("textAlign", "center");
			vakBreedte = Math.min((breedte-6)/2, (hoogte-40));
			grid.getElement().getStyle().setMarginLeft(breedte/2-vakBreedte, Unit.PX);
					
			va = new Viewer3d(kr, breedte / 2 - vakBreedte + 1, 1, vakBreedte - 1, vakBreedte - 1, bd);
			va.zetAfstand(10000000);
			va.zetSchaduw(false);
			va.zetBeginHoeken(0,0);
			va.zetMuisAan(false);
			va.zetPijlAan(false);
			va.initContext2d();
			va.draw();
			VerticalPanel panelV = new VerticalPanel();
			FlowPanel labelV = new FlowPanel();
			labelV.getElement().getStyle().setFontSize(14, Unit.PX);
			labelV.getElement().getStyle().setProperty("lineHeight", "1.2");
			labelV.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			labelV.getElement().getStyle().setProperty("textAlign", "center");
			labelV.getElement().setInnerHTML("voor");
			panelV.add(va.getCanvas());
			panelV.add(labelV);
			grid.setWidget(0,0,panelV);
			
			ra = new Viewer3d(kr, breedte/2 + 1, 1, vakBreedte - 1, vakBreedte-1, bd);
			ra.zetAfstand(10000000);
			ra.zetSchaduw(false);
			ra.zetBeginHoeken(0,-90);
			ra.zetMuisAan(false);
			ra.zetPijlAan(false);
			ra.initContext2d();
			ra.draw();
			VerticalPanel panelR = new VerticalPanel();
			FlowPanel labelR = new FlowPanel();
			labelR.getElement().getStyle().setFontSize(14, Unit.PX);
			labelR.getElement().getStyle().setProperty("lineHeight", "1.2");
			labelR.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			labelR.getElement().getStyle().setProperty("textAlign", "center");
			labelR.getElement().setInnerHTML("rechts");
			panelR.add(ra.getCanvas());
			panelR.add(labelR);
			grid.setWidget(0,1,panelR);
			
			panel.add(grid);

		}
		// boven, voor, rechts
		else if (aantalViews == 3)
		{	
			grid = new Grid(2, 2);
			//grid.setBorderWidth(1);
			//grid.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			grid.getElement().getStyle().setProperty("textAlign", "center");
			vakBreedte = Math.min((breedte-6)/2, (hoogte-40)/2);
			grid.getElement().getStyle().setMarginLeft(breedte/2-vakBreedte, Unit.PX);
			
			ba = new Viewer3d(kr, breedte/2-vakBreedte+1, hoogte/2-vakBreedte+1, vakBreedte-1, vakBreedte-1, bd);
			ba.zetAfstand(10000000);
			ba.zetSchaduw(false);
			ba.zetBeginHoeken(90,0);
			ba.zetMuisAan(false);
			ba.zetPijlAan(false);
			ba.initContext2d();
			ba.draw();
			VerticalPanel panelB = new VerticalPanel();
			FlowPanel labelB = new FlowPanel();
			labelB.getElement().getStyle().setFontSize(14, Unit.PX);
			labelB.getElement().getStyle().setProperty("lineHeight", "1.2");
			labelB.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			
			labelB.getElement().getStyle().setProperty("textAlign", "center");
			labelB.getElement().setInnerHTML("boven");
			panelB.add(labelB);
			panelB.add(ba.getCanvas());
			grid.setWidget(0,0,panelB);
			
			va = new Viewer3d(kr, breedte/2-vakBreedte+1, hoogte/2+1, vakBreedte-1, vakBreedte-1, bd);
			va.zetAfstand(10000000);
			va.zetSchaduw(false);
			va.zetBeginHoeken(0,0);
			va.zetMuisAan(false);
			va.zetPijlAan(false);
			va.initContext2d();
			va.draw();
			VerticalPanel panelV = new VerticalPanel();
			FlowPanel labelV = new FlowPanel();
			labelV.getElement().getStyle().setFontSize(14, Unit.PX);
			labelV.getElement().getStyle().setProperty("lineHeight", "1.2");
			labelV.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			labelV.getElement().getStyle().setProperty("textAlign", "center");
			labelV.getElement().setInnerHTML("voor");
			panelV.add(va.getCanvas());
			panelV.add(labelV);
			grid.setWidget(1,0,panelV);
			
			ra = new Viewer3d(kr, breedte/2+1, hoogte/2+1, vakBreedte-1, vakBreedte-1, bd);
			ra.zetAfstand(10000000);
			ra.zetSchaduw(false);
			ra.zetBeginHoeken(0,-90);
			ra.zetMuisAan(false);
			ra.zetPijlAan(false);
			ra.initContext2d();
			ra.draw();
			VerticalPanel panelR = new VerticalPanel();
			FlowPanel labelR = new FlowPanel();
			labelR.getElement().getStyle().setFontSize(14, Unit.PX);
			labelR.getElement().getStyle().setProperty("lineHeight", "1.2");
			labelR.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			labelR.getElement().getStyle().setProperty("textAlign", "center");
			labelR.getElement().setInnerHTML("rechts");
			panelR.add(ra.getCanvas());
			panelR.add(labelR);
			grid.setWidget(1,1,panelR);
			
			panel.add(grid);
		}
	}	
	
	public FlowPanel getPanel()
	{	return panel;
	}
	
	public void zetKlikAan(boolean b)
	{	va.zetKlikAan(b);
		ra.zetKlikAan(b);
		la.zetKlikAan(b);
		ba.zetKlikAan(b);
	}
	public void zetPijlAan(boolean b)
	{	va.zetPijlAan(b);
		ra.zetPijlAan(b);
		la.zetPijlAan(b);
		ba.zetPijlAan(b);
	}
	public void zetKubusRooster(KubusRooster kur)
	{	kr = kur;
		la.zetKubusRooster(kur);	
		ba.zetKubusRooster(kur);	
		va.zetKubusRooster(kur);	
		ra.zetKubusRooster(kur);
	}
	public void tekenOpnieuw()
	{	ra.tekenOpnieuw();
		ba.tekenOpnieuw();
		va.tekenOpnieuw();
	}
}
