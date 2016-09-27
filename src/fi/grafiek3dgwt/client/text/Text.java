package fi.grafiek3dgwt.client.text;

import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface Text extends ConstantsWithLookup {
	
	@DefaultStringValue("grafiek")
	String grafiekLabel();
	@DefaultStringValue("oppervlak")
	String oppervlakLabel();
	@DefaultStringValue("kromme")
	String krommeLabel();


}
