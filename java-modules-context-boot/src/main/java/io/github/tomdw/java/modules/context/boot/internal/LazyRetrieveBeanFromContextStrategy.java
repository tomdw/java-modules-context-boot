package io.github.tomdw.java.modules.context.boot.internal;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.context.support.GenericApplicationContext;

abstract class LazyRetrieveBeanFromContextStrategy<SERVICETYPE> implements InvocationHandler {
	private static final System.Logger LOGGER = System.getLogger(LazyRetrieveBeanFromContextStrategy.class.getName());

	protected final Module moduleToRetrieveFrom;
	protected final GenericApplicationContext defaultApplicationContext;
	protected final GenericApplicationContext context;
	protected final Class<SERVICETYPE> serviceClass;

	protected LazyRetrieveBeanFromContextStrategy(Module moduleToRetrieveFrom, GenericApplicationContext defaultApplicationContext, GenericApplicationContext context, Class<SERVICETYPE> serviceClass) {
		this.moduleToRetrieveFrom = moduleToRetrieveFrom;
		this.defaultApplicationContext = defaultApplicationContext;
		this.context = context;
		this.serviceClass = serviceClass;
	}

	static <SERVICECLASS> LazyRetrieveBeanFromContextStrategy<SERVICECLASS> withoutServiceName(Module moduleToRetrieveFrom, GenericApplicationContext defaultApplicationContext, GenericApplicationContext context, Class<SERVICECLASS> serviceClass) {
		return new LazyRetrieveBeanFromContextWithoutServiceNameStrategy<>(moduleToRetrieveFrom, defaultApplicationContext, context, serviceClass);
	}

	static <SERVICECLASS> LazyRetrieveBeanFromContextStrategy<SERVICECLASS> withServiceName(Module moduleToRetrieveFrom, GenericApplicationContext defaultApplicationContext, GenericApplicationContext context, Class<SERVICECLASS> serviceClass, String serviceName) {
		return new LazyRetrieveBeanFromContextWithServiceNameStrategy<>(moduleToRetrieveFrom, defaultApplicationContext, context, serviceClass, serviceName);
	}

	SERVICETYPE retrieveInstanceFromContext() {
		if (serviceClass.isInterface()) {
			LOGGER.log(DEBUG, "Providing dynamic proxy for " + logMessageDescribingInstanceToProvide() + " from module " + moduleToRetrieveFrom.getName());
			return createDynamicProxyToPostponeContextLoadingUntilInvocationOfService();
		} else {
			LOGGER.log(WARNING, "Bean was provided as implementation, better to provide interfaces so dynamic proxies are created: " + serviceClass);
			lazyStartApplicationContextForModule();
			LOGGER.log(DEBUG, "Providing " + logMessageDescribingInstanceToProvide() + " from module " + moduleToRetrieveFrom.getName());
			return getBeanFromContext();
		}
	}

	@SuppressWarnings("unchecked")
	private SERVICETYPE createDynamicProxyToPostponeContextLoadingUntilInvocationOfService() {
		return (SERVICETYPE) Proxy.newProxyInstance(
				moduleToRetrieveFrom.getClassLoader(),
				new Class[] { serviceClass },
				this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		lazyStartApplicationContextForModule();
		LOGGER.log(DEBUG, "Resolving dynamic proxy for " + logMessageDescribingInstanceToProvide() + " from ApplicationContext for module " + moduleToRetrieveFrom.getName());
		try {
			return method.invoke(getBeanFromContext(), args);
		} catch (InvocationTargetException ite) {
			throw ite.getTargetException();
		}
	}

	private void lazyStartApplicationContextForModule() {
		if (!context.isActive()) {
			if (context.equals(defaultApplicationContext)) {
				throw new InactiveDefaultApplicationContextException(moduleToRetrieveFrom, serviceClass);
			}
			LOGGER.log(INFO, "Lazy starting ApplicationContext for module " + moduleToRetrieveFrom.getName());
			context.refresh();
		}
	}

	protected abstract SERVICETYPE getBeanFromContext();

	protected abstract String logMessageDescribingInstanceToProvide();

}