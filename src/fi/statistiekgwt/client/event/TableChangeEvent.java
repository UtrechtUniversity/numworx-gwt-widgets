package fi.statistiekgwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class TableChangeEvent extends GwtEvent<TableChangeEventHandler>
{
	public static Type<TableChangeEventHandler> TYPE = new Type<TableChangeEventHandler>();
	
	public static String REMOVE_ROWS = "removeRows";
	public static String VIEWS_EDITABLE = "viewsEditable";
	public static String DATA_EDITABLE = "dataEditable";
	public static String VIEWS_ADDABLE = "viewsAddable";
	public static String SET_COLUMN_NAME = "setColumnName";
	public static String SET_VALUE_AT = "setValueAt";
	public static String ADD_ROW = "addRow";
	public static String ADD_COLUMN = "addColumn";
	public static String EDIT_COLUMN = "editColumn";
	public static String REMOVE_COLUMN = "removeColumn";
	public static String REMOVE_ROW = "removeRow";
	public static String SORT_COLUMN = "sortColumn";
	public static String UPDATE_NUMERICAL_COLUMN_TYPES = "updateNumericalColumnTypes";
	public static String IMPORT_DATA = "importData";

	private final String info;
	/**
	 * The index of the column that has changed.
	 * If not applicable the index is -1.
	 */
	private final int columnIndex;

    public TableChangeEvent(String info, int columnIndex) 
    {
        this.info = info;
        this.columnIndex = columnIndex;
    }

	@Override
	public Type<TableChangeEventHandler> getAssociatedType()
	{
		return this.TYPE;
	}

	@Override
	protected void dispatch(TableChangeEventHandler handler)
	{
		handler.onTableChange(this);
	}

	public String getInfo() 
	{
        return this.info;
    }

	public int getColumnIndex() 
	{
        return this.columnIndex;
    }
}
