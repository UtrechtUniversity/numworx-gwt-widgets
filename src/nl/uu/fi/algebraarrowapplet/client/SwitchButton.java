package nl.uu.fi.algebraarrowapplet.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import nl.uu.fi.algebraarrowapplet.client.ToolkitPanel.ArrowDirection;

import java.util.ArrayList;

/**
 *  @author casperkolkman
 *  This class makes a button to switch arrow
 */
public final class SwitchButton extends Button {

    public SwitchButton(String name){
        super(name);
        super.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                switchArrows();
            }
        });
    }

    private void switchArrows(){
        if (this.getParent() instanceof ToolkitPanel) {
            ToolkitPanel toolkitPanel = (ToolkitPanel) this.getParent();
            toolkitPanel.switchArrows();
        }
    }
}
