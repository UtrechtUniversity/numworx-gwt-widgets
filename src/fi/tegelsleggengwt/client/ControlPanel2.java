package fi.tegelsleggengwt.client;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;



public class ControlPanel2 extends LayoutPanel //JPanel implements ActionListener
{	
	Label codeveld;
	
	//JButton draaiknop,wisknop,tekenknop,legknop,terugknop,wisTegelknop;
	PushButton draaiknop,wisknop,ontwerpknop,nieuwetegelknop,terugknop,leggenknop;
	TegelsPanel eigenaar;
	
	ToggleButton zwartButton, grijsButton, roodButton, oranjeButton, groenButton, cyaanButton, blauwButton, magentaButton, 
    geelButton;
	int toggleSize = 20;

	CssColor zwart = CssColor.make(0,0,0);
	CssColor grijs = CssColor.make(220,220,220);
	CssColor rood = CssColor.make(255,0,0);
	CssColor oranje = CssColor.make(255,127,0);
	CssColor groen = CssColor.make(0,255,0);
	CssColor cyaan = CssColor.make(0,255,255);
	CssColor blauw = CssColor.make(0,0,255);
	CssColor magenta = CssColor.make(255,0,255);
	CssColor geel = CssColor.make(255,255,0);
	
	//ToggleButton[] kleurenV;
	
	//ActKeuzePanel gridKeuze;
	String rasterGroep = "rasterGroep";
	RadioButton grofButton;
	RadioButton fijnButton;

	//Font font;
	//FontMetrics fm;
	String fontString = "12px sans-serif";
	TextMetrics tm; 
	
	int offset = 10;
	int arrowButtonWidth = 20;
	
	LWArrowButton upButton, downButton;
	
	TegelsLeggenGWT owner;
	
	int xPos, yPos;
	
	public ControlPanel2(TegelsLeggenGWT o, TegelsPanel gv)
	{	
		owner = o;
		eigenaar = gv;
		
		
	}
	
	public void initialize()
	{
		
//GWT(5)
		upButton = new LWArrowButton(arrowButtonWidth, arrowButtonWidth, 0, CssColor.make(230, 230, 230));
		add(upButton.buttonCanvas);
		setWidgetLeftWidth(upButton.buttonCanvas, 0, Style.Unit.PX, arrowButtonWidth, Style.Unit.PX);
		setWidgetTopHeight(upButton.buttonCanvas, 0, Style.Unit.PX, arrowButtonWidth, Style.Unit.PX);
		upButton.setEnabled(false);
		
		//upButton.setBounds(0, 0, arrowButtonWidth, arrowButtonWidth);
		//upButton.setEnabled(false);
		//upButton.addActionListener(this);
		//add(upButton);
		MouseHandler mouseHandlerUp = new MouseHandler(true);
		upButton.buttonCanvas.addMouseDownHandler(mouseHandlerUp);
		upButton.buttonCanvas.addMouseMoveHandler(mouseHandlerUp);
		upButton.buttonCanvas.addMouseUpHandler(mouseHandlerUp);
		
		TouchHandler touchHandlerUp = new TouchHandler(true);
		upButton.buttonCanvas.addTouchStartHandler(touchHandlerUp);
		upButton.buttonCanvas.addTouchMoveHandler(touchHandlerUp);
		upButton.buttonCanvas.addTouchEndHandler(touchHandlerUp);
		
//GWT(5)		
		downButton = new LWArrowButton(arrowButtonWidth, arrowButtonWidth, 2, CssColor.make(230, 230, 230));
		add(downButton.buttonCanvas);
		setWidgetLeftWidth(downButton.buttonCanvas, 0, Style.Unit.PX, arrowButtonWidth, Style.Unit.PX);
		setWidgetTopHeight(downButton.buttonCanvas, eigenaar.controlHoogte - arrowButtonWidth, Style.Unit.PX, arrowButtonWidth, Style.Unit.PX);
		downButton.setEnabled(false);
		
		//downButton.setBounds(0, eigenaar.controlHoogte - arrowButtonWidth, arrowButtonWidth, arrowButtonWidth);
		//downButton.setEnabled(false);	
		//downButton.addActionListener(this);
		//add(downButton);

		MouseHandler mouseHandlerDown = new MouseHandler(false);
		downButton.buttonCanvas.addMouseDownHandler(mouseHandlerDown);
		downButton.buttonCanvas.addMouseMoveHandler(mouseHandlerDown);
		downButton.buttonCanvas.addMouseUpHandler(mouseHandlerDown);
		
		TouchHandler touchHandlerDown = new TouchHandler(false);
		downButton.buttonCanvas.addTouchStartHandler(touchHandlerDown);
		downButton.buttonCanvas.addTouchMoveHandler(touchHandlerDown);
		downButton.buttonCanvas.addTouchEndHandler(touchHandlerDown);
		
		tm = eigenaar.tegelsContext2d.measureText(TegelsLeggenGWT.rb.draaiknopLabel());
		double width1 = tm.getWidth();
		tm = eigenaar.tegelsContext2d.measureText(TegelsLeggenGWT.rb.nieuwetegelknopLabel());
		double width2 = tm.getWidth();
		
		int width = (int) Math.round(Math.max(width1,width2)) + 25;
		int height = 20;
		
		int currentX = arrowButtonWidth + offset;
		int currentY = offset;
		
		draaiknop = new PushButton(TegelsLeggenGWT.rb.draaiknopLabel());
		//draaiknop.setFont(font);
		draaiknop.addStyleName(TegelsLeggenGWT.tegelsLeggenGWTCssResource.pushbutton());
		add(draaiknop);
		setWidgetLeftWidth(draaiknop, currentX, Style.Unit.PX, width, Style.Unit.PX);
		setWidgetTopHeight(draaiknop, currentY, Style.Unit.PX, height, Style.Unit.PX);
		
		//draaiknop.setBounds(arrowButtonWidth + offset, offset / 2, width, height);
		//add(draaiknop);
		
		//draaiknop.addActionListener(this);
		draaiknop.addClickHandler(new PushClickHandler());

		nieuwetegelknop = new PushButton(TegelsLeggenGWT.rb.nieuwetegelknopLabel());
		//nieuwetegelknop.setFont(font);
		nieuwetegelknop.addStyleName(TegelsLeggenGWT.tegelsLeggenGWTCssResource.pushbutton());
		add(nieuwetegelknop);
		setWidgetLeftWidth(nieuwetegelknop, currentX, Style.Unit.PX, width, Style.Unit.PX);
		setWidgetTopHeight(nieuwetegelknop, currentY, Style.Unit.PX, height, Style.Unit.PX);
		
		nieuwetegelknop.addClickHandler(new PushClickHandler());
		setWidgetVisible(nieuwetegelknop,false);
//		nieuwetegelknop.addClickHandler(new PushClickHandler());		
		
		currentX += width + offset;

		tm = eigenaar.tegelsContext2d.measureText(TegelsLeggenGWT.rb.wisknopLabel());
		width1 = tm.getWidth();
		tm = eigenaar.tegelsContext2d.measureText(TegelsLeggenGWT.rb.terugknopLabel());
		width2 = tm.getWidth();
		
		width = (int) Math.round(Math.max(width1,width2)) + 25;
		
		wisknop = new PushButton(TegelsLeggenGWT.rb.wisknopLabel());
		//wisknop.setFont(font);
		wisknop.addStyleName(TegelsLeggenGWT.tegelsLeggenGWTCssResource.pushbutton());
		add(wisknop);
		setWidgetLeftWidth(wisknop, currentX, Style.Unit.PX, width, Style.Unit.PX);
		setWidgetTopHeight(wisknop, currentY, Style.Unit.PX, height, Style.Unit.PX);
		
		wisknop.addClickHandler(new PushClickHandler());
		
		terugknop = new PushButton(TegelsLeggenGWT.rb.terugknopLabel());
		//terugknop.setFont(font);
		terugknop.addStyleName(TegelsLeggenGWT.tegelsLeggenGWTCssResource.pushbutton());
		add(terugknop);
		setWidgetLeftWidth(terugknop, currentX, Style.Unit.PX, width, Style.Unit.PX);
		setWidgetTopHeight(terugknop, currentY, Style.Unit.PX, height, Style.Unit.PX);
		
		terugknop.addClickHandler(new PushClickHandler());
		setWidgetVisible(terugknop,false);

		currentX += width + offset;
		
		tm = eigenaar.tegelsContext2d.measureText(TegelsLeggenGWT.rb.ontwerpknopLabel());
		width1 = tm.getWidth();
		tm = eigenaar.tegelsContext2d.measureText(TegelsLeggenGWT.rb.leggenknopLabel());
		width2 = tm.getWidth();
		
		width = (int) Math.round(Math.max(width1,width2)) + 25;

		ontwerpknop = new PushButton(TegelsLeggenGWT.rb.ontwerpknopLabel());
		//tekenknop.setFont(font);
		ontwerpknop.addStyleName(TegelsLeggenGWT.tegelsLeggenGWTCssResource.pushbutton());
		add(ontwerpknop);
		setWidgetLeftWidth(ontwerpknop, currentX, Style.Unit.PX, width, Style.Unit.PX);
		setWidgetTopHeight(ontwerpknop, currentY, Style.Unit.PX, height, Style.Unit.PX);
		
		ontwerpknop.addClickHandler(new PushClickHandler());

		leggenknop = new PushButton(TegelsLeggenGWT.rb.leggenknopLabel());
		//legknop.setFont(font);		
		leggenknop.addStyleName(TegelsLeggenGWT.tegelsLeggenGWTCssResource.pushbutton());
		add(leggenknop);
		setWidgetLeftWidth(leggenknop, currentX, Style.Unit.PX, width, Style.Unit.PX);
		setWidgetTopHeight(leggenknop, currentY, Style.Unit.PX, height, Style.Unit.PX);
		
		leggenknop.addClickHandler(new PushClickHandler());
		setWidgetVisible(leggenknop,false);

		currentX = arrowButtonWidth + offset;
		currentY = 2 * offset + height;
		
		codeveld = new Label();
		//codeveld.setFont(font);
		codeveld.addStyleName(TegelsLeggenGWT.tegelsLeggenGWTCssResource.label());
		add(codeveld);
		setWidgetLeftWidth(codeveld, currentX, Style.Unit.PX, 200, Style.Unit.PX);
		setWidgetTopHeight(codeveld, currentY, Style.Unit.PX, height, Style.Unit.PX);
		
		setWidgetVisible(codeveld, false);
		
		//codeveld.setBounds(legknop.getLocation().x, // + 
		//				   legknop.getLocation().y + legknop.getSize().height + offset / 2,
		//				   185, height);
		//if (!eigenaar.transVersion)
		//	add(codeveld);
		//codeveld.setEditable(false);
		//codeveld.setBackground(Color.white);
		//codeveld.setVisible(false);
		
		
		currentX = arrowButtonWidth + offset;
		currentY = 2 * offset + height;

		ToggleClickHandler toggleClickHandler = new ToggleClickHandler();
		
		zwartButton = new ToggleButton(owner.zwartImage, owner.zwartImage);
		add(zwartButton);
		setWidgetLeftWidth(zwartButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		setWidgetTopHeight(zwartButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		//zwartButton.setDown(true);
		zwartButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize;// + offset;

		grijsButton = new ToggleButton(owner.grijsImage, owner.grijsImage);
		add(grijsButton);
		setWidgetLeftWidth(grijsButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		setWidgetTopHeight(grijsButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		grijsButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize;// + offset;
		
		roodButton = new ToggleButton(owner.roodImage, owner.roodImage);
		add(roodButton);
		setWidgetLeftWidth(roodButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		setWidgetTopHeight(roodButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		roodButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize;// + offset;
	
		oranjeButton = new ToggleButton(owner.oranjeImage, owner.oranjeImage);
		add(oranjeButton);
		setWidgetLeftWidth(oranjeButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		setWidgetTopHeight(oranjeButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		oranjeButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize;// + offset;
				
		groenButton = new ToggleButton(owner.groenImage, owner.groenImage);
		add(groenButton);
		setWidgetLeftWidth(groenButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		setWidgetTopHeight(groenButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		groenButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize;// + offset;
		
		cyaanButton = new ToggleButton(owner.cyaanImage, owner.cyaanImage);
		add(cyaanButton);
		setWidgetLeftWidth(cyaanButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		setWidgetTopHeight(cyaanButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		cyaanButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize;// + offset;

		blauwButton = new ToggleButton(owner.blauwImage, owner.blauwImage);
		add(blauwButton);
		setWidgetLeftWidth(blauwButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		setWidgetTopHeight(blauwButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		blauwButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize;// + offset;
				
		magentaButton = new ToggleButton(owner.magentaImage, owner.magentaImage);
		add(magentaButton);
		setWidgetLeftWidth(magentaButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		setWidgetTopHeight(magentaButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		magentaButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize;// + offset;

		
		geelButton = new ToggleButton(owner.geelImage, owner.geelImage);
		add(geelButton);
		setWidgetLeftWidth(geelButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		setWidgetTopHeight(geelButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		geelButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize + offset;
		
		roodButton.setDown(true);
		

		currentX = arrowButtonWidth + offset;
		currentY = 2 * offset + height;

		tm = eigenaar.tegelsContext2d.measureText(TegelsLeggenGWT.rb.fijnrasterTekst());
		width1 = tm.getWidth();
		//tm = eigenaar.tegelsContext2d.measureText(TegelsLeggenGWT.rb.grofrasterTekst());
		//width2 = tm.getWidth();
		
		width = (int) Math.round(width1) + 50;
		
		fijnButton = new RadioButton(rasterGroep, TegelsLeggenGWT.rb.fijnrasterTekst());
		fijnButton.addStyleName(TegelsLeggenGWT.tegelsLeggenGWTCssResource.radiobutton());
		add(fijnButton);
		setWidgetLeftWidth(fijnButton, currentX, Style.Unit.PX, width, Style.Unit.PX);
		setWidgetTopHeight(fijnButton, currentY, Style.Unit.PX, height, Style.Unit.PX);
		//fijnButtonButton.addClickHandler(toggleClickHandler);
		currentX += width + offset;
		
		tm = eigenaar.tegelsContext2d.measureText(TegelsLeggenGWT.rb.grofrasterTekst());
		width1 = tm.getWidth();
		//tm = eigenaar.tegelsContext2d.measureText(TegelsLeggenGWT.rb.grofrasterTekst());
		//width2 = tm.getWidth();
		
		width = (int) Math.round(width1) + 50;
		
		grofButton = new RadioButton(rasterGroep, TegelsLeggenGWT.rb.grofrasterTekst());
		grofButton.addStyleName(TegelsLeggenGWT.tegelsLeggenGWTCssResource.radiobutton());
		add(grofButton);
		setWidgetLeftWidth(grofButton, currentX, Style.Unit.PX, width, Style.Unit.PX);
		setWidgetTopHeight(grofButton, currentY, Style.Unit.PX, height, Style.Unit.PX);
		//grofButtonButton.addClickHandler(toggleClickHandler);
		//currentX += toggleSize;// + offset;

		grofButton.setValue(true);
		fijnButton.addValueChangeHandler(new RasterKeuzeVCH());
		grofButton.addValueChangeHandler(new RasterKeuzeVCH());
		setWidgetVisible(fijnButton,false);
		setWidgetVisible(grofButton,false);

	}
	
	public void setKleurenVisible(boolean b)
	{
		setWidgetVisible(zwartButton, b);
		setWidgetVisible(grijsButton, b);
		setWidgetVisible(roodButton, b);
		setWidgetVisible(oranjeButton, b);
		setWidgetVisible(groenButton, b);
		setWidgetVisible(cyaanButton, b);
		setWidgetVisible(blauwButton, b);
		setWidgetVisible(magentaButton, b);
		setWidgetVisible(geelButton, b);
	}
	
	public void controlTekenen()
	{	//draaiknop.setVisible(false);
		setWidgetVisible(draaiknop,false);	
		//wisknop.setVisible(false);
		setWidgetVisible(wisknop,false);
		//ontwerpknop.setVisible(false);
		setWidgetVisible(ontwerpknop,false);
		
		//terugknop.setVisible(true);
		setWidgetVisible(terugknop,true);
		//leggenknop.setVisible(true);
		setWidgetVisible(leggenknop,true);
		//nieuwetegelknop.setVisible(true);
		setWidgetVisible(nieuwetegelknop,true);
		
		setKleurenVisible(false);
		
		if (!eigenaar.transVersion)
		{	//codeveld.setVisible(true);
			codeveld.setText(eigenaar.code);
			setWidgetVisible(codeveld,true);
		}
	}
	public void controlLeggen()
	{	//draaiknop.setVisible(true);
		setWidgetVisible(draaiknop,true);
		//wisknop.setVisible(true);
		setWidgetVisible(wisknop,true);
		//ontwerpknop.setVisible(true);
		setWidgetVisible(ontwerpknop,true);
		//terugknop.setVisible(false);
		setWidgetVisible(terugknop,false);
		//leggenknop.setVisible(false);
		setWidgetVisible(leggenknop,false);
		//nieuwetegelknop.setVisible(false);
		setWidgetVisible(nieuwetegelknop,false);
		setKleurenVisible(true);
		
		setWidgetVisible(codeveld,false);
	}
	public void controlFoto()
	{	//draaiknop.setVisible(false);
		setWidgetVisible(draaiknop,false);
		//wisknop.setVisible(true);
		setWidgetVisible(wisknop,true);
		//ontwerpknop.setVisible(false);
		setWidgetVisible(ontwerpknop,false);
		//terugknop.setVisible(false);
		setWidgetVisible(terugknop,false);
		//leggenknop.setVisible(false);
		setWidgetVisible(leggenknop,false);
		//nieuwetegelknop.setVisible(true);
		setWidgetVisible(nieuwetegelknop,true);
	}
	//public void actionPerformed(ActionEvent e)
	//{
	
    class PushClickHandler implements ClickHandler
    {
    	
    	public void onClick(ClickEvent e)
    	{
    		
//System.out.println("onClick");    		
			//e.preventDefault();
			e.stopPropagation();

//GWT		
		//if (e.getSource() == upButton)
		//{	eigenaar.vorigeBasisVorm();
		//}
//GWT		
		//if (e.getSource() == downButton)
		//{	eigenaar.volgendeBasisVorm();
		//}
			if (e.getSource() == draaiknop)
			{	eigenaar.draaiBasisvorm();
//System.out.println("draaiknop");			
			}
			if (e.getSource() == wisknop)
			{	eigenaar.wisSs();
//System.out.println("wisknop");			
			}
			if (e.getSource() == ontwerpknop)
			{	controlTekenen();
				eigenaar.zetTekenen();
//System.out.println("ontwerpknop");				
			
				if (eigenaar.transVersion)
				{	//gridKeuze.setVisible(true);
					setWidgetVisible(fijnButton,true);
					setWidgetVisible(grofButton,true);
				}
			}
			if (e.getSource() == leggenknop)
			{	controlLeggen();
				eigenaar.zetLeggen();
				
//System.out.println("leggenknop");				
		
			//gridKeuze.setVisible(false);
			setWidgetVisible(fijnButton,false);
			setWidgetVisible(grofButton,false);
	
			}
			if (e.getSource() == terugknop)
			{	eigenaar.tekenStapTerug();
//System.out.println("terugknop");			
			}
			if (e.getSource() == nieuwetegelknop)
			{	eigenaar.wisTegel();
//System.out.println("nieuwetegelknop");			
			
			}
			
			eigenaar.paint();
    	} //onClick
	}
    
	class RasterKeuzeVCH implements ValueChangeHandler<Boolean>
	{	//public void actionPerformed(ActionEvent e)
		public void onValueChange(ValueChangeEvent<Boolean> e)
		{	if (e.getSource() == grofButton)
			{	int factor = 2;
				eigenaar.vermenigvuldigPunten((double)Trans.factor/factor);
				Trans.zetFactor(factor);
				eigenaar.tekenOpnieuw();
			
			}
			else if (e.getSource() == fijnButton)
			{
				int factor = 1;
				eigenaar.vermenigvuldigPunten((double)Trans.factor/factor);
				Trans.zetFactor(factor);
				eigenaar.tekenOpnieuw();
			}
		}
	}	
    
 	void buttonsUp(ToggleButton tb)
   	{
   		if (!zwartButton.equals(tb))
   			zwartButton.setDown(false);
   		if (!grijsButton.equals(tb))
   			grijsButton.setDown(false);
 		if (!roodButton.equals(tb))
 			roodButton.setDown(false);
   		if (!oranjeButton.equals(tb))
   			oranjeButton.setDown(false);
   		if (!groenButton.equals(tb))
   			groenButton.setDown(false);
   		if (!cyaanButton.equals(tb))
   			cyaanButton.setDown(false);
   		if (!blauwButton.equals(tb))
   			blauwButton.setDown(false);
 		if (!magentaButton.equals(tb))
   			magentaButton.setDown(false);
 		if (!geelButton.equals(tb))
 			geelButton.setDown(false);
   	}
 		
   	class ToggleClickHandler implements ClickHandler
	{
   		public void onClick(ClickEvent e)
		{
    		if (e.getSource() == zwartButton)
    		{	if (zwartButton.isDown())
    			{	buttonsUp(zwartButton);
    				eigenaar.kleurBasisvorm(zwart);
    				//owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.zwart;
    			}
    		}
    		else if (e.getSource() == grijsButton)
    		{	if (grijsButton.isDown())
    			{	buttonsUp(grijsButton);
    				eigenaar.kleurBasisvorm(grijs);
    				//owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.grijs;
    			}	
    		}

    		else if (e.getSource() == roodButton)
    		{	if (roodButton.isDown())
    			{	buttonsUp(roodButton);
    				eigenaar.kleurBasisvorm(rood);
   					//owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.rood;
    			}
    		}

    		else if (e.getSource() == oranjeButton)
    		{	if (oranjeButton.isDown())
    			{	buttonsUp(oranjeButton);
					eigenaar.kleurBasisvorm(oranje);
   					//owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.oranje;
    			}
    		}
    		else if (e.getSource() == groenButton)
    		{	if (groenButton.isDown())
    			{	buttonsUp(groenButton);
					eigenaar.kleurBasisvorm(groen);
					//owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.groen;
    			}
    		}
    		else if (e.getSource() == cyaanButton)
    		{	if (cyaanButton.isDown())
    			{	buttonsUp(cyaanButton);
					eigenaar.kleurBasisvorm(cyaan);
      				//owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.cyaan;
    			}
    		}
    		else if (e.getSource() == blauwButton)
    		{	if (blauwButton.isDown())
    			{	buttonsUp(blauwButton);
    				eigenaar.kleurBasisvorm(blauw);
      				//owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.blauw;
    			}
    		}
    		else if (e.getSource() == magentaButton)
    		{	if (magentaButton.isDown())
    			{	buttonsUp(magentaButton);
    				eigenaar.kleurBasisvorm(magenta);
    				//owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.magenta;
    			}
    		}
    		
    		else if (e.getSource() == geelButton)
    		{
    			if (geelButton.isDown())
    			{	buttonsUp(geelButton);
    				eigenaar.kleurBasisvorm(geel);
      				//owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.geel;
      				
    			}
    		}
    		
    			
		}
		
	}

	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		boolean up;
		
		public MouseHandler(boolean b)
		{
			up = b;
		}
		
		//public void mousePressed(MouseEvent e)
		public void onMouseDown(MouseDownEvent e)
		{
			e.preventDefault();
			
			// prevent scrolling 
			e.stopPropagation();

			if (up)
				eigenaar.vorigeBasisVorm();
			else
				eigenaar.volgendeBasisVorm();

			//int eventX = e.getX();
			//int eventY = e.getY();
			
//			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		//public void mouseDragged(MouseEvent e)
		public void onMouseMove(MouseMoveEvent e)	
		{
			
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
//System.out.println("mouse move veld");			
			
//			if (!mouseDown)
//				return;

//			int eventX = e.getX();
//			int eventY = e.getY();
			//boolean shiftPressed = e.isShiftKeyDown();

//System.out.println("sp = " + shiftPressed);

//			mouseMoveTouchMoveAction(eventX, eventY);
			
			
			
		} // onMouseMove
		
		//public void mouseReleased(MouseEvent e)
		public void onMouseUp(MouseUpEvent e)	
		{
			
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
//			mouseDown = false;
		
//			mouseUpTouchEndAction(e.getX(), e.getY());

		}

	} //MLMML


	// tablet, dwo 
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		boolean up;
		
		public TouchHandler(boolean b)
		{
			up = b;
		}
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);

				if (up)
					eigenaar.vorigeBasisVorm();
				else
					eigenaar.volgendeBasisVorm();

//				int eventX = touch.getPageX() - tegelsCanvas.getAbsoluteLeft();
//				int eventY = touch.getPageY() - tegelsCanvas.getAbsoluteTop();				
				
//				mouseDownTouchStartAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
/*			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				//boolean shiftPressed = e.isShiftKeyDown();

			    //boolean shiftPressed = false;
			    int eventX = touch.getPageX() - tegelsCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - tegelsCanvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY);
				
				lastMoveX = eventX;
				lastMoveY = eventY;

				
		    }
			e.preventDefault();
			e.stopPropagation();
*/			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			//mouseUpTouchEndAction(lastMoveX, lastMoveY);
		}

	}

   	
/*	
	public void paint(Graphics g)
	{	super.paint(g);
	}
*/		
}