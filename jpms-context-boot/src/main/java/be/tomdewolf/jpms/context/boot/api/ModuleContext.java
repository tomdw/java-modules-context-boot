package be.tomdewolf.jpms.context.boot.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.MODULE)
@Documented
public @interface ModuleContext {

	Class<?> mainConfigurationClass() default NoConfiguration.class;

	@Configuration
	class NoConfiguration {
	}
}