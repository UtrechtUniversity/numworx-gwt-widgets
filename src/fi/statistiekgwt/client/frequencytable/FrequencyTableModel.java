package fi.statistiekgwt.client.frequencytable;

import java.util.ArrayList;
import java.util.Observable;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import fi.statistiekgwt.client.SplitOptions;
import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.histogram.HistogramModel.FrequencyTuple;

/**
 * MVC model for StatistiekView FrequencyTable
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class FrequencyTableModel
{
	private int columnIndex;
	private boolean showPercentage;
	private boolean showFreqCumulative;

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
	public FrequencyTableModel(StatTableModel tableModel, String viewName)
	{
		this.tableModel = tableModel;
		
		this.splitOptions = new SplitOptions();

		this.viewName = viewName;

		// set initial values
		// initialize number of bins with an invalid value
		this.noBins = -1;
		this.columnIndex = -1;
		this.binBoundaries = new ArrayList<Double>();
		this.binBoundaries.add(new Double(-100));
		this.binBoundaries.add(new Double(100));

		this.showPercentage = true;
		this.showFreqCumulative = true;
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

	void setShowPercentage(boolean b)
	{
		this.showPercentage = b;
	}

	boolean isShowCumulative()
	{
		return this.showFreqCumulative;
	}

	void setShowCumulative(boolean b)
	{
		this.showFreqCumulative = b;
	}
	
	/**
	 * Initialize the number of bins without updating other fields.
	 * @param noBins
	 */
	public void initNoBins(int noBins)
	{
		this.noBins = noBins;
	}

	/**
	 * Set the number of bins and update bin boundaries.
	 * 
	 * @param noBins
	 *            the new number of bins
	 */
	public void setNoBins(int noBins)
	{
		this.noBins = noBins;

		// set appropriate boundaries
		if (this.tableModel.getRowCount() > 0 && this.columnIndexValid())
		{
			double min = this.tableModel.getColumnMin(this.columnIndex);
			double max = this.tableModel.getColumnMax(this.columnIndex);
			this.binBoundaries = StatistiekGWT.appropriateBoundaries(min, max,
				noBins);
		}
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
	 * Set the column of the datatable that this StatistiekView will show
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
				if (this.noBins == -1)
					this.noBins = 6; // default
				this.binBoundaries = StatistiekGWT.appropriateBoundaries(
					this.tableModel.getColumnMin(this.columnIndex),
					this.tableModel.getColumnMax(this.columnIndex),
					this.noBins);
				
				// opnieuw berekenen met de berekende binboundaries
				int bin0Decimals = StatistiekGWT.getNumberOfDecimals(this.binBoundaries.get(0).toString());
				int bin1Decimals = StatistiekGWT.getNumberOfDecimals(this.binBoundaries.get(1).toString());
				int maxNumberOfDecimals = Math.max(bin0Decimals, bin1Decimals);

				this.binBoundaries = StatistiekGWT.appropriateBoundariesFromBinSettings(
					this.tableModel.getColumnMin(this.columnIndex),
					this.tableModel.getColumnMax(this.columnIndex), 
					// door afronding kan de aftreksom heel veel decimalen hebben
					StatistiekGWT.round(this.binBoundaries.get(1) - this.binBoundaries.get(0), 
						maxNumberOfDecimals), this.binBoundaries.get(0));
				this.noBins = this.binBoundaries.size() - 1;
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
	 * this bin Only use for columns of type integer or double
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
	 * Find the frequency of every class Only use for columns of type enum of
	 * string
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

	public void setSplitBoundaries(ArrayList<Double> boundaries)
	{
		this.splitOptions.setBinBoundaries(boundaries);
	}
}
