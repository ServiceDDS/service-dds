package org.sddsxmpp.commands;

import org.dom4j.Namespace;
import org.dom4j.tree.DefaultElement;

public class MyOnDataAvailableEventElement extends DefaultElement {

	
	public MyOnDataAvailableEventElement(String name, Object[] data) {
		super("sdds",new Namespace("","sddsopenfire"));
		this.addAttribute("command", "on_data_available");
		DefaultElement sample = new DefaultElement("sample",new Namespace("","sddsopenfire"));
		sample.addAttribute("instancename", name);
		// Process data fields
		for (int i=0; i<data.length; i++) {
			DefaultElement newElement = new DefaultElement("samplefield",new Namespace("","sddsopenfire"));
			String type = new String("string");
			if (data[i] instanceof Double) {
				type = "double";
			} else if(data[i] instanceof Float) {
				type = "float";
			} if (data[i] instanceof Short) {
				type = "short";
			} if (data[i] instanceof Integer) {
				type = "long";
			} if (data[i] instanceof Boolean) {
				type = "bool";
			} 		
			newElement.addAttribute("dataype", type);
			newElement.addAttribute("value",data[i].toString());
			sample.add(newElement);
		}
		this.add(sample);
	}
		
}
