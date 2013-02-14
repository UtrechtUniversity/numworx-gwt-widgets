package nl.uu.fi.algebraarrowapplet.client.arrows;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Created with IntelliJ IDEA.
 * User: Jaap
 * Date: 13-12-12
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public class InOutArrowMenu extends PopupPanel {

    Command toggleLabelCommand;
    Command toggleTableCommand;
    Command toggleChainCommand;
    Command toggleCancelCommand;

    MenuBar popupMenuBar = new MenuBar(true);
    MenuItem labelItem;
    MenuItem tableItem;
    MenuItem chainItem;
    MenuItem cancel;

    InOutArrow arrow;

    /**
     * Constructor which needs a boolean to set the visibility and the InOutArrow from where the menu is created.
     * @param visibility
     * @param pArrow
     */
    public InOutArrowMenu(boolean visibility, InOutArrow pArrow) {
        super(visibility);
        arrow = pArrow;
        setCommands();
        addMenuItems();
        setStyle();
        popupMenuBar.setVisible(true);
        this.add(popupMenuBar);
    }

    /**
     * This methode sets the commands which are used to add functionality to menuItems.
     */
    private void setCommands() {
        toggleLabelCommand = new Command() {
            public void execute() {
                arrow.toggleLabel();
            }
        };

        toggleTableCommand = new Command() {
            public void execute() {
                arrow.toggleTable();
            }
        };

        toggleChainCommand = new Command() {
            public void execute() {
                arrow.toggleChain();
            }
        };

        toggleCancelCommand = new Command() {
            public void execute() {
                arrow.toggleCancel();
            }
        };
    }

    /**
     * This methode adds menuItems to the menu.
     */
    private void addMenuItems() {
        labelItem = new MenuItem("Show/Hide label", true, toggleLabelCommand);
        tableItem = new MenuItem("Show/Hide tabel", true, toggleTableCommand);
        chainItem = new MenuItem("Show 3", true, toggleChainCommand);
        cancel = new MenuItem("Annuleer", true, toggleCancelCommand);
        popupMenuBar.addItem(labelItem);
        popupMenuBar.addItem(tableItem);
        popupMenuBar.addItem(chainItem);
        popupMenuBar.addItem(cancel);
    }

    /**
     * This methode sets the style of the menu.
     */
    private void setStyle() {
        labelItem.getElement().getStyle().setBackgroundColor("#FFFFFF");
        tableItem.getElement().getStyle().setBackgroundColor("#FFFFFF");
        chainItem.getElement().getStyle().setBackgroundColor("#FFFFFF");
        popupMenuBar.getElement().getStyle().setBackgroundColor("#FFFFFF");
        popupMenuBar.getElement().getStyle().setProperty("border", "1px solid #000000");
        popupMenuBar.getElement().getStyle().setProperty("cursor", "pointer");
    }
}
