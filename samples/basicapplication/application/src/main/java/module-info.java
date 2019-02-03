import io.github.tomdw.java.modules.spring.samples.basicapplication.internal.MainApplicationConfiguration;
import io.github.tomdw.java.modules.context.boot.api.ModuleContext;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.SpeakerService;

@ModuleContext(mainConfigurationClass = MainApplicationConfiguration.class)
module io.github.tomdw.java.modules.spring.samples.basicapplication.application {
	requires io.github.tomdw.java.modules.spring.samples.basicapplication.speaker;
	requires io.github.tomdw.java.modules.context.boot;

	opens io.github.tomdw.java.modules.spring.samples.basicapplication.internal to spring.beans, spring.core, spring.context;

	uses SpeakerService;
	uses MultipleSpeakerService;
}