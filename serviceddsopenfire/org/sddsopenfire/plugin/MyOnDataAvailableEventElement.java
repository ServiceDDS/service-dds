package org.sddsopenfire.plugin;

import org.dom4j.Element;
import org.xmpp.packet.PacketExtension;

public class MyOnDataAvailableEventElement extends PacketExtension {

	
	private String name;
	private Object[] data;

	public MyOnDataAvailableEventElement(String name, Object[] data) {
		super("sdds","sddsopenfire");
		this.name = name;
		this.data = data;
	}
	
	public Element getElement() {
		return new org.sddsxmpp.commands.MyOnDataAvailableEventElement(name, data);
	}
	
	
		
}
