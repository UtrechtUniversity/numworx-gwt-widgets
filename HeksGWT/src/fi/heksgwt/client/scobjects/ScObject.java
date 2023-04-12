package fi.heksgwt.client.scobjects;

import com.google.gwt.canvas.dom.client.Context2d;

public interface ScObject 
{
	public abstract void schaal(double s);

	public abstract void setResized(boolean b);
	
	public abstract void paint(Context2d g);
	
	public abstract boolean contains (int x, int y);

}
