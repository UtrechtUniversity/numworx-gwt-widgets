package nl.numworx.aimodelgwt.client;

import java.util.Map;
import java.util.TreeMap;

class Tupel {
	
	final static String INITIAL = "initial";
	
	String name;
	String value;
	String type;
	
	Map<String,String> toJSON() {
		TreeMap<String,String> result = new TreeMap<>();
		result.put("name", name);
		result.put("value", value);
		result.put("type", type);
		return result;
	}

	Tupel(String name, String value, String type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}
	
	Tupel(String name, String value) {
		this(name, value, INITIAL);
	}

	
	
}
