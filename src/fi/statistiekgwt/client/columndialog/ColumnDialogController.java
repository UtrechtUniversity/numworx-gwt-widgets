package fi.statistiekgwt.client.columndialog;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import fi.statistiekgwt.client.event.AddColumnEvent;
import fi.statistiekgwt.client.event.EditColumnEvent;
import fi.statistiekgwt.client.types.AllowedTypes;

/**
 * Controller for add column dialog
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class ColumnDialogController
{
	private ColumnDialogModel model;
	private ColumnDialogView view;
	// handlers as global field?
	private ColumnDialogClickHandler clickHandler;
	private ColumnDialogBlurHandler blurHandler;
	private ColumnDialogChangeHandler changeHandler;
	private ColumnDialogValueChangeHandler valueChangeHandler;

	private HandlerRegistration handlerRegistration;
	private ColumnDialogKeyDownHandler keyDownHandler;

	/**
	 * Constructor
	 * 
	 * @param model
	 *            MVC Model
	 * @param view
	 *            MVC View
	 */
	public ColumnDialogController(ColumnDialogModel model,
		ColumnDialogView view)
	{
		this.model = model;
		this.view = view;
		this.clickHandler = new ColumnDialogClickHandler();
		this.blurHandler = new ColumnDialogBlurHandler();
		this.changeHandler = new ColumnDialogChangeHandler();
		this.valueChangeHandler = new ColumnDialogValueChangeHandler();
		this.keyDownHandler = new ColumnDialogKeyDownHandler();
		this.view.addClickHandlers(this.clickHandler);
		this.view.addChangeHandlers(this.changeHandler);
		this.view.addValueChangeHandlers(this.valueChangeHandler);
		this.view.addKeyDownHandlers(this.keyDownHandler);
	}

	/**
	 * Return whether the column originally was of type enumeration.
	 * When the type was originally string, a list of stringOptions is generated 
	 * as possible enum options. Changes in the string options list are
	 * not immediately processed, but effecuated when OK button is clicked. 
	 * When the original type was enum, the current enum options are shown. 
	 * Changes in the enum list are immediately processed.
	 * @return
	 */
	private boolean wasEnum()
	{
		return this.view.getOriginalColumnType().equals(AllowedTypes.ENUM);
	}

	/**
	 * Get handler registration. Used to manage AddColumnEventHandlers.
	 * 
	 * @return the handlerRegistration
	 */
	public HandlerRegistration getHandlerRegistration()
	{
		return handlerRegistration;
	}

	/**
	 * Set handler registration. Used to manage AddColumnEventHandlers.
	 * 
	 * @param handlerRegistration the handlerRegistration to set
	 */
	public void setHandlerRegistration(HandlerRegistration handlerRegistration)
	{
		this.handlerRegistration = handlerRegistration;
	}

	class ColumnDialogChangeHandler implements ChangeHandler
	{
		@Override
		public void onChange(ChangeEvent e)
		{
			if (e.getSource() == ColumnDialogController.this.view.getTypeBox())
			{
				ColumnDialogController.this.model.setType(ColumnDialogController.this.view.getSelectedType());
				ColumnDialogController.this.view.update();
				
				ColumnDialogController.this.model.setHasChangedType(true);
			}
		}
	}

	class ColumnDialogValueChangeHandler implements ValueChangeHandler<String>
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> e)
		{
			if (e.getSource() == ColumnDialogController.this.view.getNameField())
			{
				ColumnDialogController.this.model.setName(ColumnDialogController.this.view.getCurrentName());

				ColumnDialogController.this.model.setHasChangedName(true);
			}
			else if (e.getSource() == ColumnDialogController.this.view.getAddEnumElementField())
			{
//				if (ColumnDialogController.this.wasEnum())
//				{
					String newElement = ColumnDialogController.this.view.getEnumOption();
					ColumnDialogController.this.model.addEnumOption(newElement);
//				}
//				else
//				{
//					ColumnDialogController.this.view.addStringOption(
//						ColumnDialogController.this.view.getEnumOption());
//					//ColumnDialogController.this.view.update();
//				}

				// clear the text in the input field
				ColumnDialogController.this.view.clearAddEnumElementField();
				ColumnDialogController.this.view.update();
				
				ColumnDialogController.this.model.setHasChangedEnumOptions(true);
			}
			else if (e.getSource() == ColumnDialogController.this.view.getUitlegArea())
			{
				ColumnDialogController.this.model.setUitleg(ColumnDialogController.this.view.getUitlegArea().getText());

				ColumnDialogController.this.model.setHasChangedUitleg(true);
			}
		}
	}


	class ColumnDialogClickHandler implements ClickHandler//TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		@Override
		public void onClick(ClickEvent e)
		{
			if (e.getSource() == ColumnDialogController.this.view.getRemoveSelectedElement())
			{
				if (ColumnDialogController.this.wasEnum())
				{
					ColumnDialogController.this.model.removeEnumOption(
						ColumnDialogController.this.view.getSelectedOptionInListIndex());

					ColumnDialogController.this.view.update();
					ColumnDialogController.this.model.setHasChangedEnumOptions(true);
				}
				else
				{
					ColumnDialogController.this.view.removeStringOption(
						ColumnDialogController.this.view.getSelectedOptionInListIndex());
					ColumnDialogController.this.view.update();
				}
					
			}
			else if (e.getSource() == ColumnDialogController.this.view.getRemoveAllElements())
			{
				if (ColumnDialogController.this.wasEnum())
				{
					ColumnDialogController.this.model.removeAllEnumOption();
					
					ColumnDialogController.this.view.update();
					ColumnDialogController.this.model.setHasChangedEnumOptions(true);
				}
				else
				{
					ColumnDialogController.this.view.removeAllStringOptions();
					ColumnDialogController.this.view.update();
				}
			}
			else if (e.getSource() == ColumnDialogController.this.view.getSortElements())
			{
				if (ColumnDialogController.this.wasEnum())
				{
					ColumnDialogController.this.model.sortEnumOptions();
					
					ColumnDialogController.this.view.update();
					ColumnDialogController.this.model.setHasChangedEnumOptions(true);
				}
				else
				{
					ColumnDialogController.this.view.sortStringOptions();
					ColumnDialogController.this.view.update();
				}
			}
			else if (e.getSource() == ColumnDialogController.this.view.getMoveElementUp())
			{
				int index = ColumnDialogController.this.view.getSelectedOptionInListIndex();
				
				if (ColumnDialogController.this.wasEnum())
				{
					ColumnDialogController.this.model.swapEnumOptions(index,
						index - 1);
					
					ColumnDialogController.this.view.update();
					ColumnDialogController.this.view.setSelectedOptionInListIndex(index - 1);
					ColumnDialogController.this.model.setHasChangedEnumOptions(true);
				}
				else
				{
					ColumnDialogController.this.view.swapStringOptions(index,
						index - 1);
					ColumnDialogController.this.view.update();
					ColumnDialogController.this.view.setSelectedOptionInListIndex(index - 1);
				}
			}
			else if (e.getSource() == ColumnDialogController.this.view.getMoveElementDown())
			{
				int index = ColumnDialogController.this.view.getSelectedOptionInListIndex();
				
				if (ColumnDialogController.this.wasEnum())
				{
					ColumnDialogController.this.model.swapEnumOptions(index,
						index + 1);
					
					ColumnDialogController.this.view.update();
					ColumnDialogController.this.view.setSelectedOptionInListIndex(index + 1);
					ColumnDialogController.this.model.setHasChangedEnumOptions(true);
				}
				else
				{
					ColumnDialogController.this.view.swapStringOptions(index,
						index + 1);
					ColumnDialogController.this.view.update();
					ColumnDialogController.this.view.setSelectedOptionInListIndex(index + 1);
				}
			}
			else if (e.getSource() == ColumnDialogController.this.view.getOkButton())
			{
//				ColumnDialogController.this.model.setDonePressed(true);
//				ColumnDialogController.this.view.setVisible(false);
				ColumnDialogController.this.view.hide();
				
				if (ColumnDialogController.this.model.getOldName().equals(""))
				{
					// send an add column event
					AddColumnEvent event = new AddColumnEvent(
						ColumnDialogController.this.view.getCurrentName(),
						ColumnDialogController.this.view.getSelectedType(),
						ColumnDialogController.this.model.getEnumOptions(),
						ColumnDialogController.this.view.getUitleg());
					ColumnDialogController.this.model.fireEvent(event);
				}
				else
				{
					// als type gewijzigd in enum, update enum options
					if (!ColumnDialogController.this.wasEnum() 
						&& ColumnDialogController.this.model.getType().equals(AllowedTypes.ENUM))
					{
						ColumnDialogController.this.view.updateEnumOptions();
					}

					// send an edit column event
					EditColumnEvent event = new EditColumnEvent(
						ColumnDialogController.this.model.getColumnIndex(),
						ColumnDialogController.this.view.getCurrentName(), ColumnDialogController.this.model.hasChangedName(),
						ColumnDialogController.this.view.getSelectedType(), ColumnDialogController.this.model.hasChangedType(),
						ColumnDialogController.this.model.getEnumOptions(), ColumnDialogController.this.model.hasChangedEnumOptions(),
						ColumnDialogController.this.view.getUitleg(), ColumnDialogController.this.model.hasChangedUitleg());
					ColumnDialogController.this.model.fireEvent(event);
				}
				
				// remove handler statTableModel from ColumnDialogModel
				HandlerRegistration registration = ColumnDialogController.this.getHandlerRegistration(); 
				if (registration != null) // null in case of column info mode
					registration.removeHandler();
			}
			else if (e.getSource() == ColumnDialogController.this.view.getCancelButton())
			{
				ColumnDialogController.this.view.setVisible(false);
				ColumnDialogController.this.view.hide();

				// reset stringOptions voor het geval er een stringoption verwijderd is
				ColumnDialogController.this.view.resetOriginalStringOptions();
				
				// remove handler statTableModel from ColumnDialogModel
				ColumnDialogController.this.getHandlerRegistration().removeHandler();
			}
		}
		
	} // class AddColumnClickHandler
	
	class ColumnDialogBlurHandler implements BlurHandler
	{
		@Override
		public void onBlur(BlurEvent e)
		{
			if (e.getSource() == ColumnDialogController.this.view.getNameField())
			{
				ColumnDialogController.this.model.setName(ColumnDialogController.this.view.getCurrentName());
				
				ColumnDialogController.this.model.setHasChangedName(true);
			}
			else if (e.getSource() == ColumnDialogController.this.view.getAddEnumElementField())
			{
				String newElement = ColumnDialogController.this.view.getEnumOption();
				ColumnDialogController.this.model.addEnumOption(newElement);

				ColumnDialogController.this.view.clearAddEnumElementField();
				ColumnDialogController.this.view.update();
				
				ColumnDialogController.this.model.setHasChangedEnumOptions(true);
			}
			else if (e.getSource() == ColumnDialogController.this.view.getUitlegArea())
			{
				ColumnDialogController.this.model.setUitleg(ColumnDialogController.this.view.getUitleg());
				
				ColumnDialogController.this.model.setHasChangedUitleg(true);
			}
		}
	} // class ColumnDialogBlurHandler
	
	class ColumnDialogKeyDownHandler implements KeyDownHandler
	{
		@Override
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				if (e.getSource() == ColumnDialogController.this.view.getNameField())
				{
					ColumnDialogController.this.model.setName(ColumnDialogController.this.view.getCurrentName());

					ColumnDialogController.this.model.setHasChangedName(true);
				}
				else if (e.getSource() == ColumnDialogController.this.view.getAddEnumElementField())
				{
					String newElement = ColumnDialogController.this.view.getEnumOption();
					ColumnDialogController.this.model.addEnumOption(newElement);

					// clear the text in the input field
					ColumnDialogController.this.view.clearAddEnumElementField();
					ColumnDialogController.this.view.update();
					
					ColumnDialogController.this.model.setHasChangedEnumOptions(true);
				}
			}
		}
	} // class ColumnDialogKeyDownHandler
}