package nl.uu.fi.algebraarrowapplet.client.arrows;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Label;
import nl.uu.fi.algebraarrowapplet.client.CalculationPanel;
import nl.uu.fi.algebraarrowapplet.client.ToolkitPanel;

public class NonEditableArrow extends Arrow implements EventListener, TouchStartHandler, TouchEndHandler, TouchCancelHandler {

    protected Label operator;
    private static final String backgroundColor = "#FFC800";

    public NonEditableArrow(ToolkitPanel.ArrowDirection direction, String pOperator, PickupDragController pController) {
        super(direction, pController);
        operator = new Label(pOperator);
        innerAbsolutePanel.add(operator, 10, 4);
        calculateWidth();
        setStyle();
        this.sinkEvents(Event.TOUCHEVENTS);
        this.sinkEvents(Event.MOUSEEVENTS);
        this.addTouchCancelHandler(this);
        this.addTouchEndHandler(this);
        this.addTouchStartHandler(this);
    }

    private void setStyle() {
        Style style = innerAbsolutePanel.getElement().getStyle();
        style.setPaddingRight(3, Style.Unit.PX);
        style.setBackgroundColor("#FFC800");
        style.setProperty("border", "1px solid #000000");
        style.setProperty("borderRadius", "5px");
    }

    /**
     * Methode to calculate the width of the arrow.
     */
    protected void calculateWidth() {
        innerAbsolutePanel.setPixelSize(40, 25);
    }

    /**
     * This methode returns the operator of the arrow.
     * @return
     */
    public String getOperator() {
        return operator.getText();
    }

    public void unSetDrawFocus() {
        innerAbsolutePanel.getElement().getStyle().setProperty("backgroundColor", backgroundColor);
    }

    public void onTouchStart(TouchStartEvent event) {
        event.preventDefault();
        if (event.getTouches().length() > 0) {
            press = true;
            taptime = System.currentTimeMillis();
            doubletap.add(taptime);
        }
    }

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
            }
        }
    }

    public void onTouchCancel(TouchCancelEvent event) {
        event.preventDefault();
    }

    private void setDrawState() {
        if(!wantDraw) {
            ((CalculationPanel) this.getParent()).cancelDraw();
            innerAbsolutePanel.getElement().getStyle().setProperty("backgroundColor", backgroundColor);
            wantDraw=true;
        } else {
            ((CalculationPanel) this.getParent()).startDraw(this);
            innerAbsolutePanel.getElement().getStyle().setProperty("backgroundColor", "red");
            wantDraw = false;
        }
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

    private void setClickValues(Event event) {
        if(!isDraggable) {
            press = true;
            taptime = System.currentTimeMillis();
            doubletap.add(taptime);
        }
    }

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
            }
        }
    }

    /**
     * click/tap behavior
     */
    private void click() {
        if(this.getParent() instanceof CalculationPanel) {
            if(((CalculationPanel) this.getParent()).isDraw() && this != ((CalculationPanel) this.getParent()).getSender()) {
                ((CalculationPanel) this.getParent()).endDraw(this);
                doubletap.clear();
            }
        }
    }
}
