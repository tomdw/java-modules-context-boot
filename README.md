# java-modules-context-boot
Simple framework to boot a spring context within each java module

1. [Requirements](#requirements)
2. [Maven Dependency](#maven-dependency)
3. [Define a spring context within a module](#define-a-spring-context-within-a-module)
4. [Starting the application](#starting-the-application)
5. [Integrating modules using services](#integrating-modules-using-services)
6. [Samples](#samples)

The [Boot Spring Context within each Java 11 Module](https://devcreativity.wordpress.com/2017/11/18/boot-spring-context-within-each-java-9-module/) blog post describes the approach in detail. 

## Requirements

- uses Spring 5.1.3.RELEASE as automatic modules
- build and run using jdk11 on the modulepath

## Maven Dependency

The dependency to use is

```xml
<dependency>
	<groupId>be.tomdw.java.modules.spring</groupId>
	<artifactId>java-modules-context-boot</artifactId>
	<version>1.0.0</version>
</dependency>
```

which provides you with a java module named 

`be.tomdw.java.modules.context.boot`

transitively providing read access to spring modules and javax.inject.

**WARNING:** javax.inject is an automatic module without a reserved Automatic-Module-Name. 
No guarantees are given that its name will not change in future. By requiring it transitively 
client code of java-modules-context-boot does not need to require javax.inject and the impact is limited
to the java-modules-context-boot module descriptor.

## Define a spring context within a module

To define a spring context within a java module, you need to:
 - use the ModuleContext annotation on the module pointing to a java-based spring configuration
 - require be.tomdw.java.modules.context.boot
 - open the package of the module to spring
 
For example:

```
@ModuleContext(
	mainConfigurationClass = SpeakerConfiguration.class
)
module be.tomdw.java.modules.spring.samples.basicapplication.speaker {
	requires be.tomdw.java.modules.context.boot;
	opens be.tomdw.java.modules.spring.samples.basicapplication.speaker.internal to spring.beans, spring.core, spring.context;
}
```

java-modules-context-boot will make sure to start a separate spring context for every module annotated with this annotation.

If you would like to use another `ApplicationContext` than the default `AnnotationConfigApplicationContext` you can specify the class using the `applicationContextClass` parameter of the `@ModuleContext` annotation. This class should extend `GenericApplicationContext` and implement the `AnnotationConfigRegistry` interface.

This could be useful when you want to use a Spring Boot specific ApplicationContext that contains starters. For example an application context supporting web servlets such as `org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext`. 

## Starting the application

### Using the built-in main class

You can use the be.tomdw.java.modules.context.boot.api.ModuleContextBooter.main as main method to start the application.

Make sure to add the application modules to your module path. For every module on the module path annotated with @ModuleContext
we boot a spring context.

### Using your own (main) classes

When you want to control when the modules get booted with spring contexts you can call

```
ModuleContextBooter.boot();
```
from anywhere in your project's code.

## Integrating modules using services

Using services through the ServiceLoader API allows to integrate modules in a loosely coupled way. The 
following sections describe how to do this between multiple spring contexts in different modules.

### Provide a module service from the spring context

To provide a service through the ServiceLoader API you need to add the 'provides' in your module-info:
```
@ModuleContext(...)
module be.tomdw.java.modules.spring.samples.basicapplication.speaker {
	...
	provides SpeakerService with DefaultSpeakerService;
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
	private SpeakerService speakerService;

}

```

Don't forget to add a 'uses' entry in the module-info:
```
@ModuleContext(...)
module be.tomdw.java.modules.spring.samples.basicapplication.application {
	...
	uses SpeakerService;
}
```

Every spring context is enriched with a processor to retrieve the necessary services 
through the ServiceLoader API and make them available for injection in the spring context of that module.

### Reference a list of module services from another spring context

Similar to using a single service, the annotation @ModuleServiceReference can be used to inject a list of all services that implement a certain interface.
```
@Named
public class MessageGenerator {

	@ModuleServiceReference
	@Named("speakerServiceList")
	private List<SpeakerService> speakerService;

}

```

The difference is that we cannot inject by type, because of generics limitations a list of speakerServices are not known by spring to inject.
To support this, we are registring the list of provided services with a bean name. By convention this name will be the type of the services suffixed by "List".
When injecting the service list you should use the @Named or @Qualifier annotation to specify the name following this naming convention.

## Samples
- Under 'samples' the 'basicapplication' sample shows this in a working hello world application.
	- follow the instructions regarding the java toolchains from the [java-modules-parent project](https://github.com/tomdw/java-modules-parent)
	- start the application using 'mvn toolchains:toolchain exec:exec' from within the 'basicapplication/application' module.
  