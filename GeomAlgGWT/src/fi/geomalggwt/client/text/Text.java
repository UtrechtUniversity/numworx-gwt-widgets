package fi.geomalggwt.client.text;

import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface Text extends ConstantsWithLookup {
	
	@DefaultStringValue("nl")
	String taalString();
	@DefaultStringValue("wis")
	String wisKnopLabel();
	@DefaultStringValue("terug")
	String terugKnopLabel();
	@DefaultStringValue("Kijk na")
	String kijkNaKnopLabel();
	@DefaultStringValue("samenvoegen")
	String samenvoegenLabel();
	@DefaultStringValue("Draai")
	String menuDraaiLabel();
	@DefaultStringValue("Kopieer")
	String menuKopieerLabel();
	@DefaultStringValue("Splits")
	String menuSplitsLabel();
	@DefaultStringValue("Splits volledig")
	String menuSplitsVolledigLabel();
	@DefaultStringValue("Voeg samen")
	String menuVoegSamenLabel();
	@DefaultStringValue("Maak los")
	String menuMaakLosLabel();
	@DefaultStringValue("Maak alles los")
	String menuMaakAllesLosLabel();
	@DefaultStringValue("opp")
	String oppervlakteLabel();

	
	

}
