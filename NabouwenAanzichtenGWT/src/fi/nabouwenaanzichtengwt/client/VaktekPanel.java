package fi.nabouwenaanzichtengwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;

import fi.nabouwenaanzichtengwt.client.text.Text;

/**
 * klasse die een of meer aanzichten van een kubusrooster toont;<br>
 * mogelijkheden: boven+voor+rechts, voor+rechts, boven, voor of rechts
 * @author Peter Boon
 */

public class VaktekPanel 
{	
	/**
	 * internationalisatie labels
	 */
	static final Text rb = GWT.create(Text.class);
	/**
	 * breedte en hoogte van dit VaktekPanel
	 */
	int breedte, hoogte;
	/**
	 * breedte (en hoogte) van een individuele aanzichtviewer
	 */
	int vakBreedte;
	/**
	 * viewers voor voor-, boven- en rechteraanzicht
	 */
	Viewer3d va, ba, ra; 
	/**
	 * eigenaar van dit VaktekPanel
	 */
	NabouwenAanzichtenGWT eigenaar;
	/**
	 * het kubusrooster waarvan de aanzichten getoond worden
	 */
	KubusRooster kr;
	/**
	 * het aantal aanzichten in dit VaktekPanel
	 */
	int aantalViews;

	/**
	 * FlowPanel dat de viewers(s) en de labels bevat
	 */
	FlowPanel panel = new FlowPanel();
	/**
	 * Grid voor layout componenten
	 */
	private Grid grid = null;
	
	/**
	 * constructor: maak viewers en labels
	 * @param k het kubusrooster waarvan de aanzichten getoond worden 
	 * @param b breedte
	 * @param h hoogte
	 * @param aantalViews aantal aanzichten: 3 = boven, voor en rechts, 2 = voor en rechts<br>
	 * trukje: 4 = boven, 5 = voor, 6 = rechts
	 * @param bd eigenaar
	 */
	public VaktekPanel(KubusRooster k, int b, int h, int aantalViews, NabouwenAanzichtenGWT bd)
	{	this.aantalViews = aantalViews;
		
		panel.getElement().getStyle().setWidth(b, Unit.PX);
		panel.getElement().getStyle().setHeight(b, Unit.PX);
		panel.getElement().getStyle().setProperty("textAlign", "center");
		kr = k;
		eigenaar = bd;
		breedte = b;
		hoogte = h;
		
		//voor,rechts
		if (aantalViews == 2)
		{	
			grid = new Grid(1, 2);
			grid.addStyleName(bd.nabouwenAanzichtenCss.borderless());
			grid.getElement().getStyle().setProperty("textAlign", "center");
			vakBreedte = Math.min((breedte-6)/2, (hoogte-40));
			grid.getElement().getStyle().setMarginLeft(breedte/2-vakBreedte, Unit.PX);
					
			va = new Viewer3d(kr, breedte / 2 - vakBreedte + 1, 1, vakBreedte - 1, vakBreedte - 1, bd);
			va.zetAfstand(10000000);
			va.zetSchaduw(false);
			va.zetBeginHoeken(0,0);
			va.zetMuisAan(false);
			va.zetKlikAan(false);
			va.zetPijlAan(false);
			va.initContext2d();
			va.draw();
			VerticalPanel panelV = new VerticalPanel();
			FlowPanel labelV = new FlowPanel();
			labelV.getElement().getStyle().setFontSize(14, Unit.PX);
			labelV.getElement().getStyle().setProperty("lineHeight", "1.2");
			labelV.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			labelV.getElement().getStyle().setProperty("textAlign", "center");
			labelV.getElement().setInnerHTML(rb.voor());
			panelV.add(va.getCanvas());
			panelV.add(labelV);
			grid.setWidget(0,0,panelV);
			
			ra = new Viewer3d(kr, breedte/2 + 1, 1, vakBreedte - 1, vakBreedte-1, bd);
			ra.zetAfstand(10000000);
			ra.zetSchaduw(false);
			ra.zetBeginHoeken(0,-90);
			ra.zetMuisAan(false);
			ra.zetKlikAan(false);
			ra.zetPijlAan(false);
			ra.initContext2d();
			ra.draw();
			VerticalPanel panelR = new VerticalPanel();
			FlowPanel labelR = new FlowPanel();
			labelR.getElement().getStyle().setFontSize(14, Unit.PX);
			labelR.getElement().getStyle().setProperty("lineHeight", "1.2");
			labelR.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			labelR.getElement().getStyle().setProperty("textAlign", "center");
			labelR.getElement().setInnerHTML(rb.rechts());
			panelR.add(ra.getCanvas());
			panelR.add(labelR);
			grid.setWidget(0,1,panelR);
			
			panel.add(grid);

		}
		// boven, voor, rechts
		else if (aantalViews == 3)
		{	
			grid = new Grid(2, 2);
			grid.addStyleName(bd.nabouwenAanzichtenCss.borderless());
			grid.getElement().getStyle().setProperty("textAlign", "center");
			vakBreedte = Math.min((breedte-6)/2, (hoogte-40)/2);
			grid.getElement().getStyle().setMarginLeft(breedte/2-vakBreedte, Unit.PX);
			
			ba = new Viewer3d(kr, breedte/2-vakBreedte+1, hoogte/2-vakBreedte+1, vakBreedte-2, vakBreedte-2, bd);
			ba.zetAfstand(10000000);
			ba.zetSchaduw(false);
			ba.zetBeginHoeken(90,0);
			ba.zetMuisAan(false);
			ba.zetKlikAan(false);
			ba.zetPijlAan(false);
			ba.initContext2d();
			ba.draw();
			VerticalPanel panelB = new VerticalPanel();
			FlowPanel labelB = new FlowPanel();
			labelB.getElement().getStyle().setFontSize(14, Unit.PX);
			labelB.getElement().getStyle().setProperty("lineHeight", "1.2");
			labelB.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			
			labelB.getElement().getStyle().setProperty("textAlign", "center");
			labelB.getElement().setInnerHTML(rb.boven());
			panelB.add(labelB);
			panelB.add(ba.getCanvas());
			grid.setWidget(0,0,panelB);
			
			va = new Viewer3d(kr, breedte/2-vakBreedte+1, hoogte/2+1, vakBreedte-2, vakBreedte-2, bd);
			va.zetAfstand(10000000);
			va.zetSchaduw(false);
			va.zetBeginHoeken(0,0);
			va.zetMuisAan(false);
			va.zetKlikAan(false);
			va.zetPijlAan(false);
			va.initContext2d();
			va.draw();
			VerticalPanel panelV = new VerticalPanel();
			FlowPanel labelV = new FlowPanel();
			labelV.getElement().getStyle().setFontSize(14, Unit.PX);
			labelV.getElement().getStyle().setProperty("lineHeight", "1.2");
			labelV.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			labelV.getElement().getStyle().setProperty("textAlign", "center");
			labelV.getElement().setInnerHTML(rb.voor());
			panelV.add(va.getCanvas());
			panelV.add(labelV);
			grid.setWidget(1,0,panelV);
			
			ra = new Viewer3d(kr, breedte/2+1, hoogte/2+1, vakBreedte-2, vakBreedte-2, bd);
			ra.zetAfstand(10000000);
			ra.zetSchaduw(false);
			ra.zetBeginHoeken(0,-90);
			ra.zetMuisAan(false);
			ra.zetKlikAan(false);
			ra.zetPijlAan(false);
			ra.initContext2d();
			ra.draw();
			VerticalPanel panelR = new VerticalPanel();
			FlowPanel labelR = new FlowPanel();
			labelR.getElement().getStyle().setFontSize(14, Unit.PX);
			labelR.getElement().getStyle().setProperty("lineHeight", "1.2");
			labelR.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			labelR.getElement().getStyle().setProperty("textAlign", "center");
			labelR.getElement().setInnerHTML(rb.rechts());
			panelR.add(ra.getCanvas());
			panelR.add(labelR);
			grid.setWidget(1,1,panelR);
			
			panel.add(grid);
		}
		// trukje: 4 = boven
		else if (aantalViews == 4)
		{
			vakBreedte = Math.min(2*(breedte-6)/3, 2*(hoogte-40)/3);
			
			ba = new Viewer3d(kr, breedte/2-vakBreedte+1, hoogte/2-vakBreedte+1, vakBreedte-2, vakBreedte-2, bd);
			ba.zetAfstand(10000000);
			ba.zetSchaduw(false);
			ba.zetBeginHoeken(90,0);
			ba.zetMuisAan(false);
			ba.zetKlikAan(false);
			ba.zetPijlAan(false);
			ba.initContext2d();
			ba.draw();
			VerticalPanel panelB = new VerticalPanel();
			FlowPanel labelB = new FlowPanel();
			labelB.getElement().getStyle().setFontSize(14, Unit.PX);
			labelB.getElement().getStyle().setProperty("lineHeight", "1.2");
			labelB.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			labelB.getElement().getStyle().setProperty("textAlign", "center");
			labelB.getElement().setInnerHTML(rb.boven());
			panelB.add(labelB);
			panelB.add(ba.getCanvas());
			//grid.setWidget(0,0,panelB);
			panel.add(panelB);
		}
		// trukje: 5 = voor
		else if (aantalViews == 5)
		{
			vakBreedte = Math.min(2*(breedte-6)/3, 2*(hoogte-40)/3);
			
			va = new Viewer3d(kr, breedte/2-vakBreedte+1, hoogte/2+1, vakBreedte-2, vakBreedte-2, bd);
			va.zetAfstand(10000000);
			va.zetSchaduw(false);
			va.zetBeginHoeken(0,0);
			va.zetMuisAan(false);
			va.zetKlikAan(false);
			va.zetPijlAan(false);
			va.initContext2d();
			va.draw();
			VerticalPanel panelV = new VerticalPanel();
			FlowPanel labelV = new FlowPanel();
			labelV.getElement().getStyle().setFontSize(14, Unit.PX);
			labelV.getElement().getStyle().setProperty("lineHeight", "1.2");
			labelV.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			labelV.getElement().getStyle().setProperty("textAlign", "center");
			labelV.getElement().setInnerHTML(rb.voor());
			panelV.add(va.getCanvas());
			panelV.add(labelV);
			panel.add(panelV);
			
		}
		// trukje: 6 = rechts
		else if (aantalViews == 6)
		{
			vakBreedte = Math.min(2*(breedte-6)/3, 2*(hoogte-40)/3);
			
			ra = new Viewer3d(kr, breedte/2+1, hoogte/2+1, vakBreedte-2, vakBreedte-2, bd);
			ra.zetAfstand(10000000);
			ra.zetSchaduw(false);
			ra.zetBeginHoeken(0,-90);
			ra.zetMuisAan(false);
			ra.zetKlikAan(false);
			ra.zetPijlAan(false);
			ra.initContext2d();
			ra.draw();
			VerticalPanel panelR = new VerticalPanel();
			FlowPanel labelR = new FlowPanel();
			labelR.getElement().getStyle().setFontSize(14, Unit.PX);
			labelR.getElement().getStyle().setProperty("lineHeight", "1.2");
			labelR.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
			labelR.getElement().getStyle().setProperty("textAlign", "center");
			labelR.getElement().setInnerHTML(rb.rechts());
			panelR.add(ra.getCanvas());
			panelR.add(labelR);
			
			panel.add(panelR);

		}

		
	}	

	/**
	 * getter voor dit VaktekPanel 
	 * @return panel
	 */
	public FlowPanel getPanel()
	{	return panel;
	}
	/**
	 * zet KubusRooster kur in alle (non-null) aanzichtviewers
	 * @param kur het kubusrooster
	 */
	public void zetKubusRooster(KubusRooster kur)
	{	kr = kur;
		if (ba != null)
			ba.zetKubusRooster(kur);
		if (va != null)
			va.zetKubusRooster(kur);
		if (ra != null)
			ra.zetKubusRooster(kur);
	}
	
	/**
	 * herteken dit VaktekPanel
	 */
	public void tekenOpnieuw()
	{	if (ra != null)
			ra.tekenOpnieuw();
		if (ba != null)
			ba.tekenOpnieuw();
		if (va != null)
			va.tekenOpnieuw();
	}
	
}
