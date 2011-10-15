/*
 * To change this contract, choose Tools | Templates
 * and open the contract in the editor.
 */

package ServiceDDS.service.operativeunit;

import ServiceDDS.Peer;
import ServiceDDS.service.contract.ServiceContract;
import ServiceDDS.servicetopic.ServiceTopic;


/**
 *
 * @author PC
 */
public class OperationInvocationUnit {

    Peer peer;        
    OperationInvocationListener oil;
    ServiceTopic operationInvocationServiceTopic;
    
    public OperationInvocationUnit(Peer peer) {
        this.peer = peer;
    }     
    
    /* DEPRECATED SHOULDN'T BE USED */
//    public Object invoke(ServiceContract contract, String operationName, QoSParameters qos) {
////        System.out.println("OperationInvocationUnit.invoke: operation="+operationName);
////        OperationInvocationResultReader responseReader = new OperationInvocationResultReader(this.peer,operationName); // With timeout/QoS
////        RemoteOperationInvocation invocation = new RemoteOperationInvocation(this.peer, contract, operationName);
////        Thread invThread = new Thread(invocation);
////        invThread.start();
////        return responseReader.readResult(qos); 
//    	return null;
//    }
    
    public void newServicePublished(ServiceContract contract) {
        // Create an operation invocation listener for the new service
        this.oil = new OperationInvocationListener(this.peer,contract);
    }
    
}
