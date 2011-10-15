package junittests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ServiceDDS.Group;
import ServiceDDS.Peer;
import ServiceDDS.QoSParameters;
import ServiceDDS.exception.ImpossibleToCreateDDSDomainParticipant;
import ServiceDDS.service.RemoteServiceInstance;
import ServiceDDS.service.contract.ServiceContract;
import ServiceDDS.service.operativeunit.provider.ServiceProvider;


public class BasicWorkflowJUnitTest extends ServiceProvider {
	Peer testPeer;
	Peer testPeerClient;
	Group testGroup;
	ServiceContract testContract;
    @Before 
    public void setUp() {
    	try {
    		testPeer = new Peer("new_peer");
        	testPeerClient = new Peer("new_peer_client");
        	testGroup = new Group("TestGroup");
        	testContract = new ServiceContract("MyService",testPeer.name);    		
    	} catch (ImpossibleToCreateDDSDomainParticipant e) {
    		e.printStackTrace();
    	}
    }

    @Test
    public void createPeer() {   	
    	assertNotNull(testPeer); 
    	assertEquals(testPeer.getName(), "new_peer");
    	assertTrue(testPeer.isInGroup("SERVICEDDS"));
    }
    
    @Test
    public void createGroup() {
    	assertNotNull(testGroup);
    }
    
    @Test
    public void joinGroup() {
    	testPeer.joinGroup(testGroup);
    	assertTrue(testPeer.isInGroup("TestGroup"));
    }
    
    @Test
    public void leaveGroup() {
    	testPeer.leaveGroup("TestGroup");
    	assertFalse(testPeer.isInGroup("TestGroup"));    	
    }
    
    @Test
    public void publishService() {
    	testPeer.publishService(testContract, this);
    	assertTrue(testPeer.getProvider("MyService")==this);
    }
    
    @Test
    public void lookUpService() {
    	testPeerClient.joinGroup(testGroup);
    	RemoteServiceInstance rsi = testPeerClient.lookForService(testContract, new QoSParameters());
    	assertNotNull(rsi);
    	
    }
    
    @Test
    public void invokeOperation() {
    	
    }
    
    public void hello() {
    	System.out.println("Hello!");
    }
    
}
