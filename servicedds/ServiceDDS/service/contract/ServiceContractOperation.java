/**
 * 
 */
package ServiceDDS.service.contract;

/**
 * An object representing a service operation in a contract
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 * 
 */
public class ServiceContractOperation {
	/**
	 * The name of the operation
	 */
	String name;
	/** 
	 * The types of the arguments
	 */
	Class[] argTypes;
	
	/**
	 * The basic constructor
	 * @param name The name of the operation
	 * @param argTypes The types of its arguments
	 */
	public ServiceContractOperation(String name, Class[] argTypes) {
		this.name = name;
		this.argTypes = argTypes;
	}
}
