package nl.uu.fi.algebraarrowapplet.client.arrows;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class LabelPanel extends AbsolutePanel implements MouseDownHandler, MouseUpHandler, BlurHandler, KeyPressHandler {

    private Label inputLabel = new Label("invoer");
    private TextBox inputLabelTextbox = new TextBox();

    /**
     * Constructor
     */
    public LabelPanel() {
        setStyle();
        this.add(inputLabel, 0, 0);
        this.add(inputLabelTextbox, 0, 0);
        inputLabelTextbox.setVisible(false);
        addHandlers();

    }

    /**
     * This methode adds eventhandlers to the Document Object Model.
     */
    private void addHandlers() {
        this.addDomHandler(this, MouseDownEvent.getType());
        this.addDomHandler(this, MouseUpEvent.getType());
        inputLabelTextbox.addBlurHandler(this);
        inputLabelTextbox.addKeyPressHandler(this);
    }

    /**
     * This methodes sets the style of the label.
     */
    private void setStyle() {
        this.getElement().getStyle().setProperty("borderLeft", "1px solid #000000");
        this.getElement().getStyle().setProperty("borderTop", "1px solid #000000");
        this.getElement().getStyle().setProperty("borderRight", "1px solid #000000");
        this.getElement().getStyle().setProperty("backgroundColor", "#848484");
        inputLabel.getElement().getStyle().setProperty("color", "#FFFFFF");
        inputLabel.getElement().getStyle().setProperty("textAlign", "center");
    }

    /**
     * This method returns the inputlabel.
     * @return Label
     */
    public Label getinputLabel() {
        return inputLabel;
    }

    /**
     * This methode executes when the blurEvent is fired.
     * @param event
     */
    public void onBlur(BlurEvent event) {
        event.stopPropagation();
        blurOff();
    }

    /**
     * This methode executes when the KeyPressEvent is fired.
     * @param event the {@link KeyPressEvent} that was fired
     */
    public void onKeyPress(KeyPressEvent event) {
        event.stopPropagation();
        updateText();
        if (this.getParent() instanceof InOutArrow) {
            ((InOutArrow) this.getParent()).calculateWidth();
        }
    }

    /**
     * Methode to set the focus to the textbox.
     */
    private void blurOn() {
        inputLabelTextbox.setVisible(true);
        inputLabelTextbox.setEnabled(true);
        inputLabelTextbox.setFocus(true);
    }

    /**
     * Methode to set the focus of the textbox off.
     */
    private void blurOff() {
        inputLabelTextbox.setVisible(false);
        inputLabelTextbox.setEnabled(false);
        inputLabelTextbox.setFocus(false);
        updateText();
        if (this.getParent().getParent() instanceof InOutArrow) {
            ((InOutArrow) this.getParent().getParent()).calculateWidth();
        }
    }

    /**
     * Methode to set the text from the textbox to the label.
     */
    private void updateText() {
        inputLabel.setText(inputLabelTextbox.getText());
    }

    /**
     * This methode executes when the MouseDownEvent is fired.
     * @param event the {@link MouseDownEvent} that was fired
     */
    public void onMouseDown(MouseDownEvent event) {
        event.preventDefault();
        event.stopPropagation();
        blurOn();
    }

    /**
     * This methode executes when the MouseUpEvent is fired.
     * @param event
     */
    public void onMouseUp(MouseUpEvent event) {
        event.preventDefault();
        event.stopPropagation();
    }
}
