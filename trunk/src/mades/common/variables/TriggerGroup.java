/**
 * 
 */
package mades.common.variables;

import java.util.ArrayList;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class TriggerGroup {
	
	private ArrayList<Trigger> triggers = new ArrayList<Trigger>();

	/**
	 * @param e
	 * @return
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	public boolean add(Trigger e) {
		return triggers.add(e);
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.ArrayList#get(int)
	 */
	public Trigger get(int index) {
		return triggers.get(index);
	}

	/**
	 * @return
	 * @see java.util.ArrayList#size()
	 */
	public int size() {
		return triggers.size();
	}
	
	
}
