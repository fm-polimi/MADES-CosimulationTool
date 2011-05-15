/**
 * 
 */
package mades.system;

import mades.common.Memento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements the memento patter for the system by allowing
 * the {@link SystemConnector} to save and restore its state.
 */
public class SystemMemento extends Memento {

	private SignalMap signals;
	
	/**
	 * Default constructor.
	 */
	SystemMemento() {	
	}

	/**
	 * @return the signals
	 */
	public SignalMap getSignals() {
		return signals;
	}

	@Override
	public void deleteRelatedFiles() {
		// TODO Auto-generated method stub
	}
		
}
