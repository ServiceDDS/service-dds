package edu.uma.servicedds.scheduling.service;

import java.util.Hashtable;

import javax.realtime.RelativeTime;
import javax.realtime.Schedulable;
import javax.realtime.Scheduler;

public abstract class WCETCalculator extends Scheduler {
	// Accepted request parameters
	protected Hashtable acceptedRequestSet;
	   
	// Methods
	public abstract RelativeTime getBudeget();
	
	protected boolean addToFeasibility(Schedulable schedulable) {
		return false;
	}
	
	protected boolean removeFromFeasibility(Schedulable schedulable) {
		return false;
	}
	   
	protected abstract void calculateWCET();
	   
	public boolean addSetToScheduler() {
		return false;
	}

}
