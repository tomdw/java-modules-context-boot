package io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal;

import javax.inject.Named;

import io.github.tomdw.java.modules.context.boot.api.ModuleServiceProvider;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;

@Named
public class OtherMultipleSpeakerService implements MultipleSpeakerService {

	public static OtherMultipleSpeakerService provider() {
		return ModuleServiceProvider.provide(OtherMultipleSpeakerService.class);
	}

	@Override
	public String getName() {
		return "Other";
	}

	@Override
	public void speak(String message) {
		System.out.println("MULTIPLE OTHER - " + message);
	}
}
