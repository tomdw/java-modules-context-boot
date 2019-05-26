package io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal;

import io.github.tomdw.java.modules.context.boot.api.ModuleServiceProvider;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.NamedSpeakerService;

public class NamedSpeakerServiceProvider {
	public static NamedSpeakerService provider() {
		return ModuleServiceProvider.provide(NamedSpeakerService.class, "otherNamedSpeakerService");
	}
}
