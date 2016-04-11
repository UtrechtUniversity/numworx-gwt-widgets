package fi.statistiekgwt.client.boxplot;

import java.util.ArrayList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import fi.statistiekgwt.client.DialogButton;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.StatistiekUtils;
import fi.statistiekgwt.client.StatistiekUtils.DummyTouchHandler;
import fi.statistiekgwt.client.event.OutlierChangeEvent;
import fi.statistiekgwt.client.event.OutlierChangeEventHandler;
import fi.statistiekgwt.client.event.SelectionChangeEvent;
import fi.statistiekgwt.client.event.SelectionChangeEventHandler;
import fi.statistiekgwt.client.event.TableChangeEvent;
import fi.statistiekgwt.client.event.TableChangeEventHandler;
import fi.statistiekgwt.client.event.ViewSelectionChangeEvent;
import fi.statistiekgwt.client.event.ViewSelectionChangeEventHandler;
import fi.statistiekgwt.client.types.AllowedTypes;

/**
 * MVC View for statistiekview Boxplot
 * 
 * @author ManuDrijvers, Sylvia van Borkulo
 * 
 */
public class BoxplotView extends LayoutPanel implements TableChangeEventHandler, SelectionChangeEventHandler, 
	HasHandlers, OutlierChangeEventHandler
{
	private BoxplotModel model;
	private BoxplotController controller;
	private BoxplotUserOptionsPanel userOptionsPanel;
	private DialogButton dialogButton;

	/**
	 * Op panel 'alles' staan mainpanel en dialogbuttonpanel.
	 */
	private DockLayoutPanel alles;
	/**
	 * Mainpanel contains the boxplot area
	 */
	private Boxplot mainPanel;
	private ScrollPanel scrollPanel;
	private HorizontalPanel dialogButtonPanel;

	public static final int KEEP_CLEAR_WIDTH = 50;

	private int width;
	private int height;
	
	/**
	 * Dummy touch handler that stops propagation. Used to avoid that an external view 
	 * in dragontouch dialogbox prevents default touch events, i.e., click events. 
	 */
	DummyTouchHandler dummyTouchHandler;
	/**
	 * The event bus to send events to event handlers associated 
	 * with the views using StatTableModel.
	 */
	EventBus eventBus;
	/**
	 * The handler registration used to remove the view's 
	 * table change event handler occurrence.
	 */
	HandlerRegistration tableChangeEventHandlerRegistration;
	/**
	 * The handler registration used to remove the view's
	 * selection change event handler occurrence.
	 */
	HandlerRegistration selectionChangeEventHandlerRegistration;
	/**
	 * The handler registration used to remove the view's
	 * outlier change event handler occurrence.
	 */
	HandlerRegistration outlierChangeEventHandlerRegistration;
	
	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;

	/**
	 * Constructor
	 * 
	 * @param model
	 *            the model
	 * @param controller
	 *            the controller
	 */
	public BoxplotView(BoxplotModel model, BoxplotController controller)
	{
		super();

		this.alles = new DockLayoutPanel(Unit.PX);
		
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.model = model;
		this.controller = controller;

		this.dummyTouchHandler = StatistiekUtils.getDummyTouchHandler();
		this.eventBus = StatistiekUtils.EVENT_BUS;//new SimpleEventBus();
		
		// bind boxplotview to stattablemodel: to handle table changes in stattablemodel
		this.tableChangeEventHandlerRegistration = this.model.getStatTableModel().addTableChangeEventHandler(this);

		// bind boxplotview to stattablemodel: to handle selection changes in stattablemodel
		this.selectionChangeEventHandlerRegistration = this.model.getStatTableModel().addSelectionChangeEventHandler(this);

		// bind boxplotview to stattablemodel: to handle outlier changes in stattablemodel
		this.outlierChangeEventHandlerRegistration = this.model.getStatTableModel().addOutlierChangeEventHandler(this);

		// create GUI
		userOptionsPanel = new BoxplotUserOptionsPanel(this, controller, model);
		// initial update for setting widgets in user options panel
		this.userOptionsPanel.update();

		this.initializeSize();

		dialogButton = userOptionsPanel.getDialogButton();
		
		this.dialogButtonPanel = new HorizontalPanel();
		this.dialogButtonPanel.setWidth("100%");
		this.dialogButtonPanel.setHeight(StatistiekGWT.BUTTON_HEIGHT + "px");//"100%");
		this.dialogButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.dialogButtonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.dialogButtonPanel.addStyleName(statistiekCss.backgroundblue());
		this.dialogButton = userOptionsPanel.getDialogButton();
		this.dialogButtonPanel.add(this.dialogButton);

		this.alles.addSouth(this.dialogButtonPanel, StatistiekGWT.BUTTON_HEIGHT);
		if (!this.model.getStatTableModel().isViewsEditable())
		{
			this.dialogButtonPanel.setHeight("0px");
		}

		this.mainPanel = new Boxplot(this);

		this.scrollPanel = new ScrollPanel(this.mainPanel.getCanvas());
		this.scrollPanel.setAlwaysShowScrollBars(false);
		this.scrollPanel.addStyleName(statistiekCss.backgroundwhite());

		this.alles.add(this.scrollPanel);

		this.alles.setPixelSize(this.getWidth(), this.getHeight() + StatistiekGWT.BUTTON_HEIGHT);
		
		// add alles to boxplotview (layoutpanel)
		this.add(this.alles);

		this.addHandlers();
	}

	/**
	 * Add handlers, i.e., click and touch handlers to the buttons.
	 * Also add dummy handlers to stop propagation when view 
	 * is shown in its own window in a touch environment.
	 */
	private void addHandlers()
	{
		// let the view stay scrollable and clickable when shown in own window on a touch screen
	    this.alles.addDomHandler(this.dummyTouchHandler, TouchStartEvent.getType());
	    this.alles.addDomHandler(this.dummyTouchHandler, TouchMoveEvent.getType());
	    this.alles.addDomHandler(this.dummyTouchHandler, TouchEndEvent.getType());

		this.dialogButton.addClickHandler(this.dialogButton.getClickHandler());
		this.dialogButton.addDomHandler(this.dummyTouchHandler, TouchStartEvent.getType());
		this.dialogButton.addDomHandler(this.dummyTouchHandler, TouchEndEvent.getType());
	}

	/**
	 * Initialize the view's size.
	 */
	private void initializeSize()
	{
		this.setWidth(this.controller.getWidth());
		
		if (this.model.getStatTableModel().isViewsEditable())
		{
			this.setHeight(this.controller.getHeight());
		}
		else
		{
			// take up the space reserved for the user options button
			this.setHeight(this.controller.getHeight() + StatistiekGWT.BUTTON_HEIGHT);
		}
	}

	private void setMainPanelSize()
	{
		this.scrollPanel.setPixelSize(this.getWidth(), this.getHeight());
		this.scrollPanel.setAlwaysShowScrollBars(false);
		
		this.scrollPanel.setPixelSize(this.getWidth(), this.getHeight());
		this.scrollPanel.setAlwaysShowScrollBars(false);

		this.mainPanel.getCanvas().setCoordinateSpaceWidth(this.getWidth() - 8); // correction of 8 for scrollbar
		this.mainPanel.getCanvas().setCoordinateSpaceHeight(this.getHeight() - 8); // correction of 8 for scrollbar
	}
	
	/**
	 * Returns true if the view has a split, else false.
	 * @return
	 * 		 True if the view has a split, else false.
	 */
	public boolean hasSplit()
	{
		return (this.model.getColumnSplitIndex() > -1);
	}

	/**
	 * Get the views width.
	 */
	public int getWidth()
	{
		return this.width;
	}
	
	/**
	 * Get the views height.
	 */
	public int getHeight()
	{
		return this.height;
	}
	
	/**
	 * Set the width of histogram view.
	 * 
	 * @param w
	 */
	public void setWidth(int w)
	{
		this.width = w;
	}

	/**
	 * Set the height of histogram view.
	 * 
	 * @param h
	 */
	public void setHeight(int h)
	{
		this.height = h;
	}

	/**
	 * Subscribe for events
	 */
	public HandlerRegistration addViewSelectionChangeEventHandler(ViewSelectionChangeEventHandler handler)
	{
		return this.eventBus.addHandler(ViewSelectionChangeEvent.TYPE, handler);
	}
	
	public ScrollPanel getScrollPanel()
	{
		return this.scrollPanel;
	}

	/**
	 * Gets the string that is currently selected in the column box
	 * 
	 * @return
	 */
	public String getColumnBoxSelectedString()
	{
		return (String) this.userOptionsPanel.getColumnBoxSelectedString();
	}

	public boolean isTukeyBoxSelected()
	{
		return this.userOptionsPanel.isTukeyBoxSelected();
	}

	public boolean isVerticalBoxesButtonSelected()
	{
		return this.userOptionsPanel.isVerticalBoxesButtonSelected();
	}

	/**
	 * @return the userOptionsPanel
	 */
	public BoxplotUserOptionsPanel getUserOptionsPanel()
	{
		return userOptionsPanel;
	}

	public int getSplitVarBoxSelectedIndex()
	{
		return userOptionsPanel.getSplitVarBoxSelectedIndex();
	}

	public int getSplitBinsBoxSelectedInt()
	{
		return userOptionsPanel.getSplitBinsBoxSelectedInt();
	}

	public double getSplitminBoundary()
	{
		return userOptionsPanel.getSplitMinBoundary();
	}

	public void setSplitMinBoundary(double d)
	{
		this.userOptionsPanel.setSplitMinBoundary(d);
	}

	public double getSplitBinWidth()
	{
		return userOptionsPanel.getSplitBinWidth();
	}

	public void setSplitBinWidth(double d)
	{
		this.userOptionsPanel.setSplitBinWidth(d);
	}

	/**
	 *Set the split bin width based on the model's split bin boundaries. 
	 */
	public void setSplitBinWidth()
	{
		this.userOptionsPanel.setSplitBinWidth();
	}

	/**
	 *	Deze methode is nodig om type te kunnen opvragen van de te
	 *	tekenen variabele in BoxplotDependentAxis.
	 * @return
	 */
	public BoxplotModel getModel()
	{
		return this.model;
	}
	
	public void setModel(BoxplotModel model)
	{
		this.model = model;
		userOptionsPanel.setModel(model);
		this.update();
		this.userOptionsPanel.update();
	}

	public void update()
	{
		model.setPercentileValues();
		
		// check for empty data set
		if (!this.model.isEmptyBoxplot())
		{
			this.dialogButton.setVisible(this.model.getStatTableModel()
				.isViewsEditable());

			userOptionsPanel.update();

			if (this.model.getStatTableModel().isColumnIndexValid(
				this.model.getColumnIndex()))
			{
				this.mainPanel.setDrawable(true);
				
				if (!this.model.getStatTableModel().isColumnIndexValid(
					this.model.getColumnSplitIndex()))
				{
					// no split
					this.mainPanel.set(
						this.model.getStatTableModel().getColumnName(this.model.getColumnIndex()),
						this.model.getOutlierMinValue(0),
						this.model.getMinValue(0),
						this.model.getLowerQuartile(0),
						this.model.getMedian(0),
						this.model.getUpperQuartile(0),
						this.model.getMaxValue(0),
						this.model.getOutlierMaxValue(0),
						this.model.getDataMinValue(),
						this.model.getDataMaxValue(),
						this.model.isVerticalBoxplots(),
						this.getWidth(),
						this.getHeight());

				} // no split
				else
				{
					// split
					
					// set with arrays of values for each split class
					this.mainPanel.set(
						this.model.getStatTableModel().getColumnName(this.model.getColumnIndex()),
						this.model.getOutlierMinValues(),
						this.model.getMinValues(),
						this.model.getLowerQuartiles(),
						this.model.getMedians(),
						this.model.getUpperQuartiles(),
						this.model.getMaxValues(),
						this.model.getOutlierMaxValues(),
						this.model.getDataMinValue(),
						this.model.getDataMaxValue(),
						this.model.isVerticalBoxplots(),
						this.getWidth(),
						this.getHeight());
						
				}

			} // columnIndex valid
			else
			{
				this.mainPanel.setDrawable(false);
			}
			
		} // non empty dataset
		else
		{
			this.mainPanel.setDrawable(false);
			
			// empty dataset
			userOptionsPanel.update();			
		}
		
		this.setMainPanelSize();

		this.mainPanel.initializeHighlightValues();
		
		if (this.model.isTukeyBox() 
			&& !this.getModel().isEmptyBoxplot()
			&& this.mainPanel.isDrawable())
		{
			this.mainPanel.initializeOutlierHighlightValues();
		}
		
		this.mainPanel.paint();
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event)
	{
//		GWT.log("BoxplotView.onSelectionChange(): event.sender = " + event.getSenderName());
//		if (!event.getSenderName().equals(this.controller.getViewName()))
//		{
//			// waarom eigenlijk?
//			this.update();
//		}
	}

	@Override
	public void onTableChange(TableChangeEvent event)
	{
		GWT.log("BoxplotView.onTableChange()");

		if (!event.getInfo().equals(TableChangeEvent.ADD_ROW) // if add row do nothing
			&& !event.getInfo().equals(TableChangeEvent.SORT_COLUMN)) // if sort column do nothing
		{
			if (event.getInfo().equals(TableChangeEvent.ADD_COLUMN))
			{
				// only update user options panel
				this.userOptionsPanel.update();
			}
			else
			{
				if (event.getInfo().equals(TableChangeEvent.REMOVE_ROW)
					|| event.getInfo().equals(TableChangeEvent.REMOVE_ROWS))
				{
					if (this.model.getSplitOptions().getColumnSplitIndex() > -1)
					{
						// er is een split
						
						// split bins opnieuw berekenen
						this.recalculateSplitBinBoundaries();
					}
				}
				else if (event.getInfo().equals(TableChangeEvent.SET_VALUE_AT)
					|| event.getInfo().equals(TableChangeEvent.EDIT_COLUMN))
				{
					if (event.getColumnIndex() == this.model.getSplitOptions().getColumnSplitIndex())
					{
						// split bins opnieuw berekenen
						this.recalculateSplitBinBoundaries();
					}
				}
				else if (event.getInfo().equals(TableChangeEvent.REMOVE_COLUMN))
				{
					this.model.updateColumnIndex(event.getColumnIndex());
					if (this.model.getColumnSplitIndex() == -1)
					{
						getUserOptionsPanel().setVisibleSplitOptions(false);
						getUserOptionsPanel().clearGUISplitComponents();
					}
				}
				
				// update both view and user options panel
				this.update();
			}
		}
	}
	
	/**
	 * Recalculate the split bin boundaries for column with columnSplitIndex
	 * if possible.
	 * 
	 * @param columnIndex
	 * 		The index of the column for which the bin
	 *      boundaries will be calculated.
	 * @param typeHasChanged
	 * 		The type has changed yes/no.
	 */
	public void recalculateSplitBinBoundaries()
	{
		if (this.model.columnIndexValid())
		{
			int splitIndex = this.model.getSplitOptions().getColumnSplitIndex();
			AllowedTypes splitType = this.model.getStatTableModel().getColumnTypes().get(splitIndex).getType();
			if (splitType.isNumber())
			{
				ArrayList<Double> boundaries = new ArrayList<Double>();
				boundaries = StatistiekGWT.appropriateBoundaries(
					this.model.getStatTableModel().getColumnMin(
						this.model.getSplitOptions().getColumnSplitIndex()),
					this.model.getStatTableModel().getColumnMax(
						this.model.getSplitOptions().getColumnSplitIndex()),
					this.getSplitBinsBoxSelectedInt());
	
				this.model.setSplitBoundaries(boundaries);
				this.model.setSplitOptions(this.model.getSplitOptions());
				this.setModel(this.model);
			}
		}
	}
	
	/**
	 * Remove all of this view's handler occurrences.
	 */
	public void removeHandlers()
	{
		this.tableChangeEventHandlerRegistration.removeHandler();
		this.selectionChangeEventHandlerRegistration.removeHandler();
		this.outlierChangeEventHandlerRegistration.removeHandler();
	}

	@Override
	public void onOutlierChange(OutlierChangeEvent event)
	{
		this.update();
	}
}
