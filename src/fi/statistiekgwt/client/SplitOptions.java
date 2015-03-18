package fi.statistiekgwt.client;

import java.util.ArrayList;

import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * Class describing how data is split
 * 
 * @author Manu Drijvers
 * 
 */
public class SplitOptions
{
	private int columnSplitIndex;
	private ArrayList<Double> binBoundaries;

	/**
	 * Constructor
	 */
	public SplitOptions()
	{
		this.columnSplitIndex = -1;
		this.binBoundaries = new ArrayList<Double>();
		this.binBoundaries.add(0.0);
		this.binBoundaries.add(50.0);
		this.binBoundaries.add(100.0);
	}

	public int getColumnSplitIndex()
	{
		return columnSplitIndex;
	}

	/**
	 * Set the index of the split column. If index is -1, 
	 * the split is removed.
	 * 
	 * @param columnSplitIndex
	 */
	public void setColumnSplitIndex(int columnSplitIndex)
	{
		this.columnSplitIndex = columnSplitIndex;
	}

	public ArrayList<Double> getBinBoundaries()
	{
		return binBoundaries;
	}

	public void setBinBoundaries(ArrayList<Double> binBoundaries)
	{
		this.binBoundaries = binBoundaries;
	}

	/**
	 * Gets the description of a split class
	 * 
	 * @param splitClass
	 *            the split class
	 * @param model
	 *            the data model
	 * @return a string containing the description of splitclass
	 */
	public String getSplitClassLabel(int splitClass, StatTableModel model)
	{
		ArrayList<ColumnType> list = model.getColumnTypes();
		ColumnType splitCType = list.get(
			this.columnSplitIndex);
		if (splitCType.getType().isNumber())
		{
			// test syl: hier: als INTEGER en binValue2 - binValue1 == 1, dan return binValue1
			String binValue1 = StatistiekGWT.getStringValue(this.binBoundaries.get(splitClass));
			String binValue2 = StatistiekGWT.getStringValue(this.binBoundaries.get(splitClass + 1));
			return binValue1 + " -< " + binValue2;
		}
		else if (splitCType.getType().equals(AllowedTypes.ENUM))
		{
			return splitCType.getEnumOptions()[splitClass];
		}
		else
		{
			ArrayList<String> options = model.getStringOptions(this.columnSplitIndex);
			return options.get(splitClass);
		}
	}

	public SplitOptions clone()
	{
		SplitOptions clone = new SplitOptions();
		clone.setColumnSplitIndex(this.columnSplitIndex);
		ArrayList<Double> cloneBoundaries = new ArrayList<Double>();
		for (Double d : this.binBoundaries)
		{
			cloneBoundaries.add(new Double(d));
		}
		clone.setBinBoundaries(cloneBoundaries);
		return clone;
	}
}
