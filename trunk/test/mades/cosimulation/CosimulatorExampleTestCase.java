package mades.cosimulation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;

import mades.common.timing.Time;
import mades.common.variables.Scope;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.common.variables.VariableFactory;
import mades.environment.modelica.ModelicaEnvironmentConnector;
import mades.environment.modelica.ModelicaWrapper;
import mades.system.zot.ZotSystemConnector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.TreeMultimap;

public class CosimulatorExampleTestCase {

	public final static String ENVIRONMENT_PATH = "/home/rax/workspace-mades/mades/examples/RC";
	public final static String ENVIRONMENT_NAME = "RC";
	
	public static final String SYSTEM = "/home/rax/workspace-mades/mades/tools/zot/SimulationToyExample_system.zot";
	String engine = "/tmp/MadesZotEngine.zot";
	String variables = "/tmp/MadesZotVariables.zot";
	
	Logger logger = Logger.getLogger(CosimulatorExampleTestCase.class.getName());

	ModelicaWrapper wrapper;
	VariableFactory variableFactory = new VariableFactory();
	
	Cosimulator cosimulator;
	ZotSystemConnector system;
	ModelicaEnvironmentConnector environment;
	
	@Before
	public void setUp() throws Exception {
		
		system = new ZotSystemConnector(ENVIRONMENT_PATH, 30, logger);
		environment = new ModelicaEnvironmentConnector(ENVIRONMENT_PATH, logger);
		cosimulator = new Cosimulator(logger);
		cosimulator.setEnvironment(environment);
		cosimulator.setSystem(system);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStartCosimulation() {
		double initialSimulationTime = 0;
		double timeStep = 5;
		double maxCosimulationTime = 30;
		int maxCosimulationAttemptsForStep = 1;
		int maxCosimulationBacktraking = 1;
		
		cosimulator.startCosimulation(
				initialSimulationTime,
				timeStep,
				maxCosimulationTime,
				maxCosimulationAttemptsForStep,
				maxCosimulationBacktraking);
		
		TreeMultimap<Time, VariableAssignment> results = cosimulator.getSharedVariablesMultimap();
		int steps = (int)(maxCosimulationTime / timeStep) + 1;
		Set<Time> keys = results.keySet();
		assertEquals(steps, keys.size());
		for (Time key: keys) {
			Set<VariableAssignment> vars = results.get(key);
			assertEquals(2, vars.size());
		}
	}

}
