package org.sddsxmpp.commands;

import org.w3c.dom.Element;
import org.xmpp.packet.IQ;


public class MyOnDataAvailableEventIQ extends IQ {

	String instanceName;
	Object[] data;
	
	public MyOnDataAvailableEventIQ(String name, Object[] data) {
		super();
		this.setType(Type.set);
		this.instanceName = name;
		this.data = data;
	//	this.setChildElement(new MyOnDataAvailableEventElement(name,data));
	}
		
//	public String getChildElementXML() {
//		// TODO Auto-generated method stub
//		String res = new String(
//				"<sdds xmlns=\"sddsopenfire\">" +
//					" <on_data_available instancename=\""+this.instanceName+"\">");
//		for (int i=0; i<this.data.length; i++) {
//			res = res +" <samplefield datatype=\"";
//			String type = new String("string");
//			if (this.data[i] instanceof Double) {
//				type = "double";
//			} else if(this.data[i] instanceof Float) {
//				type = "float";
//			} if (this.data[i] instanceof Short) {
//				type = "short";
//			} if (this.data[i] instanceof Integer) {
//				type = "long";
//			} if (this.data[i] instanceof Boolean) {
//				type = "bool";
//			} 		
//			res = res+type+"\" value=\""+this.data[i]+"\">" +
//					   " </samplefield>";
//		}
//		return  res +
//					" </on_data_available>" +
//				"</sdds>";
//	}

}
