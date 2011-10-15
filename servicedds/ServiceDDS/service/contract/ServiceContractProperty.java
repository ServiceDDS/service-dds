/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.service.contract;

/**
 * An object representing a service contract property
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 * 
 */
public class ServiceContractProperty {
	/**
	 * The key that identifies the property
	 */
    public String key;
    /**
     * The value for the property
     */
    public String value;

    /**
     * The basic constructor
     * @param key
     * @param value
     */
    public ServiceContractProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
}
