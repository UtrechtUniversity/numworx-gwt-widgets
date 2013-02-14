package nl.uu.fi.algebraarrowapplet.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The class represents a container for DraggableObjects
 *
 * @author casperkolkman
 * @since 26-11-2012
 * @version 1.0
 *
 */
public class TrashContainer extends AbsolutePanel {

    /**
     * Behavior of the method, at the time that an object is placed. Tells the
     * controller to balance again
     *
     * @param widget
     *            the widget that is placed
     */
    public void eatWidget(Widget widget) {
        widget.removeFromParent();
    }

    /**
     * Tells if the container can eat a widget
     */
    public boolean isWidgetEater() {
        return true;
    }
}
