/**
 * 
 */
package mades.environment;

import java.util.ArrayList;

import mades.common.Variable;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements the memento patter for the environment by allowing
 * the {@link EnvironmentConnector} to save and restore its state.
 */
public class EnvironmentMemento {
	
	private SignalMap signals;
	private double time;
	private ArrayList<Variable> params;
	
	/**
	 * @param time
	 * @param params
	 */
	public EnvironmentMemento(double time, ArrayList<Variable> params, SignalMap signals) {
		this.time = time;
		this.params = params;
		this.signals = signals;
	}
	
	/**
	 * @return the time
	 */
	public double getTime() {
		return time;
	}

	/**
	 * @return the params
	 */
	public ArrayList<Variable> getParams() {
		return params;
	}
	
	/**
	 * @return the signals
	 */
	public SignalMap getSignals() {
		return signals;
	}
	
	public void deleteRelatedFiles() {
		// TODO Auto-generated method stub
	}
		
}
