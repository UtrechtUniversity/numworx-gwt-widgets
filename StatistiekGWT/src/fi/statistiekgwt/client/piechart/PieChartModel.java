package fi.statistiekgwt.client.piechart;

import java.util.ArrayList;

import fi.statistiekgwt.client.SplitOptions;
import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekGWT;

// use some features from histogram
import fi.statistiekgwt.client.histogram.HistogramModel.FrequencyTuple;

/**
 * MVC model for StatistiekView PieChart
 * 
 * @author Sylvia van Borkulo
 * 
 */
public class PieChartModel
{
	private int columnIndex;
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
	public PieChartModel(StatTableModel tableModel, String viewName)
	{
		this.tableModel = tableModel;

		this.viewName = viewName;

		// set initial values
		this.columnIndex = -1;
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
	 * Find the frequency of every class. To be used only for columns of type enum or
	 * string.
	 * 
	 * @return array of FrequencyTuples (which contains class label and
	 *         frequency)
	 */
	public FrequencyTuple[][] enumClassFrequency()
	{
		return this.tableModel.enumClassFrequency(this.columnIndex, new SplitOptions());
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
	}
}
