package be.tomdw.java.modules.context.boot.api;

import be.tomdw.java.modules.context.boot.internal.ModuleContextRegistry;

public class ModuleContextBooter {

	public static void boot() {
		System.out.println("Preparing modular spring application");
		ModuleContextRegistry.provisionForLayer(ModuleLayer.boot());
		System.out.println("Booting modular spring application");
		ModuleContextRegistry.boot();
	}

	public static void main(String... commandLineArguments) {
		ModuleContextBooter.boot();
	}

}
