import io.github.tomdw.java.modules.context.boot.api.ModuleContext;
import io.github.tomdw.java.modules.spring.integration.tests.IntegrationTestService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.NamedSpeakerService;
import io.github.tomdw.java.modules.spring.samples.basicapplication.speaker.api.SpeakerService;

@ModuleContext(mainConfigurationClass = IntegrationTestService.class)
module io.github.tomdw.java.modules.spring.integration.tests {
	requires io.github.tomdw.java.modules.context.boot;
	requires io.github.tomdw.java.modules.spring.samples.basicapplication.application;
	requires io.github.tomdw.java.modules.spring.samples.basicapplication.speaker;

	uses SpeakerService;
	uses MultipleSpeakerService;
	uses NamedSpeakerService;

	opens io.github.tomdw.java.modules.spring.integration.tests to spring.beans, spring.core;
}