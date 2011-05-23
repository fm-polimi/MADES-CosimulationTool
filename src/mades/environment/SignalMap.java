/**
 * 
 */
package mades.environment;

import java.util.*;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Stores all the signals raised by the system.
 */
public class SignalMap {

	HashMap<String, ArrayList<Double>> signals = new HashMap<String, ArrayList<Double>>();
	
	/**
	 * Checks if the system is nonberkeleyan.
	 * For each signal the distance between two trigger times must be greated than
	 * a given delta time, which would be the simulation ste time.
	 * 
	 * @param minSignalDelta the delta time.
	 * @param startTime the minimum time in which signals have to be checked.
	 * @param endTime the maximum time in which the signals have to be checked.
	 * @return <code>true</code> if the signals are correct, 
	 *         <code>false</code> otherwise.
	 */
	public boolean validate(double minSignalDelta, double startTime, double endTime) {
		Set<String> keys = signals.keySet();
		for (String key: keys) {
			ArrayList<Double> signal = signals.get(key);
			for (int i = signal.size(); i > 1; i--) {
				double end = signal.get(i);
				double begin = signal.get(i - 1);
				if (begin < startTime) {
					// skip the remaining values because too old
					break;
				}
				if (end > end) {
					// skip this value because too recent
					continue;
				}
				if ((end - begin) < minSignalDelta) {
					return false;
				}
			}
		}
		return true;
	}
}
