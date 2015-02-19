package fi.statistiekgwt.client;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Legend component
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class ColorLegend extends LayoutPanel
{
	private String columnName;
	private ArrayList<String> splitStrings;
	private ArrayList<CssColor> splitColors;

	private ArrayList<Label> labels;
	private ArrayList<Label> colorPreviews;

	private Label columnLabel;
	private LayoutPanel labelsPanel;
	private ScrollPanel scrollPanel;
	private int maxWidth;

	public static final int LABEL_HEIGHT = 35;//20;
	public static final int COLOR_PREVIEW_WIDTH = 30;
	public static final int LABEL_HGAP = 10;

	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;
	/**
	 * Constructor
	 * 
	 * @param columnName
	 *            Name of the split column
	 * @param splitStrings
	 *            Names of the split groups
	 * @param splitColors
	 *            Colors of the split groups
	 */
	public ColorLegend(String columnName, ArrayList<String> splitStrings,
		ArrayList<CssColor> splitColors, int width, int height)
	{
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

//		super(new BorderLayout());
		super.getElement().getStyle().setBackgroundColor(CssColor.make(240, 240, 240).toString());
		this.columnName = columnName;
		this.columnLabel = new Label(this.columnName);
//		this.columnLabel.getElement().getStyle().setFontSize(12.0, Unit.PX);
		super.add(this.columnLabel);//, BorderLayout.NORTH);

		this.splitStrings = splitStrings;
		this.splitColors = splitColors;
		this.labelsPanel = new LayoutPanel();
		this.labelsPanel.getElement().getStyle().setBackgroundColor(CssColor.make(240, 240, 240).toString());
//		this.labelsPanel.setSize("100%", "100%");
		this.labelsPanel.setPixelSize(width, height);
		this.makeLabels();
		this.updatePreferredSize();
		this.placeComponents();
		this.scrollPanel = new ScrollPanel(this.labelsPanel);
		this.scrollPanel.setSize(width + "px", height + "px");
//		this.scrollPanel.setSize("10em", "100%"); // dit werkt niet
//		this.scrollPanel.setSize("100%", "100%"); // dit werkt niet
		super.add(this.scrollPanel);//, BorderLayout.CENTER);

		// set position
		super.setWidgetLeftWidth(this.columnLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		super.setWidgetTopHeight(this.columnLabel, 0, Style.Unit.PX, LABEL_HEIGHT, Style.Unit.PX);
		super.setWidgetLeftWidth(this.scrollPanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		super.setWidgetTopHeight(this.scrollPanel, 30, Style.Unit.PX, 70, Style.Unit.PCT);
	}

	/**
	 * Set new split groups
	 * 
	 * @param splitStrings
	 *            names of the split groups
	 * @param splitColors
	 *            colors of the split groups
	 */
	public void setColors(ArrayList<String> splitStrings,
		ArrayList<CssColor> splitColors)
	{
		this.splitStrings = splitStrings;
		this.splitColors = splitColors;
		this.removeAllWidgetsFromPanel(this.labelsPanel);
		this.makeLabels();
		this.updatePreferredSize();
		this.placeComponents();
	}

	/**
	 * Remove all widgets from the given panel
	 * @param labelsPanel2
	 */
	private void removeAllWidgetsFromPanel(LayoutPanel panel)
	{
		int count = panel.getWidgetCount();
		for (int i = count - 1; i > -1; i--)
		{
			panel.remove(i);
		}
	}

	/**
	 * Update the column name
	 * 
	 * @param columnName
	 *            Name of the split column
	 */
	public void setColumnString(String colummName)
	{
		// System.out.println("columnString set to " + columnName);
		this.columnName = colummName;
		this.columnLabel.setText(this.columnName);
	}

	/**
	 * Create the labels and add them to labelsPanel
	 */
	private void makeLabels()
	{
		this.labels = new ArrayList<Label>();
		this.colorPreviews = new ArrayList<Label>();

		if (this.splitColors != null && this.splitStrings != null)
		{

			for (String s : this.splitStrings)
			{
				Label label = new Label(s);
				//label.getElement().getStyle().setFontSize(14, Unit.PX);
				label.addStyleName(statistiekCss.colorlegendlabel());
				this.labels.add(label);
				this.labelsPanel.add(label);
			}
			
			// set field maxWidth in order to position the labels
			this.setMaxWidth();
			
			int count = 0;
			for (Label label : this.labels)
			{
				this.labelsPanel.setWidgetLeftWidth(label, 0, Style.Unit.PX, this.maxWidth + ColorLegend.LABEL_HGAP, Style.Unit.PX);
				this.labelsPanel.setWidgetTopHeight(label, count * LABEL_HEIGHT, Style.Unit.PX, LABEL_HEIGHT, Style.Unit.PX);
				count++;
			}

			// reset count
			count = 0;
			
			// add the color boxes
			for (CssColor c : this.splitColors)
			{
				Label colorLabel = new Label();
				this.colorPreviews.add(colorLabel);
				this.labelsPanel.add(colorLabel);
				colorLabel.getElement().getStyle().setBackgroundColor(c.toString()); // Het kleurvakje
				this.labelsPanel.setWidgetLeftWidth(colorLabel, this.maxWidth + ColorLegend.LABEL_HGAP, Style.Unit.PX, ColorLegend.COLOR_PREVIEW_WIDTH, Style.Unit.PX);
				this.labelsPanel.setWidgetTopHeight(colorLabel, count * LABEL_HEIGHT, Style.Unit.PX, LABEL_HEIGHT, Style.Unit.PX);
				count++;
			}
			
//			this.labelsPanel.setPixelSize(this.maxWidth + ColorLegend.LABEL_HGAP + ColorLegend.COLOR_PREVIEW_WIDTH, (count + 1) * LABEL_HEIGHT);
		}
	}

	/**
	 * Place all components at the right location
	 */
	private void placeComponents()
	{
		// test syl: dit is toch al gebeurd in makeLabels()??
		
		if (this.splitStrings == null || this.splitColors == null)
		{
			return;
		}

//		this.labelsPanel.setSize(String.valueOf(Math.max(super.getOffsetWidth() - 5, 0)),
//			String.valueOf(this.splitStrings.size() * LABEL_HEIGHT
//				+ (this.splitStrings.size() - 1) * LABEL_HGAP));
//		for (int i = 0; i < this.splitStrings.size(); i++)
//		{
//			this.labels.get(i).getElement().getStyle().setLeft(5, Unit.PX); // x
//			this.labels.get(i).getElement().getStyle().setTop(
//				i * (LABEL_HEIGHT + LABEL_HGAP) + 5, Unit.PX); // y
//			this.labels.get(i).getElement().getStyle().setWidth(
//				this.maxLength, Unit.PX); // w
//			this.labels.get(i).getElement().getStyle().setHeight(
//				LABEL_HEIGHT, Unit.PX); // h
//
////			this.colorPreviews.get(i).setBounds(this.maxLength + 10,
////				i * (LABEL_HEIGHT + LABEL_HGAP) + 5, COLOR_PREVIEW_WIDTH,
////				LABEL_HEIGHT);
//			this.colorPreviews.get(i).getElement().getStyle().setLeft(
//				this.maxLength + 10, Unit.PX); // x
//			this.colorPreviews.get(i).getElement().getStyle().setTop(
//				i * (LABEL_HEIGHT + LABEL_HGAP) + 5, Unit.PX); // y
//			this.colorPreviews.get(i).getElement().getStyle().setWidth(
//				COLOR_PREVIEW_WIDTH, Unit.PX); // w
//			this.colorPreviews.get(i).getElement().getStyle().setHeight(
//				LABEL_HEIGHT, Unit.PX); // h
//		}
	}

	/**
	 * Set the preferred size
	 */
	private void updatePreferredSize()
	{
//		super.getElement().getStyle().setHeight((this.labels.size() + 1) * this.LABEL_HEIGHT, Unit.PX);
//		super.getElement().getStyle().setWidth(this.maxLength + COLOR_PREVIEW_WIDTH + 20, Unit.PX);

//		this.labelsPanel.getElement().getStyle().setHeight((this.labels.size() + 1) * this.LABEL_HEIGHT, Unit.PX);
//		this.labelsPanel.getElement().getStyle().setWidth((this.maxWidth + COLOR_PREVIEW_WIDTH) + this.LABEL_HGAP, Unit.PX);
		//this.labelsPanel.setPixelSize((this.labels.size() + 1) * ColorLegend.LABEL_HEIGHT, (this.maxWidth + COLOR_PREVIEW_WIDTH) + ColorLegend.LABEL_HGAP);
	}
	
	private void setMaxWidth()
	{
		TextMetrics metrics;
		Canvas canvas = Canvas.createIfSupported();
		Context2d context = canvas.getContext2d();
		// get the labels's font
		String font = labels.get(0).getElement().getStyle().getFontSize() + " sans-serif";
		context.setFont(font);
//		System.out.println("ColorLegend.setMaxWidth(): context.font = " + context.getFont() 
//			+ ", label.fontSize = " + labels.get(0).getElement().getStyle().getFontSize());
		this.maxWidth = 0;
		
//		for (Label label : this.labels)
//		{
//			metrics = context.measureText(label.getText());
//			this.maxWidth = (int) Math.max(this.maxWidth, metrics.getWidth());
//		}

		if (this.splitStrings != null)
		{
			for (String s : this.splitStrings)
			{
				metrics = context.measureText(s);
				this.maxWidth = (int) Math.max(this.maxWidth, metrics.getWidth());
			}
		}
	}

	public void setBounds(String width, String height)//(Rectangle r)
	{
		super.setSize(width, height);
		this.placeComponents();
	}
}
