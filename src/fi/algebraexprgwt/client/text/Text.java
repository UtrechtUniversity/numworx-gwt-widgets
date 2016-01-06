package fi.algebraexprgwt.client.text;

import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface Text extends ConstantsWithLookup {
	
	@DefaultStringValue("expressie")
	String expressieLabel();

	@DefaultStringValue("waarde")
	String waardeLabel();

	@DefaultStringValue("Wis")
	String wisKnopLabel();

	@DefaultStringValue("grafiek")
	String grafiekLabel();

	@DefaultStringValue("tabel")
	String tabelLabel();

	@DefaultStringValue("Bewerkingen")
	String bewerkingenLabel();
	
	@DefaultStringValue("In-/Uitvoer")
	String invoerLabel();

	@DefaultStringValue("toon label")
	String toonLabel();

	@DefaultStringValue("verberg label")
	String verbergLabel();
	
	@DefaultStringValue("toon tabel")
	String toonTabel();
	
	@DefaultStringValue("verberg tabel")
	String verbergTabel();
	
	@DefaultStringValue("toon ketting")
	String toonKetting();

	@DefaultStringValue("verberg ketting")
	String verbergKetting();
	
	@DefaultStringValue("Kijk na")
	String kijkNa();
	
	

}
