package io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal;

import io.github.tomdw.java.modules.context.boot.api.ModuleServiceProvider;

public class MultipleSpeakerWithGenericsServiceProvider {

	public static DefaultMultipleSpeakerWithGenericsService provider() {
		return ModuleServiceProvider.provide(DefaultMultipleSpeakerWithGenericsService.class);
	}

}
