package fi.statistiekgwt.client.crosstabulationtable;

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
 * MVC Controller for StatistiekView CrossTabulationTable
 * 
 * @author Sylvia van Borkulo
 * 
 */
public class CrossTabulationTableController implements StatistiekView
{

	private CrossTabulationTableModel model;
	private CrossTabulationTableView view;
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
	public CrossTabulationTableController(StatTableModel tableModel, String viewName,
		int startVarRows, int startVarColumns, int width, int height)
	{
		this.model = new CrossTabulationTableModel(tableModel, viewName);
		this.model.setColumnIndex(startVarRows);
		
		this.view = new CrossTabulationTableView(this.model, this);
		
		// set size
		this.setWidth(width);
		this.setHeight(height);
		
		// setSplit after the view has been created
		setSplit(startVarColumns);

		this.view.update();
	}
	
	/**
	 * Set the split variable, i.e., the variable for the columns in the crosstab table.
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
				.getType(), CrossTabulationTableModel.DEFAULT_NUMBER_OF_BINS);//index);
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
			int bin0Decimals = StatistiekGWT.getNumberOfDecimals(boundaries.get(0).toString());
			int bin1Decimals = StatistiekGWT.getNumberOfDecimals(boundaries.get(1).toString());
			int maxNumberOfDecimals = Math.max(bin0Decimals, bin1Decimals);
			
			boundaries = StatistiekGWT.appropriateBoundariesFromBinSettings(this.model.getStatTableModel().getColumnMin(
					this.model.getSplitOptions().getColumnSplitIndex()),
				this.model.getStatTableModel().getColumnMax(
					this.model.getSplitOptions().getColumnSplitIndex()), 
					// door afronding kan de aftreksom heel veel decimalen hebben
					StatistiekGWT.round(boundaries.get(1) - boundaries.get(0), maxNumberOfDecimals), boundaries.get(0));

			this.model.setSplitBoundaries(boundaries);
			this.model.setSplitOptions(this.model.getSplitOptions());
			this.view.setModel(this.model);
		}
	}
	
	public String getViewType()
	{
		return "Kruistabel";
	}

	public Object getState()
	{
		HashMap h = new HashMap();

		h.put("viewName", this.getViewName());
		h.put("columnIndex", this.model.getColumnIndex());
		h.put("binBoundaries", this.model.getBinBoundaries());
		h.put("columnSplitIndex", this.model.getSplitOptions()
			.getColumnSplitIndex());
		h.put("splitBoundaries", this.model.getSplitOptions()
			.getBinBoundaries());
		h.put("showPercentage", this.model.isShowPercentage());
		h.put("showPercentage_endTotal", this.model.isShowPercentage_endTotal());
		h.put("showPercentage_rowTotal", this.model.isShowPercentage_rowTotal());
		h.put("showPercentage_columnTotal", this.model.isShowPercentage_columnTotal());

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
		}
		if (map.containsKey("splitBoundaries"))
		{
			//ObjectList list = map.getObjectList("splitBoundaries");
			List<Double> list = map.getDoubleList("splitBoundaries");
			ArrayList<Double> splitBoundaries = new ArrayList<Double>();
			for (int i = 0; i < list.size(); i++) 
			{
				splitBoundaries.add(list.get(i));//getDouble(i));
			}

			this.model.setSplitBoundaries(splitBoundaries);
		}
		if (map.containsKey("showPercentage"))
		{
			this.model.setShowPercentage(map.getBoolean("showPercentage"));
		}
		if (map.containsKey("showPercentage_endTotal"))
		{
			this.model.setShowPercentage_endTotal(map.getBoolean("showPercentage_endTotal"));
		}
		if (map.containsKey("showPercentage_rowTotal"))
		{
			this.model.setShowPercentage_rowTotal(map.getBoolean("showPercentage_rowTotal"));
		}
		if (map.containsKey("showPercentage_columnTotal"))
		{
			this.model.setShowPercentage_columnTotal(map.getBoolean("showPercentage_columnTotal"));
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

	/**
	 * Update the bin boundaries for the rows variable
	 * using the settings for the minimum boundary
	 * and the bin width,
	 * and determine the number of bins.
	 */
	void updateBoundariesFromRowsBinSettings()
	{
		ArrayList<Double> boundaries = new ArrayList<Double>();
		
		double min = this.model.getStatTableModel().getColumnMin(
			this.model.getColumnIndex());
		double max = this.model.getStatTableModel().getColumnMax(
			this.model.getColumnIndex());
		
		boundaries = StatistiekGWT.appropriateBoundariesFromBinSettings(
			min,
			max,
			view.getBinWidthRows(),
			view.getMinBoundaryRows());
		
		// if result is valid, set boundaries
		if (boundaries != null)
		{
			this.model.setBinBoundaries(boundaries);
		}
		else
		{
			// reset to old values
			ArrayList<Double> oldBoundaries = this.model.getBinBoundaries(); 
			this.view.setBinWidthRows();
			this.view.setMinBoundaryRows(oldBoundaries.get(0));
		}
	}
	
	/**
	 * Update the bin boundaries for the columns variable
	 * using the settings for the minimum boundary
	 * and the bin width,
	 * and determine the number of bins.
	 */
	void updateBoundariesFromColumnsBinSettings()
	{
		ArrayList<Double> boundaries = new ArrayList<Double>();
		
		double min = this.model.getStatTableModel().getColumnMin(
			this.model.getColumnSplitIndex());
		double max = this.model.getStatTableModel().getColumnMax(
			this.model.getColumnSplitIndex());
		
		boundaries = StatistiekGWT.appropriateBoundariesFromBinSettings(
			min,
			max,
			this.view.getBinWidthColumns(),
			this.view.getMinBoundaryColumns());
		
		// if result is valid, set boundaries
		if (boundaries != null)
		{
			this.model.setSplitBoundaries(boundaries);
		}
		else
		{
			// reset to old values
			ArrayList<Double> oldBoundaries = this.model.getSplitOptions().getBinBoundaries(); 
			this.view.setBinWidthColumns();
			this.view.setMinBoundaryColumns(oldBoundaries.get(0));
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
}
