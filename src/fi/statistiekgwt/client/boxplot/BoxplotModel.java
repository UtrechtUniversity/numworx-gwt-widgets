package fi.statistiekgwt.client.boxplot;

import java.util.ArrayList;
import java.util.Collections;

import fi.statistiekgwt.client.SplitOptions;
import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * MVC Model for statistiekview Boxplot
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class BoxplotModel
{
	private StatTableModel statTableModel;

	private int columnIndex;
	private SplitOptions splitOptions;

	private ArrayList<Double> minValues;
	private ArrayList<Double> lowerQuartiles;
	private ArrayList<Double> medians;
	private ArrayList<Double> upperQuartiles;
	private ArrayList<Double> maxValues;

	private Double dataMinValue;
	private Double dataMaxValue;

	private boolean verticalBoxplots;

	private String viewName;

	/**
	 * Constructor
	 * 
	 * @param tableModel
	 *            the tablemodel
	 * @param viewName
	 *            the name of the view
	 */
	public BoxplotModel(StatTableModel tableModel, String viewName)
	{
		this.viewName = viewName;
		this.statTableModel = tableModel;
		this.splitOptions = new SplitOptions();

		// set initial values
		this.columnIndex = -1;
		this.verticalBoxplots = false;
	}

	/**
	 * @return true if using verticalboxplots, false if using horizontal
	 *         boxplots
	 */
	public boolean isVerticalBoxplots()
	{
		return verticalBoxplots;
	}

	/**
	 * Set the orientation of the boxplots
	 * 
	 * @param verticalBoxplots
	 *            the new orientation, true for vertical, false for horizontal
	 */
	public void setVerticalBoxplots(boolean verticalBoxplots)
	{
		if (this.verticalBoxplots != verticalBoxplots)
		{
			this.verticalBoxplots = verticalBoxplots;
		}
	}

	/**
	 * Get the lowest value in the data
	 * 
	 * @return the lowest value in the data
	 */
	public Double getDataMinValue()
	{
		return dataMinValue;
	}

	/**
	 * Get the highest value in the data
	 * 
	 * @return the highest value in the data
	 */
	public Double getDataMaxValue()
	{
		return dataMaxValue;
	}

	/**
	 * Get the splitoptions
	 * 
	 * @return the splitoptions
	 */
	public SplitOptions getSplitOptions()
	{
		return this.splitOptions;
	}

	/**
	 * Set the splitoptions
	 * 
	 * @param splitOptions
	 *            the new splitoptions
	 */
	public void setSplitOptions(SplitOptions splitOptions)
	{
		this.splitOptions = splitOptions;
		this.setPercentileValues();
	}
	
	public void setSplitBoundaries(ArrayList<Double> boundaries)
	{
		this.splitOptions.setBinBoundaries(boundaries);
	}

	/**
	 * Get the name of this view
	 * 
	 * @return the name of this view
	 */
	public String getViewName()
	{
		return viewName;
	}

	/**
	 * set the name of this view
	 * 
	 * @param viewName
	 *            the new name of this view
	 */
	public void setViewName(String viewName)
	{
		if (!this.viewName.equals(viewName))
		{
			this.viewName = viewName;
		}
	}

	/**
	 * Get the tablemodel
	 * 
	 * @return the tablemodel
	 */
	public StatTableModel getStatTableModel()
	{
		return this.statTableModel;
	}

	/**
	 * Get the index of the column represented by this boxplot
	 * 
	 * @return the index of the column represented by this boxplot
	 */
	public int getColumnIndex()
	{
		return columnIndex;
	}

	/**
	 * Set the column that is represented by this boxplot
	 * 
	 * @param columnIndex
	 *            the index of the column
	 */
	public void setColumnIndex(int columnIndex)
	{
		if (!(this.columnIndex == columnIndex))
		{
			this.columnIndex = columnIndex;
			this.setPercentileValues();
		}
	}

	/**
	 * Get the index of the column by which the data is split
	 * 
	 * @return the index of the column by which the data is split
	 */
	public int getColumnSplitIndex()
	{
		return this.splitOptions.getColumnSplitIndex();
	}

	public void setColumnSplitIndex(int columnSplitIndex)
	{
		if (this.splitOptions.getColumnSplitIndex() != columnSplitIndex)
		{
			this.splitOptions.setColumnSplitIndex(columnSplitIndex);
			this.setPercentileValues();
		}
	}

	public ArrayList<Double> getSplitBinBoundaries()
	{
		return this.splitOptions.getBinBoundaries();
	}

	public void setSplitBinBoundaries(ArrayList<Double> splitBoundaries)
	{
		this.splitOptions.setBinBoundaries(splitBoundaries);
		this.setPercentileValues();
	}

	/**
	 * Get the array of minimum values for all split classes.
	 * 
	 * @return
	 */
	public ArrayList<Double> getMinValues()
	{
		return minValues;
	}

	/**
	 * Get the minimum value for the given split classes.
	 * 
	 * @param bin
	 * @return
	 */
	public Double getMinValue(int bin)
	{
		return minValues.get(bin);
	}

	/**
	 * Get the array of lower quartile values for all split classes.
	 * 
	 * @return
	 */
	public ArrayList<Double> getLowerQuartiles()
	{
		return lowerQuartiles;
	}

	/**
	 * Get the lower quartile value for the given split classes.
	 * 
	 * @param bin
	 * @return
	 */
	public Double getLowerQuartile(int bin)
	{
		return lowerQuartiles.get(bin);
	}

	/**
	 * Get the array of median values for all split classes.
	 * 
	 * @return
	 */
	public ArrayList<Double> getMedians()
	{
		return medians;
	}

	/**
	 * Get the median value for the given split classes.
	 * 
	 * @param bin
	 * @return
	 */
	public Double getMedian(int bin)
	{
		return medians.get(bin);
	}

	/**
	 * Get the array of upper quartile values for all split classes.
	 * 
	 * @return
	 */
	public ArrayList<Double> getUpperQuartiles()
	{
		return upperQuartiles;
	}

	/**
	 * Get the upper quartile value for the given split classes.
	 * 
	 * @param bin
	 * @return
	 */
	public Double getUpperQuartile(int bin)
	{
		return upperQuartiles.get(bin);
	}

	/**
	 * Get the array of maximum values for all split classes.
	 * 
	 * @return
	 */
	public ArrayList<Double> getMaxValues()
	{
		return maxValues;
	}

	/**
	 * Get the maximum value for the given split classes.
	 * 
	 * @param bin
	 * @return
	 */
	public Double getMaxValue(int bin)
	{
		return maxValues.get(bin);
	}

	/**
	 * Get the number of split classes.
	 * 
	 * @return
	 */
	public int getSplitClasses()
	{
		return this.statTableModel.splitVarClasses(this.splitOptions);
	}

	/**
	 * Calculates the percentile values
	 */
	public void setPercentileValues()
	{
		if (!(this.getStatTableModel().isColumnIndexValid(this.columnIndex)))
		{
			return;
		}
		
		// als niet-numerieke variabele gekozen, dan kun je geen percentielwaarden berekenen
		String type = this.statTableModel.getColumnTypes().get(columnIndex).getType().toString();
		if (type.equals("Enum") || type.equals("String"))
		{
			// 'empty' the dataset; field dataMinValue is used to test for empty dataset
			this.dataMinValue = null;
			return;
		}

		if (!this.getStatTableModel().isColumnIndexValid(
			this.splitOptions.getColumnSplitIndex()))
		{
			ArrayList<Double> data = new ArrayList<Double>();

			for (int i = 0; i < this.statTableModel.getRowCount(); i++)
			{
				String valueString = (String) this.statTableModel.getValueAt(i,
					columnIndex);
				if (!valueString.equals(ColumnType.WILDCARD))
				{
					// get the value
					Double d = Double.parseDouble(valueString);

					// add the value to a list based on the splitclass
					data.add(d);
				}
			}

			this.minValues = new ArrayList<Double>();
			this.lowerQuartiles = new ArrayList<Double>();
			this.medians = new ArrayList<Double>();
			this.upperQuartiles = new ArrayList<Double>();
			this.maxValues = new ArrayList<Double>();

			Collections.sort(data);
			int size = data.size();
			if (size == 0)
			{
				this.minValues.add(null);
				this.lowerQuartiles.add(null);
				this.medians.add(null);
				this.upperQuartiles.add(null);
				this.maxValues.add(null);
			}
			else
			{
				this.minValues.add(data.get(0));
				this.lowerQuartiles
					.add(data.get((int) Math.ceil(0.25 * size) - 1));
				
				addMedian(data);
				
				this.upperQuartiles
					.add(data.get((int) Math.ceil(0.75 * size) - 1));
				this.maxValues.add(data.get(size - 1));
			}
			this.dataMinValue = this.getMinValue(0);
			this.dataMaxValue = this.getMaxValue(0);
		}
		else
		{
			int splitClasses = this.getSplitClasses();

			ArrayList<ArrayList<Double>> sortedData = new ArrayList<ArrayList<Double>>();
			for (int i = 0; i < splitClasses; i++)
			{
				sortedData.add(new ArrayList<Double>());
			}

			for (int i = 0; i < this.statTableModel.getRowCount(); i++)
			{
				String valueString = (String) this.statTableModel.getValueAt(i,
					columnIndex);
				String valueSplitString = (String) this.statTableModel.getValueAt(
					i, this.splitOptions.getColumnSplitIndex());
				if (!valueSplitString.equals(ColumnType.WILDCARD)
					&& !valueString.equals(ColumnType.WILDCARD))
				{
					// get the value
					Double d = Double.parseDouble(valueString);

					int index = this.statTableModel.classifyObject(valueSplitString,
						this.splitOptions.getColumnSplitIndex(),
						this.splitOptions.getBinBoundaries());
					// add the value to a list based on the splitclass
					if (index > -1)
					{
						sortedData.get(index).add(d);
					}
				}
			}

			this.minValues = new ArrayList<Double>();
			this.lowerQuartiles = new ArrayList<Double>();
			this.medians = new ArrayList<Double>();
			this.upperQuartiles = new ArrayList<Double>();
			this.maxValues = new ArrayList<Double>();

			for (int i = 0; i < splitClasses; i++)
			{
				Collections.sort(sortedData.get(i));
				int size = sortedData.get(i).size();
				if (size == 0)
				{
					this.minValues.add(null);
					this.lowerQuartiles.add(null);
					this.medians.add(null);
					this.upperQuartiles.add(null);
					this.maxValues.add(null);
				}
				else
				{
					this.minValues.add(sortedData.get(i).get(0));
					this.lowerQuartiles.add(sortedData.get(i).get(
						(int) Math.ceil(0.25 * size) - 1));
					
					addMedian(sortedData.get(i));
					
					this.upperQuartiles.add(sortedData.get(i).get(
						(int) Math.ceil(0.75 * size) - 1));
					this.maxValues.add(sortedData.get(i).get(size - 1));
				}
			}

			if (splitClasses > 0)
			{
				this.dataMinValue = this.getMinValue(0);
				this.dataMaxValue = this.getMaxValue(0);

				for (int i = 1; i < splitClasses; i++)
				{
					if (this.getMinValue(i) != null
						&& (this.dataMinValue == null || this.getMinValue(i) < this.dataMinValue))
					{
						this.dataMinValue = this.getMinValue(i);
					}
					if (this.getMaxValue(i) != null
						&& (this.dataMaxValue == null || this.getMaxValue(i) > this.dataMaxValue))
					{
						this.dataMaxValue = this.getMaxValue(i);
					}
				}
			}
		}
	}
	
	/*
	 * Determine the median of the data set and 
	 * add to medians.
	 */
	private void addMedian (ArrayList<Double> data)
	{
		int index;
		Double median;
		int size = data.size();

		if (size % 2 == 0)
		{
			// even number of values in data set
			
			index = (size/2) - 1;
			// mediaan is het gemiddelde van de twee waarden in het midden
			median = (data.get(index) + data.get(index + 1))/2;
		}
		else
		{
			// odd number of values in data set
			
			index = (int) ((size + 1)/2) - 1;
			// mediaan is de middelste waarde
			median = data.get(index);
		}
		
		this.medians.add(median);
	}

	/**
	 * Update the column index and the split column index
	 * given that removedColumn has been removed.
	 * @param removedColumn
	 */
	public void updateColumnIndex(int removedColumn)
	{
		// index van de geselecteerde variabele bijwerken
		if (removedColumn < this.columnIndex)
		{
			this.columnIndex = this.columnIndex - 1;
		}
		else if (removedColumn == this.columnIndex)
		{
			this.columnIndex = -1;
		}
		
		// index van de split variabele bijwerken
		if (removedColumn < this.splitOptions.getColumnSplitIndex())
		{
			this.splitOptions.setColumnSplitIndex(this.splitOptions.getColumnSplitIndex() - 1);
		}
		else if (removedColumn == this.splitOptions.getColumnSplitIndex())
		{
			this.splitOptions.setColumnSplitIndex(- 1);
		}
	}

	/**
	 * Set column split index without triggering an updating event.
	 * @param columnSplitIndex
	 */
	public void setColumnSplitIndexWithoutEvent(int columnSplitIndex)
	{
		this.splitOptions.setColumnSplitIndex(columnSplitIndex);
	}

	/**
	 * Set split options without triggering an updating event.
	 * @param splitOptions
	 */
	public void setSplitOptionsWithoutEvent(
		SplitOptions splitOptions)
	{
		this.splitOptions = splitOptions;
	}

	/**
	 * Check if the current column index is a valid column index
	 * 
	 * @return true iff valid
	 */
	public boolean columnIndexValid()
	{
		return this.columnIndex >= 0
			&& this.columnIndex < this.statTableModel.getColumnCount();
	}
}
