package fi.nabouwenaanzichtengwt.client.text;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;

@DefaultLocale("en")
public interface Msgs extends Messages {
	@DefaultMessage("{0,number} cubes")
	@AlternateMessage({"one", "one cube"})
	String blokjes(@PluralCount int aantal);
}
