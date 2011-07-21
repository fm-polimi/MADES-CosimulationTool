/**
 * 
 */
package mades.environment;

import java.util.ArrayList;

import mades.common.timing.Time;
import mades.common.variables.Scope;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.system.SystemMemento;

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
		this.signals = new SignalMap(signals);
	}
	
	public EnvironmentMemento(EnvironmentMemento oldMemento) {
		this.time = oldMemento.getTime();
		this.params = new ArrayList<VariableAssignment>();
		for (VariableAssignment v: oldMemento.params) {
			VariableAssignment var = new VariableAssignment(
					v.getVariableDefinition(),
					v.getValue(),
					v.getAnnotation());
			this.params.add(var);
		}
		this.signals = new SignalMap(oldMemento.signals);
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
	
	/**
	 * Return a variable assignment for the given variable definition.
	 * 
	 * @param def the variable definition
	 * @return the assignment of the given variable od <code>null</code> if not assigned.
	 */
	public VariableAssignment getVariable(VariableDefinition def) {
		for (VariableAssignment v: params) {
			// You can use == because they MUST be the same instance
			if (v.getVariableDefinition() == def) {
				return v;
			}
		}
		return null;
	}
	
	/**
	 * @param memento
	 */
	public void update(SystemMemento memento) {
		if (time!=memento.getLatestSimulatedTime()) {
			throw new RuntimeException("Updating an environment memento at time: " + 
					time + 
					"  with a system memento at time: " +
					memento.getLatestSimulatedTime());
		}
		
		
		for (int i = params.size() - 1; i > -1; i --) {
			VariableAssignment envVar = params.get(i);
			VariableDefinition def = envVar.getVariableDefinition();
			if (def.getScope() == Scope.SYSTEM_SHARED) {
				VariableAssignment var = memento.getVariable(def, getTime());
				if (var == null) {
					throw new AssertionError("Missing variable from memento: " + def.getSystemName());
				}
				params.set(i, var.clone());
			}
		}
	}
}
