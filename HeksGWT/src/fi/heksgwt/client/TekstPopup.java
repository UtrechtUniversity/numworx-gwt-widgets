package fi.heksgwt.client;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

import fi.heksgwt.client.scobjects.ScContainer;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;

public class TekstPopup extends PopupPanel
{
	GetalComponent owner;

	/**
	 * De editor die gebruikt wordt in de tekstpopup voor invoer van numerieke
	 * waarden of formules of voor tekst.
	 */
	FormuleEditor editor;

	/**
	 * Het dwo-toetsenbord.
	 */
	FormuleKeyboardIF kb;

	/**
	 * Touch panel om een FormuleEditorTouchHandler aan te koppelen opdat je
	 * binnen de formule-editor de focus kunt zetten voor de verschillende
	 * invulvakken van een formule.
	 */
	Panel touchPanel = null;
	/**
	 * Geeft aan of invoer-popup geopend is.
	 */
	private boolean popupOpened = false;

	int maxVisibleCharacters = 10;
	int maxCharacters = 30;
	boolean isForLabel = false;

	ScContainer bigOwner;

	public TekstPopup(GetalComponent o, ScContainer bigO)
	{
		super(true);

		owner = o;
		bigOwner = bigO;

		editor = new FormuleEditor()
		{
			@Override
			public void insert(String text)
			{
				super.insert(text);

				resizePopup();
			}

			@Override
			public void enter()
			{
				super.enter();

				exitAction();
				hidePopup();
			}

		};

		kb = editor.getKeyboard();

		touchPanel = (editor.getAsPanel());
		setWidget(touchPanel);
		editor.requestFocus();
		//om te zorgen dat cursor ook getekend wordt:
		if (editor.getCurrentElement() == null)
		{	
			editor.setCurrentElementRepaint(editor.getMainRegel());
		}

		addCloseHandler(new PopupCloseHandler());
		
		new FormuleEditorTouchHandler(editor).initHandler();
	}

	public String getText()
	{
		String text = "";
		text = editor.toString();

		return text;
	}

	public void setText(String text)
	{
		editor.clearMain();
		editor.clearAll();
		editor.insert(text);

		resizePopup();
	}

	public void setFocus(boolean b)
	{
		editor.requestFocus();

		// om te zorgen dat cursor ook getekend wordt:
		if (editor.getCurrentElement() == null)
		{
			editor.setCurrentElementRepaint(editor.getMainRegel());
		}
	}

	/**
	 * Selecteer de tekst in de editor in tekstpopup.
	 * 
	 */
	public void setSelected()
	{
		editor.startSelection(0, 0);
		editor.endSelection(editor.getMainRegel().getWidth(), 0);
	}

	public void exitAction()
	{
		if (getText().equals(""))
			return;

		owner.zetInvulWaarde(getText());
		
		if (bigOwner instanceof Pagina21Panel)
		{
			((Pagina21Panel) bigOwner).opnieuwAction();
		}
		else if (bigOwner instanceof Pagina22Panel)
		{
			((Pagina22Panel) bigOwner).opnieuwAction();
		}
		else if (bigOwner instanceof Pagina23Panel)
		{
			((Pagina23Panel) bigOwner).instellingAction(owner);
		}
	}

	void hidePopup()
	{
		if (isShowing())
		{
			// hide keyboard
			kb.blur();
			// hide popup
			hide();
			setPopupOpened(false);
		}
	}

	/**
	 * Set popupOpened. Indicates whether the tekstpopup is opened.
	 * 
	 * @param b
	 */
	void setPopupOpened(boolean b)
	{
		popupOpened = b;
	}
	
	/**
	 * True als de popup geopend is, anders false.
	 * 
	 * @return
	 */
	boolean isPopupOpened()
	{
		return popupOpened;
	}

	/**
	 * Resize the popuppanel according to the size of the editor.
	 */
	public void resizePopup()
	{
		if (editor.getWidth() < 35)
			setWidth("35px");
		else
			setWidth(editor.getWidth() + "px");
		
		if (editor.getHeight() < 20)
			setHeight("20px");
		else
			setHeight(editor.getHeight() + "px");	
	}

	class PopupCloseHandler implements CloseHandler<PopupPanel>
	{
		public void onClose(CloseEvent<PopupPanel> e)
		{
			exitAction();
		}
	}
}