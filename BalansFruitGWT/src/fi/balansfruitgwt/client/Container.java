package fi.balansfruitgwt.client;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * .
 * The class represents a container for DraggableObjects
 *
 * @author casperkolkman
 * @since 26-11-2012
 * @version 1.0
 *
 */
public class Container extends AbsolutePanel {
    private static final int BUFFERIMAGE = 50;

    private int height;
    private int width;
    private DragDropPanel controller;

    /**
     * .
     * Making a instance of Container
     *
     * @param pWidth width of the container
     * @param pHeight height of the container
     * @param pController the controller that's control all containers
     */
    public Container(final int pWidth, final int pHeight, final DragDropPanel pController) {
        super.setPixelSize(pWidth, pHeight);
        width = pWidth;
        height = pHeight;
        controller = pController;
    }

    /**
     * .
     * Behavior of the method, at the time that an object is dropped. Tells the
     * controller to balance again
     *
     * @param widget the widget that is placed
     * @param x position x in the container
     */
    public void eatWidget(final Widget widget, final int x) {
        if (widget instanceof DraggableObject) {
            widget.removeFromParent();
            super.add(widget, x, height - ((DraggableObject) widget).getHeight());
            controller.redrawBalance();
            controller.setEquation();
        }
    }


    /**
     * .
     * Tells if the container can eat a widget
     * @return true = eats widget, false = dont eat widgets
     */
    public boolean isWidgetEater() {
        return true;
    }

    /**
     * .
     * Get weight of all widgets in the container
     * @return weight of all widgets
     */
    public final int getWeight() {
        int weight = 0;
        for (int i = 0; i < this.getWidgetCount(); i++) {
            weight += ((DraggableObject) this.getWidget(i)).getWeight();
        }
        return weight;
    }

    public final void add(final Widget widget, final int x) {
        if (widget instanceof DraggableObject) {
        	int position;
        	if(x >= 0) {
        		position = x;
        	} else {
        		position = Random.nextInt(width - BUFFERIMAGE);
        	}
            super.add(widget, position, height - ((DraggableObject) widget).getHeight());
        }
    }
}
