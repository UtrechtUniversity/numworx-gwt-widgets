package fi.algebrapijlengwt.client;

//import java.awt.*;
//import java.awt.event.*;
import fi.algebrapijlengwt.client.UitvoerSchuifComponent.TextBoxKeyDownHandler;
import fi.algebrapijlengwt.client.expressies_ap.*;

//import javax.swing.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.LayoutPanel;

import java.util.HashMap;

public class BewerkingSchuifComponent extends AlgebraSchuifComponent //implements ActionListener, FocusListener
{	
	BasisExpressie beginw;
	//JTextField tf;
	
	//TextBox tf;
	TekstPopup tf;
	LayoutPanel inputOwner;
	String tfString = "";
	
	//Font f;
	//FontMetrics fm;
	int fontSize = 14;
	
//GWT	
	//protected PlusMinKnop plusMinKnop;
	
	protected boolean scrollable;
	
	
	public BewerkingSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(2, asv, x, y, b, h);
	
		//f = new Font("SansSerrif",Font.PLAIN,14);
		//fm = getFontMetrics(f);
		
		beginw = new BasisExpressie("3");
		
//GWT
/*		
		tf = new JTextField();
		if (!links)
			tf.setBounds(20,1,19,18);
		else 
			tf.setBounds(10,1,19,18);
		tf.addActionListener(this);
		tf.addFocusListener(this);
		tf.setVisible(false);
		tf.setEnabled(false);
*/		

		inputOwner = asv.owner.canvasPanel;
//GWT deze wordt niet gebruikt in AP
		
/*		
		if(!links)
		{	plusMinKnop = new PlusMinKnop(b-12,2,10,h-4, PlusMinKnop.VERTIKAAL);
		}
		else
		{	plusMinKnop = new PlusMinKnop(b-22,2,10,h-4, PlusMinKnop.VERTIKAAL);
		}
		plusMinKnop.addActionListener(this);
		plusMinKnop.setColor(new Color(255,150,0));
*/		
	}
/*	
	public void showTextBox()
	{
		tf = new TextBox();
		tf.setText(tfString);
		
		inputOwner.add(tf);
		
		if (!links)
		{
			inputOwner.setWidgetLeftWidth(tf, xPos + 20, Style.Unit.PX, 19, Style.Unit.PX);
			inputOwner.setWidgetTopHeight(tf, yPos + 1, Style.Unit.PX, 18, Style.Unit.PX);
		}
		else
		{
			inputOwner.setWidgetLeftWidth(tf, xPos + 10, Style.Unit.PX, 19, Style.Unit.PX);
			inputOwner.setWidgetTopHeight(tf, yPos + 1, Style.Unit.PX, 18, Style.Unit.PX);
		}
		
		tf.setFocus(true);
		
		tf.addKeyDownHandler(new TextBoxKeyDownHandler());
	}
*/
	public void showTekstPopup()
	{
		int popupX = xPos + 10 + inputOwner.getAbsoluteLeft();
		int popupY = yPos + hoogte + inputOwner.getAbsoluteTop();
		
		if ((tf != null) && tf.isVisible())
		{
			zetInvulWaarde();
		}

		tf = new TekstPopup(this);
		tf.setText(tfString);
		tf.setWidth("25px");
		tf.setHeight("20px");
		//tf.setModal(true);
		tf.setPopupPosition(popupX, popupY);
		tf.show();
		tf.textBox.setFocus(true);

	}
	
	public void setScrollable(boolean b)
	{	scrollable = b;
//GWT	
/*	
		if(b)
			add(plusMinKnop,0);
		else 
			remove(plusMinKnop);
*/			
	}
	
	public HashMap<String,Object> getState()
	{	String basisExp  = null;
				
		basisExp = this.beginw.basisString;
				
		HashMap<String,Object> h = super.getState();
	    h.put("basisExp", basisExp);
	    
	    return h;
	}

    public void setState(HashMap<String,Object> h)
    {	String basisExp = (String)h.get("basisExp");
    
				
		beginw = new BasisExpressie(basisExp);
		//beginw.zetMaat(fm);
		beginw.zetMaat(fontSize, ascContext2d);
				
		super.setState(h);
		
		zetMaat();
		
    }
    
	public void zetLinks(boolean b)
	{	links = b;
	
//GWT
/*	
		if(!links)tf.setBounds(30,1,19,18);
		else tf.setBounds(20,1,19,18);
*/		
		
		for(int i=0 ; i<aantalPu ; i++)
		{	pijlUit[i].zetLinks(b);
			if (!links)
			{	//pijlUit[i].zetPlaats(getLocation().x + getSize().width+9 ,getLocation().y + 10 );
				pijlUit[i].zetPlaats(xPos + breedte + 9 ,yPos + 10 );
			}
			else 
			{	//pijlUit[i].zetPlaats(getLocation().x - 10 ,getLocation().y + 10 );
				pijlUit[i].zetPlaats(xPos - 10 , yPos + 10 );
			
			}
		}
		
//GWT
/*		
		if(!links)
		{	plusMinKnop.setLocation(getSize().width-12,1);
		}
		else
		{	plusMinKnop.setLocation(getSize().width-22,1);
		}
*/		
		//asv.tekenOpnieuw();
	}
	
	//public void paint(Graphics g)
	public void paint(Context2d g)
  	{ 	
		if (!visible)
			return; 
		
		if (!links)
		{	
			//g.setColor(Color.orange);
			g.setFillStyle(CssColor.make(255, 200, 0));
			
			//g.fillRoundRect(10, 0, getSize().width - 11, getSize().height - 1, 8, 8);
			g.fillRect(xPos + 10, yPos + 0, breedte - 11, hoogte - 1);
			
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			
			//g.drawRoundRect(10,0,getSize().width-11,getSize().height-1,8,8);
			g.strokeRect(xPos + 10,yPos + 0,breedte-11,hoogte-1);
		}
		else
		{	//g.setColor(Color.orange);
			g.setFillStyle(CssColor.make(255, 200, 0));
			
			//g.fillRoundRect(0,0,getSize().width-11,getSize().height-1,8,8);
			g.fillRect(xPos + 0, yPos + 0, breedte - 11, hoogte - 1);
			
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			
			//g.drawRoundRect(0,0,getSize().width-11,getSize().height-1,8,8);
			g.strokeRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);
		}	
		super.paint(g);
	}
	
	public void zetMaat()
	{	int b = AlgebraSchuifVeld.basisB; //50;
		int h = AlgebraSchuifVeld.basisH; //20;
		int corr = 0;
		if (beginw != null)
		{	b = beginw.breedte;
			if (b > 10)
				b = b+40;
			else 
				b = AlgebraSchuifVeld.basisB; //50;
			
		}
		
		//setSize(b,h);
		breedte = b;
		hoogte = h;
	
//GWT		
//		tf.setBounds(20,1,b-31,18);
				
		int sccrollCorr = 0;
		if (scrollable) 
			sccrollCorr = 10;
		
//GWT
/*		
		if(!links)
		{	tf.setBounds(30-sccrollCorr,1,b-31,18);
			plusMinKnop.setLocation(b-12,2);
		}
		else
		{	tf.setBounds(20-sccrollCorr,1,b-31,18);
			plusMinKnop.setLocation(b-22,2);
		}
*/			

	}
	
//GWT
/*	
	//public void mouseClicked(MouseEvent e)
	{	if (asv.fixed)
			return;
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;
		
//System.out.println("clicked");		
		
		add(tf);
		tf.setVisible(true)	;
		tf.setEnabled(true);
		tf.selectAll();
		tf.requestFocus();
		
	}
*/	
	public void zetInvulWaarde()
	{	boolean isGeldigeInvoer = true;
		{	try
			{	String s = tf.getText();
				s = s.replace(',','.');
				tf.setText(s);
				Double w = Double.valueOf(tf.getText());
			}
			catch(NumberFormatException ex)
			{	isGeldigeInvoer = false;
				//tf.setText(Expressie.df.format(beginw.geefWaarde()));
				tf.setText(UF.format0(beginw.geefWaarde(),3));
			}
		}
		if (isGeldigeInvoer)
		{	
			tfString = tf.getText();
			beginw = new BasisExpressie(tf.getText());
			//beginw.zetMaat(fm);
			beginw.zetMaat(fontSize, ascContext2d);
		}
		else
		{	beginw = new BasisExpressie("3");
			//beginw.zetMaat(fm);
			beginw.zetMaat(fontSize, ascContext2d);
		}
		zetMaat();
		zetVeranderd(20);
	
//GWT
		
//		tf.setEnabled(false);
//		remove(tf);
		tf.setVisible(false);
		inputOwner.remove(tf);
		
		
		asv.tekenOpnieuw();
	}
	
	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	if (asv.fixed)
			return;
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;		

		press = true;
        taptime = System.currentTimeMillis();
        doubletap.add(taptime);
		super.mouseDownTouchStartAction(eventX, eventY);
	}	
	
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction()
	{	if (asv.fixed)
			return;
		if (asv.isDemo)
			return;
		
		if (asv.frozen)
			return;		
		
		//super.mouseReleased(e);
		if (isDoubleClick()) 
		{
			if (!isStapel)
			{	showTekstPopup();
				//showTextBox();
			
			}
            doubletap.clear();
        } 
		else if (isLongClick()) 
		{
            doubletap.clear();
        } 
		else 
		{
            if (doubletap.size() >= 2) 
            {	
            	//doubletap.clear();
            	doubletap.remove(0);
            }
        }
		super.mouseUpTouchEndAction();
		
		
	}

	class TextBoxKeyDownHandler implements KeyDownHandler
	{
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
//System.out.println("enter");
				zetInvulWaarde();

			}
		}
	}
	
//GWT
/*	
	public void actionPerformed(ActionEvent e)
	{	if(e.getSource()==tf)
		{	zetInvulWaarde();
		}
		else if(e.getSource()==plusMinKnop)
		{	if(beginw!=null && !Double.isNaN(beginw.geefWaarde().doubleValue()))
			{	double w = beginw.geefWaarde().doubleValue();
				if(e.getActionCommand().equals("min"))w -= 1;
				if(e.getActionCommand().equals("plus"))w += 1;
				String waardeString = Expressie.df.format(w);
				beginw = new BasisExpressie(waardeString);
				tf.setText(waardeString);
				zetVeranderd(20);
				beginw.zetMaat(fm);
				zetMaat();
				schuifveld.tekenOpnieuw();
			}
		}
		
	}
*/	
//GWT	
//	public void focusLost(FocusEvent e)
//	{	zetInvulWaarde();
//	}
//	public void focusGained(FocusEvent e){;	}
}
