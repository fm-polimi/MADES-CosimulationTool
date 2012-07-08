/**
 * 
 */
package mades.system.nuzot;

import static mades.common.utils.Files.*;


import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import mades.common.timing.Clock;
import mades.common.timing.Time;
import mades.common.variables.TriggerFactory;
import mades.common.variables.VariableFactory;
import mades.system.SystemMemento;


import it.polimi.nuzot.smt.TypeChecker;
import it.polimi.nuzot.smt.grammar.Script;
import it.polimi.nuzot.ltl.LTLInterpreter;
import it.polimi.nuzot.shell.ShellInterpreter;
import it.polimi.nuzot.Z3.Z3Interpreter;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Handles the communication with NuZot.
 */
public class NuZotWrapper {
	
	public static final String SYSTEM_FILE_EXT = ".zot";
	
	/**
	 * The name of the ltl-smt file containing the system that has to
	 * be simulated.
	 */
	private String systemFileName;
	private String systemPath;
	private String systemName;
	
	private Logger logger;
	private Clock clock;
	private VariableFactory variableFactory;
	private TriggerFactory triggerFactory;
	
	// Sets 
	private Script initScript;
	
	/**
	 * Initializes this instance with the engine and the given system.
	 * 
	 * @param systempath the system path.
	 * @param systemName the system file name.
	 * @param initialVariablesFileName the variables file name.
	 * @param maxSimulationStep the maximum simulation steps.
	 * 
	 * @throws AssertionError if any of the given files do not 
	 *         exist or if they are a directory.
	 */
	public NuZotWrapper(String systemPath, String systemName, 
			Clock clock, VariableFactory variableFactory,
			TriggerFactory triggerFactory, 
			Logger logger) {
		
		this.clock = clock;
		this.variableFactory = variableFactory;
		this.triggerFactory = triggerFactory;
		this.logger = logger;
		
		this.systemPath = systemPath;
		this.systemName = systemName;
		
		// Check project directory
		checkFolderExistAndIsWritableOrThrow(systemPath, logger);
		
		systemFileName = systemPath + File.separator + systemName + SYSTEM_FILE_EXT;
		checkFileExistsOrThrow(systemFileName, logger);
	}
	
	public void initialize(SystemMemento systemMemento) {
		initScript = SystemMementoAdapter.generateInitScript(clock);
	}
	
	protected SystemMemento runZot(Time time, SystemMemento memento) {
		// Creates the stack
		Z3Interpreter z3 = new Z3Interpreter();
		TypeChecker typeChecker = new TypeChecker();
        typeChecker.next(z3);
        LTLInterpreter ltl = new LTLInterpreter();
    	ltl.next(typeChecker);
		ShellInterpreter shell = new ShellInterpreter();
        shell.next(ltl);
        
        // Add the init script
        shell.doVisit(initScript);
        // Loads the system
        shell.load(systemFileName);
    	logger.fine("System visiting step " + time.getSimulationStep());
        shell.doVisit(SystemMementoAdapter.mementoToScript(
        		clock, variableFactory, memento));
		
        shell.save(systemPath + File.separator + systemName + "_" 
        		+ time.getSimulationStep() + SYSTEM_FILE_EXT);
        if (!z3.checkSat()) {
			return null;
		}
		
		SystemMemento newMemento = SystemMementoAdapter.modelToMemento(
				time, variableFactory, memento, z3.getModel());
		newMemento.computeTransitions(triggerFactory);
		return newMemento;	
	}
	
	public SystemMemento executeSimulationStep(
			Time time, SystemMemento memento) {
		if (memento == null) {
			throw new IllegalArgumentException(
					"Memento cannot be null.");
		}
		
		SystemMemento results = runZot(time, memento);
		// Add rolled back states
		if (results != null) {
			results.setRolledBackVariablesMultimap(
					memento.getRolledBackVariablesMultimap());
		}
		return results;
	}
	
}
