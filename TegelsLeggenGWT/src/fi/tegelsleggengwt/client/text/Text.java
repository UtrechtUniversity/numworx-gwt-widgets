package fi.tegelsleggengwt.client.text;

import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface Text extends ConstantsWithLookup {
	
	@DefaultStringValue("Draai")
	String draaiknopLabel();
	@DefaultStringValue("Wis alles")
	String wisknopLabel();
	@DefaultStringValue("Ontwerp tegel")
	String ontwerpknopLabel();
	@DefaultStringValue("Nieuwe tegel")
	String nieuwetegelknopLabel();
	@DefaultStringValue("Een stap terug")
	String terugknopLabel();
	@DefaultStringValue("Tegels leggen")
	String leggenknopLabel();
	@DefaultStringValue("Fijn raster")
	String fijnrasterTekst();
	@DefaultStringValue("Grof raster")
	String grofrasterTekst();
	
	@DefaultStringValue("Draai")
	String menuDraaiLabel();
	@DefaultStringValue("Spiegel")
	String menuSpiegelLabel();
	@DefaultStringValue("Kleur")
	String menuKleurLabel();
	@DefaultStringValue("Kopieer")
	String menuKopieerLabel();


	
}
