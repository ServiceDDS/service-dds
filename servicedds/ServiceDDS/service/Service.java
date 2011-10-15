package ServiceDDS.service;

import ServiceDDS.service.contract.ServiceContract;
import ServiceDDS.service.operativeunit.provider.ServiceProvider;

/**
 * A container class for services published by a Peer. This is tipically the result of a previous
 * service publishing process. 
 * For operative interfaces of remote services, @see RemoteServiceInstance
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public class Service {
	/**
	 * The service contract
	 */
	public ServiceContract contract;
	/**
	 * The service provider, used to perform incoming executions of operations in the service
	 */
	public ServiceProvider provider;
	
	/**
	 * The only constructor
	 * @param c The service contract
	 * @param p The service provider that implements the operations
	 */
	public Service(ServiceContract c, ServiceProvider p) {
		this.contract = c;
		this.provider = p;
	}
}
