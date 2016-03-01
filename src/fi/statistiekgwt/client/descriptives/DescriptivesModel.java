package fi.statistiekgwt.client.descriptives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fi.statistiekgwt.client.SplitOptions;
import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekGWT;
// use some features from histogram
import fi.statistiekgwt.client.histogram.HistogramModel.FrequencyTuple;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * MVC model for StatistiekView Descriptives
 * 
 * @author Sylvia van Borkulo
 * 
 */
public class DescriptivesModel
{
	private int columnIndex;
	/**
	 * Bin boundaries (by default one bin)
	 * in order to be able to get the frequencies.
	 */
	private ArrayList<Double> binBoundaries;
	private int noBins;
	
	private SplitOptions splitOptions;

	private StatTableModel statTableModel;
	private String viewName;

	/**
	 * Constructor
	 * 
	 * @param tableModel
	 *            the data model
	 * @param viewName
	 *            the initial name of this view
	 */
	public DescriptivesModel(StatTableModel tableModel, String viewName)
	{
		this.statTableModel = tableModel;
		
		this.splitOptions = new SplitOptions();

		this.viewName = viewName;

		// set initial values
		this.columnIndex = -1;
		this.noBins = 1;
		this.binBoundaries = new ArrayList<Double>();
		this.binBoundaries.add(new Double(-100)); // why??
		this.binBoundaries.add(new Double(100)); // why??
	}

	/**
	 * Get the bin boundaries
	 * 
	 * @return The bin boundaries
	 */
	public ArrayList<Double> getBinBoundaries()
	{
		return this.binBoundaries;
	}

	/**
	 * Set the bin boundaries
	 * 
	 * @param bins
	 *            The new bin boundaries
	 */
	public void setBinBoundaries(ArrayList<Double> bins)
	{
		this.binBoundaries = bins;
		
		this.noBins = this.binBoundaries.size() - 1;
	}

	/**
	 * Set the name of this StatistiekView
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
	 * @return this StatistiekView's name
	 */
	public String getViewName()
	{
		return this.viewName;
	}

	/**
	 * Set the data table
	 * 
	 * @param tableModel
	 *            the new data table
	 */
	public void setTableModel(StatTableModel tableModel)
	{
		if (!(this.statTableModel == tableModel))
		{
			this.statTableModel = tableModel;
		}
	}

	/**
	 * @return the data table
	 */
	public StatTableModel getStatTableModel()
	{
		return this.statTableModel;
	}

	/**
	 * Set the column index in the descriptives table.
	 * 
	 * @param columnIndex
	 *            The index of the column that will be shown
	 */
	public void setColumnIndex(int columnIndex)
	{
		if (!(this.columnIndex == columnIndex))
		{
			this.columnIndex = columnIndex;
			
			if (this.columnIndexValid()
				&& this.statTableModel.getColumnTypes().get(this.columnIndex)
					.getType().isNumber())
			{
				this.binBoundaries = StatistiekGWT.appropriateBoundaries(
					this.statTableModel.getColumnMin(this.columnIndex),
					this.statTableModel.getColumnMax(this.columnIndex),
					1);
			}
		}
	}

	/**
	 * @return The index of the column that is represented by this
	 *         StatistiekView
	 */
	public int getColumnIndex()
	{
		return this.columnIndex;
	}

	/**
	 * Check if the current column index is a valid column index
	 * 
	 * @return true iff valid
	 */
	public boolean columnIndexValid()
	{
		boolean valid = false;
		
		if (this.statTableModel != null)
		{
			valid = (this.columnIndex >= 0
				&& this.columnIndex < this.statTableModel.getColumnCount()); 
		}
		return valid;
	}

	/**
	 * Find the frequency of every bin, and the amount of selected objects in
	 * this bin. To be used only for columns of type integer or double.
	 * 
	 * @return array of frequencies, with index 2*i the frequency of bin i, and
	 *         2*i + 1 the amount of selected items in this bin.
	 */
	public int[][] numberClassFrequency()
	{
		return (this.statTableModel.numberClassFrequency(this.binBoundaries,
			this.columnIndex, this.splitOptions));
	}

	/**
	 * Find the frequency of every class. To be used only for columns of type enum or
	 * string.
	 * 
	 * @return array of FrequencyTuples (which contains class label and
	 *         frequency)
	 */
	public FrequencyTuple[][] enumClassFrequency()
	{
		return this.statTableModel.enumClassFrequency(this.columnIndex, this.splitOptions);
	}

	/**
	 * Update the column index 
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
			setColumnSplitIndex(this.splitOptions.getColumnSplitIndex() - 1);
		}
		else if (removedColumn == this.splitOptions.getColumnSplitIndex())
		{
			setColumnSplitIndex(- 1);
		}
	}
	
	public SplitOptions getSplitOptions()
	{
		return this.splitOptions;
	}
	
	public void setSplitOptions(SplitOptions splitOptions)
	{
		this.splitOptions = splitOptions;
	}
	
	public void setColumnSplitIndex(int columnSplitIndex)
	{
		if (this.splitOptions.getColumnSplitIndex() != columnSplitIndex)
		{
			this.splitOptions.setColumnSplitIndex(columnSplitIndex);
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

	public void setSplitBoundaries(ArrayList<Double> boundaries)
	{
		this.splitOptions.setBinBoundaries(boundaries);
	}
	
	/**
	 * Get the mode of column columnIndex for the given split class.
	 * if there is no split, the mode of column columnIndex for the complete data set is
	 * returned.
	 * If forSelection is true, the mode is determined for the 
	 * selected values within the given split class.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @param splitClass
	 *            The split class
	 * @param forSelection
	 *            Only look at the selection y/n
	 * @return The mode of column columnIndex for the given split class. 
	 */
	public String getColumnMode(int columnIndex, int splitClass, boolean forSelection)
	{
		String mode = "";
		AllowedTypes type = this.statTableModel.getColumnTypes().get(columnIndex).getType();
		int splitColumnIndex = this.splitOptions.getColumnSplitIndex();
		
		if (splitColumnIndex == -1)
		{
			// there is no split
			if (forSelection)
			{
				mode = this.statTableModel.getColumnModeOfSelection(columnIndex);
			}
			else
			{
				mode = this.statTableModel.getColumnMode(columnIndex);
			}
		}
		else
		{ 
			// there is a split
			AllowedTypes splitType = this.statTableModel.getColumnTypes().get(splitColumnIndex).getType();
			int maxFreq = 0;
			boolean multipleModes = false;
			boolean valueInSplit = false;
			String valueString, splitValueString;
			
			ArrayList<String> data = new ArrayList<String>();
	
			for (int i = 0; i < this.statTableModel.getRowCount(); i++)
			{
				if ((forSelection && this.statTableModel.getSelectionList().get(i))
					|| (!forSelection))
				{
					valueString = (String) this.statTableModel.getValueAt(i, columnIndex);
					splitValueString = (String) this.statTableModel.getValueAt(i, splitColumnIndex);
		
					if (!valueString.equals(ColumnType.WILDCARD)
						&& !this.getStatTableModel().isOutlier(i, columnIndex))
					{
						valueInSplit = this.isValueInSplit(splitValueString, splitType, splitClass);
						
						if (valueInSplit)
						{
							// add the value to a list based on the splitclass
							data.add(valueString);
						}
					}
				}
			} // for loop over all data rows
				
			Collections.sort(data);
			
			// for-loop om de frequenties te berekenen
			Map<String, Integer> frequencyMap = new HashMap<String, Integer>();
			for (int i = 0; i < data.size(); i++)
			{
				Integer currentCount = frequencyMap.get(data.get(i));
				frequencyMap.put(data.get(i), (currentCount == null ? 1 : currentCount.intValue() + 1));
			}
			
			for (int i = 0; i < data.size(); i++)
			{
//				int freq_i = Collections.frequency(data, data.get(i)); // Collections.frequency() is traag voor grote datasets...
				int freq_i = frequencyMap.get(data.get(i)).intValue();
				if (freq_i > maxFreq)
				{
					maxFreq = freq_i;
					mode = String.valueOf(data.get(i));
					multipleModes = false;
				}
				else if ((freq_i != 0) && (freq_i == maxFreq) && (!data.get(i).equals(data.get(i - 1))))
				{ // check if there is another value with the same max frequency
					multipleModes = true;
				}
			}
			
			if (multipleModes || mode.equals(""))
			{
				mode = StatistiekGWT.rb.getString("notAvailable");
			}
		} // there is a split
		
		return mode;
	}	
	
	private boolean isValueInSplit(String splitValueString,
		AllowedTypes splitType, int splitClass)
	{
		boolean valueInSplit = false;
		
		if (splitType.isNumber())
		{
			// determine the split bin values
			Double splitClassMinValue = this.splitOptions.getBinBoundaries().get(splitClass); 
			Double splitClassMaxValue = this.splitOptions.getBinBoundaries().get(splitClass + 1);
			if (!splitValueString.equals(ColumnType.WILDCARD))
			{
				// get the value of the split column
				Double d_split = Double.parseDouble(splitValueString);

				// check if the value of the split column is in the split bin
				if ((d_split >= splitClassMinValue) && (d_split < splitClassMaxValue))
				{
					valueInSplit = true;
				}
			}
		}
		else
		{ // split type is string or enum
			if (splitValueString.equals(
				this.splitOptions.getSplitClassLabel(splitClass, this.statTableModel)))
			{
				valueInSplit = true;
			}
		}
		
		return valueInSplit;
	}

	/**
	 * Get the minimum value of column columnIndex for the given split class.
	 * If there is no split, the minimum value of column columnIndex
	 * for the complete data set is returned.
	 * If forSelection is true, the minimum value is determined for the 
	 * selected values within the given split class.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @param splitClass
	 *            The split class
	 * @param forSelection
	 *            Only look at the selection y/n
	 * @return 
	 * 		The minimum value of column columnIndex for the given split class.
	 * 		Returns "not available" if column is not numerical or 
	 * 		if no minimum can be calculated. 
	 */
	public String getColumnMin(int columnIndex, int splitClass, boolean forSelection)
	{
		double min;
		String minString = null;
		
		AllowedTypes type = this.statTableModel.getColumnTypes().get(columnIndex).getType();
		int splitColumnIndex = this.splitOptions.getColumnSplitIndex();
		
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			// type is not numerical
			minString = StatistiekGWT.rb.getString("notAvailable");
		}
		else if (splitColumnIndex == -1)
		{
			// there is no split
			if (forSelection)
			{
				minString = this.statTableModel.getColumnMinOfSelection(columnIndex);
			}
			else
			{
				minString = StatistiekGWT.getStringValue(this.statTableModel.getColumnMin(columnIndex));
			}
		}
		else
		{
			// there is a split
			AllowedTypes splitType = this.statTableModel.getColumnTypes().get(splitColumnIndex).getType();	
			Double minDouble = Double.MAX_VALUE;
			boolean valueInSplit = false;
			String valueString, splitValueString;

			for (int i = 0; i < this.statTableModel.getRowCount(); i++)
			{
				if ((forSelection && this.statTableModel.getSelectionList().get(i))
					|| (!forSelection))
				{
					valueString = (String) this.statTableModel.getValueAt(i, columnIndex);
					splitValueString = (String) this.statTableModel.getValueAt(i, splitColumnIndex);
	
					if (!valueString.equals(ColumnType.WILDCARD)
						&&!this.getStatTableModel().isOutlier(i, columnIndex))
					{
						valueInSplit = this.isValueInSplit(splitValueString, splitType, splitClass);
						
						if (valueInSplit)
						{
							Double d = Double.parseDouble(valueString);
							if (d < minDouble)
							{
								minDouble = d;
							}
						}
					}
				}
			}
			
			if (minDouble.equals(Double.MAX_VALUE))
			{
				// no minimum found
				minString = StatistiekGWT.rb.getString("notAvailable");
			}
			else
			{
				minString = StatistiekGWT.getStringValue(minDouble);
			}
		} // there is a split
		
		return minString;
	}

	/**
	 * Get the maximum value of column columnIndex for the given split class.
	 * If there is no split, the maximum value of column columnIndex
	 * for the complete data set is returned.
	 * If forSelection is true, the maximum value is determined for the 
	 * selected values within the given split class.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @param splitClass
	 *            The split class
	 * @param forSelection
	 *            Only look at the selection y/n
	 * @return 
	 * 		The maximum value of column columnIndex for the given split class.
	 * 		Returns 0 if column is not numerical. 
	 */
	public String getColumnMax(int columnIndex, int splitClass, boolean forSelection)
	{
//		double max;
		String maxString = null;

		AllowedTypes type = this.statTableModel.getColumnTypes().get(columnIndex).getType();
		int splitColumnIndex = this.splitOptions.getColumnSplitIndex();
		
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			// type is not numerical
			maxString = StatistiekGWT.rb.getString("notAvailable");
		}
		else if (splitColumnIndex == -1)
		{
			// there is no split
			if (forSelection)
			{
				maxString = this.statTableModel.getColumnMaxOfSelection(columnIndex);
			}
			else
			{
				maxString = StatistiekGWT.getStringValue(this.statTableModel.getColumnMax(columnIndex));
			}
		}
		else
		{
			// there is a split
			AllowedTypes splitType = this.statTableModel.getColumnTypes().get(splitColumnIndex).getType();	
			Double maxDouble = Double.MIN_VALUE;
			boolean valueInSplit = false;
			String valueString, splitValueString;

			for (int i = 0; i < this.statTableModel.getRowCount(); i++)
			{
				if ((forSelection && this.statTableModel.getSelectionList().get(i))
					|| (!forSelection))
				{
					valueString = (String) this.statTableModel.getValueAt(i, columnIndex);
					splitValueString = (String) this.statTableModel.getValueAt(i, splitColumnIndex);
	
					if (!valueString.equals(ColumnType.WILDCARD)
						&& !this.getStatTableModel().isOutlier(i, columnIndex))
					{
						valueInSplit = this.isValueInSplit(splitValueString, splitType, splitClass);
						
						if (valueInSplit)
						{
							Double d = Double.parseDouble(valueString);
							if (d > maxDouble)
							{
								maxDouble = d;
							}
						}
					}
				}
			}
			
			if (maxDouble.equals(Double.MIN_VALUE))
			{
				// no maximum found
				maxString = StatistiekGWT.rb.getString("notAvailable");
			}
			else
			{
				maxString = StatistiekGWT.getStringValue(maxDouble);
			}
		} // there is a split
		
		return maxString;
	}

	/**
	 * Get the mean value of column columnIndex for the given split class.
	 * The returned string contains a double with language specific separator.
	 * 
	 * If there is no split, the mean value of column columnIndex
	 * for the complete data set is returned.
	 * If forSelection is true, the mean value is determined for the 
	 * selected values within the given split class.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @param splitClass
	 *            The split class
	 * @param forSelection
	 *            Only look at the selection y/n
	 * @return 
	 * 		The mean value of column columnIndex for the given split class.
	 * 		Returns "Not available" if column is not numerical or if the mean cannot be calculated.
	 */
	public String getColumnMean(int columnIndex, int splitClass, boolean forSelection)
	{
//		double mean;
		String meanString = null;

		AllowedTypes type = this.statTableModel.getColumnTypes().get(columnIndex).getType();
		int splitColumnIndex = this.splitOptions.getColumnSplitIndex();
		
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			// type is not numerical
			meanString = StatistiekGWT.rb.getString("notAvailable");
		}
		else if (splitColumnIndex == -1)
		{
			// there is no split
			if (forSelection)
			{
				meanString = this.statTableModel.getColumnMeanOfSelection(columnIndex);
			}
			else
			{
				meanString = StatistiekGWT.getStringValue(this.statTableModel.getColumnMean(columnIndex));
			}
		}
		else
		{
			// there is a split
			AllowedTypes splitType = this.statTableModel.getColumnTypes().get(splitColumnIndex).getType();	
			boolean valueInSplit = false;
			String valueString, splitValueString;

			Double sum = 0.0;
			int count = 0; // number of valid values
			for (int i = 0; i < this.statTableModel.getRowCount(); i++)
			{
				if ((forSelection && this.statTableModel.getSelectionList().get(i))
					|| (!forSelection))
				{
					valueString = (String) this.statTableModel.getValueAt(i, columnIndex);
					splitValueString = (String) this.statTableModel.getValueAt(i, splitColumnIndex);
	
					if (!valueString.equals(ColumnType.WILDCARD)
						&&!this.getStatTableModel().isOutlier(i, columnIndex))
					{
						valueInSplit = this.isValueInSplit(splitValueString, splitType, splitClass);
						
						if (valueInSplit)
						{
							Double d = Double.parseDouble(valueString);
							sum += d;
							count++;
						}
					}
				}
			}
			
			if (count > 0)
			{
				meanString = StatistiekGWT.getStringValue(sum/count);
			}
			else
			{
				meanString = StatistiekGWT.rb.getString("notAvailable");
			}
		} // there is a split
		
		return meanString;
	}

	/**
	 * Get the mean value of column columnIndex for the given split class.
	 * The returned string contains a double without language specific separator.
	 * This method is used when the mean value is used in a calculation.
	 * 
	 * If there is no split, the mean value of column columnIndex
	 * for the complete data set is returned.
	 * If forSelection is true, the mean value is determined for the 
	 * selected values within the given split class.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @param splitClass
	 *            The split class
	 * @param forSelection
	 *            Only look at the selection y/n
	 * @return 
	 * 		The mean value of column columnIndex for the given split class.
	 * 		Returns "Not available" if column is not numerical or if the mean cannot be calculated.
	 */
	public String getColumnMeanDoubleValue(int columnIndex, int splitClass, boolean forSelection)
	{
		double mean;
		String meanString = null;

		AllowedTypes type = this.statTableModel.getColumnTypes().get(columnIndex).getType();
		int splitColumnIndex = this.splitOptions.getColumnSplitIndex();
		
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			// type is not numerical
			meanString = StatistiekGWT.rb.getString("notAvailable");
		}
		else if (splitColumnIndex == -1)
		{
			// there is no split
			if (forSelection)
			{
				meanString = this.statTableModel.getColumnMeanOfSelectionDoubleValue(columnIndex);
			}
			else
			{
				meanString = String.valueOf(this.statTableModel.getColumnMean(columnIndex));
			}
		}
		else
		{
			// there is a split
			AllowedTypes splitType = this.statTableModel.getColumnTypes().get(splitColumnIndex).getType();	
			boolean valueInSplit = false;
			String valueString, splitValueString;

			Double sum = 0.0;
			int count = 0; // number of valid values
			for (int i = 0; i < this.statTableModel.getRowCount(); i++)
			{
				if ((forSelection && this.statTableModel.getSelectionList().get(i))
					|| (!forSelection))
				{
					valueString = (String) this.statTableModel.getValueAt(i, columnIndex);
					splitValueString = (String) this.statTableModel.getValueAt(i, splitColumnIndex);
	
					if (!valueString.equals(ColumnType.WILDCARD)
						&&!this.getStatTableModel().isOutlier(i, columnIndex))
					{
						valueInSplit = this.isValueInSplit(splitValueString, splitType, splitClass);
						
						if (valueInSplit)
						{
							Double d = Double.parseDouble(valueString);
							sum += d;
							count++;
						}
					}
				}
			}
			
			if (count > 0)
			{
				meanString = String.valueOf(sum/count);
			}
			else
			{
				meanString = StatistiekGWT.rb.getString("notAvailable");
			}
		} // there is a split
		
		return meanString;
	}

	/**
	 * Get the standard deviation of column columnIndex, excluding missing values,
	 * for the given split class.
	 * If there is no split, the standard deviation of column columnIndex
	 * for the complete data set is returned.
	 * If forSelection is true, the standard deviation is determined for the 
	 * selected values within the given split class.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @param splitClass
	 *            The split class
	 * @param forSelection
	 *            Only look at the selection y/n
	 * @return 
	 * 		The standard deviation of column columnIndex for the given split class. 
	 * 		Returns "Not available" if column is not numerical or if the standard deviation cannot be calculated.
	 */
	public String getColumnSD(int columnIndex, int splitClass, boolean forSelection)
	{
		String sdString = null;
		String notAvailable = StatistiekGWT.rb.getString("notAvailable");

//		double sd;
		AllowedTypes type = this.statTableModel.getColumnTypes().get(columnIndex).getType();
		int splitColumnIndex = this.splitOptions.getColumnSplitIndex();
		
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			// type is not numerical
			sdString = notAvailable;
		}
		else if (splitColumnIndex == -1)
		{
			// there is no split
			
			if (!forSelection)
			{
				sdString = StatistiekGWT.getStringValue(this.statTableModel.getColumnSD(columnIndex));
			}
			else
			{
				sdString = StatistiekGWT.getStringValue(this.statTableModel.getColumnSDOfSelection(columnIndex));
			}
		}
		else
		{
			// there is a split
			AllowedTypes splitType = this.statTableModel.getColumnTypes().get(splitColumnIndex).getType();	
			boolean valueInSplit = false;
			String valueString, splitValueString;

			Double sum = 0.0;
			int count = 0; // number of valid values
			double mean;
			
			String meanString = this.getColumnMeanDoubleValue(columnIndex, splitClass, forSelection);//this.getColumnMean(columnIndex, splitClass, forSelection); // hier zit een komma in!
			if (meanString.equals(notAvailable))
			{
				sdString = notAvailable;
			}
			else
			{
				mean = StatistiekGWT.parseDouble(meanString);
				for (int i = 0; i < this.statTableModel.getRowCount(); i++)
				{
					if ((forSelection && this.statTableModel.getSelectionList().get(i))
						|| (!forSelection))
					{
						valueString = (String) this.statTableModel.getValueAt(i, columnIndex);
						splitValueString = (String) this.statTableModel.getValueAt(i, splitColumnIndex);
		
						if (!valueString.equals(ColumnType.WILDCARD)
							&& !this.getStatTableModel().isOutlier(i, columnIndex))
						{
							valueInSplit = this.isValueInSplit(splitValueString, splitType, splitClass);
							
							if (valueInSplit)
							{
								Double d = Double.parseDouble(valueString);
								sum += Math.pow(d - mean, 2);
								count++;
							}
						}
					}
				}
				
				if (count > 0) // count == 0 kan eigenlijk niet voorkomen; als er een gemiddelde is, is er ook een SD
				{
					sdString = StatistiekGWT.getStringValue(Math.sqrt(sum/count));
				}
				else
				{
					sdString = StatistiekGWT.rb.getString("notAvailable");
				}
			}
		} // there is a split
		
		return sdString;
	}

	/**
	 * Get the median value of column columnIndex, excluding missing values,
	 * for the given split class.
	 * If there is no split, the median value of column columnIndex
	 * for the complete data set is returned.
	 * If forSelection is true, the median value is determined for the 
	 * selected values within the given split class.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @param splitClass
	 *            The split class
	 * @param forSelection
	 *            Only look at the selection y/n
	 * @return 
	 * 		The median value of column columnIndex for the given split class. 
	 * 		Returns 0 if column is not numerical or if the median value cannot be calculated.
	 */
	public double getColumnMedian(int columnIndex, int splitClass, boolean forSelection)
	{
		double median;
		AllowedTypes type = this.statTableModel.getColumnTypes().get(columnIndex).getType();
		int splitColumnIndex = this.splitOptions.getColumnSplitIndex();
		
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			// type is not numerical
			median = 0;
		}
		else if (splitColumnIndex == -1)
		{
			// there is no split
			if (forSelection)
			{
				median = this.statTableModel.getColumnMedianOfSelection(columnIndex);
			}
			else
			{
				median = this.statTableModel.getColumnMedian(columnIndex);
			}
		}
		else
		{
			// there is a split and column type is numerical
			AllowedTypes splitType = this.statTableModel.getColumnTypes().get(splitColumnIndex).getType();
			boolean valueInSplit = false;
			String valueString, splitValueString;
			
			ArrayList<Double> data = new ArrayList<Double>();
	
			for (int i = 0; i < this.statTableModel.getRowCount(); i++)
			{
				if ((forSelection && this.statTableModel.getSelectionList().get(i))
					|| (!forSelection))
				{
					valueString = (String) this.statTableModel.getValueAt(i, columnIndex);
					splitValueString = (String) this.statTableModel.getValueAt(i, splitColumnIndex);
		
					if (!valueString.equals(ColumnType.WILDCARD)
						&& !this.getStatTableModel().isOutlier(i, columnIndex))
					{
						valueInSplit = this.isValueInSplit(splitValueString, splitType, splitClass);
						
						if (valueInSplit)
						{
							// get the value
							Double d = Double.parseDouble(valueString);
	
							// add the value to a list based on the splitclass
							data.add(d);
						}
					}
				}
			} // for loop over all data rows

			Collections.sort(data);
			int size = data.size();
			int index;

			if ((size == 0) ||(size == 1))
			{
				median = 0;
			}
			else if (size % 2 == 0)
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
		} // there is a split
		
		return median;
	}
}
