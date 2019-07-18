package nl.numworx.geodefinergwt.client;



public interface EntryBean {

  long getTimestamp();

  void setTimestamp(long timestamp);

  String getName();

  void setName(String name);

  boolean isVisible();

  void setVisible(boolean visible);

  Boolean getDragging();

  void setDragging(Boolean dragging);

  Double getX();

  void setX(Double x);

  Double getY();

  void setY(Double y);

  Double getValue();

  void setValue(Double value);

  double getEvx();

  void setEvx(double evx);

  double getEvy();

  void setEvy(double evy);

  String getArg();

  void setArg(String arg);

}
