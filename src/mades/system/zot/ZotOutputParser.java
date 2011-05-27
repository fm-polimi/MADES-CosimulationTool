/**
 * 
 */
package mades.system.zot;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mades.common.timing.Time;
import mades.common.variables.Variable;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;

import com.google.common.collect.TreeMultimap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Parse the output of Zot and saves it into a collection of variables.
 */
public class ZotOutputParser {

	private static String STEP0 = "------ time 0 ------";
	private static String STEP = "^------ time (\\d+) ------$";
	
	private enum State {
	    HEADER, VARIABLES 
	}
	
	private State state = State.HEADER;
	private int step;
	private int lastStep;
	
	private Pattern stepPattern = Pattern.compile(STEP);
	
	private BufferedReader reader;
	private TreeMultimap<Time, VariableDefinition> variablesMultimap;
	
	public ZotOutputParser(InputStream stream, int lastStep) {
		reader = new BufferedReader(
				new InputStreamReader(stream));
		this.lastStep = lastStep;
		parse();
	}
	
	protected void parse() {
		variablesMultimap = TreeMultimap.create();
		step = 0;
		
		String line = "";
		try {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				processLine(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	protected void processLine(String line) {
		switch(state) {
			case HEADER: {
				if (line.equals(STEP0)) {
					state = State.VARIABLES;
				}
				break;
			}
			case VARIABLES: {
				Matcher matcher = stepPattern.matcher(line);
				if (matcher.matches()) {
					// TODO(rax): Set all the missing variables to false
					step = Integer.parseInt(matcher.group(1));
				} else {
					// TODO(rax): We should know if a variable is shared or private
					variablesMultimap.put(step, new VariableAssignment(line, true, ));
				}
				break;
			}
		}
	}
	
}
