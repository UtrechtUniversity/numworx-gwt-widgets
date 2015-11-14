package fi.graphtoolgwt.client;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

public class DomeinVakGWT extends LayoutPanel {
	
	private FormuleEditor editor;
	private int ashoogte;
	private int minBreedte;
	private boolean tabletAan;
	
	private static FormuleFont defaultfont = FormuleFont.createFromFontSize(18);
	
	GraphToolGWTClientBundle graphToolGWTClientBundle;
	GraphToolCssResource graphToolCss;
	
	Canvas domeinVakCanvas;
	Context2d g;
	
	
	
	public DomeinVakGWT()
	{
		domeinVakCanvas = Canvas.createIfSupported();
		g = domeinVakCanvas.getContext2d();
		
		graphToolGWTClientBundle = GWT.create(GraphToolGWTClientBundle.class);
		graphToolCss = graphToolGWTClientBundle.getGraphToolGWTCSS();
		graphToolCss.ensureInjected();
		
		//setLayout(null);
		//addMouseListener(this);
		//if(!WiskOpdr.formTimes) formuleVakFont = WiskOpdr.tekstFont;
		
		//formuleComponent = new FormuleVak();
		//formuleComponent.setFont(formuleVakFont);
		//((FormuleVak)formuleComponent).setBorder(false);
		//((FormuleVak)formuleComponent).addActionListener(this);
		//formuleComponent.setLocation(0,0);
		//add(formuleComponent);
		
		//setOpaque(false);
		
		editor = new FormuleEditor();
		editor.getAsPanel().getElement().getStyle().setMarginTop(5, Unit.PX);
		editor.setFont(defaultfont);
		TouchPanel tp = (TouchPanel) editor.getAsPanel();
		tp.getElement().getStyle().setProperty("display", "inline-block");
		editor.setCurrent(0, 0);
		//kb = interactiePanel.kb; // THE ONE AND ONLY TODO betere interface naar interactiePanel.kb
		//editor.installKeyboard(kb);
		//editor.requestFocus(); // Wim: pas requestfocus als zichtbaar
		this.add(tp);
		addFormulePanelListeners(tp, editor);
	}
	
	public String geefTekst()
	{
		return editor.toString();
	}
	
	public void maakEditorLeeg()
	{
		editor.clearAll();
	}
	
	private void addFormulePanelListeners(final TouchPanel tp, final FormuleEditor editor)
	{
		tp.addTouchHandler(new FormuleEditorTouchHandler(editor));
	}
	
	/*
	 public int getAsHoogte()
	    {
		 return ((FormuleElement)formuleComponent).ashoogte + (getFontMetrics(formuleVakFont)).getAscent()/2;
	    }
		
		public FormuleVak geefFormuleVak()
		{	return (FormuleVak)formuleComponent;
		}
		*/
		
		public void zetTabletAan(boolean b)
		{	tabletAan = b;
		}
		
		/*
		public void paintComponent(Context2d g)
		{	g.setColor(Color.white);
			g.fillRect(1,2,getSize().width-2, getSize().height-4);
			
			g.setColor(Color.gray);
			g.drawRect(1,2,getSize().width-2, getSize().height-4);
			
		}
		*/
		
		public void zetMinBreedte(int b)
		{	minBreedte = b;	
		}
		
		/*
		public void zetMaat()
		{	setSize(Math.max(minBreedte, formuleComponent.getSize().width+20), formuleComponent.getSize().height+8);
			formuleComponent.setLocation(4,4);
			ashoogte = ((FormuleElement)formuleComponent).ashoogte+3;
			
		}
		*/
		
		
		/*
		public int geefAsHoogte()
		{
			return ashoogte + (getFontMetrics(formuleVakFont)).getAscent()/2;
		}
		*/
		
		/*
		public void actionPerformed(ActionEvent e) 
		{	if(e.getSource()==formuleComponent && e.getActionCommand().equals("focus"))
			{	zetTabletUser();
			}
			if(e.getSource()==formuleComponent && e.getActionCommand().equals("zetMaat") )
			{	zetMaat();
				this.getParent().doLayout();
			}
		}

		public void activateTablet()
		{	
			Container parent = getParent();
			int x = parent.getLocation().x;
			int y = parent.getLocation().y;
			int h = parent.getSize().height;
			for(int i=0 ; parent!=null && i<40 ; i++)
			{	if(parent instanceof TabletOwner) 
				{	((TabletOwner)parent).addTablet(this, x+20, y+h+20);
					Tablet tablet = ((TabletOwner)parent).getTablet();
					if(tablet==null) break;
					int tx = Math.min(parent.getSize().width-tablet.getSize().width, x+20);
					int ty = y+h+20+tablet.getSize().height>parent.getSize().height ? y-tablet.getSize().height-10 : y+h+20;
					tablet.setLocation(tx, ty);
					break;
				}
				else 
				{	parent = parent.getParent();
					if(parent==null)return;
					x += parent.getLocation().x;
					y += parent.getLocation().y;
				}
			}
			
		}
		
		*/
		
		/*
		public void zetTabletUser()
		{	Container parent = getParent();
			for(int i=0 ; parent!=null && i<40 ; i++)
			{	if(parent instanceof TabletOwner) 
				{	((TabletOwner)parent).zetTabletUser(this);
					break;
				}
				else 
				{	parent = parent.getParent();
				}
			}
		}
		
		public void mousePressed(MouseEvent e)
		{	formuleComponent.requestFocus();
			((FormuleVak)formuleComponent).zetOpEind();
				activateTablet();
		}
		
		public void mouseClicked(MouseEvent e){;}
		public void mouseReleased(MouseEvent e){;}
		public void mouseEntered(MouseEvent e){;}
		public void mouseExited(MouseEvent e){;}
*/
		/*
		
		//ActionProducer
		private ActionListener actionListener = null;
		
		public void addActionListener(ActionListener l) 
	 	{	actionListener = AWTEventMulticaster.add(actionListener,l);
	 	}
	 	
	 	public void removeActionListener(ActionListener l)
	 	{	actionListener = AWTEventMulticaster.remove(actionListener, l);
	 	}	
	 	
	 	public void produceAction(String command)
	 	{	if (actionListener != null)
	 		{	actionListener.actionPerformed( new ActionEvent(this, 0, command) );
	 		}
	 	}
	 	//end ActionProducer
	 	  */
	 	 
}


//}
