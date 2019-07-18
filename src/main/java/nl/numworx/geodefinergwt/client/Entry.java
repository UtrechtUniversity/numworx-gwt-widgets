package nl.numworx.geodefinergwt.client;

public class Entry implements EntryBean {
  
  private Entry() {}
  static EntryBean entry() {
    return new Entry();
  }
  
  
  private long timestamp;
  private String name;
  private boolean visible;
  private Boolean dragging;
  private Double x,y,value;
  private double evx, evy;
  private String arg;
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#getTimestamp()
   */
  @Override
  public long getTimestamp() {
    return timestamp;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#setTimestamp(long)
   */
  @Override
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#getName()
   */
  @Override
  public String getName() {
    return name;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#setName(java.lang.String)
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#isVisible()
   */
  @Override
  public boolean isVisible() {
    return visible;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#setVisible(boolean)
   */
  @Override
  public void setVisible(boolean visible) {
    this.visible = visible;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#getDragging()
   */
  @Override
  public Boolean getDragging() {
    return dragging;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#setDragging(java.lang.Boolean)
   */
  @Override
  public void setDragging(Boolean dragging) {
    this.dragging = dragging;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#getX()
   */
  @Override
  public Double getX() {
    return x;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#setX(java.lang.Double)
   */
  @Override
  public void setX(Double x) {
    this.x = x;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#getY()
   */
  @Override
  public Double getY() {
    return y;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#setY(java.lang.Double)
   */
  @Override
  public void setY(Double y) {
    this.y = y;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#getValue()
   */
  @Override
  public Double getValue() {
    return value;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#setValue(java.lang.Double)
   */
  @Override
  public void setValue(Double value) {
    this.value = value;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#getEvx()
   */
  @Override
  public double getEvx() {
    return evx;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#setEvx(double)
   */
  @Override
  public void setEvx(double evx) {
    this.evx = evx;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#getEvy()
   */
  @Override
  public double getEvy() {
    return evy;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#setEvy(double)
   */
  @Override
  public void setEvy(double evy) {
    this.evy = evy;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#getArg()
   */
  @Override
  public String getArg() {
    return arg;
  }
  /* (non-Javadoc)
   * @see nl.numworx.geodefinergwt.client.EntryBean#setArg(java.lang.String)
   */
  @Override
  public void setArg(String arg) {
    this.arg = arg;
  }
}