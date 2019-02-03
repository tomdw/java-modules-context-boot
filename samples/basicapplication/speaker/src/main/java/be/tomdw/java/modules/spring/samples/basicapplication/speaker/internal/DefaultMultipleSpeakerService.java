package be.tomdw.java.modules.spring.samples.basicapplication.speaker.internal;

import javax.inject.Named;

import be.tomdw.java.modules.context.boot.api.ModuleServiceProvider;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;

@Named
public class DefaultMultipleSpeakerService implements MultipleSpeakerService {

	public static DefaultMultipleSpeakerService provider() {
		return ModuleServiceProvider.provide(DefaultMultipleSpeakerService.class);
	}

	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public void speak(String message) {
		System.out.println("MULTIPLE - " + message);
	}
}
