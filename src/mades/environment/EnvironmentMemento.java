/**
 * 
 */
package mades.environment;

import java.util.ArrayList;

import mades.common.Memento;
import mades.common.Variable;
import mades.system.SignalMap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements the memento patter for the environment by allowing
 * the {@link EnvironmentConnector} to save and restore its state.
 */
public class EnvironmentMemento extends Memento {
	
	private SignalMap signals;
	
	/**
	 * @param time
	 * @param params
	 */
	public EnvironmentMemento(double time, ArrayList<Variable> params, SignalMap signals) {
		super(time, params);
		this.signals = signals;
	}

	/**
	 * @return the signals
	 */
	public SignalMap getSignals() {
		return signals;
	}
	
	/* (non-Javadoc)
	 * @see mades.common.Memento#deleteRelatedFiles()
	 */
	@Override
	public void deleteRelatedFiles() {
		// TODO Auto-generated method stub
	}
		
}
