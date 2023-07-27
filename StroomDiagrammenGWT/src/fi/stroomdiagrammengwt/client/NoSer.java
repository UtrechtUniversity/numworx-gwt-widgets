package fi.stroomdiagrammengwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

/**
 * class translating the classes VertexCopy, EdgeCopy and DiagramCopy,
 * which were Serializable in Java, to and from JSON-compatible data 
 * (see class NoSer in the Java-version); used in reading launchdata
 * and in getState/setState. 
 */
public class NoSer 
{
	/**
	 * large negative integer
	 */
	public static int notDefined = -10000;
	
	/**
	 * translate a VertexCopy into a HashMap
	 * @param vc VertexCopy to be translated
	 * @return the HashMap
	 */
	public static HashMap<String,Object> vertexCopyToVertexHashMap(VertexCopy vc)
	{
		HashMap<String,Object> h = new HashMap<String,Object>(); 
    	h.put("code", new Integer(vc.code));
    	h.put("layernum", new Integer(vc.layerNum));
    	h.put("ylocation", new Integer(vc.yLocation));
    	int flowNom;
    	int flowDenom = 1;
    	if (vc.flow.isUndefined())
    		flowNom = notDefined;
    	else
    	{	flowNom = vc.flow.nom;
    		flowDenom = vc.flow.denom;
    	}
    	h.put("flownom", new Integer(flowNom));
    	h.put("flowdenom", new Integer(flowDenom));
    	h.put("decimals", new Integer(vc.decimals));
    	h.put("root", new Boolean(vc.root));
    	h.put("tracefrom", new Boolean(vc.traceFrom));
    	h.put("labeltext", vc.labelText);
    	return h;
	}
	
	/**
	 * translate a HashMap into a VertexCopy
	 * @param map the HashMap to be Translated
	 * @return the VertexCopy
	 */
	public static VertexCopy vertexHashMapToVertexCopy(HashMap<String,Object> map)
	{
		ObjectMap vhm = JSONUtilities.wrapMap(map);
		
		int code = 0;
	    int layerNum = 0;
	    int yLocation = 0;
	    Rational flow = Rational.unDefined();
	    int flowNom = notDefined;
	    int flowDenom = 1;
	    int decimals = 2;
	    boolean root = false;  
	    boolean traceFrom = false; 
	    String labelText = "";

    	if (vhm.containsKey("code"))
    		code = vhm.getInt("code");
    	if (vhm.containsKey("layernum"))
    		layerNum = vhm.getInt("layernum");
    	if (vhm.containsKey("ylocation"))
    		yLocation = vhm.getInt("ylocation");
    	if (vhm.containsKey("flownom"))
    		flowNom = vhm.getInt("flownom");
    	if (vhm.containsKey("flowdenom"))
    		flowDenom = vhm.getInt("flowdenom");
    	if (flowNom != notDefined)
    		flow = new Rational(flowNom,flowDenom);
    	if (vhm.containsKey("decimals"))
    		decimals = vhm.getInt("decimals");
    	if (vhm.containsKey("root"))
    		root = vhm.getBoolean("root");
    	if (vhm.containsKey("tracefrom"))
    		traceFrom = vhm.getBoolean("tracefrom");
    	if (vhm.containsKey("labeltext"))
    		labelText = vhm.getString("labeltext");

    	VertexCopy vc = new VertexCopy(code, layerNum, yLocation, flow, decimals, root, labelText);
    	vc.traceFrom = traceFrom; // nodig? 
    	return vc;
    	
    	
	}
	
	/**
	 * translate an EdgeCopy into a HashMap
	 * @param ec the EdgeCopy to be translated
	 * @return the HashMap
	 */
	public static HashMap<String,Object> edgeCopyToEdgeHashMap(EdgeCopy ec)
	{
		HashMap<String,Object> h = new HashMap<String,Object>(); 

		HashMap<String,Object> fromVertexHM = vertexCopyToVertexHashMap(ec.fromVertexCopy);
    	h.put("fromvertexhm", fromVertexHM);
    	HashMap<String,Object> toVertexHM = vertexCopyToVertexHashMap(ec.toVertexCopy);
    	h.put("tovertexhm", toVertexHM);
    	h.put("capnom", new Integer(ec.capacity.nom));
    	h.put("capdenom", new Integer(ec.capacity.denom));
    	h.put("lasttimechanged", new Long(ec.lastTimeChanged));
    	h.put("mode", new Integer(ec.mode));
	
		return h;
	}	
		
	/**
	 * translate a HashMap into an EdgeCopy
	 * @param map the HashMap to be translated
	 * @return the EdgeCopy
	 */
	public static EdgeCopy edgeHashMapToEdgeCopy(HashMap<String,Object> map)
	{
		ObjectMap ehm = JSONUtilities.wrapMap(map);
		
		VertexCopy fromVertexCopy = null, toVertexCopy = null;
	    int capNom = 0;
	    int capDenom = 1;
		Rational capacity;
	    long lastTimeChanged = 0;
	    int mode = 0;

	    HashMap<String,Object> fromVertexHM = new HashMap<String,Object>();
    	if (ehm.containsKey("fromvertexhm"))
    		fromVertexHM = (HashMap<String,Object>) ehm.getMap("fromvertexhm");
    	fromVertexCopy = vertexHashMapToVertexCopy(fromVertexHM);
	    HashMap<String,Object> toVertexHM = new HashMap<String,Object>();
    	if (ehm.containsKey("tovertexhm"))
    		toVertexHM = (HashMap<String,Object>) ehm.getMap("tovertexhm");
    	toVertexCopy = vertexHashMapToVertexCopy(toVertexHM);
    	if (ehm.containsKey("capnom"))
    		capNom = ehm.getInt("capnom");
    	if (ehm.containsKey("capdenom"))
    		capDenom = ehm.getInt("capdenom");
    	capacity = new Rational(capNom, capDenom);
    	if (ehm.containsKey("lasttimechanged"))
    		lastTimeChanged = ehm.getInt("lasttimechanged");
    	if (ehm.containsKey("mode"))
    		mode = ehm.getInt("mode");
	    
	    EdgeCopy ec = new EdgeCopy(capacity, lastTimeChanged, mode);
	    ec.fromVertexCopy = fromVertexCopy;
	    ec.toVertexCopy = toVertexCopy;
	    return ec;
	    
	}
	
	/**
	 * translate a DiaghramCopy into a HashMap
	 * @param dc the DiagramCopy to be translated
	 * @return the HashMap
	 */
	public static HashMap<String,Object> diagramCopyToDiagramHashMap(DiagramCopy dc)
	{
		HashMap<String,Object> h = new HashMap<String,Object>();
		
		h.put("maxcode", new Integer(dc.maxCode));
		h.put("layerdistance", new Integer(dc.layerDistance));
		h.put("numlayers", new Integer(dc.numLayers));
	    h.put("breedte", new Integer(dc.breedte));
	    h.put("hoogte", new Integer(dc.hoogte));
	    h.put("flowmode", new Integer(dc.flowMode));
	    h.put("thickmode", new Integer(dc.thickMode));
	    h.put("labelheight", new Integer(dc.labelHeight));

	    ArrayList<HashMap<String,Object>> vertexArrayList = new ArrayList<HashMap<String,Object>>();
	    for (int vCnt = 0; vCnt < dc.vertexCopies.size(); vCnt++)
	    {	VertexCopy vc = (VertexCopy) dc.vertexCopies.elementAt(vCnt);
	    	HashMap <String,Object> vhm = vertexCopyToVertexHashMap(vc);
	    	vertexArrayList.add(vhm);
	    }
	    h.put("vertexarraylist", vertexArrayList);
	    
	    ArrayList<HashMap<String,Object>> edgeArrayList = new ArrayList<HashMap<String,Object>>();
	    for (int eCnt = 0; eCnt < dc.edgeCopies.size(); eCnt++)
	    {	EdgeCopy ec = (EdgeCopy) dc.edgeCopies.elementAt(eCnt);
	    	HashMap <String,Object> ehm = edgeCopyToEdgeHashMap(ec);
	    	edgeArrayList.add(ehm);
	    }
	    h.put("edgearraylist", edgeArrayList);
		
		return h;
	}
	
	/**
	 * translate a HashMap into a DiagramCopy
	 * @param map the HashMap to be translated
	 * @return the DiagramCopy
	 */
	public static DiagramCopy diagramHashMapToDiagramCopy(HashMap<String,Object> map)
	{
		ObjectMap dhm = JSONUtilities.wrapMap(map);
		
		int maxCode = 1;
		int layerDistance = 1;
	    int numLayers = 1;
	    int breedte = 10;
	    int hoogte = 10;
	    int flowMode = 0;
	    int thickMode = 0;
	    int labelHeight = 0;
	    boolean flowOn = false;
	    
	    List<Map<String,Object>> vertexArrayList = new ArrayList<Map<String,Object>>();
	    List<Map<String,Object>> edgeArrayList = new ArrayList<Map<String,Object>>();
		
    	if (dhm.containsKey("maxcode"))
    		maxCode = dhm.getInt("maxcode");
    	if (dhm.containsKey("layerdistance"))
    		layerDistance = dhm.getInt("layerdistance");
    	if (dhm.containsKey("numlayers"))
    		numLayers = dhm.getInt("numlayers");
    	if (dhm.containsKey("breedte"))
    		breedte = dhm.getInt("breedte");
    	if (dhm.containsKey("hoogte"))
    		hoogte = dhm.getInt("hoogte");
    	if (dhm.containsKey("flowmode"))
    		flowMode = dhm.getInt("flowmode");
    	if (dhm.containsKey("thickmode"))
    		thickMode = dhm.getInt("thickmode");
    	if (dhm.containsKey("labelheight"))
    		labelHeight = dhm.getInt("labelheight");
    	if (dhm.containsKey("flowon"))
    		flowOn = dhm.getBoolean("flowon");
    	if (dhm.containsKey("vertexarraylist"))
    		vertexArrayList = dhm.getMapList("vertexarraylist");
    	if (dhm.containsKey("edgearraylist"))
    		edgeArrayList = dhm.getMapList("edgearraylist");
		
		DiagramCopy dc = new DiagramCopy();
		dc.maxCode = maxCode;
		dc.layerDistance = layerDistance;
		dc.numLayers = numLayers;
		dc.breedte = breedte;
		dc.hoogte = hoogte;
		dc.flowMode = flowMode;
		dc.thickMode = thickMode;
		dc.labelHeight = labelHeight;
		for (int vCnt = 0; vCnt < vertexArrayList.size(); vCnt++)
		{	HashMap<String,Object> vhm = (HashMap<String,Object>) vertexArrayList.get(vCnt);
			VertexCopy vc = vertexHashMapToVertexCopy(vhm);
			dc.vertexCopies.addElement(vc);
		}
		for (int eCnt = 0; eCnt < edgeArrayList.size(); eCnt++)
		{	HashMap<String,Object> ehm = (HashMap<String,Object>) edgeArrayList.get(eCnt);
			EdgeCopy ec = edgeHashMapToEdgeCopy(ehm);
			dc.edgeCopies.addElement(ec);
		}
		return dc;
		
	}

}
