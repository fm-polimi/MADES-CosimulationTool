/**
 * 
 */
package mades.cosimulation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mades.common.timing.Clock;
import mades.common.timing.Time;
import mades.common.variables.Scope;
import mades.common.variables.Trigger;
import mades.common.variables.TriggerFactory;
import mades.common.variables.TriggerGroup;
import mades.common.variables.Type;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.common.variables.VariableFactory;
import mades.environment.EnvironmentMemento;
import mades.system.SystemMemento;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.collect.TreeMultimap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class InputParser extends DefaultHandler {

	private static final String COSIMULATION_ENVIRONMENT_PATH = "environmentPath";
	private static final String COSIMULATION_ENVIRONMENT_NAME = "environmentName";
	private static final String COSIMULATION_ENVIRONMENT_FILENAME = "environmentFileName";
	private static final String COSIMULATION_SYSTEM_PATH = "systemPath";
	private static final String COSIMULATION_SYSTEM_NAME = "systemName";
	private static final String TRIGGER_VALUE = "value";
	private static final String TRIGGER_THRESHOLD = "threshold";
	private static final String TRIGGER_SIGNAL = "signal";
	private static final String TRIGGER_VARIABLE = "variable";
	private static final String TRIGGER_SCOPE = "scope";
	private static final String TRIGGER_GROUP = "mades:triggergroup";
	private static final String VARIABLE_VALUE = TRIGGER_VALUE;
	private static final String VARIABLE_TYPE = "type";
	private static final String VARIABLE_SCOPE = TRIGGER_SCOPE;
	private static final String VARIABLE_SYSTEM_NAME = COSIMULATION_SYSTEM_NAME;
	private static final String VARIABLE_ENVIRONMENT_NAME = COSIMULATION_ENVIRONMENT_NAME;
	private static final String VARIABLE = "mades:variable";
	private static final String TRIGGER = "mades:trigger";
	
	private Logger logger;
	private String filename;
	private Clock clock;
	private VariableFactory variableFactory;
	private TriggerFactory triggerFactory;

	private String systemName;
	private String environmentName;
	private String systemPath;
	private String environmentPath;
	private String environmentFileName;
	
	private SystemMemento systemMemento;
	private ArrayList<VariableAssignment> systemVariables;
	private EnvironmentMemento environmentMemento;
	private ArrayList<VariableAssignment> environmentVariables;
	
	private ArrayList<TriggerGroup> triggerGroups;
	
	/**
	 * Stores all the environment triggers defined in the xml file. 
	 */
	private ArrayList<Trigger> environmentTriggers;
	
	/**
	 * Stores all the system triggers defined in the xml file. 
	 */
	private ArrayList<Trigger> systemTriggers;
	
	private TriggerGroup currentTriggerGroup;
	
	/**
	 * @param logger
	 * @param filename
	 * @param variableFactory
	 */
	public InputParser(Logger logger, Clock clock,
			VariableFactory variableFactory, 
			TriggerFactory triggerFactory,
			String filename) {
		this.logger = logger;
		this.clock = clock;
		this.filename = filename;
		this.variableFactory = variableFactory;
		this.triggerFactory = triggerFactory;
	}
	
	public void parseDocument() {
		logger.info("Starting to parse file: " + filename + ".");
		//get a factory
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		try {

			//get a new instance of parser
			SAXParser parser = parserFactory.newSAXParser();

			//parse the file and also register this class for call backs
			parser.parse(filename, this);

		}catch (Exception ex) {
			logger.severe("Parsing of file: " + filename +
					" failed with error: " + ex.getMessage());
			throw new RuntimeException(ex);
		}
		logger.info("File: " + filename + " parsed successfully.");
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		if (TRIGGER_GROUP.equalsIgnoreCase(qName)) {
			triggerGroups.add(currentTriggerGroup);
			currentTriggerGroup = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		systemVariables = new ArrayList<VariableAssignment>();
		environmentVariables = new ArrayList<VariableAssignment>();
		environmentTriggers = new ArrayList<Trigger>();
		systemTriggers = new ArrayList<Trigger>();
		triggerGroups = new ArrayList<TriggerGroup>();
	}

	private void parseCosimulation(String uri, String localName, String qName,
			Attributes attributes) {
		
		String path = new File(filename).getParent();
		
		systemName = attributes.getValue(COSIMULATION_SYSTEM_NAME);
		try {
			systemPath = new File(path, attributes.getValue(COSIMULATION_SYSTEM_PATH))
					.getCanonicalPath();
		} catch (IOException e) {
			throw new AssertionError("Invalid system path: " + e.getMessage());
		}
		environmentName = attributes.getValue(COSIMULATION_ENVIRONMENT_NAME);
		try {
			environmentPath = new File(path, attributes.getValue(COSIMULATION_ENVIRONMENT_PATH))
					.getCanonicalPath();
		} catch (IOException e) {
			throw new AssertionError("Invalid environment path: " + e.getMessage());
		}
		environmentFileName = attributes.getValue(COSIMULATION_ENVIRONMENT_FILENAME);
	}
	
	private void parseTrigger(String uri, String localName, String qName,
			Attributes attributes) {
		String scope = attributes.getValue(TRIGGER_SCOPE);
		String variable = attributes.getValue(TRIGGER_VARIABLE);
		String signal = attributes.getValue(TRIGGER_SIGNAL);
		String threshold = attributes.getValue(TRIGGER_THRESHOLD);
		double value = Double.parseDouble(attributes.getValue(TRIGGER_VALUE));
		
		Trigger trigger;
		if ("system".equalsIgnoreCase(scope)) {
			trigger = triggerFactory.getOrDefine(variable, signal, 
					threshold, Scope.SYSTEM_SHARED, value);
			if (variableFactory.isDefinedInSystem(variable)) {
				trigger.setVariable(variableFactory.getSystemVar(variable));
			}
			if (variableFactory.isDefinedInSystem(threshold)) {
				trigger.setThreshold(variableFactory.getSystemVar(threshold));
			}
			if (variableFactory.isDefinedInSystem(signal)) {
				trigger.setSignal(variableFactory.getSystemVar(signal));
			}
			systemTriggers.add(trigger);
		} else {
			trigger = triggerFactory.getOrDefine(variable, signal,
					threshold, Scope.ENVIRONMENT_SHARED, value);
			if (variableFactory.isDefinedInEnvironment(variable)) {
				trigger.setVariable(variableFactory.getEnvironmentVar(variable));
			}
			if (variableFactory.isDefinedInEnvironment(threshold)) {
				trigger.setThreshold(variableFactory.getEnvironmentVar(threshold));
			}
			if (variableFactory.isDefinedInEnvironment(signal)) {
				trigger.setSignal(variableFactory.getEnvironmentVar(signal));
			}
			environmentTriggers.add(trigger);
		}
				
		currentTriggerGroup.add(trigger);
	}
	
	private void parseVariable(String uri, String localName, String qName,
			Attributes attributes) {
		String environmentName = attributes.getValue(VARIABLE_ENVIRONMENT_NAME);
		String systemName = attributes.getValue(VARIABLE_SYSTEM_NAME);
		Scope scope = parseScope(attributes.getValue(VARIABLE_SCOPE));
		Type type = parseType(attributes.getValue(VARIABLE_TYPE));
		
		VariableDefinition def = variableFactory.define(
				systemName, environmentName, scope, type);
		
		String value = attributes.getValue(VARIABLE_VALUE);
		if (value != null) {
			// Skip variables not initialized in the XML file
			if (type == Type.STRING) {
				value = "\"" + value + "\"";
			}
			
			VariableAssignment var = new VariableAssignment(def, value);
			switch(scope) {
				case ENVIRONMENT_INTERNAL: {
					environmentVariables.add(var);
					break;
				}
				case ENVIRONMENT_SHARED: {
					environmentVariables.add(var);
					systemVariables.add(var);
					break;
				}
				case SYSTEM_INTERNAL: {
					systemVariables.add(var);
					break;
				}
				case SYSTEM_SHARED: {
					environmentVariables.add(var);
					systemVariables.add(var);
					break;
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if ("Mades:cosimulation".equalsIgnoreCase(qName)) {
			parseCosimulation(uri, localName, qName, attributes);
		} else if (VARIABLE.equalsIgnoreCase(qName)) {
			parseVariable(uri, localName, qName, attributes);	
		} else if (TRIGGER.equalsIgnoreCase(qName)) {
			parseTrigger(uri, localName, qName, attributes);
		} else if (TRIGGER_GROUP.equalsIgnoreCase(qName)) {
			currentTriggerGroup = new TriggerGroup();
		}
	}
	
	private Type parseType(String type) {
		if (type == null) {
			throw new AssertionError("Variable type cannot be null.");
		}
		if ("string".equalsIgnoreCase(type)) {
			return Type.STRING;
		} else if ("double".equalsIgnoreCase(type)) {
			return Type.DOUBLE;
		} else if ("boolean".equalsIgnoreCase(type)) {
			return Type.BOOLEAN;
		} else if ("integer".equalsIgnoreCase(type)) {
			return Type.INTEGER;
		} else {
			throw new AssertionError("Unrecognized variable type: " + type);
		}
	}
	
	private Scope parseScope(String scope) {
		if (scope == null) {
			throw new AssertionError("Variable type cannot be null.");
		}
		if ("environment_private".equalsIgnoreCase(scope)) {
			return Scope.ENVIRONMENT_INTERNAL;
		} else if ("environment_shared".equalsIgnoreCase(scope)) {
			return Scope.ENVIRONMENT_SHARED;
		} else if ("system_private".equalsIgnoreCase(scope)) {
			return Scope.SYSTEM_INTERNAL;
		} else if ("system_shared".equalsIgnoreCase(scope)) {
			return Scope.SYSTEM_SHARED;
		} else {
			throw new AssertionError("Unrecognized variable scope: " + scope);
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		environmentMemento = new EnvironmentMemento(clock.getCurrentTime(),
				environmentVariables);
		TreeMultimap<Time, VariableAssignment> multimap = TreeMultimap.create();
		for (VariableAssignment v: systemVariables) {
			multimap.put(clock.getCurrentTime(), v);
		}
		systemMemento = new SystemMemento(multimap);
	}

	/**
	 * @return the systemMemento
	 */
	public SystemMemento getSystemMemento() {
		return systemMemento;
	}

	/**
	 * @return the environmentMemento
	 */
	public EnvironmentMemento getEnvironmentMemento() {
		return environmentMemento;
	}

	/**
	 * @return the environmentTriggers
	 */
	public ArrayList<Trigger> getEnvironmentTriggers() {
		return environmentTriggers;
	}

	/**
	 * @return the systemTriggers
	 */
	public ArrayList<Trigger> getSystemTriggers() {
		return systemTriggers;
	}

	/**
	 * @return the triggerGroups
	 */
	public ArrayList<TriggerGroup> getTriggerGroups() {
		return triggerGroups;
	}

	/**
	 * @return the systemName
	 */
	public String getSystemName() {
		return systemName;
	}

	/**
	 * @return the environmentName
	 */
	public String getEnvironmentName() {
		return environmentName;
	}

	/**
	 * @return the systemPath
	 */
	public String getSystemPath() {
		return systemPath;
	}

	/**
	 * @return the environmentPath
	 */
	public String getEnvironmentPath() {
		return environmentPath;
	}

	/**
	 * @return the environmentFileName
	 */
	public String getEnvironmentFileName() {
		return environmentFileName;
	}	
	
}
