package nl.uu.fi.algebraarrowapplet.client.arrows;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import nl.uu.fi.algebraarrowapplet.client.CalculationPanel;
import nl.uu.fi.algebraarrowapplet.client.ToolkitPanel;

import java.util.ArrayList;

public abstract class EditableArrow extends Arrow implements KeyPressHandler, BlurHandler {

    protected int value;
    protected TextBox input = new TextBox();
    protected Label text = new Label("");
    protected boolean visible;
    protected boolean swipe;
    protected int x;
    protected int y;

    public EditableArrow(ToolkitPanel.ArrowDirection direction, int pValue, PickupDragController pController) {
        super(direction, pController);
        value = pValue;
        input.setVisible(false);
        text.setText(Integer.toString(value));
        input.addKeyPressHandler(this);
        input.addBlurHandler(this);
    }

    public void onKeyPress(KeyPressEvent event) {
        updateText();
        calculateWidth();
    }

    /**
     * Methode om de text uit het invoerveld in label te zetten
     */
    private void updateText() {
        if (isNum(input.getText())) {
            value = Integer.parseInt(input.getText());
            text.setText(Integer.toString(value));
            if(this instanceof InOutArrow){
                text.getElement().getStyle().setTop(3, Style.Unit.PX);
            }
        } else {
            text.setText(null);
            input.setText(null);
        }
    }

    /**
     * Controle if parameter is numeric
     *
     * @param input string input
     * @return true = numeric
     */
    private boolean isNum(String input) {
        if (input.matches("\\d+")) {
            return true;
        } else {
            return false;
        }
    }

    public int getValue() {
        return value;
    }

    /**
     * Focus off when click out an arrow
     * @param event
     */
    public void onBlur(BlurEvent event) {
        event.stopPropagation();
        InputFocusOff();
        doubletap.clear();
        press=false;
    }

    protected abstract void calculateWidth();

    protected void setInputFocus() {
        if (visible) {
            InputFocusOff();
        } else {
            input.setVisible(true);
            input.setEnabled(true);
            input.setFocus(true);
            visible = true;
        }
    }

    private void InputFocusOff() {
        input.setVisible(false);
        input.setEnabled(false);
        input.setFocus(false);
        visible = false;
        updateText();
        calculateWidth();
    }

    /**
     * repaint the parent calculationpanel
     */
    protected void repaintParent() {
        if(this.getParent() instanceof CalculationPanel) {
            ((CalculationPanel) this.getParent()).repaint();
        }
    }

    /**
     * set click start values
     * @param event
     */
    protected void setClickValues(Event event) {
        if(!isDraggable) {
            x = event.getClientX();
            y = event.getClientY();
            press = true;
            taptime = System.currentTimeMillis();
            doubletap.add(taptime);
        }
    }

    /**
     * Behavior of one click/touch
     */
    protected void click() {
        if(this.getParent() instanceof CalculationPanel) {
            if(((CalculationPanel) this.getParent()).isDraw() && this != ((CalculationPanel) this.getParent()).getSender()) {
                ((CalculationPanel) this.getParent()).endDraw(this);
                doubletap.clear();
            } else {
                setInputFocus();
            }
        } else {
            setInputFocus();
        }
    }
}
