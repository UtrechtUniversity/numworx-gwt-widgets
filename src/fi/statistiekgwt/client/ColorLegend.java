package fi.statistiekgwt.client;

import java.util.ArrayList;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Legend component
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class ColorLegend extends FlowPanel
{
	private String columnName;
	private ArrayList<String> splitStrings;
	private ArrayList<String> splitColors;

	private ArrayList<Label> labels;
	private ArrayList<Label> colorPreviews;

	private Label columnLabel;
	private FlowPanel colorLegendPanel;
	private ScrollPanel scrollPanel;

	public static final int COLOR_PREVIEW_WIDTH = 25;
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
		ArrayList<String> splitColors, int width, int height)
	{
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.addStyleName(statistiekCss.backgroundblue());
		this.columnName = columnName;

		this.splitStrings = splitStrings;
		this.splitColors = splitColors;
		this.colorLegendPanel = new FlowPanel();
		this.colorLegendPanel.addStyleName(statistiekCss.backgroundblue());
		this.makeLabelsPanel();
		
		this.scrollPanel = new ScrollPanel(this.colorLegendPanel);
		this.scrollPanel.addStyleName(statistiekCss.scrollPanel());
		this.scrollPanel.setSize("100%", "100%"); // dit zorgt voor de verticale scrollbar
		this.add(this.scrollPanel);
		this.setPixelSize(width, height);
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
		ArrayList<String> splitColors)
	{
		this.splitStrings = splitStrings;
		this.splitColors = splitColors;
		StatistiekUtils.removeAllWidgetsFromPanel(this.colorLegendPanel);
		this.makeLabelsPanel();
	}

	/**
	 * Update the column name
	 * 
	 * @param columnName
	 *            Name of the split column
	 */
	public void setColumnString(String colummName)
	{
		this.columnName = colummName;
		this.columnLabel.setText(this.columnName);
	}

	/**
	 * Create the labels and add them to labelsPanel
	 */
	private void makeLabelsPanel()
	{
		FlowPanel columnNamePanel = new FlowPanel();
		columnNamePanel.addStyleName(statistiekCss.margin());
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.addStyleName(statistiekCss.horizontalPanelWithoutBorder());
		
		FlowPanel labelsPanel = new FlowPanel();
		FlowPanel colorsPanel = new FlowPanel();
		
		this.labels = new ArrayList<Label>();
		this.colorPreviews = new ArrayList<Label>();

		// first the split variable name
		this.columnLabel = new Label(this.columnName);
		this.columnLabel.addStyleName(statistiekCss.spaceBottomLabel());
		this.columnLabel.addStyleName(statistiekCss.colorlegendlabel());
		columnNamePanel.add(this.columnLabel);
		
		if (this.splitColors != null && this.splitStrings != null)
		{

			for (String s : this.splitStrings)
			{
				Label label = new Label(s);
				label.addStyleName(statistiekCss.splitClassLabel());
				this.labels.add(label);
				labelsPanel.add(label);
			}
			
			// add the color boxes
			for (int i = 0; i < this.splitColors.size(); i++)
			{
				CssColor c = CssColor.make(this.splitColors.get(i));

				Label colorLabel = new Label();
				this.colorPreviews.add(colorLabel);
				colorLabel.addStyleName(statistiekCss.colorlegendlabel());
				colorLabel.getElement().getStyle().setWidth(ColorLegend.COLOR_PREVIEW_WIDTH, Unit.PX);
				colorLabel.getElement().getStyle().setBackgroundColor(c.toString()); // Het kleurvakje

				colorsPanel.add(colorLabel);
			}
			
			horizontalPanel.add(labelsPanel);
			horizontalPanel.add(colorsPanel);
			this.colorLegendPanel.add(columnNamePanel);
			this.colorLegendPanel.add(horizontalPanel);
		}
	}

	public void setBounds(String width, String height)
	{
		super.setSize(width, height);
	}
}
