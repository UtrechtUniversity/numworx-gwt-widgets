package fi.heksgwt.client;

//import java.awt.*;
//import java.awt.event.*;

import fi.heksgwt.client.scobjects.*;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class GetalComponent extends ScComponent //implements ActionListener, FocusListener, MouseListener 
{
	private int waarde;
	private String waardeText = "";
//GWT	
	//private ScTextField beginWaardeTf;
	private boolean isTemp, instelbaar, bekend, leeg;
//GWT	
	//private ActionListener actionListener;
	
	Context2d getalContext2d;
	
	boolean visible = true;
	
	boolean transparent = false;
	boolean p23Klein = false;
	boolean p23Emmer = false;
	
	HeksGWT owner;

	public GetalComponent(int x, int y, int b, int h, HeksGWT owner) //Context2d c2d) 
	{
		super(x, y, b, h);
		this.owner = owner;
		
		//getalContext2d = c2d;
		
		waarde = 0;
		bekend = true;
		leeg = false;
		instelbaar = false;
		isTemp = false;
//GWT
/*		
		beginWaardeTf = new ScTextField(5, 0, b - 10, h, "0");
		beginWaardeTf.addActionListener(this);
		beginWaardeTf.addFocusListener(this);
		beginWaardeTf.setVisible(false);
		beginWaardeTf.setEnabled(false);
		beginWaardeTf.setLocation(getLocation().x, getLocation().y);
		add(beginWaardeTf);
*/		
	}

	public void setVisible(boolean b)
	{
		visible = b;
	}
	//public void paint(Graphics gr)
	public void paint(Context2d g)
	{
		if (!visible)
			return;
		
//System.out.println("gc paint");

		//Graphics g = (Graphics2D) gr;
		//((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int fontSize = (int) Math.round(3 * schaal * relh / 4);
		String fontString = "" + fontSize + "px arial, sans-serif";
		
		//Font f = new Font("SansSerif", Font.PLAIN, (int) (3 * schaal * relh / 4));
		//g.setColor(getForeground());
		//g.setFont(f);
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
		//FontMetrics fm = g.getFontMetrics();
		//int woordbreedte = fm.stringWidth(s);
		
//g.setStrokeStyle(CssColor.make(0,0,255));
//g.strokeRect(xPos, yPos, breedte, hoogte);
		
		TextMetrics tm = g.measureText(s);
		int woordbreedte = (int) Math.round(tm.getWidth());
		
		if (!transparent)
		{	
			g.setFillStyle(CssColor.make(255,255,255));
			g.fillRect(xPos, yPos, breedte, hoogte);
		}
		
		//g.drawString(s, (getSize().width - woordbreedte) / 2, (getSize().height + fm.getHeight()) / 2 - fm.getDescent());
		g.setFillStyle(CssColor.make(0,0,0));
		if (p23Klein)
			g.fillText(s, xPos + (breedte - woordbreedte) / 2, yPos + 30 * schaal);
		else if (p23Emmer)
			g.fillText(s, xPos + (breedte - woordbreedte) / 2, yPos + 4 * schaal);
		else
			g.fillText(s, xPos + (breedte - woordbreedte) / 2, yPos + 42 * schaal);
		
//GWT??		
		//super.paint(g);
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

	public int geefWaarde() 
	{
		String s = waardeText; //beginWaardeTf.getText();
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
//GWT?		
		//beginWaardeTf.setText(Integer.toString(waarde));
		paint();
	}

	public void zetBekend(boolean b) 
	{
		bekend = b;
//GWT		
		if (!b)
		//	beginWaardeTf.setText("");
			waardeText = "";
		if (!b)
			waarde = -999;
	}

	public void zetLeeg(boolean b) {
		leeg = b;
	}

	public void zetAlsTemp(boolean b) {
		isTemp = b;
	}

	public boolean isBekend() {
		return bekend;
	}

	public boolean isInstelbaar() {
		return instelbaar;
	}

	public void zetInstelbaar(boolean b) 
	{
//GWT		
		//if (b && !instelbaar)
		//{	addMouseListener(this);
		//}
		//else if (!b && instelbaar)
		//{	removeMouseListener(this);
//System.out.println("mouseListener remove");		
		//}
		instelbaar = b;
	}

	public void verhoog() 
	{
		waarde++;
		waardeText = Integer.toString(waarde);
//GWT		
		//beginWaardeTf.setText(Integer.toString(waarde));
		paint();
	}

	public void verlaag() 
	{
		waarde--;
		waardeText = Integer.toString(waarde);
//GWT		
		//beginWaardeTf.setText(Integer.toString(waarde));
		paint();
	}

	public void verhoog(int d) 
	{
		waarde += d;
		waardeText = Integer.toString(waarde);
//GWT		
		//beginWaardeTf.setText(Integer.toString(waarde));
		paint();
	}

	public void verlaag(int d) 
	{
		waarde -= d;
		waardeText = Integer.toString(waarde);
//GWT		
		//beginWaardeTf.setText(Integer.toString(waarde));
		paint();
	}
/*
	public void addActionListener(ActionListener listener) 
	{
		actionListener = AWTEventMulticaster.add(actionListener, listener);
	}
*/
/*	
	public void removeActionListener(ActionListener listener) 
	{
		actionListener = AWTEventMulticaster.remove(actionListener, listener);
	}
*/
	public void vulIn() 
	{
//System.out.println("gc vulIn inst " + instelbaar);		
		if (instelbaar) 
		{
//GWT
/*			
			beginWaardeTf.schaal(schaal);
			beginWaardeTf.setVisible(true);
			beginWaardeTf.setEnabled(true);
			beginWaardeTf.selectAll();
			beginWaardeTf.requestFocus();
*/			
		}
//System.out.println("gc vulIn Tf " + beginWaardeTf.isVisible());
	}

	
	//public void mouseClicked(MouseEvent e){;}

//GWT	
	//public void mousePressed(MouseEvent e) 
	//{
//System.out.println("gc mP");
		
		//if (actionListener != null) 
		//{	actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "vulin"));
		//}
//	}

	public void mouseUpTouchEndAction() 
	{	vulIn();
	}

	//public void mouseExited(MouseEvent e){;}

	//public void mouseEntered(MouseEvent e){;}

	public void actionPerformed() 
	{
		String s = waardeText; //beginWaardeTf.getText();
		int w;
		try 
		{
			w = Integer.parseInt(s);
			zetBekend(true);
			zetWaarde(w);
		} catch (NumberFormatException ex) 
		{
			//beginWaardeTf.setText("");
			waardeText = "";
			zetBekend(false);
			waarde = -999;
			paint();
		}
		//beginWaardeTf.setEnabled(false);
		//beginWaardeTf.setVisible(false);
		paint();
		//if (actionListener != null) 
		//{
		//	actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "action"));
		//}
		//this.requestFocus();
	}

//GWT?	
/*	
	public void focusLost(FocusEvent e) 
	{
		
		String s = beginWaardeTf.getText();
		int w;
		try {
			w = Integer.parseInt(s);
			zetBekend(true);
			zetWaarde(w);
		} catch (NumberFormatException ex) {
			beginWaardeTf.setText("");
			zetBekend(false);
			waarde = -999;
			repaint();
		}
		beginWaardeTf.setEnabled(false);
		// remove(beginWaardeTf);
		beginWaardeTf.setVisible(false);
		repaint();
		if (actionListener != null) {
			actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "focuslost"));
		}
	}
*/
	//public void focusGained(FocusEvent e){;}
}
