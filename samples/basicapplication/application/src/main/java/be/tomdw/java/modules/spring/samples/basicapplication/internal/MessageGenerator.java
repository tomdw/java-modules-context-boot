package be.tomdw.java.modules.spring.samples.basicapplication.internal;

import java.util.List;

import javax.inject.Named;

import org.springframework.beans.factory.InitializingBean;

import be.tomdw.java.modules.context.boot.api.ModuleServiceReference;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.api.SpeakerService;

@Named
public class MessageGenerator implements InitializingBean {

	@ModuleServiceReference
	private SpeakerService speakerService;

	@ModuleServiceReference
	@Named("multipleSpeakerServiceList")
	private List<MultipleSpeakerService> multipleSpeakerServices;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.speakerService.speak("Hello World!");
		this.speakerService.speak("JPMS stands for 'Java Platform Module System'");
		this.speakerService.speak("Let's explore it together.");

		this.multipleSpeakerServices.forEach(multipleSpeakerService -> multipleSpeakerService.speak("Multiple services are supported"));
	}
}
