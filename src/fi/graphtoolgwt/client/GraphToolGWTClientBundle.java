package fi.graphtoolgwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;


public interface GraphToolGWTClientBundle extends ClientBundle 
{
	//@Source("fi/graphtoolgwt/client/images/goedkrul_en.gif")
	@Source("fi/graphtoolgwt/client/images/knop-indicatie-goed.png")
	public ImageResource goedkrulResource();
	
	//@Source("fi/graphtoolgwt/client/images/goedkrulhalf.gif")
	@Source("fi/graphtoolgwt/client/images/knop-indicatie-magdoor.png")
	public ImageResource goedkrulHalfResource();
	
	//@Source("fi/graphtoolgwt/client/images/foutkruis.gif")
	@Source("fi/graphtoolgwt/client/images/knop-indicatie-fout.png")
	public ImageResource foutkruisResource();
	
	@Source("fi/graphtoolgwt/client/images/teken_penknop_default.gif")
	public ImageResource drawButtonUpResource();
	
	@Source("fi/graphtoolgwt/client/images/teken_penknop_selected.gif")
	public ImageResource drawButtonDownResource();

	@Source("fi/graphtoolgwt/client/images/teken_gumknop_default.gif")
	public ImageResource deleteButtonUpResource();
	
	@Source("fi/graphtoolgwt/client/images/teken_gumknop_selected.gif")
	public ImageResource deleteButtonDownResource();
	
	@Source("fi/graphtoolgwt/client/images/teken_cursorknop_default.gif")
	public ImageResource dragButtonUpResource();
	
	@Source("fi/graphtoolgwt/client/images/teken_cursorknop_selected.gif")
	public ImageResource dragButtonDownResource();

	@Source("fi/graphtoolgwt/client/images/teken_puntenknop_default.gif")
	public ImageResource puntenButtonUpResource();
	
	@Source("fi/graphtoolgwt/client/images/teken_puntenknop_selected.gif")
	public ImageResource puntenButtonDownResource();
	
	@Source("fi/graphtoolgwt/client/images/teken_lijnenknop_default.gif")
	public ImageResource lijnenButtonUpResource();
	
	@Source("fi/graphtoolgwt/client/images/teken_lijnenknop_selected.gif")
	public ImageResource lijnenButtonDownResource();
	
	@Source("fi/graphtoolgwt/client/images/teken_krommeknop_default.gif")
	public ImageResource krommeButtonUpResource();
	
	@Source("fi/graphtoolgwt/client/images/teken_krommeknop_selected.gif")
	public ImageResource krommeButtonDownResource();
	
	@Source("fi/graphtoolgwt/client/images/teken_extrapoleerknop_default.gif")
	public ImageResource extrapoleerButtonUpResource();
	
	@Source("fi/graphtoolgwt/client/images/teken_extrapoleerknop_selected.gif")
	public ImageResource extrapoleerButtonDownResource();
	
	@Source("fi/graphtoolgwt/client/images/reseticon.gif")
	public ImageResource resetButtonResource();
	
	@Source("fi/graphtoolgwt/client/images/zoominknop.gif")
	public ImageResource zoomInButtonResource();
	
	@Source("fi/graphtoolgwt/client/images/zoomuitknop.gif")
	public ImageResource zoomUitButtonResource();
	
	@Source("fi/graphtoolgwt/client/images/zoominknoptabel.gif")
	public ImageResource zoomInTabelButtonResource();
	
	@Source("fi/graphtoolgwt/client/images/teken_wisknop_tabel.gif")
	public ImageResource resetTabelButtonResource();
	
	@Source("fi/graphtoolgwt/client/images/zoomuitknoptabel.gif")
	public ImageResource zoomUitTabelButtonResource();
	
	@Source("fi/graphtoolgwt/client/images/zoominxknop.gif")
	public ImageResource zoomInXResource();
	
	@Source("fi/graphtoolgwt/client/images/zoomuitxknop.gif")
	public ImageResource zoomUitXResource();
	
	@Source("fi/graphtoolgwt/client/images/zoominyknop.gif")
	public ImageResource zoomInYResource();
	
	@Source("fi/graphtoolgwt/client/images/zoomuityknop.gif")
	public ImageResource zoomUitYResource();
	
	@Source("fi/graphtoolgwt/client/images/zoomstandaardknop.gif")
	public ImageResource zoomStandaardResource();
	
	@Source("fi/graphtoolgwt/client/images/pijllinksknop.gif")
	public ImageResource pijlLinksButtonResource();
	
	@Source("fi/graphtoolgwt/client/images/pijlrechtsknop.gif")
	public ImageResource pijlRechtsButtonResource();
	
	@Source("fi/graphtoolgwt/client/images/pijlterug.gif")
	public ImageResource regelMinderButtonResource();
	
	@Source("fi/graphtoolgwt/client/images/pijladd.gif")
	public ImageResource regelMeerButtonResource();
		
	@Source("fi/graphtoolgwt/client/css/GraphToolGWT.css")
	public  GraphToolCssResource getGraphToolGWTCSS();
	
	

}
