package fi.algebraexprgwt.client;

import java.util.*;

public class ZoomStateHolder 
{
	
	private AlgebraSchuifVeld asv;
	private HashMap<String,Object> zoomStates;
	
	public ZoomStateHolder(AlgebraSchuifVeld asv)
	{	this.asv = asv;
        zoomStates = new HashMap<String,Object>();
        setBeginwaarde("", 0);
        setSchaalFactorX("", 1);
        setFactorRijNummerX("", 99);
        setSchaalFactorY("", 1);
        setFactorRijNummerY("", 99);
        setZoomStates("");
        
	}
/*	
	public Enumeration keys()
	{	return zoomStates.keys();
	}
*/	
	public Set keySet()
	{
		return zoomStates.keySet();
	}
	
	public HashMap<String,Object> getState()
	{	HashMap<String,Object> h = new HashMap<String,Object>();
	
		//Enumeration en = zoomStates.keys();
		Set keySet = zoomStates.keySet();
		Object[] keys = keySet.toArray();
	
		//while(en.hasMoreElements())
		for (int kCnt = 0; kCnt < keys.length; kCnt++)
		{	
			//String key = (String) en.nextElement();
			String key = (String) keys[kCnt];
			h.put(key, ((ZoomState) zoomStates.get(key)).getState());
		}
		return h;
	}

    public void setState(HashMap<String,Object> h)
    {	if (h == null) 
    		return;
    
    	//Enumeration en = h.keys();
		Set keySet = h.keySet();
		Object[] keys = keySet.toArray();
    	
		//while(en.hasMoreElements())
		for (int kCnt = 0; kCnt < keys.length; kCnt++)
		{	
			//String key = (String) en.nextElement();
			String key = (String) keys[kCnt];
			
			ZoomState zs = new ZoomState();
			zs.setState((HashMap<String,Object>) h.get(key));
			zoomStates.put(key, zs);
		}
    }
    
	public void setSchaalFactorX(String varnaam, double schaalFactorX)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState) zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setSchaalFactorX(schaalFactorX);
        zoomStates.put(varnaam, zs);
	}
	
	public void setSchaalFactorY(String varnaam, double schaalFactorY)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState) zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setSchaalFactorY(schaalFactorY);
        zoomStates.put(varnaam, zs);
    }
	public void setFactorRijNummerX(String varnaam, int factorRijNummerX)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setFactorRijNummerX(factorRijNummerX);
        zoomStates.put(varnaam, zs);
	}
	
	public void setFactorRijNummerY(String varnaam, int factorRijNummerY)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setFactorRijNummerY(factorRijNummerY);
        zoomStates.put(varnaam, zs);
	}
	
	public void setBeginwaarde(String varnaam, int beginwaarde)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setBeginwaarde(beginwaarde);
        zoomStates.put(varnaam, zs);
	}
	
	public void setSelectnummer(String varnaam, int selectnummer)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setSelectnummer(selectnummer);
        zoomStates.put(varnaam, zs);
	}
	
	public void setBeginx(String varnaam, double beginx)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setBeginx(beginx);
        zoomStates.put(varnaam, zs);
	}
	
	public void setBeginy(String varnaam, double beginy)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setBeginy(beginy);
        zoomStates.put(varnaam, zs);
	}
	
	public void setTracexD(String varnaam, double tracexD)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setTracexD(tracexD);
        zoomStates.put(varnaam, zs);
	}
	
	public void setZoomStates(String varnaam)
	{	asv.setZoomStates(varnaam, (ZoomState) zoomStates.get(varnaam));
	}
	
	public void copyZoomState(String varnaam, ZoomState zs)
	{	zoomStates.put(varnaam, zs);
	}
	
	public ZoomState getZoomState(String varnaam)
	{	return (ZoomState) zoomStates.get(varnaam);
	}
	
	
	
	
}
