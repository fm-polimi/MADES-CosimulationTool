/**
 * 
 */
package mades.system;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultimap;

import mades.common.timing.Time;
import mades.common.variables.Scope;
import mades.common.variables.Transition;
import mades.common.variables.Trigger;
import mades.common.variables.TriggerFactory;
import mades.common.variables.Type;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.environment.EnvironmentMemento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements the memento pattern for the system by allowing
 * the {@link SystemConnector} to save and restore its state.
 */
public class SystemMemento {

	private TreeMultimap<Time, VariableAssignment> variablesMultimap;
	private HashMultimap<Time, Collection<VariableAssignment>> rolledBackVariablesMultimap;	
	private ArrayList<Transition> transitions;
	
	/**
	 */
	public SystemMemento() {
		variablesMultimap = TreeMultimap.create();
		rolledBackVariablesMultimap = HashMultimap.create();
		transitions = new ArrayList<Transition>();
	}

	/**
	 */
	public SystemMemento(SystemMemento oldMemento) {
		variablesMultimap = TreeMultimap.create();
		SortedSet<Time> keys = oldMemento.variablesMultimap.keySet();
		for (Time t: keys) {
			SortedSet<VariableAssignment> variables = oldMemento.variablesMultimap.get(t);
			for (VariableAssignment v: variables) {
				VariableAssignment var = new VariableAssignment(
						v.getVariableDefinition(), 
						v.getValue());
				variablesMultimap.put(t, var);
			}
		}
		
		// TODO shall we make a deep copy?
		rolledBackVariablesMultimap = HashMultimap.create(oldMemento.rolledBackVariablesMultimap);
		transitions = new ArrayList<Transition>();
	}

	public void computeTransitions(TriggerFactory triggerFactory) {
		transitions.clear();
		SortedSet<Time> times = variablesMultimap.keySet();	
		if (times.size() < 2) {
			return;
		}
		Time now = null;
		Time previous = null;
		for (Time t: times) {
			now = t;
			previous = now;
		}
		if (!now.equals(getLatestSimulatedTime())) {
			throw new AssertionError("Wrong last time");
		}
		ArrayList<VariableAssignment> previousVars = new ArrayList(variablesMultimap.get(previous));
		ArrayList<VariableAssignment> currentVars = new ArrayList(variablesMultimap.get(now));
		
		for (int i = 0; i < previousVars.size(); i++) {
			VariableAssignment prev = previousVars.get(i);
			VariableAssignment curr = currentVars.get(i);
			if (curr.getVariableDefinition() != prev.getVariableDefinition()) {
				throw new AssertionError("Wrong variable from memento.");
			}
			// Only boolean variables
			if (curr.getVariableDefinition().getType() != Type.BOOLEAN) {
				continue;
			}
			if (curr.getValue() != prev.getValue()) {
				Trigger t = triggerFactory.get(curr.getVariableDefinition().getSystemName());
				Transition tr = t.addTransition(now.getSimulationTime(), curr.getValue() == "0");
				transitions.add(tr);
			}
		}
		
	}
	
	/**
	 * @param variablesMultimap
	 */
	public SystemMemento(TreeMultimap<Time, VariableAssignment> variablesMultimap) {
		this.variablesMultimap = TreeMultimap.create(variablesMultimap);
		rolledBackVariablesMultimap = HashMultimap.create();
		transitions = new ArrayList<Transition>();
	}

	/**
	 * 
	 */
	public void deleteRelatedFiles() {
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 */
	public void clear() {
		variablesMultimap.clear();
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean containsEntry(Object key, Object value) {
		return variablesMultimap.containsEntry(key, value);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public boolean containsValue(Object arg0) {
		return variablesMultimap.containsValue(arg0);
	}

	/**
	 * @return
	 */
	public Collection<Entry<Time, VariableAssignment>> entries() {
		return variablesMultimap.entries();
	}

	/**
	 * @param key
	 * @return
	 */
	public Collection<VariableAssignment> get(Time key) {
		return variablesMultimap.get(key);
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return variablesMultimap.isEmpty();
	}

	/**
	 * @return
	 */
	public Set<Time> keySet() {
		return variablesMultimap.keySet();
	}

	/**
	 * @return
	 */
	public Multiset<Time> keys() {
		return variablesMultimap.keys();
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean put(Time key, VariableAssignment value) {
		return variablesMultimap.put(key, value);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean remove(Object key, Object value) {
		return variablesMultimap.remove(key, value);
	}

	/**
	 * @param key
	 * @return
	 */
	public Collection<VariableAssignment> removeAll(Object key) {
		return variablesMultimap.removeAll(key);
	}

	/**
	 * @return
	 */
	public int size() {
		return variablesMultimap.size();
	}

	/**
	 * @return
	 */
	public Collection<VariableAssignment> values() {
		return variablesMultimap.values();
	}

	/**
	 * @return the variablesMultimap
	 */
	public TreeMultimap<Time, VariableAssignment> getVariablesMultimap() {
		return variablesMultimap;
	}
		
	
	/**
	 * Return a variable assignment for the given variable definition.
	 * 
	 * @param def the variable definition
	 * @param time the time of this definition
	 * @return the assignment of the given variable od <code>null</code> if not assigned.
	 */
	public VariableAssignment getVariable(VariableDefinition def, Time time) {
		Collection<VariableAssignment> variables = variablesMultimap.get(time);
		for (VariableAssignment v: variables) {
			// You can use == because they MUST be the same instance
			if (v.getVariableDefinition() == def) {
				return v;
			}
		}
		return null;
	}
	
	public void update(EnvironmentMemento memento) {
		Time time = memento.getTime();
		if (time.getSimulationStep() != getLatestSimulatedTime().getSimulationStep() + 1) {
			throw new RuntimeException("Updating a system memento at time: " + 
					getLatestSimulatedTime() + 
					"  with an environment memento at time: " +
					time);
		}
		
		SortedSet<VariableAssignment> variables = variablesMultimap.get(time);
		if (variables.size() != 0) {
			throw new RuntimeException("Uncompatible mementos: system mento should be empty at this time.");
		}
		
		for (VariableAssignment envVar: memento.getParams()) {
			VariableDefinition def = envVar.getVariableDefinition();
			if (def.getScope() == Scope.ENVIRONMENT_SHARED) {
				variablesMultimap.put(time, envVar.clone());
			}
		}
	}
	
	public void addUnsatConfiguration(Time time, Collection<VariableAssignment> variables) {
		rolledBackVariablesMultimap.put(time, variables);
	}
	
	public Set<Collection<VariableAssignment>> getUnsatConfiguration(Time time) {
		return rolledBackVariablesMultimap.get(time);
	}
	
	public Time getLatestSimulatedTime() {
		return variablesMultimap.keySet().last();
	}

	/**
	 * @return the rolledBackVariablesMultimap
	 */
	public HashMultimap<Time, Collection<VariableAssignment>> getRolledBackVariablesMultimap() {
		return rolledBackVariablesMultimap;
	}

	/**
	 * @param rolledBackVariablesMultimap the rolledBackVariablesMultimap to set
	 */
	public void setRolledBackVariablesMultimap(
			HashMultimap<Time, Collection<VariableAssignment>> rolledBackVariablesMultimap) {
		this.rolledBackVariablesMultimap = HashMultimap.create(rolledBackVariablesMultimap);
	}
	
	public void deleteTransitions() {
		for (Transition t: transitions) {
			t.getTrigger().removeTransition(t);
		}
		transitions.clear();
	}
}
