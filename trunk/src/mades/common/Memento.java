/**
 * 
 */
package mades.common;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public abstract class Memento {
	private double time;
	private ParamMap params;
	
	/**
	 * Default constructor. 
	 */
	public Memento() {}
	
	/**
	 * @return the time
	 */
	public double getTime() {
		return time;
	}

	/**
	 * @return the params
	 */
	public ParamMap getParams() {
		return params;
	}
	
	/**
	 * Deletes any file related to this memento.
	 * This method should be invoked as soon as this
	 * instance is no longer needed. 
	 */
	public abstract void deleteRelatedFiles();
}
