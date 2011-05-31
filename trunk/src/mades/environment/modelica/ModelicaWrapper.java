/**
 * 
 */
package mades.environment.modelica;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class ModelicaWrapper {

	private static String INIT_FILE_POSTFIX = "_init.txt";
	private static String OLD_FILE_POSTFIX = "_old.txt";
	private static String FINAL_FILE_POSTFIX = "_final.txt";
	
	private String environmentPath;
	private String environmentName;
	
	private String environmentFileName;
	private String initialVariableFileName;
	private String finalVariableFileName;
	
	
	/**
	 * @param environmentPath
	 * @param environmentName
	 */
	protected ModelicaWrapper(String environmentPath, String environmentName) {
		this.environmentPath = environmentPath;
		this.environmentName = environmentName;
		
		
	}
	
	
}
