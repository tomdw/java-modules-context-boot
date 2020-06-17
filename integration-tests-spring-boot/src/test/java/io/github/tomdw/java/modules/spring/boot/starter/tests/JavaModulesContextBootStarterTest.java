package io.github.tomdw.java.modules.spring.boot.starter.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import io.github.tomdw.java.modules.context.boot.api.ModuleContextBooter;
import io.github.tomdw.java.modules.context.boot.api.ModuleServiceProvider;

@SpringBootTest(classes = SpringBootTestApplication.class)
public class JavaModulesContextBootStarterTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void springBootApplicationContextIsStartedAndDetectsOwnBeans() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.getBean(DummyTestBean.class)).isNotNull();
	}

	@Test
	public void springBootStarterAutoConfiguresSpringApplicationContextForEveryModuleWithModuleContextAnnotation() {
		String[] expectedModuleNames = {"io.github.tomdw.java.modules.spring.samples.basicapplication.application",
				"io.github.tomdw.java.modules.spring.samples.basicapplication.speaker"};
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
	public void moduleServiceProviderLooksForBeansInMainSpringBootContextWhenCallingModuleHasNoModuleContextAnnotation() {
		DummyTestBean providedBean = ModuleServiceProvider.provide(DummyTestBean.class);
		assertThat(providedBean).isNotNull();

		GenericApplicationContext contextForMainSpringBootModules = ModuleContextBooter.getContextFor(DummyTestBean.class.getModule());
		assertThat(contextForMainSpringBootModules).isSameAs(applicationContext);
	}

	@Test
	public void moduleServiceReferenceAnnotationAlsoInjectsServicesLoadedFromOtherModulesInMainSpringBootContext() {
		DummyTestBean dummyTestBean = applicationContext.getBean(DummyTestBean.class);
		assertThat(dummyTestBean).isNotNull();
		assertThat(dummyTestBean.getSpeakerService()).isNotNull();
	}


}
