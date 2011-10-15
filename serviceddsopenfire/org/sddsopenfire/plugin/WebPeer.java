package org.sddsopenfire.plugin;

import java.util.Hashtable;
import java.util.LinkedList;

import org.jivesoftware.openfire.session.Session;

import ServiceDDS.Peer;
import ServiceDDS.exception.ImpossibleToCreateDDSDomainParticipant;
import ServiceDDS.servicetopic.ServiceTopic;

/**
 * This class represents a sesion of a remote client in ServiceDDS. It helps to deal with accidental disconnections.
 * @author Jose Angel Dianes
 * @version 0.1b 09/24/2010
 */
public class WebPeer extends Peer {
	/**
	 * The XMPP server session with the client.
	 */
	Session session;

	public Hashtable<String,ServiceTopic> serviceTopics;
	
	/**
	 * Basic constructor
	 * @param peerID The ServiceDDS peer ID
	 * @param session The XMPP session
	 * @throws SDDSExImpossibleToCreateDomainParticipant 
	 */
	public WebPeer(String peerID, Session session) throws ImpossibleToCreateDDSDomainParticipant {
		super(peerID);
		serviceTopics = new Hashtable<String,ServiceTopic>();
		this.session = session;
	}
	

}
