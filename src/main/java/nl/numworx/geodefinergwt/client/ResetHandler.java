package nl.numworx.geodefinergwt.client;

import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

import nl.numworx.geodefiner.common.Instance;

public class ResetHandler extends nl.numworx.geodefiner.common.ResetHandler {

  PopupPanel popup;
  
  @Inject ResetHandler(Instance instance) {
    super("Reset", instance); // i18n
  }

  private void confirm(ClickEvent ev) {
    super.command();
    cancel(ev);
  }
  private void cancel(ClickEvent ev) {
    popup.hide();
    popup = null;
  }
  
  @Override
  public void command() {
      DialogBox box = new DialogBox(true, true);
      box.addCloseHandler(ev -> { popup = null; });
      FlowPanel contents = new FlowPanel();
      FlowPanel buttons = new FlowPanel();
      contents.add(new Label("Reset?"));
      Button ja = new Button(GeoDefinerGWT.MESSAGES.LabelTester_0()); ja.addClickHandler(this::confirm);
      ja.getElement().getStyle().setPaddingLeft(20, Style.Unit.PX);
      Button nee = new Button(GeoDefinerGWT.MESSAGES.LabelTester_1()); nee.addClickHandler(this::cancel);
      nee.getElement().getStyle().setFloat(Float.RIGHT);
      nee.getElement().getStyle().setPaddingRight(20, Style.Unit.PX);
      buttons.add(ja);buttons.add(nee);
      contents.add(buttons);
      box.setWidget(contents);
      popup = box;
      box.center();
  }

  
}
