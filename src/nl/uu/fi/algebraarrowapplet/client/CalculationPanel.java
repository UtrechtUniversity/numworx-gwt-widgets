package nl.uu.fi.algebraarrowapplet.client;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import nl.uu.fi.algebraarrowapplet.client.arrows.Arrow;
import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Image;
import org.vaadin.gwtgraphics.client.shape.Path;

import java.util.List;

/**
 * @author Casper Kolkman
 * @version 2.0
 * @since 30-12-2012
 */
public class CalculationPanel extends AbsolutePanel implements DragHandler {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 700;

    private boolean draw;
    private Widget sender;
    private Widget receiver;
    private DrawingArea canvas = new DrawingArea(WIDTH, HEIGHT);

    public CalculationPanel() {
        this.setPixelSize(WIDTH, HEIGHT);
        this.getElement().getStyle().setProperty("border", "1px solid #000000");
        this.add(canvas);
    }

    /**
     * Widget can call startDraw by double tap/click
     * @param sender widget that starts the draw
     */
    public void startDraw(final Widget sender) {
        this.sender = sender;
        draw=true;
    }

    /**
     * endDraw will be call if this panel is draw and there will be
     * tap/click on a widget that is not the sender
     * @param receiver widget that ends the draw
     */
    public void endDraw(final Widget receiver) {
        this.receiver = receiver;
        boolean lineExists = false;
        List<Widget> receivers = ((Arrow) sender).getReceivers();
        for (int i = 0; i < receivers.size(); i++ ) {
            if(receivers.get(i) == receiver) {
                receivers.remove(i);
                lineExists = true;
            }
        }

        if(lineExists) {
            resetDraw();
            ((Arrow) sender).setReceivers(receivers);
            repaint();
        }

        if(NotSameSenderReceiver() && !lineExists) {
            resetDraw();
            ((Arrow) sender).addReceiver(receiver);
            drawLine(this.sender, this.receiver);
        }
    }

    /**
     * reset draw state
     */
    public void resetDraw() {
        draw=false;
        resetDrawFocus();
    }

    /**
     * Reset the start draw focus color
     */
    private void resetDrawFocus() {
        if(sender instanceof Arrow) {
            ((Arrow) sender).unSetDrawFocus();
            ((Arrow) sender).setWantDraw();
        }
    }

    /**
     * @author Ersan Öztürk
     * draw a line between pSender and pReceiver
     * @param pSender start widget
     * @param pReceiver end widget
     */
    private void drawLine(final Widget pSender, final Widget pReceiver) {
        int startposX = pSender.getAbsoluteLeft()-this.getAbsoluteLeft() + pSender.getOffsetWidth()-2;
        int startposY = pSender.getAbsoluteTop()-this.getAbsoluteTop() + pSender.getOffsetHeight()/2-2;

        int endposX = pReceiver.getAbsoluteLeft()-this.getAbsoluteLeft() + Images.INSTANCE.point().getWidth()+10;
        int endposY = pReceiver.getAbsoluteTop()-this.getAbsoluteTop() + pReceiver.getOffsetHeight()/2-2;

        Path path = new Path(startposX, startposY);
        path.setFillOpacity(0);

        // magic # 42: moet + en dan - als naar beneden, - en dan + als naar boven

        // fine-tune curves afhankelijk van de richting van de getrokken pijl.

        // CONTROL POINTS:
        // SRC
        // 1 - x: 20, y: 10
        // 2 - x: 70, y: 10
        // DEST
        // 3 - x: -60 y: 10
        // 4 - x: 0, y: 10

        if ((endposY <= startposY + 70 && endposY >= startposY - 70) && (endposX > startposX)) {
            path.curveTo(startposX + 69, startposY, endposX - 69, endposY, endposX - 19, endposY);
        } else if (startposX >= endposX && startposY >= endposY) {
            path.curveTo(startposX + 69, startposY - 42, endposX - 69, endposY + 42, endposX - 19, endposY);
        } else {
            path.curveTo(startposX + 69, startposY + 42, endposX - 69, endposY - 42, endposX - 19, endposY);
        }
        canvas.add(path);

        Image arrow = new Image(endposX - 20, endposY - 5, 10, 10, Images.INSTANCE.drawArrow().getURL());
        canvas.add(arrow);
    }

    /**
     * method to check if panel is in drawing process
     * @return true = in drawing proces
     */
    public boolean isDraw() {
        return draw;
    }

    /**
     * Look if sender is not the reciever
     * @return true = sender is not receiver
     */
    private boolean NotSameSenderReceiver() {
        if(sender != receiver) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * OnDragEnd the canvas needs to be repainted
     * @param dragEndEvent
     */
    public void onDragEnd(DragEndEvent dragEndEvent) {
        if(dragEndEvent.getSource() instanceof Arrow) {
            repaint();
        }
    }

    /**
     * Draw for each widget a line to the receiver
     */
    public void repaint() {
        canvas.clear();
        for (int i = 0; i < this.getWidgetCount(); i++) {
            if(this.getWidget(i) instanceof Arrow) {
                List<Widget> receivers = ((Arrow) this.getWidget(i)).getReceivers();
                for(int j = 0; j < receivers.size(); j++) {
                    drawLine(this.getWidget(i), receivers.get(j));
                }
            }
        }
    }

    public void onDragStart(DragStartEvent dragStartEvent) {}

    public void onPreviewDragEnd(DragEndEvent dragEndEvent) throws VetoDragException {}

    public void onPreviewDragStart(DragStartEvent dragStartEvent) throws VetoDragException { }

    /**
     * cancel a draw
     */
    public void cancelDraw() {
        sender = null;
        draw = false;
    }

    public Widget getSender() {
        return sender;
    }
}
