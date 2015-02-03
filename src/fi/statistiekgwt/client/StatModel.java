package fi.statistiekgwt.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

import fi.statistiekgwt.client.event.AddViewEvent;
import fi.statistiekgwt.client.event.AddViewEventHandler;

/**
 * Statistiek MVC Model
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class StatModel implements HasHandlers//extends Observable// implements TableModelListener 
{
	private StatTableModel statTableModel;

	// Only resetting a StatTableModel is causing too much trouble with respect
	// to updating views
	// private StatTableModel resetData; // the data to be reset

	HashMap<Object, String> resetHashMap;

	private ArrayList<StatistiekView> views;
	private ArrayList<Boolean> viewInOwnWindow;

	EventBus eventBus = StatistiekUtils.EVENT_BUS;//new SimpleEventBus();

	/**
	 * Constructor
	 */
	public StatModel()
	{
		this.views = new ArrayList<StatistiekView>();
		this.viewInOwnWindow = new ArrayList<Boolean>();
		this.statTableModel = new StatTableModel();
		this.resetHashMap = new HashMap();

//		this.data.addTableModelListener(this);
//		this.data.addChangeHandler(this);

		this.addView(StatistiekGWT.createView("Table",
			StatistiekGWT.rb.getString("tableOption") + " 1", this.statTableModel, 0, 0, null));
	}

	/**
	 * Find the lowest i >= 1 for which the view name "viewTypeName i" is free
	 * 
	 * @param viewTypeName
	 *            The name of the type of view, element of Statistiek.VIEWS
	 * @return an unique viewname
	 */
	public String findUniqueViewName(String viewTypeName)
	{
		int i;
		findI: for (i = 1; true; i++)
		{
			String s = viewTypeName + " " + i;
			for (StatistiekView view : this.views)
			{
				if (view.getViewName().equals(s))
				{
					// try next i
					continue findI;
				}
			}

			return viewTypeName + " " + i;
		}
	}

	/**
	 * Add a StatistiekView
	 * 
	 * @param view
	 *            The view that will be added
	 */
	public void addView(StatistiekView view)
	{
		this.views.add(view);
		this.viewInOwnWindow.add(false);

		AddViewEvent event = new AddViewEvent(view.getViewName());
		this.fireEvent(event);
	}

	public int mainWindowIndexToGeneralIndex(int mainWindowIndex)
	{
		int mainViews = 0;
		int separateViews = 0;
		while (mainViews <= mainWindowIndex)
		{
			if ((mainViews + separateViews) < viewInOwnWindow.size()
				&& this.viewInOwnWindow.get(mainViews + separateViews))
			{
				separateViews++;
			}
			else
			{
				mainViews++;
			}
		}

		return mainViews + separateViews - 1;
	}

	public void setViewSeparateWindowByObject(StatistiekView sv, boolean b)
	{
		int i = this.views.indexOf(sv);
		if (i >= 0)
		{
			this.setViewSeparateWindow(i, b);
		}
	}

	/**
	 * If isSeparate is true, set view with viewIndex in separate window, else
	 * set view in tabPane. 
	 * @param viewIndex
	 * @param isSeparate
	 */
	public void setViewSeparateWindow(int viewIndex, boolean isSeparate)
	{
		System.out.println("View " + viewIndex
			+ " is set to show in separate window: " + isSeparate);
		this.viewInOwnWindow.set(viewIndex, isSeparate);
//		super.setChanged();
//		super.notifyObservers();
	}

	/**
	 * Remove a view by name
	 * 
	 * @param viewName
	 *            the name of the view that will be removed
	 */
	public void removeView(String viewName)
	{
		for (int i = 0; i < this.views.size(); i++)
		{
			if (this.views.get(i).getViewName().equals(viewName))
			{
				this.removeView(i);
				break;
			}
		}
	}

	/**
	 * Remove a view by index
	 * 
	 * @param viewIndex
	 *            the index of the view that will be removed
	 */
	public void removeView(int viewIndex)
	{
		this.views.get(viewIndex).removeHandlers();
		this.views.remove(viewIndex);
		this.viewInOwnWindow.remove(viewIndex);
		
//		super.setChanged();
//		super.notifyObservers();
	}

	/**
	 * Get the added views
	 * 
	 * @return ArrayList containing all views
	 */
	public ArrayList<StatistiekView> getViews()
	{
		return this.views;
	}

	/**
	 * Get the views that should be displayed in the main window
	 * 
	 * @return ArrayList of StatistiekViews that should be displayed in the main
	 *         window
	 */
	public ArrayList<StatistiekView> getMainWindowViews()
	{
		ArrayList<StatistiekView> mainWindowViews = new ArrayList<StatistiekView>();
		for (int i = 0; i < this.views.size(); i++)
		{
			if (!this.viewInOwnWindow.get(i))
			{
				mainWindowViews.add(this.views.get(i));
			}
		}

		return mainWindowViews;
	}

	/**
	 * Get the views that should be displayed in a separate window
	 * 
	 * @return ArrayList of StatistiekViews that should be displayed in a
	 *         separate window
	 */
	public ArrayList<StatistiekView> getSeparateWindowViews()
	{
		ArrayList<StatistiekView> separateWindowViews = new ArrayList<StatistiekView>();
		for (int i = 0; i < this.views.size(); i++)
		{
			if (this.viewInOwnWindow.get(i))
			{
				separateWindowViews.add(this.views.get(i));
			}
		}

		return separateWindowViews;
	}

	public ArrayList<Boolean> getViewInOwnWindow()
	{
		return this.viewInOwnWindow;
	}

	public void setViewInOwnWindow(ArrayList<Boolean> viewInOwnWindow)
	{
		this.viewInOwnWindow = viewInOwnWindow;
//		super.setChanged();
//		super.notifyObservers();
	}

	/**
	 * Get the data
	 * 
	 * @return the data in a StatTableModel
	 */
	public StatTableModel getStatTableModel()
	{
		return this.statTableModel;
	}

	/**
	 * Get the reset hashtable
	 * 
	 * @return the reset hashtable
	 */
	public HashMap getResetHashMap()
	{
		// System.out.println("StatModel.getResetHashtable(): resetHashtable=" +
		// resetHashtable);
		return this.resetHashMap;
	}

	/**
	 * Remove all views
	 */
	public void removeViewsWithoutEvent()
	{
        Iterator<StatistiekView> iterator = this.views.iterator();
        while (iterator.hasNext()) 
        {
        	StatistiekView view = iterator.next();
        	if (view != null)
        		iterator.remove();
        }

		this.views = new ArrayList<StatistiekView>();
		this.viewInOwnWindow = new ArrayList<Boolean>();
	}

	/**
	 * Change data table
	 * 
	 * @param data
	 *            the new data table
	 */
	public void setData(StatTableModel data)
	{
		this.statTableModel = data;
		//this.data.addChangeHandler(this);
//		super.setChanged();
//		super.notifyObservers();
	}

	/**
	 * Set the reset hashtable
	 * 
	 * @param statTableModel
	 *            the reset hashtable
	 */
	public void setResetHashtable(HashMap h)
	{
		// System.out.println("StatModel.setResetHashtable(h=" + h + ")");
		this.resetHashMap = h;
	}

	@Override
	public void fireEvent(GwtEvent<?> event)
	{
	    eventBus.fireEvent(event);
	}
	
	/**
	 * Subscribe for events
	 */
	public HandlerRegistration addAddViewEventHandler(AddViewEventHandler handler)
	{
		return eventBus.addHandler(AddViewEvent.TYPE, handler);
	}
}
