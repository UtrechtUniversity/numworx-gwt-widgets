package fi.statistiekgwt.client;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

/**
 * Interface for statistical representations
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
}