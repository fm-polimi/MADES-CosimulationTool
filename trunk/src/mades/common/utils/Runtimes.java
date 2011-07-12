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
	
	public static InputStream runCommand(String command) {
		Runtime run = Runtime.getRuntime();
		Process process = null;
		try {
			process = run.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return process.getInputStream();
	}
}
