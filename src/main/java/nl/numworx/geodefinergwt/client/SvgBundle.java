package nl.numworx.geodefinergwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;

public interface SvgBundle extends ClientBundle {
  @Source("nl/numworx/geodefinergwt/client/resources/Pan-Grab.svg")
  @MimeType("image/svg+xml")
  DataResource pan_svg();

  @Source("nl/numworx/geodefinergwt/client/resources/Pan-Grab-A.svg")
  @MimeType("image/svg+xml")
  DataResource pan_active_svg();


  @Source("nl/numworx/geodefinergwt/client/resources/Kegelsnede.svg")
  @MimeType("image/svg+xml")
  DataResource kegelsnede_svg();

  @Source("nl/numworx/geodefinergwt/client/resources/Kegelsnede-A.svg")
  @MimeType("image/svg+xml")
  DataResource kegelsnede_active_svg();

  @Source("nl/numworx/geodefinergwt/client/resources/Oppervlakte.svg")
  @MimeType("image/svg+xml")
  DataResource oppervlakte_svg();

  @Source("nl/numworx/geodefinergwt/client/resources/Oppervlakte-A.svg")
  @MimeType("image/svg+xml")
  DataResource oppervlakte_active_svg();

  @Source("nl/numworx/geodefinergwt/client/resources/Cirkel-met-straal.svg")
  @MimeType("image/svg+xml")
  DataResource cirkel_met_straal_svg();

  @Source("nl/numworx/geodefinergwt/client/resources/Cirkel-met-straal-A.svg")
  @MimeType("image/svg+xml")
  DataResource cirkel_met_straal_active_svg();

  @Source("nl/numworx/geodefinergwt/client/resources/Translatie-punt.svg")
  @MimeType("image/svg+xml")
  DataResource translatie_svg();

  @Source("nl/numworx/geodefinergwt/client/resources/Translatie-punt-A.svg")
  @MimeType("image/svg+xml")
  DataResource translatie_active_svg();

  
  
}
