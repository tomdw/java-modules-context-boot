package io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api;

public interface FailingSpeakerService {

	String getSpeakerNameButThrowsRuntimeException();

	String getSpeakerNameButThrowsCheckedException() throws NoSpeakerCheckedException;
}
