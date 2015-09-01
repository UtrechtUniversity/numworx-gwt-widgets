package fi.weblogogwt.client.logotekenap;

//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;

//import javax.swing.JCheckBox;
//import javax.swing.JPanel;
//import javax.swing.JButton;
//import javax.swing.JTextField;

import fi.weblogogwt.client.JavaLogoSchuifVeld;
import fi.weblogogwt.client.WebLogoGWT;
import fi.weblogogwt.client.VarSet;
//GWT
//import fi.weblogogwt.client.VardisplayPanel;

public class TraceBeheerder //extends JPanel implements ActionListener, ItemListener
{
//GWT	
	//private JButton stapKnop,terugKnop,beginKnop,skipKnop,traceKnop;
//GWT	
	//private JCheckBox showVariables;
//GWT	
	//private JTextField methodeVeld;
	
	private int maxAantalStappen,aantalStappen;
	private Uitvoerblad tb;
	private JavaLogoSchuifVeld jlsveld;
	private boolean traceAan;
	
	private boolean isVartracing = false;
//GWT	
	//private VardisplayPanel vartracer = null;
	
	private int currentlevel;
	private boolean isSkipping;
	private int skipLevel;
	
	public TraceBeheerder(Uitvoerblad tb, JavaLogoSchuifVeld v)
	{	
		//setLayout(null);
		//setOpaque(false);
		makeGUI();
		aantalStappen = 0;
		maxAantalStappen = 0;
		isSkipping = false;
		skipLevel = 0;
		this.tb = tb;
		jlsveld = v;
		traceAan = false;
		setComponentVisibilty(false);
	}
	
//GWT naar WebLogoGWT	
	private void makeGUI()
	{
/*		
		beginKnop = new JButton(JavaLogoWeb.rb.getString("beginKnopLabel"));
		beginKnop.setBounds(0,5,50,23);
		beginKnop.setFont(JavaLogoWeb.boldfont);
		beginKnop.setMargin(new Insets(0,0,0,0));
		beginKnop.addActionListener(this);
		add(beginKnop);
		stapKnop = new JButton(JavaLogoWeb.rb.getString("stapKnopLabel"));
		stapKnop.setBounds(60,5,50,23);
		stapKnop.setFont(JavaLogoWeb.boldfont);
		stapKnop.setMargin(new Insets(0,0,0,0));
		stapKnop.addActionListener(this);
		add(stapKnop);
		terugKnop = new JButton(JavaLogoWeb.rb.getString("terugKnopLabel"));
		terugKnop.setBounds(120,5,50,23);
		terugKnop.setFont(JavaLogoWeb.boldfont);
		terugKnop.setMargin(new Insets(0,0,0,0));
		terugKnop.addActionListener(this);
		add(terugKnop);
		skipKnop = new JButton(JavaLogoWeb.rb.getString("skipKnopLabel"));
		skipKnop.setBounds(180,5,50,23);
		skipKnop.setFont(JavaLogoWeb.boldfont);
		skipKnop.setMargin(new Insets(0,0,0,0));
		skipKnop.addActionListener(this);
		add(skipKnop);
		showVariables = new JCheckBox(JavaLogoWeb.rb.getString("showVarLabel"));
		showVariables.setOpaque(false);
		showVariables.addItemListener(this);
		showVariables.setEnabled(true);
		showVariables.setSelected(false);
		showVariables.setBounds(240, 5, 160, 23);
		showVariables.setFont(JavaLogoWeb.boldfont);
		add(showVariables);
		
		traceKnop = new JButton(JavaLogoWeb.rb.getString("traceOnLabel"));
		traceKnop.setBounds(0,32,170,23);
		traceKnop.setFont(JavaLogoWeb.boldfont);
		traceKnop.setMargin(new Insets(0,0,0,0));
		traceKnop.addActionListener(this);
		add(traceKnop);
		methodeVeld = new JTextField("",15);
		methodeVeld.setBounds(180,32,160,23);
		methodeVeld.setFont(JavaLogoWeb.defaultfont);
		methodeVeld.setMargin(new Insets(0,0,0,0));
		add(methodeVeld);

		
		// handle vartracer panel here
		vartracer = new VardisplayPanel();
		vartracer.setBounds(JavaLogoSchuifVeld.ccx, JavaLogoSchuifVeld.ccy, 2*JavaLogoSchuifVeld.ccsw+10, 515);
*/		
	}
	
	private void setComponentVisibilty(boolean b)
	{	
//GWT naar WebLogoGWT
/*		
		methodeVeld.setVisible(b);
		beginKnop.setVisible(b);
		stapKnop.setVisible(b);
		terugKnop.setVisible(b);
		skipKnop.setVisible(b);
		showVariables.setVisible(b);
*/		
	}

	//-------------------------------------------------------------------------------------------
	// Execution and tracing of programs 
	//-------------------------------------------------------------------------------------------
	
	/**
	 * Will execute and paint a program without tracing
	 */
	public void executeProgram()
	{
		traceAan = false;
		jlsveld.execute(this, tb);
	}
	
	public boolean isTraceAan()
	{
		return traceAan;
	}

	/**
	 * Will execute and paint a program when tracing, up to the number of steps
	 * indicated by the variable maxAantalStappen.
	 */
	public void traceProgram()
	{
		// clear oude tracekleur
		traceAan = true;
		aantalStappen = 0;
		if ( maxAantalStappen > 0 )
		{
			jlsveld.execute(this, tb);
			// zet laatste command in textfield; methodeVeld.setText(...);
		}
		jlsveld.paint();				// show the pink trace color!
	}
	
	/**
	 * Callback method for execution of a program while tracing. CommandComponents will call
	 * this method when they have completed (or started: deeltaak, loop, if...)
	 * TraceBeheerder will signal the end of execution when maxAaantalStappen is reached.
	 * 
	 * @return	true, if execution of the program must stop at this point, false otherwise.
	 */
	public boolean commandExecuted(int commandLevel)
	{
		if ( !traceAan ) return false;
		aantalStappen++;
		if ( aantalStappen == maxAantalStappen )
		{
			if ( isSkipping )
			{											// check level of this command
				if ( commandLevel < skipLevel )
				{										// lower then skipLevel, return to normal tracing
					isSkipping = false;
					return true;
				} else
				{										// in skipped block				
					maxAantalStappen++;					// new max after skipping this command, increase max here
					return false;						// ... to make next command satify the first if
				}
			} else
			{											// not skipping, stop at this command
				return true;
			}
		} else
		{
			return false;
		}
	}

	/**
	 * Set text of 'methodeVeld' and (if var tracing is on) the varset in the vartracer.
	 * This method will be called from the execute-methods in the CC's, when trace is on
	 * and execution stops at that command.
	 * 
	 * @param varset	the current set of variables in tracing mode
	 */
	public void setCommandInfo(String actualCommand, VarSet varset)
	{
		currentlevel = varset.getLevel();
//GWT		
		//methodeVeld.setText(actualCommand);
		if ( isVartracing )
		{
//GWT			
			//vartracer.setContent(varset.toString());
		}
	}
	
	//-------------------------------------------------------------------------------------------
	//afhandeling van de knopacties
	//-------------------------------------------------------------------------------------------
//GWT naar WebLogoGWT
/*	
	@Override
	public void actionPerformed(ActionEvent e)
	{	if(e.getSource() == stapKnop)
		{	
			maxAantalStappen++;
			tb.paintDrawing(true);
		}
		if(e.getSource() == terugKnop)
		{	
			maxAantalStappen--;
			if(maxAantalStappen<0)maxAantalStappen=0;
			tb.paintDrawing(true);
		}
		if(e.getSource() == skipKnop)
		{	
			skipLevel = currentlevel;
			isSkipping = true;
			tb.paintDrawing(true);
		}
		if(e.getSource() == beginKnop)
		{	
			vartracer.setContent("");
			methodeVeld.setText("");
			isSkipping = false;					// previous trace may have stopped in skip
			maxAantalStappen = 0;
			tb.paintDrawing(true);
		}
		if(e.getSource() == traceKnop)
		{	
			if(!traceAan)
			{	
				traceAan = true;
				isSkipping = false;
				maxAantalStappen = 0;
				methodeVeld.setText("");
				tb.paintDrawing(true);
				traceKnop.setText(JavaLogoWeb.rb.getString("traceOffLabel"));
				setComponentVisibilty(true);
			}
			else
			{	
				traceAan = false;
				tb.paintDrawing(false);
				showVariables.setSelected(false);
				setVartracing(false);
				setComponentVisibilty(false);
				traceKnop.setText(JavaLogoWeb.rb.getString("traceOnLabel"));
			}
			repaint();
		}
	}
*/	
	
//GWT naar WebLogoGWT
/*	
	@Override
	public void itemStateChanged(ItemEvent e)
	{
		boolean b = ( e.getStateChange() == ItemEvent.SELECTED );
		setVartracing(b);
	}
*/	
	private void setVartracing(boolean b)
	{
		if ( b )
		{
			isVartracing = true;
//GWT naar WebLogoGWT			
			//jlsveld.add(vartracer, 0);
		} else
		{
			isVartracing = false;
//GWT naar WebLogoGWT 			
			//jlsveld.remove(vartracer);
//GWT			
			//vartracer.setContent("");
		}
		jlsveld.paint();
	}
}