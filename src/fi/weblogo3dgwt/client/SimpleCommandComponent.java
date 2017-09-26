package fi.weblogo3dgwt.client;

//import java.awt.Color;
//import java.awt.Graphics;

import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


public abstract class SimpleCommandComponent extends CommandComponent
{
	public SimpleCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, sv);
	}
	
	/**
	 * Paint background of simple command: just a rectangle, fits all simple (non-composite) CCommands.
	 * 
	 * @see fi.javalogoweb.CommandComponent#paintBackground(java.awt.Graphics)
	 */
	@Override
	//protected void paintBackground(Graphics g)
	protected void paintBackground(Context2d g)
	{
		//g.setColor(new Color(238,238,238));
		g.setFillStyle(CssColor.make(238,238,238));
		if(traceKleur)
		{	traceKleurCnt++;
			//g.setColor(traceActiveColor);
			g.setFillStyle(traceActiveColor);
			if (traceKleurCnt >= 3)
				traceKleur = false;
		}
		//g.fillRect(0,0,getSize().width-1,getSize().height-1);
		g.fillRect(xPos,yPos,getSize().width-1,getSize().height-1);
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		//g.drawRect(0,0,getSize().width-1,getSize().height-1);
		g.strokeRect(xPos,yPos,getSize().width-1,getSize().height-1);
		//g.drawRect(1,1,getSize().width-3,getSize().height-3);
		g.strokeRect(xPos+1,yPos+1,getSize().width-3,getSize().height-3);
	}
	
	/**
	 * Implemented here for CCommands without parameters.
	 * CCommands with parameters must override to display parameters.
	 * 
	 * @see fi.javalogoweb.CommandComponent#paintCommand(java.awt.Graphics)
	 */
	@Override
	//protected void paintCommand(Graphics g)
	protected void paintCommand(Context2d g)
	{
		//g.setFont(JavaLogoWeb.defaultfont);
		g.setFont(WebLogo3dGWT.fontString);
		
//System.out.println(commandName + " " + WebLogoGWT.fontString);		
//System.out.println(commandName + " " + g.getFont());

		//g.setColor(Color.black);
		g.setFillStyle(CssColor.make(0,0,0));
		//g.drawString(commandNameTranslated+"( )",10,18);

//System.out.println("paint " + getCommandName() + " b = " + breedte);		
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
	 * 
	 * @see fi.javalogoweb.CommandComponent#getCode(java.lang.String)
	 */
	@Override
	public String getCode(String tab)
	{	
		String s = tab + commandName+"( )" + "\n";
		return s;
	}
}
