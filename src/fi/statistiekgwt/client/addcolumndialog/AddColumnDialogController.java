package fi.statistiekgwt.client.addcolumndialog;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

import fi.statistiekgwt.client.types.AllowedTypes;

/**
 * Controller for add column dialog
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class AddColumnDialogController 
//	implements ActionListener, DocumentListener, FocusListener
{
	private AddColumnDialogModel model;
	private AddColumnDialogView view;
	// handlers as global field?
	private AddColumnTouchHandler touchHandler;
	private AddColumnBlurHandler blurHandler;

	/**
	 * Constructor
	 * 
	 * @param model
	 *            MVC Model
	 * @param view
	 *            MVC View
	 */
	public AddColumnDialogController(AddColumnDialogModel model,
		AddColumnDialogView view)
	{
		this.model = model;
		this.view = view;
		this.touchHandler = new AddColumnTouchHandler();
		this.view.addTouchStartHandlers(touchHandler);
		this.view.addBlurHandlers(blurHandler);
	}

	/**
	 * ActionListener implementation
	 */
//	public void actionPerformed(ActionEvent arg0)
//	{
//		String actionCommand = arg0.getActionCommand();
//		if (actionCommand.equals("addEnumElementField"))
//		{
//			if (this.wasEnum())
//			{
//				String newElement = this.view.getEnumOption();
//				this.model.addEnumOption(newElement);
//			}
//			else
//			{
//				this.view.addStringOption(this.view.getEnumOption());
//				this.view.update(null, null);
//			}
//
//			// clear the text in the input field
//			this.view.clearAddEnumElementField();
//		}
//		else if (actionCommand.equals("removeSelectedElement"))
//		{
//			if (this.wasEnum())
//			{
//				// Changes are definitively made in the model
//				this.model.removeEnumOption(this.view.getSelectedOptionInListIndex());
//			}
//			else
//			{
//				// Changes are preliminarily made in the view.
//				// Changes are made definitive after click on OK button.
//				this.view.removeStringOption(this.view.getSelectedOptionInListIndex());
//				this.view.update(null, null);
//			}
//		}
//		else if (actionCommand.equals("removeAllElements"))
//		{
//			if (this.wasEnum())
//			{
//				this.model.removeAllEnumOption();
//			}
//			else
//			{
//				this.view.removeAllStringOptions();
//				this.view.update(null, null);
//			}
//		}
//		else if (actionCommand.equals("sortElements"))
//		{
//			if (this.wasEnum())
//			{
//				this.model.sortEnumOptions();
//			}
//			else
//			{
//				this.view.sortStringOptions();
//				this.view.update(null, null);
//			}
//		}
//		else if (actionCommand.equals("moveElementUp"))
//		{
//			int index = this.view.getSelectedOptionInListIndex();
//			
//			if (this.wasEnum())
//			{
//				this.model.swapEnumOptions(index,
//					index - 1);
//			}
//			else
//			{
//				this.view.swapStringOptions(index,
//					index - 1);
//				this.view.update(null, null);
//			}
//			
//			this.view.setSelectedOptionInListIndex(index - 1);
//		}
//		else if (actionCommand.equals("moveElementDown"))
//		{
//			int index = this.view.getSelectedOptionInListIndex();
//			
//			if (this.wasEnum())
//			{
//				this.model.swapEnumOptions(index,
//					index + 1);
//			}
//			else
//			{
//				this.view.swapStringOptions(index,
//					index + 1);
//				this.view.update(null, null);
//			}
//			
//			this.view.setSelectedOptionInListIndex(index + 1);
//		}
//		else if (actionCommand.equals("typeBox"))
//		{
//			this.model.setType(this.view.getSelectedType());
//		}
//		else if (actionCommand.equals("doneButton"))
//		{
//			// als type gewijzigd in enum, update enum options
//			if (!this.wasEnum() && this.model.getType().equals(AllowedTypes.ENUM))
//			{
//				this.view.updateEnumOptions();
//			}
//
//			this.model.setDonePressed(true);
//			this.view.setVisible(false);
//		}
//		else if (actionCommand.equals("nameField"))
//		{
//			this.model.setName(this.view.getCurrentName());
//		}
//	}
	
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
	 * FocusListener implementation
	 */
//	public void focusLost(FocusEvent arg0)
//	{
//		if (arg0.getSource() == this.view.getNameField())
//		{
//			this.model.setName(this.view.getCurrentName());
//		}
//		else if (arg0.getSource() == this.view.getUitlegArea())
//		{
//			this.model.setUitleg(this.view.getUitleg());
//		}
//	}




	class AddColumnTouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{

		@Override
		public void onTouchEnd(TouchEndEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTouchMove(TouchMoveEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTouchStart(TouchStartEvent event)
		{
			// TODO Auto-generated method stub
			
		}
		
	} // class AddColumnTouchHandler
	
	class AddColumnBlurHandler implements BlurHandler
	{
		@Override
		public void onBlur(BlurEvent event)
		{
			// TODO Auto-generated method stub
			
		}
	}
}