package fi.statistiekgwt.client.event;

import java.util.ArrayList;

import com.google.gwt.event.shared.GwtEvent;
import fi.statistiekgwt.client.types.AllowedTypes;

public class AddColumnEvent extends GwtEvent<AddColumnEventHandler>
{
	public static Type<AddColumnEventHandler> TYPE = new Type<AddColumnEventHandler>();

	/**
	 * Kolomnaam.
	 */
	private final String name;
	/**
	 * Kolomtype.
	 */
	private final AllowedTypes type;
	/**
	 * Bij kolomtype opsomming de opties.
	 */
	private final ArrayList<String> enumOptions;
	/**
	 * Eventuele uitleg bij kolom.
	 */
	private final String uitleg;
	/**
	 * Eventueel de formule-string om de nieuwe (numerieke) kolom te berekenen
	 * m.b.v. andere (numerieke) kolommen, bijv. "kolomnaam1 + kolomnaam2".
	 */
	private final String computeVariableFormula;

    public AddColumnEvent(String name, AllowedTypes type, ArrayList<String> enumOptions, String uitleg) 
    {
        this.name = name;
        this.type = type;
        this.enumOptions = enumOptions;
        this.uitleg = uitleg;
        this.computeVariableFormula = "";
    }

    public AddColumnEvent(String name, AllowedTypes type, ArrayList<String> enumOptions, String uitleg, String computeVariableFormula) 
    {
        this.name = name;
        this.type = type;
        this.enumOptions = enumOptions;
        this.uitleg = uitleg;
        this.computeVariableFormula = computeVariableFormula;
    }

	@Override
	public Type<AddColumnEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(AddColumnEventHandler handler)
	{
		handler.onAddColumn(this);
	}

	public String getName()
	{
        return this.name;
    }

	public AllowedTypes getType()
	{
        return this.type;
    }

	public ArrayList<String> getEnumOptions()
	{
        return this.enumOptions;
    }

	public String getUitleg()
	{
        return this.uitleg;
    }

	public String getComputeVariableFormula()
	{
        return this.computeVariableFormula;
    }
}
