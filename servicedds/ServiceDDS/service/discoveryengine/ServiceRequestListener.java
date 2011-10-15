/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.service.discoveryengine;

import java.util.logging.Level;
import java.util.logging.Logger;

import DDSLWS.ServiceContractTemplateTopic;
import ServiceDDS.Peer;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;
import ServiceDDS.service.contract.ServiceContract;
import ServiceDDS.service.contract.ServiceContractTemplate;
import ServiceDDS.service.registry.LocalServicesRegistry;
import ServiceDDS.servicetopic.ReaderServiceTopic;
import ServiceDDS.servicetopic.ServiceTopic;
import ServiceDDS.servicetopic.ServiceTopicListener;

/**
 * The SRL is waiting for service lookup requests from remote peers. 
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public class ServiceRequestListener implements ServiceTopicListener {
    LocalServicesRegistry lsr;
    Peer peer;
    ReaderServiceTopic serviceContractTemplateServiceTopic;

    String serviceTemplateTopicTypeName;

    /**
     * The main constructor.
     * @param peer The provider peer
     * @param lsr A registry containing the services provided by this peer
     */
    public ServiceRequestListener(Peer peer, LocalServicesRegistry lsr) {
        this.peer = peer;
        this.lsr = lsr;
        try {
            this.serviceContractTemplateServiceTopic = peer.newReaderServiceTopic(new ServiceContractTemplateTopic(), "DDSLWS_ServiceContractTemplateTopic",null);
            this.serviceContractTemplateServiceTopic.addListener(this);
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
    
    /** 
     * TO-COMPLETE. In the current version compares the provider name in the contract template with this peer's name
     * After that, it performs a service matching process.
     * @param st
     */
    private void processServiceRequest(ServiceContractTemplateTopic st) {
            System.out.println("Service request for: "+
                st.serviceName
                +" received...");
        if (st.requester.compareTo(peer.getName()) != 0) {
            System.out.println("...looking for service");
            ServiceContract newContract = lsr.getMatchingServiceContracts(new ServiceContractTemplate(st));
            if (newContract != null) {
              System.out.println("ServiceRequestListener.processServiceRequest(): service "+st.serviceName+" found, responding...");  
                Thread servantThread = new Thread(new ServiceRequestListenerResponse(this.peer, newContract,st.requester));
                servantThread.start();
            } else {
                System.out.println("ServiceRequestListener.processServiceRequest(): service "+st.serviceName+" not found...");
            }
        }                            
    }

    /** 
     * A sample arriving in the ServiceDDS internal topic means that a new request has arrived.
     */
    public void on_data_available(ServiceTopic serviceTopic) {
        Object[] data = this.serviceContractTemplateServiceTopic.take();
        for (int i = 0; i < data.length; i++) {
            this.processServiceRequest((ServiceContractTemplateTopic)data[i]);
        }
    }

    /**
     * TODO
     */
	public void on_requested_deadline_missed(ServiceTopic serviceTopic) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * TODO
	 */
	public void on_sample_lost(ServiceTopic serviceTopic) {
		// TODO Auto-generated method stub
		
	}

    
}
