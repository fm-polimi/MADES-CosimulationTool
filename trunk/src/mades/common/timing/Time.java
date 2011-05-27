/**
 * 
 */
package mades.common.timing;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Defines the internal simulation time which is defined by
 * a continuous and a discrete time.
 */
public class Time implements Comparable<Time> {
	private double simulationTime;
	private int simulationStep;
	
	/**
	 * @param simulationTime
	 * @param simulationStep
	 */
	protected Time(double simulationTime, int simulationStep) {
		this.simulationTime = simulationTime;
		this.simulationStep = simulationStep;
	}

	/**
	 * @return the simulationTime
	 */
	public double getSimulationTime() {
		return simulationTime;
	}

	/**
	 * @return the simulationStep
	 */
	public int getSimulationStep() {
		return simulationStep;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Cosimulation time: " +
				simulationTime + " step: " + simulationStep + ".";
	}

	@Override
	public int compareTo(Time t) {
		if (simulationStep < t.simulationStep) {
			return -1;
		} else if (simulationStep > t.simulationStep) {
			return 1;
		} else {
			return 0;
		}
	}
	
	
	
}
