package fi.statistiekgwt.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.thirdparty.javascript.jscomp.CssRenamingMap.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;

/**
 * Statistiek InteractiePanel MVC Controller
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class StatInteractiePanel extends LayoutPanel implements ChangeHandler
{
	private StatModel model;
	private StatInteractiePanelView view;
	public static final boolean DEBUG = false;
	private double barHeight = 30;//3; 

	/**
	 * Constructor
	 */
	public StatInteractiePanel()
	{
		super();
		this.model = new StatModel();
		this.view = new StatInteractiePanelView(this.model, this, barHeight, Unit.PX);
		super.add(this.view);
	}

	public StatInteractiePanel(StatModel model)
	{
		super();
		this.model = model;
		this.view = new StatInteractiePanelView(this.model, this, barHeight, Unit.PX);
		super.add(this.view);
	}

	private void debugPrint(String s)
	{
		if (DEBUG)
		{
			System.out.println(s);
		}
	}

	public StatInteractiePanelView getView()
	{
		return view;
	}

	public void setView(StatInteractiePanelView view)
	{
		this.view = view;
	}

	public StatModel getModel()
	{
		return model;
	}

	public void setModel(StatModel model)
	{
		this.model = model;
		this.view.setModel(this.model);
	}

	public int getSelectedView()
	{
		return this.view.getSelectedView();
	}

	public void setSelectedTab(int tab)
	{
		this.view.processSelectedTab(tab);
	}

	public HashMap<String, Object> getState()
	{
		HashMap h = new HashMap();

		h.put("tableModel", this.model.getStatTableModel().getState());

		h.put("selectionList", this.model.getStatTableModel().getSelectionList());

		// statistiekViewTypes and statistiekViewStates should always be added to the state
		int noViews = this.model.getViews().size();
		String[] statistiekViewTypes = new String[noViews];
		Object[] statistiekViewStates = new Object[noViews];
		for (int i = 0; i < noViews; i++)
		{
			statistiekViewTypes[i] = this.model.getViews().get(i).getViewType();
			statistiekViewStates[i] = this.model.getViews().get(i).getState();
		}

		h.put("statistiekViewTypes", statistiekViewTypes);
		h.put("statistiekViewStates", statistiekViewStates);

		int tabInt = this.model.mainWindowIndexToGeneralIndex(this.view
			.getSelectedView());
		h.put("selectedView", new Integer(tabInt));
		System.out.println("StatInteractiePanel.getState(): this.model.mainWindowIndexToGeneralIndex(this.view.getselectedView()="
			+ this.view.getSelectedView() + ") = " + tabInt);

		return h;
	}

	public void setState(HashMap hashtable)
	{
		HashMap b = hashtable;//deepCopy(hashtable);
		
		this.model.removeViewsWithoutEvent();

		if (b.containsKey("tableModel"))
		{
			this.model.getStatTableModel().setState((HashMap) b.get("tableModel"));
			// this.view.setModel(this.model);
		}
		if (b.containsKey("selectionList"))
		{
			this.model.getStatTableModel().setSelectionList((ArrayList<Boolean>) b.get("selectionList"));
		}
		else
		{
			ArrayList<Boolean> selectionList = new ArrayList<Boolean>(
				this.model.getStatTableModel().getRowCount());
			for (int i = 0; i < this.model.getStatTableModel().getRowCount(); i++)
			{
				selectionList.add(false);
			}
			this.model.getStatTableModel().setSelectionList(selectionList);
		}

		if (b.containsKey("statistiekViewTypes")
			&& b.containsKey("statistiekViewStates"))
		{
			String[] statistiekViewTypes = (String[]) b.get("statistiekViewTypes");
			Object[] statistiekViewStates = (Object[]) b.get("statistiekViewStates");

			for (int i = 0; i < statistiekViewTypes.length; i++)
			{
				StatistiekView statistiekView = StatistiekGWT.createView(
					statistiekViewTypes[i], "", this.model.getStatTableModel(), 0, 0, this);
				if (statistiekView != null)
				{
					statistiekView.setState(statistiekViewStates[i]);
					this.model.addView(statistiekView);
				}

			}
			// this.view.setModel(this.model);
		}

		if (b.containsKey("selectedView"))
		{
			int index = ((Integer) b.get("selectedView")).intValue();
			// System.out.println("StatInteractiePanel.setState(): selectedView in hashtable = "
			// + index);
			this.view.processSelectedTab(index);
		}
	}

	public void setBounds(int x, int y, int b, int h)
	{
		super.setSize(String.valueOf(b), String.valueOf(h));
	}

	public void wis()
	{
		// TODO Auto-generated method stub
	}

	public void zetMaat()
	{
		// TODO Auto-generated method stub
	}

	public int geefAsHoogte()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getIpId()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getScore()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getScoreMax()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Retourneert altijd true, want in de statistiekcomponent wordt
	 * niets nagekeken.
	 */
	public boolean isCorrect()
	{
		return true;
	}

	public boolean isFout()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void zetMode(int mode)
	{
		// TODO Auto-generated method stub
	}

	public void zetNagekeken(boolean b)
	{
		// TODO Auto-generated method stub
	}

	public void stop()
	{
		// System.out.println("StatInteractiePanel.stop()");

		// close all dialogs
		ArrayList<Boolean> viewInOwnWindow = new ArrayList<Boolean>();
		for (int i = 0; i < this.model.getViewInOwnWindow().size(); i++)
		{
			viewInOwnWindow.add(false);
		}
		this.model.setViewInOwnWindow(viewInOwnWindow);
	}

	public void start()
	{
		// TODO Auto-generated method stub
	}

	public void destroy()
	{
		// TODO Auto-generated method stub
	}

	public void opnieuw()
	{
		// TODO Auto-generated method stub
	}

	public void kijkNa()
	{
		// TODO Auto-generated method stub
	}

	public void kijkNa(int stapNr)
	{
		// TODO Auto-generated method stub
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
//			OutputStream bos = new OutputStream();
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

	@Override
	public void onChange(ChangeEvent event)
	{
		Object source = event.getSource();
		
		// change in StatInteractiePanelView.viewsBox
		if (source.equals(this.view.getViewsBox()))
		{
			GWT.log("StatInteractiePanel.onChange(): viewsbox!");
			
			// check of tabel gekozen is
			String s = this.view.getViewsBoxString();
			String t = null;
			if (Arrays.asList(StatistiekGWT.VIEWS_translated).contains(s)
				&& (s.equals(StatistiekGWT.rb.getString("tableOption"))))
			{
				// tabel gekozen, er is geen variabelekeuze nodig
				t = StatistiekGWT.VIEWS[0];
				StatistiekView statistiekView = StatistiekGWT.createView(t,
					this.model.findUniqueViewName(s), model.getStatTableModel(),
					0, 0, this);
				this.model.addView(statistiekView);
				this.view.selectLastTab();
				this.view.clearAddViewTab();
			}
			else if (Arrays.asList(StatistiekGWT.VIEWS_translated).contains(s)
				&& (s.equals(StatistiekGWT.rb.getString("crosstabOption"))))
			{
				// update label "Kies variabele rijen:"
				this.view.setStartVarLabel(StatistiekGWT.rb.getString("chooseStartVarRowLabel"));
				this.view.setStartVarBox(true);
				this.view.setStartVar2Box(true);
			}
			else if (Arrays.asList(StatistiekGWT.VIEWS_translated).contains(s)
				&& (s.equals(StatistiekGWT.rb.getString("scatterplotOption"))))
			{
				// update label "Kies variabele x-as:"
				this.view.setStartVarLabel(StatistiekGWT.rb.getString("chooseStartVarXLabel"));
				// update label "Kies variabele y-as:"
				this.view.setStartVar2Label(StatistiekGWT.rb.getString("chooseStartVarYLabel"));
				this.view.setStartVarBox(true);
				this.view.setStartVar2Box(true);
			}
			else
			{
				// bied variabelekeuze aan
				this.view.setStartVarLabel(StatistiekGWT.rb.getString("chooseStartVarLabel"));
				this.view.setStartVarBox(true);
				this.view.setStartVar2Box(false);
			}
		} // viewsBox
		else if (source.equals(this.view.getStartVarBox()) || source.equals(this.view.getStartVar2Box()))
		{
			GWT.log("StatInteractiePanel.onChange(): startVarBox!");

			// for crosstab (kruistabel) startVarBox is for choosing the rows variable
			// startVar2Box is for choosing the columns variable
			
			String s = this.view.getViewsBoxString();
			String t = null;
			if (Arrays.asList(StatistiekGWT.VIEWS_translated).contains(s))
			{
				if (s.equals(StatistiekGWT.rb.getString("tableOption")))
				{
					t = StatistiekGWT.VIEWS[0];
				}
				else if (s.equals(StatistiekGWT.rb.getString("histogramOption")))
				{
					t = StatistiekGWT.VIEWS[1];
				}
				else if (s.equals(StatistiekGWT.rb.getString("dotplotOption")))
				{
					t = StatistiekGWT.VIEWS[2];
				}
				else if (s.equals(StatistiekGWT.rb.getString("frequencytableOption")))
				{
					t = StatistiekGWT.VIEWS[3];
				}
				else if (s.equals(StatistiekGWT.rb.getString("frequencypolygonOption")))
				{
					t = StatistiekGWT.VIEWS[4];
				}
				else if (s.equals(StatistiekGWT.rb.getString("boxplotOption")))
				{
					t = StatistiekGWT.VIEWS[5];
				}
				else if (s.equals(StatistiekGWT.rb.getString("crosstabOption")))
				{
					t = StatistiekGWT.VIEWS[6];
				}
				else if (s.equals(StatistiekGWT.rb.getString("scatterplotOption")))
				{
					t = StatistiekGWT.VIEWS[7];
				}
				else if (s.equals(StatistiekGWT.rb.getString("descriptivesOption")))
				{
					t = StatistiekGWT.VIEWS[8];
				}
				
				// Als Tabel gekozen, dan is de actionPerformed van startVarBox niet relevant
				if (!t.equals(StatistiekGWT.VIEWS[0]))
				{
					StatistiekView statistiekView = null;
					
					if (t.equals(StatistiekGWT.VIEWS[6]) || t.equals(StatistiekGWT.VIEWS[7]))
					{
						// Crosstab or scatterplot
						// Check if both varboxes are set
						if ((this.view.getStartVarBoxSelectedIndex() > 0)
							&& (this.view.getStartVar2BoxSelectedIndex() > 0))
						{
							// both variable boxes are set
							
		    				// startVarBox index -1 vanwege de eerste default 'Kies een variabele'
		    				statistiekView = StatistiekGWT.createView(t,
		    					this.model.findUniqueViewName(s), model.getStatTableModel(),
		    					this.view.getStartVarBoxSelectedIndex()-1, 
		    					this.view.getStartVar2BoxSelectedIndex()-1, this);
							this.model.addView(statistiekView);
		    				this.view.selectLastTab();
		    				this.view.clearAddViewTab();
						}
					}
					else
					{
	    				// startVarBox index -1 vanwege de eerste default 'Kies een variabele'
	    				statistiekView = StatistiekGWT.createView(t,
	    					this.model.findUniqueViewName(s), model.getStatTableModel(),
	    					this.view.getStartVarBoxSelectedIndex()-1, 0, this);
						this.model.addView(statistiekView);
	    				this.view.selectLastTab();
	    				this.view.clearAddViewTab();
					}
				}
			}
		} // ac = startVarBox || startVar2Box
//		else if (source instanceof ButtonTabComponent.TabButton)
//		{
//			int mainTabIndex = this.view
//				.indexOfTabWidget((ButtonTabComponent.TabButton) (e
//					.getSource())).getButtonTabComponent());
//			int tab = this.model.mainWindowIndexToGeneralIndex(mainTabIndex);
//			this.model.removeView(tab);
//		}

	}

	public void zetOpdracht(HashMap hashMap, String[] randomVars, HashMap randomValues)
	{
		// Waarom randomVars en randomValues?
//		System.out.println("StatInteractiePanel.zetOpdracht(hashtable=" + hashtable
//		 + ", randomVars=" + randomVars + ", randomValues=" + randomValues);
		
//		Hashtable b = deepCopy(hashtable);
//		Hashtable resetHashtable = deepCopy(hashtable);

		// Deep copy the hashtable, else references will be copied and fields
		// within resetHashtable can be changed.
		this.model.setResetHashtable(hashMap);

		this.model.removeViewsWithoutEvent();

		if (hashMap.containsKey("tableModel"))
		{
			this.model.getStatTableModel().setState((HashMap) hashMap.get("tableModel"));

			// this.view.setModel(this.model);
		}

		ArrayList<Boolean> selectionList;
		if (hashMap.containsKey("selectionList"))
		{
			selectionList = (ArrayList<Boolean>) hashMap.get("selectionList");
			this.model.getStatTableModel().setSelectionList(selectionList);
		}
		else
		{
			selectionList = new ArrayList<Boolean>(this.model.getStatTableModel().getRowCount());
			for (int i = 0; i < this.model.getStatTableModel().getRowCount(); i++)
			{
				selectionList.add(false);
			}
			this.model.getStatTableModel().setSelectionList(selectionList);
		}

		if (hashMap.containsKey("statistiekViewTypes")
			&& hashMap.containsKey("statistiekViewStates"))
		{
			String[] statistiekViewTypes = (String[]) hashMap
				.get("statistiekViewTypes");
			Object[] statistiekViewStates = (Object[]) hashMap
				.get("statistiekViewStates");

			for (int i = 0; i < statistiekViewTypes.length; i++)
			{
				StatistiekView statistiekView = StatistiekGWT.createView(
					statistiekViewTypes[i], "", this.model.getStatTableModel(), 0, 0, this);
				if (statistiekView != null)
				{
					statistiekView.setState(statistiekViewStates[i]);
					this.model.addView(statistiekView);
				}

			}
			// this.view.setModel(this.model);
		}

		if (hashMap.containsKey("selectedView"))
		{
			// test syl
//			System.out.println("StatInteractiePanel.zetOpdracht(): selectedView in hashtable = "
//			 + ((Integer)b.get("selectedView")).intValue());
			this.view.processSelectedTab(((Integer) hashMap.get("selectedView")).intValue());
			// test syl
//			this.view.processSelectedTab(1); // ?? geeft geen tab 1??!!
		}
	}

}
