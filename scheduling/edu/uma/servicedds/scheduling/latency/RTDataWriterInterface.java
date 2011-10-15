package edu.uma.servicedds.scheduling.latency;

import javax.realtime.RelativeTime;
import javax.realtime.SchedulingParameters;

public interface RTDataWriterInterface {

	public RelativeTime getLatency();
	public void setLatency(RelativeTime l);
	
	public String getFeasibilityId();
	public void setFeasibilityId(String id);
	
	public RTDWParameters getRTDWParameters();
	public SchedulingParameters getSchedulingParameters();
}
