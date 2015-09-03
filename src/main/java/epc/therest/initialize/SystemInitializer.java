package epc.therest.initialize;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import epc.spider.dependency.SpiderDependencyProvider;
import epc.spider.dependency.SpiderInstanceProvider;
import epc.systemone.SystemOneDependencyProvider;

@WebListener
public class SystemInitializer implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		SpiderDependencyProvider dependencyProvider = new SystemOneDependencyProvider();
		SpiderInstanceProvider.setSpiderDependencyProvider(dependencyProvider);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// not sure we need anything here
	}
}
