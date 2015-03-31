package fi.kansbomengwt.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

public class LijntjeLabel 
{
	CssColor lijnKleur = CssColor.make("black");
	Canvas lijnCanvas;
	Context2d g;
	int breedte;
	
	public LijntjeLabel(CssColor c, int breedte)
	{
		lijnCanvas = Canvas.createIfSupported();
		g = lijnCanvas.getContext2d();
		lijnKleur = c;
		this.breedte = breedte;
		lijnCanvas.setCoordinateSpaceHeight(10);
		lijnCanvas.setCoordinateSpaceWidth(breedte);
		}
	
	public void paint()
	{
		g.setStrokeStyle(lijnKleur);
		g.setLineWidth(2.0);
		g.beginPath();
		g.moveTo(2, 5);
		g.lineTo(breedte, 5);
		g.stroke();
		
//		setColor(lijnKleur);
//		g.drawLine(2, getSize().height / 2, 
//				   getSize().width - 2, getSize().height / 2);
		
	}
	
	public Canvas getCanvas()
	{
		return lijnCanvas;
	}
	
	public void setColor(CssColor c)
	{
		lijnKleur = c;
		paint();
	}

}
