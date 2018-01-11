package fi.statistiekgwt.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.i18n.client.NumberFormat;

import fi.statistiekgwt.client.event.AddColumnEvent;
import fi.statistiekgwt.client.event.AddColumnEventHandler;
import fi.statistiekgwt.client.event.EditColumnEvent;
import fi.statistiekgwt.client.event.EditColumnEventHandler;
import fi.statistiekgwt.client.event.OutlierChangeEvent;
import fi.statistiekgwt.client.event.OutlierChangeEventHandler;
import fi.statistiekgwt.client.event.SelectionChangeEvent;
import fi.statistiekgwt.client.event.SelectionChangeEventHandler;
import fi.statistiekgwt.client.event.TableChangeEvent;
import fi.statistiekgwt.client.event.TableChangeEventHandler;
import fi.statistiekgwt.client.event.ViewSelectionChangeEvent;
import fi.statistiekgwt.client.event.ViewSelectionChangeEventHandler;
import fi.statistiekgwt.client.histogram.HistogramModel.FrequencyTuple;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * data model, implements TableModel for Table
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * @param <T>
 * 
 */
/**
 * @author borku102
 *
 */
public class StatTableModel implements HasHandlers, AddColumnEventHandler, EditColumnEventHandler, ViewSelectionChangeEventHandler
{
	private static final int WILDCARD = -2;
	private static final int OUTLIER = -3;

	private boolean isHTML5Ready = false;
	private int rowCount;
	private int columnCount;
	private ArrayList<ColumnType> columnClass;
	private ArrayList<String> columnNames;
	private ArrayList<ArrayList<Object>> values; // ArrayList van ArrayLists

	// these hashmaps contain the frequency of every string of a column to
	// efficiently know the used strings in a column at all times
	private ArrayList<HashMap<String, Integer>> stringFrequencies;

	// this arraylist contains all used strings in a column for each column
	private ArrayList<ArrayList<String>> stringOptions;
	
	/**
	 * Array of booleans indicating which rows are marked as outliers.
	 */
	private ArrayList<Boolean> rowOutlierList;
	
	/**
	 *  This arraylist contains an array of booleans indicating which values are marked as outlier in a column for each column.
	 */
	private ArrayList<ArrayList<Boolean>> cellOutlierList;

	private ArrayList<Boolean> selectionList;
	private ArrayList<SelectionHandler<Object>> selectionHandlers;
	/**
	 * The event bus to send events to event handlers associated 
	 * with the views using StatTableModel.
	 */
	EventBus eventBus;

	private boolean viewsEditable;
	private boolean dataEditable;
	private boolean viewsAddable;

	/**
	 * Constructor
	 */
	public StatTableModel()
	{
		this.rowCount = 0;
		this.columnCount = 0;
		this.columnNames = new ArrayList<String>();
		this.columnClass = new ArrayList<ColumnType>();
		this.values = new ArrayList<ArrayList<Object>>();
		this.selectionList = new ArrayList<Boolean>();
		this.rowOutlierList = new ArrayList<Boolean>();
		this.cellOutlierList = new ArrayList<ArrayList<Boolean>>();

		this.selectionHandlers = new ArrayList<SelectionHandler<Object>>();

		this.stringFrequencies = new ArrayList<HashMap<String, Integer>>();
		this.stringOptions = new ArrayList<ArrayList<String>>();

		this.viewsEditable = true;
		this.dataEditable = true;
		this.viewsAddable = true;
		
		this.eventBus = StatistiekUtils.EVENT_BUS;//new SimpleEventBus();
	}

	public boolean isViewsEditable()
	{
		return viewsEditable;
	}

	public void setViewsEditable(boolean viewsEditable)
	{
		this.viewsEditable = viewsEditable;

		// send an event
		TableChangeEvent event = new TableChangeEvent(TableChangeEvent.VIEWS_EDITABLE, -1);
		this.fireEvent(event);
	}

	/**
	 * Set the views editable field without triggering an event.
	 * Is used when setting the state in setState().
	 * 
	 * @param viewsEditable
	 */
	public void setViewsEditableWithoutEvent(boolean viewsEditable)
	{
		this.viewsEditable = viewsEditable;
	}

	public boolean isDataEditable()
	{
		return dataEditable;
	}

	public void setDataEditable(boolean dataEditable)
	{
		this.dataEditable = dataEditable;

		// send an event
		TableChangeEvent event = new TableChangeEvent(TableChangeEvent.DATA_EDITABLE, -1);
		this.fireEvent(event);
	}

	/**
	 * Set the data editable field without triggering an event.
	 * Is used when setting the state in setState().
	 * 
	 * @param dataEditable
	 */
	public void setDataEditableWithoutEvent(boolean dataEditable)
	{
		this.dataEditable = dataEditable;
	}

	public boolean isViewsAddable()
	{
		return viewsAddable;
	}

	public void setViewsAddable(boolean viewsAddable)
	{
		this.viewsAddable = viewsAddable;

		// send an event
		TableChangeEvent event = new TableChangeEvent(TableChangeEvent.VIEWS_ADDABLE, -1);
		this.fireEvent(event);
	}

	/**
	 * Set the views addable field without triggering an event.
	 * Is used when setting the state in setState().
	 * 
	 * @param viewsAddable
	 */
	public void setViewsAddableWithoutEvent(boolean viewsAddable)
	{
		this.viewsAddable = viewsAddable;
	}

	public HashMap getState()
	{
		HashMap h = new HashMap();

		h.put("rowCount", new Integer(this.rowCount));
		h.put("columnCount", new Integer(this.columnCount));
		h.put("columnNames", this.columnNames);
		//h.put("columnClass", this.columnClass);
		if (this.isHTML5Ready)
		{
			List columnClassMap = new ArrayList();
			for (ColumnType type : this.columnClass)
			{
				columnClassMap.add(type.toMap());
			}
			h.put("columnClassMapped", columnClassMap);
		}
		h.put("values", this.values);

		h.put("viewsEditable", this.viewsEditable);
		h.put("dataEditable", this.dataEditable);
		h.put("viewsAddable", this.viewsAddable);

		return h;
	}

	public void setState(HashMap h)
	{
		//System.out.println("StatTableModel.setState()");
		
		ObjectMap map = JSONUtilities.wrapMap(h);
		
		if (map.containsKey("rowCount"))
		{
			this.rowCount = map.getInt("rowCount");//((Number) h.get("rowCount")).intValue();
		}
		if (map.containsKey("columnCount"))
		{
			this.columnCount = map.getInt("columnCount");
		}
		if (map.containsKey("columnNames"))
		{
			this.columnNames = new ArrayList(Arrays.asList(map.getStringArray("columnNames")));
		}
		if (map.containsKey("columnClassMapped"))
		{
			this.setHTML5Ready(true);
			
			this.columnClass = new ArrayList<ColumnType>();

			List<Map<String, Object>> hashtable = map.getMapList("columnClassMapped");
			for (Iterator iterator = hashtable.iterator(); iterator.hasNext();)
			{
				HashMap<String, Object> columnTypeMap = (HashMap<String, Object>) iterator.next();
				ObjectMap columnTypeMap2 = JSONUtilities.wrapMap(columnTypeMap);
				String typeString = (String) columnTypeMap.get("type");
				AllowedTypes allowedType = null;
				if (typeString.equals(AllowedTypes.INTEGER.toString()))
				{
					allowedType = AllowedTypes.INTEGER;
				}
				else if (typeString.equals(AllowedTypes.DOUBLE.toString()))
				{
					allowedType = AllowedTypes.DOUBLE;
				}
				else if (typeString.equals(AllowedTypes.ENUM.toString()))
				{
					allowedType = AllowedTypes.ENUM;
				}
				else if (typeString.equals(AllowedTypes.STRING.toString()))
				{
					allowedType = AllowedTypes.STRING;
				}
					
				ColumnType type = new ColumnType(
					allowedType, 
					columnTypeMap2.getStringArray("enumOptions"),//(String[]) columnTypeMap.get("enumOptions"), 
					(String) columnTypeMap.get("uitleg"));
				this.columnClass.add(type);
			}
		}
		else
		{
			this.setHTML5Ready(false);
			return; // if not HTML5 ready there is no use to proceed
		}
		if (map.containsKey("values"))
		{
			ObjectList list = map.getObjectList("values");
			this.values = new ArrayList<ArrayList<Object>>();
			for (int i = 0; i < list.size(); i++) 
			{
				this.values.add(new ArrayList<Object>(list.getStringList(i)));
			}
		}
		if (map.containsKey("viewsEditable"))
		{
			this.setViewsEditableWithoutEvent(map.getBoolean("viewsEditable"));
		}
		if (map.containsKey("dataEditable"))
		{
			this.setDataEditableWithoutEvent(map.getBoolean("dataEditable"));
		}
		if (map.containsKey("viewsAddable"))
		{
			this.setViewsAddableWithoutEvent(map.getBoolean("viewsAddable"));
		}

		// this.selectionListeners = new ArrayList<SelectionListener>();
		// this.listeners = new ArrayList<TableModelListener>();
		
		// setState wordt 2x aangeroepen, de 2e keer met de laatste wijzigingen.
		if (!this.stringFrequencies.isEmpty())
		{
			this.stringFrequencies.clear();
		}
		if (!this.stringOptions.isEmpty())
		{
			this.stringOptions.clear();
		}
		for (int i = 0; i < this.columnCount; i++)
		{
			this.stringFrequencies.add(this.buildColumnStringOptions(i));
			this.stringOptions.add(stringsInHashtable(this.stringFrequencies.get(i)));
		}
	}
	
	private static List toList(Object object) 
	{
		if (object instanceof List)
		{
			return (List) object;
		}
		else if (object instanceof Object[])
		{
			Object[] objects = (Object[]) object;
			return Arrays.asList(objects);
		}
		else
		{
			return null;
		}
	}
	

	/**
	 * This returns all key strings of a hashtable, sorted lexicographically
	 */
	private ArrayList<String> stringsInHashtable(
		HashMap<String, Integer> hashMap)
	{
		ArrayList<String> list = new ArrayList<String>(hashMap.keySet());
		// Use collator to sort for example '�' correctly
		//Collator collator = Collator.getInstance(Locale.getDefault());
		//Collections.sort(list, collator);
		Collections.sort(list);
		return list;
	}

	/**
	 * Subscribe for events
	 */
	public HandlerRegistration addTableChangeEventHandler(TableChangeEventHandler handler)
	{
		return this.eventBus.addHandler(TableChangeEvent.TYPE, handler);
	}
	
	/**
	 * Subscribe for events
	 */
	public HandlerRegistration addSelectionChangeEventHandler(SelectionChangeEventHandler handler)
	{
		return this.eventBus.addHandler(SelectionChangeEvent.TYPE, handler);
	}

	/**
	 * Subscribe for events
	 */
	public HandlerRegistration addOutlierChangeEventHandler(OutlierChangeEventHandler handler)
	{
		return this.eventBus.addHandler(OutlierChangeEvent.TYPE, handler);
	}

	/**
	 * Subscribe the selection handler to changes in the selection of rows.
	 */
	public void addSelectionHandler(SelectionHandler sh)
	{
		this.selectionHandlers.add(sh);
	}

	/**
	 * This method is used by JTable, always returns string so JTable will treat
	 * all data as strings.
	 */
	public Class getColumnClass(int i)
	{
		return String.class;
	}

	/**
	 * Get the amount of columns in the data
	 * 
	 * @return the amount of columns in the data
	 */
	public int getColumnCount()
	{
		return this.columnCount;
	}

	/**
	 * Get the amount of rows in the data
	 * 
	 * @return the amount of rows in the data
	 */
	public int getRowCount()
	{
		return this.rowCount;
	}

	/**
	 * Get the column names
	 * 
	 * @return an arraylist containing the column names
	 */
	public ArrayList<String> getColumnNames()
	{
		return this.columnNames;
	}

	public void setColumnName(String name, int columnIndex)
	{
		this.columnNames.set(columnIndex, name);

		// send an event
		TableChangeEvent event = new TableChangeEvent(TableChangeEvent.SET_COLUMN_NAME, columnIndex);
		this.fireEvent(event);
	}

	public ArrayList<String> getStringOptions(int column)
	{
		if (this.stringOptions.size() != 0)
		{
			return this.stringOptions.get(column);
		}
		else
		{
			return new ArrayList<String>();
		}
	}

	public boolean isColumnIndexValid(int columnIndex)
	{
		return columnIndex >= 0 && columnIndex < this.columnCount;
	}

	/**
	 * Determine in how many classes the split variable splits the data
	 * 
	 * @return the amount of classes in which the split variable splits the data
	 */
	public int numberOfSplitVarClasses(SplitOptions splitOptions)
	{
		if (splitOptions == null)
		{
			return 1;
		}
		return this.numberOfBins(splitOptions.getColumnSplitIndex(),
			splitOptions.getBinBoundaries());
	}

	/**
	 * Determine the number of bins in the given bin boundaries.
	 * 
	 * @return the number of bins in the given bin boundaries
	 */
	public int numberOfBins(int columnIndex, ArrayList<Double> binBoundaries)
	{
		if (!this.isColumnIndexValid(columnIndex))
		{
			return 1;
		}
		else
		{
			ColumnType cType = this.getColumnTypes().get(columnIndex);
			AllowedTypes type = cType.getType();
			if (type.isNumber())
			{
				return binBoundaries.size() - 1;
			}
			else if (type.equals(AllowedTypes.ENUM))
			{
				return cType.getEnumOptions().length - 1;
			}
			else
			{
				return this.getStringOptions(columnIndex).size();
			}
		}
	}

	/**
	 * Determines in which split class the object at row rowIndex is
	 * 
	 * @param rowIndex
	 *            the row index of the value to classify
	 * @param splitOptions
	 *            the split options
	 * @return the split class in which the object at rowIndex is
	 */
	public int classifyObject(int rowIndex, SplitOptions splitOptions)
	{
		if (splitOptions != null)
		{
			return this.classifyObject(rowIndex,
				splitOptions.getColumnSplitIndex(),
				splitOptions.getBinBoundaries());
		}
		else
		{
			return 0;
		}
	}

	/**
	 * Determines in which split class the value at row rowIndex is
	 * 
	 * @param rowIndex
	 *            the row index of the value to classify
	 * @param columnIndex
	 *            the index of the column by which data is split
	 * @param binBoundaries
	 *            the bin boundaries to split numerical data by
	 * @return the split class in which the object at rowIndex is
	 */
	public int classifyObject(int rowIndex, int columnIndex,
		ArrayList<Double> binBoundaries)
	{
		if (!this.isColumnIndexValid(columnIndex))
		{
			return 0;
		}
		else
		{
			if (this.isOutlier(rowIndex, columnIndex))
			{
				return StatTableModel.OUTLIER;
			}
			else
			{
				return this.classifyObject(
					(String) this.getValueAt(rowIndex, columnIndex), columnIndex,
					binBoundaries);
			}
		}
	}

	/**
	 * Determines in which split class given value is
	 * 
	 * @param value
	 *            the value to classify
	 * @param columnIndex
	 *            the index of the column by which data is split
	 * @param binBoundaries
	 *            the bin boundaries to split numerical data by
	 * @return the split class in which the given value is
	 */
	public int classifyObject(String value, int columnIndex,
		ArrayList<Double> binBoundaries)
	{
		if (!this.isColumnIndexValid(columnIndex))
		{
			return 0;
		}
		if (ColumnType.WILDCARD.equals(value))
		{
			return StatTableModel.WILDCARD;
		}

		ColumnType cType = this.getColumnTypes().get(columnIndex);
		AllowedTypes type = cType.getType();
		if (type.isNumber())
		{
			double d = Double.parseDouble(value);
			int bin = -1;
			while (((bin + 1) < binBoundaries.size())
				&& d >= binBoundaries.get(bin + 1))
			{
				bin++;
			}

			if (bin < 0 || bin >= binBoundaries.size() - 1)
			{
				System.out.println("StatTableModel.classifyObject(value=" + d + ", columnIndex = " 
					+ columnIndex + ") geeft -1\n\tBoundaries: ");
				for (Double a : binBoundaries)
				{
					System.out.println("\t" + a);
				}
				return -1;
			}
			else
			{
				return bin;
			}
		}
		else if (type.equals(AllowedTypes.ENUM))
		{
			int ret = -1;
			for (String option : cType.getEnumOptions())
			{
				if (option.equals(value))
				{
					ret++;
					break;
				}
				else if (!option.equals(ColumnType.WILDCARD))
				{
					ret++;
				}
			}
			return ret;
		}
		else
		{
			return this.stringOptions.get(columnIndex).indexOf(value);
		}
	}

	/**
	 * Get the name of column with index i
	 * 
	 * @param i
	 *            the index of the column to get the name of
	 * @return The name of column with index i
	 */
	public String getColumnName(int i)
	{
		if (i < this.columnCount)
		{
			return this.columnNames.get(i);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Gets the value of specific cell
	 * 
	 * @param rowIndex
	 *            the rowindex of the cell
	 * @param columnIndex
	 *            the columnindex of the cell
	 * @return the value of the cell
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (rowIndex < this.rowCount && columnIndex < this.columnCount)
		{
			return (this.values.get(rowIndex)).get(columnIndex);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Get the values in the table.
	 * @return
	 */
	public ArrayList<ArrayList<Object>> getValues()
	{
		return this.values;
	}

	/**
	 * Tells the JTable whether a cell is editable or not
	 * 
	 * @return true iff editable
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return this.dataEditable;
	}

	/**
	 * Checks if the input is valid
	 * 
	 * @param o
	 *            input
	 * @param columnIndex
	 *            check if the input is valid for this column
	 * @return true iff valid
	 */
	private boolean validInput(Object o, int columnIndex)
	{
		return this.columnClass.get(columnIndex).isValidInput(o);
	}

	/**
	 * Set the value of a cell without sending an event.
	 * 
	 * @param o
	 *            the new value
	 * @param rowIndex
	 *            the the cell's row index
	 * @param columnIndex
	 *            the cell's column index
	 */
	public void setValueAtWithoutEvent(Object o, int rowIndex,
		int columnIndex)
	{
		// System.out.println("StatTableModel.setValueAtWithoutEvent(object=" + o
		// + ", rowIndex=" + rowIndex + ", columnIndex=" + columnIndex + ")");

		if (rowIndex < this.rowCount && columnIndex < this.columnCount)
		{
			if (o.equals(ColumnType.WILDCARD)
				|| this.validInput(o, columnIndex))
			{
				AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
				if (!type.isNumber())
				{
					this.decreaseKeyHashMap(
						(String) this.getValueAt(rowIndex, columnIndex),
						columnIndex);
					// StatTableModel.decreaseKeyHashtable((String)this.getValueAt(rowIndex,
					// columnIndex), this.stringFrequencies.get(columnIndex));
				}
				
				if (type.equals(AllowedTypes.DOUBLE))
				{
//					System.out.println("StatTableModel.setValueAtWithoutEvent(): Double type! o = "
//						+ o);
					o = processDoubleValue(o);
//					System.out.println("... o = " + o);
				}

				this.values.get(rowIndex).set(columnIndex, o);

				if (!type.isNumber())
				{
					this.increaseKeyHashMap((String) o, columnIndex);
				}
			}
			else // invalid input
			{
				//System.out.println("StatTableModel.setValueAtWithoutEvent(): Invalid input o = " + o.toString());
			}
		}
		else
		{
			System.out.println("Error in StatTableModel.setValueAtWithoutEvent(): goal cel not in table");
		}
	}

	private Object processDoubleValue(Object o)
	{
		String processedValue = (String) o;
		
		processedValue = processedValue.replaceAll(",", ".");
		
		return processedValue;
	}

	/**
	 * Set the value of a cell
	 * 
	 * @param o
	 *            the new value
	 * @param rowIndex
	 *            the the cell's row index
	 * @param columnIndex
	 *            the cell's column index
	 */
	public void setValueAt(Object o, int rowIndex, int columnIndex)
	{
		this.setValueAtWithoutEvent(o, rowIndex, columnIndex);

		// send an event
		TableChangeEvent event = new TableChangeEvent(TableChangeEvent.SET_VALUE_AT, columnIndex);
		this.fireEvent(event);
	}

	@Override
	public void fireEvent(GwtEvent<?> e)
	{
		eventBus.fireEvent(e);
	}

	/**
	 * Add an empty row, but don't fire an event.
	 */
	public void addRowWithoutEvent()
	{
		ArrayList<Object> nieuw = new ArrayList<Object>(this.columnCount);
		for (int i = 0; i < this.columnCount; i++)
		{
			nieuw.add(i, ColumnType.WILDCARD);
		}
		this.values.add(nieuw);
		this.selectionList.add(false);
		
		// update row outlier list
		this.rowOutlierList.add(false);

		this.rowCount++;

		for (int i = 0; i < this.columnCount; i++)
		{
			if (!this.getColumnTypes().get(i).getType().isNumber())
			{
				this.increaseKeyHashMap(ColumnType.WILDCARD, i);
			}
			
			// update cell outlier list
			this.cellOutlierList.get(i).add(false);
		}
	}

	/**
	 * Add a row with the data of objects without sending an event.
	 * Used to import bulk data. Please note that string options are not sorted.
	 */
	public void addRowWithoutEvent(ArrayList<Object> objects)
	{
		this.values.add((objects));
		this.selectionList.add(false);
		
		// update row outlier list
		this.rowOutlierList.add(false);
		
		this.rowCount++;

		for (int i = 0; i < this.columnCount; i++)
		{
			if (!this.getColumnTypes().get(i).getType().isNumber())
			{
				this.increaseKeyHashMapWithoutSortingStringOptions(objects.get(i).toString(), i);
			}
			
			// update cell outlier list
			this.cellOutlierList.get(i).add(false);
		}
	}

	/**
	 * Add an empty row.
	 */
	public void addRow()
	{
		this.addRowWithoutEvent();

		// send an event
		TableChangeEvent event = new TableChangeEvent(TableChangeEvent.ADD_ROW, -1);
		this.fireEvent(event);
	}

	/**
	 * Add a column
	 * 
	 * @param columnName
	 *            this column's name
	 * @param columnType
	 *            this colum's ColumnType
	 */
	public void addColumn(String columnName, ColumnType columnType)
	{
		this.columnClass.add(columnType);
		this.columnNames.add(columnName);

		for (int i = 0; i < this.rowCount; i++)
		{
			this.values.get(i).add(ColumnType.WILDCARD);
		}
		this.columnCount++;

		this.stringFrequencies.add(this.buildColumnStringOptions(this.columnCount - 1));
		this.stringOptions.add(this.stringColumnOptions(this.columnCount - 1));

		
		// update cell outlier list; add an arraylist for the new column
		ArrayList<Boolean> newArray = new ArrayList<Boolean>(this.rowCount);
		for (int i = 0; i < this.rowCount; i++)
		{
			if (this.isOutlier(i))
				newArray.add(true);
			else
				newArray.add(false);
		}
		
		this.cellOutlierList.add(newArray);

		// send an event
		TableChangeEvent event = new TableChangeEvent(TableChangeEvent.ADD_COLUMN, this.columnCount - 1);
		this.fireEvent(event);
	}

	/**
	 * Add a column without sending an event.
	 * 
	 * @param columnName
	 *            this column's name
	 * @param columnType
	 *            this colum's ColumnType
	 */
	public void addColumnWithoutEvent(String columnName, ColumnType columnType)
	{
		this.columnClass.add(columnType);
		this.columnNames.add(columnName);

		for (int i = 0; i < this.rowCount; i++)
		{
			this.values.get(i).add(ColumnType.WILDCARD);
		}
		this.columnCount++;

		this.stringFrequencies.add(this.buildColumnStringOptions(this.columnCount - 1));
		this.stringOptions.add(this.stringColumnOptions(this.columnCount - 1));
		
		// update cell outlier list; add an arraylist for the new column
		ArrayList<Boolean> newArray = new ArrayList<Boolean>(this.rowCount);
		for (int i = 0; i < this.rowCount; i++)
		{
			if (this.isOutlier(i))
				newArray.add(true);
			else
				newArray.add(false);
		}
		
		this.cellOutlierList.add(newArray);
	}

	/**
	 * Returns whether or not the row with the given index is marked as
	 * an outlier.
	 * 
	 * @param rowIndex
	 * @return
	 */
	public boolean isOutlier(int rowIndex)
	{
		boolean b = this.rowOutlierList.get(rowIndex);
		
		return b;
	}

	/**
	 * Returns whether or not the cell with the given row and column index is marked as
	 * an outlier.
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	public boolean isOutlier(int rowIndex, int columnIndex)
	{
		boolean b = false;
		
		if ((this.cellOutlierList.size() > 0) && (this.cellOutlierList.get(0).size() > 0))
		{
			try
			{
				b = this.cellOutlierList.get(columnIndex).get(rowIndex);
			}
			catch (IndexOutOfBoundsException e)
			{
				// for some reason a non existing index is called
				b = false;
			}
		}
		
		return b;
	}

	/**
	 * Remove the row with the given index from the row outlier
	 * and the cell outlier lists.
	 * 
	 * @param index
	 */
	private void removeRowFromOutlierLists(int index)
	{
		this.rowOutlierList.remove(index);
		
		for (int i = 0; i < this.columnCount; i++)
		{
			this.cellOutlierList.get(i).remove(index);
		}
	}

	public void setCellOutlierList(ArrayList<ArrayList<Boolean>> list)
	{
		this.cellOutlierList = list;

		// TODO Nodig? Deze methode wordt gebruikt in setState/zetOpdracht/setEditState()
		// send an event
		OutlierChangeEvent event = new OutlierChangeEvent("StatTableModel");
		this.fireEvent(event);
	}

	/**
	 * Set the cell outlier list from an array of pairs of indices (row, column) of the outlier cells.
	 * 
	 * @param outlierIndices
	 */
	public synchronized void setCellOutlierIndices(ArrayList<ArrayList<Integer>> outlierIndices)
	{
		// reset the list
		this.cellOutlierList = new ArrayList<ArrayList<Boolean>>();
		
		for (int i = 0; i < this.columnCount; i++)
		{
			// update cell outlier list; add an arraylist for the new column
			ArrayList<Boolean> newArray = new ArrayList<Boolean>(this.rowCount);
			for (int j = 0; j < this.rowCount; j++)
			{
				newArray.add(false);
			}
			
			this.cellOutlierList.add(newArray);
		}
		
		// and set the outlier cells
		for (int j = 0; j < outlierIndices.size(); j++)
		{
			int rowIndex = outlierIndices.get(j).get(0);
			int columnIndex = outlierIndices.get(j).get(1);
			
			this.cellOutlierList.get(columnIndex).set(rowIndex, true);
		}
		
		OutlierChangeEvent event = new OutlierChangeEvent("StatTableModel");
		this.fireEvent(event);
	}

	/**
	 * Get the array of booleans indicating which row is marked as an outlier.
	 * 
	 * @return
	 */
	public ArrayList<Boolean> getRowOutlierList()
	{
		return this.rowOutlierList;
	}
	
	/**
	 * Get the array of indices indicating which row is marked as an outlier.
	 * 
	 * @return
	 */
	public ArrayList<Integer> getRowOutlierIndices()
	{
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		for (int i = 0; i < this.rowCount; i++)
		{
			if (this.rowOutlierList.get(i))
			{
				indices.add(i);
			}
		}
		
		return indices;
	}
	
	public void setRowOutlierList(ArrayList<Boolean> list)
	{
		this.rowOutlierList = list;

		// TODO Nodig? Deze methode wordt gebruikt in setState/zetOpdracht/setEditState()
		// send an event
		OutlierChangeEvent event = new OutlierChangeEvent("StatTableModel");
		this.fireEvent(event);
	}

	/**
	 * Set the row outlier list from an array of indices of the outlier rows.
	 * 
	 * @param outlierIndices
	 */
	public synchronized void setRowOutlierIndices(ArrayList<Integer> outlierIndices)
	{
		// reset the list
		this.rowOutlierList = new ArrayList<Boolean>();
		
		for (int i = 0; i < this.rowCount; i++)
		{
			this.rowOutlierList.add(false);
		}
		
		// and set the outlier rows
		for (int j = 0; j < outlierIndices.size(); j++)
		{
			this.rowOutlierList.set(outlierIndices.get(j), true);
		}
		
		OutlierChangeEvent event = new OutlierChangeEvent("StatTableModel");
		this.fireEvent(event);
	}

	/**
	 * Get the array of arrays booleans indicating which cell value is marked as an outlier.
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Boolean>> getCellOutlierList()
	{
		return this.cellOutlierList;
	}
	
	/**
	 * Get the array of pairs of indices (row, column) indicating which cell is marked as an outlier.
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Integer>> getCellOutlierIndices()
	{
		ArrayList<ArrayList<Integer>> indices = new ArrayList<ArrayList<Integer>>();
		
		for (int column = 0; column < columnCount; column++)
		{
			for (int row = 0; row < this.rowCount; row++)
			{
				if (this.cellOutlierList.get(column).get(row))
				{
					indices.add(new ArrayList<Integer>(Arrays.asList(row, column)));
				}
			}
		}
		
		return indices;
	}
	
	/**
	 * Edit a column
	 * 
	 * @param columnIndex
	 *            the index of the column to edit
	 * @param columnName
	 *            the new column name
	 * @param cType
	 *            the new column type
	 */
	public void editColumn(int columnIndex, String columnName,
		ColumnType cType)
	{
		this.editColumnWithoutEvent(columnIndex, columnName, cType);

		// send an event
		TableChangeEvent event = new TableChangeEvent(TableChangeEvent.EDIT_COLUMN, columnIndex);
		this.fireEvent(event);
	}

	/**
	 * Edit a column without sending an event.
	 * 
	 * @param columnIndex
	 *            the index of the column to edit
	 * @param columnName
	 *            the new column name
	 * @param cType
	 *            the new column type
	 */
	public void editColumnWithoutEvent(int columnIndex, String columnName,
		ColumnType cType)
	{
		this.columnNames.set(columnIndex, columnName);
		this.columnClass.set(columnIndex, cType);

		if (!cType.getType().isNumber())
		{
			this.stringFrequencies.set(columnIndex,
				this.buildColumnStringOptions(columnIndex));
			this.stringOptions.set(columnIndex, this
				.stringsInHashtable(this.stringFrequencies.get(columnIndex)));
		}
		else
		{
			this.stringFrequencies.set(columnIndex,new HashMap<String, Integer>());
			this.stringOptions.set(columnIndex, new ArrayList<String>());
		}

		for (int row = 0; row < this.rowCount; row++)
		{
			if (!cType.isValidInput(this.getValueAt(row, columnIndex)))
			{
				this.setValueAtWithoutEvent(ColumnType.WILDCARD, row, columnIndex);
			}
			else
			{
				// valid input but comma in double fields should be replaced
				if (cType.getType().equals(AllowedTypes.DOUBLE) 
					&& (((String) this.getValueAt(row, columnIndex)).indexOf(",") > -1))
				{
					String s = "";
					try
					{
						// Allow commas in doubles
						s = ((String) this.getValueAt(row, columnIndex)).replaceAll(",", ".");
						Double.parseDouble((String) s);
					}
					catch (NumberFormatException e)
					{
						// This should not happen since it is validInput
					}

					this.setValueAtWithoutEvent(s, row, columnIndex);
				}
			}
		}
	}

	/**
	 * Get the index of a column
	 * 
	 * @param columnName
	 *            name of the column
	 * @return The index of the column
	 */
	public int getColumnIndexByName(String columnName)
	{
		int i = 0;
		for (String s : this.columnNames)
		{
			if (s.equals(columnName))
			{
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 * Remove a row
	 * 
	 * @param row
	 *            index of the row to remove
	 */
	public void removeRow(int row)
	{
//		System.out.println("StatTableModel.removeRow(row=" + row + "), this.hashCode()=" + this.hashCode());

		if (row >= 0)
		{
			for (int i = 0; i < this.columnCount; i++)
			{
				if (!this.columnClass.get(i).getType().isNumber())
				{
					this.decreaseKeyHashMap((String) this.getValueAt(row, i), i);
				}
			}

			this.values.remove(row);
			this.selectionList.remove(row);
			this.removeRowFromOutlierLists(row);
			this.rowCount--;

			// send an event
			TableChangeEvent event = new TableChangeEvent(TableChangeEvent.REMOVE_ROW, -1);
			this.fireEvent(event);
		}
	}

	/**
	 * Remove a row without sending an event.
	 * 
	 * @param row
	 *            index of the row to remove
	 */
	public void removeRowWithoutEvent(int row)
	{
//		System.out.println("StatTableModel.removeRowWithoutEvent(row=" + row + "), this.hashCode()=" + this.hashCode());

		if (row >= 0)
		{
			for (int i = 0; i < this.columnCount; i++)
			{
				if (!this.columnClass.get(i).getType().isNumber())
				{
					this.decreaseKeyHashMap((String) this.getValueAt(row, i), i);
				}
			}

			this.values.remove(row);
			this.selectionList.remove(row);
			this.removeRowFromOutlierLists(row);
			this.rowCount--;
		}
	}

	/**
	 * Remove all rows in the table.
	 */
	public synchronized void removeAllRows()
	{
		int count = this.rowCount;
		
		for (int i = count - 1 ; i > -1; i--)
		{
			removeRowWithoutEvent(i);
		}
		
		// send an event
		TableChangeEvent event = new TableChangeEvent(TableChangeEvent.REMOVE_ROWS, -1);
		this.fireEvent(event);
	}

	private void decreaseKeyHashMap(String key, int columnIndex)
	{
		//System.out.println("decreasing: " + key + ". Column: " + columnIndex);
		boolean b = StatTableModel.decreaseKeyHashMap(key,
			this.stringFrequencies.get(columnIndex));
		if (b)
		{
			this.stringOptions.get(columnIndex).remove(key);
		}
	}

	/**
	 * Increase the frequency of key in the hashmap of columnIndex.
	 * If the key did not exist yet, it is also added to stringOptions
	 * and stringOptions are sorted.
	 * 
	 * @param key
	 * @param columnIndex
	 */
	private void increaseKeyHashMap(String key, int columnIndex)
	{
		boolean b = StatTableModel.increaseKeyHashMap(key,
			this.stringFrequencies.get(columnIndex));
		if (b)
		{
			this.stringOptions.get(columnIndex).add(key);
			Collections.sort(this.stringOptions.get(columnIndex));
		}
	}

	/**
	 * Increase the frequency of key in the hashmap of columnIndex.
	 * If the key did not exist yet, it is also added to stringOptions.
	 * For performance reasons stringOptions are not sorted.
	 * 
	 * @param key
	 * @param columnIndex
	 */
	private void increaseKeyHashMapWithoutSortingStringOptions(String key, int columnIndex)
	{
		boolean b = StatTableModel.increaseKeyHashMap(key,
			this.stringFrequencies.get(columnIndex));
		if (b)
		{
			this.stringOptions.get(columnIndex).add(key);
		}
	}
	
	/**
	 * Sort the string options for each column.
	 */
	public void sortStringOptions()
	{
		for (int i = 0; i < this.getColumnCount(); i++)
		{
			Collections.sort(this.stringOptions.get(i));
		}
	}

//	/**
//	 * Increases the value of key 'key' in a hashmap of type <T, Integer>. 
//	 * Return true if the hashmap did not contain the key yet.
//	 * 
//	 * @param <T>
//	 *            The type of keys in this hashmap
//	 * @param key
//	 *            the key
//	 * @param hashMap
//	 *            the hashmap in which a value will be increased
//	 * @return true iff the hashmap did not contain the key yet
//	 */
//	private static <T> boolean increaseKeyHashMap(T key,
//		HashMap<T, Integer> hashMap)
//	{
//		if (hashMap.containsKey(key))
//		{
//			hashMap.put(key, hashMap.get(key) + 1);
//			return false;
//		}
//		else
//		{
//			hashMap.put(key, 1);
//			return true;
//		}
//	}

	/**
	 * Increases the value of key 'key' in a hashmap of type <T, Integer>. 
	 * Return true if the hashmap did not contain the key yet.
	 * 
	 * @param <String>
	 *            The type of keys in this hashmap
	 * @param key
	 *            the key
	 * @param hashMap
	 *            the hashmap in which a value will be increased
	 * @return true iff the hashmap did not contain the key yet
	 */
	private static <String> boolean increaseKeyHashMap(String key,
		HashMap<String, Integer> hashMap)
	{
		if (hashMap.containsKey(key))
		{
			hashMap.put(key, hashMap.get(key) + 1);
			return false;
		}
		else
		{
			hashMap.put(key, 1);
			return true;
		}
	}

	/**
	 * Decrease the value of key 'key' in hashmap.
	 * 
	 * @param <T>
	 *            the type of keys in hashmap
	 * @param key
	 *            the key value
	 * @param hashMap
	 *            the hashmap in which a value will be decreased
	 * @return true iff the value of 'key' is now zero
	 */
	private static <T> boolean decreaseKeyHashMap(T key,
		HashMap<T, Integer> hashMap)
	{
		// check of ht de key bevat
		if (hashMap.get(key) != null)
		{
			hashMap.put(key, hashMap.get(key) - 1);
			if (hashMap.get(key) == 0)
			{
				hashMap.remove(key);
				return true;
			}
			else
			{
				return false;
			}
		}
		else
			return false;
	}

	/**
	 * Remove a column
	 * 
	 * @param columnName
	 *            name of the column to remove
	 */
	public void removeColumn(String columnName)
	{
		int i = this.getColumnIndexByName(columnName);
		if (i == -1)
		{
			System.out.println("StatTableModel.removeColumn(" + columnName + "): Column not found.");
		}
		else
		{
			this.removeColumn(i);
		}
	}

	/**
	 * Remove a column
	 * 
	 * @param columnIndex
	 *            index of the column to remove
	 */
	public void removeColumn(int columnIndex)
	{
		if (columnIndex >= 0)
		{
			this.columnNames.remove(columnIndex);
			this.columnClass.remove(columnIndex);
			this.stringOptions.remove(columnIndex);
			this.stringFrequencies.remove(columnIndex);
			
			for (ArrayList<Object> row : this.values)
			{
				row.remove(columnIndex);
			}
			this.columnCount--;
			
			this.cellOutlierList.remove(columnIndex);
			
			// send an event
			TableChangeEvent event = new TableChangeEvent(TableChangeEvent.REMOVE_COLUMN, columnIndex);
			this.fireEvent(event);
		}
	}

	/**
	 * Remove a column, without firing an event. To be used when clearing stattabelmodel
	 * for the import of a csv file.
	 * 
	 * @param column
	 *            index of the column to remove
	 */
	public void removeColumnWithoutEvent(int column)
	{
		if (column >= 0)
		{
			this.columnNames.remove(column);
			this.columnClass.remove(column);
			this.stringFrequencies.remove(column);
			
			for (ArrayList<Object> row : this.values)
			{
				row.remove(column);
			}
			this.columnCount--;
			
			this.cellOutlierList.remove(column);			
		}
	}

	/**
	 * @return the ColumnTypes of all columns
	 */
	public ArrayList<ColumnType> getColumnTypes()
	{
		return this.columnClass;
	}

	/**
	 * Switch two rows in values, selectionlist, and outlier lists.
	 */
	private void switchRows(int rowA, int rowB)
	{
		ArrayList<Object> temp = this.values.get(rowA);
		this.values.set(rowA, this.values.get(rowB));
		this.values.set(rowB, temp);

		boolean tempSelection = this.selectionList.get(rowA);
		this.selectionList.set(rowA, this.selectionList.get(rowB));
		this.selectionList.set(rowB, tempSelection);
		
		boolean tempRowOutlier = this.rowOutlierList.get(rowA);
		this.rowOutlierList.set(rowA, this.rowOutlierList.get(rowB));
		this.rowOutlierList.set(rowB, tempRowOutlier);
		
		boolean tempCellOutlier;
		ArrayList<Boolean> list;
		for (int i = 0; i < this.cellOutlierList.size(); i++)
		{
			list = this.cellOutlierList.get(i);
			tempCellOutlier = list.get(rowA);
			list.set(rowA, list.get(rowB));
			list.set(rowB, tempCellOutlier);
		}
	}
	
	/**
	 * Set the selection of the row with the given rowIndex to boolean b.
	 * 
	 * @param rowIndex
	 * @param b
	 */
	public void setSelected(int rowIndex, Boolean b)
	{
		this.selectionList.set(rowIndex, b);

		// send an event
		SelectionChangeEvent event = new SelectionChangeEvent("StatTableModel");
		this.fireEvent(event);
	}

	/**
	 * Set the selection of the row with the given rowIndex to boolean b.
	 * Fire a selection change event with the given sender.
	 * 
	 * @param rowIndex
	 * @param b
	 * @param sender
	 */
	public void setSelected(int rowIndex, Boolean b, String sender)
	{
		this.selectionList.set(rowIndex, b);

		// send an event
		SelectionChangeEvent event = new SelectionChangeEvent(sender);
		this.fireEvent(event);
	}

	/**
	 * Set the selection of the row with the given rowIndex to boolean b,
	 * without sending an event.
	 * 
	 * @param rowIndex
	 * @param b
	 * @param sender
	 */
	public void setSelectedWithoutEvent(int rowIndex, Boolean b)
	{
		this.selectionList.set(rowIndex, b);
	}

	/**
	 * Sort by a specified column
	 * 
	 * @param columnIndex
	 *          the index of the column to sort by
	 * @param order
	 * 			the order for sorting, ASCENDING or DESCENDING
	 */
	public void sort(int columnIndex, int order)
	{
		if (columnIndex >= 0)
		{
			if (order == StatistiekGWT.ASCENDING)
			{
				//this.quickSort(columnIndex, 0, this.rowCount - 1);
				// lots of same values causes StackOverflowError, so better use:
				this.threeWayQuickSortAscending(columnIndex, 0, this.rowCount - 1);
			}
			else
			{
				this.threeWayQuickSortDescending(columnIndex, 0, this.rowCount - 1);
			}
	
			// send an event
			TableChangeEvent event = new TableChangeEvent(TableChangeEvent.SORT_COLUMN, -1);
			this.fireEvent(event);
		}
	}

	/**
	 * Quicksort implementation
	 * 
	 * @param columnIndex
	 *            The column to sort by
	 * @param p
	 *            start index of the subarray to sort
	 * @param r
	 *            end index of the subarray to sort
	 */
	private void quickSort(int columnIndex, int p, int r)
	{
		if (p < r)
		{
			int q = this.partition(columnIndex, p, r);
			this.quickSort(columnIndex, p, q - 1);
			this.quickSort(columnIndex, q + 1, r);
		}
	}

	/**
	 * Partition subarray
	 * 
	 * @param columnIndex
	 *            The column to sort by
	 * @param p
	 *            start index of the subarray to sort
	 * @param r
	 *            end index of the subarray to sort
	 * @return index of the pivot used
	 */
	private int partition(int columnIndex, int p, int r)
	{
		ColumnType cType = this.columnClass.get(columnIndex);

		// exchange middle value with last value, so we have the middle value as
		// pivot, which gives us O(n log(n)) for ordered arrays.
		this.switchRows((p + r) / 2, r);

		// get the pivot x
		ArrayList<Object> x = this.values.get(r);

		int i = p - 1;
		for (int j = p; j < r; j++)
		{
			if (cType.compare(
//				this.values.get(j).get(columnIndex), x.get(columnIndex)) <= 0)
				this.values.get(j).get(columnIndex), x.get(columnIndex)) < 0) // no switch if equal
			{
				i++;
				this.switchRows(i, j);
			}
		}

		this.switchRows(i + 1, r);
		return i + 1;
	}
	
	/**
	 * Three way quicksort suited for data with many the same values. 
	 * Also called Dijkstra's Dutch national flag problem.
	 * This variant of quicksort is much faster for data with many 
	 * same values.
	 * 
	 * See also: http://www.isical.ac.in/~pdslab/2014/slides/23Quicksort.pdf
	 * (see section Duplicate Keys from p. 33, with code on p. 41)
	 * 
	 * @param columnIndex
	 *            The column to sort by
	 * @param p
	 *            start index of the subarray to sort
	 * @param r
	 *            end index of the subarray to sort
	 */
	private void threeWayQuickSortAscending(int columnIndex, int p, int r)
	{
		if (r <= p)
		{
			return;
		}
		
		ColumnType cType = this.columnClass.get(columnIndex);
		int lt = p;
		int gt = r;
		
		// get the pivot x
		ArrayList<Object> dataRow = this.values.get(p);

		int i = p;
		while (i <= gt)
		{
			int cmp = cType.compare(
				this.values.get(i).get(columnIndex), dataRow.get(columnIndex));

			if (cmp < 0)
			{
				this.switchRows(lt++, i++);
			}
			else if (cmp > 0)
			{
				this.switchRows(i, gt--);
			}
			else i++;
		}
		
		threeWayQuickSortAscending(columnIndex, p, lt - 1);
		threeWayQuickSortAscending(columnIndex, gt + 1, r);
	} 

	/**
	 * Three way quicksort suited for data with many the same values. 
	 * Also called Dijkstra's Dutch national flag problem.
	 * This variant of quicksort is much faster for data with many 
	 * same values.
	 * 
	 * See also: http://www.isical.ac.in/~pdslab/2014/slides/23Quicksort.pdf
	 * (see section Duplicate Keys from p. 33, with code on p. 41)
	 * 
	 * @param columnIndex
	 *            The column to sort by
	 * @param p
	 *            start index of the subarray to sort
	 * @param r
	 *            end index of the subarray to sort
	 */
	private void threeWayQuickSortDescending(int columnIndex, int p, int r)
	{
		// TODO aanpassen op descending...
		if (r <= p)
		{
			return;
		}
		
		ColumnType cType = this.columnClass.get(columnIndex);
		int lt = p;
		int gt = r;
		
		// get the pivot x
		ArrayList<Object> dataRow = this.values.get(p);

		int i = p;
		while (i <= gt)
		{
			int cmp = cType.compare(
				this.values.get(i).get(columnIndex), dataRow.get(columnIndex));

//			if (cmp < 0)
			if (cmp > 0)
			{
				this.switchRows(lt++, i++);
			}
//			else if (cmp > 0)
			else if (cmp < 0)
			{
				this.switchRows(i, gt--);
			}
			else i++;
		}
		
		threeWayQuickSortDescending(columnIndex, p, lt - 1);
		threeWayQuickSortDescending(columnIndex, gt + 1, r);
	} 

	/**
	 * Create a hashtable containing the frequency of all strings in column with
	 * index columnIndex
	 * 
	 * @param columnIndex
	 *            the index of the column
	 * @return a hashtable containing the frequency of all strings in column
	 *         with index columnIndex
	 */
	private HashMap<String, Integer> buildColumnStringOptions(int columnIndex)
	{
		HashMap<String, Integer> options = new HashMap<String, Integer>();
		if ((columnIndex >= 0 
			&& columnIndex < this.columnCount)
			&& !this.getColumnTypes().get(columnIndex).getType().isNumber())
		{
			String s;
			for (int i = 0; i < this.rowCount; i++)
			{
				s = (String) this.getValueAt(i, columnIndex);
				if (options.containsKey(s))
				{
					options.put(s, options.get(s) + 1);
				}
				else
				{
					options.put(s, 1);
				}
			}
		}

		return options;
	}

	/**
	 * Get all string values that occur in column with index 'columnIndex'
	 * 
	 * @param columnIndex
	 *            the index of the column for which the string options are
	 *            returned
	 * @return all string values that occur in column with index 'columnIndex'
	 */
	public ArrayList<String> stringColumnOptions(int columnIndex)
	{
		ArrayList<String> ret = new ArrayList<String>();

		for (String s : this.stringFrequencies.get(columnIndex).keySet())
		{
			ret.add(s);
		}

		return ret;
	}

	/**
	 * Get the minimum value column columnIndex.
	 * 
	 * @param columnIndex
	 *            the column index
	 * @return The minimum value of a numerical column. Returns 0 if column is not numerical. 
	 */
	public double getColumnMin(int columnIndex)
	{
		if (columnIndex < 0)
		{
			return 0;
		}
		
		AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			return 0;
		}
		
		try
		{
			Double min = Double.MAX_VALUE;
			for (int i = 0; i < this.rowCount; i++)
			{
				Object o = this.getValueAt(i, columnIndex);
				if (!o.equals(ColumnType.WILDCARD)
					&& !this.isOutlier(i, columnIndex))
				{
					Double d = Double.parseDouble((String) o);
					if (d < min)
					{
						min = d;
					}
				}
			}
			if (min.equals(Double.MAX_VALUE))
			{
				return 0;
			}
			else
			{
				return min.doubleValue();
			}
		}
		catch (NumberFormatException e)
		{
			System.out.println("StatTableModel.getColumnMin(): no numerical data");
			return 0;
		}
	}

	/**
	 * Get the minimum value of column columnIndex of the current selection.
	 * 
	 * @param columnIndex
	 *            the column index
	 * @return 
	 * 		The minimum value of a numerical column. Returns "Not available" 
	 * 		if column is not numerical or if the minimum cannot be calculated. 
	 */
	public String getColumnMinOfSelection(int columnIndex)
	{
		String minString = StatistiekGWT.rb.notAvailable();
		
		AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			minString = StatistiekGWT.rb.notAvailable();
		}
		else
		{
			Double min = Double.MAX_VALUE;
			for (int i = 0; i < this.rowCount; i++)
			{
				if (this.selectionList.get(i))
				{
					Object o = this.getValueAt(i, columnIndex);
					if (!o.equals(ColumnType.WILDCARD)
						&& !this.isOutlier(i, columnIndex))
					{
						Double d = Double.parseDouble((String) o);
						if (d < min)
						{
							min = d;
						}
					}
				}
			}
			if (min.equals(Double.MAX_VALUE))
			{
				minString = StatistiekGWT.rb.notAvailable();
			}
			else
			{
				minString = StatistiekGWT.getStringValue(min);
			}
		}
		
		return minString;
	}

	/**
	 * Get the maximum value of column columnIndex.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @return The maximum value of a numerical column. Returns 100 if column is not numerical.
	 */
	public double getColumnMax(int columnIndex)
	{
		AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			return 0;
		}
		
		try
		{
			Double max = Double.MIN_VALUE;
			for (int i = 0; i < this.rowCount; i++)
			{
				Object o = this.getValueAt(i, columnIndex);
				if (!o.equals(ColumnType.WILDCARD)
					&& !this.isOutlier(i, columnIndex))
				{
					Double d = Double.parseDouble((String) o);
					if (d > max)
					{
						max = d;
					}
				}
			}
			if (max.equals(Double.MIN_VALUE))
			{
				return 0;
			}
			else
			{
				return max.doubleValue();
			}
		}
		catch (NumberFormatException e)
		{
			System.out.println("StatTableModel.getColumnMax(): no numerical data");
			return 0;
		}
	}
	
	/**
	 * Get the maximum value of column columnIndex of the current selection.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @return 
	 * 		The maximum value of a numerical column. 
	 * 		Returns "Not available" if column is not numerical 
	 * 		or if the maximum cannot be calculated.
	 */
	public String getColumnMaxOfSelection(int columnIndex)
	{
		String maxString = StatistiekGWT.rb.notAvailable();
		
		AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			maxString = StatistiekGWT.rb.notAvailable();
		}
		else
		{
			Double max = Double.MIN_VALUE;
			for (int i = 0; i < this.rowCount; i++)
			{
				if (this.selectionList.get(i)) // only process the selected items
				{
					Object o = this.getValueAt(i, columnIndex);
					if (!o.equals(ColumnType.WILDCARD)
						&& !this.isOutlier(i, columnIndex))
					{
						Double d = Double.parseDouble((String) o);
						if (d > max)
						{
							max = d;
						}
					}
				}
			}
			if (max.equals(Double.MIN_VALUE))
			{
				maxString = StatistiekGWT.rb.notAvailable();
			}
			else
			{
				maxString = StatistiekGWT.getStringValue(max);
			}
		}
		
		return maxString;
	}
	
	/**
	 * Get the mean value of column columnIndex, excluding missing values.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @return The mean value of a numerical column. 
	 * 		Returns 0 if column is not numerical or if the mean cannot be calculated.
	 */
	public double getColumnMean(int columnIndex)
	{
		AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			return 0;
		}
		Double sum = 0.0;
		int count = 0; // number of valid values
		
		for (int i = 0; i < this.rowCount; i++)
		{
			Object o = this.getValueAt(i, columnIndex);
			if (!o.equals(ColumnType.WILDCARD)
				&& !this.isOutlier(i, columnIndex))
			{
				Double d = Double.parseDouble((String) o);
				sum += d;
				count++;
			}
		}
		if (count > 0)
		{
			return sum/count;
		}
		else
		{
			return 0;
		}
	}	

	/**
	 * Get the mean value of column columnIndex of the current selection, excluding missing values.
	 * The string value includes the separator of the language setting. 
	 * 
	 * @param columnIndex
	 *            The column index
	 * @return The mean value of a numerical column. 
	 * 		Returns "Not available" if column is not numerical or if the mean cannot be calculated.
	 */
	public String getColumnMeanOfSelection(int columnIndex)
	{
		String meanString = StatistiekGWT.rb.notAvailable();
		
		AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			meanString = StatistiekGWT.rb.notAvailable();
		}
		else
		{
			Double sum = 0.0;
			int count = 0; // number of valid values
			
			for (int i = 0; i < this.rowCount; i++)
			{
				if (this.selectionList.get(i))
				{
					Object o = this.getValueAt(i, columnIndex);
					if (!o.equals(ColumnType.WILDCARD)
						&& !this.isOutlier(i, columnIndex))
					{
						Double d = Double.parseDouble((String) o);
						sum += d;
						count++;
					}
				}
			}
			if (count > 0)
			{
				meanString = StatistiekGWT.getStringValue(sum/count);
			}
			else
			{
				meanString = StatistiekGWT.rb.notAvailable();
			}
		}
		
		return meanString;
	}	

	/**
	 * Get the mean value of column columnIndex of the current selection, excluding missing values.
	 * The string value contains the double value (without language specific separator).
	 * 
	 * @param columnIndex
	 *            The column index
	 * @return The mean value of a numerical column. 
	 * 		Returns "Not available" if column is not numerical or if the mean cannot be calculated.
	 */
	public String getColumnMeanOfSelectionDoubleValue(int columnIndex)
	{
		String meanString = StatistiekGWT.rb.notAvailable();
		
		AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			meanString = StatistiekGWT.rb.notAvailable();
		}
		else
		{
			Double sum = 0.0;
			int count = 0; // number of valid values
			
			for (int i = 0; i < this.rowCount; i++)
			{
				if (this.selectionList.get(i))
				{
					Object o = this.getValueAt(i, columnIndex);
					if (!o.equals(ColumnType.WILDCARD)
						&& !this.isOutlier(i, columnIndex))
					{
						Double d = Double.parseDouble((String) o);
						sum += d;
						count++;
					}
				}
			}
			if (count > 0)
			{
				meanString = String.valueOf(sum/count);
			}
			else
			{
				meanString = StatistiekGWT.rb.notAvailable();
			}
		}
		
		return meanString;
	}	

	/**
	 * Get the standard deviation of column columnindex, excluding missing values.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @return The standard deviation of a numerical column. 
	 * 		Returns 0 if column is not numerical or if the standard deviation cannot be calculated.
	 */
	public double getColumnSD(int columnIndex)
	{
		AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			return 0;
		}
		Double sum = 0.0;
		int count = 0; // number of valid values
		double mean = this.getColumnMean(columnIndex);
		
		for (int i = 0; i < this.rowCount; i++)
		{
			Object o = this.getValueAt(i, columnIndex);
			if (!o.equals(ColumnType.WILDCARD)
				&& !this.isOutlier(i, columnIndex))
			{
				Double d = Double.parseDouble((String) o);
				sum += Math.pow(d - mean, 2);
				count++;
			}
		}
		if (count > 0)
		{
			return Math.sqrt(sum/count);
		}
		else
		{
			return 0;
		}
	}	

	/**
	 * Get the standard deviation of a column columnIndex of the current selection, 
	 * excluding missing values.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @return The standard deviation of a numerical column. 
	 * 		Returns 0 if column is not numerical or if the standard deviation cannot be calculated.
	 */
	public double getColumnSDOfSelection(int columnIndex)
	{
		AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			return 0;
		}
		Double sum = 0.0;
		int count = 0; // number of valid values
		double mean = Double.parseDouble(this.getColumnMeanOfSelectionDoubleValue(columnIndex));
		
		for (int i = 0; i < this.rowCount; i++)
		{
			if (this.selectionList.get(i))
			{
				Object o = this.getValueAt(i, columnIndex);
				if (!o.equals(ColumnType.WILDCARD)
					&& !this.isOutlier(i, columnIndex))
				{
					Double d = Double.parseDouble((String) o);
					sum += Math.pow(d - mean, 2);
					count++;
				}
			}
		}
		
		if (count > 0)
		{
			return Math.sqrt(sum/count);
		}
		else
		{
			return 0;
		}
	}	

	/**
	 * Get the median value of column columnIndex, excluding missing values.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @return The median value of a numerical column. 
	 * 		Returns 0 if column is not numerical or if the median value cannot be calculated.
	 */
	public double getColumnMedian(int columnIndex)
	{
		AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			return 0;
		}
		double median = 0;
		
		ArrayList<Double> data = new ArrayList<Double>();

		for (int i = 0; i < this.getRowCount(); i++)
		{
			String valueString = (String) this.getValueAt(i, columnIndex);
			if (!valueString.equals(ColumnType.WILDCARD)
				&& !this.isOutlier(i, columnIndex))
			{
				// get the value
				Double d = Double.parseDouble(valueString);

				// add the value to data list based on the splitclass
				data.add(d);
			}
		}
		
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
//			System.out.println("StatTableModel.getColumnMedian(): even, median=" 
//				+ median);
		}
		else
		{
			// odd number of values in data set
			index = (int) ((size + 1)/2) - 1;
			// mediaan is de middelste waarde
			median = data.get(index);
//			System.out.println("StatTableModel.getColumnMedian(): odd, median=" 
//				+ median);
		}

		return median;
	}	

	/**
	 * Get the median value of column columnIndex of the current selection, excluding missing values.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @return The median value of a numerical column. 
	 * 		Returns 0 if column is not numerical or if the median value cannot be calculated.
	 */
	public double getColumnMedianOfSelection(int columnIndex)
	{
		AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
		if (!(type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			return 0;
		}
		double median = 0;
		
		ArrayList<Double> data = new ArrayList<Double>();

		for (int i = 0; i < this.getRowCount(); i++)
		{
			if (this.selectionList.get(i))
			{
				String valueString = (String) this.getValueAt(i, columnIndex);
				if (!valueString.equals(ColumnType.WILDCARD)
					&& !this.isOutlier(i, columnIndex))
				{
					// get the value
					Double d = Double.parseDouble(valueString);
	
					// add the value to data list based on the splitclass
					data.add(d);
				}
			}
		}
		
		Collections.sort(data);
		int size = data.size();
		int index;

		if (size % 2 == 0)
		{
			// even number of values in data set
			index = (size/2) - 1;
			// mediaan is het gemiddelde van de twee waarden in het midden
			median = (data.get(index) + data.get(index + 1))/2;
//			System.out.println("StatTableModel.getColumnMedian(): even, median=" 
//				+ median);
		}
		else
		{
			// odd number of values in data set
			index = (int) ((size + 1)/2) - 1;
			// mediaan is de middelste waarde
			median = data.get(index);
//			System.out.println("StatTableModel.getColumnMedian(): odd, median=" 
//				+ median);
		}

		return median;
	}	

	/**
	 * Get the mode of column columnIndex.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @return The mode of a column. 
	 */
	public String getColumnMode(int columnIndex)
	{
		AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
		String mode = StatistiekGWT.rb.notAvailable();
		int maxFreq = 0;
		boolean multipleModes = false;
		
		if ((type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			ArrayList<Double> data = new ArrayList<Double>();

			for (int i = 0; i < this.rowCount; i++)
			{
				String valueString = (String) this.getValueAt(i, columnIndex);
				if (!valueString.equals(ColumnType.WILDCARD)
					&& !this.isOutlier(i, columnIndex))
				{
					// get the value
					Double d = Double.parseDouble(valueString);

					// add the value to data list based on the splitclass
					data.add(d);
				}
			}
			
			Collections.sort(data);
			
			// for-loop om de frequenties te berekenen
			Map<Double, Integer> frequencyMap = new HashMap<Double, Integer>();
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
					// in development mode is String.valueOf(new Double(156)) = "156.0",
					// in production mode in String.valueOf(new Double(156)) = "156"
//					mode = String.valueOf(data.get(i));

					// Voor een consistente unit test met NumberFormat
					Double d = data.get(i);
					NumberFormat nf = StatistiekGWT.getNumberFormat(d);
					mode = nf.format(d);
					multipleModes = false;
				}
				else if ((freq_i != 0) && (freq_i == maxFreq) && (!data.get(i).equals(data.get(i - 1))))
				{ // check if there is another value with the same max frequency
					multipleModes = true;
				}
			}
		} // number
		else
		{ // enum or string
			FrequencyTuple[][] frequencies_enum = this.enumClassFrequency(columnIndex, null);
			
			if (frequencies_enum != null)
			{
				for (int i = 0; i < frequencies_enum[0].length; i++)
				{
					FrequencyTuple ft = frequencies_enum[0][i];
					if (!ft.label.equals(ColumnType.WILDCARD))
					{
						if (ft.frequency > maxFreq)
						{
							maxFreq = ft.frequency;
							mode = ft.label;
							multipleModes = false;
						}
						else if ((ft.frequency != 0) && (ft.frequency == maxFreq))
						{
							// there are two modes
							multipleModes = true;
						}
					}
				}
			}
		}
		
		if (multipleModes)
		{
			return StatistiekGWT.rb.notAvailable();
		}
		else
		{
			return mode;
		}
	}	

	/**
	 * Get the mode of column columnIndex of the current selection.
	 * 
	 * @param columnIndex
	 *            The column index
	 * @return The mode of a column. 
	 */
	public String getColumnModeOfSelection(int columnIndex)
	{
		AllowedTypes type = this.getColumnTypes().get(columnIndex).getType();
		String mode = StatistiekGWT.rb.notAvailable();
		int maxFreq = 0;
		boolean multipleModes = false;
		
		if ((type.equals(AllowedTypes.DOUBLE) 
			|| type.equals(AllowedTypes.INTEGER)))
		{
			ArrayList<Double> data = new ArrayList<Double>();

			for (int i = 0; i < this.rowCount; i++)
			{
				if (this.selectionList.get(i))
				{
					String valueString = (String) this.getValueAt(i, columnIndex);
					if (!valueString.equals(ColumnType.WILDCARD)
						&& !this.isOutlier(i, columnIndex))
					{
						// get the value
						Double d = Double.parseDouble(valueString);
	
						// add the value to data list based on the splitclass
						data.add(d);
					}
				}
			}
			
			Collections.sort(data);
			
			// for-loop om de frequenties te berekenen
			Map<Double, Integer> frequencyMap = new HashMap<Double, Integer>();
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
					// in development mode is String.valueOf(new Double(156)) = "156.0",
					// in production mode in String.valueOf(new Double(156)) = "156"
//					mode = String.valueOf(data.get(i));

					// Voor een consistente unit test met NumberFormat
					Double d = data.get(i);
					NumberFormat nf = StatistiekGWT.getNumberFormat(d);
					mode = nf.format(d);

					multipleModes = false;
				}
				else if ((freq_i != 0) && (freq_i == maxFreq) && (!data.get(i).equals(data.get(i - 1))))
				{ // check if there is another value with the same max frequency
					multipleModes = true;
				}
			}
		}
		else
		{ // enum or string
			FrequencyTuple[][] frequencies_enum = this.enumClassFrequency(columnIndex, null);
			
			if (frequencies_enum != null)
			{
				for (int i = 0; i < frequencies_enum[0].length; i++)
				{
					FrequencyTuple ft = frequencies_enum[0][i];
					if (!ft.label.equals(ColumnType.WILDCARD))
					{
						if (ft.selectionFrequency > maxFreq)
						{
							maxFreq = ft.selectionFrequency;
							mode = ft.label;
							multipleModes = false;
						}
						else if ((ft.selectionFrequency != 0) && (ft.selectionFrequency == maxFreq))
						{
							// there are two modes
							multipleModes = true;
						}
					}
				}
			}
		}
		
		if (multipleModes)
		{
			return StatistiekGWT.rb.notAvailable();
		}
		else
		{
			return mode;
		}
	}	

	/**
	 * Creates a matrix with valid pairs of values of column A and column B.
	 * @param columnAIndex 
	 * @param columnBIndex 
	 * @return An array with doubles. If column A or column B contains non-numerical values,
	 * these values count as missing and are not set in the array. 
	 */
	public double[][] getDataColumnsForCorrelation(int columnAIndex, int columnBIndex)
	{
		double[][] data = new double[2][this.getRowCount()]; // flip matrix to make it easier to extract the data columns
		
		// count the valid pairs of values
		int count = 0;

		for (int i = 0; i < this.getRowCount(); i++)
		{
			try
			{
				// column A value
				data[0][count] = Double.parseDouble(((String) this.getValueAt(i, columnAIndex)));
				// column B value
				data[1][count] = Double.parseDouble(((String) this.getValueAt(i, columnBIndex)));
				// if both column values are valid, increase count
				count++;
			}
			catch (NumberFormatException e)
			{
				// data contains non-numerical values; these count as missing
			}
		}
		
		// return the data with non-valid pairs of values excluded
		double[][] data_missingExcluded = new double[2][count];
		
		for (int j = 0; j < count; j++)
		{
			for (int i = 0; i < 2; i++)
			{
				data_missingExcluded[i][j] = data[i][j];
			}
		}
		
		return data_missingExcluded;
	}

	/**
	 * Creates an array with valid string values of column columnIndex.
	 * 
	 * @param columnIndex 
	 * @return An array with strings. If column columnIndex contains missing values,
	 * 		these values are not set in the array. 
	 */
	public String[] getDataColumnMissingExcluded(int columnIndex)
	{
		String[] data = new String[this.getRowCount()];
		
		// count the number of valid values
		int count = 0;

		for (int i = 0; i < this.getRowCount(); i++)
		{
			if (!((String) this.getValueAt(i, columnIndex)).equals(ColumnType.WILDCARD))
			{
				data[count] = (String) this.getValueAt(i, columnIndex);
				count++;
			}
		}
		
		// return the data with non-valid pairs of values excluded
		String[] data_missingExcluded = new String[count];
		
		for (int i = 0; i < count; i++)
		{
			data_missingExcluded[i] = data[i];
		}
		
		return data_missingExcluded;
	}

	/**
	 * Creates an array with valid string values of column columnIndex of the current selection.
	 * 
	 * @param columnIndex 
	 * @return An array with strings. If column columnIndex contains missing values,
	 * 		these values are not set in the array. 
	 */
	public String[] getDataColumnMissingExcludedOfSelection(int columnIndex)
	{
		String[] data = new String[this.getRowCount()];
		
		// count the number of valid values
		int count = 0;

		for (int i = 0; i < this.getRowCount(); i++)
		{
			if (selectionList.get(i))
			{
				if (!((String) this.getValueAt(i, columnIndex)).equals(ColumnType.WILDCARD))
				{
					data[count] = (String) this.getValueAt(i, columnIndex);
					count++;
				}
			}
		}
		
		// return the data with non-valid pairs of values excluded
		String[] data_missingExcluded = new String[count];
		
		for (int i = 0; i < count; i++)
		{
			data_missingExcluded[i] = data[i];
		}
		
		return data_missingExcluded;
	}

	/**
	 * Override toString
	 */
	public String toString()
	{
		String ret = new String();
		for (int i = 0; i < this.rowCount; i++)
		{
			for (int j = 0; j < this.columnCount; j++)
			{
				ret = ret + this.columnClass.get(j).getType().toString() + ":"
					+ this.getValueAt(i, j) + "\t";
			}
			ret = ret + "\n";
		}
		return ret;
	}

	public boolean isRowSelected(int rowIndex)
	{
		if (this.selectionList.size() > rowIndex)
		{
			return this.selectionList.get(rowIndex).booleanValue();
		}
		else
		{
			return false;
		}
	}

	/**
	 * Clear stringFrequencies.
	 */
	public void clearStringFrequencies()
	{
		this.stringFrequencies = new ArrayList<HashMap<String, Integer>>();
	}

	/**
	 * Clear selectionList with a new empty arraylist.
	 */
	public void clearSelectionList()
	{
		this.selectionList = new ArrayList<Boolean>();
	}
	
	/**
	 * Reset the current selection list to none selected.
	 */
	public void resetSelectionList()
	{
		ArrayList<Boolean> selectionList = new ArrayList<Boolean>();
		for (int row = 0; row < getRowCount(); row++)
		{
			selectionList.add(false);
		}
		
		setSelectionList(selectionList);
	}

	/**
	 * Reset the current selection list to none selected without triggering an event.
	 */
	public void resetSelectionListWithoutEvent()
	{
		ArrayList<Boolean> selectionList = new ArrayList<Boolean>();
		for (int row = 0; row < getRowCount(); row++)
		{
			selectionList.add(false);
		}
		
		setSelectionListWithoutEvent(selectionList);
	}

	/**
	 * Clear outlier lists.
	 */
	public void clearOutlierLists()
	{
		this.rowOutlierList = new ArrayList<Boolean>();
		this.cellOutlierList = new ArrayList<ArrayList<Boolean>>();
	}

	public synchronized void setSelectionList(ArrayList<Boolean> selectionList)
	{
		this.selectionList = selectionList;
		
		// send an event
		SelectionChangeEvent event = new SelectionChangeEvent("StatTableModel");
		this.fireEvent(event);
	}

	/**
	 * Set the selection list without triggering an event.
	 * Is used when setting the state in StatInteractiePanel.setState().
	 * 
	 * @param selectionList
	 */
	public synchronized void setSelectionListWithoutEvent(ArrayList<Boolean> selectionList)
	{
		this.selectionList = selectionList;
	}

	/**
	 * Set the selection list from an array of indices of the selected rows.
	 * 
	 * @param selectionIndices
	 */
	public synchronized void setSelectionIndices(ArrayList<Integer> selectionIndices)
	{
		// reset the list
		this.selectionList = new ArrayList<Boolean>();
		
		for (int i = 0; i < this.rowCount; i++)
		{
			this.selectionList.add(false);
		}
		
		// and set the selected rows
		for (int j = 0; j < selectionIndices.size(); j++)
		{
			this.selectionList.set(selectionIndices.get(j), true);
		}
		
		// send an event
		SelectionChangeEvent event = new SelectionChangeEvent("StatTableModel");
		this.fireEvent(event);
	}

	public ArrayList<Boolean> getSelectionList()
	{
		return this.selectionList;
	}
	
	/**
	 * Get the array of indices indicating which row is selected.
	 * 
	 * @return
	 */
	public ArrayList<Integer> getSelectionIndices()
	{
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		for (int i = 0; i < this.rowCount; i++)
		{
			if (this.selectionList.get(i))
			{
				indices.add(i);
			}
		}
		
		return indices;
	}
	
	/**
	 * Find the frequency of every bin, and the amount of selected objects in
	 * this bin Only use for columns of type integer or double
	 * 
	 * @return array of frequencies, with index 2*i the frequency of bin i, and
	 *         2*i + 1 the amount of selected items in this bin.
	 */
	public int[][] numberClassFrequency(ArrayList<Double> binBoundaries,
		int columnIndex, SplitOptions splitOptions)
	{
		if (columnIndex == -1)
		{
			return null;
		}
		
		// check if the column type is a number
		ColumnType cType = this.getColumnTypes().get(columnIndex);
		if (cType.getType().isNumber())
		{
			int[][] binFrequency = new int[this.numberOfSplitVarClasses(splitOptions)]
				[(binBoundaries.size() - 1) * 2];
			for (int splitClass = 0; splitClass < this.numberOfSplitVarClasses(splitOptions); splitClass++)
			{
				for (int i = 0; i < binBoundaries.size() - 1; i++)
				{
					binFrequency[splitClass][2 * i] = 0;
					binFrequency[splitClass][2 * i + 1] = 0;
				}
			}

			for (int i = 0; i < this.getRowCount(); i++)
			{
				Object o = this.getValueAt(i, columnIndex);
				if (!ColumnType.WILDCARD.equals(o)
					&& !isOutlier(i, columnIndex)) // check for outliers
				{
					Double d = Double.parseDouble((String) o);
					int bin = -1;
					while (bin < binBoundaries.size() - 1
						&& d >= binBoundaries.get(bin + 1))
					{
						bin++;
					}
					if (bin >= 0 && bin < binBoundaries.size() - 1)
					{
						int split = this.classifyObject(i, splitOptions);
						if (split > -1)
						{
							binFrequency[split][2 * bin]++;
							if (this.isRowSelected(i))
							{
								binFrequency[split][2 * bin + 1]++;
							}
						}
						else if (split == -1)
						{
							System.out.println("StatTableModel.numberClassFrequency() returns null. Objects cannot be classified");
							return null;
						}
						// split == -2 is a wildcard
					}
				}
			}
			return binFrequency;
		}
		else
		{
			// column type is not a number, return null
			return null;
		}
	}

	/**
	 * Find the frequency of every class. Only use for columns of type enum or
	 * string.
	 * 
	 * @param columnIndex 
	 * 		The column index 
	 * @param splitOptions
	 * 		The split information used to get the frequencies for each split class.
	 * @return array of FrequencyTuples (which contains class label and
	 *         frequency)
	 */
	public FrequencyTuple[][] enumClassFrequency(int columnIndex,
		SplitOptions splitOptions)
	{
		if (columnIndex == -1)
		{
			return null;
		}
		
		ColumnType cType = this.getColumnTypes().get(columnIndex);
		if (cType.getType().equals(AllowedTypes.STRING)
			|| cType.getType().equals(AllowedTypes.ENUM))
		{
			int splitClasses = this.numberOfSplitVarClasses(splitOptions);
			HashMap<String, Integer>[] frequencyTable = new HashMap[splitClasses];
			HashMap<String, Integer>[] frequencySelectionTable = new HashMap[splitClasses];

			for (int i = 0; i < splitClasses; i++)
			{
				frequencyTable[i] = new HashMap<String, Integer>();
				frequencySelectionTable[i] = new HashMap<String, Integer>();
			}

			for (int i = 0; i < this.getRowCount(); i++)
			{
				// test syl: loop 1 en 2 zijn traag bij grote aantallen...
				//System.out.println("StatTableModel.enumClassFrequency(): 1e loop, i = " + i);
				
				int split = this.classifyObject(i, splitOptions);
				if (split > -1)
				{
					StatTableModel.increaseKeyHashMap(
						(String) this.getValueAt(i, columnIndex),
						frequencyTable[split]);
					if (this.isRowSelected(i))
					{
						StatTableModel.increaseKeyHashMap(
							(String) this.getValueAt(i, columnIndex),
							frequencySelectionTable[split]);
					}
				}
				else if (split == -1)
				{
					System.out.println("StatTableModel.enumClassFrequency() returns null. Objects cannot be classified");
					return null;
				}
				// split == -2 is a wildcard
			}

			// create FrequencyTuple array from hashtable

			FrequencyTuple[][] ret = new FrequencyTuple[splitClasses][];
			for (int splitClass = 0; splitClass < splitClasses; splitClass++)
			{
				if (cType.getType().equals(AllowedTypes.ENUM))
				{
					String[] enumOptions = cType.getEnumOptions();
					ret[splitClass] = new FrequencyTuple[enumOptions.length - 1];
					for (int i = 0, j = 0; i < enumOptions.length - 1; i++)
					{
						if (enumOptions[i + j].equals(ColumnType.WILDCARD))
						{
							j++;
						}
						String option = enumOptions[i + j];
						int freq;
						int selectionFreq;
						if (frequencyTable[splitClass].containsKey(option))
						{
							freq = frequencyTable[splitClass].get(option);
						}
						else
						{
							freq = 0;
						}

						if (frequencySelectionTable[splitClass].containsKey(option))
						{
							selectionFreq = frequencySelectionTable[splitClass].get(option);
						}
						else
						{
							selectionFreq = 0;
						}

						ret[splitClass][i] = new FrequencyTuple(option, freq,
							selectionFreq);
					}
				} // Enum
				else
				{ // String
					Set<String> keySet = new HashSet<String>();
					for (HashMap<String, Integer> h : frequencyTable)
					{
						keySet.addAll(h.keySet());
					}
					List<String> keyList = Arrays.asList(keySet.toArray(new String[0]));

					// Use collator to sort for example '�' correctly -> not available in gwt
					//Collator collator = Collator.getInstance(Locale.getDefault());
					//Collections.sort(keyList, collator);
					Collections.sort(keyList);
					
					ret[splitClass] = new FrequencyTuple[keySet.size()];
					int i = 0;
					
					for (String key : keyList)
					{
						if (frequencyTable[splitClass].containsKey(key))
						{
							if (frequencySelectionTable[splitClass].containsKey(key))
							{
								ret[splitClass][i] = new FrequencyTuple(key,
									frequencyTable[splitClass].get(key),
									frequencySelectionTable[splitClass].get(key));
							}
							else
							{
								ret[splitClass][i] = new FrequencyTuple(key,
									frequencyTable[splitClass].get(key), 0);
							}
						}
						else
						{
							ret[splitClass][i] = new FrequencyTuple(key, 0, 0);
						}
						i++;
					}
				}
			}

			return ret;
		} // String or Enum
		else
		{
			// column type is not a String of Enum, return null
			return null;
		}
	}
	
	/**
	 * Find the frequency of every class in the current selection. Only use for columns of type enum or
	 * string.
	 * 
	 * @param columnIndex 
	 * 		The column index 
	 * @param splitOptions
	 * 		The split information used to get the frequencies for each split class.
	 * @return array of FrequencyTuples (which contains class label and
	 *         frequency)
	 */
	public FrequencyTuple[][] enumClassFrequencyOfSelection(int columnIndex,
		SplitOptions splitOptions)
	{
		ColumnType cType = this.getColumnTypes().get(columnIndex);
		if (cType.getType().equals(AllowedTypes.STRING)
			|| cType.getType().equals(AllowedTypes.ENUM))
		{
			int splitClasses = this.numberOfSplitVarClasses(splitOptions);
			HashMap<String, Integer>[] frequencyTable = new HashMap[splitClasses];
			HashMap<String, Integer>[] frequencySelectionTable = new HashMap[splitClasses];

			for (int i = 0; i < splitClasses; i++)
			{
				frequencyTable[i] = new HashMap<String, Integer>();
				frequencySelectionTable[i] = new HashMap<String, Integer>();
			}

			for (int i = 0; i < this.getRowCount(); i++)
			{
				int split = this.classifyObject(i, splitOptions);
				if (split > -1)
				{
					StatTableModel.increaseKeyHashMap(
						(String) this.getValueAt(i, columnIndex),
						frequencyTable[this.classifyObject(i, splitOptions)]);
					if (this.isRowSelected(i))
					{
						StatTableModel.increaseKeyHashMap(
							(String) this.getValueAt(i, columnIndex),
							frequencySelectionTable[this.classifyObject(i,splitOptions)]);
					}
				}
				else
				{
					System.out.println("StatTableModel.enumClassFrequency() returns null. Objects cannot be classified");
					return null;
				}
			}

			// create FreqencyTuple array from hashtable

			FrequencyTuple[][] ret = new FrequencyTuple[splitClasses][];
			for (int splitClass = 0; splitClass < splitClasses; splitClass++)
			{
				if (cType.getType().equals(AllowedTypes.ENUM))
				{
					String[] enumOptions = cType.getEnumOptions();

					ret[splitClass] = new FrequencyTuple[enumOptions.length - 1];
					for (int i = 0, j = 0; i < enumOptions.length - 1; i++)
					{
						if (enumOptions[i + j].equals(ColumnType.WILDCARD))
						{
							j++;
						}
						String option = enumOptions[i + j];
						int freq;
						int selectionFreq;
						if (frequencyTable[splitClass].containsKey(option))
						{
							freq = frequencyTable[splitClass].get(option);
						}
						else
						{
							freq = 0;
						}

						if (frequencySelectionTable[splitClass].containsKey(option))
						{
							selectionFreq = frequencySelectionTable[splitClass].get(option);
						}
						else
						{
							selectionFreq = 0;
						}

						ret[splitClass][i] = new FrequencyTuple(option, freq,
							selectionFreq);
					}
				} // Enum
				else
				{ // String
					Set<String> keySet = new HashSet<String>();
					for (HashMap<String, Integer> h : frequencyTable)
					{
						keySet.addAll(h.keySet());
					}
					List<String> keyList = Arrays.asList(keySet.toArray(new String[0]));

					// Use collator to sort for example '�' correctly
					//Collator collator = Collator.getInstance(Locale.getDefault());
					//Collections.sort(keyList, collator);
					Collections.sort(keyList);
					
					ret[splitClass] = new FrequencyTuple[keySet.size()];
					int i = 0;
					
					for (String key : keyList)
					{
						if (frequencyTable[splitClass].containsKey(key))
						{
							if (frequencySelectionTable[splitClass].containsKey(key))
							{
								ret[splitClass][i] = new FrequencyTuple(key,
									frequencyTable[splitClass].get(key),
									frequencySelectionTable[splitClass].get(key));
							}
							else
							{
								ret[splitClass][i] = new FrequencyTuple(key,
									frequencyTable[splitClass].get(key), 0);
							}
						}
						else
						{
							ret[splitClass][i] = new FrequencyTuple(key, 0, 0);
						}
						i++;
					}
				}
			}

			return ret;
		} // String or Enum
		else
		{
			// column type is not a String of Enum, return null
			return null;
		}
	}

	/**
	 * Update the column types. Columns with numerical values will be
	 * of type INTEGER or DOUBLE. Else the type will remain the same.
	 */
	public void updateNumericalColumnTypes()
	{
		this.updateNumericalColumnTypesWithoutEvent();
		
		// send an event
		TableChangeEvent event = new TableChangeEvent(TableChangeEvent.UPDATE_NUMERICAL_COLUMN_TYPES, -1);
		this.fireEvent(event);
	}

	/**
	 * Update the column types. Columns with numerical values will be
	 * of type INTEGER or DOUBLE. Else the type will remain the same.
	 */
	public void updateNumericalColumnTypesWithoutEvent()
	{
		for (int i = 0; i < this.columnCount; i++)
		{
			if (this.hasIntegerValues(i))
			{
				this.editColumnWithoutEvent(i, this.getColumnName(i), 
					new ColumnType(AllowedTypes.INTEGER));
			}
			else if (this.hasDoubleValues(i))
			{
				this.editColumnWithoutEvent(i, this.getColumnName(i), 
					new ColumnType(AllowedTypes.DOUBLE));
			} 
		}
	}

	/**
	 * Returns whether the given column has all double values.
	 * @param columnIndex
	 * @return
	 */
	private boolean hasDoubleValues(int columnIndex)
	{
		boolean hasDoubleValues = true;
		
		for (int row = 0; row < this.rowCount; row++)
		{
			String value = (String) this.getValueAt(row, columnIndex);

			if (!value.equals(ColumnType.WILDCARD) 
				&& !this.isDouble(value))
			{
				// a non-double value found, so return false
				hasDoubleValues = false;
				break;
			}	
		}
		
		if (this.rowCount == 0)
		{
			hasDoubleValues = false;
		}

		return hasDoubleValues;
	}
	
	/**
	 * Returns whether the given column has all integer values.
	 * @param columnIndex
	 * @return
	 */
	private boolean hasIntegerValues(int columnIndex)
	{
		boolean hasIntegerValues = true;
		
		for (int row = 0; row < this.rowCount; row++)
		{
			String value = (String) this.getValueAt(row, columnIndex);

			if (!value.equals(ColumnType.WILDCARD) 
				&& !this.isInteger(value))
			{
				// a non-integer value found, so return false
				hasIntegerValues = false;
				break;
			}	
		}

		if (this.rowCount == 0)
		{
			hasIntegerValues = false;
		}
		
		return hasIntegerValues;
	}
	
	/**
	 * Returns true if the string contains an integer value, else false.
	 * @param s
	 * @return
	 */
	public boolean isInteger(String s)
	{
		try
		{
			Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	/**
	 * Returns true if the string contains a double value, else false.
	 * @param s
	 * @return
	 */
	public boolean isDouble(String s)
	{
		try
		{
			s = (String) this.processDoubleValue(s);
			Double.parseDouble(s);
		}
		catch (NumberFormatException e)
		{
			return false;
		} 
		// only got here if we didn't return false
		return true;
	}

	/**
	 * Handle the add column event from column dialog.
	 */
	@Override
	public void onAddColumn(AddColumnEvent event)
	{
		GWT.log("StatTableModel.onAddColumn()");
		
		ArrayList<String> enumOptionsList = event.getEnumOptions();
		String[] array = enumOptionsList.toArray(new String[enumOptionsList.size()]);
		
		this.addColumn(event.getName(), 
			new ColumnType(event.getType(), array, event.getUitleg()));		
	}

	@Override
	public void onEditColumn(EditColumnEvent event)
	{
		if (event.hasChangedType() || event.hasChangedEnumOptions() || event.hasChangedUitleg())
		{
			this.editColumn(
				event.getColumnIndex(),
				event.getName(), 
				new ColumnType(
					event.getType(), 
					event.getEnumOptions().toArray(new String[event.getEnumOptions().size()]), 
					event.getUitleg()));
		}
		else if (event.hasChangedName())
		{
			this.setColumnName(event.getName(), event.getColumnIndex());
		}
	}

	@Override
	public void onViewSelectionChange(ViewSelectionChangeEvent event)
	{
		GWT.log("StatTableModel.onViewSelectionChange(): event.getSenderName() = " + event.getSenderName());
		
		SelectionChangeEvent selectionChangeEvent = new SelectionChangeEvent("StatTableModel");
		this.fireEvent(selectionChangeEvent);
	}

	/**
	 * @return the isHTML5Ready
	 */
	public boolean isHTML5Ready()
	{
		return this.isHTML5Ready;
	}

	/**
	 * @param isHTML5Ready the isHTML5Ready to set
	 */
	public void setHTML5Ready(boolean isHTML5Ready)
	{
		this.isHTML5Ready = isHTML5Ready;
	}
	
	/**
	 * Returns whether or not the column with the given index is empty, i.e., 
	 * the row count is zero or the column contains only wildcards.
	 * 
	 * @return
	 */
	public boolean isEmptyColumn(int columnIndex)
	{
		boolean isEmpty;
		
		if (this.rowCount == 0)
		{
			isEmpty = true;
		}
		else
		{
			isEmpty = true;
			
			// check for wildcards
			for (int i = 0; i < this.rowCount; i++)
			{
				Object o = this.getValueAt(i, columnIndex);
				if (!o.equals(ColumnType.WILDCARD))
				{
					isEmpty = false;
					break;
				}
			}
		}
		
		return isEmpty;
	}
	
	/**
	 * Get the number of valid data rows in the given column.
	 * 
	 * @return the amount of rows in the data
	 */
	public int getNumberOfValidDataRows(int columnIndex)
	{
		int count = 0;
		
		for (int i = 0; i < this.rowCount; i++)
		{
			Object o = this.getValueAt(i, columnIndex);
			if (o!=null && !o.equals(ColumnType.WILDCARD)
				&& !this.isOutlier(i, columnIndex))
			{
				count++;
			}
		}

		return count;
	}
	
	/**
	 * Mark the value in the cell with the given column and row index as an outlier.
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @param b
	 */
	public void markCellAsOutlier(int rowIndex, int columnIndex, boolean b)
	{
		this.cellOutlierList.get(columnIndex).set(rowIndex, b);
		
		// Als een rij een cell bevat die geen outlier is, 
		// dan is de rij als geheel ook niet meer gemarkeerd als outlier 
		if (b == false)
		{
			this.rowOutlierList.set(rowIndex, false);
		}
		else
		{
			// check if all cell in the row are now marked as outlier
			// if so, mark the row as outlier
			boolean allCellsInRowMarkedAsOutlier = true;
			
			for (int i = 0; i < getColumnCount(); i++)
			{
				if (!this.cellOutlierList.get(i).get(rowIndex))
				{
					allCellsInRowMarkedAsOutlier = false;
				}
			}
			
			if (allCellsInRowMarkedAsOutlier)
			{
				this.rowOutlierList.set(rowIndex, true);
			}
		}
		
		// send an event
		OutlierChangeEvent event = new OutlierChangeEvent("StatTableModel");
		this.fireEvent(event);
	}
	
	/**
	 * Mark the row with the given index as an outlier.
	 * 
	 * @param rowIndex
	 * @param b
	 */
	public void markRowAsOutlier(int rowIndex, boolean b)
	{
		this.rowOutlierList.set(rowIndex, b);

		// also update the cell outlier list
		for (int i = 0; i < this.getColumnCount(); i++)
		{
			this.cellOutlierList.get(i).set(rowIndex, b);
		}
		
		// send an event
		OutlierChangeEvent event = new OutlierChangeEvent("StatTableModel");
		this.fireEvent(event);
	}
	
	/**
	 * Get the height reserved for the dialog button of the views
	 * based on the setting 'views editable' yes/no.
	 * Returns 0 is no dialog button is shown.
	 */
	public int getDialogButtonHeight()
	{
		int height = 0;
		
		if (this.isViewsEditable())
		{
			height = StatistiekGWT.BUTTON_HEIGHT;
		}
		
		return height;
	}
}