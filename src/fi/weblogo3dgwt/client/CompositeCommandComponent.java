package fi.weblogo3dgwt.client;


/**
 * abstract superclass for all CommandComponents that have a list of CCs inside.
 * see also class CompositeCommandComponent in WebLogoGWT 
 * @author Berge020
 */
public abstract class CompositeCommandComponent extends CommandComponent
{	
	
	public CompositeCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, sv);
	}
	
	/**
	 * Add a CC to this CompositeCommandComponent at the end of this CCC's CommandContainer.
	 * Note: this is for adding components from a script (ProgrammaImporter). This component's
	 * CommandContainer will handle dragg and drop.
	 * @param cc	CC to be added 
	 */
	abstract void addCComponent(CommandComponent cc);
	
	/**
	 * Called by the enclosed CommandContainer when its height has changed through
	 * addition or removal of CommandComponents. This component can than recalculate its
	 * own height.
	 * @param h		new height of the CommandContainer
	 */
	abstract void containerHeightChanged(int h);

}
