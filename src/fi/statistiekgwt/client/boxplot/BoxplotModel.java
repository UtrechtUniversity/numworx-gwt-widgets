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

	/**
	 * An arraylist with for each split the value below which a
	 * value is a strong outlier in the Tukey boxplot, i.e.,  
	 * more than 3 * IQR below Q1.
	 */
	private ArrayList<Double> outlierStrongMinValues;
	/**
	 * An arraylist with for each split the value below which a
	 * value is a weak outlier in the Tukey boxplot, i.e., 
	 * between 1.5 * IQR and 3 * IQR below Q1.
	 */
	private ArrayList<Double> outlierWeakMinValues;
	/**
	 * An arraylist with an array of outlier values on the
	 * lower side of the Tukey boxplot for each split class.
	 */
	private ArrayList<ArrayList<Double>> outlierMinValues;
	/**
	 * An array of the minimum values for each split class.
	 * For a Tukey boxplot it is the minimum non-outlier value. 
	 */
	private ArrayList<Double> minValues;
	private ArrayList<Double> lowerQuartiles;
	private ArrayList<Double> medians;
	private ArrayList<Double> upperQuartiles;
	/**
	 * An array of the maximum values for each split class.
	 * For a Tukey boxplot it is the maximum non-outlier value. 
	 */
	private ArrayList<Double> maxValues;
	/**
	 * An arraylist with an array of outlier values on the
	 * upper side of the Tukey boxplot for each split class.
	 */
	private ArrayList<ArrayList<Double>> outlierMaxValues;
	/**
	 * An arraylist with for each split the value above which a
	 * value is a weak outlier in the Tukey boxplot, i.e., 
	 * between 1.5 * IQR and 3 * IQR above Q3.
	 */
	private ArrayList<Double> outlierWeakMaxValues;
	/**
	 * An arraylist with for each split the value above which a
	 * value is a strong outlier in the Tukey boxplot, i.e.,  
	 * more than 3 * IQR above Q3.
	 */
	private ArrayList<Double> outlierStrongMaxValues;

	private Double dataMinValue;
	private Double dataMaxValue;

	/**
	 * Boolean that indicates whether a Tukey boxplot is displayed.
	 */
	private boolean isTukeyBox;
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
		this.isTukeyBox = false;
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
		return this.minValues;
	}

	/**
	 * Get the minimum value for the given split class.
	 * 
	 * @param splitClass
	 * @return
	 */
	public Double getMinValue(int splitClass)
	{
		return this.minValues.get(splitClass);
	}

	/**
	 * Get the array of the values determining a strong outlier 
	 * on the lower side for all split classes, i.e., strong outliers
	 * on the lower side are smaller than this value.
	 * 
	 * @return
	 */
	public ArrayList<Double> getOutlierStrongMinValues()
	{
		return this.outlierStrongMinValues;
	}

	/**
	 * Get the value determining a strong outlier 
	 * on the lower side for the given split class, i.e., strong outliers
	 * on the lower side are smaller than this value.
	 * 
	 * @param splitClass
	 * @return
	 */
	public Double getOutlierStrongMinValue(int splitClass)
	{
		return this.outlierStrongMinValues.get(splitClass);
	}

	/**
	 * Get the array of the values determining a weak outlier 
	 * on the lower side for all split classes, i.e., weak outliers
	 * are lower than this value and higher than the value that determines
	 * a strong outlier.
	 * 
	 * @return
	 */
	public ArrayList<Double> getOutlierWeakMinValues()
	{
		return this.outlierWeakMinValues;
	}

	/**
	 * Get the value determining a weak outlier 
	 * on the lower side for the given split class, i.e., weak outliers
	 * are lower than this value and higher than the value that determines
	 * a strong outlier.
	 * 
	 * @param splitClass
	 * @return
	 */
	public Double getOutlierWeakMinValue(int splitClass)
	{
		return this.outlierWeakMinValues.get(splitClass);
	}

	/**
	 * Get the array of the values determining a weak outlier 
	 * on the upper side for all split classes, i.e., weak outliers
	 * are larger than this value and lower than the value that determines
	 * a strong outlier.
	 * 
	 * @return
	 */
	public ArrayList<Double> getOutlierWeakMaxValues()
	{
		return this.outlierWeakMaxValues;
	}

	/**
	 * Get the value determining a weak outlier 
	 * on the upper side for the given split class, i.e., weak outliers
	 * are larger than this value and lower than the value that determines
	 * a strong outlier.
	 * 
	 * @param splitClass
	 * @return
	 */
	public Double getOutlierWeakMaxValue(int splitClass)
	{
		return this.outlierWeakMaxValues.get(splitClass);
	}

	/**
	 * Get the array of the values determining a strong outlier 
	 * on the upper side for all split classes, i.e., strong outliers
	 * on the upper side are larger than this value.
	 * 
	 * @return
	 */
	public ArrayList<Double> getOutlierStrongMaxValues()
	{
		return this.outlierStrongMaxValues;
	}

	/**
	 * Get the value determining a strong outlier 
	 * on the upper side for the given split class, i.e., strong outliers
	 * on the upper side are larger than this value.
	 * 
	 * @param splitClass
	 * @return
	 */
	public Double getOutlierStrongMaxValue(int splitClass)
	{
		return this.outlierStrongMaxValues.get(splitClass);
	}

	/**
	 * Get the array of outlier values on the lower side for all split classes.
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Double>> getOutlierMinValues()
	{
		return this.outlierMinValues;
	}

	/**
	 * Get the array of outlier values on the lower side for the given split classes.
	 * 
	 * @param splitClass
	 * @return
	 */
	public ArrayList<Double> getOutlierMinValue(int splitClass)
	{
		if (this.outlierMinValues != null && this.outlierMinValues.size() > 0)
			return this.outlierMinValues.get(splitClass);
		else
			return null;
	}

	/**
	 * Get the array of outlier values on the upper side for all split classes.
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Double>> getOutlierMaxValues()
	{
		return this.outlierMaxValues;
	}

	/**
	 * Get the array of outlier values on the upper side for the given split classes.
	 * 
	 * @param splitClass
	 * @return
	 */
	public ArrayList<Double> getOutlierMaxValue(int splitClass)
	{
		if (this.outlierMaxValues != null && this.outlierMaxValues.size() > 0)
			return this.outlierMaxValues.get(splitClass);
		else
			return null;
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
	 * @param splitClass
	 * @return
	 */
	public Double getLowerQuartile(int splitClass)
	{
		return lowerQuartiles.get(splitClass);
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
	 * @param splitClass
	 * @return
	 */
	public Double getMedian(int splitClass)
	{
		return medians.get(splitClass);
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
	 * @param splitClass
	 * @return
	 */
	public Double getUpperQuartile(int splitClass)
	{
		return upperQuartiles.get(splitClass);
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
	 * @param splitClass
	 * @return
	 */
	public Double getMaxValue(int splitClass)
	{
		return maxValues.get(splitClass);
	}

	/**
	 * Get the number of split classes.
	 * 
	 * @return
	 */
	public int getNumberOfSplitClasses()
	{
		return this.statTableModel.numberOfSplitVarClasses(this.splitOptions);
	}

	/**
	 * Calculates the percentile values, and the outliers. 
	 * If there is a split, there is an array of values for each split.
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

		// initialize
		this.outlierStrongMinValues = new ArrayList<Double>();
		this.outlierWeakMinValues = new ArrayList<Double>();
		this.outlierMinValues = new ArrayList<ArrayList<Double>>();
		this.minValues = new ArrayList<Double>();
		this.lowerQuartiles = new ArrayList<Double>();
		this.medians = new ArrayList<Double>();
		this.upperQuartiles = new ArrayList<Double>();
		this.maxValues = new ArrayList<Double>();
		this.outlierMaxValues = new ArrayList<ArrayList<Double>>();
		this.outlierWeakMaxValues = new ArrayList<Double>();
		this.outlierStrongMaxValues = new ArrayList<Double>();

		if (!this.getStatTableModel().isColumnIndexValid(
			this.splitOptions.getColumnSplitIndex()))
		{
			// no split
			
			ArrayList<Double> data = new ArrayList<Double>();

			for (int i = 0; i < this.statTableModel.getRowCount(); i++)
			{
				String valueString = (String) this.statTableModel.getValueAt(i,
					columnIndex);
				if (!valueString.equals(ColumnType.WILDCARD)
					&& !this.getStatTableModel().isOutlier(i, columnIndex))
				{
					// get the value
					Double d = Double.parseDouble(valueString);

					// add the value to a list
					data.add(d);
				}
			}

			Collections.sort(data);
			int size = data.size();
			if (size == 0)
			{
				this.outlierStrongMinValues.add(null);
				this.outlierWeakMinValues.add(null);
				this.outlierMinValues.add(null);
				this.minValues.add(null);
				this.lowerQuartiles.add(null);
				this.medians.add(null);
				this.upperQuartiles.add(null);
				this.maxValues.add(null);
				this.outlierMaxValues.add(null);
				this.outlierWeakMaxValues.add(null);
				this.outlierStrongMaxValues.add(null);
				this.dataMinValue = null;
				this.dataMaxValue = null;
			}
			else
			{
				ArrayList<Double> lowerDataHalf = new ArrayList<Double>(data.subList(0, (int) Math.ceil(0.5 * size)));
				this.lowerQuartiles
					.add(this.determineMedian(lowerDataHalf));
				
				addMedian(data);
				
				int fromIndex;
				if (size % 2 == 0) // even
					fromIndex = (int) Math.ceil(0.5 * size);
				else
					fromIndex = (int) Math.ceil(0.5 * size) - 1;
				ArrayList<Double> upperDataHalf = new ArrayList<Double>(data.subList(fromIndex, size));
				this.upperQuartiles
					.add(this.determineMedian(upperDataHalf));

				// Voor tukey boxplot andere waarden bij de uiteinden, afhankelijk van lowerQuartile en upperQuartile
				Double minValue;
				Double maxValue;
				if (this.isTukeyBox())
				{
					// determine the inter quartile range Q3 - Q1
					Double q1 = lowerQuartiles.get(0);
					Double q3 = upperQuartiles.get(0);
					double iQR = q3 - q1;
					Double tukeyMinValue = q1 - 1.5 * iQR;
					Double tukeyMaxValue = q3 + 1.5 * iQR;
					this.outlierWeakMinValues.add(tukeyMinValue);
					this.outlierStrongMinValues.add(q1 - 3 * iQR);
					this.outlierWeakMaxValues.add(tukeyMaxValue);
					this.outlierStrongMaxValues.add(q3 + 3 * iQR);

					minValue = this.getObservedTukeyMinValue(tukeyMinValue, data);
					maxValue = this.getObservedTukeyMaxValue(tukeyMaxValue, data);
					
					this.setOutlierValuesNoSplit(data);
				}
				else
				{
					minValue = data.get(0);
					maxValue = data.get(size - 1);
				}
				
				this.minValues.add(minValue);
				this.maxValues.add(maxValue);
			}
			
			if (size > 0)
			{
				this.dataMinValue = data.get(0);
				this.dataMaxValue = data.get(size - 1);
			}
			
		} // no split
		else
		{
			// there is a split
			
			int numberOfSplitClasses = this.getNumberOfSplitClasses();

			ArrayList<ArrayList<Double>> sortedData = new ArrayList<ArrayList<Double>>();
			for (int i = 0; i < numberOfSplitClasses; i++)
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
					&& !valueString.equals(ColumnType.WILDCARD)
					&& !this.getStatTableModel().isOutlier(i, columnIndex)
					&& !this.getStatTableModel().isOutlier(i, this.splitOptions.getColumnSplitIndex()))
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

			boolean initialized = false;
			
			for (int i = 0; i < numberOfSplitClasses; i++)
			{
				Collections.sort(sortedData.get(i));
				int size = sortedData.get(i).size();
				if (size == 0)
				{
					this.outlierStrongMinValues.add(null);
					this.outlierWeakMinValues.add(null);
					this.minValues.add(null);
					this.lowerQuartiles.add(null);
					this.medians.add(null);
					this.upperQuartiles.add(null);
					this.maxValues.add(null);
					this.outlierWeakMaxValues.add(null);
					this.outlierStrongMaxValues.add(null);
				}
				else
				{
					this.lowerQuartiles.add(sortedData.get(i).get(
						(int) Math.ceil(0.25 * size) - 1));
					
					addMedian(sortedData.get(i));
					
					this.upperQuartiles.add(sortedData.get(i).get(
						(int) Math.ceil(0.75 * size) - 1));

					// Voor tukey boxplot andere waarden bij de uiteinden, afhankelijk van lowerQuartile en upperQuartile
					Double minValue;
					Double maxValue;
					if (this.isTukeyBox())
					{
						// determine the inter quartile range Q3 - Q1
						Double q1 = lowerQuartiles.get(i);
						Double q3 = upperQuartiles.get(i);
						double iQR = q3 - q1;
						Double tukeyMinValue = q1 - 1.5 * iQR;
						Double tukeyMaxValue = q3 + 1.5 * iQR;
						this.outlierWeakMinValues.add(tukeyMinValue);
						this.outlierStrongMinValues.add(q1 - 3 * iQR);
						this.outlierWeakMaxValues.add(tukeyMaxValue);
						this.outlierStrongMaxValues.add(q3 + 3 * iQR);
						
						minValue = this.getObservedTukeyMinValue(tukeyMinValue, sortedData.get(i));
						maxValue = this.getObservedTukeyMaxValue(tukeyMaxValue, sortedData.get(i));
					}
					else
					{
						minValue = sortedData.get(i).get(0);
						maxValue = sortedData.get(i).get(size - 1);
					}
					this.minValues.add(minValue);
					this.maxValues.add(maxValue);
				}
				
				if (!initialized && (size > 0))
				{
					// initial values
					this.dataMinValue = sortedData.get(i).get(0);
					this.dataMaxValue = sortedData.get(i).get(size - 1);
					
					initialized = true;
				}
				else if (size > 0)
				{
					if (this.dataMinValue > sortedData.get(i).get(0))
					{
						this.dataMinValue = sortedData.get(i).get(0);
					}
					if (this.dataMaxValue < sortedData.get(i).get(size - 1))
					{
						this.dataMaxValue = sortedData.get(i).get(size - 1);
					}
				}
			} // for loop over split classes

			if (this.isTukeyBox())
			{
				this.setOutlierValuesForEachSplit(sortedData);
			}
		} // there is a split
	}
	
	/**
	 * Set the outlier values based on the values for weak and strong outliers
	 * as set in the fields, for the given sorted data for each split.
	 * 
	 * @param sortedData
	 */
	private void setOutlierValuesForEachSplit(ArrayList<ArrayList<Double>> sortedData)
	{
		ArrayList<Double> minList = new ArrayList<Double>();
		ArrayList<Double> maxList = new ArrayList<Double>();
		ArrayList<Double> splitData;

		for (int splitClass = 0; splitClass < sortedData.size(); splitClass++)
		{
			splitData = sortedData.get(splitClass);
			minList = new ArrayList<Double>();
			maxList = new ArrayList<Double>();
			
			for (int i = 0; i < splitData.size(); i++)
			{
				if (splitData.get(i) < this.outlierWeakMinValues.get(splitClass))
				{
					minList.add(splitData.get(i));
				}
				else if (splitData.get(i) > this.outlierWeakMaxValues.get(splitClass))
				{
					maxList.add(splitData.get(i));
				}
			}
			
			this.outlierMinValues.add(minList);
			this.outlierMaxValues.add(maxList);

		}
	}

	/**
	 * Set the outlier values based on the values for weak and strong outliers
	 * as set in the fields, for the given sorted data. There is no split.
	 * 
	 * @param sortedData
	 */
	private void setOutlierValuesNoSplit(ArrayList<Double> sortedData)
	{
		ArrayList<Double> minList = new ArrayList<Double>();
		ArrayList<Double> maxList = new ArrayList<Double>();
		
		for (int i = 0; i < sortedData.size(); i++)
		{
			if (sortedData.get(i) < this.outlierWeakMinValues.get(0))
			{
				minList.add(sortedData.get(i));
			}
			else if (sortedData.get(i) > this.outlierWeakMaxValues.get(0))
			{
				maxList.add(sortedData.get(i));
			}
		}
		
		this.outlierMinValues.add(minList);
		this.outlierMaxValues.add(maxList);
	}

	/**
	 * Get the first observed value greater than or equal to the given value.
	 * 
	 * @param value
	 * @param observedValues
	 * @return
	 */
	private Double getObservedTukeyMinValue(Double value, ArrayList<Double> observedValues)
	{
		Double observedMinValue = null;
		
		for (int i = 0; i < observedValues.size(); i++)
		{
			if (value <= observedValues.get(i))
			{
				// the first observed value greater than value has been found
				observedMinValue = observedValues.get(i);
				break;
			}
		}
		
		return observedMinValue;
	}
	
	/**
	 * Get the first observed value smaller than or equal to the given value.
	 * 
	 * @param value
	 * @param observedValues
	 * @return
	 */
	private Double getObservedTukeyMaxValue(Double value, ArrayList<Double> observedValues)
	{
		Double observedMaxValue = null;
		
		for (int i = observedValues.size() - 1; i > 0 ; i--)
		{
			if (value >= observedValues.get(i))
			{
				// the first observed value greater than value has been found
				observedMaxValue = observedValues.get(i);
				break;
			}
		}
		
		return observedMaxValue;
	}
	
	/**
	 * Determine the median of the given data set.
	 * 
	 * @param data
	 * @return
	 */
	private Double determineMedian(ArrayList<Double> data)
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
		
		return median;
	}
	
	/*
	 * Determine the median of the data set and 
	 * add to medians.
	 */
	private void addMedian (ArrayList<Double> data)
	{
		Double median = this.determineMedian(data);
		
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
	
	/**
	 * Returns whether the boxplot is empty with respect to the boxplot for the selected variable.
	 * 
	 * @return
	 */
	public boolean isEmptyBoxplot()
	{
		return (this.getDataMinValue() == null);
	}

	public Boolean isTukeyBox()
	{
		return this.isTukeyBox;
	}
	
	/**
	 * Returns whether the Tukey boxplot has lower outliers (strong or weak).
	 * 
	 * @param splitClass
	 * @return
	 */
	public boolean hasLowerOutliers(int splitClass)
	{
		boolean b = false;
		
		if (this.isTukeyBox() && this.outlierMinValues.get(splitClass) != null && this.outlierMinValues.get(splitClass).size() > 0)
		{
			b = true;
		}
		
		return b;
	}
	
	/**
	 * Returns whether the Tukey boxplot has upper outliers (strong or weak).
	 * 
	 * @param splitClass
	 * @return
	 */
	public boolean hasUpperOutliers(int splitClass)
	{
		boolean b = false;
		
		if (this.isTukeyBox() && this.outlierMaxValues.get(splitClass) != null && this.outlierMaxValues.get(splitClass).size() > 0)
		{
			b = true;
		}
		
		return b;
	}
	
	void setIsTukeyBox(boolean b)
	{
		this.isTukeyBox = b;
	}
}
