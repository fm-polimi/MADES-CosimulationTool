/**
 * 
 */
package mades.system.zot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Set;

import mades.common.Variable;
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
	

	/**
	 * Initializes this instance with the engine and the given system.
	 * 
	 * @param engineFileName the engine file name.
	 * @param systemFileName the system file name.
	 * @param initialVariablesFileName the variables file name.
	 * 
	 * @throws AssertionError if any of the given files do not 
	 *         exist or if they are a directory.
	 */
	public ZotWrapper(String engineFileName, String systemFileName,
			String initialVariablesFileName) {
		this.engineFileName = engineFileName;
		File engineFile = new File(engineFileName);
		if (!engineFile.exists() || engineFile.isDirectory()) {
			throw new AssertionError(
					"Engine file not found or is a directory: " +
					engineFileName);
		}
		
		this.systemFileName = systemFileName;
		File systemFile = new File(systemFileName);
		if (!systemFile.exists() || systemFile.isDirectory()) {
			throw new AssertionError(
					"System file not found or is a directory: " + 
					systemFileName);
		}
		
		this.initialVariablesFileName = initialVariablesFileName;
		File currenVariablesFile = new File(initialVariablesFileName);
		if (!currenVariablesFile.exists() || currenVariablesFile.isDirectory()) {
			throw new AssertionError(
					"Variables file not found or is a directory: " +
					initialVariablesFileName);
		}
		
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
		
		Set<Integer> keys = memento.keySet();
		for (Integer k: keys) {
			Collection<Variable> variables = memento.get(k);
			if (k == 0) {
				composeVariableCollection(builder, variables);
			} else {
				builder.append("(futr ");
				composeVariableCollection(builder, variables);
				builder.append(" " + k +")/n");
			}
		}
		
		builder.append("))\n" +
		  		"\n" +
				"(defvar constraints\n" +
				"(&&\n" +
				"	history\n" +
		  		")))\n");
		return builder.toString();
	}
	
	/**
	 * Adds to the string builder a collection of variables.
	 * 
	 * @param builder The builder in use.
	 * @param variables The collection of variables to be added.
	 */
	private void composeVariableCollection(StringBuilder builder,
			Collection<Variable> variables) {
		builder.append("(&& ");
		for (Variable v: variables) {
			if (v.getValue() == 0) {
				builder.append("(!! (-P- " + v.getName() + "))");
			} else {
				builder.append("(-P- " + v.getName() + ")");
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
		try {
			PrintWriter variableswriter = new PrintWriter(initialVariablesFileName);
			variableswriter.write(composeVariablesFile(memento));
			variableswriter.flush();
			variableswriter.close();
		} catch (FileNotFoundException e) {
			throw new AssertionError(e);
		}
	}
	
	protected SystemMemento runZot() {
		
	}
	
	public SystemMemento executeSimulationStep(int step, SystemMemento memento) {
		if (step < 0) {
			throw new IllegalArgumentException("Step must be grater than 0. Found: " + step);
		}
		if (memento == null) {
			throw new IllegalArgumentException("Memento cannot be null.");
		}
		overrideVariables(memento);
		return runZot();
	}
	
	
}
