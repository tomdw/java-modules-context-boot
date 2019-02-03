import be.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.api.SpeakerService;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.internal.DefaultMultipleSpeakerService;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.internal.OtherMultipleSpeakerService;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.internal.SpeakerConfiguration;
import be.tomdw.java.modules.context.boot.api.ModuleContext;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.internal.DefaultSpeakerService;

@ModuleContext(mainConfigurationClass = SpeakerConfiguration.class)
module be.tomdw.java.modules.spring.samples.basicapplication.speaker {
	exports be.tomdw.java.modules.spring.samples.basicapplication.speaker.api;
	requires be.tomdw.java.modules.context.boot;

	provides SpeakerService with DefaultSpeakerService;
	provides MultipleSpeakerService with DefaultMultipleSpeakerService, OtherMultipleSpeakerService;

	opens be.tomdw.java.modules.spring.samples.basicapplication.speaker.internal to spring.beans, spring.core, spring.context;

}