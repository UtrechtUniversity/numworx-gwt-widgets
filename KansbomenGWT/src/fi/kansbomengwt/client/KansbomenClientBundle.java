package fi.kansbomengwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface KansbomenClientBundle extends ClientBundle 
{
	//@Source("fi/kansbomen/client/images/goedkrul_en.gif")
	@Source("fi/kansbomengwt/client/images/knop-indicatie-goed.png")
	public ImageResource goedkrulResource();
	
	//@Source("fi/kansbomen/client/images/goedkrulhalf.gif")
	@Source("fi/kansbomengwt/client/images/knop-indicatie-magdoor.png")
	public ImageResource goedkrulHalfResource();
	
	//@Source("fi/kansbomen/client/images/foutkruis.gif")
	@Source("fi/kansbomengwt/client/images/knop-indicatie-fout.png")
	public ImageResource foutkruisResource();

}
