package fi.balansfruitgwt.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * . This class makes the hole Balance
 * 
 * @author casperkolkman, Sylvia van Borkulo
 * @version 1.0
 * @since 29-11-2012
 */
public final class BalansFruitGWT extends AbsolutePanel implements InteractionStub, EntryPoint, CBookEventListener
{

	private static final int WIDTH = 500;
	private static final int HEIGHT = 360;
	private static final int BALANCEHEIGHT = 260;
	private static final int STOCKHEIGHT = 100;

	private DragDropPanel control = new DragDropPanel();
	private DraggableObjectFactory factory = new DraggableObjectFactory(control.getDragController());
	private DWOAdapter adapter;

	private boolean reset = false;
	private boolean save = false;

	boolean nagekeken = false;
	private OpdrNavIF comRoot;
	private Object lastEquation;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		this.add(createUI(), 0, 0);
		adapter = new DWOAdapter(this, factory, control);
		this.add(control);
		
		setPixelSize(WIDTH, HEIGHT);
		RootPanel rootPanel = RootPanel.get("rootPanel");		
		if (rootPanel != null)
		{	rootPanel.add(this);
			Stub.publish(this);
		}
	}

	public BalansFruitGWT()
	{
		
	}
	/**
	 * . this panel has two layers: UI and DragAndDrop
	 * 
	 * @param preferences
	 *            preferences from DWO Learning Environment
	 */
	public BalansFruitGWT(final HashMap<String, Object> preferences)
	{
		this.add(createUI(), 0, 0);
		adapter = new DWOAdapter(this, factory, control);
		this.add(control);
		readPreferences(preferences);
	}

	public void setSavedMode(final boolean pSave)
	{
		save = pSave;
	}

	public void setResetMode(final boolean pReset)
	{
		reset = pReset;
	}

	private void createResetButton()
	{
		final Button resetButton = new Button("Reset");
		resetButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				reset();
			}
		});
		this.add(resetButton, 0, 0);
	}

	private void reset()
	{
		control.reset();
		adapter.readLaunchData(adapter.getLaunchData());
		control.redrawBalance();
	}

	/**
	 * . This methode make the user interface layer that would be placed under
	 * the drag and drop layer
	 * 
	 * @return User Interface Layer
	 */
	private VerticalPanel createUI()
	{
		VerticalPanel vertical = new VerticalPanel();
		AbsolutePanel balance = new AbsolutePanel();
		control.setBalanceDestination(balance);
		AbsolutePanel stock = new AbsolutePanel();
		balance.setPixelSize(WIDTH, BALANCEHEIGHT);
		stock.getElement().getStyle().setBackgroundColor("#ffd07f");
		stock.setPixelSize(WIDTH, STOCKHEIGHT);
		vertical.setPixelSize(WIDTH, HEIGHT);
		vertical.add(balance);
		vertical.add(stock);
		return vertical;
	}

	public boolean getSaveMode()
	{
		return save;
	}

	@Override
	public HashMap<String, Object> getState()
	{
		return adapter.getState();
	}

	@Override
	public void setState(final HashMap<String, Object> h)
	{
		adapter.setState(h);
	}

	@Override
	public int getScore()
	{
		return 0;
	}

	@Override
	public Boolean isCorrect()
	{
		return Boolean.TRUE;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		comRoot.addCBookEventListener(BALANSVERGELIJKING, this);
	}

	final static String BALANSVERGELIJKING = "balansvergelijking";
	private void fire(String command) {
		if(command == BALANSVERGELIJKING) // use CONSTANT always
		{
			CBookEvent event = new CBookEvent(this, command, Collections.singletonMap(command, lastEquation));
			comRoot.fireEvent(event);
		}
		
	}
	
	
	
	@Override
	public void init(int w, int h, Map<String, Object> launchData, Map<String, Number> values) {
		if (launchData != null) {
			adapter.readLaunchData(launchData);
			if (reset)
			{
				createResetButton();
			}
			control.redrawBalance();
			control.setEquation(this, false);
		}
	}
	
	/**
	 * Reads the constructor data
	 * 
	 * @param launchData
	 *            from DWO
	 */
	public void readPreferences(final Map<String, Object> preferences)
	{
		if (preferences != null && preferences.containsKey(DWOAdapter.keyLaunchData))
		{
			Map<String,Object> map = (Map<String, Object>) preferences.get(DWOAdapter.keyLaunchData);
			init(WIDTH, HEIGHT, map, null);
		}
	}

	@Override
	public void kijkNa() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getAsHoogte() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() 
	{
		return BalansFruitGWT.HEIGHT;
	}

	@Override
	public int getWidth() 
	{
		return BalansFruitGWT.WIDTH;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
	}
	
	public void zetNagekeken(boolean b)
	{
		nagekeken = b;
	}

	public int[][] getScoreObjectives()
	{
		return null;
	}

	public void setEquation(String equation, boolean b) {
		boolean equals = equation.equals(lastEquation);
		lastEquation = equation;
		if(b && !equals)
		{
			fire(BALANSVERGELIJKING);
		}
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if(BALANSVERGELIJKING.equals(event.getCommand())) {
			String equation = event.getParameter(BALANSVERGELIJKING).toString();
			Logger.getLogger("BalansFruitGWT").info("accept "  + equation);
		}
	}

}

