/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.service.contract;
import DDSLWS.ServiceContractTopic;
import DDSLWS.ServiceContractTopicProperty;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A Java class representation of a ServiceDDS service contract. 
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public class ServiceContract {
	/**
	 * The name of the service
	 */
    public String serviceName;
    /**
     * The name of the publisher peer
     */
    public String publisher;
    /**
     * Property list for discovering the service
     */
    LinkedList<ServiceContractProperty> properties = new LinkedList();
    /** 
     * Operation list
     */
    LinkedList<ServiceContractOperation> operations = new LinkedList();

    /**
     * Creates a service contract from a service contract topic
     * @param serviceContractTopic
     */
    public ServiceContract(ServiceContractTopic serviceContractTopic) {
        this.serviceName = serviceContractTopic.serviceName;
        this.publisher = serviceContractTopic.publisher;
        for (int i=0; i<serviceContractTopic.properties.length; i++) {
            ServiceContractTopicProperty newTopicProperty = serviceContractTopic.properties[i];
            this.properties.add(new ServiceContractProperty(
                    newTopicProperty.key,
                    newTopicProperty.value));
        }
    }


    /**
     * Creates a service contract using a name and a publisher name
     * @param serviceName
     * @param publisher
     */
    public ServiceContract(String serviceName, String publisher) {
        this.serviceName = serviceName;
        this.publisher = publisher;
    }

    
    /** 
     * Add a new property to the service contract
     * @param p The property
     */
    public void addProperty(ServiceContractProperty p) {
        properties.add(p);
    }

    /**
     * Add a new operation to the service contract
     * @param o
     */
    public void addOperation(ServiceContractOperation o) {
        operations.add(o);
    }
    
    /**
     * Get a list with all the properties in the service contract
     * @return List of ServiceContractProperties representing the properties in the ServiceContract
     */
    public List<ServiceContractProperty> getProperties() {
        return this.properties;
    }

    /**
     * Get a list with all the operations in the service contract
     * @return List of ServiceContractOperations representing the operations in the ServiceContract
     */
    public List<ServiceContractOperation> getOperations() {
        return this.operations;
    }

    
    public String toString() {
        String res = "["+serviceName+" ";
        Iterator it;
        res = res+" OPERATIONS=(";
        it = this.operations.iterator();
        while (it.hasNext()) {
            ServiceContractOperation newOperation = 
            	(ServiceContractOperation) it.next();
            res = res+"<"+newOperation.name+","+newOperation.argTypes.length+">";
        }
        res = res+")";
        
        res = res+" PROPERTIES=(";
        it = this.properties.iterator();
        while (it.hasNext()) {
            ServiceContractProperty newProperty = 
            	(ServiceContractProperty) it.next();
            res = res+"<"+newProperty.key+","+newProperty.value+">";
        }
        res = res+")";
        
        return res+"]";
    }
}
