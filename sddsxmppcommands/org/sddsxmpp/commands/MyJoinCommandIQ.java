package org.sddsxmpp.commands;
import org.jivesoftware.smack.packet.IQ;


public class MyJoinCommandIQ extends IQ {

	String group;
	
	public MyJoinCommandIQ(String groupName) {
		super();
		this.setType(IQ.Type.SET);
		this.group = groupName;
	}
	@Override
	public String getChildElementXML() {
		// TODO Auto-generated method stub
		return new String(
				"<sdds xmlns=\"sddsopenfire\">" +
				" <join group=\""+this.group+"\">" +
				" </join>" +
				"</sdds>"		
		);
	}

}
