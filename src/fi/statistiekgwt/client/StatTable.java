package fi.statistiekgwt.client;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;

import fi.statistiekgwt.client.columndialog.ColumnDialogController;
import fi.statistiekgwt.client.columndialog.ColumnDialogModel;
import fi.statistiekgwt.client.columndialog.ColumnDialogView;
import fi.statistiekgwt.client.event.AddColumnEvent;
import fi.statistiekgwt.client.event.AddColumnEventHandler;
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
public class StatTable extends DockLayoutPanel implements StatistiekView, TableChangeEventHandler
//	,TableModelListener, ActionListener, ListSelectionListener,
//	SelectionListener
{
	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;
	private static final String DELIMITER = ";";
    
	public static final ProvidesKey<List<String>> KEY_PROVIDER = new ProvidesKey<List<String>>()
	{
		@Override
		public Object getKey(List<String> s)
		{
			// the last item in s is the rowNumber
			return s == null ? null : s.get(s.size()-1);

		}
	};

	private StatTableModel statTableModel;

	// Include field statInteractiePanel to process the reset actions
	private StatInteractiePanel statInteractiePanel;

//	private CellTable<Object> table;
//	private CellTable<List<String>> table;
	private DataGrid<List<String>> table; // datagrid provides fixed header and footer section
	protected ListDataProvider<List<String>> dataProvider;
//	private SimplePager pager;
	private MultiSelectionModel<List<String>> selectionModel;
	
	private String viewName;
	private PopupPanel popupMenu;
	private MenuBar menuBar;
	private MenuItem sortItem;
	private MenuItem editItem;
	private MenuItem deleteItem;
	private Command sortCommand;
	private Command editCommand;
	private Command deleteCommand;
	private int popUpColumnIndex;
	private CellTable<Object> rowTable; // nodig? Was om regelnummers te tonen in de tabel
	//private ScrollPanel scrollPanel; // niet nodig met DataGrid

	private LayoutPanel editDataPanel;
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
	FileReader reader;
	Blob blob;
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
		this.statTableModel.addTableChangeEventHandler(this);

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
		this.statTableModel.addTableChangeEventHandler(this);

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
		setUpFileUpload();
		
		//this.setPixelSize(800, 400);
		this.getElement().getStyle().setBackgroundColor("tomato");
		
		//this.scrollPanel = new ScrollPanel();
//		this.table = new CellTable<Object>();//(this.statTableModel)
//		this.table = new CellTable<List<String>>();
		this.table = new DataGrid<List<String>>();
		//this.table.setMinimumTableWidth(140, Unit.EM);
		this.table.setWidth("100%");
		this.table.setHeight("100%");
		 /*
	     * Do not refresh the headers every time the data is updated. The footer
	     * depends on the current data, so we do not disable auto refresh on the
	     * footer.
	     */
	    this.table.setAutoHeaderRefreshDisabled(true);

		// test syl
		this.table.getElement().getStyle().setBackgroundColor("powderblue");
	    this.dataProvider = new ListDataProvider<List<String>>();
	    // Add the table to the dataProvider.
		this.dataProvider.addDataDisplay(this.table);
		
		// Attach a column sort handler to the ListDataProvider to sort the list.
	    ListHandler<List<String>> sortHandler =
	        new ListHandler<List<String>>(this.dataProvider.getList());
	    this.table.addColumnSortHandler(sortHandler);

	    // Create a Pager to control the table.
//	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
//	    pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
//	    pager.setDisplay(this.table);
//	    VerticalPanel vPanel = new VerticalPanel();
//	    vPanel.add(this.table);
//	    vPanel.add(pager);

	    // Add a selection model so we can select cells.
	    selectionModel =
	        new MultiSelectionModel<List<String>>(KEY_PROVIDER);
	    this.table.setSelectionModel(selectionModel, DefaultSelectionEventManager
	        .<List<String>> createCheckboxManager());	    
//		{
//			protected JTableHeader createDefaultTableHeader()
//			{
//				return new JTableHeader(columnModel)
//				{
//					public String getToolTipText(MouseEvent e)
//					{
//						String tip = null;
//						java.awt.Point p = e.getPoint();
//						int index = columnModel.getColumnIndexAtX(p.x);
//						int realIndex = columnModel.getColumn(index)
//							.getModelIndex();
//						return StatTable.this.statTableModel.getColumnTypes()
//							.get(realIndex).getUitleg();
//					}
//				};
//			}
//		};
		//this.table.getSelectionModel().addListSelectionListener(this);
		//this.statTableModel.addTableModelListener(this);
		//this.statTableModel.addSelectionListener(this);
//		this.scrollPanel.add(this.table);//setViewportView(this.table);
//		this.scrollPanel.setHeight("500px");

//		this.rowTable = new RowNumberTable(this.table);
//		this.scrollPanel.setRowHeaderView(this.rowTable);
//		this.scrollPanel.setCorner(ScrollPanel.UPPER_LEFT_CORNER,
//			this.rowTable.getTableHeader());

		this.menuBar = new MenuBar();
		this.popupMenu = new PopupPanel(true);
		this.popupMenu.add(this.menuBar);
		this.popupMenu.hide();
		
		sortCommand = new Command() {
	        @Override
            public void execute() 
	        {
	        	StatTable.this.statTableModel.sort(StatTable.this.popUpColumnIndex);
            }
        };
        this.createEditCommand();
        deleteCommand = new Command() {
	        @Override
            public void execute() 
	        {
				StatTable.this.statTableModel.removeColumn(StatTable.this.popUpColumnIndex);
            }
        };
		sortItem = new MenuItem(StatistiekGWT.rb.getString("sortItem"), true, sortCommand);
		editItem = new MenuItem(StatistiekGWT.rb.getString("editcolumnItem"), true, editCommand);
		deleteItem = new MenuItem(StatistiekGWT.rb.getString("deletecolumnItem"), true, deleteCommand);
		this.menuBar.addItem(sortItem);
		if (this.statTableModel.isDataEditable())
		{
			this.menuBar.addItem(editItem);
			this.menuBar.addItem(deleteItem);
		}
		//MouseListener popupListener = new PopupListener();
		//this.table.getTableHeader().addMouseListener(popupListener);

		// test syl
		//this.scrollPanel.getElement().getStyle().setBackgroundColor("powderblue");
//		super.add(this.scrollPanel);//, BorderLayout.CENTER); // moet als laatste
//		this.add(this.scrollPanel);//, BorderLayout.CENTER);
//		this.setWidgetLeftWidth(this.scrollPanel, 0, Style.Unit.PX, 800, Style.Unit.PX);
//		this.setWidgetTopHeight(this.scrollPanel, 0, Style.Unit.PX, 370, Style.Unit.PX);

		
		// maak editDataPanel
		this.editDataPanel = new LayoutPanel();
		
		this.importButton = new Button(StatistiekGWT.rb.getString("importButton"));
		//this.importButton.addActionListener(this);
		this.importButton.addClickHandler(this.clickHandler);
		this.editDataPanel.add(this.importButton);
		// set position
		this.editDataPanel.setWidgetLeftWidth(this.importButton, 0, Style.Unit.PX, 130, Style.Unit.PX);
		this.editDataPanel.setWidgetTopHeight(this.importButton, 0, Style.Unit.PX, 30, Style.Unit.PX);

		this.addRowButton = new Button(StatistiekGWT.rb.getString("addrowButton"));
		//this.addRowButton.addActionListener(this);
		this.addRowButton.addClickHandler(this.clickHandler);
		this.editDataPanel.add(this.addRowButton);
		// set position
		this.editDataPanel.setWidgetLeftWidth(this.addRowButton, 130, Style.Unit.PX, 130, Style.Unit.PX);
		this.editDataPanel.setWidgetTopHeight(this.addRowButton, 0, Style.Unit.PX, 30, Style.Unit.PX);
		
		this.addColumnButton = new Button(StatistiekGWT.rb.getString("addcolumnButton"));
		//this.addColumnButton.addActionListener(this);
		this.addColumnButton.addClickHandler(this.clickHandler);
		this.editDataPanel.add(this.addColumnButton);
		// set position
		this.editDataPanel.setWidgetLeftWidth(this.addColumnButton, 260, Style.Unit.PX, 130, Style.Unit.PX);
		this.editDataPanel.setWidgetTopHeight(this.addColumnButton, 0, Style.Unit.PX, 30, Style.Unit.PX);
		
		this.deleteRowsButton = new Button(StatistiekGWT.rb.getString("deleteselectedrowsButton"));
		//this.deleteRowsButton.addActionListener(this);
		this.deleteRowsButton.addClickHandler(this.clickHandler);
		this.editDataPanel.add(this.deleteRowsButton);
		// set position
		this.editDataPanel.setWidgetLeftWidth(this.deleteRowsButton, 390, Style.Unit.PX, 130, Style.Unit.PX);
		this.editDataPanel.setWidgetTopHeight(this.deleteRowsButton, 0, Style.Unit.PX, 30, Style.Unit.PX);
		
		this.pasteButton = new Button(StatistiekGWT.rb.getString("pasteclipboardButton"));
//		this.pasteButton.addActionListener(this);
		this.pasteButton.addClickHandler(this.clickHandler);
		this.editDataPanel.add(this.pasteButton);
		// set position
		this.editDataPanel.setWidgetLeftWidth(this.pasteButton, 520, Style.Unit.PX, 130, Style.Unit.PX);
		this.editDataPanel.setWidgetTopHeight(this.pasteButton, 0, Style.Unit.PX, 30, Style.Unit.PX);
		
		this.resetButton = new PushButton(new Image(statistiekGWTClientBundle.resetResource().getSafeUri()));
		this.resetButton.addStyleName(statistiekCss.pushbutton());
//		this.resetButton.addActionListener(this);
		this.resetButton.addClickHandler(this.clickHandler);
		this.editDataPanel.add(this.resetButton);
		// set position
		this.editDataPanel.setWidgetLeftWidth(this.resetButton, 650, Style.Unit.PX, 
			statistiekGWTClientBundle.resetResource().getWidth() + 11, Style.Unit.PX);
		this.editDataPanel.setWidgetTopHeight(this.resetButton, 0, Style.Unit.PX, 30, Style.Unit.PX);
		
		this.editDataPanel.setVisible(this.statTableModel.isDataEditable());

		// test syl
		this.editDataPanel.getElement().getStyle().setBackgroundColor("lemonchiffon");
		super.addSouth(this.editDataPanel, 3);//, BorderLayout.SOUTH);
//		super.add(this.scrollPanel);//, BorderLayout.CENTER); // moet als laatste
		super.add(this.table);//, BorderLayout.CENTER); // moet als laatste
//		super.add(vPanel);
		//this.add(this.editDataPanel);//, BorderLayout.SOUTH);
//		this.setWidgetLeftWidth(this.editDataPanel, 0, Style.Unit.PX, 800, Style.Unit.PX);
//		this.setWidgetTopHeight(this.editDataPanel, 370, Style.Unit.PX, 30, Style.Unit.PX);
		
		Label label = new Label(StatistiekGWT.rb.getString("importDialogLabel"));
	    importBox = new ImportMessageDialogBox(label);
	    
		this.update();
		// set the right selection
		this.selectionChanged();
	}
	
	private void createEditCommand()
	{
        editCommand = new Command() 
        {
	        @Override
            public void execute() 
	        {
	        	StatTable.this.popupMenu.setVisible(false);
	        	
	        	ArrayList<ColumnType> list = StatTable.this.statTableModel.getColumnTypes();
				ColumnDialogModel m = new ColumnDialogModel(
					StatTable.this.statTableModel,
					StatTable.this.statTableModel.getColumnName(StatTable.this.popUpColumnIndex),
					list.get(popUpColumnIndex),
					StatTable.this.popUpColumnIndex);
				ColumnDialogView v;

				Widget container = StatistiekGWT.getTopLevelAncestor(StatTable.this);
				if (container instanceof Frame)
				{
					v = new ColumnDialogView((Frame) container, m);
				}
				else if (container instanceof DialogBox)
				{
					v = new ColumnDialogView((DialogBox) container, m);
				}
				else
				{
					System.out.println("Error finding top level frame/dialog.");
					return;
				}
				ColumnDialogController c2 = new ColumnDialogController(m, v);

				v.setVisible(true);

				if (m.getDonePressed())
				{
					StatTable.this.statTableModel.editColumn(StatTable.this.popUpColumnIndex,
						m.getName(), new ColumnType(m));
				}
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
		panel.add(fileUploadSelectButton);
		panel.add(fileUploadCancelButton);
		this.popupFileUploadPanel.add(panel);
		this.popupFileUploadPanel.hide();
		this.add(this.popupFileUploadPanel);
		
		this.formPanel = new FormPanel(); 
		this.formPanel.setAction(com.google.gwt.core.client.GWT.getModuleBaseURL() + "/myFormHandler");// TODO hier moet iets mee...
		this.formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		this.formPanel.setMethod(FormPanel.METHOD_POST);
		this.formPanel.setWidget(this.popupFileUploadPanel);
		this.addEast(this.formPanel, 0);
		
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
//			int column = StatTable.this.table.columnAtPoint(p);
//
//			// convert view index to model index
//			StatTable.this.popUpColumnIndex = column; //StatTable.this.table.convertColumnIndexToModel(column);
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
		// System.out.println("Table changed");

//		if (e.getFirstRow() == TableModelEvent.HEADER_ROW
//			&& this.statTableModel != null)
//		{
//			// a HEADER_ROW CHANGED TableModelEvent resets the cellRenderers, so
//			// set them again.
//			this.setCellRenderers();
//		}

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
		//this.statTableModel.addTableModelListener(this);
		//this.statTableModel.addSelectionListener(this);
//		this.table = new CellTable<String>();
//			new JTable(this.statTableModel)
//		{
//			protected JTableHeader createDefaultTableHeader()
//			{
//				return new JTableHeader(columnModel)
//				{
//					public String getToolTipText(MouseEvent e)
//					{
//						String tip = null;
//						java.awt.Point p = e.getPoint();
//						int index = columnModel.getColumnIndexAtX(p.x);
//						int realIndex = columnModel.getColumn(index)
//							.getModelIndex();
//						return StatTable.this.statTableModel.getColumnTypes()
//							.get(realIndex).getUitleg();
//					}
//				};
//			}
//		};
		//this.table.getSelectionModel().addListSelectionListener(this);
		//this.rowTable.setModel(this.statTableModel);

		// beetje raar, nieuwe rowTable...?
		//this.rowTable = new RowNumberTable(this.table);
		//this.scrollPanel.add(this.rowTable);//setRowHeaderView(this.rowTable);
//		this.scrollPanel.add(this.table);//setViewportView(this.table);

//		this.scrollPanel.setCorner(JScrollPane.UPPER_LEFT_CORNER,
//			this.rowTable.getTableHeader());

		ColumnClickHandler columnClickHandler = new ColumnClickHandler(); // was: MouseListener
		//this.table.getTableHeader().addMouseListener(popupListener);

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
	 * Override setBounds
	 */
	public void setBounds(int x, int y, int b, int h)
	{
		//super.setBounds(x, y, b, h);
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
	 * Try to paste clipboard data into the tablemodel. 
	 * Splits data over cells with tabs ('\t') and over rows with newlines ('\n'). 
	 * Will only paste the data into the model if amount of cells at every line 
	 * corresponds with the amount of columns.
	 */
	public void pasteClipboardData()
	{
//		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//		Transferable clipboardData = clipboard.getContents(this);
//		String clipboardString;
//		try
//		{
//			clipboardString = (String) clipboardData
//				.getTransferData(DataFlavor.stringFlavor);
//		}
//		catch (UnsupportedFlavorException e)
//		{
//			System.out.println("UnsupportedFlavorException in AddColmnDialogController.pasteClipboardData");
//			e.printStackTrace();
//			return;
//		}
//		catch (IOException e)
//		{
//			System.out.println("IOException in AddColmnDialogController.pasteClipboardData");
//			e.printStackTrace();
//			return;
//		}
//
//		String[] rowStrings = clipboardString.split("\n");
//
//		// check the amount of cells in each row
//		for (String s : rowStrings)
//		{
//			if (s.split("\t").length != this.statTableModel.getColumnCount())
//			{
//				// row has incorrect amount of cells
//				return;
//			}
//		}
//
//		int currentRow = this.statTableModel.getRowCount();
//		int currentColumn;
//		for (String s : rowStrings)
//		{
//			this.statTableModel.addRowWithoutEvent();
//			currentColumn = 0;
//			for (String cellString : s.split("\t"))
//			{
//				this.statTableModel.setValueAtWithoutEvent(cellString,
//					currentRow, currentColumn);
//				currentColumn++;
//			}
//			currentRow++;
//		}
//
//		this.statTableModel.fireTableModelEvent();
	}

	/*
	 * Opent de dialoog voor het openen van een data bestand.
	 */
	private void openFileChooserDialog()
	{
		this.popupFileUploadPanel.show();
		
//		int returnVal;
//		
//		returnVal = this.fileUpload.showOpenDialog(this);
//		
//		if (returnVal == JFileChooser.APPROVE_OPTION) 
//		{
////			System.out.println("You chose to open this file: " +
////				fileChooser.getSelectedFile().getName());
//			
//			// Remove old views
//			this.removeViews();
//			
//			// remove listeners related to views
//			this.removeViewListeners();
//			
//			processCSVDataFile(fileUpload.getSelectedFile());
//		}
	}

	/**
	 * Remove listeners related to views, except the table view.
	 */
	private void removeViewListeners()
	{
//		// remove table model listeners
//		ArrayList<TableModelListener> listeners = this.statTableModel.getTableModelListeners();
//		
//		for (int i = listeners.size() - 1; i >= 0; i--)
//		{
//			TableModelListener l = listeners.get(i);
//			
//			// check for listeners other than listeners related to the table view
//			if (!(l instanceof StatModel) 
//				&& !(l instanceof StatTable)
//				&& !(l.getClass().getName().equals("fi.statistiek.StatTable$1"))
//				&& !(l instanceof RowNumberTable))
//			{
//				// remove listener related to a view other than table
//				this.statTableModel.removeTableModelListener(l);
//			}
//		}
//		
//		// remove selection listeners
//		ArrayList<SelectionListener> selectionListeners = this.statTableModel.getSelectionListeners();
//		
//		for (int i = selectionListeners.size() - 1; i >= 0; i--)
//		{
//			SelectionListener l = selectionListeners.get(i);
//			
//			// check for listeners other than listeners related to the table view
//			if (!(l instanceof StatTable))
//			{
//				// remove selection listener related to a view other than table
//				this.statTableModel.removeSelectionListener(l);
//			}
//		}
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

//		Scheduler.get().scheduleDeferred(new ScheduledCommand()
//		{
//			@Override
//			public void execute()
//			{
//				
//				GWT.log("StatTable.processCSVDataFile(): csvText is set!");
//
//				Scheduler.get().scheduleDeferred(new ScheduledCommand()
//				{
//					@Override
//					public void execute()
//					{
//						GWT.log("text is read!");
//					}
//				});
//			}
//		});
		
		Timer t = new Timer()
		{
			@Override
			public void run()
			{
				GWT.log("StatTable.processCSVDatFile(): timer.run()");
			}
		};
		t.schedule(4000);

		// process the first file
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

			this.fireEvent("importCSV");
			
			DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
		}
		else
		{
			DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
			GWT.log("CSV file is empty!");
		}
	}
	
	/**
	 * Make statTableModel fire an event.
	 * @param info
	 */
	private void fireEvent(String info)
	{
		// send an event
		TableChangeEvent event = new TableChangeEvent(info);
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

	public void selectionChanged()
	{
		//System.out.println("selection Changed called");
		//ListSelectionModel selectionModel = this.table.getSelectionModel();
		MultiSelectionModel<Object> selectionModel = (MultiSelectionModel<Object>) this.table.getSelectionModel();
		//selectionModel.removeListSelectionListener(this); // moet met HandlerRegistration if needed... 

		for (int row = 0; row < this.statTableModel.getRowCount(); row++)
		{
			Object rowObject = this.statTableModel.getValues().get(row);//this.table.getRowElement(row);
			if (this.statTableModel.isRowSelected(row)
				&& !selectionModel.isSelected(rowObject))
			{
				// System.out.println("Selecting row " + row);
				selectionModel.setSelected(rowObject, true);//addSelectionInterval(row, row);
			}
			else if (!this.statTableModel.isRowSelected(row)
				&& selectionModel.isSelected(rowObject))
			{
				// System.out.println("Deselecting row " + row);
				selectionModel.setSelected(rowObject, false);//removeSelectionInterval(row, row);
			}
		}
		//selectionModel.addListSelectionListener(this); // iets met HasSelectionHandler, onSelection()
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
			}
			else if (e.getSource() == StatTable.this.addColumnButton)
			{
				ColumnDialogModel dialogModel = new ColumnDialogModel(
					StatTable.this.statTableModel);
				// test syl: TODO dit worden er steeds meer! Bestaande verwijderen, ook al is dialogModel nieuw...
				HandlerRegistration handlerRegistration = dialogModel.addAddColumnEventHandler(StatTable.this.statTableModel);
				ColumnDialogView dialogView;

				dialogView = new ColumnDialogView(dialogModel);
				
				// Try to find the top level ancestor (Dialog or Frame)
/*				Widget container = StatistiekGWT.getTopLevelAncestor(StatTable.this);
				if (container instanceof Frame)
				{
					dialogView = new AddColumnDialogView((Frame) container, dialogModel);
				}
				else if (container instanceof DialogBox)
				{
					dialogView = new AddColumnDialogView((DialogBox) container, dialogModel);
				}
				else
				{
					System.out.println("Error finding top level frame/dialog");
					return;
				}
*/				
				ColumnDialogController dialogController = new ColumnDialogController(
					dialogModel, dialogView);
				dialogController.setHandlerRegistration(handlerRegistration);

				//dialogView.setVisible(true);
				dialogView.center();
				dialogView.show();

				// statTableModel.addColumn() wordt uitgevoerd in onAddColumn() van AddColumnEvent
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
					TableChangeEvent event = new TableChangeEvent("removeRows");
					StatTable.this.statTableModel.fireEvent(event);
				}
				
				// reset selection
				StatTable.this.clearSelectionModel();
			}
			else if (e.getSource() == StatTable.this.pasteButton)
			{
				StatTable.this.pasteClipboardData();
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
				
				// Remove old views
				StatTable.this.removeViews();
				
				// remove listeners related to views
				StatTable.this.removeViewListeners();
				
				StatTable.this.processCSVDataFile(fileList);
			}
			else if (e.getSource() == StatTable.this.fileUploadCancelButton)
			{
				StatTable.this.popupFileUploadPanel.hide();				
			}
		}

	} // class StatTableClickHandler
	
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

			VerticalPanel vPanel = new VerticalPanel();
			vPanel.add(message);
			vPanel.add(okButton);
			vPanel.add(cancelButton);
			setWidget(vPanel);
		}
	} // class ImportDialogBox

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
	private void update()
	{
		GWT.log("StatTable.update()");
		
		this.updateColumns();

		ArrayList<ArrayList<Object>> values = this.statTableModel.getValues();
		//GWT.log("StatTable.update(): values.size() = "+ values.size());
		//GWT.log("StatTable.update(): values.get(0) = "+ values.get(0)); // this is a data row
		
		ArrayList<List<String>> columns = new ArrayList<List<String>>();
		
//		for (int j = 0; j < values.get(0).size(); j ++) // j loops over the columns
//		{
//			List<String> column = new ArrayList<String>();
//			for (int i = 0; i < values.size(); i++) // i loops over the rows
//			{
//				column.add((String) values.get(i).get(j));
//			}
//			columns.add(column);
//		}
		
		ArrayList<List<String>> rows = new ArrayList<List<String>>();
		
		for (int i = 0; i < values.size(); i++) // i loops over the rows
		{
			List<String> row = new ArrayList<String>();
			for (int j = 0; j < values.get(0).size(); j ++) // j loops over the columns
			{
				row.add((String) values.get(i).get(j));
			}
			rows.add(row);
		}
		
		dataProvider = new ListDataProvider<List<String>>();
		dataProvider.addDataDisplay(this.table);
		
		// test syl
//		this.dataProvider.getList().addAll(columns);
		this.dataProvider.getList().addAll(rows);
		this.dataProvider.refresh();
		this.dataProvider.flush();
		this.table.setVisibleRange(0, values.size());
		this.table.redraw();
	}

	private void updateColumns()
	{
		// remove columns
		for (int i = this.table.getColumnCount() - 1; i >= 0; i--)
		{
			this.table.removeColumn(i);
		}

		// Checkbox column. This table will uses a checkbox column for
		// selection.
		// Alternatively, you can call dataGrid.setSelectionEnabled(true) to
		// enable
		// mouse selection.
		Column<List<String>, Boolean> checkColumn = new Column<List<String>, Boolean>(
			new CheckboxCell(true, false))
		{
			@Override
			public Boolean getValue(List<String> s)
			{
				// Get the value from the selection model.
				// Add row number to s
				int rowIndex = dataProvider.getList().indexOf(s);
				s.add(String.valueOf(rowIndex));
				return selectionModel.isSelected(s);
			}
		};
		this.table.addColumn(checkColumn,
			SafeHtmlUtils.fromSafeConstant("<br/>"));
		this.table.setColumnWidth(checkColumn, 40, Unit.PX);

		// put the data of statTableModel into this.table
		ArrayList<String> headers = this.statTableModel.getColumnNames();

		for (int i = 0; i < this.statTableModel.getColumnCount(); i++)
		{
			this.setTempColumnIndex(i);

			// check column type
			if (this.statTableModel.getColumnTypes().get(i).getType()
				.equals(AllowedTypes.ENUM))
			{
				String[] enumOptions = StatTable.this.statTableModel
					.getColumnTypes().get(i).getEnumOptions();

				SelectionCell enumCell = new SelectionCell(
					Arrays.asList(enumOptions));

				Column<List<String>, String> enumColumn = new Column<List<String>, String>(
					enumCell)
				{
					int columnIndex = StatTable.this.getTempColumnIndex();

					@Override
					public String getValue(List<String> s)
					{
						return s == null ? "" : s.get(columnIndex);
					}
				};

				enumColumn
					.setFieldUpdater(new FieldUpdater<List<String>, String>()
					{
						int columnIndex = StatTable.this.getTempColumnIndex();

						@Override
						public void update(int rowIndex, List<String> s,
							String value)
						{
							GWT.log("StatTable.updateColumns(): index = "
								+ rowIndex + ", s = " + s + ", value = " + value);
							StatTable.this.statTableModel.setValueAt(value,
								rowIndex, columnIndex);

						}
					});

				enumColumn.setSortable(true);
				enumColumn.setCellStyleNames(statistiekCss.selectioncell());
				this.table.addColumn(enumColumn, headers.get(i));
				this.table.setColumnWidth(enumColumn, 10.0, Unit.EM);
			}
			else
			{
				Column<List<String>, String> column = new Column<List<String>, String>(
					new TextInputCell())
				{
					int columnIndex = StatTable.this.getTempColumnIndex();

					@Override
					public String getValue(List<String> s)
					{
						return s == null ? "" : s.get(columnIndex);
					}
				};

				column.setFieldUpdater(new FieldUpdater<List<String>, String>()
				{
					int columnIndex = StatTable.this.getTempColumnIndex();

					@Override
					public void update(int rowIndex, List<String> s,
						String value)
					{
						GWT.log("StatTable.updateColumns(): index = " + rowIndex + ", s = " + s + ", value = " + value);
						StatTable.this.statTableModel.setValueAt(value, rowIndex, columnIndex);
					}
				});
				column.setSortable(true);
				column.setCellStyleNames(statistiekCss.textinputcell());
				this.table.addColumn(column, headers.get(i));
				this.table.setColumnWidth(column, 10.0, Unit.EM);
			}
		} // for-loop over columns
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
}
