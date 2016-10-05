package fi.sliderwidgetgwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class SliderWidgetGWT implements EntryPoint, InteractionStub
{
	private static final String XWIDGET_DOUBLE_SLIDER_VALUE = "double.sliderValue";

	private static final Logger logger = Logger.getLogger(SliderWidgetGWT.class.getName());

	OpdrNavIF comRoot;

	/**
	 * Simple panel om basisPanel te tonen.
	 */
	SimplePanel simpel = new SimplePanel();
	SliderWidgetInteractiePanel basisPanel;
	
	public static int DEFAULT_WIDTH = 560;
	public static int DEFAULT_HEIGHT = 340;
	int breedte = SliderWidgetGWT.DEFAULT_WIDTH;
	int hoogte = SliderWidgetGWT.DEFAULT_HEIGHT;
	boolean volledigeBreedte = false;
	private Map<String, Object> launchState; 

	/*
	 * Default zero argument constructor is required.
	 */
	public SliderWidgetGWT()
	{
	}
	
	public SliderWidgetGWT(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden, int volleBreedte)
	{	
		basisPanel = new SliderWidgetInteractiePanel();
		basisPanel.geefSchuifParameter().geefSlider().setSliderWidgetGWT(this); // backlink tbv cross widget communicatie
		
		ObjectMap map = JSONUtilities.wrapMap(h);
	
		if (map != null)
		{
			if (map.containsKey("breedte"))
			{
				breedte = map.getInt("breedte");
			}
			if (map.containsKey("hoogte"))
			{
				hoogte = map.getInt("hoogte");
			}
			if (map.containsKey("volledigeBreedte"))
			{
				volledigeBreedte = map.getBoolean("volledigeBreedte");
			}
		}

		if (volledigeBreedte)
		{
			breedte = volleBreedte;
		}
	
		if (h != null && h.get("interactiePanelLaunchState") != null)
		{
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		}

		//alle gegevens uit launchState halen: 
		init(breedte, hoogte, launchState, randomVarWaarden);
	}

	/**
	 * Initialize with the values in launch data.
	 */
	@Override
	public void init(int width, int height, Map<String, Object> launchDataMap,
		Map<String, Number> values)
	{
		breedte = width;
		hoogte = height;
		
		launchState = launchDataMap;
		
		// in initialize() wordt de launchState in sliderwidget gezet
		this.initialize();
		
		simpel.setWidget(asWidget());
	}
	
	/**
	 * Deze methode wordt aangeroepen in init()
	 */
	private void initialize()
	{
		basisPanel.setSize(breedte, hoogte);
		basisPanel.setState((HashMap) launchState);
		
		paint();
	}

	public void onModuleLoad()
	{
		basisPanel = new SliderWidgetInteractiePanel();
		basisPanel.geefSchuifParameter().geefSlider().setSliderWidgetGWT(this); // backlink tbv cross widget communicatie
		basisPanel.setWidth("" + breedte);
		basisPanel.setHeight("" + hoogte);
		basisPanel.setPixelSize(breedte, hoogte);
		
		RootLayoutPanel.get().add(simpel);
		
		//simpel.setWidget(asWidget()); // deze regel aanzetten voor standalone test
		//paint(); // deze regel aanzetten voor standalone test
		Stub.publish(this); // deze regel uitzetten voor standalone test
	}

	public void paint()
	{
		basisPanel.paint();
	}

	@Override
	public HashMap<String, Object> getState()
	{
		return this.basisPanel.getState();
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		if (h == null || h.isEmpty()) 
			return;
		
		this.basisPanel.setState(h);
		paint();
	}

	@Override
	public int getScore()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[][] getScoreObjectives()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isCorrect()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void kijkNa()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void zetNagekeken(boolean b)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		this.comRoot.addCBookEventListener(XWIDGET_DOUBLE_SLIDER_VALUE, basisPanel);
	}

	@Override
	public void zetVolledigeBreedte(int breedte)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public Widget asWidget()
	{
		return basisPanel;
	}

	@Override
	public int getAsHoogte()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight()
	{
		return hoogte;
	}

	@Override
	public int getWidth()
	{
		return breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte)
	{
		// TODO Auto-generated method stub
	}
	
	/**
	 * Fire cross widget event with the variable name and current double value.
	 * 
	 * @param command
	 */
	public void fire(String command)
	{
		if (comRoot != null)
		{
			Map<String,Object> map = new HashMap<String,Object>();
			// haal de waarden uit schuifparameter
			String name = basisPanel.geefSchuifParameter().geefNaam();
			map.put("name", name);
			Object value = basisPanel.geefSchuifParameter().geefDoubleStand();
			map.put("value", value);
			CBookEvent event = new CBookEvent(this, XWIDGET_DOUBLE_SLIDER_VALUE, map);
			comRoot.fireEvent(event);
		}
	}

}
