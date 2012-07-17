/**
 * 
 */
package mades.system.nuzot;

import static mades.common.utils.Files.checkFileExistsOrThrow;
import static mades.common.utils.Files.checkFolderExistAndIsWritableOrThrow;
import it.polimi.nuzot.Z3.Z3Interpreter;
import it.polimi.nuzot.ltl.LTLInterpreter;
import it.polimi.nuzot.shell.ShellInterpreter;
import it.polimi.nuzot.smt.TypeChecker;
import it.polimi.nuzot.smt.grammar.Script;

import java.io.File;
import java.util.logging.Logger;

import mades.common.timing.Clock;
import mades.common.timing.Time;
import mades.common.variables.TriggerFactory;
import mades.common.variables.VariableFactory;
import mades.environment.EnvironmentMemento;
import mades.system.SystemConnector;
import mades.system.SystemMemento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class NuZotPushAndPopSystemConnector implements SystemConnector {

	protected Clock clock;
	protected Logger logger;
	
	protected SystemMemento systemMemento;
	
	public static final String SYSTEM_FILE_EXT = ".zot";
	
	/**
	 * The name of the ltl-smt file containing the system that has to
	 * be simulated.
	 */
	private String systemFileName;
	private String systemPath;
	private String systemName;
	
	private VariableFactory variableFactory;
	private TriggerFactory triggerFactory;
	
	private Z3Interpreter z3;
	private TypeChecker typeChecker; 
	private LTLInterpreter ltl;
	private ShellInterpreter shell;
	
	// Sets 
	private Script initScript;
	
	
	public NuZotPushAndPopSystemConnector(Logger logger) {
		this.logger = logger;
	}
	
	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#initialize(java.util.ArrayList, int)
	 */
	@Override
	public SystemMemento initialize(
			String systemPath, String systemName, 
			Clock clock,
			VariableFactory variableFactory,
			TriggerFactory triggerFactory,
			SystemMemento systemMemento) {
		
		this.clock = clock;
		this.variableFactory = variableFactory;
		this.triggerFactory = triggerFactory;
		
		this.systemPath = systemPath;
		this.systemName = systemName;
		
		// Check project directory
		checkFolderExistAndIsWritableOrThrow(systemPath, logger);
		
		systemFileName = systemPath + File.separator + systemName + SYSTEM_FILE_EXT;
		checkFileExistsOrThrow(systemFileName, logger);
		
		// Creates the stack
		z3 = new Z3Interpreter();
		typeChecker = new TypeChecker();
        typeChecker.next(z3);
        ltl = new LTLInterpreter();
    	ltl.next(typeChecker);
		shell = new ShellInterpreter();
        shell.next(ltl);
		
		initScript = SystemMementoAdapter.generateInitScript(clock);
        // Add the init script
        shell.doVisit(initScript);
        // Loads the system
        shell.load(systemFileName);
    	
		return systemMemento;
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#load(mades.system.SystemMemento, mades.environment.EnvironmentMemento)
	 */
	@Override
	public void load(SystemMemento systemMemento,
			EnvironmentMemento environmentParams) {
		this.systemMemento = new SystemMemento(systemMemento);
		this.systemMemento.update(environmentParams);
	}

	
	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#simulateNext(int)
	 */
	@Override
	public SystemMemento simulateNext() {
		if (systemMemento == null) {
			throw new IllegalArgumentException(
					"Memento cannot be null.");
		}
		
		Time time = clock.getCurrentTime();
		logger.fine(
				"System visiting step " +
				time.getSimulationStep());
        shell.doVisit(
        		SystemMementoAdapter.mementoToScript(
        				time, clock, variableFactory, systemMemento));
		
        
        shell.save(
        		systemPath + File.separator + systemName + "_" 
        		+ time.getSimulationStep() + SYSTEM_FILE_EXT);
        
        if (!z3.checkSat()) {
			return null;
		}
		
		SystemMemento newMemento = SystemMementoAdapter.modelToMemento(
				time, variableFactory, systemMemento, z3.getModel());
		newMemento.computeTransitions(triggerFactory);
		
		// Add rolled back states
		if (newMemento != null) {
			newMemento.setRolledBackVariablesMultimap(
					systemMemento.getRolledBackVariablesMultimap());
		}
		return newMemento;
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#getCurrentParams()
	 */
	@Override
	public SystemMemento getCurrentParams() {
		return systemMemento;
	}

	@Override
	public void push() {
		shell.push();
	}

	@Override
	public void pop() {
		shell.pop();
	}

}
