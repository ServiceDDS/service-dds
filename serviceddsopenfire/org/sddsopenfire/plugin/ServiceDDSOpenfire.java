package org.sddsopenfire.plugin;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQRouter;
import org.jivesoftware.openfire.MessageRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.event.SessionEventDispatcher;
import org.jivesoftware.openfire.event.SessionEventListener;
import org.jivesoftware.util.TaskEngine;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.component.ComponentManagerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.IQ.Type;

import ServiceDDS.exception.ImpossibleToCreateDDSTopic;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TimerTask;

/**
 * This class represents the Openfire plugin. See Openfire documentation about plugins for more information.
 * @author Jose Angel Dianes
 * @version 0.1b 09/24/2010
 *
 */
public class ServiceDDSOpenfire implements Plugin, Component {

	Hashtable<String, WebPeer> peerTable = new Hashtable<String, WebPeer>();
    private JID serverAddress;
	private MessageRouter messageRouter;
	private IQRouter iqRouter;
	private SessionEventListener listener;
	ServiceDDSWebCommander webCommander = new ServiceDDSWebCommander(this);
	private ComponentManager componentManager;
	private PluginManager pluginManager;
	private String serverName;
	
	/**
	 * Initializes the plugin and its data structures. This method is called by Openfire after instantiating the class.
	 */
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
      System.out.println("ServiceDDSOpenfire: initializing plugin...");
		this.pluginManager = manager;
		// Create a JID address to use as the from address when send out... 
		this.serverAddress = new JID(XMPPServer.getInstance().getServerInfo().getHostname());
		this.serverName = XMPPServer.getInstance().getServerInfo().getHostname();
	  System.out.println("ServiceDDSOpenfire: hostName = "+XMPPServer.getInstance().getServerInfo().getHostname());
	  System.out.println("ServiceDDSOpenfire: serverAddress = "+this.serverAddress);
		// Obtain a reference to the MessageRouter which we'll user to send our messages
		this.messageRouter = XMPPServer.getInstance().getMessageRouter();
		// Register the plugin as a component
		this.componentManager = ComponentManagerFactory.getComponentManager();
        try {
            componentManager.addComponent("serviceddsopenfire", this);
        }
        catch (ComponentException e) {
        	e.printStackTrace();
//            componentManager.getLog().error(e);
        }
        
        System.out.println("ServiceDDSOpenfire.initializePlugin(): name = "+getName());
        System.out.println("ServiceDDSOpenfire.initializePlugin(): description = "+getDescription());
		// Add our ServiceDDSOpenfireSessionEventListener to the SessionEventDispatcher		
		this.listener = new ServiceDDSOpenfireSessionEventListener(
				this.serverAddress,
				this.messageRouter,
				this.iqRouter,
				this.peerTable);
		SessionEventDispatcher.addListener(listener);	
	}
	
	/** 
	 * Called when the plugin is going to be removed.
	 */
    public void destroyPlugin() {
    	SessionEventDispatcher.removeListener(listener);
    	listener = null;
    	serverAddress = null;
    	messageRouter = null;
    }

	@Override
	public String getDescription() {
//		System.out.println("ServiceDDSOpenfire.getDescription()");
		return pluginManager.getDescription(this);
	}

	@Override
	public String getName() {
//		System.out.println("ServiceDDSOpenfire.getName()");
		return pluginManager.getName(this);
	}

	@Override
	public void initialize(JID arg0, ComponentManager arg1)
			throws ComponentException {
//		System.out.println("ServiceDDSOpenfire.initialize()");
		
	}

	/**
	 * Filter XMPP stanzas looking for the 'sdds' child element.
	 */
	@Override
	public void processPacket(Packet received) {	
		System.out.println("ServiceDDSOpenfire.processPacket() detected packet "+received.toXML());	
		// Clients wait for replay, so they send IQ packets, right?
		if ((received instanceof IQ) 
		&& ((((IQ)received).getChildElement()!=null)
            && (((IQ)received).getChildElement().getName().compareTo("sdds")==0))) {			
			IQ iq = (IQ)received;
			System.out.println("ServiceDDSOpenfire.processPacket() detected IQ stanza with sdds command from:");
			System.out.println("JID="+iq.getFrom());
			System.out.println("domain="+iq.getFrom().getDomain());
			System.out.println("node="+iq.getFrom().getNode());
						
			String peerId = iq.getFrom().getNode()+"@"+iq.getFrom().getDomain();
			Element iqChild = iq.getChildElement();
//			System.out.println("peerId="+peerId);
			
			iqChild = iq.getChildElement();
			System.out.println("ServiceDDSOpenfire.processPacket: "+iqChild.asXML());
			System.out.println("name="+iqChild.getName());
//			System.out.println("qualifiedName="+iqChild.getQualifiedName());
		    Iterator<Element> it = iqChild.elementIterator();
		    while (it.hasNext()) {
		    	Element nextElement = it.next();
				System.out.println("ServiceDDSOpenfire.processPacket: "+nextElement.asXML());
//				System.out.println("name="+nextElement.getName());
//				System.out.println("qualifiedName="+nextElement.getQualifiedName());
				processSDDSCommandElement(nextElement,peerId,iq.getID());
				
		    }
/*		    
	      // Read a sample from service-topic	
	      } else if (iqChild.getName().compareTo("READ")==0) {
	      	
	
	      // Take a sample from a service-topic
	      } else if (iqChild.getName().compareTo("TAKE")==0) {
	      	
	*/	
		} /* sdds.IQ stanza processing */
	} /* processPacket */

	/**
	 * Process 'sdds' child elements of XMPP stanzas.
	 * @param element
	 * @param peerId
	 * @param iqId
	 */
	private void processSDDSCommandElement(Element element, String peerId, String iqId) {
//		System.out.println("ServiceDDSOpenfire.processSDDSCommandElement(): processing element "+element.asXML());
		String commandName = element.getName();
	    // Join a group	
    	if (commandName.compareTo("join")==0) {
    		System.out.print("ServiceDDSOpenfire.processSDDSCommandElement(): detected 'join' command for group=");
    		WebPeer peer = this.peerTable.get(peerId);
    		String groupName = element.attributeValue("group");
//    		System.out.println(groupName);
    		this.webCommander.joinGroup(peer,groupName);
    		this.sendListenResponse(peer, iqId);
		// Leave a group	
    	} else if (commandName.compareTo("leave")==0) {
   
   	    // Creates a new service-topic	
    	} else 	if (commandName.compareTo("new")==0) {
		  System.out.println("ServiceDDSOpenfire.processSDDSCommandElement(): detected 'new' command...");
			WebPeer peer = this.peerTable.get(peerId);
//		  System.out.println("ServiceDDSOpenfire.processSDDSCommandElement(): obtained Peer = "+peer.name);
			String topicDataType = element.attributeValue("datatype");
//		  System.out.println("ServiceDDSOpenfire.processSDDSCommandElement(): obtained Data type = "+topicDataType);	
			String instanceName = element.attributeValue("instancename");
//		  System.out.println("ServiceDDSOpenfire.processSDDSCommandElement(): obtained Instance name = "+instanceName);	
			try {
				this.webCommander.newTopicInstance(
						peer,
						topicDataType,
						instanceName);
				this.sendListenResponse(peer, iqId);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}     	
		// Write a sample into a service topic
		// In this version only one sample writing per command
		// FUTURE VERSIONS: multiple samples (we already allow multiple commands by IQ)
			catch (ImpossibleToCreateDDSTopic e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else if (commandName.compareTo("write")==0) {
  	      System.out.println("ServiceDDSOpenfire.processPacket() detected 'write' command...");
  	        WebPeer peer = this.peerTable.get(peerId);
			String topicName = element.attributeValue("instancename");
		    Object[] topicData = processArguments(element);		    	
			try {
				this.webCommander.writeTopic(peer, 
						topicName, 
						topicData);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}	
			this.sendListenResponse(peer, iqId); // Provisional
		// Read a sample from a service-topic
    	} else if (commandName.compareTo("read")==0) {
    		
    		
    	// Take a sample from a service-topic	
    	} else if (commandName.compareTo("take")==0) {
    		
        // Start to listen to a service-topic
    	} else if (commandName.compareTo("listen")==0) {
    		System.out.println("ServiceDDSOpenfire.processPacket() detected 'listen' command...");
    		WebPeer peer = this.peerTable.get(peerId);
    		String topicName = element.attributeValue("instancename");
    		System.out.println("instanceName="+topicName);
    		this.webCommander.listenToTopic(
						peer, 
						topicName,
						serverAddress);
    		this.sendListenResponse(peer, iqId);
    	}
	}
	
	private void sendListenResponse(WebPeer peer, String id) {
		final IQ iq = new IQ();
		iq.setType(Type.set);
		iq.setFrom(this.serverAddress);
		iq.setTo(peer.session.getAddress());
		iq.setID(id);
		// Send the message to the client
		TimerTask messageTask = new TimerTask() {
			public void run() {
//				System.out.println("ServiceDDSOpenfire.sendListenReponse: sending on_data_available event command "+iq.toXML());
				XMPPServer.getInstance().getIQRouter().route(iq);
			}
		};
		TaskEngine.getInstance().schedule(messageTask,3000);
		
	}
	private void sendOKResponse(String string) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void shutdown() {
		System.out.println("ServiceDDSOpenfire.shutdown()");
		
	}

	@Override
	public void start() {
		System.out.println("ServiceDDSOpenfire.start()");
		
	}
    

	private Object[] processArguments(Element element) {
		Object[] res = new Object[element.elements().size()];
	    Iterator<Element> it = element.elementIterator();
	    int i=0;
	    while (it.hasNext()) {
	    	Element nextData = it.next();
	    	String dataType = nextData.attributeValue("datatype");
	    	// string data type
	    	if (dataType.compareTo("string")==0) {
	    		res[i] = nextData.attributeValue("value").toString();
	    	} else if (dataType.compareTo("bool")==0) {
	    		res[i] = Boolean.parseBoolean(nextData.attributeValue("value").toString());
	    	} else if ((dataType.compareTo("long")==0) || (dataType.compareTo("int")==0)) {
	    		res[i] = Integer.parseInt(nextData.attributeValue("value").toString());
	    	} else if (dataType.compareTo("float")==0) {
	    		res[i] = Float.parseFloat(nextData.attributeValue("value").toString());
	    	} else if (dataType.compareTo("double")==0) {
	    		res[i] = Double.parseDouble(nextData.attributeValue("value").toString());
	    	} else if (dataType.compareTo("sequence")==0) {
	    		
	    	} else {
	    		System.err.println("ServiceDDSOpenfire.processArguments: '"+dataType+"' type not supported.");
	    	}
	    	
	    	i++;
	    }		
		return res;
	}
	
}
