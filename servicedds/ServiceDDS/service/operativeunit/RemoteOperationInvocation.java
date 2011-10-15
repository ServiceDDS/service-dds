/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.service.operativeunit;

import DDSLWS.OperationInvocationTopic;
import ServiceDDS.Peer;
import ServiceDDS.service.contract.ServiceContract;
import ServiceDDS.servicetopic.ServiceTopic;

/**
 *
 * @author PC
 */
public class RemoteOperationInvocation implements Runnable {
    
    Peer peer;
    ServiceTopic operationInvocationServiceTopic;
    String operationName;
    ServiceContract contract;
        
    public RemoteOperationInvocation(ServiceTopic operationInvocationServiceTopic, Peer peer, ServiceContract contract, String name) {
        this.peer = peer;
        this.operationName = name;
        this.operationInvocationServiceTopic = operationInvocationServiceTopic;
        this.contract = contract;
         
    }

    public void run() {
//        System.out.println("OperationInvocation.run()...");
    	writeRequestTopic();        
    }
    private void writeRequestTopic() {
//      System.out.println("OperationInvocation.writeRequestTopic for operation="+this.operationName);
      OperationInvocationTopic operationInvocationTopic;        
       /* Initialize the service contract topic */
      operationInvocationTopic = new OperationInvocationTopic();
      operationInvocationTopic.invoker = this.peer.getName();
      operationInvocationTopic.serviceName = this.contract.serviceName;
      operationInvocationTopic.operationName = this.operationName;
      this.operationInvocationServiceTopic.write(operationInvocationTopic);
  }    

    
}
