package be.tomdw.java.modules.context.boot.api;

import org.springframework.context.support.GenericApplicationContext;

import be.tomdw.java.modules.context.boot.internal.ModuleContextRegistry;

public class ModuleContextBooter {

	public static void boot() {
		ModuleContextRegistry.provisionForBootLayer();
		ModuleContextRegistry.boot();
	}

	public static GenericApplicationContext getContextFor(Module module) {
		return ModuleContextRegistry.getContextFor(module);
	}

	public static void main(String... commandLineArguments) {
		ModuleContextBooter.boot();
	}

}
