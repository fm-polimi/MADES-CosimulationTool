/**
 * 
 */
package mades.common.variables;

import java.util.ArrayList;
import java.util.Collections;

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
	
	/**
	 * Validate this {@link TriggerGroup} for a given time.
	 * 
	 * The algorithm to check whether the group does not satisfy the
	 * non-berkeleyness condition is the following:
	 * <ol>
	 * <li> take the times of all "last transitions" for each trigger
	 * in the group and put them in an array.
	 * <li> sort the array.
	 * <li> take the minimum distance between these times.
	 * 
	 * @param delta the minimum time delta between two transitions.
	 * 		Transitions closer than this delta are not valid.
	 * @param tolerance the minimum threshold under which two transitions
	 * 		are considered as simultaneous and therefore valid.
	 * @return <code>true</code> if the transitions in this {@link TriggerGroup}
	 * 		are valid,, <code>false</code> otherwise.
	 */
	public boolean validate(double delta, double tolerance) {
		// Check single values
		for (Trigger v: triggers) {
			if (!v.validate(delta)) {
				return false;
			}
		}
		
		// Only groups with 2 or more triggers should do that.
		if (triggers.size() < 2) {
			return true;
		}
		
		double min = Double.MAX_VALUE;
		
		// Step 1: create a collection with all the last transitions
		ArrayList<Double> times = new ArrayList<Double>();
		for (Trigger v: triggers) {
		    Transition vt = v.getLatestTransition();
		    if (vt == null) { 
		    	// This trigger has no transition, yet.
		    	continue; 
		    }
		    times.add(vt.getTime());
		}
		
		// If there is only 1 transition or less, then by definition
		// non-berkeleyness is not violated if the transition time
		// is bigger than delta.
		if (times.size() == 0) {
			return true;
		} else if (times.size() == 1) {
			return times.get(0) > delta;
		}
		 
		// Step 2
		Collections.sort(times);
		for (int i = 1; i < times.size(); i++){
			double distance = times.get(i) - times.get(i - 1);
			// Events which are closer in time than the tolerance are
			// considered as happened as the same time and discarded.
			if (distance > tolerance) {
				min = (distance < min) ? distance : min;
			}
		}

		return min > delta;
	}
}
