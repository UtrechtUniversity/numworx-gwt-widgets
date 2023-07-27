package fi.doorziengwt.client;

import java.util.*;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;

/**
 * utility class facilitating saving and retrieving an Object3D in a 
 * non-serializable way; note that all methods are static 
 * @author huub
 */

public class NoSer 
{
	/**
	 * convert a Vector3D to a List of 3 Doubles
	 * @param vec Vector3D to be converted
	 * @return List of 3 Doubles
	 */
	public static List<Double> getVectorDState(Vector3D vec)
	{
		List<Double> coeff = new ArrayList<Double>();
		coeff.add(new Double(vec.x));
		coeff.add(new Double(vec.y));
		coeff.add(new Double(vec.z));
		
		return coeff;
		
	}

	/**
	 * convert a List of 3 Doubles to a Vector3D
	 * @param coeff List of 3 Doubles to be converted
	 * @return Vector3D
	 */
	public static Vector3D setVector3DState(List<Double> coeff)
	{
		Vector3D vec = new Vector3D();
		
		if (coeff.size() >= 3)
		{
			double vecx = ((Double) coeff.get(0)).doubleValue();
			double vecy = ((Double) coeff.get(1)).doubleValue();
			double vecz = ((Double) coeff.get(2)).doubleValue();
			
			vec = new Vector3D(vecx, vecy, vecz);
		}
		
		
		return vec;
	}
	
	
	/**
	 * convert a Matrix3D to a List of 9 Doubles
	 * @param mat Matrix3D to be converted
	 * @return List of 9 Doubles
	 */
	public static List<Double> getMatrix3DState(Matrix3D mat)
	{

		List<Double> coeff = new ArrayList<Double>();
		coeff.add(new Double(mat.row1.x));
		coeff.add(new Double(mat.row1.y));
		coeff.add(new Double(mat.row1.z));
		coeff.add(new Double(mat.row2.x));
		coeff.add(new Double(mat.row2.y));
		coeff.add(new Double(mat.row2.z));
		coeff.add(new Double(mat.row3.x));
		coeff.add(new Double(mat.row3.y));
		coeff.add(new Double(mat.row3.z));
		
		return coeff;
	}

	/**
	 * convert a List of 9 Doubles to a Matrix3D
	 * @param coeff List of 9 Doubles
	 * @return Matrix3D
	 */
	public static Matrix3D setMatrix3DState(List<Double> coeff)
	{
		Matrix3D mat = new Matrix3D(0, 0, 0, 0, 0, 0, 0, 0, 0);
		
		if (coeff.size() >= 9)
		{
			double c0 = ((Double) coeff.get(0)).doubleValue();
			double c1 = ((Double) coeff.get(1)).doubleValue();
			double c2 = ((Double) coeff.get(2)).doubleValue();
			double c3 = ((Double) coeff.get(3)).doubleValue();
			double c4 = ((Double) coeff.get(4)).doubleValue();
			double c5 = ((Double) coeff.get(5)).doubleValue();
			double c6 = ((Double) coeff.get(6)).doubleValue();
			double c7 = ((Double) coeff.get(7)).doubleValue();
			double c8 = ((Double) coeff.get(8)).doubleValue();
			
			mat = new Matrix3D(c0, c1, c2, c3, c4, c5, c6, c7, c8);
		}
		
		
		return mat;
	}

	/**
	 * convert a Line3D to a List of 6 Doubles (coordinates of two points on the Line3D) 
	 * @param li Line3D to be converted
	 * @return List of 6 Doubles
	 */
	public static List<Double> getLine3DState(Line3D li)
	{
		List<Double> params = new ArrayList<Double>();
		
		params.add(new Double(li.point1.x));
		params.add(new Double(li.point1.y));
		params.add(new Double(li.point1.z));
		params.add(new Double(li.point2.x));
		params.add(new Double(li.point2.y));
		params.add(new Double(li.point2.z));
		
		return params;
	}

	/**
	 * convert a List of 6 Doubles to a Line3D (Doubles are the coordinates of two points on the Line3D)
	 * @param params List of 6 Doubles
	 * @return Line3D
	 */
	public static Line3D setLine3DState(List<Double> params)
	{
		double p0 = ((Double) params.get(0)).doubleValue();
		double p1 = ((Double) params.get(1)).doubleValue();
		double p2 = ((Double) params.get(2)).doubleValue();
		double p3 = ((Double) params.get(3)).doubleValue();
		double p4 = ((Double) params.get(4)).doubleValue();
		double p5 = ((Double) params.get(5)).doubleValue();
		
		Vector3D vec1 = new Vector3D(p0, p1, p2);
		Vector3D vec2 = new Vector3D(p3, p4, p5);
		
		return new Line3D(vec1, vec2);
	}

	/**
	 * convert a Plane3D to a List of 9 Doubles (the coordinates of the 
	 * support vector and the two direction vectors, see class Plane3D)
	 * @param pl Plane3D to be converted
	 * @return List of 9 Doubles
	 */
	public static List<Double> getPlane3DState(Plane3D pl)
	{
		List<Double> params = new ArrayList<Double>();
		
		Vector3D v1 = new Vector3D(pl.support);
		Vector3D v2 = Vector3D.plus(pl.direction1, v1);
		Vector3D v3 = Vector3D.plus(pl.direction2, v1);
		
		params.add(new Double(v1.x));
		params.add(new Double(v1.y));
		params.add(new Double(v1.z));
		params.add(new Double(v2.x));
		params.add(new Double(v2.y));
		params.add(new Double(v2.z));
		params.add(new Double(v3.x));
		params.add(new Double(v3.y));
		params.add(new Double(v3.z));
		
		return params;
	}

	/**
	 * convert a List of 9 Doubles to a Plane3D (Doubles are the coordinates
	 * of the support and the two direction vectors)
	 * @param params List of 9 Doubles to be converted
	 * @return Plane3D
	 */
	public static Plane3D setPlane3DState(List<Double> params)
	{
		double p0 = ((Double) params.get(0)).doubleValue();
		double p1 = ((Double) params.get(1)).doubleValue();
		double p2 = ((Double) params.get(2)).doubleValue();
		double p3 = ((Double) params.get(3)).doubleValue();
		double p4 = ((Double) params.get(4)).doubleValue();
		double p5 = ((Double) params.get(5)).doubleValue();
		double p6 = ((Double) params.get(6)).doubleValue();
		double p7 = ((Double) params.get(7)).doubleValue();
		double p8 = ((Double) params.get(8)).doubleValue();
		
		Vector3D vec1 = new Vector3D(p0, p1, p2);
		Vector3D vec2 = new Vector3D(p3, p4, p5);
		Vector3D vec3 = new Vector3D(p6, p7, p8);
		
		return new Plane3D(vec1, vec2, vec3);
	}
	
	//public static double[] getVerticesState(Vector3D[] vertices)
	/**
	 * convert an array of Vector3D to a List of Doubles (where the length
	 * of the List is 3 times the length of the array)    
	 * @param vertices array of Vector3D to be converted
	 * @return List of Doubles 
	 */
	public static List<Double> getVerticesState(Vector3D[] vertices)
	{
		List<Double> vert = new ArrayList<Double>();
		
		for (int vCnt = 0; vCnt < vertices.length; vCnt++)
		{	vert.add(vertices[vCnt].x);
			vert.add(vertices[vCnt].y);
			vert.add(vertices[vCnt].z);
		}
		
		return vert;
	}
	
	/**
	 * convert a List of Doubles to an array of Vector3D, where the length of
	 * the array is one-third of the length of the List)
	 * @param vert List of Doubles to be converted
	 * @return array of Vector3D
	 */
	public static Vector3D[] setVerticesState(List<Double> vert)
	{
		Vector3D[] vertices = new Vector3D[vert.size() / 3];
		
		for (int vCnt = 0; vCnt < vertices.length; vCnt++)
		{	
			vertices[vCnt] = new Vector3D( ((Double) vert.get(3 * vCnt)).doubleValue(),
										   ((Double) vert.get(3 * vCnt + 1)).doubleValue(),
										   ((Double) vert.get(3 * vCnt + 2)).doubleValue());
		} 
		
		return vertices;
	}

	/**
	 * convert a Facet3D to a Map containing a List with the vertex-indices
	 * and a List with the vertex-labels 
	 * @param facet Facet3D to be converted
	 * @return Map 
	 */
	public static Map<String,Object> getFacet3DState(Facet3D facet)
	{
		Map<String,Object> h = new HashMap<String,Object>();
		
		List<Integer> indices = new ArrayList<Integer>();
		List<String> vertexLabels = new ArrayList<String>();
		for (int pCnt = 0; pCnt < facet.numPoints; pCnt++)
		{
			indices.add(new Integer(facet.indices[pCnt]));
			if (facet.vertexLabels[pCnt] == null)
				vertexLabels.add(new String(""));
			else
				vertexLabels.add(new String(facet.vertexLabels[pCnt]));
		}
		
		
		h.put("indices", indices);
		h.put("vertexLabels", vertexLabels);
		
		return h;
	}
	
	/**
	 * convert the vertex-array if a Facet3D to a List of Doubles;
	 * used for saving the startFacet of a foldout
	 * @param facet Facet3D whose vertex-array should be converted
	 * @return List of Doubles
	 */
	public static List<Double> getFacet3DVertexState(Facet3D facet)
	{
		List<Double> vert = getVerticesState(facet.points);
		
		return vert;
	}
	
	/**
	 * reconstruct a Facet3D form a Map containing a List with the vertex-indices
	 * and a List with the vertex-labels, and an array of vertices  
	 * @param map Map containing a List with the vertex-indices
	 * and a List with the vertex-labels
	 * @param vertices array of Vector3D (the vertices)
	 * @return Facet3D
	 */
	public static Facet3D setFacet3DState(Map<String,Object> map, Vector3D[] vertices)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		List<Integer> indices = new ArrayList<Integer>();
		List<String> vertexLabels = new ArrayList<String>();
		
		if (h.containsKey("indices"))
			indices = h.getIntegerList("indices"); 
		if (h.containsKey("vertexLabels"))
			vertexLabels = h.getStringList("vertexLabels");
		
		int[] indicesArray = new int[indices.size()];
		String[] vertexLabelsArray = new String[vertexLabels.size()];
		for (int iCnt = 0; iCnt < indices.size(); iCnt++)
		{	indicesArray[iCnt] = ((Number) indices.get(iCnt)).intValue();
			vertexLabelsArray[iCnt] = (String) vertexLabels.get(iCnt);
		}
		
		Facet3D facet = new Facet3D(vertices, indicesArray, DrawConstants.objectColor);
		
		facet.vertexLabels = vertexLabelsArray;
		
		return facet;
	}
	

	/**
	 * convert a List of Doubles to an array of Vector3D and use these
	 * as vertices of a Facet3D; used for retrieving the startFacet
	 * of a foldout
	 * @param vertices List of Doubles to be converted
	 * @return Facet3D
	 */
	public static Facet3D setFacet3DVertexState(List<Double> vertices)
	{
		Vector3D[] points = setVerticesState(vertices);
		
		int[] indices = new int[points.length];
		for (int iCnt = 0; iCnt < indices.length; iCnt++)
			indices[iCnt] = iCnt;
		
		return new Facet3D(points, indices, DrawConstants.objectColor);
	}
	
	/**
	 * convert an Object3D to a Map containing: <br>
	 * the number of vertices, the vertex-array coverted to a List of Doubles,
	 * the number of vertex labels, the vertex-label-array converted to a List of String,
	 * the number of facets, the facet array converted to a List of Maps
	 * various parameters related to center and diameter   
	 * @param object Object3D to be converted
	 * @return Map
	 */
	public static HashMap<String,Object> getObject3DState(Object3D object)
	{
		HashMap<String,Object> h = new HashMap<String,Object>();
		
		int numVertices = object.numVertices;
		List<Double> vertices = getVerticesState(object.vertices);
		int numVertexLabels = object.numVertexLabels;
		String[] vertexLabelsArray = object.vertexLabels;
		List<String> vertexLabels = new ArrayList<String>();
		for (int lCnt = 0; lCnt < vertexLabelsArray.length; lCnt++)
		{	
			if (vertexLabelsArray[lCnt] == null)
				vertexLabels.add(new String(""));
			else
				vertexLabels.add(new String(vertexLabelsArray[lCnt]));
					
		}

		h.put("numVertices", new Integer(numVertices));
		h.put("vertices", vertices);
		h.put("numVertexLabels", new Integer(numVertexLabels));
		h.put("vertexLabels", vertexLabels);

		int numFacets = object.numFacets;
		List<Map<String,Object>> facets = new ArrayList<Map<String,Object>>();
		for (int fCnt = 0; fCnt < numFacets; fCnt++)
			facets.add(getFacet3DState(object.facets[fCnt]));
		
		h.put("numFacets", new Integer(numFacets));
		h.put("facets", facets);

        boolean centerSet = object.centerSet;
        boolean diamSet = object.diamSet;
        double diameter = object.diameter;
        List<Double> center = getVectorDState(object.center);
        int modelCode = object.modelCode;
		
        h.put("centerSet", new Boolean(centerSet));
        h.put("diamSet", new Boolean(diamSet));
        h.put("diameter", new Double(diameter));
        h.put("center", center);
        h.put("modelCode", new Integer(modelCode));
        
		return h;
	}

	/**
	 * convert a Map (contents see method getObject3DState) to an Object3D
	 * @param map Map to be converted
	 * @return Object3D
	 */
	public static Object3D setObject3DState(Map<String,Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		Object3D object = new EmptyObject3D();
		
		int numVertices = 0;
		List<Double> vertices = new ArrayList<Double>(); 
		int numVertexLabels = 0;
		List<String> vertexLabels = new ArrayList<String>(); 
		
		if (h.containsKey("numVertices"))
			numVertices = h.getInt("numVertices");
		if (h.containsKey("vertices"))
			vertices = h.getDoubleList("vertices");
		if (h.containsKey("numVertexLabels"))
			numVertexLabels = h.getInt("numVertexLabels");
		if (h.containsKey("vertexLabels"))
			vertexLabels = h.getStringList("vertexLabels");

		int numFacets = 0;
		List<Map<String,Object>> facets = new ArrayList<Map<String,Object>>();  
		
		if (h.containsKey("numFacets"))
			numFacets = h.getInt("numFacets");
		if (h.containsKey("facets"))
			facets = h.getMapList("facets");
		
		boolean centerSet = false;
		boolean diamSet = false;
		double diameter = 0;
		List<Double> center = new ArrayList<Double>();  
		int modelCode = 0;
		
		if (h.containsKey("centerSet"))
			centerSet = h.getBoolean("centerSet");
		if (h.containsKey("diamSet"))
			diamSet = h.getBoolean("diamSet");
		if (h.containsKey("diameter"))
			diameter = h.getDouble("diameter");
		if (h.containsKey("center"))
			center = h.getDoubleList("center");
		if (h.containsKey("modelCode"))
			modelCode = h.getInt("modelCode");

		String[] vertexLabelsArray = new String[vertexLabels.size()];
		for (int iCnt = 0; iCnt < vertexLabels.size(); iCnt++)
		{	vertexLabelsArray[iCnt] = (String) vertexLabels.get(iCnt);
		}
		
		object.numVertices = numVertices;
		object.vertices = setVerticesState(vertices);
		object.trVertices = new Vector3D[numVertices];
		object.numVertexLabels = numVertexLabels;
		object.vertexLabels = vertexLabelsArray;
		
		object.numFacets = numFacets;
		object.facets = new Facet3D[numFacets];
		for (int fCnt = 0; fCnt < numFacets; fCnt++)
		{	
			object.facets[fCnt] = setFacet3DState((Map<String,Object>) facets.get(fCnt), object.vertices);
		
		}

		object.centerSet = centerSet;
		object.diamSet = diamSet;
		object.diameter = diameter;
		object.center = setVector3DState(center);
		object.modelCode = modelCode;
		
		return object;
	}

	/**
	 * convert a Vector containing the contruction of an ObjectGroup3D (the elements of such construction are
	 * Lines3D's and Plane3D's, see class ObjectWithPlane) to a List (of Object) by converting the Lines3D's and Plane3D's,
	 * to Lists of Doubles   
	 * @param construction construction Vector to be converted
	 * @return List of Object
	 */
	public static List<Object> getConstructionState(Vector construction)
	{
		List<Object> conState = new ArrayList<Object>();
		
		for (int cCnt = 0; cCnt < construction.size(); cCnt++)
		{
			Object o = construction.elementAt(cCnt);
			
			if (o instanceof Line3D)
			{	Line3D line3D = (Line3D) o;
				List<Double> line = getLine3DState(line3D);
				conState.add(line);
			}
			else if (o instanceof Plane3D)
			{	Plane3D plane3D = (Plane3D) o;
				List<Double> plane = getPlane3DState(plane3D);
				conState.add(plane);
				
			}
				
		}
		return conState;
	}
	
	/**
	 * convert a List of Object (each Object being a List of Doubles representing a Line3D or a Plane3D)
	 * to a Vector containing these Lines3D's and Plane3D's  
	 * @param l List of Object to be converted
	 * @return construction Vector
	 */
	public static Vector setConstructionState(List<Object> l)
	{
		Vector construction = new Vector();
		
		ObjectList conState = JSONUtilities.wrapList(l);
		
		for (int cCnt = 0; cCnt < conState.size(); cCnt++)
		{
			List<Double> instruct = conState.getDoubleList(cCnt);
			
			if (instruct.size() == 6)
			{	Line3D line3D = setLine3DState(instruct);
				construction.addElement(line3D);
			}
			else if (instruct.size() == 9)
			{	Plane3D plane3D = setPlane3DState(instruct);
				construction.addElement(plane3D);
				
			}
		}
		
		return construction;
	}


	/**
	 * check if Object3D o contains Facet3D f
	 * @param o Object3D to check
	 * @param f Facet3D to check
	 * @return true/false
	 */
	public static int containsFacet(Object3D o, Facet3D f)
    {   o.fixFacetArray();
        int result = -1;
        for (int i = 0; i < o.numFacets; i++)
        {   if (Facet3D.isEqualTo(o.facets[i], f) >= 0)
               return i;
        }
        return result;
    }

	
}	
