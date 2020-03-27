package io.github.tomdw.java.modules.context.boot.starter;

import org.springframework.beans.factory.InitializingBean;

import io.github.tomdw.java.modules.context.boot.api.ModuleContextBooter;

public class ModuleContextStarter implements InitializingBean {

	private static final System.Logger LOGGER = System.getLogger(ModuleContextStarter.class.getName());

	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.log(System.Logger.Level.INFO, "Spring Boot Starter for Java Module Context activated ...");
		ModuleContextBooter.boot();
		LOGGER.log(System.Logger.Level.INFO, "Spring Boot Starter for Java Module Context finished.");
	}
}
