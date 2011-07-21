/**
 * 
 */
package mades.common.timing;

import java.util.WeakHashMap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Factory class for {@link Time}.
 */
public class TimeFactory {

	private WeakHashMap<Double, Time> times =
			new WeakHashMap<Double, Time>();
	
	private WeakHashMap<Integer, Time> steps =
			new WeakHashMap<Integer, Time>();
	
	/**
	 * Get the {@link Time} corresponding to the given time.
	 * 
	 * @param time the time.
	 * @return the simulation time corresponding to the given time.
	 */
	public Time get(double time) {
		return times.get(time);
	}
	
	/**
	 * Get the {@link Time} corresponding to the given step.
	 * 
	 * @param step the simulation step.
	 * @return the simulation time corresponding to the given step.
	 */
	public Time get(int step) {
		return steps.get(step);
	}
	
	/**
	 * Creates a new simulation {@link Time};
	 * 
	 * @param time
	 * @param step
	 * @return
	 */
	public Time create(double time, int step) {
		Time t;
		if (times.containsKey(time)) {
			return times.get(time);
		} else if (steps.containsKey(step)) {
			return steps.get(step);
		}
		t = new Time(time, step);
		times.put(time, t);
		steps.put(step, t);
		return t;
	}
	
}

