package be.tomdw.java.modules.spring.integration.tests;

import java.util.List;

import javax.inject.Named;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import be.tomdw.java.modules.context.boot.api.ModuleServiceReference;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.api.SpeakerService;

@Named
public class IntegrationTestConfiguration implements ApplicationContextAware {

	@ModuleServiceReference
	private SpeakerService speakerService;

	@ModuleServiceReference
	@Named("multipleSpeakerServiceList")
	private List<MultipleSpeakerService> multipleSpeakerServices;

	private static ApplicationContext applicationContext;

	public List<MultipleSpeakerService> getMultipleSpeakerServices() {
		return multipleSpeakerServices;
	}

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
