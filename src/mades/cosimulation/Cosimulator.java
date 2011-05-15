/**
 * 
 */
package mades.cosimulation;

import java.util.HashMap;

import mades.common.ParamMap;
import mades.environment.EnvironmentConnector;
import mades.environment.EnvironmentMemento;
import mades.system.SignalMap;
import mades.system.SystemConnector;
import mades.system.SystemMemento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Defines the main controller for the co-simulation. The cosimulator
 * is responsible for initializing the system and the environment and
 * for supervising the simulation. 
 *
 */
public class Cosimulator {

	private boolean simulationStarted;
	
	/**
	 * The last accepted co-simulation time.
	 */
	private double lastAcceptedCosimulationTime;
	
	/**
	 * The initial time of this co-simulation.
	 */
	private double initialSimulationTime;
	
	/**
	 * The final time of this co-simulation.
	 */
	private double maxCosimulationTime;
	
	/**
	 * The environment simulator used in this co-simulation.
	 */
	private EnvironmentConnector environment;
	
	/**
	 * The system simulator used in this co-simulation.
	 */
	private SystemConnector system;
	
	/**
	 * The time step of each simulation step.
	 */
	private double timeStep;
	
	/**
	 * The maximum number of attempts at each simulation step.
	 * If the maximum number of attempts is reached the co-simulation
	 * will roll back of a step. 
	 */
	private int maxCosimulationAttemptsForStep = 3;
	
	/**
	 * The maximum number of steps that the co-simulation
	 * will roll back before aborting.
	 */
	private int maxCosimulationBacktraking = 3;
	
	/**
	 * Default constructor.
	 */
	public Cosimulator() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	/**
	 * Gets the current {@link EnvironmentConnector}.
	 * 
	 * @return the environment
	 */
	public EnvironmentConnector getEnvironment() {
		return environment;
	}

	/**
	 * Sets a new {@link EnvironmentConnector}.
	 * 
	 * @param environment the environment to set
	 */
	public void setEnvironment(EnvironmentConnector environment) {
		this.environment = environment;
	}

	/**
	 * Gets the current {@link SystemConnector}.
	 * 
	 * @return the system
	 */
	public SystemConnector getSystem() {
		return system;
	}

	/**
	 * Sets a new {@link SystemConnector}.
	 * 
	 * @param system the system to set
	 */
	public void setSystem(SystemConnector system) {
		this.system = system;
	}
	
	/**
	 * Gets if the co-simulation is running.
	 * 
	 * @return <code>true</code> if the co-simulation is already running,
	 *         <code>false</code> otherwise.
	 */
	public boolean isSimulationStarted() {
		return simulationStarted;
	}

	/**
	 * Gets the current simulation time.
	 * 
	 * @return the simulationTime
	 */
	public double getSimulationTime() {
		return lastAcceptedCosimulationTime;
	}

	/**
	 * Starts a new co-simulation.
	 * 
	 * @param initialSimulationTime the initial time of this simulation.
	 * @param timeStep the time between two co-simulation steps.
	 * @param maxCosimulationTime the time at which the co-simulation has to
	 *         be interrupted.
	 * @param maxCosimulationAttemptsForStep the maximum number of attempts
	 *         at each step.
	 * @param maxCosimulationBacktraking the maximum number of steps back.
	 * @param environmentParams the initial configuration for the 
	 *         {@link EnvironmentConnector}.
	 * @param systemParams the initial configuration for the 
	 *         {@link SystemConnector}. 
	 * 
	 * @throws IllegalStateException if the co-simulation is already running.
	 * @throws AssertionError if either the {@link EnvironmentConnector} or the
	 *         {@link SystemConnector} are <code>null</code> or not properly
	 *         initialized.
	 * @throws MaxCosimulationAttemptsReached if the co-simulation has reached
	 *         the maximum number of retry without finding a suitable state.
	 */
	public void startCosimulation(
			double initialSimulationTime,
			double timeStep,
			double maxCosimulationTime,
			int maxCosimulationAttemptsForStep,
			int maxCosimulationBacktraking,
			ParamMap environmentParams,
			ParamMap systemParams
			) {
		if (simulationStarted) {
			throw new IllegalStateException("Simulation is already running"); 
		}
		if (environment == null) {
			throw new AssertionError("EnvironmentConnector cannot be null.");
		}
		if (system == null) {
			throw new AssertionError("SystemConnector cannot be null.");
		}
		
		this.initialSimulationTime = initialSimulationTime;
		lastAcceptedCosimulationTime = this.initialSimulationTime;
		this.maxCosimulationTime = maxCosimulationTime;
		this.timeStep = timeStep;
		this.maxCosimulationAttemptsForStep = maxCosimulationAttemptsForStep;
		this.maxCosimulationBacktraking = maxCosimulationBacktraking;
		
		environment.initialize(environmentParams, initialSimulationTime);
		system.initialize(systemParams, initialSimulationTime);
		
		simulationStarted = true;
		
		try {
			// Runs the co-simulation
			while(lastAcceptedCosimulationTime < this.maxCosimulationTime) {
				performCosimulationStep();
			}
		} finally {
			simulationStarted = false;
		}
	}

	protected void performCosimulationStep() {
		double nextSimulationTime = lastAcceptedCosimulationTime + timeStep;
		SystemMemento currentSystemParams = system.getCurrentParams();
		SystemMemento nextSystemParams = null;
		EnvironmentMemento currentEnvironmentParams = environment.getCurrentParams();
		EnvironmentMemento nextEnvironmentParams = currentEnvironmentParams;
	
		boolean stepApproved = false;
		int attempts = maxCosimulationAttemptsForStep;
		
		while (!stepApproved) {
			system.load(currentSystemParams, nextEnvironmentParams);
			nextSystemParams = system.simulateNext(nextSimulationTime);
			
			if (!acceptSystemStep(nextSystemParams)) {
				attempts -= 1;
				if (attempts == 0) {
					throw new MaxCosimulationAttemptsReached();
				}
				environment.load(currentEnvironmentParams, nextSystemParams);
				nextEnvironmentParams = environment.simulateNext(lastAcceptedCosimulationTime);
			} else {
				stepApproved = true;
			}
		}
		
		environment.load(currentEnvironmentParams, nextSystemParams);
		nextEnvironmentParams = environment.simulateNext(nextSimulationTime);
		lastAcceptedCosimulationTime = nextSimulationTime;
	}
	
	private boolean acceptSystemStep(SystemMemento nextSystemParams) {
		return true;
	}
	
}
