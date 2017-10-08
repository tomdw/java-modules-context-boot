@be.tomdewolf.jpms.context.boot.api.ModuleContext(
	mainConfigurationClass = be.tomdewolf.jpms.context.boot.samples.basicapplication.internal.MainApplicationConfiguration.class
)
module be.tomdewolf.jpms.context.boot.samples.basicapplication.application {
	requires be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker;
	requires be.tomdewolf.jpms.context.boot;

	opens be.tomdewolf.jpms.context.boot.samples.basicapplication.internal to spring.beans, spring.core, spring.context;

	uses be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker.api.SpeakerService;
}