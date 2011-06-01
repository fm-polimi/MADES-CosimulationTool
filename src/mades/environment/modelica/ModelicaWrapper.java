/**
 * 
 */
package mades.environment.modelica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mades.common.timing.Clock;
import mades.common.variables.Scope;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.common.variables.VariableFactory;
import mades.environment.EnvironmentMemento;
import mades.environment.SignalMap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Wrapper class for using modelica exported system as
 * simulator for the environment.
 *
 */
public class ModelicaWrapper {

	public static final Object START_TIME_VAR_NAME = " start value";
	public static final Object END_TIME_VAR_NAME = " stop value";
	private static String INIT_FILE_POSTFIX = "_init.txt";
	private static String BASE_FILE_POSTFIX = "_variables.txt";
	private static String FINAL_FILE_POSTFIX = "_final.txt";
	private static String SIGNAL_FILE_POSTFIX = "_transitions.txt";
	private static String RUN_FILE = "run.sh";
	
	private static String VARIABLE_LINE = "^([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)( //[ ]?default)? //([ ]?[\\w \\._\\(\\)]+)$";
	private Pattern variablePattern = Pattern.compile(VARIABLE_LINE);
	
	private String environmentPath;
	private String environmentName;
	
	private String environmentFileName;
	private String runFileName;
	private String initialVariableFileName;
	private String baseVariableFileName;
	private String finalVariableFileName;
	private String signalsFileName;
	
	private SignalMap signals;
	
	private VariableFactory variableFactory;
	private Clock clock;
	
	/**
	 * @param environmentPath
	 * @param environmentName
	 */
	protected ModelicaWrapper(String environmentPath, String environmentName, Clock clock) {
		this.environmentPath = environmentPath;
		this.environmentName = environmentName;
		
		this.clock = clock;
		signals = new SignalMap();
		
		environmentFileName = environmentPath + File.separator + environmentName;
		baseVariableFileName = environmentFileName + BASE_FILE_POSTFIX;
		initialVariableFileName = environmentFileName + INIT_FILE_POSTFIX;
		// Copy the base variables in the initial file
		copy(baseVariableFileName, initialVariableFileName);
		
		finalVariableFileName = environmentFileName + FINAL_FILE_POSTFIX;
		signalsFileName = environmentFileName + SIGNAL_FILE_POSTFIX;
		runFileName = environmentPath + File.separator + RUN_FILE;
	}
	
	
	protected void copy(String fileSource, String fileDest) {
		File source = new File(fileSource);
		if (!source.isFile()) {
			throw new AssertionError("Source file is not a file: " + fileSource);
		}
		if (!source.canRead()) {
			throw new AssertionError("Cannot read source file: " + fileSource);
		}
		
		File dest = new File(fileDest);
		if (!dest.canWrite()) {
			throw new AssertionError("Cannot write destintion file: " + fileSource);
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(source));
			PrintWriter writer = new PrintWriter(dest);
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					writer.println(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				writer.flush();
				writer.close();
			}
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize this wrapper by reading the init file.
	 * The init file contains variables and their initial value but not
	 * their scope. All the variable defined in the init file but not
	 * already defined in the co-simulation are considered internal.
	 * 
	 * @param variableFactory A factory containing all the variables 
	 *         already defined.
	 * @return the initial state of this environment.
	 */
	public EnvironmentMemento initFromFile(VariableFactory variableFactory) {
		this.variableFactory = variableFactory;
		ArrayList<VariableAssignment> variables = new ArrayList<VariableAssignment>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(baseVariableFileName));
			String line = null;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = variablePattern.matcher(line);
				if (matcher.matches()) {
					String value = matcher.group(1);
					String name = matcher.group(4);
					VariableDefinition def = null;
					if (!variableFactory.isDefined(name)) {
						def = variableFactory.define(name, Scope.ENVIRONMENT_INTERNAL);
					}
					else {
						def = variableFactory.get(name);
					}
					
					double doubleValue = Double.parseDouble(value);
					if (name.equals(START_TIME_VAR_NAME)) {
						doubleValue = 0;
					} else if (name.equals(END_TIME_VAR_NAME)) {
						doubleValue = clock.getTimeStep();
					}
					
					variables.add(
						    new VariableAssignment(def, doubleValue));
				} 
			}
			reader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EnvironmentMemento memento = new EnvironmentMemento(clock.getCurrentTime(), variables, signals);
		return memento;
	}
	
	
	public EnvironmentMemento simulateNext(EnvironmentMemento memento) {
		writeVariablesFromMemento(memento);
		runModelica();
		return loadVariablesFromSimulation();
	}
	
	protected void writeVariablesFromMemento(EnvironmentMemento memento) {
		ArrayList<VariableAssignment> variables = memento.getParams();
		try {
			PrintWriter writer = new PrintWriter(initialVariableFileName);
			
			for (VariableAssignment v: variables) {
				writer.println(v.getValue() + " //" + v.getVariableDefinition().getName());
			}
			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	protected void runModelica() {
		Runtime run = Runtime.getRuntime();
		Process process = null;
		try {
			process = run.exec(runFileName + " " + environmentPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
	}
	
	protected EnvironmentMemento loadVariablesFromSimulation() {
		ArrayList<VariableAssignment> variables = new ArrayList<VariableAssignment>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(finalVariableFileName));
			String line = null;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = variablePattern.matcher(line);
				if (matcher.matches()) {
					String value = matcher.group(1);
					String name = matcher.group(4);
					VariableDefinition def = variableFactory.get(name);
					
					double doubleValue = Double.parseDouble(value);
					if (name.equals(START_TIME_VAR_NAME)) {
						doubleValue = clock.getCurrentTime().getSimulationTime() - clock.getTimeStep();
					} else if (name.equals(END_TIME_VAR_NAME)) {
						doubleValue = clock.getCurrentTime().getSimulationTime();
					}
					
					variables.add(new VariableAssignment(def, doubleValue));
				}
			}
			reader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File signalsFile = new File(signalsFileName);
		if (signalsFile.isFile() && signalsFile.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(signalsFile));
				String line = null;
				while ((line = reader.readLine()) != null) {
					parseSignalLine(line);
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		EnvironmentMemento memento = new EnvironmentMemento(clock.getCurrentTime(), variables, signals);
		return memento;
	}
	
	protected void parseSignalLine(String line) {
		
	}
}
