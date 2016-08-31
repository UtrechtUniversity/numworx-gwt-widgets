package fi.statistiekgwt.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vectomatic.file.Blob;
import org.vectomatic.file.ErrorCode;
import org.vectomatic.file.File;
import org.vectomatic.file.FileError;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import org.vectomatic.file.events.ErrorEvent;
import org.vectomatic.file.events.ErrorHandler;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource.ClassName;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

import fi.statistiekgwt.client.StatistiekUtils.DummyTouchHandler;
import fi.statistiekgwt.client.columndialog.ColumnDialogController;
import fi.statistiekgwt.client.columndialog.ColumnDialogModel;
import fi.statistiekgwt.client.columndialog.ColumnDialogView;
import fi.statistiekgwt.client.event.OutlierChangeEvent;
import fi.statistiekgwt.client.event.OutlierChangeEventHandler;
import fi.statistiekgwt.client.event.SelectionChangeEvent;
import fi.statistiekgwt.client.event.SelectionChangeEventHandler;
import fi.statistiekgwt.client.event.TableChangeEvent;
import fi.statistiekgwt.client.event.TableChangeEventHandler;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;


/**
 * A Table StatistiekView
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class StatTable extends DockLayoutPanel implements StatistiekView, TableChangeEventHandler,
	SelectionChangeEventHandler, OutlierChangeEventHandler
{
	private static final Logger logger = Logger.getLogger(ClassName.class.getName());

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
	
	private int width;
	private int height;

	/**
	 * The key provider that provides the unique ID of a row in the table, i.e., the row number.
	 */
	public static final ProvidesKey<List<String>> KEY_PROVIDER = new ProvidesKey<List<String>>()
	{
		@Override
		public Object getKey(List<String> s)
		{
			// the last item in s is the rowNumber; this is the unique ID
			return ((s == null) || (s.size() == 0)) ? null : s.get(s.size()-1);
		}
	};
	
	private static final int TABLE_PAGE_SIZE = 1000;
	private static final String DEFAULT_SEPARATOR = ";";
	private static final String CHECKBOX_CELL_WIDTH_STYLE = "width: 20px";//30px";
	private static final int CHECKBOX_COLUMN_WIDTH = 35;//50;
	private static final int COLUMN_ENUM_DATA_PADDING = 50;//45;
	/**
	 * For large datasets determining the column and cell width will not
	 * consider all rows, but only the first LARGE_DATASET_ROWCOUNT.
	 */
	private static final int LARGE_DATASET_LIMITED_ROWCOUNT = 250;
	/**
	 * The string representing the type of an excel file saved as CSV.
	 * 
	 * Note: Old excel files (with .xls extension) have the same file type
	 * "application/vnd.ms-excel". When trying to import an xls file, an
	 * error message will appear in the table instead of in a popup window 
	 * in the import dialog.
	 */
	private static final String CSV_FROM_EXCEL_FILE_TYPE = "application/vnd.ms-excel";
	private static final String CSV_FROM_EXCEL_FILE_TYPE2 = "text/csv";

	private static final int WIDTH_PASTE_DIALOG = 300;
	private static final int HEIGHT_PASTE_DIALOG = 230;
	public static final String CELL_STYLE_FONT_SIZE = "font-size: 13px";//0.875em";
	public static final String TABLE_HEADER_FONT = "bold 13px sans-serif";//"bold Arial Unicode MS, Arial, sans-serif small";
	private static final int HEADER_PADDING = 15;
	/**
	 * Some extra padding used for comboboxes.
	 */
	private static final int ENUM_PADDING = 22;
	private static final int MINIMUM_CELL_WIDTH = 30;
	private static final int MAXIMUM_CELL_WIDTH = 60;

	/**
	 * The number of all possible buttons, i.e.,
	 * 		- Open file
	 * 		- Add row
	 * 		- Add column
	 * 		- Delete selected rows
	 * 		- Copy (to another component with cross widget communication) 
	 * 		- Paste from clipboard
	 * 		- Reset 
	 */
	private static final int NUMBER_OF_ALL_POSSIBLE_BUTTONS = 7;

	private StatTableModel statTableModel;

	// Include field statInteractiePanel to process the reset actions
	private StatInteractiePanel statInteractiePanel;

	private StatTableDataGrid<List<String>> table; // datagrid provides fixed header and footer section
	protected ListDataProvider<List<String>> dataProvider;
	private DockLayoutPanel tablePanel;
	private SimplePager pager;
	private HorizontalPanel pagerPanel;
	private MultiSelectionModel<List<String>> selectionModel;
	ArrayList<String> headers;
	
	private Label tableMessageLabel;
	/**
	 * Array with the column header width for each data column in table.
	 * The first checkbox column is excluded. 
	 */
	private int[] columnHeaderWidth;
	
	/**
	 * Array with the maximum cell width for each data column in table.
	 * The first checkbox column is excluded. 
	 */
	private int[] maxCellWidth;	
	
	/**
	 * Array with the actual cell width for each data column in table.
	 * The first checkbox column is excluded. 
	 */
	private int[] cellWidth;
	
	/**
	 * Array with the actual column width for each data column in table.
	 * The first checkbox column is excluded. 
	 */
	private int[] columnWidth;
	
	/**
	 * The width of the row number column.
	 */
	private int rowNumberWidth;

	private String viewName;
	private PopupPanel headerPopupMenu;
	private MenuBar headerMenuBar;
	private MenuItem sortAscendingItem;
	private MenuItem sortDescendingItem;
	private MenuItem editItem;
	private MenuItem deleteItem;
	private MenuItem infoItem;
	private Command sortAscendingCommand;
	private Command sortDescendingCommand;
	private Command editCommand;
	private Command deleteCommand;
	private Command infoCommand;
	private int popupColumnIndex;

	/**
	 * A popup menu with options for marking the cell and the row as outlier.
	 */
	private PopupPanel outlierPopupMenu;
	private MenuBar outlierMenuBar;
	/**
	 * Item in the outlierPopupMenu.
	 */
	private MenuItem outlierCellItem;
	/**
	 * Item in the outlierPopupMenu.
	 */
	private MenuItem outlierRowItem;
	/**
	 * A popup with only the option for marking the row as outlier.
	 * Used for right clicking the row numbers and the selection
	 * checkbox in the table.
	 */
	private PopupPanel rowOutlierPopupMenu;
	private MenuBar rowOutlierMenuBar;
	/**
	 * Item in the rowOutlierPopupMenu.
	 */
	private MenuItem rowOutlierRowItem;

	private Command markOutlierCellCommand;
	private Command markOutlierRowCommand;
	private Command demarkOutlierCellCommand;
	private Command demarkOutlierRowCommand;
	private int outlierPopupRowIndex;
	private int outlierPopupColumnIndex;
	
	
	/**
	 * Dialog in which data can be pasted and imported into the table. 
	 */
	private DialogBox pasteDataDialog;
	/**
	 * The button to import data in the paste data dialog.
	 */
	/**
	 * The text area in which data can be pasted.
	 * Used to import pasted clipboard data.
	 */
	private ExtendedTextArea pasteDataArea;
	/**
	 * Label used to give fail messages related to the import
	 * of pasted data.
	 */
	private Label importPasteDataMessage;
	/**
	 * The button to import the pasted data in 
	 * the paste data dialog.
	 */
	private Button importPasteDataButton;
	/**
	 * The button to cancel the paste data dialog.
	 */
	private Button cancelPasteDataButton;
	
	private HorizontalPanel editDataPanel;
	private Button addRowButton;
	private Button addColumnButton;
	private Button pasteButton, copyButton;
	private Button deleteRowsButton;
	private PushButton resetButton;
	private Button importButton;
	private FileUploadExt fileUpload;
	private DialogBox fileUploadDialogBox;
	private Button fileUploadImportButton;
	private Button fileUploadCancelButton;
//	private FormPanel formPanel;
	
	private StatTableClickHandler clickHandler;
	/**
	 * Mouse up handler for resetting isMouseDown.
	 */
	private StatTableMouseUpHandler mouseUpHandler;
	/**
	 * Index of the last row clicked.
	 */
	private int clickedRowIndex = -1;
	private Handler<List<String>> cellPreviewHandler;
	private DummyTouchHandler dummyTouchHandler;
	private ImportMessageDialogBox importBox;
	private MessageDialogBox messageBox;
	
	// fields for reading import data from CSV file
	protected FileReader reader;
	protected Blob blob;
	protected List<File> readQueue;
	String csvText;
	String[] csvHeaders;
	ArrayList<String> dataRows;
	
	/**
	 * Temporary index used to create columns.
	 */
	private int tempColumnIndex;

	/**
	 * Temporary index used when handling edit column events.
	 */
	private int editColumnIndex;
	
	/**
	 * The duration of the touch tap, to be able to detect a long tap
	 * for showing the outlier menu.
	 */
	protected long taptime;
	
	private boolean isMouseDown = false;

	/**
	 * Constructor without viewname, the initial table view.
	 * 
	 * @param statTableModel
	 *            The datamodel
	 * @param statInteractiePanel
	 *            The StatInteractiePanel
	 */
	public StatTable(StatTableModel statTableModel,
		StatInteractiePanel statInteractiePanel)
	{
		super(Unit.PX);
		
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();
		
		this.statTableModel = statTableModel;
		this.tableChangeEventHandlerRegistration = this.statTableModel.addTableChangeEventHandler(this);
		this.selectionChangeEventHandlerRegistration = this.statTableModel.addSelectionChangeEventHandler(this);
		this.outlierChangeEventHandlerRegistration = this.statTableModel.addOutlierChangeEventHandler(this);

		this.statInteractiePanel = statInteractiePanel;
		this.viewName = "";
		this.setUp();
	}

	/**
	 * Constructor with viewname
	 * 
	 * @param statTableModel
	 *            The datamodel
	 * @param statInteractiePanel
	 *            The StatInteractiePanel
	 * @param viewName
	 *            The name of this view
	 */
	public StatTable(StatTableModel statTableModel,
		StatInteractiePanel statInteractiePanel, String viewName)
	{
		super(Unit.PX);
		
		statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.statTableModel = statTableModel;
		this.tableChangeEventHandlerRegistration = this.statTableModel.addTableChangeEventHandler(this);
		this.selectionChangeEventHandlerRegistration = this.statTableModel.addSelectionChangeEventHandler(this);
		this.outlierChangeEventHandlerRegistration = this.statTableModel.addOutlierChangeEventHandler(this);

		this.statInteractiePanel = statInteractiePanel;
		this.viewName = viewName;
		this.setUp();
	}

	/**
	 * Update field popup. If the data is not editable, the options edit column and
	 * delete column are not available.
	 */
	private void updatePopUp()
	{
		if (!this.statTableModel.isDataEditable())
		{
			// delete menu items if present
			if (this.headerMenuBar.getItemIndex(editItem) > -1)
			{
				this.headerMenuBar.removeItem(editItem);
			}
			if (this.headerMenuBar.getItemIndex(deleteItem) > -1)
			{
				this.headerMenuBar.removeItem(deleteItem);
			}
		}
		else
		{ // data is editable
			// add menu items if not present
			if (this.headerMenuBar.getItemIndex(editItem) == -1)
			{
				this.headerMenuBar.addItem(editItem);
			}
			if (this.headerMenuBar.getItemIndex(deleteItem) == -1)
			{
				this.headerMenuBar.addItem(deleteItem);
			}
		}
	}

	/**
	 * Initialize
	 */
	private void setUp()
	{
		setUpHandlers();
		
		if (this.statInteractiePanel != null)
		{
			// set size
			this.setWidth(this.statInteractiePanel.getWidth());
			this.setHeight(this.statInteractiePanel.getHeight());
		}
		else
		{
			// use default
			this.setWidth(StatistiekGWT.DEFAULT_WIDTH);
			this.setHeight(StatistiekGWT.DEFAULT_HEIGHT - StatistiekGWT.BUTTON_HEIGHT);
		}
		
		// set up the file chooser to open a data file
		this.setUpFileUpload();
		
		this.setUpPasteDataDialog();
		
		this.table = new StatTableDataGrid<List<String>>(KEY_PROVIDER);//new DataGrid<List<String>>(KEY_PROVIDER);
		 /*
	     * Do not refresh the headers every time the data is updated. The footer
	     * depends on the current data, so we do not disable auto refresh on the
	     * footer.
	     */
	    this.table.setAutoHeaderRefreshDisabled(true);
	    // Set the message to display when the table is empty
	    tableMessageLabel = new Label(StatistiekGWT.rb.emptyTableMessage());
	    // set large width for horizontal scrollbar, because an empty table (e.g., with many columns) won't show a horizontal scrollbar
	    tableMessageLabel.setWidth("20000px");
	    tableMessageLabel.addStyleName(statistiekCss.noScrollBars());
	    this.table.setEmptyTableWidget(tableMessageLabel);
	    // set style
	    this.table.addStyleName(statistiekCss.dataGrid());
	    this.table.addStyleName(statistiekCss.backgroundblue());
//	    this.table.addStyleName(statistiekCss.dataGridSelectedRow()); //werkt niet
	    this.table.addStyleName(statistiekCss.noSelect());
	    this.table.setWidth("100%");

	    this.dataProvider = new ListDataProvider<List<String>>();
	    // Add the table to the dataProvider.
		this.dataProvider.addDataDisplay(this.table);
		
	    // Create a Pager to control the table.
	    pagerPanel = new HorizontalPanel();
	    pagerPanel.setSize("100%", "100%");
	    pagerPanel.addStyleName(statistiekCss.backgroundblue());
		pagerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		pagerPanel.addStyleName(statistiekCss.noScrollBars());
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
//	    pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager = new StatTablePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.setDisplay(this.table);
	    pager.setPageSize(StatTable.TABLE_PAGE_SIZE);
	    pager.setRangeLimited(true);
	    pager.addStyleName(statistiekCss.noScrollBars());
	    pagerPanel.add(pager);

	    tablePanel = new DockLayoutPanel(Unit.PX);
	    tablePanel.addSouth(pagerPanel, 30);
	    tablePanel.add(this.table);
	    tablePanel.setHeight("100%");
	    tablePanel.setWidth("100%");

	    // Add a selection model so we can select cells.
	    selectionModel =
	        new MultiSelectionModel<List<String>>(KEY_PROVIDER);
	    
	    this.table.setSelectionModel(selectionModel, DefaultSelectionEventManager
	        .<List<String>> createCheckboxManager(0));
	    // set the row number column width
	    this.updateRowNumberWidth();
	    initializeWidth();

	    // create vertical header menubar
		this.headerMenuBar = new MenuBar(true);
		this.headerPopupMenu = new PopupPanel(true);
		this.headerPopupMenu.add(this.headerMenuBar);

		this.headerPopupMenu.setVisible(false);
		this.headerPopupMenu.hide();
		
		sortAscendingCommand = new Command() {
	        @Override
            public void execute() 
	        {
	        	StatTable.this.statTableModel.sort(StatTable.this.popupColumnIndex, StatistiekGWT.ASCENDING);
	        	
	        	// test syl
	        	StatTable.this.setSelectionFromModelInTable();
	        	
	        	StatTable.this.hideHeaderPopupMenu();
	        	
	        	// via stattablemodel.sort() wordt een tablechangeevent getriggered die een stattable.update() doet (met setSelectionBackground()),
	        	// maar kennelijk is default selectiekleur weer gezet, dus:
	        	setSelectionBackground();
            }
        };
		sortDescendingCommand = new Command() {
	        @Override
            public void execute() 
	        {
	        	StatTable.this.statTableModel.sort(StatTable.this.popupColumnIndex, StatistiekGWT.DESCENDING);
	        	
	        	// test syl
	        	StatTable.this.setSelectionFromModelInTable();
	        	
	        	StatTable.this.hideHeaderPopupMenu();
	        	
	        	// via stattablemodel.sort() wordt een tablechangeevent getriggered die een stattable.update() doet (met setSelectionBackground()),
	        	// maar kennelijk is default selectiekleur weer gezet, dus:
	        	setSelectionBackground();
            }
        };
        this.createEditCommand();
        deleteCommand = new Command() {
	        @Override
            public void execute() 
	        {
				StatTable.this.statTableModel.removeColumn(StatTable.this.popupColumnIndex);
	        	StatTable.this.hideHeaderPopupMenu();
            }
        };
        this.createInfoCommand();
		sortAscendingItem = new MenuItem(StatistiekGWT.rb.sortAscendingItem(), true, sortAscendingCommand);
		sortDescendingItem = new MenuItem(StatistiekGWT.rb.sortDescendingItem(), true, sortDescendingCommand);
		editItem = new MenuItem(StatistiekGWT.rb.editcolumnItem(), true, editCommand);
		deleteItem = new MenuItem(StatistiekGWT.rb.deletecolumnItem(), true, deleteCommand);
		infoItem = new MenuItem(StatistiekGWT.rb.infocolumnItem(), true, infoCommand);
		this.headerMenuBar.addItem(sortAscendingItem);
		this.headerMenuBar.addItem(sortDescendingItem);
		if (this.statTableModel.isDataEditable())
		{
			this.headerMenuBar.addItem(editItem);
			this.headerMenuBar.addItem(deleteItem);
		}
		else
		{
			this.headerMenuBar.addItem(infoItem);
		}
		
	    // create vertical outlier popup menubar
		this.outlierMenuBar = new MenuBar(true);
		this.outlierPopupMenu = new PopupPanel(true);
		this.outlierPopupMenu.add(this.outlierMenuBar);

		this.outlierPopupMenu.setVisible(false);
		this.outlierPopupMenu.hide();
		
		createOutlierCommands();
		
		this.outlierCellItem = new MenuItem(StatistiekGWT.rb.markOutlierCell(), true, markOutlierCellCommand);
		this.outlierRowItem = new MenuItem(StatistiekGWT.rb.markOutlierRow(), true, markOutlierRowCommand);
		
		this.outlierMenuBar.addItem(outlierCellItem);
		this.outlierMenuBar.addItem(outlierRowItem);

	    // create vertical row outlier popup menubar (only one option for marking row as outlier)
		this.rowOutlierMenuBar = new MenuBar(true);
		this.rowOutlierPopupMenu = new PopupPanel(true);
		this.rowOutlierPopupMenu.add(this.rowOutlierMenuBar);

		this.outlierPopupMenu.setVisible(false);
		this.outlierPopupMenu.hide();
		
		this.rowOutlierRowItem = new MenuItem(StatistiekGWT.rb.markOutlierRow(), true, markOutlierRowCommand);

		this.rowOutlierMenuBar.addItem(rowOutlierRowItem);

		// maak editDataPanel met buttons
		this.editDataPanel = new HorizontalPanel();//new LayoutPanel();
		this.editDataPanel.setSize("100%", "100%");
		this.editDataPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);//ALIGN_LEFT);
		this.editDataPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		this.editDataPanel.addStyleName(statistiekCss.backgroundblue());
		
		this.importButton = new Button(getButtonText(StatistiekGWT.rb.importButton()));
		this.importButton.setTitle(StatistiekGWT.rb.importButton());
		this.importButton.setHeight(StatistiekGWT.BUTTON_HEIGHT + "px");
		this.importButton.setWidth(getButtonWidth() + "px");
		this.importButton.addStyleName(statistiekCss.tableButton());
		if (reader != null) // don't add the button if there is no reader available
		{
			this.editDataPanel.add(this.importButton);
		}

		this.addRowButton = new Button(getButtonText(StatistiekGWT.rb.addrowButton()));
		this.addRowButton.setTitle(StatistiekGWT.rb.addrowButton());
		this.addRowButton.setHeight(StatistiekGWT.BUTTON_HEIGHT + "px");
		this.addRowButton.setWidth(getButtonWidth() + "px");
		this.addRowButton.addStyleName(statistiekCss.tableButton());
		this.editDataPanel.add(this.addRowButton);
		
		this.addColumnButton = new Button(getButtonText(StatistiekGWT.rb.addcolumnButton()));
		this.addColumnButton.setTitle(StatistiekGWT.rb.addcolumnButton());
		this.addColumnButton.setHeight(StatistiekGWT.BUTTON_HEIGHT + "px");
		this.addColumnButton.setWidth(getButtonWidth() + "px");
		this.addColumnButton.addStyleName(statistiekCss.tableButton());
		this.editDataPanel.add(this.addColumnButton);
		
		this.deleteRowsButton = new Button(getButtonText(StatistiekGWT.rb.deleteselectedrowsButton()));
		this.deleteRowsButton.setTitle(StatistiekGWT.rb.deleteselectedrowsButton());
		this.deleteRowsButton.setHeight(StatistiekGWT.BUTTON_HEIGHT + "px");
		this.deleteRowsButton.setWidth(getButtonWidth() + "px");
		this.deleteRowsButton.addStyleName(statistiekCss.tableButton());
		this.editDataPanel.add(this.deleteRowsButton);

		this.copyButton = new Button(getButtonText(StatistiekGWT.rb.copyclipboardButton()));
		this.copyButton.setTitle(StatistiekGWT.rb.copyclipboardButton());
		this.copyButton.setPixelSize(getButtonWidth(), StatistiekGWT.BUTTON_HEIGHT);
		this.copyButton.addStyleName(statistiekCss.tableButton());
		this.editDataPanel.add(this.copyButton);
		
		this.pasteButton = new Button(getButtonText(StatistiekGWT.rb.pasteclipboardButton()));
		this.pasteButton.setTitle(StatistiekGWT.rb.pasteclipboardButton());
		this.pasteButton.setHeight(StatistiekGWT.BUTTON_HEIGHT + "px");
		this.pasteButton.setWidth(getButtonWidth() + "px");
		this.pasteButton.addStyleName(statistiekCss.tableButton());
		this.editDataPanel.add(this.pasteButton);
		
		Image resetImage = new Image(statistiekGWTClientBundle.resetResource().getSafeUri());
		this.resetButton = new PushButton(resetImage);
		this.resetButton.setTitle(StatistiekGWT.rb.resetButton());
		this.resetButton.setHeight((StatistiekGWT.BUTTON_HEIGHT - 1) + "px"); // correct 1 pixel for pushbutton to make buttons and pushbutton of equal height
		this.resetButton.setWidth(getButtonWidth() + "px");
		this.resetButton.addStyleName(statistiekCss.pushbutton());
		//this.resetButton.addStyleName(statistiekCss.tableButton());
		if (StatTable.this.statInteractiePanel != null)
		{
    		HashMap resetHashMap = StatTable.this.statInteractiePanel.getModel().getResetHashMap();
    		if (resetHashMap.size() > 0)
    		{
    			this.editDataPanel.add(this.resetButton);
    		}
		}
		
		this.editDataPanel.setVisible(this.statTableModel.isDataEditable());

		super.addSouth(this.editDataPanel, StatistiekGWT.BUTTON_HEIGHT);//3);//EM
		if (!this.statTableModel.isDataEditable())
		{
			super.setWidgetSize(this.editDataPanel, 0);
		}
		
		// resizeLayoutPanel voor horizontal scrollbar van table
		ResizeLayoutPanel resizePanel = new ResizeLayoutPanel();
		resizePanel.setHeight("100%");
		resizePanel.setWidth("100%");
		resizePanel.add(tablePanel);
		super.add(resizePanel);
		
		Label label = new Label(StatistiekGWT.rb.importDialogLabel());
	    importBox = new ImportMessageDialogBox(label);
	    
	    this.addHandlers();
	    
		this.update();
		// set the right selection
		this.setSelectionFromModelInTable();
		
		setSelectionBackground();
	}

	/**
	 * Get the (if necessary shortened) text for the table buttons given the button width.
	 * 
	 * @param text
	 * @return
	 */
	private String getButtonText(String text)
	{
		String buttonText = text;
		int margin = 5;
		String periodsString = "...";
		int marginPeriods = margin + determineStringWidth(periodsString); // margin regarding "..." 
		
		int buttonWidth = getButtonWidth();
		int textWidth = determineStringWidth(text);
		
		if (textWidth > buttonWidth - margin)
		{
			buttonText = getButtonText(text, buttonWidth - marginPeriods) + periodsString;
		}
		
		return buttonText;
	}

	/**
	 * Get the first part of the text of the given width. If the width is larger than the
	 * text's width, the original text is returned.
	 * 
	 * @param text
	 * @param width
	 * @return
	 */
	private String getButtonText(String text, int width)
	{
		String shortenedText = text;
		
		while (determineStringWidth(shortenedText) > width)
		{
			shortenedText = shortenedText.substring(0, shortenedText.length() - 1);
		}
		
		return shortenedText;
	}

	/**
	 * Get the width of the table buttons. The given width of StatInteractiePanel
	 * is equally distributed over the buttons, taking the button's margin into account.
	 * 
	 * @return
	 */
	private int getButtonWidth()
	{
		int width;
		
		width = (int) (getWidth() - getNumberOfButtons() * 2 * StatistiekGWT.TABLE_BUTTON_MARGIN)
			/getNumberOfButtons();
		
		return width;
	}

	private int getNumberOfButtons()
	{
		int number = NUMBER_OF_ALL_POSSIBLE_BUTTONS;
		
		if (StatTable.this.statInteractiePanel == null)
		{
			// no reset button
			number = number - 1;
		}
		
		if (reader == null)
		{
			// no import button
			number = number - 1;
		}
		
		return number;
	}

	/**
	 * Set up the handlers. 
	 */
	private void setUpHandlers()
	{
		this.clickHandler = new StatTableClickHandler();
		this.mouseUpHandler = new StatTableMouseUpHandler();
		this.dummyTouchHandler = StatistiekUtils.getDummyTouchHandler();
		this.cellPreviewHandler = new Handler<List<String>>(){

			@Override
			public void onCellPreview(CellPreviewEvent<List<String>> event)
			{
				int rowIndex = event.getIndex();				
				int columnIndex = event.getColumn();
				
		        int button = event.getNativeEvent().getButton();
		        NativeEvent nativeEvent = event.getNativeEvent();
		        
				if ("click".equals(nativeEvent.getType())
					&& columnIndex == 0 // klik op rijnummer doet selectie
					&& button != NativeEvent.BUTTON_RIGHT)
		        {
					List<List<String>> list = (List<List<String>>) dataProvider.getList();
	    			List<String> rowObject = list.get(rowIndex);

					if (nativeEvent.getCtrlKey())
					{
						// add to selection
						StatTable.this.statTableModel.setSelected(rowIndex, true, SelectionChangeEvent.STAT_TABLE);
						selectionModel.setSelected(rowObject, true);
					}
					else
					{
						if (nativeEvent.getShiftKey() && clickedRowIndex > -1) // for shift click select a block
						{
							// select rows between clicked and current row index
							selectAllRowsBetweenIndices(rowIndex);
						}
						else
						{
							// clear previous selection
							StatTable.this.statTableModel.resetSelectionList();
							
							// no controlkey, no shiftkey: simply select
							clickedRowIndex = rowIndex;

							StatTable.this.statTableModel.setSelected(rowIndex, true, SelectionChangeEvent.STAT_TABLE);
							selectionModel.setSelected(rowObject, true);
						}
					}
					
					setSelectionBackground();
		        }
				else if (("mousedown".equals(nativeEvent.getType()) || "touchstart".equals(nativeEvent.getType())) 
					&& columnIndex == 0
					&& button != NativeEvent.BUTTON_RIGHT
					&& !nativeEvent.getShiftKey()) // when shift-clicking the old clickedRowIndex needs to be remained
				{
					isMouseDown = true;
					clickedRowIndex = rowIndex; // nodig voor klik-sleep
				}
				else if (isMouseDown && "mouseover".equals(nativeEvent.getType())
					&& columnIndex == 0
					&& button != NativeEvent.BUTTON_RIGHT)
				{
					// add the row that is left by the mouse to the selection
		    		List<List<String>> list = (List<List<String>>) dataProvider.getList();
	    			List<String> rowObject = list.get(rowIndex);
	    								
					// clear previous selection
					StatTable.this.statTableModel.resetSelectionList();					
					// select rows between clicked and current row index
	    			selectAllRowsBetweenIndices(rowIndex);
	    			
	    			setSelectionBackground();
				}
				else if ("touchend".equals(nativeEvent.getType())
					&& columnIndex == 0)
				{
//					Window.alert("rowIndex = " + rowIndex + ", columnIndex = " + columnIndex);
					
					// add the row that is left by the mouse to the selection
		    		List<List<String>> list = (List<List<String>>) dataProvider.getList();
	    								
					// clear previous selection
					StatTable.this.statTableModel.resetSelectionList();					
					// select rows between clicked and current row index
	    			selectAllRowsBetweenIndices(rowIndex);
	    			
	    			setSelectionBackground();
				}
				
				if (!rowOutlierPopupMenu.isShowing() && !outlierPopupMenu.isShowing())
				{
		            outlierPopupRowIndex = rowIndex;
		            outlierPopupColumnIndex = columnIndex - 2;
				}
				
		        
		        if (nativeEvent.getTouches() != null)
		        {
		        	// a touch event happened
		        	processTouch(nativeEvent);
		        }
		        else
		        {
		        	// a click even happened
		        	int x = event.getNativeEvent().getClientX();
		        	int y = event.getNativeEvent().getClientY();
		        	
					if ("click".equals(nativeEvent.getType())) 
			        {
			            if (nativeEvent.getCtrlKey()) 
			            {
				        	showOutlierPopup(nativeEvent, x, y);
			            }
			        }
			        else if (button == NativeEvent.BUTTON_RIGHT) 
			        {
			        	showOutlierPopup(nativeEvent, x, y);
			        }
		        }
			}

			/**
			 * Select the rows between clickedRowIndex and current row index.
			 * 
			 * @param currentRowIndex
			 * @param list
			 */
			private void selectAllRowsBetweenIndices(int currentRowIndex)
			{
	    		List<List<String>> list = (List<List<String>>) dataProvider.getList();
				List<String> rowObject;
				// select the rows from clickedRowIndex to the current rowIndex
				if (clickedRowIndex >= currentRowIndex)
				{
					for (int i = currentRowIndex; i <= clickedRowIndex; i++)
					{
						rowObject = list.get(i);
						StatTable.this.statTableModel.setSelected(i, true, SelectionChangeEvent.STAT_TABLE);
						selectionModel.setSelected(rowObject, true);
					}
				}
				else
				{
					for (int i = clickedRowIndex; i <= currentRowIndex; i++)
					{
						rowObject = list.get(i);
						StatTable.this.statTableModel.setSelected(i, true, SelectionChangeEvent.STAT_TABLE);
						selectionModel.setSelected(rowObject, true);
					}
				}
			}

		};
	}

	/**
	 * Create the commands for the outlier popup menu.
	 */
	private void createOutlierCommands()
	{
		this.markOutlierCellCommand = new Command() {
	        @Override
            public void execute() 
	        {
	        	getStatTableModel().markCellAsOutlier(
	        		outlierPopupRowIndex, 
	        		outlierPopupColumnIndex, 
	        		true);
	        	
	        	table.getRowElement(outlierPopupRowIndex).getCells().getItem(outlierPopupColumnIndex + 2).scrollIntoView();
	        	
	        	hideOutlierPopupMenu();
            }
        };
        
		this.demarkOutlierCellCommand = new Command() {
	        @Override
            public void execute() 
	        {
	        	getStatTableModel().markCellAsOutlier(
	        		outlierPopupRowIndex, 
	        		outlierPopupColumnIndex, 
	        		false);
	        	
	        	table.getRowElement(outlierPopupRowIndex).getCells().getItem(outlierPopupColumnIndex + 2).scrollIntoView();
	        	
	        	hideOutlierPopupMenu();
            }
        };
        
		this.markOutlierRowCommand = new Command() {
	        @Override
            public void execute() 
	        {
	        	getStatTableModel().markRowAsOutlier(
	        		outlierPopupRowIndex, 
	        		true);
	        	
	        	table.getRowElement(outlierPopupRowIndex).getCells().getItem(outlierPopupColumnIndex + 2).scrollIntoView();
	        	
	        	// the command is connected to either one of the popupmenus, so hide both
	        	hideOutlierPopupMenu();
	        	hideRowOutlierPopupMenu();
            }
        };
        
		this.demarkOutlierRowCommand = new Command() {
	        @Override
            public void execute() 
	        {
	        	getStatTableModel().markRowAsOutlier(
	        		outlierPopupRowIndex, 
	        		false);
	        	
	        	table.getRowElement(outlierPopupRowIndex).getCells().getItem(outlierPopupColumnIndex + 2).scrollIntoView();
	        	
	        	// the command is connected to either one of the popupmenus, so hide both
	        	hideOutlierPopupMenu();
	        	hideRowOutlierPopupMenu();
            }
        };
	}

	/**
	 * Add handlers, i.e., click and touch handlers to the buttons.
	 * Also add dummy handlers to prevent propagation when view 
	 * is shown in its own window in a touch environment.
	 */
	private void addHandlers()
	{
	    //this.table.addColumnSortHandler(sortHandler);
	    this.table.addDomHandler(this.dummyTouchHandler, TouchStartEvent.getType());
	    this.table.addDomHandler(this.dummyTouchHandler, TouchEndEvent.getType());
//	    this.table.addDomHandler(this.dummyTouchHandler, ContextMenuEvent.getType());

	    // add the handler for handling the outlier menu and for selecting rows
		addOutlierAndSelectionHandler();

	    // click handlers
		this.importButton.addClickHandler(this.clickHandler);
		this.addRowButton.addClickHandler(this.clickHandler);
		this.addColumnButton.addClickHandler(this.clickHandler);
		this.deleteRowsButton.addClickHandler(this.clickHandler);
		this.pasteButton.addClickHandler(this.clickHandler);
		this.copyButton.addClickHandler(this.clickHandler);
		this.resetButton.addClickHandler(this.clickHandler);

		// dummy touch handlers to avoid problems when shown in touchondrag dialogbox 
		// touch start
		this.importButton.addTouchStartHandler(this.dummyTouchHandler);
		this.addRowButton.addTouchStartHandler(this.dummyTouchHandler);
		this.addColumnButton.addTouchStartHandler(this.dummyTouchHandler);
		this.deleteRowsButton.addTouchStartHandler(this.dummyTouchHandler);
		this.pasteButton.addTouchStartHandler(this.dummyTouchHandler);
		this.copyButton.addTouchStartHandler(this.dummyTouchHandler);
		this.resetButton.addTouchStartHandler(this.dummyTouchHandler);
		// touch end
		this.importButton.addTouchEndHandler(this.dummyTouchHandler);
		this.addRowButton.addTouchEndHandler(this.dummyTouchHandler);
		this.addColumnButton.addTouchEndHandler(this.dummyTouchHandler);
		this.deleteRowsButton.addTouchEndHandler(this.dummyTouchHandler);
		this.pasteButton.addTouchEndHandler(this.dummyTouchHandler);
		this.copyButton.addTouchEndHandler(this.dummyTouchHandler);
		this.resetButton.addTouchEndHandler(this.dummyTouchHandler);
	}

	/**
	 * Set up the dialog for pasting data and importing 
	 * the data into the table.
	 */
	private void setUpPasteDataDialog()
	{
		this.pasteDataDialog = new DialogBox(false, true);
		this.pasteDataDialog.setText(StatistiekGWT.rb.pasteclipboardDialog());
		
		FlowPanel panel = new FlowPanel();
		
		// messages
		String messageString = StatistiekGWT.rb.pasteclipboardMessage()
			+ StatistiekGWT.rb.importPastedDataButton()
			+ ".\n" + StatistiekGWT.rb.pasteclipboardMessage2();
		Label pasteMessage = new Label(messageString);
		this.importPasteDataMessage = new Label(); // used for fail message
		this.importPasteDataMessage.addStyleName(statistiekCss.failMessage());
		
		// text area
		this.pasteDataArea = new ExtendedTextArea();
		this.pasteDataArea.setPixelSize(WIDTH_PASTE_DIALOG, HEIGHT_PASTE_DIALOG);
		this.pasteDataArea.addValueChangeHandler(new ValueChangeHandler<String>() {

		    @Override
		    public void onValueChange(ValueChangeEvent<String> event) 
		    {
		    	if (StatTable.this.importPasteDataMessage.getText().length() > 0)
		    	{
		    		StatTable.this.importPasteDataMessage.setText("");
		    	}
		    }
		});
		
		// buttons
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.addStyleName(statistiekCss.horizontalPanelWithoutBorder());
		buttonPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		String importPasteButtonText = StatistiekGWT.rb.importPastedDataButton();
		this.importPasteDataButton = new Button(importPasteButtonText, this.clickHandler);
		this.importPasteDataButton.addStyleName(statistiekCss.margin());
		String cancelButtonText = StatistiekGWT.rb.cancelButtonText();
		this.cancelPasteDataButton = new Button(cancelButtonText, this.clickHandler);
		this.cancelPasteDataButton.addStyleName(statistiekCss.margin());
		buttonPanel.add(this.importPasteDataButton);
		buttonPanel.add(this.cancelPasteDataButton);
		
		// put the elements on the panel
		panel.add(pasteMessage);
		panel.add(this.importPasteDataMessage);
		panel.add(pasteDataArea);
		panel.add(buttonPanel);
		
		// add panel to dialog
		this.pasteDataDialog.add(panel);
		this.pasteDataDialog.center();
		this.pasteDataDialog.hide();
	}

	/**
	 * Hide the column's header popup menu.
	 */
	protected void hideHeaderPopupMenu()
	{
    	StatTable.this.headerPopupMenu.setVisible(false);
    	StatTable.this.headerPopupMenu.hide();
	}
	
//	/**
//	 * Show the column's header popup menu.
//	 */
//	protected void showHeaderPopupMenu()
//	{
//    	StatTable.this.headerPopupMenu.setVisible(true);
//    	StatTable.this.headerPopupMenu.show();
//	}

	/**
	 * Hide the outlier popup menu.
	 */
	protected void hideOutlierPopupMenu()
	{
    	StatTable.this.outlierPopupMenu.setVisible(false);
    	StatTable.this.outlierPopupMenu.hide();
	}
	
//	/**
//	 * Show the outlier popup menu.
//	 */
//	protected void showOutlierPopupMenu()
//	{
//    	StatTable.this.outlierPopupMenu.setVisible(true);
//    	StatTable.this.outlierPopupMenu.show();
//	}

	/**
	 * Hide the row outlier popup menu (with only the option for marking
	 * the row as an outlier).
	 */
	protected void hideRowOutlierPopupMenu()
	{
    	StatTable.this.rowOutlierPopupMenu.setVisible(false);
    	StatTable.this.rowOutlierPopupMenu.hide();
	}
	
	/**
	 * Show the row outlier popup menu (with only the option for marking
	 * the row as an outlier).
	 */
	protected void showRowOutlierPopupMenu()
	{
    	StatTable.this.rowOutlierPopupMenu.setVisible(true);
    	StatTable.this.rowOutlierPopupMenu.show();
	}

	/**
	 * Create the column info command for the column's menu bar.
	 */
	private void createInfoCommand()
	{
        this.infoCommand = new Command() {
	        @Override
            public void execute() 
	        {
	        	StatTable.this.hideHeaderPopupMenu();
	        	
	        	ArrayList<ColumnType> list = StatTable.this.statTableModel.getColumnTypes();
				
	        	ColumnDialogModel dialogModel = new ColumnDialogModel(
					StatTable.this.statTableModel,
					StatTable.this.statTableModel.getColumnName(StatTable.this.popupColumnIndex),
					list.get(popupColumnIndex),
					StatTable.this.popupColumnIndex);
				
				ColumnDialogView dialogView;

				dialogView = new ColumnDialogView(dialogModel, StatistiekGWT.rb.columninfo());
				
				ColumnDialogController dialogController = new ColumnDialogController(
					dialogModel, dialogView);

				dialogView.center();
				dialogView.show();
            }
        };
	}

	/**
	 * Create the edit column command for the column's menu bar.
	 */
	private void createEditCommand()
	{
        editCommand = new Command() 
        {
	        @Override
            public void execute() 
	        {
	        	StatTable.this.hideHeaderPopupMenu();
	        	
	        	ArrayList<ColumnType> list = StatTable.this.statTableModel.getColumnTypes();
				
	        	ColumnDialogModel dialogModel = new ColumnDialogModel(
					StatTable.this.statTableModel,
					StatTable.this.statTableModel.getColumnName(StatTable.this.popupColumnIndex),
					list.get(popupColumnIndex),
					StatTable.this.popupColumnIndex);
				
	        	HandlerRegistration editColumnHandlerRegistration = dialogModel.addEditColumnEventHandler(StatTable.this.statTableModel);
				HandlerRegistration addColumnHandlerRegistration = dialogModel.addAddColumnEventHandler(StatTable.this.statTableModel);
				
				ColumnDialogView dialogView;

				dialogView = new ColumnDialogView(dialogModel, StatistiekGWT.rb.editacolumn());
				
				ColumnDialogController dialogController = new ColumnDialogController(
					dialogModel, dialogView);
				dialogController.setAddColumnHandlerRegistration(addColumnHandlerRegistration);
				dialogController.setEditColumnHandlerRegistration(editColumnHandlerRegistration);

				dialogView.center();
				dialogView.show();
            }
        };
	}

	private void setUpFileUpload()
	{
		this.fileUpload = new FileUploadExt(false);
		this.fileUpload.addStyleName(statistiekCss.margin());
		
		Label selectLabel = new Label(StatistiekGWT.rb.selectCSVFile());
		selectLabel.addStyleName(statistiekCss.margin());
		
	    this.fileUploadImportButton = new Button(StatistiekGWT.rb.importFile());
		this.fileUploadImportButton.addStyleName(statistiekCss.margin());
	    this.fileUploadImportButton.addClickHandler(this.clickHandler);

	    this.fileUploadCancelButton = new Button(StatistiekGWT.rb.cancelButtonText());
		this.fileUploadCancelButton.addStyleName(statistiekCss.margin());
	    this.fileUploadCancelButton.addClickHandler(this.clickHandler);

		HorizontalPanel buttonPanel = new HorizontalPanel();
		//buttonPanel.setWidth("100%");
		//buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		buttonPanel.add(fileUploadImportButton);
		buttonPanel.add(fileUploadCancelButton);

		VerticalPanel alles = new VerticalPanel();
		//alles.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		alles.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		alles.add(selectLabel);
		alles.add(this.fileUpload);
		alles.add(buttonPanel);

	    this.fileUploadDialogBox = new DialogBox(false, true);
		this.fileUploadDialogBox.add(alles);
		this.fileUploadDialogBox.hide();

		// try to create a file reader; in some browsers FileReader is not available
		try
		{
			// Create a file reader a and queue of files to read.
			// UI event handler will populate this queue by calling queueFiles()
			reader = new FileReader();
			
			reader.addLoadEndHandler(new LoadEndHandler()
			{
				/**
				 * This handler is invoked when FileReader.readAsText(),
				 * FileReader.readAsBinaryString() or FileReader.readAsArrayBuffer()
				 * successfully completes
				 */
				@Override
				public void onLoadEnd(LoadEndEvent event)
				{
					// when the load has ended the csv text can be read and processed
					processCSVText();
					
					if (reader.getError() == null)
					{
						if (readQueue.size() > 0)
						{
							readQueue.remove(0);
							readNextFile();
						}
					}
				}
			});
	
			reader.addErrorHandler(new ErrorHandler()
			{
				/**
				 * This handler is invoked when FileReader.readAsText(),
				 * FileReader.readAsBinaryString() or FileReader.readAsArrayBuffer()
				 * fails
				 */
				@Override
				public void onError(ErrorEvent event)
				{
					if (readQueue.size() > 0)
					{
						File file = readQueue.get(0);
						handleError(file, event.toDebugString());
						readQueue.remove(0);
						readNextFile();
					}
				}
			});
		}
		catch (Exception e)
		{
			logger.info("FileReader is not available");
		}
		
		readQueue = new ArrayList<File>();

		// initialize the messagebox for showing error messages
		this.messageBox = new MessageDialogBox(new Label(""));
	}

	/**
	 * Change this view's model
	 * 
	 * @param model
	 *            the new StatTableModel
	 */
	public void setModel(StatTableModel model)
	{
		this.statTableModel = model;

		this.editDataPanel.setVisible(this.statTableModel.isDataEditable());
	}

	/**
	 * StatistiekView implementation
	 */
	@Override
	public Widget getWidget()
	{
		return this;
	}

	/**
	 * StatistiekView implementation
	 * 
	 * @return this view's name
	 */
	public String getViewName()
	{
		return this.viewName;
	}

	public void setViewName(String s)
	{
		this.viewName = s;

	}

	/**
	 * Determine which rows are currently selected
	 * 
	 * @return an array containing indices of selected rows
	 */
	public int[] getSelectedRows()
	{
		//return this.table.getSelectedRows();
		//moet dit worden: return this.statTableModel.getSelectedRows()?
		ArrayList<Integer> intList = new ArrayList<Integer>();

		List<List<String>> list = (List<List<String>>) this.dataProvider.getList();

		int i = 0;

		for (List<String> row : list)
		{
		    if (selectionModel.isSelected(row))
		    {
		        intList.add(i);
		    }

		    i++;
		}
		
		// convert Integer list to primitive int array
		int[] selectedIndices = new int[intList.size()];
	    Iterator<Integer> iterator = intList.iterator();
	    for (int j = 0; j < selectedIndices.length; j++)
	    {
	        selectedIndices[j] = iterator.next().intValue();
	    }
		
		return selectedIndices;
	}

	/**
	 * Determine which column is currently selected
	 * 
	 * @return the selected column's index
	 */
	public int[] getSelectedColumns()
	{
		//return this.table.getSelectedColumns();
		return null;
	}

	/**
	 * StatistiekView implementation
	 */
	public void setUp(Frame owner)
	{
		// nothing to set up here
	}

	/**
	 * StatistiekView implementation
	 */
	public void setUp(DialogBox owner)
	{
		// nothing to set up here
	}

	/**
	 * Import the data pasted in pasteDataArea.
	 * If the data has an incorrect format, a message is shown and
	 * the pasteDataDialog remains open. Else the data is
	 * imported and the pasteDataDialog closed.
	 */
	public void importPasteData()
	{
		// read from clipboard is not trivial. See
		// http://stackoverflow.com/questions/1317052/how-to-copy-to-clipboard-with-gwt
		// http://blog.dandoy.org/2011/09/using-zeroclipboard-with-gwt.html

		String dataString = this.pasteDataArea.getText();
		String[] dataRows = dataString.split("\n");
		
		String separator = "";
		
		if (dataRows.length > 0)
		{
			separator = this.getSeparator(dataRows[0]);
		}

		// check the amount of cells in each row
		for (String row : dataRows)
		{
			if (row.split(separator).length != this.statTableModel.getColumnCount())
			{
				// row has incorrect amount of cells
				// set message
				this.importPasteDataMessage.setText(StatistiekGWT.rb.importPastedDataFailMessage());
				return;
			}
		}

		// start adding data after the last row
		int currentRow = this.statTableModel.getRowCount();
		int currentColumn;
		for (String s : dataRows)
		{
			this.statTableModel.addRowWithoutEvent();
			currentColumn = 0;
			for (String cellString : s.split(separator))//"\t"))
			{
				this.statTableModel.setValueAtWithoutEvent(cellString,
					currentRow, currentColumn);
				currentColumn++;
			}
			currentRow++;
		}

		this.importPasteDataMessage.setText(""); // clear fail message
		this.pasteDataArea.setText(""); // clear pasted text
		this.pasteDataDialog.hide();
		this.fireEvent(TableChangeEvent.IMPORT_DATA, -1);
	}

	/**
	 * Return the separator. Possible values are: "\t" or ";"
	 * 
	 * @param string
	 * @return
	 */
	private String getSeparator(String string)
	{
		String separator = "";
		
		if (string.indexOf("\t") > -1)
		{
			separator = "\t";
		}
		else if (string.indexOf(";") > -1)
		{
			separator = ";";
		}
		else
		{
			// set a default although it does not occur
			separator = "/t";
		}
		
		return separator;
	}

	/*
	 * Opent de dialoog voor het openen van een data bestand.
	 */
	private void openFileChooserDialog()
	{
		this.fileUploadDialogBox.center();
		this.fileUploadDialogBox.show();
	}

	/**
	 * Remove views except Table.
	 */
	private void removeViews()
	{
		ArrayList<StatistiekView> views = new ArrayList<StatistiekView>();
		
		// In edit-mode en standalone is statInteractiePanel null... De tabs blijven daar gewoon staan
		if (this.statInteractiePanel != null)
		{
			views = this.statInteractiePanel.getModel().getViews();
		
			for (int i = views.size() - 1; i >= 0; i--)
			{
	        	StatistiekView view = views.get(i);
	        	if (!view.getViewName().equals(this.viewName))
	        	{
	        		this.statInteractiePanel.getModel().removeView(view.getViewName());
	        	}
			}
		}
	}

	
	/*
	 * Create columns based on the names. 
	 */
	private void createColumns(String[] names)
	{
		//System.out.println("StatTable.createColumns(): " + names.toString());
		for (int i = 0; i < names.length; i++)
		{
    		this.statTableModel.addColumnWithoutEvent(names[i],
    			new ColumnType(AllowedTypes.STRING));
		}
	}

	/*
	 * Remove all columns. 
	 */
	private void removeColumns()
	{
		//System.out.println("StatTable.removeColumns()");
		for (int i = this.statTableModel.getColumnCount() - 1; i >= 0; i--)
		{
    		this.statTableModel.removeColumn(i);
		}
	}

	/*
	 * Clear the statTableModel.
	 */
	private void clearStatTableModel()
	{
		// statTable rij voor rij, kolom voor kolom leegmaken
		int numberOfRows = this.statTableModel.getRowCount();
		for (int i = numberOfRows - 1; i >= 0; i--)
		{
			//System.out.println("StatTable.clearStatTableModel(): remove row " + i);
			this.statTableModel.removeRowWithoutEvent(i);
		}

		int numberOfColumns = this.statTableModel.getColumnCount(); 
		for (int i = numberOfColumns - 1; i >=0; i--)
		{
			//System.out.println("StatTable.clearStatTableModel(): remove column " + i);
			this.statTableModel.removeColumnWithoutEvent(i);
		}
		
		// clear the selected set
		this.selectionModel.clear();
	}

	/*
	 * Process the CSV data file.
	 */
	private void processCSVDataFile(FileList files)
	{
		// read the files
		//GWT.log("number of files in read queue = " + files.getLength());
		for (File file : files)
		{
			readQueue.add(file);
		}
		
		// Start processing the queue
		readNextFile();
	}
	
	/**
	 * Process the CSV text in the table.
	 */
	private void processCSVText()
	{
		// process the first file: getStringResult() is only available onLoadEnd()!!
		this.csvText = this.reader.getStringResult();
		
		if (!(this.csvText == null) 
			&& !this.csvText.equals("") 
			&& !this.csvText.equals("Error"))
		{
			String[] lines = csvText.split("\\r?\\n"); 
			
			// check de situatie waarin csvText alleen \r als regelscheiding bevat
			if ((lines.length == 1) && csvText.contains("\r") && !csvText.contains("\r\n"))
			{
				// csvText has only \r as separator
				lines = csvText.split("\\r");
			}
			
			// check for an incorrect data file
			if (!this.isCorrectFormat(lines))
			{
				this.csvText = "";
				this.tableMessageLabel.setText(StatistiekGWT.rb.errorLoadingTable());
				this.table.setEmptyTableWidget(tableMessageLabel);
				
				return;
			}
			
			String separator = this.getSeparator(lines[0]);
			csvHeaders = lines[0].split(separator);

			dataRows = new ArrayList<String>();

			for (int i = 1; i < lines.length; i++)
			{
				dataRows.add(lines[i]);
			}
			
			// clear the old data
			this.clearStatTableModel();
			
			// create string columns from headers
			this.createColumns(csvHeaders);
			
			// add row data
			this.addDataRowsWithoutEvent(dataRows);
			
			this.statTableModel.updateNumericalColumnTypesWithoutEvent();
			
			this.fireEvent(TableChangeEvent.IMPORT_DATA, -1);
			
//			RootPanel.getBodyElement().getStyle().setProperty("cursor", "default");
		}
		else
		{
//			RootPanel.getBodyElement().getStyle().setProperty("cursor", "default");
			GWT.log("CSV file is empty!");
		}		
	}
	
	/**
	 * Checks whether the first two data lines are of equal length (so, excluding first header row 0,
	 * reading from row 1). 
	 * Used to check for a correct format for importing data.
	 * 
	 * @param lines
	 * @return
	 */
	private boolean isCorrectFormat(String[] lines)
	{
		boolean isCorrect = true;
		
		int length = 0;
		String separator;
		
		if (lines.length > 0)
		{
			separator = this.getSeparator(lines[0]);
		}
		else
		{
			separator = StatTable.DEFAULT_SEPARATOR;
		}
		
		for (int i = 1; (i < lines.length) && (i <= 2); i++)
		{
			int lengthRow = lines[i].split(separator, -1).length;
			
			if (i == 1)
			{
				length = lengthRow;
			}
			
			if (lengthRow != length)
			{
				isCorrect = false;
				break;
			}
		}
		
		return isCorrect;
	}

	/**
	 * Make statTableModel fire a table change event.
	 * 
	 * @param info
	 * 		The type of table change that has been done.
	 * @param columnIndex
	 * 		The columnIndex that is effected by the table change event. 
	 * 		If not applicable the index is -1.
	 */
	private void fireEvent(String info, int columnIndex)
	{
		// send an event
		TableChangeEvent event = new TableChangeEvent(info, columnIndex);
		this.statTableModel.fireEvent(event);
	}

	/**
	 * Processes the next CSV file in the queue.
	 */
	private void readNextFile()
	{
		GWT.log("StatTable.readNextFile(): readQueue.size() = " + readQueue.size());
		if (readQueue.size() > 0)
		{
			File file = readQueue.get(0);
			String type = file.getType();
			String name = file.getName();
			
			try
			{
				if (type.startsWith(StatTable.CSV_FROM_EXCEL_FILE_TYPE) || type.startsWith(StatTable.CSV_FROM_EXCEL_FILE_TYPE2)
					|| (type.equals("") && name.endsWith(".csv"))) // voor chromebook
				{
					// If the file is larger than 1kb, read only the first 1000
					// characters to demonstrate file slicing
					blob = file;
//					if (file.getSize() > 0)
//					{
//						blob = file.slice(0, 1000, "application/vnd.ms-excel; charset=utf-8");//text/csv//text/plain
//					}

					reader.readAsText(blob);
					
					// the result reader.getStringResult() is available in LoadEndHandler.onLoadEnd()
				}
				else
				{
					GWT.log("type = " + type);
					readQueue.remove(0);
					readNextFile();
				}
			}
			catch (Throwable t)
			{
				// Necessary for FF (see bug
				// https://bugzilla.mozilla.org/show_bug.cgi?id=701154)
				// Standard-complying browsers will not go in this branch
				handleError(file, t.getMessage() + ", " + t.getStackTrace());
				readQueue.remove(0);
				readNextFile();
			}
		}
	}

	private void handleError(File file, String message)
	{
		FileError error = reader.getError();
		String errorDesc = "";
		if (error != null)
		{
			ErrorCode errorCode = error.getCode();
			if (errorCode != null)
			{
				errorDesc = ": " + errorCode.name();
			}
		}
		Window.alert("File loading error for file: " + file.getName() + "\n"
			+ errorDesc + ", message: " + message);
	}
	
	/**
	 * Add the row data to the table.
	 * Used in processCSVText()
	 * @param dataRows
	 */
	private void addDataRowsWithoutEvent(ArrayList<String> dataRows)
	{
        Iterator<String> rowIterator = dataRows.iterator();
        String dataRow = "";
        int i = 0;
        
        try
        {
	        while (rowIterator.hasNext()) 
	        {
	        	i++;
	        	dataRow = rowIterator.next();
	
	        	String[] values = dataRow.split(";", -1);
	         	this.replaceMissingValues(values);
	        	ArrayList<Object> valuesList= new ArrayList<Object>(Arrays.asList(values));
	        	
	        	this.statTableModel.addRowWithoutEvent(valuesList);
	        }
        }
        catch (Exception e)
        {
        	System.out.println("StatTable.addDataRowsWithoutEvent(): datarow = " + dataRow + ", " + e.toString());
        }

        // finally sort the string options all at once, for performance reason
        this.statTableModel.sortStringOptions();
	}

	/**
	 * Replace empty values with missing value wildcard.
	 * @param values
	 */
	private void replaceMissingValues(String[] values)
	{
		for (int i = 0; i < values.length; i++)
		{
			if (values[i].equals(""))
			{
				values[i] = ColumnType.WILDCARD;
			}
		}
	}

	public Object getState()
	{
		return this.viewName;
	}

	public void setState(Object state)
	{
		if (state instanceof String)
		{
			this.viewName = (String) state;
		}
	}

	public String getViewType()
	{
		return "Table";
	}

	/**
	 * De selectie uit StatTableModel wordt in de tabel gezet
	 */
	public void setSelectionFromModelInTable()
	{
		GWT.log("StatTable.setSelectionFromModelInTable()");

		List<List<String>> list = (List<List<String>>) this.dataProvider.getList();

		for (int row = 0; row < this.statTableModel.getRowCount(); row++)
		{
			List<String> rowObject = list.get(row);
			
			if (this.statTableModel.isRowSelected(row)
				&& !selectionModel.isSelected(rowObject))
			{
				selectionModel.setSelected(rowObject, true);
			}
			else if (!this.statTableModel.isRowSelected(row)
				&& selectionModel.isSelected(rowObject))
			{
				selectionModel.setSelected(rowObject, false);
			}
		}
	}

	public String toString()
	{
		return this.getViewName();
	}

	public StatInteractiePanel getStatInteractiePanel()
	{
		return statInteractiePanel;
	}

	public void setStatInteractiePanel(StatInteractiePanel statInteractiePanel)
	{
		this.statInteractiePanel = statInteractiePanel;
	}

	class StatTableMouseUpHandler implements MouseUpHandler
	{

		@Override
		public void onMouseUp(MouseUpEvent event)
		{
			isMouseDown = false;
		}
		
	} // class StatTableMouseUpHandler
	
	class StatTableClickHandler implements ClickHandler//TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		private FileList fileList;
		
		@Override
		public void onClick(ClickEvent e)
		{
			if (e.getSource() == StatTable.this.addRowButton)
			{
				StatTable.this.statTableModel.addRow();
			}
			else if (e.getSource() == StatTable.this.addColumnButton)
			{
				ColumnDialogModel dialogModel = new ColumnDialogModel(
					StatTable.this.statTableModel);
				HandlerRegistration handlerRegistration = dialogModel.addAddColumnEventHandler(StatTable.this.statTableModel);
				
				ColumnDialogView dialogView;

				dialogView = new ColumnDialogView(dialogModel, StatistiekGWT.rb.addacolumn());
				
				ColumnDialogController dialogController = new ColumnDialogController(
					dialogModel, dialogView);
				dialogController.setAddColumnHandlerRegistration(handlerRegistration);

				dialogView.center();
				dialogView.show();
			}
			else if (e.getSource() == StatTable.this.deleteRowsButton)
			{
				int[] toRemove = StatTable.this.getSelectedRows();
				if (toRemove.length > 0)
				{
					for (int i = toRemove.length - 1; i >= 0; i--)
					{
						StatTable.this.statTableModel.removeRowWithoutEvent(toRemove[i]);
					}
					
					// reset selection
					StatTable.this.clearSelectionModel();
					
					// send an event
					TableChangeEvent event = new TableChangeEvent(TableChangeEvent.REMOVE_ROWS, -1);
					StatTable.this.statTableModel.fireEvent(event);
				}
			}
			else if (e.getSource() == StatTable.this.pasteButton)
			{
				StatTable.this.pasteDataDialog.show();
			}
			else if (e.getSource() == StatTable.this.copyButton)
			{
				StatTable.this.copyAction();
			}
			else if (e.getSource() == StatTable.this.importPasteDataButton)
			{
				StatTable.this.importPasteData();
			}
			else if (e.getSource() == StatTable.this.cancelPasteDataButton)
			{
				StatTable.this.pasteDataDialog.hide();
			}
			else if (e.getSource() == StatTable.this.resetButton)
			{
				if (StatTable.this.statInteractiePanel != null)
				{
	    			HashMap resetHashMap = StatTable.this.statInteractiePanel.getModel()
	    				.getResetHashMap();
	    			
	    			// clear stringFrequencies
	    			StatTable.this.getStatTableModel().clearStringFrequencies();

	    			// clear selectionList and listeners
	    			StatTable.this.getStatTableModel().clearSelectionList();
	    			
	    			// clear the cellOutlierList and rowOutlierList
	    			StatTable.this.getStatTableModel().clearOutlierLists();
	    			
	    			// remove views (and their occurrences as handler)
	    			statInteractiePanel.getStatModel().removeViews();
	    
	    			// Complete reset met zetOpdracht()
	    			StatTable.this.statInteractiePanel.getView().getController()
	    				.zetOpdracht(resetHashMap, null, null);
	    			
	    			int selectedView = statInteractiePanel.getSelectedView();
	    			statInteractiePanel.getView().updateView(selectedView);
	    			
				} // else the button is clicked in edit-mode: do nothing
			}
			else if (e.getSource() == StatTable.this.importButton)
			{
				if (StatTable.this.statTableModel.getRowCount() > 0)
				{
					importBox.center();
		            importBox.show();
				}
				else
				{
					openFileChooserDialog();
				}
			}
			else if (e.getSource() == StatTable.this.importBox.okButton)
			{
				importBox.hide();
				openFileChooserDialog();
			}
			else if (e.getSource() == StatTable.this.importBox.cancelButton)
			{
				importBox.hide();
			}
			else if (e.getSource() == StatTable.this.messageBox.closeButton)
			{
				messageBox.hide();
			}
			else if (e.getSource() == StatTable.this.fileUploadImportButton)
			{
				fileList = StatTable.this.fileUpload.getFiles();

				String fileType = "";
				
				if (fileList.getLength() > 0)
				{
					fileType = fileList.getItem(0).getType();
					if (fileType.startsWith(StatTable.CSV_FROM_EXCEL_FILE_TYPE) || fileType.startsWith(StatTable.CSV_FROM_EXCEL_FILE_TYPE2)
						|| (fileType.equals("") && fileList.getItem(0).getName().endsWith(".csv"))) // voor chromebook
					{
						statInteractiePanel.getView().addStyleName(statistiekCss.waitCursor());
						table.addStyleName(statistiekCss.waitCursor());
	
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	
							@Override
							public void execute() 
							{
								clearStatTableModel();
								
								table.setEmptyTableWidget(new Label(StatistiekGWT.rb.loadingTable()));
								
								// Remove old views except table
								StatTable.this.removeViews();
								
								if (StatTable.this.statInteractiePanel != null)
								{
									StatTable.this.statInteractiePanel.getView().removeViewTabsExceptTable();
								}
								
								// update to see the empty table while loading
								update();
								StatTable.this.fileUploadDialogBox.hide();
	//							table.addStyleName(statistiekCss.waitCursor());
															
								Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	
									@Override
									public void execute() 
									{
										StatTable.this.processCSVDataFile(fileList);
										
										statInteractiePanel.getView().removeStyleName(statistiekCss.waitCursor());
										table.removeStyleName(statistiekCss.waitCursor());
									}
								});
							}
						});
					}
					else
					{
						// TODO clear fileUpload selected file
						
						messageBox.setMessage(StatistiekGWT.rb.noCSVMessage());
						messageBox.center();
						messageBox.show();
					}
				}
				else
				{
					// no file selected
					messageBox.setMessage(StatistiekGWT.rb.noFileMessage());
					messageBox.center();
					messageBox.show();
				}
			}
			else if (e.getSource() == StatTable.this.fileUploadCancelButton)
			{
				StatTable.this.fileUploadDialogBox.hide();				
			}
		} // onClick()

	} // class StatTableClickHandler
	
	class StatTableColumn extends Column 
		implements Comparator
	{
		int columnIndex;
		ColumnType type;

		public StatTableColumn(TextInputCell cell, ColumnType type)
		{
			super(cell);
			this.columnIndex = StatTable.this.getTempColumnIndex();
			// type can be numerical or string
			this.type = type;
			//this.setCellStyleNames(statistiekCss.dataGridCell());
		}

		public StatTableColumn(TextCell cell, ColumnType type)
		{
			super(cell);
			this.columnIndex = StatTable.this.getTempColumnIndex();
			// type can be numerical or string
			this.type = type;
			//this.setCellStyleNames(statistiekCss.dataGridCell());
		}

		public StatTableColumn(SelectionCell enumCell, ColumnType type)
		{
			super(enumCell);
			this.columnIndex = StatTable.this.getTempColumnIndex();
			// a selectioncell is always type enum
			this.type = type;
			//this.setCellStyleNames(statistiekCss.dataGridCell());
		}

		public int getColumnIndex()
		{
			return this.columnIndex;
		}

		@Override
		public Object getValue(Object object)
		{
			return object == null ? "" : ((List<String>) object).get(this.columnIndex);
		}

		@Override
		public int compare(Object o1, Object o2)
		{
			int returnValue = type.compare(((List<String>) o1).get(this.columnIndex), ((List<String>) o2).get(this.columnIndex));
			
			return returnValue;
		}
		
	} // class StatTableColumn
	

	private static class StatTableTextHeader extends TextHeader
	{

		private ClickHandler handler;

		public StatTableTextHeader(String text, ClickHandler handler)
		{
			super(text);
			this.handler = handler;
		}

		@Override
		public void onBrowserEvent(Context context, final Element elem,
			final NativeEvent event)
		{
			//System.out.println("StatTable.StatTableTextHeader.onBrowserEvent()");
			
			// maybe hijack click event
			if (handler != null)
			{

				if (Event.ONCLICK == Event.getTypeInt(event.getType()))
				{

					handler.onClick(new ClickEvent()
					{
						{
							setNativeEvent(event);
							setRelativeElement(elem);
							setSource(StatTableTextHeader.this);
						}
					});
				}
			}

			// default dom event handler
			super.onBrowserEvent(context, elem, event);
		}
		
	} // class StatTableTextHeader
	
	
	/**
	 * A dialogbox that shows a warning message that the imported data
	 * will overwrite existing data and remove views.  
	 * 
	 * @author Sylvia van Borkulo
	 *
	 */
	class ImportMessageDialogBox extends DialogBox
	{
		private Label message = new Label();
		private Button okButton = new Button(
			StatistiekGWT.rb.oKButtonText());
		private Button cancelButton = new Button(
			StatistiekGWT.rb.cancelButtonText());

		public ImportMessageDialogBox(Label label)
		{
			super();
			setTitle(label.getText());
			message.setText(StatistiekGWT.rb.importWarning());
			okButton.addClickHandler(StatTable.this.clickHandler);
			okButton.addStyleName(statistiekCss.margin());
			cancelButton.addClickHandler(StatTable.this.clickHandler);
			cancelButton.addStyleName(statistiekCss.margin());

			HorizontalPanel buttonPanel = new HorizontalPanel();
			buttonPanel.add(okButton);
			buttonPanel.add(cancelButton);

			VerticalPanel vPanel = new VerticalPanel();
			vPanel.add(message);
			vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			vPanel.add(buttonPanel);

			setWidget(vPanel);
		}
	} // class ImportMessageDialogBox
	
	class MessageDialogBox extends DialogBox
	{
		private Label message = new Label();
		private Button closeButton = new Button(
			StatistiekGWT.rb.closeButtonText());

		public MessageDialogBox(Label title)
		{
			super();
			setTitle(title.getText());
			closeButton.addClickHandler(StatTable.this.clickHandler);

			HorizontalPanel buttonPanel = new HorizontalPanel();
			buttonPanel.setWidth("100%");
			buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			buttonPanel.add(closeButton);

			VerticalPanel vPanel = new VerticalPanel();
			vPanel.add(message);
			//vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			vPanel.add(buttonPanel);

			setWidget(vPanel);
		}
		
		public void setMessage(String text)
		{
			this.message.setText(text);
		}
		
	} // class MessageDialogBox
	
	/**
	 * Textarea that fires a value change event when text is pasted.
	 * 
	 * @author Sylvia van Borkulo
	 *
	 */
	private class ExtendedTextArea extends TextArea 
	{

	    public ExtendedTextArea()
	    {
	        super();
	        sinkEvents(Event.ONPASTE);
	    }

		@Override
		public void onBrowserEvent(Event event)
		{
			super.onBrowserEvent(event);
			
			switch (DOM.eventGetType(event))
			{
				case Event.ONPASTE:
					Scheduler.get().scheduleDeferred(new ScheduledCommand()
					{

						@Override
						public void execute()
						{
							ValueChangeEvent.fire(ExtendedTextArea.this, getText());
						}

					});
					break;
			}
		}
	} // class ExtendedTextArea
	
	/**
	 * Class for setting the style of TextInputCell, else the width of the table column
	 * will not effect the cell's width.
	 * 
	 * @author Sylvia van Borkulo
	 *
	 */
	static class StatTableInputCell extends TextInputCell
	{
	    private static Template template;
	    private ColumnType type;
		private int columnWidth;
		private boolean editable;
		private StatTable statTable;

	    interface Template extends SafeHtmlTemplates
	    {   
	    	// {0}, {1}, {2} relate to value, style, color
	        @Template("<input type=\"text\" value=\"{0}\" tabindex=\"-1\" style=\"{1}\"></input>")
	        SafeHtml input(String value, String style);
	    }

	    public StatTableInputCell(ColumnType type, int columnWidth, boolean editable, StatTable statTable)
	    {
	        template = GWT.create(Template.class);
	        this.type = type;
	        this.columnWidth = columnWidth;
	        this.editable = editable;
	        this.statTable = statTable;
	    }

	    @Override
	    public void render(Context context, String value, SafeHtmlBuilder sb)
	    {
	        // Get the view data.
	        Object key = context.getKey();
	        ViewData viewData = getViewData(key);
	        if (viewData != null && viewData.getCurrentValue().equals(value))
	        {
	            clearViewData(key);
	            viewData = null;
	        }

	        String s = (viewData != null) ? viewData.getCurrentValue() : value;
			if (s != null)
			{
				if (this.type.getType().equals(AllowedTypes.DOUBLE))
				{
					// Get the string value with language dependent separator
					s = StatistiekGWT.getStringValue(s);
				}
				
				String colorString = "color: black";// default
				
				if (statTable.getStatTableModel().isOutlier(context.getIndex(), context.getColumn() - 2))
				{
					String rgbString = ColorUtils.getOutlierColor().value();
					colorString = "color: " + rgbString;
				}
				
				String styleString = "width: " + this.columnWidth
					+ "px; " + StatTable.CELL_STYLE_FONT_SIZE + ";" + colorString;
				// set value, style
				sb.append(template.input(s, styleString));
			}
	        else
	        {
	            sb.appendHtmlConstant("<input type=\"text\" tabindex=\"-1\"></input>");
	        }
	    } // render()
	    
	    @Override
        public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) 
        {
            if (!this.editable)
            {
                event.preventDefault();
            }
            else
            {
            	//Window.alert("StatTableInputCell.onBrowserEvent()");
                super.onBrowserEvent(context, parent, value, event, valueUpdater);
            }
        } // onBrowserEvent()
	    
	} // class StatTableInputCell

	
	/**
	 * Class for setting the style of TextCell, else the width of the table column
	 * will not effect the cell's width.
	 * 
	 * @author Sylvia van Borkulo
	 *
	 */
	static class StatTableTextCell extends TextCell
	{
	    private static Template template;
	    private ColumnType type;
		private int columnWidth;
		private StatTable statTable;

	    interface Template extends SafeHtmlTemplates
	    {   
	    	// {0}, {1} relate to value, style
	        @Template("<div style=\"{1}\">{0}</div>")
	        SafeHtml cell(String value, String style);
	    }

	    public StatTableTextCell(ColumnType type, int columnWidth, StatTable statTable)
	    {
	        template = GWT.create(Template.class);
	        this.type = type;
	        this.columnWidth = columnWidth;
	        this.statTable = statTable;
	    }

	    @Override
	    public void render(Context context, String value, SafeHtmlBuilder sb)
	    {
			if (value != null)
			{
				if (this.type.getType().equals(AllowedTypes.DOUBLE))
				{
					// Get the string value with language dependent separator
					value = StatistiekGWT.getStringValue(value);
				}

				String colorString = "color: black";// default
				
				if (statTable.getStatTableModel().isOutlier(context.getIndex(), context.getColumn() - 2))
				{
					String rgbString = ColorUtils.getOutlierColor().value();
					colorString = "color: " + rgbString;
				}
				
				String styleString = "width: " + this.columnWidth
					+ "px; " + StatTable.CELL_STYLE_FONT_SIZE + ";" + colorString;
				// set value, style
//				sb.append(template.cell(value, "width: " + this.columnWidth
//					+ "px; " + StatTable.CELL_STYLE_FONT_SIZE));
				sb.append(template.cell(value, styleString));
			}
	        else
	        {
	            sb.appendHtmlConstant("<input type=\"text\" tabindex=\"-1\"></input>");
	        }
	    } // render()

	    
	} // class StatTableTextCell

	
	/**
	 * Class for setting the style of SelectionCell, else the width of the table column
	 * will not effect the cell's width.
	 * 
	 * @author borku102
	 *
	 */
	static class StatTableSelectionCell extends SelectionCell
	{
		private static Template template;
		private HashMap<String, Integer> indexForOption = new HashMap<String, Integer>();
		private final List<String> options;
		private StatTable statTable;

		interface Template extends SafeHtmlTemplates
		{
			// {0} relates to style
			@Template("<select tabindex=\"-1\" style=\"{0}\">")
			SafeHtml beginSelect(String style);

			// {0} relates to style
//			@Template("<select tabindex=\"-1\" style=\"font-size: 14px\">")//0.875em\">")//14px\">")//
//			SafeHtml beginSelect();

			@Template("<option value=\"{0}\">{1}</option>")
	        SafeHtml deselected(int hash, String option);

	        @Template("<option value=\"{0}\" selected=\"selected\">{1}</option>")
	        SafeHtml selected(int hash, String option);

			@Template("</select>")
			SafeHtml endSelect();
		}

		public StatTableSelectionCell(List<String> options, StatTable statTable)
		{
			super(options);
			template = GWT.create(Template.class);

			this.options = new ArrayList<String>(options);
			int index = 0;
			for (String option : options)
			{
				indexForOption.put(option, index++);
			}
			this.statTable = statTable;
		}

		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb)
		{
			String colorString = "color: black";// default
			
			if (statTable.getStatTableModel().isOutlier(context.getIndex(), context.getColumn() - 2))
			{
				String rgbString = ColorUtils.getOutlierColor().value();
				colorString = "color: " + rgbString;
			}
			
			String styleString = StatTable.CELL_STYLE_FONT_SIZE + ";" + colorString;
			
			// set style
//			sb.append(template.beginSelect(StatTable.CELL_STYLE_FONT_SIZE));
			sb.append(template.beginSelect(styleString));

			for (int i = 0; i < options.size(); i++)
			{
				String item = options.get(i);
				if (item.equals(value))
				{
					sb.append(template.selected(i, item));
				}
				else
				{
					sb.append(template.deselected(i, item));
				}
			}
			
			sb.append(template.endSelect());
		} // render()
		
	} // class StatTableSelectionCell
	
	
	/**
	 * Class for setting the style of CheckboxCell, else the width of the table column
	 * will not effect the cell's width.
	 * 
	 * @author Sylvia van Borkulo
	 *
	 */
	static class StatTableCheckboxCell extends CheckboxCell
	{
		private static Template template;

		interface Template extends SafeHtmlTemplates
		{
	    	// {0}, {1} relate to value, style
	        @Template("<input type=\"checkbox\" value=\"{0}\" tabindex=\"-1\" style=\"{1}\"></input>")
	        SafeHtml input(String value, String style);
	        
	    	// {0} relates to style
	        @Template("<input type=\"checkbox\" tabindex=\"-1\" style=\"{0}\" checked/>")
	        SafeHtml inputChecked(String style);

	    	// {0} relates to style
	        @Template("<input type=\"checkbox\" tabindex=\"-1\" style=\"{0}\"/>")
	        SafeHtml inputUnchecked(String style);
		}

		public StatTableCheckboxCell(boolean dependsOnSelection, boolean handlesSelection)
		{
			super(dependsOnSelection, handlesSelection);
			template = GWT.create(Template.class);
		}

		@Override
		public void render(Context context, Boolean value, SafeHtmlBuilder sb)
		{
	        // Get the view data.
	        Object key = context.getKey();
	        Boolean viewData = getViewData(key);
	        if (viewData != null && viewData.equals(value))
	        {
	            clearViewData(key);
	            viewData = null;
	        }

	        Boolean b = (viewData != null) ? viewData : value;

	        // set value, style
	        if (b.booleanValue())
	        {
	        	sb.append(template.inputChecked(StatTable.CHECKBOX_CELL_WIDTH_STYLE));
	        }
	        else
	        {
	        	sb.append(template.inputUnchecked(StatTable.CHECKBOX_CELL_WIDTH_STYLE));
	        }
		}

	} // class StatTableSelectionCell
	
	/**
	 * Class to provide access to the data grid's scroll panel.
	 * 
	 * @author Sylvia van Borkulo
	 *
	 * @param <T>
	 */
	class StatTableDataGrid<T> extends DataGrid<T>
	{
		public StatTableDataGrid(ProvidesKey<T> keyProvider)
		{
			super(keyProvider);
		}

		 /**
		  * Method to access the data grid's scrollpanel.
		  * 
		  * @return
		  */
		public ScrollPanel getScrollPanel()
		{
			HeaderPanel header = (HeaderPanel) getWidget();
			return (ScrollPanel) header.getContentWidget();
		}
	} // class StatTableDataGrid

	
	public class StatTablePager extends SimplePager
	{

		public StatTablePager(TextLocation center, Resources pagerResources,
			boolean b, int i, boolean c)
		{
			super(TextLocation.CENTER, pagerResources, false, 0, true);
		}

		protected String createText()
		{
			String text;
			
			if (getDisplay().getRowCount() == 0)
			{
				if (StatistiekGWT.getLanguage().equals("nl"))
				{
					text = "0 van 0";
				}
				else
				{
					// default is English
					text = "0 of 0";
				}
			}
			else
			{
				text = super.createText();
			}
			
			if (StatistiekGWT.getLanguage().equals("nl"))
			{
				String replacement = "van";
				text = text.replaceAll("of", replacement);
			}

			return text;
		} 
	} // class StatTablePager

	@Override
	public void onTableChange(TableChangeEvent event)
	{
		GWT.log("StatTable.onTableChange()");
		
		if (event.getInfo().equals(TableChangeEvent.DATA_EDITABLE))
		{
			int size = this.statTableModel.isDataEditable() ? StatistiekGWT.BUTTON_HEIGHT : 0;
			super.setWidgetSize(this.editDataPanel, size);
		}
		else if (event.getInfo().equals(TableChangeEvent.SET_COLUMN_NAME))
		{
			this.updateWidth(event.getColumnIndex());
		}
		else if (event.getInfo().equals(TableChangeEvent.ADD_COLUMN))
		{
			this.addWidth();

			this.update();
			
			this.editColumnIndex = event.getColumnIndex();
			this.scrollEditColumnIntoView();

			return;
		}
		else if (event.getInfo().equals(TableChangeEvent.EDIT_COLUMN))
		{
			this.updateWidth(event.getColumnIndex());

			this.update();

			this.editColumnIndex = event.getColumnIndex();
			this.scrollEditColumnIntoView();
			
			return;
		}
		else if (event.getInfo().equals(TableChangeEvent.ADD_ROW))
		{
			if (this.statTableModel.getRowCount() == 1) // first row added
			{
				this.updateRowNumberWidth();
				// all maxColumn/CellWidths must be recalculated
				updateWidth();
			}
			else
			{
				this.updateColumnWidth(this.statTableModel.getColumnCount() - 1); // update the last column in case scrollbar appeared

				this.update();
				
				if (StatTable.this.pager.isVisible())
				{
					// show the row added
					StatTable.this.pager.lastPage();
				}
				//int lastIndex = StatTable.this.statTableModel.getRowCount() - 1;
				int lastIndex = StatTable.this.table.getVisibleItemCount() - 1;
				//StatTable.this.table.getRowElement(lastIndex).scrollIntoView(); // scrolls to the right
				StatTable.this.table.getRowElement(lastIndex).getCells().getItem(0).scrollIntoView();
				
				// update has been done, so return
				return;
			}
		}
		else if (event.getInfo().equals(TableChangeEvent.IMPORT_DATA)) // import data from file
		{
			this.updateRowNumberWidth();
			initializeWidth();
		}
		else if (event.getInfo().equals(TableChangeEvent.SET_VALUE_AT))
		{
			this.updateWidth(event.getColumnIndex());
			
			this.update();

			this.editColumnIndex = event.getColumnIndex();
			this.scrollEditColumnIntoView();
			
			return;
		}
		else if (event.getInfo().equals(TableChangeEvent.REMOVE_COLUMN))
		{
			removeWidth();
			updateWidthFromIndex(event.getColumnIndex());
		}
		else if (event.getInfo().equals(TableChangeEvent.REMOVE_ROW) || event.getInfo().equals(TableChangeEvent.REMOVE_ROWS))
		{
			this.updateRowNumberWidth();
			this.updateColumnWidth(this.statTableModel.getColumnCount() - 1); // update the last column in case scrollbar disappeared
		}

		this.update();
	}

	/**
	 * Copy the table content in CSV format to another widget via cross widget communication.
	 */
	public void copyAction()
	{
		int rows = statTableModel.getRowCount();
		int cols = statTableModel.getColumnCount();
		StringBuilder sb = new StringBuilder();
		final char eol = '\n';
		final char eod = ';';
		for (int i = 0; i < rows; i++)
		{
			char sep = eol;
			for (int j = 0; j < cols; j++)
			{
				sb.append(sep);
				sb.append(statTableModel.getValueAt(i, j));
				sep = eod;
			}
		}
		sb.append(eol);
		final String data = sb.substring(1);
		System.err.println(sb);
		statInteractiePanel.statistiekGWT.fire("text.csv", "content", data);
	}

	/**
	 * Update the values for column header width, maximum cell width,
	 * the actual cell width and actual column width 
	 * for the columns with index the given column index or larger in the table.
	 *  
	 * @param fromIndex
	 */
	private void updateWidthFromIndex(int fromIndex)
	{
		this.updateColumnHeaderWidthFromIndex(fromIndex);
		this.updateMaxCellWidthFromIndex(fromIndex);
		this.updateCellWidthFromIndex(fromIndex);
		this.updateColumnWidthFromIndex(fromIndex);
	}

	/**
	 * Remove item from the end of the arrays for column header width, max cell width,
	 * the actual cell width and the actual column width.
	 */
	private void removeWidth()
	{
		this.removeColumnHeaderWidth();
		this.removeMaxCellWidth();
		this.removeCellWidth();
		this.removeColumnWidth();
	}

	/**
	 * Add an item to the arrays for column header width, max cell width,
	 * the actual cell width and the actual column width.
	 */
	private void addWidth()
	{
		this.addColumnHeaderWidth();
		this.addMaxCellWidth();
		this.addCellWidth();
		this.addColumnWidth();
	}

	/**
	 * Update the values for column header width and maximum cell width,
	 * and the actual cell and column width for each column.
	 */
	private void updateWidth()
	{
		this.updateColumnHeaderWidth();
		this.updateMaxCellWidth();
		this.updateCellWidth();
		this.updateColumnWidth();
	}

	/**
	 * Update the values for column header width and maximum cell width,
	 * and the actual cell and column width for each column.
	 * 
	 * @param columnIndex
	 */
	private void updateWidth(int columnIndex)
	{
		this.updateColumnHeaderWidth(columnIndex);
		this.updateMaxCellWidth(columnIndex);
		this.updateCellWidth(columnIndex);
		this.updateColumnWidth(columnIndex);
	}

	/**
	 * Initialize the values for column header width and maximum cell width,
	 * and the actual cell and column width for each column.
	 *
	 */
	private void initializeWidth()
	{
		this.initializeColumnHeaderWidth();
		this.initializeMaxCellWidth();
		this.initializeCellWidth();
		this.initializeColumnWidth();
	}
	
	private void scrollEditColumnIntoView()
	{
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() 
			{
				if (StatTable.this.statTableModel.getRowCount() > 0)
				{
					try
					{
						if (StatTable.this.table.getRowElement(0).getCells().getItem(editColumnIndex + 2) != null)
						{
							StatTable.this.table.getRowElement(0).getCells().getItem(editColumnIndex + 2).scrollIntoView(); // + 2 voor rownumber en check columns; set wel de horizontalScrollPosition, maar behoudt het niet...
						}
					}
					catch(Exception e)
					{
						logger.log(Level.INFO, "Error while scrolling column into view.");
					}
				}
			}
		});

	}

	/**
	 * Add empty table when importing data.
	 */
	public void addEmptyTable()
	{
		String t = StatistiekGWT.VIEWS[0];

		StatistiekView statistiekView = StatistiekGWT.createView(t,
			this.statInteractiePanel.getStatModel().findUniqueViewName(t), 
			this.statTableModel,
			0, 0, this.statInteractiePanel);
		this.statInteractiePanel.getStatModel().addView(statistiekView);
		this.statInteractiePanel.getView().selectLastTab();
		this.statInteractiePanel.getView().clearAddViewTab();
		
		((StatTable) statistiekView).table.setEmptyTableWidget(new Label(StatistiekGWT.rb.loadingTable()));
	}

	/**
	 * Get the array with column header widths for the data columns (row number column
	 * and checkbox column excluded).
	 * 
	 * @return
	 */
	public int[] getColumnHeaderWidth()
	{
		return this.columnHeaderWidth;
	}

	/**
	 * Clear the selection model so that none items are selected.
	 */
	public void clearSelectionModel()
	{
		this.selectionModel.clear();
	}

	/**
	 * Update the 'Table' view
	 */
	public void update()
	{
		GWT.log("StatTable.update()");
		
		ArrayList<ArrayList<Object>> values = this.statTableModel.getValues();
		
		ArrayList<List<String>> rows = new ArrayList<List<String>>();
		
		for (int i = 0; i < values.size(); i++) // i loops over the rows
		{
			List<String> row = new ArrayList<String>();
			for (int j = 0; j < values.get(0).size(); j ++) // j loops over the columns
			{
				row.add(values.get(i).get(j).toString());
			}

			row.add("" + i); // add row number to the end as a key
			rows.add(row);
		}
		
		this.updateColumns();
		
		// add the data
		this.dataProvider.getList().clear();
		this.dataProvider.getList().addAll(rows);
		this.dataProvider.refresh();
		this.dataProvider.flush();
		this.table.setRowCount(rows.size()); // nodig anders gaat table oude data renderen...
		
		if ((rows.size() == 0) || (rows.size() <= TABLE_PAGE_SIZE))
		{
			this.tablePanel.setWidgetHidden(this.pagerPanel, true);
		}
		else
		{
			this.tablePanel.setWidgetHidden(this.pagerPanel, false);
		}
		
		if (rows.size() == 0)
		{
			tableMessageLabel.setText(StatistiekGWT.rb.emptyTableMessage());
			table.setEmptyTableWidget(tableMessageLabel);
		}
		
		this.table.redraw(); // nodig om te tonen in tabLayoutPanel
		
		setSelectionBackground(); // dit moet op het eind anders toont tabel default selectiekleur na tabwissel
	}

	/**
	 * Set the selection background of the table.
	 */
	private void setSelectionBackground()
	{
		int[] list = this.getSelectedRows();
		
		for (int i = 0; i < list.length; i++)
		{
			this.table.getRowElement(list[i]).getStyle().setBackgroundColor(ColorUtils.SELECTION_COLOR_TABLE);
		}
	}

	/**
	 * Remove and create the columns in the table.
	 */
	private void updateColumns()
	{
		if (this.statInteractiePanel != null)
		{
			this.setWidth(this.statInteractiePanel.getWidth());
		}
		
		// remove columns
		for (int i = this.table.getColumnCount() - 1; i >= 0; i--)
		{
			this.table.removeColumn(i);
		}

		// Checkbox column. This table will uses a checkbox column for
		// selection.
		// Alternatively, you can call dataGrid.setSelectionEnabled(true) to
		// enable mouse selection. [METHOD NOT AVAILABLE??]
		
		// variable to determine the table's width
		int totalWidth = 0;
		
		// if there are rows and/or columns add the row number column and check column 
		if ((this.statTableModel.getColumnCount() > 0)
			|| (this.statTableModel.getRowCount() > 0))
		{
			// row number column
			TextColumn<List<String>> rowNumberColumn = new TextColumn<List<String>>() {

			    @Override
			    public String getValue(List<String> s) {
			        return Integer.toString(dataProvider.getList().indexOf(s) + 1);
			    }
			};
			
			rowNumberColumn.setCellStyleNames(statistiekCss.datagridcell());
			
			//			this.table.addColumn(rowNumberColumn); // de (lege) header van rijnummerkolom heeft ongewenste border-bottom 2px
			TextCell emptyCell = new TextCell();
			Header<String> emptyHeader = new Header<String>(emptyCell) 
			{
				@Override
				public String getValue()
				{
					return null;
				}
			};
			
			emptyHeader.setHeaderStyleNames(statistiekCss.datagridcell());
			this.table.addColumn(rowNumberColumn, emptyHeader);
			this.table.setColumnWidth(rowNumberColumn, this.rowNumberWidth, Unit.PX);
			// add the column's width to total width
			totalWidth = totalWidth + this.rowNumberWidth;

			
			// check column
			StatTableCheckboxCell cell = new StatTableCheckboxCell(true, true);
	
			Column<List<String>, Boolean> checkColumn = new Column<List<String>, Boolean>(
				cell)// false))
			{
				@Override
				public Boolean getValue(List<String> s)
				{
					// Get the value from the selection model.
					// Add row number to s
					int rowIndex = dataProvider.getList().indexOf(s);
	
					// add rowIndex to s if necessary
					if (s.size() == table.getColumnCount() - 2) // table has extra row number and checkbox column, so -2
					{
						s.add(String.valueOf(rowIndex));
					}
					return selectionModel.isSelected(s);
				}
			};
			
			checkColumn
				.setFieldUpdater(new FieldUpdater<List<String>, Boolean>()
				{
					@Override
					public void update(int rowIndex, List<String> s,
						Boolean value)
					{
						// setSelectionList van StatTableModel 
						StatTable.this.statTableModel.setSelected(rowIndex, value, SelectionChangeEvent.STAT_TABLE);
						selectionModel.setSelected(s, value);
						
						setSelectionBackground();
					}
				});
			
			checkColumn.setCellStyleNames(statistiekCss.datagridcell());
			
			Header<Boolean> selectAllHeader = new Header<Boolean>(
				new StatTableCheckboxCell(true, true)) 
				{
		
					@Override
					public Boolean getValue()
					{
						for (List<String> item : dataProvider.getList())
						{
							if (!selectionModel.isSelected(item))
							{
								return false;
							}
						}
						return table.getVisibleItems().size() > 0;
					}
				};
			selectAllHeader.setUpdater(new ValueUpdater<Boolean>()
			{
				@Override
				public void update(Boolean value)
				{
					List<List<String>> list = (List<List<String>>) dataProvider.getList();
	
					for (int row = 0; row < statTableModel.getRowCount(); row++)
					{
						List<String> rowObject = list.get(row);
						
						// setSelectionList van StatTableModel 
						StatTable.this.statTableModel.setSelectedWithoutEvent(row, value);
						selectionModel.setSelected(rowObject, value);
					}
					
					setSelectionBackground();
	
					// send an event
					SelectionChangeEvent event = new SelectionChangeEvent(SelectionChangeEvent.STAT_TABLE);
					StatTable.this.statTableModel.fireEvent(event);
				}
			});
			
			selectAllHeader.setHeaderStyleNames(statistiekCss.datagridcell());
			this.table.addColumn(checkColumn, selectAllHeader);
			this.table.setColumnWidth(checkColumn, StatTable.CHECKBOX_COLUMN_WIDTH, Unit.PX);
			// add the column's width to total width
			totalWidth = totalWidth + StatTable.CHECKBOX_COLUMN_WIDTH;
		} // add checkColumn

		this.headers = this.statTableModel.getColumnNames();
		
		for (int i = 0; i < this.statTableModel.getColumnCount(); i++)
		{
			this.setTempColumnIndex(i);

			// value updater for the column header
			// Bij een klik op een column header wordt headerPopupMenu getoond met sorteer, bewerk etc. opties.
			ValueUpdater<String> valueUpdater = new ValueUpdater<String>()
				{
					int columnIndex = StatTable.this.getTempColumnIndex();

					@Override
					public void update(String value)
					{
						// set the popup index
						StatTable.this.popupColumnIndex = columnIndex; // StatTable.this.table.convertColumnIndexToModel(column);

						headerPopupMenu.setVisible(true);
						// get position of current column
						int x;
						int y;

						if (table.getRowCount() == 0)
						{
							// position is based on a first row cell (not possible to get position from header),
							// so add dummy row
							List<String> dummy = new ArrayList<String>();
							for (int i = 0; i < table.getColumnCount(); i++)
							{
								dummy.add("");
							}

							dataProvider.getList().add(dummy);
							dataProvider.refresh();
							dataProvider.flush();

							// get position from row
							x = table.getRowElement(0).getCells()
								.getItem(columnIndex + 2).getAbsoluteLeft(); // table has a row number and check column, so + 2
							y = table.getRowElement(0).getCells()
								.getItem(columnIndex + 2).getAbsoluteTop(); // table has a row number and check column, so + 2

							// remove dummy row
							dataProvider.getList().clear();
						}
						else
						{
							// get position from row
							x = table.getRowElement(0).getCells()
								.getItem(columnIndex + 2).getAbsoluteLeft(); // table has a row number and check column, so + 2
							y = table.getRowElement(0).getCells()
								.getItem(columnIndex + 2).getAbsoluteTop(); // table has a row number and check column, so + 2
						}
						
						int scrollYPosition = table.getScrollPanel().getVerticalScrollPosition();

						headerPopupMenu.setPopupPosition(x, y + scrollYPosition);
						headerPopupMenu.show();
					}
				}; // ValueUpdater
				
			FieldUpdater<List<String>, String> fieldUpdater = new FieldUpdater<List<String>, String>()
			{
				int columnIndex = StatTable.this.getTempColumnIndex();

				@Override
				public void update(int rowIndex, List<String> s, String value)
				{
					StatTable.this.statTableModel.setValueAt(
						value, rowIndex, columnIndex);
				}
			};
			
			// voor iedere kolom een kolomheader met clickabletextcell voor popup-options
			ClickableTextCell headerCell = new ClickableTextCell();
			Header<String> columnHeader = new Header<String>(headerCell) 
			{
				int columnIndex = StatTable.this.getTempColumnIndex();

				@Override
			    public String getValue() 
				{
			        return headers.get(this.columnIndex);
			    }
			};
			columnHeader.setHeaderStyleNames(statistiekCss.datagridcell());
			columnHeader.setUpdater(valueUpdater);

			// different column types for different situations/settings
			if (!this.getStatTableModel().isDataEditable())
			{
				ColumnType type = this.statTableModel.getColumnTypes().get(i);
				StatTableTextCell textCell = new StatTableTextCell(
					type, 
					this.cellWidth[i], 
					this);
				Column<List<String>, String> column = new StatTableColumn(textCell, type);

				column.setSortable(true);
				column.setCellStyleNames(statistiekCss.datagridcell());
				
				this.table.addColumn(column, columnHeader);
				this.table.setColumnWidth(column, this.columnWidth[i], Unit.PX);
				// add the column's width to total width
				totalWidth = totalWidth + this.columnWidth[i];				
			}
			else if (this.statTableModel.getColumnTypes().get(i).getType()
				.equals(AllowedTypes.ENUM))
			{
				// ENUM
				String[] enumOptions = StatTable.this.statTableModel
					.getColumnTypes().get(i).getEnumOptions();

				SelectionCell enumCell = new StatTableSelectionCell(Arrays.asList(enumOptions), this);
				ColumnType type = this.statTableModel.getColumnTypes().get(i);
				Column<List<String>, String> enumColumn = new StatTableColumn(enumCell, type);

				enumColumn.setFieldUpdater(fieldUpdater);
				
				enumColumn.setSortable(true);
				enumColumn.setCellStyleNames(statistiekCss.datagridcell());
				
				this.table.addColumn(enumColumn, columnHeader);
				this.table.setColumnWidth(enumColumn, this.columnWidth[i], Unit.PX);
				// add the column's width to total width
				totalWidth = totalWidth + this.columnWidth[i];
			} // ENUM
			else
			{ 
				// STRING OR NUMERIC
				ColumnType type = this.statTableModel.getColumnTypes().get(i);
				StatTableInputCell inputCell = new StatTableInputCell(
					type, 
					this.cellWidth[i], 
					this.getStatTableModel().isDataEditable(), this);
				Column<List<String>, String> column = new StatTableColumn(inputCell, type);

				column.setFieldUpdater(fieldUpdater);
				column.setSortable(true);
				column.setCellStyleNames(statistiekCss.datagridcell());
				
				this.table.addColumn(column, columnHeader);
				this.table.setColumnWidth(column, this.columnWidth[i], Unit.PX);
				// add the column's width to total width
				totalWidth = totalWidth + this.columnWidth[i];
			}
		} // for-loop over columns
		
	}
	
	/**
	 * Add a cell preview handler to handle the right mouse click
	 * of long tap for showing the outlier menu and for handling row selection.
	 * Add a mousup handler for resetting isMouseDown.
	 */
	private void addOutlierAndSelectionHandler()
	{
		// add a mouse up handler for resetting isMouseDown
		this.table.addDomHandler(this.mouseUpHandler, MouseUpEvent.getType());
		
		// add a cell preview handler for the outlier menu and for row selection
		this.table.addCellPreviewHandler(this.cellPreviewHandler);
	}
	
	/**
	 * Process the touch event, set the taptime and for
	 * a long tap show the outlier menu.
	 * 
	 * @param nativeEvent
	 */
	private void processTouch(NativeEvent nativeEvent)
	{
		if ("touchstart".equals(nativeEvent.getType()))
		{
			taptime = System.currentTimeMillis();
		}
		else if ("touchend".equals(nativeEvent.getType()) && isLongClick())
		{
			Touch touch = null;
			
			int x = 0;
			int y = 0;
			
			if (nativeEvent.getTouches().length() > 0)
			{
				touch = nativeEvent.getTouches().get(0);
			}
			else if ((nativeEvent.getChangedTouches() != null) 
				&& (nativeEvent.getChangedTouches().length() > 0)) 
			{
				touch = nativeEvent.getChangedTouches().get(0);
			}

			try
			{
				x = touch.getClientX();
				y = touch.getClientY();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			showOutlierPopup(nativeEvent, x, y);
		}
	}
	
	private void showOutlierPopup(NativeEvent nativeEvent, int x, int y)
	{
    	nativeEvent.stopPropagation();
    	nativeEvent.preventDefault();
    	
    	if (outlierPopupColumnIndex < 0) // when clicking the first two columns, open the popup with only one option for marking the row as outlier
    	{
			updateRowOutlierPopup(outlierPopupRowIndex);

			rowOutlierPopupMenu.setVisible(true);
			rowOutlierPopupMenu.setPopupPosition(x, y);
			rowOutlierPopupMenu.show();
    	}
    	else
    	{
			updateOutlierPopup(outlierPopupRowIndex, outlierPopupColumnIndex);

			outlierPopupMenu.setVisible(true);
        	outlierPopupMenu.setPopupPosition(x, y);
			outlierPopupMenu.show();
        }
	}

	/**
	 * Update the outlier popup.
	 */
	private void updateOutlierPopup(int rowIndex, int columnIndex)
	{
		if (this.statTableModel.isOutlier(rowIndex, columnIndex))
		{
			this.outlierCellItem.setText(StatistiekGWT.rb.demarkOutlierCell());
			this.outlierCellItem.setScheduledCommand(demarkOutlierCellCommand);
		}
		else
		{
			this.outlierCellItem.setText(StatistiekGWT.rb.markOutlierCell());
			this.outlierCellItem.setScheduledCommand(markOutlierCellCommand);
		}
		
		if (this.statTableModel.isOutlier(rowIndex))
		{
			this.outlierRowItem.setText(StatistiekGWT.rb.demarkOutlierRow());
			this.outlierRowItem.setScheduledCommand(demarkOutlierRowCommand);
		}
		else
		{
			this.outlierRowItem.setText(StatistiekGWT.rb.markOutlierRow());
			this.outlierRowItem.setScheduledCommand(markOutlierRowCommand);
		}
	}

	/**
	 * Update the row outlier popup.
	 */
	private void updateRowOutlierPopup(int rowIndex)
	{
		if (this.statTableModel.isOutlier(rowIndex))
		{
			this.rowOutlierRowItem.setText(StatistiekGWT.rb.demarkOutlierRow());
			this.rowOutlierRowItem.setScheduledCommand(demarkOutlierRowCommand);
		}
		else
		{
			this.rowOutlierRowItem.setText(StatistiekGWT.rb.markOutlierRow());
			this.rowOutlierRowItem.setScheduledCommand(markOutlierRowCommand);
		}
	}

	/**
	 * Initialize the cell width for each column in table
	 * considering the column header width and the table data in the column. 
	 */
	private void initializeCellWidth()
	{
		this.cellWidth = new int[this.statTableModel.getColumnCount()];
		
		this.updateCellWidth();
	}
	
	/**
	 * Initialize the column width for each column in table
	 * considering the column header width and the table data in the column. 
	 */
	private void initializeColumnWidth()
	{
		this.columnWidth = new int[this.statTableModel.getColumnCount()];
		
		this.updateColumnWidth();
	}
	
	/**
	 * Initialize the column headerwidth for each column in table. 
	 */
	private void initializeColumnHeaderWidth()
	{
		this.columnHeaderWidth = new int[this.statTableModel.getColumnCount()];
		
		this.updateColumnHeaderWidth();
	}
	
	/**
	 * Update the cell width for the given column in table
	 * based on the max column width and max cell width. 
	 */
	private void updateCellWidth(int columnIndex)
	{
		cellWidth[columnIndex] = this.determineCellWidth(columnIndex);
	}
	
	/**
	 * Update the column width for the given column in table
	 * based on the max column width and max cell width. 
	 */
	private void updateColumnWidth(int columnIndex)
	{
		if (columnIndex > -1)
		{
			columnWidth[columnIndex] = this.determineColumnWidth(columnIndex);
		}
	}
	
	/**
	 * Determine the cell width based on column header width, max cell width,
	 * and MAXIMUM_CELL_WIDTH. The column header must always be visible to the full extent,
	 * in other cases MAXIMUM_CELL_WIDTH is taken into account.
	 *  
	 * @param columnIndex
	 * @return
	 */
	private int determineCellWidth(int columnIndex)
	{
		int cellWidth;
		
//		Math.min(
////			MAXIMUM_COLUMN_WIDTH, 
//			MAXIMUM_CELL_WIDTH, 
//			Math.max(this.maxCellWidth[columnIndex], this.columnHeaderWidth[columnIndex]));
		
		if (this.columnHeaderWidth[columnIndex] > MAXIMUM_CELL_WIDTH)
		{
			cellWidth = this.columnHeaderWidth[columnIndex];
		}
		else if (this.maxCellWidth[columnIndex] > MAXIMUM_CELL_WIDTH)
		{
			cellWidth = MAXIMUM_CELL_WIDTH;
		}
		else if (this.maxCellWidth[columnIndex] > this.columnHeaderWidth[columnIndex])
		{
			cellWidth = this.maxCellWidth[columnIndex];
		}
		else
		{
			cellWidth = this.columnHeaderWidth[columnIndex];
		}
		
		return cellWidth;
	}

	/**
	 * Determine the column width based on cell width.
	 * For columns containing enumeration listboxes some extra space is required.
	 *  
	 * @param columnIndex
	 * @return
	 */
	private int determineColumnWidth(int columnIndex)
	{
		int columnWidth = this.determineCellWidth(columnIndex) + HEADER_PADDING;
		
		// voor enum kolommen met listboxes (data editable) iets meer ruimte
		if (this.getStatTableModel().isDataEditable()
			&& this.statTableModel.getColumnTypes().get(columnIndex).getType()
				.equals(AllowedTypes.ENUM))
		{
			if (this.maxCellWidth[columnIndex] > this.columnHeaderWidth[columnIndex] - StatTable.ENUM_PADDING)
			{
				columnWidth = columnWidth + StatTable.ENUM_PADDING;
			}
		}
		
		int scrollHeight = this.table.getScrollPanel().getElement().getScrollHeight();
		int clientHeight = this.table.getScrollPanel().getElement().getClientHeight();
		int bottom = this.table.getScrollPanel().getElement().getAbsoluteBottom();
		
		if (this.isLastColumn(columnIndex) 
			&& (scrollHeight > clientHeight)) // there is a vertical scrollbar; FIXME dit werkt niet! Extra space wordt nu altijd gezet.
		{
			columnWidth = columnWidth + 10;
		}
		
		return columnWidth;
	}

	/**
	 * Returns whether the column with the given index is the last column in the table.
	 * 
	 * @param columnIndex
	 * @return
	 */
	private boolean isLastColumn(int columnIndex)
	{
		boolean b = (columnIndex == this.statTableModel.getColumnCount() - 1);
		
		return b;
	}

	/**
	 * Update the column header width for the given column in table. 
	 */
	private void updateColumnHeaderWidth(int columnIndex)
	{
		String header;
		
		header = this.statTableModel.getColumnNames().get(columnIndex);
		columnHeaderWidth[columnIndex] = this.determineStringWidth(header);
	}
	
	/**
	 * Update the column header width for the columns with index the given column index 
	 * or larger in table. 
	 */
	private void updateColumnHeaderWidthFromIndex(int startColumnIndex)
	{
		for (int i = startColumnIndex; i < this.statTableModel.getColumnCount(); i++)
		{
			this.updateColumnHeaderWidth(i);
		}
	}
	
	/**
	 * Update the column width for the columns with index the given column index 
	 * or larger in table considering the column header width and the table data in the column. 
	 */
	private void updateColumnWidthFromIndex(int startColumnIndex)
	{
		for (int i = startColumnIndex; i < this.statTableModel.getColumnCount(); i++)
		{
			this.updateColumnWidth(i);
		}
	}
	
	/**
	 * Update the cell width for the columns with index the given column index 
	 * or larger in table considering the table data in the column. 
	 */
	private void updateCellWidthFromIndex(int startColumnIndex)
	{
		for (int i = startColumnIndex; i < this.statTableModel.getColumnCount(); i++)
		{
			this.updateCellWidth(i);
		}
	}
	
	/**
	 * Update the cell width for all columns in table
	 * based on the max column width and max cell width. 
	 */
	private void updateCellWidth()
	{
		for (int i = 0; i < this.statTableModel.getColumnCount(); i++)
		{
			this.updateCellWidth(i);
		}
	}
	
	/**
	 * Update the column width for all columns in table
	 * based on the max column width and max cell width. 
	 */
	private void updateColumnWidth()
	{
		for (int i = 0; i < this.statTableModel.getColumnCount(); i++)
		{
			this.updateColumnWidth(i);
		}
	}
	
	/**
	 * Update the column header width for all columns in table. 
	 */
	private void updateColumnHeaderWidth()
	{
		for (int i = 0; i < this.statTableModel.getColumnCount(); i++)
		{
			this.updateColumnHeaderWidth(i);
		}
	}
	
	/**
	 * Add columnHeaderWidth to the end of the array for added column.
	 *  
	 */
	private void addColumnHeaderWidth()
	{
		int[] oldColumnHeaderWidth = this.columnHeaderWidth;
		this.columnHeaderWidth = new int[oldColumnHeaderWidth.length + 1]; 
	
		for (int i = 0; i < this.columnHeaderWidth.length - 1; i++)
		{
			this.columnHeaderWidth[i] = oldColumnHeaderWidth[i];
		}
	
		// the last element is the newly added column
		int indexAddedColumn = this.columnHeaderWidth.length - 1;
		this.updateColumnHeaderWidth(indexAddedColumn);
	}
	
	/**
	 * Remove columnHeaderWidth from the end of the array for removed column.
	 *  
	 */
	private void removeColumnHeaderWidth()
	{
		int[] oldColumnHeaderWidth = this.columnHeaderWidth;
		this.columnHeaderWidth = new int[oldColumnHeaderWidth.length - 1]; 
	
		for (int i = 0; i < this.columnHeaderWidth.length; i++)
		{
			this.columnHeaderWidth[i] = oldColumnHeaderWidth[i];
		}
	}
	
	/**
	 * Add a columnWidth to the end of the array for added column.
	 *  
	 */
	private void addColumnWidth()
	{
		int[] oldColumnWidth = this.columnWidth;
		this.columnWidth = new int[oldColumnWidth.length + 1]; 
	
		for (int i = 0; i < this.columnWidth.length - 1; i++)
		{
			this.columnWidth[i] = oldColumnWidth[i];
		}
	
		// the last element is the newly added column
		int indexAddedColumn = this.columnWidth.length - 1;
		this.updateColumnWidth(indexAddedColumn);
	}
	
	/**
	 * Remove columnWidth from the end of the array for removed column.
	 *  
	 */
	private void removeColumnWidth()
	{
		int[] oldColumnWidth = this.columnWidth;
		this.columnWidth = new int[oldColumnWidth.length - 1]; 
	
		for (int i = 0; i < this.columnWidth.length; i++)
		{
			this.columnWidth[i] = oldColumnWidth[i];
		}
	}
	
	/**
	 * Add a cellWidth to the end of the array for added column.
	 *  
	 */
	private void addCellWidth()
	{
		int[] oldCellWidth = this.cellWidth;
		this.cellWidth = new int[oldCellWidth.length + 1]; 
	
		for (int i = 0; i < this.cellWidth.length - 1; i++)
		{
			this.cellWidth[i] = oldCellWidth[i];
		}
	
		// the last element is the newly added column
		int indexAddedColumn = this.cellWidth.length - 1;
		this.updateCellWidth(indexAddedColumn);
	}
	
	/**
	 * Remove cellWidth from the end of the array for removed column.
	 *  
	 */
	private void removeCellWidth()
	{
		int[] oldCellWidth = this.cellWidth;
		this.cellWidth = new int[oldCellWidth.length - 1]; 
	
		for (int i = 0; i < this.cellWidth.length; i++)
		{
			this.cellWidth[i] = oldCellWidth[i];
		}
	}
	
	/**
	 * Determine the width of the string.
	 * 
	 */
	private int determineStringWidth(String text)
	{
		TextMetrics metrics;
		Canvas canvas = Canvas.createIfSupported();
		Context2d context = canvas.getContext2d();
		
		// set the table's font
		context.setFont(StatTable.TABLE_HEADER_FONT);
		metrics = context.measureText(text);
		// initialize maxWidth with the header width
		int textWidth = (int) metrics.getWidth();

		return textWidth;
	}

	/**
	 * Update the width of the row number column in table.
	 *  
	 */
	private void updateRowNumberWidth()
	{
		TextMetrics metrics;
		Canvas canvas = Canvas.createIfSupported();
		Context2d context = canvas.getContext2d();
		context.setFont(StatTable.CELL_STYLE_FONT_SIZE);
		
		int maxRow = this.statTableModel.getRowCount();
		metrics = context.measureText(String.valueOf(maxRow));
		this.rowNumberWidth = (int) metrics.getWidth() + 22;
	}
	
	/**
	 * Initialize the max cell width for each column in table.
	 *  
	 */
	private void initializeMaxCellWidth()
	{
		this.maxCellWidth = new int[this.statTableModel.getColumnCount()];
		
		this.updateMaxCellWidth();
	}
	
	/**
	 * Update the max cell width for the given column in table.
	 *  
	 */
	private void updateMaxCellWidthFromIndex(int startColumnIndex)
	{
		for (int i = startColumnIndex; i < this.statTableModel.getColumnCount(); i++)
		{
			this.updateMaxCellWidth(i);
		}
	}
	
	/**
	 * Update the max cell width for the given column in table.
	 *  
	 */
	private void updateMaxCellWidth(int columnIndex)
	{
		maxCellWidth[columnIndex] = this.determineMaxCellWidth(columnIndex);
	}
	
	/**
	 * Update the max cell width for all columns in table.
	 *  
	 */
	private void updateMaxCellWidth()
	{
		for (int i = 0; i < this.statTableModel.getColumnCount(); i++)
		{
			this.updateMaxCellWidth(i);
		}
	}
	
	/**
	 * Add maxCellWidth to the end of the array for added column.
	 *  
	 */
	private void addMaxCellWidth()
	{
		int[] oldMaxCellWidth = this.maxCellWidth;
		this.maxCellWidth = new int[oldMaxCellWidth.length + 1]; 
		
		for (int i = 0; i < this.maxCellWidth.length - 1; i++)
		{
			this.maxCellWidth[i] = oldMaxCellWidth[i];
		}
		
		// the last element is the newly added column
		int indexAddedColumn = this.maxCellWidth.length - 1;
		this.updateMaxCellWidth(indexAddedColumn);
	}
	
	/**
	 * Remove maxCellWidth from the end of the array for removed column.
	 *  
	 */
	private void removeMaxCellWidth()
	{
		int[] oldMaxCellWidth = this.maxCellWidth;
		this.maxCellWidth = new int[oldMaxCellWidth.length - 1]; 
		
		for (int i = 0; i < this.maxCellWidth.length; i++)
		{
			this.maxCellWidth[i] = oldMaxCellWidth[i];
		}
	}
	
	/**
	 * Determine the maximum width of the column's content, considering  
	 * the table data in the column.
	 * 
	 */
	private int determineMaxCellWidth(int columnIndex)
	{
		TextMetrics metrics;
		Canvas canvas = Canvas.createIfSupported();
		Context2d context = canvas.getContext2d();
		
		// set the table's font
		context.setFont(StatTable.TABLE_HEADER_FONT);
		
		// the minimum cell width
		int maxWidth = StatTable.MINIMUM_CELL_WIDTH;

		if (this.statTableModel.getColumnTypes().get(columnIndex).getType()
			.equals(AllowedTypes.ENUM))
		{
			String[] enumOptions = StatTable.this.statTableModel
				.getColumnTypes().get(columnIndex).getEnumOptions();
			
			if (enumOptions != null)
			{
				//for (String s : enumOptions)
				// loop over all values in enumOptions.
				// for large datasets, take a limited number of enumOptions into account
				for (int i = 0; i < Math.min(enumOptions.length, StatTable.LARGE_DATASET_LIMITED_ROWCOUNT); i++)
				{
					String s = enumOptions[i];
					metrics = context.measureText(s);
					maxWidth = (int) Math.max(maxWidth, metrics.getWidth());
				}
			}
		}
		else
		{
			ArrayList<ArrayList<Object>> values = this.statTableModel.getValues();
			
			if (values != null)
			{
				// loop over all values in the column
				// for large datasets, take a limited number of rows into account
				for (int i = 0; i < Math.min(values.size(), StatTable.LARGE_DATASET_LIMITED_ROWCOUNT); i++) // i loops over the rows
				{
					String s = StatistiekGWT.getStringValue(values.get(i).get(columnIndex).toString());
					metrics = context.measureText(s);
					int w = (int) metrics.getWidth(); // for debugging
					maxWidth = (int) Math.max(maxWidth, w);
				}
			}
			
			if (this.getStatTableModel().isDataEditable()
				&& this.statTableModel.getColumnTypes().get(columnIndex).getType().isNumber())
			{
				maxWidth = (int) (maxWidth * 1.1); // somehow a little extra is needed for numbers
			}
		}

		return maxWidth;
	}

	/**
	 * Get temporary column index.
	 * Used to create columns and get the correct value from the row string list.
	 * 
	 * @return
	 */
	private int getTempColumnIndex()
	{
		return this.tempColumnIndex;
	}
	
	/**
	 * Set the outlier popup row index.
	 * Used to be able to mark the clicked row.
	 * 
	 * @param i
	 */
	private void setOutlierPopupRowIndex(int i)
	{
		this.outlierPopupRowIndex = i;
	}

	/**
	 * Set temporary column index.
	 * Used to create columns and get the correct value from the row string list.
	 * 
	 * @param i
	 */
	private void setTempColumnIndex(int i)
	{
		this.tempColumnIndex = i;
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event)
	{
		GWT.log("StatTable.onSelectionChange(): sender = " + event.getSenderName());
		
		// only perform an update if the selection change event is triggered by
		// another view
		if (!event.getSenderName().equals(SelectionChangeEvent.STAT_TABLE))
		{
			this.setSelectionFromModelInTable();
			this.update();
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

	/**
	 * Get the view's width.
	 */
	public int getWidth()
	{
		return this.width;
	}
	
	/**
	 * Get the view's height.
	 */
	public int getHeight()
	{
		return this.height;
	}
	
	/**
	 * Set the view's width.
	 */
	public void setWidth(int w)
	{
		this.width = w;
	}
	
	/**
	 * Set the view's height.
	 */
	public void setHeight(int h)
	{
		this.height = h;
	}
	
	public StatTableModel getStatTableModel()
	{
		return this.statTableModel;
	}

	@Override
	public void onOutlierChange(OutlierChangeEvent event)
	{
		this.update();
	}
	
	/**
	 * Detect whether there has been a loong click or 'tap & hold'.
	 * 
	 * @return
	 */
    protected boolean isLongClick() 
    {
    	return System.currentTimeMillis() - taptime > 300;
	}
}

