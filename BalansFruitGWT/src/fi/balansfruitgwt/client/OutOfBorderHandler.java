package fi.balansfruitgwt.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

/**
 * .
 * The OutOfBorderHandler prevents that objects that are dragged outside browser window will placed in the wrong panel
 * @author casperkolkman
 * @version 1.0
 * @since 25-11-2012
 */
public final class OutOfBorderHandler implements DragHandler {

    private Container start;
    private int x;
    private PickupDragController controller;

    /**
     * .
     * Initialize the a dragcontroller to make a DraggableObject draggable
     * @param pController the dragcontroller
     */
    public OutOfBorderHandler(final PickupDragController pController) {
         controller = pController;
    }

    /**
     * .
     * If the object is not dragged in a container, than add the object to his old container.
     * @param dragEndEvent
     */
    public void onDragEnd(final DragEndEvent dragEndEvent) {
        if (!(((Widget) dragEndEvent.getSource()).getParent() instanceof Container)) {
            if (dragEndEvent.getSource() instanceof DraggableObject) {
            	ImageResource img = ((DraggableObject) dragEndEvent.getSource()).getImageResource();
            	int weight = ((DraggableObject) dragEndEvent.getSource()).getWeight();
            	int id = ((DraggableObject) dragEndEvent.getSource()).getId(); 
                DraggableObject temp = new DraggableObject(img, weight, id);             
                start.add(temp, x);
                ((Widget) dragEndEvent.getSource()).removeFromParent();
                controller.makeDraggable(temp);
            }
        }
    }

    /**
     * .
     * Saves the container at onDragStart
     * @param dragStartEvent
     */
    public void onDragStart(final DragStartEvent dragStartEvent) {
    	int containerX = ((Container) ((DraggableObject) dragStartEvent.getSource()).getParent()).getAbsoluteLeft();
    	int objectX = ((DraggableObject) dragStartEvent.getSource()).getAbsoluteLeft();
    	x = objectX - containerX;
        start = (Container) ((DraggableObject) dragStartEvent.getSource()).getParent();
    }

    public void onPreviewDragEnd(final DragEndEvent dragEndEvent) throws VetoDragException {
    }

    public void onPreviewDragStart(final DragStartEvent dragStartEvent) throws VetoDragException {
    }
}
