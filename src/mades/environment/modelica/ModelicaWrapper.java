/**
 * 
 */
package mades.environment.modelica;

import static mades.common.utils.Files.checkFileExistsOrThrow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mades.common.timing.Clock;
import mades.common.variables.Type;
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

	private final static double TOLERANCE = 0.00000001;
	
	public static final Object START_TIME_VAR_NAME = " start value";
	public static final Object END_TIME_VAR_NAME = " stop value";
	private static String INIT_FILE_POSTFIX = "_init.txt";
	private static String FINAL_FILE_POSTFIX = "_final.txt";
	private static String SIGNAL_FILE_NAME = "A_Transitions";
	private static String RUN_FILE = "./env/modelica.sh";
	
	private static final String VARIABLE_NAME = "[ ]*[\\w -\\._\\(\\)]+";
	private static final String DOUBLE = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
	private static final String LABEL = "(\"[\\w\\W]+\")";
	private static final String VARIABLE_LINE = "^("  + DOUBLE + "|" + LABEL + ")( //[ ]*(default))? //(" + VARIABLE_NAME + ")$";
	private Pattern variablePattern = Pattern.compile(VARIABLE_LINE);
	
	private static final String SIGNAL_LINE = "^(TRANSnp|TRANSpn):\\t(" + VARIABLE_NAME + ")\\t(" + DOUBLE + ")$";
	private Pattern signalPattern = Pattern.compile(SIGNAL_LINE);
	
	private String environmentFolder;
	private String environmentName;
	
	private String initialVariableFileName;
	private String finalVariableFileName;
	private String signalsFileName;
	
	private VariableFactory variableFactory;
	private Clock clock;
	
	private int numVariables;
	
	/**
	 * @param environmentPath
	 * @param environmentName
	 */
	protected ModelicaWrapper(String environmentPath, Clock clock,
			VariableFactory variableFactory) {
		this.variableFactory = variableFactory;
		this.clock = clock;
		
		File folder = new File(environmentPath);
		
		environmentFolder = environmentPath ;
		environmentName = folder.getName();
		
		
		initialVariableFileName = environmentFolder + 
				File.separator + environmentName + INIT_FILE_POSTFIX;
		finalVariableFileName = environmentFolder +
				File.separator + environmentName + FINAL_FILE_POSTFIX;
		signalsFileName = environmentPath + File.separator + SIGNAL_FILE_NAME;
		
	}
	
	public EnvironmentMemento initialize(
			EnvironmentMemento environmentMemento) {
		numVariables = environmentMemento.getParams().size();
		// TODO(rax): check initial variables
		/*
		ArrayList<VariableAssignment> variables = environmentMemento.getParams();
		for (VariableAssignment v: variables) {
			String name = v.getVariableDefinition().getEnvironmentName();
			if (name.equals(START_TIME_VAR_NAME)) {
				v. = "0";
			} else if (name.equals(END_TIME_VAR_NAME)) {
				value = "" + clock.getTimeStep();
			}
		}*/
		return environmentMemento;
	} 
	
	private String composeThresholdString() {
		StringBuilder builder = new StringBuilder();
		for (VariableAssignment v: thresholds) {
			builder.append("parameter Real " +
					v.getVariableDefinition().getEnvironmentName() + 
					" = " + v.getValue() + ";\n");
		}
		return builder.toString();
	}

	private String composeSignalsString() {
		StringBuilder builder = new StringBuilder();
		for (VariableAssignment v: signals) {
			builder.append("discrete Real " +
					v.getVariableDefinition().getEnvironmentName() + ";\n");
		}
		return builder.toString();
	}
	
	private void addThresholdAndSignalVariablesToMo(StringBuilder builder) {
		String equation = "^equation$";
		Pattern equationPattern = Pattern.compile(equation);
		
		String thresholdsBegin = "/\\*\\*thresholds begin\\*\\*/";
		String thresholdsEnd = "\\*\\*thresholds end\\*\\*";
		String signalsBegin = "/\\*\\*signals begin\\*\\*/";
		String signalsEnd = "/\\*\\*signals end\\*\\*";
		
		Pattern thresholdBeginPattern = Pattern.compile(thresholdsBegin);
		Pattern thresholdEndPattern = Pattern.compile(thresholdsEnd);
		
		Pattern signalsBeginPattern = Pattern.compile(signalsBegin);
		Pattern signalsEndPattern = Pattern.compile(signalsEnd);
		
		String model = builder.toString();
		Matcher matcher;
		
		matcher = equationPattern.matcher(model);
		int equationIndex = -1;
		if (matcher.matches()) {
			equationIndex = matcher.start();
		} else {
			throw new AssertionError("Equation section not found in .mo file.");
		}
		
		int beginIndex = -1;
		int endIndex = -1;
		
		matcher = thresholdBeginPattern.matcher(model);
		if (matcher.matches()) {
			beginIndex = matcher.end();
			matcher = thresholdEndPattern.matcher(model);
			if (matcher.matches()) {
				endIndex = matcher.start();
				// Remove the previous thresholds and add the new one
				builder.delete(beginIndex, endIndex);
				builder.insert(beginIndex, composeThresholdString());
			} else {
				throw new AssertionError("Thresholds end not found in .mo file.");
			}
			
			
		} else {
			// Insert the threshold before "equation"
			builder.insert(equationIndex, "/**thresholds begin**/" + 
					composeThresholdString() + "/**thresholds end**/");
		}
		
		// Update the model
		model = builder.toString();
		
		matcher = signalsBeginPattern.matcher(model);
		if (matcher.matches()) {
			beginIndex = matcher.end();
			matcher = signalsEndPattern.matcher(model);
			if (matcher.matches()) {
				endIndex = matcher.start();
				// Remove the previous thresholds and add the new one
				builder.delete(beginIndex, endIndex);
				builder.insert(beginIndex, composeSignalsString());
			} else {
				throw new AssertionError("Signals end not found in .mo file.");
			}
			
			
		} else {
			// Insert the threshold before "equation"
			builder.insert(equationIndex, "/**signals begin**/" + 
					composeSignalsString() + "/**signals end**/");
		}
				
		
	}
	
	private String composeChangesString() {
		StringBuilder builder = new StringBuilder();

		for (VariableAssignment v: signals) {
			/*
			 * when C1.v > threshold_C1_v then
			 *     trigger_C1_v := 1.0;
	         * elsewhen C1.v <= threshold_C1_v then
             *     trigger_C1_v := 0.0;
	         * end when;
	         * 
  	         * when change(trigger_C1_v) then
  	  	     *     FilePrint(trigger_C1_v, pre(trigger_C1_v), time);
  	         * end when;
  	         */
		}
		return builder.toString();
	}
	
	private void addSignalChangesToMo(StringBuilder builder) {
		
		String algorithm = "^algorithm$";
		String changesBegin = "/\\*\\*changes begin\\*\\*/";
		String changesEnd = "\\*\\*changes end\\*\\*";
		
		Pattern algorithmPattern = Pattern.compile(algorithm);
		Pattern changesBeginPattern = Pattern.compile(changesBegin);
		Pattern changesEndPattern = Pattern.compile(changesEnd);
		
		String model = builder.toString();
		Matcher matcher;
		
		matcher = algorithmPattern.matcher(model);
		int algorithmIndex = -1;
		if (matcher.matches()) {
			algorithmIndex = matcher.end();
		} else {
			throw new AssertionError("Algorithm section not found in .mo file.");
		}
		
		int beginIndex = -1;
		int endIndex = -1;
		
		matcher = changesBeginPattern.matcher(model);
		if (matcher.matches()) {
			beginIndex = matcher.end();
			matcher = changesEndPattern.matcher(model);
			if (matcher.matches()) {
				endIndex = matcher.start();
				// Remove the previous thresholds and add the new one
				builder.delete(beginIndex, endIndex);
				builder.insert(beginIndex, composeChangesString());
			} else {
				throw new AssertionError("Thresholds end not found in .mo file.");
			}
			
			
		} else {
			// Insert the threshold before "equation"
			builder.insert(algorithmIndex, "/**changes begin**/" + 
					composeChangesString() + "/**changes end**/");
		}
		
	}
	
	private void checkAndUpdateMo() {
		checkFileExistsOrThrow(MO_FILE, logger);
		
		try {
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(MO_FILE));
			
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			reader.close();
			
			addThresholdAndSignalVariablesToMo(builder);
			addSignalChangesToMo(builder);
			
			PrintWriter pw = new PrintWriter(new FileOutputStream(MO_FILE));
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
	
	private void deleteSignalFile() {
		File signalFile = new File(signalsFileName);
		if (signalFile.exists()) {
			signalFile.delete();
		}
	}
	
	
	public EnvironmentMemento simulateNext(EnvironmentMemento memento) {
		writeVariablesFromMemento(memento);
		deleteSignalFile();
		runModelica(memento);
		return loadVariablesFromSimulation(memento);
	}
	
	protected void writeVariablesFromMemento(EnvironmentMemento memento) {
		ArrayList<VariableAssignment> variables = memento.getParams();
		try {
			PrintWriter writer = new PrintWriter(initialVariableFileName);
			
			for (VariableAssignment v: variables) {
				String name = v.getVariableDefinition().getEnvironmentName();
				String annotation = v.getAnnotation();
				String value = v.getValue();
				// Update simulation time
				if (name.equals(START_TIME_VAR_NAME)) {
					value = "" + (clock.getCurrentTime().getSimulationTime() - clock.getTimeStep());
				} else if (name.equals(END_TIME_VAR_NAME)) {
					value = "" + (clock.getCurrentTime().getSimulationTime());
				}
				// Format output values
				switch (v.getVariableDefinition().getType()) {
					case STRING: {
						break;
					}
					case INTEGER: {
						value = Integer.parseInt(value) + "";
						break;
					}
					case DOUBLE: {
						value = Double.parseDouble(value) + "";
						break;
					}
					case BOOLEAN: {
						break;
					}
				
				}
				
				if (!annotation.equals("")) {
					writer.println(value + " //" + annotation +" //" +name);
				}else {
					writer.println(value + " //" + name);
				}
			}
			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	protected void runModelica(EnvironmentMemento memento) {
		Runtime run = Runtime.getRuntime();
		Process process = null;
		try {
			process = run.exec(RUN_FILE + " " + environmentFolder + " " + environmentName);
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
	
	protected EnvironmentMemento loadVariablesFromSimulation(EnvironmentMemento memento) {
		ArrayList<VariableAssignment> variables = new ArrayList<VariableAssignment>();
		
		
		double startTime = 0;
		double endTime = 0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(finalVariableFileName));
			String line = null;

			
			while ((line = reader.readLine()) != null) {
				Matcher matcher = variablePattern.matcher(line);
				if (matcher.matches()) {
					String value = matcher.group(1);
					String annotation = matcher.group(5);
					String name = matcher.group(6);
					VariableDefinition def = variableFactory.getEnvironmentVar(name);
					
					if (name.equals(START_TIME_VAR_NAME)) {
						startTime = Double.parseDouble(value);
						value = "" + (clock.getCurrentTime().getSimulationTime() - clock.getTimeStep());
					} else if (name.equals(END_TIME_VAR_NAME)) {
						endTime = Double.parseDouble(value);
						value = "" + clock.getCurrentTime().getSimulationTime();
					}
					
					if (annotation == null) {
						annotation = "";
					}
					
					VariableAssignment var = new VariableAssignment(def, value, annotation);
					variables.add(var);
				} else {
					System.out.println("** Skipped line: " + line);
				}
			}
			reader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// TODO(rax): use modelica variable " tolerance";
		if (Math.abs(endTime - startTime) > TOLERANCE) {
			throw new AssertionError("Incomplete simulation");
		}
		
		if (variables.size() != numVariables) {
			throw new AssertionError(
				    "Wrong number of loaded variables. Found: " + 
				    variables.size() + " Expected: " + 
				    numVariables);
		}
		
		
		EnvironmentMemento resultMemento = new EnvironmentMemento(clock.getCurrentTime(), variables, memento.getSignals());
		File signalsFile = new File(signalsFileName);
		if (signalsFile.isFile() && signalsFile.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(signalsFile));
				String line = null;
				while ((line = reader.readLine()) != null) {
					parseSignalLine(resultMemento.getSignals(), line);
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return resultMemento;
	}
	
	protected void parseSignalLine(SignalMap signals, String line) {
		Matcher matcher = signalPattern.matcher(line);
		if (matcher.matches()) {
			String upDown = matcher.group(1);
			String name = matcher.group(2);
			String value = matcher.group(3);
			signals.put(name, Double.parseDouble(value));
		}
	}
}
