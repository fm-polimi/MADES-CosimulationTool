/**
 * 
 */
package mades.common.timing;

import java.util.logging.Logger;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Handles the cosimulation time.
 *
 */
public class Clock {

	private Logger logger;
	
	private Time currentTime;
	private double timeStep;
	private double finalTime;
	
	private TimeFactory factory = new TimeFactory();
	
	public Clock(Logger logger, double timeStep, double initialTime, double finalTime) {
		this.logger = logger;
		this.timeStep = timeStep;
		this.finalTime = finalTime;
		currentTime = factory.create(initialTime, 0);
	}

	/**
	 * @return the currentTime
	 */
	public Time getCurrentTime() {
		return currentTime;
	}
	
	public boolean hasReachCosimulationEnd() {
		return currentTime.getSimulationTime() > finalTime;
	}
	
	public Time tickForward() {
		currentTime = factory.create(
				currentTime.getSimulationTime() + timeStep,
				currentTime.getSimulationStep() + 1);
		
		logger.fine("Simulation time increased to: " +
				currentTime.getSimulationTime());
		logger.fine("Simulation steps increased to: " +
				currentTime.getSimulationStep());
		
		return currentTime;
	}
	
	public Time tickBackward() {
		currentTime = factory.create(
				currentTime.getSimulationTime() - timeStep,
				currentTime.getSimulationStep() - 1);
		
		logger.fine("Simulation time decreased to: " +
				currentTime.getSimulationTime());
		logger.fine("Simulation steps decreased to: " +
				currentTime.getSimulationStep());
		
		return currentTime;
	}

	/**
	 * @return the factory
	 */
	public TimeFactory getFactory() {
		return factory;
	}

	/**
	 * @return the timeStep
	 */
	public double getTimeStep() {
		return timeStep;
	}
	
}
