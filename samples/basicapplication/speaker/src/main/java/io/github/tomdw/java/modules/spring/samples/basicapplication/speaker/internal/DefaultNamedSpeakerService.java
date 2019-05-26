package io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal;

import javax.inject.Named;

import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.NamedSpeakerService;

@Named("defaultNamedSpeakerService")
public class DefaultNamedSpeakerService implements NamedSpeakerService {

	@Override
	public String getSpeakerName() {
		return "defaultNamedSpeakerServiceName";
	}
}
