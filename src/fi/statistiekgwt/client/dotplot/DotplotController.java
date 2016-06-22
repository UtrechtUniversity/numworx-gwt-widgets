package fi.statistiekgwt.client.dotplot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

import fi.statistiekgwt.client.ColorUtils;
import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekView;
import fi.statistiekgwt.client.types.AllowedTypes;

/**
 * MVC Controller for StatistiekView Dotplot
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class DotplotController implements StatistiekView
{
	private DotplotView view;
	private DotplotModel model;
	private int width;
	private int height;

	/**
	 * Constructor of DotplotController.
	 * 
	 * @param tableModel
	 *            The data table
	 * @param viewName
	 *            this view's name
	 */
	public DotplotController(StatTableModel tableModel, String viewName,
		int startVar, int width, int height)
	{
		this.model = new DotplotModel(tableModel, viewName, false);
		model.setColumnXIndex(startVar);
		model.initializeMinXOnScale();
		model.initializeMaxXOnScale();
		
		this.view = new DotplotView(this.model, this);
		
		// set size
		this.setWidth(width);
		this.setHeight(height);
		
		try
		{
			// voor standalone
			this.model.setMinXOnScale(this.view.getMinXOnScale());
			this.model.setMaxXOnScale(this.view.getMaxXOnScale());
		}
		catch (NumberFormatException e) 
		{
			// Bij create view vanuit zet opdracht wordt eerst een histogram-view met columnIndex 0 gemaakt
			// dan is er nog geen minBoundary en maxBinOnScale
		}

		this.view.update();
	}

	/**
	 * Constructor of DotplotController for scatterplot mode with variables
	 * for x and y axis.
	 * 
	 * @param tableModel
	 *            The data table
	 * @param viewName
	 *            this view's name
	 */
	public DotplotController(StatTableModel tableModel, String viewName,
		int startVar1, int startVar2, int width, int height)
	{
		this.model = new DotplotModel(tableModel, viewName, true);
		model.setColumnXIndex(startVar1);
		model.setColumnYIndex(startVar2);
		model.initializeMinXOnScale();
		model.initializeMaxXOnScale();

		this.view = new DotplotView(this.model, this);

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

			this.model.setSplitBoundaries(boundaries);
			this.model.setSplitOptions(this.model.getSplitOptions());
			this.view.setModel(this.model);
		}

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
	@Override
	public int getWidth()
	{
		return this.width;
	}
	
	/**
	 * Get the view's height.
	 */
	@Override
	public int getHeight()
	{
		return this.height;
	}
	
	/**
	 * Set the view's width.
	 */
	@Override
	public void setWidth(int w)
	{
		this.width = w;
		this.view.setWidth(w);
	}
	
	/**
	 * Set the view's height.
	 */
	@Override
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

	public String getViewType()
	{
		return (this.model.isScatterplotMode() ? "Spreidingsdiagram"
			: "Dotplot");
	}

	public Object getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();

		h.put("showCorrelation", this.model.isShowCorrelation());
		h.put("useColorScale", this.model.isUseColorScale());
		h.put("columnColorIndex", this.model.getColumnColorIndex());
		h.put("columnXIndex", this.model.getColumnXIndex());
		h.put("columnYIndex", this.model.getColumnYIndex());
		h.put("colorAString", this.model.getColorA().value());
		h.put("colorBString", this.model.getColorB().value());
		h.put("viewName", this.model.getViewName());
		h.put("columnSplitIndex", this.model.getSplitOptions()
			.getColumnSplitIndex());
		h.put("splitBoundaries", this.model.getSplitBinBoundaries());
		h.put("splitInSingleView", this.model.splitInSingleView());
		h.put("optimizeScaleX", this.model.isOptimizeScaleX());
		h.put("minXOnScale", this.model.getMinXOnScale());
		h.put("maxXOnScale", this.model.getMaxXOnScale());


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

		if (map.containsKey("showCorrelation"))
		{
			this.model.setShowCorrelation(map.getBoolean("showCorrelation"));
		}
		if (map.containsKey("useColorScale"))
		{
			this.model.setUseColorScale(map.getBoolean("useColorScale"));
		}
		if (map.containsKey("columnColorIndex"))
		{
			this.model
				.setColumnColorIndex(map.getInt("columnColorIndex"));
		}
		if (map.containsKey("columnXIndex"))
		{
			this.model.setColumnXIndex(map.getInt("columnXIndex"));
		}
		if (map.containsKey("columnYIndex"))
		{
			this.model.setColumnYIndex(map.getInt("columnYIndex"));
		}
		if (map.containsKey("colorAString"))
		{
			this.model.setColorA(CssColor.make(map.getString("colorAString")));
		}
		else
		{
			// set default color A
			this.model.setColorA(ColorUtils.getDefaultColorA());
		}
		if (map.containsKey("colorBString"))
		{
			this.model.setColorB(CssColor.make(map.getString("colorBString")));
		}
		else
		{
			// set default color B 
			this.model.setColorB(ColorUtils.getDefaultColorB());
		}
		if (map.containsKey("viewName"))
		{
			this.model.setViewName(map.getString("viewName"));
		}
		if (map.containsKey("columnSplitIndex"))
		{
			int splitIndex = map.getInt("columnSplitIndex");
			this.model.setColumnSplitIndex(splitIndex);
			// set visibility split in uop
			boolean validSplitIndex = (splitIndex == -1) ? false : true;
			this.view.getUserOptionsPanel().setVisibleSplitOptions(validSplitIndex);
		}
		if (map.containsKey("splitInSingleView"))
		{
			this.model.setSplitInSingleView(map.getBoolean("splitInSingleView"));
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

		if (h.containsKey("optimizeScaleX"))
		{
			this.model.setOptimizeScaleX(map.getBoolean("optimizeScaleX"));
		}
		
		if (h.containsKey("minXOnScale"))
		{
			this.model.setMinXOnScale(map.getDouble("minXOnScale"));
		}
		else
		{
			// default is the columnX's minimum value
			double minColumnXValue = this.model.getStatTableModel().getColumnMin(this.model.getColumnXIndex());
			this.model.setMinXOnScale(minColumnXValue);
		}
		
		if (h.containsKey("maxXOnScale"))
		{
			this.model.setMaxXOnScale(map.getDouble("maxXOnScale"));
		}
		else
		{
			// default is the columnX's maximum value
			double maxColumnXValue = this.model.getStatTableModel().getColumnMax(this.model.getColumnXIndex());
			this.model.setMaxXOnScale(maxColumnXValue);
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
	
	public String toString()
	{
		return this.getViewName();
	}

	public DotplotModel getModel()
	{
		return this.model;
	}

	@Override
	public void setUp(Frame owner)
	{
	}

	@Override
	public void setUp(DialogBox owner)
	{
	}
}
