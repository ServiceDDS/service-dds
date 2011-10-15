/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import DDSLWS.OperationInvocationTopic;
import DDSLWS.ServiceContractTopic;
import ServiceDDS.Peer;
import ServiceDDS.QoSParameters;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;
import ServiceDDS.service.contract.ServiceContract;
import ServiceDDS.service.discoveryengine.ServiceRequestListener;
import ServiceDDS.service.operativeunit.OperationInvocationResultReader;
import ServiceDDS.service.operativeunit.RemoteOperationInvocation;
import ServiceDDS.servicetopic.WriterServiceTopic;

/**
 * A representation of a service provided by a remote peer. This is typically the result of a previous
 * service look up process. An instance of this object is used to invoke remote operations.
 * The ServiceDDS service model is quite special. Service invocations are performed using topics.
 * Service providers are not bound to the service client. They are listening in the invocation topic
 * since the moment that they published the service.
 * Therefore, there are no explicit service provider references in this class. It is a very decoupled service model.
 * FUTURE WORK: When a service provider stops providing a service, the runtime environment
 * must notify the clients in order to remove RemoteServiceInstance objects.
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public class RemoteServiceInstance {
	/**
	 * The remote service contract
	 */
    public ServiceContract contract;
    /**
     * A representative of the service provider object that is not used to invoke the operation (DEPRECATED)
     */
    public String provider;
    /**
     * The local peer (the invoker)
     */
    Peer peer;
    /*
     * Service topic used to invoke operations
     */
	WriterServiceTopic operationInvocationServiceTopic;

	/**
	 * Creates a instance of a remote service
	 * @param serviceContractTopic
	 * @param peer
	 */
    public RemoteServiceInstance(
    		ServiceContractTopic serviceContractTopic, 
    		Peer peer) {
    	this.peer = peer;
        contract = new ServiceContract(serviceContractTopic);
        provider = serviceContractTopic.publisher;
        try {
            this.operationInvocationServiceTopic = peer.newWriterServiceTopic(new OperationInvocationTopic(), "DDSLWS_OperationInvocationTopic"+this.contract.serviceName,null);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServiceRequestListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ServiceRequestListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceRequestListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ImpossibleToCreateDDSTopic e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

   
/*    public RemoteServiceInstance(ServiceContract c, String p) {
        contract = c;
        provider = p;
    }
  */  
    @Override
    public String toString() {
        return provider+"."+contract;
    }
    
    /**
     * Provisional. Operation interfaces will be generated from service
     * contracts using reflection. This operation blocks until the result
     * is available.
     * @param operationName The name of the service operation to invoke
     * @param args The arguments
     * @return The result of the invocation
     */
    public Object invoke(String operationName, Object[] args, QoSParameters qos) {
//        System.out.println("RemoteServiceInstance.invoke: operation="+operationName);
        OperationInvocationResultReader responseReader = new OperationInvocationResultReader(this.peer,operationName); // With timeout/QoS
        RemoteOperationInvocation invocation = new RemoteOperationInvocation(
        		this.operationInvocationServiceTopic, 
        		this.peer, 
        		contract, 
        		operationName);
        Thread invThread = new Thread(invocation);
        invThread.start();
        return responseReader.readResult(qos);        
    }
    
}
