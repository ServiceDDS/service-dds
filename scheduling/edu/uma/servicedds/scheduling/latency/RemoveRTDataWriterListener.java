package edu.uma.servicedds.scheduling.latency;

import javax.realtime.RelativeTime;

import DDSLWS.RemoveRTDataWriterTopic;
import ServiceDDS.servicetopic.ServiceTopic;
import ServiceDDS.servicetopic.ServiceTopicListener;

public class RemoveRTDataWriterListener implements ServiceTopicListener {

	LatencyAnalyzer la;
	
	public RemoveRTDataWriterListener(LatencyAnalyzer la) {
		this.la = la;
	}
	
	public void on_data_available(ServiceTopic arg0) {
		Object[] data = arg0.take();
		for (int i=0; i<data.length; i++) {
			RemoveRTDataWriterTopic newSample = (RemoveRTDataWriterTopic)data[i];
//			System.out.println("RemoveRTDataWriterListener.on_data_available() "+newSample.writerID);
			this.la.removeFromFeasibilityInternal(newSample.writerID);
		}		
		
	}

	public void on_requested_deadline_missed(ServiceTopic arg0) {
		// TODO Auto-generated method stub
		
	}

	public void on_sample_lost(ServiceTopic arg0) {
		// TODO Auto-generated method stub
		
	}

}
