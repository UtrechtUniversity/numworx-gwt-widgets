package fi.statistiekgwt.client.histogram;

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
 * MVC Controller for StatistiekView Histogram
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class HistogramController implements StatistiekView
	//, ActionListener, FocusListener
{
	private HistogramModel model;
	private HistogramView view;
	private int width;
	private int height;

	/**
	 * Constructor
	 * 
	 * @param tableModel
	 *      the data table
	 * @param viewName
	 *      The initial name of the StatistiekView
	 * @param frequencyPolygonMode
	 * 		Is frequence polygon mode yes/no
	 * @param startVar
	 * 		The index of the variable
	 * @param width
	 * 		The width of the histogram
	 * @param height
	 * 		The height of the histogram
	 */
	public HistogramController(StatTableModel tableModel, String viewName,
		boolean frequencyPolygonMode, int startVar, int width, int height)
	{
//		System.out.println("HistogramController(): maakt nieuw HistogramModel en daarmee HistogramView");
		
		this.model = new HistogramModel(tableModel, viewName, frequencyPolygonMode);
		model.setColumnIndex(startVar);
		model.setDefaultLabelPositioning();
		
		// set size
		this.setWidth(width);
		this.setHeight(height);
		
		this.view = new HistogramView(this.model, this);
		this.view.update();
		
//		System.out.println("... HistogramController(): this.model.binBoundaries=" + this.model.getBinBoundaries());
//		System.out.println("... HistogramController(): identityHashCode(this)=" 
//			+ identityHashCode(this));
//		System.out.println("... HistogramController(): identityHashCode(this.model.getBinBoundaries())=" 
//			+ identityHashCode(this.model.getBinBoundaries()));
	}

	private static int identityHashCode(Object o)
	{
		return System.identityHashCode(o);
	}
	
	/**
	 * @return The view's name
	 */
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
	 * and the bin width, and determine the number of bins.
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
			this.model.setMinOnScale(view.getMinBoundary());
			this.model.setBinBoundaries(boundaries);
			this.model.setBinWidth(view.getBinWidth());
		}
		else
		{
			// reset to old values
			ArrayList<Double> oldBoundaries = this.model.getBinBoundaries(); 
			this.view.setBinWidth();
			this.view.setMinBoundary(oldBoundaries.get(0));
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
			view.getSplitMinBoundary());
		
		// if result is valid, set boundaries
		if (boundaries != null)
		{
			this.model.setSplitBoundaries(boundaries);
			//this.view.setModel(this.model); // test syl: waarom moet dit hier en niet bij updateBoundariesFromBinSettings()?
		}
		else
		{
			// reset to old values
			ArrayList<Double> oldBoundaries = this.model.getSplitOptions().getBinBoundaries(); 
			this.view.setSplitBinWidth();
			this.view.setSplitMinBoundary(oldBoundaries.get(0));
		}
	}
	
	/*
	 * Update the bin boundaries with the set number of bins.
	 */
	private void updateBoundaries()
	{
		ArrayList<Double> boundaries = new ArrayList<Double>();
		for (int i = 0; i <= this.model.getNoBins(); i++)
		{
			boundaries.add(new Double(view.getMinBoundary() + i
				* view.getBinWidth()));
		}
		
		this.model.setBinBoundaries(boundaries);
	}

	private void updateSplitBoundaries()
	{
		ArrayList<Double> boundaries = new ArrayList<Double>();
		for (int i = 0; i <= this.view.getSplitBinsBoxSelectedInt(); i++)
		{
			boundaries.add(new Double(view.getSplitMinBoundary() + i
				* view.getSplitBinWidth()));
		}
		this.model.setSplitBoundaries(boundaries);
		this.view.setModel(this.model);
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

	public String getViewType()
	{
		return (this.model.isFrequencyPolygonMode() ? "Frequentiepolygoon"
			: "Histogram");
	}

	public Object getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();

		h.put("binBoundaries", this.model.getBinBoundaries());
		h.put("columnIndex", this.model.getColumnIndex());
		h.put("percentage", this.model.getPercentage());
		h.put("percentage_splitTotal", this.model.getPercentageSplitTotal());
		h.put("labelUnderBin", this.model.getLabelUnderBin());
		h.put("showUserOptions", this.model.getShowUserOptions());
		h.put("verticalBars", this.model.hasVerticalBars());
		h.put("viewName", this.model.getViewName());
		h.put("frequencyPolygonCumulativeMode",
			this.model.isFrequencyPolygonCumulativeMode());
		h.put("columnSplitIndex", this.model.getSplitOptions()
			.getColumnSplitIndex());
		h.put("splitBoundaries", this.model.getSplitOptions()
			.getBinBoundaries());
		h.put("splitInSingleView", this.model.isSplitInSingleView());
		h.put("nextToEachOther", this.model.isNextToEachOther());
		h.put("optimizeScale", this.model.isOptimizeScale());
		h.put("minOnScale", this.model.getMinOnScale());
		h.put("maxOnScale", this.model.getMaxOnScale());
		h.put("binWidth", this.model.getBinWidth());

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
			// Let op: setColumnIndex() zet ook de binBoundaries
			// Dat wordt hieronder goed gemaakt als de binBoundaries
			// uit de hashtable worden gezet.
			this.model.setColumnIndex(map.getInt("columnIndex"));
			
			// set size??
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
		if (map.containsKey("percentage"))
		{
			this.model.setPercentage(map.getBoolean("percentage"));
		}
		if (h.containsKey("percentage_splitTotal"))
		{
			this.model.setPercentageSplitTotal(map.getBoolean("percentage_splitTotal"));
		}
		else
		{
			// default is true (split total 100%)
			this.model.setPercentageSplitTotal(true);
		}
		if (map.containsKey("labelUnderBin"))
		{
			this.model.setLabelUnderBin(map.getBoolean("labelUnderBin"));
		}
		if (map.containsKey("showUserOptions"))
		{
			this.model.setShowUserOptions(map.getBoolean("showUserOptions"));
		}
		if (map.containsKey("verticalBars"))
		{
			this.model.setVerticalBars(map.getBoolean("verticalBars"));
		}
		if (map.containsKey("viewName"))
		{
			this.model.setViewName(map.getString("viewName"));
		}
		if (map.containsKey("frequencyPolygonCumulativeMode"))
		{
			this.model.setFrequencyPolygonCumulativeMode(map.getBoolean("frequencyPolygonCumulativeMode"));
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
		if (map.containsKey("splitInSingleView"))
		{
			this.model.setSplitInSingleView(map.getBoolean("splitInSingleView"));
		}
		if (map.containsKey("nextToEachOther"))
		{
			this.model.setNextToEachOther(map.getBoolean("nextToEachOther"));
		}
		if (h.containsKey("optimizeScale"))
		{
			this.model.setOptimizeScale(map.getBoolean("optimizeScale"));
		}
		if (h.containsKey("minOnScale"))
		{
			this.model.setMinOnScale(map.getDouble("minOnScale"));
		}
		if (h.containsKey("maxOnScale"))
		{
			this.model.setMaxOnScale(map.getDouble("maxOnScale"));
		}
		if (h.containsKey("binWidth"))
		{
			this.model.setBinWidth(map.getDouble("binWidth"));
		}
	}

	public String toString()
	{
		return this.getViewName();
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

	@Override
	public void setUp(Frame owner)
	{
		// TODO Auto-generated method stub
		
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
