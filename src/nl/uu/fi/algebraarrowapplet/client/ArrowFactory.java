package nl.uu.fi.algebraarrowapplet.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import nl.uu.fi.algebraarrowapplet.client.ToolkitPanel.ArrowDirection;
import nl.uu.fi.algebraarrowapplet.client.arrows.Arrow;
import nl.uu.fi.algebraarrowapplet.client.arrows.ExpressionArrow;
import nl.uu.fi.algebraarrowapplet.client.arrows.InOutArrow;
import nl.uu.fi.algebraarrowapplet.client.arrows.NonEditableArrow;

import java.util.ArrayList;

public class ArrowFactory {

    private PickupDragController dragController;
    private String[] operatorsEditable = {"+", "-", "/","x"};
    private String[] operatorsNonEditable = {"1/...", "\u221A...", "...\u00B2"};

    /**
     * Class constructor
     * @param controller
     */
    public ArrowFactory(PickupDragController controller) {
        dragController = controller;
    }

    /**
     * Method using to make Editable and non-Editable arrows with certain direction(right or left)
     * @param direction
     * @param values
     * @return
     */
    public Arrow[] createToolkitArrows(ArrowDirection direction, int[] values) {
        int[] invoerWaarde = new int[operatorsEditable.length];
        if (values != null) {
            invoerWaarde = values;
        }
        ArrayList<Arrow> temp = new ArrayList<Arrow>();

        for (int i = 0; i < operatorsEditable.length; i++) {
            temp.add(new ExpressionArrow(direction, operatorsEditable[i], invoerWaarde[i], dragController));
        }

        for (int i = 0; i < operatorsNonEditable.length; i++) {
            temp.add(new NonEditableArrow(direction, operatorsNonEditable[i], dragController));
        }

        Arrow[] temp1 = new Arrow[temp.size()];
        temp1 = temp.toArray(temp1);
        return temp1;
    }

    /**
     * Method using to make InOut arrow with certain direction(right or left)
     * @param direction
     * @return
     */
    public Arrow createInOutArrow(ArrowDirection direction) {
        Arrow inOutArrow = new InOutArrow(direction, 0, dragController);
        return inOutArrow;
    }

    /**
     * Method using to make a new arrow element instead element that was dragged
     * @param widget
     * @return
     */
    public Arrow createDraggableArrow(final Object widget) {
        Arrow element;
        ArrowDirection temp = ((ToolkitPanel) ((Arrow) widget).getParent()).getArrowDirection();
        if(widget instanceof ExpressionArrow) {
            element = new ExpressionArrow(temp, ((ExpressionArrow) widget).getOperator(), ((ExpressionArrow) widget).getValue(), dragController);
        } else if (widget instanceof InOutArrow){
            element = new InOutArrow(temp, ((InOutArrow) widget).getValue(), dragController);
        } else  {
            element = new NonEditableArrow(temp, ((NonEditableArrow) widget).getOperator(), dragController);
        }
        return element;
    }
}

