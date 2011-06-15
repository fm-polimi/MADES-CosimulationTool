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
	 * @return <code>true</code> if the signals are correct, 
	 *         <code>false</code> otherwise.
	 */
	public boolean validate(double minSignalDelta) {
		Set<String> keys = signals.keySet();
		for (String key: keys) {
			ArrayList<Double> signal = signals.get(key);
			for (int i = signal.size() - 1; i > 1; i--) {
				double end = signal.get(i);
				double begin = signal.get(i - 1);
				
				if ((end - begin) < minSignalDelta) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param name
	 * @param time
	 * @return
	 */
	public ArrayList<Double> put(String name, Double time) {
		ArrayList<Double> valuesArrayList = signals.get(name);
		if (valuesArrayList == null) {
			valuesArrayList = new ArrayList<Double>();
			signals.put(name, valuesArrayList);
		}
		valuesArrayList.add(time);
		return valuesArrayList;
	}
}
