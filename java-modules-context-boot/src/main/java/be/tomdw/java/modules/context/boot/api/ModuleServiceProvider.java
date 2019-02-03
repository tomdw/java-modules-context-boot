package be.tomdw.java.modules.context.boot.api;

import be.tomdw.java.modules.context.boot.internal.ModuleContextRegistry;

public class ModuleServiceProvider {

	public static <SERVICETYPE> SERVICETYPE provide(Class<SERVICETYPE> servicetypeClass) {
		return ModuleContextRegistry.retrieveInstanceFromContext(servicetypeClass);
	}
}
