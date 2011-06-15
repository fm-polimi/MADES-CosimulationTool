/**
 * 
 */
package mades.cosimulation;

import java.util.Collection;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.TreeMultimap;

import mades.common.timing.Clock;
import mades.common.timing.Time;
import mades.common.variables.Scope;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableFactory;
import mades.environment.EnvironmentConnector;
import mades.environment.EnvironmentMemento;
import mades.environment.SignalMap;
import mades.environment.modelica.ModelicaEnvironmentConnector;
import mades.system.SystemConnector;
import mades.system.SystemMemento;
import mades.system.zot.ZotSystemConnector;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Defines the main controller for the co-simulation. The cosimulator
 * is responsible for initializing the system and the environment and
 * for supervising the simulation. 
 *
 */
public class Cosimulator {

	private Logger logger;
	
	private boolean simulationStarted;
	
	private Clock clock;
	private VariableFactory variableFactory;
	
	/**
	 * The environment simulator used in this co-simulation.
	 */
	private EnvironmentConnector environment;
	
	/**
	 * The system simulator used in this co-simulation.
	 */
	private SystemConnector system;
	
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
	 * Stores all the simulation variables that have to be returned
	 * at the end of the co-simulation. It only contains shared variables.
	 */
	private TreeMultimap<Time, VariableAssignment> sharedVariablesMultimap;
	
	/**
	 * Default constructor.
	 */
	public Cosimulator(Logger logger) {
		this.logger = logger;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String ENVIRONMENT_PATH = "./examples/RC";
		Logger logger = Logger.getLogger(Cosimulator.class.getName());
		logger.setLevel(Level.ALL);
		logger.info("Starting co-simulation");
		
		SystemConnector system = new ZotSystemConnector(ENVIRONMENT_PATH, 30, logger);
		EnvironmentConnector environment = new ModelicaEnvironmentConnector(ENVIRONMENT_PATH, logger);
		Cosimulator cosimulator = new Cosimulator(logger);
		cosimulator.setEnvironment(environment);
		cosimulator.setSystem(system);
		
		double initialSimulationTime = 0;
		double timeStep = 10;
		double maxCosimulationTime = 60;
		int maxCosimulationAttemptsForStep = 3;
		int maxCosimulationBacktraking = 3;
		
		cosimulator.startCosimulation(
				initialSimulationTime,
				timeStep,
				maxCosimulationTime,
				maxCosimulationAttemptsForStep,
				maxCosimulationBacktraking);
		
		TreeMultimap<Time, VariableAssignment> results = cosimulator.getSharedVariablesMultimap();
		
		StringBuilder builder = new StringBuilder();
		builder.append("Simulation output:\n");
		Set<Time> keys = results.keySet();
		for (Time key: keys) {
			builder.append("\n" + key.toString() + "\n");
			Set<VariableAssignment> vars = results.get(key);
			for(VariableAssignment v: vars) {
				builder.append(v.getVariableDefinition().getName() + " = " + v.getValue() + "\n");
			}
		}
		logger.info(builder.toString());
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
		logger.info("New environment set.");
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
		logger.info("New system set.");
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
	 * Starts a new co-simulation.
	 * 
	 * @param initialSimulationTime the initial time of this simulation.
	 * @param timeStep the time between two co-simulation steps.
	 * @param maxCosimulationTime the time at which the co-simulation has to
	 *         be interrupted.
	 * @param maxCosimulationAttemptsForStep the maximum number of attempts
	 *         at each step.
	 * @param maxCosimulationBacktraking the maximum number of steps back.
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
			int maxCosimulationBacktraking
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
		
		clock = new Clock(logger, timeStep,
				initialSimulationTime, maxCosimulationTime);
		variableFactory = new VariableFactory();
		
		this.maxCosimulationAttemptsForStep = maxCosimulationAttemptsForStep;
		this.maxCosimulationBacktraking = maxCosimulationBacktraking;
		
		reinitializeSimulation(initialSimulationTime);
		
		
		simulationStarted = true;
		logger.info("Starting simulation at time: " + initialSimulationTime);
		
		try {
			// Runs the co-simulation
			while(!clock.hasReachCosimulationEnd()) {
				performCosimulationStep();
			}
		} finally {
			simulationStarted = false;
			logger.info("Simulation ended at time: " + clock.getCurrentTime().getSimulationTime());
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
			double initialSimulationTime) {
		
		if (environmentMementoStack != null) {
			while (!environmentMementoStack.isEmpty()) {
				environmentMementoStack.pop();
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
		
		sharedVariablesMultimap = TreeMultimap.create();
		
		// Add the initial states to the bottom of the stack
		// The system must be initialized first
		SystemMemento systemMemento = system.initialize(clock, variableFactory);
		if (systemMemento == null) {
			String msg = "Unsatisfiable initial configuration: aborting...";
			logger.severe(msg);
			throw new AssertionError(msg);
		}
		systemMementoStack.push(systemMemento);
		environmentMementoStack.push(
				environment.initialize(clock,
						variableFactory));
		
		
	}
	
	protected void performCosimulationStep() {
		int environmentBacktrakingAttemptsLeft = maxCosimulationBacktraking;
		int systemBacktrakingAttemptsLeft = maxCosimulationBacktraking;
		
		boolean systemStepApproved = false;
		
		while (!systemStepApproved) {
			boolean environmentStepApproved = false;
			
			while (!environmentStepApproved) {
				systemBacktrakingAttemptsLeft -= 1;
				if (systemBacktrakingAttemptsLeft < 0) {
					logger.severe(
							"Max co-simulation backtraking attempts " +
							"reached: aborting...");
					throw new RuntimeException(
							"Max co-simulation backtraking attempts reached.");
				}
	
				int attemptsInStep = maxCosimulationAttemptsForStep;
				
				while (!environmentStepApproved) {
					assert(environmentMementoStack.size() == 
						    systemMementoStack.size());
					
					environmentBacktrakingAttemptsLeft -= 1;
					if (environmentBacktrakingAttemptsLeft < 0) {
						logger.severe(
								"Max co-simulation backtraking attempts " +
								"reached: aborting...");
						throw new RuntimeException(
								"Max co-simulation backtraking attempts reached.");
					}
					
					clock.tickForward();
					// Simulate the environment at the next time.
					simulateEnvironment();
					
					// If the environment is not valid then we need to
					// re-simulate the system at the previous time, then the
					// environment again.
					if (!isLastEnvironmentSimulationValid()) {
						logger.severe("Last simulated system state is " +
								"not valid.");
						rollbackEnvironment();
						attemptsInStep -= 1;
	
						// Roll back the environment and try again.
						clock.tickBackward();
						rollbackSystem();
						try {
							simulateSystem();
						} catch(AssertionError err) {
							break;
						}
						
						if (attemptsInStep < 0) {
							logger.severe(
								"Max co-simulation attempts at this step " +
								"reached: backtracking...");
							break;
						}
					} else {
						environmentStepApproved = true;
					}
				}
				
				if (!environmentStepApproved) {
					// Roll back the system and the simulation time
					rollbackEnvironment();
					clock.tickBackward();
					try {
						simulateSystem();
					}
					catch(AssertionError err) {
						String msg = "Unsatisfiable configuration during backtraking: aborting.";
						logger.severe(msg);
						throw err;
					}
				}
			}
			
			// Simulate the environment at the next time
			try {
				simulateSystem();
				systemStepApproved = true;
			} catch(AssertionError err) {
				// Roll back the system and the simulation time
				rollbackEnvironment();
				clock.tickBackward();
				rollbackSystem();
			}
			
		}
		//Remove from the two stacks elements earlier than maxCosimulationBacktraking
		deleteObsoleteData();
	}

	
	/**
	 * Removes from the stacks all the {@link Memento} which represent an
	 * iteration which occurred earlier than 
	 * {@link Cosimulator.maxCosimulationBacktraking}.
	 */
	protected void deleteObsoleteData() {
		int elementsToremove = environmentMementoStack.size() -
				maxCosimulationBacktraking;
		for (int i = elementsToremove; i > 0; i--) {
			environmentMementoStack.remove(0);
		}
		logger.info("Obsolete environment state deleted.");
		
		elementsToremove = systemMementoStack.size() -
				maxCosimulationBacktraking;
		for (int i = elementsToremove; i > 0; i--) {
			SystemMemento memento = systemMementoStack.remove(0);
			// TODO(rax): assert is the right memento
			memento.deleteRelatedFiles();
		}
		logger.info("Obsolete system state deleted.");
	}
	
	protected void rollbackEnvironment() {
		assert(simulationStarted);
		logger.info("Rolling back environment state.");
		if (environmentMementoStack.size() == 1) {
			logger.severe("End of environment state stack reached.");
			throw new RuntimeException(
					"Cannot rollback initial environment state: aborting...");
		}
		EnvironmentMemento memento = environmentMementoStack.pop();
		assert(memento.getTime() == clock.getCurrentTime());
		
		// Remove shared variables
		for (VariableAssignment v: memento.getParams()) {
			sharedVariablesMultimap.remove(memento.getTime(), v);
		}
	}
	
	protected void rollbackSystem() {
		assert(simulationStarted);
		logger.info("Rolling back system state.");
		if (systemMementoStack.size() == 1) {
			logger.severe("End of system state stack reached.");
			throw new RuntimeException("Cannot rollback initial system state: aborting...");
		}
		SystemMemento discardedMemento = systemMementoStack.pop();
		// TODO(rax): assert is the right memento
		
		// Remove shared variables
		Collection<VariableAssignment> variables = 
				discardedMemento.get(clock.getCurrentTime());
		for (VariableAssignment v: variables) {
			sharedVariablesMultimap.remove(clock.getCurrentTime(), v);
		}
		
		// Add the unsat configuration to the current memento
		SystemMemento currentMemento = systemMementoStack.peek();
		currentMemento.addUnsatConfiguration(clock.getCurrentTime(), variables);
	}
	
	protected void simulateEnvironment() {
		assert(simulationStarted);
		logger.info("Symulating environment at time: " + clock.getCurrentTime().getSimulationTime());
		environment.load(environmentMementoStack.peek(), systemMementoStack.peek());
		
		EnvironmentMemento environmentMemento =
			    environment.simulateNext();
		
		// Add memento on top of the stack
		environmentMementoStack.push(environmentMemento);
		
		// Add variables to shared variables map
		for (VariableAssignment var: environmentMemento.getParams()) 
		{
			if (var.getVariableDefinition().getScope() == Scope.ENVIRONMENT_SHARED) {
				sharedVariablesMultimap.put(clock.getCurrentTime(), var);
			}
		}
	}
	
	protected void simulateSystem() {
		assert(simulationStarted);
		logger.info("Simulating system at step: " + clock.getCurrentTime().getSimulationStep());
		system.load( systemMementoStack.peek(), environmentMementoStack.peek());
		
		SystemMemento systemMemento = system.simulateNext();
		
		if (systemMemento == null) {
			String msg = "Unsatisfieable configuration.";
			logger.severe(msg);
			throw new AssertionError(msg);
		}
		
		// Add memento to the top of the stack
		systemMementoStack.push(systemMemento);
		
		// Add shared variables to the variable map
		Collection<VariableAssignment> vars = systemMemento.get(clock.getCurrentTime());
		for (VariableAssignment var: vars) 
		{
			if (var.getVariableDefinition().getScope() == Scope.SYSTEM_SHARED) {
				sharedVariablesMultimap.put(clock.getCurrentTime(), var);
			}
		}
	}
	
	boolean isLastEnvironmentSimulationValid() {
		EnvironmentMemento environmentMemento = environmentMementoStack.peek();
		SignalMap signals = environmentMemento.getSignals();
		return signals.validate(clock.getTimeStep());
	}

	/**
	 * @return the sharedVariablesMultimap
	 */
	public TreeMultimap<Time, VariableAssignment> getSharedVariablesMultimap() {
		return sharedVariablesMultimap;
	}
	
}
