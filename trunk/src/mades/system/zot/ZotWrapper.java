/**
 * 
 */
package mades.system.zot;

import static mades.common.utils.Files.*;
import static mades.common.utils.Runtimes.runCommand;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import mades.common.timing.Clock;
import mades.common.timing.Time;
import mades.common.variables.TriggerFactory;
import mades.common.variables.Type;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.common.variables.VariableFactory;
import mades.system.SystemMemento;


/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Handles the communication with Zot.
 * Zot is an open and easily extendable bounded model/satisfiability
 * checker. It was born as a satisfiability checker, as its original
 * language is the TRIO metric temporal logic. Zot now supports
 * operational, descriptive, or hybrid models. Its plug-in based
 * architecture permits both mono- and bi-infinite discrete temporal
 * domains (i.e. infinite both towards the past and the future),
 * and also supports a dense-time variant of the MTL logic.
 * 
 * This class invokes Zot at each step of the simulation.
 * 
 * @see http://home.dei.polimi.it/pradella/
 */
public class ZotWrapper {

	public static final String ENGINE_FOLDER = getCurrentPath(ZotWrapper.class) +
			File.separator + "env" + File.separator + "zot" + File.separator;
	public static final String ENGINE = "run.zot";
	public static final String LISP_INTERPRETER = "zot";
	
	public static final String SYSTEM = "_system.zot";
	public static final String HISTORY = "_history.zot";
	public static final String CONSTRAINTS = "_constraints.zot";
	
	
	/**
	 * The name of the lisp file containing the system that has to
	 * be simulated.
	 */
	private String systemFileName;
	
	private String historyFileName;
	private String constraintsFileName;
	
	private String systemPath;
	
	private Logger logger;
	private Clock clock;
	private VariableFactory variableFactory;
	private TriggerFactory triggerFactory;
	private ArrayList<VariableDefinition> definedVariables;
	
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
	public ZotWrapper(String systemPath, String systemName, 
			Clock clock, VariableFactory variableFactory,
			TriggerFactory triggerFactory, 
			Logger logger) {
		
		this.clock = clock;
		this.variableFactory = variableFactory;
		this.triggerFactory = triggerFactory;
		this.logger = logger;
		
		this.systemPath = systemPath;
		
		// Check project directory
		checkFolderExistAndIsWritableOrThrow(systemPath, logger);
		
		systemFileName = systemPath + File.separator + systemName + SYSTEM;
		checkFileExistsOrThrow(systemFileName, logger);
		
		historyFileName = systemPath + File.separator + systemName + HISTORY;
		constraintsFileName = systemPath + File.separator + systemName + CONSTRAINTS;
		checkFileExistsOrThrow(constraintsFileName, logger);
		
		// The windows lisp interpreter wants "//" as a path separator
		// instead of File.separator
		if (File.separator.equals("\\")) {
			String separator = "//";
			systemFileName.replace(File.separator, separator);
			historyFileName.replace(File.separator, separator);
			constraintsFileName.replace(File.separator, separator);
		}
		
		checkAndUpdateEngine(this.clock.getFinalStep());
	}
	
	private void checkAndUpdateEngine(int maxSimulationStep) {
		try {
			logger.info("Copying engine dir: " + new File(ENGINE_FOLDER).getAbsolutePath());
			copyDir(new File(ENGINE_FOLDER), new File(systemPath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String engine = systemPath + File.separator + ENGINE;
		checkFileExistsOrThrow(engine, logger);
		
		try {
			/*
			 * (defvar TSPACE {% TSPACE %})
			 * (load "{% ZOT_MODEL %}")
			 * (load "{% ZOT_HYSTORY %}")
			 * (load "{% ZOT_CONSTRAINTS %}")
			 */
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("TSPACE", "" + (2 * clock.getFinalStep()));
			substitutions.put("ZOT_MODEL", systemFileName);
			substitutions.put("ZOT_HYSTORY", historyFileName);
			substitutions.put("ZOT_CONSTRAINTS", constraintsFileName);
			
			String mos = compileTemplateFile(substitutions, new FileReader(engine));
			PrintWriter pw = new PrintWriter(new FileWriter(engine));
			pw.print(mos);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
			
	}
	
	public void initialize(SystemMemento systemMemento) {
		Collection<VariableAssignment> variables = systemMemento.get(clock.getCurrentTime());
		definedVariables = new ArrayList<VariableDefinition>();
		for (VariableAssignment v: variables) {
			definedVariables.add(v.getVariableDefinition());
		}
	}
	
	
	/**
	 * Adds to the string builder a collection of variables.
	 * 
	 * @param builder The builder in use.
	 * @param variables The collection of variables to be added.
	 */
	private void composeVariableCollection(StringBuilder builder,
			Collection<VariableAssignment> variables) {
		builder.append("(&&");
		for (VariableAssignment v: variables) {
			VariableDefinition def = v.getVariableDefinition();
			if (def.getType() == Type.BOOLEAN) {
				double value = Double.parseDouble(v.getValue());
			  	if (value == 0) {
			  		builder.append("(!! (-P- " + def.getSystemName() + "))");
			 	} else {
			    	builder.append("(-P- " + def.getSystemName() + ")");
			  	}	
			} else {
				double value = Double.parseDouble(v.getValue());
				if (Math.abs(value) < 0.001)
				  value = 0.0;
                //BigDecimal dec_value = new BigDecimal(value).setScale(6, BigDecimal.ROUND_HALF_UP);				
				builder.append("([=] (-V- " + def.getSystemName() + ") " + value + ")");
			}
		}
		
		builder.append(")");
	}

	private void composeUnsatConstrains(StringBuilder builder,
			Collection<VariableAssignment> variables) {
		builder.append("(!!");
		composeVariableCollection(builder, variables);
		builder.append(")");
	}
	
	
	protected void composeStepPredicate(StringBuilder builder,
			Collection<VariableAssignment> variables,
			Set<Collection<VariableAssignment>> unsat
			) {
		
		if (unsat.size() != 0) {
			builder.append("(&& ");
			for (Collection<VariableAssignment> set: unsat) {
				composeUnsatConstrains(builder, set);
			}
			composeVariableCollection(builder, variables);
			builder.append(")");
		} else {
			composeVariableCollection(builder, variables);
		}
	}
	
	/**
	 * Overrides the variable file with the given new values.
	 * 
	 * @param step the simulation step.
	 */
	protected void overrideVariables(SystemMemento memento) {
		
		StringBuilder builder;
		builder = new StringBuilder();
		builder.append("(defvar history (&&\n");
		Set<Time> keys = memento.keySet();
		for (Time t: keys) {
			Collection<VariableAssignment> variables = memento.get(t);
			Set<Collection<VariableAssignment>> unsat = memento.getUnsatConfiguration(t);
			int step = t.getSimulationStep();
			if (step == 0) {
				composeStepPredicate(builder, variables, unsat);
				builder.append("\n");
			} else {
				builder.append("(futr ");
				composeStepPredicate(builder, variables, unsat);
				builder.append(" " + step +")\n");
			}
		}
		builder.append("\n))");
		
		PrintWriter variableswriter;
		try {
			variableswriter = new PrintWriter(
					historyFileName);
			variableswriter.write(builder.toString());
			variableswriter.flush();
			variableswriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	protected SystemMemento runZot(Time time) {
		String engine = systemPath + File.separator + ENGINE;
		InputStream inputStream = runCommand(LISP_INTERPRETER + " " + engine);
		
		ZotOutputParser parser = new ZotOutputParser(clock, 
				variableFactory, definedVariables, time.getSimulationStep(), 
				inputStream);
		SystemMemento memento = new SystemMemento(parser.parse());
		if (parser.isUnsat()) {
			return null;
		}
		memento.computeTransitions(triggerFactory);
		return memento;
	}
	
	public SystemMemento executeSimulationStep(
			Time time, SystemMemento memento) {
		if (memento == null) {
			throw new IllegalArgumentException(
					"Memento cannot be null.");
		}
		overrideVariables(memento);
		SystemMemento results = runZot(time);
		if (results != null) {
			results.setRolledBackVariablesMultimap(
					memento.getRolledBackVariablesMultimap());
		}
		return results;
	}
	
}
