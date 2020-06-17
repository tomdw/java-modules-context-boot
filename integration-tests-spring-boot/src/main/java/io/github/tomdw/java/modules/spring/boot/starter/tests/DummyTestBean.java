package io.github.tomdw.java.modules.spring.boot.starter.tests;

import org.springframework.stereotype.Component;

import io.github.tomdw.java.modules.context.boot.api.ModuleServiceReference;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.SpeakerService;

@Component
public class DummyTestBean {

	@ModuleServiceReference
	private SpeakerService speakerService;

	public SpeakerService getSpeakerService() {
		return speakerService;
	}
}
