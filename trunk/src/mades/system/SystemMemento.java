/**
 * 
 */
package mades.system;

import mades.common.Memento;
import mades.common.ParamMap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements the memento patter for the system by allowing
 * the {@link SystemConnector} to save and restore its state.
 */
public class SystemMemento extends Memento {


	private SignalMap signals;

	/**
	 * @param time
	 * @param params
	 */
	public SystemMemento(double time, ParamMap params, SignalMap signals) {
		super(time, params);
		this.signals = signals;
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
