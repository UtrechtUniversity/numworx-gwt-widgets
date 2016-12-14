package fi.stroomdiagrammengwt.client;


import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * class displaying the capacity of an edge as a box 
 * on the edge
 */

public class CapacityPanel  
{
	CssColor veryLightBlue = CssColor.make(198, 239, 247);
	/**
	 * color for the border
	 */
	CssColor bgColor = veryLightBlue;
	/**
	 * capacity displayed in (white) rectangle
	 */
	Rectangle capacityTextField;
	/**
	 * String displaying the capacity
	 */
	String capacityText = "";
		
	/**
	 * simulating a compoment: x position
	 */
	int xPos;
	/**
	 * simulating a component: y position
	 */
	int yPos;
	/**
	 * simulating a compoment: width
	 */
	int breedte;
	/**
	 * simulating a component: height
	 */
	int hoogte;
	/**
	 * String defining the font to be used
	 */
	String fontString = "12px arial, sans-serif";
	
	/**
	 * constructor 
	 * @param w width
	 * @param h height
	 */
	public CapacityPanel(int w, int h)
	{	breedte = w;
		hoogte = h;
		capacityTextField = new Rectangle(6, 2, breedte - 12, hoogte - 4);
	}

	/**
	 * simulating a component
	 * @param x the x position
	 * @param y the y position
	 */
	public void setLocation(int x, int y)
	{
		xPos = x; yPos = y;
		capacityTextField = new Rectangle(xPos + 6, yPos + 2, breedte - 12, hoogte - 4);
	}
	
	/**
	 * check if the capacityTextField was clicked 
	 * @param x x coordinate clicked
	 * @param y y coordinate clicked
	 * @return true/false
	 */
	public boolean capacityClicked(int x, int y)
	{
	   	Rectangle capacityRect = new Rectangle(xPos, yPos, breedte, hoogte);
	   	return capacityRect.contains(x, y);
	}
	 
	/**
	 * set the text displaying the capacity
	 * @param t the text
	 */
	public void setText(String t)
	{	capacityText = t;
	}

	/**
	 * get the capacity as text
	 * @return capacityText
	 */
	public String getText()
	{	return capacityText;
	}
	
	/**
	 * paint the capacity panel
	 * @param g the Context2d to be used
	 */
	public void paintComponent(Context2d g)
    {	// bgColor rectangle 
		g.setFillStyle(bgColor);
		g.fillRect(xPos, yPos, breedte, hoogte);
        //black outline;
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.strokeRect(xPos, yPos, breedte - 1, hoogte - 1);
		// white capacity rectangle
		g.setFillStyle(CssColor.make(255,255,255));
		g.fillRect(capacityTextField.x, capacityTextField.y, capacityTextField.width, capacityTextField.height);
        //grey outline for capacicity rectangle;
		g.setStrokeStyle(CssColor.make(192,192,192));
		g.strokeRect(capacityTextField.x, capacityTextField.y, capacityTextField.width, capacityTextField.height);
		// capacity text in black
		g.setFillStyle(CssColor.make(0,0,0));
		g.setFont(fontString);
		g.fillText(capacityText, capacityTextField.x + 2, capacityTextField.y + 16, capacityTextField.width);
		

    }
}
