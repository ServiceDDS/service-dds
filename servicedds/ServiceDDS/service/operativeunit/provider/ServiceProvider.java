package ServiceDDS.service.operativeunit.provider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A service provider must implement this abstract class for the ServiceDDS framework
 * be able to invoke its operations when required by any potential client. An instance of this
 * class is passed as argument to the Peer.publish() method so the framework has a reference
 * for executing operation invocations.
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public abstract class ServiceProvider {

	/**
	 * This method is topically invoked by the ServiceDDS framework to invoke methods 
	 * in the class implementing ServiceProvider. These methods typically represent
	 * the operations of a service provided (and published) by a Peer.
	 * @param operationName The name of the operation
	 * @param args The arguments of the invocation
	 * @return The result, if any
	 */
	public Object call(String operationName, Object[] args) {
		Object res = null;
        try {          
            Class cls = this.getClass();
            Method meth = cls.getMethod(operationName, new Class[]{});
            res = meth.invoke(this, args);
        } catch (NoSuchMethodException ne) { 
        	ne.printStackTrace();
        } catch (InvocationTargetException ite) {
        	ite.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
	}
}
