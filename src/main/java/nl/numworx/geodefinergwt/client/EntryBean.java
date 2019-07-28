package nl.numworx.geodefinergwt.client;



public interface EntryBean {

  long getTimestamp();

  void setTimestamp(long timestamp);

  String getName();

  void setName(String name);

  Boolean isVisible();

  void setVisible(boolean visible);

  Boolean getDragging();

  void setDragging(Boolean dragging);

  Double getX();

  void setX(Double x);

  Double getY();

  void setY(Double y);

  Double getValue();

  void setValue(Double value);

  Double getEvx();

  void setEvx(double evx);

  Double getEvy();

  void setEvy(double evy);

  String getArg();

  void setArg(String arg);

  Double getWy();
  Double getWx();
  void setWx(Double x);
  void setWy(Double y);
}
