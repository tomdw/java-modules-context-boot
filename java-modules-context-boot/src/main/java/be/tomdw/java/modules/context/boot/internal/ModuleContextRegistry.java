package be.tomdw.java.modules.context.boot.internal;

import static java.lang.System.Logger.Level.INFO;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.support.GenericApplicationContext;

import be.tomdw.java.modules.context.boot.api.ModuleContext;

public class ModuleContextRegistry {
	private static final System.Logger LOGGER = System.getLogger(ModuleContextRegistry.class.getName());

	private static final Map<Module, GenericApplicationContext> moduleContexts = new HashMap<>();

	private ModuleContextRegistry() {}

	private static GenericApplicationContext register(Module module, GenericApplicationContext applicationContext) {
		synchronized (moduleContexts) {
			LOGGER.log(INFO, "Registering spring application context for module " + module.getName());
			moduleContexts.put(module, applicationContext);
			return applicationContext;
		}
	}

	private static GenericApplicationContext get(Module module) {
		if (module.isAnnotationPresent(ModuleContext.class)) {
			GenericApplicationContext contextForModule;
			synchronized (moduleContexts) {
				contextForModule = moduleContexts.get(module);
			}
			if (contextForModule == null) {
				contextForModule = prepareApplicationContextFor(module);
			}
			return contextForModule;
		} else {
			throw new UnsupportedOperationException("Can only get an application context for a Module annotated with @ModuleContext");
		}
	}

	private static GenericApplicationContext get() {
		return get(getCallerModule());
	}

	public static <SERVICETYPE> SERVICETYPE retrieveInstanceFromContext(Class<SERVICETYPE> serviceClass) {
		LOGGER.log(INFO, "Providing instance of " + serviceClass.getSimpleName() + " from module " + serviceClass.getModule().getName());
		GenericApplicationContext context = get();
		if (!context.isActive()) {
			LOGGER.log(INFO, "Lazy starting ApplicationContext for module " + serviceClass.getModule().getName());
			context.refresh();
		}
		return context.getBean(serviceClass);
	}

	public static void provisionForBootLayer() {
		provisionForLayer(ModuleLayer.boot());
	}

	public static void provisionForLayer(ModuleLayer layer) {
		LOGGER.log(INFO, "Preparing modular spring application");
		for (Module module : layer.modules()) {
			if (module.isAnnotationPresent(ModuleContext.class)) {
				prepareApplicationContextFor(module);
			}
		}
	}

	public static void boot() {
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

	private static GenericApplicationContext prepareApplicationContextFor(Module module) {
		ModuleContext moduleContext = module.getAnnotation(ModuleContext.class);
		Class<?> mainConfigurationClass = moduleContext.mainConfigurationClass();
		LOGGER.log(INFO, "Preparing ApplicationContext for Module " + module.getName() + " using config class " + mainConfigurationClass.getSimpleName());
		GenericApplicationContext context = instantiateApplicationContext(moduleContext);
		AnnotationConfigRegistry annotationConfigRegistry = asAnnotationConfigRegistry(context);
		annotationConfigRegistry.register(mainConfigurationClass);
		annotationConfigRegistry.register(ModuleServiceReferenceAnnotationPostProcessorConfiguration.class);
		return register(module, context);
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
		return moduleContexts.get(module);
	}
}