package fi.statistiekgwt.client;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

/**
 * Interface for statistical representations.
 * Please note that in general a representation's controller
 * implements the StatistiekView interface.
 *  
 * @author Manu Drijvers, Sylvia van Borkulo
 *
 */
public interface StatistiekView {
	
	/**
	 * Set up this representation with a Frame as owner
	 * @param owner Frame that can be used as owner of a Dialog
	 */
	public void setUp(Frame owner);
	
	/**
	 * Set up this representation with a Dialog as owner
	 * @param owner Dialog that can be used as owner of a Dialog
	 */
	public void setUp(DialogBox owner);
	
	/**
	 * Get the visual component
	 * @return the JComponent containing the view
	 */
	public Widget getWidget();
	
	/**
	 * Get the type of this view
	 * @return the type of this view
	 */
	public String getViewType();
	
	/**
	 * set a new viewName
	 * @param s the new viewName
	 */
	public void setViewName(String s);
	
	/**
	 * Get the view state
	 * @return Object containing the view's state
	 */
	public Object getState();
	
	/**
	 * Restore the view's state
	 * @param state an old state
	 */
	public void setState(Object state);
	
	/**
	 * Get this view's name
	 * @return this view's name
	 */
	public String getViewName();
	
	/**
	 * Remove all of this view's handler occurrences.
	 */
	public void removeHandlers();

	/**
	 * Get the views width.
	 */
	public int getWidth();
	
	/**
	 * Get the views height.
	 */
	public int getHeight();
	
	/**
	 * Set the views width.
	 */
	public void setWidth(int w);
	
	/**
	 * Set the views height.
	 */
	public void setHeight(int h);
	
	/**
	 * Update the view. Used to update table view after switch in tablayoutpanel
	 * in statinteractiepanel. Views there are only accessible as statistiekViews.
	 */
	public void update();
}