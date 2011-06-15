/**
 * 
 */
package mades.system.zot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mades.common.timing.Clock;
import mades.common.timing.Time;
import mades.common.variables.Scope;
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

	public static final String LISP_INTERPRETER = "clisp";
	public static final String ENGINE = "_run.zot";
	public static final String SYSTEM = "_system.zot";
	public static final String VARIABLES = "_constraints.zot";
	public static final String INIT = ".init";
	
	
	/**
	 * The name of the lisp file containing the engine to run the
	 * simulation with Zot.
	 */
	private String engineFileName;
	
	/**
	 * The name of the lisp file containing the system that has to
	 * be simulated.
	 */
	private String systemFileName;
	
	/**
	 * The name of the lisp file containing the simulation variables.
	 * A new file will be used for each step of the simulation.
	 */
	private String constraintsFileName;
	
	private String initFileName;
	
	private Logger logger;
	private Clock clock;
	private VariableFactory variableFactory;
	private ArrayList<VariableDefinition> definedVariables;
	
	private static final String DOUBLE = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
	private static final String SYSTEM_DEF = "sys";
	private static final String SHARED_DEF = "shared";
	private static final String BOOLEAN = "boolean";
	private static final String REAL = "real";
	private static final String INIT_VARIABLE = "^([\\w]+) (env|sys) (private|shared) (" + BOOLEAN +"|" + REAL + ") (" + DOUBLE + ")$";
	private Pattern stepPattern = Pattern.compile(INIT_VARIABLE);
	
	private static final String CONSTRAINS_BEGIN = "\\Q(defvar history\\E[\\n]+";
	private static final String CONSTRAINS_END = "\\Q)\\E[\\n]+\\Q(defvar constraints\\E";
	private Pattern constrainsBeginPattern = Pattern.compile(CONSTRAINS_BEGIN);
	private Pattern constrainsEndPattern = Pattern.compile(CONSTRAINS_END);
	
	/**
	 * Initializes this instance with the engine and the given system.
	 * 
	 * @param engineFileName the engine file name.
	 * @param systemFileName the system file name.
	 * @param initialVariablesFileName the variables file name.
	 * @param maxSimulationStep the maximum simulation steps.
	 * 
	 * @throws AssertionError if any of the given files do not 
	 *         exist or if they are a directory.
	 */
	public ZotWrapper(String path, int maxSimulationStep,
			Clock clock, VariableFactory variableFactory, Logger logger) {
		
		this.clock = clock;
		this.variableFactory = variableFactory;
		this.logger = logger;
		
		// Check project directory
		File folder = new File(path);
		if (!folder.exists() || !folder.isDirectory()) {
			String errorMsg = "Path " + path + " must be a folder: aborting...";
			logger.severe(errorMsg);
			throw new AssertionError(errorMsg);
		}
		if (!folder.canWrite()) {
			String errorMsg = "Path " + path + " must have write permission: aborting...";
			logger.severe(errorMsg);
			throw new AssertionError(errorMsg);
		}
		
		String projectName = folder.getName();
		
		systemFileName = path + File.separator + projectName + SYSTEM;
		checkFileExist(systemFileName);
		
		constraintsFileName = path + File.separator + projectName + VARIABLES;
		checkFileExist(constraintsFileName);
		
		engineFileName = path + File.separator + projectName + ENGINE;
		checkAndUpdateEngine(maxSimulationStep);
		
		initFileName = path + File.separator + projectName + INIT;
		checkFileExist(initFileName);
	}
	
	private void checkFileExist(String filename) {
		File file = new File(filename);
		if (!file.exists() || !file.isFile()) {
			String errorMsg = "File not found or is a directory: " + 
					filename;
			logger.severe(errorMsg);
			throw new AssertionError(errorMsg);
		}
	}
	
	private void checkAndUpdateEngine(int maxSimulationStep) {
		checkFileExist(this.engineFileName);
		// TODO(rax): update file
	}
	
	public ArrayList<VariableAssignment> parseInit() {
		definedVariables = new ArrayList<VariableDefinition>();
		ArrayList<VariableAssignment> variables = new ArrayList<VariableAssignment>();
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(initFileName));
			
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = stepPattern.matcher(line);
				if (matcher.matches()) {
					String name = matcher.group(1);
					String sysOrEnv = matcher.group(2);
					String privateOrShared = matcher.group(3);
					String bool = matcher.group(4);
					String value = matcher.group(5);
					Scope scope;
					if (sysOrEnv.equals(SYSTEM_DEF)) {
						if (privateOrShared.equals(SHARED_DEF)) {
							scope = Scope.SYSTEM_SHARED;
						} else {
							scope = Scope.SYSTEM_INTERNAL;
						}
					} else {
						if (privateOrShared.equals(SHARED_DEF)) {
							scope = Scope.ENVIRONMENT_SHARED;
						} else {
							scope = Scope.SYSTEM_SHARED;
						}
					}
					VariableDefinition def = variableFactory.define(
							name, scope, bool.equals(BOOLEAN));
					definedVariables.add(def);
					variables.add(new VariableAssignment(def, Double.parseDouble(value)));
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return variables;
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
			if (def.isBoolean()) {
				if (v.getValue() == 0) {
					builder.append("(!! (-P- " + def.getName() + "))");
				} else {
					builder.append("(-P- " + def.getName() + ")");
				}	
			} else {
				builder.append("([=] (-V- " + def.getName() + ") " + (int)Math.round(v.getValue()) + ")");
			}
		}
		builder.append(")");
	}

	/**
	 * Overrides the variable file with the given new values.
	 * 
	 * @param step the simulation step.
	 */
	protected void overrideVariables(SystemMemento memento) {
		
		StringBuilder builder = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(constraintsFileName));
			String line = null;
			while ((line = br.readLine()) != null) {
				builder.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new AssertionError(e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// Fail silently
			}
		}
		
		String constrains = builder.toString();
		Matcher matcherBegin = constrainsBeginPattern.matcher(constrains);
		Matcher matcherEnd = constrainsEndPattern.matcher(constrains);
		if (!matcherBegin.find() || !matcherEnd.find()) {
			throw new AssertionError("Constraints file has no constraint");
		}
		
		int start = matcherBegin.end();
		int end = matcherEnd.start();
		
		builder = new StringBuilder();
		builder.append("(&&\n");
		Set<Time> keys = memento.keySet();
		for (Time t: keys) {
			Collection<VariableAssignment> variables = memento.get(t);
			int step = t.getSimulationStep();
			if (step == 0) {
				composeVariableCollection(builder, variables);
				builder.append("\n");
			} else {
				builder.append("(futr ");
				composeVariableCollection(builder, variables);
				builder.append(" " + step +")\n");
			}
		}
		builder.append("\n)");
		
		CharSequence toReplace = constrains.subSequence(start, end);
		constrains =  constrains.replace(toReplace, builder.toString());
		
		PrintWriter variableswriter;
		try {
			variableswriter = new PrintWriter(
					constraintsFileName);
			variableswriter.write(constrains);
			variableswriter.flush();
			variableswriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	protected SystemMemento runZot(Time time) {
		String cmd = LISP_INTERPRETER + " " + engineFileName;
		Runtime run = Runtime.getRuntime();
		Process process = null;
		try {
			process = run.exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		ZotOutputParser parser = new ZotOutputParser(clock, 
				variableFactory, definedVariables, time.getSimulationStep(), 
				process.getInputStream());
		SystemMemento memento = new SystemMemento(parser.parse());
		if (parser.isUnsat()) {
			return null;
		}
		return memento;
	}
	
	public SystemMemento executeSimulationStep(
			Time time, SystemMemento memento) {
		if (memento == null) {
			throw new IllegalArgumentException(
					"Memento cannot be null.");
		}
		overrideVariables(memento);
		return runZot(time);
	}
	
	
}
