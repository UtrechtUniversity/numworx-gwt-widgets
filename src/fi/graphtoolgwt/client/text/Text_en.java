package fi.graphtoolgwt.client.text;

import java.util.Vector;

public class Text_en extends Text_nl {

	
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
	{ 	"ofLabel", "or" },
	{	"enOfButton_En", "and"},
	{	"enOfButton_Of", "or"},
	
	{	"kijkNaButton", "Ready"},
	
	{	"feedbackTekstPuntenDeels", "Not all points are drawn (correctly) yet."},
    {	"feedbackTekstGrafiekenDeels", "Not all graphs are drawn (correctly) yet."},
    {	"feedbackTekstLabelsAssen", "Place the correct letters next to the axes"},
    {	"feedbackTekstTekenGrafiek", "Also draw the graph."},
    {	"feedbackTekstTeWeinigPunten", "An insufficient number of points has been drawn."},
    	
	{	"fc_huidigDomein", "Current domain: "},
    {	"fc_domeinBijFunctie", "Function domain"},
    {	"fc_nieuwDomein", "New domain: "},
	
	};
}
