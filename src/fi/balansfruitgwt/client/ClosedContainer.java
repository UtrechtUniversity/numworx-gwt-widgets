package fi.balansfruitgwt.client;

/**
 * .
 * The class represents a container that ignore all DraggableObjects
 *
 * @author casperkolkman
 * @version 1.0
 * @since 26-11-2012
 */
public final class ClosedContainer extends Container {

    public ClosedContainer(final int width, final int height, final DragDropPanel controller) {
        super(width, height, controller);
    }

    /**
     * .
     * Don't eat the widget
     * @return false = don't eat widgets
     */
    @Override
    public final boolean isWidgetEater() {
        return false;
    }
}
