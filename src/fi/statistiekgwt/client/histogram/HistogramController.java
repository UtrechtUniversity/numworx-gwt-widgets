package fi.statistiekgwt.client.histogram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekView;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * MVC Controller for StatistiekView Histogram
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class HistogramController implements StatistiekView
	//, ActionListener, FocusListener
{
	private HistogramModel model;
	private HistogramView view;

	/**
	 * Constructor
	 * 
	 * @param tableModel
	 *            the data table
	 * @param viewName
	 *            The initial name of the StatistiekView
	 */
	public HistogramController(StatTableModel tableModel, String viewName,
		boolean frequencyPolygonMode, int startVar)
	{
//		System.out.println("HistogramController(): maakt nieuw HistogramModel en daarmee HistogramView");
		
		this.model = new HistogramModel(tableModel, viewName, frequencyPolygonMode);
		model.setColumnIndex(startVar);
		model.setDefaultLabelPositioning();
		this.view = new HistogramView(this.model, this);
		this.view.update();
		
//		System.out.println("... HistogramController(): this.model.binBoundaries=" + this.model.getBinBoundaries());
//		System.out.println("... HistogramController(): identityHashCode(this)=" 
//			+ identityHashCode(this));
//		System.out.println("... HistogramController(): identityHashCode(this.model.getBinBoundaries())=" 
//			+ identityHashCode(this.model.getBinBoundaries()));
	}

	private static int identityHashCode(Object o)
	{
		return System.identityHashCode(o);
	}
	
	/**
	 * @return The view's name
	 */
	public String getViewName()
	{
		return this.model.getViewName();
	}

	public void setViewName(String s)
	{
		this.model.setViewName(s);
	}

	/*
	 * Update the bin boundaries using the settings for the minimum boundary
	 * and the bin width, and determine the number of bins.
	 */
	public void updateBoundariesFromBinSettings()
	{
		ArrayList<Double> boundaries = new ArrayList<Double>();
		
		double min = this.model.getStatTableModel().getColumnMin(
			this.model.getColumnIndex());
		double max = this.model.getStatTableModel().getColumnMax(
			this.model.getColumnIndex());
		
		boundaries = StatistiekGWT.appropriateBoundariesFromBinSettings(
			min,
			max,
			view.getBinWidth(),
			view.getMinBoundary());
		
		// if result is valid, set boundaries
		if (boundaries != null)
		{
			this.model.setBinBoundaries(boundaries);
		}
		else
		{
			// reset to old values
			ArrayList<Double> oldBoundaries = this.model.getBinBoundaries(); 
			this.view.setBinWidth(oldBoundaries.get(1) - oldBoundaries.get(0));
			this.view.setMinBoundary(oldBoundaries.get(0));
		}
	}
	
	/*
	 * Update the split bin boundaries using the settings for the minimum boundary
	 * and the bin width, and determine the number of bins.
	 */
	public void updateSplitBoundariesFromBinSettings()
	{
		ArrayList<Double> boundaries = new ArrayList<Double>();

		double min = this.model.getStatTableModel().getColumnMin(
			this.model.getSplitOptions().getColumnSplitIndex());
		double max = this.model.getStatTableModel().getColumnMax(
			this.model.getSplitOptions().getColumnSplitIndex());
		
		boundaries = StatistiekGWT.appropriateBoundariesFromBinSettings(
			min,
			max,
			view.getSplitBinWidth(),
			view.getSplitMinBoundary());
		
		// if result is valid, set boundaries
		if (boundaries != null)
		{
			this.model.setSplitBoundaries(boundaries);
			//this.view.setModel(this.model); // test syl: waarom moet dit hier en niet bij updateBoundariesFromBinSettings()?
		}
		else
		{
			// reset to old values
			ArrayList<Double> oldBoundaries = this.model.getSplitOptions().getBinBoundaries(); 
			this.view.setSplitBinWidth();
			this.view.setSplitMinBoundary(oldBoundaries.get(0));
		}
	}
	
	/*
	 * Update the bin boundaries with the set number of bins.
	 */
	private void updateBoundaries()
	{
		ArrayList<Double> boundaries = new ArrayList<Double>();
		for (int i = 0; i <= this.model.getNoBins(); i++)
		{
			boundaries.add(new Double(view.getMinBoundary() + i
				* view.getBinWidth()));
		}
		
		this.model.setBinBoundaries(boundaries);
	}

	private void updateSplitBoundaries()
	{
		ArrayList<Double> boundaries = new ArrayList<Double>();
		for (int i = 0; i <= this.view.getSplitBinsBoxSelectedInt(); i++)
		{
			boundaries.add(new Double(view.getSplitMinBoundary() + i
				* view.getSplitBinWidth()));
		}
		this.model.setSplitBoundaries(boundaries);
		this.view.setModel(this.model);
	}

	public void setSplitType(AllowedTypes type)
	{
		if (type.isNumber())
		{
			ArrayList<Double> boundaries = new ArrayList<Double>();
			boundaries = StatistiekGWT.appropriateBoundaries(
				this.model.getStatTableModel().getColumnMin(
					this.model.getSplitOptions().getColumnSplitIndex()),
				this.model.getStatTableModel().getColumnMax(
					this.model.getSplitOptions().getColumnSplitIndex()),
				this.view.getSplitBinsBoxSelectedInt());

			this.model.setSplitBoundaries(boundaries);
			this.model.setSplitOptions(this.model.getSplitOptions());
			this.view.setModel(this.model);
		}
	}

	public String getViewType()
	{
		return (this.model.isFrequencyPolygonMode() ? "Frequentiepolygoon"
			: "Histogram");
	}

	public Object getState()
	{
//		System.out.println("HistogramController.getState()...");

		HashMap h = new HashMap();

		h.put("binBoundaries", this.model.getBinBoundaries());
//		System.out.println("   binBoundaries=" +
//			this.model.getBinBoundaries());

		h.put("columnIndex", this.model.getColumnIndex());
		// System.out.println("   columnIndex=" + this.model.getColumnIndex());

		h.put("percentage", this.model.getPercentage());
		// System.out.println("   percentage=" + this.model.getPercentage());

		h.put("labelUnderBin", this.model.getLabelUnderBin());
		//System.out.println("   labelUnderBin=" + this.model.getLabelUnderBin());

		h.put("showUserOptions", this.model.getShowUserOptions());
		// System.out.println("   showUserOptions=" +
		// this.model.getShowUserOptions());

		h.put("verticalBars", this.model.hasVerticalBars());
		// System.out.println("   verticalBars=" +
		// this.model.getVerticalBars());

		h.put("viewName", this.model.getViewName());
		// System.out.println("   viewName=" + this.model.getViewName());

		h.put("frequencyPolygonCumulativeMode",
			this.model.isFrequencyPolygonCumulativeMode());
		// System.out.println("   frequencyPolygonCumulativeMode=" +
		// this.model.isFrequencyPolygonCumulativeMode());

		h.put("columnSplitIndex", this.model.getSplitOptions()
			.getColumnSplitIndex());
		// System.out.println("   columnSplitIndex=" +
		// this.model.getSplitOptions().getColumnSplitIndex());

		h.put("splitBoundaries", this.model.getSplitOptions()
			.getBinBoundaries());
		// System.out.println("   splitBoundaries=" +
		// this.model.getSplitOptions().getBinBoundaries());

		h.put("splitInSingleView", this.model.isSplitInSingleView());
		// System.out.println("   splitInSingleView=" +
		// this.model.isSplitInSingleView());

		h.put("nextToEachOther", this.model.isNextToEachOther());
		// System.out.println("   nextToEachOther=" +
		// this.model.isNextToEachOther());

		// System.out.println("END HistogramController.getState()...");

		return h;
	}
	
	/**
	 * Deeply clones a HashMap by serializing and deserializing.
	 */
//	private HashMap<String, Object> deepCopy(HashMap<String, Object> original)
//	{
//		Object copy = null;
//		try
//		{
//			// Write the object out to a byte array
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			ObjectOutputStream out = new ObjectOutputStream(bos);
//			out.writeObject(original);
//			out.flush();
//			out.close();
//
//			// Make an input stream from the byte array and read
//			// a copy of the object back in.
//			ObjectInputStream in = new ObjectInputStream(
//				new ByteArrayInputStream(bos.toByteArray()));
//			copy = in.readObject();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//		catch (ClassNotFoundException cnfe)
//		{
//			cnfe.printStackTrace();
//		}
//		return (HashMap) copy;
//	}

	public void setState(Object state)
	{
		if (!(state instanceof HashMap))
		{
			return;
		}

		HashMap h = (HashMap) state;

		if (h.containsKey("columnIndex"))
		{
			// Let op: setColumnIndex() zet ook de binBoundaries
			// Dat wordt hieronder goed gemaakt als de binBoundaries
			// uit de hashtable worden gezet.
			this.model.setColumnIndex(((Integer) h.get("columnIndex"))
				.intValue());
		}
		if (h.containsKey("binBoundaries"))
		{
//			System.out.println("HistogramController.setState(): binBoundaries="
//				+ (ArrayList<Double>) h.get("binBoundaries"));
			this.model.setBinBoundaries((ArrayList<Double>) h
				.get("binBoundaries"));
//			System.out.println("... (setState) identityHashCode(this.model)=" + identityHashCode(this.model));
//			System.out.println("... (setState) identityHashCode(this.model.getBinBoundaries())=" + identityHashCode(this.model.getBinBoundaries()));
//			System.out.println("... (setState) identityHashCode(this.view)=" + identityHashCode(this.view));
//			System.out.println("... (setState) identityHashCode(this.view.getModel().getBinBoundaries())=" + identityHashCode(this.view.getModel().getBinBoundaries()));
//			System.out.println("... (setState) identityHashCode(this)=" + identityHashCode(this));
		}
		if (h.containsKey("percentage"))
		{
			this.model.setPercentage(((Boolean) h.get("percentage"))
				.booleanValue());
		}
		if (h.containsKey("labelUnderBin"))
		{
			this.model.setLabelUnderBin(((Boolean) h.get("labelUnderBin"))
				.booleanValue());
//			System.out.println("HistogramController.setState(): labelUnderBin="
//				+ ((Boolean) h.get("labelUnderBin")).booleanValue());
		}
		if (h.containsKey("showUserOptions"))
		{
			this.model.setShowUserOptions(((Boolean) h.get("showUserOptions"))
				.booleanValue());
		}
		if (h.containsKey("verticalBars"))
		{
			this.model.setVerticalBars(((Boolean) h.get("verticalBars"))
				.booleanValue());
		}
		if (h.containsKey("viewName"))
		{
			this.model.setViewName((String) h.get("viewName"));
		}
		if (h.containsKey("frequencyPolygonCumulativeMode"))
		{
			this.model.setFrequencyPolygonCumulativeMode(((Boolean) h
				.get("frequencyPolygonCumulativeMode")).booleanValue());
		}

		if (h.containsKey("columnSplitIndex"))
		{
			this.model.setColumnSplitIndex((Integer) h.get("columnSplitIndex"));
		}
		if (h.containsKey("splitBoundaries"))
		{
			this.model.setSplitBoundaries((ArrayList<Double>) h
				.get("splitBoundaries"));
		}
		if (h.containsKey("splitInSingleView"))
		{
			this.model.setSplitInSingleView(((Boolean) h
				.get("splitInSingleView")).booleanValue());
		}
		if (h.containsKey("nextToEachOther"))
		{
			this.model.setNextToEachOther(((Boolean) h.get("nextToEachOther"))
				.booleanValue());
		}
	}

	public String toString()
	{
		return this.getViewName();
	}

	@Override
	public void setUp(DialogBox owner)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * Return the widget containing the graphical representation
	 */
	@Override
	public Widget getWidget() // was: getComponent()
	{
		return this.view;
	}

	@Override
	public void setUp(Frame owner)
	{
		// TODO Auto-generated method stub
		
	}
}
