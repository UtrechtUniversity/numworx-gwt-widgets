package fi.statistiekgwt.client.columndialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekUtils;
import fi.statistiekgwt.client.event.AddColumnEvent;
import fi.statistiekgwt.client.event.AddColumnEventHandler;
import fi.statistiekgwt.client.event.EditColumnEvent;
import fi.statistiekgwt.client.event.EditColumnEventHandler;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * Model for the add or edit column dialog.
 * Is deze nog nodig? Wijzigingen worden toch uit view afgelezen 
 * en niet via ColumnDialogModel aan StatTableModel doorgegeven.
 * (ColumnDialogController stuurt een Add/EditColumnEvent naar de handler (StatTableModel).)
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 */
public class ColumnDialogModel implements HasHandlers// extends Observable
{
	private String name;
	private String oldName;
	private AllowedTypes type;
	private int columnIndex;
	private ArrayList<String> enumOptions;
	private String uitleg;
	private StatTableModel tableModel;
	private boolean donePressed;
	
	private boolean hasChangedName;
	private boolean hasChangedType;
	private boolean hasChangedEnumOptions;
	private boolean hasChangedUitleg;
	
	EventBus bus = StatistiekUtils.EVENT_BUS;//new SimpleEventBus();

	/**
	 * Constructor
	 */
	public ColumnDialogModel(StatTableModel tableModel)
	{
		this.tableModel = tableModel;
		this.name = this.getFirstFreeName();
		this.oldName = new String("");
		this.type = AllowedTypes.INTEGER;
		this.enumOptions = new ArrayList<String>();
		this.enumOptions.add(ColumnType.WILDCARD);
		this.uitleg = new String();
		this.donePressed = false;
		
		this.hasChangedName = false;
		this.hasChangedType = false;
		this.hasChangedEnumOptions = false;
		this.hasChangedUitleg = false;
	}

	/**
	 * Constructor for restoring previous settings
	 * 
	 * @param tableModel
	 *            The table model
	 * @param name
	 *            The initial column name
	 * @param cType
	 *            The initial column type
	 */
	public ColumnDialogModel(StatTableModel tableModel, String name,
		ColumnType cType, int columnIndex)
	{
		this.name = name;
		this.oldName = name;
		this.tableModel = tableModel;
		this.type = cType.getType();
		this.columnIndex = columnIndex;
		this.enumOptions = new ArrayList<String>();
		if (this.type.equals(AllowedTypes.ENUM))
		{
			for (String s : cType.getEnumOptions())
			{
				this.enumOptions.add(s);
			}
		}
		else
		{
			this.enumOptions.add(ColumnType.WILDCARD);
		}

		this.uitleg = cType.getUitleg();
		this.donePressed = false;
		
		this.hasChangedName = false;
		this.hasChangedType = false;
		this.hasChangedEnumOptions = false;
		this.hasChangedUitleg = false;
	}

	/**
	 * Set new type value
	 * 
	 * @param type
	 *            new AllowedType value
	 */
	public void setType(AllowedTypes type)
	{
		this.type = type;
	}
	
	public int getColumnIndex()
	{
		return this.columnIndex;
	}

	public boolean getDonePressed()
	{
		return this.donePressed;
	}

	public void setDonePressed(boolean b)
	{
		this.donePressed = b;
	}

	public void setHasChangedName(boolean b)
	{
		this.hasChangedName = b;
	}

	public void setHasChangedType(boolean b)
	{
		this.hasChangedType = b;
	}

	public void setHasChangedEnumOptions(boolean b)
	{
		this.hasChangedEnumOptions = b;
	}

	public void setHasChangedUitleg(boolean b)
	{
		this.hasChangedUitleg = b;
	}

	public boolean hasChangedName()
	{
		return this.hasChangedName;
	}

	public boolean hasChangedType()
	{
		return this.hasChangedType;
	}

	public boolean hasChangedEnumOptions()
	{
		return this.hasChangedEnumOptions;
	}

	public boolean hasChangedUitleg()
	{
		return this.hasChangedUitleg;
	}

	/**
	 * Check if a name is already in use
	 * 
	 * @param name
	 *            The name to check
	 * @return true iff name is in use
	 */
	private boolean nameInUse(String name)
	{
		if (name.equals(this.oldName))
		{
			// You can use the old name again
			return false;
		}
		
		ArrayList<String> list = this.tableModel.getColumnNames();
		for (String s : list)
		{
			if (s.equals(name) && !s.equals(this.oldName))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Find the first unused column name "Column #"
	 * 
	 * @return the first unused column name
	 */
	private String getFirstFreeName()
	{
		int i = 1;
		while (this.nameInUse("Column " + i))
		{
			i++;
		}
		return "Column " + i;
	}

	/**
	 * Set new column name
	 * 
	 * @param name
	 *            new name
	 */
	public void setName(String name)
	{
		if (!name.isEmpty() && !this.nameInUse(name))
		{
			this.name = name;
		}
	}

	/**
	 * @return this column's name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Add an element to enumeration
	 * 
	 * @param option
	 *            new element
	 */
	public void addEnumOption(String option)
	{
		if (!this.enumOptions.contains(option))
		{
			// set as last element of current arraylist
			this.enumOptions.set(this.enumOptions.size() - 1, option);

			// add WILDCARD again
			this.enumOptions.add(ColumnType.WILDCARD);
		}
	}

	/**
	 * Add an element to enumeration without firing an event
	 * 
	 * @param option
	 *            new element
	 */
	public void addEnumOptionWithoutEvent(String option)
	{
		if (!this.enumOptions.contains(option))
		{
			// set as last element of current arraylist
			this.enumOptions.set(this.enumOptions.size() - 1, option);

			// add WILDCARD again
			this.enumOptions.add(ColumnType.WILDCARD);
		}
	}

	/**
	 * Remove last element from enumeration
	 */
	public void removeLastEnumOption()
	{
		if (this.enumOptions.size() > 1)
		{
			this.enumOptions.remove(this.enumOptions.size() - 2);
		}
	}

	/**
	 * Removes an enum option if that is a valid option and the option is not
	 * the wildcard option
	 * 
	 * @param index
	 *            the index of the option to remove
	 */
	public void removeEnumOption(int index)
	{
		if (index >= 0 && index < this.enumOptions.size()
			&& !this.enumOptions.get(index).equals(ColumnType.WILDCARD))
		{
			this.enumOptions.remove(index);
		}
	}

	/**
	 * Set new uitleg for this column
	 * 
	 * @param uitleg
	 *            new uitleg
	 */
	public void setUitleg(String uitleg)
	{
		//System.out.println("setUitleg(" + uitleg + ")");
		this.uitleg = uitleg;
	}

	/**
	 * @return enumeration elements as ArrayList
	 */
	public ArrayList<String> getEnumOptions()
	{
		return this.enumOptions;
	}

	/**
	 * @return enumeration elements as String
	 */
	public String getEnumOptionsString()
	{
		String ret = new String();
		for (int i = 0; i < this.enumOptions.size(); i++)
		{
			ret = ret + this.enumOptions.get(i) + "\n";
		}
		return ret;
	}

	/**
	 * @return this column's AllowedType
	 */
	public AllowedTypes getType()
	{
		return this.type;
	}
	
	/**
	 * @return The statTableModel
	 */
	public StatTableModel getTableModel()
	{
		return this.tableModel;
	}

	/**
	 * @return this column's uitleg
	 */
	public String getUitleg()
	{
		return this.uitleg;
	}

	/**
	 * Remove al enum options except '*'.
	 */
	public void removeAllEnumOption()
	{
		for (int i = this.enumOptions.size() - 1; i > -1; i--)
		{
			if (!this.enumOptions.get(i).equals(ColumnType.WILDCARD))
			{
				this.enumOptions.remove(i);
			}
		}
	}

	/**
	 * Sort enum options alphabetically
	 */
	public void sortEnumOptions()
	{
		String[] sortedEnumOptions = new String[this.enumOptions.size()];
		sortedEnumOptions = this.enumOptions.toArray(sortedEnumOptions);
		
		Arrays.sort(sortedEnumOptions, new Comparator<String>() {
            @Override
            /**
             * Compare strings alphabetically. 
             * A wildcard is larger than any other string.
             * @param s1
             * @param s2
             * @return
             */
            public int compare(String s1, String s2) 
            {
            	// check for wildcard among the strings
            	if (s1.equals(ColumnType.WILDCARD))
            		return 1;
            	else if (s2.equals(ColumnType.WILDCARD))
            		return -1;
            	else 
            	{
            		// apart from '*' sort the enum options alphabetically
            		return s1.compareTo(s2);
            	}
            }
        });
		
		this.enumOptions = new ArrayList(Arrays.asList(sortedEnumOptions));
	}

	/**
	 * Swap enum options with index1 and index2. A wildcard is not swapped. 
	 * @param index1
	 * @param index2
	 */
	public void swapEnumOptions(int index1, int index2)
	{
		if (this.validEnumIndex(index1) && this.validEnumIndex(index2)
			&& !this.enumOptions.get(index1).equals(ColumnType.WILDCARD) // the wildcard should stay at the end
			&& !this.enumOptions.get(index2).equals(ColumnType.WILDCARD))
		{
			Collections.swap(this.enumOptions, index1, index2);
		}
	}

	/**
	 * Check whether index is a valid index in enum options.
	 * @param index
	 * @return True if index is a valid index, else false.
	 */
	private boolean validEnumIndex(int index)
	{
		boolean isValid = false;
		
//		if ((this.enumOptions == null) || this.enumOptions.size() == 0)
//			isValid = false;
//		else 
			if ((index > -1) && (index < this.enumOptions.size()))
			isValid = true;
		
		return isValid;
	}

	/**
	 * Send event to receiver StatTableModel.
	 */
	@Override
	public void fireEvent(GwtEvent<?> event) 
	{
	    bus.fireEvent(event);
	}

	/**
	 * Subscribe for add column events
	 */
	public HandlerRegistration addAddColumnEventHandler(AddColumnEventHandler handler)
	{
		return bus.addHandler(AddColumnEvent.TYPE, handler);
	}

	/**
	 * Get the name as it was when ColumnDialog was opened.
	 * If a new column is added, old name is an empty string.
	 * @return
	 */
	public String getOldName()
	{
		return this.oldName;
	}

	/**
	 * Subscribe for edit column events
	 */
	public HandlerRegistration addEditColumnEventHandler(EditColumnEventHandler handler)
	{
		return bus.addHandler(EditColumnEvent.TYPE, handler);
	}
}
