package io.github.tomdw.java.modules.spring.integration.tests;

import java.util.List;

import javax.inject.Named;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.github.tomdw.java.modules.context.boot.api.ModuleServiceReference;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerWithGenericsService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.NamedSpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.SpeakerService;

@Named
public class IntegrationTestUsingConstructorInjectionService implements ApplicationContextAware {

	private final SpeakerService speakerService;

	private final List<MultipleSpeakerService> multipleSpeakerServices;

	private final NamedSpeakerService namedSpeakerService;

	private final List<MultipleSpeakerWithGenericsService<?>> multipleSpeakerWithGenericsServices;

	private static ApplicationContext applicationContext;

	public IntegrationTestUsingConstructorInjectionService(
			@ModuleServiceReference SpeakerService speakerService,
			@ModuleServiceReference @Named("multipleSpeakerServiceList") List<MultipleSpeakerService> multipleSpeakerServices,
			@ModuleServiceReference NamedSpeakerService namedSpeakerService,
			@ModuleServiceReference @Named("multipleSpeakerWithGenericsServiceList") List<MultipleSpeakerWithGenericsService<?>> multipleSpeakerWithGenericsServices) {
		this.speakerService = speakerService;
		this.multipleSpeakerServices = multipleSpeakerServices;
		this.namedSpeakerService = namedSpeakerService;
		this.multipleSpeakerWithGenericsServices = multipleSpeakerWithGenericsServices;
	}

	public List<MultipleSpeakerService> getMultipleSpeakerServices() {
		return multipleSpeakerServices;
	}

	public NamedSpeakerService getNamedSpeakerService() {
		return namedSpeakerService;
	}

	public List<MultipleSpeakerWithGenericsService<?>> getMultipleSpeakerWithGenericsServices() { return multipleSpeakerWithGenericsServices; }

	public SpeakerService getSpeakerService() {
		return speakerService;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
