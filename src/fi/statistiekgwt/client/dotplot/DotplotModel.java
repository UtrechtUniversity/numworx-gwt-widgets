package fi.statistiekgwt.client.dotplot;

import java.util.ArrayList;

import com.google.gwt.canvas.dom.client.CssColor;

import fi.statistiekgwt.client.ColorUtils;
import fi.statistiekgwt.client.SplitOptions;
import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekGWT;

/**
 * MVC Model for StatistiekView Dotplot and Scatterplot
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class DotplotModel
{
	private StatTableModel statTableModel;
	private int columnXIndex;
	private int columnYIndex;
	private final boolean scatterplotMode;

	private SplitOptions splitOptions;

	/**
	 * Whether or not the scale of variable X is optimized. Only available in dotplot,
	 * not in scatterplot.
	 */
	private boolean optimizeScaleX;
	/**
	 * The minimum value of columnX that is used on the scale of the dotplot.
	 */
	private double minXOnScale;
	/**
	 * The maximum value of columnX that is used on the scale of the dotplot.
	 */
	private double maxXOnScale;

	private boolean useColorScale;
	private CssColor colorA;
	private CssColor colorB;
	private int columnColorIndex;

	private boolean splitInSingleView;

	private boolean showCorrelation;

	private String viewName;

	/**
	 * Constructor
	 * 
	 * @param tableModel
	 *            data table
	 * @param viewName
	 *            The initial name of this view
	 */
	public DotplotModel(StatTableModel tableModel, String viewName,
		boolean scatterplotMode)
	{
		this.statTableModel = tableModel;

		this.viewName = viewName;
		this.showCorrelation = false;
		this.useColorScale = false;

		// set default colors
		this.setColorA(ColorUtils.getDefaultColorA());
		this.setColorB(ColorUtils.getDefaultColorB());

		this.columnColorIndex = -1;
		this.columnXIndex = -1;
		this.columnYIndex = -1;
		this.splitOptions = new SplitOptions();
		this.splitOptions.setColumnSplitIndex(-1);

		ArrayList<Double> splitBoundaries = new ArrayList<Double>();
		// by default 2 split bins
		splitBoundaries.add(0.0);
		splitBoundaries.add(50.0);
		splitBoundaries.add(100.0);
		this.splitOptions.setBinBoundaries(splitBoundaries);
		this.scatterplotMode = scatterplotMode;
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
	 * @return The data table
	 */
	public StatTableModel getStatTableModel()
	{
		return this.statTableModel;
	}

	/**
	 * @param colorA
	 *            the colorA to set
	 */
	public void setColorA(CssColor colorA)
	{
		this.colorA = colorA;
	}

	/**
	 * @return the colorA
	 */
	public CssColor getColorA()
	{
		return colorA;
	}

	/**
	 * @param columnXIndex
	 *            the columnXIndex to set
	 */
	public void setColumnXIndex(int columnXIndex)
	{
		if (!(this.columnXIndex == columnXIndex))
		{
			this.columnXIndex = columnXIndex;
		}
	}

	/**
	 * @return the columnXIndex
	 */
	public int getColumnXIndex()
	{
		return columnXIndex;
	}

	/**
	 * Get the minimum value of columnX on the scale.
	 * 
	 * @return
	 */
	public double getMinXOnScale()
	{
		return this.minXOnScale;
	}
	
	/**
	 * Set the minimum value of columnX on the scale.
	 * 
	 * @param min
	 *            the new minimum value
	 */
	public void setMinXOnScale(double min)
	{
		double minColumnValue = this.getStatTableModel().getColumnMin(
			this.getColumnXIndex());

		if (this.getStatTableModel().isEmptyColumn(this.getColumnXIndex())
			|| ((min <= minColumnValue) && (min != this.minXOnScale)))
		{
			this.minXOnScale = min;
		}
	}

	/**
	 * Set the maximum value of columnX on the scale.
	 * 
	 * @param max
	 *            the new maximum value
	 */
	public void setMaxXOnScale(double max)
	{
		double maxColumnValue = this.getStatTableModel().getColumnMax(
			this.getColumnXIndex());

		if (this.getStatTableModel().isEmptyColumn(this.getColumnXIndex())
			|| ((max >= maxColumnValue) && (max != this.maxXOnScale)))
		{
			this.maxXOnScale = max;
		}
	}

	/**
	 * Get the maximum value of columnX on the scale.
	 * 
	 * @return
	 */
	public double getMaxXOnScale()
	{
		return this.maxXOnScale;
	}

	/**
	 * @param columnYIndex
	 *            the columnYIndex to set
	 */
	public void setColumnYIndex(int columnYIndex)
	{
		if (!(this.columnYIndex == columnYIndex))
		{
			this.columnYIndex = columnYIndex;
		}
	}

	/**
	 * @return the columnYIndex
	 */
	public int getColumnYIndex()
	{
		return columnYIndex;
	}

	public void setColumnSplitIndex(int columnSplitIndex)
	{
		if (!(this.splitOptions.getColumnSplitIndex() == columnSplitIndex))
		{
			this.splitOptions.setColumnSplitIndex(columnSplitIndex);
		}
	}

	public int getColumnSplitIndex()
	{
		return this.splitOptions.getColumnSplitIndex();
	}

	/**
	 * @param useColorScale
	 *            the useColorScale to set
	 */
	public void setUseColorScale(boolean b)
	{
		this.useColorScale = b;
	}

	/**
	 * @return the useColorScale
	 */
	public boolean isUseColorScale()
	{
		return useColorScale;
	}

	/**
	 * @param colorB
	 *            the colorB to set
	 */
	public void setColorB(CssColor colorB)
	{
		this.colorB = colorB;
	}

	/**
	 * @return the colorB
	 */
	public CssColor getColorB()
	{
		return colorB;
	}

	/**
	 * @param columnColorIndex
	 *            the columnColorIndex to set
	 */
	public void setColumnColorIndex(int columnColorIndex)
	{
		if (!(this.columnColorIndex == columnColorIndex))
		{
			this.columnColorIndex = columnColorIndex;
		}
	}

	/**
	 * @return the columnColorIndex
	 */
	public int getColumnColorIndex()
	{
		return columnColorIndex;
	}

	/**
	 * @param viewName
	 *            the viewName to set
	 */
	public void setViewName(String viewName)
	{
		this.viewName = viewName;
	}

	/**
	 * @return this StatistiekView's name
	 */
	public String getViewName()
	{
		return this.viewName;
	}

	/**
	 * Tests if the variable index for column X is valid
	 * 
	 * @return true if index is valid
	 */
	public boolean columnXIndexValid()
	{
		return (this.columnXIndex >= 0
			&& this.columnXIndex < this.statTableModel.getColumnCount());
	}

	/**
	 * Tests if the variable index for column Y is valid
	 * 
	 * @return true if index is valid
	 */
	public boolean columnYIndexValid()
	{
		return (this.columnYIndex >= 0
			&& this.columnYIndex < this.statTableModel.getColumnCount());
	}

	/**
	 * Tests if the variable index for the color scale is valid
	 * 
	 * @return true if index is valid
	 */
	public boolean columnColorIndexValid()
	{
		return (this.columnColorIndex >= 0
			&& this.columnColorIndex < this.statTableModel.getColumnCount());
	}

	public boolean columnSplitIndexValid()
	{
		return (this.splitOptions.getColumnSplitIndex() >= 0
			&& this.splitOptions.getColumnSplitIndex() < this.statTableModel
				.getColumnCount());
	}

	public boolean splitInSingleView()
	{
		return splitInSingleView;
	}

	public void setSplitInSingleView(boolean b)
	{
		splitInSingleView = b;
	}

	/**
	 * @return true if currently showing correlation
	 */
	public boolean isShowCorrelation()
	{
		return showCorrelation;
	}

	/**
	 * Set whether the correlation should be shown
	 * 
	 * @param showCorrelation
	 *            true if correlation should be shown
	 */
	public void setShowCorrelation(boolean showCorrelation)
	{
		this.showCorrelation = showCorrelation;
	}

	public void setOptimizeScaleX(boolean b)
	{
		this.optimizeScaleX = b;
	}

	public ArrayList<Double> getSplitBinBoundaries()
	{
		return this.splitOptions.getBinBoundaries();
	}

	public void setNoSplitBins(int noBins)
	{
		this.splitOptions
			.setBinBoundaries(StatistiekGWT.appropriateBoundaries(this.statTableModel
				.getColumnMin(this.splitOptions.getColumnSplitIndex()),
				this.statTableModel.getColumnMax(this.splitOptions
					.getColumnSplitIndex()), noBins));
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
	 * Update the column index and the split column index
	 * given that removedColumn has been removed.
	 * @param removedColumn
	 */
	public void updateColumnIndex(int removedColumn)
	{
		// index van de geselecteerde variabele bijwerken
		if (removedColumn < this.columnXIndex)
		{
			setColumnXIndex(this.columnXIndex - 1);
		}
		else if (removedColumn == this.columnXIndex)
		{
			setColumnXIndex(- 1);
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

	public boolean isScatterplotMode()
	{
		return this.scatterplotMode;
	}
	
	/**
	 * Returns whether the scale of columnX is optimized.
	 * @return
	 */
	public boolean isOptimizeScaleX()
	{
		return optimizeScaleX;
	}

	public void setSplitBoundaries(ArrayList<Double> boundaries)
	{
		this.splitOptions.setBinBoundaries(boundaries);
	}
	
	/**
	 * @return the splitInSingleView
	 */
	public boolean isSplitInSingleView()
	{
		return this.splitInSingleView;
	}
}
