package fi.graphtoolgwt.client.text;

import java.util.Vector;

public class Text_nl {

	
	private Vector<String> keys = new Vector<String>();
	private Vector<String> values = new Vector<String>();

	public Text_nl()
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
	{	"enOfButton_En", "en"},
	{	"enOfButton_Of", "of"},
	
	{	"kijkNaButton", "Klaar"},
	
	{	"feedbackTekstPuntenDeels", "Nog niet alle punten zijn (goed) getekend."},
    {	"feedbackTekstGrafiekenDeels", "Nog niet alle grafieken zijn goed getekend."},
    {	"feedbackTekstLabelsAssen", "Zet de juiste letters bij de assen"},
    {	"feedbackTekstTekenGrafiek", "Let op: teken ook de grafiek."},
    {	"feedbackTekstTeWeinigPunten", "Let op: je hebt nog niet voldoende punten getekend."},
    	
	{	"fc_huidigDomein", "Domein is nu: "},
    {	"fc_domeinBijFunctie", "Domein bij functie"},
    {	"fc_nieuwDomein", "Nieuw domein: "},
	
	};
}
