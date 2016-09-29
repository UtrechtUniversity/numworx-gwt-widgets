package fi.sliderwidgetgwt.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.LayoutPanel;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class SliderWidgetInteractiePanel extends LayoutPanel implements CBookEventListener
{
	private SchuifParameterGWT schuifParameter;

	public SliderWidgetInteractiePanel()
	{
		super();

		schuifParameter = new SchuifParameterGWT(100, "a");
		// schuifParameter.zetLocatie(0, 0);

		super.add(schuifParameter.geefSlider());
	}

	public SchuifParameterGWT geefSchuifParameter()
	{
		return schuifParameter;
	}

	public void setState(Map<String, Object> launchState)
	{
		if ((launchState != null) && !launchState.isEmpty())
		{

			ObjectMap map = JSONUtilities.wrapMap(launchState);

			// zet defaults
			String paramNaam = "a";
			double paramWaarde = 0;
			double paramOnderGrensWaarde = 0;
			double paramBovenGrensWaarde = 5;
			double paramStapGrootte = 0.1;
			int paramLengte = 100;

			if (map.containsKey("paramNaam"))
				paramNaam = map.getString("paramNaam");
			if (map.containsKey("paramWaarde"))
				paramWaarde = map.getDouble("paramWaarde");
			if (map.containsKey("paramOnderGrensWaarde"))
				paramOnderGrensWaarde = map.getDouble("paramOnderGrensWaarde");
			if (map.containsKey("paramBovenGrensWaarde"))
				paramBovenGrensWaarde = map.getDouble("paramBovenGrensWaarde");
			if (map.containsKey("paramStapGrootte"))
				paramStapGrootte = map.getDouble("paramStapGrootte");
			if (map.containsKey("paramLengte"))
				paramLengte = map.getInt("paramLengte");

			schuifParameter.zetGrensWaarden(paramOnderGrensWaarde, paramBovenGrensWaarde);
			schuifParameter.zetWaarde(paramWaarde, false);
			schuifParameter.zetNaam(paramNaam);
			schuifParameter.zetStapGrootte(paramStapGrootte);
			schuifParameter.zetLengte(paramLengte);
		}
	}

	public HashMap<String, Object> getState()
	{
		String paramNaam = schuifParameter.geefNaam();
		double paramWaarde = schuifParameter.geefWaarde();
		double paramOnderGrensWaarde = schuifParameter.geefOnderGrens();
		double paramBovenGrensWaarde = schuifParameter.geefBovenGrens();
		double paramStapGrootte = schuifParameter.geefStapGrootte();
		int paramLengte = schuifParameter.geefLengte();

		HashMap h = new HashMap();
		h.put("paramNaam", paramNaam);
		h.put("paramWaarde", paramWaarde);
		h.put("paramOnderGrensWaarde", paramOnderGrensWaarde);
		h.put("paramBovenGrensWaarde", paramBovenGrensWaarde);
		h.put("paramStapGrootte", paramStapGrootte);
		h.put("paramLengte", paramLengte);

		return h;
	}

	public void zetOpdracht(HashMap h, String[] randomVars, HashMap randomValues)
	{
		setState(h);
	}

	/**
	 * Paint de schuifparameter.
	 */
	public void paint()
	{
		schuifParameter.paint();
	}
	

	// @Override
	// public String[] getSendCmds()
	// {
	// String[] commands =
	// { "double.sliderValue" };
	// return commands;
	// }
	//
	// @Override
	// public String[] getAcceptedCmds()
	// {
	// String[] commands =
	// { "double.sliderValue" };
	// return commands;
	// }

	@Override
	public void acceptCBookEvent(CBookEvent event)
	{
		String command = event.getCommand();

		if (command.startsWith("double"))
		{
			Map map = (Map) event.getParameters();
			if (map != null)
			{
				String name = (String) map.get("name");
				double waarde = ((Double) map.get("value")).doubleValue();
				if (name.equals(schuifParameter.geefNaam()))
				{
					schuifParameter.zetWaarde(waarde, false);
				}
			}
			else
			{
				String message = event.getMessage();
				double waarde = Double.parseDouble(message);
				schuifParameter.zetWaarde(waarde, false);
			}
		}

	}

	/**
	 * Set the size of SliderWidgetInteractiePanel and its slider.
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height)
	{
		this.setPixelSize(width, height);

		schuifParameter.geefSlider().setSize(width, height);
	}

	// @Override
	// public void actionPerformed(ActionEvent e)
	// {
	// if (e.getSource() == schuifParameter.geefSlider())
	// {
	// double waarde = schuifParameter.geefDoubleStand();
	// Map<String, Object> map = new HashMap<String, Object>();
	// map.put("name", schuifParameter.geefNaam());
	// map.put("value", new Double(schuifParameter.geefDoubleStand()));
	// cbookEventHandler.fire("double.sliderValue", map);
	// }
	//
	// }

	// @Override
	// public String getLocalizedCmd(String cmd)
	// {
	// // TODO Auto-generated method stub
	// return SliderWidget.rb.getString(CBA_PREFIX + cmd);
	// }

}
