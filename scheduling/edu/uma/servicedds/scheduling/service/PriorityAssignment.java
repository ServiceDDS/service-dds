package edu.uma.servicedds.scheduling.service;


public abstract class PriorityAssignment {
	// Requests
	protected RTServiceInvocation last;
	   
	// Constructor

	// Methods
	public void addRequest(RTServiceInvocation p) {
		
	}
	
	public void removeRequest(RTServiceInvocation p) {
		
	}
	
	// This method is called just after addRequest
	// and it has access to the last and all the
	// previous requests
	protected abstract void assignPriorities();

}
