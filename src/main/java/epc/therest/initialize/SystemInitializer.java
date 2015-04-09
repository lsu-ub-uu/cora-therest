package epc.therest.initialize;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import epc.systemone.SystemBuilderForProduction;

@WebListener
public class SystemInitializer implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		SystemBuilderForProduction.createAllDependenciesInSystemHolder();
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// not sure we need anything here
	}
}
