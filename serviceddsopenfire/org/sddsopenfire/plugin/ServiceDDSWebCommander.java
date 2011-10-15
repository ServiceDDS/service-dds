package org.sddsopenfire.plugin;

import java.lang.reflect.Field;

import org.xmpp.packet.JID;
import DDS.HistoryQosPolicyKind;
import ServiceDDS.Group;
import ServiceDDS.Peer;
import ServiceDDS.QoSParameters;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;
import ServiceDDS.servicetopic.ServiceTopic;
import ServiceDDS.servicetopic.ServiceTopicListener;

/**
 * The ServiceDDSWebCommander methods make calls to the ServiceDDS API.
 * @author Jose Angel Dianes
 * @version 0.1b 09/24/2010
 *
 */
public class ServiceDDSWebCommander {

	private ServiceDDSOpenfire plugin;

	public ServiceDDSWebCommander(ServiceDDSOpenfire p) {
		this.plugin = p;
	}
	/**
	 * Creates a new couple service-topics, one for writing and one for reading. In futre versions there will be separate
	 * methods for each one.
	 * @param peer The ServiceDDS peer that will create the service-topics
	 * @param topicDataTypeName The topic data type name
	 * @param topicName The topic ID
	 * @throws ClassNotFoundException This exception is thrown when there is not a class in the server with the name specified in topicDataTypeName
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ImpossibleToCreateDDSTopic 
	 */
	public void newTopicInstance(Peer peer, 
			String topicDataTypeName, String topicName) 
			throws ClassNotFoundException, 
				   InstantiationException, 
				   IllegalAccessException, ImpossibleToCreateDDSTopic {
//	  System.out.println("ServiceDDSWebCommander.newTopicInstance()");
	    WebPeer webPeer = this.plugin.peerTable.get(peer.getName());
	    if (peer!=null) {
			// Obtain topic data type class
			Class topicClass = Class.forName(topicDataTypeName);
			// Create an instance to put the data in
			Object topicData = topicClass.newInstance();
			// OJO, QoS PROVISIONAL!!!
			QoSParameters serviceTopicQoS = new QoSParameters();
	    	serviceTopicQoS.setHistory(HistoryQosPolicyKind.KEEP_LAST_HISTORY_QOS, 3);
	    	serviceTopicQoS.setDeadline(30, 0);
			webPeer.serviceTopics.put(topicName,peer.newReaderWriterServiceTopic(topicData, topicName, null,null));//serviceTopicQoS);
			//webPeer.serviceTopics.add(peer.newWriterServiceTopic(topicData, topicName, null));//serviceTopicQoS);
	    }
	}

	/**
	 * Method for joining a ServiceDDS peer to a group
	 * @param peer The ServiceDDs peer
	 * @param groupName The name of the group
	 */
	public void joinGroup(Peer peer, String groupName) {
		peer.joinGroup(new Group(groupName));
	}

	/**
	 * Writes a sample into a topic
	 * @param peer The ServiceDDS peer that performs the action
	 * @param instanceName The topic instance ID
	 * @param data The fields of the data sample
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void writeTopic(Peer peer, String instanceName, Object[] data) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//		  System.out.println("ServiceDDSWebCommander.writeTopic()");		
		// Get the instance
		ServiceTopic serviceTopic=null;
		WebPeer webPeer = this.plugin.peerTable.get(peer.getName());
		if (webPeer != null) serviceTopic = webPeer.serviceTopics.get(instanceName);
		if (serviceTopic != null) {
//		  System.out.println("ServiceDDSWebCommander.writeTopic(): service topic found = "+serviceTopic.toString()+" of type "+serviceTopic.topicDataType.getClass()+" ID = "+serviceTopic.topicID);
			// Obtain topic data type class
			Class topicClass = serviceTopic.topicDataType.getClass();
			// Create an instance to put the data in
			Object topicSample = topicClass.newInstance();
			// Fill the instance with the given data to create a sample
			Field[] fields = topicClass.getDeclaredFields();
			for (int i = 0; i< fields.length; i++) {
				fields[i].set(topicSample,data[i]);
			}
			// Write the sample
			serviceTopic.write(topicSample);
		}
	}
	
	/**
	 * Allows a peer to listen to new samples in a topic
	 * @param peer The ServiceDDS peer
	 * @param instanceName The topic instance ID
	 * @param serverAddress The XMPP address (jid)
	 */
	public void listenToTopic(
			WebPeer peer, 
			String instanceName, 
			JID serverAddress) {
//	  System.out.println("ServiceDDSWebCommander.listenToTopic()");		
		// Get the instance
		ServiceTopic serviceTopic = peer.serviceTopics.get(instanceName);
		// If there is an instance for that topic...
		if (serviceTopic != null) {
//		  System.out.println("ServiceDDSWebCommander.listenToTopic(): service topic found = "+serviceTopic.toString()+" of type "+serviceTopic.topicDataType.getClass()+" ID = "+serviceTopic.topicID);
		    ServiceTopicListener listener = new ServiceDDSWebClientListener(peer.session, serverAddress);
		    serviceTopic.addListener(listener);
		} else {
			System.err.println("ServiceDDSWebCommander.listenToTopic(): not able to find topic instance '"+instanceName+"' in web peer "+peer.name);
		}
	}
	
}
