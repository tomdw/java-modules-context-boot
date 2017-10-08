package be.tomdewolf.jpms.context.boot.api;

import be.tomdewolf.jpms.context.boot.internal.ModuleContextRegistry;

public class ModuleServiceProvider {

	public static <SERVICETYPE> SERVICETYPE provide(Class<SERVICETYPE> servicetypeClass) {
		return ModuleContextRegistry.provide(servicetypeClass);
	}
}
