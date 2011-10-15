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

import edu.uma.servicedds.scheduling.foo.FooLatencyAnalyzer;
import edu.uma.servicedds.scheduling.latency.LatencyAnalyzer;
import edu.uma.servicedds.scheduling.latency.RTDWParameters;
import edu.uma.servicedds.scheduling.latency.RTDataWriter;


public class LatencyAnalyzer_Remote_IntegrationTest {
	LatencyAnalyzer la1;
	LatencyAnalyzer la2;	
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
		// Create two LatencyAnalizer
		la1 = new FooLatencyAnalyzer("1");
		la2 = new FooLatencyAnalyzer("2");
		// Create first writer and its parameters
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
		rtdw1 = new RTDataWriter(la1,releaseParams1,schedulingParams1,rtdwParams1);
		// Create second writer and its parameters
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
		rtdw2 = new RTDataWriter(la1,releaseParams2,schedulingParams2,rtdwParams2);	
		// Create third writer and its parameters associated with the second analyzer
		rtdwParams3 = new RTDWParameters();
		rtdwParams3.setBandwidth(80);
		rtdwParams3.setHeartbeatPeriod(new RelativeTime(10,0));
		rtdwParams3.setJitter(new RelativeTime(100,0));
		rtdwParams3.setMessageCost(new RelativeTime(200,0));
		rtdwParams3.setResolution(new RelativeTime(0,10));
		rtdwParams3.setSampleSize(1000);
		releaseParams3 = new PeriodicParameters(new RelativeTime(5000,0));
		releaseParams3.setDeadline(new RelativeTime(5000,0));
		schedulingParams3 = new PriorityParameters(1);
		rtdw3 = new RTDataWriter(la2,releaseParams3,schedulingParams3,rtdwParams3);
		
	}
	
	@Test
	public void creation() {
		// Test if LAs are created
		assertNotNull(la1);
		assertNotNull(la2);
		// Test if RTDWs are created and its parameters set
		assertNotNull(rtdw1);
		assertNotNull(rtdw1.getScheduler());
		assertNotNull(rtdw1.getRTDWParameters());
		assertNotNull(rtdw1.getReleaseParameters());
		assertNotNull(rtdw1.getSchedulingParameters());
		assertNotNull(rtdw1.getRTDWParameters());
		
		assertNotNull(rtdw2);
		assertNotNull(rtdw2.getScheduler());
		assertNotNull(rtdw2.getRTDWParameters());
		assertNotNull(rtdw2.getReleaseParameters());
		assertNotNull(rtdw2.getSchedulingParameters());
		assertNotNull(rtdw2.getRTDWParameters());

		assertNotNull(rtdw3);
		assertNotNull(rtdw3.getScheduler());
		assertNotNull(rtdw3.getRTDWParameters());
		assertNotNull(rtdw3.getReleaseParameters());
		assertNotNull(rtdw3.getSchedulingParameters());
		assertNotNull(rtdw3.getRTDWParameters());

	}	
	
	@Test
	public void addToFeasibility() {
		// Adds to feasibility and must be feasible since deadline>latency
		assertTrue(rtdw1.addToFeasibility());
		assertEquals(la1.getNumWriters(),1);
		assertTrue(rtdw1.getLatency().getMilliseconds()==200);
		
		assertTrue(rtdw2.addToFeasibility());
		assertTrue(rtdw2.getLatency().getMilliseconds()==400);
		assertEquals(la1.getNumWriters(),2);
		
		assertTrue(rtdw3.addToFeasibility());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(la1.getNumWriters(),3);
		assertEquals(la2.getNumWriters(),3);
		assertTrue(rtdw1.getLatency().getMilliseconds()==200);
		assertTrue(rtdw2.getLatency().getMilliseconds()==400);
		assertEquals(rtdw3.getLatency().getMilliseconds(),600);
		
	}	
	
	@Test
	public void removeFromFeasibility() {
		this.addToFeasibility();
		assertTrue(rtdw2.removeFromFeasibility());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(la1.getNumWriters(),2);
		assertEquals(la2.getNumWriters(),2);
		assertEquals(rtdw1.getLatency().getMilliseconds(),200);
		assertEquals(rtdw3.getLatency().getMilliseconds(),400);
	}
	
}
