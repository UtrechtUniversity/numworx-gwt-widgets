package fi.algebrapijlengwt.client;

import java.util.*;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

/**
 * een klasse die de zoomStates voor de variabelen bevat in de vorm van een HashMap
 * met Keys de namen van de variabele;<br>
 * zie klasse zoomState 
 */

public class ZoomStateHolder 
{	

	/**
	 * het werkveld
	 */
	private AlgebraSchuifVeld asv;
	/**
	 * de HashMap met zoomStates
	 */
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

	/**
	 * vind de mogelijke Keys als een Set 
	 * @return de Set met Keys
	 */
	public Set keySet()
	{
		return zoomStates.keySet();
	}
	
	/**
	 * maak een Map-kopie van de HashMap via getState van de klasse zoomState 
	 * @return een Map
	 */
	public Map<String,Object> getState()
	{	Map<String,Object> h = new HashMap<String,Object>();
		Set keySet = zoomStates.keySet();
		Object[] keys = keySet.toArray();
		for (int kCnt = 0; kCnt < keys.length; kCnt++)
		{	String key = (String) keys[kCnt];
			h.put(key, ((ZoomState) zoomStates.get(key)).getState());
		}
		return h;
	}

	/**
	 * kopieer de Map naar de HashMap via setState van de klasse zoomState
	 * @param map een Map
	 */
    public void setState(Map<String,Object> map)
    {	if (map == null) 
    		return;
    	ObjectMap h = JSONUtilities.wrapMap(map);
    	Set keySet = map.keySet();
    	Object[] keys = keySet.toArray();
    	for (int kCnt = 0; kCnt < keys.length; kCnt++)
		{	String key = (String) keys[kCnt];
    		ZoomState zs = new ZoomState();
			zs.setState(h.getMap(key));
			zoomStates.put(key, zs);
		}
    }
    
    /**
     * update schaalFactorX voor variable varnaam, als er geen zoomState
     * is voor variabele varnaam, maak een nieuwe
     * @param varnaam variabele
     * @param schaalFactorX nieuwe schaalFactorX
     */
	public void setSchaalFactorX(String varnaam, double schaalFactorX)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState) zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setSchaalFactorX(schaalFactorX);
        zoomStates.put(varnaam, zs);
	}

    /**
     * update schaalFactorY voor variable varnaam, als er geen zoomState
     * is voor variabele varnaam, maak een nieuwe
     * @param varnaam variabele
     * @param schaalFactorY nieuwe schaalFactorY
     */
	public void setSchaalFactorY(String varnaam, double schaalFactorY)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState) zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setSchaalFactorY(schaalFactorY);
        zoomStates.put(varnaam, zs);
    }
    /**
     * update factorRijNummerX voor variable varnaam, als er geen zoomState
     * is voor variabele varnaam, maak een nieuwe
     * @param varnaam variabele
     * @param factorRijNummerX nieuwe factorRijNummerX
     */
	public void setFactorRijNummerX(String varnaam, int factorRijNummerX)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setFactorRijNummerX(factorRijNummerX);
        zoomStates.put(varnaam, zs);
	}
    /**
     * update FactorRijNummerY voor variable varnaam, als er geen zoomState
     * is voor variabele varnaam, maak een nieuwe
     * @param varnaam variabele
     * @param factorRijNummerY nieuwe factorRijNummerY
     */
	public void setFactorRijNummerY(String varnaam, int factorRijNummerY)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setFactorRijNummerY(factorRijNummerY);
        zoomStates.put(varnaam, zs);
	}
    /**
     * update beginwaarde voor variable varnaam, als er geen zoomState
     * is voor variabele varnaam, maak een nieuwe
     * @param varnaam variabele
     * @param beginwaarde nieuwe beginwaarde
     */
	public void setBeginwaarde(String varnaam, int beginwaarde)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setBeginwaarde(beginwaarde);
        zoomStates.put(varnaam, zs);
	}
    /**
     * update selectnummer voor variable varnaam, als er geen zoomState
     * is voor variabele varnaam, maak een nieuwe
     * @param varnaam variabele
     * @param selectnummer nieuwe selectnummer
     */
	public void setSelectnummer(String varnaam, int selectnummer)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setSelectnummer(selectnummer);
        zoomStates.put(varnaam, zs);
	}

    /**
     * update beginx voor variable varnaam, als er geen zoomState
     * is voor variabele varnaam, maak een nieuwe
     * @param varnaam variabele
     * @param beginx nieuwe beginx
     */
	public void setBeginx(String varnaam, double beginx)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setBeginx(beginx);
        zoomStates.put(varnaam, zs);
	}

    /**
     * update beginy voor variable varnaam, als er geen zoomState
     * is voor variabele varnaam, maak een nieuwe
     * @param varnaam variabele
     * @param beginy nieuwe beginy
     */
	public void setBeginy(String varnaam, double beginy)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setBeginy(beginy);
        zoomStates.put(varnaam, zs);
	}
	
    /**
     * update tracexD voor variable varnaam, als er geen zoomState
     * is voor variabele varnaam, maak een nieuwe
     * @param varnaam variabele
     * @param tracexD nieuwe tracexD
     */
	public void setTracexD(String varnaam, double tracexD)
	{	ZoomState zs = null;
        if (zoomStates.containsKey(varnaam)) 
        	zs = (ZoomState)zoomStates.get(varnaam);
        if (zs == null) 
        	zs = new ZoomState();
        zs.setTracexD(tracexD);
        zoomStates.put(varnaam, zs);
	}
	
	/**
	 * zet de zoomState van alle variabelen met naam varnaam in componenten op het werkveld
	 * @param varnaam de naam van de variabele
	 */
	public void setZoomStates(String varnaam)
	{	asv.setZoomStates(varnaam, (ZoomState) zoomStates.get(varnaam));
	}
	
	/**
	 * voeg de zoomState van variabele varnaam toe aan de zoomStateHolder 
	 * @param varnaam de naam van de variabele
	 * @param zs de zoomState van de variabele
	 */
	public void copyZoomState(String varnaam, ZoomState zs)
	{	zoomStates.put(varnaam, zs);
	}

	/**
	 * vindt de zoomState van de variabele met naam varnaam
	 * @param varnaam de naam van de variabele
	 * @return de zoomState van de variabele varnaam
	 */
	public ZoomState getZoomState(String varnaam)
	{	return (ZoomState) zoomStates.get(varnaam);
	}
}
