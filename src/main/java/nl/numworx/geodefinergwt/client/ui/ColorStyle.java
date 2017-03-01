package nl.numworx.geodefinergwt.client.ui;

public class ColorStyle {
	final String color;
	
	ColorStyle(String c) { color = c; }
	
	public String getColor() { 
		return color;
	}

	@Override
	public String toString() {
		return "ColorStyle [color=" + color + "]";
	}
	
}
