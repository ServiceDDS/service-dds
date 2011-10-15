package org.sddsxmpp.commands.packetextensions;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;


public class SDDSPacketExtensionProvider implements PacketExtensionProvider {

	@Override
	public PacketExtension parseExtension(XmlPullParser arg0) throws Exception {
//		System.out.println("SDDSPacketExtensionProvider.parseExtension(): "+arg0.getText());
//		System.out.println("SDDSPacketExtensionProvider.parseExtension(): attribute count = "+arg0.getAttributeCount());
		return new SDDSPacketExtension(arg0.getAttributeValue(0));
	}

}
