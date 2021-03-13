package io.github.tomdw.java.modules.context.boot.internal;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.INFO;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.support.GenericApplicationContext;

import io.github.tomdw.java.modules.context.boot.api.ModuleContext;

public class ModuleContextRegistry {
	private static final System.Logger LOGGER = System.getLogger(ModuleContextRegistry.class.getName());

	private static final Map<Module, GenericApplicationContext> moduleContexts = new HashMap<>();
	private static GenericApplicationContext defaultApplicationContext;

	private ModuleContextRegistry() {}

	public static void reset() {
		defaultApplicationContext = null;
		moduleContexts.clear();
	}

	private static void register(Module module, GenericApplicationContext applicationContext) {
		synchronized (moduleContexts) {
			LOGGER.log(DEBUG, "Registering spring application context for module " + module.getName());
			if (moduleContexts.containsKey(module)) {
				throw new IllegalStateException("An application context for module " + module.getName() + " was already registered");
			}
			moduleContexts.put(module, applicationContext);
		}
	}

	private static GenericApplicationContext getOrPrepareContextFor(Module module) {
		prepareApplicationContextForModuleIfNeeded(module);
		synchronized (moduleContexts) {
			if (moduleContexts.containsKey(module)) {
				return moduleContexts.get(module);
			} else if (defaultApplicationContext != null) {
				return defaultApplicationContext;
			} else {
				throw new IllegalStateException("No Application context found for module " + module.getName());
			}

		}
	}

	private static void prepareApplicationContextForModuleIfNeeded(Module module) {
		if (ModuleInfoReader.of(module).isAnnotationPresent(ModuleContext.class)) {
			synchronized (moduleContexts) {
				if (!moduleContexts.containsKey(module)) {
					prepareApplicationContextFor(module);
				}
			}
		}
	}

	public static <SERVICETYPE> SERVICETYPE retrieveInstanceFromContext(Class<SERVICETYPE> serviceClass) {
		Module moduleToRetrieveFrom = getCallerModule();
		GenericApplicationContext context = getOrPrepareContextFor(moduleToRetrieveFrom);
		return LazyRetrieveBeanFromContextStrategy
				.withoutServiceName(moduleToRetrieveFrom, defaultApplicationContext, context, serviceClass)
				.retrieveInstanceFromContext();
	}

	public static <SERVICETYPE> SERVICETYPE retrieveInstanceFromContext(Class<SERVICETYPE> serviceClass, String serviceName) {
		Module moduleToRetrieveFrom = getCallerModule();
		GenericApplicationContext context = getOrPrepareContextFor(moduleToRetrieveFrom);
		return LazyRetrieveBeanFromContextStrategy
				.withServiceName(moduleToRetrieveFrom, defaultApplicationContext, context, serviceClass, serviceName)
				.retrieveInstanceFromContext();
	}

	private static void provisionForLayer(ModuleLayer layer) {
		LOGGER.log(INFO, "Preparing modular spring application");
		for (Module module : layer.modules()) {
			if (ModuleInfoReader.of(module).isAnnotationPresent(ModuleContext.class)) {
				prepareApplicationContextForModuleIfNeeded(module);
			}
		}
	}

	public static void boot(ModuleLayer layer) {
		provisionForLayer(layer);
		LOGGER.log(INFO, "Booting modular spring application");
		synchronized (moduleContexts) {
			for (Map.Entry<Module, GenericApplicationContext> contextEntry : moduleContexts.entrySet()) {
				Module module = contextEntry.getKey();
				GenericApplicationContext context = contextEntry.getValue();
				if (!context.isActive()) {
					LOGGER.log(INFO, "Starting ApplicationContext for Module " + module.getName());
					context.refresh();
				} else {
					LOGGER.log(INFO, "ApplicationContext for Module " + module.getName() + " is already active");
				}
			}
		}
	}

	public static void boot() {
		boot(ModuleLayer.boot());
	}

	public static void boot(GenericApplicationContext defaultApplicationContext) {
		ModuleContextRegistry.defaultApplicationContext = defaultApplicationContext;
		enhanceApplicationContext(defaultApplicationContext);
		boot();
	}

	private static void prepareApplicationContextFor(Module module) {
		ModuleContext moduleContext = ModuleInfoReader.of(module).getAnnotation(ModuleContext.class);
		Class<?> mainConfigurationClass = moduleContext.mainConfigurationClass();
		LOGGER.log(INFO, "Preparing ApplicationContext for Module " + module.getName() + " using config class " + mainConfigurationClass.getSimpleName());
		GenericApplicationContext context = instantiateApplicationContext(moduleContext);
		context.setId("module-context-" + module.getName());
		AnnotationConfigRegistry annotationConfigRegistry = asAnnotationConfigRegistry(context);
		annotationConfigRegistry.register(mainConfigurationClass);
		enhanceApplicationContext(context);
		register(module, context);
	}

	private static void enhanceApplicationContext(GenericApplicationContext context) {
		AnnotationConfigRegistry annotationConfigRegistry = asAnnotationConfigRegistry(context);
		annotationConfigRegistry.register(ModuleServiceReferenceAnnotationPostProcessor.class);
		if (defaultApplicationContext != null && context != defaultApplicationContext) {
			context.setEnvironment(defaultApplicationContext.getEnvironment());
		}
	}

	private static GenericApplicationContext instantiateApplicationContext(ModuleContext moduleContext) {
		try {
			return moduleContext.applicationContextClass().getDeclaredConstructor().newInstance();
		} catch (InstantiationException e) {
			LOGGER.log(System.Logger.Level.ERROR, "Cannot instantiate " + moduleContext.applicationContextClass());
			throw new ApplicationContextInstantiationException(e);
		} catch (IllegalAccessException e) {
			LOGGER.log(System.Logger.Level.ERROR, "No access to default constructor of " + moduleContext.applicationContextClass());
			throw new ApplicationContextInstantiationException(e);
		} catch (InvocationTargetException e) {
			LOGGER.log(System.Logger.Level.ERROR, "Cannot invoke default constructor of " + moduleContext.applicationContextClass());
			throw new ApplicationContextInstantiationException(e);
		} catch (NoSuchMethodException e) {
			LOGGER.log(System.Logger.Level.ERROR, "No Default constructor for " + moduleContext.applicationContextClass());
			throw new ApplicationContextInstantiationException(e);
		}
	}

	private static AnnotationConfigRegistry asAnnotationConfigRegistry(GenericApplicationContext context) {
		try {
			return (AnnotationConfigRegistry) context;
		} catch (ClassCastException e) {
			LOGGER.log(System.Logger.Level.ERROR, "The provided Application Context type " + context.getClass() + " should implement org.springframework.context.annotation.AnnotationConfigRegistry");
			throw new ApplicationContextInstantiationException(e);
		}
	}

	private static Module getCallerModule() {
		return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
				.walk(stackFrameStream -> stackFrameStream
						.map(StackWalker.StackFrame::getDeclaringClass)
						.map(Class::getModule)
						.filter(module -> module != ModuleContextRegistry.class.getModule())
						.findFirst()
						.orElseThrow(() -> new UnsupportedOperationException("Can only get an application context for a Module annotated with @ModuleContext")));
	}

	public static GenericApplicationContext getContextFor(Module module) {
		return getOrPrepareContextFor(module);
	}
}