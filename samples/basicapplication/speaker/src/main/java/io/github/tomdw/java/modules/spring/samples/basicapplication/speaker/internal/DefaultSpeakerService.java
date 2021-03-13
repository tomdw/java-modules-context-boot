package io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal;

import javax.inject.Named;

import io.github.tomdw.java.modules.context.boot.api.ModuleServiceProvider;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.SpeakerService;

@Named
public class DefaultSpeakerService implements SpeakerService {

	public static SpeakerService provider() {
		return ModuleServiceProvider.provide(SpeakerService.class);
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
