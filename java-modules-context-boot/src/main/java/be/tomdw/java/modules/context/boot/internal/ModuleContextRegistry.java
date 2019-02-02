package be.tomdw.java.modules.context.boot.internal;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import be.tomdw.java.modules.context.boot.api.ModuleContext;

public class ModuleContextRegistry {

	private static Map<Module, AnnotationConfigApplicationContext> moduleContexts = new HashMap<>();

	private static AnnotationConfigApplicationContext register(Module module, AnnotationConfigApplicationContext applicationContext) {
		synchronized (moduleContexts) {
			System.out.println("Registering spring application context for module " + module.getName());
			moduleContexts.put(module, applicationContext);
			return applicationContext;
		}
	}

	private static AnnotationConfigApplicationContext get() {
		return get(getCallerModule());
	}

	private static AnnotationConfigApplicationContext get(Module module) {
		if (module.isAnnotationPresent(ModuleContext.class)) {
			AnnotationConfigApplicationContext contextForModule;
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

	public static <SERVICETYPE> SERVICETYPE provide(Class<SERVICETYPE> serviceClass) {
		System.out.println("Providing instance of " + serviceClass.getSimpleName() + " from module " + serviceClass.getModule().getName());
		AnnotationConfigApplicationContext context = get(serviceClass.getModule());
		if (!context.isActive()) {
			System.out.println("Lazy starting ApplicationContext for module " + serviceClass.getModule().getName());
			context.refresh();
		}
		return context.getBean(serviceClass);
	}

	public static void provisionForLayer(ModuleLayer layer) {
		for (Module module : layer.modules()) {
			if (module.isAnnotationPresent(ModuleContext.class)) {
				prepareApplicationContextFor(module);
			}
		}
	}

	public static void boot() {
		synchronized (moduleContexts) {
			for (Map.Entry<Module, AnnotationConfigApplicationContext> contextEntry : moduleContexts.entrySet()) {
				Module module = contextEntry.getKey();
				AnnotationConfigApplicationContext context = contextEntry.getValue();
				if (!context.isActive()) {
					System.out.println("Starting ApplicationContext for Module " + module.getName());
					context.refresh();
				} else {
					System.out.println("ApplicationContext for Module " + module.getName() + " is already active");
				}
			}
		}
	}

	private static AnnotationConfigApplicationContext prepareApplicationContextFor(Module module) {
		ModuleContext moduleContext = module.getAnnotation(ModuleContext.class);
		Class<?> mainConfigurationClass = moduleContext.mainConfigurationClass();
		System.out.println("Preparing ApplicationContext for Module " + module.getName() + " using config class " + mainConfigurationClass.getSimpleName());
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(mainConfigurationClass);
		context.registerBean(ModuleServiceReferenceAnnotationPostProcessor.class);
		return register(module, context);
	}

	private static Module getCallerModule() {
		Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
		return callerClass.getModule();
	}
}
