package nl.numworx.geodefinergwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import fi.euclides.event.Tracker;
import fi.euclides.model.Punt;
import fi.euclides.model.Visitor;
import fi.euclides.model.VrijPunt;
import fi.euclides.model.math.Numbers;
import fi.euclides.util.DefaultAdapter;
import nl.numworx.geodefiner.common.GeoTriangle;

public class GWTTriangle extends GeoTriangle {

	final static int width = 450;
	final static int height = 450;
	
	private final VrijPunt imageCenter;

	public GWTTriangle(Tracker viewer) {
		super(viewer, (450.0/28.0)/2.0);
		imageCenter = new VrijPunt(width/2.0, height/2.0);
		imageCenter.setFree(false);
		
		String base = GWT.getModuleBaseURL();
		String resource = base + "../images/resources/geodriehoek.png";
	    ImageElement img = Document.get().createImageElement();
	    img.setSrc(resource);
	    DefaultAdapter.getDefault(this).put(ImageElement.class, img);
	}

	@Override
	public Punt imageCenter() {
		return imageCenter;
	}

	@Override
	public void visit(Visitor v) {
		v.visitImage(this);
	}

	@Override
	public Numbers rotation() {
		return Numbers.div(super.rotation(), Numbers.createInteger(width));
	}


}
