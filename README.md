# java-modules-context-boot
Simple framework to boot a spring context within each java platform module

1. [Requirements](#requirements)
2. [Maven Dependency](#maven-dependency)
3. [Define a spring context within a module](#define-a-spring-context-within-a-module)
4. [Starting the application](#starting-the-application)
5. [Integrating modules using services](#integrating-modules-using-services)
6. [Samples](#samples)

## Requirements

- uses Spring 5.0.0 as automatic modules
- build and run using jdk9 on the modulepath

## Maven Dependency

The dependency to use is

```xml
<dependency>
	<groupId>be.tomdewolf.jpms.context.boot</groupId>
	<artifactId>jpms-context-boot</artifactId>
	<version>1.0.0</version>
</dependency>
```

which provides you with a java module named 

`be.tomdewolf.jpms.context.boot`

transitively providing read access to spring modules and javax.inject.

**WARNING:** javax.inject is an automatic module without a reserved Automatic-Module-Name. 
No guarantees are given that its name will not change in future. By requiring it transitively 
client code of jpms-context-boot does not need to require javax.inject and the impact is limited
to the jpms-context-boot module descriptor.

## Define a spring context within a module

To define a spring context within a java module, you need to:
 - use the ModuleContext annotation on the module pointing to a java-based spring configuration
 - require be.tomdewolf.jpms.context.boot
 - open the package of the module to spring
 
For example:

```
@be.tomdewolf.jpms.context.boot.api.ModuleContext(
	mainConfigurationClass = be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker.internal.SpeakerConfiguration.class
)
module be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker {
	requires be.tomdewolf.jpms.context.boot;
	opens be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker.internal to spring.beans, spring.core, spring.context;
}
```

jpms-context-boot will make sure to start a separate spring context for every module annotated with this annotation.

## Starting the application

### Using the built-in main class

You can use the be.tomdewolf.jpms.context.boot.api.ModuleContextBooter.main as main method to start the application.

Make sure to add the application modules to your module path. For every module on the module path annotated with @ModuleContext
we boot a spring context.

### Using your own (main) classes

When you want to control when the modules get booted with spring contexts you can call

```
be.tomdewolf.jpms.context.boot.api.ModuleContextBooter.boot();
```
from anywhere in your project's code.

## Integrating modules using services

Using services through the ServiceLoader API allows to integrate modules in a loosely coupled way. The 
following sections describe how to do this between multiple spring contexts in different modules.

### Provide a module service from the spring context

To provide a service through the ServiceLoader API you need to add the 'provides' in your module-info:
```
@be.tomdewolf.jpms.context.boot.api.ModuleContext(...)
module be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker {
	...
	provides be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker.api.SpeakerService with be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker.internal.DefaultSpeakerService;
	...
}
```

Instead of using a default contructor, leverage the 'provider' factory method alternative as follows:
```
@Named
public class DefaultSpeakerService implements SpeakerService {
	...
	public static DefaultSpeakerService provider() {
		return ModuleServiceProvider.provide(DefaultSpeakerService.class);
	}
	...
}

```

The ModuleServiceProvider.provide helper method retrieves a bean of the given type from 
the spring application context associated with the module calling the provide method.

### Reference a module service from another spring context

Using a service in another module can simply be done by annotating with @ModuleServiceReference:
```
@Named
public class MessageGenerator {

	@ModuleServiceReference
	@Inject
	private SpeakerService speakerService;

}

```

Don't forget to add a 'uses' entry in the module-info:
```
@be.tomdewolf.jpms.context.boot.api.ModuleContext(...)
module be.tomdewolf.jpms.context.boot.samples.basicapplication.application {
	...
	uses be.tomdewolf.jpms.context.boot.samples.basicapplication.speaker.api.SpeakerService;
}
```

Every spring context is enriched with a processor to retrieve the necessary services 
through the ServiceLoader API and make them available for injection in the spring context of that module.

## Samples
- Under 'samples' the 'basicapplication' sample shows this in a working hello world application.
	- follow the instructions regarding the java toolchains from the [java-modules-parent project](https://github.com/tomdw/java-modules-parent)
	- start the application using 'mvn toolchains:toolchain exec:exec' from within the 'basicapplication/application' module.
