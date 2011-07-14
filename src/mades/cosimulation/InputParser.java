/**
 * 
 */
package mades.cosimulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mades.common.timing.Clock;
import mades.common.timing.Time;
import mades.common.variables.Scope;
import mades.common.variables.Trigger;
import mades.common.variables.Type;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.common.variables.VariableFactory;
import mades.environment.EnvironmentMemento;
import mades.environment.SignalMap;
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

	private static final String SIGNALS = "mades:signals";
	private static final String VARIABLE_ID = "id";
	private static final String VARIABLE_ANNOTATION = "annotation";
	private static final String VARIABLE_VALUE = "value";
	private static final String VARIABLE_TYPE = "type";
	private static final String VARIABLE_SCOPE = "scope";
	private static final String VARIABLE_SYSTEM_NAME = "systemName";
	private static final String VARIABLE_ENVIRONMENT_NAME = "environmentName";
	private static final String VARIABLE_THRESHOLD = "threshold";
	private static final String VARIABLE = "mades:variable";
	
	private Logger logger;
	private String filename;
	private Clock clock;
	private VariableFactory variableFactory;

	private SystemMemento systemMemento;
	private ArrayList<VariableAssignment> systemVariables;
	private EnvironmentMemento environmentMemento;
	private ArrayList<VariableAssignment> environmentVariables;
	
	/**
	 * Stores all the defined variables by id as they are defined
	 * in the xml file.
	 */
	private HashMap<String, VariableDefinition> definedVariablesMap;
	
	/**
	 * Stores all the triggers defined in the xml file. 
	 */
	private ArrayList<Trigger> triggers;
	
	
	private String currentStringData;
	private String currentQName;
	
	/**
	 * @param logger
	 * @param filename
	 * @param variableFactory
	 */
	public InputParser(Logger logger, Clock clock,
			VariableFactory variableFactory, String filename) {
		this.logger = logger;
		this.clock = clock;
		this.filename = filename;
		this.variableFactory = variableFactory;
	}
	
	public void parseDocument() {

		//get a factory
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		try {

			//get a new instance of parser
			SAXParser parser = parserFactory.newSAXParser();

			//parse the file and also register this class for call backs
			parser.parse(filename, this);

		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		currentStringData = new String(ch,start,length);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		if (SIGNALS.equalsIgnoreCase(qName)) {
			String[] ids = currentStringData.split(",");
			// TODO(rax): collect the signal groups
		}
		
		currentQName = null;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		systemVariables = new ArrayList<VariableAssignment>();
		environmentVariables = new ArrayList<VariableAssignment>();
		definedVariablesMap = new HashMap<String, VariableDefinition>();
		triggers = new ArrayList<Trigger>();
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		currentStringData = null;
		currentQName = qName;
		
		if (VARIABLE.equalsIgnoreCase(qName)) {
			String environmentName = attributes.getValue(VARIABLE_ENVIRONMENT_NAME);
			String systemName = attributes.getValue(VARIABLE_SYSTEM_NAME);
			Scope scope = parseScope(attributes.getValue(VARIABLE_SCOPE));
			Type type = parseType(attributes.getValue(VARIABLE_TYPE));
			
			VariableDefinition def = variableFactory.define(
					systemName, environmentName, scope, type);
			
			String id = attributes.getValue(VARIABLE_ID);
			definedVariablesMap.put(id, def);
			
			String value = attributes.getValue(VARIABLE_VALUE);
			if (type == Type.STRING) {
				value = "\"" + value + "\"";
			}
			
			String annotation = attributes.getValue(VARIABLE_ANNOTATION);
			if (annotation == null) {
				annotation = "";
			}
			
			VariableAssignment var = new VariableAssignment(def, value, annotation);
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
			
			String threshold = attributes.getValue(VARIABLE_THRESHOLD);
			if (threshold != null) {
				// Add threshold
				String thresholdName = "threshold_" + environmentName.replace('.', '_')
						.replace('(', '_')
						.replace(')', '_');
				VariableDefinition thresholdDef = variableFactory.define(
						thresholdName, thresholdName,
						Scope.ENVIRONMENT_INTERNAL,
						Type.DOUBLE);
				VariableAssignment thresholdVar = new VariableAssignment(
						thresholdDef, threshold, "");
				environmentVariables.add(thresholdVar);
				
				// Add trigger
				String signalName = "signal_" + environmentName.replace('.', '_')
						.replace('(', '_')
						.replace(')', '_');
				VariableDefinition signalDef = variableFactory.define(
						signalName, signalName,
						Scope.ENVIRONMENT_INTERNAL,
						Type.BOOLEAN);
				String signalValue = 
					    Double.parseDouble(value) < Double.parseDouble(threshold) ? 
					    		"0" : "1";
				VariableAssignment signalVar = new VariableAssignment(
						signalDef, signalValue, "");
				environmentVariables.add(signalVar);
				
				triggers.add(new Trigger(var, thresholdVar, signalVar));
			}
			
		} //else if () {}
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
				environmentVariables, new SignalMap());
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
	 * @return the triggers
	 */
	public ArrayList<Trigger> getTriggers() {
		return triggers;
	}
	
	
}
