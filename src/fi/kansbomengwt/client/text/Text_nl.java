package fi.kansbomengwt.client.text;

import java.util.Vector;

import com.google.gwt.core.shared.GWT;

public class Text_nl {
	
		private Vector<String> keys = new Vector<String>();
		private Vector<String> values = new Vector<String>();

		public final TextConstants constants = GWT.create(TextConstants.class);
		
		
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

@Deprecated
		public String getString(String key)
		{
//			int keyint = keys.indexOf(key);
//			return values.get(keyint);
			return constants.getString(key);
		}

		static final Object[][] contents =
		{

			{	"trekkingBalkTekst", "Trekking" },
        	{	"trekkingBalkTekstMv", "trekkingen"},
        	{	"metTerugleggenTekst" , "Met terugleggen" },
	        {	"zonderTerugleggenTekst" , "Zonder terugleggen"},
	        {	"geenLabelTekst", "Label: geen"},
	        {	"letterLabelTekst", "Label: letter"},
	        {	"kansNaastLabelTekst", "Label: kans (in regel)"},
	        {	"kansOnderLabelTekst", "Label: kans"},
	        { 	"kleurTekst" , "Verschillende kleuren" },
	        {	"geenKansVolgordeTekst", "Geen kans of volgorde"},
	        {	"kansNaastTekst", "Kans zichtbaar (in regel)"},
	        {	"kansOnderTekst", "Kans zichtbaar"},
	        {	"volgordeTekst", "Volgorde zichtbaar"},
	        {	"letterTekst", "Letters zichtbaar"},
	        //{ 	"aantalTrekkingenTekst" , "Aantal trekkingen" },
	        { 	"aantalOptiesTekst" , "Aantal opties" },
	        {	"naamLetterTekst", "Namen en letters"},
	        //{ 	"naamOptieTekst" , "Naam optie " },
	        //{	"letterOptieTekst", "Letter optie " },
	        { 	"optieTekst" , "Optie " },
	        { 	"aantalTekst" , "Aantal " },
	        {	"naam1StringTekst" , "Blauw" },
	        {	"naam2StringTekst" , "Groen" },
	        {	"naam3StringTekst" , "Rood" },
	        {	"naam4StringTekst" , "Cyaan" },
	        {	"naam5StringTekst" , "Oranje" },
	        {	"naam6StringTekst" , "Magenta" },
	        {	"legendaTekst", "Legenda" },
	        {	"zichtbaarTekst", "Instelbaar voor leerling"},
	        {	"teruglegZichtbaarTekst", "Met/zonder terugleggen"},
	        {	"trekkingZichtbaarTekst", "Aantal trekkingen"},
	        {	"optiesZichtbaarTekst", "Aantal opties"},
	        {	"ballenZichtbaarTekst", "Aantal ballen per optie"},
	        {	"legendaZichtbaarTekst", "Legenda zichtbaar"},
	        {	"bovenbalkZichtbaarTekst", "Bovenbalk zichtbaar"},
	        {	"kijkNaTekst", "Kijk na"},
	        {	"nakijkModelTekst", "Nakijkmodel"},
	        {	"maxScoreTekst", "Maximum score"},
	        {	"externControlerenTekst", "Extern controleren"},
	        {	"bovenbalkTekst", "Bovenbalk"},
	        {	"bovenbalkMvTekst", "Meervoud"}
		};


}
