/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.service.operativeunit;

import java.util.logging.Level;
import java.util.logging.Logger;

import DDSLWS.OperationInvocationTopic;
import ServiceDDS.Peer;
import ServiceDDS.QoSParameters;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;
import ServiceDDS.service.discoveryengine.ServiceRequestListener;
import ServiceDDS.servicetopic.ReaderServiceTopic;

/**
 *
 * @author PC
 */
public class OperationInvocationResultReader {
	Peer peer;
	ReaderServiceTopic operationInvocationResultServiceTopic;
	String operationName;
	public OperationInvocationResultReader(Peer peer, String operationName) {
	       this.peer = peer;
	       this.operationName = operationName;
	        try {
	            this.operationInvocationResultServiceTopic = this.peer.newReaderServiceTopic(new OperationInvocationTopic(), "DDSLWS_OperationInvocationResultTopic_"+operationName,null);
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
    public Object readResult(QoSParameters qos) {
//    	System.out.println("OperationInvocationUnitResultReader.readResult(): reading result for operation "+this.operationName);
        this.operationInvocationResultServiceTopic.waitData(qos);

        Object[] data = this.operationInvocationResultServiceTopic.take();

        return data[0];
    }
}
