package be.tomdw.java.modules.spring.samples.basicapplication.speaker.internal;

import javax.inject.Named;

import be.tomdw.java.modules.context.boot.api.ModuleServiceProvider;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.api.SpeakerService;

@Named
public class DefaultSpeakerService implements SpeakerService {

	public static DefaultSpeakerService provider() {
		return ModuleServiceProvider.provide(DefaultSpeakerService.class);
	}

	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public void speak(String message) {
		System.out.println("Speaker says: " + message);
	}
}
