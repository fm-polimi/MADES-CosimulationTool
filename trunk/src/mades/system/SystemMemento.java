/**
 * 
 */
package mades.system;

import mades.common.ParamMap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements the memento patter for the system by allowing
 * the {@link SystemConnector} to save and restore its state.
 */
public class SystemMemento {
	private double time;
	private ParamMap params;
	private SignalMap signals;
	
	/**
	 * Default constructor.
	 */
	SystemMemento() {	
	}
		
}
