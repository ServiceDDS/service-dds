/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.service.contract;

import java.util.LinkedList;
import DDSLWS.ServiceContractTemplateTopic;
import DDSLWS.ServiceContractTopicProperty;

import java.util.Iterator;
import java.util.List;

/**
 * Service contract templates are incomplete service contracts used for service discovery
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 * 
 */
public class ServiceContractTemplate {
	/**
	 * The name of the required service
	 */
    public String serviceName;
    /**
     * The name fo the peer requesting the service
     */
    public String requester;
    /**
     * The list of the properties that want to be present in the service
     */
    LinkedList<ServiceContractProperty> properties = new LinkedList();

    /**
     * A basic constructor
     * @param serviceContractTemplateTopic
     */
    public ServiceContractTemplate(ServiceContractTemplateTopic serviceContractTemplateTopic) {
        this.serviceName = serviceContractTemplateTopic.serviceName;
        this.requester = serviceContractTemplateTopic.requester;
        for (int i=0; i<serviceContractTemplateTopic.properties.length; i++) {
            ServiceContractTopicProperty newTemplateTopicProperty =
                    serviceContractTemplateTopic.properties[i];
            this.properties.add(new ServiceContractProperty(
                    newTemplateTopicProperty.key,
                    newTemplateTopicProperty.value));
        }
    }

    /*
    public ServiceContractTemplate(String serviceName, String requester) {
        this.serviceName = serviceName;
        this.requester = requester;
    }
*/
    /**
     * Adds a property to the template
     * @param p The service contract property
     */
    public void addProperty(ServiceContractProperty p) {
        properties.add(p);
    }

    /**
     * Get all the properties
     * @return A List with all thep roperties in teh contract template
     */
    public List getProperties() {
        return this.properties;
    }

    @Override
    public String toString() {
        String res = serviceName;
        Iterator it = this.properties.iterator();
        while (it.hasNext()) {
            ServiceContractProperty newProperty = (ServiceContractProperty) it.next();
            res = res+" <"+newProperty.key+","+newProperty.value+">";
        }
        return res;
    }
}
