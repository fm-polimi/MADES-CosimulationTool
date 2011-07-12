/**
 * 
 */
package mades.system.zot;

import static mades.common.utils.Files.*;
import static mades.common.utils.Runtimes.runCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mades.common.timing.Clock;
import mades.common.timing.Time;
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

	public static final String ENGINE = "./env/run.zot";
	public static final String LISP_INTERPRETER = "clisp";
	
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
	
	private Logger logger;
	private Clock clock;
	private VariableFactory variableFactory;
	private ArrayList<VariableDefinition> definedVariables;
	
	/*
	private static final String CONSTRAINS_BEGIN = "\\Q(defvar history\\E[\\n]+";
	private static final String CONSTRAINS_END = "\\Q)\\E[\\n]+\\Q(defvar constraints\\E";
	private Pattern constrainsBeginPattern = Pattern.compile(CONSTRAINS_BEGIN);
	private Pattern constrainsEndPattern = Pattern.compile(CONSTRAINS_END);
	*/
	
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
		checkFolderExistAndIsWritableOrThrow(path, logger);
		File folder = new File(path);
		
		String projectName = folder.getName();
		
		systemFileName = path + File.separator + projectName + SYSTEM;
		checkFileExistsOrThrow(systemFileName, logger);
		
		historyFileName = path + File.separator + projectName + HISTORY;
		constraintsFileName = path + File.separator + projectName + CONSTRAINTS;
		checkFileExistsOrThrow(constraintsFileName, logger);
		
		checkAndUpdateEngine(maxSimulationStep);
		
	}
	
	private void checkAndUpdateEngine(int maxSimulationStep) {
		checkFileExistsOrThrow(ENGINE, logger);
		
		/*
		 * Looks for the following lines and updates them
         * (defvar TSPACE 70)
		 * (load "./examples/RC/RC_system.zot")
		 * (load "./examples/RC/RC_constraints.zot")
		 */
		String tSpace = "\\Q(\\Edefvar TSPACE ([\\d])+\\Q)\\E";
		String system = "\\Q(\\Eload TSPACE \"([\\w]/\\.)+_system.zot\"\\Q)\\E";
		String history = "\\Q(\\Eload TSPACE \"([\\w]/\\.)+_history.zot\"\\Q)\\E";
		String constraints = "\\Q(\\Eload TSPACE \"([\\w]/\\.)+_constraints.zot\"\\Q)\\E";
		
		Pattern tSpacePattern = Pattern.compile(tSpace);
		Pattern systemPattern = Pattern.compile(system);
		Pattern historyPattern = Pattern.compile(history);
		Pattern constraintsPattern = Pattern.compile(constraints);
		
		try {
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(ENGINE));
			
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = tSpacePattern.matcher(line);
				if (matcher.matches()) {
					line = "(defvar TSPACE " + maxSimulationStep + ")";
				} else {
					matcher = systemPattern.matcher(line);
					if (matcher.matches()) {
						line = "(load \"" + systemFileName + "\")";
					} else {
						matcher = historyPattern.matcher(line);
						if (matcher.matches()) {
							line = "(load \"" + historyFileName + "\")";
						} else {
							matcher = constraintsPattern.matcher(line);
							if (matcher.matches()) {
								line = "(load \"" + constraintsFileName + "\")";
							}
						}
					}
				}
				builder.append(line + "\n");
			}
			reader.close();
			
			PrintWriter pw = new PrintWriter(new FileOutputStream(ENGINE));
			pw.print(builder.toString());
			pw.flush();
			pw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				builder.append("([=] (-V- " + def.getSystemName() + ") " + v.getValue() + ")");
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
		
		StringBuilder builder;/* = new StringBuilder();
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
		*/
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
		
		/*CharSequence toReplace = constrains.subSequence(start, end);
		constrains =  constrains.replace(toReplace, builder.toString());
		*/
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
		InputStream inputStream = runCommand(LISP_INTERPRETER + " " + ENGINE);
		
		ZotOutputParser parser = new ZotOutputParser(clock, 
				variableFactory, definedVariables, time.getSimulationStep(), 
				inputStream);
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
