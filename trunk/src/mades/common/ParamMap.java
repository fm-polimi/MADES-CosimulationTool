/**
 * 
 */
package mades.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Manages a collection of simulation parameters and offers a set
 * of access methods for quick retrieval.
 */
public class ParamMap {

	HashMap<String, Object> params = new HashMap<String, Object>();

	/**
	 * Initialize this class with a set of existing parameters.
	 * 
	 * @param params the initial collection of parameters.
	 */
	public ParamMap(HashMap<String, Object> params) {
		super();
		this.params = (HashMap<String, Object>) params.clone();
	}
	
	/**
	 * Constructor by copy.
	 * 
	 * @param map the object to copy.
	 */
	public ParamMap(ParamMap map) {
		this.params = (HashMap<String, Object>) map.params.clone();
	}

	/**
	 * 
	 * @see java.util.HashMap#clear()
	 */
	public void clear() {
		params.clear();
	}

	/**
	 * @return
	 * @see java.util.HashMap#clone()
	 */
	public ParamMap clone() {
		return new ParamMap(params);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.HashMap#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return params.containsKey(key);
	}

	/**
	 * @param value
	 * @return
	 * @see java.util.HashMap#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return params.containsValue(value);
	}

	/**
	 * @return
	 * @see java.util.HashMap#entrySet()
	 */
	public Set<Entry<String, Object>> entrySet() {
		return params.entrySet();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.AbstractMap#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return params.equals(o);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	public Object get(Object key) {
		return params.get(key);
	}

	/**
	 * @return
	 * @see java.util.HashMap#isEmpty()
	 */
	public boolean isEmpty() {
		return params.isEmpty();
	}

	/**
	 * @return
	 * @see java.util.HashMap#keySet()
	 */
	public Set<String> keySet() {
		return params.keySet();
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(String key, Object value) {
		return params.put(key, value);
	}

	/**
	 * @param m
	 * @see java.util.HashMap#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends String, ? extends Object> m) {
		params.putAll(m);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.HashMap#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		return params.remove(key);
	}

	/**
	 * @return
	 * @see java.util.HashMap#size()
	 */
	public int size() {
		return params.size();
	}

	/**
	 * @return
	 * @see java.util.AbstractMap#toString()
	 */
	public String toString() {
		return params.toString();
	}

	/**
	 * @return
	 * @see java.util.HashMap#values()
	 */
	public Collection<Object> values() {
		return params.values();
	}
}
