import be.tomdw.java.modules.spring.samples.basicapplication.internal.MainApplicationConfiguration;
import be.tomdw.java.modules.context.boot.api.ModuleContext;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.api.SpeakerService;

@ModuleContext(mainConfigurationClass = MainApplicationConfiguration.class)
module be.tomdw.java.modules.spring.samples.basicapplication.application {
	requires be.tomdw.java.modules.spring.samples.basicapplication.speaker;
	requires be.tomdw.java.modules.context.boot;

	opens be.tomdw.java.modules.spring.samples.basicapplication.internal to spring.beans, spring.core, spring.context;

	uses SpeakerService;
	uses MultipleSpeakerService;
}