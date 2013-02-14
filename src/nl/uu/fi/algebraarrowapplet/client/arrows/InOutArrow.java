package nl.uu.fi.algebraarrowapplet.client.arrows;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import nl.uu.fi.algebraarrowapplet.client.CalculationPanel;
import nl.uu.fi.algebraarrowapplet.client.ToolkitPanel.ArrowDirection;

public class InOutArrow extends EditableArrow implements ContextMenuHandler, EventListener, TouchStartHandler, TouchEndHandler, TouchCancelHandler, TouchMoveHandler {

    protected int value;
    private static final int defaultWidth = 40;
    private static final int defaultHeight = 25;
    private TablePanel tablePanel = new TablePanel();

    private LabelPanel labelPanel = new LabelPanel();
    private InOutArrowMenu popupPanel = new InOutArrowMenu(true, this);

    private static final int SwipeMarge = 2;

    /**
     * Constructor
     * @param direction
     * @param pValue
     * @param pController
     */
    public InOutArrow(ArrowDirection direction, int pValue, PickupDragController pController) {
        super(direction, pValue, pController);
        value = pValue;
        makeArrow();
        setHandlers();
    }

    /**
     * This methode fills the arrow with the desired panels and sets some settings.
     */
    private void makeArrow() {
        innerAbsolutePanel.add(text, 2, 2);
        innerAbsolutePanel.add(input, 2, 0);
        outerVerticalPanel.insert(labelPanel, 0);
        setStyle();
        outerVerticalPanel.add(tablePanel);
        tablePanel.setVisible(false);
        calculateWidth();
    }

    /**
     * this methode adds eventHandlers to the arrow.
     */
    private void setHandlers() {
        addDomHandler(this, ContextMenuEvent.getType());
        this.sinkEvents(Event.TOUCHEVENTS);
        this.sinkEvents(Event.MOUSEEVENTS);
        this.addTouchStartHandler(this);
        this.addTouchCancelHandler(this);
        this.addTouchEndHandler(this);
        this.addTouchMoveHandler(this);
    }

    /**
     * This methode sets the style of the arrow.
     */
    private void setStyle() {
        Style style = innerAbsolutePanel.getElement().getStyle();
        text.getElement().getStyle().setProperty("backgroundColor", "#FFFFFF");
        text.getElement().getStyle().setProperty("textAlign", "center");
        style.setProperty("backgroundColor", "#848484");
        style.setProperty("borderLeft", "1px solid #000000");
        style.setProperty("borderRight", "1px solid #000000");
        style.setProperty("borderBottom", "1px solid #000000");
        style.setProperty("borderTop", "1px solid #000000");
        style.setProperty("padding", "0 3px 0 0");
        labelPanel.setPixelSize(defaultWidth, defaultHeight);
        innerAbsolutePanel.setPixelSize(defaultWidth, defaultHeight);
        text.setPixelSize(defaultWidth - 4, defaultHeight - 4);
        labelPanel.setVisible(false);
        String marginString = outerHorizontalContainer.getWidget(0).getElement().getStyle().getWidth();
        int margin = Integer.parseInt(marginString.substring(0, marginString.length() - 2));
        tablePanel.getElement().getStyle().setMarginLeft(margin, Style.Unit.PX);
        labelPanel.getElement().getStyle().setMarginLeft(margin, Style.Unit.PX);
    }

    /**
     * Methode to calculate the width of the arrow.
     */
    @Override
    public void calculateWidth() {
        int textWidth = getWidth();
        if (labelPanel.isVisible() || tablePanel.isVisible()) {
            setWidthAndBorderVisibleLabel(getDesiredWidth(textWidth));
        } else {
            setWidthAndBorderInvisibleLabel(textWidth);
        }
        repaintParent();
    }

    /**
     * This methode sets the width and the border of the arrow when its label is invisible.
     * @param textWidth
     */
    private void setWidthAndBorderInvisibleLabel(int textWidth) {
        innerAbsolutePanel.getElement().getStyle().setWidth(textWidth + 1, Style.Unit.PX);
        input.getElement().getStyle().setWidth(textWidth, Style.Unit.PX);
        text.getElement().getStyle().setWidth(textWidth, Style.Unit.PX);
        innerAbsolutePanel.getElement().getStyle().setProperty("borderTop", "1px solid #000000");
    }

    /**
     * This methode sets the width and border of the arrow when its label is visible.
     * @param desiredWidth
     */
    private void setWidthAndBorderVisibleLabel(double desiredWidth) {
        innerAbsolutePanel.getElement().getStyle().setWidth(desiredWidth + 1, Style.Unit.PX);
        text.getElement().getStyle().setWidth(desiredWidth, Style.Unit.PX);
        labelPanel.getElement().getStyle().setWidth(desiredWidth + 4, Style.Unit.PX);
        input.getElement().getStyle().setWidth(desiredWidth, Style.Unit.PX);
        innerAbsolutePanel.getElement().getStyle().setProperty("borderTop", "0px solid #000000");
        labelPanel.getinputLabel().getElement().getStyle().setWidth(labelPanel.getOffsetWidth(), Style.Unit.PX);
        tablePanel.getElement().getStyle().setWidth(desiredWidth + 4, Style.Unit.PX);
    }

    /**
     * this methode returns the width based on the width of its individual panels.
     * @param textWidth
     * @return
     */
    private double getDesiredWidth(int textWidth) {
        double inputLabelWidth = getTextWidth(labelPanel.getinputLabel());
        double tablePanelWidth = tablePanel.getPanelWidth();
        double desiredWidth;
        if (inputLabelWidth >= textWidth && inputLabelWidth >= tablePanelWidth) {
            desiredWidth = inputLabelWidth;
        } else if (tablePanelWidth >= textWidth) {
            desiredWidth = tablePanelWidth;
        } else {
            desiredWidth = textWidth;
        }
        return desiredWidth;
    }

    /**
     *  This methode returns the width of the arrow.
     * @return
     */
    private int getWidth() {
        int textWidth = getTextWidth(text);
        if (textWidth < defaultWidth) {
            textWidth = defaultWidth;
        }
        return textWidth;
    }

    /**
     * This methode executes when ContextMenuEvent is fired.
     * @param event the {@link ContextMenuEvent} that was fired
     */
    public void onContextMenu(ContextMenuEvent event) {
        event.preventDefault();
        event.stopPropagation();
        popupPanel.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
        popupPanel.show();
    }

    /**
     * This methode toggles the visibility of the label.
     */
    public void toggleLabel() {
        popupPanel.hide();
        labelPanel.setVisible(!labelPanel.isVisible());
        calculateWidth();
    }

    /**
     * This methode toggles the visibility of the table.
     */
    public void toggleTable() {
        popupPanel.hide();
        tablePanel.setVisible(!tablePanel.isVisible());
        calculateWidth();
    }

    /**
     * This methode toggles the visibility of every arrow attached to this arrow.
     */
    public void toggleChain() {
        popupPanel.hide();
    }

    /**
     * This methode hides the contextmenu.
     */
    public void toggleCancel() {
        popupPanel.hide();
    }

    private void setDrawState() {
        if(!wantDraw) {
            ((CalculationPanel) this.getParent()).cancelDraw();
            unSetDrawFocus();
            input.setVisible(false);
            input.setEnabled(false);
            input.setFocus(false);
            visible = false;
            wantDraw=true;
        } else {
            ((CalculationPanel) this.getParent()).startDraw(this);
            text.getElement().getStyle().setProperty("backgroundColor", "#DDDDDD");
            input.setVisible(false);
            input.setEnabled(false);
            input.setFocus(false);
            visible = false;
            wantDraw = false;
        }
    }

    /**
     * on swipe show menu
     * @param event
     */
    private void swipe(TouchEndEvent event) {
        popupPanel.setPopupPosition(event.getNativeEvent().getClientY(), event.getNativeEvent().getClientX());
        popupPanel.show();
    }

    @Override
    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEDOWN: {
                event.preventDefault();
                setClickValues(event);
                break;
            }
            case Event.ONMOUSEUP: {
                event.preventDefault();
                detectClickPattern();
                break;
            }
        }
        super.onBrowserEvent(event);
    }

    /**
     * detect:
     * -doubleclick
     * -longclick
     * -click
     */
    private void detectClickPattern() {
        if (isDoubleClick() && this.getParent() instanceof CalculationPanel) {
            setDrawState();
            doubletap.clear();
        } else if (isLongClick()) {
            setDragState();
            doubletap.clear();
        } else {
            click();
            if (doubletap.size() >= 2) {
                doubletap.clear();
                visible = false;
            }
        }
    }

    /**
     * set start touch values
     * @param event the {@link TouchStartEvent} that was fired
     */
    public void onTouchStart(TouchStartEvent event) {
        event.preventDefault();
        if (event.getTouches().length() > 0) {
            Touch touch = event.getTouches().get(0);
            x = touch.getPageX();
            y = touch.getPageY();
            press = true;
            taptime = System.currentTimeMillis();
            doubletap.add(taptime);
        }
    }

    public void onTouchCancel(TouchCancelEvent event) {
        event.preventDefault();
    }

    /**
     * detect:
     * -doubletap
     * -longtap
     * -swipe
     * -tap
     * @param event
     */
    public void onTouchEnd(TouchEndEvent event) {
        event.preventDefault();
        if (isDoubleClick() && this.getParent() instanceof CalculationPanel) {
            setDrawState();
            doubletap.clear();
        } else if (isLongClick()) {
            setDragState();
            doubletap.clear();
        } else if (swipe) {
            swipe(event);
            swipe = false;
            press = false;
        } else {
            click();
            if (doubletap.size() >= 2) {
                doubletap.clear();
                visible = false;
            }
        }
    }

    public void onTouchMove(TouchMoveEvent event) {
        event.preventDefault();
        if (event.getTouches().length() > 0 && press) {
            Touch touch = event.getTouches().get(0);
            if (isSwipe(SwipeMarge, touch)) {
                swipe = true;
            }
        }
        press = false;
        doubletap.clear();
    }

    /**
     * Look if user swiped
     * @param marge
     * @param touch to get start end positions
     * @return true if difference between start and end is larger than marge
     */
    private boolean isSwipe(final int marge, final Touch touch) {
        return touch.getPageX() - x > marge || touch.getPageX() - x < -marge || touch.getPageY() - y < -marge || touch.getPageY() - y > marge;
    }

    public void unSetDrawFocus() {
        text.getElement().getStyle().setProperty("backgroundColor", "#FFFFFF");
    }
}
