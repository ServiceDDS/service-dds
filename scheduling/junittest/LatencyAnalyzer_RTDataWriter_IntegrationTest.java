package junittests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.realtime.PeriodicParameters;
import javax.realtime.PriorityParameters;
import javax.realtime.RelativeTime;

import org.junit.Before;
import org.junit.Test;

import ServiceDDS.Peer;

import edu.uma.servicedds.scheduling.exceptions.RTDWParametersNotSet;
import edu.uma.servicedds.scheduling.foo.FooLatencyAnalyzer;
import edu.uma.servicedds.scheduling.latency.LatencyAnalyzer;
import edu.uma.servicedds.scheduling.latency.RTDWParameters;
import edu.uma.servicedds.scheduling.latency.RTDataWriter;

public class LatencyAnalyzer_RTDataWriter_IntegrationTest {
	LatencyAnalyzer la;
	RTDataWriter rtdw1;
	RTDataWriter rtdw2;
	RTDataWriter rtdw3;
	RTDWParameters rtdwParams1;
	RTDWParameters rtdwParams2;
	RTDWParameters rtdwParams3;
	PeriodicParameters releaseParams1;
	PeriodicParameters releaseParams2;
	PeriodicParameters releaseParams3;
	PriorityParameters schedulingParams1;
	PriorityParameters schedulingParams2;
	PriorityParameters schedulingParams3;
	@Before
	public void setUp() throws Exception {
		la = new FooLatencyAnalyzer("1");
		
		rtdwParams1 = new RTDWParameters();
		rtdwParams1.setBandwidth(80);
		rtdwParams1.setHeartbeatPeriod(new RelativeTime(10,0));
		rtdwParams1.setJitter(new RelativeTime(100,0));
		rtdwParams1.setMessageCost(new RelativeTime(200,0));
		rtdwParams1.setResolution(new RelativeTime(0,10));
		rtdwParams1.setSampleSize(1000);
		releaseParams1 = new PeriodicParameters(new RelativeTime(5000,0));
		releaseParams1.setDeadline(new RelativeTime(5000,0));
		schedulingParams1 = new PriorityParameters(5);
		rtdw1 = new RTDataWriter(la,releaseParams1,schedulingParams1,rtdwParams1);
	
		rtdwParams2 = new RTDWParameters();
		rtdwParams2.setBandwidth(80);
		rtdwParams2.setHeartbeatPeriod(new RelativeTime(10,0));
		rtdwParams2.setJitter(new RelativeTime(100,0));
		rtdwParams2.setMessageCost(new RelativeTime(200,0));
		rtdwParams2.setResolution(new RelativeTime(0,10));
		rtdwParams2.setSampleSize(1000);
		releaseParams2 = new PeriodicParameters(new RelativeTime(5000,0));
		releaseParams2.setDeadline(new RelativeTime(5000,0));
		schedulingParams2 = new PriorityParameters(3);
		rtdw2 = new RTDataWriter(la,releaseParams2,schedulingParams2,rtdwParams2);		
		
		rtdwParams3 = new RTDWParameters();
		rtdwParams3.setBandwidth(80);
		rtdwParams3.setHeartbeatPeriod(new RelativeTime(10,0));
		rtdwParams3.setJitter(new RelativeTime(100,0));
		rtdwParams3.setMessageCost(new RelativeTime(200,0));
		rtdwParams3.setResolution(new RelativeTime(0,10));
		rtdwParams3.setSampleSize(1000);
		releaseParams3 = new PeriodicParameters(new RelativeTime(5000,0));
		releaseParams3.setDeadline(new RelativeTime(500,0));
		schedulingParams3 = new PriorityParameters(1);
		rtdw3 = new RTDataWriter(la,releaseParams3,schedulingParams3,rtdwParams3);
	}

	@Test
	public void creation() {
		// LA is created
		assertNotNull(la);
		// RTDW is created and its parameters set properly
		assertNotNull(rtdw1);
		assertNotNull(rtdw1.getScheduler());
		assertNotNull(rtdw1.getRTDWParameters());
		assertNotNull(rtdw1.getReleaseParameters());
		assertNotNull(rtdw1.getSchedulingParameters());
		
		assertNotNull(rtdw2);
		assertNotNull(rtdw2.getScheduler());
		assertNotNull(rtdw2.getRTDWParameters());
		assertNotNull(rtdw2.getReleaseParameters());
		assertNotNull(rtdw2.getSchedulingParameters());
	}
	
	@Test
	public void addToFeasibility() {
		// Adds to feasibility and must be feasible since deadline>latency
		assertTrue(rtdw1.addToFeasibility());
		assertEquals(la.getNumWriters(),1);
		// Do not add repeated writers
		rtdw1.addToFeasibility();
		assertEquals(la.getNumWriters(),1);
		// Adding a second writer
		assertTrue(rtdw2.addToFeasibility());
		assertEquals(la.getNumWriters(),2);
		assertTrue(la.isFeasible());
		// Adding the last writer
		assertFalse(rtdw3.addToFeasibility());
		assertEquals(la.getNumWriters(),3);
		assertFalse(la.isFeasible());
		
	}
	
	@Test
	public void removeFromFeasibility() throws RTDWParametersNotSet {
		rtdwParams1.setMessageCost(new RelativeTime(10000,0));
		rtdw1.setRTDWParameters(rtdwParams1);
		assertFalse(rtdw1.addToFeasibility());
		assertFalse(la.isFeasible());
		assertEquals(la.getNumWriters(),1);
		assertTrue(rtdw1.removeFromFeasibility());
		assertEquals(la.getNumWriters(),0);
		// Adding a second writer
		rtdwParams2.setMessageCost(new RelativeTime(500,0));
		rtdw2.setRTDWParameters(rtdwParams2);
		assertTrue(rtdw2.addToFeasibility());
		assertFalse(rtdw1.addToFeasibility());
		assertEquals(la.getNumWriters(),2);
		rtdwParams1.setMessageCost(new RelativeTime(1000,0));
		rtdw1.setRTDWParameters(rtdwParams1);
		la.calculateLatencies(rtdw1);
		assertTrue(la.isFeasible());
		assertTrue(rtdw1.removeFromFeasibility());
		assertEquals(la.getNumWriters(),1);
	}
	
	@Test
	public void addIfFeasible() {
		assertTrue(rtdw1.addToFeasibility());
		assertEquals(la.getNumWriters(),1);
		rtdw1.removeFromFeasibility();
		rtdwParams1.setMessageCost(new RelativeTime(10000,0));
		assertFalse(rtdw1.addIfFeasible());
		assertEquals(la.getNumWriters(),0);
		// Try with two writers
		rtdwParams1.setMessageCost(new RelativeTime(1000,0));
		assertTrue(rtdw1.addToFeasibility());
		assertEquals(la.getNumWriters(),1);
		rtdwParams2.setMessageCost(new RelativeTime(10000,0));
		assertFalse(rtdw2.addIfFeasible());
		assertEquals(la.getNumWriters(),1);
	}
	
	@Test
	public void setDeadlineIfFeasible() {
		// Add a couple of writers and then try to change on of them
		assertTrue(rtdw1.addIfFeasible());
		assertTrue(rtdw2.addIfFeasible());
		assertEquals(la.getNumWriters(),2);
		releaseParams3.setDeadline(new RelativeTime(10,0));
		assertFalse(rtdw2.setReleaseParametersIfFeasible(releaseParams3));
		assertEquals(la.getNumWriters(),2);
		// Try for true
		releaseParams3.setDeadline(new RelativeTime(10000,0));
		assertTrue(rtdw2.setReleaseParametersIfFeasible(releaseParams3));
		assertEquals(la.getNumWriters(),2);
		// Try removing first, for false
		rtdw2.removeFromFeasibility();
		assertEquals(la.getNumWriters(),1);
		releaseParams3.setDeadline(new RelativeTime(10,0));
		assertFalse(rtdw2.setReleaseParametersIfFeasible(releaseParams3));
		assertEquals(la.getNumWriters(),1);
		// Try removing first, for true
		rtdw2.removeFromFeasibility();
		assertEquals(la.getNumWriters(),1);
		releaseParams3.setDeadline(new RelativeTime(10000,0));
		assertTrue(rtdw2.setReleaseParametersIfFeasible(releaseParams3));
		assertEquals(la.getNumWriters(),2);
		
	}
	
	@Test
	public void getLatency() {
		assertTrue(rtdw1.addIfFeasible());
//		System.out.println("LatencyAnalyzer_RTDataWriter_IntegrationTest.getLatency(): latency = "+rtdw1.getLatency().getMilliseconds());
		assertTrue(rtdw1.getLatency().getMilliseconds()==200);
		assertTrue(rtdw2.addIfFeasible());
		assertEquals(la.getNumWriters(),2);
		assertTrue(rtdw2.getLatency().getMilliseconds()==400);
		rtdw1.removeFromFeasibility();
		assertTrue(rtdw2.getLatency().getMilliseconds()==200);
	}
}
