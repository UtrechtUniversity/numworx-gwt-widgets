package fi.balansfruitgwt.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.resources.client.ImageResource;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * .
 * This class places DraggableObjects in the containers
 * @author casperkolkman
 * @version 2.0
 * @since 05-12-2012
 */
public final class DraggableObjectFactory {

    /**
     * .
     * For each image that can be dragged an enum
     */
    public enum Objects {
        ONE, ONEGRAM, FIVEGRAMS, TENGRAMS,
        FIFTYGRAMS, HUNDERDGRAMS, FIVEHUNDERDGRAMS,
        PINEAPPLE, APPLE, BANANA, LEMON, PEAR, PEACH,
        ORANGE, TOMATO, X, Y, ONEEMPTY,
        ONEBLOCKABSTRACT, TWOBLOCKABSTRACT, FIVEBLOCKABSTRACT,
        TENBLOCKABSTRACT, TWENTYBLOCKABSTRACT, FIFTYBLOCKABSTRACT,
        HUNDREDBLOCKABSTRACT
    }

    private boolean fixed = false;

    private PickupDragController dragController;

    private Images image = Images.INSTANCE;

    public DraggableObjectFactory(final PickupDragController pDragController) {
        dragController = pDragController;
    }

    /**
     * .
     * The method create DraggableObjects and place them in a container
     * @param c the container where the DraggableObject will be placed
     * @param x position
     * @param object declare which image you want
     */
    public void createObjects(final Container c, final int x, final Objects object, final int weight, final int id) throws IllegalArgumentException {
        if(object == null || c == null) {
            throw new IllegalArgumentException("Ongeldige invoer");
        } else {
            switch (object) {
                case ONE:
                    placeObjects(c, x, image.one(), weight, id);
                    break;
                case ONEGRAM:
                    placeObjects(c, x, image.oneGram(), weight, id);
                    break;
                case FIVEGRAMS:
                    placeObjects(c, x, image.fiveGrams(), weight, id);
                    break;
                case TENGRAMS:
                    placeObjects(c, x, image.tenGrams(), weight, id);
                    break;
                case FIFTYGRAMS:
                    placeObjects(c, x, image.fiftyGrams(), weight, id);
                    break;
                case HUNDERDGRAMS:
                    placeObjects(c, x, image.hundredGrams(), weight, id);
                    break;
                case FIVEHUNDERDGRAMS:
                    placeObjects(c, x, image.fiveHundredGrams(), weight, id);
                    break;
                case PINEAPPLE:
                    placeObjects(c, x, image.pineapple(), weight, id);
                    break;
                case APPLE:
                    placeObjects(c, x, image.apple(), weight, id);
                    break;
                case BANANA:
                    placeObjects(c, x, image.banana(), weight, id);
                    break;
                case LEMON:
                    placeObjects(c, x, image.lemon(), weight, id);
                    break;
                case PEAR:
                    placeObjects(c, x, image.pear(), weight, id);
                    break;
                case PEACH:
                    placeObjects(c, x, image.peach(), weight, id);
                    break;
                case ORANGE:
                    placeObjects(c, x, image.orange(), weight, id);
                    break;
                case TOMATO:
                    placeObjects(c, x, image.tomato(), weight, id);
                    break;
                case X:
                    placeObjects(c, x, image.x(), weight, id);
                    break;
                case Y:
                    placeObjects(c, x, image.y(), weight, id);
                    break;
                case ONEEMPTY:
                    placeObjects(c, x, image.oneEmpty(), weight, id);
                    break;
                case ONEBLOCKABSTRACT:
                    placeObjects(c, x, image.oneBlockAbstract(), weight, id);
                    break;
                case TWOBLOCKABSTRACT:
                    placeObjects(c, x, image.twoBlockAbstract(), weight, id);
                    break;
                case FIVEBLOCKABSTRACT:
                    placeObjects(c, x, image.fiveBlockAbstract(), weight, id);
                    break;
                case TENBLOCKABSTRACT:
                    placeObjects(c, x, image.tenBlockAbstract(), weight, id);
                    break;
                case TWENTYBLOCKABSTRACT:
                    placeObjects(c, x, image.twentyBlockAbstract(), weight, id);
                    break;
                case FIFTYBLOCKABSTRACT:
                    placeObjects(c, x, image.fiftyBlockAbstract(), weight, id);
                    break;
                case HUNDREDBLOCKABSTRACT:
                    placeObjects(c, x, image.hundredBlockAbstract(), weight, id);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * .
     * place the DraggableObjects in a container
     * @param c the container where the DraggableObject will be placed
     * @param x x-position
     * @param img image out the ClientBundle images
     * @param weight weight of the DraggableObject
     * @param id from DWO
     */
    private void placeObjects(final Container c, final int x, final ImageResource img, final int weight, final int id) {
    	DraggableObject temp = new DraggableObject(img, weight, id);
	    c.add(temp, x);
	    if (!fixed) {
	    	dragController.makeDraggable(temp);
	    }
    }

    /**
     * .
     * This method make the balance static
     * @param pFixed if static true, if not static false
     */
    public void setFixed(final boolean pFixed) {
        fixed = pFixed;
    }
}
