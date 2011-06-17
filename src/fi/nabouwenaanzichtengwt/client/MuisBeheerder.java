package fi.nabouwenaanzichtengwt.client;

//import java.awt.*;
//import java.awt.event.*;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;

class MuisBeheerder //implements MouseListener, MouseMotionListener
{
	private int eerstex, laatstex, eerstey, laatstey, dx, dy;
	private Viewer3d eigenaar;
	//private AnimatieBeheerder ab;
	//private boolean animatieWasAan;
	
	public MuisBeheerder(Viewer3d v3d)
	{	eigenaar = v3d;
		eerstex = 0;
		eerstey = 0;
		laatstex = 0;
		laatstey = 0;
		dx = 0;
		dy = 0;
	}
	
	//-------------------------------------------------------------------------------------------
	//de AnimatieBeheerder maakt zich met deze methode kenbaar aan de Muisbeheerder  
	//-------------------------------------------------------------------------------------------
	//public void meldAnimatieBeheerder(AnimatieBeheerder ab)
	//{	this.ab = ab;
	//}
	
	//-------------------------------------------------------------------------------------------
	//afhandeling van de muis gebeurtenissen 
	//-------------------------------------------------------------------------------------------
	public void mousePressed(MouseEvent e)
	{	//if(ab!=null && ab.animatieStatus())
		//{	animatieWasAan = true;
		//	ab.onderbreekAnimatie();
		//}
		eerstex = e.getX();
		eerstey = e.getY();
		laatstex = e.getX();
		laatstey = e.getY();
		eigenaar.muisDrukActie(e);
	}
	
	public void mouseDragged(MouseEvent e)
	{	int x = e.getX();
		int y = e.getY();
		dx = x - laatstex;
		dy = laatstey -y;
		eigenaar.muisSleepActie();
		laatstex = x;
		laatstey = y;	
	}
	
	public void mouseReleased(MouseEvent e)
	{	eigenaar.muisLosActie(e);
		//if(animatieWasAan)
		//{	animatieWasAan = false;
		//	ab.beginAnimatie();
		//}
		
	}
	public void mouseClicked(MouseEvent e)
	{	//if(ab!=null && ab.animatieStatus())
		//{	animatieWasAan = true;
		//	ab.onderbreekAnimatie();
		//}
		eerstex = e.getX();
		eerstey = e.getY();
		laatstex = e.getX();
		laatstey = e.getY();
		eigenaar.muisKlikActie();
		//if(animatieWasAan)
		//{	animatieWasAan = false;
		//	ab.beginAnimatie();
		//}
	
	}
	public void mouseExited(MouseEvent e){;}
	
	public void mouseEntered(MouseEvent e){;}
	public void mouseMoved(MouseEvent e){;}
	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door de muishandlers in het leerlingenprogramma
	//-------------------------------------------------------------------------------------------
	public int geefSleepdx()
	{	return dx;
	}
	public int geefSleepdy()
	{	return dy;
	}
	public int geefDrukx()
	{	return eerstex;
	}
	public int geefDruky()
	{	return eerstey;
	}
	public int geefX()
	{	return laatstex;
	}
	public int geefY()
	{	return laatstey;
	}

}	