package fi.algebrapijlengwt.client;

import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;


public class TekstPopup extends PopupPanel
{
	AlgebraSchuifComponent owner;
	/**
	 * De editor die gebruikt wordt in de tekstpopup voor invoer
	 * van numerieke waarden of formules of voor tekst.
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
	TouchPanel touchPanel = null;
	
	//int tekstX, tekstY;
	int maxVisibleCharacters = 10;
	int maxCharacters = 30;
	boolean isForLabel = false;
	private boolean popupOpened = false;
	
	public TekstPopup(AlgebraSchuifComponent o, boolean isForLabel)
	{
		super(false);
		
		this.isForLabel = isForLabel;
		
		owner = o;
		//tekstX = eventX;
		//tekstY = eventY;
		
		touchPanel = new TouchPanel();
		
		editor = new FormuleEditor(){

			@Override
			public void enter()
			{
				super.enter();

				hidePopup();
			}

			@Override
			public void setCurrentElementRepaint()
			{
				// hier kom ik niet helaas; TODO uitzoeken waarom niet...
				super.setCurrentElementRepaint();
				// de cursor is weggehaald, focus is weg, dus popup hiden
				if (popupOpened)
				{
					if (TekstPopup.this.isForLabel)
					{
						((UitvoerSchuifComponent) owner).zetLabelTekst();
					}
					else
					{
						owner.zetInvulWaarde();
					}

					setPopupOpened(false);
					hide();
				}
			}

			@Override
			public void setCurrentElementRepaint(FormuleElement e)
			{
				// hier kom ik niet helaas; TODO uitzoeken waarom niet...
				super.setCurrentElementRepaint(e);
			}
			
		};
		
		kb = editor.getKeyboard();
		
		if (isForLabel)
		{
			// TODO set keyboard in abc-mode
			//kb.goTo("Alpha");//nl.uu.fi.dwo.mobile.client.ui.dwokb.FormuleKeyboard
			
			// werkt dit? nee, geeft geen abc-modus van dwo-toetsenbord
			editor.setFormuleToolBijFocus(false);
		}
		else
		{
			editor.setFormuleToolBijFocus(true);
		}
		
		touchPanel.add(editor.getAsPanel());
		setWidget(touchPanel);
		editor.requestFocus();
		//om te zorgen dat cursor ook getekend wordt:
		if (editor.getCurrentElement() == null)
		{	
			editor.setCurrentElementRepaint(editor.getMainRegel());
		}
		
		addCloseHandler(new PopupCloseHandler());
		
		touchPanel.addTouchHandler(new FormuleEditorTouchHandler(editor) {

			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				super.onTouchStart(event);
			}
			
		});
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
	}

	public void setFocus(boolean b)
	{
		editor.requestFocus();

		//om te zorgen dat cursor ook getekend wordt:
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
	
	class TextBoxKeyDownHandler implements KeyDownHandler
	{
		public void onKeyDown(KeyDownEvent e)
		{
			// deze zit alleen aan TextBox vast en niet aan formuleeditor; hier komen we dus niet nu we formuleeditor gebruiken
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
//System.out.println("enter");
				if ((owner instanceof UitvoerSchuifComponent) && isForLabel)
					((UitvoerSchuifComponent) owner).zetLabelTekst();
				else
					owner.zetInvulWaarde();

				hidePopup();
			}
			else if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
			{
				// keep the previous value
				setText(((UitvoerSchuifComponent) owner).geefExpressieString());
				hidePopupWithoutSavingInput();
			}
		}
	}
	
	class PopupCloseHandler implements CloseHandler<PopupPanel>
	{
		public void onClose(CloseEvent<PopupPanel> e)
		{
			if ((owner instanceof UitvoerSchuifComponent) && isForLabel)
				((UitvoerSchuifComponent) owner).zetLabelTekst();
			else
				owner.zetInvulWaarde();
			
			setPopupOpened(false);
		}
	}
	
	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event)
	{
		String type = event.getNativeEvent().getType();
		String target = event.getNativeEvent().getEventTarget().toString();
		
		if (type.equals(BrowserEvents.KEYDOWN))
		{
			if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE)
			{
				// keep the previous value
				if (owner instanceof UitvoerSchuifComponent)
				{
					if (isForLabel)
						setText(((UitvoerSchuifComponent) owner).geefLabelTekst());
					else
						setText(((UitvoerSchuifComponent) owner).geefExpressieString());
				}
				else if (owner instanceof BewerkingSchuifComponent)
					setText(((BewerkingSchuifComponent) owner).geefExpressieString());
				
				hidePopupWithoutSavingInput();
			}
		}
		else if (type.equals(BrowserEvents.CLICK) || type.equals(BrowserEvents.TOUCHEND))
		{
			if (!(target.contains("insert_formule") || target.contains("keyboard-container") || target.contains("gwt-Image key"))
				&& target.contains("canvas") && target.contains("class") && popupOpened)
			{
				// if not clicked on the keyboard or popup hide popup
				hidePopup();
			}
		}
		
		// t.b.v. invoegen formule met DWO-keyboard; de maat van editor-inhoud is pas bekend na mouseout
		resize();
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

	void hidePopup()
	{
		// input waarde doorgeven
		if (isForLabel)
		{
			((UitvoerSchuifComponent) owner).zetLabelTekst();
		}
		else
		{
			owner.zetInvulWaarde();
		}

		// hide keyboard
		kb.blur();
		// hide popup
		hide();
		setPopupOpened(false);
	}

	void hidePopupWithoutSavingInput()
	{
		// hide keyboard
		kb.blur();
		// hide popup
		hide();
		setPopupOpened(false);
	}

	/**
	 * Resize the popuppanel according to the size of the editor.
	 */
	public void resize()
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
}	