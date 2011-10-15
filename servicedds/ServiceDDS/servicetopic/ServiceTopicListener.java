/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceDDS.servicetopic;


/**
 * This interface is a wrapper for easy handling DDS topic events. Event names are the same than those generated
 * from the IDL to Java mapping
 * @author Jose Angel Dianes
 * @version 0.1b, 09/24/2010
 */
public interface ServiceTopicListener {

    public void on_data_available(ServiceTopic serviceTopic);

	public void on_requested_deadline_missed(ServiceTopic serviceTopic);

	public void on_sample_lost(ServiceTopic serviceTopic);

}
