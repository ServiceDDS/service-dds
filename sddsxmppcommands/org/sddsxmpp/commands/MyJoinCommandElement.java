package org.sddsxmpp.commands;

import org.dom4j.tree.DefaultElement;

public class MyJoinCommandElement extends DefaultElement {

	String group;
	
	public MyJoinCommandElement(String groupName) {
		super("sdds");
		this.addAttribute("groupname", groupName);
		this.group = groupName;		
	}

}
