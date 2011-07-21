/**
 * 
 */
package mades.environment.modelica;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import mades.common.timing.Clock;
import mades.common.variables.VariableAssignment;
import mades.environment.EnvironmentMemento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class InitXmlUpdater {

	private String filePath;
	private HashMap<String, String> variableMap;
	private HashMap<String, String> experimentMap;
	
	public InitXmlUpdater(EnvironmentMemento memento, String filePath, Clock clock) {
		this.filePath = filePath;
		variableMap = new HashMap<String, String>();
		for (VariableAssignment v: memento.getParams()) {
			variableMap.put(v.getVariableDefinition().getEnvironmentName(), v.getValue());
		}
		experimentMap = new HashMap<String, String>();
		experimentMap.put("stopTime", "" + clock.getCurrentTime().getSimulationTime());
		experimentMap.put("startTime", "" + (clock.getCurrentTime().getSimulationTime() - 
				clock.getTimeStep()));
	}
	
	private void updateDefaultExperiment(Document doc) {
		Node staff = doc.getElementsByTagName("DefaultExperiment").item(0);
		NamedNodeMap attributes = staff.getAttributes();
		for (int i = 0; i< attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			String var = experimentMap.get(attribute.getNodeName());
			if (var != null) {
				attribute.setTextContent(var);
			}
		}
	}
	
	private void updateScalarVariable(Document doc) {
		NodeList variableNodes = doc.getElementsByTagName("ScalarVariable");
		for (int i = 0; i < variableNodes.getLength(); i++) {
			Node node = variableNodes.item(i);
			NamedNodeMap attributes = node.getAttributes();
			Node attribute = attributes.getNamedItem("name");
			String varname = attribute.getNodeValue();
			String var = variableMap.get(varname);
			if (var != null) {
				NodeList children = node.getChildNodes();
				for (int j = 0; j < children.getLength(); j++) {
					Node child = children.item(j);
					if ("Real".equalsIgnoreCase(child.getNodeName())) {
						NamedNodeMap childAttributes = child.getAttributes();
						Node start = childAttributes.getNamedItem("start");
						if (start != null) {
							start.setNodeValue(var);
						}
					}
				}
			}
		}
	}
	
	public void doUpdate() throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(filePath);
		
		Element root = doc.getDocumentElement(); 
		if (root == null) {
			throw new RuntimeException("Empty XML document");
		}
		
		// DefaultExperiment
		updateDefaultExperiment(doc);
		
		// ScalarVariable
		updateScalarVariable(doc);
		
	    // Write the content into xml file
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    DOMSource source = new DOMSource(doc);
	    StreamResult result =  new StreamResult(new File(filePath));
	    transformer.transform(source, result);
	}
}
