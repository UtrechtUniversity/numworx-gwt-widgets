package fi.algebraexprgwt.client;

import java.util.HashMap;

public class ZoomState 
{
	private double schaalFactorY = 1;
	private int factorRijNummerY = 99;
	private double schaalFactorX = 1;
	private int factorRijNummerX = 99;
	private int beginwaarde = 0;
	private int selectnummer = 999; 
	private double beginx = 14;
	private double beginy = 14;
	private double tracexD = 0;
	
	public HashMap<String,Object> getState()
	{	HashMap<String,Object> h = new HashMap<String,Object>();
		h.put("schaalFactorY", new Double(schaalFactorY));
		h.put("factorRijNummerY", new Integer(factorRijNummerY));
		h.put("schaalFactorX", new Double(schaalFactorX));
		h.put("factorRijNummerX", new Integer(factorRijNummerX));
		h.put("beginwaarde", new Integer(beginwaarde));
		h.put("selectnummer", new Integer(selectnummer));
		h.put("beginx", new Double(beginx));
		h.put("beginy", new Double(beginy));
		h.put("tracexD", new Double(tracexD));
		return h;
	}

    public void setState(HashMap<String,Object> h)
    {	double schaalFactorY = 1;
		int factorRijNummerY = 99;
		double schaalFactorX = 1;
		int factorRijNummerX = 99;
		int beginwaarde = 0;
		int selectnummer = 999; 
		double beginx = 0;
		double beginy = 0;
		double tracexD = 0;
		
		if (h.containsKey("schaalFactorY")) 
			schaalFactorY = ((Double) h.get("schaalFactorY")).doubleValue();
		if (h.containsKey("factorRijNummerY")) 
			factorRijNummerY = ((Integer) h.get("factorRijNummerY")).intValue();
		if (h.containsKey("schaalFactorX")) 
			schaalFactorX = ((Double) h.get("schaalFactorX")).doubleValue();
		if (h.containsKey("factorRijNummerX")) 
			factorRijNummerX = ((Integer) h.get("factorRijNummerX")).intValue();
		if (h.containsKey("beginwaarde")) 
			beginwaarde = ((Integer) h.get("beginwaarde")).intValue();
		if (h.containsKey("selectnummer")) 
			selectnummer = ((Integer) h.get("selectnummer")).intValue();
		if (h.containsKey("beginx")) 
			beginx = ((Double) h.get("beginx")).doubleValue();
		if (h.containsKey("beginy")) 
			beginy = ((Double) h.get("beginy")).doubleValue();
		if (h.containsKey("tracexD")) 
			tracexD = ((Double) h.get("tracexD")).doubleValue();
		
		this.schaalFactorY = schaalFactorY;
		this.factorRijNummerY = factorRijNummerY;
		this.schaalFactorX = schaalFactorX;
		this.factorRijNummerX = factorRijNummerX;
		this.beginwaarde = beginwaarde;
		this.selectnummer = selectnummer;
		this.beginx = beginx;
		this.beginy = beginy;
		this.tracexD = tracexD;
	}
    
	public void setSchaalFactorX(double schaalFactorX)
	{	this.schaalFactorX = schaalFactorX;
	}
	
	public void setSchaalFactorY(double schaalFactorY)
	{	this.schaalFactorY = schaalFactorY;
	}
	
	public void setFactorRijNummerX(int factorRijNummerX)
	{	this.factorRijNummerX = factorRijNummerX;
	}
	
	public void setFactorRijNummerY(int factorRijNummerY)
	{	this.factorRijNummerY = factorRijNummerY;
	}
	
	public void setBeginwaarde(int beginwaarde)
	{	this.beginwaarde = beginwaarde;
	}
	
	public void setSelectnummer(int selectnummer)
	{	this.selectnummer = selectnummer;
	}
	
	public void setBeginx(double beginx)
	{	this.beginx = beginx;
	}
	
	public void setBeginy(double beginy)
	{	this.beginy = beginy;
	}
	
	public void setTracexD(double tracexD)
	{	this.tracexD = tracexD;
	}
	
	public double getSchaalFactorX()
	{	return schaalFactorX;
	}
	
	public double getSchaalFactorY()
	{	return schaalFactorY;
	}
	
	public int getFactorRijNummerX()
	{	return factorRijNummerX;
	}
	
	public int getFactorRijNummerY()
	{	return factorRijNummerY;
	}
	
	public int getBeginwaarde()
	{	return beginwaarde;
	}
	
	public int getSelectnummer()
	{	return selectnummer;
	}
	
	public double getBeginx()
	{	return beginx;
	}
	
	public double getBeginy()
	{	return beginy;
	}
	
	public double getTracexD()
	{	return tracexD;
	}

}
