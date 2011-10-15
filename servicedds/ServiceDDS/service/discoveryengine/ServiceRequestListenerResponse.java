/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.service.discoveryengine;

import java.util.logging.Level;
import java.util.logging.Logger;

import DDSLWS.ServiceContractTopic;
import ServiceDDS.Peer;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;
import ServiceDDS.service.contract.ServiceContract;
import ServiceDDS.servicetopic.WriterServiceTopic;

/**
 * The SRLR is a runnable entity created for responding to service look up requests. It writes into a specific
 * DDS internal topic used by ServiceDDS for this purpose.
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public class ServiceRequestListenerResponse implements Runnable {
    
    ServiceContract sc;
    Peer peer;
    WriterServiceTopic serviceContractServiceRequest;
    
    String partitionName;
    
    /**
     * The based constructor
     * @param peer The peer that will provide the service
     * @param sc The service contract that matched the service lookup request
     * @param requesterName The name of the peer that requested the service
     */
    public ServiceRequestListenerResponse(Peer peer, ServiceContract sc, String requesterName) {
        this.peer = peer;
        partitionName = requesterName;
        this.sc = sc;
    }

    /**
     * The task that writes the response into the ServiceDDS internal topic
     */
    public void run() {
        try {
            System.out.println("ServiceRequestListenerResponse.run()...");
            this.serviceContractServiceRequest = peer.newWriterServiceTopic(
                    new ServiceContractTopic(),
                    "DDSLWS_ServiceContractTopic",
                    null);
            ServiceContractTopic sct = new ServiceContractTopic();
            sct.publisher = sc.publisher;
            sct.serviceName = sc.serviceName;
            this.serviceContractServiceRequest.write(sct);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServiceRequestListenerResponse.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ServiceRequestListenerResponse.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceRequestListenerResponse.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ImpossibleToCreateDDSTopic e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
