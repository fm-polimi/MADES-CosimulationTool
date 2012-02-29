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
import java.util.List;
//import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mades.common.timing.Clock;
import mades.common.utils.Constants;
import mades.common.variables.Scope;
import mades.common.variables.Transition;
import mades.common.variables.Trigger;
import mades.common.variables.TriggerFactory;
import mades.common.variables.VariableAssignment;
//import mades.common.variables.VariableAssignment;
//import mades.common.variables.VariableDefinition;
import mades.common.variables.VariableFactory;
import mades.environment.EnvironmentMemento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Wrapper class for using modelica exported system as
 * simulator for the environment.
 *
 */
public class ModelicaWrapper {
	private static String INIT_DOT_XML_FILE_POSTFIX = "_init.xml";
	private static String CVS_RES_FILE_POSTFIX = "_res.csv";
	private static String SIGNAL_FILE_NAME = "Transitions";
	private static String RUN_FILE = "modelica.sh";
	
	private static final String VARIABLE_NAME = "[ ]*[\\w -\\._\\(\\)]+";
	private static final String SIGNAL_LINE = "^(TRANSnp|TRANSpn):\\t(" + 
			VARIABLE_NAME + ")\\t(" + Constants.DOUBLE + ")$";
	private static String UP_DOWN = "TRANSpn";
	private Pattern signalPattern = Pattern.compile(SIGNAL_LINE);
	
	private String environmentPath;
	private String environmentName;
	
	private String initDotXmlFileName;
	private String signalsFileName;
	private String csvFileName;
	
	private VariableFactory variableFactory;
	private TriggerFactory triggerFactory;
	private Clock clock;
	
	/**
	 * @param environmentPath
	 * @param environmentName
	 */
	protected ModelicaWrapper(String environmentPath,
			String environmentFileName,
			String environmentName, Clock clock,
			VariableFactory variableFactory, 
			TriggerFactory triggerFactory,
			ArrayList<Trigger> triggers) {
		this.variableFactory = variableFactory;
		this.triggerFactory = triggerFactory;
		this.clock = clock;
		
		this.environmentPath = environmentPath;
		this.environmentName = environmentName;
		
		
		initDotXmlFileName = environmentPath + 
			File.separator + environmentName + INIT_DOT_XML_FILE_POSTFIX;
		csvFileName = environmentPath + 
			File.separator + environmentName + CVS_RES_FILE_POSTFIX;
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
		
		ArrayList<VariableAssignment> variables =
				new ArrayList<VariableAssignment>(csvParser.getVariables());
		// Keep the system state
		for (VariableAssignment v: memento.getParams()) {
			if (v.getVariableDefinition().getScope() == Scope.SYSTEM_SHARED) {
				variables.add(v);
			}
		}
		EnvironmentMemento resultMemento = new EnvironmentMemento(
				clock.getCurrentTime(), variables);
		File signalsFile = new File(signalsFileName);
		if (signalsFile.isFile() && signalsFile.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(signalsFile));
				String line = null;
				while ((line = reader.readLine()) != null) {
					parseSignalLine(resultMemento, line);
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
	
	protected void parseSignalLine(EnvironmentMemento memento, String line) {
		Matcher matcher = signalPattern.matcher(line);
		if (matcher.matches()) {
			String upDown = matcher.group(1);
			String variable = matcher.group(2);
			String time = matcher.group(3);
			
			List<Trigger> triggers = triggerFactory.get(variable);
			for (Trigger t: triggers) {
				Transition tr = t.addTransition(Double.parseDouble(time),
						UP_DOWN.equalsIgnoreCase(upDown));
				memento.addTransition(tr);	
			}
		}
	}
}
