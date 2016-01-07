package fi.graphtoolgwt.client;

import java.util.logging.Logger;

import org.eclipse.jetty.util.log.Log;
import java.lang.Character;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

import fi.graphtoolgwt.client.TekenComponentGWT.PushClickHandler;


public class TabelVakGWT extends LayoutPanel{



	// attributen
	TabelComponentGWT owner;
	//GraphToolGWTClientBundle graphToolGWTClientBundle;
	//GraphToolCssResource graphToolCss;
	
	//Canvas tabelVakCanvas;
	//Context2d g;
	
	int vakIndex;

	//Label tabelVakLabel;
	TextBox tabelVakTextField;
	//TekstPopup tabelVakTekstPopup;
	
	String text = ""; 
	
	boolean editable; 
	
	//FontMetrics fm;
	
	//boolean randomAllowed = true;
	
	//public TabelVakGWT(TabelComponentGWT o, int vIndex, 
		//		    int x, int y, int w, int h, boolean edit)
	public TabelVakGWT(TabelComponentGWT o, int vIndex, int w, int h, boolean edit)
	{	
		owner = o;
		vakIndex = vIndex;
		editable = edit;
		
		tabelVakTextField = new TextBox();
		tabelVakTextField.setReadOnly(!editable);
		add(tabelVakTextField);
		this.setWidgetLeftWidth(tabelVakTextField, 2, Style.Unit.PX, w - 2, Style.Unit.PX);
		this.setWidgetTopHeight(tabelVakTextField, 1, Style.Unit.PX, h - 1, Style.Unit.PX);
		
		// Listen for keyboard events in the input box.
	    tabelVakTextField.addKeyDownHandler(new KeyDownHandler() {
	      public void onKeyDown(KeyDownEvent event) {
	    	  if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
	        	text = tabelVakTextField.getText();
	        	String text1 = trimTrailingZeros(text);
				boolean changed1 = (text.length() != text1.length());
				String text2 = addLeadingZero(text1);
				boolean changed2 = (text1.length() != text2.length());
				if (changed1 || changed2)
				{	text = text2;
					tabelVakTextField.setText(text);
					owner.adaptToText(vakIndex);
				}

				// als valide tweetal aan punten toevoegen
				// of verwijderen			
				owner.processTabelPunt(vakIndex);
	        }
	      }
	    });
	    tabelVakTextField.addKeyUpHandler(new KeyUpHandler() {
	    	public void onKeyUp(KeyUpEvent event){
	    		String txt = tabelVakTextField.getText();
	    		boolean corrected = false;
				// kijk of txt illegale characters bevat
				// dit zou er maximaal 1 moeten zijn
				int index = -1;
				for (int cCnt = 0; cCnt < txt.length(); cCnt++)
				{	char c = txt.charAt(cCnt);
					if (!isLegal(c))
						index = cCnt;
				}
				// verwijder illegaal karakter
				if (index >= 0)
				{	txt = removeCharAt(txt, index);
					corrected = true;
				}
				// dubbele decimale punt
				// voldoende er twee te zoeken
				int pIndex1 = txt.indexOf('.');
				int pIndex2 = txt.lastIndexOf('.');
				if ((pIndex1 >= 0) && (pIndex2 >= 0) && (pIndex1 != pIndex2))
				{	// verwijderen
					txt = removeCharAt(txt, pIndex2);
					corrected = true;
				}
				// proberen een legaal karakter voor het
				// minteken (dit staat dan op plek 1) in te vullen
				if (txt.indexOf('-') == 1)
				{	txt = removeCharAt(txt, 0);
					corrected = true;
				}
				// minteken
				// alleen vooraan if any
				int minIndex = txt.lastIndexOf('-');
				if (minIndex > 0)
				{	txt = removeCharAt(txt, minIndex);
					corrected = true;
				}
				// leading zeros, leiden niet tot een NumberFormatException
				// geval met minteken
				if ((txt.indexOf('-') == 0) && (txt.length() >= 3) &&
					(txt.charAt(1) == '0') && Character.isDigit(txt.charAt(2)))
				{	txt = removeCharAt(txt, 1);
					corrected = true;
				}
				// geen minteken
				if ((txt.indexOf('-') < 0) && (txt.length() >= 2) &&
					(txt.charAt(0) == '0') && Character.isDigit(txt.charAt(1)))
				{	txt = removeCharAt(txt, 0);
					corrected = true;
				}
				
				//if (corrected)
					tabelVakTextField.setText(txt);
				
				// pas de textbreedte aan
				owner.adaptToText(vakIndex);
	    	}
	    });
	    tabelVakTextField.addBlurHandler(new BlurHandler(){

			@Override
			public void onBlur(BlurEvent event) {
				text = tabelVakTextField.getText();

				String text1 = trimTrailingZeros(text);
				boolean changed1 = (text.length() != text1.length());
				String text2 = addLeadingZero(text1);
				boolean changed2 = (text1.length() != text2.length());
				if (changed1 || changed2)
				{	text = text2;
					tabelVakTextField.setText(text);
					owner.adaptToText(vakIndex);
				}

				tabelVakTextField.setText(text); //nodig?
				// als valide tweetal aan punten toevoegen
				// of verwijderen			
				owner.processTabelPunt(vakIndex);
				
			}

			
	    	
			
	    });
	    
				
	}
	
	public boolean isLegal(char c) {	
		return Character.isDigit(c) || (c == '-') || (c == '.');
	}

	public void zetBreedte(int breedte)
	{	this.setWidgetLeftRight(tabelVakTextField, 1, Style.Unit.PX, 1, Style.Unit.PX);
	}
	
	public void verhoogIndex()
	{	vakIndex++;
	}
	
	//public void translate(int dx)
	//{	
	//	setWidgetLeftWidth(this, this.getAbsoluteLeft() + dx, Style.Unit.PX, this.getOffsetWidth(), Style.Unit.PX);
	//}
	
	public void zetEditable(boolean b)
	{	editable = b;
		tabelVakTextField.setReadOnly(!editable);
	}

	/*
	// aanpassen invulveld. Voor GWT: uitzoeken hoe dit handig kan zonder font metrics!!
	public int geefBreedte(String s)
	{	// bepaal de gewenste breedte
		int width = fm.stringWidth(" " + s + " ");
		int vakBreedte = owner.getVakBreedte();
		if (width <= vakBreedte)
			return vakBreedte;
		else if ((width > vakBreedte) && (width <= 2 * vakBreedte))
			return width;
		else
			return 2 * vakBreedte;		
	}
	*/

	// extern text zetten	
	public int zetText(String s)
	{	text = s;
		//tabelVakLabel.setText(text);
		tabelVakTextField.setText(text);
		//Label testLabel = new Label("heeele lange tekst die nooit in 21 past");
		
		//int testwidth = testLabel.getElement().getClientWidth();
		//int testwidth2 = testLabel.getOffsetWidth();
		//System.out.println("testwidth: " + testwidth + " en testwidth 2: " + testwidth2);
		
		//int width = tabelVakLabel.getElement().getClientWidth();
		//System.out.println("width: " + width);
		
		
		Canvas tabelVakCanvas = Canvas.createIfSupported();
		Context2d g = tabelVakCanvas.getContext2d();
		TextMetrics tm = g.measureText(text);
		int width = (int) tm.getWidth() + 20;
		int vakBreedte = owner.getVakBreedte();
		
		// bepaal de gewenste breedte. Aanpassen voor GWT!!
		
		//int width = fm.stringWidth(" " + s + " ");
		//int vakBreedte = owner.getVakBreedte();
		if (width <= vakBreedte)
			return vakBreedte;
		else if ((width > vakBreedte) && (width <= 2 * vakBreedte))
			return width;
		else
			return 2 * vakBreedte;	
			
	}
	
	/*
	public void zetFont(Font f)
	{	tabelVakLabel.setFont(f);
		tabelVakTextField.setFont(f);
		fm = getFontMetrics(f);
	}
	*/

	public double geefWaarde()
	{	double result = Double.NaN;
		try
		{	result = Double.parseDouble(text);
		}
		catch (NumberFormatException nfe) {}
		return result;
	}

	public String geefText()
	{	//if (tabelVakTekstPopup.isVisible())
		//	return tabelVakTekstPopup.getText();
		if(tabelVakTextField.isVisible())
			return tabelVakTextField.getText();
		else
			return text;	
	}
	
	/*
	class TextFL implements FocusListener
	{	public void focusGained(FocusEvent e)
		{
		}
		public void focusLost(FocusEvent e)
		{	
			
			text = tabelVakTextField.getText();

			String text1 = trimTrailingZeros(text);
			boolean changed1 = (text.length() != text1.length());
			String text2 = addLeadingZero(text1);
			boolean changed2 = (text1.length() != text2.length());
			if (changed1 || changed2)
			{	text = text2;
				tabelVakTextField.setText(text);
				owner.adaptToText(vakIndex);
			}

			tabelVakTextField.setVisible(false);
			tabelVakLabel.setText(text);
			tabelVakLabel.setVisible(true);
			// als valide tweetal aan punten toevoegen
			// of verwijderen			
			owner.processTabelPunt(vakIndex);
			
		}
	}
	*/
	
	/*
	class TextML extends MouseAdapter
	{	public void mousePressed(MouseEvent e)
		{	if (editable)
			{	tabelVakLabel.setVisible(false);
				tabelVakTextField.setVisible(true);
				//tabelVakTextField.requestFocus();
			}
		}
	} 
	*/
	
	
	/*
	class TextAL implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{	
			tabelVakTextField.setVisible(false);
			tabelVakLabel.setText(text);
			tabelVakLabel.setVisible(true);
		}
	}
	*/
	
	/*
	public void hideTekstVeld(boolean empty)
	{
		if ((tabelVakTekstPopup == null) || !tabelVakTekstPopup.isVisible())
			return;
		
		text = tabelVakTekstPopup.getText();
		
		tabelVakTekstPopup.setVisible(false);
		tabelVakLabel.setVisible(true);
		tabelVakLabel.setText(text);
		
		//processtabelpunt?
	}
	*/

	public String trimTrailingZeros(String s)
	{	String txt = new String(s);
		if (txt.indexOf('.') < 0)
			return txt;
		char c = txt.charAt(txt.length() - 1);
		while (c == '0')
		{	txt = removeCharAt(txt, txt.length() - 1);
			c = txt.charAt(txt.length() - 1);
		}	
		c = txt.charAt(txt.length() - 1);
		if (c == '.')
			txt = removeCharAt(txt, txt.length() - 1);
		return txt;		
	}				
		
	public String addLeadingZero(String s)
	{	String txt = new String(s);
		// met minteken
		if ((txt.length() >= 2) && (txt.charAt(0) == '-') &&
			(txt.charAt(1) == '.'))
		{	txt = "-0" + txt.substring(1);
		}	
		// zonder minteken
		if ((txt.length() >= 1) && (txt.charAt(0) == '.'))
		{	txt = "0" + txt;
		}
		return txt;
	}

	public String removeCharAt(String s, int index)
	{	String txt = new String(s);
		// eerste
		if (index == 0)
			txt = txt.substring(1);
		// laatste	
		else if (index == (txt.length() - 1))
			txt = txt.substring(0, txt.length() - 1);
		// middenin	
		else
		{	String txt1 = txt.substring(0, index);
			String txt2 = txt.substring(index + 1);
			txt = txt1 + txt2;
		}
		return txt;
	}	
	
	/*
	class PushClickHandler implements ClickHandler
    {
    	//public void onMouseDown(MouseDownEvent e)
    	public void onClick(ClickEvent e)
    	{
    		//e.preventDefault();
			e.stopPropagation();
			if (editable)
			{	tabelVakLabel.setVisible(false);
				tabelVakTekstPopup.setVisible(true);
			}
			
    		
    	}
    	
    }
    */
	
	/*
	class InputKL extends KeyAdapter
	{	public void keyReleased(KeyEvent e)
		{	
			String txt = tabelVakTextField.getText();
			
			//om randomvariabele in te kunnen vullen
			if (randomAllowed && isLegal(txt))
			{	owner.adaptToText(vakIndex);
				return;
			}
			
			boolean corrected = false;
			// kijk of txt illegale characters bevat
			// dit zou er maximaal 1 moeten zijn
			int index = -1;
			for (int cCnt = 0; cCnt < txt.length(); cCnt++)
			{	char c = txt.charAt(cCnt);
				if (!isLegal(c))
					index = cCnt;
			}
			// verwijder illegaal karakter
			if (index >= 0)
			{	txt = removeCharAt(txt, index);
				corrected = true;
			}
			// dubbele decimale punt
			// voldoende er twee te zoeken
			int pIndex1 = txt.indexOf('.');
			int pIndex2 = txt.lastIndexOf('.');
			if ((pIndex1 >= 0) && (pIndex2 >= 0) && (pIndex1 != pIndex2))
			{	// verwijderen
				txt = removeCharAt(txt, pIndex2);
				corrected = true;
			}
			// proberen een legaal karakter voor het
			// minteken (dit staat dan op plek 1) in te vullen
			if (txt.indexOf('-') == 1)
			{	txt = removeCharAt(txt, 0);
				corrected = true;
			}
			// minteken
			// alleen vooraan if any
			int minIndex = txt.lastIndexOf('-');
			if (minIndex > 0)
			{	txt = removeCharAt(txt, minIndex);
				corrected = true;
			}
			// leading zeros, leiden niet tot een NumberFormatException
			// geval met minteken
			if ((txt.indexOf('-') == 0) && (txt.length() >= 3) &&
				(txt.charAt(1) == '0') && Character.isDigit(txt.charAt(2)))
			{	txt = removeCharAt(txt, 1);
				corrected = true;
			}
			// geen minteken
			if ((txt.indexOf('-') < 0) && (txt.length() >= 2) &&
				(txt.charAt(0) == '0') && Character.isDigit(txt.charAt(1)))
			{	txt = removeCharAt(txt, 0);
				corrected = true;
			}
			
			if (corrected)
				tabelVakTextField.setText(txt);
			
			// pas de textbreedte aan
			owner.adaptToText(vakIndex);
		}
	
		public boolean isLegal(String s)
		{	
			if (s != null && s.length() > 0)
				return (s.charAt(0) == '#');
			else 
				return false;
		}
	
		public boolean isLegal(char c)
		{	return Character.isDigit(c) || (c == '-') || (c == '.');
		}
	}
	*/
	
}
	
	

