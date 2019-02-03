import be.tomdw.java.modules.context.boot.api.ModuleContext;
import be.tomdw.java.modules.spring.integration.tests.IntegrationTestConfiguration;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.api.MultipleSpeakerService;
import be.tomdw.java.modules.spring.samples.basicapplication.speaker.api.SpeakerService;

@ModuleContext(mainConfigurationClass = IntegrationTestConfiguration.class)
module be.tomdw.java.modules.spring.integration.tests {
	requires be.tomdw.java.modules.spring.samples.basicapplication.application;
	requires be.tomdw.java.modules.context.boot;
	requires be.tomdw.java.modules.spring.samples.basicapplication.speaker;

	uses SpeakerService;
	uses MultipleSpeakerService;

	opens be.tomdw.java.modules.spring.integration.tests to spring.beans, spring.core;
}