/**
 * 
 */
package mades.cosimulation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.TreeMultimap;

import mades.common.timing.Clock;
import mades.common.timing.Time;
import mades.common.variables.Scope;
import mades.common.variables.TriggerGroup;
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

	public static final String SIMULATION_STEP_DONE = "Simulation step done";

	private Logger logger;
	
	private boolean simulationStarted = false;
    private boolean cosimulationStopped = false;
	
	private Clock clock;
	private VariableFactory variableFactory;
	
	private PropertyChangeSupport propertyChangeSupport;
	
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

	//private String environmentFileName;

	//private String initFileName;

	private ArrayList<TriggerGroup> triggerGroups;
	
	/**
	 * Default constructor.
	 */
	public Cosimulator(Logger logger) {
		this.logger = logger;
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
        
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path;
		if (args.length < 0) {
			path = args[0];
		} else {
			path = "./examples/RC/mades.xml";
		}
		Logger logger = Logger.getLogger(Cosimulator.class.getName());
		logger.setLevel(Level.ALL);
		logger.info("Starting co-simulation");
		
		SystemConnector system = new ZotSystemConnector(logger);
		EnvironmentConnector environment = 
                        new ModelicaEnvironmentConnector(logger);
		Cosimulator cosimulator = new Cosimulator(logger);
		cosimulator.setEnvironment(environment);
		cosimulator.setSystem(system);
		
		double timeStep = 1;
		double maxCosimulationTime = 20;
		int maxCosimulationAttemptsForStep = 3;
		int maxCosimulationBacktraking = 3;
		
		cosimulator.startCosimulation(
				path,
				timeStep,
				maxCosimulationTime,
				maxCosimulationAttemptsForStep,
				maxCosimulationBacktraking);
		
		OutputWriter writer = cosimulator.createOutputWriter();
		String output = path + File.separator + "madesOutput.xml";
		writer.writeXmlFile(output);
		logger.info("Results written on: " + output);
	}
	
	
	public OutputWriter createOutputWriter() {
		return new OutputWriter(variableFactory, sharedVariablesMultimap);
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
	 * Sets a new {@link EnvironmentConnector}
	 * 
	 * @param environment the environment to set.
	 * @throws IllegalStateException if the simulation is running.
	 */
	public void setEnvironment(EnvironmentConnector environment) {
		if (simulationStarted) {
			throw new IllegalStateException(
				"Cannot set a new environment connector " +
                                "while the simulation is running.");
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
			String modelFileName,
			double timeStep,
			double maxCosimulationTime,
			int maxCosimulationAttemptsForStep,
			int maxCosimulationBacktraking
			) {
		double initialSimulationTime = 0;
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
		
		reinitializeSimulation(modelFileName);
		
		
		simulationStarted = true;
                cosimulationStopped = false;
		logger.info("Starting simulation at time: " +
                        initialSimulationTime);
		
		try {
			// Runs the co-simulation
			while(!clock.hasReachCosimulationEnd() && !cosimulationStopped) {
				performCosimulationStep(); 
				propertyChangeSupport.firePropertyChange(
						SIMULATION_STEP_DONE, null, sharedVariablesMultimap);
			}
			logger.info("Simulation ended at time: " +
                                clock.getCurrentTime().getSimulationTime());
		}  catch (Error err) {
			logger.severe("Simulation ended with error: " +
                                err.getMessage() + " at time: " + 
                                clock.getCurrentTime().getSimulationTime());
			throw err;
		} finally {
			simulationStarted = false;
                        cosimulationStopped = true;
		}
	}
	
        
        public void stopCosimulation() {
            logger.info("Simulation stopped by user.");
            cosimulationStopped = true;
        }
        
	
	/**
	 * Cleans the internal stacks and initializes the first state.
	 * 
	 * @param modelFileName
	 */
	private void reinitializeSimulation(
			String modelFileName
			) {
		
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
		
		InputParser inputParser = new InputParser(logger, clock, 
                        variableFactory, modelFileName);
		inputParser.parseDocument();
		triggerGroups = inputParser.getTriggerGroups();
	
		// Add the initial states to the bottom of the stack
		// The system must be initialized first
		SystemMemento systemMemento = system.initialize(
				inputParser.getSystemPath(),
				inputParser.getSystemName(),
				
				clock, variableFactory,
				inputParser.getSystemMemento());
		if (systemMemento == null) {
			String msg = "Unsatisfiable initial configuration: aborting...";
			logger.severe(msg);
			throw new AssertionError(msg);
		}
		systemMementoStack.push(systemMemento);
		storeSharedVariables(systemMemento);
		
		EnvironmentMemento environmentMemento = environment.initialize(
				inputParser.getEnvironmentPath(),
				inputParser.getEnvironmentFileName(),
				inputParser.getEnvironmentName(),
				clock,
				variableFactory, inputParser.getEnvironmentMemento(),
				inputParser.getEnvironmentTriggers());
		
		environmentMementoStack.push(environmentMemento);
		storeSharedVariables(environmentMemento);
		
	}
	
	protected void performCosimulationStep() {
		/******************/
		int backtrakingAttempts = maxCosimulationBacktraking;
		boolean stepApproved = false;
		boolean skipEnv = false;
		int nextStep = clock.getCurrentTime().getSimulationStep() + 1;
		do {
			int attemptsInStep = maxCosimulationAttemptsForStep;
			do {
				if (!skipEnv) {
					// t -> t + delta
					clock.tickForward();
					simulateEnvironment();
				} else {
					// The environment was already simulated.
					skipEnv = false;
				}
				
				
				if (isLastEnvironmentSimulationValid()) {
					try {
						simulateSystem();
						stepApproved = true;
					} catch(AssertionError err) {
						// Unsat
						rollbackEnvironment();
						clock.tickBackward();
						attemptsInStep = attemptsInStep - 1;
						rollbackSystem();
						logger.severe("The simulated step has no solution: retrying (" + 
								attemptsInStep +" attempts left).");
						// no need to resimulate modelica because it is deterministic
						skipEnv = true;
					}
				} else {
					// If the environment is not valid then we need to
					// re-simulate the system at the previous time, then the
					// environment again.
					rollbackEnvironment();
					clock.tickBackward();
					attemptsInStep = attemptsInStep - 1;
					rollbackSystem();
					logger.severe("The simulated step is invalid: retrying (" + 
							attemptsInStep +" attemptsleft).");
					// no need to resimulate modelica because it is deterministic
					skipEnv = true;
				}
				
			} while (attemptsInStep > 0 && !stepApproved && 
					!(clock.getCurrentTime().getSimulationStep() == nextStep));
			
			if (stepApproved) {
				break;
			}
			rollbackEnvironment();
			clock.tickBackward();
			backtrakingAttempts = backtrakingAttempts -1;
			rollbackSystem();
			logger.severe("Maximum number of attempts in state reached: backtraking (" + 
					backtrakingAttempts + " backtraking left)");
			// no need to resimulate modelica because it is deterministic
			skipEnv = true;
			
		} while (backtrakingAttempts > 0 && !stepApproved);
		
		if (!stepApproved) {
			logger.severe("Maximum backtraking depth reached: aborting...");
			throw new RuntimeException("Maximum backtraking depth reached.");
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
		int currentStep = clock.getCurrentTime().getSimulationStep();
		for (int i = elementsToremove; i > 0; i--) {
			EnvironmentMemento memento = environmentMementoStack.remove(0);
			int deltaSteps = currentStep - memento.getTime().getSimulationStep();
			if (deltaSteps < maxCosimulationBacktraking) {
				throw new AssertionError("EnvironmentMemento at time " + 
						memento.getTime() + " is not obsolete.");
			}
		}
		elementsToremove = systemMementoStack.size() -
				maxCosimulationBacktraking;
		for (int i = elementsToremove; i > 0; i--) {
			SystemMemento memento = systemMementoStack.remove(0);
			
			int deltaSteps = currentStep - memento.getLatestSimulatedTime().getSimulationStep();
			if (deltaSteps < maxCosimulationBacktraking) {
				throw new AssertionError("EnvironmentMemento at time " + 
						memento.getLatestSimulatedTime() + " is not obsolete.");
			}
			
			memento.deleteRelatedFiles();
			
		}
	}
	
	protected void rollbackEnvironment() {
		if (!simulationStarted) {
			throw new AssertionError("Simulation is not started.");
		}
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
			if (v.getVariableDefinition().getScope() == Scope.ENVIRONMENT_SHARED) {
				sharedVariablesMultimap.remove(memento.getTime(), v);
			}
		}
	}
	
	protected void rollbackSystem() {
		if (!simulationStarted) {
			throw new AssertionError("Simulation is not started.");
		}
		logger.info("Rolling back system state.");
		if (systemMementoStack.size() == 1) {
			logger.severe("End of system state stack reached.");
			throw new RuntimeException("Cannot rollback initial system state: aborting...");
		}
		SystemMemento discardedMemento = systemMementoStack.pop();
		// TODO(rax): assert is the right memento
		
		// Remove shared variables
		Time t = clock.getCurrentTime();
		Collection<VariableAssignment> variables = 
				discardedMemento.get(t);
		for (VariableAssignment v: variables) {
			if (v.getVariableDefinition().getScope() == Scope.SYSTEM_SHARED) {
				sharedVariablesMultimap.remove(t, v);
			}
		}
		
		// Add the unsat configuration to the current memento
		SystemMemento currentMemento = systemMementoStack.peek();
		currentMemento.addUnsatConfiguration(clock.getCurrentTime(), variables);
	}
	
	protected void simulateEnvironment() {
		if (!simulationStarted) {
			throw new AssertionError("Simulation is not started");
		}
		logger.info("Symulating environment at time: " + clock.getCurrentTime().getSimulationTime());
		
		environment.load(environmentMementoStack.peek(), systemMementoStack.peek());
		
		EnvironmentMemento environmentMemento =
			    environment.simulateNext();
		
		// Add memento on top of the stack
		environmentMementoStack.push(environmentMemento);
		
		// Add variables to shared variables map
		storeSharedVariables(environmentMemento);
	}

	private void storeSharedVariables(EnvironmentMemento environmentMemento) {
		for (VariableAssignment var: environmentMemento.getParams()) 
		{
			if (var.getVariableDefinition().getScope() == Scope.ENVIRONMENT_SHARED) {
				System.out.println("(" + environmentMemento.getTime() + "):" + var);
				sharedVariablesMultimap.put(clock.getCurrentTime(), var.clone());
			}
		}
	}
	
	protected void simulateSystem() {
		if (!simulationStarted) {
			throw new AssertionError("Simulation is not started");
		}
		logger.info("Simulating system at step: " + clock.getCurrentTime().getSimulationStep());
		system.load(systemMementoStack.peek(), environmentMementoStack.peek());
		
		SystemMemento systemMemento = system.simulateNext();
		
		if (systemMemento == null) {
			String msg = "Unsatisfieable configuration.";
			logger.severe(msg);
			throw new AssertionError(msg);
		}
		
		// Add memento to the top of the stack
		systemMementoStack.push(systemMemento);
		
		// Add shared variables to the variable map
		storeSharedVariables(systemMemento);
	}

	private void storeSharedVariables(SystemMemento systemMemento) {
		Collection<VariableAssignment> vars = systemMemento.get(clock.getCurrentTime());
		for (VariableAssignment var: vars) 
		{
			if (var.getVariableDefinition().getScope() == Scope.SYSTEM_SHARED) {
				System.out.println("(" + clock.getCurrentTime() + "):" + var);
				sharedVariablesMultimap.put(clock.getCurrentTime(),
						var.clone());
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

	/**
	 * @param propertyName
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @return the clock
	 */
	public Clock getClock() {
		return clock;
	}
	
}
