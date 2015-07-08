package fi.statistiekgwt.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;

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
	public static class DummyTouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
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
}

