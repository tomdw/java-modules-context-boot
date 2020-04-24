package io.github.tomdw.java.modules.context.boot.starter;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.github.tomdw.java.modules.context.boot.api.ModuleContextBooter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JavaModulesContextBootApplicationContextInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private static final System.Logger LOGGER = System.getLogger(JavaModulesContextBootApplicationContextInitializer.class.getName());

	@Override
	public void initialize(GenericApplicationContext applicationContext) {
		LOGGER.log(System.Logger.Level.INFO, "Spring Boot Starter for Java Module Context activated ...");
		ModuleContextBooter.boot(applicationContext);
		LOGGER.log(System.Logger.Level.INFO, "Spring Boot Starter for Java Module Context finished.");
	}
}

