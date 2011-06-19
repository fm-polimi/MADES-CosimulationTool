/**
 * 
 */
package mades.system.zot;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mades.common.timing.Clock;
import mades.common.timing.Time;
import mades.common.timing.TimeFactory;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.common.variables.VariableFactory;

import com.google.common.collect.TreeMultimap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Parse the output of Zot and saves it into a
 * collection of variables.
 */
public class ZotOutputParser {

	private static String START_STEP = "------ time 1 ------";
	private static String STEP = "^------ time (\\d+) ------$";
	private static String END = "------ end ------";
	private static String SIGNALS = "\\*\\*(\\w+)\\*\\*";
	
	private static final String DOUBLE = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
	private static String VARIABLE = "([\\w._-]+)( = (" + DOUBLE + ")?)";
	private static String VARIABLE_FRACTION = "([\\w._-]+)( = ([0-9]+)/([0-9]+)?)";
	
	private static final String UNSAT = "---UNSAT---";
	private Pattern unsatPattern = Pattern.compile(UNSAT);
	
	private enum State {
	    HEADER, VARIABLES 
	}

	private TimeFactory timeFactory;
	
	private State state = State.HEADER;
	private int step;
	private int simulationStep;
	
	private Pattern stepPattern = Pattern.compile(STEP);
	private Pattern signalPattern = Pattern.compile(SIGNALS);
	private Pattern variablePattern = Pattern.compile(VARIABLE);
	private Pattern variableFractionPattern = Pattern.compile(VARIABLE_FRACTION);
	
	private BufferedReader reader;
	private TreeMultimap<Time, VariableAssignment> variablesMultimap;
	
	private Time currentTime;
	private boolean simulationStepReached = false;
	
	private VariableFactory variableFactory;
	private ArrayList<VariableDefinition> variables;
	private ArrayList<VariableDefinition> falseVariablesAtStep;
	
	private boolean unsat = false;
	
	public ZotOutputParser(Clock clock,
			VariableFactory variableFactory,
			ArrayList<VariableDefinition> variables,
			int simulationStep, InputStream stream) {
		timeFactory = clock.getFactory();
		this.variableFactory = variableFactory;
		this.variables = variables;
		reader = new BufferedReader(
				new InputStreamReader(stream));
		this.simulationStep = simulationStep;
	}
	
	public TreeMultimap<Time, VariableAssignment> parse() {
		variablesMultimap = TreeMultimap.create();
		step = 0;
		
		String line = "";
		try {
			while (!simulationStepReached && 
					(line = reader.readLine()) != null) {
				System.out.println(line);
				processLine(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return variablesMultimap;
	}
	
	protected void processLine(String line) {
		Matcher matcher = unsatPattern.matcher(line);
		if (matcher.matches()) {
			unsat = true;
		}
		
		switch(state) {
			case HEADER: {
				if (line.equals(START_STEP)) {
					state = State.VARIABLES;
					currentTime = timeFactory.get(0);
					falseVariablesAtStep = (ArrayList<VariableDefinition>) variables.clone();
				}
				break;
			}
			case VARIABLES: {
				if (line.equals(END)) {
					simulationStepReached = true;
					break;
				}
				
				Matcher lineMatcher = stepPattern.matcher(line);
				
				if (lineMatcher.matches()) {
					// Set all the missing variables to false
					for (VariableDefinition def: falseVariablesAtStep) {
						if (def.isBoolean()) {
							variablesMultimap.put(currentTime, new VariableAssignment(def, "0", ""));
						}
					}
					
					step = Integer.parseInt(lineMatcher.group(1)) - 1;
					if (step > simulationStep) {
						simulationStepReached = true;
						break;
					}
					currentTime = timeFactory.get(step);
					falseVariablesAtStep = (ArrayList<VariableDefinition>) variables.clone();
				} else {
					String varname = line.trim();
					if (varname.equals("")) {
						break;
					}
					Matcher signalMatcher = signalPattern.matcher(varname);
					if (signalMatcher.matches()) {
						break;
					}
					Matcher varMatcher = variablePattern.matcher(varname);
					if (varMatcher.matches()) {
						String name = varMatcher.group(1);
						String value = varMatcher.group(3);
						
						VariableDefinition def = variableFactory.get(name);
						falseVariablesAtStep.remove(def);
						variablesMultimap.put(currentTime, new VariableAssignment(def, value, ""));
					} else {
						Matcher fractMatcher = variableFractionPattern.matcher(varname);
						if (fractMatcher.matches()) {
							String name = fractMatcher.group(1);
							String value1 = fractMatcher.group(3);
							String value2 = fractMatcher.group(4);
							String value = "" + (Double.parseDouble(value1) / Double.parseDouble(value2));
							
							VariableDefinition def = variableFactory.get(name);
							falseVariablesAtStep.remove(def);
							variablesMultimap.put(currentTime, new VariableAssignment(def, value, ""));
						}else {
							VariableDefinition def = variableFactory.get(varname);
							falseVariablesAtStep.remove(def);
							variablesMultimap.put(currentTime, new VariableAssignment(def, "1", ""));
						}
					}

				}
				break;
			}
		}
	}

	/**
	 * @return the unsat
	 */
	public boolean isUnsat() {
		return unsat;
	}
	
}
