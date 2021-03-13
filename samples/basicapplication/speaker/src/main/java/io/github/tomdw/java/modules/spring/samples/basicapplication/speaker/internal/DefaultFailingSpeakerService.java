package io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal;

import javax.inject.Named;

import io.github.tomdw.java.modules.context.boot.api.ModuleServiceProvider;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.FailingSpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.NoSpeakerCheckedException;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.NoSpeakerRuntimeException;

@Named
public class DefaultFailingSpeakerService implements FailingSpeakerService {

	public static FailingSpeakerService provider() {
		return provideByInterfaceSoThatADynamicProxyWillBeCreatedWhichShouldNotWrapExceptions();
	}

	private static FailingSpeakerService provideByInterfaceSoThatADynamicProxyWillBeCreatedWhichShouldNotWrapExceptions() {
		return ModuleServiceProvider.provide(FailingSpeakerService.class);
	}

	@Override
	public String getSpeakerNameButThrowsRuntimeException() {
		throw new NoSpeakerRuntimeException();
	}

	@Override
	public String getSpeakerNameButThrowsCheckedException() throws NoSpeakerCheckedException {
		throw new NoSpeakerCheckedException();
	}
}
