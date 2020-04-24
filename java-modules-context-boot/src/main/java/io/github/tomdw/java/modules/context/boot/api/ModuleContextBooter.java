package io.github.tomdw.java.modules.context.boot.api;

import org.springframework.context.support.GenericApplicationContext;

import io.github.tomdw.java.modules.context.boot.internal.ModuleContextRegistry;

public class ModuleContextBooter {

	public static void boot() {
		ModuleContextRegistry.boot();
	}

	public static void boot(GenericApplicationContext defaultApplicationContext) {
		ModuleContextRegistry.boot(defaultApplicationContext);
	}

	public static void reset() {
		ModuleContextRegistry.reset();
	}

	public static GenericApplicationContext getContextFor(Module module) {
		return ModuleContextRegistry.getContextFor(module);
	}

	public static void main(String... commandLineArguments) {
		ModuleContextBooter.boot();
	}

}
