package io.github.tomdw.java.modules.context.boot.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JavaModulesContextBootAutoConfiguration {

	@Bean
	public ModuleContextStarter moduleContextStarter() {
		return new ModuleContextStarter();
	}

}
