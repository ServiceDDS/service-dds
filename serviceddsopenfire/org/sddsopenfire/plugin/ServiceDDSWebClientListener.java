package org.sddsopenfire.plugin;

import java.lang.reflect.Field;
import java.util.TimerTask;
import org.jivesoftware.openfire.MessageRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.util.TaskEngine;
import org.sddsxmpp.commands.MyOnDataAvailableEventElement;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketExtension;

import ServiceDDS.servicetopic.ServiceTopic;
import ServiceDDS.servicetopic.ServiceTopicListener;

/**
 * An instance of this class is created when a client starts to listen to a topic. The instance is subscribed to ServiceDDS events
 * about the service topic, and when notified, will translate the notification to the client.
 * @author Jose Angel Dianes
 * @version 0.1b 09/24/2010
 *
 */
public class ServiceDDSWebClientListener implements ServiceTopicListener {
	/**
	 * This attribute identifies the XMPP client and is used to send it the data
	 */
	Session session;
	/**
	 * The server, used for setting the 'from' attribute in the notification
	 */
	JID serverAddress;
	/**
	 * To send the message
	 */
	MessageRouter router = XMPPServer.getInstance().getMessageRouter();
	
	/**
	 * The basic constructor
	 * @param session Identifies the XMPP client
	 * @param serverAddress Identifies the XMPP server
	 */
	public ServiceDDSWebClientListener(
			Session session, JID serverAddress) {
		this.session = session;
		this.serverAddress = serverAddress;
	}
	
	/**
	 * For receiving the on_data_available event from ServiceDDS
	 */
	@Override
	public void on_data_available(ServiceTopic arg0) {
//	  System.out.println("ServiceDDSWebClientListener.on_data_available()");
		// Obtain topic data type class
		Class topicClass = arg0.topicDataType.getClass();
		// Take the samples from the service-topic (default writer)
		Object[] samples = arg0.take();
		// For every taken sample...
		for (int j=0; j<samples.length; j++) {
			// For every field in the sample...
			Field[] fields = topicClass.getDeclaredFields();
			Object[] data = new Object[fields.length];
			for (int i = 0; i< fields.length; i++) {
				try {
					data[i] = fields[i].get(samples[j]);
//					System.out.println("ServiceDDSWebClientListener.on_data_available(): data field = "+data[i]);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Finally set the Message data... (we wait for no reply)
			sendMessageToClient("on_data_available",arg0.topicID,data);
			
//			final IQ iq = new IQ();
//			iq.setType(IQ.Type.result);
//			iq.setFrom(this.serverAddress);
//			iq.setTo(session.getAddress());	
//			//iq.addExtension(new PacketExtension("sdds","sddsopenfire"));
//			//iq.setChildElement(new MyOnDataAvailableEventElement(arg0.topicID,data));
//			// Send the message to the client
//			TimerTask messageTask = new TimerTask() {
//				public void run() {
//					System.out.println("ServiceDDSWebClient: sending on_data_available event command "+iq.toXML());
//					router.route(iq);
//				}
//			};
//			TaskEngine.getInstance().schedule(messageTask,5000);
		}
	}

	@Override
	public void on_requested_deadline_missed(ServiceTopic arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void on_sample_lost(ServiceTopic arg0) {
		// TODO Auto-generated method stub

	}

	/** 
	 * Sends the data to the client. For internal use only.
	 * @param body
	 * @param topicName
	 * @param data
	 */
	private void sendMessageToClient(String body, String topicName, Object[] data) {
//		System.out.println("ServiceDDSWebClientListener.sendMessageToClient(): data length = "+data.length);
		// Create a new Message object.
		final Message message = new Message();
		
		// Set the "to" field on the message by getting the address of the user that just signed in. 
		message.setTo(session.getAddress());
//		System.out.println("ServiceDDSOpenfireSessionEventListener.sessionCreated(): to = "
//				+session.getAddress().getNode()
//				+"@"+session.getAddress().getDomain());
		
		// Set the "from" address, using the serverAddress we constructed in the #initializePlugin method. 
		message.setFrom(this.serverAddress);
//		System.out.println("ServiceDDSOpenfireSessionEventListener.sessionCreated(): from = "
//				+this.serverAddress.getNode()
//				+"@"+this.serverAddress.getDomain());

		// Set the "subject"
		message.setSubject(topicName+".on_data_available");
//		System.out.println("ServiceDDSOpenfireSessionEventListener.sessionCreated(): subject = "+message.getSubject());

		// Set the "body" using the value from retrieved from the #getMessage method.
		message.setBody(body);//getMessage());
//		System.out.println("ServiceDDSOpenfireSessionEventListener.sessionCreated(): body = "+message.getBody());
		message.addExtension(
				new SamplePacketExtension(
						new MyOnDataAvailableEventElement(topicName,data)));
		TimerTask messageTask = new TimerTask() {
			public void run() {
//			System.out.println("ServiceDDSWebClientListener.sendMessageToClient(): routing message = "+message.toXML());
				router.route(message);
        	}
		};

		TaskEngine.getInstance().schedule(messageTask, 5000);
	}
	
}
