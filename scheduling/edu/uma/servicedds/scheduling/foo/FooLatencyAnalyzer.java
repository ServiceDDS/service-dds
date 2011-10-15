package edu.uma.servicedds.scheduling.foo;

import java.util.Iterator;

import javax.realtime.MemoryParameters;
import javax.realtime.PriorityParameters;
import javax.realtime.ProcessingGroupParameters;
import javax.realtime.RelativeTime;
import javax.realtime.ReleaseParameters;
import javax.realtime.Schedulable;
import javax.realtime.SchedulingParameters;

import edu.uma.servicedds.scheduling.exceptions.RTDWParametersNotSet;
import edu.uma.servicedds.scheduling.latency.LatencyAnalyzer;
import edu.uma.servicedds.scheduling.latency.RTDataWriter;
import edu.uma.servicedds.scheduling.latency.RTDataWriterInterface;

public class FooLatencyAnalyzer extends LatencyAnalyzer {

	public FooLatencyAnalyzer(String id) {
		super(id);
	}
	
	public void calculateLatencies(RTDataWriterInterface sourceWriter) {
		// Iterate through the priority-ordered local writer list and assign latencies
		//System.out.println("FooLatencyAnalyzer.calculateLatencies()");
		if (sourceWriter.getRTDWParameters() == null) {/* TODO throw an exception */ return; }
//		System.out.println(this.getName()+".calculateLatencies(): there are "+this.priorityOrderedWriterSet.size()+" writers in the set.");
		Iterator<RTDataWriter> it = this.priorityOrderedWriterSet.iterator();
		boolean found = false;
		long newLatency = 0;
		int i=0;
		RTDataWriterInterface newWriter = null;
		while (it.hasNext() && !found) {
			newWriter = it.next();
			i++;
			found =  ((PriorityParameters)newWriter.getSchedulingParameters()).getPriority() 
					<= ((PriorityParameters)sourceWriter.getSchedulingParameters()).getPriority();
			if (!found) { 
				newLatency = newWriter.getLatency().getMilliseconds();
				System.out.println(this.getName()+".calculateLatencies("+i+"): acumulated latency of "+newLatency);
			}
			else { 
				newWriter.setLatency(new RelativeTime(newLatency+newWriter.getRTDWParameters().getMessageCost().getMilliseconds(),0));
				newLatency = newWriter.getLatency().getMilliseconds();
				System.out.println(this.getName()+".calculateLatencies("+i+"): assigned latency "+newWriter.getLatency()+" to writer "+newWriter.getFeasibilityId());
			}
		}
		
		while (it.hasNext()) {
			newWriter = it.next();
			i++;
			newWriter.setLatency(new RelativeTime(newLatency+newWriter.getRTDWParameters().getMessageCost().getMilliseconds(),0));
			System.out.println(this.getName()+".calculateLatencies("+i+"): assigned latency "+newWriter.getLatency()+" to writer "+newWriter.getFeasibilityId());
		}
	}

	public String getPolicyName() {
		return "FooLatencyAnalyzer";
	}
	
	// Not implemented methods...
	
	public void fireSchedulable(Schedulable arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean setIfFeasible(Schedulable arg0, ReleaseParameters arg1,
			MemoryParameters arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean setIfFeasible(Schedulable arg0, ReleaseParameters arg1,
			MemoryParameters arg2, ProcessingGroupParameters arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean setIfFeasible(Schedulable arg0, SchedulingParameters arg1,
			ReleaseParameters arg2, MemoryParameters arg3,
			ProcessingGroupParameters arg4) {
		// TODO Auto-generated method stub
		return false;
	}

}
