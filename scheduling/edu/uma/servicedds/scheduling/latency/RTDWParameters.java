package edu.uma.servicedds.scheduling.latency;

import javax.realtime.RelativeTime;

public class RTDWParameters implements Cloneable {
	   
	RelativeTime cost;
	int sampleSize;
	int bandwidth;
	RelativeTime heartbeatPeriod;
	RelativeTime jitter;
	RelativeTime resolution;
		
	// constructors
	public RTDWParameters() {
		   
	}
	   
	// methods
	public Object clone() {
		return null;
		   // TODO
	}
	
	/**
	 * It returns the message cost associated with this parameter set
	 * @return the message cost associated with this parameter set
	 */
	public RelativeTime getMessageCost() {
		return this.cost;	   
	}
	/**
	 * It sets the message cost associated with this parameter set
	 * @param cost The cost to be associated with the parameter set
	 */
	public void setMessageCost(RelativeTime cost) {
		this.cost = cost;
	}
	/**
	 * It returns the sample size associated with this parameter set. The specific
	 * unit will depend on the usage of these class.
	 * @return the sample size associated with this parameter set
	 */
	public int getSampleSize() {
		return this.sampleSize;
	}
	/**
	 * It sets the sample size associated with this parameter set.
	 * @param size the sample size associated with this parameter set.
	 */
	public void setSampleSize(int size) {
		this.sampleSize = size;
	}	
	/**
	 * It returns the bandwidth associated with this set of parameters.
	 * @return the bandwidth associated with this set of parameters
	 */
	public int getBandwidth() {
		return this.bandwidth;
	}
	/**
	 * It sets the bandwidth associated with this set of parameters.
	 * @param bandwidth the badnwidth that wants to be associated with this set of parameters
	 */
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}
	/**
	 * It returns the heartbeat period associated with this set of parameters.
	 * @return
	 */
	public RelativeTime getHeartbeatPeriod() {
		return this.heartbeatPeriod;
	}
	/**
	 * It sets the heartbeat period associated with this set of parameters.
	 * @param period
	 */
	public void setHeartbeatPeriod(RelativeTime period) {
		this.heartbeatPeriod=period;
	}
	/**
	 * It returns the jitter associated with this set of parameters.
	 * @return the jitter associated with this set of parameters
	 */
	public RelativeTime getJitter() {
		return this.jitter;
	}
	/**
	 * It sets the jitter associated with this set of parameters.
	 * @param jitter the jitter to be associated with this set of parameters
	 */
	public void setJitter(RelativeTime jitter) {
		this.jitter = jitter;
	}
	/**
	 * It returns the time resolution associated with this set of parameters
	 * @return the time resolution associated with this set of parameters
	 */
	public RelativeTime getResolution() {
		return this.resolution;
	}
	/**
	 * It sets the time resolution associated with this set of parameters
	 * @param res the time resolution to be associated with this set of parameters
	 */
	public void setResolution(RelativeTime res) {
		this.resolution = res;
	}

}
