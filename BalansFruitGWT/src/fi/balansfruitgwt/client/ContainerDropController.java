package fi.balansfruitgwt.client;

import java.awt.SystemTray;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.Widget;

/**
 * .
 * This class specific the behavior of DropController for a container
 *
 * @author casperkolkman
 * @version 1.0
 * @since 26-11-2012
 */
public final class ContainerDropController extends SimpleDropController {

    private Container container;

    private static final int LEFTMOST = 0;
    private static final int CONTAINERRIGHTMOST = 155;
    private static final int STOCKRIGHTMOST = 450;
    private static final int LMARGINLCONTAINTER = 7;
    private static final int LMARGINRCONTAINER = 286;
    private static final int LMARGINSTOCKCONTAINER = 0;

    /**
     * .
     * These variables are measured.
     * Given that balance panel is added to the root panel coordinates 0.0
     * these variables ensure that a part of the images are not lost on the sides of a container
     */
    private static final int MAXYPOSITIONCONTAINER = 200;
    private static final int MAXXPOSITIONLCONTAINER = 200;
    private static final int MINXPOSITIONRCONTAINER = 250;
    private static final int MINXPOSITIONIMAGELEFT = 6;
    private static final int MAXXPOSITIONIMAGELEFT = 160;
    private static final int MINXPOSITIONIMAGERIGHT = 284;
    private static final int MAXXPOSITIONIMAGERIGHT = 441;
    private static final int MINXPOSITIONIMAGESTOCK = -1;
    private static final int MAXXPOSITIONIMAGESTOCK = 450;

    /**
     * .
     * Constructor saves the container that this class controlls
     *
     * @param pContainer Container that must have a dropcontroller
     */
    public ContainerDropController(final Container pContainer) {
        super(pContainer);
        container = pContainer;
    }

    @Override
    public void onDrop(final DragContext context) {
        super.onDrop(context);
        for (Widget widget : context.selectedWidgets) {
            container.eatWidget(widget, calculateX(context, widget));
        }
    }

    /**
     * .
     * this method calculate the x position for a DraggableObject in a container
     *
     * @param context context has the desiredDraggableX coordinate
     * @param w the widget that will be placed in the container
     * @return x positon for the DraggableObject to be placed
     */
    private int calculateX(final DragContext context, final Widget w) {
        int positionXOnWindow = w.getParent().getParent().getAbsoluteLeft();
        int positionYOnWindow = w.getParent().getParent().getAbsoluteTop();
        int x;

        // If it is within the boundaries of the left container
        if (inBoundariesLeftContainer(context, positionXOnWindow, positionYOnWindow)) {
            x = calculateContainerX(context, MINXPOSITIONIMAGELEFT, MAXXPOSITIONIMAGELEFT, LMARGINLCONTAINTER, positionXOnWindow);
        }
        // If it is within the boundaries of the right container
        else if (inBoundariesRightContainer(context, positionXOnWindow, positionYOnWindow)) {
            x = calculateContainerX(context, MINXPOSITIONIMAGERIGHT, MAXXPOSITIONIMAGERIGHT, LMARGINRCONTAINER, positionXOnWindow);
        }
        // If it is within the boundaries of the stock container
        else if (inBoundariesStockContainer(context, positionYOnWindow)) {
            x = calculateContainerX(context, MINXPOSITIONIMAGESTOCK, MAXXPOSITIONIMAGESTOCK, LMARGINSTOCKCONTAINER, positionXOnWindow);
        } else {
        	System.out.println("hier!");
            x = LEFTMOST;
        }
        return x;
    }

    /**
     * .
     * Looks if the desiredDraggableY is in the boundaries of stock container
     * @param context context has the desiredDraggableX and Y coordinate
     * @param positionYOnWindow y positie of balansPanel on browser window
     * @return true if its in the boundaries of the container
     */
    private boolean inBoundariesStockContainer(final DragContext context, final int positionYOnWindow) 
    {
    	boolean isIn = context.desiredDraggableY >= MAXYPOSITIONCONTAINER + positionYOnWindow;

    	return isIn;
    }

    /**
     * .
     * Looks if the desiredDraggableY and desiredDraggableX are in the boundaries of left container
     * @param context context has the desiredDraggableX and Y coordinate
     * @param positionXOnWindow x positie of balansPanel on browser window
     * @param positionYOnWindow y positie of balansPanel on browser window
     * @return true if its in the boundaries of the container
     */
    private boolean inBoundariesLeftContainer(final DragContext context, final int positionXOnWindow, final int positionYOnWindow) {
        return context.desiredDraggableX < MAXXPOSITIONLCONTAINER + positionXOnWindow && context.desiredDraggableY < MAXYPOSITIONCONTAINER + positionYOnWindow;
    }

    /**
     * .
     * Looks if the desiredDraggableY and desiredDraggableX are in the boundaries of right container
     * @param context context has the desiredDraggableX and Y coordinate
     * @param positonXOnWindow x positie of balansPanel on browser window
     * @param positonYOnWindow y positie of balansPanel on browser window
     * @return true if its in the boundaries of the container
     */
    private boolean inBoundariesRightContainer(final DragContext context, final int positonXOnWindow, final int positonYOnWindow) {
        return context.desiredDraggableX > MINXPOSITIONRCONTAINER + positonXOnWindow && context.desiredDraggableY < MAXYPOSITIONCONTAINER + positonYOnWindow;
    }

    /**
     * .
     * Advanced calculation for the x coordinate in the container.
     *
     * @param context context has the desiredDraggableX coordinate
     * @param minXPositionImage Minimum x position where the image is completely visible
     * @param maxXPositionImage Maximum x position where the image is completely visible
     * @param margin margin of a container
     * @param positionXOnWindow the x position of the balancePanel in the browser window
     * @return x coordinate
     */
    private int calculateContainerX(final DragContext context, final int minXPositionImage, 
    	final int maxXPositionImage, final int margin, final int positionXOnWindow) 
    {
        int x = 0;
        if (context.desiredDraggableX < minXPositionImage + positionXOnWindow) 
        {
            x = LEFTMOST;
        } 
        else if (context.desiredDraggableX > maxXPositionImage + positionXOnWindow) 
        {
            if (margin == LMARGINSTOCKCONTAINER) 
            {
                x = STOCKRIGHTMOST;
            } 
            else 
            {
                x = CONTAINERRIGHTMOST;
            }
        } 
        else 
        {
            x = context.desiredDraggableX - margin - positionXOnWindow;
        }
        
        return x;
    }

    /**
     * .
     * describe the behavior if a container isn't a widget eater
     *
     * @param context
     * @throws VetoDragException ensures that the drag gesture will be canceled
     */
    @Override
    public void onPreviewDrop(final DragContext context) throws VetoDragException {
        super.onPreviewDrop(context);
        if (!container.isWidgetEater()) {
            throw new VetoDragException();
        }
    }
}
