/**
 * 
 */
package mades.environment.modelica;

import static mades.common.utils.Files.*;
import static mades.common.utils.Runtimes.runCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.Files;

import mades.common.variables.Trigger;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Instrument the given model at the first execution.
 *
 */
public class ModelInstrumentor {
	private static final String MODELICA_SOURCE_FOLDER = "sources";

	private static final String OMC = "omc";
	
	private String environmentPath;
	private String absoluteFileName;
	private String fileName;
	private String modelName;
	private Logger logger;
	private ArrayList<Trigger> triggers;
	
	public ModelInstrumentor(String environmentPath, String fileName,
			String environmentName) {
		this.environmentPath = environmentPath;
		this.fileName = fileName;
		this.absoluteFileName = environmentPath + File.separator +
				MODELICA_SOURCE_FOLDER + File.separator + fileName;
		this.modelName = environmentName;
	} 
	
	private String composeThresholdString() {
		StringBuilder builder = new StringBuilder();
		for (Trigger t: triggers) {
			builder.append("\tparameter Real " +
					t.getThresholdName() + 
					" = " + t.getValue() + ";\n");
		}
		return builder.toString();
	}

	private String composeSignalsString() {
		StringBuilder builder = new StringBuilder();
		for (Trigger t: triggers) {
			builder.append("\tdiscrete Real " +
					t.getSignalName() + ";\n");
		}
		return builder.toString();
	}
	
	private void addThresholdAndSignalVariablesToMo(StringBuilder builder) {
		instrument(builder, "\n\t/**thresholds begin**/\n", 
				"\t/**thresholds end**/\n", "model " + modelName,
				false, composeThresholdString());
		instrument(builder, "\n\t/**signals begin**/\n", 
				"\t/**signals end**/\n", "model " + modelName,
				false, composeSignalsString());
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
			String var = t.getVariableName();
			String threshold = t.getThresholdName();
			String signal = t.getSignalName();
			
			builder.append("\twhen " + 
					var + 
					" > " + 
					threshold + 
					" then\n");
			builder.append("\t\t" + signal + " := 1.0;\n");
			builder.append("\telsewhen " + 
					var + 
					" <= " + 
					threshold + 
					" then\n");
			builder.append("\t\t" + signal + " := 0.0;\n");
			builder.append("\tend when;\n");
			builder.append("\n");
			
			builder.append("\twhen change(" + signal + ") then\n");
			builder.append("\t\tFilePrint(\"" + var + "\", " + signal + ", pre(" + signal + "), time);\n");
			builder.append("\tend when;\n");
			builder.append("\n");
		}
		return builder.toString();
	}
	
	private void addTriggersToMo(StringBuilder builder) {		
		instrument(builder, "\n\t/**triggers begin**/\n", "\t/**triggers end**/\n",
				"end " + modelName + ";", true,
				composeChangesString());
	}
	
	private void instrument(StringBuilder builder, String begin,
			String end, String alternative, boolean before, 
			String substitution) {
		Pattern alternativePattern = Pattern.compile("\\Q" + alternative + "\\E");
		Pattern beginPattern = Pattern.compile("\\Q" + begin + "\\E");
		Pattern endPattern = Pattern.compile("\\Q" + end + "\\E");
		
		String model = builder.toString();
		int beginIndex = -1;
		int endIndex = -1;
		
		Matcher matcher = alternativePattern.matcher(model);
		int algorithmIndex = -1;
		if (matcher.find()) {
			algorithmIndex = before?matcher.start():matcher.end();
		} else {
			throw new AssertionError("Expected string " + alternative +
					" section not found in .mo file.");
		}
		
		matcher = beginPattern.matcher(model);
		if (matcher.find()) {
			beginIndex = matcher.end();
			matcher = endPattern.matcher(model);
			if (matcher.find()) {
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
		this.triggers = triggers;
		checkFileExistsOrThrow(absoluteFileName, logger);
		
		try {
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(absoluteFileName));
			
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			reader.close();
			
			addThresholdAndSignalVariablesToMo(builder);
			addTriggersToMo(builder);
			addInclusions(builder);
			
			PrintWriter pw = new PrintWriter(new FileOutputStream(absoluteFileName));
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
		File currentPath = getCurrentPath(this.getClass());
		File currentSourceDir = new File(currentPath, "./env/modelica/sources");
		File environmentDir = new File(environmentPath);
		File environmentSourceDir = new File(environmentDir, "sources");
		try {
			copyDir(currentSourceDir, environmentSourceDir);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		File mosFile = new File(environmentSourceDir, "./compile.mos");
		try {
			instrumentMos(mosFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		runCommand(OMC + " " + mosFile.getAbsolutePath());
		// TODO(rax): check compilation is successful
		try {
			File modelicaSh = new File(environmentDir, "modelica.sh");
			Files.copy(new File(currentPath, "./env/modelica/modelica.sh"),
					modelicaSh);
			modelicaSh.setExecutable(true, false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void instrumentMos(File file)
			throws FileNotFoundException, IOException {
		/**
		 * {% MODELFILE.MO %}
		 * {% PACKAGE.MODEL %}
		 * {% TIME_STEP %}
		 */
		HashMap<String, String> substitutions = new HashMap<String, String>();
		substitutions.put("MODEL_PATH", environmentPath);
		substitutions.put("MODEL_FILE", fileName);
		substitutions.put("MODEL_NAME", modelName);
		substitutions.put("TIME_STEP", "10");
		
		String mos = compileTemplateFile(substitutions, new FileReader(file));
		PrintWriter pw = new PrintWriter(file);
		pw.print(mos);
		pw.flush();
		pw.close();
	}

	private void addInclusions(StringBuilder builder) {
		File inclusionFileName = new File(getCurrentPath(this.getClass()),
				"./env/modelica/include/include.mo");
		StringBuilder inclusion = new StringBuilder(); 
		try {
			BufferedReader br = new BufferedReader(new FileReader(inclusionFileName));
			String line;
			while ((line = br.readLine()) != null) {
				inclusion.append(line + "\n");
			}
			br.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
			
		instrument(builder, "\n/** include begin **/\n",
				"/** include end **/\n", "end " + modelName + ";", false,
				inclusion.toString());
	} 
}
