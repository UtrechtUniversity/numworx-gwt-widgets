package fi.statistiekgwt.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

import fi.statistiekgwt.client.columndialog.ColumnDialogController;
import fi.statistiekgwt.client.columndialog.ColumnDialogModel;
import fi.statistiekgwt.client.columndialog.ColumnDialogView;
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
	SelectionChangeEventHandler
//	,TableModelListener, ActionListener, ListSelectionListener,
//	SelectionListener
{
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

	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;
	private static final String DELIMITER = ";";
	private static final String STANDARD_CELL_WIDTH_STYLE = "width: 100px";
	private static final String CHECKBOX_CELL_WIDTH_STYLE = "width: 30px";
	private static final int STANDARD_COLUMN_WIDTH = 150;
	private static final int CHECKBOX_COLUMN_WIDTH = 60;
	private static final int TABLE_PAGE_SIZE = 20;
	
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
	
	private static final int WIDTH_PASTE_DIALOG = 300;
	private static final int HEIGHT_PASTE_DIALOG = 230;

	private StatTableModel statTableModel;

	// Include field statInteractiePanel to process the reset actions
	private StatInteractiePanel statInteractiePanel;

//	private CellTable<Object> table;
//	private CellTable<List<String>> table;
	private DataGrid<List<String>> table; // datagrid provides fixed header and footer section
	protected ListDataProvider<List<String>> dataProvider;
	private ListHandler<List<String>> sortHandler;
	private SimplePager pager;
	private MultiSelectionModel<List<String>> selectionModel;
	ArrayList<String> headers;
	
	
	private String viewName;
	private PopupPanel popupMenu;
	private MenuBar menuBar;
	//private StackPanel columnOptionsStack;
	private MenuItem sortItem;
	private MenuItem editItem;
	private MenuItem deleteItem;
	private MenuItem infoItem;
	private Command sortCommand;
	private Command editCommand;
	private Command deleteCommand;
	private Command infoCommand;
	private int popUpColumnIndex;
	private CellTable<Object> rowTable; // nodig? Was om regelnummers te tonen in de tabel
	//private ScrollPanel scrollPanel; // niet nodig met DataGrid

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
	private Button pasteButton;
	private Button deleteRowsButton;
	private PushButton resetButton;
	private Button importButton;
	private FileUploadExt fileUpload;
	private PopupPanel popupFileUploadPanel;
	private Button fileUploadSelectButton;
	private Button fileUploadCancelButton;
	private FormPanel formPanel;
	
	private StatTableClickHandler clickHandler;
	private ImportMessageDialogBox importBox;
	
	// fields for reading import data from CSV file
	protected FileReader reader;
	protected Blob blob;
	protected List<File> readQueue;
	String csvText;
	String[] CSVheaders;
	ArrayList<String> dataRows;
	
	/**
	 * Temporary index used to create columns.
	 */
	private int tempColumnIndex;
	
	/**
	 * Constructor without viewname
	 * 
	 * @param statTableModel
	 *            The datamodel
	 * @param statInteractiePanel
	 *            The StatInteractiePanel
	 */
	public StatTable(StatTableModel statTableModel,
		StatInteractiePanel statInteractiePanel)
	{
		super(Unit.EM);
		
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();
		
		this.statTableModel = statTableModel;
		this.tableChangeEventHandlerRegistration = this.statTableModel.addTableChangeEventHandler(this);
		this.selectionChangeEventHandlerRegistration = this.statTableModel.addSelectionChangeEventHandler(this);

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
		super(Unit.EM);
		
		statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.statTableModel = statTableModel;
		this.tableChangeEventHandlerRegistration = this.statTableModel.addTableChangeEventHandler(this);
		this.selectionChangeEventHandlerRegistration = this.statTableModel.addSelectionChangeEventHandler(this);

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
			//if (this.popupMenuBar.getSubElements().length == 3)
			// delete menu items if present
			if (this.menuBar.getItemIndex(editItem) > -1)
			{
				this.menuBar.removeItem(editItem);
			}
			if (this.menuBar.getItemIndex(deleteItem) > -1)
			{
				this.menuBar.removeItem(deleteItem);
			}
		}
		else
		{ // data is editable
			// add menu items if not present
			if (this.menuBar.getItemIndex(editItem) == -1)
			{
				this.menuBar.addItem(editItem);
			}
			if (this.menuBar.getItemIndex(deleteItem) == -1)
			{
				this.menuBar.addItem(deleteItem);
			}
		}
	}

	/**
	 * Initialize
	 */
	private void setUp()
	{
		this.clickHandler = new StatTableClickHandler();
		
		// set up the file chooser to open a data file
		this.setUpFileUpload();
		
		this.setUpPasteDataDialog();
		
		this.table = new DataGrid<List<String>>(KEY_PROVIDER);
		// test syl: this should make the keyboard selection the same as the contents of the selection model:
		//this.table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION); // nu kan ik er maar 1 tegelijk selecteren
		//this.table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		 /*
	     * Do not refresh the headers every time the data is updated. The footer
	     * depends on the current data, so we do not disable auto refresh on the
	     * footer.
	     */
	    this.table.setAutoHeaderRefreshDisabled(true);
	    // Set the message to display when the table is empty
	    this.table.setEmptyTableWidget(new Label(StatistiekGWT.rb.getString("emptyTableMessage")));
	    // set style
	    this.table.addStyleName(statistiekCss.dataGrid());

		// test syl
		this.table.getElement().getStyle().setBackgroundColor("powderblue");
	    this.dataProvider = new ListDataProvider<List<String>>();
	    // Add the table to the dataProvider.
		this.dataProvider.addDataDisplay(this.table);
		
		// Attach a column sort handler to the ListDataProvider to sort the list.
	    sortHandler =
	        new ListHandler<List<String>>(this.dataProvider.getList());
	    
	    this.table.addColumnSortHandler(sortHandler);

	    // Create a Pager to control the table.
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.setDisplay(this.table);
	    pager.setPageSize(StatTable.TABLE_PAGE_SIZE);
	    pager.setRangeLimited(true);
	    DockLayoutPanel tablePanel = new DockLayoutPanel(Unit.EM);
	    tablePanel.addSouth(pager, 3);
	    tablePanel.add(this.table);
	    tablePanel.setHeight("100%");
	    tablePanel.setWidth("100%");

	    // Add a selection model so we can select cells.
	    selectionModel =
	        new MultiSelectionModel<List<String>>(KEY_PROVIDER);
	    this.table.setSelectionModel(selectionModel, DefaultSelectionEventManager
	        .<List<String>> createCheckboxManager(0));

	    // create vertical menubar
		this.menuBar = new MenuBar(true);
		this.popupMenu = new PopupPanel(true);
		this.popupMenu.add(this.menuBar);

		this.popupMenu.setVisible(false);
		this.popupMenu.hide();
		
		sortCommand = new Command() {
	        @Override
            public void execute() 
	        {
	        	StatTable.this.statTableModel.sort(StatTable.this.popUpColumnIndex);
	        	StatTable.this.hidePopupMenu();
            }
        };
        this.createEditCommand();
        deleteCommand = new Command() {
	        @Override
            public void execute() 
	        {
				StatTable.this.statTableModel.removeColumn(StatTable.this.popUpColumnIndex);
	        	StatTable.this.hidePopupMenu();
            }
        };
        this.createInfoCommand();
		sortItem = new MenuItem(StatistiekGWT.rb.getString("sortItem"), true, sortCommand);
		editItem = new MenuItem(StatistiekGWT.rb.getString("editcolumnItem"), true, editCommand);
		deleteItem = new MenuItem(StatistiekGWT.rb.getString("deletecolumnItem"), true, deleteCommand);
		infoItem = new MenuItem(StatistiekGWT.rb.getString("infocolumnItem"), true, infoCommand);
		this.menuBar.addItem(sortItem);
		if (this.statTableModel.isDataEditable())
		{
			this.menuBar.addItem(editItem);
			this.menuBar.addItem(deleteItem);
		}
		this.menuBar.addItem(infoItem);

		// maak editDataPanel met 6 buttons
		this.editDataPanel = new HorizontalPanel();//new LayoutPanel();
		this.editDataPanel.setSize("100%", "100%");
		this.editDataPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		this.importButton = new Button(StatistiekGWT.rb.getString("importButton"));
		this.importButton.addClickHandler(this.clickHandler);
		this.editDataPanel.add(this.importButton);

		this.addRowButton = new Button(StatistiekGWT.rb.getString("addrowButton"));
		this.addRowButton.addClickHandler(this.clickHandler);
		this.editDataPanel.add(this.addRowButton);
		
		this.addColumnButton = new Button(StatistiekGWT.rb.getString("addcolumnButton"));
		this.addColumnButton.addClickHandler(this.clickHandler);
		this.editDataPanel.add(this.addColumnButton);
		
		this.deleteRowsButton = new Button(StatistiekGWT.rb.getString("deleteselectedrowsButton"));
		this.deleteRowsButton.addClickHandler(this.clickHandler);
		this.editDataPanel.add(this.deleteRowsButton);
		
		this.pasteButton = new Button(StatistiekGWT.rb.getString("pasteclipboardButton"));
		this.pasteButton.addClickHandler(this.clickHandler);
		this.editDataPanel.add(this.pasteButton);
		
		this.resetButton = new PushButton(new Image(statistiekGWTClientBundle.resetResource().getSafeUri()));
		this.resetButton.addStyleName(statistiekCss.pushbutton());
		this.resetButton.addClickHandler(this.clickHandler);
		this.editDataPanel.add(this.resetButton);
		
		this.editDataPanel.setVisible(this.statTableModel.isDataEditable());

		// test syl
		this.editDataPanel.getElement().getStyle().setBackgroundColor("lemonchiffon");
		super.addSouth(this.editDataPanel, 4);//3);//EM //, BorderLayout.SOUTH);
		ResizeLayoutPanel resizePanel = new ResizeLayoutPanel();
		resizePanel.setHeight("100%");
		resizePanel.setWidth("100%");
		resizePanel.add(tablePanel);
		super.add(resizePanel); // resizeLayoutPanel voor horizontal scrollbar van table
		
		Label label = new Label(StatistiekGWT.rb.getString("importDialogLabel"));
	    importBox = new ImportMessageDialogBox(label);
	    
		this.update();
		// set the right selection
		this.setSelectionFromModelInTable();
	}

	/**
	 * Set up the dialog for pasting data and importing 
	 * the data into the table.
	 */
	private void setUpPasteDataDialog()
	{
		this.pasteDataDialog = new DialogBox(false, true);
		this.pasteDataDialog.setPixelSize(WIDTH_PASTE_DIALOG, HEIGHT_PASTE_DIALOG);
		this.pasteDataDialog.setText(StatistiekGWT.rb.getString("pasteclipboardDialog"));
		
		LayoutPanel panel = new LayoutPanel();
		panel.setPixelSize(WIDTH_PASTE_DIALOG, HEIGHT_PASTE_DIALOG); // panel moet pixelsize hebben anders toont hij niet...
		
		// messages
		String messageString = StatistiekGWT.rb.getString("pasteclipboardMessage")
			+ StatistiekGWT.rb.getString("importPastedDataButton");
		Label pasteMessage = new Label(messageString);
		this.importPasteDataMessage = new Label(); // used for fail message
		this.importPasteDataMessage.addStyleName(statistiekCss.failMessage());
		
		// text area
		this.pasteDataArea = new ExtendedTextArea();
		this.pasteDataArea.addValueChangeHandler(new ValueChangeHandler<String>() {

		    @Override
		    public void onValueChange(ValueChangeEvent<String> event) 
		    {
		    	if (StatTable.this.importPasteDataMessage.getText().length() > 0)
		    		StatTable.this.importPasteDataMessage.setText("");
		    
		    	//StatTable.this.importPasteDataMessage.setText(event.getValue());
		    }
		});
		
		// buttons
		String importPasteButtonText = StatistiekGWT.rb.getString("importPastedDataButton");
		this.importPasteDataButton = new Button(importPasteButtonText, this.clickHandler);
		String cancelButtonText = StatistiekGWT.rb.getString("cancelButtonText");
		this.cancelPasteDataButton = new Button(cancelButtonText, this.clickHandler);
		
		// put the elements on the panel
		panel.add(pasteMessage);
		panel.add(this.importPasteDataMessage);
		panel.add(pasteDataArea);
		panel.add(this.importPasteDataButton);
		panel.add(this.cancelPasteDataButton);
		
		// set position
		panel.setWidgetLeftWidth(pasteMessage, 10, Style.Unit.PCT, 80, Style.Unit.PCT);
		panel.setWidgetTopHeight(pasteMessage, 0, Style.Unit.PX, 50, Style.Unit.PX);
		panel.setWidgetLeftWidth(this.importPasteDataMessage, 10, Style.Unit.PCT, 80, Style.Unit.PCT);
		panel.setWidgetTopHeight(this.importPasteDataMessage, 50, Style.Unit.PX, 30, Style.Unit.PX);
		panel.setWidgetLeftWidth(pasteDataArea, 10, Style.Unit.PCT, 80, Style.Unit.PCT);
		panel.setWidgetTopHeight(pasteDataArea, 80, Style.Unit.PX, 100, Style.Unit.PX);
		panel.setWidgetLeftWidth(this.importPasteDataButton, 50, Style.Unit.PX, 100, Style.Unit.PX);
		panel.setWidgetTopHeight(this.importPasteDataButton, 180, Style.Unit.PX, 50, Style.Unit.PX);
		panel.setWidgetLeftWidth(this.cancelPasteDataButton, 150, Style.Unit.PX, 100, Style.Unit.PX);
		panel.setWidgetTopHeight(this.cancelPasteDataButton, 180, Style.Unit.PX, 50, Style.Unit.PX);
		
		// add panel to dialog
		this.pasteDataDialog.add(panel);
		this.pasteDataDialog.center();
		this.pasteDataDialog.hide();
	}

	/**
	 * Hide the column's popup menu.
	 */
	protected void hidePopupMenu()
	{
    	StatTable.this.popupMenu.setVisible(false);
    	StatTable.this.popupMenu.hide();
	}
	
	/**
	 * Show the column's popup menu.
	 */
	protected void showPopupMenu()
	{
    	StatTable.this.popupMenu.setVisible(true);
    	StatTable.this.popupMenu.show();
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
	        	StatTable.this.hidePopupMenu();
	        	
	        	ArrayList<ColumnType> list = StatTable.this.statTableModel.getColumnTypes();
				
	        	ColumnDialogModel dialogModel = new ColumnDialogModel(
					StatTable.this.statTableModel,
					StatTable.this.statTableModel.getColumnName(StatTable.this.popUpColumnIndex),
					list.get(popUpColumnIndex),
					StatTable.this.popUpColumnIndex);
				
				ColumnDialogView dialogView;

				dialogView = new ColumnDialogView(dialogModel, StatistiekGWT.rb.getString("columninfo"));
				
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
	        	StatTable.this.hidePopupMenu();
	        	
	        	ArrayList<ColumnType> list = StatTable.this.statTableModel.getColumnTypes();
				
	        	ColumnDialogModel dialogModel = new ColumnDialogModel(
					StatTable.this.statTableModel,
					StatTable.this.statTableModel.getColumnName(StatTable.this.popUpColumnIndex),
					list.get(popUpColumnIndex),
					StatTable.this.popUpColumnIndex);
				
				dialogModel.addEditColumnEventHandler(StatTable.this.statTableModel);
				HandlerRegistration handlerRegistration = dialogModel.addAddColumnEventHandler(StatTable.this.statTableModel);
				
				ColumnDialogView dialogView;

				dialogView = new ColumnDialogView(dialogModel, StatistiekGWT.rb.getString("editacolumn"));
				
				ColumnDialogController dialogController = new ColumnDialogController(
					dialogModel, dialogView);
				dialogController.setHandlerRegistration(handlerRegistration);

				dialogView.center();
				dialogView.show();
            }
        };
	}

	private void setUpFileUpload()
	{
		this.fileUpload = new FileUploadExt();
		
		Label selectLabel = new Label(StatistiekGWT.rb.getString("selectCSVFile"));
	    this.fileUploadSelectButton = new Button(StatistiekGWT.rb.getString("importFile"));
	    this.fileUploadSelectButton.addClickHandler(this.clickHandler);
	    this.fileUploadCancelButton = new Button(StatistiekGWT.rb.getString("cancelButtonText"));
	    this.fileUploadCancelButton.addClickHandler(this.clickHandler);

	    this.popupFileUploadPanel = new PopupPanel(false);
		VerticalPanel panel = new VerticalPanel();
		panel.add(selectLabel);
		panel.add(this.fileUpload);
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(fileUploadSelectButton);
		buttonPanel.add(fileUploadCancelButton);
		panel.add(buttonPanel);
		
		this.popupFileUploadPanel.add(panel);
		this.popupFileUploadPanel.hide();
		this.add(this.popupFileUploadPanel);
		
		this.formPanel = new FormPanel(); 
		this.formPanel.setAction(com.google.gwt.core.client.GWT.getModuleBaseURL() + "/myFormHandler");// TODO hier moet iets mee...
		this.formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		this.formPanel.setMethod(FormPanel.METHOD_POST);
		this.formPanel.setWidget(this.popupFileUploadPanel);
		
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
						// test syl
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
					handleError(file);
					readQueue.remove(0);
					readNextFile();
				}
			}
		});
		readQueue = new ArrayList<File>();
	}

	class ColumnClickHandler implements ClickHandler//TouchStartHandler //extends MouseAdapter
	{
		public void onClick(ClickEvent e)//mouseReleased(MouseEvent e)
		{
			Point p = new Point(e.getClientX(),
				e.getClientY());

			StatTable.this.popupMenu.setPopupPosition((int) p.getX(), (int) p.getY());
			StatTable.this.popupMenu.show();
		}
	}

	/**
	 * Implementation of TableModelListener
	 * 
	 * This is necessary because HEADER_ROW_CHANGED events reset the
	 * cellRenderers. On such an event this method will restore the
	 * cellRenderers
	 */
	public void onChange(ChangeEvent e) //tableChanged(TableModelEvent e)
	{
		this.editDataPanel.setVisible(this.statTableModel.isDataEditable());
		this.updatePopUp();
	}

	/**
	 * Set the right cellRenderers
	 */
	private void setCellRenderers()
	{
		// System.out.println("Setting cell renderers!");
		int i = 0;
    	ArrayList<ColumnType> list = StatTable.this.statTableModel.getColumnTypes();

		for (ColumnType type : list)
		{
			if (type.getType().equals(AllowedTypes.ENUM))
			{
				// Enums get a dropdownbox as editor
				ListBox box = new ListBox();
				for (String s : type.getEnumOptions())
				{
					box.addItem(s);
				}
//				this.table.getColumnModel().getColumn(i)
//					.setCellEditor(new DefaultCellEditor(box));
			}
			i++;
		}
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

//		ColumnClickHandler columnClickHandler = new ColumnClickHandler(); // was: MouseListener

		this.editDataPanel.setVisible(this.statTableModel.isDataEditable());

		this.setCellRenderers();
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

		for(List<String> row : list)
		{
		    if( selectionModel.isSelected(row) )
		        intList.add(i);

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

		// check the amount of cells in each row
		for (String row : dataRows)
		{
			if (row.split("\t").length != this.statTableModel.getColumnCount())
			{
				// row has incorrect amount of cells
				// set message
				this.importPasteDataMessage.setText(StatistiekGWT.rb.getString("importPastedDataFailMessage"));
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
			for (String cellString : s.split("\t"))
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

	/*
	 * Opent de dialoog voor het openen van een data bestand.
	 */
	private void openFileChooserDialog()
	{
		this.popupFileUploadPanel.show();
	}

	/**
	 * Remove views except Table.
	 */
	private void removeViews()
	{
		// test syl
		//System.out.println("StatTable.removeViews()");
		
		ArrayList<StatistiekView> views = new ArrayList<StatistiekView>();
		
		// In edit-mode en standalone is statInteractiePanel null... De tabs blijven daar gewoon staan
		if (this.statInteractiePanel != null)
		{
			views = this.statInteractiePanel.getModel().getViews();
		
	        Iterator<StatistiekView> iterator = views.iterator();
	        while (iterator.hasNext()) 
	        {
	        	StatistiekView view = iterator.next();
	        	if (!view.getViewName().equals(this.viewName))
	        	{
	        		iterator.remove();
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
	}

	/*
	 * Process the CSV data file.
	 */
	private void processCSVDataFile(FileList files)
	{
		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
		
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
		csvText = reader.getStringResult();
		
		if (!(csvText == null) && !csvText.equals("") && !csvText.equals("Error"))
		{
			String[] lines = csvText.split("\\r?\\n"); //System.getProperty("line.separator")); // System.getProperty() does not work in gwt 
			
			CSVheaders = lines[0].split(StatTable.DELIMITER);

			dataRows = new ArrayList<String>();

			for (int i = 1; i < lines.length; i++)
			{
				dataRows.add(lines[i]);
			}
			
			// clear the old data
			this.clearStatTableModel();
			
			// create string columns from headers
			this.createColumns(CSVheaders);
			
			// add row data
			this.addDataRowsWithoutEvent(dataRows);
			
			this.statTableModel.updateNumericalColumnTypesWithoutEvent();

			this.fireEvent(TableChangeEvent.IMPORT_DATA, -1);
			
			RootPanel.getBodyElement().getStyle().setProperty("cursor", "default");
		}
		else
		{
			RootPanel.getBodyElement().getStyle().setProperty("cursor", "default");
			GWT.log("CSV file is empty!");
		}		
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
			GWT.log("StatTable.readNextFile(): file type = " + type);
			try
			{
				if (type.startsWith("application/vnd.ms-excel"))//text/"))//application/vnd.ms-excel"))//text/csv
				{
					// If the file is larger than 1kb, read only the first 1000
					// characters
					// to demonstrate file slicing
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
					// melding geen CSV?
					GWT.log("type = " + type);
				}
			}
			catch (Throwable t)
			{
				// Necessary for FF (see bug
				// https://bugzilla.mozilla.org/show_bug.cgi?id=701154)
				// Standard-complying browsers will not go in this branch
				handleError(file);
				readQueue.remove(0);
				readNextFile();
			}
		}
	}

	private void handleError(File file)
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
			+ errorDesc);
	}
	
//	/**
//	 * 
//	 * @param url
//	 * @param callback
//	 */
//	public static void httpGetFile(final String url,
//		final AsyncCallback<String> callback)
//	{
//		final RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
//		rb.setCallback(new RequestCallback()
//		{
//			public void onResponseReceived(Request request, Response response)
//			{
//				try
//				{
//					final int responseCode = response.getStatusCode() / 100;
//					if (url.startsWith("file:/") || (responseCode == 2))
//					{
//						callback.onSuccess(response.getText());
//					}
//					else
//					{
//						callback.onFailure(new IllegalStateException(
//							"HttpError#" + response.getStatusCode() + " - "
//								+ response.getStatusText()));
//					}
//				}
//				catch (Throwable e)
//				{
//					callback.onFailure(e);
//				}
//			}
//
//			public void onError(Request request, Throwable exception)
//			{
//				callback.onFailure(exception);
//			}
//		});
//		try
//		{
//			rb.send();
//		}
//		catch (RequestException e)
//		{
//			callback.onFailure(e);
//		}
//	}
	
	/**
	 * Add the row data to the table.
	 * @param dataRows
	 */
	private void addDataRowsWithoutEvent(ArrayList<String> dataRows)
	{
        Iterator<String> rowIterator = dataRows.iterator();
        int rowCount = 0;
        while (rowIterator.hasNext()) 
        {
        	String dataRow = rowIterator.next();

        	String[] values = dataRow.split(";", -1);
         	this.replaceMissingValues(values);
        	ArrayList<Object> valuesList= new ArrayList<Object>(Arrays.asList(values));
        	
        	this.statTableModel.addRowWithoutEvent(valuesList);
        }
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
		this.setCellRenderers();
	}

	public String getViewType()
	{
		return "Table";
	}

//	public void valueChanged(ListSelectionEvent arg0)
//	{
//		if (!arg0.getValueIsAdjusting())
//		{
//			ArrayList<Boolean> newSelection = new ArrayList<Boolean>(
//				this.statTableModel.getRowCount());
//			for (int row = 0; row < this.statTableModel.getRowCount(); row++)
//			{
//				newSelection.add(this.table.isRowSelected(row));
//				// System.out.println(this.table.isRowSelected(row));
//			}
//			System.out.println();
//			this.statTableModel.setSelectionList(newSelection);
//		}
//	}

	/**
	 * De selectie uit StatTableModel wordt in de tabel gezet
	 */
	public void setSelectionFromModelInTable()
	{
		GWT.log("StatTable.setSelectionFromModelInTable()");

//		MultiSelectionModel<Object> selectionModel = (MultiSelectionModel<Object>) this.table.getSelectionModel(); // er is toch een field selectionModel?
		List<List<String>> list = (List<List<String>>) this.dataProvider.getList();

		for (int row = 0; row < this.statTableModel.getRowCount(); row++)
		{
			//Object rowObject = this.statTableModel.getValues().get(row);//this.table.getRowElement(row); // hier zit geen rowIndex bij!, dus vergelijkt hij op de laatste (echte) kolom
//			Object rowObject = list.get(row);
			List<String> rowObject = list.get(row); // deze bevat geen rijnummer op het eind van de rij...
			
			if (this.statTableModel.isRowSelected(row)
				&& !selectionModel.isSelected(rowObject))
			{
				// System.out.println("Selecting row " + row);
				selectionModel.setSelected(rowObject, true);
			}
			else if (!this.statTableModel.isRowSelected(row)
				&& selectionModel.isSelected(rowObject))
			{
				// System.out.println("Deselecting row " + row);
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

	class StatTableClickHandler implements ClickHandler//TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		@Override
		public void onClick(ClickEvent e)
		{
			if (e.getSource() == StatTable.this.addRowButton)
			{
				StatTable.this.statTableModel.addRow();
				// show the row added
				StatTable.this.pager.lastPage();
			}
			else if (e.getSource() == StatTable.this.addColumnButton)
			{
				ColumnDialogModel dialogModel = new ColumnDialogModel(
					StatTable.this.statTableModel);
				// test syl: TODO dit worden er steeds meer! Bestaande verwijderen, ook al is dialogModel nieuw...
				HandlerRegistration handlerRegistration = dialogModel.addAddColumnEventHandler(StatTable.this.statTableModel);
				
				ColumnDialogView dialogView;

				dialogView = new ColumnDialogView(dialogModel, StatistiekGWT.rb.getString("addacolumn"));
				
				ColumnDialogController dialogController = new ColumnDialogController(
					dialogModel, dialogView);
				dialogController.setHandlerRegistration(handlerRegistration);

				dialogView.center();
				dialogView.show();

				// statTableModel.addColumn() wordt uitgevoerd in onAddColumn()
//				if (dialogModel.getDonePressed())
//				{
//					StatTable.this.statTableModel.addColumn(dialogModel.getName(),
//						new ColumnType(dialogModel));
//				}
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
					
					// send an event
					TableChangeEvent event = new TableChangeEvent(TableChangeEvent.REMOVE_ROWS, -1);
					StatTable.this.statTableModel.fireEvent(event);
				}
				
				// reset selection
				StatTable.this.clearSelectionModel();
			}
			else if (e.getSource() == StatTable.this.pasteButton)
			{
				StatTable.this.pasteDataDialog.show();
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
	    			StatTable.this.statTableModel.clearStringFrequencies();

	    			// clear selectionList and listeners
	    			StatTable.this.statTableModel.clearSelectionList();
	    			//StatTable.this.statTableModel.clearHandlers();
	    			
	    			// remove views (and their occurrences as handler)
	    			statInteractiePanel.getStatModel().removeViewsWithoutEvent();
	    
	    			// System.out.println("reset clicked! this.statInteractiePanel.getModel().getResetHashtable()="
	    			// + resetHashtable);
	    
	    			// Complete reset met zetOpdracht()
	    			StatTable.this.statInteractiePanel.getView().getController()
	    				.zetOpdracht(resetHashMap, null, null);
				} // else the button is clicked in edit-mode: do nothing
			}
			else if (e.getSource() == StatTable.this.importButton)
			{
				if (StatTable.this.statTableModel.getRowCount() > 0)
				{
					StatTable.this.importBox.center();
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
			else if (e.getSource() == StatTable.this.fileUploadSelectButton)
			{
				FileList fileList = StatTable.this.fileUpload.getFiles();

				StatTable.this.popupFileUploadPanel.hide();
				
				// test syl: TODO clear the table; werkt nog niet
				StatTable.this.pager.startLoading();
				
				// Remove old views
				StatTable.this.removeViews();
				StatTable.this.statInteractiePanel.getView().removeViewTabs();
				
				StatTable.this.processCSVDataFile(fileList);
				
				StatTable.this.table.setVisible(true);
			}
			else if (e.getSource() == StatTable.this.fileUploadCancelButton)
			{
				StatTable.this.popupFileUploadPanel.hide();				
			}
		}

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
			this.setCellStyleNames(statistiekCss.dataGridCell());
		}

		public StatTableColumn(SelectionCell enumCell, ColumnType type)
		{
			super(enumCell);
			this.columnIndex = StatTable.this.getTempColumnIndex();
			// a selectioncell is always type enum
			this.type = type;
			this.setCellStyleNames(statistiekCss.dataGridCell());
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
	
	
	class ImportMessageDialogBox extends DialogBox
	{
		private Label message = new Label();
		private Button okButton = new Button(
			StatistiekGWT.rb.getString("OKButtonText"));
		private Button cancelButton = new Button(
			StatistiekGWT.rb.getString("cancelButtonText"));

		public ImportMessageDialogBox(Label label)
		{
			super();
			setTitle(label.getText());
			message.setText(StatistiekGWT.rb.getString("importWarning"));
			okButton.addClickHandler(StatTable.this.clickHandler);
			cancelButton.addClickHandler(StatTable.this.clickHandler);

			HorizontalPanel buttonPanel = new HorizontalPanel();
			//buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			buttonPanel.add(okButton);
			buttonPanel.add(cancelButton);

			VerticalPanel vPanel = new VerticalPanel();
			vPanel.add(message);
			vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			vPanel.add(buttonPanel);

			setWidget(vPanel);
		}
	} // class ImportDialogBox
	
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
	 * @author borku102
	 *
	 */
	static class StatTableInputCell extends TextInputCell
	{
	    private static Template template;

	    interface Template extends SafeHtmlTemplates
	    {   
	        // {0}, {1}, {2} relate to value, size, style
//	        @Template("<input type=\"text\" value=\"{0}\" tabindex=\"-1\" size=\"{1}\" maxlength=\"{1}\" style=\"{2}\"></input>")
//	        SafeHtml input(String value, String size, String style);

	    	// {0}, {1} relate to value, style
	        @Template("<input type=\"text\" value=\"{0}\" tabindex=\"-1\" style=\"{1}\"></input>")
	        SafeHtml input(String value, String style);
	    }

	    public StatTableInputCell()
	    {
	        template = GWT.create(Template.class);
	    }

	    @Override
	    public void render(Context context, String value, SafeHtmlBuilder sb)
	    {
	        // Get the view data.
	        Object key = context.getKey();
	        ViewData viewData = getViewData(key);
	        if(viewData != null && viewData.getCurrentValue().equals(value))
	        {
	            clearViewData(key);
	            viewData = null;
	        }

	        String s = (viewData != null) ? viewData.getCurrentValue() : value;
	        if(s != null)
	        {
	            // set value, style
	            sb.append(template.input(s, StatTable.STANDARD_CELL_WIDTH_STYLE));
	        }
	        else
	        {
	            sb.appendHtmlConstant("<input type=\"text\" tabindex=\"-1\"></input>");
	        }
	    }
	} // class StatTableInputCell

	
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

		interface Template extends SafeHtmlTemplates
		{
			// {0} relates to style
			@Template("<select tabindex=\"-1\" style=\"{0}\">")
			SafeHtml beginSelect(String style);

			@Template("<option value=\"{0}\">{1}</option>")
	        SafeHtml deselected(int hash, String option);

	        @Template("<option value=\"{0}\" selected=\"selected\">{1}</option>")
	        SafeHtml selected(int hash, String option);

			@Template("</select>")
			SafeHtml endSelect();
		}

		public StatTableSelectionCell(List<String> options)
		{
			super(options);
			template = GWT.create(Template.class);

			this.options = new ArrayList<String>(options);
			int index = 0;
			for (String option : options)
			{
				indexForOption.put(option, index++);
			}
		}

		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb)
		{
			// set style
			sb.append(template.beginSelect(StatTable.STANDARD_CELL_WIDTH_STYLE));

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
		}

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
	        if(viewData != null && viewData.equals(value))
	        {
	            clearViewData(key);
	            viewData = null;
	        }

	        Boolean b = (viewData != null) ? viewData : value;

	        // set value, style
	        if (b.booleanValue())
	        	sb.append(template.inputChecked(StatTable.CHECKBOX_CELL_WIDTH_STYLE));
	        else
	        	sb.append(template.inputUnchecked(StatTable.CHECKBOX_CELL_WIDTH_STYLE));
		}

	} // class StatTableSelectionCell
	
	@Override
	public void onTableChange(TableChangeEvent event)
	{
		GWT.log("StatTable.onTableChange()");
		this.update();
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
//				row.add((String) values.get(i).get(j)); // geeft ClassCastException: Ljava.lang.Object; cannot be cast to java.lang.String
				row.add(values.get(i).get(j).toString());
			}
			
			row.add("" + i); // add row number to the end as a key
			rows.add(row);
		}
		
		this.updateColumns();
		
		// add the data
		this.dataProvider.getList().clear();;
		this.dataProvider.getList().addAll(rows); // zonder rowNumber op eind van rij?
		this.dataProvider.refresh();
		this.dataProvider.flush();
		this.table.redraw(); // nodig om te tonen in tabLayoutPanel
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
		
		StatTableCheckboxCell cell = new StatTableCheckboxCell(true, true);//false); 

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
				if (s.size() == table.getColumnCount() - 1) // table has extra checkbox column, so -1
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
					GWT.log("StatTable.updateColumns(): checkbox, rowIndex = "
						+ rowIndex + ", value = " + value
						+ ", columnIndex = " + 0);
					
					// setSelectionList van StatTableModel 
					StatTable.this.statTableModel.setSelected(rowIndex, value, SelectionChangeEvent.STAT_TABLE);
					// test syl
					selectionModel.setSelected(s, value);
				}
			});
		
		this.table.addColumn(checkColumn,
			SafeHtmlUtils.fromSafeConstant("<br/>"));
		this.table.setColumnWidth(checkColumn, StatTable.CHECKBOX_COLUMN_WIDTH, Unit.PX);
		// add the column's width to total width
		totalWidth = totalWidth + StatTable.CHECKBOX_COLUMN_WIDTH;

		// put the data of statTableModel into this.table
		headers = this.statTableModel.getColumnNames();

		for (int i = 0; i < this.statTableModel.getColumnCount(); i++)
		{
			this.setTempColumnIndex(i);

			// check column type
			if (this.statTableModel.getColumnTypes().get(i).getType()
				.equals(AllowedTypes.ENUM))
			{
				String[] enumOptions = StatTable.this.statTableModel
					.getColumnTypes().get(i).getEnumOptions();

				SelectionCell enumCell = new StatTableSelectionCell(
					Arrays.asList(enumOptions));

				ColumnType type = this.statTableModel.getColumnTypes().get(i);
				
				Column<List<String>, String> enumColumn = new StatTableColumn(enumCell, type);

				enumColumn
					.setFieldUpdater(new FieldUpdater<List<String>, String>()
					{
						int columnIndex = StatTable.this.getTempColumnIndex();

						@Override
						public void update(int rowIndex, List<String> s,
							String value)
						{
							GWT.log("StatTable.updateColumns(): rowIndex = "
								+ rowIndex + ", s = " + s + ", value = " + value
								+ ", columnIndex = " + columnIndex);
							StatTable.this.statTableModel.setValueAt(value,
								rowIndex, columnIndex);
							// TODO test syl: scroll to the edit position; geeft JavaScriptException
							//table.getRowElement(table.getVisibleItems().indexOf(value)).getCells().getItem(columnIndex).scrollIntoView();
						}
					});
				
				enumColumn.setSortable(true);
				enumColumn.setCellStyleNames(statistiekCss.selectioncell());
				
				// test syl: voor iedere kolom een kolomheader met clickabletextcell
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

				columnHeader.setUpdater(new ValueUpdater<String>()
				{
					int columnIndex = StatTable.this.getTempColumnIndex();

					@Override
					public void update(String value)
					{
//						Window.alert("Header " + this.columnIndex + " clicked!");
						// set the popup index
						StatTable.this.popUpColumnIndex = columnIndex; //StatTable.this.table.convertColumnIndexToModel(column);
						
						popupMenu.setVisible(true);
						// get position of current column
						int x = table.getRowElement(0).getCells().getItem(columnIndex + 1).getAbsoluteLeft();
	                    int y = table.getRowElement(0).getCells().getItem(columnIndex + 1).getAbsoluteTop();
						popupMenu.setPopupPosition(x, y);
						popupMenu.show();
					}
				});

				this.table.addColumn(enumColumn, columnHeader);//headers.get(i));
				this.table.setColumnWidth(enumColumn, StatTable.STANDARD_COLUMN_WIDTH, Unit.PX);//10.0, Unit.EM);
				// add the column's width to total width
				totalWidth = totalWidth + StatTable.STANDARD_COLUMN_WIDTH;
			} // ENUM
			else
			{
				//AllowedTypes type = this.statTableModel.getColumnTypes().get(i).getType();
				ColumnType type = this.statTableModel.getColumnTypes().get(i);
				Column<List<String>, String> column = new StatTableColumn(new StatTableInputCell(), type);//new TextInputCell(), type);

				column.setFieldUpdater(new FieldUpdater<List<String>, String>()
				{
					int columnIndex = StatTable.this.getTempColumnIndex();

					@Override
					public void update(int rowIndex, List<String> dataRow,
						String value)
					{
						GWT.log("StatTable.updateColumns(): rowIndex = " + rowIndex 
								+ ", s = " + dataRow + ", value = " + value
								+ ", columnIndex = " + columnIndex);
						StatTable.this.statTableModel.setValueAt(value, rowIndex, columnIndex);
						// TODO test syl: scroll to the edit position; geeft JavaScriptException 
						// kan de eigenschap compareDocumentPosition van een niet-gedefinieerde verwijzing of een verwijziging naar een lege waarde niet ophalen
						//table.getRowElement(rowIndex).getCells().getItem(columnIndex).scrollIntoView(); // Geeft exception: is het probleem dat ik rowIndex aan de data heb toegevoegd?
						
						// dataRow is niet geupdate met de nieuwe value!
//						dataProvider.flush(); // force immediate update of datagrid table
//						table.getRowElement(18).getCells().getItem(columnIndex).scrollIntoView();
					}
				});

				column.setSortable(true);
				column.setCellStyleNames(statistiekCss.textinputcell());
				
				// test syl: voor iedere kolom een kolomheader met clickabletextcell
				Header<String> columnHeader = new Header<String>(new ClickableTextCell()) 
				{
					int columnIndex = StatTable.this.getTempColumnIndex();

					@Override
				    public String getValue() 
					{
				        return headers.get(this.columnIndex);
				    }
				};

				columnHeader.setUpdater(new ValueUpdater<String>()
				{
					int columnIndex = StatTable.this.getTempColumnIndex();

					@Override
					public void update(String value)
					{
//						Window.alert("Header " + this.columnIndex + " clicked!");
						// set the popup index
						StatTable.this.popUpColumnIndex = columnIndex; //StatTable.this.table.convertColumnIndexToModel(column);
						
						popupMenu.setVisible(true);
						// get position of current column (columnIndex + 1 in table because of the checkbox column)  
						int x = table.getRowElement(0).getCells().getItem(columnIndex + 1).getAbsoluteLeft();
	                    int y = table.getRowElement(0).getCells().getItem(columnIndex + 1).getAbsoluteTop();
						popupMenu.setPopupPosition(x, y);
						popupMenu.show();
					}
				});

				this.table.addColumn(column, columnHeader);//headers.get(i));
				this.table.setColumnWidth(column, StatTable.STANDARD_COLUMN_WIDTH, Unit.PX);//10.0, Unit.EM);
				// add the column's width to total width
				totalWidth = totalWidth + StatTable.STANDARD_COLUMN_WIDTH;				
			}
		} // for-loop over columns
		
		// set minimum table width to enable horizontal scrollbar
	    this.table.setMinimumTableWidth(totalWidth, Unit.PX);
		
		// test syl
//		AsyncHandler columnSortHandler = new ColumnSortEvent.AsyncHandler(table)
//		{
//			@Override
//			public void onColumnSort(ColumnSortEvent event)
//			{
////				GWT.log("StatTable.updateColumns().onColumnSort(): ((StatTableColumn) event.getColumn()).getColumnIndex() = " + ((StatTableColumn) event.getColumn()).getColumnIndex());
//				
////				List<? extends List<String>> newData = new ArrayList(table.getVisibleItems());
//				List<List<String>> newData = new ArrayList<List<String>>(table.getVisibleItems());
//				if (event.isSortAscending())
//				{
//					Collections.sort(newData, (Comparator) event.getColumn()); // dit roept StatTableColumn.compare() aan 
//				}
//				else
//				{
//					Collections.sort(newData, (Comparator) event.getColumn()); // dit roept StatTableColumn.compare() aan
//					Collections.reverse(newData);
//				}
//				table.setRowData(newData);
//			}
//		};
        
		// add handler for right mouse click
		this.table.addHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event)
			{
		        //Cell cell = StatTable.this.table.getCellForEvent(event);
		        int button = event.getNativeEvent().getButton();
		        
		        if (button == NativeEvent.BUTTON_LEFT) 
		        {
//		        	System.out.println("StatTable.updateColumns().onMouseDown(): left!");
		            //doLeftClick(cell);
		        }
		        else if (button == NativeEvent.BUTTON_RIGHT) 
		        {
//		        	System.out.println("StatTable.updateColumns().onMouseDown(): right!");
		            event.preventDefault();
		            //doRightClick(cell);
		        }
			}
		}, MouseDownEvent.getType());
		
		this.table.addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
		        //Cell cell = StatTable.this.table.getCellForEvent(event);
		        int button = event.getNativeEvent().getButton();
		        
		        if (button == NativeEvent.BUTTON_LEFT) 
		        {
//		        	System.out.println("StatTable.updateColumns().onClick(): left!");
		            //doLeftClick(cell);
		        }
		        else if (button == NativeEvent.BUTTON_RIGHT) 
		        {
//		        	System.out.println("StatTable.updateColumns().onClick(): right!");
		            event.preventDefault();
		            //doRightClick(cell);
		        }
			}
		}, ClickEvent.getType());
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
	 * Set the views width.
	 */
	public void setWidth(int w)
	{
		this.width = w;
	}
	
	/**
	 * Set the views height.
	 */
	public void setHeight(int h)
	{
		this.height = h;
	}
}
