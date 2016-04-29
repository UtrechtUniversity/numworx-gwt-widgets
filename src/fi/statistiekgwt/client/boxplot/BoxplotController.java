package fi.statistiekgwt.client.boxplot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekView;
import fi.statistiekgwt.client.types.AllowedTypes;

/**
 * 
 * MVC Controller for statistiekview Boxplot
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class BoxplotController implements StatistiekView
{
	private BoxplotView view;
	private BoxplotModel model;
	private int width;
	private int height;

	/**
	 * Constructor
	 * 
	 * @param tableModel
	 *            the tablemodel
	 * @param viewName
	 *            the name of this view
	 * @param startVar
	 * 		The index of the variable
	 * @param width
	 * 		The width of the boxplot
	 * @param height
	 * 		The height of the boxplot
	 */
	public BoxplotController(StatTableModel tableModel, String viewName,
		int startVar, int width, int height)
	{
		this.model = new BoxplotModel(tableModel, viewName);
		this.model.setColumnIndex(startVar);
		
		this.view = new BoxplotView(this.model, this);
		
		// set size
		this.setWidth(width);
		this.setHeight(height);

		this.view.update();
	}

	void setSplitType(AllowedTypes type)
	{
		if (type.isNumber())
		{
			ArrayList<Double> boundaries = new ArrayList<Double>();
			boundaries = StatistiekGWT.appropriateBoundaries(
				this.model.getStatTableModel().getColumnMin(
					this.model.getSplitOptions().getColumnSplitIndex()),
				this.model.getStatTableModel().getColumnMax(
					this.model.getSplitOptions().getColumnSplitIndex()),
				this.view.getSplitBinsBoxSelectedInt());

			this.model.setSplitBinBoundaries(boundaries);
			this.model.setSplitOptions(this.model.getSplitOptions());
			this.view.setModel(this.model);
		}

	}

	/*
	 * Update the split bin boundaries using the settings for the minimum boundary
	 * and the bin width, and determine the number of bins.
	 */
	public void updateSplitBoundariesFromBinSettings()
	{
		ArrayList<Double> boundaries = new ArrayList<Double>();

		double min = this.model.getStatTableModel().getColumnMin(
			this.model.getSplitOptions().getColumnSplitIndex());
		double max = this.model.getStatTableModel().getColumnMax(
			this.model.getSplitOptions().getColumnSplitIndex());
		
		boundaries = StatistiekGWT.appropriateBoundariesFromBinSettings(
			min,
			max,
			view.getSplitBinWidth(),
			view.getSplitminBoundary());
		
		// if result is valid, set boundaries
		if (boundaries != null)
		{
			this.model.setSplitBinBoundaries(boundaries);
		}
		else
		{
			// reset to old values
			ArrayList<Double> oldBoundaries = this.model.getSplitOptions().getBinBoundaries(); 
			this.view.setSplitBinWidth();
			this.view.setSplitMinBoundary(oldBoundaries.get(0));
		}
	}

	public String getViewType()
	{
		return "Boxplot";
	}

	public void setViewName(String s)
	{
		this.model.setViewName(s);
	}

	public Object getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("columnIndex", this.model.getColumnIndex());
		h.put("columnSplitIndex", this.model.getColumnSplitIndex());
		h.put("name", this.model.getViewName());
		h.put("splitBoundaries", this.model.getSplitBinBoundaries());
		h.put("tukeyBox", this.model.isTukeyBox());
		h.put("verticalBoxplots", this.model.isVerticalBoxplots());

		return h;
	}

	public void setState(Object state)
	{
		if (!(state instanceof HashMap))
		{
			return;
		}

		HashMap<String, Object> h = (HashMap<String, Object>) state;
		ObjectMap map = JSONUtilities.wrapMap(h);
		
		if (map.containsKey("columnIndex"))
		{
			this.model.setColumnIndex(map.getInt("columnIndex"));
		}
		if (map.containsKey("columnSplitIndex"))
		{
			int splitIndex = map.getInt("columnSplitIndex");
			this.model.setColumnSplitIndex(splitIndex);
			// set visibility split in uop
			boolean validSplitIndex = (splitIndex == -1) ? false : true;
			this.view.getUserOptionsPanel().setVisibleSplitOptions(validSplitIndex);
		}
		if (map.containsKey("name"))
		{
			this.model.setViewName(map.getString("name"));
		}
		if (map.containsKey("splitBoundaries"))
		{
			List<Double> list = map.getDoubleList("splitBoundaries");
			ArrayList<Double> splitBoundaries = new ArrayList<Double>();
			for (int i = 0; i < list.size(); i++) 
			{
				splitBoundaries.add(list.get(i));
			}

			this.model.setSplitBoundaries(splitBoundaries);
		}
		if (map.containsKey("tukeyBox"))
		{
			this.model.setIsTukeyBox(map.getBoolean("tukeyBox"));
		}
		else
		{
			this.model.setIsTukeyBox(false); // oude boxplots default geen Tukey
		}
		if (map.containsKey("verticalBoxplots"))
		{
			this.model.setVerticalBoxplots(map.getBoolean("verticalBoxplots"));
		}
	}

	public String getViewName()
	{
		return this.model.getViewName();
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

	@Override
	public Widget getWidget()
	{
		return this.view;
	}

	/**
	 * Remove all of this view's handler occurrences.
	 */
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
}
