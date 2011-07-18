/**
 * 
 */
package mades.cosimulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import mades.common.timing.Time;
import mades.common.variables.Scope;
import mades.common.variables.Type;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.common.variables.VariableFactory;

import com.google.common.collect.TreeMultimap;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class OutputWriter {
	private TreeMultimap<Time, VariableAssignment> sharedVariablesMultimap;
	private ArrayList<VariableDefinition> variables;

	/**
	 * @param sharedVariablesMultimap
	 */
	public OutputWriter(VariableFactory variableFactory,
			TreeMultimap<Time, VariableAssignment> sharedVariablesMultimap) {
		this.sharedVariablesMultimap = sharedVariablesMultimap;
		variables = new ArrayList<VariableDefinition>();
		for (VariableDefinition def: variableFactory.getDefinedVariables()){
			Scope s = def.getScope(); 
			if (s == Scope.SYSTEM_SHARED || s == Scope.ENVIRONMENT_SHARED) {
				variables.add(def);
			}
		}
		
	}
	
	public void writeXmlFile(String filename) {
		try {
			PrintWriter printWriter = new PrintWriter(filename);
			XMLOutputFactory xmlFactory = XMLOutputFactory.newFactory();
			XMLStreamWriter xmlWriter = xmlFactory.createXMLStreamWriter(printWriter);
			
			xmlWriter.writeStartDocument();
			Set<Time> keys = sharedVariablesMultimap.keySet();
			xmlWriter.writeStartElement("Mades:cosimulation");
			for (Time time: keys) {
				xmlWriter.writeStartElement("Mades:step");
				xmlWriter.writeAttribute("continuous", "" + time.getSimulationTime());
				xmlWriter.writeAttribute("discrete", "" + time.getSimulationStep());
				Set<VariableAssignment> vars = sharedVariablesMultimap.get(time);
				for(VariableAssignment v: vars) {
					xmlWriter.writeEmptyElement("Mades:variable");
					xmlWriter.writeAttribute("systemName", 
							v.getVariableDefinition().getSystemName());
					xmlWriter.writeAttribute("environmentName", 
							v.getVariableDefinition().getEnvironmentName());
					xmlWriter.writeAttribute("value", 
							v.getValue());
				}
				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();
			xmlWriter.writeEndDocument();
			xmlWriter.flush();
			xmlWriter.close();
			
		} catch(XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the variables
	 */
	public ArrayList<VariableDefinition> getVariables() {
		return variables;
	}
	
	
	public DataSource getDataSource(VariableDefinition def) {
		if (def.getType() == Type.STRING) {
			throw new AssertionError("Cannot print not numerical variable");
		}
		
		DataTable variableDataSource = new DataTable(Double.class, Double.class);
		Set<Time> keys = sharedVariablesMultimap.keySet();
		for (Time time: keys) {
			Set<VariableAssignment> vars = sharedVariablesMultimap.get(time);
			for(VariableAssignment v: vars) {
				// same instance!
				if (v.getVariableDefinition() == def) {
					variableDataSource.add(time.getSimulationTime(),
							Double.parseDouble(v.getValue()));
				}
			}
		}
		return variableDataSource;
	}
}
