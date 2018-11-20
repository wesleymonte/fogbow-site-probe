package br.edu.ufcg.lsd.core;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import br.edu.ufcg.lsd.core.utils.ProbeConstants;

public class ProbeControllerTest {
	
	// test case: Success case. Configuring by properties
	@Test
	public void testGetSchedulerPeriod() {
		// set up
		ProbeController probeController = new ProbeController();
		Properties properties = new Properties();
		long schedulerPeriodExpected = 1234;
		properties.put(ProbeConstants.Properties.SCHEDULER_PERIOD, String.valueOf(schedulerPeriodExpected));
		probeController.setProperties(properties);
		
		// exercise
		long schedulerPeriod = probeController.getSchedulerPeriod();
		
		// verify
		Assert.assertEquals(schedulerPeriodExpected, schedulerPeriod);
	}
	
	// test case: Success case. Properties is null, so the default value will be seted
	@Test
	public void testGetSchedulerPeriodDefaultValue() {
		// set up
		ProbeController probeController = new ProbeController();
		Properties properties = new Properties();
		probeController.setProperties(properties);
		
		// exercise
		long schedulerPeriod = probeController.getSchedulerPeriod();
		
		// verify
		Assert.assertEquals(ProbeController.SCHEDULER_TIME_DEFAULT, schedulerPeriod);
	}	
	
	// test case: RAScomponent throw exception with properties empty
	@Test(expected=Exception.class)
	public void testConfigureComponent() throws Exception {
		// set up
		ProbeController probeController = new ProbeController();
		
		// exercise
		probeController.configureComponent(new Properties());
	}
	
}
