package edu.uma.servicedds.scheduling.service;

import javax.realtime.RelativeTime;
import javax.realtime.ReleaseParameters;
import javax.realtime.Schedulable;
import javax.realtime.SchedulingParameters;

public abstract class RTServiceInvocation implements Schedulable {
	private RelativeTime wcet;
	private WCETCalculator calculator;
	private PriorityAssignment passignment;
	// constructors
	public RTServiceInvocation(WCETCalculator c, PriorityAssignment pa){
	}

	// add to feasibility set methods
	public boolean addIfFeasible(){
		return false;
	}
	
	public boolean addToFeasibility(){
		return false;
	}
	
	public boolean removeFromFeasibility(){
		return false;
	}

	// parameter setting and getting methods
	public boolean setReleaseParametersIfFeasible(ReleaseParameters release){
			return false;
	}
	
	public boolean setSchedulingParametersIfFeasible(SchedulingParameters sched){
		return false;
	}
	  
	public void setWCET(RelativeTime wcet){
		this.wcet = wcet;
	}

	public ReleaseParameters getReleaseParameters(){
		return null;
	}
	
	public SchedulingParameters getSchedulingParameters(){
		return null;
	}
	
	
	public RelativeTime getWCET(){
		return this.wcet;
	}

	// methods for setting and getting the 
	// WCETCalculator and PriorityAssignment
	public void setCalculator(WCETCalculator calc){
		this.calculator = calc;
	}
	public WCETCalculator getCalculator() {
		return this.calculator;
	}
	
	public void setPriorityAssignment(PriorityAssignment pa) {
		this.passignment = pa;
	}
	
	public PriorityAssignment getPriortyAssignment() {
		return this.passignment;
	}

}
