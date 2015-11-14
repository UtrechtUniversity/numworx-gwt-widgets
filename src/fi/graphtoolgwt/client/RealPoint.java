package fi.graphtoolgwt.client;

import java.io.Serializable;

public class RealPoint implements Serializable
{	public static final double NZERO = 1e-5d;

	private double x, y;
	private int index;
	private int tabelIndex;
	private String xString;
	private String yString;
	
	//constructor 0
	
	public RealPoint()
	{}
	
	// constructor 1
	public RealPoint(double x, double y)
	{	this.setX(x);
		this.setY(y);
		setxString(Double.toString(x));
		setyString(Double.toString(y));
	}
	// constructor 2
	public RealPoint(RealPoint rp)
	{	setX(rp.getX());
		setY(rp.getY());
		setIndex(rp.getIndex());
		setTabelIndex(rp.getTabelIndex());
		setxString(rp.getxString());
		setyString(rp.getyString());
	}

    // redefine for method contains in Vector     
    // equality of this RealPoint and RealPoint u in Manhattan metric NZero
    public boolean equals(Object obj)
    {    if (obj instanceof RealPoint)
             return (((RealPoint) obj).getIndex() == getIndex()) &&
             		(Math.abs(getX() - ((RealPoint) obj).getX()) < NZERO) &&
    		   		(Math.abs(getY() - ((RealPoint) obj).getY()) < NZERO);
         return false;    
    }

	public boolean hasLargerXThen(RealPoint rp)
	{	return (getIndex() == rp.getIndex()) &&
			   (getX() > (rp.getX() + NZERO)); 	 
	}

	public boolean hasSameXAs(RealPoint rp)
	{	return (getIndex() == rp.getIndex()) &&
			   (Math.abs(getX() - rp.getX()) < NZERO); 	 
	}

	public RealPoint standarize()
	{	double length = Math.sqrt(getX() * getX() + getY() * getY());
		if (length > NZERO)
			return new RealPoint(getX() / length, getY() / length);
		else 
			return new RealPoint(0, 0);	
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getTabelIndex() {
		return tabelIndex;
	}

	public void setTabelIndex(int tabelIndex) {
		this.tabelIndex = tabelIndex;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getxString() {
		return xString;
	}

	public void setxString(String xString) {
		this.xString = xString;
	}

	public String getyString() {
		return yString;
	}

	public void setyString(String yString) {
		this.yString = yString;
	}
	
}