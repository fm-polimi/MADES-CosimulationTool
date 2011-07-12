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
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mades.common.variables.VariableAssignment;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Updates the model at the first execution.
 *
 */
public class ModelUpdater {

	private static final String OMC = "omc";
	
	private String fileName;
	private Logger logger;
	
	public ModelUpdater(String fileName) {
		this.fileName = fileName;
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
		updateModel(builder, "/**thresholds begin**/", 
				"/**thresholds end**/", "equation", composeThresholdString());
		updateModel(builder, "/**signals begin**/", 
				"/**signals end**/", "equation", composeSignalsString());
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
		updateModel(builder, "/**changes begin**/", "/**changes end**/",
				"algorithm", composeChangesString());
	}
	
	private void updateModel(StringBuilder builder, String begin,
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
	
	public void checkAndUpdateModel() {
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
			addSignalChangesToMo(builder);
			
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
