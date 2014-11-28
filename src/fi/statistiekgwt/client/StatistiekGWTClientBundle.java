package fi.statistiekgwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface StatistiekGWTClientBundle extends ClientBundle 
{
	@Source("fi/statistiekgwt/client/images/reseticon.gif")
	public ImageResource resetResource();
	
	@Source("fi/statistiekgwt/client/images/arrow-137-16_525252up.gif")
	public ImageResource arrowUpResource();
	
	@Source("fi/statistiekgwt/client/images/arrow-199-16_525252down.gif")
	public ImageResource arrowDownResource();
	
	@Source("fi/statistiekgwt/client/css/StatistiekGWT.css")
	public  StatistiekCssResource getStatistiekGWTCSS();

}
