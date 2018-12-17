package fi.graphtoolgwt.client;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.statistiekgwt.client.ColorUtils;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.colorpicker.Dialog;
import fi.statistiekgwt.client.colorpicker.HueChangedEvent;
import fi.statistiekgwt.client.colorpicker.HuePicker;
import fi.statistiekgwt.client.colorpicker.IHueChangedHandler;
import fi.statistiekgwt.client.colorpicker.SaturationLightnessPicker;
import fi.statistiekgwt.client.event.ColorChangeEventHandler;

/**
 * Class for picking two colors.
 * 
 * @author Sylvia van Borkulo
 *
 */
public class SingleColorPickerDialog extends Dialog implements HasHandlers
{
	// color A picker
	private SaturationLightnessPicker slPickerA;
	private HuePicker huePickerA;
	private String colorA;
	private CssColor cssColorA;
	private int colorIndex;
	
	GraphToolGWTClientBundle graphToolGWTClientBundle;
	GraphToolCssResource graphToolCss;
	/**
	 *  To enable that static calls to StatistiekGWT.
	 */
	StatistiekGWT statistiek;


	/**
	 * The event bus to send events to event handler formuleComponentGWT.
	 */
	EventBus eventBus = GWT.create(SimpleEventBus.class);;
	
	public SingleColorPickerDialog()
	{
		super();
	}

	@Override
	protected Widget createDialogArea()
	{
		graphToolGWTClientBundle = GWT.create(GraphToolGWTClientBundle.class);
		graphToolCss = graphToolGWTClientBundle.getGraphToolGWTCSS();
		graphToolCss.ensureInjected();
		
		statistiek = new StatistiekGWT();
		
		setText(GraphToolGWT.rb.selectColor());

		HorizontalPanel panel = new HorizontalPanel();

		// color picker A
		slPickerA = new SaturationLightnessPicker();
		slPickerA.addStyleName(graphToolCss.margin());
		panel.add(slPickerA);
		huePickerA = new HuePicker();
		huePickerA.addStyleName(graphToolCss.margin());
		panel.add(huePickerA);

		// bind saturation/lightness picker and hue picker together
		huePickerA.addHueChangedHandler(new IHueChangedHandler()
		{
			public void hueChanged(HueChangedEvent event)
			{
				slPickerA.setHue(event.getHue());
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
	 * Get color A in CssColor format.
	 * 
	 * @return
	 */
	public CssColor getCssColorA()
	{
		return this.cssColorA;
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
	
	@Override
	protected void buttonClicked(Widget button)
	{
		// remember color when "OK" is clicked
		if (button == getOkButton())
		{
			this.extractColors();

			// fire color change event, so that the view, the color previewer in uop 
			// and dotplotmodel's colors are updated
			GraphToolColorChangeEvent event = new GraphToolColorChangeEvent(colorIndex,
				this.getCssColorA().value(), null);
			this.fireEvent(event);
		}

		close(button == getCancelButton());
	}

	@Override
	public void fireEvent(GwtEvent<?> e)
	{
		this.eventBus.fireEvent(e);
	}
	
	/**
	 * Read color A and color B from the pickers and set color A and B fields.
	 */
	private void extractColors()
	{
		this.colorA = this.slPickerA.getColor();
		setCssColorA();
	}

	/**
	 * Set the index of the color set by the dialog.
	 * Used in FormuleComponent.
	 * 
	 * @param index
	 */
	public void setColorIndex(int index)
	{
		colorIndex = index;
	}
	
	/**
	 * Subscribe for events
	 */
	public HandlerRegistration addGraphToolColorChangeEventHandler(ColorChangeEventHandler handler)
	{
		return this.eventBus.addHandler(GraphToolColorChangeEvent.TYPE, handler);
	}

}