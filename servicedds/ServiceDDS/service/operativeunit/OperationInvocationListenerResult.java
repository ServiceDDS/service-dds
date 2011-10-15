/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.service.operativeunit;

import java.util.logging.Level;
import java.util.logging.Logger;

import DDSLWS.OperationInvocationTopic;
import ServiceDDS.Peer;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;
import ServiceDDS.servicetopic.WriterServiceTopic;

/**
 *
 * @author PC
 */
public class OperationInvocationListenerResult implements Runnable {
    
    Peer peer;
    WriterServiceTopic operationInvocationResultServiceTopic;
    String operationName;
        
    public OperationInvocationListenerResult(Peer peer, String name) {
        this.peer = peer;
        this.operationName = name;
    }

    public void run() {
        try {
//            System.out.println("OperationInvocationListenerResult.run()...");
            this.operationInvocationResultServiceTopic = peer.newWriterServiceTopic(new OperationInvocationTopic(), "DDSLWS_OperationInvocationResultTopic_"+operationName,null);
            OperationInvocationTopic oir = new OperationInvocationTopic();
            oir.operationName = this.operationName;
            // TO FILL...
            this.operationInvocationResultServiceTopic.write(oir);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OperationInvocationListenerResult.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(OperationInvocationListenerResult.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(OperationInvocationListenerResult.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ImpossibleToCreateDDSTopic e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
