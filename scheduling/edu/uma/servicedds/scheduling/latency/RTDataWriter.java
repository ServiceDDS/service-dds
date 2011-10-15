package edu.uma.servicedds.scheduling.latency;

import javax.realtime.MemoryParameters;
import javax.realtime.ProcessingGroupParameters;
import javax.realtime.RelativeTime;
import javax.realtime.ReleaseParameters;
import javax.realtime.Schedulable;
import javax.realtime.Scheduler;
import javax.realtime.SchedulingParameters;

public class RTDataWriter implements Schedulable, RTDataWriterInterface {
	
	LatencyAnalyzer latencyAnalyzer;
	ReleaseParameters releaseParameters;
	SchedulingParameters schedulingParameters;
	RTDWParameters rtdwParameters;
	RelativeTime latency;
	MemoryParameters memoryParameters;
	ProcessingGroupParameters processingGroupParameters;
	int numReaders;
	private String feasibilityId;
	
	// constructors
	public RTDataWriter(LatencyAnalyzer la, ReleaseParameters rp, SchedulingParameters sp, RTDWParameters rtdw) {
		this.latencyAnalyzer = la;
		this.releaseParameters = rp;
		this.schedulingParameters = sp;
		this.rtdwParameters = rtdw;
		this.feasibilityId = la.getNextFeasibilityId();
//		System.out.println("RTDataWriter: local writer <" + this.feasibilityId + "> created");
	}
	public RTDataWriter(LatencyAnalyzer la, String id, ReleaseParameters rp, SchedulingParameters sp, RTDWParameters rtdw) {
		this.latencyAnalyzer = la;
		this.releaseParameters = rp;
		this.schedulingParameters = sp;
		this.rtdwParameters = rtdw;		
		this.feasibilityId = id;
	}
	
	public boolean addIfFeasible() {
		return this.latencyAnalyzer.setIfFeasible(
				this, 
				this.releaseParameters, 
				this.rtdwParameters, 
				this.schedulingParameters
				);
	}
	
	/**
	 * 
	 * @return
	 */
	public RelativeTime getLatency() {
		return this.latency;
	}
	
	/**
	 * 
	 * @param latency
	 */
	public void setLatency(RelativeTime latency) {
		this.latency = latency;
	}

	/**
	 * 
	 * @param rtdw
	 */
	public void setRTDWParameters(RTDWParameters rtdw) {
		this.rtdwParameters = rtdw;
	}
	
	/**
	 * 
	 * @return
	 */
	public RTDWParameters getRTDWParameters() {
		return this.rtdwParameters;
	}
		
	public boolean addToFeasibility() {
		return this.latencyAnalyzer.addToFeasibility(this);
	}

	public MemoryParameters getMemoryParameters() {
		return this.memoryParameters;
	}

	public ProcessingGroupParameters getProcessingGroupParameters() {
		return this.processingGroupParameters;
	}

	public ReleaseParameters getReleaseParameters() {
		return this.releaseParameters;
	}

	public Scheduler getScheduler() {
		return this.latencyAnalyzer;
	}

	public SchedulingParameters getSchedulingParameters() {
		return this.schedulingParameters;
	}

	public boolean removeFromFeasibility() {
		return this.latencyAnalyzer.removeFromFeasibility(this);
	}

	public boolean setIfFeasible(ReleaseParameters arg0, MemoryParameters arg1) {
		return this.latencyAnalyzer.setIfFeasible(this, arg0, arg1);
	}

	public boolean setIfFeasible(ReleaseParameters arg0,
			ProcessingGroupParameters arg1) {
		return this.latencyAnalyzer.setIfFeasible(this, arg0, this.memoryParameters, arg1);
	}

	public boolean setIfFeasible(ReleaseParameters arg0, MemoryParameters arg1,
			ProcessingGroupParameters arg2) {
		return this.latencyAnalyzer.setIfFeasible(this,arg0,arg1,arg2);
	}

	public boolean setIfFeasible(SchedulingParameters arg0,
			ReleaseParameters arg1, MemoryParameters arg2) {
		return this.latencyAnalyzer.setIfFeasible(this, arg0, arg1, arg2, this.processingGroupParameters);
	}

	public boolean setIfFeasible(SchedulingParameters arg0,
			ReleaseParameters arg1, MemoryParameters arg2,
			ProcessingGroupParameters arg3) {
		return this.latencyAnalyzer.setIfFeasible(this, arg0, arg1, arg2, arg3);
	}

	public void setMemoryParameters(MemoryParameters arg0) {
		this.memoryParameters = arg0;		
	}

	public boolean setMemoryParametersIfFeasible(MemoryParameters arg0) {
		return this.latencyAnalyzer.setIfFeasible(this, this.releaseParameters, arg0);
	}

	public void setProcessingGroupParameters(ProcessingGroupParameters arg0) {
		this.processingGroupParameters = arg0;		
	}

	public boolean setProcessingGroupParametersIfFeasible(
			ProcessingGroupParameters arg0) {
		return this.latencyAnalyzer.setIfFeasible(this, this.releaseParameters, this.memoryParameters, arg0);
	}

	public void setReleaseParameters(ReleaseParameters arg0) {
		this.releaseParameters = arg0;
	}

	public boolean setReleaseParametersIfFeasible(ReleaseParameters arg0) {
		return this.latencyAnalyzer.setIfFeasible(this, arg0, this.rtdwParameters, this.schedulingParameters);
	}

	public void setScheduler(Scheduler arg0) {
		if (arg0 instanceof LatencyAnalyzer) {
			this.latencyAnalyzer = (LatencyAnalyzer) arg0;
		}
	}

	public void setScheduler(Scheduler arg0, SchedulingParameters arg1,
			ReleaseParameters arg2, MemoryParameters arg3,
			ProcessingGroupParameters arg4) {
		if ((arg0 == null)||!(arg0 instanceof LatencyAnalyzer))
			throw new IllegalArgumentException();
		this.setScheduler(arg0);
		// Not sure what to do with the rest of parameters....
	}

	public void setSchedulingParameters(SchedulingParameters arg0) {
		this.schedulingParameters = arg0;
	}

	public boolean setSchedulingParametersIfFeasible(SchedulingParameters arg0) {
		return this.latencyAnalyzer.setIfFeasible(this, this.releaseParameters, this.rtdwParameters, arg0);
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}
	   
	/**
	 * 
	 * @return
	 */
	public int getNumReaders() {
		return this.numReaders;
	}
	
	/**
	 * 
	 * @param nr
	 */
	public void setNumreaders(int nr) {
		this.numReaders = nr;
	}

	public void setFeasibilityId(String i) {
		this.feasibilityId = i;		
	}
	
	public String getFeasibilityId() {
		return this.feasibilityId;
	}

	public boolean equals(RTDataWriter o) {
		return this.getFeasibilityId().compareTo(o.getFeasibilityId())==0;
	}

}