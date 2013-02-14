package nl.uu.fi.algebraarrowapplet.client;

import com.allen_sauer.gwt.dnd.client.*;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import nl.uu.fi.algebraarrowapplet.client.arrows.Arrow;

import java.util.ArrayList;

public class ToolkitPanel extends TrashContainer implements DragHandler {

    private PickupDragController dragController;
    private int height = 40;
    private int left = 15;
    private CalculationPanel calculationPanel;
    private boolean madeOne = false;
    public enum ArrowDirection {LEFT, RIGHT}
    private ArrowDirection arrowDirection = ArrowDirection.RIGHT ;

    private ArrowFactory factory;

    /**
     * Constructor which stores the PickUpDragController and the CalculationPanel in attributes of the class.
     * @param pDragController
     * @param pCalculationPanel
     */
    public ToolkitPanel(PickupDragController pDragController, CalculationPanel pCalculationPanel) {
        dragController = pDragController;
        calculationPanel = pCalculationPanel;
        dragController.addDragHandler(this);
        dragController.addDragHandler(calculationPanel);
        factory = new ArrowFactory(dragController);
        setStyle();
        addInterface();
    }

    /**
     * This methode executes when DragEndEvent event is fired.
     * @param event
     */
    public void onDragEnd(DragEndEvent event) {
        madeOne=false;
        if(event.getSource() instanceof Arrow) {
            dragController.makeNotDraggable((Arrow) event.getSource());
            ((Arrow) event.getSource()).setNotDraggable();
        }
    }

    /**
     * This methode executes when DragStartEvent event is fired.
     * @param event
     */
    public void onDragStart(DragStartEvent event) {
        newComponent(event);
    }

    /**
     * This methode creates a new component of the type of the component used to fire the DragStartEvent event.
     * @param event
     */
    private void newComponent(DragStartEvent event) {
        if (((Arrow) event.getSource()).getParent() instanceof ToolkitPanel && !madeOne) {
            Arrow temp = factory.createDraggableArrow(event.getSource());
            int x = ((Arrow) event.getSource()).getAbsoluteLeft()- this.getAbsoluteLeft()-1;
            int y = ((Arrow) event.getSource()).getAbsoluteTop() - this.getAbsoluteTop()-1;
            this.add(temp, x, y);
            madeOne=true;
        }
    }

    /**
     * This methode executes when DragEndEvent event is fired.
     * @param event
     * @throws VetoDragException
     */
    public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
    }

    /**
     * This methode executes when DragStartEvent event is fired.
     * @param event
     * @throws VetoDragException
     */
    public void onPreviewDragStart(DragStartEvent event)
            throws VetoDragException {
    }

    /**
     * This methode creates the interface of the ToolkitPanel by adding all the components to it.
     */
    private void addInterface() {
        addLabel("In/Uitvoer");
        setInOutArrow();
        addLabel("Bewerkingen");
        setObjects(null);
        addLabel("Opties");
        this.add(new SwitchButton("Wissel"), left, height);
        height += 25;
        this.add(new CheckBox("tabel"), left, height);
        height += 25;
        this.add(new CheckBox("grafiek"), left, height);
        height += 25;
        this.add(new ClearButton("Wissen", calculationPanel), left, height);
        height += 30;
    }

    /**
     * This methode adds a label to the ToolkitPanel.
     * @param text
     */
    private void addLabel(final String text){
        this.add(new Label(text), left, height);
        height += 25;
    }

    /**
     * This methode adds an InOutArrow to the ToolkitPanel.
     */
    private void setInOutArrow(){
        Arrow inOutArrow = factory.createInOutArrow(arrowDirection);
        this.add(inOutArrow, left, height);
        height +=35;
    }

    /**
     * This method changes the direction of the arrows in the toolkitPanel
     *
     * @param values
     */
    private void setObjects(final int[] values) {
        for (Arrow component : factory.createToolkitArrows(arrowDirection, values)) {
            this.add(component, left, height);
            height += 40;
        }
    }

    /**
     * This methode returns the arrowDirection.
     * @return
     */
    public ArrowDirection getArrowDirection() {
        return arrowDirection;
    }

    /**
     * this methode sets the style of the ToolkitPanel.
     */
    private void setStyle() {
        this.setPixelSize(110, 600);
        Style style = this.getElement().getStyle();
        style.setProperty("backgroundColor", "#D2D2D2");
        style.setProperty("borderWidth", "1px 0 1px 1px");
        style.setProperty("borderStyle", "solid");
        style.setProperty("borderColor", "#000000");
    }

    /**
     * this methode returns a list of arrows in the ToolkitPanel.
     * @return
     */
    private ArrayList<Arrow> getArrows() {
        int widgetCount = this.getWidgetCount();
        ArrayList<Arrow> arrows = new ArrayList<Arrow>();
        for(int i=0;i< widgetCount;i++) {
            if(this.getWidget(i) instanceof Arrow){
                arrows.add((Arrow) this.getWidget(i));
            }
        }
        return arrows;
    }

    /**
     * This methode switches the direction of the arrows in the ToolkitPanel.
     */
    public void switchArrows() {
        if(arrowDirection == ArrowDirection.RIGHT) {
            arrowDirection = ArrowDirection.LEFT;
        } else {
            arrowDirection = ArrowDirection.RIGHT;
        }
        ArrayList<Arrow> arrows = getArrows();
        for(Arrow a : arrows) {
            a.setDirection(arrowDirection);
        }
    }
}

