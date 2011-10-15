/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.service.discoveryengine;

import java.util.logging.Level;
import java.util.logging.Logger;

import DDSLWS.ServiceContractTopic;
import ServiceDDS.Peer;
import ServiceDDS.QoSParameters;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;
import ServiceDDS.service.RemoteServiceInstance;
import ServiceDDS.servicetopic.ReaderServiceTopic;

/**
* The SLURR waits for service look up responses. Its read response method is onvoked by the service lookup unit after
* sending a request for a service. FUNCTIONALITY INCOMPLETE.
* @author Jose Angel Dianes
* @version 0.1b, 09/24/2010
*/
public class ServiceLookupUnitResponseReader {
    Peer peer;
    ReaderServiceTopic serviceContractServiceTopic;
    
    /**
     * Main constructor
     * @param peer The peer that sent the lookup request
     */
    public ServiceLookupUnitResponseReader(Peer peer) {
        this.peer = peer;
        try {
            this.serviceContractServiceTopic = peer.newReaderServiceTopic(
                    new ServiceContractTopic(),
                    "DDSLWS_ServiceContractTopic",                    
                    null);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServiceLookupUnitResponseReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ServiceLookupUnitResponseReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceLookupUnitResponseReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ImpossibleToCreateDDSTopic e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public RemoteServiceInstance readResponse(QoSParameters qos) {
      System.out.println("ServiceLookupUnitResponseReader.readResponse()...");
        RemoteServiceInstance res = null;

        this.serviceContractServiceTopic.waitData(qos);

        Object[] data = this.serviceContractServiceTopic.take();

        for (int i = 0; i < data.length; i++) {
            res = this.processServiceContractTopicResponse((ServiceContractTopic)data[i]);
        }

        return res;
    }
    
    private RemoteServiceInstance processServiceContractTopicResponse(ServiceContractTopic serviceContractTopic) {
            System.out.println("ServiceLookupUnitResponseReader.processServiceContractTopicResponse(): Service response for: "+
                serviceContractTopic.serviceName
                +" received...");
            return new RemoteServiceInstance(serviceContractTopic,this.peer);
    }

    
}
