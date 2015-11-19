package fi.balansfruitgwt.client;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

/**
 * .
 * The class represents a DraggableObject with an Image and weight in it
 *
 * @author casperkolkman
 * @version 2.0
 * @since 26-11-2012
 */
public final class DraggableObject extends Image {

    private int weight;
    private ImageResource image;
    private int id;

    /**
     * .
     * Initialize a DraggableObject with an image and weight
     *
     * @param img     image resource of the object
     * @param pWeight weight of the object
     * @param pId id of the object
     */
    public DraggableObject(final ImageResource img, final int pWeight, final int pId) {
        super(img);
        image = img;
        weight = pWeight;
        id = pId;
    }

    public int getWeight() {
        return weight;
    }

    public ImageResource getImageResource() {
        return image;
    }

	public int getId() {
		return id;
	}
}
