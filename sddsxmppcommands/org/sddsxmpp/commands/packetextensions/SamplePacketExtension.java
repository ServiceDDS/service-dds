package org.sddsxmpp.commands.packetextensions;
import org.jivesoftware.smack.packet.PacketExtension;


public class SamplePacketExtension implements PacketExtension {

	public String instanceName; 
	
	public SamplePacketExtension(String instanceName) {
		this.instanceName = instanceName;
//		System.out.println("SamplePAcketExtension: creating with name = "+instanceName);
	}
	
	@Override
	public String getElementName() {
		return "sample";
	}

	@Override
	public String getNamespace() {
		return "sddsopenfire";
	}

	@Override
	public String toXML() {
		return new String(
				"<sample instancename=\""+this.instanceName+"\">"	
		);
	}

}
