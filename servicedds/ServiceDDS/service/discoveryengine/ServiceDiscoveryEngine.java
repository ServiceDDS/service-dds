/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.service.discoveryengine;

import ServiceDDS.Group;
import ServiceDDS.Peer;
import ServiceDDS.QoSParameters;
import ServiceDDS.service.RemoteServiceInstance;
import ServiceDDS.service.contract.ServiceContract;
import ServiceDDS.service.registry.LocalServicesRegistry;
import ServiceDDS.service.registry.RemoteServicesRegistry;

/**
 * The SDE (ServiceDiscoveryEgine) is used by peers to look for services. It makes use of the ServiceLookupUnit
 * when discovering services. Moreover, it creates the ServiceRequestListener in order to provide service discovery
 * services to remote peers. 
 * Each peer has an instance of a SDE.
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public class ServiceDiscoveryEngine {

    ServiceLookupUnit slu;
    ServiceRequestListener srl;
    LocalServicesRegistry lsr;
    RemoteServicesRegistry rsr;
    Thread srlThread;
    Peer peer; 
    Group group;
    
    public ServiceDiscoveryEngine(Peer peer, Group group,
    		LocalServicesRegistry lsr, RemoteServicesRegistry rsr) {
        this.peer = peer;
        this.group = group;
        this.lsr = lsr;
        this.rsr = rsr;
        
        slu = new ServiceLookupUnit(peer);
        srl = new ServiceRequestListener(peer, lsr);
        
    }

    public RemoteServiceInstance lookForService(ServiceContract sc, QoSParameters qos, String requester) {
        return slu.lookUp(sc, qos, requester);
    }
}
