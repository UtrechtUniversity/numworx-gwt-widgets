package fi.statistiekgwt.client;

import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.gargoylesoftware.htmlunit.javascript.host.Window;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.event.TableChangeEvent;
import fi.statistiekgwt.client.event.TableChangeEventHandler;

/**
 * Statistiek InteractiePanel MVC View
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class StatInteractiePanelView extends LayoutPanel implements TableChangeEventHandler//TabLayoutPanel// implements Observer
{
	private static final String RESET_ICON_PATH = "resources/reseticon.gif";
	protected StatModel model;
	private StatInteractiePanel controller;
	
	private static Label NO_VIEWS_LABEL = new Label("No views added.");
	private TabLayoutPanel tabPanel;
	private LayoutPanel addViewTab;
	private ListBox viewsBox, startVarBox, startVar2Box;
	private HandlerRegistration viewsBoxHandlerRegistration;
	private HandlerRegistration startVarBoxHandlerRegistration;
	private HandlerRegistration startVar2BoxHandlerRegistration;
	private Label addViewLabel;
	private Label chooseStartVarLabel, chooseStartVar2Label;

	// button to reset the data -> button now implemented in StatTable for
	// layout reasons
	// keep the code, because StatInteractiePanelView is a more logical place
	// private JButton resetButton;

	// The index of the selected view
	private int selectedView;

	// The index of the previous selected view
	private int previousSelectedView = 0;

	// The index of the selected view in tabPane
	private int selectedViewInTabPane = 0;

	private ArrayList<SeparateViewDialog> dialogs;

	/**
	 * Constructor
	 * 
	 * @param model
	 *            MVC Model
	 * @param controller
	 *            MVC Controller
	 */
	public StatInteractiePanelView(StatModel model, 
		StatInteractiePanel controller,
		double barHeight,
		Unit barUnit)
	{
		//super(barHeight, barUnit); // komt van TabLayoutPanel
		this.tabPanel = new TabLayoutPanel(barHeight, barUnit);
		
		this.model = model;
		this.model.getStatTableModel().addChangeEventHandler(this);//addObserver(this);
		this.controller = controller;
		
		this.dialogs = new ArrayList<SeparateViewDialog>();

		this.addViewTab = new LayoutPanel();

		this.addViewLabel = new Label(StatistiekGWT.rb.getString("addaviewKnopTekst"));

		this.chooseStartVarLabel = new Label(StatistiekGWT.rb.getString("chooseStartVarLabel"));
		this.chooseStartVarLabel.setVisible(false);

		this.chooseStartVar2Label = new Label(StatistiekGWT.rb.getString("chooseStartVarColumnLabel"));
		this.chooseStartVar2Label.setVisible(false);

		this.viewsBox = new ListBox();
		this.viewsBox.addItem(StatistiekGWT.rb.getString("chooseaviewOption"));
		for (int i = 0; i < StatistiekGWT.VIEWS_translated.length; i++)
		{
			this.viewsBox.addItem(StatistiekGWT.VIEWS_translated[i]);
		}
		this.viewsBoxHandlerRegistration = 
			this.viewsBox.addChangeHandler(this.controller);

		this.startVarBox = new ListBox();
		this.startVarBoxHandlerRegistration = 
			this.startVarBox.addChangeHandler(this.controller);
		this.startVarBox.setVisible(false);

		this.startVar2Box = new ListBox();
		this.startVar2BoxHandlerRegistration = 
			this.startVar2Box.addChangeHandler(this.controller);
		this.startVar2Box.setVisible(false);

		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.add(addViewLabel);
		hp1.add(viewsBox);

		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.add(chooseStartVarLabel);
		hp2.add(startVarBox);

		HorizontalPanel hp3 = new HorizontalPanel();
		hp3.add(chooseStartVar2Label);
		hp3.add(startVar2Box);

		VerticalPanel vp = new VerticalPanel();
		vp.add(hp1);
		vp.add(hp2);
		vp.add(hp3);

		HorizontalPanel hp = new HorizontalPanel();
		hp.add(vp);

		addViewTab.add(hp);

		this.update();//(null, null);
	}

	public void clearAddViewTab()
	{
		this.viewsBoxHandlerRegistration.removeHandler();
		viewsBox.setSelectedIndex(0);
		this.viewsBoxHandlerRegistration = this.viewsBox.addChangeHandler(controller);

		this.startVarBoxHandlerRegistration.removeHandler();
		startVarBox.setSelectedIndex(0);
		this.startVarBoxHandlerRegistration = this.startVarBox.addChangeHandler(controller);
		startVarBox.setVisible(false);
		chooseStartVarLabel.setVisible(false);
		
		this.startVar2BoxHandlerRegistration.removeHandler();
		startVar2Box.setSelectedIndex(0);
		this.startVar2BoxHandlerRegistration = this.startVar2Box.addChangeHandler(controller);
		startVar2Box.setVisible(false);
		chooseStartVar2Label.setVisible(false);
	}
	
	public ListBox getStartVarBox()
	{
		return this.startVarBox;
	}

	public void setStartVarBox(boolean b)
	{
		startVarBox.setVisible(b);
		chooseStartVarLabel.setVisible(b);
	}
	
	public void setStartVarLabel(String s)
	{
		chooseStartVarLabel.setText(s);
	}

	public void setStartVar2Label(String s)
	{
		chooseStartVar2Label.setText(s);
	}

	public ListBox getStartVar2Box()
	{
		return this.startVar2Box;
	}

	public void setStartVar2Box(boolean b)
	{
		startVar2Box.setVisible(b);
		chooseStartVar2Label.setVisible(b);
	}

	/**
	 * Get the model
	 */
	public StatModel getModel()
	{
		return model;
	}

	/**
	 * Get the controller
	 */
	public StatInteractiePanel getController()
	{
		return controller;
	}

	/**
	 * Set new model
	 * 
	 * @param model
	 *            The new model
	 */
	public void setModel(StatModel model)
	{
		if (this.model != model)
		{
			GWT.log("StatInteractiePanelView.setModel()");
			//this.model.deleteObserver(this);
			this.model = model;
			//this.model.addObserver(this);
			this.update();//(null, null);
		}
	}

	/**
	 * @return The index of the currently selected view
	 */
	public int getSelectedView()
	{
		return selectedView;
	}

	/**
	 * @return The index of the previous selected view
	 */
	public int getPreviousSelectedView()
	{
		return previousSelectedView;
	}

	/**
	 * Set the field previousSelectedView
	 * 
	 * @param view
	 *            The index of the previous selected view
	 */
	public void setPreviousSelectedView(int view)
	{
//		GWT.log("setPreviousSelectedView(view=" + view + ")");
		previousSelectedView = view;
	}

	/**
	 * Process the actions related to selecting a view: Set the selected view,
	 * the previous selected view, and update the selected view in tabPane. This
	 * method is called when a view in own window is activated.
	 * 
	 * @param view
	 *            the index of the view that has to be selected
	 */
	public void processSelectedView(int view)
	{
		// GWT.log("*** begin *** StatInteractiePanelView.processSelectedView(view="
		// + view + ")");

		if (view < this.model.getViews().size()
			&& this.model.getViews().size() > 1) // check if valid view number
		{
			// set the previous selected view
			if (view != selectedView)
			{
				setPreviousSelectedView(selectedView);
			}
			else
				// GWT.log("view = selectedView!");

				// set the selected view
				setSelectedView(view);

			// update the selected view in tabPane
			setTabPane(selectedViewInTabPane);
		}
		// GWT.log("*** end *** StatInteractiePanelView.processSelectedView(view="
		// + view + ")");
	}

	/**
	 * Set the field selectedView
	 * 
	 * @param view
	 */
	private void setSelectedView(int view)
	{
		// GWT.log("setSelectedView(view=" + view + ")");
		selectedView = view;
	}

	/**
	 * Select the correct tab in tabPane.
	 */
	private void setTabPane(int view)
	{
		// maak selectedViewInTabPane zichtbaar in tabPane
		selectViewInTabPane(view);
	}

	/**
	 * Select view in tabPane
	 * 
	 * @param view
	 */
	private void selectViewInTabPane(int view)
	{
		int tab;

		// Determine the tab number of view in tabPane
		// taking into account views in own window
		tab = determineTab(view);

		// test syl: je kunt aangeven of er events moeten worden getriggerd
		boolean fireEvents = false;
		tabPanel.selectTab(tab, fireEvents);
	}

	/**
	 * Determine the tab number of view in tabPane taking into account views in
	 * own window
	 * 
	 * @param view
	 * @return
	 */
	private int determineTab(int view)
	{
		int tab;
		int count = 0;

		ArrayList<Boolean> viewInOwnWindow = getModel().getViewInOwnWindow();

		// GWT.log("determineTab(view=" + view +
		// "): viewInOwnWindow=" + viewInOwnWindow);

		if (viewInOwnWindow.get(view).booleanValue())
		{
			// view is not in tabPane
			tab = 0;
		}
		else
		{
			for (int i = 0; i < view; i++)
			{
				if (!viewInOwnWindow.get(i).booleanValue())
				{
					// count the number of views in tabPane
					count++;
				}
			}
			tab = count;
		}

		// GWT.log("determineTab(view=" + view + "): count=" +
		// count);

		return tab;
	}

	/**
	 * Process the actions related to selecting a tab: Set the selected tab in
	 * the tabbed pane and update selectedView and previousSelectedView. This
	 * method is called when a tab in tabPane is selected.
	 * 
	 * @param tab
	 *            the index of the tab that refers to the view that has to be
	 *            selected
	 */
	public void processSelectedTab(int tab)
	{
		// GWT.log("...begin... StatInteractiePanelView.processSelectedTab(tab="
		// + tab + "): this.model.getViews().size() = " +
		// this.model.getViews().size());

		if (tab < this.model.getViews().size()
			&& this.model.getViews().size() > 1)
		{
			// determine the view related to tab
			int view = determineView(tab);

			// set the field selectedViewInTabPane to keep track of the visible
			// tab in tabPane in case of views in own window
			setSelectedViewInTabPane(view);

			// set previous selected view
			if (view != selectedView)
			{
				setPreviousSelectedView(selectedView);
			}
			// else
			// GWT.log("tab = selectedView!");

			setSelectedView(view);

			this.tabPanel.selectTab(tab);
			// GWT.log("StatInteractiePanelView.processSelectedTab(): tabPane.setSelectedIndex(tab="
			// + tab + ")");
		}
		// GWT.log("...end.... StatInteractiePanelView.processSelectedTab");
	}

	/**
	 * Set the field selectedViewInTabPane.
	 * 
	 * @param view
	 */
	public void setSelectedViewInTabPane(int view)
	{
		selectedViewInTabPane = view;
		// GWT.log("setSelectedViewInTabPane(view=" + view + ")");
	}

	/**
	 * Determine the view number related to tab in tabPane.
	 * 
	 * @param tab
	 * @return
	 */
	private int determineView(int tab)
	{
		int count = 0;
		int view = 0;

		ArrayList<Boolean> viewInOwnWindow = getModel().getViewInOwnWindow();

		// GWT.log("determineView(tab=" + tab + "): viewInOwnWindow="
		// + viewInOwnWindow);

		for (int i = 0; i < viewInOwnWindow.size(); i++)
		{
			if (!viewInOwnWindow.get(i).booleanValue())
			{
				count++;
			}
			if (count == tab + 1)
			{
				view = i;
				break;
			}
		}

		// GWT.log("determineView(tab=" + tab + "): view = " + view);

		return view;
	}

	private boolean isInOwnWindow(int view)
	{
		ArrayList<Boolean> viewInOwnWindow = getModel().getViewInOwnWindow();

		if (viewInOwnWindow.get(view))
			return true;
		else
			return false;
	}

	public int indexOfTabWidget(Widget widget)
	{
		return this.tabPanel.getWidgetIndex(widget);
	}

	/**
	 * Tests whether there is a dialog showing the statistiekview "sv"
	 * 
	 * @param sv
	 *            The StatistiekView of which we want to know whether its
	 *            currently being displayed in a dialog
	 * @return true if there is a dialog showing sv
	 */
	private boolean dialogExists(StatistiekView sv)
	{
		for (SeparateViewDialog dialog : this.dialogs)
		{
			if (sv == dialog.sv)
			{
//				GWT.log("Dialog for statistiekview "
//					+ sv.getViewName() + " already exists.");
				return true;
			}
		}

//		GWT.log("Dialog for statistiekview " + sv.getViewName()
//			+ " doesn't exist yet");
		return false;
	}

	/**
	 * Shows a StatistiekView in a separate dialog
	 * 
	 * @param sv
	 *            The statistiekview that will be shown in a separate dialog
	 * @param location
	 *            The initial centerlocation of the dialog
	 */
	public void showViewInDialog(StatistiekView sv, Point location)
	{
		Widget owner = StatistiekGWT.getTopLevelAncestor(this);
		if (owner instanceof Frame)
		{
			Frame frameOwner = (Frame) owner;
			SeparateViewDialog dialog = new SeparateViewDialog(sv, frameOwner);
			this.dialogs.add(dialog);
			dialog.setPopupPosition((int) location.getX(), (int) location.getY());
			dialog.setVisible(true);
		}
		else if (owner instanceof DialogBox)
		{
			DialogBox dialogOwner = (DialogBox) owner;
			SeparateViewDialog dialog = new SeparateViewDialog(sv, dialogOwner);
			this.dialogs.add(dialog);
			dialog.setPopupPosition((int) location.getX(), (int) location.getY());
			dialog.setVisible(true);
		}
		else
		{
			GWT.log("Error finding top level frame/dialog");
		}
	}

	/**
	 * Select the last real tab (not the add view tab) in the tabPane
	 */
	public void selectLastTab()
	{
		this.tabPanel.selectTab(this.tabPanel.getWidgetCount() - 2);
	}

	public void update()//(Observable arg0, Object arg1)
	{
		//GWT.log("StatInteractiePanelView.update()");
		for (int i = super.getWidgetCount() - 1; i > 0; i--)
		{
			super.remove(i);
		}

		ArrayList<StatistiekView> views = this.model.getMainWindowViews();
		ArrayList<StatistiekView> separateWindowViews = this.model.getSeparateWindowViews();

		int amountOfTabs = views.size();
		if (this.model.getStatTableModel().isViewsAddable())
		{
			amountOfTabs++;
		}

		// draw main window
		if (amountOfTabs == 0)
		{
			// GWT.log("no views added");
			super.add(NO_VIEWS_LABEL);
		}
		else if (views.size() == 1 && !this.model.getStatTableModel().isViewsAddable())
		{
			// GWT.log("one view added");
			super.add(views.get(0).getWidget());
		}
		else
		{
			// GWT.log(">1 views added");
			if (this.model.getStatTableModel().isViewsAddable())
			{
				for (int i = 0; i < views.size(); i++)
				{
					StatistiekView view = views.get(i);
					this.tabPanel.add(view.getWidget(), view.getViewName());
//					this.tabPanel.setTabComponentAt(i, new ButtonTabComponent(
//						this.tabPanel, this.controller));
				}
			}
			else
			{
				for (StatistiekView view : views)
				{
					this.tabPanel.add(view.getWidget(), view.getViewName());
				}
			}

			if (this.model.getStatTableModel().isViewsAddable())
			{
				this.tabPanel.add(this.addViewTab, "+");
			}

			DraggedTabTouchHandler touchHandler = new DraggedTabTouchHandler(this.tabPanel);
			this.tabPanel.addHandler(touchHandler, TouchStartEvent.getType());
			// test syl
			this.tabPanel.getElement().getStyle().setBackgroundColor("cyan");
			this.tabPanel.removeFromParent();
			super.add(this.tabPanel);
		}

		// remove dialogs that are no longer needed
		for (int i = 0; i < this.dialogs.size(); i++)
		{
			SeparateViewDialog dialog = this.dialogs.get(i);
			if (!separateWindowViews.contains(dialog.sv))
			{
				dialog.setVisible(false);
				this.dialogs.remove(dialog);
				i--;
			}
		}

		// add new dialogs
		for (StatistiekView sv : separateWindowViews)
		{
			if (!this.dialogExists(sv))
			{
				this.showViewInDialog(sv, new Point(super.getAbsoluteLeft()
					+ (int) (0.5 * super.getOffsetWidth()), super.getAbsoluteTop()
					+ (int) (0.5 * super.getOffsetHeight())));
			}
		}

		// update the selected view in tabPane
//		System.out.println("StatInteractiePanelView.update(): selectedView = " + this.selectedView
//			+ ", selectedViewInPane = " + this.selectedViewInTabPane);
		if (this.isInOwnWindow(selectedView))
		{
			this.setTabPane(previousSelectedView);
		}
		else
		{
			this.setTabPane(selectedViewInTabPane);
		}

		// Fill boxes with variable names
		updateStartVarBox();
		updateStartVar2Box();

//		this.revalidate();
//		this.repaint();
	}

	/*
	 * Update the startVarBox with the variable names.
	 */
	private synchronized void updateStartVarBox()
	{
//		GWT.log("StatInteractiePanelView.updateStartVarBox()");

		// Alleen updaten als er kolomnamen zijn 
		if (this.model.getStatTableModel().getColumnNames().size() > 0)
		{
			// test syl: moet dit zonder handler?
			this.startVarBoxHandlerRegistration.removeHandler();
			
    		// Check the first item
			String firstItem = StatistiekGWT.rb.getString("chooseAVariableOption");
			if ((this.startVarBox.getItemCount() == 0) 
				|| (!firstItem.equals(this.startVarBox.getItemText(0))))
			{
				this.startVarBox.addItem(firstItem);
			}
    
			boolean exists;
			String columnName;
    		// Check the variable names in model.getStatTableModel()
//			for (String varName : this.model.getStatTableModel().getColumnNames())
			for (int j = 0; j < this.model.getStatTableModel().getColumnNames().size(); j++)
			{
				ArrayList<String> list = this.model.getStatTableModel().getColumnNames();
				columnName = list.get(j);
				exists = false;
				for (int i = 0; i < this.startVarBox.getItemCount() && !exists; i++)
				{
					if (columnName.equals(this.startVarBox.getItemText(i)))
					{
						exists = true;
					}
				}
				if (!exists)
				{
					// startVarBox heeft een eerste item 'Kies een variabele', dus j + 1
					this.startVarBox.insertItem(columnName, j + 1);
				}
			}
			
			// Check if items from startVarBox need to be removed
			for (int i = 1; i < this.startVarBox.getItemCount(); i++)
			{
				exists = false;
				ArrayList<String> list = this.model.getStatTableModel().getColumnNames();
				for (String varName : list)
				{
					if (varName.equals(this.startVarBox.getItemText(i)))
					{
						exists = true;
						break;
					}
				}
				if (!exists)
				{
					this.startVarBox.removeItem(i);
				}					
			}
			
			this.startVarBoxHandlerRegistration = this.startVarBox.addChangeHandler(controller);
		}
	}

	/*
	 * Update the startVar2Box with the variable names.
	 */
	private synchronized void updateStartVar2Box()
	{
//		GWT.log("StatInteractiePanelView.updateStartVar2Box()");

		// Alleen updaten als er kolomnamen zijn 
		if (this.model.getStatTableModel().getColumnNames().size() > 0)
		{
			// test syl: moet dit zonder handler?
			this.startVar2BoxHandlerRegistration.removeHandler();
			
    		// Check the first item
			String firstItem = StatistiekGWT.rb.getString("chooseAVariableOption");
			if ((this.startVar2Box.getItemCount() == 0) 
				|| (!firstItem.equals(this.startVar2Box.getItemText(0))))
			{
				this.startVar2Box.addItem(firstItem);
			}
    
			boolean exists;
			String columnName;
    		// Check the variable names in model.getStatTableModel()
//			for (String varName : this.model.getStatTableModel().getColumnNames())
			for (int j = 0; j < this.model.getStatTableModel().getColumnNames().size(); j++)
			{
				ArrayList<String> list = this.model.getStatTableModel().getColumnNames();
				columnName = list.get(j);
				exists = false;
				for (int i = 0; i < this.startVar2Box.getItemCount() && !exists; i++)
				{
					if (columnName.equals(this.startVar2Box.getItemText(i)))
					{
						exists = true;
					}
				}
				if (!exists)
				{
					// startVarBox heeft een eerste item 'Kies een variabele', dus j + 1
					this.startVar2Box.insertItem(columnName, j + 1);
				}
			}
			
			// Check if items from startVarBox need to be removed
			for (int i = 1; i < this.startVar2Box.getItemCount(); i++)
			{
				exists = false;
				ArrayList<String> list = this.model.getStatTableModel().getColumnNames();
				for (String varName : list)
				{
					if (varName.equals(this.startVar2Box.getItemText(i)))
					{
						exists = true;
						break;
					}
				}
				if (!exists)
				{
					this.startVar2Box.removeItem(i);
				}					
			}
			
			this.startVar2BoxHandlerRegistration = this.startVar2Box.addChangeHandler(controller);
		}
	}

	/**
	 * class that handles tabs being dragged out of the TabPanel
	 * 
	 * @author Manu Drijvers
	 * 
	 */
	//public class DraggedTabListener implements EventListener
	public class DraggedTabTouchHandler implements ClickHandler, TouchStartHandler, TouchEndHandler
	{
		private Point startPoint;
		private Point currentPoint; // test syl
		private boolean inDrag;
		private int draggedTab;
		private Image draggedImage;

		private TabLayoutPanel tabPanel;

		/**
		 * Constructor
		 * 
		 * @param tabPanel2
		 *            the DraggedTabListener will look for tabs being dragged
		 *            out of this JTabbedPane
		 */
		public DraggedTabTouchHandler(TabLayoutPanel tabPanel2)
		{
			this.tabPanel = tabPanel2;
			
			// initialize with an invalid value
			this.draggedTab = -1;
			
			// test
			//java.net.URL imageURL = StatistiekGWT.class.getResource("resources/arrow-137-16_525252up.gif");
			//this.draggedImage = new Image(imageURL);
		}

		@Override
		public void onTouchStart(TouchStartEvent event)
		{
			GWT.log("StatInteractiePanelView.DraggedTabTouchHandler.onTouchStart()!");
			
			// uit mousePressed()... :
			int tab = this.tabPanel.getSelectedIndex();
			//GWT.log("StatInteractiePanelView.DraggedTabTouchHandler.onTouchStart(): tab = " + tab);
			if (tab < 0)
			{
				return;
			}
			
			Touch touch = event.getTouches().get(0);
			int eventX = touch.getPageX();
			int eventY = touch.getPageY();				

//			if (event.getSource() == MouseEvent.BUTTON1)
//			{
//				this.inDrag = true;
//				this.startPoint = new Point(eventX, eventY);
//				this.draggedTab = tab;
//				if (this.draggedTab >= 0
//					&& this.draggedTab < StatInteractiePanelView.this.model
//						.getMainWindowViews().size())
//				{
//					// test syl: move cursor blijft
////					StatInteractiePanelView.super.setCursor(new Cursor(
////						Cursor.MOVE_CURSOR));
//				}
//			}
			// 'middle click'-optie
//			else if (event.getSource() == MouseEvent.BUTTON2
//				&& StatInteractiePanelView.this.model.getStatTableModel()
//					.isViewsAddable()
//				&& tab >= 0
//				&& (tab < StatInteractiePanelView.this.tabPanel.getWidgetCount() - 1))
//			{
//				StatInteractiePanelView.this.model
//					.removeView(StatInteractiePanelView.this.model
//						.mainWindowIndexToGeneralIndex(tab));
//			}
			// 'rechtermuisknop'-optie
//			else if (event.getSource() == MouseEvent.BUTTON3
//				&& StatInteractiePanelView.this.model.getStatTableModel()
//					.isViewsEditable())
//			{
//				Widget c = StatistiekGWT.getTopLevelAncestor(StatInteractiePanelView.this);
//				if (c instanceof Frame)
//				{
//					ChangeViewNameDialog dialog = new ChangeViewNameDialog(
//						(Frame) c, StatInteractiePanelView.this.model, tab,
//						StatInteractiePanelView.this,
//						StatInteractiePanelView.this.getLocationOnScreen());
//					dialog.setVisible(true);
//				}
//				else if (c instanceof DialogBox)
//				{
//					ChangeViewNameDialog dialog = new ChangeViewNameDialog(
//						(Dialog) c, StatInteractiePanelView.this.model, tab,
//						StatInteractiePanelView.this,
//						StatInteractiePanelView.this.getLocationOnScreen());
//					dialog.setVisible(true);
//				}
//			}
		}

		@Override
		public void onTouchEnd(TouchEndEvent event)
		{
			GWT.log("StatInteractiePanelView.DraggedTabTouchHandler.onTouchEnd()!");
			
			// uit mouseReleased()... :
			Touch touch = event.getTouches().get(0);
			int eventX = touch.getPageX();
			int eventY = touch.getPageY();
			Point point = new Point(eventX, eventY);

			this.inDrag = false;
			//StatInteractiePanelView.super.setCursor(Cursor.DEFAULT);
//			GWT.log("Drag finished, dragged from " + this.startPoint
//				+ " to " + arg0.getPoint());
			if (this.draggedTab >= 0
				&& this.draggedTab < StatInteractiePanelView.this.model
					.getMainWindowViews().size())
			{
//				 GWT.log("This dragged tab: " + this.draggedTab);

				// Test if tab is dragged outside of tabPane
				if (this.isDraggedOutside(point))
				{
					// tab is dragged outside of tabPane
					int viewIndex = StatInteractiePanelView.this.model
						.mainWindowIndexToGeneralIndex(this.draggedTab);
					StatInteractiePanelView.this.showViewInDialog(
						StatInteractiePanelView.this.model.getViews().get(
							viewIndex), point);

					StatInteractiePanelView.this.model.setViewSeparateWindow(
						StatInteractiePanelView.this.model
							.mainWindowIndexToGeneralIndex(this.draggedTab),
						true);
				}
				else
				{
					// tab is not dragged but selected
					// set the selected tab
					StatInteractiePanelView.this.controller
						.setSelectedTab(this.draggedTab);

//					 GWT.log("mouseReleased(): setSelectedTab(draggedTab="
//					 + this.draggedTab + ")");
				}
			}
		}

		private boolean isDraggedOutside(Point point)
		{
			boolean isDraggedOutside = false;
			
			if ((point.getX() < this.tabPanel.getAbsoluteLeft())
				|| (point.getY() < this.tabPanel.getAbsoluteTop())
				|| (point.getX() > this.tabPanel.getAbsoluteLeft() + this.tabPanel.getOffsetWidth())
				|| (point.getY() > this.tabPanel.getAbsoluteTop() + this.tabPanel.getOffsetHeight()))
			{
				isDraggedOutside = true;
			}
			
			return isDraggedOutside;
		}

		@Override
		public void onClick(ClickEvent event)
		{
			// TODO Auto-generated method stub
			
		}
	}

	/**
	 * A Dialog for showing a single StatistiekView
	 * 
	 * @author Manu Drijvers, Sylvia van Borkulo
	 * 
	 */
	public class SeparateViewDialog extends DialogBox //implements WindowListener
	{
		private StatistiekView sv;

		/**
		 * Constructor for Frame owner
		 * 
		 * @param sv
		 *            The statistiekview that will be shown
		 * @param owner
		 *            The owner
		 */
		public SeparateViewDialog(StatistiekView sv, Frame owner)
		{
			//super(owner, sv.getViewName(), false);
			this.sv = sv;
			this.setUp();
		}

		/**
		 * Constructor for Dialog owner
		 * 
		 * @param sv
		 *            The statistiekview that will be shown
		 * @param owner
		 *            The owner
		 */
		public SeparateViewDialog(StatistiekView sv, DialogBox owner)
		{
			//super(owner, sv.getViewName(), false);
			this.sv = sv;
			this.setUp();
		}

		private void setUp()
		{
			super.setWidget(sv.getWidget());
//			super.setSize(StatInteractiePanelView.this.getSize());
//			super.addHandler(this, MouseMoveEvent.getType());
		}

//		public void windowActivated(WindowEvent arg0)
//		{
//			// Deze methode wordt aangeroepen als een window wordt aangeklikt,
//			// en dus ook als een window wordt gesloten.
//
//			// GWT.log("StatInteractiePanelView.SeparateViewDialog.windowActivated()");
//			// GWT.log("... selectedView = " + selectedView);
//			// GWT.log("... indexOf(sv) = " +
//			// StatInteractiePanelView.this.getModel().getViews().indexOf(sv));
//
//			int view = StatInteractiePanelView.this.getModel().getViews()
//				.indexOf(sv);
//			StatInteractiePanelView.this.processSelectedView(view);
//		}

		public void onWindowClosing(ClosingEvent arg0)
		{
			// GWT.log("windowClosing(): previousSelectedView="
			// + StatInteractiePanelView.this.previousSelectedView +
			// ", selectedView=" + selectedView);

			// oldSelectedTab is de oude selectedIndex van tabPane.
			int oldSelectedTab = tabPanel.getSelectedIndex();

			int newSelectedTab;

			// GWT.log("VOOR setViewSeparateWindow..... tabPane.getSelectedIndex()="
			// + tabPane.getSelectedIndex());

			// zet de view terug in tabPane; hierna is tabPane.selectedIndex 0
			StatInteractiePanelView.this.model.setViewSeparateWindowByObject(
				this.sv, false);
			// GWT.log("NA setViewSeparateWindow..... tabPane.getSelectedIndex()="
			// + tabPane.getSelectedIndex());

			// Als er een view wordt teruggezet vòòr de oude selectedTab,
			// dan wordt de nieuwe selectedTab 1 hoger
			if (selectedView <= oldSelectedTab)
				newSelectedTab = oldSelectedTab + 1;
			else
				newSelectedTab = oldSelectedTab;
			StatInteractiePanelView.this.processSelectedTab(newSelectedTab);

			// GWT.log("windowClosing(): tabPane.setSelectedIndex(selectedTab="
			// + selectedTab + ")");
			// dit geeft problemen, omdat selectedView niet is gezet
			// tabPane.setSelectedIndex(selectedTab);
		}
	}

	public ListBox getViewsBox()
	{
		return this.viewsBox;
	}
	
	public String getViewsBoxString()
	{
		int selectedIndex = this.viewsBox.getSelectedIndex();
		return this.viewsBox.getItemText(selectedIndex);
	}

	public int getStartVarBoxSelectedIndex()
	{
		return this.startVarBox.getSelectedIndex();
	}
	
	public int getStartVar2BoxSelectedIndex()
	{
		return this.startVar2Box.getSelectedIndex();
	}

	@Override
	public void onTableChange(TableChangeEvent event)
	{
		GWT.log("StatInteractiePanelView.onTableChange()");
		this.update();
	}
}
