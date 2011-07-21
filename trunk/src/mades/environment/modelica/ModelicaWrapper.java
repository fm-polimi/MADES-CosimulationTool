/**
 * 
 */
package mades.environment.modelica;

import static mades.common.utils.Runtimes.runCommand;
//import static mades.common.utils.Files.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.PrintWriter;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mades.common.timing.Clock;
import mades.common.variables.Scope;
import mades.common.variables.Trigger;
import mades.common.variables.VariableAssignment;
//import mades.common.variables.VariableAssignment;
//import mades.common.variables.VariableDefinition;
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
	//private static String INIT_FILE_POSTFIX = "_init.txt";
	private static String INIT_DOT_XML_FILE_POSTFIX = "_init.xml";
	//private static String FINAL_FILE_POSTFIX = "_final.txt";
	private static String CVS_RES_FILE_POSTFIX = "_res.csv";
	private static String SIGNAL_FILE_NAME = "Transitions";
	private static String RUN_FILE = "modelica.sh";
	
	private static final String VARIABLE_NAME = "[ ]*[\\w -\\._\\(\\)]+";
	private static final String DOUBLE = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
	//private static final String LABEL = "(\"[\\w\\W]+\")";
	//private static final String VARIABLE_LINE = "^("  + DOUBLE + "|" + LABEL + ")( //[ ]*(default))? //(" + VARIABLE_NAME + ")$";
	//private Pattern variablePattern = Pattern.compile(VARIABLE_LINE);
	
	private static final String SIGNAL_LINE = "^(TRANSnp|TRANSpn):\\t(" + VARIABLE_NAME + ")\\t(" + DOUBLE + ")$";
	private Pattern signalPattern = Pattern.compile(SIGNAL_LINE);
	
	private String environmentPath;
	//private String environmentFileName;
	private String environmentName;
	
	//private String initialVariableFileName;
	private String initDotXmlFileName;
	//private String finalVariableFileName;
	private String signalsFileName;
	private String csvFileName;
	
	private VariableFactory variableFactory;
	private Clock clock;
	
	/**
	 * @param environmentPath
	 * @param environmentName
	 */
	protected ModelicaWrapper(String environmentPath,
			String environmentFileName,
			String environmentName, Clock clock,
			VariableFactory variableFactory, ArrayList<Trigger> triggers) {
		this.variableFactory = variableFactory;
		this.clock = clock;
		
		this.environmentPath = environmentPath;
		//this.environmentFileName = environmentFileName;
		this.environmentName = environmentName;
		
		
		/*initialVariableFileName = environmentPath + 
				File.separator + environmentName + INIT_FILE_POSTFIX;*/
		initDotXmlFileName = environmentPath + 
			File.separator + environmentName + INIT_DOT_XML_FILE_POSTFIX;
		csvFileName = environmentPath + 
			File.separator + environmentName + CVS_RES_FILE_POSTFIX;
		/*finalVariableFileName = environmentPath +
				File.separator + environmentName + FINAL_FILE_POSTFIX;*/
		signalsFileName = environmentPath + File.separator + SIGNAL_FILE_NAME;
		
		ModelInstrumentor instrumentor = new ModelInstrumentor(environmentPath,
				environmentFileName, environmentName);
		instrumentor.checkAndUpdateModel(triggers);
		instrumentor.compile();
	}
	
	public EnvironmentMemento initialize(
			EnvironmentMemento environmentMemento) {
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
	
		
	private void deleteSignalFile() {
		File signalFile = new File(signalsFileName);
		if (signalFile.exists()) {
			signalFile.delete();
		}
	}
	
	
	public EnvironmentMemento simulateNext(EnvironmentMemento memento) {
		writeInitDotXmlFromMemento(memento);
		deleteSignalFile();
		runModelica(memento);
		return loadVariablesFromSimulation(memento);
	}
	
	protected void writeInitDotXmlFromMemento(EnvironmentMemento memento) {
		InitXmlUpdater updater = new InitXmlUpdater(memento,
				initDotXmlFileName, clock);
		try {
			updater.doUpdate();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/*
	 * Commented because compatible with the previous version of modelica only
	protected void writeInitDotTxtFromMemento(EnvironmentMemento memento) {
		ArrayList<VariableAssignment> variables = memento.getParams();
		try {
			PrintWriter writer = new PrintWriter(initialVariableFileName);
			
			for (VariableAssignment v: variables) {
				String name = v.getVariableDefinition().getEnvironmentName();
				String annotation = v.getAnnotation();
				String value = v.getValue();
				// Update simulation time
				if (name.equals(START_TIME_VAR_NAME)) {
					value = "" + (clock.getCurrentTime().getSimulationTime() - 
							clock.getTimeStep());
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
	*/
	
	protected void runModelica(EnvironmentMemento memento) {
		BufferedReader buf = new BufferedReader(
				new InputStreamReader(
						runCommand(environmentPath + File.separator + RUN_FILE + " " +
								environmentPath + " " + environmentName)));
		
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
		ModelicaCsvParser csvParser = new ModelicaCsvParser(
				csvFileName, variableFactory);
		try {
			csvParser.doParse();
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		
			
		// TODO(rax): use modelica variable " tolerance";
		/*
		if (clock.getCurrentTime().getSimulationTime() - csvParser.getStopTime()
				> TOLERANCE) {
			throw new AssertionError("Incomplete modelica simulation.");
		}*/
		
		
		ArrayList<VariableAssignment> variables =
				new ArrayList<VariableAssignment>(csvParser.getVariables());
		// Keep the system state
		for (VariableAssignment v: memento.getParams()) {
			if (v.getVariableDefinition().getScope() == Scope.SYSTEM_SHARED) {
				variables.add(v);
			}
		}
		EnvironmentMemento resultMemento = new EnvironmentMemento(
				clock.getCurrentTime(), variables,
				memento.getSignals());
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
