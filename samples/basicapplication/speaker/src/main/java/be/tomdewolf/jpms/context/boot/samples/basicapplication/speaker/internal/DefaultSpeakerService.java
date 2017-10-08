package be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker.internal;

import javax.inject.Named;

import be.tomdewolf.jpms.context.boot.api.ModuleServiceProvider;
import be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker.api.SpeakerService;

@Named
public class DefaultSpeakerService implements SpeakerService {

	public static DefaultSpeakerService provider() {
		return ModuleServiceProvider.provide(DefaultSpeakerService.class);
	}

	@Override
	public void speak(String message) {
		System.out.println("Speaker says: " + message);
	}
}
