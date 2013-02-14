package nl.uu.fi.algebraarrowapplet.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {

    public static final Images INSTANCE =  GWT.create(Images.class);

    @Source("images/punt.png")
    ImageResource point();

    @Source("images/pijl_links.png")
    ImageResource arrowLeft();

    @Source("images/pijl_rechts.png")
    ImageResource arrowRight();

    @Source("images/arrow-up.png")
    ImageResource arrowMinus();

    @Source("images/arrow-down.png")
    ImageResource arrowPlus();

    @Source("images/Zoom-Out.png")
    ImageResource buttonZoomOut();

    @Source("images/Zoom-In.png")
    ImageResource buttonZoomIn();

    @Source("images/pijl_draw.png")
    ImageResource drawArrow();
}
