package nl.uu.fi.algebraarrowapplet.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * The class represent a button that can clear a calculation panel
 *
 * @author casperkolkman
 * @since 05-12-2012
 * @version 1.0
 *
 */
public final class ClearButton extends Button {

    private CalculationPanel panelToClear;

    public ClearButton(final String name, final CalculationPanel destination){
        super(name);
        panelToClear = destination;
        this.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                panelToClear.clear();
            }
        });
    }
}
