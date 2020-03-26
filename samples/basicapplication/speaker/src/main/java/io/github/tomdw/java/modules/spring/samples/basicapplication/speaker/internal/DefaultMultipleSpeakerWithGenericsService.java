package io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal;

import javax.inject.Named;

import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerWithGenericsService;

@Named
public class DefaultMultipleSpeakerWithGenericsService implements MultipleSpeakerWithGenericsService<String> {

	@Override
	public String getMultipleSpeakerName() {
		return "myMultipleGenericSpeakerName";
	}
}

