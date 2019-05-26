package io.github.tomdw.java.modules.spring.integration.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;

import io.github.tomdw.java.modules.context.boot.api.ModuleContext;
import io.github.tomdw.java.modules.context.boot.api.ModuleContextBooter;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;

public class JavaModulesSpringContextBootIntegrationTest {

	@BeforeAll
	public static void boot() {
		ModuleContextBooter.main();
	}

	@Test
	public void createsSpringApplicationContextForEveryModuleWithModuleContextAnnotation() {
		List<Module> modulesWithModuleContextAnnotation = ModuleLayer.boot().modules().stream().filter(module -> module.isAnnotationPresent(ModuleContext.class)).collect(Collectors.toList());
		assertThat(modulesWithModuleContextAnnotation.stream().map(Module::getName)).containsExactlyInAnyOrder(
				"io.github.tomdw.java.modules.spring.samples.basicapplication.application",
				"io.github.tomdw.java.modules.spring.samples.basicapplication.speaker",
				"io.github.tomdw.java.modules.spring.integration.tests");

		modulesWithModuleContextAnnotation.forEach(
				module -> {
					GenericApplicationContext context = ModuleContextBooter.getContextFor(module);
					assertThat(context).withFailMessage("A context should have been created for module " + module.getName()).isNotNull();
					assertThat(context.isActive()).withFailMessage("The context should be active for module " + module.getName()).isTrue();
				}
		);
	}

	@Test
	public void singleServiceFromOtherModuleCanBeInjectedUsingModuleServiceReference() {
		IntegrationTestService testConfig = IntegrationTestService.getApplicationContext().getBean(IntegrationTestService.class);
		assertThat(testConfig).isNotNull();

		assertThat(testConfig.getSpeakerService()).withFailMessage("No SpeakerService injected").isNotNull();
		assertThat(testConfig.getSpeakerService().getName()).withFailMessage("Wrong SpeakerService injected").isEqualTo("Default");
	}

	@Test
	public void serviceListFromOtherModuleCanBeInjectedUsingModuleServiceReference() {
		IntegrationTestService testConfig = IntegrationTestService.getApplicationContext().getBean(IntegrationTestService.class);
		assertThat(testConfig).isNotNull();

		assertThat(testConfig.getMultipleSpeakerServices()).withFailMessage("No MultipleSpeakerService(s) injected").isNotNull();
		assertThat(testConfig.getMultipleSpeakerServices().stream().map(MultipleSpeakerService::getName)).containsExactlyInAnyOrder("Default", "Other");
	}

	@Test
	public void singleServiceFromOtherModuleCanBeRetrievedSpecificallyOnBeanName() {
		IntegrationTestService testConfig = IntegrationTestService.getApplicationContext().getBean(IntegrationTestService.class);
		assertThat(testConfig).isNotNull();

		assertThat(testConfig.getNamedSpeakerService().getSpeakerName()).isEqualTo("otherNamedSpeakerServiceName");
	}

}
