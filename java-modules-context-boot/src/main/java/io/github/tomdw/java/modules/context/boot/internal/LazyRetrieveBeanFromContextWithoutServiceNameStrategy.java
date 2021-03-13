package io.github.tomdw.java.modules.context.boot.internal;

import org.springframework.context.support.GenericApplicationContext;

class LazyRetrieveBeanFromContextWithoutServiceNameStrategy<SERVICETYPE> extends LazyRetrieveBeanFromContextStrategy<SERVICETYPE> {
	LazyRetrieveBeanFromContextWithoutServiceNameStrategy(Module moduleToRetrieveFrom, GenericApplicationContext defaultApplicationContext, GenericApplicationContext context, Class<SERVICETYPE> serviceClass) {
		super(moduleToRetrieveFrom, defaultApplicationContext, context, serviceClass);
	}

	@Override
	protected SERVICETYPE getBeanFromContext() {
		return context.getBean(serviceClass);
	}

	@Override
	protected String logMessageDescribingInstanceToProvide() {
		return "instance of " + serviceClass.getSimpleName();
	}

}
