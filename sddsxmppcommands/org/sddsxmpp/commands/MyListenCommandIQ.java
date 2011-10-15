package org.sddsxmpp.commands;
import org.jivesoftware.smack.packet.IQ;


public class MyListenCommandIQ extends IQ {

	String instanceName;
	
	public MyListenCommandIQ(String name) {
		super();
		this.setType(IQ.Type.SET);
		this.instanceName = name;
	}
	@Override
	public String getChildElementXML() {
		return new String(
				"<sdds xmlns=\"sddsopenfire\">" +
				" <listen instancename=\""+this.instanceName+"\">" +
				" </listen>" +
				"</sdds>"		
		);
	}

}
