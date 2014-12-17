package fi.statistiekgwt.client;

import java.util.ArrayList;

import com.google.gwt.canvas.dom.client.CssColor;
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
	private int maxLength;

	public static final int LABEL_HEIGHT = 20;
	public static final int COLOR_PREVIEW_WIDTH = 30;
	public static final int LABEL_HGAP = 10;

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
		ArrayList<CssColor> splitColors)
	{
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
		this.makeLabels();
		this.updatePreferredSize();
		this.placeComponents();
		this.scrollPanel = new ScrollPanel(this.labelsPanel);
		super.add(this.scrollPanel);//, BorderLayout.CENTER);

		// set position
		super.setWidgetLeftWidth(this.columnLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		super.setWidgetTopHeight(this.columnLabel, 0, Style.Unit.PX, LABEL_HEIGHT, Style.Unit.PX);
		super.setWidgetLeftWidth(this.scrollPanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		super.setWidgetTopHeight(this.scrollPanel, 30, Style.Unit.PX, 100, Style.Unit.PCT);
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
	 * Create the JLabels and add them to labelsPanel
	 */
	private void makeLabels()
	{
		this.labels = new ArrayList<Label>();
		this.colorPreviews = new ArrayList<Label>();

		if (this.splitColors != null && this.splitStrings != null)
		{

			int count = 0;
			for (String s : this.splitStrings)
			{
				Label label = new Label(s);
				this.labels.add(label);
				this.labelsPanel.add(label);
				this.labelsPanel.setWidgetLeftWidth(label, 0, Style.Unit.PCT, 50, Style.Unit.PCT);
				this.labelsPanel.setWidgetTopHeight(label, count * LABEL_HEIGHT, Style.Unit.PX, LABEL_HEIGHT, Style.Unit.PX);
				count++;
			}

			// reset count
			count = 0;
			for (CssColor c : this.splitColors)
			{
				Label label = new Label();
				this.colorPreviews.add(label);
				this.labelsPanel.add(label);
				label.getElement().getStyle().setBackgroundColor(c.toString()); // Het kleurvakje
				this.labelsPanel.setWidgetLeftWidth(label, 50, Style.Unit.PCT, 50, Style.Unit.PCT);
				this.labelsPanel.setWidgetTopHeight(label, count * LABEL_HEIGHT, Style.Unit.PX, LABEL_HEIGHT, Style.Unit.PX);
				count++;
			}
		}
	}

	/**
	 * Place all components at the right location
	 */
	private void placeComponents()
	{
		if (this.splitStrings == null || this.splitColors == null)
		{
			return;
		}
//		this.labelsPanel.setSize(String.valueOf(super.getOffsetWidth() - 5),
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
		this.maxLength = 0;
//		for (Label label : this.labels)
//		{
//			this.maxLength = Math.max(this.maxLength,
//				Integer.parseInt(label.getElement().getStyle().getWidth()));
//		}
//		super.getElement().getStyle().setHeight(this.maxLength
//			+ COLOR_PREVIEW_WIDTH + 20, Unit.PX);
//		super.getElement().getStyle().setWidth(0, Unit.PX); // Waarom 0?
	}

	public void setBounds(String width, String height)//(Rectangle r)
	{
		super.setSize(width, height);
		this.placeComponents();
	}
}
