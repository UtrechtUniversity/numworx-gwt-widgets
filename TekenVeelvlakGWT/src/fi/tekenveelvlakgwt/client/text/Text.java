package fi.tekenveelvlakgwt.client.text;

import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface Text extends ConstantsWithLookup {
	
	@DefaultStringValue("Maak lijn")
	String lijnKnopLabel();
	@DefaultStringValue("MAAK LIJN")
	String lijnKnopCapLabel();
	@DefaultStringValue("Maak vlak")
	String vlakKnopLabel();
	@DefaultStringValue("MAAK VLAK")
	String vlakKnopCapLabel();
	@DefaultStringValue("Verberg basis")
	String verbergBasisKnopLabel();
	@DefaultStringValue("Toon basis")
	String toonBasisKnopLabel();
	@DefaultStringValue("Wis lijnen")
	String wisLijnKnopLabel();
	@DefaultStringValue("Wis vlakken")
	String wisVlakKnopLabel();
	@DefaultStringValue("Maak ongedaan")
	String terugKnopLabel();
	@DefaultStringValue("zoom")
	String zijdeLabel();
	@DefaultStringValue("kubus")
	String kubusLabel();
	@DefaultStringValue("tetraeder")
	String tetraederLabel();
	@DefaultStringValue("dodecaeder")
	String dodecaederLabel();
	@DefaultStringValue("octaeder")
	String octaederLabel();
	@DefaultStringValue("isosaeder")
	String icosaederLabel();
	@DefaultStringValue("Kijk na")
	String kijkNaLabel();

	
	
	
/*	
				{ "wisLijnKnopLabel" , "Wis lijnen" },
                { "wisVlakKnopLabel" , "Wis vlakken" },
                { "terugKnopLabel" , "Maak ongedaan" },
				{ "zijdeLabel" , "zoom" },
				{ "kubusLabel" , "kubus" },
				{ "tetraederLabel" , "tetraeder" },
				{ "dodecaederLabel" , "dodecaeder" },
				{ "octaederLabel" , "octaeder" },
				{ "icosaederLabel" , "icosaeder" },
*/


}
