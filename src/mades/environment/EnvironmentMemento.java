/**
 * 
 */
package mades.environment;

import java.util.ArrayList;

import mades.common.timing.Time;
import mades.common.variables.Scope;
import mades.common.variables.Transition;
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
	
	private ArrayList<Transition> transitions;
	private Time time;
	private ArrayList<VariableAssignment> params;
	
	/**
	 * @param time
	 * @param params
	 */
	public EnvironmentMemento(Time time,
			ArrayList<VariableAssignment> params) {
		this.time = time;
		this.params = params;
		this.transitions = new ArrayList<Transition>();
	}
	
	public EnvironmentMemento(EnvironmentMemento oldMemento) {
		this.time = oldMemento.getTime();
		this.params = new ArrayList<VariableAssignment>(oldMemento.params);
		this.transitions = new ArrayList<Transition>(oldMemento.transitions);
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

	/**
	 * @return the transitions
	 */
	public ArrayList<Transition> getTransitions() {
		return transitions;
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	public boolean addTransition(Transition e) {
		return transitions.add(e);
	}
	
	public void deleteTransitions() {
		for (Transition t: transitions) {
			t.getTrigger().removeTransition(t);
		}
		transitions.clear();
	}
}
