package nl.numworx.geodefinergwt.client.ui;

public class ColorStyle {
  final String color;
  final int rgba;

  ColorStyle(String c, int i) {
    color = c;
    rgba = i;
  }

  ColorStyle(int i) {
    this(colorString(i),i);
  }
  
  public String getColor() {
    return color;
  }

  @Override
  public String toString() {
    return "ColorStyle [color=" + color + "]";
  }

  public static String colorString(int rgba) {
    int a = (rgba >> 24) & 0xFF;
    if (a < 0xFF) {
      return "rgba(" + ((rgba >> 16) & 0xFF) + ',' + ((rgba >> 8) & 0xFF) + ',' + (rgba & 0xFF)
          + ',' + (a / 255.0f) + ')';
    }
    String hex = Integer.toHexString(rgba & 0xFFFFFF).toUpperCase();
    hex = "00000" + hex;
    int l = hex.length();
    hex = hex.substring(l - 6);
    return "#" + hex;
  }

  private static final Float FACTOR = 0.7f;
  public static int toTrailColor(int rgba) {
    if (rgba == 0)
      return 0;
    int a = rgba >>> 24; a = Math.max(10, a/8);
    int r = (rgba >>> 16) & 0xFF; r = bright(r);
    int g = (rgba >>> 8) & 0xFF; g = bright(g);
    int b = rgba & 0xFF; b = bright(b);
    return (a<<24)| (r<<16) | (g<<8) | b;
  }

  private static int bright(int r) {
    return Math.min(255, Math.round(Math.max(r, 1)/FACTOR));
  }
  
  public ColorStyle trailColorStyle() {
    return new ColorStyle(toTrailColor(rgba));
  }
  
  public FillStyle trailFillStyle() {
    return new FillStyle(toTrailColor(rgba));
  }
}
