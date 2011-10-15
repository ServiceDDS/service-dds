package edu.uma.servicedds.scheduling.latency;

import java.util.Comparator;

import javax.realtime.PriorityParameters;

public class RTDataWriterComparator implements Comparator<RTDataWriter> {

	public int compare(RTDataWriter o1, RTDataWriter o2) {
		return  ((PriorityParameters)o2.getSchedulingParameters()).getPriority() -
				((PriorityParameters)o1.getSchedulingParameters()).getPriority();
	}
	
}
