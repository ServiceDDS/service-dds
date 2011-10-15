package org.sddsxmpp.commands;

import org.jivesoftware.smack.packet.Packet;


public class MyJoinCommandPacket extends Packet {

	String groupName;
	
	public MyJoinCommandPacket(String group) {
		super();
		this.groupName = group;
	}
	
	@Override
	public String toXML() {
		return new String(
				"<sdds type=join group=\""+this.groupName+"\">" +
				"</sdds>"		
		);
	}

}
