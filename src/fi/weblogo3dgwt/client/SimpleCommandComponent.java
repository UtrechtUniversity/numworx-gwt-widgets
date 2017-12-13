package fi.weblogo3dgwt.client;


import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * see class SimpleCommandComponent in WebLogoGWT
 */

public abstract class SimpleCommandComponent extends CommandComponent
{
	public SimpleCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, sv);
	}
	
	/**
	 * Paint background of simple command: just a rectangle, fits all simple (non-composite) CCommands.
	 */
	protected void paintBackground(Context2d g)
	{
		g.setFillStyle(CssColor.make(238,238,238));
		if(traceKleur)
		{	traceKleurCnt++;
			g.setFillStyle(traceActiveColor);
			if (traceKleurCnt >= 3)
				traceKleur = false;
		}
		g.fillRect(xPos,yPos,getSize().width-1,getSize().height-1);
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.strokeRect(xPos,yPos,getSize().width-1,getSize().height-1);
		g.strokeRect(xPos+1,yPos+1,getSize().width-3,getSize().height-3);
	}
	
	/**
	 * Implemented here for CCommands without parameters.
	 * CCommands with parameters must override to display parameters.
	 */
	protected void paintCommand(Context2d g)
	{
		g.setFont(WebLogo3dGWT.fontString);
		
		g.setFillStyle(CssColor.make(0,0,0));

		TextMetrics tm = g.measureText(commandName+"( )");
		int textWidth = (int) Math.round(tm.getWidth());
		if (textWidth > breedte - 10)
		{	tm = g.measureText(commandName);
			textWidth = (int) Math.round(tm.getWidth()); 
			if (textWidth > breedte - 10)
			{	g.fillText(commandName.substring(0,1),xPos+10,yPos+18);
			}
			else
				g.fillText(commandName,xPos+10,yPos+18);
		}
		else
			g.fillText(commandName+"( )",xPos+10,yPos+18);
		
	}

	/**
	 * Get a string value of the command in this componenent. 
	 * Implemented here for covenience so simple commands don't need to override.
	 * Parameter commands, however, must override
	 */
	public String getCode(String tab)
	{	
		String s = tab + commandName+"( )" + "\n";
		return s;
	}
}
