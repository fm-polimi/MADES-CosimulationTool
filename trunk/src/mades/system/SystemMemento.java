/**
 * 
 */
package mades.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultimap;

import mades.common.timing.Time;
import mades.common.variables.Scope;
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


	/**
	 * 
	 */
	private TreeMultimap<Time, VariableAssignment> variablesMultimap;
	
	private TreeMultimap<Time, Collection<VariableAssignment>> rolledBackVariablesMultimap;
	
	/**
	 * @param time
	 * @param params
	 */
	public SystemMemento() {
		variablesMultimap = TreeMultimap.create();
	}

	/**
	 * @param variablesMultimap
	 */
	public SystemMemento(TreeMultimap<Time, VariableAssignment> variablesMultimap) {
		this.variablesMultimap = TreeMultimap.create(variablesMultimap);
		rolledBackVariablesMultimap = TreeMultimap.create();
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
		SortedSet<VariableAssignment> variables = variablesMultimap.get(time);
		if (variables == null) {
			return;
		}
		
		for (VariableAssignment systemVar: variables) {
			VariableDefinition def = systemVar.getVariableDefinition();
			if (def.getScope() == Scope.ENVIRONMENT_SHARED) {
				systemVar.setValue(memento.getVariable(def).getValue());
			}
		}
	}
	
	public void addUnsatConfiguration(Time time, Collection<VariableAssignment> variables) {
		rolledBackVariablesMultimap.put(time, variables);
	}
	
	public SortedSet<Collection<VariableAssignment>> getUnsatConfiguration(Time time) {
		return rolledBackVariablesMultimap.get(time);
	}
}
