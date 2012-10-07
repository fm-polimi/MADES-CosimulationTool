/**
 * 
 */
package mades.common.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Utility class for running shell commands.
 */
public class Runtimes {

	private Runtimes() {}
	
	//MR
	//Added a parameter to allow caller to control whether the runtime should wait
	//for the command to terminate or not; waiting for the command to terminate is
	//needless for zot, since the output parser can determine when the execution has
	//ended by itself
	public static InputStream runCommand(String command, Boolean wait) {
		Runtime run = Runtime.getRuntime();

        Process process = null;
		try {
			process = run.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(
					"Could not execute command: " + command + ".",
					e);
		}
		
		//MR
		//wait for process to terminate if this parameter "wait" is true
		if (wait) {
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return process.getInputStream();
	}
}
