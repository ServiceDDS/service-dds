package junittests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.realtime.RelativeTime;

import org.junit.Before;
import org.junit.Test;

import edu.uma.servicedds.scheduling.latency.RTDWParameters;

public class RTDWParametersUnitTests {
	RTDWParameters rtdw;
	@Before
	public void setUp() throws Exception {
		rtdw = new RTDWParameters();
	}

	@Test
	public void creation() {
		assertNotNull(rtdw);
	}
	
	@Test
	public void bandwidth() {
		// Bandwidth setting and getting
		rtdw.setBandwidth(80);
		assertEquals(rtdw.getBandwidth(), 80);
	}
	
	@Test
	public void heartbeatPeriod() {
		// setting and getting heartbeat period
		rtdw.setHeartbeatPeriod(new RelativeTime(10,0));
		assertNotNull(rtdw.getHeartbeatPeriod());
		assertTrue(rtdw.getHeartbeatPeriod().compareTo(new RelativeTime(5,0))>0);
		assertTrue(rtdw.getHeartbeatPeriod().compareTo(new RelativeTime(15,0))<0);
		assertTrue(rtdw.getHeartbeatPeriod().compareTo(new RelativeTime(10,0))==0);
	}
	
	@Test
	public void jitter() {
		// setting and getting jitter
		rtdw.setJitter(new RelativeTime(100,0));
		assertNotNull(rtdw.getJitter());
		assertTrue(rtdw.getJitter().compareTo(new RelativeTime(50,0))>0);
		assertTrue(rtdw.getJitter().compareTo(new RelativeTime(150,0))<0);
		assertTrue(rtdw.getJitter().compareTo(new RelativeTime(100,0))==0);		
	}
	
	@Test
	public void messageCost() {
		// setting and getting message cost
		rtdw.setMessageCost(new RelativeTime(200,0));
		assertNotNull(rtdw.getMessageCost());
		assertTrue(rtdw.getMessageCost().compareTo(new RelativeTime(50,0))>0);
		assertTrue(rtdw.getMessageCost().compareTo(new RelativeTime(250,0))<0);
		assertTrue(rtdw.getMessageCost().compareTo(new RelativeTime(200,0))==0);		
	}		
	
	@Test
	public void resolution() {
		// setting and getting resolution
		rtdw.setResolution(new RelativeTime(0,10));
		assertNotNull(rtdw.getResolution());
		assertTrue(rtdw.getResolution().compareTo(new RelativeTime(0,5))>0);
		assertTrue(rtdw.getResolution().compareTo(new RelativeTime(0,15))<0);
		assertTrue(rtdw.getResolution().compareTo(new RelativeTime(0,10))==0);		
	}
	
	@Test
	public void sampleSize() {
		// setting and getting sample size
		rtdw.setSampleSize(1000);
		assertEquals(rtdw.getSampleSize(), 1000);
	}
	
}
