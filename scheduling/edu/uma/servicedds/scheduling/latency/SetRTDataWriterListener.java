package edu.uma.servicedds.scheduling.latency;

import javax.realtime.PeriodicParameters;
import javax.realtime.PriorityParameters;
import javax.realtime.RelativeTime;
import javax.realtime.ReleaseParameters;
import javax.realtime.SchedulingParameters;

import DDSLWS.SetRTDataWriterTopic;
import ServiceDDS.servicetopic.ServiceTopic;
import ServiceDDS.servicetopic.ServiceTopicListener;

public class SetRTDataWriterListener implements ServiceTopicListener {

	LatencyAnalyzer la;
	
	public SetRTDataWriterListener(LatencyAnalyzer la) {
		this.la = la;
	}
	
	public void on_data_available(ServiceTopic arg0) {
//		System.out.println("SetRTDataWriterListener.on_data_available: "+arg0+" "+la);
		Object[] data = arg0.take();
		for (int i=0; i<data.length; i++) {
			SetRTDataWriterTopic newSample = (SetRTDataWriterTopic)data[i];
//			System.out.println("SetRTDataWriterListener.on_data_available("+i+") "+newSample.writerID
//					+" with priority "+newSample.priority
//					+", deadline "+newSample.deadline
//					+", latency "+newSample.latency);
			
			RTDWParameters rtdwParams = new RTDWParameters();
			rtdwParams.setBandwidth(newSample.bandwidth);
			rtdwParams.setHeartbeatPeriod(new RelativeTime(newSample.hb_period,0));
			rtdwParams.setJitter(new RelativeTime(newSample.jitter,0));
			rtdwParams.setMessageCost(new RelativeTime(newSample.msg_cost,0));
			rtdwParams.setResolution(new RelativeTime(newSample.resolution,0));
			rtdwParams.setSampleSize(newSample.sample_size);
			PeriodicParameters releaseParams = new PeriodicParameters(new RelativeTime(newSample.deadline+1,0));
			releaseParams.setDeadline(new RelativeTime(newSample.deadline+1,0));
			PriorityParameters schedulingParams = new PriorityParameters(newSample.priority);
			RTDataWriter newWriter = new RTDataWriter(
					this.la,
					newSample.writerID,
					releaseParams, 
					schedulingParams,
					rtdwParams);
			newWriter.setLatency(new RelativeTime(newSample.latency,0));		
			this.la.addToFeasibilityInternal(newWriter);
		}
		
	}

	public void on_requested_deadline_missed(ServiceTopic arg0) {
		// TODO Auto-generated method stub
		
	}

	public void on_sample_lost(ServiceTopic arg0) {
		// TODO Auto-generated method stub
		
	}

}
