package fi.statistiekgwt.client.crosstabulationtable;

import java.util.ArrayList;

import fi.statistiekgwt.client.SplitOptions;
import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekGWT;

// use some features from histogram
import fi.statistiekgwt.client.histogram.HistogramModel.FrequencyTuple;

/**
 * MVC model for StatistiekView CrossTabulationTable
 * 
 * @author Sylvia van Borkulo
 * 
 */
public class CrossTabulationTableModel
{
	static int DEFAULT_NUMBER_OF_BINS = 6;
	private int columnIndex;
	private boolean showPercentage;
	private boolean showPercentage_endTotal;
	private boolean showPercentage_rowTotal;
	private boolean showPercentage_columnTotal;

	private int noBins;
	private ArrayList<Double> binBoundaries;
	
	private SplitOptions splitOptions;

	private StatTableModel tableModel;
	private String viewName;

	/**
	 * Constructor
	 * 
	 * @param tableModel
	 *            the data model
	 * @param viewName
	 *            the initial name of this view
	 */
	public CrossTabulationTableModel(StatTableModel tableModel, String viewName)
	{
		this.tableModel = tableModel;
		
		this.splitOptions = new SplitOptions();

		this.viewName = viewName;

		// set initial values
		this.noBins = 10;
		this.columnIndex = -1;
		this.binBoundaries = new ArrayList<Double>();
		this.binBoundaries.add(new Double(-100));
		this.binBoundaries.add(new Double(100));

		this.showPercentage = false;
		this.showPercentage_endTotal = true;
		this.showPercentage_rowTotal = false;
		this.showPercentage_columnTotal = false;
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

	boolean isShowPercentage()
	{
		return this.showPercentage;
	}

	boolean isShowPercentage_endTotal()
	{
		return this.showPercentage_endTotal;
	}

	boolean isShowPercentage_rowTotal()
	{
		return this.showPercentage_rowTotal;
	}

	boolean isShowPercentage_columnTotal()
	{
		return this.showPercentage_columnTotal;
	}

	void setShowPercentage(boolean b)
	{
		this.showPercentage = b;
	}

	void setShowPercentage_endTotal(boolean b)
	{
		this.showPercentage_endTotal = b;
	}

	void setShowPercentage_rowTotal(boolean b)
	{
		this.showPercentage_rowTotal = b;
	}

	void setShowPercentage_columnTotal(boolean b)
	{
		this.showPercentage_columnTotal = b;
	}

	/**
	 * @return number of bins
	 */
	public int getNoBins()
	{
		return this.noBins;
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
		if (!(this.tableModel == tableModel))
		{
			this.tableModel = tableModel;
		}
	}

	/**
	 * @return the data table
	 */
	public StatTableModel getStatTableModel()
	{
		return this.tableModel;
	}

	/**
	 * Set the column index, i.e. for the rows, in the crosstabulation table.
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
				&& this.tableModel.getColumnTypes().get(this.columnIndex)
					.getType().isNumber())
			{
				this.binBoundaries = StatistiekGWT.appropriateBoundaries(
					this.tableModel.getColumnMin(this.columnIndex),
					this.tableModel.getColumnMax(this.columnIndex),
					CrossTabulationTableModel.DEFAULT_NUMBER_OF_BINS);
				
				// opnieuw berekenen bins met de hierboven berekende binboundaries
				int bin0Decimals = StatistiekGWT.getNumberOfDecimals(this.binBoundaries.get(0).toString());
				int bin1Decimals = StatistiekGWT.getNumberOfDecimals(this.binBoundaries.get(1).toString());
				int maxNumberOfDecimals = Math.max(bin0Decimals, bin1Decimals);
				
				this.binBoundaries = StatistiekGWT.appropriateBoundariesFromBinSettings(this.tableModel.getColumnMin(this.columnIndex),
					this.tableModel.getColumnMax(this.columnIndex), 
					// door afronding kan de aftreksom heel veel decimalen hebben
					StatistiekGWT.round(this.binBoundaries.get(1) - this.binBoundaries.get(0), maxNumberOfDecimals), this.binBoundaries.get(0));
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
		return (this.columnIndex >= 0
			&& this.columnIndex < this.tableModel.getColumnCount());
	}

	/**
	 * Determines the bin in which double d is contained. The upper bin boundary 
	 * is exclusive.
	 * @param d
	 * @return
	 */
	public int binOfNumber(double d)
	{
		int bin = 0;
		int i = 0;
		
		while (i < this.binBoundaries.size()
			&& d >= this.binBoundaries.get(i))
		{
			i++;
		}
		
		bin = i-1;

		return bin;
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
		return this.tableModel.numberClassFrequency(this.binBoundaries,
			this.columnIndex, this.splitOptions);
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
		return this.tableModel.enumClassFrequency(this.columnIndex, this.splitOptions);
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

	public void swapVariables()
	{
		// index old row variable
		int indexRow_old = this.getColumnIndex();
		// bins of the old row variable
		ArrayList<Double> binBoundariesRow_old = getBinBoundaries();

		// index old column variable
		int indexColumn_old = this.getColumnSplitIndex();
		// split options of the column variable
		SplitOptions splitOptionsColumn_old = this.getSplitOptions();
		
		// Set as the new row variable the column variable with its bins
		this.setColumnIndex(indexColumn_old);
		this.setBinBoundaries(splitOptionsColumn_old.getBinBoundaries());
		
		// Set as the new column variable the row variable with its bins
		// setSplit() uit controller
		this.setColumnSplitIndex(indexRow_old);
		this.setSplitBoundaries(binBoundariesRow_old);
		// test syl: onderstaande is niet nodig?
		//this.setSplitOptions(this.getSplitOptions());
	}
}
