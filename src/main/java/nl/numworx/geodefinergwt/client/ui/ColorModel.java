package nl.numworx.geodefinergwt.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.dom.client.CssColor;

import fi.euclides.model.Destroyable;
import fi.euclides.util.DefaultAdapter;
import nl.numworx.geodefiner.common.UIModel;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class ColorModel<T extends Destroyable> implements UIModel<T, Void> {

	protected T item;
	public int rgba = 0xFF000000;
	public boolean visible = true;
	
	@Override
	public UIModel<T, Void> init(T item) {
		this.item = item;
		return this;
	}

	@Override
	public void install() {
		CssColor css = CssColor.make(colorString());
		DefaultAdapter.getDefault(item).put(CssColor.class, css);
		item.setVisible(visible);
	}

	protected String colorString() {
		String hex = Integer.toHexString(rgba&0xFFFFFF).toUpperCase();
		hex = "00000" + hex;
		int l = hex.length();
		hex = hex.substring(l-6);
		return "#" + hex;
	}

	@Override
	public Map<String, Object> toMap() {
		HashMap<String,Object> h = new HashMap<String,Object>();
		h.put("color", rgba);
		h.put("visible", visible);
		return h;
	}

	@Override
	public void fromMap(ObjectMap value) {
		rgba = value.getInt("color");
		visible = value.getBoolean("visible", true);
	}

	@Override
	public Void editor() {
		return null;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
