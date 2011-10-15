/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.service.operativeunit;

import java.util.logging.Level;
import java.util.logging.Logger;

import DDSLWS.OperationInvocationTopic;
import ServiceDDS.Peer;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;
import ServiceDDS.service.contract.ServiceContract;
import ServiceDDS.service.discoveryengine.ServiceRequestListener;
import ServiceDDS.servicetopic.ReaderServiceTopic;
import ServiceDDS.servicetopic.ServiceTopic;
import ServiceDDS.servicetopic.ServiceTopicListener;

/**
 *
 * @author PC
 */
public class OperationInvocationListener implements ServiceTopicListener {
//    LocalServicesRegistry lsr;
    Peer peer;
    
    /* Others */
    String operationInvocationTypeName;
    ReaderServiceTopic operationInvocationServiceTopic;
    
    public OperationInvocationListener(Peer peer, ServiceContract contract) {
        this.peer = peer;
        try {
            this.operationInvocationServiceTopic = peer.newReaderServiceTopic(new OperationInvocationTopic(), "DDSLWS_OperationInvocationTopic"+contract.serviceName,null);
            this.operationInvocationServiceTopic.addListener(this);
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
    
 
	public void on_data_available(ServiceTopic serviceTopic) {
        /* Note: using read does not remove the samples from
           unregistered instances from the DataReader. This means
           that the DataRase would use more and more resources.
           That's why we use take here instead. */
//    	System.out.println("OperationInvocationListener.on_data_available: received request for operation ...");
        Object[] data = this.operationInvocationServiceTopic.take();
        for (int i = 0; i < data.length; i++) {
            this.processOperationInvocation((OperationInvocationTopic)data[i]);
        }

    }

	public void on_requested_deadline_missed(ServiceTopic serviceTopic) {
		// TODO Auto-generated method stub
		
	}


	public void on_sample_lost(ServiceTopic serviceTopic) {
		// TODO Auto-generated method stub
		
	}

    /**
     * Here is where the operation invocation start to take place ...
     */
    private void processOperationInvocation(OperationInvocationTopic operationInvocationTopic) {
//            System.out.println(" new invocation arrived for operation "        	
//            		+operationInvocationTopic.operationName);
        Object res = peer.getProvider(operationInvocationTopic.serviceName).call(operationInvocationTopic.operationName, new Object[] {});
        writeResponse(res,operationInvocationTopic.operationName);
    }


	private void writeResponse(Object res, String operationName) {
		OperationInvocationListenerResult response = new OperationInvocationListenerResult(this.peer,operationName);
		Thread newThread = new Thread(response);
		newThread.start();
	}
	
}
