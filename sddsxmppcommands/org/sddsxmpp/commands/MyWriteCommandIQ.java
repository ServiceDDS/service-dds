package org.sddsxmpp.commands;
import org.jivesoftware.smack.packet.IQ;


public class MyWriteCommandIQ extends IQ {

	String instanceName;
	Object[] data;
	
	public MyWriteCommandIQ(String name, Object[] data) {
		super();
		this.setType(IQ.Type.SET);
		this.instanceName = name;
		this.data = data;
	}
	@Override
	public String getChildElementXML() {
		// TODO Auto-generated method stub
		String res = new String(
				"<sdds xmlns=\"sddsopenfire\">" +
					" <write instancename=\""+this.instanceName+"\">");
		for (int i=0; i<this.data.length; i++) {
			res = res +" <samplefield datatype=\"";
			String type = new String("string");
			if (this.data[i] instanceof Double) {
				type = "double";
			} else if(this.data[i] instanceof Float) {
				type = "float";
			} if (this.data[i] instanceof Short) {
				type = "short";
			} if (this.data[i] instanceof Integer) {
				type = "long";
			} if (this.data[i] instanceof Boolean) {
				type = "bool";
			} 		
			res = res+type+"\" value=\""+this.data[i]+"\">" +
					   " </samplefield>";
		}
		return  res +
					" </write>" +
				"</sdds>";
	}

}
