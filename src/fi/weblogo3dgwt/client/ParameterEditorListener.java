package fi.weblogo3dgwt.client;

/**
 * Interface for CommandComponents that want to receive updates of parameters 
 * that have been edited.
 */
public interface ParameterEditorListener
{
	/**
	 * Receive the new value of a edited parameter
	 * @param text	the new parameter text as a String. No parsing has been done.
	 */
	public void parameterEdited(String text);
	
	/**
	 * Start parameter editing, triggered by a mouse click on the specified position
	 * @param x		x postion of mouseclick
	 * @param y		y postion of mouseclick
	 */
	public void parameterComponentClicked(int x, int y);
}
