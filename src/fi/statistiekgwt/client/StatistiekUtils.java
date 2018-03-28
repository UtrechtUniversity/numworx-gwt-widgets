package fi.statistiekgwt.client;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HorizontalScrollbar;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalScrollbar;

import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * Class that provides utility classes.
 * 
 * @author borku102
 * 
 */
public class StatistiekUtils
{
	public static EventBus EVENT_BUS = GWT.create(SimpleEventBus.class);
	
	/**
	 * Dummy touch handler to avoid that the DragOnTouch handler of an external view
	 * will prevent click events on stattable. Implements touchstart and touchend handling.
	 * 
	 * @author Sylvia van Borkulo
	 *
	 */
	public static class DummyTouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler//, ContextMenuHandler
	{
		@Override
		public void onTouchStart(TouchStartEvent event)
		{
			event.stopPropagation();
		}

		@Override
		public void onTouchMove(TouchMoveEvent event)
		{
			event.stopPropagation();
		}

		@Override
		public void onTouchEnd(TouchEndEvent event)
		{
			event.stopPropagation();
		}

//		@Override
//		public void onContextMenu(ContextMenuEvent event)
//		{
//			event.preventDefault();
//		}
	} // class DummyTouchHandler
	
	/**
	 * A custom scrollpanel class to enable the hiding of scrollbars.
	 * 
	 * @author Sylvia van Borkulo
	 *
	 */
	public static class CustomScrollPanel extends ScrollPanel
	{
		public CustomScrollPanel(Canvas canvas)
		{
			super(canvas);
		}

		public void setAlwaysHideHorizontalScrollBar(boolean alwaysHide)
		{
			getScrollableElement().getStyle().setOverflowX(
				alwaysHide ? Overflow.HIDDEN : Overflow.AUTO);
		}
		
		public void setAlwaysHideVerticalScrollBar(boolean alwaysHide)
		{
			getScrollableElement().getStyle().setOverflowY(
				alwaysHide ? Overflow.HIDDEN : Overflow.AUTO);
		}
		
		public void setAlwaysHideScrollBars(boolean alwaysHide)
		{
			this.setAlwaysHideHorizontalScrollBar(alwaysHide);
			this.setAlwaysHideVerticalScrollBar(alwaysHide);
		}

		public HorizontalScrollbar getHorizontalScrollbar()
		{
			return this.getHorizontalScrollbar();
		}

		public VerticalScrollbar getVerticalScrollbar()
		{
			return this.getVerticalScrollbar();
		}
		
	} // class MyScrollPanel
	
	public static DummyTouchHandler getDummyTouchHandler()
	{
		return new DummyTouchHandler();
	}
	
	/**
	 * Remove all items from the given listbox.
	 * 
	 * @param listBox
	 */
	public static void removeAllItemsFromListBox(ListBox listBox)
	{
		for (int i = listBox.getItemCount() - 1; i > -1; i--)
		{
			listBox.removeItem(i);
		}
	}

	/**
	 * Remove all widgets from the given panel.
	 * 
	 * @param panel
	 */
	public static void removeAllWidgetsFromPanel(ComplexPanel panel)
	{
		int count = panel.getWidgetCount();
		for (int i = count - 1; i > -1; i--)
		{
			panel.remove(i);
		}
	}
	
	/**
	 * Zet voor enum-only view j/n de kolomnamen in de gegeven varListBox 
	 * al dan niet enabled.
	 * Als enumOnlyView true is, dan worden alleen de 
	 * kolomnamen van kolommen van type enum enabled.
	 * Als enumOnlyView false, dan worden alle kolomnamen enabled.
	 * 
	 * @param varListBox     De lijst met kolomnamen.
	 * @param isEnumOnlyView
	 * @param ignoreFirstItem De eerste overslaan (bijv "kies een variabele")
	 * @param model
	 */
	public static void setEnumOnlyColumnsEnabledVarListBox(ListBox varListBox, boolean isEnumOnlyView, boolean ignoreFirstItem, StatTableModel model)
	{
		ArrayList<ColumnType> types = model.getColumnTypes();
		
		NodeList startVarOptions = varListBox.getElement().<SelectElement>cast().getOptions();
		
		for (int i = 0; i < types.size(); i++)
		{
			Node option;
			if (ignoreFirstItem)
				option = startVarOptions.getItem(i + 1); // de eerste overslaan (bijv "kies een variabele")
			else
				option = startVarOptions.getItem(i);

			if (isEnumOnlyView && AllowedTypes.ENUM.toString().equals(types.get(i).getType().toString()))
			{
				((SelectElement) option).setDisabled(!isEnumOnlyView);
			}
			else
				((SelectElement) option).setDisabled(isEnumOnlyView);
		}
	}
}

