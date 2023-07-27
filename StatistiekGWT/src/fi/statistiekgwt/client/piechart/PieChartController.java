package fi.statistiekgwt.client.piechart;

import java.util.HashMap;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekView;

/**
 * MVC Controller for StatistiekView PieChart
 * 
 * @author Sylvia van Borkulo
 * 
 */
public class PieChartController implements StatistiekView
{
	private PieChartModel model;
	private PieChartView view;
	private int width;
	private int height;

	/**
	 * Constructor
	 * 
	 * @param tableModel
	 *            the data model
	 * @param viewName
	 *            the initial name of the view
	 */
	public PieChartController(StatTableModel tableModel, String viewName,
		int startVar, int width, int height)
	{
		this.model = new PieChartModel(tableModel, viewName);
		this.model.setColumnIndex(startVar);
		
		this.view = new PieChartView(this.model, this);
		
		// set size
		this.setWidth(width);
		this.setHeight(height);
		
		this.view.update();
	}
	
	public String getViewType()
	{
		return "Cirkeldiagram";
	}

	public Object getState()
	{
		HashMap h = new HashMap();

		h.put("viewName", this.getViewName());
		h.put("columnIndex", this.model.getColumnIndex());

		return h;
	}

	public void setState(Object state)
	{
		if (!(state instanceof HashMap))
		{
			return;
		}

		HashMap h = (HashMap) state;
		ObjectMap map = JSONUtilities.wrapMap(h);
		
		if (map.containsKey("viewName"))
		{
			this.setViewName(map.getString("viewName"));
		}
		if (map.containsKey("columnIndex"))
		{
			this.model.setColumnIndex(map.getInt("columnIndex"));
		}
	}

	public String getViewName()
	{
		return this.model.getViewName();
	}

	public void setViewName(String s)
	{
		this.model.setViewName(s);
	}

	@Override
	public void setUp(Frame owner)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUp(DialogBox owner)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * Return the widget containing the graphical representation
	 */
	@Override
	public Widget getWidget()
	{
		return this.view;
	}

	/**
	 * Remove all of this view's handler occurrences.
	 */
	@Override
	public void removeHandlers()
	{
		this.view.removeHandlers();
	}

	/**
	 * Get the view's width.
	 */
	public int getWidth()
	{
		return this.width;
	}
	
	/**
	 * Get the view's height.
	 */
	public int getHeight()
	{
		return this.height;
	}
	
	/**
	 * Set the view's width.
	 */
	public void setWidth(int w)
	{
		this.width = w;
		this.view.setWidth(w);
	}
	
	/**
	 * Set the view's height.
	 */
	public void setHeight(int h)
	{
		this.height = h;
		this.view.setHeight(h);
	}

	/**
	 * Update the view.
	 */
	@Override
	public void update()
	{
		this.view.update();
	}

//	@Override
//	public void setEditable(boolean editable)
//	{
//		this.view.setEditable(editable);
//	}
}
