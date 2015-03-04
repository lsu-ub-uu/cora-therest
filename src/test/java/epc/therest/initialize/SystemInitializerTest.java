package epc.therest.initialize;

import org.testng.annotations.Test;

public class SystemInitializerTest {
	@Test
	public void testInitializeSystem() {
		SystemInitializer systemInitializer = new SystemInitializer();
		systemInitializer.contextInitialized(null);
//		Assert.assertNotNull(object);
		//TODO: figure out what to test
	}
	@Test
	public void testDestroySystem(){
		SystemInitializer systemInitializer = new SystemInitializer();
		systemInitializer.contextDestroyed(null);
		//TODO: should we do something on destroy?
	}
}
