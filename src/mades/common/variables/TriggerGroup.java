/**
 * 
 */
package mades.common.variables;

import java.util.ArrayList;

import mades.common.timing.Clock;

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
	
	public boolean validate(double delta, double tolerance, double oldestTime) {
		// Check single values
		for (Trigger v: triggers) {
			if (!v.validate(delta, oldestTime)) {
				return false;
			}
		}
		
		// Only groups with 2 or more triggers should do that.
		if (triggers.size() < 2) {
			return true;
		}
		
		// Check groups
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (Trigger v: triggers) {
			double t = v.getLatestTransition().getTime();
			min = (t < min) ? t : min;
			max = (t > max) ? t : max;
		} 
		double distance = (max - min);
		if (distance < delta && distance > tolerance) {
			return false;
		}
		
		
		return true;
	}
}
