package org.sddsxmpp.commands.packetextensions;
import org.jivesoftware.smack.packet.PacketExtension;


public class SamplefieldPacketExtension implements PacketExtension {

	public String dataType;
	public String value;
	
	public SamplefieldPacketExtension(String dataType, String value) {
		this.dataType = dataType;
		this.value = value;
//		System.out.println("SamplefieldPacketExtension: creating with type = "+dataType+" and value= "+this.value);
	}
	
	@Override
	public String getElementName() {
		return "samplefield";
	}

	@Override
	public String getNamespace() {
		return "sddsopenfire";
	}

	@Override
	public String toXML() {
		return new String(
				"<samplefield dataype=\""+this.dataType+"\" value=\""+this.value+"\">"	
		);
	}

}
