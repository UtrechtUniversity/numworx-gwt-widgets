package fi.graphtoolgwt.client;

import fi.statistiekgwt.client.event.ColorChangeEvent;

public class GraphToolColorChangeEvent extends ColorChangeEvent
{

	int colorIndex = -1;
	
	public GraphToolColorChangeEvent(String colorA, String colorB)
	{
		super(colorA, colorB);
	}

	public GraphToolColorChangeEvent(int index, String colorA, String colorB)
	{
		super(colorA, colorB);

		colorIndex = index;
	}

	public int getColorIndex()
	{
		return colorIndex;
	}
}
