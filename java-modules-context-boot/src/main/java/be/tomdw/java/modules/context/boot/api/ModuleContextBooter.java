package be.tomdw.java.modules.context.boot.api;

import be.tomdw.java.modules.context.boot.internal.ModuleContextRegistry;

public class ModuleContextBooter {

	public static void boot() {
		ModuleContextRegistry.provisionForBootLayer();
		ModuleContextRegistry.boot();
	}

	public static void main(String... commandLineArguments) {
		ModuleContextBooter.boot();
	}

}
