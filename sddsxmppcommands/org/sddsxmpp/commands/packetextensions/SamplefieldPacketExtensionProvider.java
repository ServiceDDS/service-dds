package org.sddsxmpp.commands.packetextensions;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;


public class SamplefieldPacketExtensionProvider implements PacketExtensionProvider {

	@Override
	public PacketExtension parseExtension(XmlPullParser arg0) throws Exception {
//		System.out.println("SamplefieldPacketExtensionProvider.parseExtension(): "+arg0.getText());
//		System.out.println("SamplefieldPacketExtensionProvider.parseExtension(): attribute count = "+arg0.getAttributeCount());
		return new SamplefieldPacketExtension(
				arg0.getAttributeValue(0),
				arg0.getAttributeValue(1)
		);
	}

}
