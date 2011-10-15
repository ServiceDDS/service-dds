/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.service.discoveryengine;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import DDSLWS.ServiceContractTemplateTopic;
import DDSLWS.ServiceContractTopicProperty;
import ServiceDDS.Group;
import ServiceDDS.Peer;
import ServiceDDS.QoSParameters;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;
import ServiceDDS.service.RemoteServiceInstance;
import ServiceDDS.service.contract.ServiceContract;
import ServiceDDS.service.contract.ServiceContractProperty;
import ServiceDDS.servicetopic.WriterServiceTopic;

/**
* The SLU (ServiceLookupUnit) uses DDS to distribute service look up requests in the global data space. 
* @author Jose Angel Dianes
* @version 0.1b, 09/24/2010
*/
public class ServiceLookupUnit {
    Peer peer;
    Group group;

    WriterServiceTopic serviceContractTemplateServiceTopic;

    /**
     * The main constructor.
     * @param peer The peer local peer
     */
    public ServiceLookupUnit(Peer peer) {
        this.peer = peer;
        try {
            this.serviceContractTemplateServiceTopic = peer.newWriterServiceTopic(new ServiceContractTemplateTopic(), "DDSLWS_ServiceContractTemplateTopic",null);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServiceLookupUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ServiceLookupUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceLookupUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ImpossibleToCreateDDSTopic e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    public RemoteServiceInstance lookUp(ServiceContract template, QoSParameters qos, String requester) {
      System.out.println("ServiceLookupUnit.lookUp(): looking for service "+template);
        writeRequestTopic(template, requester);
        ServiceLookupUnitResponseReader responseReader = new ServiceLookupUnitResponseReader(this.peer); // With timeout/QoS
        return responseReader.readResponse(qos);
        
    }

    private void writeRequestTopic(ServiceContract template, String requester) {
        ServiceContractTemplateTopic templateTopic = new ServiceContractTemplateTopic();
         /* Initialize the ServiceContractTemplateTopic */
        templateTopic.requester = requester;
        templateTopic.serviceName = template.serviceName;
        List properties = template.getProperties();
        templateTopic.properties = new ServiceContractTopicProperty[properties.size()];
        Iterator it = properties.iterator();
        int i=0;
        while (it.hasNext()) {
            ServiceContractProperty newProperty = (ServiceContractProperty)it.next();
            ServiceContractTopicProperty newTopicProperty = new ServiceContractTopicProperty();
            newTopicProperty.key = newProperty.key;
            newTopicProperty.value = newProperty.value;
            templateTopic.properties[i] = newTopicProperty;
            i++;
        }
        this.serviceContractTemplateServiceTopic.write(templateTopic);

    }

}
