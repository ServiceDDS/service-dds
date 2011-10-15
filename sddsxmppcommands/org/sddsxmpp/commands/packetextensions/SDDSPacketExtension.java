package org.sddsxmpp.commands.packetextensions;
import org.jivesoftware.smack.packet.PacketExtension;


public class SDDSPacketExtension implements PacketExtension {

	public String command; 
	
	public SDDSPacketExtension(String command) {
		this.command = command;
//		System.out.println("SDDSPacketExtension: creating with command = "+command);
	}
	
	@Override
	public String getElementName() {
		return "sdds";
	}

	@Override
	public String getNamespace() {
		return "sddsopenfire";
	}

	@Override
	public String toXML() {
		return new String(
				"<sdds xmlns=\"sddsopenfire\" command=\""+this.command+"\"></sdds>"	
		);
	}

}
