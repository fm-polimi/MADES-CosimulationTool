/**
 * 
 */
package mades.environment.modelica;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import mades.common.variables.Scope;
import mades.common.variables.Type;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.common.variables.VariableFactory;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class ModelicaCsvParser {

	private String csvFileName;
	private VariableFactory variableFactory;
	
	private ArrayList<String> variablenames;
	private ArrayList<VariableAssignment> variables;
	private double stopTime;
	
	public ModelicaCsvParser(String csvFileName, VariableFactory variableFactory) {
		this.csvFileName = csvFileName;
		this.variableFactory = variableFactory;
		
		this.variablenames = new ArrayList<String>();
		this.variables = new ArrayList<VariableAssignment>();
	}
	
	public void doParse() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(csvFileName));
		String line = br.readLine();
		String lastLine = null;
		if (line != null) {
			parseVariableNames(line);
		} else {
			throw new RuntimeException("File: " + csvFileName +
					" seams to be empty. Please make it sure that modelica is properly configured.");
		}
		while ((line = br.readLine()) != null) {
			lastLine = line;
		}
		if (lastLine != null) {
			parseValues(lastLine);
		} else {
			throw new RuntimeException("File: " + csvFileName +
			" has no data. Please make it sure that modelica is properly configured.");
		}		
	}
	
	private void parseVariableNames(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, ",");
		while (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			token = token.substring(1, token.length() -1);
			variablenames.add(token);
		}
	}
	
	private void parseValues(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, ",");
		int i = 0;
		while (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			String varName = variablenames.get(i);
			if ("time".equals(varName)) {
				stopTime =  Double.parseDouble(token);
			} else {
				VariableDefinition def;
				try {
					def = variableFactory.getEnvironmentVar(varName);
				} catch(AssertionError ex) {
					def = variableFactory.define(varName, varName,
							Scope.ENVIRONMENT_INTERNAL, Type.DOUBLE);
				}
				variables.add(new VariableAssignment(def, token, ""));
			}
			i ++;
		}
	}

	/**
	 * @return the variables
	 */
	public ArrayList<VariableAssignment> getVariables() {
		return variables;
	}

	/**
	 * @return the stopTime
	 */
	public double getStopTime() {
		return stopTime;
	}
		
}
