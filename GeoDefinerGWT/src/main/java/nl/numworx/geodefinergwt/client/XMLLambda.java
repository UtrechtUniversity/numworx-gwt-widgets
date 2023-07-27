package nl.numworx.geodefinergwt.client;

import java.io.IOException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

import fi.euclides.expr.Lambda;
import fi.euclides.model.Codec;
import fi.euclides.model.Destroyable;
import fi.euclides.model.Label;
import fi.euclides.util.DefaultAdapter;
import fi.euclides.util.Observable;
import nl.tue.win.riaca.openmath.lang.OMApplication;
import nl.tue.win.riaca.openmath.lang.OMBinding;
import nl.tue.win.riaca.openmath.lang.OMFloat;
import nl.tue.win.riaca.openmath.lang.OMInteger;
import nl.tue.win.riaca.openmath.lang.OMObject;
import nl.tue.win.riaca.openmath.lang.OMSymbol;
import nl.tue.win.riaca.openmath.lang.OMVariable;

public class XMLLambda extends Lambda {

	public XMLLambda() {
	}

	public Destroyable[] createDepend(Codec codec, Label label)
			throws IOException {
		try {
			DefaultAdapter.getDefault(label).put(OMObject.class, parse(label.getString()));
		} catch (DOMParseException e) {
			throw (IOException) new IOException(e.getMessage()).initCause(e);
		}
		return super.createDepend(codec, label);
	}

	private OMObject parse(String string) throws DOMParseException {
		Document document = XMLParser.parse(string);
		Node root = document.getDocumentElement();
		return toOMObject(root);
	}

	private OMObject toOMObject(Node root) {
		if(root.getNodeType() != Node.ELEMENT_NODE)
			return null;
		String name = root.getNodeName();
		NamedNodeMap attr = root.getAttributes();
		if("OMA".equals(name))
			return toOMA(root.getChildNodes());
		if("OMS".equals(name))
			return new OMSymbol(attr.getNamedItem("cd").getNodeValue(), attr.getNamedItem("name").getNodeValue());
		if("OMV".equals(name))
			return new OMVariable(attr.getNamedItem("name").getNodeValue());
		if("OMBIND".equals(name)) {
			return toOMBinding(root.getChildNodes());
		}
		if("OMI".equals(name)) {
			return toOMInteger(root.getChildNodes());
		}
		if("OMF".equals(name)) {
			Node dec = attr.getNamedItem("dec");
			if(dec != null)
				return new OMFloat(dec.getNodeValue(), "dec");
			else
				return new OMFloat(attr.getNamedItem("hex").getNodeValue(),"hex");
		}
// ETC...
		return null;
	}

	
	private OMObject toOMInteger(NodeList nodes) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < nodes.getLength(); i++) {
			sb.append(nodes.item(i).getNodeValue());
		}
		return new OMInteger(sb.toString());
	}

	@Override
	public boolean define(Label l) {
		writeXML(l);
		return super.define(l);
	}


	public void writeXML(Label l) {
		l.setString(l.adapt(OMObject.class).toString()); // bijna goed
	}

	private OMApplication toOMA(NodeList nodes) {
		OMApplication result = new OMApplication();
		for(int i = 0; i < nodes.getLength(); i++) {
			OMObject o = toOMObject(nodes.item(i));
			if(o != null)
				result.addElement(o);
		}
		return result;
	}
	private OMBinding toOMBinding(NodeList nodes) {
		OMBinding result = new OMBinding();
		result.setBinder(toOMObject ( nodes.item(0) ));
		result.setBody(toOMObject(nodes.item(2)));
		nodes = nodes.item(1).getChildNodes();		
		for(int i = 0; i < nodes.getLength(); i++) {
			OMObject o = toOMObject(nodes.item(i));
			if(o != null)
				result.addVariable(o);
		}
		return result;
	}

	
}
