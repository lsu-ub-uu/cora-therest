package se.uu.ub.cora.therest.fitnesse;

import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.systemone.SystemOneDependencyProvider;

public abstract class DependencyProviderForMultipleTestsWorkingTogether {
	public static SpiderDependencyProvider spiderDependencyProvider = new SystemOneDependencyProvider();
}
