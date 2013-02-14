package nl.uu.fi.algebraarrowapplet.client.arrows;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Label;
import nl.uu.fi.algebraarrowapplet.client.CalculationPanel;
import nl.uu.fi.algebraarrowapplet.client.ToolkitPanel.ArrowDirection;

public class ExpressionArrow extends EditableArrow implements EventListener, TouchCancelHandler, TouchStartHandler, TouchEndHandler {

    private static final int LeftMarginOperator = 8;
    private static final int TopMarginText = 4;
    private static final int LeftMarginLabel = 22;
    private static final int LeftMarginInput = 16;
    private static final int TopMarginInput = -2;
    private static final int padding = 3;
    private static final String backgroundColor = "#FFC800";

    private Label operator;

    public ExpressionArrow(final ArrowDirection direction, final String pOperator, final int pValue, final PickupDragController pController) {
        super(direction, pValue, pController);
        makeArrow(pOperator);
        setHandlers();
    }

    /**
     * This methode fills the arrow with its desired panels and sets some settings.
     * @param pOperator
     */
    private void makeArrow(final String pOperator) {
        operator = new Label(pOperator);
        innerAbsolutePanel.add(operator, LeftMarginOperator, TopMarginText);
        innerAbsolutePanel.add(text, LeftMarginLabel, TopMarginText);
        innerAbsolutePanel.add(input, LeftMarginInput, TopMarginInput);
        calculateWidth();
        setStyle();
    }

    /**
     * Methode to calculate the width of the arrow.
     */
    @Override
    protected void calculateWidth() {
        innerAbsolutePanel.setPixelSize(getTextWidth(text) + getTextWidth(operator) + 23, ArrowHeight);
        input.setPixelSize(getTextWidth(text) + 15, 20);
        repaintParent();
    }

    /**
     * This methode sets the style of the arrow.
     */
    private void setStyle() {
        Style style = innerAbsolutePanel.getElement().getStyle();
        style.setPaddingRight(padding, Style.Unit.PX);
        style.setBackgroundColor(backgroundColor);
        style.setProperty("border", "1px solid #000000");
        style.setProperty("borderRadius", "5px");
    }

    /**
     * This methode returns the operator of this arrow.
     * @return
     */
    public String getOperator() {
        return operator.getText();
    }

    /**
     * This methode adds eventHandlers to the arrow.
     */
    private void setHandlers() {
        this.sinkEvents(Event.TOUCHEVENTS);
        this.sinkEvents(Event.MOUSEEVENTS);
        this.addTouchStartHandler(this);
        this.addTouchCancelHandler(this);
        this.addTouchEndHandler(this);
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
     * detect a:
     *  -click
     *  -doubleclick
     *  -longclick
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

    private void setDrawState() {
        if(!wantDraw) {
            ((CalculationPanel) this.getParent()).cancelDraw();
            innerAbsolutePanel.getElement().getStyle().setProperty("backgroundColor", backgroundColor);
            input.setVisible(false);
            input.setEnabled(false);
            input.setFocus(false);
            visible = false;
            wantDraw=true;
        } else {
            ((CalculationPanel) this.getParent()).startDraw(this);
            innerAbsolutePanel.getElement().getStyle().setProperty("backgroundColor", "red");
            input.setVisible(false);
            input.setEnabled(false);
            input.setFocus(false);
            visible = false;
            wantDraw = false;
        }
    }

    /**
     * set start touch values
     * @param event the {@link TouchStartEvent} that was fired
     */
    public void onTouchStart(TouchStartEvent event) {
        event.preventDefault();
        if (event.getTouches().length() > 0) {
            press = true;
            taptime = System.currentTimeMillis();
            doubletap.add(taptime);
        }
    }

    /**
     * detect:
     * -Doubletap
     * -LongTap
     * -Tap
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
        } else {
            click();
            if (doubletap.size() >= 2) {
                doubletap.clear();
                visible = false;
            }
        }
    }

    public void unSetDrawFocus() {
        innerAbsolutePanel.getElement().getStyle().setProperty("backgroundColor", backgroundColor);
    }

    public void onTouchCancel(TouchCancelEvent event) {
        event.preventDefault();
    }
}
