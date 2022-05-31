package fi.graphtoolgwt.client.ui;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

public class ResizePanel extends LayoutPanel implements RequiresResize, ProvidesResize {
	
	RequiresResize resizer;

	public ResizePanel() {
	}

	@Override
	public void onResize() {
		if (resizer != null) resizer.onResize();
		super.onResize();
	}

	public void add(ResizeWidget w) {
		super.add(w);
		resizer = w;
	}
		
	@Override
	public void clear() {
		resizer = null;
		super.clear();
	}

	@Override
	public boolean remove(IsWidget child) {
		if (child == resizer)  resizer = null;
		return super.remove(child);
	}


}
