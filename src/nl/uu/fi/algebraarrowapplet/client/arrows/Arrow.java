package nl.uu.fi.algebraarrowapplet.client.arrows;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.*;
import nl.uu.fi.algebraarrowapplet.client.Images;
import nl.uu.fi.algebraarrowapplet.client.ToolkitPanel.ArrowDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Class containing basic Arrow behaviour
 */
public abstract class Arrow extends FocusPanel {

    private static final int characterLength = 9;

    protected HorizontalPanel outerHorizontalContainer = new HorizontalPanel();
    protected AbsolutePanel innerAbsolutePanel = new AbsolutePanel();
    protected VerticalPanel outerVerticalPanel = new VerticalPanel();
    protected boolean isDraggable = false;
    protected static final int ArrowHeight = 25;
    protected PickupDragController controller;

    protected boolean press;
    protected long taptime;
    protected List<Long> doubletap = new ArrayList<Long>();
    protected boolean wantDraw = true;

    private List<Widget> receivers = new ArrayList<Widget>();

    /**
     * Arrow constructor
     * @param direction Direction to which the arrow points
     * @param pController Dragcontroller for this arrow
     */
    public Arrow(ArrowDirection direction, PickupDragController pController) {
        controller = pController;
        this.add(outerVerticalPanel);
        outerVerticalPanel.add(outerHorizontalContainer);
        setImageOnDirection(direction);
    }

    /**
     * Resets the arrow's direction.
     * @param direction Direction to which the arrow points
     */
    public void setDirection(final ArrowDirection direction) {
        outerHorizontalContainer.clear();
        setImageOnDirection(direction);
    }

    /**
     * Adds new arrow pointing at given direction.
     * @param direction  Given direct to which arrow should point.
     */
    private void setImageOnDirection(final ArrowDirection direction) {
        Image arrow;
        Image point = new Image(Images.INSTANCE.point());
        if (direction == ArrowDirection.RIGHT) {
            arrow = new Image(Images.INSTANCE.arrowRight());
            outerHorizontalContainer.add(point);
            outerHorizontalContainer.add(innerAbsolutePanel);
            outerHorizontalContainer.add(arrow);
        } else {
            arrow = new Image(Images.INSTANCE.arrowLeft());
            outerHorizontalContainer.add(arrow);
            outerHorizontalContainer.add(innerAbsolutePanel);
            outerHorizontalContainer.add(point);
        }
        point.getElement().getStyle().setProperty("margin", "4px 0px 0px 0px");
        arrow.getElement().getStyle().setProperty("margin", "7px 0px 0px -1px");
    }

    /**
     * Method used to get the label's text width
     *
     * @param label Label for which textwidth is determined
     * @return  Textwidth of given label
     */
    protected int getTextWidth(Label label) {
        return label.getText().length() * characterLength;
    }

    /**
     * Stops arrow from being draggable.
     */
    public void setNotDraggable() {
        isDraggable = false;
    }

    /**
     * Flips dragstate
     * If something is draggable, it becomes undraggable.
     * And vica versa.
     */
    protected void setDragState() {
        if(isDraggable) {
            controller.makeNotDraggable(this);
            isDraggable=false;
        } else {
            controller.makeDraggable(this);
            isDraggable=true;
        }
    }

    /**
     * Checks if tap/click is longer than 300ms
     * @return Whether or not the tap time has been over 300ms
     */
    protected boolean isLongClick() {
        return System.currentTimeMillis() - taptime > 300;
    }

    /**
     * Checks if a double tap/click has occured
     * @return If 2 or more taps happened in 700ms
     */
    protected boolean isDoubleClick() {
        return doubletap.size() >= 2 && doubletap.get(1) - doubletap.get(0) < 700;
    }

    /**
     * Sets ability to draw
     */
    public void setWantDraw() {
        wantDraw = true;
    }

    /**
     * Gets the Arrow's receiving arrows.
     * @return  List of receivers
     */
    public List<Widget> getReceivers() {
        return receivers;
    }

    /**
     * If a line is drawn, a receiver will be added
     * @param receiver
     */
    public void addReceiver(final Widget receiver) {
        receivers.add(receiver);
    }

    /**
     * Sets a list of receivers as this arrow's receivers.
     * @param pReceivers list of receivers
     */
    public void setReceivers(final List<Widget> pReceivers) {
        receivers = pReceivers;
    }

    public abstract void unSetDrawFocus();
}
