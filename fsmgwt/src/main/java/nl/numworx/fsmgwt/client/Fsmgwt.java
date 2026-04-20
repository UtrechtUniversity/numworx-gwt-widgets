package nl.numworx.fsmgwt.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Fsmgwt extends Composite implements EntryPoint, InteractionStub  {

	private int height;
	private int width;
	private CanvasViewer viewer;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//init(400,300, null, null);
		Stub.publish(this);
	}

	@Override
	public HashMap<String, Object> getState() {
		return new HashMap<>();
	}

	@Override
	public void setState(HashMap<String, Object> h) {
	}

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public int[][] getScoreObjectives() {
		return null;
	}

	@Override
	public Boolean isCorrect() {
		return Boolean.TRUE;
	}

	@Override
	public void kijkNa() {

	}

	@Override
	public void zetNagekeken(boolean b) {

	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
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
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		
	}

	@Override
	public void init(int width, int height, Map<String, Object> launchData, Map<String, Number> values) {
		this.width = width;
		this.height = height;
		viewer = new CanvasViewer(width, height);
		initWidget(viewer.asWidget());
		RootPanel.get().add(this);
	}
}
