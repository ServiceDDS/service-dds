/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS;

import java.util.Hashtable;

import DDS.ANY_STATUS;
import DDS.DomainParticipant;
import DDS.DomainParticipantFactory;
import DDS.HistoryQosPolicyKind;
import DDS.PARTICIPANT_QOS_DEFAULT;
import DDS.Publisher;
import DDS.PublisherQosHolder;
import DDS.ReliabilityQosPolicyKind;
import DDS.Subscriber;
import DDS.SubscriberQosHolder;
import DDS.TopicQosHolder;
import ServiceDDS.exception.ImpossibleToCreateDDSDomainParticipant;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;
import ServiceDDS.service.RemoteServiceInstance;
import ServiceDDS.service.Service;
import ServiceDDS.service.contract.ServiceContract;
//import ServiceDDS.service.contract.ServiceContractTemplate;
import ServiceDDS.service.discoveryengine.ServiceDiscoveryEngine;
import ServiceDDS.service.operativeunit.OperationInvocationUnit;
import ServiceDDS.service.operativeunit.provider.ServiceProvider;
import ServiceDDS.service.registry.LocalServicesRegistry;
import ServiceDDS.service.registry.RemoteServicesRegistry;
import ServiceDDS.servicetopic.ContentFilteredReaderServiceTopic;
import ServiceDDS.servicetopic.ReaderServiceTopic;
import ServiceDDS.servicetopic.ReaderWriterServiceTopic;
import ServiceDDS.servicetopic.ServiceTopic;
import ServiceDDS.servicetopic.WriterServiceTopic;

/**
 * Every participant in ServiceDDS must be represented by a Peer in order
 * to use the rest of the interaction mechanisms. Therefore, this integration
 * mechanism represents the basic deployment unit. Using Peers, applications
 * can specify its capabilities and requirements, join to groups and interact 
 * with other Peer if they have a group in common.
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 * 
 */
public class Peer {
 
	/* DDS related entities*/
    DomainParticipantFactory   dpf;
    DomainParticipant participant;
    Publisher publisher;
    Subscriber subscriber;
    TopicQosHolder defaultTopicQos;
    int status;
    PublisherQosHolder pubQos = new PublisherQosHolder();
    SubscriberQosHolder subQos = new SubscriberQosHolder();    
	
	ServiceDiscoveryEngine sde;
    LocalServicesRegistry lsr;
    RemoteServicesRegistry rsr;
    OperationInvocationUnit oiu;
    public String name;
	
    Hashtable<String,ServiceTopic> serviceTopicTable = new Hashtable<String, ServiceTopic>();
    Hashtable<String,Group> groupTable = new Hashtable<String,Group>();

    /**
     * Creates a Peer with a given name. The peer is joined automatically in
     * the Group SERVICEDDS.
     * @param peerName - The name of the Peer. 
     */
    public Peer(String peerName) throws ImpossibleToCreateDDSDomainParticipant { 
    	this.defaultTopicQos = new TopicQosHolder();   
    	groupTable.put("SERVICEDDS", new Group("SERVICEDDS"));
        createDDSEntities();    
        this.name = peerName;
        lsr = new LocalServicesRegistry();
        rsr = new RemoteServicesRegistry();
        oiu = new OperationInvocationUnit(this);
        sde = new ServiceDiscoveryEngine(
        		this, 
        		this.groupTable.get("SERVICEDDS"), 
        		lsr, rsr);
    }
    
    /***
     * Joins the Peer to a Group.
     * @param g The Group where the Peer will join to.
     */
    public void joinGroup(Group g) {
    	// First, we have to check credentials
    	// TODO
    	
    	// Insert the group in the table
    	this.groupTable.put(g.name, g);
    	
    	// Change partitions in pub/sub QoS
    	String[] oldPartitions = pubQos.value.partition.name;
        pubQos.value.partition.name = new String[oldPartitions.length+1];
        for (int i=0; i<oldPartitions.length; i++) {
        	pubQos.value.partition.name[i] = oldPartitions[i];
        }
        pubQos.value.partition.name[oldPartitions.length] = g.name;
        status = this.publisher.set_qos(pubQos.value);
        ErrorHandler.checkStatus(
                status, "ServiceDDS.Peer.publisher.joinGroup");
        
        
    	oldPartitions = subQos.value.partition.name;
    	subQos.value.partition.name = new String[oldPartitions.length+1];
        for (int i=0; i<oldPartitions.length; i++) {
        	subQos.value.partition.name[i] = oldPartitions[i];
        }
        subQos.value.partition.name[oldPartitions.length] = g.name;
        status = this.subscriber.set_qos(subQos.value);
        ErrorHandler.checkStatus(
                status, "ServiceDDS.Peer.subscriber.joinGroup");
        
    }
    
    /***
     * A Peer leaves a Group.
     * IMPORTANT: Not implemented yet.
     * @param groupName The name of the Group that the Peer wants to leave.
     */
    public void leaveGroup(String groupName) {
    	this.groupTable.remove(groupName);
    	// Change partitions in pub/sub QoS
    	// TODO

    }
    
    /**
     * Check if a Peer has been included previously in a group with a name given.
     * @param groupName the name fo the group
     * @return true if the Peer is associated with a Group with the name 'groupName' or false in other case
     */
    public boolean isInGroup(String groupName) {
    	return this.groupTable.containsKey(groupName);
    }
        
    /* Service related methods */
    /***
     * Allows a Peer to look for a Service given a service contract template and requesting
     * for a certain Quality of Service from the provider.
     * @param sc The incomplete service contract or "service template"
     * @param qos The quality of service requirements
     * @return The RemoteServiceInstance that represents the service or null
     */
    public RemoteServiceInstance lookForService(ServiceContract sc, QoSParameters qos) {
        RemoteServiceInstance s = this.sde.lookForService(sc, qos, this.name);
        if (s!= null) this.rsr.addService(s);
        return s;
    }

    /**
     * Allows a Peer to publish a service given a Service contract and a service implementation
     * @param s The service contract for the service that is going to be published
     * @param p The instance that will implement the service operations
     */
    public void publishService(ServiceContract s, ServiceProvider p) {
        this.lsr.addService(new Service(s,p));
        this.oiu.newServicePublished(s);
    }
    /**
     * Returns a String with the list of DISCOVERED services
     * @return A String with the DISCOVERED services
     */
    public String servicesToString() {
        return this.rsr.toString();
    }
    
    /* Service topics related methods */

    /***
     * This method can be used by a Peer to instantiate a DDS topic with an associated TopicReader.
     * The Peer only have to provide the three things that define a topic instance:
     * - The instance name
     * - The Topic data type
     * - The QoS parameters (currently reduced to a subset of those of DDS)
     * @param topicDataType The Java class that represents the DDS topic data type (generated from the IDL)
     * @param topicName The instance name
     * @param readerQos The quality of service parameters @see QoSParameters
     * @return An instance that can be used to directly read, take and listen topic samples @see ReaderServiceTopic
     * @throws ImpossibleToCreateDDSTopic 
     * 
     */
    public ReaderWriterServiceTopic newReaderWriterServiceTopic(Object topicDataType, String topicName,
    		QoSParameters readerQos, QoSParameters writerQos) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ImpossibleToCreateDDSTopic {
    	ReaderWriterServiceTopic res = new ReaderWriterServiceTopic(topicDataType, 		// Topic data type
    							topicName, 			// Topic instance name
    							participant,// Domain participant
    							subscriber,
    							publisher,	// Subscriber
    							writerQos,
    							readerQos); 			// Default reader QoS
    	//this.serviceTopicTable.put(topicName, res);
    	return res;
    }
    
    
    /***
     * This method can be used by a Peer to instantiate a DDS topic with an associated TopicReader.
     * The Peer only have to provide the three things that define a topic instance:
     * - The instance name
     * - The Topic data type
     * - The QoS parameters (currently reduced to a subset of those of DDS)
     * @param topicDataType The Java class that represents the DDS topic data type (generated from the IDL)
     * @param topicName The instance name
     * @param readerQos The quality of service parameters @see QoSParameters
     * @return An instance that can be used to directly read, take and listen topic samples @see ReaderServiceTopic
     * @throws ImpossibleToCreateDDSTopic 
     * 
     */
    public ReaderServiceTopic newReaderServiceTopic(Object topicDataType, String topicName,
    		QoSParameters readerQos) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ImpossibleToCreateDDSTopic {
    	ReaderServiceTopic res = new ReaderServiceTopic(topicDataType, 		// Topic data type
    							topicName, 			// Topic instance name
    							participant,// Domain participant
    							subscriber,	// Subscriber
    							readerQos); 			// Default reader QoS
//    	this.serviceTopicTable.put(topicName, res);
    	return res;
    }
 
    /***
     * This method can be used by a Peer to instantiate a DDS topic with an associated TopicWriter.
     * The Peer only have to provide the three things that define a topic instance:
     * - The instance name
     * - The Topic data type
     * - The QoS parameters (currently reduced to a subset of those of DDS)
     * @param topicDataType The Java class that represents the DDS topic data type (generated from the IDL)
     * @param topicName The instance name
     * @return An instance that can be used to directly write topic samples @see WriterServiceTopic
     * @throws ImpossibleToCreateDDSTopic 
     * 
     */
    public WriterServiceTopic newWriterServiceTopic(Object topicDataType, String topicName,
    		QoSParameters writerQos) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ImpossibleToCreateDDSTopic {
    	WriterServiceTopic res = new WriterServiceTopic(topicDataType, 		// Topic data type
    							topicName, 			// Topic instance name
    							participant,// Domain participant
    							publisher,	// Publisher
    							writerQos);			// Default writer QoS
//    	this.serviceTopicTable.put(topicName, res);
    	return res;
    }

    /***
     * This method can be used by a Peer to instantiate a DDS topic with an associated TopicReader.
     * The Peer only have to provide the three things that define a topic instance:
     * - The instance name
     * - The Topic data type
     * - The QoS parameters (currently reduced to a subset of those of DDS)
     * @param topicDataType The Java class that represents the DDS topic data type (generated from the IDL)
     * @param topicName The instance name
     * @param readerQos The quality of service parameters @see QoSParameters
     * @return An instance that can be used to directly read, take and listen topic samples @see ReaderServiceTopic
     * @throws ImpossibleToCreateDDSTopic 
     * 
     */
    public ContentFilteredReaderServiceTopic newContentFilteredReaderServiceTopic(
    		Object topicDataType, 
    		String topicName,
    		String expression,
    		String[] args,
    		QoSParameters readerQos) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ImpossibleToCreateDDSTopic {
    	ContentFilteredReaderServiceTopic res = new ContentFilteredReaderServiceTopic(
    							topicDataType, 		// Topic data type
    							topicName, 			// Topic instance name
    							expression,
    							args,
    							participant,// Domain participant
    							subscriber,	// Subscriber
    							readerQos); 			// Default reader QoS
//    	this.serviceTopicTable.put(topicName, res);
    	return res;
    }
    
    
//    /**
//     * 
//     * @param topicDataType
//     * @param topicName
//     * @param topicQos
//     * @param readerQos
//     * @param writerQos
//     * @return
//     * @throws ClassNotFoundException
//     * @throws InstantiationException
//     * @throws IllegalAccessException
//     */
//    public ServiceTopic newServiceTopic(Object topicDataType, String topicName,
//    		QoSParameters topicQos, QoSParameters readerQos, QoSParameters writerQos) 
//    		throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//    	ServiceTopic res = new ServiceTopic(topicDataType, 		// Topic data type
//				topicName, 			// Topic instance name
//				participant,// Domain participant
//				publisher,	// Publisher
//				subscriber,	// Subscriber
//				topicQos,			// Topic QoS
//				readerQos, 			// Default reader QoS
//				writerQos);			// Default writer QoS
//    	this.serviceTopicTable.put(topicName, res);
//    	return res;
//    }
//    
//    /**
//     * 
//     * @param topicDataType
//     * @param topicName
//     * @param topicQos
//     * @return
//     * @throws ClassNotFoundException
//     * @throws InstantiationException
//     * @throws IllegalAccessException
//     */
//    public ServiceTopic newServiceTopic(Object topicDataType, String topicName,
//    		QoSParameters topicQos) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//    	ServiceTopic res = new ServiceTopic(topicDataType, 		// Topic data type
//				topicName, 			// Topic instance name
//				participant,// Domain participant
//				publisher,	// Publisher
//				subscriber,	// Subscriber
//				topicQos);  // TopicQos
//    	this.serviceTopicTable.put(topicName, res);
//    	return res;
//    }
//    
//    /**
//     * 
//     * @param topicDataType
//     * @param topicName
//     * @return
//     * @throws ClassNotFoundException
//     * @throws InstantiationException
//     * @throws IllegalAccessException
//     */
//    public ServiceTopic newServiceTopic(Object topicDataType, String topicName) 
//			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//    	ServiceTopic res = new ServiceTopic(topicDataType, 		// Topic data type
//				topicName, 			// Topic instance name
//				participant,// Domain participant
//				publisher,	// Publisher
//				subscriber);	// Subscriber
//    	this.serviceTopicTable.put(topicName, res);
//    	return res;
//    }
//    
//    /**
//     * 
//     * @param topicDataType
//     * @param topicName
//     * @param groups
//     * @return
//     * @throws ClassNotFoundException
//     * @throws InstantiationException
//     * @throws IllegalAccessException
//     */
//    public ServiceTopic newServiceTopic(
//    		Object topicDataType, 
//    		String topicName,
//    		Group[] groups) 
//	throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//    	// Create specific publisher and subscriber
//    	// Adapt the default publisher qos to write 
//    	// into the group partitions
//    	PublisherQosHolder myPubQos = new PublisherQosHolder();
//        status = participant.get_default_publisher_qos(myPubQos);
//        ErrorHandler.checkStatus(
//            status, "ServiceDDS.Peer.newServiceTopic.get_default_publisher_qos");
//        myPubQos.value.partition.name = new String[groups.length];
//        for (int i=0; i<groups.length; i++) {
//        	myPubQos.value.partition.name[i] = groups[i].name;
//        }
//        // Create the Publisher
//    	Publisher myPublisher = participant.create_publisher(
//            myPubQos.value, null, ANY_STATUS.value);
//        ErrorHandler.checkHandle(
//            myPublisher, "ServiceDDS.Peer.newServiceTopic.create_publisher");
//        
//        // Adapt the default SubscriberQos to 
//        // read from the group partitions
//        SubscriberQosHolder mySubQos = new SubscriberQosHolder();
//        status = participant.get_default_subscriber_qos(mySubQos);
//        ErrorHandler.checkStatus(
//            status, "ServiceDDS.Peer.newServiceTopic.get_default_subscriber_qos");
//        mySubQos.value.partition.name = new String[groups.length];
//        for (int i=0; i<groups.length; i++) {
//        	mySubQos.value.partition.name[i] = groups[i].name;
//        }
//        // Create the Subscriber
//        Subscriber mySubscriber = participant.create_subscriber(
//            mySubQos.value, null, ANY_STATUS.value);
//        ErrorHandler.checkHandle(
//            subscriber, "ServiceDDS.Peer.newServiceTopic.create_subscriber");
//    	
//    	// Create the service-topic
//    	ServiceTopic res = new ServiceTopic(topicDataType, 		// Topic data type
//				topicName, 			// Topic instance name
//				participant,// Domain participant
//				myPublisher,	// Publisher
//				mySubscriber);	// Subscriber
//		this.serviceTopicTable.put(topicName, res);
//		return res;
//    }

//    /***
//     * Returns a service topic instance associated with this Peer given the Topic instance name.
//     * @param instanceName The topic instance name
//     * @return A ServiceTopic instance that was previously created.
//     */
//    public ServiceTopic getServiceTopic(String instanceName) {
//    	return this.serviceTopicTable.get(instanceName);
//    }
    
    /* Peer properties related methods */
    /**
     * Returns the Peer name.
     * @return The Peer name
     */
    public String getName() {
        return this.name;
    }
   
    private void createDDSEntities() throws ImpossibleToCreateDDSDomainParticipant {
        // Get an instance to a domain participant factory
        dpf = DomainParticipantFactory.get_instance();
        ErrorHandler.checkHandle(
            dpf, "ServiceDDS.PeerImpl.DomainParticipantFactory.get_instance");
//      System.out.println("Peer.createDDSEntities: factory instance obtained...");
        // Create a domain participant SET PROPER QoS!!!
        participant = dpf.create_participant(
            null,//group.name,
            PARTICIPANT_QOS_DEFAULT.value,
            null,
            ANY_STATUS.value);
        if (participant==null) throw new ImpossibleToCreateDDSDomainParticipant(this.name);
//        ErrorHandler.checkHandle(
//            participant, "ServiceDDS.ServiceTopic.Peer.DomainParticipantFactory.create_participant in peer "+this.name);
//      System.out.println("PeerImpl.createDDSEntities: participant created... "+participant);	
        // Set the ReliabilityQosPolicy to RELIABLE and KEEP_ALL
        status = participant.get_default_topic_qos(defaultTopicQos);
        ErrorHandler.checkStatus(
            status, "ServiceDDS.ServiceTopic.DomainParticipant.get_default_topic_qos");
        defaultTopicQos.value.reliability.kind =
            ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
        defaultTopicQos.value.history.kind =
            HistoryQosPolicyKind.KEEP_ALL_HISTORY_QOS;
        // Make the tailored QoS the new default
        status = participant.set_default_topic_qos(defaultTopicQos.value);
        ErrorHandler.checkStatus(
            status, "ServiceDDS.ServiceTopic.DomainParticipant.set_default_topic_qos");
        
        // Adapt the default PublisherQos to write into the partition
        status = participant.get_default_publisher_qos(pubQos);
        ErrorHandler.checkStatus(
            status, "ServiceDDS.ServiceTopic.DomainParticipant.get_default_publisher_qos");
        pubQos.value.partition.name = new String[0];
//        pubQos.value.partition.name[0] = "";//group.name;
       
        // Create a Publisher
        publisher = participant.create_publisher(
            pubQos.value, null, ANY_STATUS.value);
        ErrorHandler.checkHandle(
            publisher, "ServiceDDS.ServiceTopic.DomainParticipant.create_publisher");
//      System.out.println("PeerImpl.createDDSEntities: publisher created... "+publisher+" in partition "+pubQos.value.partition.name[0]);        
        
      // Adapt the default SubscriberQos to read from the Partition
        status = participant.get_default_subscriber_qos(subQos);
        ErrorHandler.checkStatus(
            status, "ServiceDDS.ServiceTopic.DomainParticipant.get_default_subscriber_qos");
        subQos.value.partition.name = new String[0];
//        subQos.value.partition.name[0] = "";//group.name;

        // Create a Subscriber
        subscriber = participant.create_subscriber(
            subQos.value, null, ANY_STATUS.value);
        ErrorHandler.checkHandle(
            subscriber, "ServiceDDS.ServiceTopic.DomainParticipant.create_subscriber");
//      System.out.println("PeerImpl.createDDSEntities: subscriber created... "+subscriber+" in partition "+subQos.value.partition.name[0]);

        
    }
    
    protected void finalize() throws Throwable {
        // Remove the Publisher
        status = participant.delete_publisher(this.publisher);
        ErrorHandler.checkStatus(
            status, "DDS.DomainParticipant.delete_publisher");
        // Remove the Subscriber
        status = this.participant.delete_subscriber(this.subscriber);
        ErrorHandler.checkStatus(
            status, "DDS.DomainParticipant.delete_subscriber");    	
        // Remove the DomainParticipant
        status = dpf.delete_participant(participant);
        ErrorHandler.checkStatus(
            status, "DDS.DomainParticipantFactory.delete_participant");
        super.finalize(); //not necessary if extending Object.   	
    }


    /***
     * Checks if a Peer provides a service with a given name.
     * @param serviceName A service name
     * @return true if the Peer provies a service with the name 'serviceName' or false in other case
     */
	public boolean providesService(String serviceName) {
		// TODO Auto-generated method stub
		return (this.lsr.getService(serviceName)!=null);
	}


	/***
	 * Returns the provider for the first service that the Peer provides with a given name
	 * @param serviceName The name of the service
	 * @return the provider for the first name that the Peer provides with a given name
	 */
	public ServiceProvider getProvider(String serviceName) {
		// TODO Auto-generated method stub
		return this.lsr.getService(serviceName).provider;
	}
    
}
