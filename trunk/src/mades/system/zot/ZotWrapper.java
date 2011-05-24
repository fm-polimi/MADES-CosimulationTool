/**
 * 
 */
package mades.system.zot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

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
	
	private String composeVariablesFile(SystemMemento memento) {
		StringBuilder builder = new StringBuilder();
		builder.append(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n");
		builder.append(";;; variable at step:" + memento.getTime() + "\n");
		builder.append(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n" +
				"\n" +
				"(defvar history\n" +
				"	(&&\n");
		builder.append("	(&& (!! (-P- cond1)) (-P- react1) (!! (-P- act1)))" +
				"	(futr (&& (!! (-P- cond1)) (!! (-P- react1)) (!! (-P- act1))) 1)" +
				"	(futr (&& (-P- cond1) (!! (-P- react1)) (!! (-P- act1))) 2)");
		builder.append("))\n" +
		  		"\n" +
				"(defvar constraints\n" +
				"(&&\n" +
				"	history\n" +
		  		")))\n");
		return builder.toString();
	}
	
	/**
	 * Overrides the variable file with the given new values.
	 * 
	 * @param step the simulation step.
	 */
	public void overrideVariables(int step, SystemMemento memento) {
		if (step < 0) {
			throw new IllegalArgumentException("Step must be grater than 0. Found: " + step);
		}
		if (memento == null) {
			throw new IllegalArgumentException("Memento cannot be null.");
		}
		String filename = this.initialVariablesFileName + "_" + step;
		try {
			PrintWriter variableswriter = new PrintWriter(filename);
			variableswriter.write(composeVariablesFile(memento));
			variableswriter.flush();
			variableswriter.close();
		} catch (FileNotFoundException e) {
			throw new AssertionError(e);
		}
	}
	
	
}
