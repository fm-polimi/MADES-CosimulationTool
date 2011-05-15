/**
 * 
 */
package mades.environment;

import mades.common.ParamMap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements the memento patter for the environment by allowing
 * the {@link EnvironmentConnector} to save and restore its state.
 */
public class EnvironmentMemento {
	private double time;
	private ParamMap params;
	
	/**
	 * Default constructor.
	 */
	EnvironmentMemento () {
	}
}
