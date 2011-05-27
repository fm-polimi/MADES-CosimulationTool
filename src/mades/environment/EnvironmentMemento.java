/**
 * 
 */
package mades.environment;

import java.util.ArrayList;

import mades.common.timing.Time;
import mades.common.variables.VariableAssignment;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements the memento patter for the environment by allowing
 * the {@link EnvironmentConnector} to save and restore its state.
 */
public class EnvironmentMemento {
	
	private SignalMap signals;
	private Time time;
	private ArrayList<VariableAssignment> params;
	
	/**
	 * @param time
	 * @param params
	 */
	public EnvironmentMemento(Time time, ArrayList<VariableAssignment> params, SignalMap signals) {
		this.time = time;
		this.params = params;
		this.signals = signals;
	}
	
	/**
	 * @return the time
	 */
	public Time getTime() {
		return time;
	}

	/**
	 * @return the params
	 */
	public ArrayList<VariableAssignment> getParams() {
		return params;
	}
	
	/**
	 * @return the signals
	 */
	public SignalMap getSignals() {
		return signals;
	}
	
}
