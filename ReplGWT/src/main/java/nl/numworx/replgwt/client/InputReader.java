package nl.numworx.replgwt.client;

import java.util.function.Consumer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;

public class InputReader extends Composite implements HasText, ValueChangeHandler<String>, ClickHandler {

	private static final Consumer<String> NULL = (x) -> {};

	private TextBox textBox;
	
	private Consumer<String> consumer = NULL;
	
	public Consumer<String> getConsumer() {
		return consumer;
	}


	public void setConsumer(Consumer<String> consumer) {
		if (consumer == null)
			consumer = NULL;
		this.consumer = consumer;
	}


	public InputReader() {
		textBox = new TextBox();
		textBox.addValueChangeHandler(this);
		//textBox.addClickHandler(this);
		textBox.addStyleDependentName("input");
		initWidget(textBox);
	}


	@Override
	public String getText() {
		return textBox.getText();
	}


	@Override
	public void setText(String text) {
		this.textBox.setText(text);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		consumer.accept(event.getValue());
	}

	public void start() {
		textBox.setValue("");
		Scheduler.get().scheduleDeferred(() -> { 
			textBox.setFocus(true);			
		});
		
	}


	@Override
	public void onClick(ClickEvent event) {
		consumer.accept(getText());
		event.preventDefault();
		event.stopPropagation();
	}
	
	
}
