package org.sddsopenfire.plugin;

import org.dom4j.QName;
import org.xmpp.packet.PacketExtension;

/**
 * An XML extension to manage custom XMPP stanzas when notifying clients of new samples in topics. 
 * @author Jose Angel Dianes
 * @version 0.1b 09/24/2010
 */
public class SamplePacketExtension extends PacketExtension {

	public SamplePacketExtension(String name, String namespace) {
		super(name, namespace);
		registeredExtensions.put(new QName(name), MyOnDataAvailableEventElement.class);
	}
	
	public SamplePacketExtension(org.dom4j.Element element) {
		super(element);
		registeredExtensions.put(element.getQName(), MyOnDataAvailableEventElement.class);
	}

}
