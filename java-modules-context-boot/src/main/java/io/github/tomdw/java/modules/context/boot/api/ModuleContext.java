package io.github.tomdw.java.modules.context.boot.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.MODULE)
@Documented
public @interface ModuleContext {

	Class<?> mainConfigurationClass() default NoConfiguration.class;

	Class<? extends GenericApplicationContext> applicationContextClass() default AnnotationConfigApplicationContext.class;

	@Configuration
	class NoConfiguration {
	}
}
