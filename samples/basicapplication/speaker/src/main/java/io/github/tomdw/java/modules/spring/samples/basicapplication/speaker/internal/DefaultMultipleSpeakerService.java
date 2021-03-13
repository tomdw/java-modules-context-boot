package io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal;

import javax.inject.Named;

import io.github.tomdw.java.modules.context.boot.api.ModuleServiceProvider;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;

@Named
public class DefaultMultipleSpeakerService implements MultipleSpeakerService {

	public static MultipleSpeakerService provider() {
		return ModuleServiceProvider.provide(MultipleSpeakerService.class, "defaultMultipleSpeakerService");
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
