package nl.numworx.fsmgwt.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

import fi.euclides.util.Hashtable;
import nl.numworx.fsm.editor.Output;
import nl.numworx.fsm.shared.Memento;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Fsmgwt extends Composite implements EntryPoint, InteractionStub  {

	private int height;
	private int width;
	private CanvasViewer viewer;
	private Memento memento;


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//init(400,300, null, null);
		Stub.publish(this);
	}

	@Override
	public HashMap<String, Object> getState() {
		Object model, names, accepted;
		memento.getModel().clearSelection();
		JSONOutput dos = new JSONOutput();
		memento.setDataOutputStream(dos);
		HashMap<String, Object> result = new HashMap<>();
		try {
			memento.writeModel(memento.getModel());
			model = dos.toList();
			result.put("model", model);
			dos = new JSONOutput();
			memento.writeNames(dos);
			names = dos.toList();
			result.put("names", names);
			dos = new JSONOutput();
			memento.writeAccepted(dos);
			accepted = dos.toList();
			result.put("accepted", accepted);			
		} catch (IOException e) {
			GWT.log ("does not occur", e);
		}		
		return result;	}

	private void setState(Map<String,Object> h) {
		if (h == null) return;
		ObjectMap state = JSONUtilities.wrapMap(h);
		ObjectList data = state.getObjectList("model");
		if (data != null) {
			memento.setDataInputStream(new JSONInput(data));
			try {
				memento.readModel(viewer);
				ObjectList names = state.getObjectList("names");
				memento.readNames(new JSONInput(names));
				ObjectList accepted = state.getObjectList("accepted");
				memento.readAccepted(new JSONInput(accepted));
			} catch (Exception e) {
				GWT.log("setState fails", e);
			}
		}
	}
	
	
	@Override
	public void setState(HashMap<String, Object> h) {
		setState( (Map<String,Object>)h);
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
		memento = new Memento();
		viewer = new CanvasViewer(width, height);
		viewer.setModel(memento.getModel());
		initWidget(viewer.asWidget());
		RootPanel.get().add(this);
		setState(launchData);
	}
}
