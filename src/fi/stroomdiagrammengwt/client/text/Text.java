package fi.stroomdiagrammengwt.client.text;

import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface Text extends ConstantsWithLookup {

	@DefaultStringValue(",")
	String decSep();
	@DefaultStringValue("nieuw diagram")
	String nieuwDiagramKnopLabel();
	@DefaultStringValue("terug")
	String terugKnopLabel();
	
	@DefaultStringValue("berekeningen")
	String berekeningenLabel();
	@DefaultStringValue("decimaal")
	String decimaalLabel();
	@DefaultStringValue("breuken")
	String breukenLabel();
	
	@DefaultStringValue("stroombreedte")
	String stroombreedteLabel();
	@DefaultStringValue("absoluut")
	String absoluutLabel();
	@DefaultStringValue("relatief")
	String relatiefLabel();
	
	@DefaultStringValue("opties")
	String optiesLabel();
	@DefaultStringValue("knooppunten met labels")
	String knooppuntenMetLabelsLabel();
	@DefaultStringValue("knooppunten zonder labels")
	String knooppuntenZonderLabelsLabel();
	@DefaultStringValue("voeg nieuwe bron toe")
	String voegNieuweBronToeLabel();


}
