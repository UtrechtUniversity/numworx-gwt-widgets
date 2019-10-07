package fi.weblogogwt.client;

import java.util.ArrayList;
import fi.weblogogwt.client.formuleobjects.StringUtils;

/**
 * class which parses a single very long String containing the full program code into program 
 * 1) subroutine headers 2) code lines of subroutine bodied 3) code lines of main program, where
 * code line can be simple code lines, the code lines making up an if-else command or the code lines
 * making up a loop command; subsequently, the class creates the corresponding CommandComponents; <br>     
 * Note 1: the input String can be in Dutch or English (backwards compatibility); <br>
 * Note 2: method ProgrammaExporter in JavaLogoSchuifveld produces code in Dutch or English according to the
 * choosen internationalization; <br>
 * Note 3: the format of code to be imported is quite specific: export some code to get an example of how
 * this should be correctly done.      
 */

public class ProgrammaImporter 
{
	/**
	 * owner
	 */
	private JavaLogoSchuifVeld veld;
	/**
	 * default subroutine names in Dutch
	 */
	private String[] deeltaaknamen = { "deeltaak1", "deeltaak2", "deeltaak3", "deeltaak4", "deeltaak5"};
	/**
	 * default subroutine names in Dutch or English according to the
	 * choosen internationalization
	 */
	private String[] deeltaaknamenTrans =	
		{ WebLogoGWT.rb.deeltaakTekst()+"1", WebLogoGWT.rb.deeltaakTekst()+"2", WebLogoGWT.rb.deeltaakTekst()+"3",
		  WebLogoGWT.rb.deeltaakTekst()+"4", WebLogoGWT.rb.deeltaakTekst()+"5" };		
	
	/**
	 * text for compound statements in Dutch
	 */
	private String strIf1 = "Keuze: Als";
	private String strIf2 = "Dan";
	private String strFor1 = "Herhaal";
	private String strFor2 = "keer";
	private String strWhile1 = "Zolang";
	private String strWhile2 = "herhaal";
	
	/**
	 * text for compound statements in Dutch or English according to the
	 * choosen internationalization
	 */
	private String strIf1Trans = WebLogoGWT.rb.keuzeTekst() + " " + WebLogoGWT.rb.alsTekst();
	private String strIf2Trans = WebLogoGWT.rb.danTekst();
	private String strFor1Trans = WebLogoGWT.rb.herhaal1Tekst();
	private String strFor2Trans = WebLogoGWT.rb.keerTekst();
	private String strWhile1Trans = WebLogoGWT.rb.zolangTekst();
	private String strWhile2Trans = WebLogoGWT.rb.herhaal2Tekst();
	
	/**
	 * constructor
	 * @param v instance of JavaLogoSchuifVeld
	 */
	public ProgrammaImporter(JavaLogoSchuifVeld v)
	{	veld = v;
	}
	
	/**
	 * Main method of the Importer. Imports code in three steps:
	 * (1) deeltaakheaders
	 * (2) deeltaakbody's
	 * (3) hoofdprogramma
	 * import headers first, deeltaak1 can call deeltaak2 (not yet imported)! step 2 and 3 are interchangeable after this
	 * @param s	the full program text from the import frame
	 */
	void importProgramma(String s)
	{
//System.out.println("+++  Starting import");
		String programmaTekst = s + "\n";
//System.out.println("+++  Deeltaaknamen");
		// split the full program String according to separator String "Deeltaak:"
		// see class StringUtils
		String[] codeParts1 = StringUtils.split(programmaTekst, "Deeltaak:");
		// split the full program String according to separator String "Deeltaak:" or "Subroutine:"
		// see class StringUtils
		String[] codeParts2 = StringUtils.split(programmaTekst, WebLogoGWT.rb.deeltaak1Tekst());

		// assume Dutch
		String[] codeParts = codeParts1;
		// String was not split, so language must be English
		if (codeParts.length == 1)
			codeParts = codeParts2;
		
		// note that codeParts[0] is the main program
//System.out.println("+++  Deeltaakheaders");		

		// this replaces existing subroutines
		for(int i=1 ; i<=JavaLogoSchuifVeld.aantalDeeltaken ; i++)
		{
			if ( i<codeParts.length )
			{
				importDeeltaakHeader(i-1, codeParts[i]);
			} 
			// else: default names are "deeltaakn" n=0,1,2,3,4. No need to change		
		}		
//System.out.println("+++  Deeltaakbodies");
		for(int i=1 ; i<codeParts.length ; i++)
		{
			if ( i<codeParts.length )
			{
				importDeeltaakBody(i-1, codeParts[i]);				
			} 
		}		
//System.out.println("+++  Hoofdprogramma");
		importHoofdprogramma(codeParts[0]);
	}

	/**
	 * set the header of the subroutine with index i to the first line of the parsed String code;
	 * @param i index of subroutine
	 * @param code String containing subroutine code, first line contains name and parameterlist in parenthesis
	 */
	private void importDeeltaakHeader(int i, String code) 
	{
		
//System.out.println("DTH " + i + " code = " + code);

		DeeltaakBodyComponent ccont = veld.getDeeltaakBody(i);
		
		if (!ccont.isOpen)
			ccont.changeHeight();
		
		// get the first line, that is the remainder of the line after "Deeltaak:" or "Subroutine:"
		String s = code.substring(0,code.indexOf("\n")).trim();
		int bracketpos = s.indexOf("(");
		if ( bracketpos == -1 )
		{	// no brackets, import deeltaak without parameter (so you can import old exports)
			deeltaaknamen[i] = s;
			ccont.setDeeltaakHeader(s, "");
		} 
		else // subroutine with parameterlist
		{
			// name before "("
			String deeltaakNaam = s.substring(0, bracketpos).trim();
			// deeltaakNaam equals the default, replace by the default name in the choosen language 
			if (deeltaakNaam.equals(deeltaaknamen[i]) || deeltaakNaam.equals(deeltaaknamenTrans[i]))
				deeltaaknamen[i] = deeltaaknamenTrans[i];
			else // user defined subroutine name 
				deeltaaknamen[i] = s.substring(0, bracketpos).trim();
			String param = getParamText(s);
			ccont.setDeeltaakHeader(deeltaaknamen[i], param.trim());
		}
	}

	/**
	 * set the body of the subroutine with index i to the lines (exact the first) of parsed String code;
	 * @param i index of subroutine
	 * @param code String containing the subroutine code, first line contains name and parameterlist in parenthesis 
	 */
	private void importDeeltaakBody(int i, String code) 
	{
// System.out.println("+++ import deeltaakbody: "+deeltaaknamen[i]);
		// split String code into separate lines
		ArrayList<String> codeLines = StringUtils.splitLines(code);
		// first line has deeltaaknaam and parameter list
		codeLines.remove(0);					
		DeeltaakBodyComponent ccont = veld.getDeeltaakBody(i);
		readBlock(codeLines, ccont);
	}
	
	/**
	 * set the body of the main program to the lines  of parsed String code;
	 * @param code code String containing the main program code
	 */
	private void importHoofdprogramma(String code) 
	{
		ArrayList<String> codeLines = StringUtils.splitLines(code);
		readBlock(codeLines, veld.getProgramma());
	}
	
	/**
	 * Check if a line of code starts and ends with the keywords for a control structure
	 * @param s			the code line
	 * @param start		starting keyword
	 * @param end		closing keyword
	 * @return			boolean
	 */
	private boolean checkHeader(String s, String start, String end)
	{
		return ( s.length()>start.length()+end.length()+1 && s.startsWith(start) && s.endsWith(end) );
	}
	
	/**
	 * Strip a line of code of the start and end keywords for a control structure. to obtain the control condition/loop count
	 * @param s			the code line
	 * @param start		starting keyword
	 * @param end		closing keyword
	 * @return			String, the condition/loop count
	 */
	private String stripKeywords(String s, String start, String end)
	{
		return s.substring(start.length(),s.length()-end.length()).trim();
	}
	
	/**
	 * Cuts a block of code from a given list of code lines. The block must start with a line containing "{"
	 * and will run to the first line containing "}" at the same level (so it will copy subblocks)
	 * The lines in the block will be removed form the ArrayList 'lines'.
	 * @param lines		ArrayList with the original code. WILL BE CHANGED!
	 * @return			list of lines in block, without the enclosing brackets.
	 */
	private ArrayList<String> getBlock(ArrayList<String> lines) {
		ArrayList<String> blockLines = new ArrayList<String>();
		String line = lines.remove(0);
		while (line.equals("")) // niet moeders mooiste, even hardhandig lege regels verwijderen
		{
			line = lines.remove(0);
		}
		if (!line.equals("{")) {
			System.out.println("PARSE ERROR: missing {");
			return blockLines;
		}
		int level = 1;
		while (level > 0) {
			if (lines.isEmpty()) {
				System.out.println("PARSE ERROR: missing }");
				return blockLines;
			}
			line = lines.remove(0);
			if (line.equals("{"))
				level++;
			if (line.equals("}"))
				level--;
			// copy line into block, but don't copy last } (that caused level to go down to
			// 0)
			if (level > 0) {
				blockLines.add(line);
			}
		}
		return blockLines;
	}
	
	/**
	 * Read a block of code, translate to CommandComponents and add them to the specified CommandContainer.
	 * This method will consume all code in the ArrayList 'lines', this will be empty after the operation.
	 * @param lines		ArrayList containing lines of code (Strings)
	 * @param ccont		the target CommandContainer for the code.
	 */
	private void readBlock(ArrayList<String> lines, CompositeCommandComponent ccont)
	{
		String line = "";
		CommandComponent ccomp = null;
		while ( !lines.isEmpty() )
		{
			// will stay null if error in command
			ccomp = null;
			// set line to first line of lines and remove first line of lines 
			line = lines.remove(0);
			if (line.startsWith("Herhaal") || line.startsWith(WebLogoGWT.rb.herhaal1Tekst()))
			{
//System.out.println("+++  Start For-loop");
				ccomp = readForLoopCommand(ccont, line, lines);
			}
			if (line.startsWith("Zolang") || line.startsWith(WebLogoGWT.rb.zolangTekst()))
			{
//System.out.println("+++  Start While-loop");
				ccomp = readWhileLoopCommand(ccont, line, lines);
			}
			else if (line.startsWith("Keuze:") || line.startsWith(WebLogoGWT.rb.keuzeTekst()))
			{
//System.out.println("+++  Start Keuze");
				ccomp = readKeuzeCommand(ccont, line, lines);
			}
			else
			{
				ccomp = readSimpleCommand(line);
				if ( ccomp != null)
				{	// ads at bottom position
					ccont.addCComponent(ccomp);		
				}
			}
		}
	}
	
	/**
	 * Generate a ForLoopCommandComponent from a header line s and a list of code lines, containing ALL of the remaining code.
	 * The number of repetitions in the loop will be read from <em>headerline</em>.
	 * The method will cut a block of lines (from '{' to the corresponding '}' from the list 'lines'.
	 * Therefore this list will be changed!
	 * @param ccont 		the CompositeCommandComponent 
	 * @param headerline	header line, with number of repetitions
	 * @param lines			remaining list of code lines. WILL BE CHANGED!
	 * @return				the ForLoopCommandComponent
	 */
	private CommandComponent readForLoopCommand(CompositeCommandComponent ccont, String headerline, ArrayList<String> lines)
	{
		ForLoopCommandComponent cc= new ForLoopCommandComponent(0, 0, 0, 0, veld);
		cc.clearStapel();
		// strip headerline of "Herhaal" and "keer" and add remainder as parameter
		// returns false and does not change headerline if language is English 
		if ( checkHeader(headerline, strFor1, strFor2) )
		{
			String nrrep = stripKeywords(headerline, strFor1, strFor2);
			//System.out.println("+++  aantal: "+nrrep);
			cc.setLoopCount(nrrep);
			// need to assign this loop to a Container before adding CC's to this one
			ccont.addCComponent(cc);				
		}
		// strip headerline of "Repeat" and "times" and add remainder as parameter
		else if ( checkHeader(headerline, strFor1Trans, strFor2Trans) )
		{
			String nrrep = stripKeywords(headerline, strFor1Trans, strFor2Trans);
			//System.out.println("+++  aantal: "+nrrep);
			cc.setLoopCount(nrrep);
			ccont.addCComponent(cc);
		}
		ArrayList<String> body = getBlock(lines);
		readBlock(body, cc);
//System.out.println("+++  Eind Herhaal");
		return cc;
	}

	/**
	 * Generate a WhileLoopCommandComponent from a header line s and a list of code lines, containing ALL of the remaining code.
	 * The condition of the while-loop will be read from <em>headerline</em>.
	 * The method will cut a block of lines (from '{' to the corresponding '}' from the list 'lines'.
	 * Therefore this list will be changed!
	 * @param ccont 		the CompositeCommandComponent
	 * @param headerline	header line, with condition
	 * @param lines			remaining list of code lines. WILL BE CHANGED!
	 * @return				the WhileLoopCommandComponent
	 */
	private CommandComponent readWhileLoopCommand(CompositeCommandComponent ccont, String headerline, ArrayList<String> lines)
	{
		WhileLoopCommandComponent cc= new WhileLoopCommandComponent(0, 0, 0, 0, veld);
		cc.clearStapel();
		// strip headerline of "Zolang" and "herhaal" and add remainder as parameter
		// returns false and does not change headerline if language is English
		if ( checkHeader(headerline, strWhile1, strWhile2) )
		{
			String nrrep = stripKeywords(headerline, strWhile1, strWhile2);
			//System.out.println("+++  voorwaarde: "+nrrep);
			cc.setLoopCount(nrrep);
			// need to assign this loop to a Container before adding CC's to this one
			ccont.addCComponent(cc);				
		}
		// strip headerline of "While" and "repeat" and add remainder as parameter
		else if ( checkHeader(headerline, strWhile1Trans, strWhile2Trans) ) 
		{
			String nrrep = stripKeywords(headerline, strWhile1Trans, strWhile2Trans);
			//System.out.println("+++  voorwaarde: "+nrrep);
			cc.setLoopCount(nrrep);
			// need to assign this loop to a Container before adding CC's to this one
			ccont.addCComponent(cc);				
		}
		ArrayList<String> body = getBlock(lines);
		readBlock(body, cc);
//System.out.println("+++  Eind While");
		return cc;
	}

	/**
	 * Generate a KeuzeCommandComponent from a header line s and a list of code lines, containing ALL of the remaining code.
	 * The condition of the choice will be read from <em>headerline</em>.
	 * The method will cut a block of lines (from '{' to the corresponding '}' from the list 'lines'.
	 * if this is followed by "Anders", a second block will be cut from the list 'lines', the else-part.
	 * Therefore this list will be changed!
	 * @param ccont 		the CompositeCommandComponent
	 * @param headerline	header line, with condition
	 * @param lines			remaining list of code lines. WILL BE CHANGED!
	 * @return				the KeuzeCommandComponent
	 */
	private CommandComponent readKeuzeCommand(CompositeCommandComponent ccont, String headerline, ArrayList<String> lines)
	{
		KeuzeCommandComponent cc= new KeuzeCommandComponent(0, 0, 0, 0, veld);
		
		cc.clearStapel();
		// strip headerline of "Keuze: Als" and "Dan" and add remainder as parameter
		// returns false and does not change headerline if language is English
		if ( checkHeader(headerline, strIf1, strIf2) )
		{
			String condition = stripKeywords(headerline, strIf1, strIf2);
			//System.out.println("+++  voorwaarde: "+condition);
			cc.setBoolExpression( condition );
			ccont.addCComponent(cc);				
		}
		// strip headerline of "Choice: If" and "Else" and add remainder as parameter
		else if ( checkHeader(headerline, strIf1Trans, strIf2Trans) )
		{
			String condition = stripKeywords(headerline, strIf1Trans, strIf2Trans);
			//System.out.println("+++  voorwaarde: "+condition);
			cc.setBoolExpression( condition );
			ccont.addCComponent(cc);				
		}
			
		ArrayList<String> body_true = getBlock(lines);
//System.out.println("+++  Start If");
		cc.setInIfBlock(true);
		readBlock(body_true, cc);				
//System.out.println("+++  Eind if");
		// there still are lines left
		if  ( !lines.isEmpty() )
		{
			String s2 = lines.get(0);
			if (s2.equals("Anders") || s2.equals(WebLogoGWT.rb.andersTekst()))
			{
				cc.setElseVisible(true);
//System.out.println("+++  Start Else");
				cc.setInIfBlock(false);
				// line containing "Anders" hasn't been removed yet
				lines.remove(0);				
				ArrayList<String> body_false = getBlock(lines);
				readBlock(body_false, cc);				
//System.out.println("+++  Eind Else");
			}
			
		}
		return cc;
	}

	/**
	 * Finds the string containing the parameters of a (simple) command.
	 * It returns the substring between the first '(' and the last ')'.
	 * If there is no such string, the method returns null.
	 * @param codeline	The current command
	 * @return			the String containing parameters, or null if there none
	 */
	private String getParamText(String codeline)
	{
		int indexbegin = codeline.indexOf("(")+1;
		int indexeind = codeline.lastIndexOf(")");
		if ( indexeind > indexbegin && indexbegin > 0 )
		{
			return codeline.substring(indexbegin, indexeind);
		} 
		else
		{
			return null;
		}
	}
	
	/**
	 * Read a simple command (single-line command). Includes variables and deeltaak-calls.
	 * @param codeline		line containing the command
	 * @return				CommandComponent for this line, or null if line is empty or not valid
	 */
	private CommandComponent readSimpleCommand(String codeline)
	{
		if ( codeline.equals("")) 
			return null;
		CommandComponent cc = null;
		// 1: commandComponents for simple commands
		if (codeline.startsWith("vooruit(") || codeline.startsWith(WebLogoGWT.rb.vooruitTekst() + "("))
		{	cc = new VooruitCComponent(-100,-100,25,25, veld);
		}
		else if (codeline.startsWith("rechts(") || codeline.startsWith(WebLogoGWT.rb.rechtsTekst() + "("))
		{	cc = new RechtsCComponent(-100,-100,25,25, veld);
		}
		else if (codeline.startsWith("links(") || codeline.startsWith(WebLogoGWT.rb.linksTekst() + "("))
		{	cc = new LinksCComponent(-100,-100,25,25, veld);
		}
		else if (codeline.startsWith("stap(") || codeline.startsWith(WebLogoGWT.rb.stapTekst() + "("))
		{	cc = new StapCComponent(-100,-100,25,25, veld);
		}
		else if (codeline.startsWith("penAan(") || codeline.startsWith(WebLogoGWT.rb.penAanTekst() + "("))
		{	cc = new PenAanCComponent(-100,-100,25,25, veld);
		}
		else if (codeline.startsWith("penUit(") || codeline.startsWith(WebLogoGWT.rb.penUitTekst() + "("))
		{	cc = new PenUitCComponent(-100,-100,25,25, veld);
		}
		else if (codeline.startsWith("vulAan(") || codeline.startsWith(WebLogoGWT.rb.vulAanTekst() + "("))
		{	cc = new VulAanCComponent(-100,-100,25,25, veld);
		}
		else if (codeline.startsWith("vulUit(") || codeline.startsWith(WebLogoGWT.rb.vulUitTekst() + "("))
		{	cc = new VulUitCComponent(-100,-100,25,25, veld);
		}
		else if (codeline.startsWith("vulBlad(") || codeline.startsWith(WebLogoGWT.rb.vulBladTekst() + "("))
		{	cc = new VulBladCComponent(-100,-100,25,25, veld);
		} 
		else if (codeline.startsWith("print(") || codeline.startsWith(WebLogoGWT.rb.printTekst() + "("))
		{	cc = new PrintCComponent(-100,-100,25,25, veld);
		}
		else if (codeline.startsWith("println(") || codeline.startsWith(WebLogoGWT.rb.printlnTekst() + "("))
		{	cc = new PrintlCComponent(-100,-100,25,25, veld);
		}
		// cc has been initialized if and only if the line contains one of the simple commands.
		// If so, read possible parameters
		if ( cc != null )
		{
//System.out.println("     +++  was command");
			cc.clearStapel();
			String parameter = getParamText(codeline);
			parameter = parameter.trim();
			if ( parameter != null && cc instanceof ParameterCommandComponent )
			{
				((ParameterCommandComponent)cc).setParameter(parameter);
			}
			return cc;
		}
		// 2: call of DeelTaak
		int dtnr = isDeeltaak(codeline);
		if ( dtnr > 0 )
		{
//System.out.println("     +++  was deeltaak: "+dtnr);
			DeeltaakBodyComponent ccont = veld.getDeeltaakBody(dtnr-1);
			cc = new DeeltaakCallCComponent(-100,-100,25,25, dtnr, veld);
			cc.clearStapel();
			String p = getParamText(codeline);
			((DeeltaakCallCComponent)cc).setBody(ccont);
			if ( p != null && ccont.getParameterCount() > 0 )
			{
				((DeeltaakCallCComponent)cc).setParameter(p);
			}
			return cc;
		}
		// 3: variabele-expressie
//System.out.println("     +++  was var-expressie");
		String[] params = StringUtils.split(codeline,"=");
		if ( params.length > 1)
		{
			cc = new VarCComponent(-100,-100,25,25, veld);
			cc.clearStapel();
			((VarCComponent)cc).setVariable(params[0], params[1]);
		}		
		return cc;
	}

	/**
	 * check is String codeLine is a subroutine header (assuming these headers
	 * have already been imported); 
	 * @param codeline String to be checked
	 * @return 0 (not subroutine header) or number of subroutine (1-5) 
	 */
	private int isDeeltaak(String codeline)
	{
		String s;
		int bracketpos = codeline.indexOf("(");
		if ( bracketpos == -1 )
		{
			s = codeline;			// old programs: import deeltaak call without brackets
		} 
		else
		{
			s = codeline.substring(0, bracketpos).trim();
		}
		
		for (int i=0; i<5; i++)
		{
			if ( s.equals(deeltaaknamen[i]) ) return i+1;
		}
		return 0;
	}
}
