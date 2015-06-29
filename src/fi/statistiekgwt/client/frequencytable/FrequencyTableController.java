package fi.statistiekgwt.client.frequencytable;

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
 * MVC Controller for StatistiekView FrequencyTable
 * 
 * @author ManuDrijvers, Sylvia van Borkulo
 * 
 */
public class FrequencyTableController implements StatistiekView
{

	private FrequencyTableModel model;
	private FrequencyTableView view;
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
	public FrequencyTableController(StatTableModel tableModel, String viewName,
		int startVar, int width, int height)
	{
		this.model = new FrequencyTableModel(tableModel, viewName);
		this.model.setColumnIndex(startVar);

		// set size
		this.setWidth(width);
		this.setHeight(height);

		this.view = new FrequencyTableView(this.model, this);
		this.view.update();
	}

//	private void updateSplitBoundaries()
//	{
//		ArrayList<Double> boundaries = new ArrayList<Double>();
//		for (int i = 0; i <= this.view.getSplitBinsBoxSelectedInt(); i++)
//		{
//			boundaries.add(new Double(view.getSplitMinBoundary() + i
//				* view.getSplitBinWidth()));
//		}
//		this.model.setSplitBoundaries(boundaries);
//		this.view.setModel(this.model);
//	}

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
	
	public String getViewType()
	{
		return "Frequentietabel";
	}

	public Object getState()
	{
		HashMap h = new HashMap();

		h.put("showPercentage", this.model.isShowPercentage());
		h.put("showCumulative", this.model.isShowCumulative());
		h.put("binBoundaries", this.model.getBinBoundaries());
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
		
		if (map.containsKey("showPercentage"))
		{
			this.model.setShowPercentage(map.getBoolean("showPercentage"));
		}
		if (map.containsKey("showCumulative"))
		{
			this.model.setShowCumulative(map.getBoolean("showCumulative"));
		}
		if (map.containsKey("viewName"))
		{
			this.setViewName(map.getString("viewName"));
		}
		if (map.containsKey("columnIndex"))
		{
			// Let op: setColumnIndex() zet ook de binBoundaries
			// Dat wordt hieronder goed gemaakt als de binBoundaries
			// uit de hashtable worden gezet.
			this.model.setColumnIndex(map.getInt("columnIndex"));
		}
		if (map.containsKey("binBoundaries"))
		{
			List<Double> list = map.getDoubleList("binBoundaries");
			ArrayList<Double> binBoundaries = new ArrayList<Double>();
			for (int i = 0; i < list.size(); i++) 
			{
				binBoundaries.add(list.get(i));
			}

			this.model.setBinBoundaries(binBoundaries);
		}
		if (map.containsKey("columnSplitIndex"))
		{
			int splitIndex = map.getInt("columnSplitIndex");
			this.model.setColumnSplitIndex(splitIndex);
			// set visibility split in uop
			boolean validSplitIndex = (splitIndex == -1) ? false : true;
			this.view.getUserOptionsPanel().setVisibleSplitOptions(validSplitIndex);
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
	}

	public String getViewName()
	{
		return this.model.getViewName();
	}

	public void setViewName(String s)
	{
		this.model.setViewName(s);
	}

	/*
	 * Update the bin boundaries using the settings for the minimum boundary
	 * and the bin width.
	 * and determine the number of bins.
	 */
	public void updateBoundariesFromBinSettings()
	{
		ArrayList<Double> boundaries = new ArrayList<Double>();
		
		double min = this.model.getStatTableModel().getColumnMin(
			this.model.getColumnIndex());
		double max = this.model.getStatTableModel().getColumnMax(
			this.model.getColumnIndex());
		
		boundaries = StatistiekGWT.appropriateBoundariesFromBinSettings(
			min,
			max,
			view.getBinWidth(),
			view.getMinBoundary());
		
		// if result is valid, set boundaries
		if (boundaries != null)
		{
			this.model.setBinBoundaries(boundaries);
		}
		else
		{
			// reset to old values
			ArrayList<Double> oldBoundaries = this.model.getBinBoundaries(); 
			this.view.setBinWidth();
			this.view.setMinBoundary(oldBoundaries.get(0));
		}
	}
	
	public void setSplitType(AllowedTypes type)
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
