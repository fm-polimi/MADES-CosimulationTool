/**
 * 
 */
package mades.environment.modelica;

import static mades.common.utils.Files.checkFileExistsOrThrow;
import static mades.common.utils.Runtimes.runCommand;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mades.common.variables.Scope;
import mades.common.variables.Trigger;
import mades.common.variables.VariableAssignment;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Instrument the given model at the first execution.
 *
 */
public class ModelInstrumentor {

	private static final String OMC = "omc";
	
	private String fileName;
	private Logger logger;
	private ArrayList<Trigger> triggers;
	
	public ModelInstrumentor(String fileName) {
		this.fileName = fileName;
	} 
	
	private String composeThresholdString() {
		StringBuilder builder = new StringBuilder();
		for (Trigger t: triggers) {
			VariableAssignment v = t.getThreshold();
			builder.append("parameter Real " +
					v.getVariableDefinition().getEnvironmentName() + 
					" = " + v.getValue() + ";\n");
		}
		return builder.toString();
	}

	private String composeSignalsString() {
		StringBuilder builder = new StringBuilder();
		for (Trigger t: triggers) {
			VariableAssignment v = t.getSignal();
			builder.append("discrete Real " +
					v.getVariableDefinition().getEnvironmentName() + ";\n");
		}
		return builder.toString();
	}
	
	private void addThresholdAndSignalVariablesToMo(StringBuilder builder) {
		instrument(builder, "/**thresholds begin**/", 
				"/**thresholds end**/", "equation", composeThresholdString());
		instrument(builder, "/**signals begin**/", 
				"/**signals end**/", "equation", composeSignalsString());
	}
	
	private String composeChangesString() {
		StringBuilder builder = new StringBuilder();

		for (Trigger t: triggers) {
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
			String var = t.getVariable().getVariableDefinition().getEnvironmentName();
			String threshold = t.getThreshold().getVariableDefinition().getEnvironmentName();
			String signal = t.getSignal().getVariableDefinition().getEnvironmentName();
			
			builder.append("when " + 
					var + 
					" > " + 
					threshold + 
					" then\n");
			builder.append("    " + signal + " := 1.0;\n");
			builder.append("elsewhen " + 
					var + 
					" <= " + 
					threshold + 
					" then\n");
			builder.append("    " + signal + " := 0.0;\n");
			builder.append("end when;\n");
			builder.append("\n");
			
			builder.append("when change(" + signal + ") then");
			builder.append("    FilePrint(" + signal + ", pre(" + signal + "), time);\n");
			builder.append("end when;\n");
			builder.append("\n");
		}
		return builder.toString();
	}
	
	private void addTriggersToMo(StringBuilder builder) {		
		instrument(builder, "/**triggers begin**/", "/**triggers end**/",
				"algorithm", composeChangesString());
	}
	
	private void instrument(StringBuilder builder, String begin,
			String end, String alternative, String substitution) {
		
		Pattern alternativePattern = Pattern.compile("\\Q" + alternative + "\\E");
		Pattern beginPattern = Pattern.compile("\\Q" + begin + "\\E");
		Pattern endPattern = Pattern.compile("\\Q" + end + "\\E");
		
		String model = builder.toString();
		int beginIndex = -1;
		int endIndex = -1;
		
		Matcher matcher = alternativePattern.matcher(model);
		int algorithmIndex = -1;
		if (matcher.matches()) {
			algorithmIndex = matcher.end();
		} else {
			throw new AssertionError("Expected string " + alternative +
					" section not found in .mo file.");
		}
		
		matcher = beginPattern.matcher(model);
		if (matcher.matches()) {
			beginIndex = matcher.end();
			matcher = endPattern.matcher(model);
			if (matcher.matches()) {
				endIndex = matcher.start();
				// Remove the previous thresholds and add the new one
				builder.delete(beginIndex, endIndex);
				builder.insert(beginIndex, substitution);
			} else {
				throw new AssertionError("Required string" + end + 
						" not found in .mo file.");
			}
			
			
		} else {
			// Insert the threshold before "equation"
			builder.insert(algorithmIndex, begin + 
					substitution + end);
		}
	}
	
	public void checkAndUpdateModel(ArrayList<Trigger> triggers) {
		this.triggers = new ArrayList<Trigger>();
		for (Trigger t: triggers) {
			Scope s = t.getVariable().getVariableDefinition().getScope();
			if (s == Scope.ENVIRONMENT_INTERNAL || s == Scope.ENVIRONMENT_SHARED) {
				this.triggers.add(t);
			}
		}
		checkFileExistsOrThrow(fileName, logger);
		
		try {
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			reader.close();
			
			addThresholdAndSignalVariablesToMo(builder);
			addTriggersToMo(builder);
			
			PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
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
	
	public void compile() {
		runCommand(OMC + " " + fileName);
		// TODO(rax): check compilation is successful
	}

	
}
