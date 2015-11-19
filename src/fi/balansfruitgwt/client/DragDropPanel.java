package fi.balansfruitgwt.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

/**
 * .
 * DragDropPanel makes the containers and control them
 *
 * @author casperkolkman
 * @version 1.0
 * @since 25-11-2012
 */
public final class DragDropPanel extends AbsolutePanel {

    private static final int LMARGINLCONTAINER = 7;
    private static final int LMARGINRCONTAINER = 286;
    private static final int LMARGINLEFTMOST = 0;
    private static final int TMARGINTOPMOST = 0;
    private static final int TMARGINSTOCK = 260;
    private static final int TMARGINBALANCED = 31;
    private static final int TMARGINMOREWEIGHT = 61;
    private static final int TMARGINLESSWEIGHT = 1;
    private static final int TMARGINBALANCE = 40;
    private static final int APPWIDTH = 500;
    private static final int APPHEIGHT = 360;
    private static final int SCALEWIDTH = 204;
    private static final int SCALEHEIGHT = 120;
    private static final int STOCKHEIGHT = 100;

    private ClosedContainer outsideContainers = new ClosedContainer(APPWIDTH, APPHEIGHT, this);
    private Container left = new Container(SCALEWIDTH, SCALEHEIGHT, this);
    private Container right = new Container(SCALEWIDTH, SCALEHEIGHT, this);
    private Container stock = new Container(APPWIDTH, STOCKHEIGHT, this);

    private AbsolutePanel balance;
    private boolean balanceDestinationSet = false;

    private PickupDragController dragController;
	private BalansFruitGWT balansFruitGWT;

    public DragDropPanel() {
        super.add(outsideContainers, LMARGINLEFTMOST, TMARGINTOPMOST);
        super.add(left, LMARGINLCONTAINER, TMARGINBALANCED);
        super.add(right, LMARGINRCONTAINER, TMARGINBALANCED);
        super.add(stock, LMARGINLEFTMOST, TMARGINSTOCK);
        super.setPixelSize(APPWIDTH, APPHEIGHT);
        dragController = new PickupDragController(this, true);
        this.getElement().getStyle().setPosition(Position.RELATIVE);
        registerDropControllers();
        dragController.addDragHandler(new OutOfBorderHandler(dragController));
    }

    /**
     * .
     * Register all dropcontrollers in the dragController
     */
    private void registerDropControllers() {
        Container[] containerList = new Container[] {outsideContainers, left, right, stock};
        for (Container container : containerList) {
            DropController dropcontroller = new ContainerDropController(container);
            dragController.registerDropController(dropcontroller);
        }
    }

    /**
     * .
     * Set the destination of the balance picture
     * @param pBalance panel where balance picture is added too
     */
    public void setBalanceDestination(final AbsolutePanel pBalance) {
    	if(!balanceDestinationSet) {
    		balance = pBalance;
            drawBalance(Images.INSTANCE.balanceCenter());
            balanceDestinationSet = true;
    	}     
    }

    /**
     * .
     * Painting the balance towards a direction
     * @param img of the balance to draw
     */
    private void drawBalance(final ImageResource img) {
        balance.clear();
        balance.add(new Image(img), LMARGINLEFTMOST, TMARGINBALANCE);
    }

    /**
     * .
     * Redraw the balance
     */
    public void redrawBalance() {
        if (left.getWeight() > right.getWeight()) {
            drawBalance(Images.INSTANCE.balanceLeft());
            setPositionContainer(left, LMARGINLCONTAINER, TMARGINMOREWEIGHT);
            setPositionContainer(right, LMARGINRCONTAINER, TMARGINLESSWEIGHT);

        } else if (left.getWeight() < right.getWeight()) {
            drawBalance(Images.INSTANCE.balanceRight());
            setPositionContainer(left, LMARGINLCONTAINER, TMARGINLESSWEIGHT);
            setPositionContainer(right, LMARGINRCONTAINER, TMARGINMOREWEIGHT);
        } else {
            drawBalance(Images.INSTANCE.balanceCenter());
            setPositionContainer(left, LMARGINLCONTAINER, TMARGINBALANCED);
            setPositionContainer(right, LMARGINRCONTAINER, TMARGINBALANCED);
        }
    }
    
    private char equationOperator() {
        if (left.getWeight() > right.getWeight()) {
            return '>';
        } else if (left.getWeight() < right.getWeight()) {
            return '<';
        } else {
            return '=';
        }
    }
    
    public String generateEquation() {
    	StringBuilder sb = new StringBuilder();
    	getExpression(left, sb);
    	sb.append(equationOperator());
    	getExpression(right, sb);
    	return sb.toString();
    }

    private int getCount(Container c, int id) {
    	int result = 0;
    	int len = c.getWidgetCount();
    	for(int i = 0; i < len; i++) {
    		if ( id == ((DraggableObject) c.getWidget(i)).getId()) 
    			result++;
    	}
    	return result;  	
    }
    
    private void getExpression(Container c, StringBuilder sb) {
    	int cntX = getCount(c, DWOAdapter.idX);
    	int cnt1 = getCount(c, DWOAdapter.idOne) + getCount(c, DWOAdapter.idOneEmpty);
    	if( cnt1 == 0 && cntX == 0)
    	{	sb.append('0');
    		return;
    	}
    	if( cntX == 1) {
    		sb.append('x');
    	} else if(cntX > 1) {
    		sb.append(cntX).append('x');
    	}
    	if (cntX != 0 && cnt1 != 0)
    		sb.append('+');
    	if (cnt1 > 0) {
    		sb.append(cnt1);
    	}
	}

	/**
     * .
     * set the position of a container
     * @param container container that needs to be repositioned
     * @param leftMargin new margin for the x position
     * @param topMargin new margin for the y position
     */
    private void setPositionContainer(final Container container, final int leftMargin, final int topMargin) {
        Container temp = container;
        container.removeFromParent();
        this.add(temp, leftMargin, topMargin);
    }

    /**
     * .
     * @return the left container
     */
    public Container getLeftContainer() {
        return left;
    }

    /**
     * .
     * @return the left container
     */
    public Container getRightContainer() {
        return right;
    }

    /**
     * .
     * @return the stock container
     */
    public Container getStockContainer() {
        return stock;
    }

    /**
     * .
     * @return the dragController
     */
    public PickupDragController getDragController() {
        return dragController;
    }

    public void reset() {
        left.clear();
        right.clear();
        stock.clear();
    }

	public void setEquation(BalansFruitGWT balansFruitGWT, boolean b) {
		this.balansFruitGWT = balansFruitGWT;
		balansFruitGWT.setEquation(generateEquation(), b);
	}
	
	public void setEquation() {
		setEquation(balansFruitGWT, true);
	}
}
