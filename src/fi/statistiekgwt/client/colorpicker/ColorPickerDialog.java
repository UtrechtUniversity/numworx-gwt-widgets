package fi.statistiekgwt.client.colorpicker;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.statistiekgwt.client.ColorUtils;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekUtils;
import fi.statistiekgwt.client.colorpicker.Dialog;
import fi.statistiekgwt.client.event.ColorChangeEvent;
import fi.statistiekgwt.client.event.ColorChangeEventHandler;

/**
 * Class for picking two colors.
 * 
 * @author Sylvia van Borkulo
 *
 */
public class ColorPickerDialog extends Dialog implements HasHandlers
{
	// color A picker
	private SaturationLightnessPicker slPickerA;
	private HuePicker huePickerA;
	private String colorA;
	private CssColor cssColorA;

	// color B picker
	private SaturationLightnessPicker slPickerB;
	private HuePicker huePickerB;
	private String colorB;
	private CssColor cssColorB;

	/**
	 * The event bus to send events to event handlers associated 
	 * with the views using StatTableModel.
	 */
	EventBus eventBus;
	
	public ColorPickerDialog()
	{
		super();
		
		this.eventBus = StatistiekUtils.EVENT_BUS;
	}

	@Override
	protected Widget createDialogArea()
	{
		setText(StatistiekGWT.rb.selectColors());

		HorizontalPanel panel = new HorizontalPanel();

		// color picker A
		slPickerA = new SaturationLightnessPicker();
		slPickerA.addStyleName(statistiekCss.margin());
		panel.add(slPickerA);
		huePickerA = new HuePicker();
		huePickerA.addStyleName(statistiekCss.margin());
		panel.add(huePickerA);

		// bind saturation/lightness picker and hue picker together
		huePickerA.addHueChangedHandler(new IHueChangedHandler()
		{
			public void hueChanged(HueChangedEvent event)
			{
				slPickerA.setHue(event.getHue());
			}
		});

		// color picker B
		slPickerB = new SaturationLightnessPicker();
		slPickerB.addStyleName(statistiekCss.margin());
		panel.add(slPickerB);
		huePickerB = new HuePicker();
		huePickerB.addStyleName(statistiekCss.margin());
		panel.add(huePickerB);

		// bind saturation/lightness picker and hue picker together
		huePickerB.addHueChangedHandler(new IHueChangedHandler()
		{
			public void hueChanged(HueChangedEvent event)
			{
				slPickerB.setHue(event.getHue());
			}
		});

		return panel;
	}

	public void setColorA(String color)
	{
		int[] rgb = ColorUtils.getRGB(color);
		int[] hsl = ColorUtils.rgb2hsl(rgb);
		
		this.huePickerA.setHue(hsl[0]);
		this.slPickerA.setColor(color);
	}

	public void setColorA(CssColor color)
	{
		int[] rgb = ColorUtils.getRGB(color);
		int[] hsl = ColorUtils.rgb2hsl(rgb);
		
		this.huePickerA.setHue(hsl[0]);
		this.slPickerA.setColor(color);
	}

	public void setColorB(String color)
	{
		int[] rgb = ColorUtils.getRGB(color);
		int[] hsl = ColorUtils.rgb2hsl(rgb);
		
		this.huePickerB.setHue(hsl[0]);
		this.slPickerB.setColor(color);
	}

	public void setColorB(CssColor color)
	{
		int[] rgb = ColorUtils.getRGB(color);
		int[] hsl = ColorUtils.rgb2hsl(rgb);
		
		this.huePickerB.setHue(hsl[0]);
		this.slPickerB.setColor(color);
	}

	/**
	 * Get color A in format for example "15efef".
	 * 
	 * @return
	 */
	public String getColorA()
	{
		return this.colorA;
	}

	/**
	 * Get color B in format for example "15efef".
	 * 
	 * @return
	 */
	public String getColorB()
	{
		return this.colorB;
	}

	/**
	 * Get color A in CssColor format.
	 * 
	 * @return
	 */
	public CssColor getCssColorA()
	{
		return this.cssColorA;
	}
	
	/**
	 * Get color B in CssColor format.
	 * 
	 * @return
	 */
	public CssColor getCssColorB()
	{
		return this.cssColorB;
	}
	
	/**
	 * Set color A in CssColor format based on field colorA.
	 * 
	 * @return
	 */
	public CssColor setCssColorA()
	{
		if (this.colorA == null)
		{
			return null;
		}
		else
		{
			int[] rgb = ColorUtils.getRGB(this.colorA);
			
			// set the css color format
			if (rgb.length == 3)
			{
				this.cssColorA = CssColor.make(rgb[0], rgb[1], rgb[2]);
			}
			else
			{
				this.cssColorA = null;
			}
	
			return this.cssColorA;
		}
	}
	
	/**
	 * Set color B in CssColor format based on field colorB.
	 * 
	 * @return
	 */
	public CssColor setCssColorB()
	{
		if (this.colorB == null)
		{
			return null;
		}
		else
		{
			int[] rgb = ColorUtils.getRGB(this.colorB);
			
			// set the css color format
			if (rgb.length == 3)
			{
				this.cssColorB = CssColor.make(rgb[0], rgb[1], rgb[2]);
			}
			else
			{
				this.cssColorB = null;
			}
	
			return this.cssColorB;
		}
	}

	@Override
	protected void buttonClicked(Widget button)
	{
		// remember color when "OK" is clicked
		if (button == getOkButton())
		{
			this.extractColors();
			
			// fire color change event, so that the view, the color previewer in uop 
			// and dotplotmodel's colors are updated
			ColorChangeEvent event = new ColorChangeEvent(
				this.getCssColorA().value(), this.getCssColorB().value());
			this.fireEvent(event);
		}

		close(button == getCancelButton());
	}

	/**
	 * Read color A and color B from the pickers and set color A and B fields.
	 */
	private void extractColors()
	{
		this.colorA = this.slPickerA.getColor();
		setCssColorA();

		this.colorB = this.slPickerB.getColor();
		setCssColorB();
	}

	@Override
	public void fireEvent(GwtEvent<?> e)
	{
		this.eventBus.fireEvent(e);
	}
	
	/**
	 * Subscribe for events
	 */
	public HandlerRegistration addColorChangeEventHandler(ColorChangeEventHandler handler)
	{
		return this.eventBus.addHandler(ColorChangeEvent.TYPE, handler);
	}


}