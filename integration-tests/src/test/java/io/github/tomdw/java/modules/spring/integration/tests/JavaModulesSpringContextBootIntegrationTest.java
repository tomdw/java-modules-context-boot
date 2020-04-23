package io.github.tomdw.java.modules.spring.integration.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;

import io.github.tomdw.java.modules.context.boot.api.ModuleContextBooter;
import io.github.tomdw.java.modules.context.boot.api.ModuleServiceProvider;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.SpeakerService;

public class JavaModulesSpringContextBootIntegrationTest {

	@AfterEach
	public void reset() {
		ModuleContextBooter.reset();
	}

	@Test
	public void createsSpringApplicationContextForEveryModuleWithModuleContextAnnotation() {
		ModuleContextBooter.main();

		String[] expectedModuleNames = {"io.github.tomdw.java.modules.spring.samples.basicapplication.application",
				"io.github.tomdw.java.modules.spring.samples.basicapplication.speaker",
				"io.github.tomdw.java.modules.spring.integration.tests"};
		List<Module> modulesWithModuleContextAnnotation = ModuleLayer.boot().modules().stream().filter(module -> List.of(expectedModuleNames).contains(module.getName())).collect(Collectors.toList());

		assertThat(modulesWithModuleContextAnnotation.stream().map(Module::getName)).containsExactlyInAnyOrder(expectedModuleNames);

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
		ModuleContextBooter.main();

		IntegrationTestService testConfig = IntegrationTestService.getApplicationContext().getBean(IntegrationTestService.class);
		assertThat(testConfig).isNotNull();

		assertThat(testConfig.getSpeakerService()).withFailMessage("No SpeakerService injected").isNotNull();
		assertThat(testConfig.getSpeakerService().getName()).withFailMessage("Wrong SpeakerService injected").isEqualTo("Default");
	}

	@Test
	public void serviceListFromOtherModuleCanBeInjectedUsingModuleServiceReference() {
		ModuleContextBooter.main();

		IntegrationTestService testConfig = IntegrationTestService.getApplicationContext().getBean(IntegrationTestService.class);
		assertThat(testConfig).isNotNull();

		assertThat(testConfig.getMultipleSpeakerServices()).withFailMessage("No MultipleSpeakerService(s) injected").isNotNull();
		assertThat(testConfig.getMultipleSpeakerServices().stream().map(MultipleSpeakerService::getName)).containsExactlyInAnyOrder("Default", "Other");
	}

	@Test
	public void singleServiceFromOtherModuleCanBeRetrievedSpecificallyOnBeanName() {
		ModuleContextBooter.main();

		IntegrationTestService testConfig = IntegrationTestService.getApplicationContext().getBean(IntegrationTestService.class);
		assertThat(testConfig).isNotNull();

		assertThat(testConfig.getNamedSpeakerService().getSpeakerName()).isEqualTo("otherNamedSpeakerServiceName");
	}

	@Test
	public void serviceListFromOtherModuleWithGenericsCanBeInjectedUsingModuleServiceReference() {
		ModuleContextBooter.main();

		IntegrationTestService testConfig = IntegrationTestService.getApplicationContext().getBean(IntegrationTestService.class);
		assertThat(testConfig).isNotNull();

		assertThat(testConfig.getMultipleSpeakerWithGenericsServices()).withFailMessage("No MultipleSpeakerWithGenericsService(s) injected").isNotNull();
		assertThat(testConfig.getMultipleSpeakerWithGenericsServices().get(0).getMultipleSpeakerName()).asString().isEqualTo("myMultipleGenericSpeakerName");
	}


	@Test
	public void usingModuleServiceProviderBeforeExplicitBootIsSupported() {
		SpeakerService singleSpeakerService = ModuleServiceProvider.provide(SpeakerService.class);
		assertThat(singleSpeakerService).isNotNull();

		ModuleContextBooter.boot();

		GenericApplicationContext contextForModule = ModuleContextBooter.getContextFor(SpeakerService.class.getModule());
		assertThat(contextForModule).isNotNull();
	}

	@Test
	public void bootingModuleContextBooterMultipleTimesIsNotAProblem() {
		ModuleContextBooter.boot();
		ModuleContextBooter.boot();

		GenericApplicationContext contextForModule = ModuleContextBooter.getContextFor(SpeakerService.class.getModule());
		assertThat(contextForModule).isNotNull();
	}


}
