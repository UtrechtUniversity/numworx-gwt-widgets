package fi.weblogogwt.client;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.parameters.TAParameter;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

/**
 * class representing the print(arg) command, where arg can be
 * 1) a piece of text enclosed in quotes or
 * 2) a valid variable name in which case its value will be printed
 * see class TekenBlad; <br>
 * the command has one parameter: a String representing arg
 * see class TextParameter   
 */

public class CommentCComponent extends ParameterCommandComponent
{

	static class CommentParameter extends TAParameter {
		private String parameter = WebLogoGWT.rb.commentTekst();

		@Override
		public void setParameter(String s) {
			parameter = s;
			
		}

		@Override
		public String getParameterText() {
			return parameter;
		}

		@Override
		public boolean isCorrect(VarSet varSet) {
			return true;
		}

		@Override
		public boolean isCorrect() {
			return true;
		}

		@Override
		public String getValueText() {
			return parameter;
		}
		
		
	}
	/**
	 * constructor
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param sv instance of JavaLogoSchuifVeld sv containing the drawing Canvas; necessary for superclass constructor
	 */
	public CommentCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new CommentParameter();
		commandName = "#"; 
		commandNameTranslated = commandName; 
		strBeforeEditor = " ";
		strAfterEditor = "";
	}

	/**
	 * check the text parameter of this command for correctness;
	 * execute this command, if tracing, change its color and display
	 * the command and parameter; see class TraceBeheerder
	 */
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) 
		{	trb.setCommandInfo(getFullText(), varSet);
		}
		return traceKleur;
	}

	@Override
	void setBeforeAndAfterStrings(int index) {
	}

	@Override
	public String getCode(String tab) {
		return tab + getCommandName() + strBeforeEditor + getFullParameterText() + "\n";
	}	

	protected String getFullText() {
		return getCommandName() + strBeforeEditor +  getFullParameterText() + strAfterEditor;
	}
	
}