package io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal;

import javax.inject.Named;

import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.NamedSpeakerService;

@Named("otherNamedSpeakerService")
public class OtherNamedSpeakerService implements NamedSpeakerService {

	@Override
	public String getSpeakerName() {
		return "otherNamedSpeakerServiceName";
	}
}
