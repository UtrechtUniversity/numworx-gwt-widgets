package fi.weblogogwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * abstract superclass for simple program components, that is program components not containing
 * other program components;  
 */
public abstract class SimpleCommandComponent extends CommandComponent
{
	/**
	 * constructor
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param sv parent containing the drawing canvas
	 */
	public SimpleCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	super(x, y, b, h, sv);
	}
	
	/**
	 * Paint background of simple command: just a rectangle, OK for all simple (non-composite) CCommands.
	 */
	protected void paintBackground(Context2d g)
	{	// light gray
		g.setFillStyle(CssColor.make(238,238,238));
		// component is traced
		if(traceKleur)
		{	traceKleurCnt++;
			g.setFillStyle(traceActiveColor);
			// untrace after 3 paints
			if (traceKleurCnt >= 3)
				traceKleur = false;
		}
		g.fillRect(xPos,yPos,getSize().width-1,getSize().height-1);
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.strokeRect(xPos,yPos,getSize().width-1,getSize().height-1);
		//g.strokeRect(xPos+1,yPos+1,getSize().width-3,getSize().height-3);
	}
	
	/**
	 * Implemented here for CCommands without parameters.
	 * CCommands with parameters must override to display parameters.
	 */
	@Override
	protected void paintCommand(Context2d g)
	{	g.setFont(WebLogoGWT.fontString);
		// black
		g.setFillStyle(CssColor.make(0,0,0));
		// make sure the width of the commandName is smaller then the
		// width of the component, if necessary trim:
		// when dragging some components have reduced width
		TextMetrics tm = g.measureText(commandName + "( )");
		int textWidth = (int) Math.round(tm.getWidth());
		if (textWidth > breedte - 10)
		{	// try without parenthesis
			tm = g.measureText(commandName);
			textWidth = (int) Math.round(tm.getWidth()); 
			if (textWidth > breedte - 10)
			{	// first two letters of commandName
				g.fillText(commandName.substring(0,1),xPos+10,yPos+18);
			}
			else
				g.fillText(commandName,xPos+10,yPos+18);
		}
		else // display full tekst
			g.fillText(commandName+"( )",xPos+10,yPos+18);
	}

	/**
	 * Get a string value of the command in this component. 
	 * Implemented here for convenience so simple commands don't need to override.
	 * Parameter commands, however, must override this
	 * @see fi.weblogogwt.client.CommandComponent#getCode(java.lang.String)
	 */
	@Override
	public String getCode(String tab)
	{	
		String s = tab + commandName+"( )" + "\n";
		return s;
	}
}
