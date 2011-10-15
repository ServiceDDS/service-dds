package org.sddsxmpp.commands;
import org.jivesoftware.smack.packet.IQ;


public class MyNewCommandIQ extends IQ {

	String dataType;
	String instanceName;
	
	public MyNewCommandIQ(String type, String name) {
		super();
		this.setType(IQ.Type.SET);
		this.dataType = type;
		this.instanceName = name;
	}
	@Override
	public String getChildElementXML() {
		// TODO Auto-generated method stub
		return new String(
				"<sdds xmlns=\"sddsopenfire\">" +
					" <new" +
						" datatype=\""+this.dataType+"\"" +
						" instancename=\""+this.instanceName+"\">" +
					" </new>" +
				"</sdds>"		
		);
	}

}
