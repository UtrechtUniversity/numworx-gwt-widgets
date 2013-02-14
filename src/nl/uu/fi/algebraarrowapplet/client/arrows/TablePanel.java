package nl.uu.fi.algebraarrowapplet.client.arrows;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;
import nl.uu.fi.algebraarrowapplet.client.Images;
import nl.uu.fi.algebraarrowapplet.client.arrows.InOutArrow;

import java.util.ArrayList;
import java.util.List;

/**
 * Tablepanel used in InOutArrow
 */
public class TablePanel extends AbsolutePanel {

    private List<Double> inputNumbers = new ArrayList<Double>();
    private final int amountOfInitialNumbers = 8;
    private Label[] inputLabels = new Label[amountOfInitialNumbers];
    private final int maxValue = 70000;
    private final int minValue = -70000;
    private final int buttonZoomInLeft = 0;
    private final int buttonZoomInTop = 60;
    private final int arrowButtonsLeft = 34;
    private final int arrowMinusTop = 0;
    private final int buttonZoomOutTop = buttonZoomInTop + 70;
    private final int numberListLeft = arrowButtonsLeft + 2;
    private final int numberListTop = 26;
    private final int arrowPlusTop = 201;
    private int focusedLabel = -1;
    private boolean timesBool;
    private boolean firstCalculation = true;
    private int stepNumber = 0;
    private double[] steps = new double[]{2, 2.5, 2};
    private VerticalPanel NumberList = new VerticalPanel();
    double panelWidth = 70;
    private String borderProperties = "1px solid #000000";
    Button arrowMinus;
    Button arrowPlus;
    Button buttonZoomIn;
    Button buttonZoomOut;
    Command zoomOutInputNumbers;
    Command zoomInInputNumbers;
    Command decreaseInputNumbers;
    Command increaseInputNumbers;

    /**
     * Constructor
     */
    public TablePanel() {
        initiateInputNumbers();
        setButtons();
        buildPanel();
        setStyle();
    }

    /**
     * Sets inputnumbers for the table.
     * Amount of numbers determined by variable amountOfInitialNumbers
     */
    private void initiateInputNumbers() {
        for (double i = 0; i < amountOfInitialNumbers; i++) {
            inputNumbers.add(i);
        }
    }

    /**
     * Sets the Tablepanel's buttons
     * Buttons used to zoom in/out and going up/down the table.
     */
    private void setButtons() {
        setCommands();
        arrowMinus = createButton(Images.INSTANCE.arrowMinus(), decreaseInputNumbers);
        arrowPlus = createButton(Images.INSTANCE.arrowPlus(), increaseInputNumbers);
        buttonZoomOut = createButton(Images.INSTANCE.buttonZoomOut(), zoomOutInputNumbers);
        buttonZoomIn = createButton(Images.INSTANCE.buttonZoomIn(), zoomInInputNumbers);
    }

    /**
     * sets Commands for the button's methods
     * Commands can be used as a reference for a method
     * Commands can be used as a method variable
     */
    private void setCommands() {
        zoomOutInputNumbers = new Command() {
            public void execute() {
                zoomOutInputNumbers();
            }
        };
        zoomInInputNumbers = new Command() {
            public void execute() {
                zoomInInputNumbers();
            }
        };
        decreaseInputNumbers = new Command() {
            public void execute() {
                arrowMinus();
            }
        };
        increaseInputNumbers = new Command() {
            public void execute() {
                arrowPlus();
            }
        };
    }

    /**
     * Sets the Panel's style.
     */
    private void setStyle() {
        this.setSize(panelWidth + "px", "230px");
        this.getElement().getStyle().setBackgroundColor("#848484");
        setStyleProperty("borderLeft", borderProperties);
        setStyleProperty("borderRight", borderProperties);
        setStyleProperty("borderBottom", borderProperties);
        setStyleProperty("marginTop", "-5px");
        NumberList.getElement().getStyle().setBackgroundColor("#FFFFFF");
        NumberList.getElement().getStyle().setProperty("border", "1px solid #000000");
    }

    /**
     * Method used to set style of certain parts of the panel
     * @param style Part of the panel that has to be set
     * @param properties  Style properties given to specific part
     */
    private void setStyleProperty(String style, String properties) {
        this.getElement().getStyle().setProperty(style, properties);
    }

    /**
     * Creates button in table panel
     * @param imageResource Image used for button
     * @param command  Method associated with button
     * @return created button
     */
    private Button createButton(ImageResource imageResource, final Command command) {
        Button button = new Button();
        Image image = new Image(imageResource);
        button.getElement().appendChild(image.getElement());
        button.addMouseDownHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                event.preventDefault();
                event.stopPropagation();
                command.execute();
            }
        });
        button.addMouseUpHandler(new MouseUpHandler() {
            public void onMouseUp(MouseUpEvent event) {
                event.preventDefault();
                event.stopPropagation();
            }
        });
        return button;
    }

    /**
     * Adds table's buttons+list
     * Fills the table's list
     */
    private void buildPanel() {
        addTableContent();
        fillNumberList();
    }

    /**
     * Method used in buildPanel() to fill the panel with buttons and a list.
     */
    private void addTableContent() {

        this.add(arrowMinus, arrowButtonsLeft, arrowMinusTop);
        this.add(buttonZoomIn, buttonZoomInLeft, buttonZoomInTop);
        this.add(buttonZoomOut, buttonZoomInLeft, buttonZoomOutTop);
        this.add(NumberList, numberListLeft, numberListTop);
        this.add(arrowPlus, arrowButtonsLeft, arrowPlusTop);
    }

    /**
     * Fills the table with content
     * If a number is selected in the table it's style properties change to highlight selection.
     */
    private void fillNumberList() {
        for (int i = 0; i < inputNumbers.size(); i++) {

            inputLabels[i] = new Label(Double.toString(inputNumbers.get(i)));
            NumberList.add(inputLabels[i]);
            inputLabels[i].getElement().getStyle().setProperty("padding", "0 5px 0 5px");
            inputLabels[i].getElement().getStyle().setProperty("borderTop", "1px solid #FFFFFF");
            inputLabels[i].getElement().getStyle().setProperty("borderBottom", "1px solid #FFFFFF");
            if(focusedLabel == i) {
                inputLabels[i].getElement().getStyle().setProperty("backgroundColor", "#F8E0E6");
                inputLabels[i].getElement().getStyle().setProperty("borderTop", "1px solid #000000");
                inputLabels[i].getElement().getStyle().setProperty("borderBottom", "1px solid #000000");
            }
            inputLabels[i].addMouseDownHandler(new MouseDownHandler() {
                public void onMouseDown(MouseDownEvent event) {
                    event.preventDefault();
                    event.stopPropagation();
                    if(focusedLabel != -1){
                        inputLabels[focusedLabel].getElement().getStyle().setProperty("backgroundColor", "#FFFFFF");
                        inputLabels[focusedLabel].getElement().getStyle().setProperty("borderTop", "1px solid #FFFFFF");
                        inputLabels[focusedLabel].getElement().getStyle().setProperty("borderBottom", "1px solid #FFFFFF");
                    }
                    focusedLabel = getLabelIndex((Label)event.getSource());
                    inputLabels[focusedLabel].getElement().getStyle().setProperty("backgroundColor", "#F8E0E6");
                    inputLabels[focusedLabel].getElement().getStyle().setProperty("borderTop", "1px solid #000000");
                    inputLabels[focusedLabel].getElement().getStyle().setProperty("borderBottom", "1px solid #000000");
                }
            });
            inputLabels[i].addMouseUpHandler(new MouseUpHandler() {
                public void onMouseUp(MouseUpEvent event) {
                    event.preventDefault();
                    event.stopPropagation();
                }
            });
        }
    }

    /**
     * Returns the index of the selected label
     * @param label  Selected label
     * @return Index number of the label
     */
    private int getLabelIndex(Label label){
        int index = -1;
        for(int i=0;i<inputLabels.length;i++) {
            if(label == inputLabels[i]) {
                index = i;
            }
        }
        return index;
    }

    /**
     * Clears the panel and it's content
     * Builds the panel and sets the width.
     * Let's the InOutArrow to which it belongs calculate width
     */
    private void rebuildPanel() {

        this.clear();
        NumberList.clear();
        buildPanel();
        setTableWidth();
        if (this.getParent().getParent() instanceof InOutArrow) {
            ((InOutArrow) this.getParent().getParent()).calculateWidth();
        }
    }

    /**
     * Sets the width by adding the button's width & the width of the table together.
     */
    private void setTableWidth() {
        int zoomButtonWidth = NumberList.getAbsoluteLeft() - this.getAbsoluteLeft();
        int NumberListWidth = NumberList.getOffsetWidth();
        int ExtraWidth = 5;
        panelWidth = zoomButtonWidth + NumberListWidth + ExtraWidth;
        this.getElement().getStyle().setWidth(panelWidth, Style.Unit.PX);
    }

    /**
     * Zooms in on the table's numbers, dividing them.
     * Rebuilds panel to set new numbers and width
     */
    private void zoomInInputNumbers() {
        divide();
        rebuildPanel();


    }

    /**
     * Divide the numbers in the table
     * Numbers are divided by 10 in 3 steps (/2, /2, /2.5)
     */
    private void divide() {
        if (firstCalculation) {
            firstCalculation = false;
            timesBool = false;    //boolean
        } else {
            if (timesBool) {        //!boolean
                timesBool = false;     //boolean
            } else {
                decreaseStepNumber();  //times or divide?
            }
        }
        if (!getDivisionBorderPassed()) {
            for (int i = 0; i < inputNumbers.size(); i++) {
                if (inputNumbers.get(i) != 0) {
                    inputNumbers.set(i, inputNumbers.get(i) / steps[stepNumber]);
                }
            }


        }
    }

    /**
     * Determines if the number is too small to be divided any further.
     * @return Whether or not the number has more than 4 decimals.
     */
    private boolean getDivisionBorderPassed() {
        boolean divisionBorderPassed = false;
        for (int i = 0; i < inputNumbers.size(); i++) {
            if (getNumberOfDecimals(inputNumbers.get(i) / steps[stepNumber]) > 4) {
                divisionBorderPassed = true;
                break;
            }
        }
        return divisionBorderPassed;
    }

    /**
     * Decreases the stepnumber after a division
     * Stepnumber is used  to check what division/multiplier to use next.
     */
    private void decreaseStepNumber() {
        if (stepNumber == 0) {
            stepNumber = steps.length - 1;
        } else {
            stepNumber--;
        }
    }

    /**
     * Zooms out on the table's numbers, multiplying them
     * Rebuilds panel to set new numbers & width
     */
    private void zoomOutInputNumbers() {
        times();
        rebuildPanel();

    }

    /**
     * Multiplies the numbers in the table
     * Numbers are multiplied by 10 in 3 steps (*2, *2, *2.5)
     */
    private void times() {
        if (firstCalculation) {
            firstCalculation = false;
            timesBool = true;
        } else {
            if (!timesBool) {
                timesBool = true;
            } else {
                increaseStepNumber();
            }

        }
        if (!(maxValueReached() || minValueReached() )) {
            for (int i = 0; i < inputNumbers.size(); i++) {
                inputNumbers.set(i, inputNumbers.get(i) * steps[stepNumber]);
            }

        }
    }

    /**
     * Increases the stepnumber after a division
     * Stepnumber is used  to check what division/multiplier to use next.
     */
    private void increaseStepNumber() {
        if (stepNumber == steps.length - 1) {
            stepNumber = 0;
        } else {
            stepNumber++;
        }
    }


    /**
     * Table shifts 1 row down, adding a new number down the list
     * Removes number on top of the list
     * Rebuilds panel
     */
    private void arrowPlus() {
        if (!maxValueReached()) {
            increaseInputNumbers();
            rebuildPanel();
        }
    }

    /**
     * Checks the stepsize between 2 numbers
     * Table shifts 1 row down, adding a new number down the list
     * Removes number on top of the list
     */
    private void increaseInputNumbers() {
        double stepSize = getStepSize();
        inputNumbers.remove(0);
        inputNumbers.add(inputNumbers.get(inputNumbers.size() - 1) + stepSize);
    }

    /**
     * Checks if the highest number of the list has crossed the maximum value
     * @return If the highest number has crossed the maximum value
     */
    private boolean maxValueReached() {
        return inputNumbers.get(amountOfInitialNumbers - 1) > maxValue;
    }

    /**
     * Table shifts 1 row up, adding a new number on top of the list
     * Removes number down the list
     * Rebuilds panel
     */
    private void arrowMinus() {
        if (!minValueReached()) {
            decreaseInputNumbers();
            rebuildPanel();
        }
    }

    /**
     * Checks the stepsize between 2 numbers
     * Table shifts 1 row up, adding a new number on top of the list
     * Removes number on the bottom of the list
     */
    private void decreaseInputNumbers() {
        double stepSize = getStepSize();
        inputNumbers.remove(inputNumbers.size() - 1);
        inputNumbers.add(0, inputNumbers.get(0) - stepSize);
    }

    /**
     * Checks if the lowest number of the list has crossed the minimum value
     * @return If the lowest number has crossed the minimum value
     */
    private boolean minValueReached() {
        return inputNumbers.get(0) < minValue;
    }

    /**
     * Gets the stepsize between 2 numbers
     * @return Stepsize
     */
    private double getStepSize() {
        return inputNumbers.get(1) - inputNumbers.get(0);
    }

    /**
     * Amount of decimals passed the '.'
     * @param number Number for which the decimal checked
     * @return amount of numbers after the '.'
     */
    private static int getNumberOfDecimals(double number) {
        String strNumber = Double.toString(number);

        int stringLength = strNumber.length();
        int numberOfDecimals = 0;
        char theChar = 'e';
        int counter;

        for (counter = 1; theChar != '.'; counter++) {
            theChar = strNumber.charAt(counter);
        }

        numberOfDecimals = stringLength - counter;

        return numberOfDecimals;
    }

    /**
     * Getter for the panelWidth
     * @return  panelWidth
     */
    public double getPanelWidth() {
        return panelWidth;
    }

    /**
     * Getter used for testing, required to access local variable
     * @return Arraylist inputNumbers
     */
    public List<Double> getInputNumbers(){
        return inputNumbers;
    }
    /**
     * Getter used for testing, required to access local variable
     * @return buttonZoomin
     */
    public Button getZoomInButton(){
        return buttonZoomIn;
    }

    /**
     * Method used for testing, checks division method
     */
    public void zoomInTest(){
        divide();
    }

    /**
     * Method used for testing, checks times method
     */
    public void zoomOutTest(){
        times();
    }

    /**
     * Method used for testing, checks increaseInputNumbers method
     */
    public void scrollDownTest(){
        increaseInputNumbers();
    }

    /**
     * Method used for testing, checks decreaseInputNumbers method
     */
    public void scrollUpTest(){
        decreaseInputNumbers();
    }

    /**
     * Method used for testing, checks maxValueReached method
     */
    public boolean maxValueReachedTest(double i){
        inputNumbers.set(7, i);
        return maxValueReached();

    }

    /**
     * Method used for testing, checks minValueReached method
     */
    public boolean minValueReachedTest(double i){
        inputNumbers.set(0, i);
        return minValueReached();

    }
}
