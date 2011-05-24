/**
 * 
 */
package mades.system;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultimap;

import mades.common.Variable;

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
	private TreeMultimap<Integer, Variable> variablesMultimap;
	
	/**
	 * @param time
	 * @param params
	 */
	public SystemMemento() {
		variablesMultimap = TreeMultimap.create();
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
	public Collection<Entry<Integer, Variable>> entries() {
		return variablesMultimap.entries();
	}

	/**
	 * @param key
	 * @return
	 */
	public Collection<Variable> get(Integer key) {
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
	public Set<Integer> keySet() {
		return variablesMultimap.keySet();
	}

	/**
	 * @return
	 */
	public Multiset<Integer> keys() {
		return variablesMultimap.keys();
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean put(Integer key, Variable value) {
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
	public Collection<Variable> removeAll(Object key) {
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
	public Collection<Variable> values() {
		return variablesMultimap.values();
	}
		
}
