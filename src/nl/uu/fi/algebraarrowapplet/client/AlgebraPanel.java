package nl.uu.fi.algebraarrowapplet.client;

import java.util.HashMap;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class AlgebraPanel extends AbsolutePanel implements InteractionView
{

	private PickupDragController dragController = new PickupDragController(this, true);

	private CalculationPanel calculationPanel = new CalculationPanel();
	private ToolkitPanel toolkitPanel = new ToolkitPanel(dragController, calculationPanel);

	public AlgebraPanel()
	{
		super.setPixelSize(813, 602);
		super.add(toolkitPanel, 0, 0);
		super.add(calculationPanel, 110, 0);
		dragController.setBehaviorDragStartSensitivity(5);
		addDropControllers();
	}

	private void addDropControllers()
	{
		dragController.registerDropController(new ToolkitPanelDropController(toolkitPanel));
		dragController.registerDropController(new AbsolutePositionDropController(calculationPanel));
	}

	@Override
	public HashMap<String, Object> getState()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getScore()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isCorrect()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		// TODO Auto-generated method stub

	}
}
