package org.sddsopenfire.plugin;

import java.util.Hashtable;

import org.jivesoftware.openfire.IQRouter;
import org.jivesoftware.openfire.MessageRouter;
import org.jivesoftware.openfire.event.SessionEventListener;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.util.JiveGlobals;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

import ServiceDDS.exception.ImpossibleToCreateDDSDomainParticipant;

/**
 * Detect new client connections and creates new peers if necessary @see WebPeer class.
 * @author Jose Angel Dianes
 * @version 0.1b 09/24/2010
 *
 */
public class ServiceDDSOpenfireSessionEventListener implements SessionEventListener {

	private static final String MESSAGE = null;
	private static final String SUBJECT = null;
	private JID serverAddress;
	private MessageRouter router;
	private IQRouter iqRouter;
	private Hashtable<String,WebPeer> peerTable;

	/**
	 * The basic constructor
	 * @param serverAddress
	 * @param router
	 * @param iqRouter
	 * @param peerTable
	 */
	public ServiceDDSOpenfireSessionEventListener(
			JID serverAddress, 
			MessageRouter router, 
			IQRouter iqRouter,
			Hashtable<String,WebPeer> peerTable) {
		System.out.println("ServiceDDSOpenfireSessionEventListener: creating listener...");
		this.serverAddress = serverAddress;
		this.router = router;
		this.peerTable = peerTable;
	}
	
	@Override
	public void anonymousSessionCreated(Session arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void anonymousSessionDestroyed(Session arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resourceBound(Session arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionCreated(Session session) {
System.out.println("ServiceDDSOpenfireSessionEventListener.sessionCreated()");
		if (isEnabled()) {
			System.out.println("ServiceDDSOpenfireSessionEventListener.sessionCreated(): activated!");

//			 final IQ iqPacket = new IQ(IQ.Type.set);
//	         iqPacket.setFrom(this.serverAddress);
//	         iqPacket.setTo(session.getAddress());
//	         //iqPacket.setChildElement("query", "clearance");
//	         //more code here to add elements
//	         System.out.println("ServiceDDSOpenfireSessionEventListener.sessionCreated(): sending IQ = "+iqPacket.toXML());
//	         this.iqRouter.route(iqPacket);			

//			TimerTask iqTask = new TimerTask() {
//			public void run() {
//					System.out.println("ServiceDDSOpenfire: sending IQ = "+iqPacket.toXML());
//					iqRouter.route(iqPacket);
//         	}
//			};
//
//			TaskEngine.getInstance().schedule(iqTask, 5000);

			
//			
			// Create a new Message object.
			final Message message = new Message();
			
			// Set the "to" field on the message by getting the address of the user that just signed in. 
			message.setTo(session.getAddress());//+"@"+session.getAddress().getDomain());
			System.out.println("ServiceDDSOpenfireSessionEventListener.sessionCreated(): to = "
					+session.getAddress().getNode());
			
			// Set the "from" address, using the serverAddress we constructed in the #initializePlugin method. 
			message.setFrom(this.serverAddress);
			System.out.println("ServiceDDSOpenfireSessionEventListener.sessionCreated(): from = "
					+this.serverAddress.getNode());

			// Set the "subject" using the value from retrieved from the #getSubject method.
			message.setSubject("Welcome Message");//getSubject());
			System.out.println("ServiceDDSOpenfireSessionEventListener.sessionCreated(): subject = "+message.getSubject());

			// Set the "body" using the value from retrieved from the #getMessage method.
			message.setBody("Welcome to Habitat");//getMessage());
			System.out.println("ServiceDDSOpenfireSessionEventListener.sessionCreated(): body = "+message.getBody());
			System.out.println("ServiceDDSOpenfireSessionEventListener.sessionCreated(): whole element = "+message.getElement());
			this.router.route(message);
//			TimerTask messageTask = new TimerTask() {
//				public void run() {
//					System.out.println("ServiceDDSOpenfire: sending message = "+message.toXML());
//					router.route(message);
//            	}
//			};
//
//			TaskEngine.getInstance().schedule(messageTask, 5000);
			
			// Creates a peer into the default SERVICEDDS group
			String peerID = session.getAddress().getNode()+"@"+session.getAddress().getDomain();
			try {
				this.peerTable.put(peerID, 
						new WebPeer(peerID,session));
			} catch (ImpossibleToCreateDDSDomainParticipant e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private boolean isEnabled() {
		return true;
	}

	@Override
	public void sessionDestroyed(Session arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setMessage(String message) {
		JiveGlobals.setProperty(MESSAGE, message);
	}

	public String getMessage() {
		return JiveGlobals.getProperty(MESSAGE, "Big Brother is watching.");
	}

	public String getSubject() {
		return JiveGlobals.getProperty(SUBJECT, "No subject");
	}
}
