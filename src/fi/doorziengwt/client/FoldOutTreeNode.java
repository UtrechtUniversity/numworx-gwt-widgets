package fi.doorziengwt.client;

import java.util.Vector;

/**
 * class to keep track of the foldout of an object; note how this works: <br>
 * the foldout is a tree of facets with root the facet functioning as center of the foldout;
 * each facet in the foldout is put into a node of this tree, represented by an instance
 * of FoldOutTreeNode; each FoldOutTreeNode has a parent node (a facet to which it is connected)
 * and a Vector of child nodes (a list of facets which are connected to the facet in the 
 * FoldOutTreeNode; it is also necessary to keep track of the facets in the subtree with root the facet
 * in the FoldOutTreeNode: this subtree also contains all visible facets replacing a facet
 * of the subtree, in order to correctly make a foldout of an object which has been cut by
 * lines or planes; the facet in the FoldOutTreeNode is connected to the facet in the parent
 * FoldOutTreeNode (if any) along a directed edge which is also the rotation axis during folding out,
 * so remember start and end vertex of this edge; also remember the minimum and current angle
 * of the facet in the FoldOutTreeNode and the facet in the parent FoldOutTreeNode.
 * @author huub
 */

public class FoldOutTreeNode 
{
	/**
	 * parent node (root of the tree has none)
	 */
    FoldOutTreeNode parentNode = null;
    /**
     * child nodes
     */
    Vector childNodes = new Vector();
    /**
     * the facet represented by the node
     */
    Facet3D facet;
    /**
     * the fold out component, that is, all facets in the subtree 
     * with root this facet, including all visible facets replacing
     * these facets 
     */
    Vector foldOutFacets = new Vector();
    /**
     * the minimum foldout angle
     */
    double minAngle;
    /**
     * the current foldout angle (relative to minAngle) 
     */
    double currentAngle; 
    /**
     * indices of start and end vertices of the edge forming the 
     * common rotation axis with the facet in the parent FoldOutTreeNode    
     */
    int axisFrom, axisTo;

    /**
     * level in the foldout tree
     */
    int level;
    
    /**
     * constructor
     * @param f facet represented by this FoldOutTreeNode
     * @param angle angle with facet in parent FoldOutTreeNode
     * @param aFrom start vertex of edge common with facet in parent FoldOutTreeNode 
     * @param aTo end vertex of edge common with facet in parent FoldOutTreeNode
     */
    public FoldOutTreeNode(Facet3D f, double angle, int aFrom, int aTo)
    {   facet = f;
        minAngle = angle;
        axisFrom = aFrom;
        axisTo = aTo;
    }
    
    
    
}

