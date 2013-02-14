package nl.uu.fi.algebraarrowapplet.client;

import com.allen_sauer.gwt.dnd.client.drop.DropController;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import nl.uu.fi.algebraarrowapplet.client.arrows.ExpressionArrow;

/**
 * This class specific the behavior of DropController for a container
 * @author casperkolkman
 * @since 26-11-2012
 * @version 1.0
 *
 */
public final class ToolkitPanelDropController extends SimpleDropController {

    private TrashContainer container;

    /**
     * Constructor saves the trashContainer
     * @param container that must have a dropcontroller
     */
    public ToolkitPanelDropController(TrashContainer container) {
        super(container);
        this.container = container;
    }

    @Override
    public void onDrop(DragContext context) {
        for (Widget widget : context.selectedWidgets) {
            container.eatWidget(widget);
        }
        super.onDrop(context);
    }

    @Override
    public void onEnter(DragContext context) {
        super.onEnter(context);
    }

    @Override
    public void onLeave(DragContext context) {
        super.onLeave(context);
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
        super.onPreviewDrop(context);
        if (!container.isWidgetEater()) {
            throw new VetoDragException();
        }
    }
}
