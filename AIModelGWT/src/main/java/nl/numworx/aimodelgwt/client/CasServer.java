package nl.numworx.aimodelgwt.client;

import nl.uu.fi.dwo.ideas.client.IdeasClient;
import nl.uu.fi.dwo.ideas.client.IdeasIF;

public class CasServer extends IdeasClient {

  static private native String casServer() /*-{
    return $wnd.casServer
  }-*/;

  private CasServer(String cas) {
    super(cas, NONE);
  }
  
  public static IdeasIF create() {
    try {
      String cas = casServer();
      if (cas != null && !cas.isEmpty()) {
        return new CasServer(cas);
      }
    } catch (Exception e) {}
    return new FailingIdeas();
  }
}
