package fi.stroomdiagrammengwt.client;

//import java.awt.*;
//import java.awt.event.*;

//import javax.swing.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class CapacityPanel // extends JPanel 
{
	CssColor veryLightBlue = CssColor.make(198, 239, 247);
	//JTextField capacityTextField;
	Rectangle capacityTextField;
	String capacityText = "";
	CssColor bgColor = veryLightBlue;
	
	int xPos, yPos, breedte, hoogte;
	
	String fontString = "12px arial, sans-serif";
	
	public CapacityPanel(int w, int h)
	{
		//setLayout(null);
		//setSize(w, h);
		breedte = w;
		hoogte = h;
		//capacityTextField = new JTextField();
		capacityTextField = new Rectangle(6, 2, breedte - 12, hoogte - 4);
		//add(capacityTextField);
		
	}

	public void setLocation(int x, int y)
	{
		xPos = x; yPos = y;
		capacityTextField = new Rectangle(xPos + 6, yPos + 2, breedte - 12, hoogte - 4);
	}
	public void setText(String t)
	{
		capacityText = t;
	}
	
	public String getText()
	{
		return capacityText;
	}
	
	
	public void paintComponent(Context2d g)
    {   //g.setColor(bgColor);
		g.setFillStyle(bgColor);
        //g.fillRoundRect(0, 0, getSize().width, getSize().height, DrawingPanel.roundWidth, DrawingPanel.roundHeight);
		g.fillRect(xPos, yPos, breedte, hoogte);
		
        //g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
        // outline
        //g.drawRoundRect(0, 0, getSize().width - 1, getSize().height - 1, DrawingPanel.roundWidth, DrawingPanel.roundHeight);
		g.strokeRect(xPos, yPos, breedte - 1, hoogte - 1);
		
		g.setFillStyle(CssColor.make(255,255,255));
        //g.fillRoundRect(0, 0, getSize().width, getSize().height, DrawingPanel.roundWidth, DrawingPanel.roundHeight);
		g.fillRect(capacityTextField.x, capacityTextField.y, capacityTextField.width, capacityTextField.height);
		
        //g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(192,192,192));
        // outline
        //g.drawRoundRect(0, 0, getSize().width - 1, getSize().height - 1, DrawingPanel.roundWidth, DrawingPanel.roundHeight);
		g.strokeRect(capacityTextField.x, capacityTextField.y, capacityTextField.width, capacityTextField.height);
		
		g.setFillStyle(CssColor.make(0,0,0));
		g.setFont(fontString);
		g.fillText(capacityText, capacityTextField.x + 2, capacityTextField.y + 16, capacityTextField.width);
		

    }
}
