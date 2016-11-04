package fi.stroomdiagrammengwt.client.text;

import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface Text extends ConstantsWithLookup {

	@DefaultStringValue(",")
	String decSep();
	@DefaultStringValue("nieuw diagram")
	String nieuwDiagramKnopLabel();
	@DefaultStringValue("terug")
	String terugKnopLabel();


}
