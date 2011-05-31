/**
 * 
 */
package mades.system.zot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import mades.common.timing.Clock;
import mades.common.timing.Time;
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

	public static String LISP_INTERPRETER = "clisp";
	
	
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
	private String initialVariablesFileName;
	
	private Clock clock;
	private VariableFactory variableFactory;
	private ArrayList<VariableDefinition> variables;
	
	
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
	public ZotWrapper(String engineFileName, String systemFileName,
			String initialVariablesFileName, int maxSimulationStep,
			Clock clock, VariableFactory variableFactory,
			ArrayList<VariableDefinition> variables) {
		
		this.variables = variables;
		this.clock = clock;
		this.variableFactory = variableFactory;
		
		this.systemFileName = systemFileName;
		File systemFile = new File(systemFileName);
		if (!systemFile.exists() || systemFile.isDirectory()) {
			throw new AssertionError(
					"System file not found or is a directory: " + 
					systemFileName);
		}
		
		this.initialVariablesFileName = initialVariablesFileName;
		/*File currenVariablesFile = new File(initialVariablesFileName);
		if (!currenVariablesFile.exists() || currenVariablesFile.isDirectory()) {
			throw new AssertionError(
					"Variables file not found or is a directory: " +
					initialVariablesFileName);
		}*/
		
		this.engineFileName = engineFileName;
		try {
			writeEngine(maxSimulationStep);
		} catch (FileNotFoundException e) {
			throw new AssertionError(
					"Engine file " + systemFileName +
					" is not writable: " + e.getMessage()
					);
		}
		
	}
	
	private void writeEngine(int maxSimulationTime) throws FileNotFoundException {
		StringBuilder builder = new StringBuilder();
		builder.append("(asdf:operate 'asdf:load-op 'eezot)\n");
		builder.append("(use-package :trio-utils)\n");
		builder.append("(defvar TSPACE " + maxSimulationTime + ")\n");
		builder.append("\n");
		builder.append("(load \"" + systemFileName + "\")\n");
		builder.append("(load \"" + initialVariablesFileName + "\")\n");
		builder.append("\n");
		builder.append("(eezot:zot TSPACE (&& the-system constraints))\n");
		String engine = builder.toString();
		
		PrintWriter writer = new PrintWriter(engineFileName);
		writer.write(engine);
		writer.flush();
		writer.close();
	}
	
	/**
	 * Converts the given {@link SystemMemento} in a lisp variables file. 
	 * 
	 * @param memento the memento to convert.
	 * @return a string containing the lisp representation of
	 *         the given memento.
	 */
	private String composeVariablesFile(SystemMemento memento) {
		StringBuilder builder = new StringBuilder();
		builder.append(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n");
		builder.append(";;; variables \n");
		builder.append(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n" +
				"\n" +
				"(defvar history\n" +
				"	(&&\n");
		
		Set<Time> keys = memento.keySet();
		for (Time t: keys) {
			Collection<VariableAssignment> variables = memento.get(t);
			int step = t.getSimulationStep();
			if (step == 0) {
				composeVariableCollection(builder, variables);
			} else {
				builder.append("(futr ");
				composeVariableCollection(builder, variables);
				builder.append(" " + step +")\n");
			}
		}
		
		builder.append("))\n" +
		  		"\n" +
				"(defvar constraints\n" +
				"(&&\n" +
				"	history\n" +
		  		"))\n");
		return builder.toString();
	}
	
	/**
	 * Adds to the string builder a collection of variables.
	 * 
	 * @param builder The builder in use.
	 * @param variables The collection of variables to be added.
	 */
	private void composeVariableCollection(StringBuilder builder,
			Collection<VariableAssignment> variables) {
		builder.append("(&& ");
		for (VariableAssignment v: variables) {
			VariableDefinition def = v.getVariableDefinition();
			if (v.getValue() == 0) {
				builder.append("(!! (-P- " + def.getName() + "))");
			} else {
				builder.append("(-P- " + def.getName() + ")");
			}
		}
		builder.append(")\n");
	}

	/**
	 * Overrides the variable file with the given new values.
	 * 
	 * @param step the simulation step.
	 */
	protected void overrideVariables(SystemMemento memento) {
		try {
			PrintWriter variableswriter = new PrintWriter(
					initialVariablesFileName);
			variableswriter.write(composeVariablesFile(memento));
			variableswriter.flush();
			variableswriter.close();
		} catch (FileNotFoundException e) {
			throw new AssertionError(e);
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
				variableFactory, variables, time.getSimulationStep(), 
				process.getInputStream());
		SystemMemento memento = new SystemMemento(parser.parse());
		/*
		BufferedReader buf = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		String line = "";
		try {
			while ((line=buf.readLine())!=null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		*/
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
