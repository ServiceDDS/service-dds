package edu.uma.servicedds.scheduling.latency;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.realtime.PeriodicParameters;
import javax.realtime.PriorityParameters;
import javax.realtime.ReleaseParameters;
import javax.realtime.Schedulable;
import javax.realtime.Scheduler;
import javax.realtime.SchedulingParameters;

import edu.uma.servicedds.scheduling.exceptions.RTDWParametersNotSet;

import DDSLWS.RemoveRTDataWriterTopic;
import DDSLWS.SetRTDataWriterTopic;
import ServiceDDS.Group;
import ServiceDDS.Peer;
import ServiceDDS.exception.ImpossibleToCreateDDSDomainParticipant;
import ServiceDDS.exception.ImpossibleToCreateDDSTopic;
import ServiceDDS.servicetopic.ContentFilteredReaderServiceTopic;
import ServiceDDS.servicetopic.ReaderWriterServiceTopic;
import ServiceDDS.servicetopic.ServiceTopicListener;
import ServiceDDS.servicetopic.WriterServiceTopic;

public abstract class LatencyAnalyzer extends Scheduler {
	// Fields used to do the calculations
	protected TreeSet<RTDataWriter> priorityOrderedWriterSet;
	protected Hashtable<String,RTDataWriter> hashedWriterSet;

	WriterServiceTopic removeRTDataWriterTopic_writer;
	ContentFilteredReaderServiceTopic removeRTDataWriterTopic_reader;
	WriterServiceTopic setRTDataWriterTopic_writer;
	ContentFilteredReaderServiceTopic setRTDataWriterTopic_reader;
	
	private static int nextId=0;
	Peer peer;
	
	// Constructor
	public LatencyAnalyzer(String id) {
		try {
			this.peer = new Peer(this.getPolicyName()+id);
			this.peer.joinGroup(new Group("serviceDDS"));
			this.priorityOrderedWriterSet = new TreeSet<RTDataWriter>(new RTDataWriterComparator());
			this.hashedWriterSet = new Hashtable<String,RTDataWriter>();

			// Create internal service-topics	
			this.removeRTDataWriterTopic_reader = this.peer.newContentFilteredReaderServiceTopic(new RemoveRTDataWriterTopic(), "RemoveRTDataWriter", "peerID <> %0", new String[] {this.peer.getName()}, null);
			this.removeRTDataWriterTopic_reader.addListener(new RemoveRTDataWriterListener(this));
			this.removeRTDataWriterTopic_writer = this.peer.newWriterServiceTopic(new RemoveRTDataWriterTopic(), "RemoveRTDataWriter", null);
			
			this.setRTDataWriterTopic_writer = this.peer.newWriterServiceTopic(new SetRTDataWriterTopic(), "SetRTDataWriter", null);
			this.setRTDataWriterTopic_reader = this.peer.newContentFilteredReaderServiceTopic(new SetRTDataWriterTopic(), "SetRTDataWriter", "peerID <> %0", new String[] {this.peer.getName()}, null);
			ServiceTopicListener setListener = new SetRTDataWriterListener(this);
			this.setRTDataWriterTopic_reader.addListener(setListener);
			
			setListener.on_data_available(setRTDataWriterTopic_reader);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImpossibleToCreateDDSTopic e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImpossibleToCreateDDSDomainParticipant e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void updateState() {
		
		
	}

	/**
	 * This method first performs a feasibility analysis using the proposed 
	 * parameter objects as replacements for the current parameters of 
	 * schedulable. If the resulting system is feasible, this method replaces 
	 * the current parameters of schedulable with the proposed ones.
	 *
     * This method does not require that the schedulable object be in the 
     * feasibility set before it is called. If it is not initially a member 
     * of the feasibility set it will be added if the resulting system is 
     * feasible. 
     * 
	 * @param scedulable The schedulable object for which the changes are 
	 * proposed.
	 * @param release The proposed release parameters. If null, the default 
	 * value of the data writer object is used.
	 * @param rtdw The proposed real-time data writer parameters. If null, the
	 * parameters associated with the Schedulable (if available) will be used. 
	 * @param scheduling The proposed scheduling parameters. If null, the parameters
	 * already associated (if any) with the schedulable object will be used.
	 * @return
	 */
	public boolean setIfFeasible(RTDataWriter writer, 
			ReleaseParameters release, RTDWParameters rtdw, 
			SchedulingParameters scheduling) {
		if ((writer == null)||(writer.getScheduler()!=this))
			throw new IllegalArgumentException();
		
		boolean containsWriter = this.hashedWriterSet.contains(writer);
		// First remove the writer from the set (if already was in)
		if (containsWriter) this.removeFromFeasibilityInternal(writer.getFeasibilityId());
		// Store the old parameter set (some of them may be null)
		ReleaseParameters oldRP = writer.getReleaseParameters();
		RTDWParameters oldRTDWP = writer.getRTDWParameters();
		SchedulingParameters oldSP = writer.getSchedulingParameters();
		// Set the new parameters if necessary
		if ((release!=null)&&(release!=oldRP)) {
			writer.setReleaseParameters(release);
		} if ((rtdw!=null)&&(rtdw!=oldRTDWP)) {
			writer.setRTDWParameters(rtdw); 
		} if ((scheduling!=null)&&(scheduling!=oldSP)) {
			writer.setSchedulingParameters(scheduling); 
		}
		// Now that we have the new writer, add it... 
		// (the writer has been previously removed so it will be added for sure)
		this.addToFeasibilityInternal(writer);
		if (!this.isFeasible()) {
			// The set is not feasible, we have to restore
			// We can not simply change the parameters because
			// the writer could not be previously in the set...
			this.removeFromFeasibilityInternal(writer.getFeasibilityId());
			if (containsWriter) { // If belonged to, must be reinserted...
				writer.setReleaseParameters(oldRP);
				writer.setRTDWParameters(oldRTDWP);
				writer.setSchedulingParameters(oldSP);
				this.addToFeasibilityInternal(writer);
			}
			// Notice the until this point no sample is written into
			// the internal topics, since nothing has changed...
			return false;
		} else {
			// If set, we have to notify other latency writers...
			SetRTDataWriterTopic topicData = new SetRTDataWriterTopic();
			topicData.deadline = (int) writer.getReleaseParameters().getDeadline().getMilliseconds();
			topicData.sample_size = ((RTDataWriter)writer).getRTDWParameters().getSampleSize();
			topicData.priority = ((PriorityParameters)(writer.getSchedulingParameters())).getPriority();
			topicData.writerID = ((RTDataWriter)writer).getFeasibilityId();
			topicData.latency = (int) writer.getLatency().getMilliseconds();
			this.setRTDataWriterTopic_writer.write(topicData);
//			System.out.println("LatencyAnalyzer.setIfFeasibile(): "+topicData.writerID
//					+" with priority "+topicData.priority
//					+" and deadline "+topicData.deadline);
			return true;			
		}
	}
	
	@Override
	protected boolean addToFeasibility(Schedulable schedulable) {
		if ((schedulable == null)
				||(schedulable.getScheduler()!=this)
				||!(schedulable instanceof RTDataWriterInterface))
			throw new IllegalArgumentException();
		boolean added;
//		System.out.println(this.peer.getName()+".addToFeasibility: trying to add writer <" + 
//				((RTDataWriter)schedulable).getFeasibilityId() +">");
		added = this.addToFeasibilityInternal((RTDataWriterInterface)schedulable);
		boolean feasible = this.isFeasible();
		// If added, we have to notify other latency writers...
		if ((added) && (schedulable instanceof RTDataWriter)) {		
			RTDataWriter writer = (RTDataWriter) schedulable;
			SetRTDataWriterTopic topicData = new SetRTDataWriterTopic();
			if (writer.getReleaseParameters()!=null)
				topicData.deadline = (int) writer.getReleaseParameters().getDeadline().getMilliseconds();
			if (((RTDataWriter)writer).getRTDWParameters()!=null) {
				topicData.sample_size = writer.getRTDWParameters().getSampleSize();
				topicData.bandwidth = writer.getRTDWParameters().bandwidth;
				topicData.hb_period = (int)writer.getRTDWParameters().getHeartbeatPeriod().getMilliseconds();
				topicData.jitter = (int)writer.getRTDWParameters().getJitter().getMilliseconds();
				topicData.msg_cost = (int)writer.getRTDWParameters().getMessageCost().getMilliseconds();
				topicData.resolution  = (int)writer.getRTDWParameters().getResolution().getMilliseconds();
			}
			topicData.priority = ((PriorityParameters)(writer.getSchedulingParameters())).getPriority();
			topicData.deadline = (int)((PeriodicParameters)writer.getReleaseParameters()).getDeadline().getMilliseconds();
			topicData.writerID = ((RTDataWriter)writer).getFeasibilityId();
			topicData.peerID = this.peer.getName();
			topicData.latency = (int) writer.getLatency().getMilliseconds();

			this.setRTDataWriterTopic_writer.write(topicData);
//			System.out.println(this.peer.getName()+".addToFeasibility(): "+topicData.writerID
//					+" with priority "+topicData.priority
//					+", deadline "+topicData.deadline
//					+", latency "+topicData.latency
//					+", hb_period "+topicData.hb_period);			
		}
		return feasible;
	} 
	boolean addToFeasibilityInternal(RTDataWriterInterface writer) {
		// Add to the feasibility set
//		System.out.println(">>"+this.peer.getName()+".addToFeasibilityInternal: trying to add writer <" + 
//				((RTDataWriter)writer).getFeasibilityId() +">");
		if (!this.hashedWriterSet.containsKey(writer.getFeasibilityId())) {
//			System.out.println(">>"+this.peer.getName()+".addToFeasibilityInternal: LA does not contains writer <" + 
//					((RTDataWriter)writer).getFeasibilityId() +"> ...");
			this.priorityOrderedWriterSet.add((RTDataWriter)writer);
//			System.out.println(">>"+this.peer.getName()+".addToFeasibilityInternal: added to ordered set...");
			this.hashedWriterSet.put(writer.getFeasibilityId(), (RTDataWriter)writer);
//			System.out.println(">>"+this.peer.getName()+".addToFeasibilityInternal: added to hashed set...");
			this.calculateLatencies(writer);		
//			System.out.println(">>"+this.peer.getName()+".addToFeasibilityInternal: writer <" + 
//					((RTDataWriter)writer).getFeasibilityId()
//					+"> added to list");
			return true;
		}
//		System.out.println(">>"+this.peer.getName()+".addToFeasibilityInternal: writer <" + 
//				((RTDataWriter)writer).getFeasibilityId()
//				+"> NOT added to list");
		return false;
	}
	
	@Override
	protected boolean removeFromFeasibility(Schedulable schedulable) {
		if ((schedulable == null)||!(schedulable instanceof RTDataWriterInterface))
			throw new IllegalArgumentException();
		boolean removed = this.removeFromFeasibilityInternal(((RTDataWriterInterface)schedulable).getFeasibilityId());
		boolean feasible = this.isFeasible();
		// If finally removed, notify other latency analyzers
		if (removed) {		
			RemoveRTDataWriterTopic topicData = new RemoveRTDataWriterTopic();
			topicData.writerID = ((RTDataWriter)schedulable).getFeasibilityId();
			topicData.peerID = this.peer.getName();
			this.removeRTDataWriterTopic_writer.write(topicData);
//			System.out.println("LatencyAnalyzer.removeFromFeasibility(): "+topicData.writerID);
		}
		return feasible;
	}
	boolean removeFromFeasibilityInternal(String writerID) {
		// Remove from the feasibility set
		if (this.hashedWriterSet.containsKey(writerID)) {
			RTDataWriter writer = this.hashedWriterSet.get(writerID);
			this.priorityOrderedWriterSet.remove(writer);
			this.hashedWriterSet.remove(writerID);
			this.calculateLatencies(writer);
//				System.out.println(">>"+this.peer.getName()+".removeFromFeasibilityInternal: local writer <" + 
//						this.peer.getName()+((RTDataWriter)writer).getFeasibilityId()
//						+"> removed from list");
			return true;
		}
		return false;
	}
		
	@Override
	public boolean isFeasible() {
		boolean feasible = true;
		Iterator<RTDataWriter> it = this.priorityOrderedWriterSet.iterator();
		while ((feasible)&&(it.hasNext())) {
			RTDataWriter writer = it.next();
			// the set keeps being feasible while writer 
			// deadlines are greater than their latencies
			if ((writer.getReleaseParameters()!=null)
			  &&(writer.getReleaseParameters().getDeadline().compareTo(writer.getLatency())<0))
				feasible = false;
		}
		return feasible;	
	}
	
	/**
	 * The user must define the specific calculation method here. This method
	 * will be called each time a change is produces in the feasibility set
	 * and must assign new latencies to the writers using the RTDataWriterInterface.setLatency()
	 * method and consulting whatever 'get' methods needed. 
	 * @throws RTDWParametersNotSet 
	 */
	public abstract void calculateLatencies(RTDataWriterInterface sourceWriter);
	
	public int getNumWriters() {
		return this.priorityOrderedWriterSet.size();
	}
	
	synchronized String getNextFeasibilityId() {
		return this.peer.getName()+"_writer"+nextId++;
	}
	
	public String getName() {
		return this.peer.getName();
	}
}
