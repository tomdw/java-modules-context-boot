package io.github.tomdw.java.modules.context.boot.internal;

import org.springframework.context.annotation.Bean;

public class ModuleServiceReferenceAnnotationPostProcessorConfiguration {

	@Bean
	public ModuleServiceReferenceAnnotationPostProcessor moduleServiceReferenceAnnotationPostProcessor() {
		return new ModuleServiceReferenceAnnotationPostProcessor();
	}
}
