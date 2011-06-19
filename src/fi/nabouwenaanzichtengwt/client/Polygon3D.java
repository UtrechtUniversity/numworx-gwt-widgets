package fi.nabouwenaanzichtengwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

class Polygon3D
{
	public Polygon pol;
	public Punt3D normaal;
	public double gemz;
	public CssColor vulkleur,lijnkleur;
	public boolean isLijn,isOmlijnd,isLeeg;
	
	public void draw(Context2d gIm, boolean schaduw)
  	{
		if(normaal.z >0)
		{	
			if(schaduw)
			{	String vkString = vulkleur.toString().substring(4,vulkleur.toString().length()-1);
				String[] kleurenStr = StringUtils.split(vkString,",");

				int blue =  Integer.parseInt(kleurenStr[2]);
				int green = Integer.parseInt(kleurenStr[1]);
				int red =   Integer.parseInt(kleurenStr[0]);
				
				double grijsfactor = 0.5*((-normaal.x - normaal.y + normaal.z)/Math.sqrt(3)+1);
				if(grijsfactor<0)grijsfactor=0;if(grijsfactor>1)grijsfactor=1;
				int roodwaarde = 50+(int)(red*grijsfactor*0.75);
				int groenwaarde = 50+(int)(green*grijsfactor*0.75);
				int blauwwaarde = 50+(int)(blue*grijsfactor*0.75);
				//gIm.setColor(new Color(roodwaarde,groenwaarde,blauwwaarde));
				vulkleur = CssColor.make(roodwaarde,groenwaarde,blauwwaarde);
				gIm.setFillStyle(vulkleur);
			}
			else
			{	gIm.setFillStyle(vulkleur);
			}
			
			CssColor gebruikteLijnkleur = lijnkleur;
			CssColor gebruikteVulkleur = vulkleur;
			
			if(isLeeg)gebruikteVulkleur = null;
			if(!isLijn && !isOmlijnd)gebruikteLijnkleur = null;
			
			pol.draw(gIm, gebruikteLijnkleur, gebruikteVulkleur);
			
			
		}
  	}
}
