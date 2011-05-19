/**
 * 
 */
package mades.cosimulation;

import java.util.Stack;
import java.util.logging.Logger;

import mades.common.Memento;
import mades.common.ParamMap;
import mades.environment.EnvironmentConnector;
import mades.environment.EnvironmentMemento;
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

	Logger logger = Logger.getLogger(this.getClass().getName());
	
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
	private Stack<EnvironmentMemento> environmentMementoStack;
	private Stack<SystemMemento> systemMementoStack;
	
	
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
	 * @param environment the environment to set.
	 * @throws IllegalStateException if the simulation is running.
	 */
	public void setEnvironment(EnvironmentConnector environment) {
		if (simulationStarted) {
			throw new IllegalStateException(
					"Cannot set a new environment connector while the " +
					"simulation is running.");
		}
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
	 * @param system the system to set.
	 * @throws IllegalStateException if the simulation is running.
	 */
	public void setSystem(SystemConnector system) {
		if (simulationStarted) {
			throw new IllegalStateException(
					"Cannot set a new system connector while the " +
					"simulation is running.");
		}
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
		
		reinitializeSimulation(initialSimulationTime,
				environmentParams, systemParams);
		
		
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

	/**
	 * Cleans the internal stacks and initializes the first state.
	 * 
	 * @param initialSimulationTime
	 * @param environmentParams
	 * @param systemParams
	 */
	private void reinitializeSimulation(
			double initialSimulationTime, 
			ParamMap environmentParams,
			ParamMap systemParams) {
		
		if (environmentMementoStack != null) {
			while (!environmentMementoStack.isEmpty()) {
				EnvironmentMemento memento = environmentMementoStack.pop();
				memento.deleteRelatedFiles();
			}
			assert(environmentMementoStack.isEmpty());
		} else {
			environmentMementoStack = new Stack<EnvironmentMemento>();
		}
		
		if (systemMementoStack != null) {
			while (!systemMementoStack.isEmpty()) {
				SystemMemento memento = systemMementoStack.pop();
				memento.deleteRelatedFiles();
			}
			assert(systemMementoStack.isEmpty());
		} else {
			systemMementoStack = new Stack<SystemMemento>();
		}
		
		// Add the initial states to the bottom of the stack
		environmentMementoStack.push(
				environment.initialize(environmentParams, initialSimulationTime));
		systemMementoStack.push(
				system.initialize(systemParams, initialSimulationTime));
		
	}
	
	protected void performCosimulationStep() {
		double nextSimulationTime = lastAcceptedCosimulationTime + timeStep;
		
		int backtrakingAttemptsLeft = maxCosimulationBacktraking;
		boolean stepApproved = false;
		
		while (!stepApproved) {
			int attemptsInStep = maxCosimulationAttemptsForStep;
			
			while (!stepApproved) {
				// Simulate the system at the next time.
				simulateSystem(nextSimulationTime);
				
				if (!isLastSystemSimulationValid()) {
					logger.severe("New simulated system state is not valid.");
					rollbackSystem();
					attemptsInStep -= 1;

					// Roll back the environment and try again.
					rollbackEnvironment();
					simulateEnvironment(lastAcceptedCosimulationTime);
					
					if (attemptsInStep < 0) {
						logger.severe(
							"Max co-simulation attempts at this step reached: backtracking...");
						break;
					}
				} else {
					stepApproved = true;
				}
			}
			
			if (stepApproved) {
				break;
			} else {
				// Roll back to previous system state
				backtrakingAttemptsLeft -= 1;
				if (backtrakingAttemptsLeft < 0) {
					logger.severe(
							"Max co-simulation backtraking attempts reached: aborting...");
					throw new RuntimeException(
							"Max co-simulation backtraking attempts reached.");
				} else {
					// Roll back the system and the simulation time
					rollbackSystem();
					nextSimulationTime = lastAcceptedCosimulationTime;
					lastAcceptedCosimulationTime -= timeStep;
					logger.severe("Simulation time decreased to: " +
							lastAcceptedCosimulationTime);
					simulateSystem(lastAcceptedCosimulationTime);
				}
			}
			

		}
		
		// Simulate the environment at the next time
		simulateEnvironment(nextSimulationTime);
		lastAcceptedCosimulationTime = nextSimulationTime;
		logger.severe("Simulation time increased to: " +
				lastAcceptedCosimulationTime);
		//Remove from the two stacks elements earlier than maxCosimulationBacktraking
		deleteObsoleteData();
	}
	
	/**
	 * Removes from the stacks all the {@link Memento} which represent an
	 * iteration which occurred earlier than 
	 * {@link Cosimulator.maxCosimulationBacktraking}
	 */
	protected void deleteObsoleteData() {
		int elementsToremove = environmentMementoStack.size() -
				maxCosimulationBacktraking;
		for (int i = elementsToremove; i > 0; i--) {
			EnvironmentMemento memento = environmentMementoStack.remove(0);
			assert(memento.getTime() <
					(lastAcceptedCosimulationTime - 
							(maxCosimulationBacktraking * timeStep)));
			memento.deleteRelatedFiles();
		}
		logger.severe("Obsolete environment state deleted.");
		
		elementsToremove = systemMementoStack.size() -
				maxCosimulationBacktraking;
		for (int i = elementsToremove; i > 0; i--) {
			SystemMemento memento = systemMementoStack.remove(0);
			assert(memento.getTime() <
					(lastAcceptedCosimulationTime - 
							(maxCosimulationBacktraking * timeStep)));
			memento.deleteRelatedFiles();
		}
		logger.severe("Obsolete system state deleted.");
	}
	
	protected void rollbackEnvironment() {
		assert(simulationStarted);
		logger.severe("Rolling back environment state.");
		if (environmentMementoStack.size() == 1) {
			logger.severe("End of environment state stack reached.");
			throw new RuntimeException(
					"Cannot rollback initial environment state: aborting...");
		}
		EnvironmentMemento memento = environmentMementoStack.pop();
		assert(memento.getTime() == lastAcceptedCosimulationTime);
	}
	
	protected void rollbackSystem() {
		assert(simulationStarted);
		logger.severe("Rolling back system state.");
		if (systemMementoStack.size() == 1) {
			logger.severe("End of system state stack reached.");
			throw new RuntimeException("Cannot rollback initial system state: aborting...");
		}
		SystemMemento memento = systemMementoStack.pop();
		assert(memento.getTime() == lastAcceptedCosimulationTime);
	}
	
	protected void simulateEnvironment(double time) {
		assert(simulationStarted);
		logger.severe("Symulating environment at time: " + time);
		environment.load(environmentMementoStack.peek(), systemMementoStack.peek());
		environmentMementoStack.push(environment.simulateNext(time));
	}
	
	protected void simulateSystem(double time) {
		assert(simulationStarted);
		logger.severe("Symulating system at time: " + time);
		system.load( systemMementoStack.peek(), environmentMementoStack.peek());
		systemMementoStack.push(system.simulateNext(time));
	}
	
	boolean isLastSystemSimulationValid() {
		SystemMemento systemMemento = systemMementoStack.peek();
		return true;
	}
	
}
