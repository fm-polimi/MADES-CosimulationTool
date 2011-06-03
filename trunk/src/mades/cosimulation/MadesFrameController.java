/**
 * 
 */
package mades.cosimulation;

import java.util.logging.Logger;

import mades.environment.modelica.ModelicaEnvironmentConnector;
import mades.system.zot.ZotSystemConnector;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class MadesFrameController implements Runnable {

	Logger logger = Logger.getLogger(MadesFrameController.class.getName());
	
	private MadesFrame frame;
	
	private boolean running;
	private String path;

	
	public void run() {
		
		String path = frame.getJTextField().getText();
		
		Cosimulator cosimulator = new Cosimulator(logger);
		
		ZotSystemConnector system = new ZotSystemConnector(path, 30, logger);
		ModelicaEnvironmentConnector environment = new ModelicaEnvironmentConnector(path, logger);
		
		cosimulator.setEnvironment(environment);
		cosimulator.setSystem(system);
	}
	
}
