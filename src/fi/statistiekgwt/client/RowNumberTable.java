package fi.statistiekgwt.client;

import java.beans.*;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;

/*
 *	Use a CellTable as a renderer for row numbers of a given main table.
 *  This table must be added to the row header of the scrollpanel that
 *  contains the main table.
 *  
 *  Bron: http://tips4java.wordpress.com/2008/11/18/row-number-table/
 */
public class RowNumberTable extends CellTable<Object> //implements ChangeListener, PropertyChangeListener
{
	private CellTable<Object> main;

	public RowNumberTable(CellTable<Object> table)
	{
		main = table;
		//main.addPropertyChangeListener(this);

		//setFocusable(false);
		//setAutoCreateColumnsFromModel(false);
		//setModel(main.getModel());
		setSelectionModel(main.getSelectionModel());

		//main.setSelectionBackground(ColorGenerator.SELECTION_COLOR);

		EditTextCell editCell = new EditTextCell(); // TODO use this type of cells
		Column<Object, Object> column = new Column<Object, Object>(null)
		{
			@Override
			public String getValue(Object o)
			{
				return o.toString();
			}
		};
		
		column.setFieldUpdater(new FieldUpdater<Object, Object>() 
		{
		     @Override
		     public void update(int index, Object object, Object value) 
		     {
		         // do something when value changes
		     }
		});
		
		//column.setHeaderValue(" ");
		addColumn(column);
		//column.setCellRenderer(new RowNumberRenderer());

		//getColumnModel().getColumn(0).setPreferredWidth(50);
		//setPreferredScrollableViewportSize(getPreferredSize());
	}

//	@Override
//	public void addNotify()
//	{
//		super.addNotify();
//
//		Component c = getParent();
//
//		// Keep scrolling of the row table in sync with the main table.
//
//		if (c instanceof JViewport)
//		{
//			JViewport viewport = (JViewport) c;
//			viewport.addChangeListener(this);
//		}
//	}

	/*
	 * Delegate method to main table
	 */
	@Override
	public int getRowCount()
	{
		return main.getRowCount();
	}

	public int getRowHeight(int row)
	{
		return main.getRowElement(row).getOffsetHeight();//getRowHeight(row);
	}

	/*
	 * This table does not use any data from the main TableModel, so just return
	 * a value based on the row parameter.
	 * ...?
	 */
	public Object getValueAt(int row, int column)
	{
		return Integer.toString(row + 1);
	}

	/*
	 * Don't edit data in the main TableModel by mistake
	 */
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	//
	// Implement the ChangeListener
	//
//	public void stateChanged(ChangeEvent e)
//	{
//		// Keep the scrolling of the row table in sync with main table
//
//		JViewport viewport = (JViewport) e.getSource();
//		JScrollPane scrollPane = (JScrollPane) viewport.getParent();
//		scrollPane.getVerticalScrollBar()
//			.setValue(viewport.getViewPosition().y);
//	}

	//
	// Implement the PropertyChangeListener
	//
//	public void propertyChange(PropertyChangeEvent e)
//	{
//		// Keep the row table in sync with the main table
//
//		if ("selectionModel".equals(e.getPropertyName()))
//		{
//			setSelectionModel(main.getSelectionModel());
//		}
//
//		if ("model".equals(e.getPropertyName()))
//		{
//			setModel(main.getModel());
//		}
//	}

	/*
	 * Borrow the renderer from JDK1.4.2 table header
	 */
//	private static class RowNumberRenderer extends DefaultTableCellRenderer
//	{
//		public RowNumberRenderer()
//		{
//			setHorizontalAlignment(JLabel.CENTER);
//		}
//
//		public Component getTableCellRendererComponent(JTable table,
//			Object value, boolean isSelected, boolean hasFocus, int row,
//			int column)
//		{
//			if (table != null)
//			{
//				JTableHeader header = table.getTableHeader();
//
//				if (header != null)
//				{
//					setForeground(header.getForeground());
//					setBackground(header.getBackground());
//					setFont(header.getFont());
//				}
//			}
//
//			if (isSelected)
//			{
//				setFont(getFont().deriveFont(Font.BOLD));
//			}
//
//			setText((value == null) ? "" : value.toString());
//			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
//
//			return this;
//		}
//	} // class RowNumberRenderer
}
