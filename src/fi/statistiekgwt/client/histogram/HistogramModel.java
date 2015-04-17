package fi.statistiekgwt.client.histogram;

import java.util.ArrayList;

import fi.statistiekgwt.client.SplitOptions;
import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * MVC model for StatistiekView Histogram
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class HistogramModel
	//extends Observable implements TableModelListener, SelectionListener, StatBinsModel
{
	private StatTableModel statTableModel;
	private String viewName;
	private int columnIndex;

	private int noBins;
	private ArrayList<Double> binBoundaries;

	private SplitOptions splitOptions;

	private boolean percentage; // true = show percentage, false = show
								// frequency
	private boolean hasVerticalBars; // true = vertical bars, false = horizontal
								  // bars
	private boolean labelUnderBin; 	// true = show labels under bin, false = 
									// show labels between bins
	private boolean showUserOptions;
	private final boolean frequencyPolygonMode;
	private boolean frequencyPolygonCumulativeMode;

	/**
	 *  true for displaying multiple splitgroups in a single view,
	 *  false to display multiple splitgroups in multiple views in a scrollpane
	 */
	private boolean splitInSingleView;
	private boolean nextToEachOther;

	/**
	 *  true: display multiple cumulative frequencyPolygons stacked on top of each other
	 *  false: display multiple cumulative frequencyPolygons using mixing of colors
	 */
	private boolean frequencyPolygonStackMode;


	/**
	 * Constructor
	 * 
	 * @param tableModel
	 *            data table
	 * @param viewName
	 *            The initial name of this view
	 */
	public HistogramModel(StatTableModel tableModel, String viewName,
		boolean frequencyPolygonMode)
	{
		this.statTableModel = tableModel;

		this.splitOptions = new SplitOptions();

		this.viewName = viewName;

		// set initial values
		this.noBins = 10;
		this.columnIndex = -1;
		this.binBoundaries = new ArrayList<Double>();
		this.binBoundaries.add(new Double(-100));
		this.binBoundaries.add(new Double(100));
		this.percentage = false;
		this.hasVerticalBars = true;
		this.showUserOptions = false;

		this.frequencyPolygonMode = frequencyPolygonMode;
		this.frequencyPolygonCumulativeMode = false;
		this.splitInSingleView = true;
		this.frequencyPolygonStackMode = false;
	}

	public SplitOptions getSplitOptions()
	{
		return this.splitOptions;
	}

	public void setSplitOptions(SplitOptions splitOptions)
	{
		this.splitOptions = splitOptions;
	}

	/**
	 * Set the bin boundaries
	 * 
	 * @param bins
	 *            The new bin boundaries
	 */
	public void setBinBoundaries(ArrayList<Double> bins)
	{
		// Make a deep copy, not only copy the reference, since this will cause strange behavior
		ArrayList<Double> copy = new ArrayList<Double>(bins.size());
		for (Double d: bins)
		{
			copy.add(new Double(d));
		}
		
		this.binBoundaries = copy; 
			
		this.noBins = this.binBoundaries.size() - 1;
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

	public boolean isFrequencyPolygonMode()
	{
		return this.frequencyPolygonMode;
	}

	public boolean isFrequencyPolygonCumulativeMode()
	{
		return this.frequencyPolygonCumulativeMode;
	}

	public void setFrequencyPolygonCumulativeMode(boolean b)
	{
		if (b != this.frequencyPolygonCumulativeMode)
		{
			this.frequencyPolygonCumulativeMode = b;
		}
	}

	/**
	 * Set the number of bins
	 * 
	 * @param noBins
	 *            the new number of bins
	 */
	public void setNoBins(int noBins)
	{
		this.noBins = noBins;

		// set appropriate boundaries
		if (this.statTableModel.getRowCount() > 0 && this.columnIndexValid())
		{
			double min = this.statTableModel.getColumnMin(this.columnIndex);
			double max = this.statTableModel.getColumnMax(this.columnIndex);
			this.binBoundaries = StatistiekGWT.appropriateBoundaries(min, max,
				this.noBins);
			//System.out.println("... setNoBins(): boundaries=" + this.binBoundaries);
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
			
			if (this.columnIndexValid())
			{
				ArrayList<ColumnType> list = this.statTableModel.getColumnTypes();
				if (list.get(this.columnIndex)
					.getType().isNumber())
				{
    				// binBoundaries worden hier standaard gezet
    				this.binBoundaries = StatistiekGWT
    					.appropriateBoundaries(
    						this.statTableModel.getColumnMin(this.columnIndex),
    						this.statTableModel.getColumnMax(this.columnIndex),
    						this.noBins);
    				
    				// test syl: kan mooier, maar het werkt wel: opnieuw berekenen 
    				// met de berekende binboundaries, omdat er mogelijk minder bins nodig zijn
    				// TODO appropriateBoundaries(min, max) implementeren die binwidth en het aantal klassen bepaalt 
    				this.binBoundaries = StatistiekGWT.appropriateBoundariesFromBinSettings(this.statTableModel.getColumnMin(this.columnIndex),
    					this.statTableModel.getColumnMax(this.columnIndex), 
    					this.binBoundaries.get(1) - this.binBoundaries.get(0), this.binBoundaries.get(0));
    				
    				this.noBins = this.binBoundaries.size() - 1;
				}

				// set bin label positioning
				AllowedTypes type = list.get(this.columnIndex).getType(); 
				if (type.equals(AllowedTypes.INTEGER))
				{
					setLabelUnderBin(true);
				}
				else if (type.equals(AllowedTypes.DOUBLE))
				{
					setLabelUnderBin(false);
				}
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

	public boolean splitInSingleView()
	{
		return splitInSingleView;
	}

	public boolean isNextToEachOther()
	{
		return nextToEachOther;
	}

	/**
	 * Set the index of the split column. If index is -1, 
	 * the split is removed.
	 * 
	 * @param columnSplitIndex
	 */
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

	/**
	 * Set if the user options will be visible or not
	 * 
	 * @param b
	 *            true is visible, false is not visible
	 */
	public void setShowUserOptions(boolean b)
	{
		if (!(this.showUserOptions == b))
		{
			this.showUserOptions = b;
		}
	}

	/**
	 * Check if the user options are visible
	 * 
	 * @return true iff visible
	 */
	public boolean getShowUserOptions()
	{
		return this.showUserOptions;
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

	public int binOfNumber(double d)
	{
		int bin = -1;
		while (bin < this.binBoundaries.size() - 1
			&& d >= this.binBoundaries.get(bin + 1)) // syl: >=, want de grens hoort bij de volgende klasse
		{
			bin++;
		}

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
		return this.statTableModel.numberClassFrequency(this.binBoundaries,
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
		return this.statTableModel.enumClassFrequency(this.columnIndex, 
			this.splitOptions);

	}

	/**
	 * Set whether the Histogram will display frequency or relative frequency
	 * 
	 * @param b
	 *            true for relative frequency, false for frequency
	 */
	public void setPercentage(boolean b)
	{
		if (!(this.percentage == b))
		{
			this.percentage = b;
		}
	}

	/**
	 * Set whether the Histogram will display labels under the bins
	 * or between the bins.
	 * 
	 * @param b
	 *            true for label under bin, false for label between bins
	 */
	public void setLabelUnderBin(boolean b)
	{
		if (!(this.labelUnderBin == b))
		{
			this.labelUnderBin = b;
		}
	}

	/**
	 * @return true if showing relative frequency
	 */
	public boolean getPercentage()
	{
		return this.percentage;
	}

	/**
	 * @return true if showing labels under bin, false is showing labels
	 * between bins
	 */
	public boolean getLabelUnderBin()
	{
		return this.labelUnderBin;
	}

	/**
	 * Set whether the Histogram will use horizontal or vertical bars
	 * 
	 * @param b
	 *            true for vertical, false for horizontal
	 */
	public void setVerticalBars(boolean b)
	{
		if (!(this.hasVerticalBars == b))
		{
			this.hasVerticalBars = b;
		}
	}

	/**
	 * @return true iff using vertical bars
	 */
	public boolean hasVerticalBars()
	{
		return this.hasVerticalBars;
	}

	/**
	 * @param splitInSingleView
	 *            the splitInSingleView to set
	 */
	public void setSplitInSingleView(boolean splitInSingleView)
	{
		// System.out.println("HistogramModel.setSplitInSingleView(" +
		// splitInSingleView + ")");

		if (this.splitInSingleView != splitInSingleView)
		{
			// de waarde is gewijzigd
			this.splitInSingleView = splitInSingleView;
		}

		// Als je split doet en splitInSingleView blijft false,
		// dan moeten onderstaande acties ook uitgevoerd worden.
		if (!splitInSingleView)
		{
			nextToEachOther = false;
		}
	}

	public void setNextToEachOther(boolean nextToEachOther)
	{
		if (this.nextToEachOther != nextToEachOther)
		{
			this.nextToEachOther = nextToEachOther;
			if (nextToEachOther)
			{
				splitInSingleView = true;
			}
		}
	}

	/**
	 * @return the splitInSingleView
	 */
	public boolean isSplitInSingleView()
	{
		return splitInSingleView;
	}

	/**
	 * @param frequencyPolygonStackMode
	 *            the frequencyPolygonStackMode to set
	 */
	public void setFrequencyPolygonStackMode(boolean frequencyPolygonStackMode)
	{
		if (this.frequencyPolygonStackMode != frequencyPolygonStackMode)
		{
			this.frequencyPolygonStackMode = frequencyPolygonStackMode;
		}
	}

	/**
	 * @return the frequencyPolygonStackMode
	 */
	public boolean isFrequencyPolygonStackMode()
	{
		return frequencyPolygonStackMode;
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
			setColumnSplitIndex(this.splitOptions.getColumnSplitIndex() - 1);
		}
		else if (removedColumn == this.splitOptions.getColumnSplitIndex())
		{
			setColumnSplitIndex(- 1);
		}
	}


	/**
	 * Class used to represent label and frequency tuple
	 * 
	 * @author ManuDrijvers
	 * 
	 */
	public static class FrequencyTuple
	{
		public final String label;
		public final int frequency;
		public final int selectionFrequency;

		public FrequencyTuple(String label, int frequency,
			int selectionFrequency)
		{
			this.label = label;
			this.frequency = frequency;
			this.selectionFrequency = selectionFrequency;
		}
	}

	/*
	 * Set the default label positioning depending on column type.
	 * Default for integer variables is under the bin,
	 * default for double variable is between the bins.
	 */
	public void setDefaultLabelPositioning()
	{
//		System.out.println("HistogramModel.setDefaultLabelPositioning(): this.getColumnIndex() = " 
//			+ this.getColumnIndex());
		if (this.getColumnIndex() > -1)
		{
			ArrayList<ColumnType> list = this.getStatTableModel().getColumnTypes();
    		ColumnType cType = list.get(this.getColumnIndex());
    		AllowedTypes type = cType.getType();
//			System.out.println("HistogramModel.setDefaultLabelPositioning(): type = " 
//				+ type);
    		if (type.equals(AllowedTypes.INTEGER))
    		{
    			this.labelUnderBin = true;
    		}
    		else if (type.equals(AllowedTypes.DOUBLE))
    		{
    			this.labelUnderBin = false;
    		}
		}
		else
		{
			this.labelUnderBin = false;
		}
	}
}
