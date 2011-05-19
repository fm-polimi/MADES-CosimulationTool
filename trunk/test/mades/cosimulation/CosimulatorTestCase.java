/**
 * 
 */
package mades.cosimulation;

import static org.junit.Assert.*;

import java.util.ArrayList;

import mades.common.Variable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class CosimulatorTestCase {
	
	Cosimulator cosimulator;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		cosimulator = new Cosimulator();
		cosimulator.setEnvironment(new EchoEnvironmentConnectorMock());
		cosimulator.setSystem(new EchoSystemConnectorMock());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link mades.cosimulation.Cosimulator#startCosimulation()}.
	 */
	@Test
	public void testStartCosimulation() {
		ArrayList<Variable> environmentParams = new ArrayList<Variable>();
		ArrayList<Variable> systemParams = new ArrayList<Variable>();
		
		double startTime = 0;
		double timeStep = 1;
		double maxCosimulationTime = 10;
		int maxCosimulationAttemptsForStep = 1;
		int maxCosimulationBacktraking = 1;
		
		cosimulator.startCosimulation(
				startTime,
				timeStep,
				maxCosimulationTime, 
				maxCosimulationAttemptsForStep,
				maxCosimulationBacktraking, 
				environmentParams,
				systemParams);
		
		assertTrue(maxCosimulationTime <= cosimulator.getSimulationTime());
	}
}
