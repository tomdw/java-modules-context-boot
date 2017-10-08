@be.tomdewolf.jpms.context.boot.api.ModuleContext(
	mainConfigurationClass = be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker.internal.SpeakerConfiguration.class
)
module be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker {
	exports be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker.api;
	requires be.tomdewolf.jpms.context.boot;

	provides be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker.api.SpeakerService with be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker.internal.DefaultSpeakerService;

	opens be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker.internal to spring.beans, spring.core, spring.context;

}