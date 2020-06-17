package io.github.tomdw.java.modules.spring.integration.tests;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {IntegrationTestUsingConstructorInjectionService.class, IntegrationTestService.class})
public class IntegrationTestConfiguration {

}
