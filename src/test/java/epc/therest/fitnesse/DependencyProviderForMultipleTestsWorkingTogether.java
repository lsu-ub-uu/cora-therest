package epc.therest.fitnesse;

import epc.spider.dependency.SpiderDependencyProvider;
import epc.systemone.SystemOneDependencyProvider;

public abstract class DependencyProviderForMultipleTestsWorkingTogether {
	public static SpiderDependencyProvider spiderDependencyProvider = new SystemOneDependencyProvider();
}
