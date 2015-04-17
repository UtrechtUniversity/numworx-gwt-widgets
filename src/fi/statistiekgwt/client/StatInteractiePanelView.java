package fi.statistiekgwt.client;

import java.util.ArrayList;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.DialogBox.Caption;

import fi.statistiekgwt.client.event.AddViewEvent;
import fi.statistiekgwt.client.event.AddViewEventHandler;
import fi.statistiekgwt.client.event.TableChangeEvent;
import fi.statistiekgwt.client.event.TableChangeEventHandler;

/**
 * Statistiek InteractiePanel MVC View
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class StatInteractiePanelView extends LayoutPanel 
	implements TableChangeEventHandler, AddViewEventHandler//TabLayoutPanel// implements Observer
{
	private static final String RESET_ICON_PATH = "resources/reseticon.gif";
	protected StatModel model;
	private StatInteractiePanel controller;
	
	private static Label NO_VIEWS_LABEL = new Label("No views added.");
	private ScrollableTabLayoutPanel tabPanel;
	private FlowPanel addViewTab;
	private ListBox viewsBox, startVarBox, startVar2Box;
	
	private HandlerRegistration viewsBoxHandlerRegistration;
	private HandlerRegistration startVarBoxHandlerRegistration;
	private HandlerRegistration startVar2BoxHandlerRegistration;
	
	protected long taptime;
	
	private Label addViewLabel;
	private Label chooseStartVarLabel, chooseStartVar2Label;
	
	private PopupPanel changeViewNamePopupMenu;
	private MenuBar menuBar;
	private MenuItem changeNameItem;
	private Command changeNameCommand;

	// button to reset the data -> button now implemented in StatTable for
	// layout reasons
	// keep the code, because StatInteractiePanelView is a more logical place
	// private JButton resetButton;

	// The index of the selected view
	private int selectedView;

	// The index of the previous selected view
	private int previousSelectedView = 0;

	// The index of the selected tab in tabPane
	private int selectedTab = 0;

	private ArrayList<SeparateViewDialog> separateViews;//dialogs;
	
	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;

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
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();
		
		this.initModel(model);
		
		this.initPopupMenu();

		this.controller = controller;
		
		this.initTabPanel(barHeight, barUnit);

		this.separateViews = new ArrayList<SeparateViewDialog>();

		this.addViewTab = new FlowPanel();

		this.addViewLabel = new Label(StatistiekGWT.rb.getString("addaviewKnopTekst"));
		this.addViewLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.chooseStartVarLabel = new Label(StatistiekGWT.rb.getString("chooseStartVarLabel"));
		this.chooseStartVarLabel.setVisible(false);
		this.chooseStartVarLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.chooseStartVar2Label = new Label(StatistiekGWT.rb.getString("chooseStartVarColumnLabel"));
		this.chooseStartVar2Label.setVisible(false);
		this.chooseStartVar2Label.addStyleName(statistiekCss.spaceTopLabel());

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

		addViewTab.add(this.addViewLabel);
		addViewTab.add(this.viewsBox);
		addViewTab.add(this.chooseStartVarLabel);
		addViewTab.add(this.startVarBox);
		addViewTab.add(this.chooseStartVar2Label);
		addViewTab.add(this.startVar2Box);
		
		this.update();
	}

	/**
	 * Initialize the popup menu for changing a view's name.
	 */
	private void initPopupMenu()
	{
		// create vertical menubar
		this.menuBar = new MenuBar(true);
		this.changeViewNamePopupMenu = new PopupPanel(true, true);
		this.changeViewNamePopupMenu.add(this.menuBar);

		this.changeViewNamePopupMenu.setVisible(false);
		this.changeViewNamePopupMenu.hide();
		
		this.createChangeNameCommand();
		this.changeNameItem = new MenuItem(StatistiekGWT.rb.getString("changeViewName"), true, changeNameCommand);
		this.menuBar.addItem(changeNameItem);
	}

	/**
	 * Create the column info command for the column's menu bar.
	 */
	private void createChangeNameCommand()
	{
        this.changeNameCommand = new Command() {
	        @Override
            public void execute() 
	        {
	        	hideTabPopupMenu();
	        	
				ChangeViewNameDialog dialogView;

				dialogView = new ChangeViewNameDialog(model, getSelectedViewName(), StatInteractiePanelView.this);
				
				dialogView.center();
				dialogView.show();
            }
        };
	}
	
	/**
	 * Get the view name of the selected tab.
	 * @return View name
	 */
	protected String getSelectedViewName()
	{
		int selectedTabIndex = this.tabPanel.getSelectedIndex(); 
		String name = this.getViews().get(selectedTabIndex).getViewName();
		
		return name;
	}

	/**
	 * Get the index of the selected tab.
	 * @return Index
	 */
	protected int getSelectedTabIndex()
	{
		int selectedTabIndex = this.tabPanel.getSelectedIndex(); 
		
		return selectedTabIndex;
	}

	/**
	 * Hide the view tab's popup menu.
	 */
	protected void hideTabPopupMenu()
	{
    	this.changeViewNamePopupMenu.setVisible(false);
    	this.changeViewNamePopupMenu.hide();
	}
	
	/**
	 * Show the view's popup menu.
	 * 
	 * @param x
	 * @param y
	 */
	protected void showTabPopupMenu(int x, int y)
	{
		this.changeViewNamePopupMenu.setPopupPosition(x, y);
    	this.changeViewNamePopupMenu.setVisible(true);
    	this.changeViewNamePopupMenu.show();
	}



	/**
	 * Set the model and add event handlers.
	 * 
	 * @param model
	 */
	private void initModel(StatModel model)
	{
		this.model = model;
		this.model.getStatTableModel().addTableChangeEventHandler(this);//addObserver(this);
		this.model.addAddViewEventHandler(this);
	}

	/**
	 * Create tabpanel and add selection handler
	 * 
	 * @param barHeight
	 * @param barUnit
	 */
	private void initTabPanel(double barHeight, Unit barUnit)
	{
		//super(barHeight, barUnit); // komt van TabLayoutPanel
		this.tabPanel = //new TabLayoutPanel(barHeight, barUnit); // als er teveel tabs zijn, vallen ze buiten beeld
//			new ScrolledTabLayoutPanel(barHeight, barUnit, 
//				this.statistiekGWTClientBundle.scrollArrowLeftResource(), 
//				this.statistiekGWTClientBundle.scrollArrowRightResource()); // met scrollbar als er teveel tabs zijn; lelijk, buttons links
			new ScrollableTabLayoutPanel(barHeight, barUnit, 
				this.controller.getWidth(), controller.getHeight()); // met scrollbar als er teveel tabs zijn; alternatief 

		this.tabPanel.addSelectionHandler(new SelectionHandler<Integer>()
		{
			// https://code.google.com/p/google-web-toolkit/issues/detail?id=6889
			// datagrid wordt niet volledig getoond zonder panel.forceLayout()
			@Override
			public void onSelection(SelectionEvent<Integer> event)
			{
				Integer selectedItem = event.getSelectedItem();
				TabLayoutPanel panel = (TabLayoutPanel) event.getSource();
				panel.forceLayout();

				setSelectedView(selectedItem);
				setSelectedTab(selectedItem);
				
				updateViewIfNecessary(selectedItem);

			}
		});
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
			setTabPane(selectedTab);
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
			setSelectedTab(tab);//view);

			// set previous selected view
			if (view != selectedView)
			{
				setPreviousSelectedView(selectedView);
			}
			// else
			// GWT.log("tab = selectedView!");

			setSelectedView(view);

			//this.tabPanel.selectTab(tab); // gebeurt in setSelectedTab()
			// GWT.log("StatInteractiePanelView.processSelectedTab(): tabPane.setSelectedIndex(tab="
			// + tab + ")");
		}
		// GWT.log("...end.... StatInteractiePanelView.processSelectedTab");
	}

	/**
	 * Set tab index selected in tabPanel and
	 * set the field selectedTab to keep track of the visible
 	 * tab in tabPane in case of views in own window.
	 * 
	 * @param index
	 */
	public void setSelectedTab(int index)
	{
		selectedTab = index;
		this.tabPanel.selectTab(index);
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
	 * Tests whether there is a dialogbox showing the statistiekview "sv"
	 * 
	 * @param sv
	 *            The StatistiekView of which we want to know whether its
	 *            currently being displayed in a dialogbox
	 * @return true if there is a dialog showing sv
	 */
	private boolean dialogExists(StatistiekView sv)
	{
		for (SeparateViewDialog dialog : this.separateViews)
		{
			if (sv.equals(dialog.statistiekView))
			{
				return true;
			}
		}

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
	public void showViewInDialog(StatistiekView sv, int x, int y)
	{
		CaptionImpl caption = new CaptionImpl();
		SeparateViewDialog dialog = new SeparateViewDialog(sv, false, false, caption);
		// set caption's dialog box and view to be able to handle the closing action
		caption.setDialogBox(dialog);
		caption.setView(sv);
		
		DragOnTouch t = new DragOnTouch(dialog);
		dialog.addDomHandler(t, TouchStartEvent.getType());
		dialog.addDomHandler(t, TouchMoveEvent.getType());
		dialog.addDomHandler(t, TouchEndEvent.getType());
		dialog.addDomHandler(t, TouchCancelEvent.getType());
		
		this.separateViews.add(dialog);
		dialog.setPopupPosition(x, y);
		dialog.setVisible(true);
		//dialog.center();
		dialog.show();
	}
	
	/**
	 * Process the closing of a separate view.
	 * 
	 * @param statistiekView The separate view that has been closed
	 */
	private void processCloseSeparateView(StatistiekView statistiekView)
	{
		// oldSelectedTab is de oude selectedIndex van tabPane.
		int oldSelectedTab = tabPanel.getSelectedIndex();

		int newSelectedTab;

		// GWT.log("VOOR setViewSeparateWindow..... tabPane.getSelectedIndex()="
		// + tabPane.getSelectedIndex());

		// zet de view terug in tabPane; hierna is tabPane.selectedIndex 0
		StatInteractiePanelView.this.model.setViewSeparateWindowByObject(
			statistiekView, false);
		// GWT.log("NA setViewSeparateWindow..... tabPane.getSelectedIndex()="
		// + tabPane.getSelectedIndex());

		// Als er een view wordt teruggezet vòòr de oude selectedTab,
		// dan wordt de nieuwe selectedTab 1 hoger
		// test syl: let op: als je een tab selecteert en daarna een extern view sluit, dan is selectedView niet de te sluiten externe view maar de selectedTab!
		// hier moet: de index van statistiekView
//		if (selectedView <= oldSelectedTab)
		if (this.getIndexOfViewName(statistiekView.getViewName()) <= oldSelectedTab)
			newSelectedTab = oldSelectedTab + 1;
		else
			newSelectedTab = oldSelectedTab;
		
		StatInteractiePanelView.this.processSelectedTab(newSelectedTab);

		// dit geeft problemen, omdat selectedView niet is gezet
		// tabPane.setSelectedIndex(selectedTab);
		
		this.update();
	}

	/**
	 * Select the last real tab (not the add view tab) in the tabPane
	 */
	public void selectLastTab()
	{
		int lastTabIndex = this.tabPanel.getWidgetCount() - 2;
		this.setSelectedView(lastTabIndex);
		this.setSelectedTab(lastTabIndex);
	}
	
	/**
	 * Get views.
	 */
	private ArrayList<StatistiekView> getViews()
	{
		ArrayList<StatistiekView> views = this.model.getMainWindowViews();
		return views;
	}
	
	/**
	 * Update the view if it is a table view and it is selected .
	 * 
	 * @param viewName
	 */
	public void updateViewIfNecessary(String viewName)
	{
		// tabel-views moeten geupdate worden anders toont datagrid geen inhoud in de tab ((datagrid) table.redraw() is noodzakelijk)
		for (int i = 0; i < model.getViews().size(); i++)
		{
			StatistiekView view = model.getViews().get(i);
			if (view.getViewName().equals(viewName) && view.getViewType().equals(StatistiekGWT.VIEWS[0]) // tabel view
				&& this.isSelected(view))
			{
				view.update();
			}
		}
	}
	
	/**
	 * Update the view with the given index if it is a table view and it is selected .
	 * 
	 * @param viewName
	 */
	public void updateViewIfNecessary(int index)
	{
		// tabel-views moeten geupdate worden anders toont datagrid geen inhoud in de tab ((datagrid) table.redraw() is noodzakelijk)
		for (int i = 0; i < model.getViews().size(); i++)
		{
			StatistiekView view = model.getViews().get(i);
			if ((i == index) && view.getViewType().equals(StatistiekGWT.VIEWS[0]) // tabel view
				&& this.isSelected(view))
			{
				view.update();
			}
		}
	}
	
	/**
	 * Returns true if the given view is the selected view in tabPanel.
	 * 
	 * @param view
	 * @return
	 */
	private boolean isSelected(StatistiekView view)
	{
		boolean isSelected;
		
		if (tabPanel.getWidget(tabPanel.getSelectedIndex()).equals(view.getWidget()))
		{
			isSelected = true;
		}
		else
		{
			isSelected = false;
		}
		
		return isSelected;
	}

	/**
	 * Update the views and start var boxes.
	 */
	public void update()
	{
		GWT.log("StatInteractiePanelView.update()");
		
		// onderstaande loop verwijdert alleen de views en niet de +-tab...
		for (int i = super.getWidgetCount() - 1; i > 0; i--)
		{
			super.remove(i);
		}
		
		this.removeAllTabs();

		ArrayList<StatistiekView> mainWindowViews = this.model.getMainWindowViews();// this.getViews()
		ArrayList<StatistiekView> separateWindowViews = this.model.getSeparateWindowViews();

		int amountOfTabs = mainWindowViews.size();
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
		else if (mainWindowViews.size() == 1 && !this.model.getStatTableModel().isViewsAddable())
		{
			// GWT.log("one view added");
			super.add(mainWindowViews.get(0).getWidget());
		}
		else
		{
			// GWT.log(">1 views added");
			if (this.model.getStatTableModel().isViewsAddable())
			{
				for (int i = 0; i < mainWindowViews.size(); i++)
				{
					StatistiekView view = mainWindowViews.get(i);
					this.tabPanel.add(view.getWidget(), this.getTabTitle(view.getWidget(), view.getViewName()));
				}
			}
			else
			{
				for (StatistiekView view : mainWindowViews)
				{
					this.tabPanel.add(view.getWidget(), this.getTabTitleNonEditable(view.getWidget(), view.getViewName()));//view.getViewName());
				}
			}

			if (this.model.getStatTableModel().isViewsAddable())
			{
				this.tabPanel.add(this.addViewTab, "+");
			}

			this.tabPanel.addStyleName(statistiekCss.backgroundblue());//getElement().getStyle().setBackgroundColor("Azure");
			this.tabPanel.removeFromParent();
			super.add(this.tabPanel);
		}

		// remove dialogs that are no longer needed
		for (int i = 0; i < this.separateViews.size(); i++)
		{
			SeparateViewDialog dialog = this.separateViews.get(i);
			if (!separateWindowViews.contains(dialog.statistiekView))
			{
				dialog.setVisible(false);
				this.separateViews.remove(dialog);
				i--;
			}
		}

		// add new dialogs
		for (StatistiekView sv : separateWindowViews)
		{
			if (!this.dialogExists(sv))
			{
				// wanneer gebeurt dit...?
				this.showViewInDialog(sv, super.getAbsoluteLeft() + (int) (0.5 * super.getOffsetWidth()), super.getAbsoluteTop()
					+ (int) (0.5 * super.getOffsetHeight()));
			}
		}

		// update the selected tab in tabPanel
		tabPanel.selectTab(selectedTab);

		// Fill boxes with variable names
		updateStartVarBox();
		updateStartVar2Box();
	}

	/*
	 * Update the startVarBox with the variable names.
	 */
	private void updateStartVarBox()
	{
//		GWT.log("StatInteractiePanelView.updateStartVarBox()");

		// Alleen updaten als er kolomnamen zijn 
		if (this.model.getStatTableModel().getColumnNames().size() > 0)
		{
			// test syl: moet dit zonder handler?
			this.startVarBoxHandlerRegistration.removeHandler();
			
			// remove all items
			for (int i = this.startVarBox.getItemCount() - 1; i > -1; i--)
			{
				this.startVarBox.removeItem(i);
			}
			
			// add first item
			String firstItem = StatistiekGWT.rb.getString("chooseAVariableOption");
			this.startVarBox.addItem(firstItem);
			
			// add column names
			ArrayList<String> list = this.model.getStatTableModel().getColumnNames();
			
			for (String varName : list)
			{
				this.startVarBox.addItem(varName);
			}
			
			this.startVarBoxHandlerRegistration = this.startVarBox.addChangeHandler(controller);
		} // if there are column names
	}

	/*
	 * Update the startVar2Box with the variable names.
	 */
	private void updateStartVar2Box()
	{
//		GWT.log("StatInteractiePanelView.updateStartVar2Box()");

		// Alleen updaten als er kolomnamen zijn 
		if (this.model.getStatTableModel().getColumnNames().size() > 0)
		{
			// test syl: moet dit zonder handler?
			this.startVar2BoxHandlerRegistration.removeHandler();
			
			// remove all items
			for (int i = this.startVar2Box.getItemCount() - 1; i > -1; i--)
			{
				this.startVar2Box.removeItem(i);
			}
			
			// add first item
			String firstItem = StatistiekGWT.rb.getString("chooseAVariableOption");
			this.startVar2Box.addItem(firstItem);
			
			// add column names
			ArrayList<String> list = this.model.getStatTableModel().getColumnNames();
			
			for (String varName : list)
			{
				this.startVar2Box.addItem(varName);
			}

			this.startVar2BoxHandlerRegistration = this.startVar2Box.addChangeHandler(controller);
		}
	}
	
	/**
	 * Class that handles right mouse click on tab title label.
	 * 
	 * @author borku102
	 *
	 */
	public class LabelClickHandler implements ContextMenuHandler, ClickHandler
	{
		private Widget widget;
		private String viewName;
		
		public LabelClickHandler(Widget widget, String viewName)
		{
			super();
			this.widget = widget;
			this.viewName = viewName;
		}

		@Override
		public void onClick(ClickEvent event)
		{
			int viewIndex = getIndexOfViewName(viewName);
			setSelectedView(viewIndex);
			setSelectedTab(viewIndex);
			
			updateViewIfNecessary(this.viewName);
		}

		@Override
		public void onContextMenu(ContextMenuEvent event)
		{
			// select tab
			tabPanel.selectTab(widget);
			int viewIndex = getIndexOfViewName(viewName);
			setSelectedView(viewIndex);
			setSelectedTab(viewIndex);
			
			// tabel-views moeten geupdate worden anders toont datagrid geen inhoud in de tab ((datagrid) table.redraw() is noodzakelijk)
			for (int i = 0; i < model.getViews().size(); i++)
			{
				StatistiekView view = model.getViews().get(i);
				if (view.getViewName().equals(this.viewName) && view.getViewType().equals(StatistiekGWT.VIEWS[0])) // tabel view
				{
					view.update();
				}
			}
			
			if (StatInteractiePanelView.this.model.getStatTableModel().isViewsEditable())
			{
				int x = event.getNativeEvent().getClientX();
			    int y = event.getNativeEvent().getClientY();
				showTabPopupMenu(x, y);
			}
		    
			event.preventDefault(); 
	    }
		
	} // class LabelClickHandler

	/**
	 * Class that DragTabHandler handles views
	 * being dragged out of the tabPanel.
	 * 
	 * @author Sylvia van Borkulo
	 * 
	 */
	//public class DragTabListener implements EventListener
	public class DragTabHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler,  
		MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		private boolean inDrag;
		private Image dragImage;

		private Widget widget;
		private String viewName;
		private int x;
		private int y;
		private boolean hasMoved;
		
		boolean mouseDown = false;

		/**
		 * Constructor of DragTabHandler handles views (widget with viewName)
		 * being dragged out of the tabPanel.
		 * 
		 * @param widget
		 * @param viewName
		 */
		public DragTabHandler(Widget widget, String viewName)
		{
			super();

			this.widget = widget;
			this.viewName = viewName;
		}

		@Override
		public void onTouchStart(TouchStartEvent event)
		{
			GWT.log("StatInteractiePanelView.DraggedTabHandler.onTouchStart()!");
			
			// --------------------->>>>>>>>>>>>>>>>>>>>>
			// uit TouchHandler --->
			event.preventDefault();
			event.stopPropagation();
			
			// set tap time to detect tap & hold
			taptime = System.currentTimeMillis();
			// uit TouchHandler <---
			// <<<<<<<<<<<<<<<<<<<<--------------------
			
			
			// uit mousePressed()... :
			int tab = tabPanel.getSelectedIndex();
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
		public void onTouchMove(TouchMoveEvent event)
		{
			GWT.log("StatInteractiePanelView.DragTabHandler.onTouchMove()!");
			
			event.preventDefault();
			event.stopPropagation();
			
			if (event.getTouches().length() > 0)
			{
				Touch touch = event.getTouches().get(0);
				
				this.hasMoved = true;
				
//			    int eventX = touch.getPageX() - algebraPijlenGWTCanvas.getAbsoluteLeft();
//				int eventY = touch.getPageY() - algebraPijlenGWTCanvas.getAbsoluteTop();				
//			    
//				lastMoveX = eventX; 
//				lastMoveY = eventY;
//				
//				asv.mouseMoveTouchMoveAction(eventX, eventY);
				
				// toon gesleepte view...
				
		    }
			event.preventDefault();
			event.stopPropagation();
		}

		@Override
		public void onTouchEnd(TouchEndEvent event)
		{
			GWT.log("StatInteractiePanelView.DragTabHandler.onTouchEnd()!");
			
			// --------------------->>>>>>>>>>>>>>>>>>>
			// uit TouchHandler --->
			// select tab; dit moet als tab niet uit gesleept, maar geselecteerd...!
//			tabPanel.selectTab(widget);
//			int viewIndex = getIndexOfViewName(viewName);
//			setSelectedView(viewIndex);
//			setSelectedViewInTabPane(viewIndex);
//			
//			// tabel-views moeten geupdate worden anders toont datagrid geen inhoud in de tab ((datagrid) table.redraw() is noodzakelijk)
//			for (int i = 0; i < model.getViews().size(); i++)
//			{
//				StatistiekView view = model.getViews().get(i);
//				if (view.getViewName().equals(this.viewName) && view.getViewType().equals(StatistiekGWT.VIEWS[0])) // tabel view
//				{
//					view.update();
//				}
//			}
//			
//			if (!this.hasMoved 
//				&& isLongClick() && StatInteractiePanelView.this.model.getStatTableModel().isViewsEditable())
//			{
//				int x = event.getNativeEvent().getClientX();
//			    int y = event.getNativeEvent().getClientY();
//				showTabPopupMenu(x, y);
//			}
			// uit <--- TouchHandler
			// <<<<<<<<<<<<<<<<<<<<<<---------------------
			
						
			
			// uit mouseReleased()... :
			Touch touch = event.getTouches().get(0);
			int eventX = touch.getPageX();
			int eventY = touch.getPageY();

			this.inDrag = false;
			//StatInteractiePanelView.super.setCursor(Cursor.DEFAULT);
//			GWT.log("Drag finished, dragged from " + this.startPoint
//				+ " to " + arg0.getPoint());
//			if (this.dragTabIndex >= 0
//				&& this.dragTabIndex < StatInteractiePanelView.this.model
//					.getMainWindowViews().size())
//			{
//				 GWT.log("This dragged tab: " + this.dragTab);

				// Test if tab is dragged outside of tabPane
				if (this.isDraggedOutside(eventX, eventY))
				{
					this.setDraggedOutside(eventX, eventY);
				}
				else
				{
					// tab is not dragged but selected
					// set the selected tab
					this.setSelected();
//					 GWT.log("mouseReleased(): setSelectedTab(draggedTab="
//					 + this.draggedTab + ")");
				}
//			}
		}

		private boolean isDraggedOutside(int x, int y)
		{
			boolean isDraggedOutside;
			
			// check is the cursor is dragged outside of the label
			if ((x < tabPanel.getAbsoluteLeft() - 20)
				|| (y < tabPanel.getAbsoluteTop() - 20)
				|| (x > tabPanel.getAbsoluteLeft() + tabPanel.getOffsetWidth())
				|| (y > tabPanel.getAbsoluteTop() + tabPanel.getOffsetHeight()))
			{
				isDraggedOutside = true;
			}
			else
			{
				isDraggedOutside = false;
			}
			
			return isDraggedOutside;
		}

		@Override
		public void onMouseDown(MouseDownEvent event)
		{
			// prevent scrolling 
			event.stopPropagation();
			
			x = event.getClientX();
			y = event.getClientY();
			
			mouseDown = true;
			this.setSelected();
			
			// test syl
			//System.out.println("StatInteractiePanelView.DragTabHandler.onMouseDown(): mouseDown = true");
		}

		@Override
		public void onMouseMove(MouseMoveEvent event)
		{
			// prevent scrolling
			event.stopPropagation();
			
			if (!mouseDown)
				return;
			
			// test syl: capturing the mouse to the dragged widget
			//DOM.setCapture(getElement());
			
			int movedX = event.getClientX();
			int movedY = event.getClientY();
			
			if (event.getSource() instanceof Label)
			{
				Label label = (Label) event.getSource();
				int w = label.getOffsetWidth();
				int h = label.getOffsetHeight();
				int top = label.getAbsoluteTop();
				int bottom = top + h;
				int left = label.getAbsoluteLeft();
				int right = left + w;

				//System.out.println("StatInteractiePanelView.DragTabHandler.onMouseMove(): instanceof Label, (" + movedX + ", " + movedY + ")");

				// test syl: alternatief: bij iedere mouseMove drag outside
				this.setSelected();
				this.setDraggedOutside(movedX, movedY);
				mouseDown = false;
				// test syl
				//System.out.println("StatInteractiePanelView.DragTabHandler.onMouseMove(): outside! mouseDown = false");

//				if ((movedX == left) || (movedX == right)
//					|| (movedY == top) || (movedY == bottom))
//				{
//					this.setDraggedOutside(movedX, movedY);
//					mouseDown = false;
//					
//					// test syl
//					System.out.println("StatInteractiePanelView.DragTabHandler.onMouseMove(): outside! mouseDown = false");
//
//					// test syl
//					//DOM.releaseCapture(getElement());
//				}
//				else
//				{
//					this.setSelected();
//					//System.out.println("StatInteractiePanelView.DragTabHandler.onMouseMove(): NIET outside, setSelected()");
//				}
			}
			else
			{
				
			}
			
//			if (isDraggedOutside(movedX, movedY))
//			{
//				GWT.log("StatInteractiePanelView.DragTabHandler.onMouseMove(): outside!");
//				this.setDraggedOutside(movedX, movedY);
//				//this.setDraggedOutside(200, 200);
//				mouseDown = false;
//			}
//			else
//			{
//				this.setSelected();
//			}

		}

		private void setSelected()
		{
			// test syl: is onderstaande allemaal nodig?
			tabPanel.selectTab(widget);
			// test syl: let op: dit is geen tab index maar de index in views
			int viewIndex = getIndexOfViewName(viewName);
			int tabIndex = tabPanel.getWidgetIndex(widget);
//			setSelectedView(viewIndex);
//			setSelectedTab(viewIndex);
			
			// process the setting of selected tab
			//StatInteractiePanelView.this.controller.setSelectedTab(this.dragTabIndex);
//			processSelectedTab(viewIndex);
			processSelectedTab(tabIndex);

			updateViewIfNecessary(this.viewName);
			// tabel-views moeten geupdate worden anders toont datagrid geen inhoud in de tab ((datagrid) table.redraw() is noodzakelijk)
//			for (int i = 0; i < model.getViews().size(); i++)
//			{
//				StatistiekView view = model.getViews().get(i);
//				if (view.getViewName().equals(this.viewName) && view.getViewType().equals(StatistiekGWT.VIEWS[0])) // tabel view
//				{
//					view.update();
//				}
//			}
		}

		private void setDraggedOutside(int x, int y)
		{
			// tab is dragged outside of tabPane
			int viewIndex = getIndexOfViewName(viewName);
			int originalViewIndex = StatInteractiePanelView.this.model
				.mainWindowIndexToGeneralIndex(viewIndex);
			StatInteractiePanelView.this.showViewInDialog(
				StatInteractiePanelView.this.model.getViews().get(originalViewIndex), x, y);

			StatInteractiePanelView.this.model.setViewSeparateWindow(
				originalViewIndex,
				true);
			
			// set the dragged view selected
			setSelectedView(originalViewIndex);
			
			// update table view if that is the selected view in tabPanel
			String selectedTabViewName = "";
			if (tabPanel.getTabWidget(tabPanel.getSelectedIndex()) instanceof HorizontalPanel)
			{
				if (((HorizontalPanel) tabPanel.getTabWidget(tabPanel.getSelectedIndex())).getWidget(0) instanceof Label)
				{
					// get the tab text of the selected tab
					// test syl: moet dit met selectedTab??
					// The tab widget is a horizontal panel containing the tab label as widget 0
					selectedTabViewName = ((Label) ((HorizontalPanel) tabPanel.getTabWidget(tabPanel.getSelectedIndex())).getWidget(0)).getText();
				}
			}
			updateViewIfNecessary(selectedTabViewName);// niet this.viewName, maar de selectedTab
		}

		/**
		 * Let op: mouse up wordt niet getriggerd als de muis buiten 
		 * tab label is.
		 */
		@Override
		public void onMouseUp(MouseUpEvent event)
		{
			int movedX = event.getX();
			int movedY = event.getY();

			// prevent scrolling
			event.stopPropagation();
			
			mouseDown = false;
			// test syl
			//DOM.releaseCapture(getElement());
			
//			System.out.println("StatInteractiePanelView.DragTabHandler.onMouseUp()! mouseDown = false, movedX = "
//				+ movedX + ", movedY = " + movedY);
		}
	} //class DragTabHandler
	
	
	/**
	 * Class MouseHandler 
	 * 
	 * @author Sylvia van Borkulo
	 *
	 */
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{   
		boolean mouseDown = false;
		
		public void onMouseDown(MouseDownEvent e)
		{
			//e.preventDefault();
			
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true;
			
			//asv.mouseDownTouchStartAction(eventX, eventY);
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			//e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();

			//asv.mouseMoveTouchMoveAction(eventX, eventY);
		}
		
		public void onMouseUp(MouseUpEvent e)	
		{
			//e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;

			int eventX = e.getX();
			int eventY = e.getY();
			
			//asv.mouseUpTouchEndAction(eventX,eventY);
		}
	} // Class MouseHandler
	
	/**
	 * Class TouchHandler to implement tap & hold on tabs in tabPanel
	 * for changing a view's name.
	 * 
	 * @author Sylvia van Borkulo
	 *
	 */
	class TouchHandler implements TouchStartHandler, TouchEndHandler
	{
		private Widget widget;
		private String viewName;
		private int x;
		private int y;
		private boolean hasMoved;
		
		public TouchHandler(Widget widget, String viewName)
		{
			super();
			this.widget = widget;
			this.viewName = viewName;
		}

		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			// set tap time to detect tap & hold
			taptime = System.currentTimeMillis();
		}
		
		public void onTouchMove(TouchMoveEvent event)
		{
			event.preventDefault();
			event.stopPropagation();
			
			if (event.getTouches().length() > 0)
			{
				Touch touch = event.getTouches().get(0);
				
				this.hasMoved = true;
				
//			    int eventX = touch.getPageX() - algebraPijlenGWTCanvas.getAbsoluteLeft();
//				int eventY = touch.getPageY() - algebraPijlenGWTCanvas.getAbsoluteTop();				
//			    
//				lastMoveX = eventX; 
//				lastMoveY = eventY;
//				
//				asv.mouseMoveTouchMoveAction(eventX, eventY);
				
				// toon gesleepte view...
				
		    }
			event.preventDefault();
			event.stopPropagation();
			
		}

		public void onTouchEnd(TouchEndEvent event)
		{
			// select tab
			tabPanel.selectTab(widget);
			int viewIndex = getIndexOfViewName(viewName);
			setSelectedView(viewIndex);
			setSelectedTab(viewIndex);
			
			// tabel-views moeten geupdate worden anders toont datagrid geen inhoud in de tab ((datagrid) table.redraw() is noodzakelijk)
			for (int i = 0; i < model.getViews().size(); i++)
			{
				StatistiekView view = model.getViews().get(i);
				if (view.getViewName().equals(this.viewName) && view.getViewType().equals(StatistiekGWT.VIEWS[0])) // tabel view
				{
					view.update();
				}
			}
			
			if (!this.hasMoved 
				&& isLongClick() && StatInteractiePanelView.this.model.getStatTableModel().isViewsEditable())
			{
				int x = event.getNativeEvent().getClientX();
			    int y = event.getNativeEvent().getClientY();
				showTabPopupMenu(x, y);
			}

		}
	} // class TouchHandler
	
	/**
	 * CaptionImpl is a class for creating a caption with a closing cross.
	 * 
	 * @author Wim van Velthoven, Sylvia van Borkulo
	 *
	 */
	public class CaptionImpl extends Composite implements Caption
	{
		FlowPanel flow = new FlowPanel();
		HTML btn;
		private DialogBox.CaptionImpl label;
		private StatistiekView statistiekView;
		private DialogBox box;

		public CaptionImpl()
		{
			btn = new DialogBox.CaptionImpl();
			btn.setHTML("<span class='btn btn-danger'><i class='fa fa-times fa-lg'></i> &nbsp;</span>");
			btn.setText("><");
			Style btnStyle = btn.getElement().getStyle();
			btnStyle.setFloat(Style.Float.LEFT);
			btnStyle.setWidth(2, Unit.EM);
			label = new DialogBox.CaptionImpl();
			label.setText("Popup");
			flow.add(btn);
			flow.add(label);
			initWidget(flow);
			addMouseDownHandler(new MouseDownHandler()
			{

				@Override
				public void onMouseDown(MouseDownEvent event)
				{
					event.stopPropagation();
					event.preventDefault();
					tearDown();
				}
			});
			addTouchStartHandler(new TouchStartHandler()
			{

				@Override
				public void onTouchStart(TouchStartEvent event)
				{
					event.stopPropagation();
					event.preventDefault();
					tearDown();
				}

			});
		}
		
		public void setView(StatistiekView view)
		{
			this.statistiekView = view;
		}

		public void setDialogBox(DialogBox box)
		{
			this.box = box;
		}

		/**
		 * Called when a view in own window is closed.
		 */
		void tearDown()
		{
			box.hide();
			processCloseSeparateView(this.statistiekView);
		}

		@Override
		public HandlerRegistration addMouseDownHandler(MouseDownHandler handler)
		{
			return btn.addMouseDownHandler(handler);
		}

		public HandlerRegistration addTouchStartHandler(
			TouchStartHandler handler)
		{
			return btn.addDomHandler(handler, TouchStartEvent.getType());
		}

		@Override
		public HandlerRegistration addMouseUpHandler(MouseUpHandler handler)
		{
			return null;
		}

		@Override
		public HandlerRegistration addMouseOutHandler(MouseOutHandler handler)
		{
			return null;
		}

		@Override
		public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
		{
			return null;
		}

		@Override
		public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler)
		{
			return null;
		}

		@Override
		public HandlerRegistration addMouseWheelHandler(
			MouseWheelHandler handler)
		{
			return null;
		}

		@Override
		public String getHTML()
		{
			return label.getHTML();
		}

		@Override
		public void setHTML(String html)
		{
			label.setHTML(html);
		}

		@Override
		public String getText()
		{
			return label.getText();
		}

		@Override
		public void setText(String text)
		{
			label.setText(text);
		}

		@Override
		public void setHTML(SafeHtml html)
		{
			label.setHTML(html);
		}

	}
	
	/**
	 * Class for smooth dragging dialogbox with touch handlers.
	 * 
	 * @author Wim van Velthoven, Sylvia van Borkulo
	 */
	class DragOnTouch implements TouchStartHandler, TouchMoveHandler,
		TouchEndHandler, TouchCancelHandler,
		com.google.gwt.animation.client.AnimationScheduler.AnimationCallback
	{
		int x, y;
		boolean track;
		AnimationHandle handle;
		DialogBox box;
		
		public DragOnTouch(DialogBox box)
		{
			super();
			this.box = box;
		}

		@Override
		public void onTouchEnd(TouchEndEvent event)
		{
			track = false;
			if (handle != null)
				handle.cancel();
			handle = null;
			box.onMouseMove(box.getCaption().asWidget(), x, y);
			box.onMouseUp(box.getCaption().asWidget(), x, y);
			// logger.info("touch end " + x + "," + y);
			event.stopPropagation();
			event.preventDefault();
		}

		@Override
		public void onTouchMove(TouchMoveEvent event)
		{
			getXY(event);
			// box.onMouseMove(box.getCaption().asWidget(), x, y);
			// logger.info("touch move " + x + "," + y);
		}

		void getXY(TouchEvent<?> event)
		{
			x = event.getTouches().get(0).getRelativeX(box.getElement());
			y = event.getTouches().get(0).getRelativeY(box.getElement());
			event.stopPropagation();
			event.preventDefault();
		}

		@Override
		public void onTouchStart(TouchStartEvent event)
		{
			getXY(event);
			box.onMouseDown(box.getCaption().asWidget(), x, y);
			track = true;
			handle = AnimationScheduler.get().requestAnimationFrame(this,
				box.getElement());
			// logger.info("touch start " + x + "," + y);
		}

		@Override
		public void onTouchCancel(TouchCancelEvent event)
		{
			track = false;
			box.onMouseUp(box.getCaption().asWidget(), x, y);
		}

		@Override
		public void execute(double timestamp)
		{
			if (track)
			{
				box.onMouseMove(box.getCaption().asWidget(), x, y);
				handle = AnimationScheduler.get().requestAnimationFrame(this,
					box.getElement());
			}
		}

	} // class DragOnTouch

	/**
	 * A Dialog for showing a single StatistiekView
	 * TODO Deze hebben we niet nodig? 
	 * 
	 * @author Manu Drijvers, Sylvia van Borkulo
	 * 
	 */
	public class SeparateViewDialog extends DialogBox //implements WindowListener
	{
		private StatistiekView statistiekView;
//		HTML close = new HTML("[X]");
//		HTML title;
//		HorizontalPanel captionPanel = new HorizontalPanel();
		
		StatistiekGWTClientBundle statistiekGWTClientBundle;
		StatistiekCssResource statistiekCss;

		/**
		 * Constructor for SeparateViewDialog
		 * 
		 * @param sv
		 *		The statistiekview that will be shown
		 * @param autoHide true if the dialog should be automatically hidden when the user clicks 
		 * 		outside of it
		 * @param modal true if keyboard and mouse events for widgets not contained by the dialog
		 * 		should be ignored
		 */
		public SeparateViewDialog(StatistiekView sv, boolean autoHide, boolean modal, DialogBox.Caption caption)
		{
			super(autoHide, modal, caption);
			
			this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
			this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
			this.statistiekCss.ensureInjected();

			this.statistiekView = sv;
			LayoutPanel panel = new LayoutPanel();
			panel.addStyleName(statistiekCss.separateViewDialog());
			panel.add(sv.getWidget());
			panel.setPixelSize(sv.getWidth(), sv.getHeight() + StatistiekGWT.BUTTON_HEIGHT); // set size explicitely, else panel won't show in dialogbox
			this.setWidget(panel);
			this.setText(sv.getViewName());
		}

		public void onWindowClosing(ClosingEvent arg0)
		{
			// GWT.log("windowClosing(): previousSelectedView="
			// + StatInteractiePanelView.this.previousSelectedView +
			// ", selectedView=" + selectedView);

			processCloseSeparateView(this.statistiekView);
		}
		
	} // class SeparateViewDialog
	
	
	public class ImageAnchor extends Anchor
	{
		public ImageAnchor()
		{
		}

		public void setResource(ImageResource imageResource)
		{
			Image img = new Image(imageResource);
			img.setStyleName("navbarimg");
			DOM.insertBefore(getElement(), 
				img.getElement(),
				DOM.getFirstChild(getElement()));
		}
		
	} // class ImageAnchor
	
	/**
	 * Detect whether there has been a loong click or 'tap & hold'.
	 * 
	 * @return
	 */
    protected boolean isLongClick() 
    {
    	return System.currentTimeMillis() - taptime > 300;
	}

    /**
	 * Get tab title widget with close button and context menu.
	 * 
	 * @param widget
	 * @param title
	 * @return
	 */
	private Widget getTabTitle(final Widget widget, final String title) 
	{

	    final HorizontalPanel hPanel = new HorizontalPanel();
	    final Label label = new Label(title);
	    DOM.setStyleAttribute(label.getElement(), "whiteSpace", "nowrap");
	    
	    label.addDomHandler(new LabelClickHandler(widget, title), ContextMenuEvent.getType());
	    label.addClickHandler(new LabelClickHandler(widget, title)); // to handle click to set focus

	    // make the tab draggable
	    DragTabHandler handler = new DragTabHandler(widget, title);
	    label.addMouseDownHandler(handler);
	    label.addMouseMoveHandler(handler);
	    label.addMouseUpHandler(handler);
	    label.addTouchStartHandler(handler);
	    label.addTouchMoveHandler(handler);
	    label.addTouchEndHandler(handler);
	    
	    // add handler to the horizontal panel 
//	    hPanel.addDomHandler(handler, MouseDownEvent.getType());
//	    hPanel.addDomHandler(handler, MouseMoveEvent.getType());
//	    hPanel.addDomHandler(handler, MouseUpEvent.getType());
//	    hPanel.addDomHandler(handler, TouchStartEvent.getType());
//	    hPanel.addDomHandler(handler, TouchMoveEvent.getType());
//	    hPanel.addDomHandler(handler, TouchEndEvent.getType());
	    
	    label.getElement().getStyle().setCursor(Cursor.POINTER);//DEFAULT);  

	    ImageAnchor closeBtn = new ImageAnchor();
	    closeBtn.setResource(statistiekGWTClientBundle.crossResource());

	    closeBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event)
			{
				int widgetIndex = tabPanel.getWidgetIndex(widget);
				GWT.log("StatInteractiePanelView.getTabTitle().onClick(): tab index " + widgetIndex
					+ ", widgetCount = " + StatInteractiePanelView.this.tabPanel.getWidgetCount());
				
				if (StatInteractiePanelView.this.model.getStatTableModel()
						.isViewsAddable()
					&& widgetIndex >= 0
					&& (widgetIndex < StatInteractiePanelView.this.tabPanel.getWidgetCount() - 1)) // is getWidgetCount getTabCount()?
				{
					StatInteractiePanelView.this.model
						.removeView(StatInteractiePanelView.this.model
							.mainWindowIndexToGeneralIndex(widgetIndex));

					tabPanel.remove(widgetIndex);
					
					// tabel-views moeten geupdate worden anders toont datagrid geen inhoud in de tab ((datagrid) table.redraw() is noodzakelijk)
					for (int i = 0; i < model.getViews().size(); i++)
					{
						StatistiekView view = model.getViews().get(i);
						if (tabPanel.getWidget(tabPanel.getSelectedIndex()) instanceof StatTable && view.getViewType().equals(StatistiekGWT.VIEWS[0])) // tabel view
						{
							view.update();
						}
					}
				}
				
			}
		});
	    hPanel.add(label);
	    hPanel.add(new HTML("&nbsp&nbsp&nbsp"));
	    hPanel.add(closeBtn);
	    hPanel.setStyleName("gwt-TabLayoutPanelTab");
	    
	    return hPanel;
	}

	/**
	 * Get a non-editable tab title widget, i.e., no close button and no context menu.
	 * 
	 * @param widget
	 * @param title
	 * @return
	 */
	private Widget getTabTitleNonEditable(final Widget widget, final String title) 
	{

	    final HorizontalPanel hPanel = new HorizontalPanel();
	    final Label label = new Label(title);
	    DOM.setStyleAttribute(label.getElement(), "whiteSpace", "nowrap");
	    
	    label.addClickHandler(new LabelClickHandler(widget, title)); // to handle click to set focus
	    
	    // make the tab draggable
	    DragTabHandler handler = new DragTabHandler(widget, title);

	    label.getElement().getStyle().setCursor(Cursor.DEFAULT);  

	    hPanel.add(label);
	    hPanel.setStyleName("gwt-TabLayoutPanelTab");
	    return hPanel;
	}

	/**
	 * Get the index of the given view name (considering in all views).
	 * 
	 * @param name
	 * @return
	 */
	public int getIndexOfViewName(String name)
	{
		int index = -1;
		ArrayList<StatistiekView> views = this.getViews();
		for (int i = 0; i < views.size(); i++)
		{
			if (views.get(i).getViewName().equals(name))
			{
				index = i;
				break;
			}
		}
		
		return index;
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
		String info = event.getInfo();
		// test syl: alleen nodig als add/edit/remove column, niet als addRow of dataEditable...
		if (info.equals(TableChangeEvent.ADD_COLUMN)
			|| info.equals(TableChangeEvent.EDIT_COLUMN)
			|| info.equals(TableChangeEvent.REMOVE_COLUMN)
			|| info.equals(TableChangeEvent.IMPORT_DATA))
		{
			GWT.log("StatInteractiePanelView.onTableChange()");
			this.update();
		}
	}

	@Override
	public void onAddView(AddViewEvent event)
	{
		GWT.log("StatInteractiePanelView.onAddView()");
		this.update();
	}

	/**
	 * Remove all view tabs
	 */
	public void removeViewTabs()
	{
		int count = this.tabPanel.getWidgetCount();
		if (count > 1)
		{
			for (int i = this.tabPanel.getWidgetCount() - 2; i > -1; i--)
			{
				this.tabPanel.remove(i);
			}
		}
	}
	
	/**
	 * Remove all tabs.
	 */
	public void removeAllTabs()
	{
		int count = this.tabPanel.getWidgetCount();
		if (count > 0)
		{
			for (int i = this.tabPanel.getWidgetCount() - 1; i > -1; i--)
			{
				this.tabPanel.remove(i);
			}
		}
	}
	
	public void setWidth(int width)
	{
		this.tabPanel.setWidth(width);
	}
	
	public void setHeight(int height)
	{
		this.tabPanel.setHeight(height);
	}
}
