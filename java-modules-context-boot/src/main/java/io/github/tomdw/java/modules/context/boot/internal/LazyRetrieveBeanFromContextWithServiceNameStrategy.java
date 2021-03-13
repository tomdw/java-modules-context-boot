package io.github.tomdw.java.modules.context.boot.internal;

import org.springframework.context.support.GenericApplicationContext;

public class LazyRetrieveBeanFromContextWithServiceNameStrategy<SERVICETYPE> extends LazyRetrieveBeanFromContextStrategy<SERVICETYPE> {

	private final String serviceName;

	public LazyRetrieveBeanFromContextWithServiceNameStrategy(Module moduleToRetrieveFrom, GenericApplicationContext defaultApplicationContext, GenericApplicationContext context, Class<SERVICETYPE> serviceClass, String serviceName) {
		super(moduleToRetrieveFrom, defaultApplicationContext, context, serviceClass);
		this.serviceName = serviceName;
	}

	@Override
	protected SERVICETYPE getBeanFromContext() {
		return context.getBean(serviceName, serviceClass);
	}

	@Override
	protected String logMessageDescribingInstanceToProvide() {
		return "instance of " + serviceClass.getSimpleName() + " with name '" + serviceName + "'";
	}
}
