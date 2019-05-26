package io.github.tomdw.java.modules.context.boot.api;

import io.github.tomdw.java.modules.context.boot.internal.ModuleContextRegistry;

public class ModuleServiceProvider {

	public static <SERVICETYPE> SERVICETYPE provide(Class<SERVICETYPE> servicetypeClass) {
		return ModuleContextRegistry.retrieveInstanceFromContext(servicetypeClass);
	}

	public static <SERVICETYPE> SERVICETYPE provide(Class<SERVICETYPE> servicetypeClass, String serviceName) {
		return ModuleContextRegistry.retrieveInstanceFromContext(servicetypeClass, serviceName);
	}
}
