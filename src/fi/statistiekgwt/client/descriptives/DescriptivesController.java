package fi.statistiekgwt.client.descriptives;

import java.util.ArrayList;
import java.util.HashMap;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekView;
import fi.statistiekgwt.client.types.AllowedTypes;

/**
 * MVC Controller for StatistiekView Descriptives
 * 
 * @author Sylvia van Borkulo
 * 
 */
public class DescriptivesController implements StatistiekView
{
	private DescriptivesModel model;
	private DescriptivesView view;
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
	public DescriptivesController(StatTableModel tableModel, String viewName,
		int startVar, int width, int height)
	{
		this.model = new DescriptivesModel(tableModel, viewName);
		this.model.setColumnIndex(startVar);

		// set size
		this.setWidth(width);
		this.setHeight(height);
		
		this.view = new DescriptivesView(this.model, this);
		this.view.update();
	}
	
	/**
	 * Set the split variable, i.e., the variable for the columns in the 
	 * descriptives table.
	 * 
	 * @param index
	 */
	public void setSplit(int index)
	{
		this.model.setColumnSplitIndex(index);
		this.model.setSplitOptions(this.model.getSplitOptions());
		if (index > -1)
		{
			this.setSplitType(this.model
				.getStatTableModel()
				.getColumnTypes()
				.get(this.model.getSplitOptions().getColumnSplitIndex())
				.getType(), 5);
		}
	}

	/**
	 * 
	 * @param type
	 * @param noBins
	 */
	private void setSplitType(AllowedTypes type, int noBins)
	{
		if (type.isNumber())
		{
			ArrayList<Double> boundaries = new ArrayList<Double>();
			boundaries = StatistiekGWT.appropriateBoundaries(
				this.model.getStatTableModel().getColumnMin(
					this.model.getSplitOptions().getColumnSplitIndex()),
				this.model.getStatTableModel().getColumnMax(
					this.model.getSplitOptions().getColumnSplitIndex()),
				noBins);
			
			// opnieuw boundaries berekenen met de hierboven berekende binwidth
			boundaries = StatistiekGWT.appropriateBoundariesFromBinSettings(this.model.getStatTableModel().getColumnMin(
					this.model.getSplitOptions().getColumnSplitIndex()),
				this.model.getStatTableModel().getColumnMax(
					this.model.getSplitOptions().getColumnSplitIndex()), 
					boundaries.get(1) - boundaries.get(0), boundaries.get(0));

			this.model.setSplitBoundaries(boundaries);
			this.model.setSplitOptions(this.model.getSplitOptions());
			this.view.setModel(this.model);
		}
	}
	
	/*
	 * Update the split bin boundaries using the settings for the minimum boundary
	 * and the bin width, and determine the number of bins.
	 */
	void updateSplitBoundariesFromBinSettings()
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
			view.getSplitMinBoundary());
		
		// if result is valid, set boundaries
		if (boundaries != null)
		{
			this.model.setSplitBoundaries(boundaries);
		}
		else
		{
			// reset to old values
			ArrayList<Double> oldBoundaries = this.model.getSplitOptions().getBinBoundaries(); 
			this.view.setSplitBinWidth();
			this.view.setSplitMinBoundary(oldBoundaries.get(0));
		}
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

			this.model.setSplitBoundaries(boundaries);
			this.model.setSplitOptions(this.model.getSplitOptions());
			this.view.setModel(this.model);
		}
	}

	public void setUp(Frame owner)
	{
		// TODO Auto-generated method stub

	}

	public String getViewType()
	{
		return "Kengetallen";
	}

	public Object getState()
	{
		HashMap h = new HashMap();

		h.put("viewName", this.getViewName());
		h.put("columnIndex", this.model.getColumnIndex());
		h.put("columnSplitIndex", this.model.getSplitOptions()
			.getColumnSplitIndex());
		h.put("splitBoundaries", this.model.getSplitOptions()
			.getBinBoundaries());

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
		if (map.containsKey("columnSplitIndex"))
		{
			this.model.setColumnSplitIndex(map.getInt("columnSplitIndex"));
		}
		if (map.containsKey("splitBoundaries"))
		{
			ObjectList list = map.getObjectList("splitBoundaries");
			ArrayList<Double> splitBoundaries = new ArrayList<Double>();
			for (int i = 0; i < list.size(); i++) 
			{
				splitBoundaries.add(list.getDouble(i));
			}

			this.model.setSplitBoundaries(splitBoundaries);
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

	public void focusLost()//FocusEvent e)
	{
//		System.out.println("DescriptivesController.focusLost(): e.getSource()="
//			+ e.getSource());

		updateSplitBoundariesFromBinSettings();
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
	public void removeHandlers()
	{
		this.view.removeHandlers();
	}

	/**
	 * Get the views width.
	 */
	public int getWidth()
	{
		return this.width;
	}
	
	/**
	 * Get the views height.
	 */
	public int getHeight()
	{
		return this.height;
	}

	/**
	 * Set the views width.
	 */
	public void setWidth(int w)
	{
		this.width = w;
	}
	
	/**
	 * Set the views height.
	 */
	public void setHeight(int h)
	{
		this.height = h;
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
