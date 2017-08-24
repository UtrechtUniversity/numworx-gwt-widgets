package fi.graphtoolgwt.client.text;

import java.util.Vector;

public class Text_en {

	
	private Vector<String> keys = new Vector<String>();
	private Vector<String> values = new Vector<String>();

	public Text_en()
	{
		Object[][] items = this.getContents();
		for (int i = 0; i < items.length; i++)
		{
			keys.add(items[i][0].toString());
			values.add(items[i][1].toString());
		}
	}

	public Object[][] getContents()
	{
		return contents;
	}

	public String getString(String key)
	{
		int keyint = keys.indexOf(key);
		return values.get(keyint);
	}

	static final Object[][] contents =
	{
	{ 	"ofLabel", "of" },
	{ 	"ofLabel", "or" },
	{	"enOfButton_En", "en"},
	{	"enOfButton_En", "and"},
	{	"enOfButton_Of", "of"},
	{	"enOfButton_Of", "or"},
	
	{	"kijkNaButton", "Klaar"},
	{	"kijkNaButton", "Ready"},
	
	{	"feedbackTekstPuntenDeels", "Nog niet alle punten zijn (goed) getekend."},
	{	"feedbackTekstPuntenDeels", "Not all points are drawn (correctly) yet."},
    {	"feedbackTekstGrafiekenDeels", "Nog niet alle grafieken zijn goed getekend."},
    {	"feedbackTekstGrafiekenDeels", "Not all graphs are drawn (correctly) yet."},
    {	"feedbackTekstLabelsAssen", "Zet de juiste letters bij de assen"},
    {	"feedbackTekstLabelsAssen", "Place the correct letters to the axes"},
    {	"feedbackTekstTekenGrafiek", "Let op: teken ook de grafiek."},
    {	"feedbackTekstTekenGrafiek", "Be aware: also draw the graph."},
    {	"feedbackTekstTeWeinigPunten", "Let op: je hebt nog niet voldoende punten getekend."},
    {	"feedbackTekstTeWeinigPunten", "An insufficient amount of points have been drawn."},
    	
	{	"fc_huidigDomein", "Domein is nu: "},
	{	"fc_huidigDomein", "Current domain: "},
    {	"fc_domeinBijFunctie", "Domein bij functie"},
    {	"fc_domeinBijFunctie", "Function domain"},
    {	"fc_nieuwDomein", "Nieuw domein: "},
    {	"fc_nieuwDomein", "New domain: "},
	
	};
}
