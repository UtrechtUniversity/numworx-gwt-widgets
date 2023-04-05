package fi.binomverdgwt.client.text;

import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface Text extends ConstantsWithLookup {
	
	@DefaultStringValue("Hypergeometrisch")
	String hypergeometrischTekst();
	@DefaultStringValue("Binomiaal")
	String binomiaalTekst();
	@DefaultStringValue("populatie")
	String populatieTekst();
	@DefaultStringValue("Twee grenswaarden")
	String tweeGrenswaardenTekst();
	@DefaultStringValue("Kijk Na")
	String kijkNaTekst();



}
