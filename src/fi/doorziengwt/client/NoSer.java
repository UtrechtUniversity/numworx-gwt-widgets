package fi.doorziengwt.client;

import java.util.*;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

//import fi.beans.base64code.StringCodeObject;

public class NoSer 
{
	//public static double[] getVectorDState(Vector3D vec)
	public static List<Double> getVectorDState(Vector3D vec)
	{
		//double[] coeff = new double[9];
		List<Double> coeff = new ArrayList<Double>();
		//coeff[0] = vec.x;
		//coeff[1] = vec.y;
		//coeff[2] = vec.z;
		coeff.add(new Double(vec.x));
		coeff.add(new Double(vec.y));
		coeff.add(new Double(vec.z));
		
		return coeff;
		
	}

	//public static Vector3D setVector3DState(double[] coeff)
	public static Vector3D setVector3DState(List<Double> coeff)
	{
		Vector3D vec = new Vector3D();
		
		//if (coeff.length >= 3)
		//	vec = new Vector3D(coeff[0], coeff[1], coeff[2]);
		if (coeff.size() >= 3)
		{
			double vecx = ((Double) coeff.get(0)).doubleValue();
			double vecy = ((Double) coeff.get(1)).doubleValue();
			double vecz = ((Double) coeff.get(2)).doubleValue();
			
			vec = new Vector3D(vecx, vecy, vecz);
		}
		
		
		return vec;
	}
	
	
	//public static double[] getMatrix3DState(Matrix3D mat)
	public static List<Double> getMatrix3DState(Matrix3D mat)
	{
		//double[] coeff = new double[9];
		List<Double> coeff = new ArrayList<Double>();
		//coeff[0] = mat.row1.x;
		//coeff[1] = mat.row1.y;
		//coeff[2] = mat.row1.z;
		//coeff[3] = mat.row2.x;
		//coeff[4] = mat.row2.y;
		//coeff[5] = mat.row2.z;
		//coeff[6] = mat.row3.x;
		//coeff[7] = mat.row3.y;
		//coeff[8] = mat.row3.z;
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
	
	public static Matrix3D setMatrix3DState(List<Double> coeff)
	{
		Matrix3D mat = new Matrix3D(0, 0, 0, 0, 0, 0, 0, 0, 0);
		
		//if (coeff.length >= 9)
		//	mat = new Matrix3D(coeff[0], coeff[1], coeff[2], 
		//			           coeff[3], coeff[4], coeff[5], 
		//			           coeff[6], coeff[7], coeff[8]);
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

	//public static double[] getLine3DState(Line3D li)
	public static List<Double> getLine3DState(Line3D li)
	{
		//double[] params = new double[6];
		List<Double> params = new ArrayList<Double>();
		
		//params[0] = li.point1.x;
		//params[1] = li.point1.y;
		//params[2] = li.point1.z;
		//params[3] = li.point2.x;
		//params[4] = li.point2.y;
		//params[5] = li.point2.z;
		
		params.add(new Double(li.point1.x));
		params.add(new Double(li.point1.y));
		params.add(new Double(li.point1.z));
		params.add(new Double(li.point2.x));
		params.add(new Double(li.point2.y));
		params.add(new Double(li.point2.z));
		
		
		return params;
	}

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

	//public static double[] getPlane3DState(Plane3D pl)
	public static List<Double> getPlane3DState(Plane3D pl)
	{
		//double[] params = new double[9];
		List<Double> params = new ArrayList<Double>();
		
		Vector3D v1 = new Vector3D(pl.support);
		Vector3D v2 = Vector3D.plus(pl.direction1, v1);
		Vector3D v3 = Vector3D.plus(pl.direction2, v1);
		
//		params[0] = v1.x;
//		params[1] = v1.y;
//		params[2] = v1.z;
//		params[3] = v2.x;
//		params[4] = v2.y;
//		params[5] = v2.z;
//		params[6] = v3.x;
//		params[7] = v3.y;
//		params[8] = v3.z;

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
	public static List<Double> getVerticesState(Vector3D[] vertices)
	{
		//double[] vert = new double[3 * vertices.length];
		List<Double> vert = new ArrayList<Double>();
		
//		for (int vCnt = 0; vCnt < vertices.length; vCnt++)
//		{	vert[3 * vCnt] = vertices[vCnt].x;
//			vert[3 * vCnt + 1] = vertices[vCnt].y;
//			vert[3 * vCnt + 2] = vertices[vCnt].z;
//		}

		for (int vCnt = 0; vCnt < vertices.length; vCnt++)
		{	vert.add(vertices[vCnt].x);
			vert.add(vertices[vCnt].y);
			vert.add(vertices[vCnt].z);
		}
		
		return vert;
	}
	
	public static Vector3D[] setVerticesState(List<Double> vert)
	{
		//Vector3D[] vertices = new Vector3D[vert.length / 3];
		Vector3D[] vertices = new Vector3D[vert.size() / 3];
		
		for (int vCnt = 0; vCnt < vertices.length; vCnt++)
		{	
			//vertices[vCnt] = new Vector3D(vert[3 * vCnt], vert[3 * vCnt + 1], vert[3 * vCnt + 2]);
			vertices[vCnt] = new Vector3D( ((Double) vert.get(3 * vCnt)).doubleValue(),
										   ((Double) vert.get(3 * vCnt + 1)).doubleValue(),
										   ((Double) vert.get(3 * vCnt + 2)).doubleValue());
		} 
		
		return vertices;
	}
	
	public static Map<String,Object> getFacet3DState(Facet3D facet)
	{
		//Hashtable h = new Hashtable();
		Map<String,Object> h = new HashMap<String,Object>();
		
		//int[] indices = facet.indices;
		//String[] vertexLabels = facet.vertexLabels;
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
	
	public static List<Double> getFacet3DVertexState(Facet3D facet)
	{
		List<Double> vert = getVerticesState(facet.points);
		
		return vert;
	}
	
	public static Facet3D setFacet3DState(Map<String,Object> map, Vector3D[] vertices)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		//int[] indices = new int[0];
		//String[] vertexLabels = new String[0];
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
	
	//public static Facet3D setFacet3DVertexState(double[] vertices)
	public static Facet3D setFacet3DVertexState(List<Double> vertices)
	{
		Vector3D[] points = setVerticesState(vertices);
		
		int[] indices = new int[points.length];
		for (int iCnt = 0; iCnt < indices.length; iCnt++)
			indices[iCnt] = iCnt;
		
		return new Facet3D(points, indices, DrawConstants.objectColor);
	}
	
	
	public static HashMap<String,Object> getObject3DState(Object3D object)
	{
		HashMap<String,Object> h = new HashMap<String,Object>();
		
		int numVertices = object.numVertices;
		//double[] vertices = getVerticesState(object.vertices);
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

//System.out.println("get numVertices " + numVertices);
//System.out.println("get vertstate " + vertices.length);
		
		h.put("numVertices", new Integer(numVertices));
		h.put("vertices", vertices);
		h.put("numVertexLabels", new Integer(numVertexLabels));
		h.put("vertexLabels", vertexLabels);

		int numFacets = object.numFacets;
		//Hashtable[] facets = new Hashtable[numFacets];
		//for (int fCnt = 0; fCnt < numFacets; fCnt++)
		//	facets[fCnt] = getFacet3DState(object.facets[fCnt]);
		List<Map<String,Object>> facets = new ArrayList<Map<String,Object>>();
		for (int fCnt = 0; fCnt < numFacets; fCnt++)
			facets.add(getFacet3DState(object.facets[fCnt]));

//System.out.println("get numFacets " + numFacets);
//System.out.println("get facstate " + facets.length);
		
		h.put("numFacets", new Integer(numFacets));
		h.put("facets", facets);

        boolean centerSet = object.centerSet;
        boolean diamSet = object.diamSet;
        double diameter = object.diameter;
        //double[] center = getVectorDState(object.center);
        List<Double> center = getVectorDState(object.center);
        int modelCode = object.modelCode;
		
        h.put("centerSet", new Boolean(centerSet));
        h.put("diamSet", new Boolean(diamSet));
        h.put("diameter", new Double(diameter));
        h.put("center", center);
        h.put("modelCode", new Integer(modelCode));
        
		return h;
	}
	
	public static Object3D setObject3DState(HashMap<String,Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		Object3D object = new EmptyObject3D();
		
		int numVertices = 0;
		//double[] vertices = new double[0];
		List<Double> vertices = new ArrayList<Double>(); 
		int numVertexLabels = 0;
		//String[] vertexLabels = new String[0];
		List<String> vertexLabels = new ArrayList<String>(); 
		
		if (h.containsKey("numVertices"))
			numVertices = h.getInt("numVertices");
		if (h.containsKey("vertices"))
			vertices = h.getDoubleList("vertices");
		if (h.containsKey("numVertexLabels"))
			numVertexLabels = h.getInt("numVertexLabels");
		if (h.containsKey("vertexLabels"))
			vertexLabels = h.getStringList("vertexLabels");

//System.out.println("set numVertices " + numVertices);
//System.out.println("set vertstate " + vertices.length);
		
		int numFacets = 0;
		//Hashtable[] facets = new Hashtable[0];
		List<Map<String,Object>> facets = new ArrayList<Map<String,Object>>();  
		
		if (h.containsKey("numFacets"))
			numFacets = h.getInt("numFacets");
		if (h.containsKey("facets"))
			facets = h.getMapList("facets");

//System.out.println("set numFacets " + numFacets);
//System.out.println("set facstate " + facets.length);
		
		
		boolean centerSet = false;
		boolean diamSet = false;
		double diameter = 0;
		//double[] center = new double[0];
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
		
//System.out.println("set vertices " + object.vertices.length);
		
		object.numFacets = numFacets;
		object.facets = new Facet3D[numFacets];
		for (int fCnt = 0; fCnt < numFacets; fCnt++)
		{	//object.facets[fCnt] = setFacet3DState(facets[fCnt], object.vertices);
			object.facets[fCnt] = setFacet3DState((Map<String,Object>) facets.get(fCnt), object.vertices);
//System.out.println("facets " + fCnt);			
		}

//System.out.println("set facets " + object.facets.length);

		object.centerSet = centerSet;
		object.diamSet = diamSet;
		object.diameter = diameter;
		object.center = setVector3DState(center);
		object.modelCode = modelCode;
		
		return object;
	}
	
	public static Vector getConstructionState(Vector construction)
	{
		Vector conState = new Vector();
		
		for (int cCnt = 0; cCnt < construction.size(); cCnt++)
		{
			Object o = construction.elementAt(cCnt);
			
			if (o instanceof Line3D)
			{	Line3D line3D = (Line3D) o;
				//double[] line = getLine3DState(line3D);
				List<Double> line = getLine3DState(line3D);
				conState.addElement(line);
			}
			else if (o instanceof Plane3D)
			{	Plane3D plane3D = (Plane3D) o;
				//double[] plane = getPlane3DState(plane3D);
				List<Double> plane = getPlane3DState(plane3D);
				conState.addElement(plane);
				
			}
				
		}
		
		
		return conState;
	}
	
	public static Vector setConstructionState(Vector conState)
	{
		Vector construction = new Vector();
		
		for (int cCnt = 0; cCnt < conState.size(); cCnt++)
		{
			//double[] instruct = (double[]) conState.elementAt(cCnt);
			List<Double> instruct = (ArrayList<Double>) conState.elementAt(cCnt);
			
			//if (instruct.length == 6)
			if (instruct.size() == 6)
			{	Line3D line3D = setLine3DState(instruct);
				construction.addElement(line3D);
			}
			//else if (instruct.length == 9)
			else if (instruct.size() == 9)
			{	Plane3D plane3D = setPlane3DState(instruct);
				construction.addElement(plane3D);
				
			}
		}
		
		return construction;
	}

	
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
