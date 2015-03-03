package epc.therest.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import epc.systemone.SystemBuilderForProduction;

@WebListener
public class InitializeListner implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		SystemBuilderForProduction systemBuilderForProduction = new SystemBuilderForProduction();
		systemBuilderForProduction.createAllDependenciesInSystemHolder();
	}
}
