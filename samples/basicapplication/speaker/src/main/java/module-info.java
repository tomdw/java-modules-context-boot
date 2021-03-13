import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.FailingSpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerWithGenericsService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.NamedSpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.SpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal.DefaultFailingSpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal.DefaultMultipleSpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal.MultipleSpeakerWithGenericsServiceProvider;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal.NamedSpeakerServiceProvider;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal.OtherMultipleSpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal.SpeakerConfiguration;
import io.github.tomdw.java.modules.context.boot.api.ModuleContext;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal.DefaultSpeakerService;

@ModuleContext(mainConfigurationClass = SpeakerConfiguration.class)
module io.github.tomdw.java.modules.spring.samples.basicapplication.speaker {
	exports io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api;
	requires io.github.tomdw.java.modules.context.boot;

	provides SpeakerService with DefaultSpeakerService;
	provides MultipleSpeakerService with DefaultMultipleSpeakerService, OtherMultipleSpeakerService;
	provides NamedSpeakerService with NamedSpeakerServiceProvider;
	provides MultipleSpeakerWithGenericsService with MultipleSpeakerWithGenericsServiceProvider;
	provides FailingSpeakerService with DefaultFailingSpeakerService;

	opens io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.internal to spring.beans, spring.core, spring.context;

}